package net.fameless.allItemsPlus.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public final class SetUtils {

    private SetUtils() {}

    /**
     * Retrieves the element at the specified index from the given set.
     * This method behaves like {@link List#get(int)} but takes a {@link LinkedHashSet} as argument.
     * Note that this method creates a temporary list copy of the given set, so it may be inefficient for large sets.
     *
     * @param set the set of elements to retrieve from
     * @param index the index of the element to retrieve
     * @return the element at the specified index
     * @throws IndexOutOfBoundsException if the index is out of the set's bounds
     */
    public static <T> T getElementAt(LinkedHashSet<T> set, int index) {
        List<T> list = new ArrayList<>(set);
        if (index >= 0 && index < list.size()) {
            return list.get(index);
        } else {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + list.size());
        }
    }
}
