package space.maxus.flare.ui.space;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.util.MinMaxPair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode
public class Rect implements ComposableSpace {
    private final Set<Slot> slots;

    public Rect(Slot from, Slot to) {
        Set<Slot> slotsAggr = new HashSet<>();
        for(int row = from.getRow(); row <= to.getRow(); row++) {
            for(int col = from.getColumn(); col <= to.getColumn(); col++) {
                slotsAggr.add(new Slot(row, col));
            }
        }
        this.slots = ImmutableSet.copyOf(slotsAggr);
    }

    public static @NotNull Rect of(Slot from, Slot to) {
        MinMaxPair<Slot> pair = new MinMaxPair<>(from, to);
        return new Rect(pair.getMin(), pair.getMax());
    }

    @Override
    public Set<Slot> slots() {
        return this.slots;
    }

    @Override
    public String toString() {
        List<Slot> list = this.slots.stream().toList();
        return MoreObjects.toStringHelper(this)
                .add("from", list.get(0))
                .add("to", list.get(list.size() - 1))
                .toString();
    }
}
