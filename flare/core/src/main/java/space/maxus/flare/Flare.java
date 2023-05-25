package space.maxus.flare;

import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.extern.FrameRegistry;
import space.maxus.flare.handlers.ClickHandler;
import space.maxus.flare.handlers.ModalHandler;
import space.maxus.flare.nms.NmsHelper;
import space.maxus.flare.nms.NmsVersion;
import space.maxus.flare.nms.generic.ReflectingNmsHelper;
import space.maxus.flare.nms.generic.ReflectionHelper;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.PlayerFrameStateManager;

/**
 * The global Flare class
 */
public class Flare extends JavaPlugin {
    /**
     * Whether PlaceholderAPI is present in this server
     */
    @Getter
    private static boolean placeholderApiSupported = false;
    /**
     * Returns NMS helper, containing some version-dependent platform calculations
     */
    @Getter
    private static NmsHelper nms;
    /**
     * Returns instance of Flare
     */
    @Getter
    private static Flare instance;
    /**
     * Returns the Flare metrics
     */
    @Getter
    @ApiStatus.Internal
    private static @Nullable Metrics metrics;
    /**
     * Returns the frame registry
     */
    @Getter
    private static FrameRegistry frameRegistry;

    public static @NotNull org.slf4j.Logger logger() {
        return getInstance().getSLF4JLogger();
    }

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        this.reloadConfig();

        if(getConfig().getBoolean("bstats.enabled")) {
            logger().info("Enabling metrics");
            metrics = new Metrics(this, 18411);
        }

        placeholderApiSupported = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        if(placeholderApiSupported)
            Flare.logger().info("Enabled PlaceholderAPI support!");

        frameRegistry = new FrameRegistry();

        Bukkit.getPluginManager().registerEvents(new ClickHandler(), instance);
        Bukkit.getPluginManager().registerEvents(new PlayerFrameStateManager(), instance);
        Bukkit.getPluginManager().registerEvents(new ModalHandler(), instance);

        initNms();
    }

    @Override
    public void onDisable() {
        if(metrics != null)
            metrics.shutdown();
    }

    public static @NotNull Inventory open(@NotNull Frame frame, @NotNull Player player) {
        frame.bindViewer(player); // always binding viewer before rendering, since lazy inventory initialization depends on it
        frame.render();
        player.openInventory(frame.selfInventory());
        frame.open(player);
        return frame.selfInventory();
    }

    private void initNms() {
        NmsHelper helper = NmsHelper.getInstance();
        if (helper.getVersion() == NmsVersion.UNKNOWN || helper instanceof ReflectingNmsHelper) {
            Flare.logger().warn("Could not find a suitable dedicated NMS version, using fallback mode...");
            Flare.logger().warn("This may cause issues with some features.");
            Flare.logger().warn("Possibly resolution: update to a newer version of Flare that supports NMS {}", ReflectionHelper.NMS_VERSION.name());
        } else {
            Flare.logger().info("Enabled NMS support for version {}", helper.getVersion());
        }
        nms = helper;
    }
}
