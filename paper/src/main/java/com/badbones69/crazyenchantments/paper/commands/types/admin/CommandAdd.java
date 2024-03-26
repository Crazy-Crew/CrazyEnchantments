package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.commands.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

public class CommandAdd extends BaseCommand {

    private final @NotNull Starter starter = this.plugin.getStarter();

    private final @NotNull Methods methods = this.starter.getMethods();

    private final @NotNull CrazyManager crazyManager = this.starter.getCrazyManager();

    @Command("add")
    @Permission(value = "crazyenchantments.add", def = PermissionDefault.OP)
    public void add(Player player, @Suggestion("enchants") String name, @Suggestion("numbers") int level) {
        Enchantment vanillaEnchantment = this.methods.getEnchantment(name);
        CEnchantment ceEnchantment = this.crazyManager.getEnchantmentFromName(name);
        boolean isVanilla = vanillaEnchantment != null;

        if (vanillaEnchantment == null && ceEnchantment == null) {
            player.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
            return;
        }

        if (this.methods.getItemInHand(player).getType() == Material.AIR) {
            player.sendMessage(Messages.DOESNT_HAVE_ITEM_IN_HAND.getMessage());
            return;
        }

        if (isVanilla) {
            ItemStack item = this.methods.getItemInHand(player).clone();
            item.addUnsafeEnchantment(vanillaEnchantment, level);
            this.methods.setItemInHand(player, item);
        } else {
            this.methods.setItemInHand(player, this.crazyManager.addEnchantment(this.methods.getItemInHand(player), ceEnchantment, level));
        }
    }
}