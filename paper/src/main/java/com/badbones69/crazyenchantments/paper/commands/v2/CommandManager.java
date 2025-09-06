package com.badbones69.crazyenchantments.paper.commands.v2;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.commands.v2.features.admin.*;
import com.badbones69.crazyenchantments.paper.commands.v2.features.admin.validation.enums.ValidationType;
import com.badbones69.crazyenchantments.paper.commands.v2.features.base.CommandHelp;
import com.badbones69.crazyenchantments.paper.commands.v2.features.base.CommandLimit;
import com.badbones69.crazyenchantments.paper.commands.v2.features.base.standalone.CommandBlackSmith;
import com.badbones69.crazyenchantments.paper.commands.v2.features.base.standalone.CommandTinker;
import com.badbones69.crazyenchantments.paper.commands.v2.relations.ArgumentRelations;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.ryderbelserion.fusion.paper.builders.PlayerBuilder;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {

    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);
    private static final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();
    private static final Methods methods = plugin.getStarter().getMethods();
    private static final EnchantmentBookSettings settings = plugin.getStarter().getEnchantmentBookSettings();
    private static final Server server = plugin.getServer();

    private static final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(plugin);

    /**
     * Loads commands.
     */
    public static void load() {
        new ArgumentRelations().build();

        commandManager.registerArgument(GKitz.class, (sender, argument) -> crazyManager.getGKitFromName(argument));
        commandManager.registerArgument(Dust.class, (sender, argument) -> Dust.valueOf(argument));
        commandManager.registerArgument(Category.class, (sender, argument) -> settings.getCategory(argument));
        commandManager.registerArgument(Scrolls.class, (sender, argument) -> Scrolls.getFromName(argument));
        commandManager.registerArgument(CEnchantment.class, (sender, argument) -> crazyManager.getEnchantmentFromName(argument));
        commandManager.registerArgument(World.class, (sender, argument) -> server.getWorld(argument));
        commandManager.registerArgument(EnchantmentType.class, (sender, argument) -> MenuManager.getEnchantmentTypes().stream().filter(filter -> !filter.getName().equalsIgnoreCase(argument)));
        commandManager.registerArgument(PlayerBuilder.class, (sender, context) -> new PlayerBuilder(plugin, context));

        commandManager.registerSuggestion(EnchantmentType.class, (context) -> MenuManager.getEnchantmentTypes().stream().map(EnchantmentType::getName).toList());
        commandManager.registerSuggestion(Player.class, (context) -> server.getOnlinePlayers().stream().map(Player::getName).toList());
        commandManager.registerSuggestion(World.class, (context) -> server.getWorlds().stream().map(World::getName).toList());

        commandManager.registerSuggestion(CEnchantment.class, (context) -> {
            final List<String> list = new ArrayList<>();

            for (CEnchantment enchantment : crazyManager.getRegisteredEnchantments()) {
                try {
                    list.add(ColorUtils.stripStringColour(enchantment.getCustomName().replaceAll(" ", "_")));
                } catch (NullPointerException ignore) {}
            }

            return list;
        });

        commandManager.registerSuggestion(Category.class, (context) -> {
            final List<String> list = new ArrayList<>();

            for (Category category : settings.getCategories()) {
                try {
                    list.add(category.getName());
                } catch (NullPointerException ignore) {}
            }

            return list;
        });

        commandManager.registerSuggestion(Dust.class, (context) -> {
            final List<String> list = new ArrayList<>();

            for (Dust dust : Dust.values()) {
                list.addAll(dust.getKnownNames());
            }

            return list;
        });

        commandManager.registerSuggestion(GKitz.class, (context) -> new ArrayList<>(crazyManager.getGKitz().stream().map(GKitz::getName).toList()));

        commandManager.registerSuggestion(Scrolls.class, (context) -> {
            final List<String> scrolls = new ArrayList<>();

            Arrays.stream(Scrolls.values()).forEach(scroll -> scrolls.addAll(scroll.getKnownNames()));

            return scrolls;
        });

        commandManager.registerSuggestion(int.class, (context) -> {
            final List<String> numbers = new ArrayList<>();

            for (int i = 1; i <= 100; i++) numbers.add(String.valueOf(i));

            return numbers;
        });

        commandManager.registerSuggestion(double.class, (context) -> {
            final List<String> numbers = new ArrayList<>();

            int count = 0;

            while (count <= 1000) {
                double x = count / 10.0;

                numbers.add(String.valueOf(x));

                count++;
            }

            return numbers;
        });

        commandManager.registerSuggestion(SuggestionKey.of("migrators"), (context) -> {
            final List<String> migrators = new ArrayList<>();

            for (ValidationType value : ValidationType.values()) {
                final String name = value.getName();

                migrators.add(name);
            }

            return migrators;
        });

        commandManager.registerSuggestion(SuggestionKey.of("enchantments"), (context) -> {
            final List<String> list = new ArrayList<>();

            for (CEnchantment enchantment : crazyManager.getRegisteredEnchantments()) {
                try {
                    list.add(ColorUtils.stripStringColour(enchantment.getCustomName().replaceAll(" ", "_")));
                } catch (NullPointerException ignore) {}
            }

            Arrays.asList(Enchantment.values()).forEach(enchantment -> list.add(enchantment.getKey().getKey()));

            return list;
        });

        commandManager.registerSuggestion(SuggestionKey.of("current_enchantments"), (context) -> {
            final List<String> list = new ArrayList<>();

            if (context.getSender() instanceof Player player) {
                settings.getEnchantments(player.getInventory().getItemInMainHand()).forEach((ce, level) -> {
                    list.add(ce.getStrippedName());
                });
            }

            return list;
        });

        List.of(
                new CommandAdd(),
                new CommandBook(),
                new CommandBottle(),
                new CommandDebug(),
                new CommandDust(),
                new CommandFix(),
                new CommandGive(),
                new CommandGkitz(),
                new CommandInfo(),
                new CommandLostBook(),
                new CommandReload(),
                new CommandSpawn(),
                new CommandValidate(),

                new CommandBlackSmith(),
                new CommandTinker(),
                new CommandHelp(),
                new CommandLimit()
        ).forEach(commandManager::registerCommand);
    }

    public static @NotNull BukkitCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }
}