package space.maxus.flare.ui.frames;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.ui.Dimensions;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.ReactiveInventoryHolder;
import space.maxus.flare.ui.page.DefaultPagination;
import space.maxus.flare.ui.page.PageFrame;
import space.maxus.flare.ui.page.Pagination;
import space.maxus.flare.ui.page.PaginationProxy;

import java.util.function.Consumer;

public abstract class ParamPaginatedFrame<P> extends Frame implements PaginationProxy {
    protected final @NotNull ReactiveInventoryHolder holder;
    @Getter
    protected final @NotNull Pagination<Consumer<PageFrame>> pagination;
    protected final @NotNull P props;
    @Getter
    @Setter
    protected Dimensions dimensions;

    public ParamPaginatedFrame(@NotNull P props, @NotNull Dimensions dimensions) {
        this.props = props;
        this.dimensions = dimensions;
        this.holder = new ReactiveInventoryHolder(this, this::getViewer, dimensions.getTotalSize());
        this.pagination = new DefaultPagination(0);
        this.preInit(props);
        this.init();
        this.pagination.commit();
    }

    public ParamPaginatedFrame(@NotNull P props) {
        this(props, Dimensions.SIX_BY_NINE);
    }

    public void preInit(P props) {

    }

    @Override
    public @NotNull Inventory selfInventory() {
        return holder.getInventoryNoRender();
    }
}
