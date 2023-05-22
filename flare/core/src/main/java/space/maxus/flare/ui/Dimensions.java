package space.maxus.flare.ui;

import lombok.Getter;
import org.jetbrains.annotations.ApiStatus;

/**
 * Possible dimensions for Flare UIs
 */
public enum Dimensions {
    /**
     * 3x9
     */
    THREE_BY_NINE(3, 9),
    /**
     * 4x9
     */
    FOUR_BY_NINE(4, 9),
    /**
     * 5x9
     */
    FIVE_BY_NINE(5, 9),
    /**
     * 6x9
     */
    SIX_BY_NINE(6, 9),

    /**
     * 3x3
     * <br />
     * @apiNote These dimensions are <b>experimental</b>, and may not work as expected.
     */
    @ApiStatus.Experimental
    THREE_BY_THREE(3, 3),

    ;

    /**
     * Total size of these dimensions in slots
     */
    @Getter
    private final int totalSize;
    /**
     * Amount of rows in these dimensions
     */
    @Getter
    private final int rows;
    /**
     * Amount of columns in these dimensions
     */
    @Getter
    private final int columns;

    Dimensions(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.totalSize = rows * columns;
    }
}
