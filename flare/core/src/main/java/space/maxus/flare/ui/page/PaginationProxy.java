package space.maxus.flare.ui.page;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.ui.compose.complex.PaginationDisplay;
import space.maxus.flare.ui.compose.complex.Tabulation;
import space.maxus.flare.ui.space.ComposableSpace;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public interface PaginationProxy {
    Pagination<Consumer<PageFrame>> getPagination();

    default void createPage(Consumer<PageFrame> configurator) {
        getPagination().createPage(configurator);
    }

    default void createPage(String title, Consumer<PageFrame> configurator) {
        getPagination().createPage(title, configurator);
    }

    default void switchPage(Player viewer, int to) {
        getPagination().switchPage(viewer, to);
    }

    default void nextPage(Player viewer) {
        getPagination().nextPage(viewer);
    }

    default void previousPage(Player viewer) {
        getPagination().previousPage(viewer);
    }

    default void useTabulation(ComposableSpace space) {
        getPagination().composePrioritizedShared(() -> Tabulation.of(getPagination()).inside(space));
    }

    default void useTabulation(ComposableSpace space, @NotNull Callable<Tabulation> tabulation) {
        getPagination().composePrioritizedShared(() -> tabulation.call().inside(space));
    }

    default void usePaginationDisplay(ComposableSpace space) {
        getPagination().composePrioritizedShared(() -> PaginationDisplay.of(getPagination()).inside(space));
    }

    default void usePaginationDisplay(ComposableSpace space, @NotNull Callable<PaginationDisplay> paginationDisplay) {
        getPagination().composePrioritizedShared(() -> paginationDisplay.call().inside(space));
    }
}
