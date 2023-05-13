package space.maxus.flare.ui.page;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.Flare;
import space.maxus.flare.ui.*;
import space.maxus.flare.ui.space.ComposableSpace;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public final class DefaultPagination implements Pagination<Consumer<PageFrame>> {
    private final AtomicInteger pageCount = new AtomicInteger(0);
    private final List<PageFrame> pages = new ArrayList<>();
    private final AtomicInteger currentPage;
    private final Map<ComposableSpace, Composable> sharedData = new LinkedHashMap<>();
    private final ConcurrentLinkedQueue<Callable<PackedComposable>> prioritizedSharedData = new ConcurrentLinkedQueue<>();
    private final ReadWriteLock pageLock = new ReentrantReadWriteLock();

    public DefaultPagination(int defaultPage) {
        this.currentPage = new AtomicInteger(defaultPage);
    }

    @Override
    public @NotNull Frame createPage(int page, @Nullable String title, @NotNull Consumer<PageFrame> props) {
        PageFrame frame = new PageFrame(new PageFrame.Props(page, title, Dimensions.SIX_BY_NINE, sharedData, props));
        Lock lock = pageLock.writeLock();
        lock.lock();
        pages.add(frame);
        lock.unlock();
        return frame;
    }

    @Override
    public Frame getPage(int page) {
        Lock lock = pageLock.readLock();
        lock.lock();
        PageFrame frame = pages.get(page);
        lock.unlock();
        return frame;
    }

    @Override
    public void setPage(int page) {
        currentPage.setRelease(page);
    }

    @Override
    public int nextPageIdx() {
        return pageCount.getAndIncrement();
    }

    @Override
    public int pageCount() {
        return pageCount.get();
    }

    @Override
    public int currentPage() {
        return currentPage.get();
    }

    @Override
    public void enable(@NotNull Frame currentFrame) {
        PageFrame page = (PageFrame) getPage(currentPage.get());
        page.getHolder().inherit((ReactiveInventoryHolder) Objects.requireNonNull(currentFrame.selfInventory().getHolder()));
        page.render();
    }

    @Override
    public void switchPage(Player viewer, int to) {
        if (to + 1 > pageCount.get() || to <= -1)
            return;
        // Closing current page
        PageFrame previous = (PageFrame) getPage(currentPage.get());
        previous.close();

        // Opening new page
        PageFrame page = (PageFrame) getPage(to);
        setPage(to);
        page.getHolder().inherit(previous.getHolder());
        page.bindViewer(viewer); // always binding viewer before rendering, since lazy inventory initialization depends on it
        page.load();
        page.render();
        page.refreshTitle();
    }

    @Override
    public void open(Player player) {
        PageFrame frame = (PageFrame) getPage(currentPage.get());
        ReactiveInventoryHolder holder = (ReactiveInventoryHolder) player.getOpenInventory().getTopInventory().getHolder();
        assert holder != null;
        frame.getHolder().inherit(holder);
        frame.bindViewer(player); // always binding viewer before rendering, since lazy inventory initialization depends on it
        frame.load();
        frame.render();
        frame.open(player);
        frame.refreshTitle();
    }

    @Override
    public void close() {
        pages.forEach(Frame::close);
    }

    @Override
    public @Nullable Frame peekPrevious() {
        return this.currentPage() == 0 ? null : this.getPage(this.currentPage() - 1);
    }

    @Override
    public void addSharedData(Map<ComposableSpace, Composable> packed) {
        Lock lock = pageLock.writeLock();
        lock.lock();
        sharedData.putAll(packed);
        lock.unlock();
    }

    @Override
    public void composeShared(@NotNull ComposableSpace space, @NotNull Composable composable) {
        Lock lock = pageLock.writeLock();
        lock.lock();
        sharedData.put(space, composable);
        lock.unlock();
    }

    @Override
    public void composePrioritizedShared(@NotNull Callable<PackedComposable> packed) {
        Lock lock = pageLock.writeLock();
        lock.lock();
        prioritizedSharedData.add(packed);
        lock.unlock();

    }

    @Override
    public void commit() {
        Lock lock = pageLock.writeLock();
        lock.lock();
        prioritizedSharedData.forEach(c -> {
            try {
                PackedComposable value = c.call();
                sharedData.put(value.getSpace(), value.getComposable());
            } catch (Exception e) {
                Flare.logger().error("Error while committing shared data", e);
            }
        });
        prioritizedSharedData.clear();
        lock.unlock();
    }
}
