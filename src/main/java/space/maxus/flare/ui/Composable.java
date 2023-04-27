package space.maxus.flare.ui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.ui.space.Slot;

import java.util.Map;

public interface Composable {
    ItemStack renderAt(Slot slot);

    default boolean rightClick(@NotNull InventoryClickEvent e) {
        // No extra calculations there
        return true;
    }

    default boolean leftClick(@NotNull InventoryClickEvent e) {
        // No extra calculations there
        return true;
    }

    default boolean shiftFrom(@NotNull InventoryClickEvent e) {
        // No extra calculations there
        return true;
    }

    default boolean shiftInto(@NotNull ItemStack stack, @NotNull InventoryClickEvent e) {
        // No extra calculations there
        return true;
    }

    default void click(@NotNull InventoryClickEvent e) {
        // No extra calculations there
    }

    default boolean drag(@NotNull Map<Slot, ItemStack> newItems, @NotNull InventoryDragEvent e) {
        // No extra calculations there
        return true;
    }
}
