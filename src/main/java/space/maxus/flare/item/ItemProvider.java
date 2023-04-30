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
    static @NotNull ItemProvider still(@NotNull ItemStackBuilder builder) {
        return new StillItemProvider(builder.build());
    }
    static @NotNull <V> ReactiveItemProvider<V> reactive(@NotNull Computable<@Nullable V, @Nullable ItemStack> provider) {
        return new ReactiveItemProvider<>(provider);
    }

    static @NotNull <V> ReactiveItemProvider<V> reactiveBuild(@NotNull Computable<@Nullable V, @NotNull ItemStackBuilder> provider) {
        return new ReactiveItemProvider<>(val -> provider.compute(val).build());
    }

    ItemStack provide();
}
