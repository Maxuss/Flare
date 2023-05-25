package space.maxus.flare.ui.compose.complex;

import org.apache.commons.lang3.concurrent.Computable;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.item.ItemStackBuilder;
import space.maxus.flare.item.Items;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.compose.Configurable;
import space.maxus.flare.ui.page.Pagination;

/**
 * Tabulation is used to display pagination state.
 * <br />
 * See more in Flare docs: <a href="https://flare.maxus.space/ui/overview/#tabulation">Tabulation</a>
 */
public interface Tabulation extends Composable, Configurable<Tabulation> {
    /**
     * Returns the unselected item builder
     * @param idx The page index
     * @param frame The page frame
     * @return Unselected item builder
     */
    static ItemStackBuilder unselectedItemBuilder(int idx, Frame frame) {
        return Items.builder(Material.PLAYER_HEAD)
                .headSkin("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzNlNGI1MzNlNGJhMmRmZjdjMGZhOTBmNjdlOGJlZjM2NDI4YjZjYjA2YzQ1MjYyNjMxYjBiMjVkYjg1YiJ9fX0=")
                .name("<gray>Page %s <dark_gray>[~]".formatted(idx + 1))
                .addLoreLine("<gray>Click to select page")
                .addLoreLine("<dark_gray>~ <green>%s".formatted(frame.getTitle()))
                .hideAllFlags();
    }

    /**
     * Returns the selected item builder
     * @param idx The page index
     * @param frame The page frame
     * @return Selected item builder
     */
    static ItemStackBuilder selectedItemBuilder(int idx, Frame frame) {
        return Items.builder(Material.PLAYER_HEAD)
                .headSkin("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19")
                .name("<gray>Page %s <dark_gray>[<green>~</green>]".formatted(idx + 1))
                .addLoreLine("<green>Currently selected")
                .hideAllFlags();
    }

    /**
     * Constructs a new tabulation
     * @param pagination The pagination to use
     * @return Constructed tabulation
     */
    static @NotNull Tabulation of(Pagination<?> pagination) {
        return new TabulationImpl(pagination, null, null, 0);
    }

    /**
     * Constructs a new tabulation
     * @param pagination The pagination to use
     * @param idx Currently selected page index
     * @return Constructed tabulation
     */
    static @NotNull Tabulation of(Pagination<?> pagination, int idx) {
        return new TabulationImpl(pagination, null, null, idx);
    }

    /**
     * Constructs a new tabulation
     * @param pagination The pagination to use
     * @param selected Builder for selected page button
     * @param unselected Builder for unselected page button
     * @param idx Currently selected page index
     * @return Constructed tabulation
     */
    static @NotNull Tabulation of(
            Pagination<?> pagination,
            @Nullable Computable<Pair<Integer, Frame>, ItemStack> selected,
            @Nullable Computable<Pair<Integer, Frame>, ItemStack> unselected,
            int idx
    ) {
        return new TabulationImpl(pagination, selected, unselected, idx);
    }

    /**
     * Returns bound pagination
     * @return Bound pagination
     */
    Pagination<?> getPagination();

    /**
     * Returns selected page index state
     * @return Selected page index state
     */
    ReactiveState<Integer> selectedIndex();

    /**
     * Returns selected page frame state
     * @return Selected page frame state
     */
    ReactiveState<Frame> selectedFrame();

    /**
     * Switches current page to the given page
     * @param page Page index to switch to
     */
    default void switchPage(int page) {
        getPagination().switchPage(viewer(), page);
    }
}
