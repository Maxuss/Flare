package space.maxus.flare.react;

import org.jetbrains.annotations.Nullable;

public interface ReactivityProvider {
    <V> ReactiveState<V> useState(@Nullable V initial);

    default <V> ReactiveState<V> useUnboundState(@Nullable V initial) {
        return useState(initial);
    }
}
