package space.maxus.flare.ui;

import lombok.Getter;

public enum Dimensions {
    THREE_BY_NINE(3, 9),
    FOUR_BY_NINE(4, 9),
    FIVE_BY_NINE(5, 9),
    SIX_BY_NINE(6, 9)

    ;

    @Getter
    private final int totalSize;

    Dimensions(int rows, int columns) {
        this.totalSize = rows * columns;
    }
}
