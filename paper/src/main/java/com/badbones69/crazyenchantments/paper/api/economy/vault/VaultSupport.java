package com.badbones69.crazyenchantments.paper.api.economy.vault;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultSupport {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private Economy vault = null;

    public Economy getVault() {
        return vault;
    }

    public void loadVault() {
        RegisteredServiceProvider<Economy> serviceProvider = plugin.getServer().getServicesManager().getRegistration(Economy.class);

        if (serviceProvider != null) this.vault = serviceProvider.getProvider();
    }
}