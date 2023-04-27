package space.maxus.flare.ui;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@Data
public class ReactiveInventoryHolder implements InventoryHolder {
    private final @NotNull Frame frame;
    @Getter(AccessLevel.NONE)
    private final @NotNull Inventory inventory;
    private final int size;

    public ReactiveInventoryHolder(@NotNull Frame frame, int size) {
        this.frame = frame;
        this.size = size;
        this.inventory = Bukkit.createInventory(this, size);
    }

    @NotNull
    public Inventory getInventoryNoRender() {
        return inventory;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        frame.render(inventory);
        return inventory;
    }
}
