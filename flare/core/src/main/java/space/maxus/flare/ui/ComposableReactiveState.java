package space.maxus.flare.ui;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.react.ReactiveState;

/**
 * Represents a reactive state that is bound to certain composable, and will mark it dirty on each state change.
 * @param <V> The type of the value this state holds.
 */
public class ComposableReactiveState<V> extends ReactiveState<V> {
    /**
     * Reference to the related composable.
     */
    @Getter
    private final Composable selfReference;

    /**
     * Constructs a new ComposableReactiveState.
     * @param value The initial value of the state.
     * @param selfReference The related composable.
     */
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
