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
 * <br />
 * See more in Flare docs: <a href="https://flare.maxus.space/ui/composable#button">Button</a>
 */
public interface Button extends Disable, ProviderRendered, Configurable<Button> {
    /**
     * Creates a new button builder.
     * @param item The item provider to use.
     * @param disabled Whether the button should be disabled
     * @return The builder
     */
    static Builder builder(ItemProvider item, boolean disabled) {
        return new ButtonImpl.ButtonBuilderImpl(item).disabled(disabled);
    }

    /**
     * Creates a new button builder.
     * @param item The item provider to use
     * @return The builder
     */
    @Contract(value = "_ -> new", pure = true)
    static @NotNull Builder builder(ItemProvider item) {
        return new ButtonImpl.ButtonBuilderImpl(item);
    }

    /**
     * Creates a new button
     * @param item The item provider to use
     * @param onClick The click handler
     * @return The button
     */
    static Button of(ItemProvider item, ClickHandler onClick) {
        return new ButtonImpl.ButtonBuilderImpl(item).onClick(onClick).build();
    }

    @FunctionalInterface
    interface ClickHandler {
        /**
         * A no-op click handler
         */
        @Contract(pure = true)
        static @NotNull ClickHandler noop() {
            return (btn, e) -> true;
        }

        /**
         * A click handler that always cancels event
         * @param handler Inner handler
         * @return Handler
         */
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

    /**
     * The button builder
     */
    interface Builder extends ComposableLike {
        /**
         * Builds the button
         * @return The button
         */
        Button build();

        /**
         * Sets whether the button is disabled
         * @param disabled Whether the button is disabled
         * @return This builder
         */
        Builder disabled(boolean disabled);

        /**
         * Sets the click handler
         * @param handler The click handler
         * @return This builder
         */
        Builder onClick(ClickHandler handler);

        /**
         * Sets the right click handler
         * @param handler The click handler
         * @return This builder
         */
        Builder onRightClick(ClickHandler handler);

        /**
         * Sets the left click handler
         * @param handler The click handler
         * @return This builder
         */
        Builder onLeftClick(ClickHandler handler);

        /**
         * Sets the shift click handler
         * @param handler The click handler
         * @return This builder
         */
        Builder onShiftClick(ClickHandler handler);

        /**
         * Sets the click handler that cancels event
         * @param handler The click handler
         * @return This builder
         */
        default Builder onClickCancelling(BiConsumer<Button, InventoryClickEvent> handler) {
            return onClick(ClickHandler.cancelling(handler));
        }

        /**
         * Sets the right click handler that cancels event
         * @param handler The click handler
         * @return This builder
         */
        default Builder onRightClickCancelling(BiConsumer<Button, InventoryClickEvent> handler) {
            return onRightClick(ClickHandler.cancelling(handler));
        }

        /**
         * Sets the left click handler that cancels event
         * @param handler The click handler
         * @return This builder
         */
        default Builder onLeftClickCancelling(BiConsumer<Button, InventoryClickEvent> handler) {
            return onLeftClick(ClickHandler.cancelling(handler));
        }

        /**
         * Sets the shift click handler that cancels event
         * @param handler The click handler
         * @return This builder
         */
        default Builder onShiftClickCancelling(BiConsumer<Button, InventoryClickEvent> handler) {
            return onShiftClick(ClickHandler.cancelling(handler));
        }

        @Override
        default Composable asComposable() {
            return build();
        }
    }
}
