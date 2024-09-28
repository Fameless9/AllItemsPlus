package net.fameless.allItemsPlus.game.command;

import net.fameless.allItemsPlus.AllItemsPlus;
import net.fameless.allItemsPlus.language.Lang;
import net.fameless.allItemsPlus.util.Advancement;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExcludeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length < 1) {
            sender.sendMessage(Lang.getCaption("exclude-command-usage"));
            return false;
        }
        boolean excluded;
        try {
            Material toExclude = Material.valueOf(args[0]);
            excluded = AllItemsPlus.get().getGameManager().exclude(toExclude);
        } catch (IllegalArgumentException e) {
            try {
                EntityType toExclude = EntityType.valueOf(args[0]);
                excluded = AllItemsPlus.get().getGameManager().exclude(toExclude);
            } catch (IllegalArgumentException f) {
                try {
                    Biome toExclude = Biome.valueOf(args[0]);
                    excluded = AllItemsPlus.get().getGameManager().exclude(toExclude);
                } catch (IllegalArgumentException g) {
                    try {
                        Advancement toExclude = Advancement.valueOf(args[0]);
                        excluded = AllItemsPlus.get().getGameManager().exclude(toExclude);
                    } catch (IllegalArgumentException h) {
                        sender.sendMessage(Lang.getCaption("exclude-command-invalid-arg"));
                        return false;
                    }
                }
            }
        }
        if (excluded) {
            sender.sendMessage(Lang.getCaption("exclude-command-excluded"));
        } else {
            sender.sendMessage(Lang.getCaption("exclude-command-added"));
        }
        return false;
    }

    @Nullable @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            Set<String> suggestions = new HashSet<>();

            for (Material material : Material.values()) {
                suggestions.add(material.name());
            }
            for (EntityType entityType : EntityType.values()) {
                suggestions.add(entityType.name());
            }
            for (Biome biome : Biome.values()) {
                suggestions.add(biome.name());
            }
            for (Advancement advancement : Advancement.values()) {
                suggestions.add(advancement.name());
            }

            return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
        }

        return List.of();
    }
}
