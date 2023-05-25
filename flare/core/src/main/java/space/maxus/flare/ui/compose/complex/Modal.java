package space.maxus.flare.ui.compose.complex;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.ComposableLike;
import space.maxus.flare.ui.Dimensions;
import space.maxus.flare.ui.compose.Configurable;
import space.maxus.flare.ui.compose.Disable;
import space.maxus.flare.ui.compose.ProviderRendered;
import space.maxus.flare.ui.page.PageFrame;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * A modal is a composable that opens an inner frame on click.
 * <br />
 * See more in Flare docs: <a href="https://flare.maxus.space/ui/composable#modal">Modal</a>
 *
 * @see Modals
 */
public interface Modal extends ProviderRendered, Configurable<Modal>, Disable {
    /**
     * Constructs a new modal
     * @param provider Item provider for modal
     * @param dimensions Dimensions of the modal window
     * @param initializer The modal frame configurator
     * @return A new modal
     */
    static @NotNull Modal of(@NotNull ItemProvider provider, Dimensions dimensions, Consumer<ModalFrame> initializer) {
        return new ModalImpl(provider, "A Flare Modal", initializer, dimensions, false);
    }

    /**
     * Constructs a new modal builder
     * @param provider Item provider for this modal
     * @return A new modal builder
     */
    static @NotNull Builder builder(@NotNull ItemProvider provider) {
        return new ModalImpl.Builder(provider);
    }

    /**
     * Returns the frame of this modal
     * @return The frame of this modal
     */
    ModalFrame getFrame();

    /**
     * A builder for modals
     */
    interface Builder extends ComposableLike {
        /**
         * Sets the title of the modal frame
         * @param title The title of the modal frame
         * @return This builder
         */
        @NotNull Builder title(@NotNull String title);

        /**
         * Sets the dimensions of the modal frame
         * @param dimensions Dimensions of the modal frame
         * @return This builder
         */
        @NotNull Builder dimensions(@NotNull Dimensions dimensions);

        /**
         * Sets the modal frame initializer
         * @param initializer The initializer for the modal frame
         * @return This builder
         */
        @NotNull Builder initializer(@NotNull Consumer<ModalFrame> initializer);

        /**
         * Disables the modal button
         * @param disabled Disabled state
         * @return This builder
         */
        @NotNull Builder disabled(boolean disabled);

        /**
         * Builds this modal
         * @return Built modal
         */
        @NotNull Modal build();

        @Override
        default Composable asComposable() {
            return build();
        }
    }

    /**
     * The modal frame
     */
    @EqualsAndHashCode(callSuper = true)
    @ToString
    abstract class ModalFrame extends PageFrame {
        @ApiStatus.Internal
        @Getter
        protected final AtomicBoolean isClosing = new AtomicBoolean(false);
        /**
         * The parent modal used
         */
        @Getter
        protected Modal parent;

        protected ModalFrame(@NotNull ModalProps props) {
            super(new PageFrame.Props(0, props.title, props.dimensions, Collections.emptyMap(), frame -> props.initializer.accept((ModalFrame) frame)));
            this.parent = props.self;
        }

        @Override
        public void close() {
            this.composed.values().forEach(Composable::destroy);
        }
    }

    @ApiStatus.Internal
    record ModalProps(Modal self, Dimensions dimensions, String title, Consumer<ModalFrame> initializer) {

    }
}
