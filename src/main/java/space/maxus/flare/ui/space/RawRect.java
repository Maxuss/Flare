package space.maxus.flare.ui.space;

import com.google.common.base.MoreObjects;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode
public class RawRect implements ComposableSpace {
    private final Set<Slot> slots;

    @Override
    public Set<Slot> slots() {
        return this.slots;
    }

    @Override
    public Pair<Slot, Slot> points() {
        // unstable method
        List<Slot> slotList = this.slots.stream().sorted().toList();
        return Pair.of(slotList.get(0), slotList.get(slotList.size() - 1));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("slots", this.slots)
                .toString();
    }
}
