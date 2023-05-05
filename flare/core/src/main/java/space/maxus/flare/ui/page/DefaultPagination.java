package space.maxus.flare.ui.page;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.ReactiveInventoryHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public final class DefaultPagination implements Pagination<Consumer<PageFrame>> {
    private final AtomicInteger pageCount = new AtomicInteger(0);
    private final List<PageFrame> pages = new ArrayList<>();
    private final AtomicInteger currentPage;
    private final ReadWriteLock pageLock = new ReentrantReadWriteLock();

    public DefaultPagination(int defaultPage) {
        this.currentPage = new AtomicInteger(defaultPage);
    }

    @Override
    public @NotNull Frame createPage(int page, @NotNull Consumer<PageFrame> props) {
        PageFrame frame = new PageFrame(new PageFrame.Props(page, props));
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
        if(to + 1 > pageCount.get() || to <= -1)
            return;
        // Closing current page
        PageFrame previous = (PageFrame) getPage(currentPage.get());
        previous.close();

        // Opening new page
        PageFrame page = (PageFrame) getPage(to);
        setPage(to);
        page.getHolder().inherit(previous.getHolder());
        page.load();
        page.render();
    }

    @Override
    public void open(Player player) {
        PageFrame frame = (PageFrame) getPage(currentPage.get());
        ReactiveInventoryHolder holder = (ReactiveInventoryHolder) player.getOpenInventory().getTopInventory().getHolder();
        assert holder != null;
        frame.getHolder().inherit(holder);
        frame.load();
        frame.render();
        frame.open(player);
    }

    @Override
    public void close() {
        pages.forEach(Frame::close);
    }

    @Override
    public @Nullable Frame peekPrevious() {
        return this.currentPage() == 0 ? null : this.getPage(this.currentPage() - 1);
    }
}
