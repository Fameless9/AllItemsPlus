package net.fameless.allItemsPlus.util;

import net.fameless.allItemsPlus.AllItemsPlus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

public class UpdateChecker {
    private final int resourceId;

    public UpdateChecker(int resourceId) {
        this.resourceId = resourceId;
        checkForUpdates();
    }

    public void checkForUpdates() {
        try {
            URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            String latestVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

            String currentVersion = AllItemsPlus.get().getDescription().getVersion();
            if (latestVersion != null && !latestVersion.equalsIgnoreCase(currentVersion)) {
                AllItemsPlus.get().getLogger().info("A new update is available! Version " + latestVersion + " can be downloaded from the SpigotMC website: https://www.spigotmc.org/resources/119891");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        AllItemsPlus.get().getLogger().info("A new update is available! Version " + latestVersion + " can be downloaded from the SpigotMC website: https://www.spigotmc.org/resources/119891");
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.isOp()) {
                                player.sendMessage(ChatColor.RED + "All Items Plus is not running on the latest version. " +
                                        "Please keep All Items Plus up to date to avoid bugs: " + ChatColor.AQUA + ChatColor.UNDERLINE +
                                        "https://www.spigotmc.org/resources/119891"
                                );
                            }
                        }
                    }
                }.runTaskTimer(AllItemsPlus.get(), 600 * 20, 3600 * 20);
            }
        } catch (IOException e) {
            AllItemsPlus.get().getLogger().log(Level.WARNING, "Failed to check for updates: " + e.getMessage(), e);
        }
    }
}
