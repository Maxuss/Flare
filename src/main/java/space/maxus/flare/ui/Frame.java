package space.maxus.flare.ui;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.ui.space.ComposableSpace;
import space.maxus.flare.ui.space.Slot;
import space.maxus.flare.util.FlareUtil;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public abstract class Frame {
    protected final @NotNull ConcurrentHashMap<@NotNull ComposableSpace, @NotNull Composable> composed = new ConcurrentHashMap<>();

    public void render(@NotNull Inventory inside) {
        final ItemStack[] contents = new ItemStack[inside.getSize()];
        composed.forEach((key, value) -> {
            for(Slot slot: key.slots()) {
                contents[slot.rawSlot()] = value.renderAt(slot);
            }
        });
        inside.setContents(contents);
    }

    public @NotNull ConcurrentMap<ComposableSpace, Composable> composableMap() {
        return composed;
    }

    public void compose(@NotNull ComposableSpace space, @NotNull Composable element) {
        this.composed.put(space, element);
    }

    public abstract void init();
    public abstract @NotNull Inventory baseInventory();
    public final boolean fireLeftClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        boolean res = leftClick(slot, e);
        return composed.entrySet().stream()
                .filter(entry -> entry.getKey().slots().contains(slot))
                .findFirst()
                .map(entry -> entry.getValue().leftClick(e))
                .orElse(true) || res;
    }

    public final boolean fireRightClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        boolean res = rightClick(slot, e);
        return composed.entrySet().stream()
                .filter(entry -> entry.getKey().slots().contains(slot))
                .findFirst()
                .map(entry -> entry.getValue().rightClick(e))
                .orElse(true) || res;
    }

    public final void fireGenericClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        genericClick(slot, e);
        composed.entrySet().stream()
                .filter(entry -> entry.getKey().slots().contains(slot))
                .findFirst()
                .ifPresent(entry -> entry.getValue().click(e));
    }

    public final boolean fireShiftClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        boolean res = shiftClick(slot, e);
        return composed.entrySet().stream()
                .filter(entry -> entry.getKey().slots().contains(slot))
                .findFirst()
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
}
