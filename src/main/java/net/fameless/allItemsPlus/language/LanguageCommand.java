package net.fameless.allItemsPlus.language;

import net.fameless.allItemsPlus.AllItemsPlus;
import net.fameless.allItemsPlus.util.ItemUtils;
import net.fameless.allItemsPlus.util.Skull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class LanguageCommand implements CommandExecutor, Listener, InventoryHolder {

    private final AllItemsPlus allItemsPlus;

    public LanguageCommand(AllItemsPlus allItemsPlus) {
        this.allItemsPlus = allItemsPlus;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Lang.getCaption("command-not-a-player"));
            return false;
        }
        if (args.length > 0) {
            try {
                Language newLang = Language.valueOf(args[0]);
                allItemsPlus.getConfig().set("lang", newLang.getIdentifier());
                allItemsPlus.saveConfig();
                Lang.loadLanguage(allItemsPlus);
                Bukkit.broadcastMessage(Lang.getLanguage().getUpdateMessage());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + "Invalid language!");
                player.openInventory(getLanguageInventory());
            }
        } else {
            player.openInventory(getLanguageInventory());
        }

        return false;
    }

    /**
     * Creates a GUI that allows players to change the language.
     *
     * @return the inventory for the language GUI
     */
    private @NotNull Inventory getLanguageInventory() {
        Inventory gui = Bukkit.createInventory(this, 9, Lang.getCaption("adjust-language"));
        gui.setItem(0,
                ItemUtils.buildItem(
                        Skull.FLAG_UK.asItemStack(),
                        ChatColor.GOLD + "English",
                        false,
                        ChatColor.GRAY + "Click to set the language to english"
                )
        );
        gui.setItem(1,
                ItemUtils.buildItem(
                        Skull.FLAG_GERMANY.asItemStack(),
                        ChatColor.GOLD + "Deutsch",
                        false,
                        ChatColor.GRAY + "Klicke, um die Sprache auf deutsch zu stellen"
                )
        );
        return gui;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof LanguageCommand)) return;
        event.setCancelled(true);

        switch (event.getSlot()) {
            case 0 -> {
                if (Lang.getLanguage() == Language.ENGLISH) {
                    event.getWhoClicked().sendMessage(ChatColor.RED + "The language is already set to english!");
                    return;
                }
                allItemsPlus.getConfig().set("lang", "en");
                allItemsPlus.saveConfig();
                Lang.loadLanguage(allItemsPlus);
                Bukkit.broadcastMessage(Lang.getLanguage().getUpdateMessage());
            }
            case 1 -> {
                if (Lang.getLanguage() == Language.GERMAN) {
                    event.getWhoClicked()
                            .sendMessage(ChatColor.RED + "Die Sprache ist bereits auf Deutsch eingestellt.");
                    return;
                }
                allItemsPlus.getConfig().set("lang", "de");
                allItemsPlus.saveConfig();
                Lang.loadLanguage(allItemsPlus);
                Bukkit.broadcastMessage(Lang.getLanguage().getUpdateMessage());
            }
        }
        allItemsPlus.getBossbarManager().reloadTitles();
        event.getWhoClicked().openInventory(getLanguageInventory());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return getLanguageInventory();
    }
}
