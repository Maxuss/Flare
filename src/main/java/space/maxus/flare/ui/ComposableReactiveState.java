package space.maxus.flare.ui;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.react.ReactiveState;

public class ComposableReactiveState<V> extends ReactiveState<V> {
    @Getter
    private final Composable selfReference;

    public ComposableReactiveState(V value, Composable selfReference) {
        super(value);
        this.selfReference = selfReference;
    }

    @Override
    public void set(@Nullable V newValue) {
        super.set(newValue);
        selfReference.markDirty();
    }
}
