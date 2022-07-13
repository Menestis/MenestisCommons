package fr.menestis.commons.packets.holograms;

import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.MobEffect;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ariloxe
 */
public class PacketHologramManager {

    private static final PacketHologramManager PACKET_HOLOGRAM_MANAGER = new PacketHologramManager();
    public static PacketHologramManager getInstance(){ return PACKET_HOLOGRAM_MANAGER; }

    private final MobEffect potionEffect = new MobEffect(PotionEffectType.INVISIBILITY.getId(), Integer.MAX_VALUE, 9, false, false);
    private final Map<String, PacketHologram> hologramList = new HashMap<>();
    private final Map<Integer, EntitySlime> idToSlimeMap = new HashMap<>();
    private JavaPlugin javaPlugin;

    public Map<String, PacketHologram> getHologramMap() {
        return hologramList;
    }

    public Map<Integer, EntitySlime> getIdToSlimeMap() {
        return idToSlimeMap;
    }

    public MobEffect getPotionEffect() {
        return potionEffect;
    }

    /**
     * Get a specific hologram
     * @param hologramName the name of the hologram.
     */
    public PacketHologram getHologramFromName(String hologramName){ return hologramList.get(hologramName); }

    /**
     * Init the Hologram Module
     * @param javaPlugin the main class
     */
    public void init(JavaPlugin javaPlugin){
        this.javaPlugin = javaPlugin;
    }

    public JavaPlugin getBukkit() {
        return javaPlugin;
    }
}
