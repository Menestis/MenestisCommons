package fr.menestis.commons.packets.injection.core;

import fr.menestis.commons.packets.holograms.PacketHologramManager;
import fr.menestis.commons.packets.injection.PacketModel;
import fr.menestis.commons.packets.injection.manager.PacketManager;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class PacketHandler extends ChannelDuplexHandler {

    private final Player p;

    PacketHandler(final Player p) {
        this.p = p;
    }

    /**
     * Call Event and then send packet if not cancelled
     */
    @Override
    public void write(final ChannelHandlerContext ctx, final Object m, final ChannelPromise promise) throws Exception {
        final Packet packet = (Packet) m;
        final PacketModel packetModel = new PacketModel(p, packet, PacketModel.PacketDirection.OUT);

        final boolean shouldProcess = this.callPacketEvent(packetModel);

        if (shouldProcess)
            super.write(ctx, packet, promise);
    }

    /**
     * Call Event and then process packet if not cancelled
     */
    @Override
    public void channelRead(final ChannelHandlerContext c, final Object m) throws Exception {
        if (!(m instanceof Packet))
            return;

        final Packet packet = (Packet) m;
        final PacketModel packetModel = new PacketModel(p, packet, PacketModel.PacketDirection.IN);

        final boolean shouldProcess = this.callPacketEvent(packetModel);
        if (shouldProcess)
            super.channelRead(c, packet);
    }

    /**
     * Call event
     *
     * @param packetModel {@link PacketModel}
     * @return false if cancelled
     */
    private boolean callPacketEvent(final PacketModel packetModel) {
        try {
            return PacketManager.getInstance().call(packetModel);
        } catch (InvocationTargetException | IllegalAccessException e) {
            PacketHologramManager.getInstance().getBukkit().getLogger().severe("Can't call PacketListener for " + packetModel);
            e.printStackTrace();
        }

        return false;
    }

}
