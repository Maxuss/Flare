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
import org.jetbrains.annotations.ApiStatus;
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

/**
 * The super class to all Flare frames.
 * <br />
 * A frame represents an inventory that can display composable elements.
 * <br />
 * @apiNote if you want to extend a frame, it <b>is not recommended</b> to extend this class, instead extend {@link space.maxus.flare.ui.frames.SimpleFrame}
 *
 * @see space.maxus.flare.ui.frames.SimpleFrame
 * @see space.maxus.flare.ui.frames.ParamFrame
 * @see space.maxus.flare.ui.frames.PaginatedFrame
 */
public abstract class Frame implements ReactivityProvider {
    /**
     * Elements composed inside this frame
     */
    protected final @NotNull Map<@NotNull ComposableSpace, @NotNull Composable> composed = new LinkedHashMap<>();
    /**
     * Whether this frame is dirty.
     *
     * @apiNote This is an internal method. Value of this field should not be set externally, as it may lead to unexpected render issues.
     */
    @ApiStatus.Internal
    protected final AtomicBoolean isDirty = new AtomicBoolean(false);

    private final ConcurrentLinkedQueue<ComposableSpace> toRender = new ConcurrentLinkedQueue<>();
    private final ReadWriteLock renderLock = new ReentrantReadWriteLock();
    private @Nullable Object context = null;

    /**
     * Viewer currently inside this frame.
     * @apiNote Value is <b>null</b> if called during initialization in {@link #init()}
     */
    @Getter
    private Player viewer;

    /**
     * Called once <b>inside the constructor</b>
     */
    public abstract void init();

    /**
     * Gets the inventory of this frame.
     * @return the inventory of this frame.
     */
    public abstract @NotNull Inventory selfInventory();

    /**
     * Gets the dimensions of this frame's inventory
     * @return the dimensions of this frame's inventory
     */
    public abstract @NotNull Dimensions getDimensions();

    /**
     * Gets the Flare InventoryHolder for this frame
     * @return Flare InventoryHolder
     */
    public abstract @NotNull ReactiveInventoryHolder getHolder();

    /**
     * Sets the holder for this frame
     * @param holder Holder to be set
     * @apiNote This is an internal method, it is only used for frame-switching
     */
    @ApiStatus.Internal
    protected abstract void setHolder(ReactiveInventoryHolder holder);

    @Override
    public <V> ReactiveState<V> useState(@Nullable V initial) {
        return new ReactiveState<>(initial);
    }

    @Override
    @ApiStatus.Experimental
    public <V> ReactiveState<V> useBoundState(@Nullable V initial) {
        ReactiveState<V> state = new ReactiveState<>(initial);
        state.subscribe(change -> this.markDirty());
        return state;
    }

    /**
     * Sets a global context for this frame and its children components
     * @param context Context to be set
     * @param <T> Type of the context
     */
    public <T> void useContext(@Nullable T context) {
        this.context = context;
    }

    /**
     * Sets the title of this frame and broadcasts it to the provided viewer
     * @param viewer Viewer to broadcast the title to
     * @param title New title of this frame
     */
    public void setTitle(Player viewer, String title) {
        Flare.getNms().sendPacket(Flare.getNms().obtainConnection(viewer), Flare.getNms().buildTitlePacket(viewer, FlareUtil.text(title, viewer)));
    }

    /**
     * Refreshes this title for current viewer.
     */
    public void refreshTitle() {
        this.setTitle(viewer, this.getTitle());
    }

    /**
     * Binds a viewer to this frame.
     * @param viewer Viewer to be bound
     * @apiNote This is an internal method
     */
    @ApiStatus.Internal
    public final void bindViewer(Player viewer) {
        if (this.viewer != null)
            return;
        this.viewer = viewer;
    }

    /**
     * Switches this frame to another frame without reopening inventory.
     * @param other Frame to switch to
     */
    public void switchFrame(@NotNull Frame other) {
        if (other.getDimensions() != this.getDimensions()) {
            // we need to reopen inventory
            other.bindViewer(viewer); // always binding viewer before rendering, since lazy inventory initialization depends on it
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
            other.bindViewer(viewer); // always binding viewer before rendering, since lazy inventory initialization depends on it
            other.render();
            other.open(viewer);
        }
    }

    /**
     * Goes to the previous frame or does nothing if it is null.
     */
    public void goBack() {
        Frame other = PlayerFrameStateManager.restoreSnapshot(viewer);
        if (other == null)
            return;

        if (other.getDimensions() != this.getDimensions()) {
            // we need to reopen inventory
            other.bindViewer(viewer); // always binding viewer before rendering, since lazy inventory initialization depends on it
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
            other.bindViewer(viewer); // always binding viewer before rendering, since lazy inventory initialization depends on it
            other.render();
            other.setTitle(viewer, other.getTitle());
            other.restorePreviousState(viewer);
        }
    }

    /**
     * Attempts to get current context. Throws an exception if it is null.
     * @return Context of provided type
     * @param <T> Expected type of context
     */
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

    /**
     * Attempts to get current context.
     * @return Context of provided type or null.
     * @param <T> Expected type of context
     */
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

    /**
     * Renders this frame to current inventory
     */
    public void render() {
        Lock readLock = renderLock.readLock();
        readLock.lock();

        Inventory inventory = this.selfInventory();
        final ItemStack[] contents = new ItemStack[inventory.getSize()];
        composed.forEach((key, value) -> {
            for (Slot slot : key.slots()) {
                if (slot.rawSlot() >= inventory.getSize())
                    continue;
                ItemStack rendered = value.renderAt(slot);
                if (rendered != null)
                    contents[slot.rawSlot()] = rendered;
            }
        });
        inventory.setContents(contents);

        readLock.unlock();
    }

    /**
     * Re-renders part of this frame included inside the provided space
     * @param toUpdate Space to be re-rendered
     */
    protected void renderPart(@NotNull ComposableSpace toUpdate) {
        Lock readLock = renderLock.readLock();
        readLock.lock();

        Inventory inventory = this.selfInventory();
        final ItemStack[] contents = inventory.getContents();
        Set<Slot> slots = toUpdate.slots();
        composed.forEach((key, value) -> {
            Set<Slot> slotsMut = new HashSet<>(slots);
            slotsMut.retainAll(key.slots());
            if (slotsMut.isEmpty())
                return;
            for (Slot slot : slotsMut) {
                if (slot.rawSlot() >= inventory.getSize())
                    continue;
                ItemStack rendered = value.renderAt(slot);
                if (rendered != null)
                    contents[slot.rawSlot()] = rendered;
            }
        });
        inventory.setContents(contents);

        readLock.unlock();
    }

    /**
     * Returns the concurrent map containing all composed elements
     * @return Concurrent map containing all composed elements
     */
    public @NotNull Map<ComposableSpace, Composable> composableMap() {
        return composed;
    }

    /**
     * Composes provided element inside the provided space.
     * @param space Space to compose element inside
     * @param element Element to be composed
     */
    public void compose(@NotNull ComposableSpace space, @NotNull Composable element) {
        Lock writeLock = this.renderLock.writeLock();
        writeLock.lock();

        element.inside(space); // some (most actually) composable elements depend on this
        element.injectRoot(this);
        this.composed.put(space, element);

        writeLock.unlock();
    }

    /**
     * Composes provided elements inside the provided spaces.
     * @param elements Map of spaces and elements to be composed
     */
    public void composeAll(@NotNull Map<ComposableSpace, Composable> elements) {
        if (elements.isEmpty())
            return; // no need to lock
        Lock writeLock = this.renderLock.writeLock();
        writeLock.lock();

        elements.forEach(this::compose);

        writeLock.unlock();
    }

    /**
     * Composes provided packed composable
     * @param packed Packed composable to be composed
     */
    public void compose(@NotNull PackedComposable packed) {
        this.compose(packed.getSpace(), packed.getComposable());
    }

    /**
     * Marks the provided space in this composable as dirty, meaning it will be re-rendered on next tick
     * @param space Space to be marked as dirty
     */
    public void markDirty(@NotNull ComposableSpace space) {
        this.toRender.add(space);
        if (this.isDirty.get())
            return;
        this.isDirty.setRelease(true);
        Bukkit.getScheduler().runTaskLaterAsynchronously(
                Flare.getInstance(),
                () -> {
                    if (!this.isDirty.get())
                        return;
                    for (ComposableSpace renderSpace : this.toRender) {
                        this.renderPart(renderSpace);
                    }
                    this.toRender.clear();
                    this.isDirty.setRelease(false);
                },
                1L
        );
    }

    /**
     * Marks the provided composable's space as dirty, meaning it will be re-rendered on next tick
     * @param source Composable, which space to be marked as dirty
     */
    public void markDirty(@NotNull Composable source) {
        ComposableSpace space = FlareUtil.keyFromValue(composed, source);
        if (space == null)
            return;
        this.markDirty(space);
    }

    /**
     * Marks this whole frame as dirty, meaning it will be re-rendered on next tick
     */
    public void markDirty() {
        if (this.isDirty.get())
            return;
        this.isDirty.setRelease(true);
        // Only render next tick
        Bukkit.getScheduler().runTaskLaterAsynchronously(
                Flare.getInstance(),
                () -> {
                    if (!this.isDirty.get())
                        return;
                    this.render();
                    this.isDirty.setRelease(false);
                },
                1L
        );
    }

    @ApiStatus.Internal
    public final boolean fireLeftClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        boolean res = leftClick(slot, e);
        return composed.entrySet().stream()
                .filter(entry -> entry.getKey().slots().contains(slot))
                .reduce((a, b) -> b)
                .map(entry -> entry.getValue().leftClick(e))
                .orElse(true) || res;
    }

    @ApiStatus.Internal
    public final boolean fireRightClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        boolean res = rightClick(slot, e);
        return composed.entrySet().stream()
                .filter(entry -> entry.getKey().slots().contains(slot))
                .reduce((a, b) -> b)
                .map(entry -> entry.getValue().rightClick(e))
                .orElse(true) || res;
    }

    @ApiStatus.Internal
    public final void fireGenericClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        genericClick(slot, e);
        composed.entrySet().stream()
                .filter(entry -> entry.getKey().slots().contains(slot))
                .reduce((a, b) -> b)
                .ifPresent(entry -> entry.getValue().click(e));
    }

    @ApiStatus.Internal
    public final boolean fireShiftClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        boolean res = shiftClick(slot, e);
        return composed.entrySet().stream()
                .filter(entry -> entry.getKey().slots().contains(slot))
                .reduce((a, b) -> b)
                .map(entry -> entry.getValue().shiftFrom(e))
                .orElse(true) && res;
    }

    @ApiStatus.Internal
    public final boolean fireShiftRequest(@NotNull ItemStack shifting, @NotNull InventoryClickEvent e) {
        boolean res = shiftRequest(shifting, e);
        return FlareUtil.reduceBoolStream(composed.values().stream()
                .map(composable -> composable.shiftInto(shifting, e)), (left, right) -> left && right) && res;
    }

    @ApiStatus.Internal
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

    /**
     * Opens this frame for the provided player
     *
     * @apiNote This is a partially internal method, for actually opening frames you should probably use {@link Flare#open(Frame, Player)}
     * @param player Player for whom to open
     */
    public void open(Player player) {
        this.onOpen(player);
    }

    /**
     * Closes this frame
     */
    public void close() {
        this.onClose();
        this.composed.values().forEach(Composable::destroy);
    }

    @ApiStatus.Internal
    public void restorePreviousState(Player player) {
        this.onOpen(player);
        this.composed.values().forEach(Composable::restore);
    }

    /**
     * Called when this frame is <b>attempted to put items into</b> by shift click. This means that a player clicked on an item inside <em>their</em> inventory,
     * and Flare attempts to fit item inside this frame.
     * @param e The click event
     * @param stack Stack that is attempted to be moved.
     * @return True if the event should be cancelled, false otherwise
     */
    @ApiStatus.OverrideOnly
    public boolean shiftRequest(@NotNull ItemStack stack, @NotNull InventoryClickEvent e) {
        // No extra logic here
        return true;
    }

    /**
     * Called when this frame is <b>shift clicked</b>
     * @param e The click event
     * @param slot Slot that was clicked
     * @return True if the event should be cancelled, false otherwise
     */
    @ApiStatus.OverrideOnly
    public boolean shiftClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        // No extra logic here
        return true;
    }

    /**
     * Called when this frame is <b>right clicked</b>
     * @param e The click event
     * @param slot Slot that was clicked
     * @return True if the event should be cancelled, false otherwise
     */
    @ApiStatus.OverrideOnly
    public boolean rightClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        // No extra logic here
        return true;
    }

    /**
     * Called when this frame is <b>left clicked</b>
     * @param e The click event
     * @param slot Slot that was clicked
     * @return True if the event should be cancelled, false otherwise
     */
    @ApiStatus.OverrideOnly
    public boolean leftClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        // No extra logic here
        return true;
    }

    /**
     * Called every time this frame is clicked.
     * @param slot Slot that was clicked
     * @param e The click event
     */
    @ApiStatus.OverrideOnly
    public void genericClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        // No extra logic here
    }

    /**
     * Called when items are dragged across this frame.
     * @param newItems Items that are dragged inside this frame
     * @param e The drag event
     * @return True if the event should be cancelled, false otherwise.
     * @apiNote This method is currently experimental and may not work correctly
     */
    @ApiStatus.Experimental
    @ApiStatus.OverrideOnly
    public boolean drag(@NotNull Map<Slot, ItemStack> newItems, @NotNull InventoryDragEvent e) {
        // No extra logic here
        return true;
    }

    /**
     * Called when this frame is opened to the player
     * @param player Player this frame is opened to
     */
    @ApiStatus.OverrideOnly
    public void onOpen(@NotNull Player player) {
        // No extra logic here by default
    }

    /**
     * Called when this frame is closed.
     *
     * @apiNote The frame may be reopened later, see {@link #restorePreviousState(Player)} for more info
     * @see #restorePreviousState(Player)
     */
    @ApiStatus.OverrideOnly
    public void onClose() {
        // No extra logic here by default
    }

    /**
     * Gets the default title of this frame. `A Flare $className` by default.
     * @return The title of this frame
     */
    public String getTitle() {
        return "A Flare %s".formatted(this.getClass().getSimpleName());
    }

    /**
     * Thrown when context is not of expected type
     */
    @StandardException
    static class InvalidContextValue extends Exception {

    }
}
