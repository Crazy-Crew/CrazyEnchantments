package com.badbones69.crazyenchantments.paper.controllers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.LostBook;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;

public class LostBookController implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBookClean(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Category category = null;

        if ((event.getItem() == null || event.getAction() != Action.RIGHT_CLICK_AIR) && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = methods.getItemInHand(player);

        if (!item.hasItemMeta()) return;
        String data = item.getItemMeta().getPersistentDataContainer().get(DataKeys.LOST_BOOK.getKey(), PersistentDataType.STRING);
        if (data == null) return;

        for (Category eachCategory : enchantmentBookSettings.getCategories()) {
            if (!data.equalsIgnoreCase(eachCategory.getName())) continue;
            category = eachCategory;
        }

        if (category == null) return;

        event.setCancelled(true);

        if (methods.isInventoryFull(player)) return;

        LostBook lostBook = category.getLostBook();
        methods.removeItem(item, player);
        CEBook book = crazyManager.getRandomEnchantmentBook(category);

        if (book == null) {
            player.sendMessage(ColorUtils.getPrefix("&cThe category &6" + category.getName() + " &chas no enchantments assigned to it."));
            return;
        }

        player.getInventory().addItem(book.buildBook());

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%Found%", book.getItemBuilder().getName());

        player.sendMessage(Messages.CLEAN_LOST_BOOK.getMessage(placeholders));

        if (lostBook.useFirework()) methods.fireWork(player.getLocation().add(0, 1, 0), lostBook.getFireworkColors());

        if (lostBook.playSound()) player.playSound(player.getLocation(), lostBook.getSound(), 1, 1);
    }
}