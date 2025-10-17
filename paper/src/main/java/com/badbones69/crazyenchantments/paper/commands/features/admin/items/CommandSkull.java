package com.badbones69.crazyenchantments.paper.commands.features.admin.items;

import com.badbones69.crazyenchantments.enums.Files;
import com.badbones69.crazyenchantments.paper.api.enums.files.FileKeys;
import com.badbones69.crazyenchantments.paper.api.enums.files.MessageKeys;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import com.ryderbelserion.fusion.core.api.support.ModSupport;
import com.ryderbelserion.fusion.paper.builders.ItemBuilder;
import com.ryderbelserion.fusion.paper.builders.PlayerBuilder;
import com.ryderbelserion.fusion.paper.utils.ItemUtils;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Flag;
import dev.triumphteam.cmd.core.annotations.Syntax;
import dev.triumphteam.cmd.core.argument.keyed.Flags;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import java.util.UUID;

public class CommandSkull extends BaseCommand {

    @Command("skull")
    @Permission(value = "crazyenchantments.skull", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments skull [-i/--item] [-p/--player] [-m/--mob] [-h/--head]")
    @Flag(flag = "p", longFlag = "player", argument = PlayerBuilder.class, suggestion = "players")
    @Flag(flag = "c", longFlag = "chance", argument = Double.class)
    @Flag(flag = "m", longFlag = "mob", argument = Mob.class)
    @Flag(flag = "h", longFlag = "head", argument = Void.class)
    @Flag(flag = "i", longFlag = "item", argument = Void.class)
    public void skull(@NotNull final CommandSender sender, final Flags flags) {
        final BasicConfigurationNode root = Files.heads.getJsonConfiguration();

        flags.getFlagValue("item", void.class).ifPresent(flag -> {
            if (!(sender instanceof Player player)) return;

            final PlayerInventory inventory = player.getInventory();
            final ItemStack itemStack = inventory.getItemInMainHand();

            if (itemStack.isEmpty()) {
                MessageKeys.DOESNT_HAVE_ITEM_IN_HAND.sendMessage(player);

                return;
            }

            flags.getFlagValue("player", void.class).ifPresent(action -> {
                final String playerName = player.getName();

                this.fusion.log("warn", "Player %s".formatted(playerName));

                save(root.node("players", player.getName()), itemStack, flags.getFlagValue("chance", Double.class).orElse(1.0), playerName);
            });

            if (flags.hasFlag("head")) {
                final String name = UUID.randomUUID().toString();

                this.fusion.log("warn", "Name %s".formatted(name));

                save(root.node("custom", name), itemStack, flags.getFlagValue("chance", Double.class).orElse(1.0), name);

                return;
            }

            if (flags.hasFlag("mob")) {
                final String playerName = player.getName();

                final Material material = itemStack.getType();

                String type;

                switch (material) {
                    case CREEPER_HEAD -> type = "creeper";

                    case DRAGON_HEAD -> type = "dragon";

                    case PIGLIN_HEAD -> type = "piglin";

                    case ZOMBIE_HEAD -> type = "zombie";

                    case SKELETON_SKULL -> type = "skeleton";

                    case WITHER_SKELETON_SKULL -> type = "wither_skeleton";

                    default -> type = "";
                }

                if (type.isEmpty()) {
                    this.fusion.log("warn", "The material %s is not a mob head.".formatted(material.key().asString()));

                    return;
                }

                this.fusion.log("warn", "Type %s".formatted(type));

                save(root.node("mobs", material.key().asMinimalString()), itemStack, flags.getFlagValue("chance", Double.class).orElse(1.0), playerName);
            }
        });

        flags.getFlagValue("player", PlayerBuilder.class).ifPresent(builder -> {
            String playerName = "";

            final Player player = builder.getPlayer();

            if (player == null) {
                final OfflinePlayer offlinePlayer = builder.getOfflinePlayer();

                if (offlinePlayer != null) {
                    playerName = offlinePlayer.getName();
                }
            } else {
                playerName = player.getName();
            }

            if (playerName == null || playerName.isEmpty()) return;

            final ConfigurationNode child = root.node("players", playerName);

            this.fusion.log("warn", "Player Value %s".formatted(playerName));

            save(child, new ItemBuilder(ItemType.PLAYER_HEAD).asSkullBuilder().withName(playerName).build().asItemStack(), flags.getFlagValue("chance", Double.class).orElse(1.0), playerName);
        });

        flags.getFlagValue("mob", Mob.class).ifPresent(mob -> {
            ItemType itemType = null;

            switch (mob.getType()) {
                case ZOMBIE -> itemType = ItemType.ZOMBIE_HEAD;
                case SKELETON -> itemType = ItemType.SKELETON_SKULL;
                case CREEPER -> itemType = ItemType.CREEPER_HEAD;
                case PIGLIN -> itemType = ItemType.PIGLIN_HEAD;
                case WITHER_SKELETON -> itemType = ItemType.WITHER_SKELETON_SKULL;
                case ENDER_DRAGON -> itemType = ItemType.DRAGON_HEAD;
            }

            if (itemType == null) return;

            final String key = itemType.key().asMinimalString();

            this.fusion.log("warn", "Key %s".formatted(key));

            save(root.node("mobs", key), itemType.createItemStack(), flags.getFlagValue("chance", Double.class).orElse(1.0), key);
        });

        if (flags.hasFlag("head")) {
            if (this.headManager == null || !this.fusion.isModReady(ModSupport.head_database)) {
                this.fusion.log("warn", "HeadDatabaseAPI is not enabled!");

                return;
            }

            this.headManager.getHeads().forEach(head -> {
                final String[] splitter = head.split(",");
                final String name = splitter[1];
                final String itemStack = splitter[2];

                try {
                    final ConfigurationNode custom = root.node("custom", name);

                    custom.node("base64").set(itemStack);
                    custom.node("chance").set(flags.getFlagValue("chance", Double.class).orElse(1.0));
                } catch (final SerializationException exception) {
                    this.fusion.log("warn", "Failed to write %s(%s) to file".formatted(name, itemStack));
                }
            });

            Files.heads.save();
        }
    }

    private void save(@NotNull final ConfigurationNode child, @NotNull final ItemStack itemStack, final double chance, @NotNull final String message) {
        final String base64 = ItemUtils.toBase64(itemStack);

        try {
            child.node("base64").set(base64);

            child.node("chance").set(chance);
        } catch (final SerializationException exception) {
            this.fusion.log("warn", "Failed to write %s(%s) to file".formatted(message, base64));
        }

        Files.heads.save();
    }
}