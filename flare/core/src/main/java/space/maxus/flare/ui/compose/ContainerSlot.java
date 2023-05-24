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

/**
 * ContainerSlot is a component that can have an item placed in it.
 * <br />
 * See more in Flare docs: <a href="https://flare.maxus.space/ui/composable#containerslot">Container Slot</a>
 */
public interface ContainerSlot extends Configurable<ContainerSlot>, Composable, Disable {
    /**
     * Returns item builder for empty container slot item
     * @param name Extra message in item name
     * @param description Extra description
     * @return Item builder for empty container slot item
     */
    static ItemStackBuilder emptyBuilder(@Nullable String name, @Nullable String description) {
        return Items.builder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                .hideAllFlags()
                .name("<gray>%s <dark_gray>[□]".formatted(name == null ? "Empty Slot" : name))
                .lore(description == null ? "" : description)
                .padLore()
                .addLoreLine("<dark_gray>Click with item to put it here");
    }

    /**
     * Returns item provider for empty container slot item
     * @param name Extra message in item name
     * @param description Extra description
     * @return Item provider for empty container slot item
     */
    @Contract("_, _ -> new")
    static @NotNull ItemProvider emptyItem(@Nullable String name, @Nullable String description) {
        return ItemProvider.still(emptyBuilder(name, description).build());
    }

    /**
     * Constructs a new container slot with handlers
     * @param onPut Put handler
     * @param onTake Take handler
     * @return New container slot with handlers
     */
    static @NotNull ContainerSlot of(@NotNull ContainerEvent onPut, @NotNull ContainerEvent onTake) {
        return new ContainerSlotImpl(null, emptyItem(null, null), false, (a, b) -> true, (a, b) -> true, onPut, onTake);
    }

    /**
     * Constructs a new container slot builder
     * @return New container slot builder
     */
    static @NotNull Builder builder() {
        return new ContainerSlotImpl.Builder();
    }

    /**
     * Returns the reactive state of an item inside this container
     * @return Reactive state of an item inside this container
     */
    ReactiveState<ItemStack> itemState();

    /**
     * Gets the item inside this container
     * @return Item inside this container
     */
    default @Nullable ItemStack getItem() {
        return itemState().get();
    }

    /**
     * Sets the item inside this container
     * @param stack Item inside this container
     */
    default void setItem(@Nullable ItemStack stack) {
        itemState().set(stack);
    }

    /**
     * Builder for ContainerSlot
     */
    interface Builder extends ComposableLike {
        /**
         * Makes the container disabled
         * @param disabled Disabled state
         * @return This builder
         */
        Builder disabled(boolean disabled);

        /**
         * Sets the empty item provider for this container
         * @param provider The empty item provider
         * @return This builder
         */
        Builder empty(@Nullable ItemProvider provider);

        /**
         * Sets the item inside this container
         * @param item The item to be set
         * @return This builder
         */
        Builder item(@Nullable ItemStack item);

        /**
         * Sets the put handler for this container
         * @param put Put handler
         * @return This builder
         */
        Builder onPut(ContainerEvent put);

        /**
         * Sets the take handler for this container
         * @param take Take handler
         * @return This builder
         */
        Builder onTake(ContainerEvent take);

        /**
         * Sets the put filter for this container
         * @param put Put filter
         * @return This builder
         */
        Builder filterPut(ContainerPredicate put);

        /**
         * Sets the take filter for this container
         * @param take Take filter
         * @return This builder
         */
        Builder filterTake(ContainerPredicate take);

        /**
         * Builds this container
         * @return Built container slot
         */
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
