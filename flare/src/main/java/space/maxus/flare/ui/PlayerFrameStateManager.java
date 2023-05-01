package space.maxus.flare.ui;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.Flare;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerFrameStateManager implements Listener {
    private final ConcurrentHashMap<UUID, List<Frame>> snapshots = new ConcurrentHashMap<>();

    public void saveSnapshot(@NotNull HumanEntity to, @NotNull Frame snapshot) {
        Flare.LOGGER.info("SIZE: {}", snapshots.size());

        Validate.notNull(to, "Tried to save a frame snapshot to a null player");
        Validate.notNull(to, "Tried to save a null frame snapshot");

        if (!snapshots.containsKey(to.getUniqueId())) {
            List<Frame> list = new ArrayList<>();
            list.add(snapshot);
            snapshots.put(to.getUniqueId(), list);
        } else {
            snapshots.get(to.getUniqueId()).add(snapshot);
        }
    }

    public @Nullable Frame restoreSnapshot(@NotNull HumanEntity from) {
        Validate.notNull(from, "Tried to restore a frame snapshot from a null player");

        if (!snapshots.containsKey(from.getUniqueId()))
            return null;
        List<Frame> list = snapshots.get(from.getUniqueId());
        return list.remove(list.size() - 1);
    }

    @EventHandler
    void onClose(InventoryCloseEvent e) {
        if (e.getReason() == InventoryCloseEvent.Reason.OPEN_NEW) {
            if (e.getInventory().getHolder() instanceof ReactiveInventoryHolder holder)
                saveSnapshot(e.getPlayer(), holder.getFrame());
        } else if (e.getReason() != InventoryCloseEvent.Reason.TELEPORT) {
            // TELEPORT is the Magic value for internal inventory closing
            snapshots.remove(e.getPlayer().getUniqueId());
        }
    }
}
