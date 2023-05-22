package space.maxus.flare.ui.space;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

/**
 * Represents space where composable elements can be rendered
 */
public interface ComposableSpace {
    /**
     * Enumerates all slots inside this space
     * @return All slots inside this composable space
     */
    Set<Slot> slots();

    /**
     * Returns the first and last points of this space. Used mostly within {@link Rect}
     * @return Pair of first and last points of this space
     */
    Pair<Slot, Slot> points();
}
