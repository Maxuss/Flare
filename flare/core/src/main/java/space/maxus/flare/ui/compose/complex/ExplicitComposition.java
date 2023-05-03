package space.maxus.flare.ui.compose.complex;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.inventory.ItemStack;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.PackedComposable;
import space.maxus.flare.ui.compose.RootReferencing;
import space.maxus.flare.ui.space.ComposableSpace;
import space.maxus.flare.ui.space.RawRect;
import space.maxus.flare.ui.space.Slot;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
class ExplicitComposition extends RootReferencing implements Composition {
    private final List<PackedComposable> toCompose;
    private List<PackedComposable> composed;

    @Override
    public ItemStack renderAt(Slot slot) {
        PackedComposable packed = composed.stream().filter(c -> c.getSpace().slots().contains(slot)).reduce((a, b) -> b).orElse(null);
        return packed == null ? null : packed.getComposable().renderAt(slot);
    }

    @Override
    public Composition configure(Configurator<Composition> configurator) {
        configurator.configure(this);
        return this;
    }

    @Override
    public void injectRoot(Frame root) {
        this.root.set(root);
        this.composed.forEach(c -> c.getComposable().injectRoot(root));
    }

    @Override
    public List<PackedComposable> fitIn(ComposableSpace space) {
        // a little bit of caching
        if (this.composed != null)
            return this.composed;
        Set<Slot> availableSlots = space.slots();
        if (availableSlots.size() <= 1 && this.toCompose.size() != 1) {
            throw new IllegalArgumentException("Can't fit more than one item in a single slot");
        }
        Pair<Slot, Slot> points = space.points();
        Slot origin = points.getLeft();
        Slot max = points.getRight();
        this.composed = this.toCompose.stream().map(packed -> {
            Set<Slot> area = packed.getSpace().slots().stream().map(slot ->
                    new Slot(Math.min(origin.getRow() + slot.getRow(), max.getRow()), Math.min(origin.getColumn() + slot.getColumn(), max.getColumn()))
            ).collect(Collectors.toSet());
            RawRect newSpace = new RawRect(area);
            return packed.getComposable().inside(newSpace);
        }).toList();
        return this.composed;
    }

    @Override
    public List<PackedComposable> children() {
        return this.composed == null ? Collections.emptyList() : this.composed;
    }
}
