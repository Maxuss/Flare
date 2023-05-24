package space.maxus.flare.ui.compose;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.concurrent.Computable;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.Flare;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.ItemStackBuilder;
import space.maxus.flare.item.Items;
import space.maxus.flare.react.Reactive;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.ComposableLike;
import space.maxus.flare.util.FlareUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * A selection is a component that houses a list of values. Only one list value can be selected.
 * <br />
 * See more in Flare docs: <a href="https://flare.maxus.space/ui/composable#selection">Selection</a>
 * @param <E> The type of selection value
 */
public interface Selection<E> extends Disable, ProviderRendered, Configurable<Selection<E>> {
    /**
     * Builds lore list for a selection
     * @param enumeration The enumeration of values
     * @param selected Currently selected value
     * @param mapper Mapper function that converts values to display strings
     * @return List of lore components
     * @param <E> The type of selection value
     */
    static <E> List<Component> selectorLore(@NotNull List<E> enumeration, E selected, Computable<E, String> mapper) {
        return enumeration
                .stream()
                .map(v -> {
                    try {
                        if (v.equals(selected)) {
                            return FlareUtil.text("<green>▶ %s".formatted(mapper.compute(v)));
                        } else {
                            return FlareUtil.text("<gray>⏺ %s".formatted(mapper.compute(v)));
                        }
                    } catch (InterruptedException e) {
                        if (e.getCause() instanceof ThreadDeath) {
                            Thread.currentThread().interrupt();
                        } else {
                            Flare.logger().error("Error while mapping selector", e);
                        }
                        return FlareUtil.text("<red>✘ Error: %s".formatted(v));
                    }
                })
                .toList();
    }

    /**
     * Returns a reactive selector item provider with certain settings
     * @param name Extra message in the item name
     * @param description Extra description in the lore
     * @param enumeration Enumeration of selection values
     * @param selector The selection reactive state
     * @param mapper Mapper function that converts values to display strings
     * @return A reactive selector item provider with certain settings
     * @param <E> The type of selection value
     */
    static <E> @NotNull ItemProvider selector(String name, String description, List<E> enumeration, ReactiveState<E> selector, Computable<E, String> mapper) {
        return Reactive.item(selector, value -> Items
                .builder(Material.PLAYER_HEAD)
                .headSkin("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIzZTYxOWRjYjc1MTFjZGMyNTJhNWRjYTg1NjViMTlkOTUyYWM5ZjgyZDQ2N2U2NmM1MjI0MmY5Y2Q4OGZhIn19fQ==")
                .name("<gray>%s <dark_gray>[☰]".formatted(name))
                .lore(description)
                .addLore(selectorLore(enumeration, value, mapper))
                .padLore()
                .addLoreLine("<dark_gray>Left Click → Forward")
                .addLoreLine("<dark_gray>Right Click → Backward")
                .hideAllFlags()
                .build()
        );
    }

    /**
     * Returns a selector item builder with certain settings
     * @param name Extra message in the item name
     * @param description Extra description in the lore
     * @param enumeration Enumeration of selection values
     * @param mapper Mapper function that converts values to display strings
     * @param value Currently selected value
     * @return A selector item builder with certain settings
     * @param <E> The type of selection value
     */
    static <E> @NotNull ItemStackBuilder selectorBuilder(String name, String description, List<E> enumeration, E value, Computable<E, String> mapper) {
        return Items
                .builder(Material.PLAYER_HEAD)
                .headSkin("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIzZTYxOWRjYjc1MTFjZGMyNTJhNWRjYTg1NjViMTlkOTUyYWM5ZjgyZDQ2N2U2NmM1MjI0MmY5Y2Q4OGZhIn19fQ==")
                .name("<gray>%s <dark_gray>[☰]".formatted(name))
                .lore(description)
                .addLore(selectorLore(enumeration, value, mapper))
                .lore("<dark_gray>Left Click → Forward")
                .lore("<dark_gray>Right Click → Backward")
                .hideAllFlags();
    }

    /**
     * Returns a selector item builder with certain settings
     * @param name Extra message in the item name
     * @param description Extra description in the lore
     * @param enumeration Enumeration of selection values
     * @param mapper Mapper function that converts values to display strings
     * @param player Player for selection
     * @param value Currently selected value
     * @return A selector item builder with certain settings
     * @param <E> The type of selection value
     */
    static <E> @NotNull ItemStackBuilder selectorBuilder(String name, String description, List<E> enumeration, E value, Computable<E, String> mapper, @Nullable Player player) {
        return Items
                .builder(Material.PLAYER_HEAD, player)
                .headSkin("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzIzZTYxOWRjYjc1MTFjZGMyNTJhNWRjYTg1NjViMTlkOTUyYWM5ZjgyZDQ2N2U2NmM1MjI0MmY5Y2Q4OGZhIn19fQ==")
                .name("<gray>%s <dark_gray>[☰]".formatted(name))
                .lore(description)
                .addLore(selectorLore(enumeration, value, mapper))
                .lore("<dark_gray>Left Click → Forward")
                .lore("<dark_gray>Right Click → Backward")
                .hideAllFlags();
    }

    /**
     * Constructs a new selection
     * @param enumeration Enumeration of all possible values
     * @param provider The item provider
     * @return A new selection
     * @param <E> The type of selection value
     */
    static <E> @NotNull Selection<E> of(Collection<E> enumeration, ItemProvider provider) {
        return new SelectionImpl<>(provider, enumeration, 0, false, null);
    }

    /**
     * Constructs a new selection
     * @param enumeration Enumeration of all possible values
     * @param provider The item provider
     * @param selected Zero-based index of the selected item
     * @return A new selection
     * @param <E> The type of selection value
     */
    static <E> @NotNull Selection<E> of(Collection<E> enumeration, ItemProvider provider, int selected) {
        return new SelectionImpl<>(provider, enumeration, selected, false, null);
    }

    /**
     * Constructs a new selection
     * @param enumeration Enumeration of all possible values
     * @param provider The item provider
     * @param selected The selected value
     * @return A new selection
     * @param <E> The type of selection value
     */
    @Contract("_, _, _ -> new")
    static <E> @NotNull Selection<E> of(@NotNull Collection<E> enumeration, ItemProvider provider, E selected) {
        return new SelectionImpl<>(provider, enumeration, enumeration.stream().toList().indexOf(selected), false, null);
    }

    /**
     * Constructs a new selection
     * @param enumeration Enumeration of all possible values
     * @param formatter The formatter function that takes selection value and turns it to a display string
     * @return A new selection
     * @param <E> The type of selection value
     */
    static <E> @NotNull Selection<E> of(Collection<E> enumeration, Computable<E, String> formatter) {
        return new SelectionImpl<>(null, enumeration, 0, false, formatter);
    }

    /**
     * Constructs a new selection builder
     * @param enumeration Enumeration of all possible values
     * @return A new selection builder
     * @param <E> The type of selection values
     */
    static <E> @NotNull Builder<E> builder(Collection<E> enumeration) {
        return new SelectionImpl.Builder<>(new ArrayList<>(enumeration));
    }

    /**
     * Lists all possible values in this selection
     * @return All possible values
     */
    List<E> enumeration();

    /**
     * Returns the selected value
     * @return The selected value
     */
    @NotNull E getSelected();

    /**
     * Sets the selected value
     * @param index Index of the selected value
     */
    void setSelected(int index);

    /**
     * Sets the selected value
     * @param value The new selected value
     */
    void setSelected(E value);

    /**
     * Returns the selected value reactive state
     * @return The selected value reactive state
     */
    @NotNull ReactiveState<E> selectedState();

    /**
     * The selection builder
     * @param <E> The type of selected value
     */
    interface Builder<E> extends ComposableLike {
        /**
         * Sets the item provider. Either item provider or {@link #formatter} should be set.
         * @param provider The item provider to use
         * @return This builder
         */
        Builder<E> item(ItemProvider provider);

        /**
         * Sets the currently selected item index
         * @param index The currently selected item index
         * @return This builder
         */
        Builder<E> selected(int index);

        /**
         * Sets the currently selected item
         * @param item The currently selected item
         * @return This builder
         */
        Builder<E> selectedItem(E item);

        /**
         * Sets the disabled state of selection
         * @param isDisabled The disabled state
         * @return This builder
         */
        Builder<E> isDisabled(boolean isDisabled);

        /**
         * Sets the formatter. Either formatter or {@link #item} should be set.
         * @param mapper The formatter function
         * @return This builder
         */
        Builder<E> formatter(Computable<E, String> mapper);

        /**
         * Builds this selection
         * @return The selection
         */
        Selection<E> build();

        @Override
        default Composable asComposable() {
            return build();
        }
    }
}
