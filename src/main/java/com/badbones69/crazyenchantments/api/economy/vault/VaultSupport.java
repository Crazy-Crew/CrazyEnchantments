package com.badbones69.crazyenchantments.api.economy.vault;

import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.PluginSupport;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultSupport {
    
    private static Economy vault = null;

    private final static CrazyManager crazyManager = CrazyManager.getInstance();
    
    public static Economy getVault() {
        return vault;
    }
    
    public static void loadVault() {
        if (PluginSupport.SupportedPlugins.VAULT.isPluginLoaded()) {
            RegisteredServiceProvider<Economy> rsp = crazyManager.getPlugin().getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                vault = rsp.getProvider();
            }
        }
    }
}