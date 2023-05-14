package space.maxus.flare.react;

import org.jetbrains.annotations.Nullable;

/**
 * A subscriber to a reactive state.
 * @param <V> The type of the state value.
 */
@FunctionalInterface
public interface ReactiveSubscriber<V> {
    /**
     * Called whenever value inside a reactive state changes
     * @param state The new state value.
     * @throws ReactiveException If an error occurs while handling the state change.
     */
    void onStateChange(@Nullable V state) throws ReactiveException;
}
