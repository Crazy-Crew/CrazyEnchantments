package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.ryderbelserion.common.api.enums.messages.Messages;
import com.ryderbelserion.common.api.enums.Files;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlotCrystalListener implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    private static ItemStack slot_crystal;

    public void load() {
        final CommentedConfigurationNode config = Files.CONFIG.getConfiguration();

        slot_crystal = new ItemBuilder()
                .setMaterial(Methods.getNode(config, "Settings.Slot_Crystal.Item").getString("RED_WOOL"))
                .setName(Methods.getNode(config, "Settings.Slot_Crystal.Name").getString("Error getting slot crystal name."))
                .setItemModel(Methods.getNode(config, "Settings.Slot_Crystal.Model.Namespace").getString(""),
                        Methods.getNode(config, "Settings.Slot_Crystal.Model.Key").getString(""))
                .setLore(StringUtils.getStringList(config.node(Methods.getString("Settings.Slot_Crystal.Lore"))))
                .setGlow(Methods.getNode(config, "Settings.Slot_Crystal.Glowing").getBoolean(false))
                .addKey(DataKeys.slot_crystal.getNamespacedKey(), "").build();
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        final ItemStack crystalItem = event.getCursor();
        final ItemStack item = event.getCurrentItem();
        final List<Material> enchantableMaterials = new ArrayList<>();

        if (item == null || item.isEmpty() || !isSlotCrystal(crystalItem) || isSlotCrystal(item)) return;

        final CrazyManager crazyManager = this.starter.getCrazyManager();

        int maxEnchants = crazyManager.getPlayerMaxEnchantments(player);
        int enchAmount = this.enchantmentBookSettings.getEnchantmentAmount(item, crazyManager.checkVanillaLimit());
        int baseEnchants = crazyManager.getPlayerBaseEnchantments(player);
        int limiter = crazyManager.getEnchantmentLimiter(item);

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
        event.setCurrentItem(crazyManager.changeEnchantmentLimiter(item, -1));

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

    public ItemStack getSlotCrystal(final int amount) {
        final ItemStack itemStack = slot_crystal.clone();

        itemStack.setAmount(amount);

        return itemStack;
    }

    public ItemStack getSlotCrystal() {
        return getSlotCrystal(1);
    }
}