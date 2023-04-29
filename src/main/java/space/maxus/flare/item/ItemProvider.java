package space.maxus.flare.item;

import org.apache.commons.lang3.concurrent.Computable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ItemProvider {
    @Contract("_ -> new")
    static @NotNull ItemProvider still(@Nullable ItemStack stack) {
        return new StillItemProvider(stack);
    }

    static @NotNull <V> ItemProvider reactive(@NotNull Computable<@Nullable V, @Nullable ItemStack> provider) {
        return new ReactiveItemProvider<>(provider);
    }

    ItemStack provide();
}
