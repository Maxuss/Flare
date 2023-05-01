package space.maxus.flare;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.handlers.ClickHandler;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.PlayerFrameStateManager;

@UtilityClass
public class Flare {
    public final ComponentLogger LOGGER = ComponentLogger.logger("Flare");
    private boolean HOOKED = false;
    @Getter
    private Plugin hook;

    public void hook(@NotNull Plugin plugin) {
        if (HOOKED)
            return;
        HOOKED = true;
        hook = plugin;

        LOGGER.info("Hooking Flare to {}...", plugin.getName());

        Bukkit.getPluginManager().registerEvents(new ClickHandler(), hook);
        Bukkit.getPluginManager().registerEvents(new PlayerFrameStateManager(), hook);
    }

    public Inventory open(@NotNull Frame frame, Player player) {
        frame.render();
        player.openInventory(frame.selfInventory());
        frame.onOpen(player);
        return frame.selfInventory();
    }
}
