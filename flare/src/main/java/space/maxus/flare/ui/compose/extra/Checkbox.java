package space.maxus.flare.ui.compose.extra;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.ItemStackBuilder;
import space.maxus.flare.item.Items;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.ComposableReactiveState;
import space.maxus.flare.ui.compose.Configurable;
import space.maxus.flare.ui.compose.ProviderRendered;
import space.maxus.flare.ui.compose.RootReferencing;

import java.util.List;

@ToString @EqualsAndHashCode(callSuper = true)
public class Checkbox extends RootReferencing implements ProviderRendered, Configurable<Checkbox> {
    @Contract("_, _ -> new")
    public static @NotNull ItemProvider checkedItem(String message, String description) {
        return ItemProvider.still(checkedBuilder(message, description));
    }

    @Contract("_, _ -> new")
    public static @NotNull ItemProvider uncheckedItem(String message, String description) {
        return ItemProvider.still(uncheckedBuilder(message, description));
    }

    public static @NotNull ItemStackBuilder uncheckedBuilder(String message, String description) {
        return Items.builder(Material.PLAYER_HEAD)
                .headSkin("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWUyOGJlYTlkMzkzNzNkMzZlZThmYTQwZWM4M2Y5YzNmY2RkOTMxNzUyMjc3NDNmOWRkMWY3ZTc4ODZiN2VlNSJ9fX0=")
                .name("<gray>%s <dark_gray>[⭘]".formatted(message))
                .lore(description)
                .addLore(List.of(Component.empty())) // padding
                .addLore("Click to toggle")
                .hideAllFlags();
    }

    @Contract("_, _ -> new")
    public static @NotNull ItemStackBuilder checkedBuilder(String message, String description) {
        return Items.builder(Material.PLAYER_HEAD)
                .headSkin("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5YTVjOTVlZTE3YWJmZWY0NWM4ZGMyMjQxODk5NjQ5NDRkNTYwZjE5YTQ0ZjE5ZjhhNDZhZWYzZmVlNDc1NiJ9fX0=")
                .name("<gray>%s <dark_gray>[<green>✔</green>]".formatted(message))
                .lore(description)
                .addLore(List.of(Component.empty())) // padding
                .addLore("Click to toggle")
                .hideAllFlags();
    }

    @Override
    public void click(@NotNull InventoryClickEvent e) {
        if(e.getClick() == ClickType.DOUBLE_CLICK)
            return; // ignoring double clicks
        this.toggle();
        e.setCancelled(true);
    }

    private final ItemProvider checkedProvider;
    private final ItemProvider uncheckedProvider;
    private final ReactiveState<Boolean> disabledState;
    private final ReactiveState<Boolean> checkedState;

    private final ItemProvider branchingProvider;


    @lombok.Builder(builderClassName = "Builder")
    public Checkbox(@Nullable ItemProvider checkedItem, @Nullable ItemProvider uncheckedItem, boolean isChecked, boolean isDisabled) {
        this.checkedProvider = checkedItem == null ? Checkbox.checkedItem("Checkbox", "") : checkedItem;
        this.uncheckedProvider = uncheckedItem == null ? Checkbox.uncheckedItem("Checkbox", "") : uncheckedItem;
        this.disabledState = new ComposableReactiveState<>(isDisabled, this);
        this.checkedState = new ComposableReactiveState<>(isChecked, this);
        this.branchingProvider = () -> this.checkedState.get() ? this.checkedProvider.provide() : this.uncheckedProvider.provide();
    }

    public boolean isDisabled() {
        return disabledState.get();
    }
    public boolean isNotDisabled() {
        return !disabledState.get();
    }
    public void setDisabled(boolean disabled) {
        disabledState.set(disabled);
    }

    public void toggle() {
        checkedState.set(!checkedState.get());
    }
    public boolean isChecked() {
        return checkedState.get();
    }
    public void setChecked(boolean checked) {
        checkedState.set(checked);
    }

    public ReactiveState<Boolean> disabledState() {
        return disabledState;
    }

    public ReactiveState<Boolean> checkedState() {
        return checkedState;
    }

    @Override
    public @NotNull ItemProvider getProvider() {
        return this.branchingProvider;
    }

    @Override
    public Checkbox configure(@NotNull Configurator<Checkbox> configurator) {
        configurator.configure(this);
        return this;
    }
}
