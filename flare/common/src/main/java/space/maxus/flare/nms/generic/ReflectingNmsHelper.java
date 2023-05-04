package space.maxus.flare.nms.generic;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.slf4j.Logger;
import space.maxus.flare.nms.NmsVersion;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("JavaReflectionInvocation")
public final class ReflectingNmsHelper implements space.maxus.flare.nms.NmsHelper {
    private final Logger log = org.slf4j.LoggerFactory.getLogger(ReflectingNmsHelper.class);

    @Override
    public NmsVersion getVersion() {
        return NmsVersion.UNKNOWN;
    }

    @Override
    public Object obtainConnection(Player player) {
        try {
            Method getHandle = player.getClass().getMethod("getHandle");
            Object serverPlayer = getHandle.invoke(player);
            Field connection = serverPlayer.getClass().getDeclaredField("b"); // https://nms.screamingsandals.org/1.19.4/net/minecraft/server/level/ServerPlayer.html
            return connection.get(serverPlayer);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | NoSuchFieldException e) {
            log.error("Error in generic nms helper (obtaining player connection)", e);
            return null;
        }
    }

    @Override
    public Object buildTitlePacket(Player player, Component newTitle) {
        try {
            Method getHandle = player.getClass().getMethod("getHandle");
            Object serverPlayer = getHandle.invoke(player);
            Field containerMenuField = serverPlayer.getClass().getField("bP"); // https://nms.screamingsandals.org/1.19.4/net/minecraft/world/entity/player/Player.html
            Object containerMenu = containerMenuField.get(serverPlayer);
            Field containerId = containerMenu.getClass().getField("j"); // https://nms.screamingsandals.org/1.19.4/net/minecraft/world/inventory/AbstractContainerMenu.html
            Method containerType = containerMenu.getClass().getDeclaredMethod("getNotchInventoryType", Inventory.class);
            Class<?> packetClass = ReflectionHelper.classOrThrow(ReflectionHelper.findNmClassName("network.protocol.game.PacketPlayOutOpenWindow")); // https://nms.screamingsandals.org/1.19.4/net/minecraft/network/protocol/game/ClientboundOpenScreenPacket.html
            Constructor<?> ctor = packetClass.getDeclaredConstructor(
                    int.class,
                    ReflectionHelper.classOrThrow(ReflectionHelper.findNmClassName("world.inventory.Containers")),
                    ReflectionHelper.anyClassOrThrow(
                            ReflectionHelper.findNmsClassName("IChatBaseComponent"),
                            ReflectionHelper.findNmClassName("network.chat.IChatBaseComponent")
                    ));
            return ctor.newInstance(containerId.get(containerMenu), containerType.invoke(null, player.getOpenInventory().getTopInventory()), PaperAdventure.asVanilla(newTitle));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | NoSuchFieldException |
                 InstantiationException e) {
            log.error("Error in generic nms helper (building screen title packet)", e);
            return null;
        }
    }

    @Override
    public void sendPacket(Object connection, Object packet) {
        try {
            Method send = connection.getClass().getDeclaredMethod(
                    "a",
                    ReflectionHelper.classOrThrow(ReflectionHelper.findNmClassName("network.protocol.Packet"))
            );
            send.invoke(connection, packet);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Error in generic nms helper (sending packet)", e);
        }
    }
}
