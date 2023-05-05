package space.maxus.flare.ui.compose;


import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;

import java.util.function.BiConsumer;

public interface Button extends Disable, ProviderRendered, Configurable<Button> {
    static Builder builder(ItemProvider item, boolean disabled) {
        return new ButtonImpl.ButtonBuilderImpl(item).disabled(disabled);
    }

    @Contract(value = "_ -> new", pure = true)
    static @NotNull Builder builder(ItemProvider item) {
        return new ButtonImpl.ButtonBuilderImpl(item);
    }

    static Button create(ItemProvider item, ClickHandler onClick) {
        return new ButtonImpl.ButtonBuilderImpl(item).onClick(onClick).build();
    }

    static @NotNull Checkbox checkbox(ItemProvider whenChecked, ItemProvider whenUnchecked) {
        return Checkbox.create(whenChecked, whenUnchecked);
    }

    static @NotNull Checkbox checkbox(ItemProvider whenChecked, ItemProvider whenUnchecked, boolean checked) {
        return Checkbox.create(whenChecked, whenUnchecked, checked);
    }

    static @NotNull Checkbox.Builder checkbox() {
        return Checkbox.builder();
    }

    @FunctionalInterface
    interface ClickHandler {
        @Contract(pure = true)
        static @NotNull ClickHandler noop() {
            return (btn, e) -> true;
        }

        static @NotNull ClickHandler cancelling(BiConsumer<Button, InventoryClickEvent> handler) {
            return (self, e) -> {
                handler.accept(self, e);
                return true;
            };
        }

        boolean click(@NotNull Button self, @NotNull InventoryClickEvent e);
    }

    interface CancellingClickHandler extends ClickHandler {
        void trueClick(@NotNull Button self, @NotNull InventoryClickEvent e);

        @Override
        default boolean click(@NotNull Button self, @NotNull InventoryClickEvent e) {
            trueClick(self, e);
            return true;
        }
    }

    interface Builder {
        Button build();

        Builder disabled(boolean disabled);

        Builder onClick(ClickHandler handler);

        Builder onRightClick(ClickHandler handler);

        Builder onLeftClick(ClickHandler handler);

        Builder onShiftClick(ClickHandler handler);
    }
}
