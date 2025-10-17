package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.objects.User;
import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.managers.configs.ConfigManager;
import com.badbones69.crazyenchantments.paper.managers.items.ItemManager;
import com.badbones69.crazyenchantments.paper.managers.items.interfaces.CustomItem;
import com.badbones69.crazyenchantments.registry.UserRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import us.crazycrew.crazyenchantments.constants.MessageKeys;

import java.util.HashMap;
import java.util.Optional;

public class SlotCrystalListener implements Listener {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final ItemManager itemManager = this.plugin.getItemManager();

    private final ConfigManager options = this.plugin.getConfigManager();

    private final CrazyInstance instance = this.plugin.getInstance();

    private final UserRegistry userRegistry = this.instance.getUserRegistry();

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final ItemStack crystalItem = event.getCursor();
        final ItemStack item = event.getCurrentItem();

        if (item == null || item.isEmpty()) return;

        final Optional<CustomItem> crystal = this.itemManager.getItem("crystal_item");

        if (crystal.isEmpty()) return;

        final CustomItem customItem = crystal.get();

        if (!customItem.isItem(crystalItem) || !customItem.isItem(item)) return;

        //int maxEnchants = this.starter.getCrazyManager().getPlayerMaxEnchantments(player);
        int maxEnchants = 0;
        int enchAmount = this.instance.getEnchantmentAmount(item, this.options.isCheckVanillaLimit());
        //int baseEnchants = this.starter.getCrazyManager().getPlayerBaseEnchantments(player);
        int baseEnchants = 0;
        //int limiter = this.starter.getCrazyManager().getEnchantmentLimiter(item);
        int limiter = 0;


        event.setCancelled(true);

        final User user = this.userRegistry.getUser(player);

        if (enchAmount >= maxEnchants) {
            user.sendMessage(MessageKeys.hit_enchantment_max);

            return;
        }

        if ((baseEnchants - limiter) >= maxEnchants) {
            user.sendMessage(MessageKeys.hit_slot_max);

            return;
        }

        crystalItem.setAmount(crystalItem.getAmount() - 1);

        event.getCursor().setAmount(crystalItem.getAmount());

        //event.setCurrentItem(this.starter.getCrazyManager().changeEnchantmentLimiter(item, -1));

        user.sendMessage(MessageKeys.hit_slot_max, new HashMap<>(4) {{
            put("{slot}", String.valueOf(-(limiter - 1)));
            put("{max_enchants}", String.valueOf(maxEnchants));
            put("{enchant_amount}", String.valueOf(enchAmount));
            put("{base_enchants}", String.valueOf(baseEnchants));
        }});
    }
}