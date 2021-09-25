package me.badbones69.crazyenchantments.commands;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.Category;
import me.badbones69.crazyenchantments.api.objects.EnchantmentVanillaOrCE;
import me.badbones69.crazyenchantments.multisupport.Version;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;

import java.util.*;
import java.util.stream.Collectors;

public class CETabACF {
    public static final String GENERIC_VARIABLE_COMPLETION_NO_AT = "genericVariable";
    private static final String ENCHANTMENT_LEVEL_COMPLETION_NO_AT = "ceLevel";
    private static final String VANILLA_ENCHANTMENTS_NO_AT = "vanillaEnchantment";
    private static final String ENCHANTMENTS_NO_AT = "ceEnchantments";
    private static final String ENCHANTMENTS_CATEGORY_NO_AT = "ceCategory";
    public static final String VANILLA_ENCHANTMENTS_COMPLETION = "@" + VANILLA_ENCHANTMENTS_NO_AT;
    public static final String ENCHANTMENTS_COMPLETION = "@" + ENCHANTMENTS_NO_AT;
    public static final String ENCHANTMENTS_CATEGORY_COMPLETION = "@" + ENCHANTMENTS_CATEGORY_NO_AT;
    public static final String GENERIC_VARIABLE_COMPLETION = "@" + GENERIC_VARIABLE_COMPLETION_NO_AT;
    public static final String ENCHANTMENT_LEVEL_COMPLETION = "@" + ENCHANTMENT_LEVEL_COMPLETION_NO_AT;
    public static final String ENCHANTMENTS_ALL_COMPLETION = VANILLA_ENCHANTMENTS_COMPLETION + "|" + ENCHANTMENTS_COMPLETION;

    public CETabACF() {
        PaperCommandManager commandManager = Main.getInstance().getCommandManager();
        commandManager.getCommandContexts().registerContext(EnchantmentVanillaOrCE.class, this::getEnchantment);
        CommandCompletions<BukkitCommandCompletionContext> commandCompletions = commandManager.getCommandCompletions();
        commandCompletions.registerAsyncCompletion(GENERIC_VARIABLE_COMPLETION_NO_AT, this::genericVariableCompletion);
        commandCompletions.registerCompletion(ENCHANTMENT_LEVEL_COMPLETION, this::enchantmentLevel);
        commandCompletions.registerCompletion(VANILLA_ENCHANTMENTS_NO_AT, this::vanillaEnchantments);
        commandCompletions.registerCompletion(ENCHANTMENTS_NO_AT, this::enchantments);
        commandCompletions.registerCompletion(ENCHANTMENTS_CATEGORY_NO_AT, this::categories);
    }

    private EnchantmentVanillaOrCE getEnchantment(BukkitCommandExecutionContext context) {
        Object alreadyResolved = context.getResolvedArg(EnchantmentVanillaOrCE.class);
        if (alreadyResolved instanceof EnchantmentVanillaOrCE) {
            return (EnchantmentVanillaOrCE) alreadyResolved;
        }
        String enchantment = context.popFirstArg();
        Enchantment vanillaEnchantment = Methods.getEnchantment(enchantment);
        if (vanillaEnchantment != null) return new EnchantmentVanillaOrCE(vanillaEnchantment);
        CEnchantment ceEnchantment = CrazyEnchantments.getInstance().getEnchantmentFromName(enchantment);
        if (ceEnchantment != null) return new EnchantmentVanillaOrCE(ceEnchantment);
        return null;
    }

    private Collection<String> enchantmentLevel(BukkitCommandCompletionContext context) {
        EnchantmentVanillaOrCE enchantment = context.getContextValue(EnchantmentVanillaOrCE.class);
        if (enchantment == null) return Collections.singleton("1");
        int maxLevel;
        if (enchantment.isCEnchantment()) maxLevel = enchantment.getCEnchantment().getMaxLevel();
        else if (enchantment.isVanilla()) maxLevel = enchantment.getVanilla().getMaxLevel();
        else maxLevel = 1;
        try {
            Collection<String> completions = new ArrayList<>();
            String input = context.getInput();
            int lastVal = input.isEmpty() ? 0 : Integer.parseInt(input);
            for (int i = 0; i < 10; i++) {
                if (lastVal == 0 && i == 0) continue;
                if (lastVal * 10 + i <= maxLevel) {
                    completions.add(String.valueOf(lastVal * 10 + i));
                }
            }
            return completions;
        } catch (NumberFormatException e) {
            return Collections.emptyList();
        }
    }

    private Collection<String> genericVariableCompletion(BukkitCommandCompletionContext context) {
        String arg = context.getInput();
        System.out.println(arg);
        Collection<String> completions = context.getConfigs()
                .keySet()
                .stream()
                .map(s -> Methods.uppercaseFirst(s) + ":").collect(Collectors.toList());
        for (Map.Entry<String, String> configEntry : context.getConfigs().entrySet()) {
            String config = configEntry.getKey();
            if (config.toLowerCase().startsWith(arg)) {
                String completion = arg + Methods.uppercaseFirst(config).substring(arg.length()) + ":";
                switch (configEntry.getValue()) {
                    case "world":
                        for (World world : Bukkit.getWorlds()) {
                            completions.add(completion + world.getName());
                        }
                        break;
                    case "number":
                        completions.add(completion + "1");
                        break;
                }
                completions.add(completion);
            }
        }
        for (Map.Entry<String, String> configEntry : context.getConfigs().entrySet()) {
            if (arg.toLowerCase().startsWith(configEntry.getKey() + ":")) {
                addSpawnCompletion(completions, arg, configEntry);
                break;
            }
        }
        return completions;
    }

    private void addSpawnCompletion(Collection<String> completions, String arg, Map.Entry<String, String> configEntry) {
        String configValue = configEntry.getValue();
        String[] lastSectionSplit = arg.split(":", 2);
        String currentArg;
        if (arg.endsWith(":")) {
            currentArg = "";
        } else if (lastSectionSplit.length == 2) {
            // we're on the later part of the entry
            currentArg = lastSectionSplit[1];
        } else {
            currentArg = lastSectionSplit[0];
            completions.add(currentArg + ":");
        }
        List<String> addCompletions = new ArrayList<>();
        switch (configValue.toLowerCase()) {
            case "world":
                addCompletions.addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
                break;
            case "number":
                for (int i = 0; i < 10; i++) {
                    addCompletions.add(currentArg + i);
                }
                break;
        }
        for (String addCompletion : addCompletions) {
            completions.add(lastSectionSplit[0] + ":" + addCompletion);
        }
    }

    private Collection<String> categories(BukkitCommandCompletionContext bukkitCommandCompletionContext) {
        return CrazyEnchantments.getInstance().getCategories()
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }

    private Collection<String> vanillaEnchantments(BukkitCommandCompletionContext bukkitCommandCompletionContext) {
        List<String> completions = new ArrayList<>();
        if (Version.isNewer(Version.v1_12_R1)) {
            for (Enchantment enchantment : Enchantment.values()) {
                completions.add(enchantment.getKey().getKey());
            }
        } else {
            for (Enchantment enchantment : Enchantment.values()) {
                completions.add(enchantment.getName().replace(" ", "_"));
            }
        }
        return completions;
    }

    private Collection<String> enchantments(BukkitCommandCompletionContext context) {
        List<CEnchantment> enchantments = CrazyEnchantments.getInstance().getRegisteredEnchantments();
        return enchantments.stream()
                .map(enchantment -> enchantment.getCustomName().replace(" ", "_"))
                .collect(Collectors.toList());
    }
}
