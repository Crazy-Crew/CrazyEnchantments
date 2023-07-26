package com.badbones69.crazyenchantments.controllers;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.managers.guis.InfoMenuManager;
import com.badbones69.crazyenchantments.api.objects.enchants.EnchantmentType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InfoGUIControl implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    // Plugin Managers.
    private final InfoMenuManager infoMenuManager = starter.getInfoMenuManager();

    @EventHandler(ignoreCancelled = true)
    public void infoClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(infoMenuManager.getInventoryName())) {
            event.setCancelled(true);

            if (event.getCurrentItem() != null) {
                ItemStack item = event.getCurrentItem();

                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    Player player = (Player) event.getWhoClicked();

                    if (item.isSimilar(infoMenuManager.getBackLeftButton()) || item.isSimilar(infoMenuManager.getBackRightButton())) {
                        infoMenuManager.openInfoMenu(player);
                        return;
                    }

                    for (EnchantmentType enchantmentType : infoMenuManager.getEnchantmentTypes()) {
                        if (item.isSimilar(enchantmentType.getDisplayItem())) {
                            infoMenuManager.openInfoMenu(player, enchantmentType);
                            return;
                        }
                    }
                }
            }
        }
    }
}