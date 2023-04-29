package space.maxus.flare.ui.compose;

import lombok.extern.slf4j.Slf4j;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.Frame;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public abstract class RootReferencing implements Composable {
    protected final AtomicReference<Frame> root = new AtomicReference<>(null);

    @Override
    public Frame root() {
        return root.get();
    }

    @Override
    public void injectRoot(Frame root) {
        log.info("INJECTING ROOT");
        this.root.set(root);
    }
}
