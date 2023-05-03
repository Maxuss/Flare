package space.maxus.flare.ui.compose;

import org.jetbrains.annotations.NotNull;

public interface Configurable<S> {
    S configure(Configurator<S> configurator);

    @SuppressWarnings("unchecked")
    default <V extends S> V configureTyped(@NotNull Configurator<V> configurator) {
        configurator.configure((V) this);
        return (V) this;
    }

    interface Configurator<S> {
        void configure(S self);
    }
}
