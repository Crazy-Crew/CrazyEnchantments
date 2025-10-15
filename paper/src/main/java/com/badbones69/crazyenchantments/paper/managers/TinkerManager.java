package com.badbones69.crazyenchantments.paper.managers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.managers.currency.enums.Currency;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.managers.currency.CurrencyManager;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class TinkerManager {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyInstance instance = this.plugin.getInstance();

    private final CurrencyManager currencyManager = this.instance.getCurrencyManager();

    public boolean takeExperience(@NotNull final Player player, @NotNull final Currency currency, @NotNull final EquipmentSlot slot) {
        final PlayerInventory inventory = player.getInventory();

        if (inventory.isEmpty()) return false;

        final ItemStack itemStack = inventory.getItem(slot);

        if (itemStack.isEmpty()) return false;

        final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

        if (!container.has(DataKeys.experience.getNamespacedKey())) return false;

        final int amount = container.getOrDefault(DataKeys.experience.getNamespacedKey(), PersistentDataType.INTEGER, 0);

        inventory.setItem(slot, null);

        this.currencyManager.addAmount(currency, player, amount);

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);

        return true;
    }
}