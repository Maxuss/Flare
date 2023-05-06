package space.maxus.flare.ui.compose;

import lombok.Getter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.react.ReactivityProvider;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.ComposableReactiveState;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.PackedComposable;
import space.maxus.flare.ui.compose.complex.Composition;
import space.maxus.flare.ui.space.ComposableSpace;
import space.maxus.flare.ui.space.Slot;
import space.maxus.flare.util.FlareUtil;

import java.util.Objects;

public abstract class FunctionComposable<P> implements Composable, ReactivityProvider {
    protected P props;
    @Getter
    private @Nullable Composable composed;

    public FunctionComposable(P props) {
        this.props = props;
    }

    public final void activate() {
        this.composed = this.compose();
    }

    public abstract @NotNull Composable compose();

    @Override
    public Frame root() {
        return Objects.requireNonNull(this.composed).root();
    }

    @Override
    public void injectRoot(Frame root) {
        // we activate the component at root injection (if it has not been activated before)
        if (this.composed == null)
            this.activate();
        this.composed.injectRoot(root);
    }

    @Override
    public ItemStack renderAt(Slot slot) {
        return Objects.requireNonNull(composed).renderAt(slot);
    }

    @Override
    public void markDirty() {
        ComposableSpace space = FlareUtil.keyFromValue(root().composableMap(), this);
        if(this.composed != null && space != null) {
            root().markDirty(space); // FCs are not inlined, so we must mark space specifically as dirty
        }
    }

    @Override
    public @NotNull PackedComposable inside(@NotNull ComposableSpace space) {
        if (this.composed == null) // component has to be activated here
            this.activate();
        if (this.composed instanceof Composition composition)
            composition.fitIn(space);
        else
            composed.inside(space);
        return new PackedComposable(space, this);
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
        return this.handleShiftFrom(e) && Objects.requireNonNull(this.composed).shiftFrom(e);
    }

    @Override
    public boolean shiftInto(@NotNull ItemStack stack, @NotNull InventoryClickEvent e) {
        return this.handleShiftInto(stack, e) && Objects.requireNonNull(this.composed).shiftInto(stack, e);
    }

    @Override
    public boolean leftClick(@NotNull InventoryClickEvent e) {
        return this.handleLeftClick(e) && Objects.requireNonNull(this.composed).leftClick(e);
    }

    @Override
    public boolean rightClick(@NotNull InventoryClickEvent e) {
        return this.handleRightClick(e) && Objects.requireNonNull(this.composed).rightClick(e);
    }

    @Override
    public void click(@NotNull InventoryClickEvent e) {
        this.handleClick(e);
        Objects.requireNonNull(this.composed).click(e);
    }

    @Override
    public <V> ReactiveState<V> useState(@Nullable V initial) {
        return new ComposableReactiveState<>(initial, this);
    }

    @Override
    public <V> ReactiveState<V> useUnboundState(@Nullable V initial) {
        return new ReactiveState<>(initial);
    }
}
