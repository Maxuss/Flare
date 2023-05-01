package space.maxus.flare.ui.compose;

import space.maxus.flare.react.ReactiveState;

public interface Disable {
    default boolean isDisabled() {
        return disabledState().get();
    }

    default void setDisabled(boolean disabled) {
        disabledState().set(disabled);
    }

    default boolean isNotDisabled() {
        return !disabledState().get();
    }

    ReactiveState<Boolean> disabledState();
}
