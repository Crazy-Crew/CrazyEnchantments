package com.badbones69.crazyenchantments.paper.managers.currency.types.plugins;

import com.badbones69.crazyenchantments.paper.managers.configs.types.currency.VaultConfig;
import com.badbones69.crazyenchantments.paper.managers.currency.interfaces.ICurrency;
import com.badbones69.crazyenchantments.paper.support.PluginSupport;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenchantments.constants.MessageKeys;

import java.util.Map;

public class VaultCurrency extends ICurrency {

    private final VaultConfig config;

    public VaultCurrency() {
        this.config = this.configManager.getVaultConfig();
    }

    private Economy vault;

    @Override
    public void add(@NotNull final Player player, final double amount) {
        if (this.vault == null || !isEnabled()) {
            return;
        }

        this.vault.depositPlayer(player, amount);
    }

    @Override
    public void minus(@NotNull final Player player, final double amount) {
        if (this.vault == null || !isEnabled()) {
            return;
        }

        this.vault.withdrawPlayer(player, amount);
    }

    @Override
    public boolean hasAmount(@NotNull final Player player, final double amount) {
        if (this.vault == null || !isEnabled()) {
            return false;
        }

        return this.vault.has(player, amount);
    }

    @Override
    public double getAmount(@NotNull final Player player) {
        if (this.vault == null || !isEnabled()) {
            return 0;
        }

        return this.vault.getBalance(player);
    }

    @Override
    public void failed(@NotNull final Player player, @NotNull final Map<String, String> placeholders) {
        this.userRegistry.getUser(player).sendMessage(MessageKeys.need_more_money, placeholders);
    }

    @Override
    public final boolean isEnabled() {
        return this.config.isVaultEnabled() && PluginSupport.SupportedPlugins.VAULT.isPluginEnabled();
    }

    @Override
    public @NotNull final ICurrency init() {
        if (!isEnabled()) return this;

        final RegisteredServiceProvider<Economy> economy = this.server.getServicesManager().getRegistration(Economy.class);

        if (economy != null) {
            this.vault = economy.getProvider();
        }

        return this;
    }
}