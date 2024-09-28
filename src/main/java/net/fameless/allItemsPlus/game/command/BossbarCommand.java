package net.fameless.allItemsPlus.game.command;

import net.fameless.allItemsPlus.AllItemsPlus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BossbarCommand implements CommandExecutor {

    private final AllItemsPlus allItemsPlus;

    public BossbarCommand(AllItemsPlus allItemsPlus) {
        this.allItemsPlus = allItemsPlus;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player) {
            allItemsPlus.getBossbarManager().toggleHidden(player);
        }
        return false;
    }
}
