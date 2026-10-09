package com.badbones69.crazyenchantments.paper.controllers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.ryderbelserion.common.api.enums.messages.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.LostBook;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
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
import java.util.HashMap;
import java.util.Map;

public class LostBookController implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final Methods methods = this.starter.getMethods();

    @NotNull
    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    @NotNull
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBookClean(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        Category category = null;

        if ((event.getItem() == null || event.getAction() != Action.RIGHT_CLICK_AIR) && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        final ItemStack item = this.methods.getItemInHand(player);

        final String data = item.getPersistentDataContainer().get(DataKeys.lost_book.getNamespacedKey(), PersistentDataType.STRING);

        if (data == null) return;

        for (Category eachCategory : enchantmentBookSettings.getCategories()) {
            if (!data.equalsIgnoreCase(eachCategory.getName())) continue;

            category = eachCategory;
        }

        if (category == null) return;

        event.setCancelled(true);

        if (this.methods.isInventoryFull(player)) return;

        final LostBook lostBook = category.getLostBook();

        this.methods.removeItem(item, player);

        final CEBook book = this.crazyManager.getRandomEnchantmentBook(category);

        if (book == null) {
            player.sendMessage(ColorUtils.getPrefix("&cThe category &6" + category.getName() + " &chas no enchantments assigned to it."));

            return;
        }

        player.getInventory().addItem(book.buildBook(player));

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%Found%", book.getItemBuilder().getName());

        Messages.clean_lost_book.sendMessage(player, placeholders);

        if (lostBook.useFirework()) this.methods.fireWork(player.getLocation().add(0, 1, 0), lostBook.getFireworkColors());

        if (lostBook.playSound()) player.playSound(player.getLocation(), lostBook.getSound(), 1, 1);
    }
}