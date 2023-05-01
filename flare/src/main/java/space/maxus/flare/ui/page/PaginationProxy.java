package space.maxus.flare.ui.page;

import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface PaginationProxy {
    Pagination<Consumer<PageFrame>> getPagination();

    default void createPage(Consumer<PageFrame> configurator) {
        getPagination().createPage(configurator);
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
}
