package com.badbones69.crazyenchantments.api;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.managers.WingsManager;
import com.badbones69.crazyenchantments.api.support.factions.FactionsUUIDSupport;
import com.badbones69.crazyenchantments.api.support.interfaces.claims.FactionsVersion;
import com.badbones69.crazyenchantments.api.support.interfaces.claims.WorldGuardVersion;
import com.badbones69.crazyenchantments.api.support.claims.GriefPreventionSupport;
import com.badbones69.crazyenchantments.api.support.claims.TownySupport;
import com.badbones69.crazyenchantments.api.support.claims.SuperiorSkyBlockSupport;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import java.util.HashMap;

public class PluginSupport {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    private final WingsManager wingsManager = plugin.getStarter().getWingsManager();

    private final SuperiorSkyBlockSupport superiorSkyBlockSupport = plugin.getSuperiorSkyBlockSupport();

    private FactionsVersion factionsVersion = null;

    public boolean inTerritory(Player player) {
        if (factionsVersion != null && factionsVersion.inTerritory(player)) return true;

        return SupportedPlugins.SUPERIORSKYBLOCK.isPluginLoaded() && superiorSkyBlockSupport.inTerritory(player);
    }

    public boolean isFriendly(Entity entity, Entity other) {
        if (entity instanceof Player || other instanceof Player) {
            assert entity instanceof Player;
            Player player = (Player) entity;
            Player otherEntity = (Player) other;

            if (factionsVersion != null && factionsVersion.isFriendly(player, otherEntity)) return true;

            if (SupportedPlugins.SUPERIORSKYBLOCK.isPluginLoaded() && superiorSkyBlockSupport.isFriendly(player, otherEntity)) return true;

            return SupportedPlugins.MCMMO.isPluginLoaded();
        }

        return false;
    }

    public boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }

        return false;
    }

    public boolean allowCombat(Location location) {
        if (SupportedPlugins.TOWNYADVANCED.isPluginLoaded() && !TownySupport.allowsCombat(location)) return false;
        return !SupportedPlugins.WORLDEDIT.isPluginLoaded() || !SupportedPlugins.WORLDGUARD.isPluginLoaded() || crazyManager.getWorldGuardSupport().allowsPVP(location);
    }

    public boolean allowDestruction(Location location) {
        return !SupportedPlugins.WORLDEDIT.isPluginLoaded() || !SupportedPlugins.WORLDGUARD.isPluginLoaded() || crazyManager.getWorldGuardSupport().allowsBreak(location);
    }

    public boolean allowExplosion(Location location) {
        return !SupportedPlugins.WORLDEDIT.isPluginLoaded() || !SupportedPlugins.WORLDGUARD.isPluginLoaded() || crazyManager.getWorldGuardSupport().allowsExplosions(location);
    }

    public boolean inWingsRegion(Player player) {
        if (!SupportedPlugins.WORLDEDIT.isPluginLoaded() && !SupportedPlugins.WORLDGUARD.isPluginLoaded()) return true;

        WorldGuardVersion worldGuardVersion = crazyManager.getWorldGuardSupport();

        for (String region : wingsManager.getRegions()) {
            if (worldGuardVersion.inRegion(region, player.getLocation())) {
                return true;
            } else {
                if (wingsManager.canOwnersFly() && worldGuardVersion.isOwner(player)) return true;

                if (wingsManager.canMembersFly() && worldGuardVersion.isMember(player)) return true;
            }
        }

        return false;
    }

    private final Methods methods = plugin.getStarter().getMethods();

    private final HashMap<SupportedPlugins, Boolean> cachedPluginState = new HashMap<>();

    public void updateCachedPluginState() {
        if (!cachedPluginState.isEmpty()) cachedPluginState.clear();

        for (SupportedPlugins supportedPlugins : SupportedPlugins.values()) {

            Plugin grabbedPlugin = plugin.getServer().getPluginManager().getPlugin(supportedPlugins.pluginName);

            if (grabbedPlugin != null) {
                if (plugin.isEnabled()) {

                    String website = plugin.getDescription().getWebsite();

                    if (supportedPlugins == SupportedPlugins.FACTIONS_UUID) {
                        if (website != null) cachedPluginState.put(supportedPlugins, website.equals("https://www.spigotmc.org/resources/factionsuuid.1035/"));
                    } else {
                        cachedPluginState.put(supportedPlugins, true);
                        return;
                    }

                }
            } else {
                cachedPluginState.put(supportedPlugins, false);
            }
        }

        updateFactionsPlugins();
    }

    public void printHooks() {
        if (cachedPluginState.isEmpty()) updateCachedPluginState();

        plugin.getServer().getConsoleSender().sendMessage(methods.color("&4&lActive CrazyEnchantment Hooks:"));

        cachedPluginState.keySet().forEach(supportedsPlugins -> {
            if (supportedsPlugins.isPluginLoaded()) plugin.getServer().getConsoleSender().sendMessage(methods.color("&6&l " + supportedsPlugins.pluginName + " : &a&lENABLED"));
        });
    }

    private void updateFactionsPlugins() {
        for (SupportedPlugins supportedsPlugins : SupportedPlugins.values()) {
            if (supportedsPlugins.isPluginLoaded()) {
                switch (supportedsPlugins) {
                    case FACTIONS_UUID -> factionsVersion = new FactionsUUIDSupport();
                    case GRIEF_PREVENTION -> factionsVersion = new GriefPreventionSupport();
                    case TOWNYADVANCED -> factionsVersion = new TownySupport();
                    default -> {}
                }
            }
        }
    }

    public enum SupportedPlugins {
        // Economy Plugins
        VAULT("Vault"),

        // Spawner Plugins
        SILKSPAWNERS("SilkSpawners"),

        // Random Plugins
        MCMMO("McMMO"),

        // Stacker Plugins

        // WildStacker by Ome_R
        // WILD_STACKER("WildStacker"),

        // Anti Cheats
        //SPARTAN("Spartan"),
        NO_CHEAT_PLUS("NoCheatPlus"),
        //VULCAN("Vulcan"),

        // Faction Plugins
        FACTIONS_UUID("Factions"),

        GRIEF_PREVENTION("GriefPrevention"),

        // Sky Block Plugins
        SUPERIORSKYBLOCK("SuperiorSkyblock2"),

        // Region Protection
        WORLDGUARD("WorldGuard"),
        WORLDEDIT("WorldEdit"),

        TOWNYADVANCED("TownyAdvanced"),

        PLOT_SQUARED("PlotSquared"),

        // Custom Items
        ORAXEN("Oraxen");

        private final String pluginName;

        SupportedPlugins(String pluginName) {
            this.pluginName = pluginName;
        }

        private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

        public boolean isPluginLoaded() {
            return plugin.getServer().getPluginManager().getPlugin(pluginName) != null;
        }
    }
}