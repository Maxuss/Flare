package space.maxus.flare.ui.compose;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
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
 * A checkbox is a clickable component that is toggled on click.
 * <br />
 * See more in Flare docs: <a href="https://flare.maxus.space/ui/composable#checkbox">Checkbox</a>
 */
public interface Checkbox extends Disable, ProviderRendered, Configurable<Checkbox> {

    /**
     * Standard item provider for checked checkbox
     * @param message Message in the item name
     * @param description Description of the item
     * @return Standard item provider for checked checkbox
     */
    @Contract("_, _ -> new")
    static @NotNull ItemProvider checkedItem(String message, String description) {
        return ItemProvider.still(checkedBuilder(message, description));
    }

    /**
     * Standard item provider for unchecked checkbox
     * @param message Message in the item name
     * @param description Description of the item
     * @return Standard item provider for unchecked checkbox
     */
    @Contract("_, _ -> new")
    static @NotNull ItemProvider uncheckedItem(String message, String description) {
        return ItemProvider.still(uncheckedBuilder(message, description));
    }

    /**
     * Standard item builder for checked checkbox
     * @param message Message in the item name
     * @param description Description of the item
     * @return Standard item builder for checked checkbox
     */
    static @NotNull ItemStackBuilder uncheckedBuilder(String message, String description) {
        return Items.builder(Material.PLAYER_HEAD)
                .headSkin("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWUyOGJlYTlkMzkzNzNkMzZlZThmYTQwZWM4M2Y5YzNmY2RkOTMxNzUyMjc3NDNmOWRkMWY3ZTc4ODZiN2VlNSJ9fX0=")
                .name("<gray>%s <dark_gray>[⭘]".formatted(message))
                .lore(description)
                .padLore() // padding
                .addLoreLine("Click to toggle")
                .hideAllFlags();
    }

    /**
     * Standard item builder for unchecked checkbox
     * @param message Message in the item name
     * @param description Description of the item
     * @return Standard item builder for unchecked checkbox
     */
    @Contract("_, _ -> new")
    static @NotNull ItemStackBuilder checkedBuilder(String message, String description) {
        return Items.builder(Material.PLAYER_HEAD)
                .headSkin("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=")
                .name("<gray>%s <dark_gray>[<green>✔</green>]".formatted(message))
                .lore(description)
                .padLore()
                .addLoreLine("Click to toggle")
                .hideAllFlags();
    }

    /**
     * Creates a new Checkbox builder.
     * @return Checkbox builder
     */
    @Contract(value = " -> new", pure = true)
    static @NotNull Builder builder() {
        return new CheckboxImpl.Builder();
    }

    /**
     * Creates a new Checkbox with the given checked and unchecked item providers.
     * @param checkedProvider Item provider for checked checkbox
     * @param uncheckedProvider Item provider for unchecked checkbox
     * @return  Checkbox with the given checked and unchecked item providers.
     */
    @Contract("_, _ -> new")
    static @NotNull Checkbox of(ItemProvider checkedProvider, ItemProvider uncheckedProvider) {
        return new CheckboxImpl(checkedProvider, uncheckedProvider, false, false);
    }

    /**
     * Creates a new Checkbox with the given checked and unchecked item providers.
     * @param checkedProvider Item provider for checked checkbox
     * @param uncheckedProvider Item provider for unchecked checkbox
     * @param isChecked Whether the checkbox is toggled on by default
     * @return Checkbox with the given checked and unchecked item providers.
     */
    @Contract("_, _, _ -> new")
    static @NotNull Checkbox of(ItemProvider checkedProvider, ItemProvider uncheckedProvider, boolean isChecked) {
        return new CheckboxImpl(checkedProvider, uncheckedProvider, isChecked, false);
    }

    @Override
    void click(@NotNull InventoryClickEvent e);

    /**
     * Toggles the checkbox.
     */
    default void toggle() {
        checkedState().set(!checkedState().get());
    }

    /**
     * Gets whether the checkbox is checked.
     * @return Whether the checkbox is checked.
     */
    default boolean isChecked() {
        return checkedState().get();
    }

    /**
     * Sets whether the checkbox is checked.
     * @param checked Whether the checkbox is checked.
     */
    default void setChecked(boolean checked) {
        checkedState().set(checked);
    }

    ReactiveState<Boolean> disabledState();

    /**
     * Returns the reactive toggle state of the checkbox.
     * @return The reactive toggle state of the checkbox.
     */
    ReactiveState<Boolean> checkedState();

    interface Builder extends ComposableLike {
        /**
         * Sets the checked item provider for this checkbox
         * @param checkedItem The item provider
         * @return This builder
         */
        Builder checkedItem(@Nullable ItemProvider checkedItem);

        /**
         * Sets the unchecked item provider for this checkbox
         * @param uncheckedItem The item provider
         * @return This builder
         */
        Builder uncheckedItem(@Nullable ItemProvider uncheckedItem);

        /**
         * Sets whether the checkbox is toggled
         * @param isChecked Whether the checkbox is toggled
         * @return This builder
         */
        Builder isChecked(boolean isChecked);

        /**
         * Sets whether the checkbox is disabled
         * @param isDisabled Whether the checkbox is disabled
         * @return This builder
         */
        Builder isDisabled(boolean isDisabled);

        /**
         * Builds the component
         * @return The component
         */
        Checkbox build();

        @Override
        default Composable asComposable() {
            return build();
        }
    }
}
