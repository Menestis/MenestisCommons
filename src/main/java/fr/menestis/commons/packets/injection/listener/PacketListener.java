package fr.menestis.commons.packets.injection.listener;


import fr.menestis.commons.packets.injection.PacketModel;

/**
 * Mark a class as a Packet Listener, work same as (same as {@link org.bukkit.event.Listener} for standard Bukkit event's
 * Each method who listens packet should be annotated with {@link Listen} and have EXACTLY ONE PARAMETERS of type {@link PacketModel}
 *
 * @see Listen
 * @see PacketModel
 */
public interface PacketListener {

}
