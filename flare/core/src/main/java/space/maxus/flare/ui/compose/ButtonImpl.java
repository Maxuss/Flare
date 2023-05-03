package space.maxus.flare.ui.compose;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.ComposableReactiveState;

final class ButtonImpl extends RootReferencing implements Button {
    private final ReactiveState<Boolean> disabledState;
    @Getter
    private final ItemProvider provider;
    private final ClickHandler genericClick;
    private final ClickHandler rightClick;
    private final ClickHandler leftClick;
    private final ClickHandler shiftClick;

    ButtonImpl(boolean disabled, @NotNull ItemProvider provider, ClickHandler genericClick, ClickHandler rightClick, ClickHandler leftClick, ClickHandler shiftClick) {
        this.disabledState = new ComposableReactiveState<>(disabled, this);
        this.provider = provider;
        this.genericClick = genericClick;
        this.rightClick = rightClick;
        this.leftClick = leftClick;
        this.shiftClick = shiftClick;
    }

    @Override
    public void click(@NotNull InventoryClickEvent e) {
        if (this.isNotDisabled() && e.getClick() != ClickType.DOUBLE_CLICK) // skipping double clicks
            this.genericClick.click(this, e);
    }

    @Override
    public boolean leftClick(@NotNull InventoryClickEvent e) {
        return this.isNotDisabled() && this.leftClick.click(this, e);
    }

    @Override
    public boolean rightClick(@NotNull InventoryClickEvent e) {
        return this.isNotDisabled() && this.rightClick.click(this, e);
    }

    @Override
    public boolean shiftFrom(@NotNull InventoryClickEvent e) {
        return this.isNotDisabled() && this.shiftClick.click(this, e);
    }

    @Override
    public Button configure(Configurator<Button> configurator) {
        configurator.configure(this);
        return this;
    }

    @Override
    public ReactiveState<Boolean> disabledState() {
        return disabledState;
    }

    @RequiredArgsConstructor
    static class ButtonBuilderImpl implements Button.Builder {
        private final ItemProvider provider;

        private boolean disabled = false;
        private ClickHandler genericClick = ClickHandler.noop();
        private ClickHandler rightClick = ClickHandler.noop();
        private ClickHandler leftClick = ClickHandler.noop();
        private ClickHandler shiftClick = ClickHandler.noop();

        @Override
        public Button build() {
            return new ButtonImpl(disabled, provider, genericClick, rightClick, leftClick, shiftClick);
        }

        @Override
        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        @Override
        public Builder onClick(ClickHandler handler) {
            this.genericClick = handler;
            return this;
        }

        @Override
        public Builder onRightClick(ClickHandler handler) {
            this.rightClick = handler;
            return this;
        }

        @Override
        public Builder onLeftClick(ClickHandler handler) {
            this.leftClick = handler;
            return this;
        }

        @Override
        public Builder onShiftClick(ClickHandler handler) {
            this.shiftClick = handler;
            return this;
        }
    }
}
