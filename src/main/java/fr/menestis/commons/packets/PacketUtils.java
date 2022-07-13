package fr.menestis.commons.packets;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public class PacketUtils {

    private static IChatBaseComponent toChatComponent(String text) {
        return IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}");
    }

    private static PlayerConnection getPlayer(Player player) {
        return ((CraftPlayer) player).getHandle().playerConnection;
    }

    /**
     * Send a NMS packet to all online player.
     *
     * @param packet The NMS packet
     */
    public static void broadcastPacket(Packet<?> packet) {
        Bukkit.getOnlinePlayers().forEach((Consumer<Player>) player -> sendPacket(player, packet));
    }

    /**
     * Send a NMS packet to a player.
     *
     * @param player The player
     * @param packet The NMS packet
     */
    public static void sendPacket(Player player, Packet<?> packet) {
        getPlayer(player).sendPacket(packet);
    }

    public static void setField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a text in the action bar of a player.
     *
     * @param text   The text
     * @param player The player
     */
    public static void sendActionBar(String text, Player player) {
        PacketPlayOutChat packet = new PacketPlayOutChat(toChatComponent(text), (byte) 2);
        sendPacket(player, packet);
    }

    private static void sendTitleTimes(Player player, int fadeIn, int stay, int fadeOut) {
        PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut);
        sendPacket(player, packet);
    }

    /**
     * Send title and subtitle to player with time settings.
     *
     * @param player   the player
     * @param title    the title
     * @param subtitle the subtitle
     * @param fadeIn   the fade in ticks
     * @param stay     the stay ticks
     * @param fadeOut  the fade out ticks
     */
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, toChatComponent(title));
        sendTitleTimes(player, fadeIn, stay, fadeOut);
        sendPacket(player, titlePacket);
        if (subtitle != null && !subtitle.isEmpty()) {
            PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, toChatComponent(subtitle));
            sendPacket(player, subtitlePacket);
        }
    }

    /**
     * Render a big text on the player screen.
     *
     * @param title  the text
     * @param player the player
     */
    public static void sendTitle(String title, Player player) {
        sendTitle(player, title, null, 10, 30, 10);
    }

    /**
     * Render a text on the player screen.
     * You also need to send a title to show the sub title.
     *
     * @param subtitle the text
     * @param player   the player
     */
    public static void sendSubTitle(String subtitle, Player player) {
        sendTitle(player, "", subtitle, 10, 30, 10);
    }

    /**
     * Send a player list header and footer.
     *
     * @param header the header text
     * @param footer the footer text
     * @param player the player
     */
    public static void sendPlayerListHeaderFooter(String header, String footer, Player player) {
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        try {
            Field headerField = packet.getClass().getDeclaredField("a");
            headerField.setAccessible(true);
            headerField.set(packet, toChatComponent(header));
            Field footerField = packet.getClass().getDeclaredField("b");
            footerField.setAccessible(true);
            footerField.set(packet, toChatComponent(footer));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        sendPacket(player, packet);
    }
}
