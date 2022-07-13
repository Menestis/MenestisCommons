package fr.menestis.commons.packets.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Blendman974
 */
public class ScoreboardManager {
    private static final ScoreboardManager INSTANCE = new ScoreboardManager();
    private final Map<UUID, VirtualScoreboard> scoreboards = new HashMap<>();

    private ScoreboardManager() {
    }

    public static ScoreboardManager getInstance() {
        return INSTANCE;
    }

    public VirtualScoreboard registerScoreboard(Player player) {
        VirtualScoreboard virtualScoreboard = new VirtualScoreboard(player, ChatColor.LIGHT_PURPLE + "Aspaku");
        this.scoreboards.put(player.getUniqueId(), virtualScoreboard);
        return virtualScoreboard;
    }

    public void unregisterScoreboard(UUID uuid) {
        VirtualScoreboard remove = this.scoreboards.remove(uuid);
        if (remove != null)
            remove.destroy();
    }

    public VirtualScoreboard getScoreboard(UUID uuid) {
        return scoreboards.get(uuid);
    }

    public Map<UUID, VirtualScoreboard> getAll() {
        return Collections.unmodifiableMap(scoreboards);
    }

    public void reset() {
        scoreboards.forEach((uuid, virtualScoreboard) -> virtualScoreboard.destroy());
        scoreboards.clear();
        Bukkit.getOnlinePlayers().forEach(this::registerScoreboard);
    }
}
