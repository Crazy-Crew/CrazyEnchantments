package me.badbones69.crazyenchantments.api.currencyapi;

import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultSupport {
    
    private static Economy vault = null;
    
    public static Economy getVault() {
        return vault;
    }
    
    public static void loadVault() {
        if (SupportedPlugins.VAULT.isPluginLoaded()) {
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                vault = rsp.getProvider();
            }
        }
    }
    
}