package space.maxus.flare.ui.space;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

@Data
@AllArgsConstructor
public class Slot implements Comparable<Slot>, ComposableSpace {
    // Row 1 ---
    public static final Slot ROW_ONE_SLOT_ONE = new Slot(0, 0);
    public static final Slot ROW_ONE_SLOT_TWO = new Slot(0, 1);
    public static final Slot ROW_ONE_SLOT_THREE = new Slot(0, 2);
    public static final Slot ROW_ONE_SLOT_FOUR = new Slot(0, 3);
    public static final Slot ROW_ONE_SLOT_FIVE = new Slot(0, 4);
    public static final Slot ROW_ONE_SLOT_SIX = new Slot(0, 5);
    public static final Slot ROW_ONE_SLOT_SEVEN = new Slot(0, 6);
    public static final Slot ROW_ONE_SLOT_EIGHT = new Slot(0, 7);
    public static final Slot ROW_ONE_SLOT_NINE = new Slot(0, 8);
    // Row 2 ---
    public static final Slot ROW_TWO_SLOT_ONE = new Slot(1, 0);
    public static final Slot ROW_TWO_SLOT_TWO = new Slot(1, 1);
    public static final Slot ROW_TWO_SLOT_THREE = new Slot(1, 2);
    public static final Slot ROW_TWO_SLOT_FOUR = new Slot(1, 3);
    public static final Slot ROW_TWO_SLOT_FIVE = new Slot(1, 4);
    public static final Slot ROW_TWO_SLOT_SIX = new Slot(1, 5);
    public static final Slot ROW_TWO_SLOT_SEVEN = new Slot(1, 6);
    public static final Slot ROW_TWO_SLOT_EIGHT = new Slot(1, 7);
    public static final Slot ROW_TWO_SLOT_NINE = new Slot(1, 8);
    // Row 3 ---
    public static final Slot ROW_THREE_SLOT_ONE = new Slot(2, 0);
    public static final Slot ROW_THREE_SLOT_TWO = new Slot(2, 1);
    public static final Slot ROW_THREE_SLOT_THREE = new Slot(2, 2);
    public static final Slot ROW_THREE_SLOT_FOUR = new Slot(2, 3);
    public static final Slot ROW_THREE_SLOT_FIVE = new Slot(2, 4);
    public static final Slot ROW_THREE_SLOT_SIX = new Slot(2, 5);
    public static final Slot ROW_THREE_SLOT_SEVEN = new Slot(2, 6);
    public static final Slot ROW_THREE_SLOT_EIGHT = new Slot(2, 7);
    public static final Slot ROW_THREE_SLOT_NINE = new Slot(2, 8);
    // Row 4 ---
    public static final Slot ROW_FOUR_SLOT_ONE = new Slot(3, 0);
    public static final Slot ROW_FOUR_SLOT_TWO = new Slot(3, 1);
    public static final Slot ROW_FOUR_SLOT_THREE = new Slot(3, 2);
    public static final Slot ROW_FOUR_SLOT_FOUR = new Slot(3, 3);
    public static final Slot ROW_FOUR_SLOT_FIVE = new Slot(3, 4);
    public static final Slot ROW_FOUR_SLOT_SIX = new Slot(3, 5);
    public static final Slot ROW_FOUR_SLOT_SEVEN = new Slot(3, 6);
    public static final Slot ROW_FOUR_SLOT_EIGHT = new Slot(3, 7);
    public static final Slot ROW_FOUR_SLOT_NINE = new Slot(3, 8);
    // Row 5 ---
    public static final Slot ROW_FIVE_SLOT_ONE = new Slot(4, 0);
    public static final Slot ROW_FIVE_SLOT_TWO = new Slot(4, 1);
    public static final Slot ROW_FIVE_SLOT_THREE = new Slot(4, 2);
    public static final Slot ROW_FIVE_SLOT_FOUR = new Slot(4, 3);
    public static final Slot ROW_FIVE_SLOT_FIVE = new Slot(4, 4);
    public static final Slot ROW_FIVE_SLOT_SIX = new Slot(4, 5);
    public static final Slot ROW_FIVE_SLOT_SEVEN = new Slot(4, 6);
    public static final Slot ROW_FIVE_SLOT_EIGHT = new Slot(4, 7);
    public static final Slot ROW_FIVE_SLOT_NINE = new Slot(4, 8);
    // Row 6 ---
    public static final Slot ROW_SIX_SLOT_ONE = new Slot(5, 0);
    public static final Slot ROW_SIX_SLOT_TWO = new Slot(5, 1);
    public static final Slot ROW_SIX_SLOT_THREE = new Slot(5, 2);
    public static final Slot ROW_SIX_SLOT_FOUR = new Slot(5, 3);
    public static final Slot ROW_SIX_SLOT_FIVE = new Slot(5, 4);
    public static final Slot ROW_SIX_SLOT_SIX = new Slot(5, 5);
    public static final Slot ROW_SIX_SLOT_SEVEN = new Slot(5, 6);
    public static final Slot ROW_SIX_SLOT_EIGHT = new Slot(5, 7);
    public static final Slot ROW_SIX_SLOT_NINE = new Slot(5, 8);
    // Special
    public static final Rect ALL = new Rect(Slot.ROW_ONE_SLOT_ONE, Slot.ROW_SIX_SLOT_NINE);
    private final int row;
    private final int column;

    public static Slot of(
            @IntRange(from = 1, to = 6) int row,
            @IntRange(from = 1, to = 9) int column
    ) {
        return new Slot(row - 1, column - 1);
    }

    public static Slot ofRaw(@IntRange(from = 0, to = 6 * 9) int raw) {
        return new Slot(raw / 9, raw % 9);
    }

    public int rawSlot() {
        return row * 9 + column;
    }

    @Override
    public int compareTo(@NotNull Slot o) {
        return Comparator.comparingInt((Slot slot) -> slot.row)
                .thenComparingInt(slot -> slot.column)
                .compare(this, o);
    }

    @Override
    public Set<Slot> slots() {
        return Collections.singleton(this);
    }

    @Override
    public Pair<Slot, Slot> points() {
        return Pair.of(this, this);
    }
}
