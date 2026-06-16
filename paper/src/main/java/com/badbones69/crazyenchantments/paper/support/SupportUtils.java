package com.badbones69.crazyenchantments.paper.support;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.support.crops.VanillaCropSupport;
import com.badbones69.crazyenchantments.paper.support.api.enums.PluginType;
import com.badbones69.crazyenchantments.paper.support.api.interfaces.CropSupport;
import com.badbones69.crazyenchantments.paper.support.api.interfaces.TerritorySupport;
import com.badbones69.crazyenchantments.paper.support.claim.lands.LandsImpl;
import com.badbones69.crazyenchantments.paper.support.claim.plotsquared.PlotSquaredImpl;
import com.badbones69.crazyenchantments.paper.support.claim.towny.TownyImpl;
import com.badbones69.crazyenchantments.paper.support.factions.uuid.FactionsUUIDImpl;
import com.badbones69.crazyenchantments.paper.support.parties.mcmmo.McMMOImpl;
import com.badbones69.crazyenchantments.paper.support.protection.griefprevention.GriefPreventionImpl;
import com.badbones69.crazyenchantments.paper.support.protection.worldguard.WorldGuardImpl;
import com.badbones69.crazyenchantments.paper.support.skyblock.superor.SuperiorSkyBlockImpl;
import com.ryderbelserion.fusion.core.api.FusionKey;
import com.ryderbelserion.fusion.core.api.registry.mods.ModRegistry;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import java.util.Collection;
import java.util.List;

public class SupportUtils {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final FusionPaper fusion = this.plugin.getFusion();

    private final ModRegistry modRegistry = this.fusion.getModRegistry();

    private final Server server = this.plugin.getServer();

    private final ServicesManager servicesManager = this.server.getServicesManager();

    private CropSupport cropSupport;

    public void init() {
        final ComponentLogger logger = this.plugin.getComponentLogger();
        final boolean isVerbose = this.fusion.isVerbose();

        List.of(
                // protection plugins
                new GriefPreventionImpl(),
                new WorldGuardImpl(),

                // party plugins
                new McMMOImpl(),

                // faction plugins
                new FactionsUUIDImpl(),

                // plot plugins
                new PlotSquaredImpl(),

                // claim plugins
                new TownyImpl(),
                new LandsImpl(),

                // skyblock plugins
                new SuperiorSkyBlockImpl()
        ).forEach(plugin -> {
            final FusionKey key = plugin.getKey();
            final String name = key.getValue();

            this.modRegistry.addMod(key, plugin);

            if (isVerbose) {
                if (plugin.isEnabled()) {
                    logger.info(this.fusion.asComponent(Audience.empty(), "<bold><gold>" + name + " <green>FOUND"));
                } else {
                    logger.info(this.fusion.asComponent(Audience.empty(), "<bold><gold>" + name + " <red>NOT FOUND"));
                }
            }
        });

        this.cropSupport = new VanillaCropSupport();
    }

    public @NonNull final CropSupport getCropSupport() {
        return this.cropSupport;
    }

    public boolean isFriendly(@NonNull final Entity player, @NonNull final Entity target) {
        final Collection<RegisteredServiceProvider<TerritorySupport>> registry = this.servicesManager.getRegistrations(TerritorySupport.class);

        for (final RegisteredServiceProvider<TerritorySupport> instance : registry) {
            final TerritorySupport provider = instance.getProvider();

            if (!provider.isFriendly(player, target)) continue;

            return true;
        }

        return false;
    }

    public boolean isTerritory(@NonNull final String region, @NonNull final Location location) {
        final Collection<RegisteredServiceProvider<TerritorySupport>> registry = this.servicesManager.getRegistrations(TerritorySupport.class);

        for (final RegisteredServiceProvider<TerritorySupport> instance : registry) {
            final TerritorySupport provider = instance.getProvider();

            if (provider.getPluginType() != PluginType.WORLDGUARD) continue;

            if (!provider.isTerritory(region, location)) continue;

            return true;
        }

        return false;
    }

    public boolean isTerritory(@NonNull final Player player, @NonNull final Location location) {
        final Collection<RegisteredServiceProvider<TerritorySupport>> registry = this.servicesManager.getRegistrations(TerritorySupport.class);

        for (final RegisteredServiceProvider<TerritorySupport> instance : registry) {
            final TerritorySupport provider = instance.getProvider();

            if (!provider.isTerritory(player, location)) continue;

            return true;
        }

        return false;
    }

    public boolean isOwner(@NonNull final Player player) {
        final Collection<RegisteredServiceProvider<TerritorySupport>> registry = this.servicesManager.getRegistrations(TerritorySupport.class);

        for (final RegisteredServiceProvider<TerritorySupport> instance : registry) {
            final TerritorySupport provider = instance.getProvider();

            if (!provider.isOwner(player)) continue;

            return true;
        }

        return false;
    }

    public boolean isMember(@NonNull final Player player) {
        final Collection<RegisteredServiceProvider<TerritorySupport>> registry = this.servicesManager.getRegistrations(TerritorySupport.class);

        for (final RegisteredServiceProvider<TerritorySupport> instance : registry) {
            final TerritorySupport provider = instance.getProvider();

            if (!provider.isMember(player)) continue;

            return true;
        }

        return false;
    }

    public boolean isCombatEnabled(@NonNull final Location location) {
        final Collection<RegisteredServiceProvider<TerritorySupport>> registry = this.servicesManager.getRegistrations(TerritorySupport.class);

        for (final RegisteredServiceProvider<TerritorySupport> instance : registry) {
            final TerritorySupport provider = instance.getProvider();

            if (provider.isCombatEnabled(location)) continue;

            return false;
        }

        return true;
    }

    public boolean isCombatEnabled(@NonNull final Player player) {
        return isCombatEnabled(player.getLocation());
    }
}