package space.maxus.flare.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.concurrent.Computable;
import org.apache.commons.lang3.concurrent.Memoizer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.maxus.flare.ui.Composable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

@UtilityClass
public class FlareUtil {
    public final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("flare-%d").build();
    private final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(4, THREAD_FACTORY);
    private final String DASH = "⏤";
    private final String DOT = "•";

    public void execute(Runnable executable) {
        THREAD_POOL.execute(executable);
    }

    public ExecutorService executor(int threads) {
        return Executors.newFixedThreadPool(threads, THREAD_FACTORY);
    }

    public Component text(String miniMessage) {
        return MINI_MESSAGE.deserialize(miniMessage).decoration(TextDecoration.ITALIC, false);
    }

    @SuppressWarnings("unchecked")
    public <G> Class<G> genericClass() {
        return (Class<G>) new TypeToken<G>() {
        }.getRawType();
    }

    public <I, O> Computable<I, O> memoize(Computable<I, O> producer) {
        return new Memoizer<>(producer, true);
    }

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

    public boolean reduceBoolStream(@NotNull Stream<Boolean> stream, BinaryOperator<Boolean> operator) {
        Optional<Boolean> result = stream.reduce(operator);
        return result.isPresent() && result.get();
    }

    public @NotNull PlayerProfile createProfile(String withSkin) {
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", withSkin));
        return profile;
    }

    public List<String> partitionString(String input) {
        return partitionString(input, 30);
    }

    public List<String> partitionString(String input, int length) {
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

    public String formatFloat(float f) {
        BigDecimal decimal = BigDecimal.valueOf(f);
        DecimalFormat df = f % 1.0 == 0.0 ? new DecimalFormat("#,###") : new DecimalFormat("#,##0.0");
        return df.format(decimal.setScale(1, RoundingMode.DOWN).doubleValue());
    }

    public Component renderBarText(float ratio, int scale, boolean dotted) {
        long toFill = Math.round(Math.floor(ratio * scale));
        long toLeaveEmpty = scale - toFill;
        String dashesEmpty = dotted ? DOT.repeat((int) toLeaveEmpty) : DASH.repeat((int) toLeaveEmpty);
        String dashesFull = dotted ? DOT.repeat((int) toFill) : DASH.repeat((int) toFill);
        String dashedPart = dotted ? "<green>%s<gray>%s".formatted(dashesFull, dashesEmpty) : "<strikethrough><green>%s<gray>%s</strikethrough>".formatted(dashesFull, dashesEmpty);
        String numberPart = "<yellow>%s%%<gold>/<yellow>100%%".formatted(formatFloat(ratio * 100f));
        return MINI_MESSAGE.deserialize("%s %s".formatted(dashedPart, numberPart)).decoration(TextDecoration.ITALIC, false);
    }

    public <V> @Nullable V acquireCatching(Callable<V> producer) {
        try {
            return producer.call();
        } catch (Exception e) {
            return null;
        }
    }

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
}
