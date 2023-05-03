package space.maxus.flare.nms;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.nms.generic.ReflectingNmsHelper;

public interface NmsHelper {
    NmsVersion getVersion();
    Object obtainConnection(Player player);
    Object buildTitlePacket(Player player, Component newTitle);
    void sendPacket(Object connection, Object packet);

    static @NotNull NmsHelper getInstance() {
        NmsVersion version = NmsVersion.extract();
        if(version == NmsVersion.UNKNOWN)
            return new ReflectingNmsHelper();
        String expectedNmsClass = "space.maxus.flare.nms.%s.NmsHelperImpl".formatted(version.name());
        try {
            Class<?> clazz = Class.forName(expectedNmsClass);
            return (NmsHelper) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return new ReflectingNmsHelper();
        }
    }
}
