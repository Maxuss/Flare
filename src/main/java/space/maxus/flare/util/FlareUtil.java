package space.maxus.flare.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.concurrent.Computable;
import org.apache.commons.lang3.concurrent.Memoizer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

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

    @Contract("_,_ -> new")
    public static <K, V> Map<K, V> map2setIntersect(@NotNull Map<K, V> map, @NotNull Set<K> keySet) {
        Map<K, V> result = new HashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (keySet.contains(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public static boolean reduceBoolStream(Stream<Boolean> stream, BinaryOperator<Boolean> operator) {
        Optional<Boolean> result = stream.reduce(operator);
        return result.isPresent() && result.get();
    }
}
