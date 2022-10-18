package com.badbones69.crazyenchantments.api.economy.vault;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultSupport {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private Economy vault = null;

    public Economy getVault() {
        return vault;
    }

    public void loadVault() {
        RegisteredServiceProvider<Economy> serviceProvider = plugin.getServer().getServicesManager().getRegistration(Economy.class);

        plugin.getLogger().info("Vault was found so support has been enabled.");

        if (serviceProvider != null) vault = serviceProvider.getProvider();
    }
}