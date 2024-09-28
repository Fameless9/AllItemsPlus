package net.fameless.allItemsPlus.game;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fameless.allItemsPlus.AllItemsPlus;
import net.fameless.allItemsPlus.language.Lang;
import net.fameless.allItemsPlus.util.Format;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BossbarManager {

    private final AllItemsPlus allItemsPlus;
    private final int CHANGE_INTERVAL;
    private final List<String> titleCycles = new ArrayList<>();
    private final Set<Player> toHideFrom = new HashSet<>();

    private int progress = 0;
    private BossBar lastBossbar;

    public BossbarManager(@NotNull AllItemsPlus allItemsPlus) {
        this.allItemsPlus = allItemsPlus;
        this.CHANGE_INTERVAL = allItemsPlus.getConfig().getInt("bossbar.change-interval", 5);

        JsonObject bossbarTitles = Lang.getLangObject().getAsJsonObject("bossbar-titles");
        for (Map.Entry<String, JsonElement> entry : bossbarTitles.entrySet()) {
            String title = entry.getValue().getAsString();
            titleCycles.add(Format.applyMiniMessageFormat(title));
        }

        runBossbarTask();
        runTextChangeTask();
    }

    /**
     * Reloads the list of bossbar title cycles from the language file.
     */
    public void reloadTitles() {
        this.titleCycles.clear();
        JsonObject bossbarTitles = Lang.getLangObject().getAsJsonObject("bossbar-titles");
        for (Map.Entry<String, JsonElement> entry : bossbarTitles.entrySet()) {
            String title = entry.getValue().getAsString();
            titleCycles.add(Format.applyMiniMessageFormat(title));
        }
    }

    /**
     * Toggles whether the bossbar is visible to a player.
     * @param player The player to toggle the bossbar visibility for.
     */
    public void toggleHidden(Player player) {
        if (toHideFrom.contains(player)) {
            toHideFrom.remove(player);
        } else {
            toHideFrom.add(player);
        }
    }

    /**
     * Replaces placeholders in the given string with the current progress of the challenges.
     * @param input The string to replace placeholders in.
     * @return The string with all placeholders replaced.
     */
    private @NotNull String replaced(@NotNull String input) {
        return input
                .replace("{items-collected}", String.valueOf(allItemsPlus.getGameManager().getCollectedItemsCount()))
                .replace("{total-items}", String.valueOf(allItemsPlus.getGameManager().getTotalItemCount()))
                .replace("{mobs-killed}", String.valueOf(allItemsPlus.getGameManager().getKilledMobsCount()))
                .replace("{total-mobs}", String.valueOf(allItemsPlus.getGameManager().getTotalMobCount()))
                .replace("{biomes-found}", String.valueOf(allItemsPlus.getGameManager().getFoundBiomesCount()))
                .replace("{total-biomes}", String.valueOf(allItemsPlus.getGameManager().getTotalBiomeCount()))
                .replace("{advancements-finished}", String.valueOf(allItemsPlus.getGameManager().getFinishedAdvancementsCount()))
                .replace("{total-advancements}", String.valueOf(allItemsPlus.getGameManager().getTotalAdvancementCount()))
                .replace("{items-active}", "")
                .replace("{mobs-active}", "")
                .replace("{biomes-active}", "")
                .replace("{advancements-active}", "");
    }

    /**
     * Creates a repeating task that will update the bossbar of all online players at a regular interval.
     * The bossbar title is cycled through the given list, and placeholders are replaced with the current
     * progress of the challenges.
     * If a challenge is not running, the associated placeholders are removed from the title.
     * If all challenges are not running, the bossbar is removed.
     */
    private void runBossbarTask() {
        new BukkitRunnable() {
            private @NotNull List<String> getApplicableTitles() {
                List<String> applicableTitles = new ArrayList<>();
                for (String s : titleCycles) {
                    if (s.contains("{items-active}") && !allItemsPlus.getGameManager().isItemsRunning()) continue;
                    if (s.contains("{mobs-active}") && !allItemsPlus.getGameManager().isMobsRunning()) continue;
                    if (s.contains("{biomes-active}") && !allItemsPlus.getGameManager().isBiomesRunning()) continue;
                    if (s.contains("{advancements-active}") && !allItemsPlus.getGameManager().isAdvancementsRunning())
                        continue;
                    applicableTitles.add(s
                            .replace("{items-active}", "")
                            .replace("{mobs-active}", "")
                            .replace("{biomes-active}", "")
                            .replace("{advancements-active}", "")
                    );
                }
                return applicableTitles;
            }

            @Override
            public void run() {
                List<String> applicableTitles = getApplicableTitles();

                if (applicableTitles.isEmpty()) {
                    if (lastBossbar != null) {
                        lastBossbar.removeAll();
                        lastBossbar = null;
                    }
                    return;
                }

                String title = applicableTitles.get(progress % applicableTitles.size());
                BossBar bossBar = Bukkit.createBossBar(replaced(title), BarColor.PURPLE, BarStyle.SOLID);

                if (lastBossbar != null) {
                    lastBossbar.removeAll();
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (toHideFrom.contains(player)) continue;
                    bossBar.addPlayer(player);
                }
                lastBossbar = bossBar;
            }
        }.runTaskTimer(allItemsPlus, 3, 3);
    }

    /**
     * Changes the text of the bossbar every {@link #CHANGE_INTERVAL} seconds.
     * <p>
     * This method is called in the constructor and schedules a repeating task that
     * increments the {@link #progress} variable and reloads the titles of the bossbar
     * by calling {@link #reloadTitles()}.
     */
    private void runTextChangeTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                progress++;
                reloadTitles();
            }
        }.runTaskTimer(allItemsPlus, CHANGE_INTERVAL * 20L, CHANGE_INTERVAL * 20L);
    }
}
