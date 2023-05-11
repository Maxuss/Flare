package space.maxus.flare.ui;

import lombok.Getter;

public enum Dimensions {
    THREE_BY_NINE(3, 9),
    FOUR_BY_NINE(4, 9),
    FIVE_BY_NINE(5, 9),
    SIX_BY_NINE(6, 9);

    @Getter
    private final int totalSize;
    @Getter
    private final int rows;
    @Getter
    private final int columns;

    Dimensions(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.totalSize = rows * columns;
    }
}
