package net.fameless.allItemsPlus.game;

import net.fameless.allItemsPlus.AllItemsPlus;
import net.fameless.allItemsPlus.language.Lang;
import net.fameless.allItemsPlus.util.Advancement;
import net.fameless.allItemsPlus.util.Format;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class GameListener implements Listener {

    private final AllItemsPlus allItemsPlus;
    private final GameManager gameManager;

    public GameListener(@NotNull AllItemsPlus allItemsPlus) {
        this.allItemsPlus = allItemsPlus;
        this.gameManager = allItemsPlus.getGameManager();

        runItemTask();
        runBiomeTask();
        runAdvancementTask();
    }

    /**
     * Periodically checks every player's inventory for items that have not
     * been collected yet, and if such items are found, marks them as collected
     * and announces the event to all online players.
     *
     * <p>This method is called every 5 ticks.
     */
    private void runItemTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!gameManager.isItemsRunning()) return;
                if (allItemsPlus.getTimer().isNotRunning()) return;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (ItemStack item : player.getInventory().getContents()) {
                        if (item == null) continue;
                        Material type = item.getType();
                        if (gameManager.isObjectiveUnavailable(type)) continue;

                        gameManager.finish(type, player);

                        String message = Lang.getCaption("item-found")
                                .replace("{player-name}", player.getName())
                                .replace("{item-name}", LegacyComponentSerializer.legacySection().serialize(Component.translatable(item.getTranslationKey())))
                                .replace("{items-collected}", String.valueOf(gameManager.getCollectedItemsCount()))
                                .replace("{total-items}", String.valueOf(gameManager.getTotalItemCount()));
                        Bukkit.broadcastMessage(message);
                    }
                }
            }
        }.runTaskTimer(allItemsPlus, 0, 5);
    }

    /**
     * Checks if a player has killed a mob that has not been killed yet, and
     * if such a mob is found, marks it as killed and announces the event
     * to all online players.
     *
     * <p>This event is called when a player damages an entity.
     *
     * @param event the event to handle
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!gameManager.isMobsRunning()) return;
        if (allItemsPlus.getTimer().isNotRunning()) return;
        if (!(event.getDamager() instanceof Player player)) return;
        if (gameManager.isObjectiveUnavailable(event.getEntity().getType())) return;
        if (!event.getEntity().isDead()) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            if (entity.getHealth() - event.getDamage() > 0) return;
        }

        EntityType mob = event.getEntity().getType();
        gameManager.finish(mob, player);

        String message = Lang.getCaption("mob-killed")
                .replace("{player-name}", player.getName())
                .replace("{mob-name}", LegacyComponentSerializer.legacySection().serialize(Component.translatable(mob.getTranslationKey())))
                .replace("{mobs-killed}", String.valueOf(gameManager.getKilledMobsCount()))
                .replace("{total-mobs}", String.valueOf(gameManager.getTotalMobCount()));
        Bukkit.broadcastMessage(message);
    }

    /**
     * Starts a repeating task that checks if any players have entered a new biome.
     * If a player has entered a biome that is not yet finished, it will be added
     * to the list of finished biomes and an announcement will be sent to all
     * players.
     */
    private void runBiomeTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!gameManager.isBiomesRunning()) return;
                if (allItemsPlus.getTimer().isNotRunning()) return;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Biome playerBiome = player.getWorld().getBiome(player.getLocation());
                    if (gameManager.isObjectiveUnavailable(playerBiome)) continue;

                    gameManager.finish(playerBiome, player);

                    String message = Lang.getCaption("biome-found")
                            .replace("{player-name}", player.getName())
                            .replace("{biome-name}", Format.formatName(playerBiome.name()))
                            .replace("{biomes-found}", String.valueOf(gameManager.getFoundBiomesCount()))
                            .replace("{total-biomes}", String.valueOf(gameManager.getTotalBiomeCount()));
                    Bukkit.broadcastMessage(message);
                }
            }
        }.runTaskTimer(allItemsPlus, 0, 5);
    }

    /**
     * Starts a repeating task that checks if any players have finished any
     * advancements. If a player has finished an advancement that has not been
     * finished yet, it will be added to the list of finished advancements and
     * an announcement will be sent to all players.
     */
    private void runAdvancementTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!gameManager.isAdvancementsRunning()) return;
                if (allItemsPlus.getTimer().isNotRunning()) return;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (Advancement advancement : Advancement.values()) {
                        if (gameManager.isObjectiveUnavailable(advancement)) continue;
                        if (!hasAdvancement(player, advancement.getKey().toString())) continue;

                        gameManager.finish(advancement, player);

                        String message = Lang.getCaption("advancement-finished")
                                .replace("{player-name}", player.getName())
                                .replace("{advancement-name}", advancement.name)
                                .replace("{advancements-finished}", String.valueOf(gameManager.getFinishedAdvancementsCount()))
                                .replace("{total-advancements}", String.valueOf(gameManager.getTotalAdvancementCount()));
                        Bukkit.broadcastMessage(message);
                    }
                }
            }
        }.runTaskTimer(allItemsPlus, 0, 5);
    }

    /**
     * Returns true if the player has finished the specified advancement, false
     * otherwise.
     *
     * @param player The player to check.
     * @param name The name of the advancement to check, e.g. "husbandry/fish_cod".
     * @return true if the player has finished the specified advancement, false otherwise.
     */
    private boolean hasAdvancement(Player player, String name) {
        org.bukkit.advancement.Advancement advancement = getAdvancement(name);
        if (advancement == null) {
            return false;
        }
        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        return progress.isDone();
    }

    /**
     * Gets the specified advancement, or null if it could not be found.
     * Case is ignored.
     *
     * @param name The name of the advancement to find, e.g. "husbandry/fish_cod".
     * @return The specified advancement, or null if it could not be found.
     */
    private @Nullable org.bukkit.advancement.Advancement getAdvancement(String name) {
        Iterator<org.bukkit.advancement.Advancement> it = Bukkit.getServer().advancementIterator();
        while (it.hasNext()) {
            org.bukkit.advancement.Advancement a = it.next();
            if (a.getKey().toString().equalsIgnoreCase(name)) {
                return a;
            }
        }
        return null;
    }
}
