package space.maxus.flare.ui.compose;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.react.ReactivityProvider;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.ComposableReactiveState;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.space.Slot;

import java.util.concurrent.atomic.AtomicReference;

public abstract class FunctionComposable<P> implements Composable, ReactivityProvider {
    private Composable composed;
    protected P props;

    public FunctionComposable(P props) {
        this.activate(props);
    }

    public final void activate(P props) {
        this.props = props;
        this.composed = this.compose();
    }

    public abstract @NotNull Composable compose();

    @Override
    public Frame root() {
        return this.composed.root();
    }

    @Override
    public void injectRoot(Frame root) {
        this.composed.injectRoot(root);
    }

    @Override
    public ItemStack renderAt(Slot slot) {
        return composed.renderAt(slot);
    }

    @Override
    public void markDirty() {
        this.composed.markDirty();
    }

    public boolean handleShiftFrom(@NotNull InventoryClickEvent e) {
        return true;
    }

    public boolean handleShiftInto(@NotNull ItemStack stack, @NotNull InventoryClickEvent e) {
        return true;
    }

    public boolean handleLeftClick(@NotNull InventoryClickEvent e) {
        return true;
    }

    public boolean handleRightClick(@NotNull InventoryClickEvent e) {
        return true;
    }

    public void handleClick(@NotNull InventoryClickEvent e) {
        // noop
    }

    @Override
    public final boolean shiftFrom(@NotNull InventoryClickEvent e) {
        return this.handleShiftFrom(e) && this.composed.shiftFrom(e);
    }

    @Override
    public boolean shiftInto(@NotNull ItemStack stack, @NotNull InventoryClickEvent e) {
        return this.handleShiftInto(stack, e) && this.composed.shiftInto(stack, e);
    }

    @Override
    public boolean leftClick(@NotNull InventoryClickEvent e) {
        return this.handleLeftClick(e) && this.composed.leftClick(e);
    }

    @Override
    public boolean rightClick(@NotNull InventoryClickEvent e) {
        return this.handleRightClick(e) && this.composed.rightClick(e);
    }

    @Override
    public void click(@NotNull InventoryClickEvent e) {
        this.handleClick(e);
        this.composed.click(e);
    }

    @Override
    public <V> ReactiveState<V> useState(@Nullable V initial) {
        return new ComposableReactiveState<>(initial, new AtomicReference<>(this));
    }
}
