package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.FileManager;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class SlotCrystalListener implements Listener {
    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    private static ItemStack slot_crystal;


    public void load() {
        FileConfiguration config = FileManager.Files.CONFIG.getFile();
        slot_crystal = new ItemBuilder()
                .setMaterial(Objects.requireNonNull(config.getString("Settings.Slot_Crystal.Item")))
                .setName(config.getString("Settings.Slot_Crystal.Name"))
                .setLore(config.getStringList("Settings.Slot_Crystal.Lore"))
                .setGlow(config.getBoolean("Settings.Slot_Crystal.Glowing")).build();
        ItemMeta meta = slot_crystal.getItemMeta();
        meta.getPersistentDataContainer().set(DataKeys.SLOT_CRYSTAL.getKey(), PersistentDataType.BOOLEAN, true);
        slot_crystal.setItemMeta(meta);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack crystalItem = event.getCursor();
        ItemStack item = event.getCurrentItem();

        if (item == null || item.isEmpty() || !isSlotCrystal(crystalItem) || isSlotCrystal(item)) return;

        int maxEnchants = starter.getCrazyManager().getPlayerMaxEnchantments(player);
        int enchAmount = enchantmentBookSettings.getEnchantmentAmount(item, starter.getCrazyManager().checkVanillaLimit());
        int baseEnchants = starter.getCrazyManager().getPlayerBaseEnchantments(player);
        int limiter = starter.getCrazyManager().getEnchantmentLimiter(item);

        if (enchAmount >= maxEnchants || (baseEnchants - limiter) >= maxEnchants) return;

        event.setCancelled(true);

        crystalItem.setAmount(crystalItem.getAmount() - 1);
        event.getCursor().setAmount(crystalItem.getAmount());
        event.setCurrentItem(starter.getCrazyManager().changeEnchantmentLimiter(item, -1));

    }

    private boolean isSlotCrystal(ItemStack crystalItem) {
        if (crystalItem == null || !crystalItem.hasItemMeta() || crystalItem.isEmpty()) return false;
        return crystalItem.getItemMeta().getPersistentDataContainer().has(DataKeys.SLOT_CRYSTAL.getKey());
    }

    public ItemStack getSlotCrystal() {
        return slot_crystal.clone();
    }

}
