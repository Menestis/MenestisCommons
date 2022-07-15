package fr.menestis.commons.packets.holograms;

import fr.menestis.commons.bukkit.Cuboid;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang.BooleanUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

/**
 * @author Ariloxe
 */
public class PacketHologram {

    private static int i = 0;
    private final String name;
    private final List<String> hologramText;
    private final Map<Integer, EntityArmorStand> armorStandMap = new HashMap<>();
    private final Map<Integer, EntitySlime> slimeMap = new HashMap<>();
    private final Location hologramLocation;
    private final Cuboid cuboid;
    private final Map<Integer, TriConsumer<Player, PacketHologram, Integer>> integerTriConsumerMap = new HashMap<>();
    private final List<UUID> playerSeeingHologram = new ArrayList<>();
    private final List<UUID> playersThatShouldSeeHologram = new ArrayList<>();
    private int count;
    private boolean global = false;

    /**
     * Create the hologram
     *
     * @param strings  the differents text on the Hologram
     * @param location the location where the Hologram appears
     */
    public PacketHologram(Location location, String... strings) {
        this.hologramText = Arrays.asList(strings);
        this.hologramLocation = location.add(0, 0.40D, 0);
        this.name = "hologram_" + i;
        i++;
        this.cuboid = new Cuboid(location.clone().add(50, 50, 50), location.clone().subtract(50, 50, 50));
    }

    public PacketHologram build() {
        for (String text : this.hologramText) {
            this.count++;
            this.hologramLocation.subtract(0.0D, 0.40D, 0.0D);


            EntityArmorStand entity = new EntityArmorStand(((CraftWorld) this.hologramLocation.getWorld()).getHandle(), this.hologramLocation.getX(), this.hologramLocation.getY(), this.hologramLocation.getZ());
            entity.setCustomName(text);
            entity.setCustomNameVisible(true);
            entity.n(true); //SetMarket
            entity.setSmall(true);
            entity.setInvisible(true);
            entity.setGravity(false);

            armorStandMap.put(count, entity);

            if (integerTriConsumerMap.isEmpty() || !integerTriConsumerMap.containsKey(this.count))
                continue;

            EntitySlime slime = new EntitySlime(((CraftWorld) this.hologramLocation.getWorld()).getHandle());
            slime.setLocation(this.hologramLocation.getX(), this.hologramLocation.getY(), this.hologramLocation.getZ(), 0, 0);
            slime.setSize(0);
            slime.setInvisible(true);
            slime.setCustomNameVisible(false);
            slime.setCustomName(text);
            slime.getBukkitEntity().setMetadata("hologramName", new FixedMetadataValue(PacketHologramManager.getInstance().getBukkit(), this.name + "@" + this.count));
            setIA(slime, false);

            PacketHologramManager.getInstance().getIdToSlimeMap().put(slime.getId(), slime);
            slimeMap.put(count, slime);

        }

        for (int i = 0; i < this.count; i++) {
            this.hologramLocation.add(0.0D, 0.40D, 0.0D);
        }

        PacketHologramManager.getInstance().getHologramMap().put(this.name, this);

        return this;
    }

    public boolean isGlobal() {
        return this.global;
    }

    public PacketHologram setGlobal(boolean bool) {
        this.global = bool;

        return this;
    }

    void show(Player player) {
        if (playerSeeingHologram.contains(player.getUniqueId()))
            return;

        for (EntityArmorStand entityArmorStand : this.armorStandMap.values()) {
            PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
            (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(packet);
        }

        for (EntitySlime entitySlime : this.slimeMap.values()) {
            PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(entitySlime);
            (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(packet);
        }

        playerSeeingHologram.add(player.getUniqueId());
    }

//    public void hideTemporarly(Player player) {
//        if (!playerSeeingHologram.contains(player.getName()))
//            return;
//
//        for (EntityArmorStand entityArmorStand : this.armorStandMap.values()) {
//            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityArmorStand.getId());
//            (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(packet);
//        }
//
//        for (EntitySlime entitySlime : this.slimeMap.values()) {
//            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entitySlime.getId());
//            (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(packet);
//        }
//
//        playerSeeingHologram.remove(player.getName());
//    }


    void hide(Player player) {
        if (!playerSeeingHologram.contains(player.getUniqueId()))
            return;

        for (EntityArmorStand entityArmorStand : this.armorStandMap.values()) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityArmorStand.getId());
            (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(packet);
        }

        for (EntitySlime entitySlime : this.slimeMap.values()) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entitySlime.getId());
            (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(packet);
        }

        playerSeeingHologram.remove(player.getUniqueId());
    }

    public void addPlayer(Player player) {
        this.playersThatShouldSeeHologram.add(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        this.playersThatShouldSeeHologram.remove(player.getUniqueId());
        this.playerSeeingHologram.remove(player.getUniqueId());
    }

    public void updateLine(int line, Player player) {
        EntityArmorStand entityArmorStand = armorStandMap.get(line);

        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);
    }

    public void updateLine(int line) {
        EntityArmorStand entityArmorStand = armorStandMap.get(line);
        //  Location location = entityArmorStand.getBukkitEntity().getLocation();

        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);
        for (UUID uuid : playerSeeingHologram) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(metadata);
            }
        }
    }

    public void updateHologram(Player player) {
        hide(player);
        show(player);
    }

    public void updateHologram() {
        for (UUID uuid : new ArrayList<>(playerSeeingHologram)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                hide(player);
                show(player);
            }
        }
    }

    public String getName() {
        return name;
    }


    /**
     * Get the callback of this hologram, if null there isn't callback
     */
    public TriConsumer<Player, PacketHologram, Integer> getCallback(int line) {
        return this.integerTriConsumerMap.get(line);
    }

    /**
     * Add callback to hologram
     *
     * @param playerConsumer consumer
     *                       player = the player who rightclick on it
     *                       hologram = the hologram who is clicked (this)
     *                       integer = the line's number (from the top)
     */
    public void addCallback(int line, TriConsumer<Player, PacketHologram, Integer> playerConsumer) {
        this.integerTriConsumerMap.put(line, playerConsumer);
    }

    /**
     * Get the ArmorStand of a specific line
     *
     * @param numberLine the line that you want to get the armorstand
     */
    public EntityArmorStand getArmorStand(int numberLine) {
        return this.armorStandMap.get(numberLine);
    }

    /**
     * Change a line's value
     *
     * @param numberLine the line that you want to change it
     * @param text       the new text of this line
     */
    public void setLine(int numberLine, String text) {
        this.armorStandMap.get(numberLine).setCustomName(text);
    }

    /**
     * Get a line's value
     *
     * @param numberLine the line that you want to recuip it.
     */
    public String getLine(int numberLine) {
        return this.armorStandMap.get(numberLine).getCustomName();
    }

    /**
     * Get the hologram's visibility cuboid.
     *
     * @return the cuboid in which the players see it.
     */
    public Cuboid getCuboid() {
        return cuboid;
    }

    /**
     * Get the list of players who'll see it.
     *
     * @return the list of users's name.
     */
    public List<UUID> getPlayersSeeingHologram() {
        return playerSeeingHologram;
    }

    /**
     * Get the list of players who are able to see it.
     *
     * @return the list of all users who can see the hologram.
     */
    public List<UUID> getPlayersThatShouldSeeHologram() {
        return playersThatShouldSeeHologram;
    }

    private void setIA(EntitySlime bukkitEntity, Boolean bool) {
        Entity nmsEntity = (bukkitEntity.getBukkitEntity()).getHandle();
        NBTTagCompound tag = nmsEntity.getNBTTag();
        if (tag == null)
            tag = new NBTTagCompound();

        nmsEntity.c(tag);

        tag.setInt("NoAI", BooleanUtils.toInteger(!bool));
        nmsEntity.f(tag);
    }

    public void delete() {
        ArrayList<UUID> uuids = new ArrayList<>(playerSeeingHologram);
        for (UUID uuid : uuids) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
                hide(player);
        }
        PacketHologramManager.getInstance().getHologramMap().remove(this.name);

    }
}
