package net.fameless.allItemsPlus.util;

import net.fameless.allItemsPlus.language.Lang;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public final class ItemUtils {

    private ItemUtils() {}

    /**
     * Builds an {@link ItemStack} with the given name, lore, and flags applied.
     * @param itemStack The item stack to build upon.
     * @param name The name to give to the item stack.
     * @param applyFlags If true, will apply all {@link ItemFlag}s to the item stack.
     * @param lore The lore to give to the item stack. If empty, no lore is set.
     * @return The built item stack.
     */
    @Contract("_, _, _, _ -> param1")
    public static @NotNull ItemStack buildItem(@NotNull ItemStack itemStack, String name, boolean applyFlags, String... lore) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        meta.setDisplayName(name);

        if (applyFlags) Arrays.stream(ItemFlag.values()).forEach(meta::addItemFlags);

        meta.setLore(List.of(lore));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Builds an {@link ItemStack} with the given name, lore, and flags applied.
     * @param itemStack The item stack to build upon.
     * @param name The name to give to the item stack.
     * @param applyFlags If true, will apply all {@link ItemFlag}s to the item stack.
     * @param lore The lore to give to the item stack. If empty, no lore is set.
     * @return The built item stack.
     */
    @Contract("_, _, _, _ -> param1")
    public static @NotNull ItemStack buildItem(@NotNull ItemStack itemStack, String name, boolean applyFlags, List<String> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        meta.setDisplayName(name);

        if (applyFlags) Arrays.stream(ItemFlag.values()).forEach(meta::addItemFlags);

        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Gets a filler item to use in {@link Inventory} slots. This item is a black stained glass pane with a single space as its name.
     * @return A black stained glass pane with a single space as its name.
     */
    public static @NotNull ItemStack getFillerItem() {
        ItemStack stack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            stack.setItemMeta(meta);
        }
        return stack;
    }

    /**
     * Adds persistent data to the given item stack.
     * @param stack The item stack to add data to.
     * @param key The key to store the data under.
     * @param dataType The type of data to store.
     * @param data The data to store.
     */
    public static void addData(@NotNull ItemStack stack, NamespacedKey key, PersistentDataType dataType, Object data) {
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(key, dataType, data);
            stack.setItemMeta(meta);
        }
    }

    /**
     * Generates an item stack with a left-pointing arrow as its display icon, and the localized string "page-left" as its display name.
     * @return An item stack with a left-pointing arrow as its display icon and the localized string "page-left" as its display name.
     */
    public static @NotNull ItemStack pageLeft() {
        ItemStack stack = Skull.ARROW_LEFT.asItemStack();
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Lang.getCaption("page-left"));
            stack.setItemMeta(meta);
        }
        return stack;
    }

    /**
     * Generates an item stack with a right-pointing arrow as its display icon, and the localized string "page-right" as its display name.
     * @return An item stack with a right-pointing arrow as its display icon and the localized string "page-right" as its display name.
     */
    public static @NotNull ItemStack pageRight() {
        ItemStack stack = Skull.ARROW_RIGHT.asItemStack();
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Lang.getCaption("page-right"));
            stack.setItemMeta(meta);
        }
        return stack;
    }
}
