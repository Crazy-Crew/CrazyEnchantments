package com.badbones69.crazyenchantments.paper.support.v2.interfaces;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import java.util.List;
import java.util.Optional;

public abstract class TerritorySupport<B, L> {

    protected final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    protected final Server server = this.plugin.getServer();

    protected final PluginManager pluginManager = this.server.getPluginManager();

    protected final ServicesManager servicesManager = this.server.getServicesManager();

    protected boolean isEnabled = false;

    public abstract String getPluginName();

    public boolean isFactions() {
        return false;
    }

    public boolean canBreakBlock(@NonNull final Player player, @NonNull final B container) {
        return true;
    }

    public boolean canExplodeBlock(@NonNull final L container) {
        return true;
    }

    public boolean isCombatEnabled(@NonNull final L container) {
        return true;
    }

    public boolean isTerritory(@NonNull final Player player, @NonNull final L container) {
        return true;
    }

    public boolean isTerritory(@NonNull final Player player) {
        return true;
    }

    public boolean isProtected(@NonNull final L container) {
        return false;
    }

    public boolean isFriendly(@NonNull final Player player, @NonNull final Entity target) {
        return false;
    }

    public boolean isOwner(@NonNull final Player player) {
        return false;
    }

    public boolean isMember(@NonNull final Player player) {
        return false;
    }

    public boolean isPluginReady() {
        return this.isEnabled;
    }

    public boolean hasClass(@NonNull final Plugin plugin, @NonNull final Class<?> classObject) {
        boolean isRegistered = false;

        final List<RegisteredServiceProvider<?>> providers = this.servicesManager.getRegistrations(plugin);

        for (final RegisteredServiceProvider<?> provider : providers) {
            final Class<?> service = provider.getService();

            if (service == classObject) {
                isRegistered = true;

                break;
            }
        }

        return isRegistered;
    }

    public @NonNull Optional<Plugin> getPlugin() {
        return Optional.ofNullable(this.pluginManager.getPlugin(getPluginName()));
    }

    public void init() {
        if (this.isEnabled) {
            return;
        }

        this.isEnabled = true;

        this.servicesManager.register(
                TerritorySupport.class,
                this,
                this.plugin,
                ServicePriority.Normal
        );
    }
}