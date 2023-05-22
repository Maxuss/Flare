package space.maxus.flare.ui.page;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.ui.compose.complex.PaginationDisplay;
import space.maxus.flare.ui.compose.complex.Tabulation;
import space.maxus.flare.ui.space.ComposableSpace;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * A proxy interface for better accessibility.
 *
 * @see space.maxus.flare.ui.frames.PaginatedFrame
 * @see space.maxus.flare.ui.frames.ParamPaginatedFrame
 */
public interface PaginationProxy {
    /**
     * Gets the pagination of this structure
     * @return Pagination suitable for {@link PageFrame} creation
     */
    Pagination<Consumer<PageFrame>> getPagination();

    /**
     * Creates a page with the provided configurator function
     * @param configurator The configurator function
     */
    default void createPage(Consumer<PageFrame> configurator) {
        getPagination().createPage(configurator);
    }

    /**
     * Creates a page with the provided configurator function and title
     * @param configurator The configurator function
     * @param title Title of the page
     */
    default void createPage(String title, Consumer<PageFrame> configurator) {
        getPagination().createPage(title, configurator);
    }

    /**
     * Switches page for viewer to certain index
     * @param viewer Viewer of this pagination
     * @param to Zero-based index of page to change to
     */
    default void switchPage(Player viewer, int to) {
        getPagination().switchPage(viewer, to);
    }

    /**
     * Switches to the next page for the viewer
     * @param viewer Viewer observing this pagination
     */
    default void nextPage(Player viewer) {
        getPagination().nextPage(viewer);
    }

    /**
     * Switches to the previous page for the viewer
     * @param viewer Viewer observing this pagination
     */
    default void previousPage(Player viewer) {
        getPagination().previousPage(viewer);
    }

    /**
     * Configures a {@link Tabulation} inside all page frames
     * @param space Space reserved for the tabulation
     */
    default void useTabulation(ComposableSpace space) {
        getPagination().composePrioritizedShared(() -> Tabulation.of(getPagination()).inside(space));
    }

    /**
     * Configures a {@link Tabulation} inside all page frames
     * @param space Space reserved for the tabulation
     * @param tabulation Producer of the tabulation
     */
    default void useTabulation(ComposableSpace space, @NotNull Callable<Tabulation> tabulation) {
        getPagination().composePrioritizedShared(() -> tabulation.call().inside(space));
    }

    /**
     * Configures a {@link PaginationDisplay} inside all page frames
     * @param space Space reserved for the pagination display
     */
    default void usePaginationDisplay(ComposableSpace space) {
        getPagination().composePrioritizedShared(() -> PaginationDisplay.of(getPagination()).inside(space));
    }

    /**
     * Configures a {@link PaginationDisplay} inside all page frames
     * @param space Space reserved for the pagination display
     * @param paginationDisplay Producer of the pagination display
     */
    default void usePaginationDisplay(ComposableSpace space, @NotNull Callable<PaginationDisplay> paginationDisplay) {
        getPagination().composePrioritizedShared(() -> paginationDisplay.call().inside(space));
    }
}
