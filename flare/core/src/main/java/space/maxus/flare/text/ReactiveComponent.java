package space.maxus.flare.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang3.concurrent.Computable;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.react.ReactiveSubscriber;

import javax.annotation.concurrent.ThreadSafe;

/**
 * A reactive component can be subscribed to a ReactiveState, and will recalculate itself based on state change.
 * @param <V> Value of the reactive state.
 */
@ThreadSafe
public sealed interface ReactiveComponent<V> extends ReactiveSubscriber<V>, TextComponent permits ReactiveComponentImpl {
    /**
     * Constructs a new non-subscribed empty reactive component
     * @param producer Producer to be used for component building
     * @return New reactive component
     * @param <T> Type of the reactive state value
     */
    static <T> @NotNull ReactiveComponent<T> reactive(Computable<T, Component> producer) {
        return new ReactiveComponentImpl<>(producer);
    }

    /**
     * Gets parent text component that will be mutated on state change
     * @return Parent text component
     */
    @NotNull TextComponent getParent();
}
