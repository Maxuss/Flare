package space.maxus.flare.ui.page;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.ui.Composable;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.PackedComposable;
import space.maxus.flare.ui.space.ComposableSpace;

import java.util.Map;
import java.util.concurrent.Callable;

public interface Pagination<P> {
    Frame createPage(int page, @NotNull P props);
    Frame getPage(int page);
    void setPage(int page);
    int nextPageIdx();
    int pageCount();
    int currentPage();
    void enable(Frame currentFrame);
    void switchPage(Player viewer, int to);
    void open(Player player);
    void close();
    Frame peekPrevious();
    void addSharedData(Map<ComposableSpace, Composable> packed);
    void composeShared(@NotNull ComposableSpace space, @NotNull Composable composable);
    void composePrioritizedShared(@NotNull Callable<PackedComposable> packed);
    void commit();

    default Frame createPage(@NotNull P props) {
        return this.createPage(nextPageIdx(), props);
    }

    default void nextPage(Player viewer) {
        this.switchPage(viewer, currentPage() + 1);
    }

    default void previousPage(Player viewer) {
        this.switchPage(viewer, currentPage() - 1);
    }

    default void composeShared(@NotNull PackedComposable packed) {
        this.composeShared(packed.getSpace(), packed.getComposable());
    }
}
