package space.maxus.flare.ui.compose;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.concurrent.Computable;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.Flare;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.ComposableReactiveState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ToString
@EqualsAndHashCode(callSuper = true)
final class SelectionImpl<E> extends RootReferencing implements Selection<E> {
    private final ItemProvider itemProvider;
    private final ArrayList<E> values;
    private final AtomicInteger selectedIdx;
    private final ReactiveState<E> selected;
    private final ReactiveState<Boolean> disabledState;

    SelectionImpl(@Nullable ItemProvider provider, Collection<E> values, int origin, boolean isDisabled, @Nullable Computable<E, String> mapper) {
        this.values = values instanceof ArrayList<E> valuesList ? valuesList : new ArrayList<>(values);
        if (this.values.size() > 12)
            Flare.logger().warn("Selection is too big. Clients may experience rendering issues. ({} > {})", this.values.size(), 12);
        this.selectedIdx = new AtomicInteger(origin);
        this.selected = new ComposableReactiveState<>(this.values.get(origin), this);
        this.disabledState = new ComposableReactiveState<>(isDisabled, this);
        this.itemProvider = provider == null ? Selection.selector("Select", "", this.values, this.selected, mapper) : provider;
    }

    @Override
    public Selection<E> configure(Configurator<Selection<E>> configurator) {
        configurator.configure(this);
        return this;
    }

    @Override
    public ReactiveState<Boolean> disabledState() {
        return disabledState;
    }

    @Override
    public @NotNull ItemProvider getProvider() {
        return itemProvider;
    }

    @Override
    public List<E> enumeration() {
        return values;
    }

    @Override
    public @NotNull E getSelected() {
        return selected.get();
    }

    @Override
    public void setSelected(int index) {
        selectedIdx.set(index);
        selected.set(values.get(index));
    }

    @Override
    public void setSelected(E value) {
        int idx = values.indexOf(value);
        if (idx == -1)
            throw new IllegalArgumentException("Value not in list");
        selectedIdx.set(idx);
        selected.set(value);
    }

    @Override
    public @NotNull ReactiveState<E> selectedState() {
        return selected;
    }

    @Override
    public boolean leftClick(@NotNull InventoryClickEvent e) {
        if (e.getClick() == ClickType.DOUBLE_CLICK || this.isDisabled())
            return true; // ignoring double clicks
        int idx = selectedIdx.get();
        int newIdx = idx + 1 >= values.size() ? 0 : idx + 1;
        selectedIdx.setRelease(newIdx);
        selected.set(values.get(newIdx));
        return true;
    }

    @Override
    public boolean rightClick(@NotNull InventoryClickEvent e) {
        int idx = selectedIdx.get();
        int newIdx = idx - 1 < 0 ? values.size() - 1 : idx - 1;
        selectedIdx.setRelease(newIdx);
        selected.set(values.get(newIdx));
        return true;
    }

    @RequiredArgsConstructor
    static final class Builder<E> implements Selection.Builder<E> {
        private final ArrayList<E> enumeration;
        private @Nullable ItemProvider provider = null;
        private @Nullable Computable<E, String> formatter = null;
        private int selection = 0;
        private boolean disabled;

        @Override
        public Selection.Builder<E> item(ItemProvider provider) {
            this.provider = provider;
            return this;
        }

        @Override
        public Selection.Builder<E> selected(int index) {
            this.selection = index;
            return this;
        }

        @Override
        public Selection.Builder<E> selectedItem(E item) {
            int idx = enumeration.indexOf(item);
            if (idx == -1)
                throw new IllegalArgumentException("Item not in list");
            this.selection = idx;
            return this;
        }

        @Override
        public Selection.Builder<E> isDisabled(boolean isDisabled) {
            this.disabled = isDisabled;
            return this;
        }

        @Override
        public Selection.Builder<E> formatter(Computable<E, String> mapper) {
            this.formatter = mapper;
            return this;
        }

        @Override
        public Selection<E> build() {
            Validate.isTrue(provider != null || formatter != null, "Both formatter and item provider were null. At least one of them is required to be true.");
            return new SelectionImpl<>(provider, enumeration, selection, disabled, formatter);
        }
    }
}
