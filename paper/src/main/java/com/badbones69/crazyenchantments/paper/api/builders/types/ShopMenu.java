package com.badbones69.crazyenchantments.paper.api.builders.types;

import com.badbones69.crazyenchantments.paper.api.builders.gui.types.StaticInventory;
import com.badbones69.crazyenchantments.paper.managers.currency.enums.Currency;
import com.badbones69.crazyenchantments.paper.api.managers.ShopManager;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.Gui;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class ShopMenu extends StaticInventory {

    private final ShopManager shopManager = null;

    public ShopMenu(@NotNull final Player player, @NotNull final String title, final int size) {
        super(player, title, size);
    }

    private final Gui gui = getGui();

    @Override
    public void open() {
        final Map<String, String> placeholders = new HashMap<>();

        final Player player = getPlayer();

        for (final Currency currency : Currency.values()) {
            placeholders.put("{" + currency.getName() + "}", String.valueOf(this.currencyManager.getAmount(currency, player)));
        }

        for (final Map.Entry<ItemBuilder, Integer> builders : this.shopManager.getCustomizerItems().entrySet()) {
            final ItemBuilder itemBuilder = builders.getKey();
            final int slot = builders.getValue();

            this.gui.setItem(slot, new GuiItem(itemBuilder.setNamePlaceholders(placeholders).setLorePlaceholders(placeholders).build()));
        }

        final Map<ItemBuilder, Integer> cache = this.shopManager.getShopItems();

        for (final Map.Entry<ItemBuilder, Integer> builders : cache.entrySet()) {
            final ItemBuilder itemBuilder = builders.getKey();
            final int slot = builders.getValue();

            this.gui.setItem(slot, new GuiItem(itemBuilder.build()));
        }
    }
}