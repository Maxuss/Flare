package space.maxus.flare.ui;

import lombok.*;
import lombok.experimental.StandardException;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.Flare;
import space.maxus.flare.util.FlareUtil;

import java.util.concurrent.Callable;

@ToString
@EqualsAndHashCode
public class ReactiveInventoryHolder implements InventoryHolder {
    @Getter
    @Setter
    private @NotNull Frame frame;
    private @NotNull LazyInitializer<Inventory> inventory;
    @Getter
    private int size;

    public ReactiveInventoryHolder(@NotNull Frame frame, @NotNull Callable<Player> viewerGetter, int size) {
        this.frame = frame;
        this.size = size;
        ReactiveInventoryHolder self = this;
        this.inventory = new LazyInitializer<>() {
            @SneakyThrows // callable should not throw here
            @Override
            protected Inventory initialize() {
                return Bukkit.createInventory(self, size, FlareUtil.text(frame.getTitle(), viewerGetter.call()));
            }
        };
    }

    @NotNull
    public Inventory getInventoryNoRender() {
        return tryGetInventory();
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        frame.render();
        return tryGetInventory();
    }

    public void inherit(@NotNull ReactiveInventoryHolder other) {
        this.size = other.size;
        this.inventory = other.inventory;
    }

    @NotNull Inventory tryGetInventory() {
        try {
            return inventory.get();
        } catch (ConcurrentException e) {
            Flare.LOGGER.error("Failed to get inventory lazily");
            throw new InventoryInitException(e.getCause());
        }
    }

    @StandardException
    static final class InventoryInitException  extends RuntimeException { }
}
