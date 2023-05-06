package space.maxus.flare.ui.compose.complex;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.concurrent.Computable;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.Flare;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.ComposableReactiveState;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.PackedComposable;
import space.maxus.flare.ui.compose.RootReferencing;
import space.maxus.flare.ui.page.Pagination;
import space.maxus.flare.ui.space.ComposableSpace;
import space.maxus.flare.ui.space.Slot;
import space.maxus.flare.util.SafeComputable;

import java.util.List;

@ToString @EqualsAndHashCode(callSuper = true)
final class TabulationImpl extends RootReferencing implements Tabulation {
    private final Pagination<?> pagination;
    private final ReactiveState<Integer> currentIdx;
    private final ReactiveState<Frame> currentPage;
    private final SafeComputable<Pair<Integer, Frame>, ItemStack> selected;
    private final SafeComputable<Pair<Integer, Frame>, ItemStack> unselected;

    private List<Slot> allocatedSpace = List.of();

    TabulationImpl(
            @NotNull Pagination<?> pagination,
            @Nullable Computable<Pair<Integer, Frame>, ItemStack> selected,
            @Nullable Computable<Pair<Integer, Frame>, ItemStack> unselected,
            int currentIdx) {
        this.pagination = pagination;
        this.currentIdx = new ReactiveState<>(currentIdx);
        this.selected = selected == null ? (pair -> Tabulation.selectedItemBuilder(pair.getLeft(), pair.getRight()).build()) : SafeComputable.wrap(selected);
        this.unselected = unselected == null ? (pair -> Tabulation.unselectedItemBuilder(pair.getLeft(), pair.getRight()).build()) : SafeComputable.wrap(unselected);
        ReactiveState<Frame> pg;
        try {
            pg = new ComposableReactiveState<>(pagination.getPage(currentIdx), this);
        } catch (IndexOutOfBoundsException e) {
            Flare.LOGGER.error("Tried to initialize Tabulation with invalid index");
            pg = new ComposableReactiveState<>(null, this);
        }
        this.currentPage = pg;
    }

    @Override
    public ItemStack renderAt(Slot slot) {
        int idx = this.allocatedSpace.indexOf(slot);
        if(currentIdx.get() == idx)
            return this.selected.safeCompute(Pair.of(idx, pagination.getPage(idx)));
        return this.unselected.safeCompute(Pair.of(idx, pagination.getPage(idx)));
    }

    @Contract("_ -> this")
    @Override
    public Tabulation configure(Configurator<Tabulation> configurator) {
        configurator.configure(this);
        return this;
    }

    @Override
    public Pagination<?> getPagination() {
        return pagination;
    }

    @Override
    public ReactiveState<Integer> selectedIndex() {
        return currentIdx;
    }

    @Override
    public ReactiveState<Frame> selectedFrame() {
        return currentPage;
    }

    @Override
    public @NotNull PackedComposable inside(@NotNull ComposableSpace space) {
        this.allocatedSpace = space.slots().stream().sorted().toList();
        return super.inside(space);
    }

    @Override
    public void click(@NotNull InventoryClickEvent e) {
        e.setCancelled(true);
        int idx = this.allocatedSpace.indexOf(Slot.ofRaw(e.getSlot()));
        if(currentIdx.get() == idx) return;
        currentIdx.set(idx);
        currentPage.set(pagination.getPage(idx));
        pagination.switchPage((Player) e.getWhoClicked(), idx);
        this.markDirty();
    }
}
