package com.badbones69.crazyenchantments.controllers;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.enums.Messages;
import com.badbones69.crazyenchantments.api.objects.CEBook;
import com.badbones69.crazyenchantments.api.objects.Category;
import com.badbones69.crazyenchantments.api.objects.LostBook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;

public class LostBookController implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBookClean(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (e.getItem() != null && e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = methods.getItemInHand(player);

            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                Category category = crazyManager.getCategoryFromLostBook(item);

                if (category != null) {
                    e.setCancelled(true);

                    if (methods.isInventoryFull(player)) {
                        player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                        return;
                    }

                    LostBook lostBook = category.getLostBook();
                    methods.removeItem(item, player);
                    CEBook book = crazyManager.getRandomEnchantmentBook(category);

                    if (book != null) {
                        player.getInventory().addItem(book.buildBook());
                        player.updateInventory();
                        HashMap<String, String> placeholders = new HashMap<>();
                        placeholders.put("%Found%", book.getItemBuilder().getName());
                        player.sendMessage(Messages.CLEAN_LOST_BOOK.getMessage(placeholders));

                        if (lostBook.useFirework()) methods.fireWork(player.getLocation().add(0, 1, 0), lostBook.getFireworkColors());

                        if (lostBook.playSound()) player.playSound(player.getLocation(), lostBook.getSound(), 1, 1);
                    } else {
                        player.sendMessage(methods.getPrefix("&cThe category &6" + category.getName() + " &chas no enchantments assigned to it."));
                    }
                }
            }
        }
    }
}