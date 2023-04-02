package space.maxus.flare.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang3.concurrent.Computable;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.react.ReactiveSubscriber;

public sealed interface ReactiveComponent<V> extends ReactiveSubscriber<V>, TextComponent permits ReactiveComponentImpl {
    @NotNull TextComponent getParent();

    static <T> @NotNull ReactiveComponent<T> reactive(Computable<T, Component> producer) {
        return new ReactiveComponentImpl<>(producer);
    }
}
