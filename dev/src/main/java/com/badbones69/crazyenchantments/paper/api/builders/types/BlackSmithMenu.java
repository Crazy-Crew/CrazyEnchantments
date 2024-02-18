package com.badbones69.crazyenchantments.paper.api.builders.types;

import com.badbones69.crazyenchantments.paper.api.builders.InventoryBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class BlackSmithMenu extends InventoryBuilder {

    public BlackSmithMenu(Player player, int size, String title) {
        super(player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        Inventory inventory = getInventory();

        for (int slot : new int[]{0, 7, 8, 9, 16, 18, 25, 26}) {
            inventory.setItem(slot, new ItemStack((Material.GRAY_STAINED_GLASS_PANE)));
        }

        for (int slot : new int[]{1,2,3,4,5,6,10,12,13,15,19,20,21,22,23,24}) {
            inventory.setItem(slot, new ItemStack(Material.RED_STAINED_GLASS_PANE));
        }

        inventory.setItem(17, new ItemStack(Material.BARRIER));

        return this;
    }

    public static class BlackSmithListener implements Listener {

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            Inventory inventory = event.getInventory();

            if (!(inventory.getHolder(false) instanceof BlackSmithMenu holder)) return;

            InventoryView view = holder.getInventoryView();

            if (event.getClickedInventory() != view.getTopInventory()) return;

            event.setCancelled(true);
        }

        @EventHandler
        public void onInventoryDrag(InventoryDragEvent event) {
            Inventory inventory = event.getInventory();

            if (!(inventory.getHolder(false) instanceof BlackSmithMenu)) return;

            event.setCancelled(true);
        }
    }
}