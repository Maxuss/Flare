package space.maxus.flare.ui.frames;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.ui.Dimensions;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.ReactiveInventoryHolder;
import space.maxus.flare.ui.page.DefaultPagination;
import space.maxus.flare.ui.page.PageFrame;
import space.maxus.flare.ui.page.Pagination;
import space.maxus.flare.ui.page.PaginationProxy;
import space.maxus.flare.ui.space.Slot;

import java.util.Map;
import java.util.function.Consumer;

/**
 * PaginatedFrame is a simple frame that supports pagination.
 * Composition inside the {@link #init()} method will not have effect. Use {@link #createPage} instead.
 */
public abstract class PaginatedFrame extends Frame implements PaginationProxy {
    @Getter
    protected final @NotNull Dimensions dimensions;
    @Getter
    protected final @NotNull Pagination<Consumer<PageFrame>> pagination;
    @Getter
    @Setter
    protected @NotNull ReactiveInventoryHolder holder;

    public PaginatedFrame() {
        this(Dimensions.SIX_BY_NINE);
    }

    public PaginatedFrame(Dimensions dimensions) {
        this.dimensions = Dimensions.SIX_BY_NINE;
        this.holder = new ReactiveInventoryHolder(this, this::getViewer, dimensions.getTotalSize());
        this.pagination = new DefaultPagination(0);
        this.init();
        this.pagination.commit();
    }

    @Override
    public final boolean leftClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        return pagination.getPage(pagination.currentPage()).fireLeftClick(slot, e);
    }

    @Override
    public boolean rightClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        return pagination.getPage(pagination.currentPage()).fireRightClick(slot, e);
    }

    @Override
    public void genericClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        pagination.getPage(pagination.currentPage()).fireGenericClick(slot, e);
    }

    @Override
    public boolean shiftClick(@NotNull Slot slot, @NotNull InventoryClickEvent e) {
        return pagination.getPage(pagination.currentPage()).fireShiftClick(slot, e);
    }

    @Override
    public boolean shiftRequest(@NotNull ItemStack stack, @NotNull InventoryClickEvent e) {
        return pagination.getPage(pagination.currentPage()).fireShiftRequest(stack, e);
    }

    @Override
    public boolean drag(@NotNull Map<Slot, ItemStack> newItems, @NotNull InventoryDragEvent e) {
        return pagination.getPage(pagination.currentPage()).fireDrag(newItems, e);
    }

    @Override
    public void onClose() {
        this.pagination.close();
    }

    @Override
    public void onOpen(@NotNull Player player) {
        this.pagination.open(player);
    }

    @Override
    public @NotNull Inventory selfInventory() {
        return holder.getInventoryNoRender();
    }
}
