package com.badbones69.crazyenchantments.paper.controllers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.ryderbelserion.common.api.enums.messages.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.LostBook;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import java.util.HashMap;
import java.util.Map;

public class LostBookController implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyPlatform platform = this.plugin.getPlatform();

    private final EnchantmentBookSettings bookSettings = this.platform.getBookSettings();

    private final CrazyManager crazyManager = this.plugin.getCrazyManager();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBookClean(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        Category category = null;

        if ((event.getItem() == null || event.getAction() != Action.RIGHT_CLICK_AIR) && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        final ItemStack item = Methods.getItemInHand(player);

        final String data = item.getPersistentDataContainer().get(DataKeys.lost_book.getNamespacedKey(), PersistentDataType.STRING);

        if (data == null) return;

        for (Category eachCategory : this.bookSettings.getCategories()) {
            if (!data.equalsIgnoreCase(eachCategory.getName())) continue;

            category = eachCategory;
        }

        if (category == null) return;

        event.setCancelled(true);

        if (Methods.isInventoryFull(player)) return;

        final LostBook lostBook = category.getLostBook();

        Methods.removeItem(item, player);

        final CEBook book = this.crazyManager.getRandomEnchantmentBook(category);

        if (book == null) {
            Messages.no_enchantments_category.sendMessage(player, Map.of("{category}", category.getName()));

            return;
        }

        player.getInventory().addItem(book.buildBook(player));

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%Found%", book.getItemBuilder().getName());

        Messages.clean_lost_book.sendMessage(player, placeholders);

        if (lostBook.useFirework()) Methods.fireWork(player.getLocation().add(0, 1, 0), lostBook.getFireworkColors());

        if (lostBook.playSound()) player.playSound(player.getLocation(), lostBook.getSound(), 1, 1);
    }
}