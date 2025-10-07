package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith.BlackSmithManager;
import com.badbones69.crazyenchantments.paper.api.exceptions.CrazyException;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map.Entry;
import java.util.Set;

public class BlackSmithResult {

    private int cost = 0;
    private ItemStack resultItem;
    
    public BlackSmithResult(@NotNull final Player player, @Nullable ItemStack mainItem, @NotNull final ItemStack subItem) {
        if (mainItem == null || mainItem.isEmpty()) {
            throw new CrazyException("The ItemStack used with BlackSmith is either empty, or null.");
        }

        this.resultItem = mainItem.clone();

        CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

        Starter starter = plugin.getStarter();

        EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

        CEBook mainBook = enchantmentBookSettings.getCEBook(mainItem);
        CEBook subBook = enchantmentBookSettings.getCEBook(subItem);

        if (mainBook != null && subBook != null) {
            // Books are the same enchantment.
            if (mainBook.getEnchantment() == subBook.getEnchantment() &&
            // Books have to be the same level.
            mainBook.getLevel() == subBook.getLevel() &&
            // Makes sure level doesn't go past max.
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
                            mainCE.addVanillaEnchantment(enchantment, level + 1);
                            this.cost += BlackSmithManager.getLevelUp();
                        } else if (level < subLevel) {
                            mainCE.addVanillaEnchantment(enchantment, subLevel);
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
                            mainCE.addCEnchantment(enchantment, level + 1);
                            this.cost += BlackSmithManager.getLevelUp();
                        } else if (level < subLevel) {
                            mainCE.addCEnchantment(enchantment, subLevel);
                            this.cost += BlackSmithManager.getLevelUp();
                        }
                    }
                }

                // Checking for new enchantments.
                for (Entry<Enchantment, Integer> entry : compare.getNewVanillaEnchantments().entrySet()) {
                    Enchantment enchantment = entry.getKey();

                    if (enchantment.canEnchantItem(subItem) && mainCE.canAddEnchantment(player) && !hasConflictingEnchant(mainCE.getVanillaEnchantments().keySet(), enchantment)) {
                        mainCE.addVanillaEnchantment(enchantment, entry.getValue());
                        this.cost += BlackSmithManager.getAddEnchantment();
                    }
                }

                for (Entry<CEnchantment, Integer> entry : compare.getNewCEnchantments().entrySet()) {
                    CEnchantment enchantment = entry.getKey();

                    if (enchantment.canEnchantItem(mainItem) && mainCE.canAddEnchantment(player) && !hasConflictingCEEnchant(mainCE.getCEnchantments().keySet(), enchantment)) {
                        mainCE.addCEnchantment(enchantment, entry.getValue());
                        this.cost += BlackSmithManager.getAddEnchantment();
                    }
                }

                mainCE.build();
            }
        }
    }

    /**
     * Check if this enchantment conflicts with another enchantment.
     *
     * @param vanillaEnchantments The enchants to check if they are conflicting.
     * @param enchantment The enchant to check the others against.
     * @return True if there is a conflict.
     */
    private boolean hasConflictingEnchant(@NotNull final Set<Enchantment> vanillaEnchantments, @NotNull final Enchantment enchantment) {
        for (Enchantment enchant : vanillaEnchantments) {
            if (enchantment.conflictsWith(enchant)) return true;
        }

        return false;
    }
    /**
     * Check if this enchantment conflicts with another enchantment.
     *
     * @param ceEnchantments The ceEnchants to check if they are conflicting.
     * @param cEnchantment The ceEnchant to check the others against.
     * @return True if there is a conflict.
     */
    private boolean hasConflictingCEEnchant(@NotNull final Set<CEnchantment> ceEnchantments, @NotNull final CEnchantment cEnchantment) {
        for (CEnchantment enchant : ceEnchantments) {
            if (cEnchantment.conflictsWith(enchant)) return true;
        }

        return false;
    }

    public int getCost() {
        return this.cost;
    }
    
    public ItemStack getResultItem() {
        return this.resultItem;
    }
}