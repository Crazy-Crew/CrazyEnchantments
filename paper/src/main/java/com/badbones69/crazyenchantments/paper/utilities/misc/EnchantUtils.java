package com.badbones69.crazyenchantments.paper.utilities.misc;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EnchantUtils {

    private static Set<UUID> inCooldown = new HashSet<>();

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
        return isActive((Player) damager, enchant, enchants) && normalEnchantEvent(enchant, damager, item);
    }

    public static boolean isMassBlockBreakActive(Player player, CEnchantments enchant, Map<CEnchantment, Integer> enchants) {
        return isActive(player, enchant, enchants);
    }

    /**
     * Main Event used to validate that all enchants can work.
     * Global method that should be used before every enchantment is activated.
     * @param player
     * @param enchant
     * @param enchants
     * @return True if the enchant is active and can be used if the event is passed.
     */
    private static boolean isActive(Player player, CEnchantments enchant, Map<CEnchantment, Integer> enchants) {
        if (checkTimer(player.getUniqueId())) return false;
        return enchants.containsKey(enchant.getEnchantment()) &&
                (!enchant.hasChanceSystem() || enchant.chanceSuccessful(enchants.get(enchant.getEnchantment())) &&
                        !(player.hasPermission("crazyenchantments.%s.deny".formatted(enchant.getName()))));
    }

    private static boolean checkTimer(UUID uuid) {
        if (inCooldown.contains(uuid)) return true;

        inCooldown.add(uuid);
        // Limit players to using 2 enchants per second.
        CrazyEnchantments.getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(CrazyEnchantments.getPlugin(), () -> inCooldown.remove(uuid), 10);
        return false;
    }

    private static boolean normalEnchantEvent(CEnchantments enchant, Entity damager, ItemStack item) {
        EnchantmentUseEvent useEvent = new EnchantmentUseEvent((Player) damager, enchant.getEnchantment(), item);
        CrazyEnchantments.getPlugin().getServer().getPluginManager().callEvent(useEvent);
        return !useEvent.isCancelled();
    }

}