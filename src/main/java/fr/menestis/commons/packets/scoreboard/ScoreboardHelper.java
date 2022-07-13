package fr.menestis.commons.packets.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Blendman974
 */
public class ScoreboardHelper {
    private final ScoreboardManager manager;
    private final List<ScoreboardPage> pages = new ArrayList<>();
    private int timePerPage = 10;
    private int current = -1;

    public ScoreboardHelper(ScoreboardManager manager) {
        this.manager = manager;
    }

    public void addPage(ScoreboardPage page) {
        this.pages.add(page);
    }

    public void removePage(ScoreboardPage page) {
        this.pages.remove(page);
    }

    public void setTimePerPages(int timePerPage) {
        this.timePerPage = timePerPage;
    }

    public void tick(long i) {
        if (pages.isEmpty())
            return;

        int page = (int) (i / timePerPage) % this.pages.size();

        ScoreboardPage scoreboardPage = this.pages.get(page);

        for (Player pl : Bukkit.getOnlinePlayers()) {
            VirtualScoreboard sb = manager.getScoreboard(pl.getUniqueId());
            if (current != page)
                sb.removeAllLines();
            scoreboardPage.update(sb, pl, i);
        }

        current = page;
    }

}
