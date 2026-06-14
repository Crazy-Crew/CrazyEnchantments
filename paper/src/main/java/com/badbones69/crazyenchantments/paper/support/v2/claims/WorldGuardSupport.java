package com.badbones69.crazyenchantments.paper.support.v2.claims;

import com.badbones69.crazyenchantments.paper.support.v2.enums.PluginType;
import com.badbones69.crazyenchantments.paper.support.v2.interfaces.TerritorySupport;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class WorldGuardSupport extends TerritorySupport<Location, Location> {

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
    public boolean isTerritory(@NonNull final String region, @NonNull final Location container) {
        if (!isPluginReady()) {
            return true;
        }

        final BukkitWorld bukkitWorld = new BukkitWorld(container.getWorld());

        final RegionManager regionManager = this.container.get(bukkitWorld);

        if (regionManager == null) {
            return true;
        }

        final BlockVector3 vector = BlockVector3.at(container.getX(), container.getY(), container.getZ());

        boolean isRegion = false;

        for (final ProtectedRegion key : regionManager.getApplicableRegions(vector)) {
            if (region.equals(key.getId())) {
                isRegion = true;

                break;
            }
        }

        return isRegion;
    }

    @Override
    public boolean isCombatEnabled(@NonNull final Location container) {
        if (!isPluginReady()) {
            return true;
        }

        final BukkitWorld bukkitWorld = new BukkitWorld(container.getWorld());

        final RegionManager regionManager = this.container.get(bukkitWorld);

        if (regionManager == null) {
            return true;
        }

        final BlockVector3 vector = BlockVector3.at(container.getX(), container.getY(), container.getZ());

        return regionManager.getApplicableRegions(vector).queryState(null, Flags.PVP) == StateFlag.State.ALLOW;
    }

    @Override
    public boolean canExplodeBlock(@NonNull Location container) {
        if (!isPluginReady()) {
            return true;
        }

        final BukkitWorld bukkitWorld = new BukkitWorld(container.getWorld());

        final RegionManager regionManager = this.container.get(bukkitWorld);

        if (regionManager == null) {
            return true;
        }

        final BlockVector3 vector = BlockVector3.at(container.getX(), container.getY(), container.getZ());

        final ApplicableRegionSet region = regionManager.getApplicableRegions(vector);

        return region.queryState(null, Flags.OTHER_EXPLOSION) == StateFlag.State.ALLOW || region.queryState(null, Flags.TNT) == StateFlag.State.ALLOW;
    }

    @Override
    public boolean canBreakBlock(@NonNull final Player player, @NonNull final Location container) {
        if (!isPluginReady()) {
            return true;
        }

        final BukkitWorld bukkitWorld = new BukkitWorld(container.getWorld());

        final RegionManager regionManager = this.container.get(bukkitWorld);

        if (regionManager == null) {
            return true;
        }

        final BlockVector3 vector = BlockVector3.at(container.getX(), container.getY(), container.getZ());

        return regionManager.getApplicableRegions(vector).queryState(null, Flags.BLOCK_BREAK) == StateFlag.State.ALLOW;
    }

    @Override
    public boolean isMember(@NonNull final Player player) {
        if (!isPluginReady()) {
            return true;
        }

        final Location container = player.getLocation();

        final BukkitWorld bukkitWorld = new BukkitWorld(container.getWorld());

        final RegionManager regionManager = this.container.get(bukkitWorld);

        if (regionManager == null) {
            return true;
        }

        final BlockVector3 vector = BlockVector3.at(container.getX(), container.getY(), container.getZ());

        final LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        boolean isMember = false;

        for (final ProtectedRegion region : regionManager.getApplicableRegions(vector).getRegions()) {
            if (region.isMember(localPlayer)) {
                isMember = true;

                break;
            }
        }

        return isMember;
    }

    @Override
    public boolean isOwner(@NonNull final Player player) {
        if (!isPluginReady()) {
            return true;
        }

        final Location container = player.getLocation();

        final BukkitWorld bukkitWorld = new BukkitWorld(container.getWorld());

        final RegionManager regionManager = this.container.get(bukkitWorld);

        if (regionManager == null) {
            return true;
        }

        final BlockVector3 vector = BlockVector3.at(container.getX(), container.getY(), container.getZ());

        final LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        boolean isOwner = false;

        for (final ProtectedRegion region : regionManager.getApplicableRegions(vector).getRegions()) {
            if (region.isOwner(localPlayer)) {
                isOwner = true;

                break;
            }
        }

        return isOwner;
    }
}