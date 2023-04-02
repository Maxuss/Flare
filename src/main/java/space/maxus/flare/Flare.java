package space.maxus.flare;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.handlers.ClickHandler;
import space.maxus.flare.ui.PlayerFrameStateManager;

import java.util.concurrent.*;

@UtilityClass
public class Flare {
    public final ComponentLogger LOGGER = ComponentLogger.logger("Flare-Core");
    private final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("flare-%d").build();
    private final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(4, THREAD_FACTORY);
    private boolean HOOKED = false;
    @Getter
    private Plugin hook;

    public void execute(Runnable executable) {
        THREAD_POOL.execute(executable);
    }

    public ExecutorService executor(int threads) {
        return Executors.newFixedThreadPool(threads, THREAD_FACTORY);
    }

    public void hook(@NotNull Plugin plugin) {
        if(HOOKED)
            return;
        HOOKED = true;
        hook = plugin;

        LOGGER.info("Hooking Flare to the {} plugin...", plugin.getName());

        Bukkit.getPluginManager().registerEvents(new ClickHandler(), hook);
        Bukkit.getPluginManager().registerEvents(new PlayerFrameStateManager(), hook);
    }
}
