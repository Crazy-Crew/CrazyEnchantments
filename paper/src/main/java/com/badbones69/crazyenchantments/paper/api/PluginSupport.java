package com.badbones69.crazyenchantments.paper.api;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.support.claims.GriefPreventionSupport;
import com.badbones69.crazyenchantments.paper.api.support.claims.TownySupport;
import com.badbones69.crazyenchantments.paper.api.support.factions.FactionsUUIDSupport;
import com.badbones69.crazyenchantments.paper.api.support.interfaces.claims.ClaimSupport;
import com.badbones69.crazyenchantments.paper.utilities.WorldGuardUtils;
import com.badbones69.crazyenchantments.paper.utilities.misc.ColorUtils;
import com.badbones69.crazyenchantments.paper.utilities.misc.EventUtils;
import com.gmail.nossr50.party.PartyManager;
import com.google.common.collect.Maps;
import org.bukkit.Location;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class PluginSupport {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private ClaimSupport claimPlugin = null;

    private WorldGuardUtils worldGuardUtils;

    private final Map<SupportedPlugins, Boolean> cachedPlugins = Maps.newHashMap();

    public void initializeWorldGuard() {
        if (SupportedPlugins.WORLDGUARD.isPluginLoaded() && SupportedPlugins.WORLDEDIT.isPluginLoaded()) {
            this.worldGuardUtils = new WorldGuardUtils();
            this.worldGuardUtils.init();
        }
    }

    public boolean inTerritory(Player player) {
        if (claimPlugin != null) return claimPlugin.inTerritory(player);

        return SupportedPlugins.SUPERIORSKYBLOCK.isPluginLoaded() && starter.getSuperiorSkyBlockSupport().inTerritory(player);
    }

    public boolean isFriendly(Entity pEntity, Entity oEntity) {
        if (!(pEntity instanceof Player player) || !(oEntity instanceof Player otherPlayer)) return false;

        if (claimPlugin != null) return claimPlugin.isFriendly(player, otherPlayer);

        if (SupportedPlugins.SUPERIORSKYBLOCK.isPluginLoaded() && starter.getSuperiorSkyBlockSupport().isFriendly(player, otherPlayer)) return true;

        if (SupportedPlugins.MCMMO.isPluginLoaded()) return PartyManager.inSameParty((Player) pEntity, (Player) oEntity);

        return false;

    }

    public boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }

        return false;
    }

    public boolean allowCombat(Location location) {
        if (SupportedPlugins.TOWNYADVANCED.isPluginLoaded()) return TownySupport.allowsCombat(location);
        return !SupportedPlugins.WORLDEDIT.isPluginLoaded() || !SupportedPlugins.WORLDGUARD.isPluginLoaded() || this.worldGuardUtils.getWorldGuardSupport().allowsPVP(location);
    }

    private boolean damageCheck(Player one, Player two) { // Would cause lag if used on player move event, but might be the only way to actually check.
        EntityDamageByEntityEvent event = Methods.entityDamageByEntityEvent(one, two, EntityDamageEvent.DamageCause.ENTITY_ATTACK, DamageType.PLAYER_ATTACK);
        event.setDamage(0);
        EventUtils.addIgnoredEvent(event);
        plugin.getServer().getPluginManager().callEvent(event);
        EventUtils.removeIgnoredEvent(event);
        return event.isCancelled();
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
                String name = supportedPlugin.getLoadedPlugin().getDescription().getName();

                switch (supportedPlugin) {
                    case FACTIONS_UUID -> {
                        if (website != null) supportedPlugin.addPlugin(website.equals("https://www.spigotmc.org/resources/factionsuuid.1035/"));
                    }

                    case MCMMO -> {
                        if (website != null) supportedPlugin.addPlugin(website.equals("https://www.mcmmo.org"));
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
            case GRIEF_PREVENTION -> claimPlugin = new GriefPreventionSupport();
            case TOWNYADVANCED -> claimPlugin = new TownySupport();
            case FACTIONS_UUID -> claimPlugin = new FactionsUUIDSupport();
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
            Plugin plugin1 = plugin.getServer().getPluginManager().getPlugin(pluginName);
            return plugin1 != null && plugin1.isEnabled();
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