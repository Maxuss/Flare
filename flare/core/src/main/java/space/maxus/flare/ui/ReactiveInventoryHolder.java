package space.maxus.flare.ui;

import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.util.FlareUtil;

@ToString
@EqualsAndHashCode
public class ReactiveInventoryHolder implements InventoryHolder {
    @Getter
    @Setter
    private @NotNull Frame frame;
    @Getter(AccessLevel.NONE)
    @Setter
    private @NotNull Inventory inventory;
    @Getter
    private int size;

    public ReactiveInventoryHolder(@NotNull Frame frame, int size) {
        this.frame = frame;
        this.size = size;
        this.inventory = Bukkit.createInventory(this, size, FlareUtil.text(frame.getTitle()));
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
