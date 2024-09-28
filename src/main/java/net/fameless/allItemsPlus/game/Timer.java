package net.fameless.allItemsPlus.game;

import net.fameless.allItemsPlus.AllItemsPlus;
import net.fameless.allItemsPlus.language.Lang;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Timer implements CommandExecutor, TabCompleter {

    private final AllItemsPlus allItemsPlus;
    private int time;
    private boolean running;

    public Timer(@NotNull AllItemsPlus allItemsPlus, boolean running, int time) throws IllegalArgumentException {
        if (allItemsPlus.getTimer() != null) {
            throw new IllegalArgumentException("You may not create another Timer instance!");
        }
        this.allItemsPlus = allItemsPlus;
        this.running = running;
        this.time = time;

        runTimerTask();
        runActionbarTask();
    }

    public int getTime() {
        return time;
    }

    public boolean isNotRunning() {
        return !running;
    }

    private void runActionbarTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                sendActionbar();
            }
        }.runTaskTimer(allItemsPlus, 0, 3);
    }

    /**
     * Starts a repeating task that increments the timer by 1 every second.
     * If the timer is not running, the task will not increment the timer.
     */
    private void runTimerTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (running) {
                    time++;
                }
            }
        }.runTaskTimer(allItemsPlus, 0, 20);
    }

    /**
     * Sends the current timer state to all online players via action bar.
     */
    private void sendActionbar() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (running) {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Lang.getCaption("timer-running")));
            } else {
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Lang.getCaption("timer-paused")));
            }
        }
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length < 1) {
            sender.sendMessage(Lang.getCaption("timer-usage"));
            return false;
        }
        switch (args[0]) {
            case "toggle" -> running = !running;
            case "set" -> {
                if (args.length < 2) {
                    sender.sendMessage(Lang.getCaption("timer-usage"));
                    return false;
                }
                try {
                    this.time = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("time-not-a-number");
                    return false;
                }
            }
        }
        return true;
    }

    @Nullable @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], List.of("set", "toggle"), new ArrayList<>());
        }
        if (args.length == 2 && args[1].isEmpty()) {
            return List.of("<time>");
        }
        return List.of();
    }
}
