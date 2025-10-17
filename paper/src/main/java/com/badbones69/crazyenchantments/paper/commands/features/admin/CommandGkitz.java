package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsMainMenu;
import com.badbones69.crazyenchantments.paper.api.enums.files.MessageKeys;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.managers.PlayerManager;
import com.badbones69.crazyenchantments.paper.managers.configs.ConfigManager;
import com.badbones69.crazyenchantments.paper.managers.configs.types.KitConfig;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Command("gkitz")
public class CommandGkitz {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final ConfigManager options = this.plugin.getConfigManager();

    private final CrazyInstance instance = this.plugin.getInstance();

    private final PlayerManager playerManager = this.instance.getPlayerManager();

    @Command
    @Permission(value = "crazyenchantments.gkitz", def = PermissionDefault.TRUE)
    @Syntax("/gkitz")
    public void run(@NotNull final Player player) {
        if (!this.options.isGkitzToggle()) {
            MessageKeys.GKIT_NOT_ENABLED.sendMessage(player);

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
                MessageKeys.RESET_GKIT.sendMessage(sender, new HashMap<>() {{
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

        MessageKeys.GIVEN_GKIT.sendMessage(sender, placeholders);

        this.playerManager.getPlayer(player).ifPresentOrElse(cePlayer -> {
            if (sender.hasPermission("crazyenchantments.gkitz.give")) {
                MessageKeys.RECEIVED_GKIT.sendMessage(player, placeholders);

                cePlayer.giveGKit(kit);

                return;
            }

            if (!cePlayer.hasGkitPermission(kit)) {
                MessageKeys.NO_GKIT_PERMISSION.sendMessage(player, placeholders);

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

                MessageKeys.STILL_IN_COOLDOWN.sendMessage(player, newPlaceholders);

                return;
            }

            cePlayer.giveGKit(kit);
            cePlayer.addCoolDown(kit);

            final Optional<UUID> target = sender.get(Identity.UUID);

            final boolean isNotReceiver = sender instanceof ConsoleCommandSender || target.isPresent() && !target.get().toString().equalsIgnoreCase(receiver.toString());

            if (isNotReceiver) {
                MessageKeys.RECEIVED_GKIT.sendMessage(player, placeholders);
            }
        }, () -> {
            //todo() debug
        });
    }
}