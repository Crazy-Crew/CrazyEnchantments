package com.badbones69.crazyenchantments.paper.support.protection.worldguard;

import com.badbones69.crazyenchantments.paper.api.enums.keys.FileKeys;
import com.badbones69.crazyenchantments.paper.support.api.enums.PluginType;
import com.badbones69.crazyenchantments.paper.support.api.interfaces.TerritorySupport;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.association.Associables;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import java.util.List;

@NullMarked
public final class WorldGuardSupport extends TerritorySupport<Location, Location> {

    private final WorldGuard instance = WorldGuard.getInstance();

    private final WorldGuardPlatform platform = this.instance.getPlatform();

    private final RegionContainer container = this.platform.getRegionContainer();

    @Override
    public PluginType getPluginType() {
        return PluginType.WORLDGUARD;
    }

    @Override
    public String getPluginName() {
        return "WorldGuard";
    }

    @Override
    public boolean isProtected(final Location location) {
        if (!isPluginReady()) {
            return false;
        }

        final World world = location.getWorld();

        if (world == null) {
            return false;
        }

        return this.container
                .createQuery()
                .testBuild(BukkitAdapter.adapt(location), Associables.constant(Association.NON_MEMBER));
    }

    @Override
    public boolean canBreakBlock(final Player player, final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        return this.container
                .createQuery()
                .testState(BukkitAdapter.adapt(location), WorldGuardPlugin.inst().wrapPlayer(player, true), Flags.BUILD);
    }

    @Override
    public boolean canPlaceBlock(final Player player, final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        return this.container
                .createQuery()
                .testState(BukkitAdapter.adapt(location), WorldGuardPlugin.inst().wrapPlayer(player, true), Flags.BUILD);
    }

    @Override
    public boolean canInteract(final Player player, final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        return this.container
                .createQuery()
                .testState(BukkitAdapter.adapt(location), WorldGuardPlugin.inst().wrapPlayer(player, true), Flags.INTERACT);
    }

    @Override
    public boolean canInteract(final Player player, final BlockState block) {
        return canInteract(player, block.getLocation());
    }

    @Override
    public boolean canExplodeBlock(final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        return this.container
                .createQuery()
                .testState(BukkitAdapter.adapt(location), Associables.constant(Association.NON_MEMBER), Flags.TNT);
    }

    @Override
    public boolean canExplodeBlock(final Entity entity, final Location location) {
        return canExplodeBlock(entity.getLocation());
    }

    @Override
    public boolean isCombatEnabled(final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        return this.container
                .createQuery()
                .testState(BukkitAdapter.adapt(location), Associables.constant(Association.NON_MEMBER), Flags.PVP);
    }

    @Override
    public boolean isTerritory(final String region, final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        final BukkitWorld bukkitWorld = new BukkitWorld(location.getWorld());

        final RegionManager regionManager = this.container.get(bukkitWorld);

        if (regionManager == null) {
            return false;
        }

        final BlockVector3 vector = BlockVector3.at(location.getX(), location.getY(), location.getZ());

        for (final ProtectedRegion key : regionManager.getApplicableRegions(vector)) {
            if (!region.equals(key.getId())) continue;

            return true;
        }

        return false;
    }

    @Override
    public boolean isTerritory(final Player player, final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        final BukkitWorld bukkitWorld = new BukkitWorld(location.getWorld());

        final RegionManager regionManager = this.container.get(bukkitWorld);

        if (regionManager == null) {
            return false;
        }

        final BlockVector3 vector = BlockVector3.at(location.getX(), location.getY(), location.getZ());

        final List<String> regions = FileKeys.CONFIG.getConfiguration().getStringList("Settings.EnchantmentOptions.Wings.Regions");

        for (final ProtectedRegion key : regionManager.getApplicableRegions(vector)) {
            if (!regions.contains(key.getId())) continue;

            return true;
        }

        return false;
    }

    @Override
    public boolean isTerritory(final Player player) {
        return isTerritory(player, player.getLocation());
    }

    @Override
    public boolean isMember(final Player player) {
        if (!isPluginReady()) {
            return true;
        }

        final Location location = player.getLocation();

        final BukkitWorld bukkitWorld = new BukkitWorld(location.getWorld());

        final RegionManager regionManager = this.container.get(bukkitWorld);

        if (regionManager == null) {
            return false;
        }

        final BlockVector3 vector = BlockVector3.at(location.getX(), location.getY(), location.getZ());

        final LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        for (final ProtectedRegion region : regionManager.getApplicableRegions(vector).getRegions()) {
            if (!region.isMember(localPlayer)) continue;

            return true;
        }

        return false;
    }

    @Override
    public boolean isOwner(final Player player) {
        if (!isPluginReady()) {
            return true;
        }

        final Location location = player.getLocation();

        final BukkitWorld bukkitWorld = new BukkitWorld(location.getWorld());

        final RegionManager regionManager = this.container.get(bukkitWorld);

        if (regionManager == null) {
            return false;
        }

        final BlockVector3 vector = BlockVector3.at(location.getX(), location.getY(), location.getZ());

        final LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        for (final ProtectedRegion region : regionManager.getApplicableRegions(vector).getRegions()) {
            if (!region.isOwner(localPlayer)) continue;

            return true;
        }

        return false;
    }
}