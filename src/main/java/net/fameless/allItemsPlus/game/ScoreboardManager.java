package net.fameless.allItemsPlus.game;

import net.fameless.allItemsPlus.AllItemsPlus;
import net.fameless.allItemsPlus.util.Format;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

public class ScoreboardManager {

    private final AllItemsPlus allItemsPlus;
    private final Scoreboard scoreboard;
    private Objective objective;

    public ScoreboardManager(AllItemsPlus allItemsPlus) {
        this.allItemsPlus = allItemsPlus;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        runScoreboardUpdateTask();
    }

    private void runScoreboardUpdateTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.setScoreboard(scoreboard);
                }

                if (allItemsPlus.getGameManager().isChainModeEnabled()) {
                    setupObjective();
                } else {
                    clearObjective();
                    return;
                }

                updateTeam("itemTeam", ChatColor.RED, "Item » ", 4, allItemsPlus.getGameManager().isItemsRunning(), allItemsPlus.getGameManager().getItemChainList().isEmpty() ? "✔" : allItemsPlus.getGameManager().getItemChainList().getFirst().name());
                updateTeam("mobTeam", ChatColor.GREEN, "Mob » ", 3, allItemsPlus.getGameManager().isMobsRunning(), allItemsPlus.getGameManager().getMobChainList().isEmpty() ? "✔" : allItemsPlus.getGameManager().getMobChainList().getFirst().name());
                updateTeam("biomeTeam", ChatColor.BLUE, "Biome » ", 2, allItemsPlus.getGameManager().isBiomesRunning(), allItemsPlus.getGameManager().getBiomeChainList().isEmpty() ? "✔" : allItemsPlus.getGameManager().getBiomeChainList().getFirst().name());
                updateTeam("advancementTeam", ChatColor.YELLOW, "Advancement » ", 1, allItemsPlus.getGameManager().isAdvancementsRunning(), allItemsPlus.getGameManager().getAdvancementChainList().isEmpty() ? "✔" : allItemsPlus.getGameManager().getAdvancementChainList().getFirst().name());
            }
        }.runTaskTimer(allItemsPlus, 0, 3);
    }

    private void setupObjective() {
        if (objective == null) {
            objective = scoreboard.registerNewObjective("objectives", Criteria.DUMMY, ChatColor.BLUE.toString() + ChatColor.BOLD + "CHAIN MODE");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.getScore(" ").setScore(5);
        }
    }

    private void clearObjective() {
        if (objective != null) {
            objective.unregister();
            objective = null;
        }
    }

    /**
     * Updates the specified team on the scoreboard.
     *
     * @param teamName The name of the team to update.
     * @param color The color associated with the team.
     * @param prefix The prefix to display before the team's name.
     * @param score The score to set for the team.
     * @param isRunning A boolean indicating if the team is currently running.
     * @param suffix The suffix to display after the team's name.
     */
    private void updateTeam(String teamName, ChatColor color, String prefix, int score, boolean isRunning, String suffix) {
        Team team = scoreboard.getTeam(teamName);
        if (isRunning) {
            if (team == null) {
                team = scoreboard.registerNewTeam(teamName);
                team.addEntry(color.toString());
                team.setPrefix(ChatColor.GRAY + prefix);
            }
            team.setSuffix(ChatColor.BLUE + Format.formatName(suffix));
            objective.getScore(color.toString()).setScore(score);
        } else {
            if (team != null) {
                team.unregister();
                scoreboard.resetScores(color.toString());
            }
        }
    }
}
