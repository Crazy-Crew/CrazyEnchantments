package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.objects.User;
import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsMainMenu;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.managers.PlayerManager;
import com.badbones69.crazyenchantments.paper.managers.configs.ConfigManager;
import com.badbones69.crazyenchantments.paper.managers.configs.types.KitConfig;
import com.badbones69.crazyenchantments.registry.UserRegistry;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.*;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import net.kyori.adventure.identity.Identity;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenchantments.constants.MessageKeys;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Command("gkitz")
public class CommandGkitz {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final ConfigManager options = this.plugin.getConfigManager();

    private final CrazyInstance instance = this.plugin.getInstance();

    private final UserRegistry userRegistry = this.instance.getUserRegistry();

    private final PlayerManager playerManager = this.instance.getPlayerManager();

    @Command
    @Permission(value = "crazyenchantments.gkitz", def = PermissionDefault.TRUE)
    @Syntax("/gkitz")
    public void run(@NotNull final Player player) {
        if (!this.options.isGkitzToggle()) {
            this.userRegistry.getUser(player).sendMessage(MessageKeys.gkitz_not_enabled);

            return;
        }

        if (!this.playerManager.hasPlayer(player)) {
            this.playerManager.loadPlayer(player);
        }

        final KitConfig config = this.options.getKitConfig();

        new KitsMainMenu(player, config.getInventoryName(), config.getInventorySize()).open();
    }

    @Command("reset")
    @Permission(value = "crazyenchantments.reset", def = PermissionDefault.OP)
    @Flag(flag = "p", longFlag = "player", argument = Player.class)
    @Syntax("/gkitz reset <kit> [-p/--player]")
    public void reset(final CommandSender sender, final GKitz kit, final Flags flags) {
        final Optional<Player> flag = flags.getFlagValue("p", Player.class);
        final Optional<Player> safePlayer = flags.hasFlag("p") && flag.isPresent() ? flag : sender instanceof Player player ? Optional.of(player) : Optional.empty();

        if (safePlayer.isEmpty()) {
            //todo() debug

            return;
        }

        final Player player = safePlayer.get();
        final UUID receiver = player.getUniqueId();

        this.playerManager.getPlayer(player).ifPresentOrElse(cePlayer -> {
            cePlayer.removeCoolDown(kit);

            final Optional<UUID> target = sender.get(Identity.UUID);

            final boolean isNotReceiver = sender instanceof ConsoleCommandSender || target.isPresent() && !target.get().toString().equalsIgnoreCase(receiver.toString());

            if (isNotReceiver) {
                final User user = this.userRegistry.getUser(sender);

                user.sendMessage(MessageKeys.reset_gkit, new HashMap<>() {{
                    put("{player}", player.getName());
                    put("{kit}", kit.getName());
                }});
            }
        }, () -> {
            //todo() debug
        });
    }

    @Command("give")
    @Permission(value = "crazyenchantments.gkitz.give", def = PermissionDefault.OP)
    @Flag(flag = "p", longFlag = "player", argument = Player.class)
    @Syntax("/gkitz give <kit> [-p/--player]")
    public void kit(@NotNull final CommandSender sender, @NotNull final GKitz kit, final Flags flags) {
        final Optional<Player> flag = flags.getFlagValue("p", Player.class);
        final Optional<Player> safePlayer = flags.hasFlag("p") && flag.isPresent() ? flag : sender instanceof Player player ? Optional.of(player) : Optional.empty();

        if (safePlayer.isEmpty()) {
            //todo() debug

            return;
        }

        final Player player = safePlayer.get();
        final UUID receiver = player.getUniqueId();

        final Map<String, String> placeholders = new HashMap<>() {{
            put("{player}", player.getName());
            put("{kit}", kit.getName());
        }};

        final User author = this.userRegistry.getUser(sender);

        author.sendMessage(MessageKeys.given_gkit, placeholders);

        this.playerManager.getPlayer(player).ifPresentOrElse(cePlayer -> {
            final User user = this.userRegistry.getUser(player);

            if (sender.hasPermission("crazyenchantments.gkitz.give")) {
                user.sendMessage(MessageKeys.received_gkit, placeholders);

                cePlayer.giveGKit(kit);

                return;
            }

            if (!cePlayer.hasGkitPermission(kit)) {
                user.sendMessage(MessageKeys.no_gkit_permission, placeholders);

                return;
            }

            if (!cePlayer.canUseGKit(kit)) {
                final String cooldown = cePlayer.getCoolDown(kit).getCooldown();
                final String[] splitter = cooldown.split(",");

                Map<String, String> newPlaceholders = new HashMap<>(placeholders) {{
                    put("{day}", splitter[0]);
                    put("{hour}", splitter[1]);
                    put("{minute}", splitter[2]);
                    put("{second}", splitter[3]);
                }};

                user.sendMessage(MessageKeys.still_in_cooldown, newPlaceholders);

                return;
            }

            cePlayer.giveGKit(kit);
            cePlayer.addCoolDown(kit);

            final Optional<UUID> target = sender.get(Identity.UUID);

            final boolean isNotReceiver = sender instanceof ConsoleCommandSender || target.isPresent() && !target.get().toString().equalsIgnoreCase(receiver.toString());

            if (isNotReceiver) {
                author.sendMessage(MessageKeys.received_gkit, placeholders);
            }
        }, () -> {
            //todo() debug
        });
    }
}