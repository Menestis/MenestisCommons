package fr.menestis.commons.packets.scoreboard;

import net.minecraft.server.v1_8_R3.IScoreboardCriteria;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static fr.menestis.commons.packets.PacketUtils.sendPacket;
import static fr.menestis.commons.packets.PacketUtils.setField;

public class VirtualScoreboard {
    private final static int[] STATE_LENGTH = new int[]{16, 14, 16};
    private VirtualTeam[] teamsLines = new VirtualTeam[15];
    private String[] lines = new String[15];
    private Player player;
    private String title;
    private boolean destroyed;

    /**
     * Create a player scoreboard.
     * Use {@link VirtualScoreboard#setLine(int, String)} to add lines.
     *
     * @param player The player
     * @param title  The scoreboard title
     */
    public VirtualScoreboard(Player player, String title) {
        this.player = player;
        this.title = title;
        sendInitPackets();
    }

    private static boolean isColor(char c) {
        return (c >= '1' && c <= '9') || (c >= 'a' && c <= 'f');
    }

    //   private String[] getParts(String value) {
    //      if (value.length() <= 16) {
    //         return new String[]{value, "", ""};
    //     }

    //    String[] result = new String[]{"", "", ""};

    //    int sub;
    //    if (value.charAt(15) == '§') {
    //        sub = 15;
    //        result[0] = value.substring(0, 15);
    //   } else {
    //        sub = 16;
    //        result[0] = value.substring(0, 16);
    //    }

    //     if (value.substring(sub).length() > 16) {

    //       if (value.charAt(value.length() - 17) == '§') {
    //          result[2] = value.substring(value.length() - 15);
    //         result[1] = value.substring(sub, value.length() - 15);
    //      } else {
    //         result[2] = value.substring(value.length() - 16);
    //          result[1] = value.substring(sub, value.length() - 16);
    //      }

    //   } else {
    //      result[2] = value.substring(sub);
    //   }

    //    return result;
    // }

    private static boolean isColor(String c) {
        char[] valueChars = c.toCharArray();
        if (valueChars.length > 1) {
            return false;
        }
        return isColor(valueChars[0]);
    }

    /**
     * Set scoreboard title.
     *
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
        sendPacket(player, createObjectPacket(2));
    }

    public void setLine(int line, String value) {
        if (line >= 0 && line < 15) {
            String oldValue = lines[line];
            if (oldValue != null && oldValue.equals(value)) {
                return;
            }

            lines[line] = value;

            VirtualTeam team = teamsLines[line];

            String prefix;
            String playerName;
            String suffix;

            String[] parts = getParts(value);

            if (parts[1].length() > 12) {
                String firstPart = parts[1];
                parts[1] = firstPart.concat(String.valueOf(firstPart.charAt(11)));
            }

            prefix = parts[0];
            playerName = ChatColor.values()[line] + "§r" + parts[1];
            suffix = parts[2];


            if (team == null) {
                team = new VirtualTeam("_" + line);
                team.setPrefix(prefix);
                team.setSuffix(suffix);
                teamsLines[line] = team;
                sendPacket(player, team.create(playerName));
                PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(playerName);
                setField(packet, "b", player.getName());
                setField(packet, "c", 15 - line);
                setField(packet, "d", PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);
                sendPacket(player, packet);
                return;
            }

            String oldPrefix = team.getPrefix();
            String oldPlayerName = team.getPlayers().get(0);
            String oldSuffix = team.getSuffix();

            if (!prefix.equals(oldPrefix) || !suffix.equals(oldSuffix)) {
                team.setPrefix(prefix);
                team.setSuffix(suffix);
                sendPacket(player, team.update());
            }

            if (!playerName.equals(oldPlayerName)) {
                sendPacket(player, team.removePlayer(oldPlayerName));
                sendPacket(player, team.addPlayer(playerName));
                PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(playerName);
                setField(packet, "b", player.getName());
                setField(packet, "c", 15 - line);
                setField(packet, "d", PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);
                sendPacket(player, packet);
                sendPacket(player, new PacketPlayOutScoreboardScore(oldPlayerName));
            }
        }
    }

    /**
     * Add an empty line to scoreboard.
     */
    public void setEmptyLine(int line) {
        if (lines[line] != null && lines[line].startsWith("§f")) {
            return;
        }

        List<String> list = Arrays.asList(lines);

        StringBuilder res = new StringBuilder("§f");
        for (int i = 0; i < 15; i++) {
            res.append(" ");
            if (!list.contains(res.toString())) {
                break;
            }
        }

        setLine(line, res.toString());
    }

    /**
     * Remove a line on the scoreboard.
     *
     * @param line the line number
     */
    public void removeLine(int line) {
        VirtualTeam team = teamsLines[line];
        if (team != null) {
            sendPacket(player, new PacketPlayOutScoreboardScore(team.getPlayers().get(0)));
            sendPacket(player, team.destroy());
            teamsLines[line] = null;
        }
    }

    public void removeAllLines() {
        for (int i = 0; i < teamsLines.length; i++) {
            VirtualTeam team = teamsLines[i];
            if (team != null) {
                sendPacket(player, new PacketPlayOutScoreboardScore(team.getPlayers().get(0)));
                sendPacket(player, team.destroy());
                teamsLines[i] = null;
            }
            lines[i] = null;
        }

    }

    /**
     * Destroy the player scoreboard.
     * It sends the packets to remove the teams and scores.
     */
    public void destroy() {
        sendPacket(player, createObjectPacket(1));
        for (VirtualTeam team : teamsLines) {
            if (team != null) {
                sendPacket(player, team.destroy());
            }
        }
        teamsLines = new VirtualTeam[15];
        lines = new String[15];
        destroyed = true;
    }

    /*
    Private methods
     */
    public void sendInitPackets() {
        sendPacket(player, createObjectPacket(0));
        PacketPlayOutScoreboardDisplayObjective displayPacket = new PacketPlayOutScoreboardDisplayObjective();
        setField(displayPacket, "a", 1);
        setField(displayPacket, "b", player.getName());
        sendPacket(player, displayPacket);
    }

    public PacketPlayOutScoreboardObjective createObjectPacket(int mode) {
        PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective();
        setField(packet, "a", player.getName());
        setField(packet, "d", mode);
        if (mode == 0 || mode == 2) {
            setField(packet, "b", title);
            setField(packet, "c", IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
        }
        return packet;
    }

    private String[] getParts(String value) {
        //    Fin Bordure : 20:00

        if (value.length() <= 14) {
            return new String[]{value, "", ""};
        }

        short currentState = 0;
        String[] result = new String[]{"", "", ""};

        boolean nextIsColor = false;
        boolean isForceJump = false;

        StringBuilder colorsBuffer = new StringBuilder();
        StringBuilder stateBuffer = new StringBuilder();

        String[] valueChars = value.codePoints().mapToObj(cp -> new String(Character.toChars(cp)))
                .toArray(String[]::new);

        for (int index = 0; index < valueChars.length; index++) {
            String currentChar = valueChars[index];
            int currentStateMaxLength = STATE_LENGTH[currentState];
            int estimatedLength = stateBuffer.length();

            //  Check buffer length and go to next part if complete
            if (estimatedLength >= currentStateMaxLength || isForceJump) {
                isForceJump = false;
                result[currentState] = stateBuffer.toString();
                stateBuffer.setLength(0);
                stateBuffer.append(colorsBuffer);
                currentState += 1;

                int remainingChars = valueChars.length - index;
                int nextStateEstimatedLength = remainingChars + stateBuffer.length();
                if (currentState == 1 && nextStateEstimatedLength <= 16) {
                    return new String[]{result[0], "", stateBuffer + value.substring(index)};
                }
            }

            // Prevent overflow by exiting loop
            if (currentState > 2) {
                break;
            }

            if (nextIsColor) {
                nextIsColor = false;
                if (isColor(currentChar)) {
                    // Check if the state buffer was populated with old colors
                    if (stateBuffer.toString().equals(colorsBuffer.toString())) {
                        stateBuffer.setLength(0);
                    }
                    colorsBuffer.setLength(0);
                }
                if ("r".equals(currentChar)) {
                    colorsBuffer.setLength(0);
                } else {
                    colorsBuffer.append("§").append(currentChar);
                }
                if (estimatedLength + 2 < currentStateMaxLength) {
                    stateBuffer.append("§").append(currentChar);
                } else {
                    isForceJump = true;
                }
            } else if ("§".equals(currentChar)) {
                nextIsColor = true;
            } else {
                stateBuffer.append(currentChar);
            }
        }

        if (currentState < 3 && stateBuffer.length() >= colorsBuffer.length()) {
            result[currentState] = stateBuffer.toString();
        }

        return result;
    }


    public void reset() {
        destroy();
        destroyed = false;
        sendInitPackets();
    }
}
