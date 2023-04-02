package space.maxus.flare.ui;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@Data
@RequiredArgsConstructor
public class ReactiveInventoryHolder implements InventoryHolder {
    private final @NotNull Frame frame;
    @Getter(AccessLevel.NONE)
    private final @NotNull Inventory inventory = frame.baseInventory();

    @NotNull
    @Override
    public Inventory getInventory() {
        frame.render(inventory);
        return inventory;
    }
}
