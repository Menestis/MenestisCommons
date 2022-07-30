package fr.menestis.commons.packets.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author Blendman974
 */
public class ScoreboardPage {

    private final String title;

    private final Scroller scroller = new Scroller(ChatColor.AQUA, "menestis.fr", "§9", "§3", "§3", false, Scroller.ScrollType.FORWARD);

    private final Map<Integer, BiFunction<Long, Player, String>> lines = new HashMap<>();

    public ScoreboardPage(String title) {
        this.title = title;
    }

    public void addLine(int i, BiFunction<Long, Player, String> provider) {
        this.lines.put(i, provider);
    }


    public void setScroller(int line) {
        this.lines.put(line, (aLong, player) -> "§8❯ §b" + scroller.next());
    }


    public void update(VirtualScoreboard sb, Player pl, long i) {
        sb.setTitle(title);
        lines.forEach((line, f) -> {
            String result = f == null ? null : f.apply(i, pl);
            if (result == null)
                sb.removeLine(line);
            else if (result.equals("")) {
                sb.setEmptyLine(line);
            } else {
                sb.setLine(line, result);
            }
        });
    }
}
