package space.maxus.flare.ui;

import lombok.Data;
import space.maxus.flare.ui.space.ComposableSpace;

/**
 * A data class containing composable and space in which it will be placed
 *
 * @see ComposableLike#inside(ComposableSpace)
 */
@Data
public class PackedComposable {
    /**
     * Space for the composable
     */
    private final ComposableSpace space;
    /**
     * The composable itself
     */
    private final Composable composable;
}
