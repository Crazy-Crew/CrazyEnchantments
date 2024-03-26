package com.badbones69.crazyenchantments.paper.listeners;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenchantments.ConfigManager;
import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.objects.other.ItemBuilder;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.platform.impl.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;

public class SlotCrystalListener implements Listener {

    private static ItemStack slot_crystal;

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);
    private final @NotNull Starter starter = this.plugin.getStarter();
    private final @NotNull EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    public void load() {
        SettingsManager config = ConfigManager.getConfig();

        slot_crystal = new ItemBuilder()
                .setMaterial(config.getProperty(Config.slot_crystal_item))
                .setName(config.getProperty(Config.slot_crystal_name))
                .setLore(config.getProperty(Config.slot_crystal_lore))
                .setGlow(config.getProperty(Config.slot_crystal_glowing)).build();

        ItemMeta meta = slot_crystal.getItemMeta();
        meta.getPersistentDataContainer().set(DataKeys.slot_crystal.getNamespacedKey(), PersistentDataType.BOOLEAN, true);

        slot_crystal.setItemMeta(meta);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack crystalItem = event.getCursor();
        ItemStack item = event.getCurrentItem();

        if (item == null || item.isEmpty() || !isSlotCrystal(crystalItem) || isSlotCrystal(item)) return;

        int maxEnchants = this.starter.getCrazyManager().getPlayerMaxEnchantments(player);
        int enchAmount = this.enchantmentBookSettings.getEnchantmentAmount(item, this.starter.getCrazyManager().checkVanillaLimit());
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

    private boolean isSlotCrystal(ItemStack crystalItem) {
        if (crystalItem == null || !crystalItem.hasItemMeta() || crystalItem.isEmpty()) return false;

        return crystalItem.getItemMeta().getPersistentDataContainer().has(DataKeys.slot_crystal.getNamespacedKey());
    }

    public ItemStack getSlotCrystal() {
        return slot_crystal.clone();
    }

    public ItemStack getSlotCrystal(int amount) {
        ItemStack itemStack = getSlotCrystal();

        itemStack.setAmount(amount);

        return itemStack;
    }
}