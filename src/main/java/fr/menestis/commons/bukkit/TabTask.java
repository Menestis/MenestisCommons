package fr.menestis.commons.bukkit;

import fr.blendman.magnet.api.MagnetApi;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ariloxe
 */
public class TabTask extends BukkitRunnable {

    private final JavaPlugin javaPlugin;

    public TabTask(JavaPlugin javaPlugin) {
        runTaskTimerAsynchronously(javaPlugin, 50, 20);

        this.javaPlugin = javaPlugin;

        run();
    }

    private void updateTab(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(javaPlugin, () -> {
            List<String> header = new ArrayList<>();
            String footer;

            int ping = ((CraftPlayer) player).getHandle().ping;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            double tpsNumber = MinecraftServer.getServer().recentTps[0];
            String tps = String.valueOf(decimalFormat.format(tpsNumber));
            if (tpsNumber > 20)
                tps = "20*";

            ChatColor tpsColor = tpsNumber > 18 ? ChatColor.GREEN : tpsNumber > 15 ? ChatColor.YELLOW : tpsNumber > 10 ? ChatColor.RED : ChatColor.DARK_GRAY;
            ChatColor pingColor = ping < 60 ? ChatColor.GREEN : ping < 120 ? ChatColor.YELLOW : ping < 210 ? ChatColor.RED : ChatColor.DARK_GRAY;


            header.add(" ");
            header.add("§8§l» §3§lmenestis.fr §8§l«");
            header.add(" ");
            header.add("§7Ping: " + pingColor + ping + "ms §8┃ §7TPS: " + tpsColor + tps);

            footer = "\n§7Vous jouez sur le serveur §3" + MagnetApi.MagnetStore.getApi().getServerLabel().split("-")[0] + "\n §8➥ §bmenestis.fr/discord";

            StringBuilder h = new StringBuilder();
            for (String s : header)
                h.append(s).append("\n");


            send(player, h.toString(), footer);
        });

    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateTab(player);
        }
    }

    private void send(Player player, String header, String footer) {
        CraftPlayer craftplayer = (CraftPlayer) player;
        PlayerConnection connection = craftplayer.getHandle().playerConnection;
        IChatBaseComponent headerJSON = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + header + "\"}");
        IChatBaseComponent footerJSON = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}");
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

        try {
            Field headerField = packet.getClass().getDeclaredField("a");
            headerField.setAccessible(true);
            headerField.set(packet, headerJSON);
            headerField.setAccessible(!headerField.isAccessible());

            Field footerField = packet.getClass().getDeclaredField("b");
            footerField.setAccessible(true);
            footerField.set(packet, footerJSON);
            footerField.setAccessible(!footerField.isAccessible());
        } catch (Exception e) {
            e.printStackTrace();
        }

        connection.sendPacket(packet);
    }
}