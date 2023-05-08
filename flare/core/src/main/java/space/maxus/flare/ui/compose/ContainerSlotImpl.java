package space.maxus.flare.ui.compose;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.ComposableReactiveState;
import space.maxus.flare.ui.space.Slot;
import space.maxus.flare.util.FlareUtil;

@ToString @EqualsAndHashCode(callSuper = true)
final class ContainerSlotImpl extends RootReferencing implements ContainerSlot {
    private final ReactiveState<ItemStack> currentItem;
    private final ReactiveState<Boolean> disabledState;
    private final @NotNull ItemProvider emptyProvider;
    private final @NotNull ContainerSlot.ContainerPredicate checkPut;
    private final @NotNull ContainerSlot.ContainerPredicate checkTake;
    private final @NotNull ContainerSlot.ContainerEvent onPut;
    private final @NotNull ContainerSlot.ContainerEvent onTake;

    ContainerSlotImpl(@Nullable ItemStack current, @NotNull ItemProvider provider, boolean isDisabled, @NotNull ContainerPredicate checkPut, @NotNull ContainerPredicate checkTake, @NotNull ContainerEvent onPut, @NotNull ContainerEvent onTake) {
        this.currentItem = new ComposableReactiveState<>(current, this);
        this.disabledState = new ReactiveState<>(isDisabled);
        this.emptyProvider = provider;
        this.checkPut = checkPut;
        this.checkTake = checkTake;
        this.onPut = onPut;
        this.onTake = onTake;
    }

    @Override
    public ItemStack renderAt(Slot slot) {
        ItemStack current = currentItem.getOrNull();
        return current == null ? emptyProvider.provide() : current;
    }

    @Override
    public ContainerSlot configure(Configurator<ContainerSlot> configurator) {
        configurator.configure(this);
        return this;
    }

    @Override
    public ReactiveState<ItemStack> itemState() {
        return currentItem;
    }

    @Override
    public ReactiveState<Boolean> disabledState() {
        return disabledState;
    }

    @Override
    public boolean shiftInto(@NotNull ItemStack stack, @NotNull InventoryClickEvent e) {
        if(isDisabled())
            return true;
        ItemStack current = currentItem.getOrNull();
        if(current == null && checkPut.allow(stack, e)) {
            onPut.handle(stack, e);
            currentItem.set(stack.clone());
            stack.setAmount(0);
            return false;
        } else if(current != null && current.isSimilar(stack) && checkPut.allow(stack, e)) {
            onPut.handle(stack, e);
            return mergeStacks(current, stack, stack.getAmount());
        }
        return true;
    }

    @Override
    public boolean shiftFrom(@NotNull InventoryClickEvent e) {
        if(isDisabled())
            return true;
        ItemStack current = currentItem.getOrNull();
        if(current == null || !checkTake.allow(current, e))
            return true;
        onTake.handle(current, e);
        currentItem.set(null);
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean leftClick(@NotNull InventoryClickEvent e) {
        if(isDisabled())
            return true;
        ItemStack current = currentItem.getOrNull();
        ItemStack cursor = e.getCursor();
        if(current == null && FlareUtil.isNullOrAir(cursor)) {
            // current item is empty, cursor is empty,
            // so we don't do anything
            return true;
        } else if(current == null && checkPut.allow(cursor, e)) {
            // current item is empty, cursor is not empty,
            // so we put the cursor in the current item
            onPut.handle(cursor, e);
            currentItem.set(cursor.clone());
            e.setCursor(null);
            return true;
        } else if(current != null && FlareUtil.isNullOrAir(cursor) && checkTake.allow(current, e)) {
            // current item is not empty, cursor is empty,
            // so we take the current item and put it in the cursor
            onTake.handle(current, e);
            e.setCursor(current.clone());
            currentItem.set(null);
            return true;
        } else if(!FlareUtil.isNullOrAir(cursor) && current != null) {
            if(cursor.isSimilar(current) && checkPut.allow(cursor, e)) {
                // items are similar, so we are merging them
                int maxAdd = current.getMaxStackSize() - current.getAmount();
                if(cursor.getAmount() > maxAdd) {
                    cursor.setAmount(cursor.getAmount() - maxAdd);
                    current.setAmount(current.getMaxStackSize());
                    e.setCursor(cursor);
                    currentItem.set(current);
                    return true;
                } else {
                    current.setAmount(current.getAmount() + cursor.getAmount());
                    e.setCursor(null);
                    currentItem.set(current);
                }
                onPut.handle(current, e);
                return true;
            } else if(checkTake.allow(current, e) && checkPut.allow(cursor, e)) {
                // items are different, so we are swapping them
                onTake.handle(current, e);
                onPut.handle(cursor, e);
                ItemStack temp = current.clone();
                currentItem.set(cursor.clone());
                e.setCursor(temp);
            }
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean rightClick(@NotNull InventoryClickEvent e) {
        if(isDisabled())
            return true;
        ItemStack current = currentItem.getOrNull();
        ItemStack cursor = e.getCursor();
        if(current == null)
            return true;
        if(!FlareUtil.isNullOrAir(cursor))
            return true;
        int toTake = current.getAmount() / 2;
        ItemStack clone = current.clone();
        clone.setAmount(toTake);
        if(!checkTake.allow(clone, e))
            return true;
        onTake.handle(clone, e);
        e.setCursor(clone);
        current.setAmount(current.getAmount() - toTake);
        currentItem.set(current);
        return true;
    }

    private boolean mergeStacks(@NotNull ItemStack current, ItemStack cursor, int amount) {
        int maxAdd = current.getMaxStackSize() - current.getAmount();
        if(amount > maxAdd) {
            cursor.setAmount(cursor.getAmount() - maxAdd);
            current.setAmount(current.getMaxStackSize());
            currentItem.set(current);
            return true;
        } else {
            current.setAmount(current.getAmount() + cursor.getAmount());
            cursor.setAmount(0);
            currentItem.set(current);
        }
        return false;
    }

    static final class Builder implements ContainerSlot.Builder {
        private @Nullable ItemProvider empty;
        private boolean disabled = false;
        private @Nullable ItemStack item;
        private @Nullable ContainerSlot.ContainerPredicate checkPut;
        private @Nullable ContainerSlot.ContainerPredicate checkTake;
        private @Nullable ContainerSlot.ContainerEvent onPut;
        private @Nullable ContainerSlot.ContainerEvent onTake;

        @Override
        public ContainerSlot.Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        @Override
        public ContainerSlot.Builder empty(@Nullable ItemProvider provider) {
            this.empty = provider;
            return this;
        }

        @Override
        public ContainerSlot.Builder item(@Nullable ItemStack item) {
            this.item = item;
            return this;
        }

        @Override
        public ContainerSlot.Builder onPut(ContainerEvent put) {
            this.onPut = put;
            return this;
        }

        @Override
        public ContainerSlot.Builder onTake(ContainerEvent take) {
            this.onTake = take;
            return this;
        }

        @Override
        public ContainerSlot.Builder filterPut(ContainerPredicate put) {
            this.checkPut = put;
            return this;
        }

        @Override
        public ContainerSlot.Builder filterTake(ContainerPredicate take) {
            this.checkTake = take;
            return this;
        }

        @Contract(" -> new")
        @Override
        public @NotNull ContainerSlot build() {
            return new ContainerSlotImpl(item, empty == null ? ContainerSlot.emptyItem(null, null) : empty, disabled, checkPut == null ? (a, b) -> true : checkPut, checkTake == null ? (a, b) -> true : checkTake, onPut == null ? (a, b) -> { } : onPut, onTake == null ? (a, b) -> { } : onTake);
        }
    }
}
