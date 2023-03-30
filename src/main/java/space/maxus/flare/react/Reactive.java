package space.maxus.flare.react;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.text.Producer;
import space.maxus.flare.text.ReactiveComponent;

@UtilityClass
public class Reactive {
    public <V> @NotNull ReactiveComponent<V> text(@NotNull ReactiveState<V> state, @NotNull Producer<V, Component> producer) {
        Validate.notNull(state, "Tried to hook a ReactiveComponent to a null state!");
        Validate.notNull(producer, "Tried to hook a null producer to a ReactiveComponent");

        ReactiveComponent<V> component = ReactiveComponent.reactive(producer);
        state.subscribe(component);
        // Populating original value
        component.onStateChange(state.get());

        return component;
    }
}
