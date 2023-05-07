package space.maxus.flare.ui.compose.complex;

import com.google.common.collect.Iterables;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.StandardException;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.Flare;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.ComposableReactiveState;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.PackedComposable;
import space.maxus.flare.ui.compose.RootReferencing;
import space.maxus.flare.ui.page.Pagination;
import space.maxus.flare.ui.space.ComposableSpace;
import space.maxus.flare.ui.space.Slot;
import space.maxus.flare.util.FlareUtil;
import space.maxus.flare.util.SafeComputable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ToString
@EqualsAndHashCode(callSuper = true)
final class PaginationDisplayImpl extends RootReferencing implements PaginationDisplay {
    private final Pagination<?> pagination;
    private final ReactiveState<Integer> currentIdx;
    private final ReactiveState<Frame> currentPage;
    private final ItemProvider backProvider;
    private final ItemProvider forwardProvider;
    private final SafeComputable<Pair<Integer, Frame>, ItemStack> selectedPage;
    private final SafeComputable<Pair<Integer, Frame>, ItemStack> unselectedPage;
    private final Map<Slot, Integer> activeSlots = new ConcurrentHashMap<>();
    private Slot buttonBack = Slot.ROW_ONE_SLOT_ONE;
    private Slot buttonForward = Slot.ROW_ONE_SLOT_ONE;
    private List<Slot> allocatedSpace = List.of();

    PaginationDisplayImpl(
            @NotNull Pagination<?> pagination,
            int currentIdx,
            @Nullable ItemProvider back,
            @Nullable ItemProvider forward,
            @Nullable SafeComputable<Pair<Integer, Frame>, ItemStack> selectedPage,
            @Nullable SafeComputable<Pair<Integer, Frame>, ItemStack> unselectedPage
    ) {
        this.pagination = pagination;
        this.currentIdx = new ComposableReactiveState<>(currentIdx, this);
        Callable<Boolean> isLastSelected = () -> Iterables.getFirst(this.activeSlots.values(), -1) != 0;
        Callable<Boolean> isFirstSelected = () -> Iterables.getLast(this.activeSlots.values(), -1) != pagination.pageCount() - 1;

        this.backProvider = back == null ?
                () -> PaginationDisplay.arrowBackwardButton(FlareUtil.acquireThrowing(isFirstSelected)).build() :
                back;
        this.forwardProvider = forward == null ?
                () -> PaginationDisplay.arrowForwardButton(FlareUtil.acquireThrowing(isLastSelected)).build() :
                forward;
        this.selectedPage = selectedPage == null ?
                pair -> PaginationDisplay.pageNumber(pair.getRight(), pair.getLeft(), true).build() :
                selectedPage;
        this.unselectedPage = unselectedPage == null ?
                pair -> PaginationDisplay.pageNumber(pair.getRight(), pair.getLeft(), false).build() :
                unselectedPage;
        ReactiveState<Frame> pg;
        try {
            pg = new ComposableReactiveState<>(pagination.getPage(currentIdx), this);
        } catch (IndexOutOfBoundsException e) {
            Flare.LOGGER.error("Tried to initialize PaginationDisplay with invalid index");
            this.currentPage = new ComposableReactiveState<>(null, this);
            return;
        }
        this.currentPage = pg;

    }

    @Override
    public @Nullable ItemStack renderAt(Slot slot) {
        if (slot == buttonBack) {
            return backProvider.provide();
        } else if (slot == buttonForward) {
            return forwardProvider.provide();
        } else {
            Integer frameIdx = activeSlots.get(slot); // specifically not unboxing value here
            if (frameIdx == null)
                return null; // should not happen usually
            Pair<Integer, Frame> pair = Pair.of(frameIdx, currentPage.get());
            return frameIdx.equals(selectedIndex().get()) ?
                    selectedPage.compute(pair) :
                    unselectedPage.compute(pair);
        }
    }

    @Override
    public PaginationDisplay configure(Configurator<PaginationDisplay> configurator) {
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

    @SuppressWarnings({"SimplifyStreamApiCallChains"})
    @Override
    public @NotNull PackedComposable inside(@NotNull ComposableSpace space) {
        this.allocatedSpace = space.slots().stream().sorted().toList();
        if (this.allocatedSpace.size() < 3)
            throw new DisplaySizeException("PaginationDisplay expected to have at least 3 slots available, but got only %s slots!".formatted(allocatedSpace));
        this.buttonBack = allocatedSpace.get(0);
        this.buttonForward = allocatedSpace.get(allocatedSpace.size() - 1);
        if (!this.activeSlots.isEmpty())
            return super.inside(space);
        AtomicInteger idx = new AtomicInteger();
        this.allocatedSpace.stream().forEachOrdered(slot -> {
            if (slot != buttonBack && slot != buttonForward)
                this.activeSlots.put(slot, idx.getAndIncrement());
        });
        return super.inside(space);
    }

    @Override
    public void click(@NotNull InventoryClickEvent e) {
        e.setCancelled(true);
        Slot slot = Slot.ofRaw(e.getSlot());
        int idx = this.allocatedSpace.indexOf(slot);
        if (idx == -1) return; // invalid click?
        if (slot.equals(buttonBack)) {
            if (Iterables.getFirst(this.activeSlots.values(), 0) == 0)
                return;
            // shifting active values by one backward
            Map<Slot, Integer> temp = new HashMap<>(this.activeSlots);
            this.activeSlots.forEach((mapSlot, mapIdx) -> temp.put(mapSlot, mapIdx - 1));
            this.activeSlots.clear();
            this.activeSlots.putAll(temp);
            this.markDirty();
            return;
        }
        if (slot.equals(buttonForward)) {
            if (Iterables.getLast(this.activeSlots.values(), 0) == pagination.pageCount() - 1)
                return;
            // shifting active values by one forward
            Map<Slot, Integer> temp = new HashMap<>(this.activeSlots);
            this.activeSlots.forEach((mapSlot, mapIdx) -> temp.put(mapSlot, mapIdx + 1));
            this.activeSlots.clear();
            this.activeSlots.putAll(temp);
            this.markDirty();
            return;
        }
        // setting current page
        int clickedIdx = this.activeSlots.get(slot);
        this.currentIdx.set(clickedIdx);
        this.currentPage.set(pagination.getPage(clickedIdx));
        pagination.switchPage((Player) e.getWhoClicked(), clickedIdx);
        this.markDirty();
    }

    static final class Builder implements PaginationDisplay.Builder {
        private final Pagination<?> pagination;
        private int currentIdx;
        private ItemProvider back;
        private ItemProvider forward;
        private SafeComputable<Pair<Integer, Frame>, ItemStack> selectedPage;
        private SafeComputable<Pair<Integer, Frame>, ItemStack> unselectedPage;

        Builder(Pagination<?> pagination) {
            this.pagination = pagination;
        }

        @Override
        public PaginationDisplay.@NotNull Builder selectedIndex(int index) {
            this.currentIdx = index;
            return this;
        }

        @Override
        public PaginationDisplay.@NotNull Builder backButton(@Nullable ItemProvider back) {
            this.back = back;
            return this;
        }

        @Override
        public PaginationDisplay.@NotNull Builder forwardButton(@Nullable ItemProvider forward) {
            this.forward = forward;
            return this;
        }

        @Override
        public PaginationDisplay.@NotNull Builder selectedPage(@Nullable SafeComputable<Pair<Integer, Frame>, ItemStack> page) {
            this.selectedPage = page;
            return this;
        }

        @Override
        public PaginationDisplay.@NotNull Builder unselectedPage(@Nullable SafeComputable<Pair<Integer, Frame>, ItemStack> page) {
            this.unselectedPage = page;
            return this;
        }

        @Override
        public @NotNull PaginationDisplay build() {
            return new PaginationDisplayImpl(pagination, currentIdx, back, forward, selectedPage, unselectedPage);
        }
    }

    @StandardException
    static final class DisplaySizeException extends RuntimeException {
    }
}
