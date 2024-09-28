package net.fameless.allItemsPlus.game;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fameless.allItemsPlus.AllItemsPlus;
import net.fameless.allItemsPlus.util.Advancement;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

public class GameManager {

    private final AllItemsPlus allItemsPlus;
    private final File dataFile;
    private JsonObject rootObject;

    private final LinkedHashSet<Material> collectedItems = new LinkedHashSet<>();
    private final LinkedHashSet<EntityType> killedMobs = new LinkedHashSet<>();
    private final LinkedHashSet<Biome> foundBiomes = new LinkedHashSet<>();
    private final LinkedHashSet<Advancement> finishedAdvancements = new LinkedHashSet<>();

    private final LinkedHashSet<Material> excludedItems = new LinkedHashSet<>();
    private final LinkedHashSet<EntityType> excludedMobs = new LinkedHashSet<>();
    private final LinkedHashSet<Biome> excludedBiomes = new LinkedHashSet<>();
    private final LinkedHashSet<Advancement> excludedAdvancements = new LinkedHashSet<>();

    private final LinkedHashSet<Material> itemChainList = new LinkedHashSet<>();
    private final LinkedHashSet<EntityType> mobChainList = new LinkedHashSet<>();
    private final LinkedHashSet<Biome> biomeChainList = new LinkedHashSet<>();
    private final LinkedHashSet<Advancement> advancementChainList = new LinkedHashSet<>();

    private final JsonObject itemDataObject;
    private final JsonObject mobDataObject;
    private final JsonObject biomeDataObject;
    private final JsonObject advancementDataObject;

    private int itemCount;
    private int mobCount;
    private int biomeCount;
    private int advancementCount;

    private boolean itemsRunning = true;
    private boolean mobsRunning = true;
    private boolean biomesRunning = true;
    private boolean advancementsRunning = true;

    private boolean chainModeEnabled;

    public GameManager(AllItemsPlus allItemsPlus) {
        this.allItemsPlus = allItemsPlus;
        this.dataFile = new File(AllItemsPlus.get().getDataFolder(), "data.json");

        try {
            this.rootObject = JsonParser.parseReader(new FileReader(dataFile)).getAsJsonObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.itemDataObject = rootObject.has("all-items") ? rootObject.getAsJsonObject("all-items") : new JsonObject();
        this.mobDataObject = rootObject.has("all-mobs") ? rootObject.getAsJsonObject("all-mobs") : new JsonObject();
        this.biomeDataObject = rootObject.has("all-biomes") ? rootObject.getAsJsonObject("all-biomes") : new JsonObject();
        this.advancementDataObject = rootObject.has("all-advancements") ? rootObject.getAsJsonObject("all-advancements") : new JsonObject();

        this.chainModeEnabled = allItemsPlus.getConfig().getBoolean("chain-mode", true);

        initLists();
    }

    /**
     * Initialize the lists of available items, mobs, biomes, and advancements
     * that have not been collected, killed, found, or finished yet.
     * <p>
     * This method also reads the chain lists from the configuration and
     * removes the items, mobs, biomes, and advancements that have already
     * been collected, killed, found, or finished from the chain lists.
     */
    public void initLists() {
        Set<Material> availableItems = new HashSet<>();
        Set<EntityType> availableMobs = new HashSet<>();
        Set<Biome> availableBiomes = new HashSet<>();
        Set<Advancement> availableAdvancements = new HashSet<>();

        excludedItems.clear();
        excludedMobs.clear();
        excludedBiomes.clear();
        excludedAdvancements.clear();

        List<String> itemsToExclude = allItemsPlus.getConfig().getStringList("exclude.items");
        for (Material material : Material.values()) {
            if (itemsToExclude.contains(material.name())) {
                excludedItems.add(material);
                continue;
            }
            if (!material.isItem()) {
                excludedItems.add(material);
                continue;
            }
            if (material.name().endsWith("SPAWN_EGG")) {
                excludedItems.add(material);
                continue;
            }
            if (material.name().endsWith("BANNER_PATTERN")) {
                excludedItems.add(material);
                continue;
            }
            if (material.name().endsWith("BANNER")) {
                excludedItems.add(material);
                continue;
            }
            if (material.name().endsWith("CANDLE_CAKE")) {
                excludedItems.add(material);
                continue;
            }
            if (material.name().startsWith("POTTED")) {
                excludedItems.add(material);
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("TORCH")) {
                excludedItems.add(material);
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("SIGN")) {
                excludedItems.add(material);
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("HEAD")) {
                excludedItems.add(material);
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("CORAL")) {
                excludedItems.add(material);
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("BANNER")) {
                excludedItems.add(material);
                continue;
            }
            if (material.name().contains("WALL") && material.name().contains("SKULL")) {
                excludedItems.add(material);
                continue;
            }
            if (material.name().endsWith("STEM")) {
                excludedItems.add(material);
                continue;
            }

            availableItems.add(material);
        }

        List<String> mobsToExclude = allItemsPlus.getConfig().getStringList("exclude.mobs");
        for (EntityType entityType : EntityType.values()) {
            if (mobsToExclude.contains(entityType.name())) {
                excludedMobs.add(entityType);
                continue;
            }
            availableMobs.add(entityType);
        }
        List<String> biomesToExclude = allItemsPlus.getConfig().getStringList("exclude.biomes");
        for (Biome biome : Biome.values()) {
            if (biomesToExclude.contains(biome.name())) {
                excludedBiomes.add(biome);
                continue;
            }
            availableBiomes.add(biome);
        }
        List<String> advancementsToExclude = allItemsPlus.getConfig().getStringList("exclude.advancements");
        for (Advancement advancement : Advancement.values()) {
            if (advancementsToExclude.contains(advancement.name())) {
                excludedAdvancements.add(advancement);
                continue;
            }
            availableAdvancements.add(advancement);
        }

        this.itemCount = availableItems.size();
        this.mobCount = availableMobs.size();
        this.biomeCount = availableBiomes.size();
        this.advancementCount = availableAdvancements.size();

        for (Map.Entry<String, JsonElement> entry : itemDataObject.entrySet()) {
            try {
                collectedItems.add(Material.valueOf(entry.getKey()));
            } catch (IllegalArgumentException ignore) {
            }
        }

        for (Map.Entry<String, JsonElement> entry : mobDataObject.entrySet()) {
            try {
                killedMobs.add(EntityType.valueOf(entry.getKey()));
            } catch (IllegalArgumentException ignore) {
            }
        }

        for (Map.Entry<String, JsonElement> entry : biomeDataObject.entrySet()) {
            try {
                foundBiomes.add(Biome.valueOf(entry.getKey()));
            } catch (IllegalArgumentException ignore) {
            }
        }

        for (Map.Entry<String, JsonElement> entry : advancementDataObject.entrySet()) {
            try {
                finishedAdvancements.add(Advancement.valueOf(entry.getKey()));
            } catch (IllegalArgumentException ignore) {
            }
        }

        List<String> itemChainStringList = allItemsPlus.getConfig().getStringList("chain.items");
        if (itemChainStringList.isEmpty()) {
            List<Material> availableItemsList = new ArrayList<>(availableItems);
            Collections.shuffle(availableItemsList);
            itemChainList.addAll(availableItemsList);
        } else {
            for (String s : itemChainStringList) {
                try {
                    itemChainList.add(Material.valueOf(s));
                } catch (IllegalArgumentException ignore) {
                }
            }
        }

        List<String> mobChainStringList = allItemsPlus.getConfig().getStringList("chain.mobs");
        if (mobChainStringList.isEmpty()) {
            List<EntityType> availableMobsList = new ArrayList<>(availableMobs);
            Collections.shuffle(availableMobsList);
            mobChainList.addAll(availableMobsList);
        } else {
            for (String s : mobChainStringList) {
                try {
                    mobChainList.add(EntityType.valueOf(s));
                } catch (IllegalArgumentException ignore) {
                }
            }
        }

        List<String> biomeChainStringList = allItemsPlus.getConfig().getStringList("chain.biomes");
        if (biomeChainStringList.isEmpty()) {
            List<Biome> availableBiomesList = new ArrayList<>(availableBiomes);
            Collections.shuffle(availableBiomesList);
            biomeChainList.addAll(availableBiomesList);
        } else {
            for (String s : biomeChainStringList) {
                try {
                    biomeChainList.add(Biome.valueOf(s));
                } catch (IllegalArgumentException ignore) {
                }
            }
        }

        List<String> advancementChainStringList = allItemsPlus.getConfig().getStringList("chain.advancements");
        if (advancementChainStringList.isEmpty()) {
            List<Advancement> availableAdvancementsList = new ArrayList<>(availableAdvancements);
            Collections.shuffle(availableAdvancementsList);
            advancementChainList.addAll(availableAdvancementsList);
        } else {
            for (String s : advancementChainStringList) {
                try {
                    advancementChainList.add(Advancement.valueOf(s));
                } catch (IllegalArgumentException ignore) {
                }
            }
        }

        itemChainList.removeAll(collectedItems);
        itemChainList.removeAll(excludedItems);
        mobChainList.removeAll(killedMobs);
        mobChainList.removeAll(excludedMobs);
        biomeChainList.removeAll(foundBiomes);
        biomeChainList.removeAll(excludedBiomes);
        advancementChainList.removeAll(finishedAdvancements);
        advancementChainList.removeAll(excludedAdvancements);
    }

    /**
     * Registers that the given player has finished the given objective.
     *
     * @param objective the objective that was finished
     * @param whoFinished the player who finished the objective
     */
    public void finish(@NotNull Object objective, @NotNull Player whoFinished) {
        JsonObject objectiveObject = new JsonObject();
        objectiveObject.addProperty("playerUUID", whoFinished.getUniqueId().toString());
        objectiveObject.addProperty("time", allItemsPlus.getTimer().getTime());
        switch (objective) {
            case Material material -> {
                collectedItems.add(material);
                itemDataObject.add(material.name(), objectiveObject);
            }
            case EntityType entityType -> {
                killedMobs.add(entityType);
                mobDataObject.add(entityType.name(), objectiveObject);
            }
            case Biome biome -> {
                foundBiomes.add(biome);
                biomeDataObject.add(biome.name(), objectiveObject);
            }
            case Advancement advancement -> {
                finishedAdvancements.add(advancement);
                advancementDataObject.add(advancement.name(), objectiveObject);
            }
            default -> {
            }
        }

        itemChainList.removeAll(collectedItems);
        mobChainList.removeAll(killedMobs);
        biomeChainList.removeAll(foundBiomes);
        advancementChainList.removeAll(finishedAdvancements);
    }

    /**
     * Toggles whether the given objective is excluded from the game.
     * <p>
     * If the objective is currently excluded, it will be removed from the exclusion list and its
     * data will be restored. If it is not currently excluded, it will be added to the exclusion
     * list and its data will be removed. Finally, the lists of items, mobs, biomes, and advancements
     * will be reinitialized.
     * <p>
     * The method returns true if the objective was excluded, false if it was not.
     * <p>
     * Note that the given objective must be a valid objective; if it is not, the method will return
     * false and do nothing.
     *
     * @param objective the objective to toggle exclusion for
     * @return true if the objective was excluded, false if it was not
     */
    public boolean exclude(@NotNull Object objective) {
        boolean excluded = false;

        String path;
        List<String> excludedList;
        JsonObject dataObject;
        Set<?> finishedSet;
        Set<?> chainSet;

        switch (objective) {
            case Material ignored -> {
                path = "exclude.items";
                excludedList = AllItemsPlus.get().getConfig().getStringList(path);
                dataObject = itemDataObject;
                finishedSet = collectedItems;
                chainSet = itemChainList;
            }
            case EntityType ignored -> {
                path = "exclude.mobs";
                excludedList = AllItemsPlus.get().getConfig().getStringList(path);
                dataObject = mobDataObject;
                finishedSet = killedMobs;
                chainSet = mobChainList;
            }
            case Biome ignored -> {
                path = "exclude.biomes";
                excludedList = AllItemsPlus.get().getConfig().getStringList(path);
                dataObject = biomeDataObject;
                finishedSet = foundBiomes;
                chainSet = biomeChainList;
            }
            case Advancement ignored -> {
                path = "exclude.advancements";
                excludedList = AllItemsPlus.get().getConfig().getStringList(path);
                dataObject = advancementDataObject;
                finishedSet = finishedAdvancements;
                chainSet = advancementChainList;
            }
            default -> {
                return false;
            }
        }

        if (excludedList.contains(objective.toString())) {
            excludedList.remove(objective.toString());
        } else {
            excludedList.add(objective.toString());
            dataObject.remove(objective.toString());
            finishedSet.remove(objective);
            chainSet.remove(objective);
            excluded = true;
        }

        AllItemsPlus.get().getConfig().set(path, excludedList);
        AllItemsPlus.get().saveConfig();
        initLists();
        return excluded;
    }

    /**
     * Resets the player's progress to the state it was in when the plugin was first loaded.
     * This clears the data objects and sets, and then reinitializes the lists of items, mobs,
     * biomes, and advancements.
     */
    public void resetProgress() {
        itemDataObject.asMap().clear();
        mobDataObject.asMap().clear();
        biomeDataObject.asMap().clear();
        advancementDataObject.asMap().clear();

        collectedItems.clear();
        killedMobs.clear();
        foundBiomes.clear();
        finishedAdvancements.clear();

        initLists();
    }

    /**
     * Gets the objectives that have not been collected yet.
     *
     * @param values the objectives to check
     * @param <T> the type of the objectives
     * @return a set of objectives that have not been collected yet
     */
    public <T> LinkedHashSet<T> getUncollectedObjectives(T @NotNull [] values) {
        LinkedHashSet<T> uncollectedItems = new LinkedHashSet<>();
        for (T value : values) {
            if (allItemsPlus.getGameManager().isObjectiveExcluded(value)) continue;
            if (allItemsPlus.getGameManager().isObjectiveFinished(value)) continue;
            uncollectedItems.add(value);
        }
        return uncollectedItems;
    }

    /**
     * Saves the current state of the game to the file specified by the {@link #getDataFile()} method.
     * This method is called automatically when the plugin is unloaded.
     *
     * @throws RuntimeException if an I/O exception occurs while writing to the file.
     */
    public void saveData() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            rootObject = new JsonObject();
            rootObject.add("all-items", itemDataObject);
            rootObject.add("all-mobs", mobDataObject);
            rootObject.add("all-biomes", biomeDataObject);
            rootObject.add("all-advancements", advancementDataObject);
            new GsonBuilder().setPrettyPrinting().create().toJson(rootObject, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<String> itemChainStringList = new ArrayList<>();
        List<String> mobChainStringList = new ArrayList<>();
        List<String> biomeChainStringList = new ArrayList<>();
        List<String> advancementChainStringList = new ArrayList<>();

        itemChainList.forEach(material -> itemChainStringList.add(material.name()));
        mobChainList.forEach(entityType -> mobChainStringList.add(entityType.name()));
        biomeChainList.forEach(biome -> biomeChainStringList.add(biome.name()));
        advancementChainList.forEach(advancement -> advancementChainStringList.add(advancement.name()));

        allItemsPlus.getConfig().set("chain.items", itemChainStringList);
        allItemsPlus.getConfig().set("chain.mobs", mobChainStringList);
        allItemsPlus.getConfig().set("chain.biomes", biomeChainStringList);
        allItemsPlus.getConfig().set("chain.advancements", advancementChainStringList);
        allItemsPlus.saveConfig();
    }

    /**
     * Checks if the given objective is unavailable to be completed.
     * This means either the objective is excluded or already finished,
     * or if the chain mode is enabled, the objective is not the first
     * one in the chain list.
     *
     * @param objective the objective to check
     * @return true if the objective is unavailable, false otherwise
     */
    public boolean isObjectiveUnavailable(@NotNull Object objective) {
        if (isObjectiveExcluded(objective) || isObjectiveFinished(objective)) {
            return true;
        }

        if (chainModeEnabled) {
            return switch (objective) {
                case Material material -> !itemChainList.isEmpty() && !itemChainList.getFirst().equals(material);
                case EntityType entityType -> !mobChainList.isEmpty() && !mobChainList.getFirst().equals(entityType);
                case Biome biome -> !biomeChainList.isEmpty() && !biomeChainList.getFirst().equals(biome);
                case Advancement advancement ->
                        !advancementChainList.isEmpty() && !advancementChainList.getFirst().equals(advancement);
                default -> throw new IllegalStateException("Unexpected value: " + objective);
            };
        }

        return false;
    }

    /**
     * Checks if the given objective is excluded from being completed.
     * This means the objective is explicitly excluded from the configuration.
     *
     * @param objective the objective to check
     * @return true if the objective is excluded, false otherwise
     */
    public boolean isObjectiveExcluded(@NotNull Object objective) {
        return switch (objective) {
            case Material material -> excludedItems.contains(material);
            case EntityType entityType -> excludedMobs.contains(entityType);
            case Biome biome -> excludedBiomes.contains(biome);
            case Advancement advancement -> excludedAdvancements.contains(advancement);
            default -> throw new IllegalArgumentException("Not a valid objective!");
        };
    }

    /**
     * Checks if the given objective is finished.
     * This means the objective has been completed in the past.
     *
     * @param objective the objective to check
     * @return true if the objective is finished, false otherwise
     * @throws IllegalArgumentException if the objective is not valid
     */
    public boolean isObjectiveFinished(@NotNull Object objective) throws IllegalArgumentException {
        return switch (objective) {
            case Material material -> itemDataObject.has(material.name());
            case EntityType entityType -> mobDataObject.has(entityType.name());
            case Biome biome -> biomeDataObject.has(biome.name());
            case Advancement advancement -> advancementDataObject.has(advancement.name());
            default -> throw new IllegalArgumentException("Not a valid objective!");
        };
    }

    public File getDataFile() {
        return dataFile;
    }

    public boolean isChainModeEnabled() {
        return chainModeEnabled;
    }

    public void setChainModeEnabled(boolean chainModeEnabled) {
        this.chainModeEnabled = chainModeEnabled;
    }

    public LinkedHashSet<Advancement> getFinishedAdvancements() {
        return finishedAdvancements;
    }

    public LinkedHashSet<Biome> getFoundBiomes() {
        return foundBiomes;
    }

    public LinkedHashSet<EntityType> getKilledMobs() {
        return killedMobs;
    }

    public LinkedHashSet<Material> getCollectedItems() {
        return collectedItems;
    }

    public int getCollectedItemsCount() {
        return itemDataObject.size();
    }

    public int getKilledMobsCount() {
        return mobDataObject.size();
    }

    public int getFoundBiomesCount() {
        return biomeDataObject.size();
    }

    public int getFinishedAdvancementsCount() {
        return advancementDataObject.size();
    }

    public JsonObject getItemDataObject() {
        return itemDataObject;
    }

    public JsonObject getMobDataObject() {
        return mobDataObject;
    }

    public JsonObject getBiomeDataObject() {
        return biomeDataObject;
    }

    public JsonObject getAdvancementDataObject() {
        return advancementDataObject;
    }

    public int getTotalItemCount() {
        return itemCount;
    }

    public int getTotalMobCount() {
        return mobCount;
    }

    public int getTotalBiomeCount() {
        return biomeCount;
    }

    public int getTotalAdvancementCount() {
        return advancementCount;
    }

    public boolean isItemsRunning() {
        return itemsRunning;
    }

    public void setItemsRunning(boolean itemsRunning) {
        this.itemsRunning = itemsRunning;
    }

    public boolean isMobsRunning() {
        return mobsRunning;
    }

    public void setMobsRunning(boolean mobsRunning) {
        this.mobsRunning = mobsRunning;
    }

    public boolean isBiomesRunning() {
        return biomesRunning;
    }

    public void setBiomesRunning(boolean biomesRunning) {
        this.biomesRunning = biomesRunning;
    }

    public boolean isAdvancementsRunning() {
        return advancementsRunning;
    }

    public void setAdvancementsRunning(boolean advancementsRunning) {
        this.advancementsRunning = advancementsRunning;
    }

    public LinkedHashSet<Advancement> getAdvancementChainList() {
        return advancementChainList;
    }

    public LinkedHashSet<Biome> getBiomeChainList() {
        return biomeChainList;
    }

    public LinkedHashSet<EntityType> getMobChainList() {
        return mobChainList;
    }

    public LinkedHashSet<Material> getItemChainList() {
        return itemChainList;
    }
}
