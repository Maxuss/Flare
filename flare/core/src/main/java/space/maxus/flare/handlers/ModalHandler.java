package space.maxus.flare.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.Flare;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.PlayerFrameStateManager;
import space.maxus.flare.ui.ReactiveInventoryHolder;
import space.maxus.flare.ui.compose.complex.Modal;

/**
 * Handles Modal-related logic for Flare
 */
public class ModalHandler implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    void onModalClose(@NotNull InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof ReactiveInventoryHolder holder) || !(holder.getFrame() instanceof Modal.ModalFrame modal))
            return;
        if (modal.getIsClosing().get())
            return;
        modal.getIsClosing().setRelease(true);
        Frame root = modal.getParent().root();
        Player player = (Player) e.getPlayer();

        Bukkit.getScheduler().runTaskLater(
                Flare.getHook(),
                () -> {
                    root.render();
                    player.openInventory(root.selfInventory());
                    root.restorePreviousState(player);
                    PlayerFrameStateManager.restoreSnapshot(e.getPlayer()); // voiding snapshot
                },
                1L
        );
    }
}
