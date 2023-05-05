package space.maxus.flare.ui.page;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.frames.ParamFrame;
import space.maxus.flare.ui.space.ComposableSpace;
import space.maxus.flare.ui.space.Slot;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@ToString @EqualsAndHashCode(callSuper = true)
public class PageFrame extends ParamFrame<PageFrame.Props> {
    private @Nullable ClickHandlerWrapper generic;
    private @Nullable ClickHandlerWrapper right;
    private @Nullable ClickHandlerWrapper left;
    private @Nullable ClickHandlerWrapper shift;
    private @Nullable DragHandler drag;

    protected PageFrame(@NotNull Props params) {
        super(params);
    }

    public void onClick(SimpleClickHandler simple) {
        this.generic = new ClickHandlerWrapper(simple, null);
    }

    public void cancellingOnShiftClick(SimpleClickHandler simple) {
        this.shift = new ClickHandlerWrapper(simple, null);
    }

    public void onShiftClick(BooleanReturningClickHandler bool) {
        this.shift = new ClickHandlerWrapper(null, bool);
    }

    public void cancellingOnLeftClick(SimpleClickHandler simple) {
        this.left = new ClickHandlerWrapper(simple, null);
    }

    public void onLeftClick(BooleanReturningClickHandler bool) {
        this.left = new ClickHandlerWrapper(null, bool);
    }

    public void cancellingOnRightClick(SimpleClickHandler simple) {
        this.right = new ClickHandlerWrapper(simple, null);
    }

    public void onRightClick(BooleanReturningClickHandler bool) {
        this.right = new ClickHandlerWrapper(null, bool);
    }

    public void onDrag(DragHandler drag) {
        this.drag = drag;
    }

    @Override
    public void init() {
        // initializer should be called lazily, on-demand, and not eagerly in `init`
    }

    void load() {
        this.composeAll(this.props.initData);
        this.props.initializer.accept(this);
    }

    record Props(int page, Map<ComposableSpace, Composable> initData, Consumer<PageFrame> initializer) { }

    @Override
    public void genericClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        if(this.generic != null)
            this.generic.click(slot, e);
    }

    @Override
    public boolean shiftClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        if(this.shift != null)
            return this.shift.click(slot, e);
        return true;
    }

    @Override
    public boolean leftClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        if(this.left != null)
            return this.left.click(slot, e);
        return true;
    }

    @Override
    public boolean rightClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        if(this.right != null)
            return this.right.click(slot, e);
        return true;
    }

    @Override
    public boolean drag(@NotNull Map<Slot, ItemStack> newItems, @NotNull InventoryDragEvent e) {
        if(this.drag != null)
            return this.drag.drag(newItems, e);
        return true;
    }

    @RequiredArgsConstructor
    static final class ClickHandlerWrapper {
        private final @Nullable SimpleClickHandler simple;
        private final @Nullable BooleanReturningClickHandler bool;

        public boolean click(Slot slot, InventoryClickEvent e) {
            if(simple == null)
                return Objects.requireNonNull(bool).click(slot, e);
            simple.click(slot, e);
            return true;
        }
    }

    public interface DragHandler {
        boolean drag(Map<Slot, ItemStack> slots, InventoryDragEvent e);
    }

    public interface SimpleClickHandler {
        void click(Slot slot, InventoryClickEvent e);
    }

    public interface BooleanReturningClickHandler {
        boolean click(Slot slot, InventoryClickEvent e);
    }
}
