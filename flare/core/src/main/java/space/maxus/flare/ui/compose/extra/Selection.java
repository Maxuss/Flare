package space.maxus.flare.ui.compose.extra;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.concurrent.Computable;
import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.Flare;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.ItemStackBuilder;
import space.maxus.flare.item.Items;
import space.maxus.flare.react.Reactive;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.compose.Configurable;
import space.maxus.flare.ui.compose.Disable;
import space.maxus.flare.ui.compose.ProviderRendered;
import space.maxus.flare.util.FlareUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public interface Selection<E> extends Disable, ProviderRendered, Configurable<Selection<E>> {
    static <E> List<Component> selectorLore(List<E> enumeration, E selected, Computable<E, String> mapper) {
        return enumeration
                .stream()
                .map(v -> {
                    try {
                        if(v.equals(selected)) {
                            return FlareUtil.text("<green>▶ %s".formatted(mapper.compute(v)));
                        } else {
                            return FlareUtil.text("<gray>⏺ %s".formatted(mapper.compute(v)));
                        }
                    } catch (InterruptedException e) {
                        if(e.getCause() instanceof ThreadDeath) {
                            Thread.currentThread().interrupt();
                        } else {
                            Flare.LOGGER.error("Error while mapping selector", e);
                        }
                        return FlareUtil.text("<red>✘ Error: %s".formatted(v));
                    }
                })
                .toList();
    }

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

    static <E> @NotNull Selection<E> create(Collection<E> enumeration, ItemProvider provider) {
        return new SelectionImpl<>(provider, enumeration, 0, false, null);
    }

    static <E> @NotNull Selection<E> create(Collection<E> enumeration, ItemProvider provider, int selected) {
        return  new SelectionImpl<>(provider, enumeration, selected, false, null);
    }

    @Contract("_, _, _ -> new")
    static <E> @NotNull Selection<E> create(@NotNull Collection<E> enumeration, ItemProvider provider, E selected) {
        return new SelectionImpl<>(provider, enumeration, enumeration.stream().toList().indexOf(selected), false, null);
    }

    static <E> @NotNull Selection<E> create(Collection<E> enumeration, Computable<E, String> formatter) {
        return new SelectionImpl<>(null, enumeration, 0, false, formatter);
    }

    static <E> @NotNull Builder<E> builder(Collection<E> enumeration) {
        return new SelectionImpl.Builder<>(new ArrayList<>(enumeration));
    }

    List<E> enumeration();
    @NotNull E getSelected();
    void setSelected(int index);
    void setSelected(E value);
    @NotNull ReactiveState<E> selectedState();

    interface Builder<E> {
        Builder<E> item(ItemProvider provider);
        Builder<E> selected(int index);
        Builder<E> selectedItem(E item);
        Builder<E> isDisabled(boolean isDisabled);
        Builder<E> formatter(Computable<E, String> mapper);

        Selection<E> build();
    }
}
