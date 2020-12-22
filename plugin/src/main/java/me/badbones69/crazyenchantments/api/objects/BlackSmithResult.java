package me.badbones69.crazyenchantments.api.objects;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.managers.BlackSmithManager;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map.Entry;

public class BlackSmithResult {
    
    private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
    private static BlackSmithManager blackSmithManager = BlackSmithManager.getInstance();
    private int cost = 0;
    private ItemStack resultItem;
    
    public BlackSmithResult(Player player, ItemStack mainItem, ItemStack subItem) {
        resultItem = mainItem.clone();
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
                CEItem mainCE = new CEItem(resultItem);
                CEItem subCE = new CEItem(subItem);
                BlackSmithCompare compare = new BlackSmithCompare(mainCE, subCE);
                //Checking for duplicate enchantments.
                for (Entry<Enchantment, Integer> entry : mainCE.getVanillaEnchantments().entrySet()) {
                    Enchantment enchantment = entry.getKey();
                    int level = entry.getValue();
                    int subLevel = subCE.getVanillaEnchantmentLevel(enchantment);
                    if (enchantment.canEnchantItem(subItem) && subCE.hasVanillaEnchantment(enchantment)) {
                        if (level == subLevel && level < enchantment.getMaxLevel()) {
                            mainCE.setVanillaEnchantment(enchantment, level + 1);
                            cost += blackSmithManager.getLevelUp();
                        } else if (level < subLevel) {
                            mainCE.setVanillaEnchantment(enchantment, subLevel);
                            cost += blackSmithManager.getLevelUp();
                        }
                    }
                }
                for (Entry<CEnchantment, Integer> entry : mainCE.getCEnchantments().entrySet()) {
                    CEnchantment enchantment = entry.getKey();
                    int level = entry.getValue();
                    int subLevel = subCE.getCEnchantmentLevel(enchantment);
                    if (enchantment.canEnchantItem(subItem) && subCE.hasCEnchantment(enchantment)) {
                        if (level == subLevel && level < enchantment.getMaxLevel()) {
                            mainCE.setCEnchantment(enchantment, level + 1);
                            cost += blackSmithManager.getLevelUp();
                        } else if (level < subLevel) {
                            mainCE.setCEnchantment(enchantment, subLevel);
                            cost += blackSmithManager.getLevelUp();
                        }
                    }
                }
                //Checking for new enchantments.
                for (Entry<Enchantment, Integer> entry : compare.getNewVanillaEnchantments().entrySet()) {
                    Enchantment enchantment = entry.getKey();
                    if (enchantment.canEnchantItem(subItem) && ce.canAddEnchantment(player, mainItem)) {
                        mainCE.setVanillaEnchantment(enchantment, entry.getValue());
                        cost += blackSmithManager.getAddEnchantment();
                    }
                }
                for (Entry<CEnchantment, Integer> entry : compare.getNewCEnchantments().entrySet()) {
                    CEnchantment enchantment = entry.getKey();
                    if (enchantment.canEnchantItem(subItem) && ce.canAddEnchantment(player, mainItem) && ce.canAddEnchantment(player, subItem)) {
                        mainCE.setCEnchantment(enchantment, entry.getValue());
                        cost += blackSmithManager.getAddEnchantment();
                    }
                }
                mainCE.build();
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
