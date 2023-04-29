package space.maxus.flare.ui;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.react.ReactiveState;

import java.util.concurrent.atomic.AtomicReference;

public class ComposableReactiveState<V> extends ReactiveState<V> {
    @Getter
    private final AtomicReference<Composable> selfReference;

    public ComposableReactiveState(V value, AtomicReference<Composable> selfReference) {
        super(value);
        this.selfReference = selfReference;
    }

    @Override
    public void set(@Nullable V newValue) {
        super.set(newValue);
        selfReference.get().markDirty();
    }
}
