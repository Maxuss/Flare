package space.maxus.flare.nms;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.nms.generic.ReflectingNmsHelper;
import space.maxus.flare.nms.generic.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;

public interface NmsHelper {
    NmsVersion getVersion();
    Object obtainConnection(Player player);
    Object buildTitlePacket(Player player, Component newTitle);
    void sendPacket(Object connection, Object packet);

    static @NotNull NmsHelper getInstance() {
        NmsVersion version = ReflectionHelper.NMS_VERSION;
        if(version == NmsVersion.UNKNOWN || version == NmsVersion.UNPREFIXED) // currently don't have an implementation for unprefixed NMS
            return new ReflectingNmsHelper();
        if(ReflectionHelper.hasClass("space.maxus.flare.nms.%s.NmsHelperImpl".formatted(version.name()))) {
            try {
                return (NmsHelper) ReflectionHelper.classOrThrow("space.maxus.flare.nms.%s.NmsHelperImpl".formatted(version.name())).getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                return new ReflectingNmsHelper();
            }
        }
        return new ReflectingNmsHelper();
    }
}
