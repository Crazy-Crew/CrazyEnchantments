package com.badbones69.crazyenchantments.commands;

import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.enums.Dust;
import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.api.objects.Category;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CETab implements TabCompleter {
    
    private final CrazyManager crazyManager = CrazyManager.getInstance();
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) { // /ce
            if (hasPermission(sender, "access")) completions.add("help");
            if (hasPermission(sender, "debug")) completions.add("debug");
            if (hasPermission(sender, "limit")) completions.add("limit");
            if (hasPermission(sender, "info")) completions.add("info");
            if (hasPermission(sender, "reload")) completions.add("reload");
            if (hasPermission(sender, "remove")) completions.add("remove");
            if (hasPermission(sender, "add")) completions.add("add");
            if (hasPermission(sender, "spawn")) completions.add("spawn");
            if (hasPermission(sender, "scroll")) completions.add("scroll");
            if (hasPermission(sender, "crystal")) completions.add("crystal");
            if (hasPermission(sender, "scrambler")) completions.add("scrambler");
            if (hasPermission(sender, "dust")) completions.add("dust");
            if (hasPermission(sender, "book")) completions.add("book");
            if (hasPermission(sender, "lostbook")) completions.add("lostbook");

            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        } else if (args.length == 2) { // /ce arg0
            switch (args[0].toLowerCase()) {
                case "info":
                case "remove":
                case "add":
                case "book":
                    for (CEnchantment enchantment : crazyManager.getRegisteredEnchantments()) {
                        try {
                            completions.add(enchantment.getCustomName().replace(" ", "_"));
                        } catch (NullPointerException ignore) {}
                    }

                    Arrays.asList(Enchantment.values()).forEach(enchantment -> completions.add(enchantment.getKey().getKey()));
                    break;
                case "spawn":
                    for (CEnchantment enchantment : crazyManager.getRegisteredEnchantments()) {
                        try {
                            completions.add(enchantment.getCustomName().replace(" ", "_"));
                        } catch (NullPointerException ignore) {}
                    }

                    for (Category category : crazyManager.getCategories()) {
                        try {
                            completions.add(category.getName());
                        } catch (NullPointerException ignore) {}
                    }

                    break;
                case "scroll":
                    completions.add("black");
                    completions.add("white");
                    completions.add("transmog");
                    break;
                case "crystal":
                case "scrambler":
                    completions.add("1");
                    completions.add("32");
                    completions.add("64");
                    break;
                case "dust":
                    for (Dust dust : Dust.values()) {
                        completions.addAll(dust.getKnownNames());
                    }

                    break;
                case "lostbook":
                    for (Category category : crazyManager.getCategories()) {
                        try {
                            completions.add(category.getName());
                        } catch (NullPointerException ignore) {}
                    }

                    break;
            }
            return StringUtil.copyPartialMatches(args[1], completions, new ArrayList<>());
        } else if (args.length == 3) {// /ce arg0 arg1
            switch (args[0].toLowerCase()) {
                case "book":
                    CEnchantment ceEnchantment = crazyManager.getEnchantmentFromName(args[1]);

                    if (ceEnchantment != null) {
                        for (int i = 1; i <= ceEnchantment.getMaxLevel(); i++) completions.add(i + "");
                    }

                    break;
                case "add":
                    ceEnchantment = crazyManager.getEnchantmentFromName(args[1]);
                    Enchantment vanillaEnchantment = Methods.getEnchantment(args[1]);

                    if (vanillaEnchantment != null || ceEnchantment != null) {
                        int maxLevel = vanillaEnchantment != null ? vanillaEnchantment.getMaxLevel() : ceEnchantment.getMaxLevel();
                        for (int i = 1; i <= maxLevel; i++) completions.add(i + "");
                    }

                    break;
                case "spawn":
                    completions.add("Level:");
                    completions.add("World:");
                    completions.add("X:");
                    completions.add("Y:");
                    completions.add("Z:");
                    break;
                case "scroll":
                case "dust":
                case "lostbook":
                    completions.add("1");
                    completions.add("32");
                    completions.add("64");
                    break;
                case "crystal":
                case "scrambler":
                    crazyManager.getPlugin().getServer().getOnlinePlayers().forEach(player -> completions.add(player.getName()));
                    break;
            }
            return StringUtil.copyPartialMatches(args[2], completions, new ArrayList<>());
        } else if (args.length == 4) { // /ce arg0 arg1 arg2
            switch (args[0].toLowerCase()) {
                case "spawn":
                    completions.add("Level:");
                    completions.add("World:");
                    completions.add("X:");
                    completions.add("Y:");
                    completions.add("Z:");
                    break;
                case "scroll":
                case "dust":
                case "lostbook":
                    crazyManager.getPlugin().getServer().getOnlinePlayers().forEach(player -> completions.add(player.getName()));
                    break;
                default:
                    return StringUtil.copyPartialMatches(args[3], completions, new ArrayList<>());
            }
        } else if (args.length == 5) { // /ce arg0 arg1 arg2
            switch (args[0].toLowerCase()) {
                case "spawn" -> {
                    completions.add("Level:");
                    completions.add("World:");
                    completions.add("X:");
                    completions.add("Y:");
                    completions.add("Z:");
                }

                case "dust" -> {
                    completions.add("1");
                    completions.add("25");
                    completions.add("50");
                    completions.add("75");
                    completions.add("100");
                }
            }
            return StringUtil.copyPartialMatches(args[4], completions, new ArrayList<>());
        } else { // /ce arg0 arg1 arg2 args3
            if (args[0].equalsIgnoreCase("spawn")) {
                completions.add("Level:");
                completions.add("World:");
                completions.add("X:");
                completions.add("Y:");
                completions.add("Z:");
            }

            return StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<>());
        }

        return completions;
    }
    
    private boolean hasPermission(CommandSender sender, String node) {
        return sender.hasPermission("crazyenchantments." + node) || sender.hasPermission("crazyenchantments.admin");
    }
}