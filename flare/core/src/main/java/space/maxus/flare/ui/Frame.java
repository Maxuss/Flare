package space.maxus.flare.ui;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.experimental.StandardException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.Flare;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.react.ReactivityProvider;
import space.maxus.flare.ui.space.ComposableSpace;
import space.maxus.flare.ui.space.Slot;
import space.maxus.flare.util.FlareUtil;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class Frame implements ReactivityProvider {
    protected final @NotNull Map<@NotNull ComposableSpace, @NotNull Composable> composed = new LinkedHashMap<>();
    protected final AtomicBoolean isDirty = new AtomicBoolean(false);
    private final ConcurrentLinkedQueue<ComposableSpace> toRender = new ConcurrentLinkedQueue<>();
    private final ReadWriteLock renderLock = new ReentrantReadWriteLock();
    private @Nullable Object context = null;
    @Getter
    private Player viewer;

    public abstract void init();
    public abstract @NotNull Inventory selfInventory();
    public abstract @NotNull Dimensions getDimensions();
    public abstract @NotNull ReactiveInventoryHolder getHolder();
    protected abstract void setHolder(ReactiveInventoryHolder holder);

    @Override
    public <V> ReactiveState<V> useState(@Nullable V initial) {
        return new ReactiveState<>(initial);
    }
    public <T> void useContext(@Nullable T context) {
        this.context = context;
    }
    public void setTitle(Player viewer, String title) {
        Flare.getNms().sendPacket(Flare.getNms().obtainConnection(viewer), Flare.getNms().buildTitlePacket(viewer, FlareUtil.text(title)));
    }

    public final void bindViewer(Player viewer) {
        if(this.viewer != null)
            return;
        this.viewer = viewer;
    }

    public void switchFrame(Frame other) {
        if(other.getDimensions() != this.getDimensions()) {
            // we need to reopen inventory
            other.bindViewer(viewer);
            other.render();
            viewer.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);
            viewer.openInventory(other.selfInventory());
            other.open(viewer);
        } else {
            // we need to simply re-render inventory
            PlayerFrameStateManager.saveSnapshot(viewer, this);
            this.close();
            other.getHolder().inherit(this.getHolder());
            this.getHolder().setFrame(other);
            other.setTitle(viewer, other.getTitle());
            other.bindViewer(viewer);
            other.render();
            other.open(viewer);
        }
    }

    public void goBack() {
        Frame other = PlayerFrameStateManager.restoreSnapshot(viewer);
        if(other == null)
            return;

        if(other.getDimensions() != this.getDimensions()) {
            // we need to reopen inventory
            other.bindViewer(viewer);
            other.render();
            viewer.closeInventory(InventoryCloseEvent.Reason.TELEPORT);
            viewer.openInventory(other.selfInventory());
            other.restorePreviousState(viewer);
        } else {
            // we need to simply re-render inventory
            // not saving snapshots here
            this.close();
            // we don't need to change inventory reference here, since it was restored
            other.getHolder().setFrame(other); // looks weird, but it is basically setting the frame reference to the frame we just restored
            other.bindViewer(viewer);
            other.render();
            other.setTitle(viewer, other.getTitle());
            other.restorePreviousState(viewer);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable T contextOrNull() {
        if (context == null)
            return null;
        try {
            return (T) this.context;
        } catch (ClassCastException cast) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> @NotNull T context() throws InvalidContextValue {
        if (context == null)
            throw new InvalidContextValue("Context value was null when requested");
        try {
            return (T) this.context;
        } catch (ClassCastException cast) {
            throw new InvalidContextValue("Context value was of type %s, not of requested type".formatted(context.getClass()));
        }
    }

    public void render() {
        Lock readLock = renderLock.readLock();
        readLock.lock();

        Inventory inventory = this.selfInventory();
        final ItemStack[] contents = new ItemStack[inventory.getSize()];
        composed.forEach((key, value) -> {
            for (Slot slot : key.slots()) {
                if(slot.rawSlot() >= inventory.getSize())
                    return;
                ItemStack rendered = value.renderAt(slot);
                if (rendered != null)
                    contents[slot.rawSlot()] = rendered;
            }
        });
        inventory.setContents(contents);

        readLock.unlock();
    }

    protected void renderPart(@NotNull ComposableSpace toUpdate) {
        Lock readLock = renderLock.readLock();
        readLock.lock();

        Inventory inventory = this.selfInventory();
        final ItemStack[] contents = inventory.getContents();
        Set<Slot> slots = toUpdate.slots();
        composed.forEach((key, value) -> {
            Set<Slot> slotsMut = new HashSet<>(slots);
            slotsMut.retainAll(key.slots());
            if(slotsMut.isEmpty())
                return;
            for (Slot slot : slotsMut) {
                if(slot.rawSlot() >= inventory.getSize())
                    return;
                ItemStack rendered = value.renderAt(slot);
                if (rendered != null)
                    contents[slot.rawSlot()] = rendered;
            }
        });
        inventory.setContents(contents);

        readLock.unlock();
    }

    public @NotNull Map<ComposableSpace, Composable> composableMap() {
        return composed;
    }

    public void compose(@NotNull ComposableSpace space, @NotNull Composable element) {
        Lock writeLock = this.renderLock.writeLock();
        writeLock.lock();

        element.inside(space); // some (most actually) composable elements depend on this
        element.injectRoot(this);
        this.composed.put(space, element);

        writeLock.unlock();
    }

    public void composeAll(@NotNull Map<ComposableSpace, Composable> elements) {
        if(elements.isEmpty())
            return; // no need to lock
        Lock writeLock = this.renderLock.writeLock();
        writeLock.lock();

        elements.forEach(this::compose);

        writeLock.unlock();
    }

    public void compose(@NotNull PackedComposable packed) {
        this.compose(packed.getSpace(), packed.getComposable());
    }

    public void markDirty(@NotNull ComposableSpace space) {
        this.toRender.add(space);
        if(this.isDirty.get())
            return;
        this.isDirty.setRelease(true);
        Bukkit.getScheduler().runTaskLaterAsynchronously(
                Flare.getHook(),
                () -> {
                    if(!this.isDirty.get())
                        return;
                    for(ComposableSpace renderSpace : this.toRender) {
                        this.renderPart(renderSpace);
                    }
                    this.toRender.clear();
                    this.isDirty.setRelease(false);
                },
                1L
        );
    }

    public void markDirty(@NotNull Composable source) {
        ComposableSpace space = FlareUtil.keyFromValue(composed, source);
        if(space == null)
            return;
        this.markDirty(space);
    }

    public void markDirty() {
        if (this.isDirty.get())
            return;
        this.isDirty.setRelease(true);
        // Only render next tick
        Bukkit.getScheduler().runTaskLaterAsynchronously(
                Flare.getHook(),
                () -> {
                    if (!this.isDirty.get())
                        return;
                    this.render();
                    this.isDirty.setRelease(false);
                },
                1L
        );
    }

    public final boolean fireLeftClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        boolean res = leftClick(slot, e);
        return composed.entrySet().stream()
                .filter(entry -> entry.getKey().slots().contains(slot))
                .reduce((a, b) -> b)
                .map(entry -> entry.getValue().leftClick(e))
                .orElse(true) || res;
    }

    public final boolean fireRightClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        boolean res = rightClick(slot, e);
        return composed.entrySet().stream()
                .filter(entry -> entry.getKey().slots().contains(slot))
                .reduce((a, b) -> b)
                .map(entry -> entry.getValue().rightClick(e))
                .orElse(true) || res;
    }

    public final void fireGenericClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        genericClick(slot, e);
        composed.entrySet().stream()
                .filter(entry -> entry.getKey().slots().contains(slot))
                .reduce((a, b) -> b)
                .ifPresent(entry -> entry.getValue().click(e));
    }

    public final boolean fireShiftClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        boolean res = shiftClick(slot, e);
        return composed.entrySet().stream()
                .filter(entry -> entry.getKey().slots().contains(slot))
                .reduce((a, b) -> b)
                .map(entry -> entry.getValue().shiftFrom(e))
                .orElse(true) || res;
    }

    public final boolean fireShiftRequest(@NotNull ItemStack shifting, @NotNull InventoryClickEvent e) {
        boolean res = shiftRequest(shifting, e);
        return FlareUtil.reduceBoolStream(composed.values().stream()
                .map(composable -> composable.shiftInto(shifting, e)), (left, right) -> left && right) && res;
    }

    public final boolean fireDrag(@NotNull Map<Slot, ItemStack> slots, @NotNull InventoryDragEvent e) {
        boolean res = drag(slots, e);
        var slotSet = slots.keySet();
        return FlareUtil.reduceBoolStream(composed.entrySet().stream()
                .filter(entry -> Collections.disjoint(entry.getKey().slots(), slotSet))
                .map(each -> {
                    Set<Slot> intersection = Sets.intersection(each.getKey().slots(), slotSet);
                    Map<Slot, ItemStack> intersectionMap = FlareUtil.map2setIntersect(slots, intersection);
                    return each.getValue().drag(intersectionMap, e);
                }), (left, right) -> left || right) || res;
    }

    public final void open(Player player) {
        this.onOpen(player);
    }

    public final void close() {
        this.onClose();
        this.composed.values().forEach(Composable::destroy);
    }

    public final void restorePreviousState(Player player) {
        this.onOpen(player);
        this.composed.values().forEach(Composable::restore);
    }

    public boolean shiftRequest(@NotNull ItemStack stack, @NotNull InventoryClickEvent e) {
        // No extra logic here
        return true;
    }

    public boolean shiftClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        // No extra logic here
        return true;
    }

    public boolean rightClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        // No extra logic here
        return true;
    }

    public boolean leftClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        // No extra logic here
        return true;
    }

    public void genericClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        // No extra logic here
    }

    public boolean drag(@NotNull Map<Slot, ItemStack> newItems, @NotNull InventoryDragEvent e) {
        // No extra logic here
        return true;
    }

    public void onOpen(@NotNull Player player) {
        // No extra logic here by default
    }

    public void onClose() {
        // No extra logic here by default
    }

    public String getTitle() {
        return "A Flare %s".formatted(this.getClass().getSimpleName());
    }

    @StandardException
    static class InvalidContextValue extends Exception {

    }
}
