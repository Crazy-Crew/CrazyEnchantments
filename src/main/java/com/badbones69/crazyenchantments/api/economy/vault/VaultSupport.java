package com.badbones69.crazyenchantments.api.economy.vault;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.api.PluginSupport;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultSupport {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();
    
    private Economy vault = null;
    
    public Economy getVault() {
        return vault;
    }
    
    public void loadVault() {
        if (PluginSupport.SupportedPlugins.VAULT.isPluginLoaded()) {
            RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);

            if (rsp != null) vault = rsp.getProvider();
        }
    }
}