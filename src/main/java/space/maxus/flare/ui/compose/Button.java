package space.maxus.flare.ui.compose;


import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.react.ReactiveState;

import java.util.function.BiConsumer;

public sealed interface Button extends ProviderRendered, Configurable<Button> permits ButtonImpl {
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

    default boolean isDisabled() {
        return disabledState().get();
    }

    default void setDisabled(boolean disabled) {
        disabledState().set(disabled);
    }

    default boolean isNotDisabled() {
        return !disabledState().get();
    }

    ReactiveState<Boolean> disabledState();

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
