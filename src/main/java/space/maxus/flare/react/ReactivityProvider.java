package space.maxus.flare.react;

import org.jetbrains.annotations.Nullable;

public interface ReactivityProvider {
    <V> ReactiveState<V> useState(@Nullable V initial);
}
