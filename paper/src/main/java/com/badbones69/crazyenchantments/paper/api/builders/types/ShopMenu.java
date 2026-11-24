package com.badbones69.crazyenchantments.paper.api.builders.types;

import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.builders.InventoryBuilder;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.paper.api.managers.ShopManager;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class ShopMenu extends InventoryBuilder {

    private final CrazyManager crazyManager = this.plugin.getCrazyManager();

    private final CurrencyAPI currencyAPI = this.crazyManager.getCurrencyAPI();

    private final ShopManager shopManager = this.crazyManager.getShopManager();

    public ShopMenu(Player player, int size, String title) {
        super(player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        HashMap<String, String> placeholders = new HashMap<>();

        final Player player = getPlayer();

        for (Currency currency : Currency.values()) {
            placeholders.put("%" + currency.getName() + "%", String.valueOf(this.currencyAPI.getCurrency(player, currency)));
        }

        for (Map.Entry<ItemBuilder, Integer> itemBuilders : this.shopManager.getCustomizerItems().entrySet()) {
            itemBuilders.getKey().setNamePlaceholders(placeholders).setLorePlaceholders(placeholders);
            getInventory().setItem(itemBuilders.getValue(), itemBuilders.getKey().build(player));
        }

        this.shopManager.getShopItems().keySet().forEach(itemBuilder -> getInventory().setItem(this.shopManager.getShopItems().get(itemBuilder), itemBuilder.build(player)));

        return this;
    }
}