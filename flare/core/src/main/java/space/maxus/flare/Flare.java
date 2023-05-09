package space.maxus.flare;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.handlers.ClickHandler;
import space.maxus.flare.handlers.ModalHandler;
import space.maxus.flare.nms.NmsHelper;
import space.maxus.flare.nms.NmsVersion;
import space.maxus.flare.nms.generic.ReflectingNmsHelper;
import space.maxus.flare.nms.generic.ReflectionHelper;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.PlayerFrameStateManager;

@UtilityClass
public class Flare {
    public final ComponentLogger LOGGER = ComponentLogger.logger("Flare");
    private boolean HOOKED = false;
    private final LazyInitializer<Boolean> placeholderApiSupported = new LazyInitializer<>() {
        @Override
        protected @NotNull Boolean initialize() {
            boolean enabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
            if(enabled)
                LOGGER.info("Found PlaceholderAPI! Placeholder support enabled.");
            return enabled;
        }
    };
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
        Bukkit.getPluginManager().registerEvents(new ModalHandler(), hook);

        initNms();
    }

    public boolean isPlaceholderApiSupported() {
        try {
            return placeholderApiSupported.get();
        } catch (ConcurrentException e) {
            return false;
        }
    }

    public Inventory open(@NotNull Frame frame, @NotNull Player player) {
        frame.bindViewer(player); // always binding viewer before rendering, since lazy inventory initialization depends on it
        frame.render();
        player.openInventory(frame.selfInventory());
        frame.open(player);
        return frame.selfInventory();
    }

    private void initNms() {
        NmsHelper helper = NmsHelper.getInstance();
        if (helper.getVersion() == NmsVersion.UNKNOWN || helper instanceof ReflectingNmsHelper) {
            Flare.LOGGER.warn("Could not find a suitable dedicated NMS version, using fallback mode...");
            Flare.LOGGER.warn("This may cause issues with some features.");
            Flare.LOGGER.warn("Possibly resolution: update to a newer version of Flare that supports NMS {}", ReflectionHelper.NMS_VERSION.name());
        } else {
            Flare.LOGGER.info("Enabled NMS support for version {}", helper.getVersion());
        }
        nms = helper;
    }
}
