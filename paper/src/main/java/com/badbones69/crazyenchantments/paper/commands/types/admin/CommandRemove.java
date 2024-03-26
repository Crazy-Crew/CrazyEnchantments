package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.commands.BaseCommand;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class CommandRemove extends BaseCommand {

    private final @NotNull Starter starter = this.plugin.getStarter();

    private final @NotNull Methods methods = this.starter.getMethods();

    private final @NotNull CrazyManager crazyManager = this.starter.getCrazyManager();

    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    @Command("remove")
    @Permission(value = "crazyenchantments.remove", def = PermissionDefault.OP)
    public void remove(Player player, String name, @Suggestion("numbers") int level) {
        Enchantment vanillaEnchantment = this.methods.getEnchantment(name);
        CEnchantment ceEnchantment = this.crazyManager.getEnchantmentFromName(name);
        boolean isVanilla = vanillaEnchantment != null;

        if (vanillaEnchantment == null && ceEnchantment == null) {
            player.sendRichMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
            return;
        }

        if (this.methods.getItemInHand(player).getType() == Material.AIR) {
            player.sendRichMessage(Messages.DOESNT_HAVE_ITEM_IN_HAND.getMessage());
            return;
        }

        ItemStack item = this.methods.getItemInHand(player);

        if (isVanilla) {
            item.removeEnchantment(vanillaEnchantment);
            this.methods.setItemInHand(player, item);

            return;
        } else {
            if (this.enchantmentBookSettings.getEnchantments(item).containsKey(ceEnchantment)) {
                this.methods.setItemInHand(player, this.enchantmentBookSettings.removeEnchantment(item, ceEnchantment));

                Map<String, String> placeholders = new HashMap<>();

                placeholders.put("%Enchantment%", ceEnchantment.getCustomName());

                player.sendRichMessage(Messages.REMOVED_ENCHANTMENT.getMessage(placeholders).replaceAll("&", ""));

                return;
            }
        }

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%Enchantment%", name);

        player.sendRichMessage(Messages.DOESNT_HAVE_ENCHANTMENT.getMessage(placeholders));
    }
}