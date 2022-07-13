package fr.menestis.commons.packets.injection.listener;

import fr.menestis.commons.packets.injection.core.PacketHandler;
import fr.menestis.commons.packets.injection.core.PacketInjector;
import net.minecraft.server.v1_8_R3.Packet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method as a Packet Listener, it means this method will be called by the {@link PacketHandler } when appropriate conditions are meets
 *
 * @see PacketHandler
 * @see PacketInjector
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Listen {

    Class<? extends Packet> packet();

}
