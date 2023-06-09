package fr.menestis.commons.bukkit.moderation;

import fr.menestis.commons.bukkit.CommandUtils;
import fr.menestis.commons.bukkit.moderation.commands.ModCommand;
import fr.menestis.commons.bukkit.moderation.listener.ModListener;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ModerationManager {

    private static final ModerationManager instance = new ModerationManager();
    private final List<UUID> vanishPlayers = new ArrayList<>();
    private final Map<UUID, Location> uuidLocationMap = new HashMap<>();
    private final Map<UUID, Inventory> uuidInventoryMap = new HashMap<>();
    private JavaPlugin javaPlugin;

    public static ModerationManager getInstance() {
        return instance;
    }

    public List<UUID> getVanishPlayers() {
        return vanishPlayers;
    }

    public Map<UUID, Inventory> getUuidInventoryMap() {
        return uuidInventoryMap;
    }

    public Map<UUID, Location> getUuidLocationMap() {
        return uuidLocationMap;
    }

    public void init(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;

        CommandUtils.registerCommand("mod", new ModCommand());
        Bukkit.getPluginManager().registerEvents(new ModListener(), javaPlugin);

        //  MagnetApi.MagnetStore.getApi().getPlayerHandle()
    }

    public JavaPlugin getBukkit() {
        return javaPlugin;
    }
}
