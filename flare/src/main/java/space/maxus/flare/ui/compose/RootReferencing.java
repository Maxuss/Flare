package space.maxus.flare.ui.compose;

import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.Frame;

import java.util.concurrent.atomic.AtomicReference;

public abstract class RootReferencing implements Composable {
    protected final AtomicReference<Frame> root = new AtomicReference<>(null);

    @Override
    public Frame root() {
        return root.get();
    }

    @Override
    public void injectRoot(Frame root) {
        this.root.set(root);
    }
}
