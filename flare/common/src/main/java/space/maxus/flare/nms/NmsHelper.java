package space.maxus.flare.nms;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.maxus.flare.nms.generic.ReflectingNmsHelper;
import space.maxus.flare.nms.generic.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;

/**
 * A utility interface that does version-dependent logic
 */
public interface NmsHelper {
    /**
     * Creates a new NMS helper based on the current version
     * @return {@link ReflectingNmsHelper} if version is unknown, or a version-dependent implementation otherwise
     */
    static @NotNull NmsHelper getInstance() {
        NmsVersion version = ReflectionHelper.NMS_VERSION;
        if (version == NmsVersion.UNKNOWN || version == NmsVersion.UNPREFIXED) // currently don't have an implementation for unprefixed NMS
            return new ReflectingNmsHelper();
        if (ReflectionHelper.hasClass("space.maxus.flare.nms.%s.NmsHelperImpl".formatted(version.name()))) {
            try {
                return (NmsHelper) ReflectionHelper.classOrThrow("space.maxus.flare.nms.%s.NmsHelperImpl".formatted(version.name())).getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                return new ReflectingNmsHelper();
            }
        }
        return new ReflectingNmsHelper();
    }

    /**
     * Gets version this NMS helper supports
     * @return NMS version
     */
    NmsVersion getVersion();

    /**
     * Obtains player connection ({@link net.minecraft.server.network.ServerGamePacketListenerImpl })
     * @param player Player, whose connection to obtain
     * @return connection for packet sending
     */
    Object obtainConnection(Player player);

    /**
     * Builds an inventory title packet ({@link net.minecraft.network.protocol.game.ClientboundOpenScreenPacket})
     * for this player.
     * @param player player to be used
     * @param newTitle title to be displayed
     * @return OpenScreenPacket
     */
    Object buildTitlePacket(Player player, Component newTitle);

    /**
     * Sends a packet to this player
     * @param connection connection to be used
     * @param packet packet to be sent
     */
    void sendPacket(Object connection, Object packet);
}
