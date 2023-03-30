package space.maxus.flare;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.util.concurrent.*;

@UtilityClass
public class Flare {
    public final ComponentLogger LOGGER = ComponentLogger.logger("Flare-Core");
    private final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("flare-%d").build();
    private final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(4, THREAD_FACTORY);

    public void execute(Runnable executable) {
        THREAD_POOL.execute(executable);
    }

    public ExecutorService executor(int threads) {
        return Executors.newFixedThreadPool(threads, THREAD_FACTORY);
    }
}
