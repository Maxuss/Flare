package space.maxus.flare.ui;

import space.maxus.flare.ui.space.ComposableSpace;

/**
 * Represents a structure that can represent itself as a Composable
 */
public interface ComposableLike {
    /**
     * Converts self to a composable
     * @return The composable
     */
    Composable asComposable();

    /**
     * Positions itself inside a space, essentially returning a {@link PackedComposable}
     * @param space The space to position in
     * @return The packed composable
     */
    default PackedComposable inside(ComposableSpace space) {
        return asComposable().inside(space);
    }
}
