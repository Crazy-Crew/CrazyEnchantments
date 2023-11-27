package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
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

import java.util.Objects;

public class SlotCrystalListener implements Listener {
    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    private static ItemBuilder slot_crystal;


    public void load() {
        FileConfiguration config = FileManager.Files.CONFIG.getFile();
        slot_crystal = new ItemBuilder()
                .setMaterial(Objects.requireNonNull(config.getString("Settings.Slot_Crystal.Item")))
                .setName(config.getString("Settings.Slot_Crystal.Name"))
                .setLore(config.getStringList("Settings.Slot_Crystal.Lore"))
                .setGlow(config.getBoolean("Settings.Slot_Crystal.Glowing"));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack crystalItem = event.getCursor();
        ItemStack item = event.getCurrentItem();

        if (item == null) return;
        if (!isSlotCrystal(crystalItem)) return;

        int maxEnchants = crazyManager.getPlayerMaxEnchantments(player);
        int enchAmount = enchantmentBookSettings.getEnchantmentAmount(item, crazyManager.checkVanillaLimit());
        int baseEnchants = crazyManager.getPlayerBaseEnchantments(player);
        int limiter = crazyManager.getEnchantmentLimiter(item);

        if (enchAmount >= maxEnchants || (baseEnchants - limiter) >= maxEnchants) return;

        event.setCurrentItem(crazyManager.changeEnchantmentLimiter(item, - 1));
        event.getCursor().setType(Material.AIR);

    }

    private boolean isSlotCrystal(ItemStack crystalItem) {
        return crystalItem.getItemMeta().getPersistentDataContainer().has(DataKeys.SLOT_CRYSTAL.getKey());
    }

    public ItemStack getSlotCrystal() {
        return slot_crystal.build();
    }

}
