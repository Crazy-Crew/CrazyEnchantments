package com.badbones69.crazyenchantments.paper.support;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.support.crops.VanillaCropSupport;
import com.badbones69.crazyenchantments.paper.support.enums.PluginType;
import com.badbones69.crazyenchantments.paper.support.interfaces.CropSupport;
import com.badbones69.crazyenchantments.paper.support.interfaces.TerritorySupport;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import java.util.Collection;

public class SupportUtils {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Server server = this.plugin.getServer();

    private final ServicesManager servicesManager = this.server.getServicesManager();

    private CropSupport cropSupport;

    public void init() {
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

    public boolean canExplodeBlock(@NonNull final Location location) {
        final Collection<RegisteredServiceProvider<TerritorySupport>> registry = this.servicesManager.getRegistrations(TerritorySupport.class);

        for (final RegisteredServiceProvider<TerritorySupport> instance : registry) {
            final TerritorySupport provider = instance.getProvider();

            if (!provider.canExplodeBlock(location)) continue;

            return true;
        }

        return false;
    }

    public boolean canExplodeBlock(@NonNull final Player player) {
        return canExplodeBlock(player.getLocation());
    }

    public boolean canBreakBlock(@NonNull final Player player, @NonNull final Location location) {
        final Collection<RegisteredServiceProvider<TerritorySupport>> registry = this.servicesManager.getRegistrations(TerritorySupport.class);

        for (final RegisteredServiceProvider<TerritorySupport> instance : registry) {
            final TerritorySupport provider = instance.getProvider();

            if (!provider.canBreakBlock(player, location)) continue;

            return true;
        }

        return false;
    }

    public boolean canBreakBlock(@NonNull final Player player) {
        return canBreakBlock(player, player.getLocation());
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

    public boolean isTerritory(@NonNull final String region, @NonNull final Player player) {
        return isTerritory(region, player.getLocation());
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

    public boolean isTerritory(@NonNull final Player player) {
        return isTerritory(player, player.getLocation());
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