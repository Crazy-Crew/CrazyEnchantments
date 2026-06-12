package com.badbones69.crazyenchantments.paper.commands.types;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsMenu;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.keys.FileKeys;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Command(value = "gkitz")
public class KitCommand {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Starter starter = this.plugin.getStarter();

    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    @Command
    @Permission(value = "crazyenchantments.gkitz", def = PermissionDefault.TRUE)
    @Syntax("/gkitz")
    public void execute(final Player player) {
        final FileConfiguration gkitz = FileKeys.GKITZ.getConfiguration();

        player.openInventory(new KitsMenu(player, gkitz.getInt("Settings.GUI-Size", 27), gkitz.getString("Settings.Inventory-Name", "&8List of all GKitz")).build().getInventory());
    }

    @Command("reset")
    @Permission(value = "crazyenchantments.gkitz.reset", def = PermissionDefault.OP)
    @Syntax("/gkitz reset <kit> [player]")
    public void reset(final CommandSender sender, @Suggestion("kits") final String kit, final Player player) {
        Optional.ofNullable(this.crazyManager.getGKitFromName(kit)).ifPresentOrElse(gkit -> {
            this.crazyManager.getCEPlayer(player.getUniqueId()).ifPresent(cePlayer -> {
                cePlayer.removeCoolDown(gkit);

                final Map<String, String> placeholders = new HashMap<>();

                placeholders.put("%Player%", player.getName());
                placeholders.put("%Gkit%", gkit.getName());
                placeholders.put("%Kit%", gkit.getName());

                sender.sendMessage(Messages.RESET_GKIT.getMessage(placeholders));
            });
        }, () -> {
            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("%Kit%", kit);
            placeholders.put("%Gkit%", kit);

            sender.sendMessage(Messages.NOT_A_GKIT.getMessage(placeholders));
        });
    }

    @Command("give")
    @Permission(value = "crazyenchantments.gkitz.use", def = PermissionDefault.OP)
    @Syntax("/gkitz give <kit> [player]")
    public void give(final CommandSender sender, @Suggestion("kits") final String kit, @dev.triumphteam.cmd.core.annotations.Optional final Player target) {
        final Player safePlayer = target != null ? target : sender instanceof Player player ? player : null;

        if (safePlayer == null) {
            return;
        }

        final boolean ignoreCooldown = safePlayer.hasPermission("crazyenchantments.gkitz.give");

        Optional.ofNullable(this.crazyManager.getGKitFromName(kit)).ifPresentOrElse(gkit -> {
            this.crazyManager.getCEPlayer(safePlayer.getUniqueId()).ifPresent(cePlayer -> {
                final Map<String, String> placeholders = new HashMap<>();

                placeholders.put("%Player%", safePlayer.getName());
                placeholders.put("%Gkit%", gkit.getName());
                placeholders.put("%Kit%", gkit.getName());

                if (cePlayer.hasGkitPermission(gkit) || ignoreCooldown) {
                    if (cePlayer.canUseGKit(gkit) || ignoreCooldown) {
                        cePlayer.giveGKit(gkit);

                       safePlayer.sendMessage(Messages.RECEIVED_GKIT.getMessage(placeholders));

                        if (ignoreCooldown) {
                            sender.sendMessage(Messages.GIVEN_GKIT.getMessage(placeholders));
                        } else {
                            cePlayer.addCoolDown(gkit);
                        }
                    } else {
                        sender.sendMessage(ColorUtils.getPrefix() + cePlayer.getCoolDown(gkit).getCoolDownLeft(Messages.STILL_IN_COOLDOWN.getMessage(placeholders)));
                    }
                } else {
                    sender.sendMessage(Messages.NO_GKIT_PERMISSION.getMessage(placeholders));
                }
            });
        }, () -> {
            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("%Kit%", kit);
            placeholders.put("%Gkit%", kit);

            sender.sendMessage(Messages.NOT_A_GKIT.getMessage(placeholders));
        });
    }
}