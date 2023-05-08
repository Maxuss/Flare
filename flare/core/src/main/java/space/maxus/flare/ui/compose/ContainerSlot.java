package space.maxus.flare.ui.compose;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.ItemStackBuilder;
import space.maxus.flare.item.Items;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.ComposableLike;

public interface ContainerSlot extends Configurable<ContainerSlot>, Composable, Disable {
    static ItemStackBuilder emptyBuilder(@Nullable String name, @Nullable String description) {
        return Items.builder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                .hideAllFlags()
                .name("<gray>%s <dark_gray>[□]".formatted(name == null ? "Empty Slot" : name))
                .lore(description == null ? "" : description)
                .padLore()
                .addLoreLine("<dark_gray>Click with item to put it here");
    }

    @Contract("_, _ -> new")
    static @NotNull ItemProvider emptyItem(@Nullable String name, @Nullable String description) {
        return ItemProvider.still(emptyBuilder(name, description).build());
    }

    static @NotNull ContainerSlot of(@NotNull ContainerEvent onPut, @NotNull ContainerEvent onTake) {
        return new ContainerSlotImpl(null, emptyItem(null, null), false, (a, b) -> true, (a, b) -> true, onPut, onTake);
    }

    static @NotNull Builder builder() {
        return new ContainerSlotImpl.Builder();
    }

    ReactiveState<ItemStack> itemState();

    default @Nullable ItemStack getItem() {
        return itemState().get();
    }

    default void setItem(@Nullable ItemStack stack) {
        itemState().set(stack);
    }

    interface Builder extends ComposableLike {
        Builder disabled(boolean disabled);

        Builder empty(@Nullable ItemProvider provider);

        Builder item(@Nullable ItemStack item);

        Builder onPut(ContainerEvent put);

        Builder onTake(ContainerEvent take);

        Builder filterPut(ContainerPredicate put);

        Builder filterTake(ContainerPredicate take);

        ContainerSlot build();

        @Override
        default Composable asComposable() {
            return build();
        }
    }

    @FunctionalInterface
    interface ContainerPredicate {
        boolean allow(ItemStack stack, InventoryClickEvent e);
    }

    @FunctionalInterface
    interface ContainerEvent {
        void handle(ItemStack stack, InventoryClickEvent e);
    }
}
