package fr.menestis.commons.packets.scoreboard;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author Blendman974
 */
public class ScoreboardPage {

    private final String title;
    private final Map<Integer, BiFunction<Long, Player, String>> lines = new HashMap<>();

    public ScoreboardPage(String title) {
        this.title = title;
    }

    public void addLine(int i, BiFunction<Long, Player, String> provider) {
        this.lines.put(i, provider);
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
