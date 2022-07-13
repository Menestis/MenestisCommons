package fr.menestis.commons.packets.injection;

import fr.menestis.commons.packets.injection.listener.Listen;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

/**
 * Packet object given as parameter for each {@link Listen} method
 */
public final class PacketModel {

    private final Player player;
    private final Packet<?> packet;
    private final PacketDirection direction;
    private boolean handle;

    public PacketModel(final Player player, final Packet<?> packet, final PacketDirection direction) {
        this.player = player;
        this.packet = packet;
        this.direction = direction;
        // By defaults, packets are handle by the server
        this.handle = true;
    }

    /**
     * Retrieve the {@link Player} who send or received this packet
     *
     * @return The player involved, never null
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Retrieve the {@link Packet}
     *
     * @return The packet involved, never null
     */
    public Packet<?> getPacket() {
        return this.packet;
    }

    /**
     * Retrieve the direction of this transaction (input or output)
     *
     * @return The transaction direction, never null
     */
    public PacketDirection getDirection() {
        return this.direction;
    }

    /**
     * Check whenever a packet should be handle by the server or no
     *
     * @return True if the packet should be handle (default), false to be ignored
     */
    public boolean isHandle() {
        return handle;
    }

    /**
     * Define whenever a packet should be handle by the server or ignored (default is true)
     *
     * @param handle True if the packet should be handle (default), false to be ignored
     */
    public void setHandle(boolean handle) {
        this.handle = handle;
    }

    @Override
    public String toString() {
        return "PacketModel{" +
                "player=" + player +
                ", packet=" + packet +
                ", direction=" + direction +
                ", handle=" + handle +
                '}';
    }

    public enum PacketDirection {
        OUT,
        IN
    }
}
