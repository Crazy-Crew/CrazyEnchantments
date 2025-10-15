package com.badbones69.crazyenchantments.paper.managers.currency;

import com.badbones69.crazyenchantments.paper.managers.currency.enums.Currency;
import com.badbones69.crazyenchantments.paper.managers.currency.interfaces.ICurrency;
import com.badbones69.crazyenchantments.paper.managers.currency.types.ExpCurrency;
import com.badbones69.crazyenchantments.paper.managers.currency.types.LevelCurrency;
import com.badbones69.crazyenchantments.paper.managers.currency.types.plugins.VaultCurrency;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CurrencyManager {

    private final Map<Currency, ICurrency> currencies = new HashMap<>();

    public void init() {
        final VaultCurrency currency = new VaultCurrency();

        if (currency.isEnabled()) {
            this.currencies.put(Currency.VAULT, currency.init());
        } else {
            this.currencies.remove(Currency.VAULT);
        }

        final LevelCurrency levelCurrency = new LevelCurrency();

        if (levelCurrency.isEnabled()) {
            this.currencies.put(Currency.XP_LEVEL, levelCurrency.init());
        } else {
            this.currencies.remove(Currency.XP_LEVEL);
        }

        final ExpCurrency expCurrency = new ExpCurrency();

        if (expCurrency.isEnabled()) {
            this.currencies.put(Currency.XP_TOTAL, expCurrency.init());
        } else {
            this.currencies.remove(Currency.XP_TOTAL);
        }
    }

    public void addAmount(@NotNull final Currency currency, @NotNull final Player player, final int cost) {
        if (!this.currencies.containsKey(currency)) {
            return;
        }

        this.currencies.get(currency).add(player, cost);
    }

    public void takeAmount(@NotNull final Currency currency, @NotNull final Player player, final int cost) {
        if (!this.currencies.containsKey(currency)) {
            return;
        }

        this.currencies.get(currency).minus(player, cost);
    }

    public void failed(@NotNull final Currency currency, @NotNull final Player player, final int cost) {
        if (!this.currencies.containsKey(currency)) {
            return;
        }

        final ICurrency cache = this.currencies.get(currency);

        cache.failed(player, new HashMap<>() {{
            put("{money_needed}", String.valueOf(cost - cache.getAmount(player)));
            put("{currency}", currency.getName());
            put("{cost}", String.valueOf(cost));
        }});
    }

    public double getAmount(@NotNull final Currency currency, @NotNull final Player player) {
        if (!this.currencies.containsKey(currency)) {
            return 0;
        }

        return this.currencies.get(currency).getAmount(player);
    }

    public boolean hasAmount(@NotNull final Currency currency, @NotNull final Player player, final int cost) {
        if (!this.currencies.containsKey(currency)) {
            return false;
        }

        return this.currencies.get(currency).hasAmount(player, cost);
    }

    public @NotNull final Map<Currency, ICurrency> getCurrencies() {
        return Collections.unmodifiableMap(this.currencies);
    }
}