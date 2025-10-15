package com.badbones69.crazyenchantments.paper.commands.features.admin.items;

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
import me.arcaniax.hdb.enums.CategoryEnum;
import me.arcaniax.hdb.object.head.Head;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CommandSkull extends BaseCommand {

    @Command("skull")
    @Permission(value = "crazyenchantments.skull", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments skull [-i/--item] [-p/--player] [-m/--mob] [-h/--head]")
    @Flag(flag = "p", longFlag = "player", argument = PlayerBuilder.class, suggestion = "players")
    @Flag(flag = "h", longFlag = "head")
    @Flag(flag = "i", longFlag = "item")
    @Flag(flag = "c", longFlag = "chance", argument = Double.class)
    @Flag(flag = "m", longFlag = "mob", argument = Mob.class)
    public void skull(@NotNull final CommandSender sender, final Flags flags) {
        final BasicConfigurationNode root = FileKeys.heads.getJsonConfiguration();

        if (flags.hasFlag("i") && sender instanceof Player player) { // requires player, and item in hand.
            final PlayerInventory inventory = player.getInventory();
            final ItemStack itemStack = inventory.getItemInMainHand();

            if (itemStack.isEmpty()) {
                MessageKeys.DOESNT_HAVE_ITEM_IN_HAND.sendMessage(player);

                return;
            }

            final boolean isPlayerHead = flags.hasFlag("p");

            if (isPlayerHead) {
                final String playerName = player.getName();

                save(root.node("players", player.getName()), itemStack, flags.getFlagValue("chance", Double.class).orElse(1.0), playerName);

                return;
            }

            final boolean isMobHead = flags.hasFlag("m");

            if (isMobHead) {
                final String playerName = player.getName();

                final Material material = itemStack.getType();

                String type;

                switch (material) {
                    case CREEPER_HEAD -> type = "creeper_head";

                    case DRAGON_HEAD -> type = "dragon_head";

                    case PIGLIN_HEAD -> type = "piglin_head";

                    case ZOMBIE_HEAD -> type = "zombie_head";

                    case SKELETON_SKULL -> type = "skeleton_head";

                    case WITHER_SKELETON_SKULL -> type = "wither_skeleton_head";

                    default -> type = "";
                }

                if (type.isEmpty()) {
                    this.fusion.log("warn", "The material %s is not a mob head.".formatted(material.key().asString()));

                    return;
                }

                save(root.node("mobs", type), itemStack, flags.getFlagValue("chance", Double.class).orElse(1.0), playerName);

                return;
            }

            final boolean isCustomHead = flags.hasFlag("h");

            if (isCustomHead) {
                final String name = UUID.randomUUID().toString();

                save(root.node("custom", name), itemStack, flags.getFlagValue("chance", Double.class).orElse(1.0), name);

                return;
            }

            return;
        }

        if (flags.hasFlag("p")) {
            final Optional<PlayerBuilder> optional = flags.getFlagValue("p", PlayerBuilder.class);

            optional.ifPresent(builder -> {
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

                save(child, new ItemBuilder(ItemType.PLAYER_HEAD).asSkullBuilder().withName(playerName).build().asItemStack(), flags.getFlagValue("chance", Double.class).orElse(1.0), playerName);
            });

            return;
        }

        if (flags.hasFlag("m")) {
            final Optional<Mob> optional = flags.getFlagValue("m", Mob.class);

            optional.ifPresent(mob -> {
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

                save(root.node("mobs", key), itemType.createItemStack(), flags.getFlagValue("chance", Double.class).orElse(1.0), key);
            });
        }

        if (flags.hasFlag("h") && this.fusion.isModReady(ModSupport.head_database)) {
            this.fusion.getHeadDatabaseAPI().ifPresentOrElse(api -> {
                for (final CategoryEnum category : CategoryEnum.values()) {
                    final List<Head> heads = api.getHeads(category);

                    for (final Head head : heads) {
                        final String itemStack = head.b64;
                        final String name = head.name;

                        try {
                            root.node("custom", name, "base64").set(itemStack);
                            root.node("custom", name, "chance").set(flags.getFlagValue("chance", Double.class).orElse(1.0));
                        } catch (final SerializationException exception) {
                            this.fusion.log("warn", "Failed to write %s(%s) to file".formatted(name, itemStack));
                        }
                    }
                }
                
                FileKeys.heads.save();
            }, () -> this.fusion.log("warn", "HeadDatabaseAPI is not present on the server!"));
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
    }
}