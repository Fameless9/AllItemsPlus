package net.fameless.allItemsPlus.game.command;

import net.fameless.allItemsPlus.AllItemsPlus;
import net.fameless.allItemsPlus.language.Lang;
import net.fameless.allItemsPlus.util.ChallengeType;
import net.fameless.allItemsPlus.util.Format;
import net.fameless.allItemsPlus.util.ItemUtils;
import net.fameless.allItemsPlus.util.Skull;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public class SettingsCommand implements CommandExecutor, Listener, InventoryHolder {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.getCaption("command-not-a-player"));
            return false;
        }
        player.openInventory(getSettingsGUI());
        return true;
    }

    /**
     * Generates a GUI displaying settings for the current challenge.
     * @return the generated GUI
     */
    private @NotNull Inventory getSettingsGUI() {
        Inventory inventory = Bukkit.createInventory(this, 27, Lang.getCaption("settings-title"));

        inventory.setItem(0,
                ItemUtils.buildItem(
                        new ItemStack(Material.ITEM_FRAME),
                        ChatColor.BLUE + "All Items",
                        true,
                        formatLore(Lang.getCaption("menu-item-all-items-lore")))
        );
        inventory.setItem(1,
                ItemUtils.buildItem(
                        new ItemStack(Material.DIAMOND_SWORD),
                        ChatColor.BLUE + "All Mobs",
                        true,
                        formatLore(Lang.getCaption("menu-item-all-mobs-lore")))
        );
        inventory.setItem(2,
                ItemUtils.buildItem(
                        new ItemStack(Material.GRASS_BLOCK),
                        ChatColor.BLUE + "All Biomes",
                        true,
                        formatLore(Lang.getCaption("menu-item-all-biomes-lore")))
        );
        inventory.setItem(3,
                ItemUtils.buildItem(
                        new ItemStack(Material.BREWING_STAND),
                        ChatColor.BLUE + "All Advancements",
                        false,
                        formatLore(Lang.getCaption("menu-item-all-advancements-lore")))
        );
        inventory.setItem(5,
                ItemUtils.buildItem(
                        new ItemStack(Material.BARRIER),
                        Lang.getCaption("reset"),
                        true,
                        formatLore(Lang.getCaption("menu-item-reset-lore")))
        );
        inventory.setItem(6,
                ItemUtils.buildItem(
                        new ItemStack(Material.CHAIN),
                        ChatColor.BLUE + "Chain Mode",
                        true,
                        formatLore(Lang.getCaption("menu-item-chain-mode-lore"))
                )
        );
        inventory.setItem(26,
                ItemUtils.buildItem(
                        Skull.GITHUB.asItemStack(),
                        Lang.getCaption("bug-report"),
                        true,
                        formatLore(Lang.getCaption("bug-report-lore"))
                )
        );
        return inventory;
    }

    /**
     * Format the lore for an item in the settings menu. This is necessary because we want to display dynamic text in the
     * menu, such as whether certain challenges are running or not.
     *
     * @param input The input string that will be formatted.
     * @return The formatted list of strings for the lore.
     */
    private @Unmodifiable List<String> formatLore(String input) {
        input = input
                .replace("{all-advancements-active}",
                        Lang.getCaption(
                                AllItemsPlus.get().getGameManager().isAdvancementsRunning() ? "active-true" : "active-false"))
                .replace(
                        "{all-biomes-active}",
                        Lang.getCaption(
                                AllItemsPlus.get().getGameManager().isBiomesRunning() ? "active-true" : "active-false"))
                .replace(
                        "{all-mobs-active}",
                        Lang.getCaption(
                                AllItemsPlus.get().getGameManager().isMobsRunning() ? "active-true" : "active-false"))
                .replace(
                        "{all-items-active}",
                        Lang.getCaption(
                                AllItemsPlus.get().getGameManager().isItemsRunning() ? "active-true" : "active-false"))
                .replace("{chain-mode-active}",
                        Lang.getCaption(
                                AllItemsPlus.get().getGameManager().isChainModeEnabled() ? "active-true" : "active-false"));

        return List.of(input.split("\\{br}"));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof SettingsCommand)) return;
        event.setCancelled(true);
        ChallengeType challengeType = null;
        boolean newRunning = false;
        switch (event.getSlot()) {
            case 0 -> {
                challengeType = ChallengeType.ALL_ITEMS;
                newRunning = !AllItemsPlus.get().getGameManager().isItemsRunning();
                AllItemsPlus.get().getGameManager().setItemsRunning(newRunning);
            }
            case 1 -> {
                challengeType = ChallengeType.ALL_MOBS;
                newRunning = !AllItemsPlus.get().getGameManager().isMobsRunning();
                AllItemsPlus.get().getGameManager().setMobsRunning(newRunning);
            }
            case 2 -> {
                challengeType = ChallengeType.ALL_BIOMES;
                newRunning = !AllItemsPlus.get().getGameManager().isBiomesRunning();
                AllItemsPlus.get().getGameManager().setBiomesRunning(newRunning);
            }
            case 3 -> {
                challengeType = ChallengeType.ALL_ADVANCEMENTS;
                newRunning = !AllItemsPlus.get().getGameManager().isAdvancementsRunning();
                AllItemsPlus.get().getGameManager().setAdvancementsRunning(newRunning);
            }
            case 5 -> AllItemsPlus.get().getGameManager().resetProgress();
            case 6 -> AllItemsPlus.get().getGameManager().setChainModeEnabled(!AllItemsPlus.get().getGameManager().isChainModeEnabled());
            case 26 -> {
                TextComponent toClick = new TextComponent(Lang.getCaption("prefix") + ChatColor.BLUE + ChatColor.UNDERLINE + "Link to GitHub");
                toClick.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/Fameless9/AllItemsPlus/issues"));
                event.getWhoClicked().spigot().sendMessage(toClick);
                event.getWhoClicked().closeInventory();
            }
        }
        if (challengeType != null) {
            if (newRunning) {
                Bukkit.broadcastMessage(Lang.getCaption("challenge-toggled-on")
                        .replace("{challenge-name}", Format.formatName(challengeType.name())));
            } else {
                Bukkit.broadcastMessage(Lang.getCaption("challenge-toggled-off")
                        .replace("{challenge-name}", Format.formatName(challengeType.name())));
            }
        }
        event.getWhoClicked().openInventory(getSettingsGUI());
    }

    @NotNull @Override
    public Inventory getInventory() {
        return getSettingsGUI();
    }
}
