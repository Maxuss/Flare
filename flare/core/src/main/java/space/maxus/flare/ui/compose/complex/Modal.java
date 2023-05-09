package space.maxus.flare.ui.compose.complex;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
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

public interface Modal extends ProviderRendered, Configurable<Modal>, Disable {
    static @NotNull Modal of(@NotNull ItemProvider provider, Dimensions dimensions, Consumer<ModalFrame> initializer) {
        return new ModalImpl(provider, "A Flare Modal", initializer, dimensions, false);
    }

    static @NotNull Builder builder(@NotNull ItemProvider provider) {
        return new ModalImpl.Builder(provider);
    }

    ModalFrame getFrame();

    interface Builder extends ComposableLike {
        @NotNull Builder title(@NotNull String title);

        @NotNull Builder dimensions(@NotNull Dimensions dimensions);

        @NotNull Builder initializer(@NotNull Consumer<ModalFrame> initializer);

        @NotNull Builder disabled(boolean disabled);

        @NotNull Modal build();

        @Override
        default Composable asComposable() {
            return build();
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @ToString
    abstract class ModalFrame extends PageFrame {
        @Getter
        protected final AtomicBoolean isClosing = new AtomicBoolean(false);
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

    record ModalProps(Modal self, Dimensions dimensions, String title, Consumer<ModalFrame> initializer) {

    }
}
