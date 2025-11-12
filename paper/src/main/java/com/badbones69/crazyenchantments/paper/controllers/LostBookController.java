package com.badbones69.crazyenchantments.paper.controllers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.DataKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.LostBook;
import com.badbones69.crazyenchantments.paper.managers.CategoryManager;
import com.badbones69.crazyenchantments.registry.UserRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenchantments.constants.MessageKeys;

import java.util.HashMap;
import java.util.Map;

public class LostBookController implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final CrazyInstance instance = this.plugin.getInstance();

    private final UserRegistry userRegistry = this.instance.getUserRegistry();

    private final CategoryManager categoryManager = this.plugin.getCategoryManager();

    @NotNull
    private final CrazyManager crazyManager = null;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBookClean(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Category category = null;

        if ((event.getItem() == null || event.getAction() != Action.RIGHT_CLICK_AIR) && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = Methods.getItemInHand(player);

        String data = item.getPersistentDataContainer().get(DataKeys.lost_book.getNamespacedKey(), PersistentDataType.STRING);

        if (data == null) return;

        final Map<String, Category> categories = this.categoryManager.getCategories();

        for (final Map.Entry<String, Category> entry : categories.entrySet()) {
            final String name = entry.getKey();

            if (!data.equalsIgnoreCase(name)) continue;

            category = entry.getValue();

            break;
        }

        if (category == null) return;

        event.setCancelled(true);

        if (Methods.isInventoryFull(player)) return;

        LostBook lostBook = category.getLostBook();

        Methods.removeItem(item, player);

        CEBook book = crazyManager.getRandomEnchantmentBook(category);

        if (book == null) {
            //player.sendMessage(ColorUtils.getPrefix("<red>The category <gold>" + category.getName() + " <red>has no enchantments assigned to it."));
            return;
        }

        player.getInventory().addItem(book.buildBook());

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{found}", book.getItemBuilder().getName());

        this.userRegistry.getUser(player).sendMessage(MessageKeys.clean_lost_book, placeholders);

        if (lostBook.useFirework()) Methods.fireWork(player.getLocation().add(0, 1, 0), lostBook.getFireworkColors());

        if (lostBook.playSound()) player.playSound(player.getLocation(), lostBook.getSound(), 1, 1);
    }
}