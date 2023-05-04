package space.maxus.flare.nms.v1_19_R1;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import space.maxus.flare.nms.NmsHelper;
import space.maxus.flare.nms.NmsVersion;

public class NmsHelperImpl implements NmsHelper {
    @Override
    public NmsVersion getVersion() {
        return NmsVersion.v1_19_R1;
    }

    @Override
    public Object obtainConnection(Player player) {
        return ((CraftPlayer) player).getHandle().connection;
    }

    @Override
    public Object buildTitlePacket(Player player, Component newTitle) {
        AbstractContainerMenu menu = ((CraftPlayer) player).getHandle().containerMenu;
        return new ClientboundOpenScreenPacket(menu.containerId, menu.getType(), PaperAdventure.asVanilla(newTitle));
    }

    @Override
    public void sendPacket(Object connection, Object packet) {
        ((ServerGamePacketListenerImpl) connection).send((Packet<?>) packet);
    }
}
