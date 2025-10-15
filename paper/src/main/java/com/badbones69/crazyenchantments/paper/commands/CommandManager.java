package com.badbones69.crazyenchantments.paper.commands;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.enums.shop.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.shop.Scrolls;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantType;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.commands.features.admin.*;
import com.badbones69.crazyenchantments.paper.commands.features.admin.migration.CommandMigration;
import com.badbones69.crazyenchantments.paper.commands.features.admin.migration.enums.MigrationType;
import com.badbones69.crazyenchantments.paper.commands.features.base.CommandHelp;
import com.badbones69.crazyenchantments.paper.commands.features.base.CommandLimit;
import com.badbones69.crazyenchantments.paper.commands.features.base.standalone.CommandBlackSmith;
import com.badbones69.crazyenchantments.paper.commands.features.base.standalone.CommandTinker;
import com.badbones69.crazyenchantments.paper.commands.relations.ArgumentRelations;
import com.badbones69.crazyenchantments.paper.managers.CategoryManager;
import com.badbones69.crazyenchantments.paper.managers.KitsManager;
import com.ryderbelserion.fusion.paper.builders.PlayerBuilder;
import com.ryderbelserion.fusion.paper.utils.ItemUtils;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.*;

public class CommandManager {

    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);
    private static final CategoryManager categoryManager = plugin.getCategoryManager();
    private static final KitsManager kitsManager = plugin.getKitsManager();
    private static final CrazyInstance instance = plugin.getInstance();
    private static final Server server = plugin.getServer();

    private static final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(plugin);

    /**
     * Loads commands.
     */
    public static void load() {
        new ArgumentRelations().build();

        commandManager.registerArgument(GKitz.class, (sender, argument) -> kitsManager.getKitByName(argument));
        commandManager.registerArgument(Dust.class, (sender, argument) -> Dust.valueOf(argument));
        commandManager.registerArgument(Category.class, (sender, argument) -> categoryManager.getCategory(argument));
        commandManager.registerArgument(Scrolls.class, (sender, argument) -> Scrolls.getFromName(argument));
        commandManager.registerArgument(CEnchantment.class, (sender, argument) -> instance.getEnchantmentFromName(argument));
        commandManager.registerArgument(World.class, (sender, argument) -> server.getWorld(argument));
        commandManager.registerArgument(EnchantType.class, (sender, argument) -> instance.getRegisteredEnchantmentTypes().stream().filter(filter -> !filter.getName().equalsIgnoreCase(argument)));
        commandManager.registerArgument(PlayerBuilder.class, (sender, argument) -> new PlayerBuilder(plugin, argument));

        commandManager.registerArgument(MigrationType.class, (sender, argument) -> MigrationType.fromName(argument));

        commandManager.registerSuggestion(EnchantType.class, (context) -> instance.getRegisteredEnchantmentTypes().stream().map(EnchantType::getName).toList());
        commandManager.registerSuggestion(Player.class, (context) -> server.getOnlinePlayers().stream().map(Player::getName).toList());
        commandManager.registerSuggestion(World.class, (context) -> server.getWorlds().stream().map(World::getName).toList());

        commandManager.registerSuggestion(CEnchantment.class, (context) -> {
            final List<String> list = new ArrayList<>();

            for (CEnchantment enchantment : instance.getRegisteredEnchantments()) {
                try {
                    list.add(enchantment.getName());
                } catch (NullPointerException ignore) {}
            }

            return list;
        });

        commandManager.registerSuggestion(Category.class, (context) -> {
            final Set<String> categories = categoryManager.getCategories().keySet();

            return new ArrayList<>(categories);
        });

        commandManager.registerSuggestion(Dust.class, (context) -> {
            final List<String> list = new ArrayList<>();

            for (Dust dust : Dust.values()) {
                list.addAll(dust.getKnownNames());
            }

            return list;
        });

        commandManager.registerSuggestion(GKitz.class, (context) -> new ArrayList<>(kitsManager.getKits().stream().map(GKitz::getName).toList()));

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

            for (MigrationType value : MigrationType.values()) {
                final String name = value.getName();

                migrators.add(name);
            }

            return migrators;
        });

        commandManager.registerSuggestion(SuggestionKey.of("enchantments"), (context) -> {
            final List<String> list = new ArrayList<>();

            for (CEnchantment enchantment : instance.getRegisteredEnchantments()) {
                try {
                    list.add(ColorUtils.stripStringColour(enchantment.getCustomName().replaceAll(" ", "_")));
                } catch (NullPointerException ignore) {}
            }

            ItemUtils.getRegistryAccess().getRegistry(RegistryKey.ENCHANTMENT).iterator().forEachRemaining(enchantment -> list.add(enchantment.getKey().getKey()));

            return list;
        });

        commandManager.registerSuggestion(SuggestionKey.of("current_enchantments"), (context) -> {
            final List<String> list = new ArrayList<>();

            if (context.getSender() instanceof Player player) {
                final ItemStack itemStack = player.getInventory().getItemInMainHand();

                if (itemStack.isEmpty()) return list;

                final List<CEnchantment> enchantments = instance.getEnchantmentsOnItem(itemStack);

                for (final CEnchantment enchantment : enchantments) {
                    list.add(enchantment.getName());
                }
            }

            return list;
        });

        commandManager.registerSuggestion(SuggestionKey.of("items"), (context) -> {
            final List<String> list = new ArrayList<>();

            list.add("Item:DIAMOND_HELMET, Amount:1, Name:<gold><bold>Hat, Protection:4, Overload:1-5, Hulk:2-5, Lore:<green>Line 1.,<green>Line 2.");

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
                new CommandMigration(),

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