package space.maxus.flare.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.concurrent.Computable;
import org.apache.commons.lang3.concurrent.Memoizer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@UtilityClass
public class FlareUtil {
    private final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("flare-%d").build();
    private final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(4, THREAD_FACTORY);
    public void execute(Runnable executable) {
        THREAD_POOL.execute(executable);
    }
    public ExecutorService executor(int threads) {
        return Executors.newFixedThreadPool(threads, THREAD_FACTORY);
    }
    public Component text(String miniMessage) {
        return MINI_MESSAGE.deserialize(miniMessage);
    }
    public <I, O> Computable<I, O> memoize(Computable<I, O> producer) {
        return new Memoizer<>(producer, true);
    }
}
