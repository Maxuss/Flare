package space.maxus.flare.react;

import org.apache.commons.lang3.concurrent.Computable;
import org.apache.commons.lang3.concurrent.Memoizer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ReactivityProvider {
    <V> ReactiveState<V> useState(@Nullable V initial);

    default <V> ReactiveState<V> useBoundState(@Nullable V initial) {
        return useState(initial);
    }

    default <V> ReactiveState<V> useUnboundState(@Nullable V initial) {
        return useState(initial);
    }

    default <I, O> Computable<I, O> useMemo(@NotNull ReactiveState<I> dependency, @NotNull Computable<I, O> computable) {
        MemoizedReactiveComputable<I, O> memoized = new MemoizedReactiveComputable<>(new Memoizer<>(computable));
        dependency.subscribe(memoized);
        return memoized;
    }
}
