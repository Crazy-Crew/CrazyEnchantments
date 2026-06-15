package com.badbones69.crazyenchantments.paper.support.v2.interfaces;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.support.v2.enums.PluginType;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import java.util.List;
import java.util.Optional;

@NullMarked
public abstract class TerritorySupport<B, L> {

    protected final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    protected final Server server = this.plugin.getServer();

    protected final PluginManager pluginManager = this.server.getPluginManager();

    protected final ServicesManager servicesManager = this.server.getServicesManager();

    protected boolean isEnabled = false;

    public abstract PluginType getPluginType();

    public abstract String getPluginName();

    public boolean canBreakBlock(final Player player, final B block) {
        return true;
    }

    public boolean canPlaceBlock(final Player player, final B block) {
        return true;
    }

    public boolean canInteract(final Player player, final B block) {
        return true;
    }

    public boolean canExplodeBlock(final Entity entity, final L location) {
        return true;
    }

    public boolean canExplodeBlock(final L location) {
        return true;
    }

    public boolean isCombatEnabled(final L location) {
        return true;
    }

    public boolean isTerritory(final Player player, final L location) {
        return true;
    }

    public boolean isTerritory(final String region, final L location) {
        return true;
    }

    public boolean isTerritory(final Player player) {
        return true;
    }

    public boolean isTerritory(final L location) {
        return true;
    }

    public boolean isProtected(final L location) {
        return false;
    }

    public boolean isFriendly(final Entity damager, final Entity target) {
        return false;
    }

    public boolean isOwner(final Player player) {
        return false;
    }

    public boolean isMember(final Player player) {
        return false;
    }

    public boolean isPluginReady() {
        return this.isEnabled;
    }

    public boolean hasClass(final Plugin plugin, final Class<?> classObject) {
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

    public Optional<Plugin> getPlugin() {
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