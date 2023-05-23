package space.maxus.flare.ui.compose;

import space.maxus.flare.react.ReactiveState;

/**
 * A super interface for composable elements that can be disabled
 */
public interface Disable {
    /**
     * Returns whether the element is disabled
     * @return Whether the element is disabled
     */
    default boolean isDisabled() {
        return disabledState().get();
    }

    /**
     * Sets whether the element is disabled
     * @param disabled Whether the element is disabled
     */
    default void setDisabled(boolean disabled) {
        disabledState().set(disabled);
    }

    /**
     * Returns whether the element is not disabled
     * @return Whether the element is not disabled
     */
    default boolean isNotDisabled() {
        return !disabledState().get();
    }

    /**
     * Returns the reactive disabled state of the element
     * @return The reactive disabled state of the element
     */
    ReactiveState<Boolean> disabledState();
}
