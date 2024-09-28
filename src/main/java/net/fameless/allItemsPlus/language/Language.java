package net.fameless.allItemsPlus.language;

import org.bukkit.ChatColor;

public enum Language {
    ENGLISH("en", ChatColor.GREEN + "Language has been updated to english."),
    GERMAN("de", ChatColor.GREEN + "Die Sprache wurde auf deutsch gesetzt.");

    private final String identifier;
    private final String updateMessage;

    Language(String identifier, String updateMessage) {
        this.identifier = identifier;
        this.updateMessage = updateMessage;
    }

    public String getUpdateMessage() {
        return updateMessage;
    }

    public String getIdentifier() {
        return identifier;
    }
}
