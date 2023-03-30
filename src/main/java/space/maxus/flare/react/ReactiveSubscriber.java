package space.maxus.flare.react;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ReactiveSubscriber<V> {
    void onStateChange(@Nullable V state);
}
