package com.badbones69.crazyenchantments.paper.commands.api;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.commands.api.relations.ArgumentRelations;
import com.badbones69.crazyenchantments.paper.commands.types.player.single.BlackSmithCommand;
import com.badbones69.crazyenchantments.paper.commands.types.player.single.TinkerCommand;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.ryderbelserion.fusion.paper.builders.items.PlayerBuilder;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

        commandManager.registerSuggestion(SuggestionKey.of("numbers"), (_) -> {
            final List<String> numbers = new ArrayList<>();

            for (int i = 1; i <= 16; i++) numbers.add(String.valueOf(i));

            numbers.sort(Comparator.comparingInt(Integer::parseInt));

            return numbers;
        });

        commandManager.registerSuggestion(SuggestionKey.of("categories"), (context) -> {
            final List<String> enchantments = new ArrayList<>();

            for (final Category category : book.getCategories()) {
                enchantments.add(category.getName());
            }

            return enchantments;
        });

        commandManager.registerSuggestion(SuggestionKey.of("player_enchantments"), (context) -> {
            final List<String> enchantments = new ArrayList<>();

            final CommandSender sender = context.getSender();

            if (sender instanceof Player player) {
                final PlayerInventory inventory = player.getInventory();
                final ItemStack itemStack = inventory.getItemInMainHand();

                book.getEnchantments(itemStack).forEach((enchantment, _) -> enchantments.add(ColorUtils.stripStringColour(enchantment.getCustomName())));
            }

            return enchantments;
        });

        commandManager.registerSuggestion(SuggestionKey.of("enchantments"), (context) -> {
            final List<String> enchantments = new ArrayList<>();

            for (final CEnchantment enchantment : crazyManager.getRegisteredEnchantments()) {
                enchantments.add(enchantment.getCustomName().replace(" ", "_"));
            }

            final RegistryAccess access = RegistryAccess.registryAccess();

            access.getRegistry(RegistryKey.ENCHANTMENT).forEach(enchantment -> enchantments.add(enchantment.getKey().getKey()));

            return enchantments;
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
                new TinkerCommand()

                //new ConvertCommand(),
                //new ReloadCommand(),
                //new CheckCommand(),
                //new DebugCommand(),
                //new FixCommand(),

                //new LimitCommand(),
                //new HelpCommand()
        ).forEach(commandManager::registerCommand);
    }

    public static @NotNull BukkitCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }
}