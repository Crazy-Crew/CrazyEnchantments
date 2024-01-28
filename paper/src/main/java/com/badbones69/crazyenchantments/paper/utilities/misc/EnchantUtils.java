package com.badbones69.crazyenchantments.paper.utilities.misc;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
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
        return isEventActive(enchant, damager, item, enchants, 1.0);
    }

    public static boolean isEventActive(CEnchantments enchant, Entity damager, ItemStack item, Map<CEnchantment, Integer> enchants, double multiplier) {
        return isActive((Player) damager, enchant, enchants, multiplier) && normalEnchantEvent(enchant, damager, item);
    }

    public static boolean isMassBlockBreakActive(Player player, CEnchantments enchant, Map<CEnchantment, Integer> enchants) {
        return isActive(player, enchant, enchants, 1.0);
    }


    private static boolean isActive(Player player, CEnchantments enchant, Map<CEnchantment, Integer> enchants) {
        return isActive(player, enchant, enchants, 1.0);
    }

    /**
     * Main Event used to validate that all enchants can work.
     * Global method that should be used before every enchantment is activated.
     * @param player
     * @param enchant
     * @param enchants
     * @param multiplier
     * @return True if the enchant is active and can be used if the event is passed.
     */
    private static boolean isActive(Player player, CEnchantments enchant, Map<CEnchantment, Integer> enchants, double multiplier) {
        //if (CrazyEnchantments.getPlugin().getStarter().getCrazyManager().getCEPlayer(player.getUniqueId()).onEnchantCooldown(enchant)) return false;
        return enchants.containsKey(enchant.getEnchantment()) &&
                (!enchant.hasChanceSystem() || enchant.chanceSuccessful(enchants.get(enchant.getEnchantment()), multiplier) &&
                        !(player.hasPermission("crazyenchantments.%s.deny".formatted(enchant.getName()))));
    }

    private static boolean normalEnchantEvent(CEnchantments enchant, Entity damager, ItemStack item) {
        EnchantmentUseEvent useEvent = new EnchantmentUseEvent((Player) damager, enchant.getEnchantment(), item);
        CrazyEnchantments.getPlugin().getServer().getPluginManager().callEvent(useEvent);
        return !useEvent.isCancelled();
    }

    public static boolean isAuraActive(Player player, CEnchantments enchant, Map<CEnchantment, Integer> enchants) {
        if (CrazyEnchantments.getPlugin().getStarter().getCrazyManager().getCEPlayer(player.getUniqueId()).onEnchantCooldown(enchant, 20*3)) return false;
        return isActive(player, enchant, enchants);
    }

    public static boolean isMovementEnchantActive(Player player, CEnchantments enchant, Map<CEnchantment, Integer> enchants) {
        return isActive(player, enchant, enchants);
    }

    public static boolean isArmorEventActive(Player player, CEnchantments enchant, ItemStack item) {
        //if (CrazyEnchantments.getPlugin().getStarter().getCrazyManager().getCEPlayer(player.getUniqueId()).onEnchantCooldown(enchant)) return false;
        if (player.hasPermission("crazyenchantments.%s.deny".formatted(enchant.getName()))) return false;
        return normalEnchantEvent(enchant, player, item);
    }

}
