package space.maxus.flare.ui.compose;

public interface Configurable<S> {
    S configure(Configurator<S> configurator);

    interface Configurator<S> {
        void configure(S self);
    }
}
