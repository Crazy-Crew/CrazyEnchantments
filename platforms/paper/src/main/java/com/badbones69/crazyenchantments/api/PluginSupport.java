package com.badbones69.crazyenchantments.api;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.support.factions.FactionsUUIDSupport;
import com.badbones69.crazyenchantments.api.support.interfaces.claims.FactionsVersion;
import com.badbones69.crazyenchantments.api.support.claims.GriefPreventionSupport;
import com.badbones69.crazyenchantments.api.support.claims.TownySupport;
import com.badbones69.crazyenchantments.api.support.claims.SuperiorSkyBlockSupport;
import com.badbones69.crazyenchantments.utilities.WorldGuardUtils;
import com.badbones69.crazyenchantments.utilities.misc.ColorUtils;
import com.google.common.collect.Maps;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import java.util.List;
import java.util.Map;

public class PluginSupport {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private FactionsVersion factionsVersion = null;

    private WorldGuardUtils worldGuardUtils;

    private final Map<SupportedPlugins, Boolean> cachedPlugins = Maps.newHashMap();

    public void initializeWorldGuard() {
        if (SupportedPlugins.WORLDEDIT.isPluginLoaded() && SupportedPlugins.WORLDEDIT.isPluginLoaded()) {
            this.worldGuardUtils = new WorldGuardUtils();
            this.worldGuardUtils.init();
        }
    }

    public boolean inTerritory(Player player) {
        if (factionsVersion != null && factionsVersion.inTerritory(player)) return true;

        return SupportedPlugins.SUPERIORSKYBLOCK.isPluginLoaded() && starter.getSuperiorSkyBlockSupport().inTerritory(player);
    }

    public boolean isFriendly(Entity pEntity, Entity oEntity) {
        if (pEntity instanceof Player player && oEntity instanceof Player otherPlayer) {

            if (factionsVersion != null && factionsVersion.isFriendly(player, otherPlayer)) return true;

            if (SupportedPlugins.SUPERIORSKYBLOCK.isPluginLoaded() && starter.getSuperiorSkyBlockSupport().isFriendly(player, otherPlayer)) return true;

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
        return !SupportedPlugins.WORLDEDIT.isPluginLoaded() || !SupportedPlugins.WORLDGUARD.isPluginLoaded() || this.worldGuardUtils.getWorldGuardSupport().allowsPVP(location);
    }

    public boolean allowDestruction(Location location) {
        return !SupportedPlugins.WORLDEDIT.isPluginLoaded() || !SupportedPlugins.WORLDGUARD.isPluginLoaded() || this.worldGuardUtils.getWorldGuardSupport().allowsBreak(location);
    }

    public boolean allowExplosion(Location location) {
        return !SupportedPlugins.WORLDEDIT.isPluginLoaded() || !SupportedPlugins.WORLDGUARD.isPluginLoaded() || this.worldGuardUtils.getWorldGuardSupport().allowsExplosions(location);
    }

    public void updateHooks() {
        cachedPlugins.clear();

        for (SupportedPlugins supportedPlugin : SupportedPlugins.values()) {
            if (supportedPlugin.isPluginLoaded() && supportedPlugin.getLoadedPlugin().isEnabled()) {

                String website = supportedPlugin.getLoadedPlugin().getDescription().getWebsite();
                List<String> author = supportedPlugin.getLoadedPlugin().getDescription().getAuthors();
                String name = supportedPlugin.getLoadedPlugin().getDescription().getName();
                String main = supportedPlugin.getLoadedPlugin().getDescription().getMain();

                switch (supportedPlugin) {
                    case FACTIONS_UUID -> {
                        if (website != null) supportedPlugin.addPlugin(website.equals("https://www.spigotmc.org/resources/factionsuuid.1035/"));
                    }

                    case MCMMO -> {
                        if (website != null) supportedPlugin.addPlugin(website.equals("https://www.mcmmo.org"));
                    }

                    case SILK_SPAWNERS -> supportedPlugin.addPlugin(name.equals("SilkSpawners"));

                    case SILK_SPAWNERS_V2 -> {
                        supportedPlugin.addPlugin(name.equals("SilkSpawners_v2"));

                        plugin.getLogger().warning("Silk Spawners v2 by CANDC does not have any support yet.");
                    }

                    default -> supportedPlugin.addPlugin(true);
                }

                updateClaimHooks(supportedPlugin);
            } else {
                supportedPlugin.addPlugin(false);
            }
        }

        printHooks();
    }

    public WorldGuardUtils getWorldGuardUtils() {
        return this.worldGuardUtils;
    }

    public void updateClaimHooks(SupportedPlugins supportedPlugin) {
        switch (supportedPlugin) {
            case GRIEF_PREVENTION -> factionsVersion = new GriefPreventionSupport();
            case TOWNYADVANCED -> factionsVersion = new TownySupport();
            case FACTIONS_UUID -> factionsVersion = new FactionsUUIDSupport();
        }
    }

    public void printHooks() {
        if (cachedPlugins.isEmpty()) updateHooks();

        plugin.getLogger().info(ColorUtils.color("&8&l=== &e&lCrazyEnchantment Hook Status &8&l==="));

        cachedPlugins.keySet().forEach(value -> {
            if (value.isPluginLoaded()) {
                plugin.getLogger().info(ColorUtils.color("&6&l" + value.name() + " &a&lFOUND"));
            } else {
                plugin.getLogger().info(ColorUtils.color("&6&l" + value.name() + " &c&lNOT FOUND"));
            }
        });
    }

    public enum SupportedPlugins {
        // Economy Plugins
        VAULT("Vault"),

        // Spawner Plugins
        SILK_SPAWNERS("SilkSpawners"),
        SILK_SPAWNERS_V2("SilkSpawners_V2"),

        // Random Plugins
        MCMMO("McMMO"),

        // Stacker Plugins

        // WildStacker by Ome_R
        // WILD_STACKER("WildStacker"),

        // Anti Cheats
        SPARTAN("Spartan"),
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

        TOWNYADVANCED("Towny"),

        PLOT_SQUARED("PlotSquared"),

        // Custom Items
        ORAXEN("Oraxen");

        private final String pluginName;

        SupportedPlugins(String pluginName) {
            this.pluginName = pluginName;
        }

        private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

        private final Starter starter = plugin.getStarter();

        private final PluginSupport pluginSupport = starter.getPluginSupport();

        public boolean isPluginLoaded() {
            return plugin.getServer().getPluginManager().getPlugin(pluginName) != null;
        }

        public Plugin getLoadedPlugin() {
            return plugin.getServer().getPluginManager().getPlugin(pluginName);
        }

        public boolean isCachedPluginLoaded() {
            return pluginSupport.cachedPlugins.get(this);
        }

        public void addPlugin(boolean value) {
            pluginSupport.cachedPlugins.put(this, value);
        }

        public void removePlugin() {
            pluginSupport.cachedPlugins.remove(this);
        }

        public boolean isPluginEnabled() {
            return pluginSupport.cachedPlugins.get(this);
        }
    }
}