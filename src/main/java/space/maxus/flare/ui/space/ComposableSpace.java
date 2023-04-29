package space.maxus.flare.ui.space;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;

public interface ComposableSpace {
    Set<Slot> slots();
    Pair<Slot, Slot> points();
}
