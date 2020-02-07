package me.badbones69.crazyenchantments.api.objects;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.managers.BlackSmithManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map.Entry;

public class BlackSmithResult {
    
    private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
    private static BlackSmithManager blackSmithManager = BlackSmithManager.getInstance();
    private int cost;
    private ItemStack resultItem;
    
    public BlackSmithResult(Player player, ItemStack mainItem, ItemStack subItem) {
        resultItem = mainItem.clone();
        cost = 0;
        if (mainItem.getType() == ce.getEnchantmentBook().getMaterial() && subItem.getType() == ce.getEnchantmentBook().getMaterial()) {
            CEBook mainBook = ce.getCEBook(mainItem);
            CEBook subBook = ce.getCEBook(subItem);
            //Books are the same enchantment.
            if (mainBook.getEnchantment() == subBook.getEnchantment() &&
            //Books have to be the same level.
            mainBook.getLevel() == subBook.getLevel() &&
            //Makes sure level doesn't go passed max.
            mainBook.getLevel() + 1 <= mainBook.getEnchantment().getMaxLevel()) {
                resultItem = mainBook.setLevel(mainBook.getLevel() + 1).buildBook();
                cost += blackSmithManager.getBookUpgrade();
            }
        } else {
            if (mainItem.getType() == subItem.getType()) {
                HashMap<CEnchantment, Integer> duplicateEnchantments = new HashMap<>();
                HashMap<CEnchantment, Integer> newEnchantments = new HashMap<>();
                HashMap<CEnchantment, Integer> higherLevelEnchantments = new HashMap<>();
                int maxEnchants = ce.getPlayerMaxEnchantments(player);
                for (CEnchantment enchantment : ce.getEnchantmentsOnItem(mainItem)) {
                    if (ce.hasEnchantment(subItem, enchantment)) {
                        if (ce.getLevel(mainItem, enchantment) == ce.getLevel(subItem, enchantment)) {
                            if (!duplicateEnchantments.containsKey(enchantment)) {
                                duplicateEnchantments.put(enchantment, ce.getLevel(mainItem, enchantment));
                            }
                        } else {
                            if (ce.getLevel(mainItem, enchantment) < ce.getLevel(subItem, enchantment)) {
                                higherLevelEnchantments.put(enchantment, ce.getLevel(subItem, enchantment));
                            }
                        }
                    }
                }
                for (CEnchantment enchantment : ce.getEnchantmentsOnItem(subItem)) {
                    if (!duplicateEnchantments.containsKey(enchantment) && !higherLevelEnchantments.containsKey(enchantment) && !ce.hasEnchantment(mainItem, enchantment)) {
                        newEnchantments.put(enchantment, ce.getLevel(subItem, enchantment));
                    }
                }
                for (Entry<CEnchantment, Integer> enchantment : duplicateEnchantments.entrySet()) {
                    int level = enchantment.getValue();
                    if (level + 1 <= enchantment.getKey().getMaxLevel()) {
                        resultItem = ce.addEnchantment(resultItem, enchantment.getKey(), level + 1);
                        cost += blackSmithManager.getLevelUp();
                    }
                }
                for (Entry<CEnchantment, Integer> enchantment : newEnchantments.entrySet()) {
                    if (blackSmithManager.useMaxEnchantments() && (Methods.getEnchantmentAmount(resultItem) + 1) <= maxEnchants) {
                        resultItem = ce.addEnchantment(resultItem, enchantment.getKey(), enchantment.getValue());
                        cost += blackSmithManager.getAddEnchantment();
                    }
                }
                for (Entry<CEnchantment, Integer> enchantment : higherLevelEnchantments.entrySet()) {
                    resultItem = ce.addEnchantment(resultItem, enchantment.getKey(), enchantment.getValue());
                    cost += blackSmithManager.getLevelUp();
                }
            }
        }
    }
    
    public int getCost() {
        return cost;
    }
    
    public ItemStack getResultItem() {
        return resultItem;
    }
    
}