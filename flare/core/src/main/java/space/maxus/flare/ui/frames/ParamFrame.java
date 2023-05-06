package space.maxus.flare.ui.frames;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.ui.Dimensions;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.ReactiveInventoryHolder;

public abstract class ParamFrame<P> extends Frame {
    @Getter
    protected final @NotNull Dimensions dimensions;
    protected final @NotNull P props;
    @Getter
    @Setter
    protected @NotNull ReactiveInventoryHolder holder;

    protected ParamFrame(@NotNull P params) {
        this.dimensions = Dimensions.SIX_BY_NINE;
        this.holder = new ReactiveInventoryHolder(this, dimensions.getTotalSize());
        this.props = params;
        this.preInit(params);
        this.init();
    }

    public ParamFrame(@NotNull P params, @NotNull Dimensions dimensions) {
        this.dimensions = dimensions;
        this.holder = new ReactiveInventoryHolder(this, dimensions.getTotalSize());
        this.props = params;
        this.preInit(params);
        this.init();
    }

    public void preInit(P params) {

    }

    @Override
    public abstract void init();

    @Override
    public @NotNull Inventory selfInventory() {
        return holder.getInventoryNoRender();
    }
}
