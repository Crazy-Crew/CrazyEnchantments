package com.badbones69.crazyenchantments.paper.gui;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.managers.guis.InfoMenuManager;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

//TODO() redo info gui.
public class InfoGUIControl implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = CrazyEnchantments.get();

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    // Plugin Managers.
    @NotNull
    private final InfoMenuManager infoMenuManager = this.starter.getInfoMenuManager();

    @EventHandler(ignoreCancelled = true)
    public void infoClick(InventoryClickEvent event) {
        if (event.getView().title().equals(this.infoMenuManager.getInventoryName())) {
            event.setCancelled(true);

            if (event.getCurrentItem() != null) {
                ItemStack item = event.getCurrentItem();

                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    Player player = (Player) event.getWhoClicked();

                    if (item.isSimilar(this.infoMenuManager.getBackLeftButton()) || item.isSimilar(this.infoMenuManager.getBackRightButton())) {
                        this.infoMenuManager.openInfoMenu(player);
                        return;
                    }

                    for (EnchantmentType enchantmentType : this.infoMenuManager.getEnchantmentTypes()) {
                        if (item.isSimilar(enchantmentType.getDisplayItem())) {
                            this.infoMenuManager.openInfoMenu(player, enchantmentType);
                            return;
                        }
                    }
                }
            }
        }
    }
}