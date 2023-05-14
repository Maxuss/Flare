package space.maxus.flare.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.experimental.StandardException;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.concurrent.Computable;
import org.apache.commons.lang3.concurrent.Memoizer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.*;
import space.maxus.flare.Flare;
import space.maxus.flare.react.ReactiveState;
import space.maxus.flare.ui.Composable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

/**
 * A utility class containing different utilities for Flare
 */
@UtilityClass
public class FlareUtil {
    /**
     * Flare MiniMessage instance.
     *
     * @see FlareUtil#text
     */
    public final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("flare-%d").build();
    private final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(4, THREAD_FACTORY);
    private final String DASH = "⏤";
    private final String DOT = "•";

    /**
     * Executes a task on the general flare executor service
     * @param executable Task to be executed
     */
    public void execute(Runnable executable) {
        THREAD_POOL.execute(executable);
    }

    /**
     * Constructs a new executor service with the given number of threads
     * @param threads Number of threads to be used
     * @return ExecutorService with the given number of threads
     */
    public ExecutorService executor(int threads) {
        return Executors.newFixedThreadPool(threads, THREAD_FACTORY);
    }

    /**
     * Executes a task <b>asynchronously</b> using Bukkit scheduler. The task will be run `times` times with `period` period
     * @param task Task to be run
     * @param times Number of times to run the task
     * @param period Period between each run of the task <b>in ticks</b>
     */
    public void executeNTimesAsync(Runnable task, int times, long period) {
        AtomicInteger counter = new AtomicInteger(0);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Flare.getInstance(), thisTask -> {
            task.run();
            int count = counter.incrementAndGet();
            if(count >= times) {
                thisTask.cancel();
            }
        }, period, period);
    }

    /**
     * Parses a string into a MiniMessage component
     * @param miniMessage String to be parsed
     * @return Parsed component
     */
    public Component text(String miniMessage) {
        return MINI_MESSAGE.deserialize(miniMessage).decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Parses a sstring into a MiniMessage component with PlaceholderAPI support
     * @param miniMessage String to be parsed
     * @param player Player to be used for placeholder resolution. May be null
     * @return Parsed component
     */
    public Component text(String miniMessage, @Nullable Player player) {
        String parsed = Flare.isPlaceholderApiSupported() && player != null ? PlaceholderAPI.setPlaceholders(player, miniMessage) : miniMessage;
        return text(parsed);
    }

    /**
     * Attempts to get a class for a generic type parameter
     * @return Class of a generic type parameter
     * @param <G> Type parameter to be resolved into class
     */
    @SuppressWarnings("unchecked")
    public <G> Class<G> genericClass() {
        return (Class<G>) new TypeToken<G>() {
        }.getRawType();
    }

    /**
     * Memoizes a computable
     * @param producer Producer to be memoized
     * @return Memoized producer
     * @param <I> Input parameter type
     * @param <O> Output parameter type
     * @see space.maxus.flare.react.ReactivityProvider#useMemo
     */
    public <I, O> Computable<I, O> memoize(Computable<I, O> producer) {
        return new Memoizer<>(producer, true);
    }

    /**
     * Finds an intersection between map keys and provided set, returning part of map with matched keys
     * @param map Map to be used
     * @param keySet Keys to be used
     * @return Map with matched keys
     * @param <K> Type of keys
     * @param <V> Type of values
     */
    @Contract("_,_ -> new")
    public <K, V> @NotNull Map<K, V> map2setIntersect(@NotNull Map<K, V> map, @NotNull Set<K> keySet) {
        Map<K, V> result = new HashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (keySet.contains(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * Reduces a java stream of Booleans to a single boolean
     * @param stream Stream to be reduced
     * @param operator Operator to be applied on each pair
     * @return Result of the reduction
     */
    public boolean reduceBoolStream(@NotNull Stream<Boolean> stream, BinaryOperator<Boolean> operator) {
        Optional<Boolean> result = stream.reduce(operator);
        return result.isPresent() && result.get();
    }

    /**
     * Creates a new player profile with provided skin
     * @param withSkin Skin to be used for profile
     * @return New player profile
     */
    public @NotNull PlayerProfile createProfile(String withSkin) {
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", withSkin));
        return profile;
    }

    /**
     * Partitions a string into chunks of size ~30 characters
     * @param input String to be partitioned
     * @return List of partitioned chunks
     */
    public List<String> partitionString(@NotNull String input) {
        return partitionString(input, 30);
    }

    /**
     * Partitions a string into chunks of roughly size length
     * @param input String to be partitioned
     * @param length Approximate length of chunks
     * @return List of partitioned chunks
     */
    public List<String> partitionString(@NotNull String input, int length) {
        List<String> result = new ArrayList<>();
        String[] words = input.split("\\s(?=\\S{2,})");
        StringBuilder inter = new StringBuilder();
        int curLen = 0;
        for (String word : words) {
            curLen += word.replaceAll("<[^<>]+>", "").length();
            inter.append(word);
            if (curLen >= length) {
                result.add(inter.toString().stripTrailing());
                inter.setLength(0);
                curLen = 0;
                continue;
            }
            inter.append(' ');
        }
        result.add(inter.toString().stripTrailing());
        return result;
    }

    /**
     * Formats a float to a string
     * @param f Float to be formatted
     * @return Formatted string
     */
    public String formatFloat(float f) {
        BigDecimal decimal = BigDecimal.valueOf(f);
        DecimalFormat df = f % 1.0 == 0.0 ? new DecimalFormat("#,###") : new DecimalFormat("#,##0.0");
        return df.format(decimal.setScale(1, RoundingMode.DOWN).doubleValue());
    }

    /**
     * Renders a progress bar component
     * @param ratio Current progress ration. Must be between 0 and 1
     * @param scale Scale of the progress bar
     * @param dotted Whether the bar should be rendered dotted
     * @return
     */
    public Component renderBarText(@Range(from = 0, to = 1) float ratio, int scale, boolean dotted) {
        long toFill = Math.round(Math.floor(ratio * scale));
        long toLeaveEmpty = scale - toFill;
        String dashesEmpty = dotted ? DOT.repeat((int) toLeaveEmpty) : DASH.repeat((int) toLeaveEmpty);
        String dashesFull = dotted ? DOT.repeat((int) toFill) : DASH.repeat((int) toFill);
        String dashedPart = dotted ? "<green>%s<gray>%s".formatted(dashesFull, dashesEmpty) : "<strikethrough><green>%s<gray>%s</strikethrough>".formatted(dashesFull, dashesEmpty);
        String numberPart = "<yellow>%s%%<gold>/<yellow>100%%".formatted(formatFloat(ratio * 100f));
        return MINI_MESSAGE.deserialize("%s %s".formatted(dashedPart, numberPart)).decoration(TextDecoration.ITALIC, false);
    }

    /**
     * Acquires result of a callable, returning null if an error was encountered
     * @param producer Producer to be called
     * @return Result of the callable, or null if an error was encountered
     * @param <V> Type of return value
     */
    public <V> @Nullable V acquireCatching(Callable<V> producer) {
        try {
            return producer.call();
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Acquires result of a callable, throwing a runtime error if an error was encountered
     * @param producer Producer to be called
     * @return Result of the callable
     * @param <V> Type of return value
     */
    public <V> @NotNull V acquireThrowing(Callable<V> producer) {
        try {
            return producer.call();
        } catch (Exception e) {
            throw new AcquiringException(e);
        }
    }

    /**
     * Attempts to find key from value inside a map
     * @param map Map to be searched
     * @param value Value to be searched for
     * @return Found key, or null if not found
     * @param <K> Type of key
     * @param <V> Type of value
     */
    public <K, V> @Nullable K keyFromValue(@NotNull Map<K, V> map, @NotNull V value) {
        K key = null;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                key = entry.getKey();
                break;
            }
        }

        return key;
    }

    @ApiStatus.Internal
    public <K> @Nullable K keyFromComposable(@NotNull Map<K, Composable> map, @NotNull Composable value) {
        K key = null;
        for (Map.Entry<K, Composable> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                key = entry.getKey();
                break;
            }
        }

        return key;
    }

    /**
     * Checks if a stack is null or air
     * @param stack Stack to be checked
     * @return True if {@link Material#isAir()} or if stack is null
     */
    @Contract(value = "null -> true", pure = true)
    public boolean isNullOrAir(@Nullable ItemStack stack) {
        return stack == null || stack.getType().isAir();
    }

    /**
     * Thrown inside of {@link #acquireThrowing(Callable)} to indicate an error was encountered while acquiring a value
     *
     * @see FlareUtil#acquireThrowing
     */
    @StandardException
    public static class AcquiringException extends RuntimeException {
    }
}
