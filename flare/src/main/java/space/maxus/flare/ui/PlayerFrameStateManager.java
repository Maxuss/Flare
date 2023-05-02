package space.maxus.flare.ui;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerFrameStateManager implements Listener {
    private static final ConcurrentHashMap<UUID, List<Frame>> snapshots = new ConcurrentHashMap<>();

    public static void saveSnapshot(@NotNull HumanEntity to, @NotNull Frame snapshot) {
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

    public static @Nullable Frame restoreSnapshot(@NotNull HumanEntity from) {
        Validate.notNull(from, "Tried to restore a frame snapshot from a null player");

        if (!snapshots.containsKey(from.getUniqueId()))
            return null;
        List<Frame> list = snapshots.get(from.getUniqueId());
        return list.remove(list.size() - 1);
    }

    @EventHandler
    void onClose(@NotNull InventoryCloseEvent e) {
        if(!(e.getInventory().getHolder() instanceof ReactiveInventoryHolder flare))
            return;
        if(e.getReason() != InventoryCloseEvent.Reason.TELEPORT) {
            flare.getFrame().close();
            if(e.getReason() == InventoryCloseEvent.Reason.OPEN_NEW)
                saveSnapshot(e.getPlayer(), flare.getFrame()); // saving snapshot in case player wants to return back
            else
                snapshots.remove(e.getPlayer().getUniqueId()); // player left UI, we clear the snapshots
        }
    }

    @EventHandler
    void onLeave(@NotNull PlayerQuitEvent e) {
        snapshots.remove(e.getPlayer().getUniqueId());
    }
}
