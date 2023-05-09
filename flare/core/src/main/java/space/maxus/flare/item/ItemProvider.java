package space.maxus.flare.item;

import org.apache.commons.lang3.concurrent.Computable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A functional interface that is used to represent items within Flare.
 * <br>
 * Can be used as:
 * <ul>
 *   <li>A function for custom logic </li>
 *   <li>A static item (via {@link #still}) </li>
 *   <li>A reactive item that depends on a {@link space.maxus.flare.react.ReactiveState} (via {@link #reactive}) </li>
 * </ul>
 *
 * @see space.maxus.flare.react.Reactive#item
 */
@FunctionalInterface
public interface ItemProvider {
    /**
     * Constructs a static item provider that returns the same stack all the time.
     * @param stack Stack to be used
     * @return StillItemProvider
     */
    @Contract("_ -> new")
    static @NotNull ItemProvider still(@Nullable ItemStack stack) {
        return new StillItemProvider(stack);
    }

    /**
     * Constructs a static item provider that returns the same stack all the time.
     * @param builder ItemStack builder to be used
     * @return StillItemProvider
     */
    static @NotNull ItemProvider still(@NotNull ItemStackBuilder builder) {
        return new StillItemProvider(builder.build());
    }

    /**
     * Constructs a reactive item provider that returns a stack based on provider.
     * <br>
     * <b>NOTE:</b> Requires hooking to a {@link space.maxus.flare.react.ReactiveState} for actual logic
     * @param provider Provider to be used for supplying item
     * @return ReactiveItemProvider
     * @param <V> Value used inside the ReactiveState
     * @see space.maxus.flare.react.ReactiveState
     * @see space.maxus.flare.react.Reactive#item
     */
    static @NotNull <V> ReactiveItemProvider<V> reactive(@NotNull Computable<@Nullable V, @Nullable ItemStack> provider) {
        return new ReactiveItemProvider<>(provider);
    }

    /**
     * Constructs a reactive item provider that returns a stack based on provider.
     * <br>
     * <b>NOTE:</b> Requires hooking to a {@link space.maxus.flare.react.ReactiveState} for actual logic
     * @param provider Provider to be used for supplying item builder
     * @return ReactiveItemProvider
     * @param <V> Value used inside the ReactiveState
     * @see space.maxus.flare.react.ReactiveState
     * @see space.maxus.flare.react.Reactive#item
     */
    static @NotNull <V> ReactiveItemProvider<V> reactiveBuild(@NotNull Computable<@Nullable V, @NotNull ItemStackBuilder> provider) {
        return new ReactiveItemProvider<>(val -> provider.compute(val).build());
    }

    /**
     * Provides an item stack.
     * @return an ItemStack. May be null
     */
    @Nullable ItemStack provide();
}
