package space.maxus.flare.ui.frames;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.ui.Dimensions;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.ReactiveInventoryHolder;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public abstract class SimpleFrame extends Frame {
    protected final @NotNull Dimensions dimensions;

    protected SimpleFrame() {
        this(Dimensions.SIX_BY_NINE);
        this.init();
    }

    @Override
    public abstract void init();

    @Override
    public @NotNull Inventory baseInventory() {
        return new ReactiveInventoryHolder(this, dimensions.getTotalSize()).getInventoryNoRender();
    }
}
