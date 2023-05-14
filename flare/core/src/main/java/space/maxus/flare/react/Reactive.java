package space.maxus.flare.react;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.concurrent.Computable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.Flare;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.ReactiveItemProvider;
import space.maxus.flare.text.ReactiveComponent;

/**
 * A utility class for reactivity within Flare.
 */
@UtilityClass
public class Reactive {
    /**
     * Creates a reactive component from a state and a producer.
     * @param state The reactive state to hook to
     * @param producer The producer to use to produce the reactive component
     * @return The reactive component
     * @param <V> The type of the reactive state
     */
    public <V> @NotNull ReactiveComponent<V> text(@NotNull ReactiveState<V> state, @NotNull Computable<@Nullable V, Component> producer) {
        Validate.notNull(state, "Tried to hook a ReactiveComponent to a null state!");
        Validate.notNull(producer, "Tried to hook a null producer to a ReactiveComponent");

        ReactiveComponent<V> component = ReactiveComponent.reactive(producer);
        state.subscribe(component);
        // Populating original value
        try {
            component.onStateChange(state.getOrNull());
        } catch (ReactiveException e) {
            Flare.logger().error("Error while populating reactive component", e);
        }

        return component;
    }

    /**
     * Creates a reactive item provider from a state and a provider.
     * This is (partially) a wrapper around {@link ItemProvider#reactive(Computable)}
     * @param state The reactive state to hook to
     * @param provider The provider to use to produce the item reactively
     * @return The reactive item provider
     * @param <V> The type of the reactive state
     */
    public <V> @NotNull ReactiveItemProvider<V> item(@NotNull ReactiveState<V> state, @NotNull Computable<@Nullable V, @Nullable ItemStack> provider) {
        Validate.notNull(state, "Tried to hook an ItemProvider to a null state!");
        Validate.notNull(provider, "Tried to hook a null provider to an ItemProvider");

        ReactiveItemProvider<V> item = ItemProvider.reactive(provider);
        state.subscribe(item);
        // Populating original value
        item.onStateChange(state.getOrNull());

        return item;
    }
}
