package fr.menestis.commons.packets.injection.core;

import fr.menestis.commons.packets.holograms.PacketHologramManager;
import fr.menestis.commons.packets.injection.SimpleReflection;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;
import java.util.Objects;

public class PacketInjector implements Listener {

    private Field entityPlayer_playerConnection;
    private Field playerConnection_networkManager;

    private Field k;
    private Field m;

    public PacketInjector() {
        try {
            entityPlayer_playerConnection = SimpleReflection.getField(Objects.requireNonNull(SimpleReflection.getClass("{nms}.EntityPlayer")), "playerConnection");
            final Class<?> playerConnection = SimpleReflection.getClass("{nms}.PlayerConnection");
            assert playerConnection != null;
            playerConnection_networkManager = SimpleReflection.getField(playerConnection, "networkManager");

            final Class<?> networkManager = SimpleReflection.getClass("{nms}.NetworkManager");
            assert networkManager != null;
            k = SimpleReflection.getField(networkManager, "channel");
            m = SimpleReflection.getField(networkManager, "m");
            Bukkit.getPluginManager().registerEvents(this, PacketHologramManager.getInstance().getBukkit());
        } catch (final Throwable e) {
            PacketHologramManager.getInstance().getBukkit().getLogger().severe("Could not initialize PacketInjector");
            e.printStackTrace();
        }

    }

    public void addPlayer(final Player p) {
        try {
            final Channel ch = getChannel(getNetworkManager(SimpleReflection.getNmsPlayer(p)));
            if (ch.pipeline().get("PacketInjector") == null) {
                final PacketHandler h = new PacketHandler(p);
                ch.pipeline().addBefore("packet_handler", "PacketInjector", h);
            }
        } catch (final Throwable e) {
            PacketHologramManager.getInstance().getBukkit().getLogger().severe("Can't hook into player packetHandler");
            e.printStackTrace();
        }
    }

    public void removePlayer(final Player p) {
        try {
            final Channel ch = getChannel(getNetworkManager(SimpleReflection.getNmsPlayer(p)));
            if (ch.pipeline().get("PacketInjector") != null) {
                ch.pipeline().remove("PacketInjector");
            }
        } catch (final Throwable e) {
            PacketHologramManager.getInstance().getBukkit().getLogger().severe("Can't remove player from packet watchList");
            e.printStackTrace();
        }
    }

    private Object getNetworkManager(final Object ep) {
        return SimpleReflection.getFieldValue(playerConnection_networkManager, (Object) SimpleReflection.getFieldValue(entityPlayer_playerConnection, ep));
    }

    private Channel getChannel(final Object networkManager) {
        Channel ch;
        try {
            ch = SimpleReflection.getFieldValue(k, networkManager);
        } catch (final Exception e) {
            ch = SimpleReflection.getFieldValue(m, networkManager);
        }
        return ch;
    }

    public Channel getChannel(final Player player) {
        try {
            return getChannel(getNetworkManager(SimpleReflection.getNmsPlayer(player)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(final PlayerJoinEvent event) {
        addPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeave(final PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }
}
