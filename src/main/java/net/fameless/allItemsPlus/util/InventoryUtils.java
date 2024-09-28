package net.fameless.allItemsPlus.util;

import net.fameless.allItemsPlus.AllItemsPlus;
import org.bukkit.NamespacedKey;

import java.util.Set;

public final class InventoryUtils {

    public static final NamespacedKey PAGE_KEY = new NamespacedKey(AllItemsPlus.get(), "allItemsPlus.pageKey");
    public static final NamespacedKey PAGE_TYPE_KEY = new NamespacedKey(AllItemsPlus.get(), "allItemsPlus.pageTypeKey");

    private InventoryUtils() {}

    /**
     * Determines if a given page number is valid for a set of items.
     * @param set the set of items to check
     * @param page the page number to check
     * @param POSSIBLE_ENTRIES_PER_PAGE the number of items per page
     * @throws IllegalArgumentException if the page number is negative
     * @return true if the page number is valid, false otherwise
     */
    public static boolean isPageValid(Set<?> set, int page, final int POSSIBLE_ENTRIES_PER_PAGE) throws IllegalArgumentException {
        if (page < 0) return false;
        return set.size() > page * POSSIBLE_ENTRIES_PER_PAGE;
    }
}
