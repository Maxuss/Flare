package space.maxus.flare.ui;

import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@ToString @EqualsAndHashCode
public class ReactiveInventoryHolder implements InventoryHolder {
    @Getter
    private final @NotNull Frame frame;
    @Getter(AccessLevel.NONE)
    private @NotNull Inventory inventory;
    @Getter
    private int size;

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
        frame.render();
        return inventory;
    }

    public void inherit(@NotNull ReactiveInventoryHolder other) {
        this.size = other.size;
        this.inventory = other.inventory;
    }
}
