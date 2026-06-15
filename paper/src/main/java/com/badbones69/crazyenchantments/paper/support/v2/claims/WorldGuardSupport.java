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
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

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
    public boolean canBreakBlock(final Player player, final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        final BukkitWorld bukkitWorld = new BukkitWorld(location.getWorld());

        final RegionManager regionManager = this.container.get(bukkitWorld);

        if (regionManager == null) {
            return true;
        }

        final BlockVector3 vector = BlockVector3.at(location.getX(), location.getY(), location.getZ());

        return regionManager.getApplicableRegions(vector).queryState(null, Flags.BLOCK_BREAK) == StateFlag.State.ALLOW;
    }

    @Override
    public boolean canPlaceBlock(final Player player, final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        final BukkitWorld bukkitWorld = new BukkitWorld(location.getWorld());

        final RegionManager regionManager = this.container.get(bukkitWorld);

        if (regionManager == null) {
            return true;
        }

        final BlockVector3 vector = BlockVector3.at(location.getX(), location.getY(), location.getZ());

        return regionManager.getApplicableRegions(vector).queryState(null, Flags.BLOCK_PLACE) == StateFlag.State.ALLOW;
    }

    @Override
    public boolean canInteract(final Player player, final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        final BukkitWorld bukkitWorld = new BukkitWorld(location.getWorld());

        final RegionManager regionManager = this.container.get(bukkitWorld);

        if (regionManager == null) {
            return true;
        }

        final BlockVector3 vector = BlockVector3.at(location.getX(), location.getY(), location.getZ());

        return regionManager.getApplicableRegions(vector).queryState(null, Flags.INTERACT) == StateFlag.State.ALLOW;
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

        final BukkitWorld bukkitWorld = new BukkitWorld(location.getWorld());

        final RegionManager regionManager = this.container.get(bukkitWorld);

        if (regionManager == null) {
            return true;
        }

        final BlockVector3 vector = BlockVector3.at(location.getX(), location.getY(), location.getZ());

        final ApplicableRegionSet region = regionManager.getApplicableRegions(vector);

        return region.queryState(null, Flags.OTHER_EXPLOSION) == StateFlag.State.ALLOW || region.queryState(null, Flags.TNT) == StateFlag.State.ALLOW;
    }

    @Override
    public boolean isCombatEnabled(final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        final BukkitWorld bukkitWorld = new BukkitWorld(location.getWorld());

        final RegionManager regionManager = this.container.get(bukkitWorld);

        if (regionManager == null) {
            return true;
        }

        final BlockVector3 vector = BlockVector3.at(location.getX(), location.getY(), location.getZ());

        return regionManager.getApplicableRegions(vector).queryState(null, Flags.PVP) == StateFlag.State.ALLOW;
    }

    @Override
    public boolean isTerritory(final String region, final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        final BukkitWorld bukkitWorld = new BukkitWorld(location.getWorld());

        final RegionManager regionManager = this.container.get(bukkitWorld);

        if (regionManager == null) {
            return true;
        }

        final BlockVector3 vector = BlockVector3.at(location.getX(), location.getY(), location.getZ());

        for (final ProtectedRegion key : regionManager.getApplicableRegions(vector)) {
            if (!region.equals(key.getId())) continue;

            return true;
        }

        return false;
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
            return true;
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
            return true;
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