package space.maxus.flare.ui.compose;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.PackedComposable;
import space.maxus.flare.ui.space.ComposableSpace;

import java.util.concurrent.atomic.AtomicReference;

/**
 * An abstract class for component implementations that automatically handles root frame injection
 */
public abstract class RootReferencing implements Composable {
    /**
     * The root frame
     */
    protected final AtomicReference<Frame> root = new AtomicReference<>(null);
    private @Nullable ComposableSpace attachedSpace;

    @Override
    public Frame root() {
        return root.get();
    }

    @Override
    public void injectRoot(Frame root) {
        this.root.set(root);
    }

    @Override
    public @NotNull PackedComposable inside(@NotNull ComposableSpace space) {
        this.attachedSpace = space;
        return Composable.super.inside(space);
    }

    @Override
    public void markDirty() {
        if (attachedSpace == null)
            Composable.super.markDirty();
        else
            root().markDirty(attachedSpace);
    }
}
