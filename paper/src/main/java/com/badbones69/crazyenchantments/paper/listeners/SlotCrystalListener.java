package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.managers.ConfigManager;
import com.badbones69.crazyenchantments.paper.managers.items.ItemManager;
import com.badbones69.crazyenchantments.paper.managers.items.interfaces.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Optional;

public class SlotCrystalListener implements Listener {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final ItemManager itemManager = this.plugin.getItemManager();

    private final ConfigManager options = this.plugin.getOptions();

    private final CrazyInstance instance = this.plugin.getInstance();

    private final Starter starter = this.plugin.getStarter();

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

        int maxEnchants = this.starter.getCrazyManager().getPlayerMaxEnchantments(player);
        int enchAmount = this.instance.getEnchantmentAmount(item, this.options.isCheckVanillaLimit());
        int baseEnchants = this.starter.getCrazyManager().getPlayerBaseEnchantments(player);
        int limiter = this.starter.getCrazyManager().getEnchantmentLimiter(item);

        event.setCancelled(true);

        if (enchAmount >= maxEnchants) {
            player.sendMessage(Messages.HIT_ENCHANTMENT_MAX.getMessage());

            return;
        }

        if ((baseEnchants - limiter) >= maxEnchants) {
            player.sendMessage(Messages.MAX_SLOTS_UNLOCKED.getMessage());

            return;
        }

        crystalItem.setAmount(crystalItem.getAmount() - 1);

        event.getCursor().setAmount(crystalItem.getAmount());

        event.setCurrentItem(this.starter.getCrazyManager().changeEnchantmentLimiter(item, -1));

        player.sendMessage(Messages.APPLIED_SLOT_CRYSTAL.getMessage(new HashMap<>(4) {{
            put("%slot%", String.valueOf(-(limiter - 1)));
            put("%maxEnchants%", String.valueOf(maxEnchants));
            put("%enchantAmount%", String.valueOf(enchAmount));
            put("baseEnchants", String.valueOf(baseEnchants));
        }}));
    }
}
