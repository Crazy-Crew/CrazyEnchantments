package com.badbones69.crazyenchantments.paper.commands.features.admin;

import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.commands.features.BaseCommand;
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
        Enchantment vanillaEnchantment = this.methods.getEnchantment(enchantment);
        CEnchantment ceEnchantment = this.instance.getEnchantmentFromName(enchantment);

        boolean isVanilla = vanillaEnchantment != null;

        if (vanillaEnchantment == null && ceEnchantment == null) {
            player.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());

            return;
        }

        final ItemStack itemStack = this.methods.getItemInHand(player);

        if (itemStack.isEmpty()) {
            player.sendMessage(Messages.DOESNT_HAVE_ITEM_IN_HAND.getMessage());

            return;
        }

        if (isVanilla) {
            itemStack.addUnsafeEnchantment(vanillaEnchantment, level);

            this.methods.setItemInHand(player, itemStack);
        } else {
            this.crazyManager.addEnchantment(itemStack, ceEnchantment, level);

            this.methods.setItemInHand(player, itemStack);
        }
    }

    @Command("remove")
    @Permission(value = "crazyenchantments.remove", def = PermissionDefault.OP)
    @Syntax("/crazyenchantments remove <enchantment>")
    public void remove(Player player, @Suggestion("current_enchantments") String enchantment) {
        Enchantment vanillaEnchantment = this.methods.getEnchantment(enchantment);
        CEnchantment ceEnchantment = this.instance.getEnchantmentFromName(enchantment);
        boolean isVanilla = vanillaEnchantment != null;

        if (vanillaEnchantment == null && ceEnchantment == null) {
            player.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());

            return;
        }

        final ItemStack itemStack = this.methods.getItemInHand(player).clone();

        if (itemStack.getType() == Material.AIR) {
            player.sendMessage(Messages.DOESNT_HAVE_ITEM_IN_HAND.getMessage());

            return;
        }

        if (isVanilla) {
            itemStack.removeEnchantment(vanillaEnchantment);

            this.methods.setItemInHand(player, itemStack);
        } else {
            if (this.instance.getEnchantments(itemStack).containsKey(ceEnchantment)) {
                this.methods.setItemInHand(player, this.instance.removeEnchantment(itemStack, ceEnchantment));

                player.sendMessage(Messages.REMOVED_ENCHANTMENT.getMessage(new HashMap<>() {{
                    put("%Enchantment%", ceEnchantment.getCustomName());
                }}));

                return;
            }
        }

        player.sendMessage(Messages.DOESNT_HAVE_ENCHANTMENT.getMessage(new HashMap<>() {{
            put("%Enchantment%", enchantment);
        }}));
    }
}