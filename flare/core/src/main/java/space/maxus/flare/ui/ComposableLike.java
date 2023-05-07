package space.maxus.flare.ui;

import space.maxus.flare.ui.space.ComposableSpace;

public interface ComposableLike {
    Composable asComposable();
    default PackedComposable inside(ComposableSpace space) {
        return asComposable().inside(space);
    }
}
