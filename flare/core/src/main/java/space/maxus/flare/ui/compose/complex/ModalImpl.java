package space.maxus.flare.ui.compose.complex;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.Dimensions;
import space.maxus.flare.ui.compose.RootReferencing;

import java.util.function.Consumer;

final class ModalImpl extends RootReferencing implements Modal {
    private final ReactiveState<Boolean> disabledState;
    @Getter
    private final ItemProvider provider;
    private final @NotNull String title;
    private final @NotNull Consumer<ModalFrame> initializer;
    private final @NotNull Dimensions dimensions;

    public ModalImpl(@NotNull ItemProvider provider, @Nullable String title, @NotNull Consumer<ModalFrame> initializer, @NotNull Dimensions dimensions, boolean disabled) {
        this.provider = provider;
        this.title = title == null ? "A Frame Modal" : title;
        this.initializer = initializer;
        this.disabledState = new ReactiveState<>(disabled);
        this.dimensions = dimensions;
    }

    @Override
    public Modal configure(Configurator<Modal> configurator) {
        configurator.configure(this);
        return this;
    }

    @Override
    public ReactiveState<Boolean> disabledState() {
        return disabledState;
    }

    @Contract(" -> new")
    @Override
    public @NotNull ModalFrame getFrame() {
        return new ModalFrameImpl(new ModalProps(this, dimensions, title, initializer));
    }

    @Override
    public void click(@NotNull InventoryClickEvent e) {
        if (isDisabled())
            return;
        e.setCancelled(true);
        ModalFrame frame = getFrame();
        root().switchFrame(frame);
        frame.load();
        frame.render();
        frame.refreshTitle();
    }

    @RequiredArgsConstructor
    static final class Builder implements Modal.Builder {
        private final @NotNull ItemProvider item;
        private boolean disabled = false;
        private @Nullable String title;
        private @Nullable Dimensions dimensions;
        private @Nullable Consumer<ModalFrame> initializer;

        @Override
        public Modal.@NotNull Builder title(@NotNull String title) {
            this.title = title;
            return this;
        }

        @Override
        public Modal.@NotNull Builder dimensions(@NotNull Dimensions dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        @Override
        public Modal.@NotNull Builder initializer(@NotNull Consumer<ModalFrame> initializer) {
            this.initializer = initializer;
            return this;
        }

        @Override
        public Modal.@NotNull Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        @Override
        public @NotNull Modal build() {
            return new ModalImpl(item, title, initializer == null ? modal -> {
            } : initializer, dimensions == null ? Dimensions.SIX_BY_NINE : dimensions, disabled);
        }
    }

    static final class ModalFrameImpl extends ModalFrame {
        ModalFrameImpl(@NotNull ModalProps params) {
            super(params);
        }
    }
}
