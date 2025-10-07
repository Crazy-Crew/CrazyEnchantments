package com.badbones69.crazyenchantments.paper.api.builders.types;

import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.builders.InventoryBuilder;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsManager;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class BaseMenu extends InventoryBuilder {

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final EnchantmentBookSettings bookSettings = this.starter.getEnchantmentBookSettings();

    public BaseMenu(@NotNull final Player player, final int size, @NotNull final String title) {
        super(player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        if (getEnchantmentType() != null) {
            List<CEnchantment> enchantments = getEnchantmentType().getEnchantments();

            ItemBuilder book = this.bookSettings.getNormalBook().setGlow(true);

            for (CEnchantment enchantment : enchantments) {
                if (enchantment.isActivated()) {
                    getInventory().addItem(book.setName(enchantment.getInfoName()).setLore(enchantment.getInfoDescription()).build());
                }
            }

            getInventory().setItem(getSize() - 1, KitsManager.getBackRight());
        } else {
            MenuManager.getEnchantmentTypes().forEach(key -> getInventory().setItem(key.getSlot(), key.getDisplayItem()));
        }

        return this;
    }

    public static class InfoMenuListener implements Listener {

        @EventHandler(ignoreCancelled = true)
        public void onInfoClick(InventoryClickEvent event) {
            if (!(event.getInventory().getHolder() instanceof BaseMenu holder)) return;

            event.setCancelled(true);

            Player player = holder.getPlayer();

            ItemStack itemStack = event.getCurrentItem();

            if (itemStack == null) return;

            final PersistentDataContainerView view = itemStack.getPersistentDataContainer();

            if (view.has(DataKeys.back_left.getNamespacedKey()) || view.has(DataKeys.back_right.getNamespacedKey())) {
                MenuManager.openInfoMenu(player);

                return;
            }

            for (EnchantmentType enchantmentType : MenuManager.getEnchantmentTypes()) {
                if (itemStack.isSimilar(enchantmentType.getDisplayItem())) {
                    MenuManager.openInfoMenu(player, enchantmentType);

                    return;
                }
            }
        }
    }
}