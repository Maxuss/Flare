package space.maxus.flare.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

final class ReactiveComponentImpl<V> implements ReactiveComponent<V> {
    private final AtomicReference<Component> parentRef = new AtomicReference<>(Component.empty());
    private final Producer<V, Component> producer;

    ReactiveComponentImpl(Producer<V, Component> producer) {
        this.producer = producer;
    }

    @Override
    public @NotNull TextComponent getParent() {
        return Component.empty().append(parentRef.get());
    }

    @Override
    public @Unmodifiable @NotNull List<Component> children() {
        return getParent().children();
    }

    @Override
    public @NotNull TextComponent children(@NotNull List<? extends ComponentLike> children) {
        return getParent().children(children);
    }

    @Override
    public @NotNull Style style() {
        return parentRef.get().style();
    }

    @Override
    public @NotNull TextComponent style(@NotNull Style style) {
        return getParent().style(style);
    }

    @Override
    public void onStateChange(@Nullable V state) {
        parentRef.setRelease(producer.produce(state));
    }

    @Override
    public @NotNull String content() {
        return getParent().content();
    }

    @Override
    public @NotNull ReactiveComponent<V> content(@NotNull String content) {
        /// This component is immutable so we do nothing
        return this;
    }

    @Override
    public @NotNull Builder toBuilder() {
        return getParent().toBuilder();
    }

    @Override
    public @NotNull Component asComponent() {
        return parentRef.get();
    }
}
