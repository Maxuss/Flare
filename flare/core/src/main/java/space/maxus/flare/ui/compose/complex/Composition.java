package space.maxus.flare.ui.compose.complex;

import com.google.common.collect.Sets;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.PackedComposable;
import space.maxus.flare.ui.compose.Configurable;
import space.maxus.flare.ui.space.ComposableSpace;
import space.maxus.flare.ui.space.Slot;
import space.maxus.flare.util.FlareUtil;

import java.util.*;

public interface Composition extends Composable, Configurable<Composition> {
    @Contract("_ -> new")
    static @NotNull Composition of(PackedComposable... comps) {
        return new ExplicitComposition(Arrays.asList(comps));
    }

    List<PackedComposable> fitIn(ComposableSpace space);

    List<PackedComposable> children();

    @Override
    default boolean rightClick(@NotNull InventoryClickEvent e) {
        Slot slot = Slot.ofRaw(e.getSlot());
        return children().stream()
                .filter(entry -> entry.getSpace().slots().contains(slot))
                .reduce((a, b) -> b)
                .map(entry -> entry.getComposable().rightClick(e))
                .orElse(true);
    }

    @Override
    default boolean leftClick(@NotNull InventoryClickEvent e) {
        Slot slot = Slot.ofRaw(e.getSlot());
        return children().stream()
                .filter(entry -> entry.getSpace().slots().contains(slot))
                .reduce((a, b) -> b)
                .map(entry -> entry.getComposable().leftClick(e))
                .orElse(true);
    }

    @Override
    default void click(@NotNull InventoryClickEvent e) {
        Slot slot = Slot.ofRaw(e.getSlot());
        children().stream()
                .filter(entry -> entry.getSpace().slots().contains(slot))
                .reduce((a, b) -> b)
                .ifPresent(entry -> entry.getComposable().click(e));
    }

    @Override
    default boolean shiftFrom(@NotNull InventoryClickEvent e) {
        Slot slot = Slot.ofRaw(e.getSlot());
        return children().stream()
                .filter(entry -> entry.getSpace().slots().contains(slot))
                .reduce((a, b) -> b)
                .map(entry -> entry.getComposable().shiftFrom(e))
                .orElse(true);
    }

    @Override
    default boolean shiftInto(@NotNull ItemStack stack, @NotNull InventoryClickEvent e) {
        return FlareUtil.reduceBoolStream(children().stream()
                .map(composable -> composable.getComposable().shiftInto(stack, e)), (left, right) -> left && right);
    }

    @Override
    default boolean drag(@NotNull Map<Slot, ItemStack> newItems, @NotNull InventoryDragEvent e) {
        var slotSet = newItems.keySet();
        return FlareUtil.reduceBoolStream(children().stream()
                .filter(entry -> Collections.disjoint(entry.getSpace().slots(), slotSet))
                .map(each -> {
                    Set<Slot> intersection = Sets.intersection(each.getSpace().slots(), slotSet);
                    Map<Slot, ItemStack> intersectionMap = FlareUtil.map2setIntersect(newItems, intersection);
                    return each.getComposable().drag(intersectionMap, e);
                }), (left, right) -> left || right);
    }

    @Override
    @NotNull
    default PackedComposable inside(@NotNull ComposableSpace space) {
        this.fitIn(space);
        return Composable.super.inside(space);
    }
}
