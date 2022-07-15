package fr.menestis.commons.packets.holograms;

import fr.menestis.commons.packets.injection.PacketModel;
import fr.menestis.commons.packets.injection.Reflection;
import fr.menestis.commons.packets.injection.listener.Listen;
import fr.menestis.commons.packets.injection.listener.PacketListener;
import fr.menestis.commons.packets.injection.manager.PacketManager;
import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Ariloxe
 */
public class PacketHologramListeners implements Listener, PacketListener {

    private final Class<?> packetPlayInUseEntityClazz = Reflection.getMinecraftClass("PacketPlayInUseEntity");
    private final Reflection.FieldAccessor<Integer> entityIdField = Reflection.getField(this.packetPlayInUseEntityClazz, "a", int.class);

    //TODO retirer quand on aura compris ce truc de double packet de mort
    private final Map<UUID, Long> uuidLongMap = new HashMap<>();

    public PacketHologramListeners() {
        PacketManager.getInstance().register(this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        PacketHologramManager.getInstance().getHologramMap().values().stream().filter(PacketHologram::isGlobal).forEach(packetHologram -> packetHologram.addPlayer(playerJoinEvent.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent playerQuitEvent) {
        for (PacketHologram value : PacketHologramManager.getInstance().getHologramMap().values()) {
            value.removePlayer(playerQuitEvent.getPlayer());
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent.getEntity() instanceof Slime && entityDamageEvent.getEntity().hasMetadata("hologramName"))
            entityDamageEvent.setCancelled(true);

    }

    @Listen(packet = PacketPlayInUseEntity.class)
    public void onPacket(final PacketModel packetModel) {
        PacketPlayInUseEntity packetPlayInUseEntity = ((PacketPlayInUseEntity) packetModel.getPacket());
        Player player = packetModel.getPlayer();
        if (packetPlayInUseEntity.a(((CraftWorld) player.getWorld()).getHandle()) != null)
            return;

        UUID uuid = player.getUniqueId();

        if (!uuidLongMap.containsKey(uuid)) {
            uuidLongMap.putIfAbsent(uuid, System.currentTimeMillis());
        } else {
            if (System.currentTimeMillis() - uuidLongMap.get(uuid) < 50) {
                return;
            } else {
                uuidLongMap.put(uuid, System.currentTimeMillis());
            }
        }

        int packetEntityId = this.entityIdField.get(packetPlayInUseEntity);
        if (!PacketHologramManager.getInstance().getIdToSlimeMap().containsKey(packetEntityId))
            return;


        EntitySlime entity = PacketHologramManager.getInstance().getIdToSlimeMap().get(packetEntityId);

        if (entity.getBukkitEntity().hasMetadata("hologramName")) {
            String holoName = (entity.getBukkitEntity().getMetadata("hologramName").get(0)).asString();
            int line = Integer.parseInt(holoName.split("@")[1]);
            PacketHologram holo = PacketHologramManager.getInstance().getHologramFromName(holoName.split("@")[0]);
            if (holo.getCallback(line) != null)
                holo.getCallback(line).accept(player, holo, line);
        }
    }

}
