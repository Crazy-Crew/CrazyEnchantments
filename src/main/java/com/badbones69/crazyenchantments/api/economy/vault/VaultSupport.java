package com.badbones69.crazyenchantments.api.economy.vault;

import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.PluginSupport;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultSupport {
    
    private static Economy vault = null;

    private static CrazyManager crazyManager = CrazyManager.getInstance();
    
    public static Economy getVault() {
        return vault;
    }
    
    public static void loadVault() {
        if (PluginSupport.SupportedPlugins.VAULT.isPluginLoaded(crazyManager.getPlugin())) {
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                vault = rsp.getProvider();
            }
        }
    }
}