package space.maxus.flare.ui.frames;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.ui.Dimensions;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.ReactiveInventoryHolder;

/**
 * A simple frame that can take in extra props.
 * @param <P> Type of frame props.
 */
public abstract class ParamFrame<P> extends Frame {
    @Getter
    protected final @NotNull Dimensions dimensions;
    protected final @NotNull P props;
    @Getter
    @Setter
    protected @NotNull ReactiveInventoryHolder holder;

    protected ParamFrame(@NotNull P params) {
        this(params, Dimensions.SIX_BY_NINE);
    }

    public ParamFrame(@NotNull P params, @NotNull Dimensions dimensions) {
        this.dimensions = dimensions;
        this.holder = new ReactiveInventoryHolder(this, this::getViewer, dimensions.getTotalSize());
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
