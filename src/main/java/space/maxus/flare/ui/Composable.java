package space.maxus.flare.ui;

import org.bukkit.inventory.ItemStack;

public interface Composable {
    ItemStack renderAt(Slot slot);

    default boolean rightClick() {
        // No extra calculations there
        return true;
    }

    default boolean leftClick() {
        // No extra calculations there
        return true;
    }

    default boolean shiftClick() {
        // No extra calculations there
        return true;
    }
}
