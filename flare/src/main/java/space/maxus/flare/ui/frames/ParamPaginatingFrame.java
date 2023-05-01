package space.maxus.flare.ui.frames;

import lombok.Getter;
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

public abstract class ParamPaginatingFrame<P> extends Frame implements PaginationProxy {
    protected final @NotNull Dimensions dimensions;
    protected final @NotNull ReactiveInventoryHolder holder;
    @Getter
    protected final @NotNull Pagination<Consumer<PageFrame>> pagination;
    protected final @NotNull P props;

    public ParamPaginatingFrame(@NotNull P props, @NotNull Dimensions dimensions) {
        this.props = props;
        this.dimensions = dimensions;
        this.holder = new ReactiveInventoryHolder(this, dimensions.getTotalSize());
        this.pagination = new DefaultPagination(0);
        this.preInit(props);
        this.init();
    }

    public void preInit(P props) {

    }

    public ParamPaginatingFrame(@NotNull P props) {
        this(props, Dimensions.SIX_BY_NINE);
    }

    @Override
    public @NotNull Inventory selfInventory() {
        return holder.getInventoryNoRender();
    }
}
