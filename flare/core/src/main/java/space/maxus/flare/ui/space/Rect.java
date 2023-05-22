package space.maxus.flare.ui.space;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.util.MinMaxPair;

import java.util.HashSet;
import java.util.Set;

/**
 * A Rect is a ComposableSpace inside a Rectangle defined by first and last points.
 */
@EqualsAndHashCode
public class Rect implements ComposableSpace {
    private final Set<Slot> slots;
    private final Slot from;
    private final Slot to;

    /**
     * Constructs a new rect with starting and ending slot
     * @param from Strictly smallest first slot
     * @param to Strictly biggest last slot
     */
    public Rect(Slot from, Slot to) {
        Set<Slot> slotsAggr = new HashSet<>();
        for (int row = from.getRow(); row <= to.getRow(); row++) {
            for (int col = from.getColumn(); col <= to.getColumn(); col++) {
                slotsAggr.add(new Slot(row, col));
            }
        }
        this.slots = ImmutableSet.copyOf(slotsAggr);
        this.from = from;
        this.to = to;
    }

    /**
     * Constructs a new Rect from starting and ending slots. Slots are ordered using {@link MinMaxPair} and can be passed in any order.
     * @param from First slot
     * @param to Second slot
     * @return Constructed Rect
     */
    public static @NotNull Rect of(Slot from, Slot to) {
        MinMaxPair<Slot> pair = new MinMaxPair<>(from, to);
        return new Rect(pair.getMin(), pair.getMax());
    }

    @Override
    public Set<Slot> slots() {
        return this.slots;
    }

    @Override
    public Pair<Slot, Slot> points() {
        return Pair.of(from, to);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("from", from)
                .add("to", to)
                .toString();
    }
}
