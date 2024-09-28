package net.fameless.allItemsPlus.game.command;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fameless.allItemsPlus.AllItemsPlus;
import net.fameless.allItemsPlus.language.Lang;
import net.fameless.allItemsPlus.util.ChallengeType;
import net.fameless.allItemsPlus.util.Format;
import net.fameless.allItemsPlus.util.ItemUtils;
import net.fameless.allItemsPlus.util.Skull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class LeaderboardCommand implements CommandExecutor, Listener, InventoryHolder {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player player) {
            player.openInventory(getLeaderboardGUI(ChallengeType.ALL));
        }
        return false;
    }

    @SuppressWarnings("ConstantConditions") // Item at slot 1 must never be null if inventory holder instanceof this
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof LeaderboardCommand)) return;
        event.setCancelled(true);

        switch (event.getSlot()) {
            case 1 -> event.getWhoClicked().openInventory(getLeaderboardGUI(ChallengeType.ALL));
            case 2 -> event.getWhoClicked().openInventory(getLeaderboardGUI(ChallengeType.ALL_ITEMS));
            case 3 -> event.getWhoClicked().openInventory(getLeaderboardGUI(ChallengeType.ALL_MOBS));
            case 4 -> event.getWhoClicked().openInventory(getLeaderboardGUI(ChallengeType.ALL_BIOMES));
            case 5 -> event.getWhoClicked().openInventory(getLeaderboardGUI(ChallengeType.ALL_ADVANCEMENTS));
            case 7 -> event.getWhoClicked().closeInventory();
        }
    }

    /**
     * Generates the leaderboard GUI inventory based on the specified challenge type.
     *
     * @param pageType The type of challenge for which the leaderboard is to be displayed.
     * @return The generated inventory containing the leaderboard.
     */
    private @NotNull Inventory getLeaderboardGUI(@NotNull ChallengeType pageType) {
        Inventory inventory = Bukkit.createInventory(this, 36, Lang.getCaption("leaderboard-title") + " | " + Format.formatName(pageType.name()));

        Material[] materials = {Material.PAINTING, Material.ITEM_FRAME, Material.DIAMOND_SWORD, Material.GRASS_BLOCK, Material.BREWING_STAND, Material.BARRIER};
        String[] names = {"Total", "All Items", "All Mobs", "All Biomes", "All Advancements", Lang.getCaption("close")};
        for (int i = 0; i < materials.length; i++) {
            inventory.setItem(i + 1, ItemUtils.buildItem(new ItemStack(materials[i]), ChatColor.BLUE + names[i], true));
        }
        inventory.setItem(0, ItemUtils.getFillerItem());
        inventory.setItem(6, ItemUtils.getFillerItem());
        inventory.setItem(7, ItemUtils.getFillerItem());
        inventory.setItem(8, ItemUtils.getFillerItem());

        Map<OfflinePlayer, Integer> objectivesMap = new HashMap<>();
        switch (pageType) {
            case ALL_ITEMS -> populateMap(objectivesMap, AllItemsPlus.get().getGameManager().getItemDataObject());
            case ALL_MOBS -> populateMap(objectivesMap, AllItemsPlus.get().getGameManager().getMobDataObject());
            case ALL_BIOMES -> populateMap(objectivesMap, AllItemsPlus.get().getGameManager().getBiomeDataObject());
            case ALL_ADVANCEMENTS -> populateMap(objectivesMap, AllItemsPlus.get().getGameManager().getAdvancementDataObject());
            case ALL -> {
                populateMap(objectivesMap, AllItemsPlus.get().getGameManager().getItemDataObject());
                populateMap(objectivesMap, AllItemsPlus.get().getGameManager().getMobDataObject());
                populateMap(objectivesMap, AllItemsPlus.get().getGameManager().getBiomeDataObject());
                populateMap(objectivesMap, AllItemsPlus.get().getGameManager().getAdvancementDataObject());
            }
        }

        Map<OfflinePlayer, Integer> sortedMap = sortByValueDescending(objectivesMap);
        for (Map.Entry<OfflinePlayer, Integer> entry : sortedMap.entrySet()) {
            inventory.addItem(ItemUtils.buildItem(Skull.PlayerSkulls.getSkullByUUID(entry.getKey().getUniqueId()), entry.getKey().getName(), false, "", ChatColor.BLUE.toString() + entry.getValue()));
        }

        return inventory;
    }

    /**
     * Populates the given map with player objectives data.
     *
     * @param map        The map to populate with player objectives.
     * @param dataObject The data object containing player objectives data.
     */
    private void populateMap(Map<OfflinePlayer, Integer> map, @NotNull JsonObject dataObject) {
        for (Map.Entry<String, JsonElement> entry : dataObject.entrySet()) {
            JsonObject jsonObject = entry.getValue().getAsJsonObject();
            UUID playerID = UUID.fromString(jsonObject.get("playerUUID").getAsString());
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerID);
            map.putIfAbsent(player, 0);
            map.put(player, map.get(player) + 1);
        }
    }

    /**
     * Sorts the given map by value in descending order.
     *
     * @param map The map to sort.
     * @return The sorted map.
     */
    private Map<OfflinePlayer, Integer> sortByValueDescending(@NotNull Map<OfflinePlayer, Integer> map) {
        return map.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    public @NotNull Inventory getInventory() {
        return getLeaderboardGUI(ChallengeType.ALL);
    }
}
