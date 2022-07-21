package fr.menestis.commons.bukkit.moderation;

import fr.menestis.commons.bukkit.CommandUtils;
import fr.menestis.commons.bukkit.moderation.commands.ModCommand;
import fr.menestis.commons.bukkit.moderation.listener.ModListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ModerationManager {

    private static final ModerationManager instance = new ModerationManager();

    public static ModerationManager getInstance() {
        return instance;
    }

    private final List<UUID> vanishPlayers = new ArrayList<>();
    private final Map<UUID, Location> uuidLocationMap = new HashMap<>();
    private final Map<UUID, Inventory> uuidInventoryMap = new HashMap<>();

    public List<UUID> getVanishPlayers() {
        return vanishPlayers;
    }

    public Map<UUID, Inventory> getUuidInventoryMap() {
        return uuidInventoryMap;
    }

    public Map<UUID, Location> getUuidLocationMap() {
        return uuidLocationMap;
    }

    private JavaPlugin javaPlugin;

    public void init(JavaPlugin javaPlugin){
        this.javaPlugin = javaPlugin;

        CommandUtils.registerCommand("mod", new ModCommand());
        Bukkit.getPluginManager().registerEvents(new ModListener(), javaPlugin);


      //  MagnetApi.MagnetStore.getApi().getPlayerHandle()

    }


    public JavaPlugin getBukkit() {
        return javaPlugin;
    }
}
