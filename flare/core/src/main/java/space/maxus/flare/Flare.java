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
import space.maxus.flare.nms.NmsHelper;
import space.maxus.flare.nms.NmsVersion;
import space.maxus.flare.nms.generic.ReflectingNmsHelper;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.PlayerFrameStateManager;

@UtilityClass
public class Flare {
    public final ComponentLogger LOGGER = ComponentLogger.logger("Flare");
    private boolean HOOKED = false;
    @Getter
    private NmsHelper nms;
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

        initNms();
    }

    public Inventory open(@NotNull Frame frame, @NotNull Player player) {
        frame.render();
        player.openInventory(frame.selfInventory());
        frame.open(player);
        return frame.selfInventory();
    }

    private void initNms() {
        NmsHelper helper = NmsHelper.getInstance();
        if(helper.getVersion() == NmsVersion.UNKNOWN || helper instanceof ReflectingNmsHelper) {
            Flare.LOGGER.warn("Could not find a suitable dedicated NMS version, using fallback mode...");
            Flare.LOGGER.warn("This may cause issues with some features.");
            Flare.LOGGER.warn("Possibly resolution: update to a newer version of Flare that supports NMS {}", NmsVersion.currentStrVersion());
        } else {
            Flare.LOGGER.info("Enabled NMS support for version {}", helper.getVersion());
        }
        nms = helper;
    }
}