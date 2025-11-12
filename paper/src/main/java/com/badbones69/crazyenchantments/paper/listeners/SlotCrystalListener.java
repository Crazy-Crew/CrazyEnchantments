package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;

public class SlotCrystalListener implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    private static ItemStack slot_crystal;

    public void load() {
        FileConfiguration config = Files.CONFIG.getFile();

        slot_crystal = new ItemBuilder()
                .setMaterial(config.getString("Settings.Slot_Crystal.Item", "RED_WOOL"))
                .setName(config.getString("Settings.Slot_Crystal.Name", "Error getting name."))
                .setItemModel(config.getString("Settings.Slot_Crystal.Model.Namespace", ""), config.getString("Settings.Slot_Crystal.Model.Key", ""))
                .setLore(config.getStringList("Settings.Slot_Crystal.Lore"))
                .setGlow(config.getBoolean("Settings.Slot_Crystal.Glowing", false)).addKey(DataKeys.slot_crystal.getNamespacedKey(), "").build();
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
        if (crystalItem == null || crystalItem.isEmpty()) return false;

        return crystalItem.getPersistentDataContainer().has(DataKeys.slot_crystal.getNamespacedKey());
    }

    public ItemStack getSlotCrystal() {
        return slot_crystal.clone();
    }
}
