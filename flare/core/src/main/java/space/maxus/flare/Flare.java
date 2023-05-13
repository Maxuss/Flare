package space.maxus.flare;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.handlers.ClickHandler;
import space.maxus.flare.handlers.ModalHandler;
import space.maxus.flare.nms.NmsHelper;
import space.maxus.flare.nms.NmsVersion;
import space.maxus.flare.nms.generic.ReflectingNmsHelper;
import space.maxus.flare.nms.generic.ReflectionHelper;
import space.maxus.flare.ui.Frame;
import space.maxus.flare.ui.PlayerFrameStateManager;

public class Flare extends JavaPlugin {
    @Getter
    private static boolean placeholderApiSupported = false;
    @Getter
    private static NmsHelper nms;
    @Getter
    private static Flare instance;

    public static @NotNull org.slf4j.Logger logger() {
        return getInstance().getSLF4JLogger();
    }

    @Override
    public void onEnable() {
        instance = this;

        placeholderApiSupported = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        if(placeholderApiSupported)
            Flare.logger().info("Enabled PlaceholderAPI support!");

        Bukkit.getPluginManager().registerEvents(new ClickHandler(), instance);
        Bukkit.getPluginManager().registerEvents(new PlayerFrameStateManager(), instance);
        Bukkit.getPluginManager().registerEvents(new ModalHandler(), instance);

        initNms();
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
