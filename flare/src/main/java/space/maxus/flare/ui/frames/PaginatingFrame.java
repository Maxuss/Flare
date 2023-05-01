package space.maxus.flare.ui.frames;

import lombok.Getter;
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

public abstract class PaginatingFrame extends Frame implements PaginationProxy  {
    protected final @NotNull Dimensions dimensions;
    protected final @NotNull ReactiveInventoryHolder holder;
    @Getter
    protected final @NotNull Pagination<Consumer<PageFrame>> pagination;

    public PaginatingFrame() {
        this(Dimensions.SIX_BY_NINE);
    }

    public PaginatingFrame(Dimensions dimensions) {
        this.dimensions = Dimensions.SIX_BY_NINE;
        this.holder = new ReactiveInventoryHolder(this, dimensions.getTotalSize());
        this.pagination = new DefaultPagination(0);
        this.init();
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
