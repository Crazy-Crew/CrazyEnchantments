package me.badbones69.crazyenchantments.multisupport;

import me.badbones69.crazyenchantments.api.CrazyManager;
import me.badbones69.crazyenchantments.api.managers.WingsManager;
import me.badbones69.crazyenchantments.multisupport.plotsquared.PlotSquaredVersion;
import me.badbones69.crazyenchantments.multisupport.worldguard.WorldGuardVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Support {
    
    private static final Support instance = new Support();

    //private static FactionPlugin factionPlugin = null;

    private final CrazyManager ce = CrazyManager.getInstance();

    private WingsManager wingsManager;

    private WorldGuardVersion worldGuardVersion;

    private PlotSquaredVersion plotSquaredVersion;
    
    public static Support getInstance() {
        return instance;
    }
    
    public void load() {
        wingsManager = ce.getWingsManager();
        worldGuardVersion = ce.getWorldGuardSupport();
        plotSquaredVersion = ce.getPlotSquaredSupport();
    }
    
    public boolean inTerritory(Player player) {

        /*//if (factionPlugin != null && factionPlugin.inTerritory(player)) {
        //    return true;
        //}

        //if (SupportedPlugins.SUPERIOR_SKYBLOCK.isPluginLoaded() && SuperiorSkyblockSupport.inTerritory(player)) {
        //    return true;
        //}**/

        return SupportedPlugins.PLOT_SQUARED.isPluginLoaded() && plotSquaredVersion.inTerritory(player);
    }
    
    public boolean isFriendly(Entity pEntity, Entity oEntity) {
        /*if (pEntity instanceof Player && oEntity instanceof Player) {
            //Player player = (Player) pEntity;
            //Player other = (Player) oEntity;

            // if (factionPlugin != null && factionPlugin.isFriendly(player, other)) {
            //    return true;
            //}

            //if (SupportedPlugins.SUPERIOR_SKYBLOCK.isPluginLoaded() && SuperiorSkyblockSupport.isFriendly(player, other)) {
            //    return true;
            //}

            //return SupportedPlugins.MCMMO.isPluginLoaded() && MCMMOParty.isFriendly(player, other);
            // return SupportedPlugins.MCMMO.isPluginLoaded();
        }
         **/
        return false;
    }
    
    public boolean isVanished(Entity p) {
        for (MetadataValue meta : p.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }
    
    public boolean allowsPVP(Location location) {

        //if (SupportedPlugins.TOWNY.isPluginLoaded() && !TownySupport.allowsPvP(location)) {
        //    return false;
        //}

        return !SupportedPlugins.WORLD_EDIT.isPluginLoaded() || !SupportedPlugins.WORLD_GUARD.isPluginLoaded() || worldGuardVersion.allowsPVP(location);
    }
    
    public boolean allowsBreak(Location location) {
        return !SupportedPlugins.WORLD_EDIT.isPluginLoaded() || !SupportedPlugins.WORLD_GUARD.isPluginLoaded() || worldGuardVersion.allowsBreak(location);
    }
    
    public boolean allowsExplosions(Location location) {
        return !SupportedPlugins.WORLD_EDIT.isPluginLoaded() || !SupportedPlugins.WORLD_GUARD.isPluginLoaded() || worldGuardVersion.allowsExplosions(location);
    }
    
    public boolean inWingsRegion(Player player) {
        if (SupportedPlugins.WORLD_EDIT.isPluginLoaded() && SupportedPlugins.WORLD_GUARD.isPluginLoaded()) {
            for (String region : wingsManager.getRegions()) {
                if (worldGuardVersion.inRegion(region, player.getLocation())) {
                    return true;
                } else {
                    if (wingsManager.canOwnersFly() && worldGuardVersion.isOwner(player)) {
                        return true;
                    }
                    if (wingsManager.canMembersFly() && worldGuardVersion.isMember(player)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /*public void noStack(Entity entity) {
        // if (SupportedPlugins.STACK_MOB.isPluginLoaded()) {
            // StackMobSupport.preventStacking(entity);
       // }
    // }**/
    
    public enum SupportedPlugins {

        // Spawners
        SILK_SPAWNERS("SilkSpawners"),

        // Anti Cheats
        SPARTAN("Spartan"),

        // Misc
        VAULT("Vault"),
        WORLD_EDIT("WorldEdit"),

        // Claim Plugins
        WORLD_GUARD("WorldGuard"),
        PLOT_SQUARED("PlotSquared");
        
        private final String name;
        private static final Map<SupportedPlugins, Boolean> cachedPluginState = new HashMap<>();
        
        SupportedPlugins(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public boolean isPluginLoaded() {
            return cachedPluginState.get(this);
        }
        
        public Plugin getPlugin() {
            return Bukkit.getServer().getPluginManager().getPlugin(name);
        }
        
        /**
         * Used to update the states of plugins CE hooks into.
         */
        public static void updatePluginStates() {
            cachedPluginState.clear();
            for (SupportedPlugins supportedPlugin : values()) {
                Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(supportedPlugin.name);
                if (plugin != null && plugin.isEnabled()) {
                    List<String> authors = plugin.getDescription().getAuthors();
                    String version = plugin.getDescription().getVersion();
                    String website = plugin.getDescription().getWebsite() != null ? plugin.getDescription().getWebsite() : "";
                    if (supportedPlugin == SupportedPlugins.SILK_SPAWNERS) {
                        cachedPluginState.put(supportedPlugin, authors.contains("xGhOsTkiLLeRx"));
                    } else {
                        cachedPluginState.put(supportedPlugin, true);
                    }
                } else {
                    cachedPluginState.put(supportedPlugin, false);
                }
            }
        }
    }
}