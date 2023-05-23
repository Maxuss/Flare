package space.maxus.flare.ui.compose;


import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.ComposableLike;

import java.util.function.BiConsumer;

/**
 * A button is a simple clickable component.
 * <br /> &nbsp;
 *
 */
public interface Button extends Disable, ProviderRendered, Configurable<Button> {
    static Builder builder(ItemProvider item, boolean disabled) {
        return new ButtonImpl.ButtonBuilderImpl(item).disabled(disabled);
    }

    @Contract(value = "_ -> new", pure = true)
    static @NotNull Builder builder(ItemProvider item) {
        return new ButtonImpl.ButtonBuilderImpl(item);
    }

    static Button of(ItemProvider item, ClickHandler onClick) {
        return new ButtonImpl.ButtonBuilderImpl(item).onClick(onClick).build();
    }

    static @NotNull Checkbox checkbox(ItemProvider whenChecked, ItemProvider whenUnchecked) {
        return Checkbox.of(whenChecked, whenUnchecked);
    }

    static @NotNull Checkbox checkbox(ItemProvider whenChecked, ItemProvider whenUnchecked, boolean checked) {
        return Checkbox.of(whenChecked, whenUnchecked, checked);
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

    interface Builder extends ComposableLike {
        Button build();

        Builder disabled(boolean disabled);

        Builder onClick(ClickHandler handler);

        Builder onRightClick(ClickHandler handler);

        Builder onLeftClick(ClickHandler handler);

        Builder onShiftClick(ClickHandler handler);

        default Builder onClickCancelling(BiConsumer<Button, InventoryClickEvent> handler) {
            return onClick(ClickHandler.cancelling(handler));
        }

        default Builder onRightClickCancelling(BiConsumer<Button, InventoryClickEvent> handler) {
            return onRightClick(ClickHandler.cancelling(handler));
        }

        default Builder onLeftClickCancelling(BiConsumer<Button, InventoryClickEvent> handler) {
            return onLeftClick(ClickHandler.cancelling(handler));
        }

        default Builder onShiftClickCancelling(BiConsumer<Button, InventoryClickEvent> handler) {
            return onShiftClick(ClickHandler.cancelling(handler));
        }

        @Override
        default Composable asComposable() {
            return build();
        }
    }
}
