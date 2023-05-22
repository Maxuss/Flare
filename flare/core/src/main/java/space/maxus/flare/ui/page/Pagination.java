package space.maxus.flare.ui.page;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.PackedComposable;
import space.maxus.flare.ui.space.ComposableSpace;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * A pagination is used to include multiple sub-frames (pages) inside a single frame
 * @param <P> Type of the underlying page props
 */
public interface Pagination<P> {
    /**
     * Creates a single page frame
     * @param page Number of the page
     * @param title Title of the page
     * @param props Props passed to the page
     * @return Constructed page
     */
    Frame createPage(int page, @Nullable String title, @NotNull P props);

    /**
     * Gets the page with provided number
     * @param page Number of the page
     * @return Found page
     */
    Frame getPage(int page);

    /**
     * Sets current page number
     * @param page New page number
     */
    void setPage(int page);

    /**
     * Increments atomic counter and returns the index of the next page
     * @return Zero-based index of the next page
     */
    int nextPageIdx();

    /**
     * Gets the current count of pages
     * @return Current count of pages
     */
    int pageCount();

    /**
     * Gets the zero-based index of currently open page
     * @return Zero-based index of currently open page
     */
    int currentPage();

    /**
     * Enables pagination for current frame. Partially internal method
     * @param currentFrame Frame to be used
     */
    void enable(Frame currentFrame);

    /**
     * Switches page for viewer to certain index
     * @param viewer Viewer of this pagination
     * @param to Zero-based index of page to change to
     */
    void switchPage(Player viewer, int to);

    /**
     * Opens this pagination to player
     * @param player Player for whom to open this pagination
     */
    void open(Player player);

    /**
     * Closes this pagination
     */
    void close();

    /**
     * Peeks the previous frame in this pagination
     * @return Previous frame or null
     */
    @Nullable Frame peekPrevious();

    /**
     * Adds shared data to this pagination. Shared components will be injected at the initialization of page frames
     * @param packed Data to be added
     */
    void addSharedData(Map<ComposableSpace, Composable> packed);

    /**
     * Adds shared component to this pagination. Shared components will be injected at the initialization of page frames
     * @param space Space of the composed data
     * @param composable Composable to be added
     */
    void composeShared(@NotNull ComposableSpace space, @NotNull Composable composable);

    /**
     * Adds prioritized shared components to this pagination. Shared components will be injected at the initialization of page frames
     * @param packed Producer for the packed composable. It will be called lazily.
     */
    void composePrioritizedShared(@NotNull Callable<PackedComposable> packed);

    /**
     * Finishes the initialization of this pagination. After this method call {@link #composeShared}, etc. will not have any effect.
     */
    void commit();

    /**
     * Creates a frame with props
     * @param props Props for the frame
     * @return Constructed page frame
     */
    default Frame createPage(@NotNull P props) {
        return this.createPage(nextPageIdx(), null, props);
    }

    /**
     * Creates a frame with props and title
     * @param props Props for the frame
     * @param title Title of the frame
     * @return Constructed page frame
     */
    default Frame createPage(@Nullable String title, @NotNull P props) {
        return this.createPage(nextPageIdx(), title, props);
    }

    /**
     * Switches to the next page for the viewer
     * @param viewer Viewer observing this pagination
     */
    default void nextPage(Player viewer) {
        this.switchPage(viewer, currentPage() + 1);
    }

    /**
     * Switches to the previous page for the viewer
     * @param viewer Viewer observing this pagination
     */
    default void previousPage(Player viewer) {
        this.switchPage(viewer, currentPage() - 1);
    }

    /**
     * Composes shared packed composable data for this pagination. Shared components will be injected at the initialization of page frames
     * @param packed Packed composable to be composed
     */
    default void composeShared(@NotNull PackedComposable packed) {
        this.composeShared(packed.getSpace(), packed.getComposable());
    }
}
