package space.maxus.flare.ui.compose.extra;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.ComposableReactiveState;
import space.maxus.flare.ui.compose.RootReferencing;

@ToString
@EqualsAndHashCode(callSuper = true)
final class CheckboxImpl extends RootReferencing implements Checkbox {
    @Override
    public void click(@NotNull InventoryClickEvent e) {
        if (e.getClick() == ClickType.DOUBLE_CLICK || isDisabled())
            return; // ignoring double clicks
        this.toggle();
        e.setCancelled(true);
    }

    private final ItemProvider checkedProvider;
    private final ItemProvider uncheckedProvider;
    private final ReactiveState<Boolean> disabledState;
    private final ReactiveState<Boolean> checkedState;

    private final ItemProvider branchingProvider;


    public CheckboxImpl(@Nullable ItemProvider checkedItem, @Nullable ItemProvider uncheckedItem, boolean isChecked, boolean isDisabled) {
        this.checkedProvider = checkedItem == null ? Checkbox.checkedItem("Checkbox", "") : checkedItem;
        this.uncheckedProvider = uncheckedItem == null ? Checkbox.uncheckedItem("Checkbox", "") : uncheckedItem;
        this.disabledState = new ComposableReactiveState<>(isDisabled, this);
        this.checkedState = new ComposableReactiveState<>(isChecked, this);
        this.branchingProvider = () -> this.checkedState.get() ? this.checkedProvider.provide() : this.uncheckedProvider.provide();
    }

    @Override
    public ReactiveState<Boolean> disabledState() {
        return disabledState;
    }

    @Override
    public ReactiveState<Boolean> checkedState() {
        return checkedState;
    }

    @Override
    public @NotNull ItemProvider getProvider() {
        return this.branchingProvider;
    }

    @Override
    public Checkbox configure(@NotNull Configurator<Checkbox> configurator) {
        configurator.configure(this);
        return this;
    }

    public static class Builder implements Checkbox.Builder {
        private @Nullable ItemProvider checkedItem;
        private @Nullable ItemProvider uncheckedItem;
        private boolean isChecked;
        private boolean isDisabled;

        @Override
        public Builder checkedItem(@Nullable ItemProvider checkedItem) {
            this.checkedItem = checkedItem;
            return this;
        }

        @Override
        public Builder uncheckedItem(@Nullable ItemProvider uncheckedItem) {
            this.uncheckedItem = uncheckedItem;
            return this;
        }

        @Override
        public Builder isChecked(boolean isChecked) {
            this.isChecked = isChecked;
            return this;
        }

        @Override
        public Builder isDisabled(boolean isDisabled) {
            this.isDisabled = isDisabled;
            return this;
        }

        @Override
        public Checkbox build() {
            return new CheckboxImpl(this.checkedItem, this.uncheckedItem, this.isChecked, this.isDisabled);
        }

        public String toString() {
            return "Checkbox.Builder(checkedItem=" + this.checkedItem + ", uncheckedItem=" + this.uncheckedItem + ", isChecked=" + this.isChecked + ", isDisabled=" + this.isDisabled + ")";
        }
    }
}
