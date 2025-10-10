package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsMenu;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.enums.v2.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.CEPlayer;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.managers.ConfigManager;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.*;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
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

    private final ConfigManager options = this.plugin.getOptions();

    private final CrazyManager crazyManager = this.plugin.getStarter().getCrazyManager();

    @Command
    @Permission(value = "crazyenchantments.gkitz", def = PermissionDefault.TRUE)
    @Syntax("/gkitz")
    public void run(@NotNull final Player player) {
        if (!this.options.isGkitzToggle()) {
            Messages.GKIT_NOT_ENABLED.sendMessage(player);

            return;
        }

        if (this.crazyManager.getCEPlayer(player) == null) this.crazyManager.loadCEPlayer(player);

        final YamlConfiguration gkitz = FileKeys.gkitz.getYamlConfiguration();

        player.openInventory(new KitsMenu(player, gkitz.getInt("Settings.GUI-Size", 54), gkitz.getString("Settings.Inventory-Name", "&8List of all GKitz")).build().getInventory());
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

                Messages.RESET_GKIT.sendMessage(sender, new HashMap<>() {{
                    put("{player}", player.getName());
                    put("{kit}", kit.getName());
                }});
            }

            return;
        }

        if (sender instanceof Player player) {
            this.crazyManager.getCEPlayer(player).removeCoolDown(kit);

            Messages.RESET_GKIT.sendMessage(player, new HashMap<>() {{
                put("{player}", player.getName());
                put("{kit}", kit.getName());
            }});
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
                    put("{player}", player.getName());
                    put("{kit}", kit.getName());
                }};

                giveKit(player, kit, isAdmin);

                Messages.GIVEN_GKIT.sendMessage(player, placeholders);
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
            put("{player}", player.getName());
            put("{kit}", kit.getName());
        }};

        if (isAdmin) {
            cePlayer.giveGKit(kit);

            Messages.RECEIVED_GKIT.sendMessage(player, placeholders);

            return;
        }

        if (!cePlayer.hasGkitPermission(kit)) {
            Messages.NO_GKIT_PERMISSION.sendMessage(player, placeholders);

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

            Messages.STILL_IN_COOLDOWN.sendMessage(player, newPlaceholders);

            return;
        }

        Messages.RECEIVED_GKIT.sendMessage(player, placeholders);

        cePlayer.giveGKit(kit);
        cePlayer.addCoolDown(kit);
    }
}