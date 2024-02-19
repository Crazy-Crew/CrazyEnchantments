package com.badbones69.crazyenchantments.paper.commands;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CETab implements TabCompleter {

    private final CrazyEnchantments plugin = CrazyEnchantments.get();

    private final Starter starter = this.plugin.getStarter();

    private final Methods methods = this.starter.getMethods();

    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();
    
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
            if (hasPermission(sender, "updateenchants")) completions.add("updateEnchants");
            if (hasPermission(sender, "give")) completions.add("give");
            if (hasPermission(sender, "bottle")) completions.add("bottle");
            if (hasPermission(sender, "slotcrystal")) completions.add("slotcrystal");


            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        } else if (args.length == 2) { // /ce arg0
            switch (args[0].toLowerCase()) {
                case "info", "add", "book" -> {
                    for (CEnchantment enchantment : this.crazyManager.getRegisteredEnchantments()) {
                        try {
                            completions.add(enchantment.getCustomName().replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "").replace(" ", "_"));
                        } catch (NullPointerException ignore) {}
                    }

                    Arrays.asList(Enchantment.values()).forEach(enchantment -> completions.add(enchantment.getKey().getKey()));
                }
                case "remove" ->
                        this.enchantmentBookSettings.getEnchantments(((Player) sender).getInventory().getItemInMainHand())
                            .forEach((a,b) -> completions.add(a.getCustomName().replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "")));

                case "spawn" -> {
                    for (CEnchantment enchantment : this.crazyManager.getRegisteredEnchantments()) {
                        try {
                            completions.add(enchantment.getCustomName().replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", ""));
                        } catch (NullPointerException ignore) {}
                    }

                    for (Category category : this.enchantmentBookSettings.getCategories()) {
                        try {
                            completions.add(category.getName());
                        } catch (NullPointerException ignore) {}
                    }
                }

                case "scroll" -> {
                    completions.add("black");
                    completions.add("white");
                    completions.add("transmog");
                }

                case "crystal", "scrambler", "slotcrystal" -> {
                    completions.add("1");
                    completions.add("32");
                    completions.add("64");
                }

                case "dust" -> {
                    for (Dust dust : Dust.values()) {
                        completions.addAll(dust.getKnownNames());
                    }
                }

                case "lostbook" -> {
                    for (Category category : this.enchantmentBookSettings.getCategories()) {
                        try {
                            completions.add(category.getName());
                        } catch (NullPointerException ignore) {}
                    }
                }
                case "give", "bottle" -> this.plugin.getServer().getOnlinePlayers().forEach(player -> completions.add(player.getName()));
            }

            return StringUtil.copyPartialMatches(args[1], completions, new ArrayList<>());
        } else if (args.length == 3) {// /ce arg0 arg1
            CEnchantment ceEnchantment;
            switch (args[0].toLowerCase()) {
                case "book" -> {
                    ceEnchantment = this.crazyManager.getEnchantmentFromName(args[1]);

                    if (ceEnchantment != null) for (int amount = 1; amount <= ceEnchantment.getMaxLevel(); amount++)
                        completions.add(String.valueOf(amount));
                }
                case "add" -> {
                    ceEnchantment = this.crazyManager.getEnchantmentFromName(args[1]);
                    Enchantment vanillaEnchantment = this.methods.getEnchantment(args[1]);

                    if (vanillaEnchantment != null || ceEnchantment != null) {
                        int maxLevel = vanillaEnchantment != null ? vanillaEnchantment.getMaxLevel() : ceEnchantment.getMaxLevel();
                        for (int amount = 1; amount <= maxLevel; amount++) completions.add(String.valueOf(amount));
                    }
                }
                case "spawn" -> {
                    completions.add("Level:");
                    completions.add("World:");
                    completions.add("X:");
                    completions.add("Y:");
                    completions.add("Z:");
                }
                case "scroll", "dust", "lostbook", "bottle" -> {
                    completions.add("1");
                    completions.add("32");
                    completions.add("64");
                }
                case "crystal", "scrambler", "slotcrystal" -> this.plugin.getServer().getOnlinePlayers().forEach(player -> completions.add(player.getName()));
                case "give" ->
                    completions.add("Item:DIAMOND_HELMET, Amount:1, Name:&6&lHat, Protection:4, Overload:1-5, Hulk:2-5, Lore:&aLine 1.,&aLine 2.");
            }

            return StringUtil.copyPartialMatches(args[2], completions, new ArrayList<>());
        } else if (args.length == 4) { // /ce arg0 arg1 arg2
            switch (args[0].toLowerCase()) {
                case "spawn" -> {
                    completions.add("Level:");
                    completions.add("World:");
                    completions.add("X:");
                    completions.add("Y:");
                    completions.add("Z:");
                }

                case "scroll", "dust", "lostbook" -> this.plugin.getServer().getOnlinePlayers().forEach(player -> completions.add(player.getName()));
                case "bottle" -> {
                    completions.add("1");
                    completions.add("32");
                    completions.add("64");
                }
                default -> {
                    return StringUtil.copyPartialMatches(args[3], completions, new ArrayList<>());
                }
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