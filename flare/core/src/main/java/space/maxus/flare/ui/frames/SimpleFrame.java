package space.maxus.flare.ui.frames;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.ui.Dimensions;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.ReactiveInventoryHolder;

/**
 * A simple implementation of {@link Frame}
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public abstract class SimpleFrame extends Frame {
    protected final @NotNull Dimensions dimensions;
    @Setter
    protected @NotNull ReactiveInventoryHolder holder;

    protected SimpleFrame() {
        this.dimensions = Dimensions.SIX_BY_NINE;
        this.holder = new ReactiveInventoryHolder(this, this::getViewer, dimensions.getTotalSize());
        this.init();
    }

    public SimpleFrame(@NotNull Dimensions dimensions) {
        this.dimensions = dimensions;
        this.holder = new ReactiveInventoryHolder(this, this::getViewer, dimensions.getTotalSize());
        this.init();
    }

    @Override
    public abstract void init();

    @Override
    public @NotNull Inventory selfInventory() {
        return holder.getInventoryNoRender();
    }
}
