package space.maxus.flare.ui.compose.extra;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.ItemStackBuilder;
import space.maxus.flare.item.Items;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.compose.Configurable;
import space.maxus.flare.ui.compose.Disable;
import space.maxus.flare.ui.compose.ProviderRendered;

import java.util.List;

public interface Checkbox extends Disable, ProviderRendered, Configurable<Checkbox> {

    @Contract("_, _ -> new")
    static @NotNull ItemProvider checkedItem(String message, String description) {
        return ItemProvider.still(checkedBuilder(message, description));
    }

    @Contract("_, _ -> new")
    static @NotNull ItemProvider uncheckedItem(String message, String description) {
        return ItemProvider.still(uncheckedBuilder(message, description));
    }

    static @NotNull ItemStackBuilder uncheckedBuilder(String message, String description) {
        return Items.builder(Material.PLAYER_HEAD)
                .headSkin("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWUyOGJlYTlkMzkzNzNkMzZlZThmYTQwZWM4M2Y5YzNmY2RkOTMxNzUyMjc3NDNmOWRkMWY3ZTc4ODZiN2VlNSJ9fX0=")
                .name("<gray>%s <dark_gray>[⭘]".formatted(message))
                .lore(description)
                .addLore(List.of(Component.empty())) // padding
                .addLore("Click to toggle")
                .hideAllFlags();
    }

    @Contract("_, _ -> new")
    static @NotNull ItemStackBuilder checkedBuilder(String message, String description) {
        return Items.builder(Material.PLAYER_HEAD)
                .headSkin("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=")
                .name("<gray>%s <dark_gray>[<green>✔</green>]".formatted(message))
                .lore(description)
                .addLore(List.of(Component.empty())) // padding
                .addLore("Click to toggle")
                .hideAllFlags();
    }

    @Contract(value = " -> new", pure = true)
    static @NotNull Builder builder() {
        return new CheckboxImpl.Builder();
    }

    @Contract("_, _ -> new")
    static @NotNull Checkbox create(ItemProvider checkedProvider, ItemProvider uncheckedProvider) {
        return new CheckboxImpl(checkedProvider, uncheckedProvider, false, false);
    }

    @Contract("_, _, _ -> new")
    static @NotNull Checkbox create(ItemProvider checkedProvider, ItemProvider uncheckedProvider, boolean isChecked) {
        return new CheckboxImpl(checkedProvider, uncheckedProvider, isChecked, false);
    }

    @Override
    void click(@NotNull InventoryClickEvent e);

    default void toggle() {
        checkedState().set(!checkedState().get());
    }

    default boolean isChecked() {
        return checkedState().get();
    }

    default void setChecked(boolean checked) {
        checkedState().set(checked);
    }

    ReactiveState<Boolean> disabledState();

    ReactiveState<Boolean> checkedState();

    interface Builder {
        Builder checkedItem(@Nullable ItemProvider checkedItem);

        Builder uncheckedItem(@Nullable ItemProvider uncheckedItem);

        Builder isChecked(boolean isChecked);

        Builder isDisabled(boolean isDisabled);

        Checkbox build();
    }
}
