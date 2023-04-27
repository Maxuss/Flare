package space.maxus.flare.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ItemProvider {
    ItemStack provide();

    @Contract("_ -> new")
    static @NotNull ItemProvider still(@Nullable ItemStack stack) {
        return new StaticItemProvider(stack);
    }
}
