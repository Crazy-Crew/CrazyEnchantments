package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.ryderbelserion.common.api.enums.messages.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlotCrystalListener implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyPlatform platform = this.plugin.getPlatform();

    private final EnchantmentBookSettings bookSettings = this.platform.getBookSettings();

    private final CrazyManager crazyManager = this.plugin.getCrazyManager();

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        final ItemStack crystalItem = event.getCursor();
        final ItemStack item = event.getCurrentItem();
        final List<Material> enchantableMaterials = new ArrayList<>();

        if (item == null || item.isEmpty() || !isSlotCrystal(crystalItem) || isSlotCrystal(item)) return;

        int maxEnchants = this.crazyManager.getPlayerMaxEnchantments(player);
        int enchAmount = this.bookSettings.getEnchantmentAmount(item, this.crazyManager.checkVanillaLimit());
        int baseEnchants = this.crazyManager.getPlayerBaseEnchantments(player);
        int limiter = this.crazyManager.getEnchantmentLimiter(item);

        event.setCancelled(true);

        if (enchAmount >= maxEnchants) {
            Messages.hit_enchantment_max.sendMessage(player);

            return;
        }

        if ((baseEnchants - limiter) >= maxEnchants) {
            Messages.max_slots_unlocked.sendMessage(player);

            return;
        }

        for (EnchantmentType enchantmentType : MenuManager.getEnchantmentTypes()) {
            enchantableMaterials.addAll(enchantmentType.getEnchantableMaterials());
        }

        if (!enchantableMaterials.contains(item.getType())) return;

        crystalItem.setAmount(crystalItem.getAmount() - 1);
        event.getCursor().setAmount(crystalItem.getAmount());
        event.setCurrentItem(this.crazyManager.changeEnchantmentLimiter(item, -1));

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%slot%", String.valueOf(-(limiter - 1)));
        placeholders.put("%maxEnchants%", String.valueOf(maxEnchants));
        placeholders.put("%enchantAmount%", String.valueOf(enchAmount));
        placeholders.put("baseEnchants", String.valueOf(baseEnchants));

        Messages.applied_slot_crystal.sendMessage(player, placeholders);
    }

    private boolean isSlotCrystal(ItemStack crystalItem) {
        if (crystalItem == null || crystalItem.isEmpty()) return false;

        return crystalItem.getPersistentDataContainer().has(DataKeys.slot_crystal.getNamespacedKey());
    }
}