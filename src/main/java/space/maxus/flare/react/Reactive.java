package space.maxus.flare.react;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.concurrent.Computable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.ReactiveItemProvider;
import space.maxus.flare.text.ReactiveComponent;

@UtilityClass
public class Reactive {
    public <V> @NotNull ReactiveComponent<V> text(@NotNull ReactiveState<V> state, @NotNull Computable<@Nullable V, Component> producer) {
        Validate.notNull(state, "Tried to hook a ReactiveComponent to a null state!");
        Validate.notNull(producer, "Tried to hook a null producer to a ReactiveComponent");

        ReactiveComponent<V> component = ReactiveComponent.reactive(producer);
        state.subscribe(component);
        // Populating original value
        component.onStateChange(state.getOrNull());

        return component;
    }

    @SuppressWarnings("unchecked")
    public <V> @NotNull ItemProvider item(@NotNull ReactiveState<V> state, @NotNull Computable<@Nullable V, @Nullable ItemStack> provider) {
        Validate.notNull(state, "Tried to hook an ItemProvider to a null state!");
        Validate.notNull(provider, "Tried to hook a null provider to an ItemProvider");

        ReactiveItemProvider<V> item = (ReactiveItemProvider<V>) ItemProvider.reactive(provider);
        state.subscribe(item);
        // Populating original value
        item.onStateChange(state.getOrNull());

        return item;
    }
}
