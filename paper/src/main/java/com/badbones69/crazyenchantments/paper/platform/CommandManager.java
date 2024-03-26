package com.badbones69.crazyenchantments.paper.platform;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.platform.commands.types.CommandGive;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommandManager {

    private final @NotNull static CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final @NotNull static CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    private final @NotNull static EnchantmentBookSettings settings = plugin.getStarter().getEnchantmentBookSettings();

    private final @NotNull static BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(plugin);

    public static void load() {
        Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();

        getCommandManager().registerSuggestion(SuggestionKey.of("players"), (sender, context) -> players.stream().map(Player::getName).toList());

        getCommandManager().registerSuggestion(SuggestionKey.of("numbers"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            for (int i = 1; i <= 64; i++) numbers.add(String.valueOf(i));

            return numbers;
        });

        getCommandManager().registerSuggestion(SuggestionKey.of("enchants"), (sender, arguments) -> {
            List<String> enchantments = new ArrayList<>();

            for (CEnchantment enchantment : crazyManager.getRegisteredEnchantments()) {
                enchantments.add(enchantment.getCustomName().replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "").replace(" ", "_"));
            }

            Registry.ENCHANTMENT.iterator().forEachRemaining(enchantment -> enchantments.add(enchantment.getKey().getKey()));

            return enchantments;
        });

        getCommandManager().registerSuggestion(SuggestionKey.of("enchants_categories"), (sender, arguments) -> {
            List<String> enchantments = new ArrayList<>();

            for (CEnchantment enchantment : crazyManager.getRegisteredEnchantments()) {
                enchantments.add(enchantment.getCustomName().replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "").replace(" ", "_"));
            }

            for (Category category : settings.getCategories()) {
                enchantments.add(category.getName());
            }

            return enchantments;
        });

        getCommandManager().registerSuggestion(SuggestionKey.of("categories"), (sender, arguments) -> {
            List<String> enchantments = new ArrayList<>();

            for (Category category : settings.getCategories()) {
                enchantments.add(category.getName());
            }

            return enchantments;
        });

        getCommandManager().registerSuggestion(SuggestionKey.of("categories"), (sender, arguments) -> {
            List<String> categories = new ArrayList<>();

            for (Category category : plugin.getStarter().getEnchantmentBookSettings().getCategories()) {
                categories.add(category.getName());
            }

            return categories;
        });

        getCommandManager().registerMessage(BukkitMessageKey.INVALID_ARGUMENT, (sender, context) -> {
            //todo() add message
        });

        getCommandManager().registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> sender.sendRichMessage(Messages.NO_PERMISSION.getMessage()));

        getCommandManager().registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> sender.sendRichMessage(Messages.PLAYERS_ONLY.getMessage()));

        getCommandManager().registerMessage(BukkitMessageKey.CONSOLE_ONLY, (sender, context) -> {
            //todo() add message
        });

        getCommandManager().registerMessage(BukkitMessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> {
            //todo() add message
        });

        getCommandManager().registerMessage(BukkitMessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> {
            //todo() add message
        });

        List.of(
                new CommandGive()

                //new TinkerCommand(),
                //new SmithCommand(),

                //new CommandUpdate(),
                //new CommandLimit(),
                //new CommandInfo(),
                //new CommandHelp(),

                //new CommandScroll(),

                //new CommandAdd(),
                //new CommandBook(),
                //new CommandDust(),
                //new CommandRemove(),

                //new CommandLostBook(),
                //new CommandScrambler(),
                //new CommandSlotCrystal(),
                //new CommandCrystal(),

                //new CommandSpawn(),
                //new CommandConvert(),
                //new CommandBottle(),
                //new CommandReload(),
                //new CommandDebug(),
                //new CommandFix()
        ).forEach(getCommandManager()::registerCommand);
    }

    @NotNull
    public static BukkitCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }

    private static String getContext(String subCommand, String commandOrder) {
        String correctUsage = null;

        switch (subCommand) {
            case "transfer" -> correctUsage = commandOrder + "<crate-name> <player-name> <amount>";
            case "debug", "open", "set" -> correctUsage = commandOrder + "<crate-name>";
            case "tp" -> correctUsage = commandOrder + "<id>";
            case "additem" -> correctUsage = commandOrder + "<crate-name> <prize-number> <chance> [tier]";
            case "preview", "forceopen" -> correctUsage = commandOrder + "<crate-name> <player-name>";
            case "open-others" -> correctUsage = commandOrder + "<crate-name> <player-name> [key-type]";
            case "mass-open" -> correctUsage = commandOrder + "<crate-name> <key-type> <amount>";
            case "give-random" -> correctUsage = commandOrder + "<key-type> <amount> <player-name>";
            case "give", "take" -> correctUsage = commandOrder + "<key-type> <crate-name> <amount> <player-name>";
            case "giveall" -> correctUsage = commandOrder + "<key-type> <crate-name> <amount>";
        }

        return correctUsage;
    }
}