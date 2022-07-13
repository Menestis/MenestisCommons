package fr.menestis.commons.packets.scoreboard;

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;

import java.util.*;

import static fr.menestis.commons.packets.PacketUtils.setField;

public class VirtualTeam {

    private String name;
    private String displayName = "";
    private String prefix = "";
    private String suffix = "";
    private int chatColor = -1;
    private boolean allowFriendlyFire = true;
    private boolean canSeeFriendlyInvisibles = false;
    private boolean collideWithEntities = false;

    private String nameTagVisibility = "always";

    private List<String> players = new ArrayList<>();

    public VirtualTeam(String name) {
        this.name = name;
    }

    private PacketPlayOutScoreboardTeam createPacket(PacketMode packetMode, List<String> players) {
        if(players == null)
            players = new ArrayList<>();

        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
        setField(packet, "a", name);
        setField(packet, "h", packetMode.ordinal()); // i en 1.12.2 - h en 1.8.8
        switch (packetMode) {
            case CREATE:
            case UPDATE:
                setField(packet, "b", displayName);
                setField(packet, "c", prefix);
                setField(packet, "d", suffix);
//                if (!collideWithEntities) {
//                    setField(packet, "f", "never"); // 1.12.2 only
//                }
                setField(packet, "f", chatColor); // g en 1.12.2 - f en 1.8.8
                setField(packet, "e", nameTagVisibility); // nametag visibility
                setField(packet, "g", players); // h en 1.12.2 - g en 1.8.8
                setField(packet, "i", packOptionData()); // j en 1.12.2 - i en 1.8.8
                break;
            case ADD_PLAYER:
            case REMOVE_PLAYER:
                setField(packet, "g", players); // h en 1.12.2 - g en 1.8.8
                break;
        }
        return packet;
    }

    public PacketPlayOutScoreboardTeam create(String... newPlayers) {
        return create(Arrays.asList(newPlayers));
    }

    public PacketPlayOutScoreboardTeam create(List<String> newPlayers) {
        if (newPlayers != null) {
            players.addAll(newPlayers);
        }
        return createPacket(PacketMode.CREATE, players);
    }

    public PacketPlayOutScoreboardTeam update() {
        return createPacket(PacketMode.UPDATE, null);
    }

    public PacketPlayOutScoreboardTeam addPlayer(String player) {
        players.add(player);
        return createPacket(PacketMode.ADD_PLAYER, Collections.singletonList(player));
    }

    public PacketPlayOutScoreboardTeam removePlayer(String player) {
        players.remove(player);
        return createPacket(PacketMode.REMOVE_PLAYER, Collections.singletonList(player));
    }

    public PacketPlayOutScoreboardTeam destroy() {
        return createPacket(PacketMode.DESTROY, null);
    }

    private int packOptionData() {
        int var1 = 0;
        if (allowFriendlyFire) {
            var1 |= 1;
        }
        if (canSeeFriendlyInvisibles) {
            var1 |= 2;
        }
        return var1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VirtualTeam that = (VirtualTeam) o;
        return allowFriendlyFire == that.allowFriendlyFire &&
                canSeeFriendlyInvisibles == that.canSeeFriendlyInvisibles &&
                collideWithEntities == that.collideWithEntities &&
                Objects.equals(name, that.name) &&
                Objects.equals(displayName, that.displayName) &&
                Objects.equals(prefix, that.prefix) &&
                Objects.equals(suffix, that.suffix) &&
                Objects.equals(players, that.players);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, displayName, prefix, suffix, players, allowFriendlyFire, canSeeFriendlyInvisibles, collideWithEntities);
    }

    public List<String> getPlayers() {
        return players;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public int getChatColor() {
        return chatColor;
    }

    public void setChatColor(int chatColor) {
        this.chatColor = chatColor;
    }

    public boolean isAllowFriendlyFire() {
        return allowFriendlyFire;
    }

    public void setAllowFriendlyFire(boolean allowFriendlyFire) {
        this.allowFriendlyFire = allowFriendlyFire;
    }

    public boolean isCanSeeFriendlyInvisibles() {
        return canSeeFriendlyInvisibles;
    }

    public void setCanSeeFriendlyInvisibles(boolean canSeeFriendlyInvisibles) {
        this.canSeeFriendlyInvisibles = canSeeFriendlyInvisibles;
    }

    public String getNameTagVisibility() {
        return nameTagVisibility;
    }

    //always, hideForOtherTeams, hideForOwnTeam, never
    public void setNameTagVisibility(String nameTagVisibility) {
        this.nameTagVisibility = nameTagVisibility;
    }

    public boolean isCollideWithEntities() {
        return collideWithEntities;
    }

    public void setCollideWithEntities(boolean collideWithEntities) {
        this.collideWithEntities = collideWithEntities;
    }

    private enum PacketMode {

        CREATE,
        DESTROY,
        UPDATE,
        ADD_PLAYER,
        REMOVE_PLAYER
    }


}
