package space.maxus.flare.nms.generic;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.maxus.flare.nms.NmsVersion;

import java.util.Objects;

// Uses portions of code from adventure-platform, licensed under MIT License
// https://github.com/KyoriPowered/adventure-platform
public class ReflectionHelper {
    private ReflectionHelper() { }

    private static final Logger log = LoggerFactory.getLogger("Flare-Reflect");

    private static final String NM_PREFIX = "net.minecraft";
    private static final String NMS_PREFIX = "net.minecraft.server";
    private static final String BUKKIT_CRAFT_SERVER_NAME = "CraftServer";
    private static final String CB_PREFIX = "org.bukkit.craftbukkit";
    private static final String FLARE_PREFIX = "space.maxus.flare.nms";
    private static final @Nullable String CURRENT_VERSION;
    public static final boolean IS_LEGACY_BUKKIT;
    public static final boolean IS_LEGACY_NMS;

    public static final NmsVersion NMS_VERSION;

    static {
        Class<?> bukkitClass = Bukkit.getServer().getClass();
        if(!bukkitClass.getSimpleName().equals(BUKKIT_CRAFT_SERVER_NAME)) {
            log.error("Flare is currently not compatible with a server implementation {}", bukkitClass.getSimpleName());
            CURRENT_VERSION = ".";
            NMS_VERSION = NmsVersion.UNPREFIXED;
            IS_LEGACY_BUKKIT = false;
            IS_LEGACY_NMS = false;
        } else if(bukkitClass.getCanonicalName().equals("%s.%s".formatted(CB_PREFIX, BUKKIT_CRAFT_SERVER_NAME))) {
            CURRENT_VERSION = ".";
            NMS_VERSION = NmsVersion.UNPREFIXED;
            IS_LEGACY_BUKKIT = false;
            IS_LEGACY_NMS = false;
            log.info("Hooked to an unprefixed NMS implementation!");
        } else {
            String name = bukkitClass
                    .getCanonicalName()
                    .substring(CB_PREFIX.length());
            CURRENT_VERSION = name.substring(0, name.length() - BUKKIT_CRAFT_SERVER_NAME.length());
            NMS_VERSION = NmsVersion.valueOf(CURRENT_VERSION.replace(".", ""));
            IS_LEGACY_BUKKIT = true;
            // this searches for class net.minecraft.server.<>.Main, so if a version prefix is not present
            // it will return false
            IS_LEGACY_NMS = hasClass("net.minecraft.server%sMain".formatted(CURRENT_VERSION));
            log.info("Hooked to NMS version {}!", NMS_VERSION);
        }
    }

    public static @Nullable Class<?> findClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static @Nullable Class<?> findFirstClass(String @NotNull ...classNames) {
        for(String className : classNames) {
            Class<?> cls = findClass(className);
            if(cls != null) return cls;
        }
        return null;
    }

    public static boolean hasClass(String className) {
        return findClass(className) != null;
    }

    public static @Nullable Class<?> findNmsClass(String className) {
        return findClass(NMS_PREFIX + className);
    }

    public static @NotNull Class<?> classOrThrow(String className) {
        return Objects.requireNonNull(findClass(className), "Could not find required class %s".formatted(className));
    }

    public static @NotNull Class<?> anyClassOrThrow(String @NotNull ...classNames) {
        for(String className : classNames) {
            Class<?> cls = findClass(className);
            if(cls != null) return cls;
        }
        throw new IllegalArgumentException("Could not find any of the required classes");
    }

    public static String findNmsClassName(String className) {
        return IS_LEGACY_NMS ? "%s%s%s".formatted(NMS_PREFIX, CURRENT_VERSION, className) : "%s.%s".formatted(NMS_PREFIX, className);
    }

    public static String findNmClassName(String className) {
        return "%s.%s".formatted(NM_PREFIX, className);
    }

    public static String findCbClassName(String className) {
        return "%s%s%s".formatted(CB_PREFIX, CURRENT_VERSION, className);
    }
}
