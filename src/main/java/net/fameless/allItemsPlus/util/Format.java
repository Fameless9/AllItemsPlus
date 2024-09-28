package net.fameless.allItemsPlus.util;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public final class Format {

    private Format() {}

    /**
     * Converts a given time in seconds to a human-readable string.
     *
     * @param time The time in seconds to convert.
     * @return A string in the format "xd yh zm ws" representing the given time.
     */
    public static String formatTime(int time) {
        int days = time / 86400;
        int hours = time / 3600 % 24;
        int minutes = time / 60 % 60;
        int seconds = time % 60;

        StringBuilder message = new StringBuilder();

        if (days >= 1) {
            message.append(days).append("d ");
        }
        if (hours >= 1) {
            message.append(hours).append("h ");
        }
        if (minutes >= 1) {
            message.append(minutes).append("m ");
        }
        if (seconds >= 1) {
            message.append(seconds).append("s ");
        }
        if (time == 0) {
            message.append("0s");
        }
        return String.valueOf(message);
    }

    /**
     * Formats a given string by replacing underscores with spaces, splitting the string into words, capitalizing the first letter of each word, and joining the words back together with spaces.
     *
     * @param input The string to format.
     * @return A formatted string.
     */
    public static @NotNull String formatName(String input) {
        input = input.replace("_", " ");
        String[] words = input.split(" ");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            String formattedWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
            formatted.append(formattedWord).append(" ");
        }
        return formatted.toString().trim();
    }

    public static @NotNull String applyMiniMessageFormat(String input) {
        return LegacyComponentSerializer.legacySection().serialize(MiniMessage.miniMessage().deserialize(input));
    }
}
