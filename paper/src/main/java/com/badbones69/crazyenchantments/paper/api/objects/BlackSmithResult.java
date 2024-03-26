package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith.BlackSmithManager;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Map.Entry;

public class BlackSmithResult {

    private int cost = 0;
    private ItemStack resultItem;

    public BlackSmithResult(Player player, ItemStack mainItem, ItemStack subItem) {
        resultItem = mainItem.clone();

        CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

        CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

        Starter starter = plugin.getStarter();

        EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

        CEBook mainBook = enchantmentBookSettings.getCEBook(mainItem);
        CEBook subBook = enchantmentBookSettings.getCEBook(subItem);

        if (mainBook != null && subBook != null) {
            // Books are the same enchantment.
            if (mainBook.getEnchantment() == subBook.getEnchantment() &&
                    // Books have to be the same level.
                    mainBook.getLevel() == subBook.getLevel() &&
                    // Makes sure level doesn't go passed max.
                    mainBook.getLevel() + 1 <= mainBook.getEnchantment().getMaxLevel()) {
                this.resultItem = mainBook.setLevel(mainBook.getLevel() + 1).buildBook();
                this.cost += BlackSmithManager.getBookUpgrade();
            }
        } else {
            if (mainItem.getType() == subItem.getType()) {
                CEItem mainCE = new CEItem(this.resultItem);
                CEItem subCE = new CEItem(subItem);
                BlackSmithCompare compare = new BlackSmithCompare(mainCE, subCE);

                // Checking for duplicate enchantments.
                for (Entry<Enchantment, Integer> entry : mainCE.getVanillaEnchantments().entrySet()) {
                    Enchantment enchantment = entry.getKey();

                    int level = entry.getValue();
                    int subLevel = subCE.getVanillaEnchantmentLevel(enchantment);

                    if (enchantment.canEnchantItem(subItem) && subCE.hasVanillaEnchantment(enchantment)) {
                        if (level == subLevel && level < enchantment.getMaxLevel()) {
                            mainCE.setVanillaEnchantment(enchantment, level + 1);
                            this.cost += BlackSmithManager.getLevelUp();
                        } else if (level < subLevel) {
                            mainCE.setVanillaEnchantment(enchantment, subLevel);
                            this.cost += BlackSmithManager.getLevelUp();
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
                            this.cost += BlackSmithManager.getLevelUp();
                        } else if (level < subLevel) {
                            mainCE.setCEnchantment(enchantment, subLevel);
                            this.cost += BlackSmithManager.getLevelUp();
                        }
                    }
                }

                // Checking for new enchantments.
                for (Entry<Enchantment, Integer> entry : compare.getNewVanillaEnchantments().entrySet()) {
                    Enchantment enchantment = entry.getKey();

                    if (enchantment.canEnchantItem(subItem) && crazyManager.canAddEnchantment(player, mainItem)) {
                        mainCE.setVanillaEnchantment(enchantment, entry.getValue());
                        this.cost += BlackSmithManager.getAddEnchantment();
                    }
                }

                for (Entry<CEnchantment, Integer> entry : compare.getNewCEnchantments().entrySet()) {
                    CEnchantment enchantment = entry.getKey();

                    if (enchantment.canEnchantItem(subItem) && crazyManager.canAddEnchantment(player, mainItem) && crazyManager.canAddEnchantment(player, subItem)) {
                        mainCE.setCEnchantment(enchantment, entry.getValue());
                        this.cost += BlackSmithManager.getAddEnchantment();
                    }
                }

                mainCE.build();
            }
        }
    }

    public int getCost() {
        return this.cost;
    }

    public ItemStack getResultItem() {
        return this.resultItem;
    }
}