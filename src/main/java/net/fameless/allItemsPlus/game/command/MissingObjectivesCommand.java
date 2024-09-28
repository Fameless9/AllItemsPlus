package net.fameless.allItemsPlus.game.command;

import net.fameless.allItemsPlus.AllItemsPlus;
import net.fameless.allItemsPlus.language.Lang;
import net.fameless.allItemsPlus.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;

public class MissingObjectivesCommand implements CommandExecutor, Listener, InventoryHolder {

    private final AllItemsPlus allItemsPlus;
    private final int POSSIBLE_ENTRIES_PER_PAGE = 45;

    public MissingObjectivesCommand(AllItemsPlus allItemsPlus) {
        this.allItemsPlus = allItemsPlus;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player) {
            player.openInventory(getMissingObjectivesGUI(ChallengeType.ALL_ITEMS, 0));
        }
        return false;
    }

    @SuppressWarnings("ConstantConditions") // Item at slot 1 must never be null if inventory holder instanceof this
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof MissingObjectivesCommand)) return;
        event.setCancelled(true);

        ItemStack dataItem = event.getInventory().getItem(1);
        ItemMeta meta = dataItem.getItemMeta();
        int page = meta.getPersistentDataContainer().get(InventoryUtils.PAGE_KEY, PersistentDataType.INTEGER);
        ChallengeType pageType = ChallengeType.valueOf(meta.getPersistentDataContainer().get(InventoryUtils.PAGE_TYPE_KEY, PersistentDataType.STRING));

        switch (event.getSlot()) {
            case 0 -> {
                if (event.getInventory().getItem(0).equals(ItemUtils.getFillerItem())) return;
                event.getWhoClicked().openInventory(getMissingObjectivesGUI(pageType, page - 1));
            }
            case 8 -> {
                if (event.getInventory().getItem(8).equals(ItemUtils.getFillerItem())) return;
                event.getWhoClicked().openInventory(getMissingObjectivesGUI(pageType, page + 1));
            }
            case 1 -> event.getWhoClicked().openInventory(getMissingObjectivesGUI(ChallengeType.ALL_ITEMS, 0));
            case 2 -> event.getWhoClicked().openInventory(getMissingObjectivesGUI(ChallengeType.ALL_MOBS, 0));
            case 3 -> event.getWhoClicked().openInventory(getMissingObjectivesGUI(ChallengeType.ALL_BIOMES, 0));
            case 4 -> event.getWhoClicked().openInventory(getMissingObjectivesGUI(ChallengeType.ALL_ADVANCEMENTS, 0));
            case 6 -> event.getWhoClicked().closeInventory();
        }
    }

    /**
     * Generates a GUI displaying all missing objectives of a given type.
     * @param pageType the type of objectives to display
     * @param page the page number to display
     * @return the generated GUI
     */
    private @NotNull Inventory getMissingObjectivesGUI(@NotNull ChallengeType pageType, int page) {
        Inventory inventory = Bukkit.createInventory(
                this, 54, Lang.getCaption("missing-objectives-title") + " | " + Format.formatName(pageType.name()));

        ItemStack dataItem = ItemUtils.buildItem(new ItemStack(Material.ITEM_FRAME), ChatColor.BLUE + "All Items", true);
        ItemUtils.addData(dataItem, InventoryUtils.PAGE_KEY, PersistentDataType.INTEGER, page);
        ItemUtils.addData(dataItem, InventoryUtils.PAGE_TYPE_KEY, PersistentDataType.STRING, pageType.name());
        inventory.setItem(1, dataItem);
        inventory.setItem(2, ItemUtils.buildItem(new ItemStack(Material.DIAMOND_SWORD), ChatColor.BLUE + "All Mobs", true));
        inventory.setItem(3, ItemUtils.buildItem(new ItemStack(Material.GRASS_BLOCK), ChatColor.BLUE + "All Biomes", true));
        inventory.setItem(4, ItemUtils.buildItem(new ItemStack(Material.BREWING_STAND), ChatColor.BLUE + "All Advancements", true));
        inventory.setItem(5, ItemUtils.getFillerItem());
        inventory.setItem(6, ItemUtils.buildItem(new ItemStack(Material.BARRIER), Lang.getCaption("close"), true));
        inventory.setItem(7, ItemUtils.getFillerItem());

        LinkedHashSet<?> unfinishedObjectives;
        Material item;

        switch (pageType) {
            case ALL_ITEMS -> {
                unfinishedObjectives = allItemsPlus.getGameManager().getUncollectedObjectives(Material.values());
                item = Material.BARRIER;
            }
            case ALL_MOBS -> {
                unfinishedObjectives = allItemsPlus.getGameManager().getUncollectedObjectives(EntityType.values());
                item = Material.DIAMOND_SWORD;
            }
            case ALL_BIOMES -> {
                unfinishedObjectives = allItemsPlus.getGameManager().getUncollectedObjectives(Biome.values());
                item = Material.GRASS_BLOCK;
            }
            case ALL_ADVANCEMENTS -> {
                unfinishedObjectives = allItemsPlus.getGameManager().getUncollectedObjectives(Advancement.values());
                item = Material.BREWING_STAND;
            }
            default -> throw new IllegalStateException("Unexpected value: " + pageType);
        }

        if (InventoryUtils.isPageValid(unfinishedObjectives, page - 1, POSSIBLE_ENTRIES_PER_PAGE)) {
            inventory.setItem(0, ItemUtils.pageLeft());
        } else {
            inventory.setItem(0, ItemUtils.getFillerItem());
        }
        if (InventoryUtils.isPageValid(unfinishedObjectives, page + 1, POSSIBLE_ENTRIES_PER_PAGE)) {
            inventory.setItem(8, ItemUtils.pageRight());
        } else {
            inventory.setItem(8, ItemUtils.getFillerItem());
        }

        for (int i = page * POSSIBLE_ENTRIES_PER_PAGE; i < page * POSSIBLE_ENTRIES_PER_PAGE + POSSIBLE_ENTRIES_PER_PAGE &&
                i < unfinishedObjectives.size(); i++) {
            Object objective = SetUtils.getElementAt(unfinishedObjectives, i);
            Material material;
            if (objective instanceof Material) {
                material = (Material) objective;
            } else  {
                material = item;
            }

            inventory.addItem(
                    ItemUtils.buildItem(
                            new ItemStack(material),
                            objective instanceof Advancement ? ((Advancement) objective).name : Format.formatName(objective.toString()),
                            true
                    )
            );
        }
        return inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return getMissingObjectivesGUI(ChallengeType.ALL_ITEMS, 0);
    }
}
