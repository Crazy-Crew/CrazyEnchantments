package com.badbones69.crazyenchantments.paper.commands.api;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.commands.api.relations.ArgumentRelations;
import com.badbones69.crazyenchantments.paper.commands.types.admin.BookCommand;
import com.badbones69.crazyenchantments.paper.commands.types.admin.BottleCommand;
import com.badbones69.crazyenchantments.paper.commands.types.admin.DustCommand;
import com.badbones69.crazyenchantments.paper.commands.types.admin.EnchantCommand;
import com.badbones69.crazyenchantments.paper.commands.types.admin.LostBookCommand;
import com.badbones69.crazyenchantments.paper.commands.types.admin.ReloadCommand;
import com.badbones69.crazyenchantments.paper.commands.types.admin.ScrollCommand;
import com.badbones69.crazyenchantments.paper.commands.types.admin.crystal.CrystalCommand;
import com.badbones69.crazyenchantments.paper.commands.types.admin.crystal.ScramblerCommand;
import com.badbones69.crazyenchantments.paper.commands.types.admin.crystal.SlotCrystalCommand;
import com.badbones69.crazyenchantments.paper.commands.types.admin.debug.CheckCommand;
import com.badbones69.crazyenchantments.paper.commands.types.admin.debug.ConvertCommand;
import com.badbones69.crazyenchantments.paper.commands.types.admin.debug.DebugCommand;
import com.badbones69.crazyenchantments.paper.commands.types.admin.debug.FixCommand;
import com.badbones69.crazyenchantments.paper.commands.types.player.HelpCommand;
import com.badbones69.crazyenchantments.paper.commands.types.player.LimitCommand;
import com.badbones69.crazyenchantments.paper.commands.types.player.single.BlackSmithCommand;
import com.badbones69.crazyenchantments.paper.commands.types.player.single.TinkerCommand;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.ryderbelserion.fusion.paper.builders.items.PlayerBuilder;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommandManager {

    private static final CrazyEnchantments plugin = CrazyEnchantments.getPlugin(CrazyEnchantments.class);
    private static final Starter starter = plugin.getStarter();
    private static final Methods methods = starter.getMethods();
    private static final CrazyManager crazyManager = starter.getCrazyManager();
    private static final Server server = plugin.getServer();
    private static final EnchantmentBookSettings book = starter.getEnchantmentBookSettings();

    private static final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(plugin);

    /**
     * Loads commands.
     */
    public static void load() {
        new ArgumentRelations().build();

        commandManager.registerSuggestion(SuggestionKey.of("players"), (_) -> server.getOnlinePlayers().stream().map(Player::getName).toList());

        commandManager.registerSuggestion(SuggestionKey.of("worlds"), (_) -> server.getWorlds().stream().map(world -> world.getKey().getKey()).toList());

        commandManager.registerSuggestion(SuggestionKey.of("numbers"), (_) -> {
            final List<String> completions = new ArrayList<>();

            completions.add("5");
            completions.add("10");
            completions.add("20");

            Collections.sort(completions);

            return completions;
        });

        commandManager.registerSuggestion(SuggestionKey.of("ce_enchantment_numbers"), (context) -> {
            final List<String> completions = new ArrayList<>();

            final Map<String, String> arguments = context.getArgumentsMap();

            if (arguments.containsKey("custom_enchantment")) {
                final String argument = arguments.get("custom_enchantment");

                Optional.ofNullable(crazyManager.getEnchantmentFromName(argument)).ifPresent(enchantment -> {
                    for (int amount = 1; amount <= enchantment.getMaxLevel(); amount++) {
                        completions.add(String.valueOf(amount));
                    }
                });
            }

            Collections.sort(completions);

            return completions;
        });

        commandManager.registerSuggestion(SuggestionKey.of("enchantment_numbers"), (context) -> {
            final List<String> completions = new ArrayList<>();

            final Map<String, String> arguments = context.getArgumentsMap();

            if (arguments.containsKey("custom_enchantment")) {
                final String argument = arguments.get("custom_enchantment");

                Optional.ofNullable(crazyManager.getEnchantmentFromName(argument)).ifPresent(enchantment -> {
                    for (int amount = 1; amount <= enchantment.getMaxLevel(); amount++) {
                        completions.add(String.valueOf(amount));
                    }
                });

                Optional.ofNullable(methods.getEnchantment(argument)).ifPresent(enchantment -> {
                    for (int amount = 1; amount <= enchantment.getMaxLevel(); amount++) {
                        completions.add(String.valueOf(amount));
                    }
                });
            }

            Collections.sort(completions);

            return completions;
        });

        commandManager.registerSuggestion(SuggestionKey.of("dust"), (context) -> {
            final List<String> completions = new ArrayList<>();

            for (final Dust dust: Dust.values()) {
                completions.addAll(dust.getKnownNames());
            }

            return completions;
        });

        commandManager.registerSuggestion(SuggestionKey.of("scrolls"), (context) -> {
            final List<String> completions = new ArrayList<>();

            for (final Scrolls scroll : Scrolls.values()) {
                completions.addAll(scroll.getKnownNames());
            }

            return completions;
        });

        commandManager.registerSuggestion(SuggestionKey.of("categories"), (context) -> {
            final List<String> completions = new ArrayList<>();

            for (final Category category : book.getCategories()) {
                completions.add(category.getName());
            }

            return completions;
        });

        commandManager.registerSuggestion(SuggestionKey.of("enchantment_types"), (context) -> {
            final List<String> completions = new ArrayList<>();

            for (final EnchantmentType category : MenuManager.getEnchantmentTypes()) {
                completions.add(category.getName());
            }

            return completions;
        });

        commandManager.registerSuggestion(SuggestionKey.of("player_enchantments"), (context) -> {
            final List<String> completions = new ArrayList<>();

            final CommandSender sender = context.getSender();

            if (sender instanceof Player player) {
                final PlayerInventory inventory = player.getInventory();
                final ItemStack itemStack = inventory.getItemInMainHand();

                book.getEnchantments(itemStack).forEach((enchantment, _) -> completions.add(enchantment.getName()));

                if (itemStack.hasData(DataComponentTypes.ENCHANTMENTS)) {
                    final ItemEnchantments component = itemStack.getData(DataComponentTypes.ENCHANTMENTS);

                    if (component != null) {
                        component.enchantments().forEach((enchantment, _) -> completions.add(enchantment.getKey().getKey()));
                    }
                }
            }

            return completions;
        });

        commandManager.registerSuggestion(SuggestionKey.of("enchantments"), (context) -> {
            final List<String> completions = new ArrayList<>();

            for (final CEnchantment enchantment : crazyManager.getRegisteredEnchantments()) {
                completions.add(enchantment.getName().replace(" ", "_"));
            }

            final RegistryAccess access = RegistryAccess.registryAccess();

            access.getRegistry(RegistryKey.ENCHANTMENT).forEach(enchantment -> completions.add(enchantment.getKey().getKey()));

            return completions;
        });

        commandManager.registerSuggestion(SuggestionKey.of("custom_enchantments"), (context) -> {
            final List<String> completions = new ArrayList<>();

            for (final CEnchantment enchantment : crazyManager.getRegisteredEnchantments()) {
                completions.add(enchantment.getName().replace(" ", "_"));
            }

            return completions;
        });

        commandManager.registerSuggestion(SuggestionKey.of("enchantments_info"), (context) -> {
            final List<String> completions = new ArrayList<>();

            for (final CEnchantment enchantment : crazyManager.getRegisteredEnchantments()) {
                completions.add(enchantment.getName().replace(" ", "_"));
            }

            for (final EnchantmentType category : MenuManager.getEnchantmentTypes()) {
                completions.add(category.getName());
            }

            return completions;
        });

        commandManager.registerSuggestion(SuggestionKey.of("doubles"), (context) -> {
            final List<String> numbers = new ArrayList<>();

            int count = 0;

            while (count <= 1000) {
                double x = count / 10.0;

                numbers.add(String.valueOf(x));

                count++;
            }

            return numbers;
        });

        commandManager.registerArgument(PlayerBuilder.class, (_, context) -> new PlayerBuilder(context));

        List.of(
                new BlackSmithCommand(),
                new TinkerCommand(),

                new ConvertCommand(),
                new ReloadCommand(),
                new CheckCommand(),
                new DebugCommand(),
                new FixCommand(),

                new SlotCrystalCommand(),
                new ScramblerCommand(),
                new CrystalCommand(),

                new BottleCommand(),
                new DustCommand(),

                new LostBookCommand(),
                new ScrollCommand(),

                new BookCommand(),

                new EnchantCommand(),

                new LimitCommand(),
                new HelpCommand()
        ).forEach(commandManager::registerCommand);
    }

    public static @NotNull BukkitCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }
}