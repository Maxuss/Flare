package space.maxus.flare.ui;

import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class Frame {
    protected final @NotNull ConcurrentHashMap<@NotNull ComposableSpace, @NotNull Composable> composed = new ConcurrentHashMap<>();

    public void render(@NotNull Inventory inside) {
        composed.forEach((key, value) -> {
            for(Slot slot: key.slots()) {
                inside.getContents()[slot.rawSlot()] = value.renderAt(slot);
            }
        });
    }

    public @NotNull ConcurrentMap<ComposableSpace, Composable> composableMap() {
        return composed;
    }

    public void compose(@NotNull ComposableSpace space, @NotNull Composable element) {
        this.composed.put(space, element);
    }

    public abstract void init();
    public abstract @NotNull Inventory baseInventory();
}
