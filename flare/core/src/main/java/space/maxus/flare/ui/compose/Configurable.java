package space.maxus.flare.ui.compose;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a composable that can be further configured after creation
 * @param <S> The type of the object being configured
 */
public interface Configurable<S> {
    /**
     * Configures this composable element.
     * @param configurator Configurator to be applied
     * @return The configured object
     */
    S configure(Configurator<S> configurator);

    /**
     * Configures this composable element with a certain subtype of the object.
     * @param configurator Configurator to be applied
     * @return The configured objecc
     * @param <V> The subtype of the object
     */
    @SuppressWarnings("unchecked")
    default <V extends S> V configureTyped(@NotNull Configurator<V> configurator) {
        configurator.configure((V) this);
        return (V) this;
    }
    
    interface Configurator<S> {
        void configure(S self);
    }
}
