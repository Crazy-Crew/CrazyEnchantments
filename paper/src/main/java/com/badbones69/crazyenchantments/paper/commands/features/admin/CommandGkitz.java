package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.FileManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsMenu;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.CEPlayer;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.*;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Command("gkitz")
public class CommandGkitz {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final CrazyManager crazyManager = this.plugin.getStarter().getCrazyManager();

    @Command
    @Permission(value = "crazyenchantments.gkitz", def = PermissionDefault.TRUE)
    @Syntax("/gkitz")
    public void run(@NotNull final Player player) {
        if (!this.crazyManager.isGkitzEnabled()) {
            //todo() send message that kits are not enabled.

            return;
        }

        if (this.crazyManager.getCEPlayer(player) == null) this.crazyManager.loadCEPlayer(player);

        FileConfiguration gkitz = FileManager.Files.GKITZ.getFile();

        player.openInventory(new KitsMenu(player, gkitz.getInt("Settings.GUI-Size"), gkitz.getString("Settings.Inventory-Name")).build().getInventory());
    }

    @Command("reset")
    @Permission(value = "crazyenchantments.reset", def = PermissionDefault.OP)
    @Flag(flag = "p", longFlag = "player", argument = Player.class)
    @Syntax("/gkitz reset <kit> [-p/--player]")
    public void reset(final CommandSender sender, final GKitz kit, final Flags flags) {
        if (flags.hasFlag("p")) {
            final Optional<Player> flag = flags.getFlagValue("p", Player.class);

            if (flag.isPresent()) {
                final Player player = flag.get();

                this.crazyManager.getCEPlayer(player).removeCoolDown(kit);

                sender.sendMessage(Messages.RESET_GKIT.getMessage(new HashMap<>() {{
                    put("%Player%", player.getName());
                    put("%Gkit%", kit.getName());
                    put("%Kit%", kit.getName());
                }}));
            }

            return;
        }

        if (sender instanceof Player player) {
            this.crazyManager.getCEPlayer(player).removeCoolDown(kit);

            player.sendMessage(Messages.RESET_GKIT.getMessage(new HashMap<>() {{
                put("%Player%", player.getName());
                put("%Gkit%", kit.getName());
                put("%Kit%", kit.getName());
            }}));
        }
    }

    @Command("give")
    @Permission(value = "crazyenchantments.gkitz.give", def = PermissionDefault.OP)
    @Flag(flag = "p", longFlag = "player", argument = Player.class)
    @Syntax("/gkitz give <kit> [-p/--player]")
    public void kit(@NotNull final CommandSender sender, @NotNull final GKitz kit, final Flags flags) {
        final boolean isAdmin = sender.hasPermission("crazyenchantments.gkitz.give");

        if (flags.hasFlag("p")) {
            final Optional<Player> optionalPlayer = flags.getFlagValue("p", Player.class);

            if (optionalPlayer.isPresent()) {
                final Player player = optionalPlayer.get();

                final Map<String, String> placeholders = new HashMap<>() {{
                    put("%Player%", player.getName());
                    put("%Gkit%", kit.getName());
                    put("%Kit%", kit.getName());
                }};

                giveKit(player, kit, isAdmin);

                player.sendMessage(Messages.GIVEN_GKIT.getMessage(placeholders));
            }

            return;
        }

        if (sender instanceof Player human) {
            giveKit(human, kit, isAdmin);
        }
    }

    public void giveKit(@NotNull final Player player, @NotNull final GKitz kit, final boolean isAdmin) {
        final CEPlayer cePlayer = this.crazyManager.getCEPlayer(player);

        final Map<String, String> placeholders = new HashMap<>() {{
            put("%Player%", player.getName());
            put("%Gkit%", kit.getName());
            put("%Kit%", kit.getName());
        }};

        if (isAdmin) {
            cePlayer.giveGKit(kit);

            player.sendMessage(Messages.RECEIVED_GKIT.getMessage(placeholders));

            return;
        }

        if (!cePlayer.hasGkitPermission(kit)) {
            player.sendMessage(Messages.NO_GKIT_PERMISSION.getMessage(placeholders));

            return;
        }

        if (!cePlayer.canUseGKit(kit)) {
            player.sendMessage(ColorUtils.getPrefix() + cePlayer.getCoolDown(kit).getCoolDownLeft(Messages.STILL_IN_COOLDOWN.getMessage(placeholders)));

            return;
        }

        player.sendMessage(Messages.RECEIVED_GKIT.getMessage(placeholders));

        cePlayer.giveGKit(kit);
        cePlayer.addCoolDown(kit);
    }
}