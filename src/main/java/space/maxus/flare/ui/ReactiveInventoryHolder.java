package space.maxus.flare.ui;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@Data
public class ReactiveInventoryHolder implements InventoryHolder {
    private final @NotNull Frame frame;
    @Getter(AccessLevel.NONE)
    private final @NotNull Inventory inventory;

    public ReactiveInventoryHolder(@NotNull Frame frame) {
        this.frame = frame;
        this.inventory = frame.baseInventory();
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        frame.render(inventory);
        return inventory;
    }
}
