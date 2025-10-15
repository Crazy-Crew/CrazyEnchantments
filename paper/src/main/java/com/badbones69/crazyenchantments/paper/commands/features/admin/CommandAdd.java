package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.enums.files.MessageKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
import com.ryderbelserion.fusion.paper.utils.ItemUtils;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import java.util.HashMap;

public class CommandAdd extends BaseCommand {

    @Command("add")
    @Permission(value = "crazyenchantments.add", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments add <enchantment> <level>")
    public void add(final Player player, @Suggestion("enchantments") final String enchantment, final int level) {
        final Enchantment vanillaEnchantment = ItemUtils.getEnchantment(enchantment);
        final CEnchantment customEnchantment = this.instance.getEnchantmentFromName(enchantment);

        boolean isVanilla = vanillaEnchantment != null;

        if (vanillaEnchantment == null && customEnchantment == null) {
            MessageKeys.NOT_AN_ENCHANTMENT.sendMessage(player);

            return;
        }

        final ItemStack itemStack = Methods.getItemInHand(player);

        if (itemStack.isEmpty()) {
            MessageKeys.DOESNT_HAVE_ITEM_IN_HAND.sendMessage(player);

            return;
        }

        if (isVanilla) {
            itemStack.addUnsafeEnchantment(vanillaEnchantment, level);

            Methods.setItemInHand(player, itemStack);
        } else {
            this.crazyManager.addEnchantment(itemStack, customEnchantment, level);

            Methods.setItemInHand(player, itemStack);
        }
    }

    @Command("remove")
    @Permission(value = "crazyenchantments.remove", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments remove <enchantment>")
    public void remove(Player player, @Suggestion("current_enchantments") String enchantment) {
        final Enchantment vanillaEnchantment = ItemUtils.getEnchantment(enchantment);
        final CEnchantment customEnchantment = this.instance.getEnchantmentFromName(enchantment);

        boolean isVanilla = vanillaEnchantment != null;

        if (vanillaEnchantment == null && customEnchantment == null) {
            MessageKeys.NOT_AN_ENCHANTMENT.sendMessage(player);

            return;
        }

        final ItemStack itemStack = Methods.getItemInHand(player).clone();

        if (itemStack.getType() == Material.AIR) {
            MessageKeys.DOESNT_HAVE_ITEM_IN_HAND.sendMessage(player);

            return;
        }

        if (isVanilla) {
            itemStack.removeEnchantment(vanillaEnchantment);

            Methods.setItemInHand(player, itemStack);
        } else {
            if (this.instance.getEnchantments(itemStack).containsKey(customEnchantment)) {
                Methods.setItemInHand(player, this.instance.removeEnchantment(itemStack, customEnchantment));

                MessageKeys.REMOVED_ENCHANTMENT.sendMessage(player, new HashMap<>() {{
                    put("{enchantment}", customEnchantment.getCustomName());
                }});

                return;
            }
        }

        MessageKeys.DOESNT_HAVE_ENCHANTMENT.sendMessage(player, new HashMap<>() {{
            put("{enchantment}", customEnchantment.getCustomName());
        }});
    }
}