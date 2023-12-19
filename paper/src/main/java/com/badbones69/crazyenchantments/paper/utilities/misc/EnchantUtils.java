package com.badbones69.crazyenchantments.paper.utilities.misc;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class EnchantUtils {

    /**
     * Get the highest category rarity the enchantment is in.
     * @param enchantment The enchantment you are checking.
     * @return The highest category based on the rarities.
     */
    public static Category getHighestEnchantmentCategory(CEnchantment enchantment) {
        Category topCategory = null;
        int rarity = 0;

        for (Category category : enchantment.getCategories()) {
            if (category.getRarity() >= rarity) {
                rarity = category.getRarity();
                topCategory = category;
            }
        }

        return topCategory;
    }

    public static boolean isEventActive(CEnchantments enchant, Entity damager, ItemStack item, Map<CEnchantment, Integer> enchants) {

        if (!isActive((Player) damager, enchant, enchants)) return false;

        return normalEnchantEvent(enchant, damager, item);
    }

    public static boolean isBlastActive(Map<CEnchantment, Integer> enchantments, Player player, Block block) {

        if (!isActive(player, CEnchantments.BLAST, enchantments)) return false;

        return block.isEmpty() || CrazyEnchantments.getPlugin().getStarter().getCrazyManager().getBlastBlockList().contains(block.getType());

    }

    // TODO Add base spam limit.
    private static boolean isActive(Player player, CEnchantments enchant, Map<CEnchantment, Integer> enchants) {
        return enchants.containsKey(enchant.getEnchantment()) &&
                (!enchant.hasChanceSystem() || enchant.chanceSuccessful(enchants.get(enchant.getEnchantment())) &&
                        !(player.hasPermission("crazyenchantments.%s.deny".formatted(enchant.getName()))));
    }

    private static boolean normalEnchantEvent(CEnchantments enchant, Entity damager, ItemStack item) {
        EnchantmentUseEvent useEvent = new EnchantmentUseEvent((Player) damager, enchant.getEnchantment(), item);
        CrazyEnchantments.getPlugin().getServer().getPluginManager().callEvent(useEvent);
        return !useEvent.isCancelled();
    }

    public static boolean isArrowEventActive(CEnchantments enchant, Entity damager, ItemStack item, Map<CEnchantment, Integer> enchants) {
        return enchants.containsKey(enchant.getEnchantment()) &&
                (!enchant.hasChanceSystem() || enchant.chanceSuccessful(enchants.get(enchant.getEnchantment())));
    }

}