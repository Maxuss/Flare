package space.maxus.flare.nms;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public enum NmsVersion {
    v1_19_R3, // current latest supported
    UNKNOWN
    ;

    public static @NotNull String currentStrVersion() {
        return Bukkit
                .getServer()
                .getClass().getPackageName()
                .replace("org.bukkit.craftbukkit.", "")
                .replace(".CraftServer", "");
    }

    public static NmsVersion extract() {
        String versionStr = currentStrVersion();
        try {
            return NmsVersion.valueOf(versionStr);
        } catch (IllegalArgumentException e) {
            return NmsVersion.UNKNOWN;
        }
    }
}
