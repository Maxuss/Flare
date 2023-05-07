package space.maxus.flare.ui.compose.complex;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.item.ItemProvider;
import space.maxus.flare.item.ItemStackBuilder;
import space.maxus.flare.item.Items;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.compose.Configurable;
import space.maxus.flare.ui.page.Pagination;
import space.maxus.flare.util.SafeComputable;

public interface PaginationDisplay extends Composable, Configurable<PaginationDisplay> {
    static @NotNull ItemStackBuilder arrowForwardButton(boolean disabled) {
        return Items.builder(Material.PLAYER_HEAD)
                .hideAllFlags()
                .name("<gray>Next Pages <dark_gray>[→]")
                .branch(disabled,
                        builder -> builder
                                .headSkin("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjMyY2E2NjA1NmI3Mjg2M2U5OGY3ZjMyYmQ3ZDk0YzdhMGQ3OTZhZjY5MWM5YWMzYTkxMzYzMzEzNTIyODhmOSJ9fX0=")
                                .padLore()
                                .addLoreLine("<dark_gray>Last page reached!"),
                        builder -> builder
                                .headSkin("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU2YTM2MTg0NTllNDNiMjg3YjIyYjdlMjM1ZWM2OTk1OTQ1NDZjNmZjZDZkYzg0YmZjYTRjZjMwYWI5MzExIn19fQ==")
                                .padLore()
                                .addLoreLine("<dark_gray>Click to show next pages")
                );
    }

    static @NotNull ItemStackBuilder arrowBackwardButton(boolean disabled) {
        return Items.builder(Material.PLAYER_HEAD)
                .hideAllFlags()
                .name("<gray>Previous Pages <dark_gray>[←]")
                .branch(disabled,
                        builder -> builder
                                .headSkin("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY5NzFkZDg4MWRiYWY0ZmQ2YmNhYTkzNjE0NDkzYzYxMmY4Njk2NDFlZDU5ZDFjOTM2M2EzNjY2YTVmYTYifX19")
                                .padLore()
                                .addLoreLine("<dark_gray>First page reached!"),
                        builder -> builder
                                .headSkin("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19")
                                .padLore()
                                .addLoreLine("<dark_gray>Click to show previous pages")
                );
    }

    static @NotNull ItemStackBuilder pageNumber(Frame frame, int page, boolean selected) {
        return Items.builder(Material.PLAYER_HEAD)
                .hideAllFlags()
                .branch(selected,
                        builder -> builder
                                .headSkin("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTFhYTU5YjkyZGFhZTA2YjZlNjBhMDg3YzBkOTk1NjFiMjc3MjFiOTZhODk0NWVmZTQzOGUzMWM1OWY0ZWY3In19fQ==")
                                .name("<gray>Page %s <dark_gray>[<green>▼</green>]".formatted(page + 1))
                                .addLoreLine("<gray>In <green>%s".formatted(frame.getTitle()))
                                .padLore()
                                .addLoreLine("<dark_gray>This page is selected"),
                        builder -> builder
                                .headSkin("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDYxYzhmZWJjYWMyMWI5ZjYzZDg3ZjlmZDkzMzU4OWZlNjQ2OGU5M2FhODFjZmNmNWU1MmE0MzIyZTE2ZTYifX19")
                                .name("<gray>Page %s <dark_gray>[▲]".formatted(page + 1))
                                .addLoreLine("<gray>Go to <green>%s".formatted(frame.getTitle()))
                                .padLore()
                                .addLoreLine("<dark_gray>Click to select page")
                );
    }

    static @NotNull PaginationDisplay of(Pagination<?> pagination) {
        return new PaginationDisplayImpl(pagination, 0, null, null, null, null);
    }

    static @NotNull PaginationDisplay of(Pagination<?> pagination, int idx) {
        return new PaginationDisplayImpl(pagination, 0, null, null, null, null);
    }

    static @NotNull Builder builder(Pagination<?> pagination) {
        return new PaginationDisplayImpl.Builder(pagination);
    }

    Pagination<?> getPagination();

    ReactiveState<Integer> selectedIndex();

    ReactiveState<Frame> selectedFrame();

    default void switchPage(int page) {
        getPagination().switchPage(viewer(), page);
    }

    interface Builder {
        @NotNull Builder selectedIndex(int index);

        @NotNull Builder backButton(@Nullable ItemProvider back);

        @NotNull Builder forwardButton(@Nullable ItemProvider forward);

        @NotNull Builder selectedPage(@Nullable SafeComputable<Pair<Integer, Frame>, ItemStack> page);

        @NotNull Builder unselectedPage(@Nullable SafeComputable<Pair<Integer, Frame>, ItemStack> page);

        @NotNull PaginationDisplay build();
    }
}
