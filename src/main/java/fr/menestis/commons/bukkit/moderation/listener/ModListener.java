package fr.menestis.commons.bukkit.moderation.listener;

import fr.menestis.commons.bukkit.moderation.ModerationManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class ModListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        if (!ModerationManager.getInstance().getVanishPlayers().isEmpty()) {
            ModerationManager.getInstance().getVanishPlayers().forEach(uuid -> playerJoinEvent.getPlayer().hidePlayer(Bukkit.getPlayer(uuid)));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent playerQuitEvent) {
        if (ModerationManager.getInstance().getVanishPlayers().contains(playerQuitEvent.getPlayer().getUniqueId())) {
            UUID uuid = playerQuitEvent.getPlayer().getUniqueId();

            ModerationManager.getInstance().getUuidInventoryMap().remove(uuid);
            ModerationManager.getInstance().getUuidLocationMap().remove(uuid);
            ModerationManager.getInstance().getVanishPlayers().remove(uuid);
        }
    }

}
