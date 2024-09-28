package net.fameless.allItemsPlus.language;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fameless.allItemsPlus.AllItemsPlus;
import net.fameless.allItemsPlus.util.Format;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public final class Lang {

    private static JsonObject langObject = null;
    private static Language language;

    private Lang() {}

    /**
     * Loads the language file and sets the language.
     *
     * @param allItems the main plugin class
     * @throws RuntimeException if the language file is not found or the language is not supported
     */
    public static void loadLanguage(@NotNull AllItemsPlus allItems) throws RuntimeException {
        try {
            allItems.reloadConfig();
            String lang = allItems.getConfig().getString("lang", "en");

            for (Language languageEnumEntry : Language.values()) {
                if (languageEnumEntry.getIdentifier().equals(lang)) {
                    language = languageEnumEntry;
                    break;
                }
            }
            if (language == null) {
                throw new IllegalArgumentException("Unsupported language: " + lang);
            }

            File jsonFile = new File(allItems.getDataFolder(), "lang_xx.json".replace("xx", lang));
            langObject = JsonParser.parseReader(new FileReader(jsonFile)).getAsJsonObject();

            allItems.getLogger().fine("Successfully loaded language: " + lang);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No file was found for language: " + language.getIdentifier() + e);
        }
    }

    /**
     * Gets the caption from the language file.
     *
     * @param path the path to the caption
     * @return the caption
     */
    public static @NotNull String getCaption(String path) {
        String prefix = langObject.get("prefix").getAsString();
        String message = langObject.get(path).getAsString();
        message = message.replace("{prefix}", prefix)
                .replace("{timer.time}", Format.formatTime(AllItemsPlus.get().getTimer().getTime()));
        message = Format.applyMiniMessageFormat(message);
        return message;
    }

    public static Language getLanguage() {
        return language;
    }

    public static JsonObject getLangObject() {
        return langObject;
    }
}
