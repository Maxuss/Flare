package space.maxus.flare.ui.page;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.Dimensions;
import space.maxus.flare.ui.frames.ParamFrame;
import space.maxus.flare.ui.space.ComposableSpace;
import space.maxus.flare.ui.space.Slot;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A Frame implementation used within {@link DefaultPagination}
 */
@ToString
@EqualsAndHashCode(callSuper = true)
public class PageFrame extends ParamFrame<PageFrame.Props> {
    private @Nullable ClickHandlerWrapper generic;
    private @Nullable ClickHandlerWrapper right;
    private @Nullable ClickHandlerWrapper left;
    private @Nullable ClickHandlerWrapper shift;
    private @Nullable DragHandler drag;
    private @Nullable String titleText;

    protected PageFrame(@NotNull Props params) {
        super(params, params.dim);
    }

    /**
     * Adds a generic click handler
     *
     * @param simple Generic click handler
     */
    public void onClick(SimpleClickHandler simple) {
        this.generic = new ClickHandlerWrapper(simple, null);
    }

    /**
     * Adds a cancelling shift click handler
     *
     * @param simple The handler
     */
    public void cancellingOnShiftClick(SimpleClickHandler simple) {
        this.shift = new ClickHandlerWrapper(simple, null);
    }

    /**
     * Adds a shift click handler
     *
     * @param bool The handler
     */
    public void onShiftClick(BooleanReturningClickHandler bool) {
        this.shift = new ClickHandlerWrapper(null, bool);
    }

    /**
     * Adds a cancelling left click handler
     *
     * @param simple The handler
     */
    public void cancellingOnLeftClick(SimpleClickHandler simple) {
        this.left = new ClickHandlerWrapper(simple, null);
    }

    /**
     * Adds a left click handler
     *
     * @param bool The handler
     */
    public void onLeftClick(BooleanReturningClickHandler bool) {
        this.left = new ClickHandlerWrapper(null, bool);
    }

    /**
     * Adds a cancelling right click handler
     *
     * @param simple The handler
     */
    public void cancellingOnRightClick(SimpleClickHandler simple) {
        this.right = new ClickHandlerWrapper(simple, null);
    }

    /**
     * Adds a right click handler
     *
     * @param bool The handler
     */
    public void onRightClick(BooleanReturningClickHandler bool) {
        this.right = new ClickHandlerWrapper(null, bool);
    }

    /**
     * Adds a drag event handler
     *
     * @param drag The handler
     */
    public void onDrag(DragHandler drag) {
        this.drag = drag;
    }

    /**
     * Sets the default title of this page
     *
     * @param title Title of the page
     */
    public void useTitle(@Nullable String title) {
        this.titleText = title;
    }

    @Override
    public void init() {
        this.titleText = this.props.initTitle == null ? "A Flare Page" : this.props.initTitle;
        // initializer should be called lazily, on-demand, and not eagerly in `init`
    }

    @ApiStatus.Internal
    public void load() {
        this.composeAll(this.props.initData);
        this.props.initializer.accept(this);
    }

    @Override
    public void genericClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        if (this.generic != null)
            this.generic.click(slot, e);
    }

    @Override
    public boolean shiftClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        if (this.shift != null)
            return this.shift.click(slot, e);
        return true;
    }

    @Override
    public boolean leftClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        if (this.left != null)
            return this.left.click(slot, e);
        return true;
    }

    @Override
    public boolean rightClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        if (this.right != null)
            return this.right.click(slot, e);
        return true;
    }

    @Override
    public boolean drag(@NotNull Map<Slot, ItemStack> newItems, @NotNull InventoryDragEvent e) {
        if (this.drag != null)
            return this.drag.drag(newItems, e);
        return true;
    }

    @Override
    public void setTitle(Player viewer, String title) {
        this.titleText = title;
        super.setTitle(viewer, title);
    }

    @Override
    public String getTitle() {
        return titleText == null ? "A Flare Page" : titleText;
    }

    @FunctionalInterface
    public interface DragHandler {
        boolean drag(Map<Slot, ItemStack> slots, InventoryDragEvent e);
    }

    @FunctionalInterface
    public interface SimpleClickHandler {
        void click(Slot slot, InventoryClickEvent e);
    }

    @FunctionalInterface
    public interface BooleanReturningClickHandler {
        boolean click(Slot slot, InventoryClickEvent e);
    }

    public record Props(int page, @Nullable String initTitle, Dimensions dim, Map<ComposableSpace, Composable> initData,
                        Consumer<PageFrame> initializer) {
    }

    @RequiredArgsConstructor
    static final class ClickHandlerWrapper {
        private final @Nullable SimpleClickHandler simple;
        private final @Nullable BooleanReturningClickHandler bool;

        public boolean click(Slot slot, InventoryClickEvent e) {
            if (simple == null)
                return Objects.requireNonNull(bool).click(slot, e);
            simple.click(slot, e);
            return true;
        }
    }
}
