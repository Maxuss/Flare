package space.maxus.flare.react;

import org.apache.commons.lang3.concurrent.Computable;
import org.apache.commons.lang3.concurrent.Memoizer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ReactivityProvider is an interface that provides access to some utility hooks
 */
public interface ReactivityProvider {
    /**
     * Constructs a new ReactiveState
     * @param initial Initial value of the state
     * @return A new ReactiveState
     * @param <V> Type of the value inside state
     */
    <V> ReactiveState<V> useState(@Nullable V initial);

    /**
     * Constructs a new ReactiveState bound to this object
     * <br>
     * **Experimental**: This is an experimental hook, and it may change or be removed in the future
     * @param initial Initial value of the state
     * @return A new ReactiveState
     * @param <V> Type of the value inside state
     */
    @ApiStatus.Experimental
    default <V> ReactiveState<V> useBoundState(@Nullable V initial) {
        return useState(initial);
    }

    /**
     * Constructs a new ReactiveState that is **explicitly** not bound to this object
     * @param initial Initial value of the state
     * @return A new ReactiveState
     * @param <V> Type of the value inside state
     */
    default <V> ReactiveState<V> useUnboundState(@Nullable V initial) {
        return useState(initial);
    }

    /**
     * Memoizes a Computable, so it is only recalculated on state change.
     * @param dependency Dependency to watch for changes
     * @param computable Computable to memoize
     * @return A memoized computable
     * @param <I> Type of value inside dependency
     * @param <O> Type of output value
     */
    default <I, O> Computable<I, O> useMemo(@NotNull ReactiveState<I> dependency, @NotNull Computable<I, O> computable) {
        MemoizedReactiveComputable<I, O> memoized = new MemoizedReactiveComputable<>(new Memoizer<>(computable));
        dependency.subscribe(memoized);
        return memoized;
    }
}
