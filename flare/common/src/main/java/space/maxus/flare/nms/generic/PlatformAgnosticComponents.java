package space.maxus.flare.nms.generic;

import net.kyori.adventure.platform.bukkit.MinecraftComponentSerializer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class PlatformAgnosticComponents {
    private static final boolean HAS_PAPER_ADVENTURE;

    static {
        HAS_PAPER_ADVENTURE = ReflectionHelper.hasClass("io.papermc.paper.adventure.PaperAdventure");
    }

    private PlatformAgnosticComponents() {
    }

    @SuppressWarnings("UnstableApiUsage")
    public static @Nullable Object nmsComponent(Component adventure) {
        if (HAS_PAPER_ADVENTURE)
            return io.papermc.paper.adventure.PaperAdventure.asVanilla(adventure);
        else if (MinecraftComponentSerializer.isSupported())
            return MinecraftComponentSerializer.get().serialize(adventure);
        return null;
    }
}
