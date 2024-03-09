package com.badbones69.crazyenchantments.paper.support.claims;

import com.badbones69.crazyenchantments.paper.support.interfaces.claims.WorldGuardVersion;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WorldGuardSupport implements WorldGuardVersion {

    @NotNull
    private final WorldGuard instance = WorldGuard.getInstance();
    
    @Override
    public boolean allowsPVP(Location loc) {
        BukkitWorld world = new BukkitWorld(loc.getWorld());
        BlockVector3 v = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());

        try {
            RegionManager set = this.instance.getPlatform().getRegionContainer().get(world);

            if (set != null) return set.getApplicableRegions(v).queryState(null, Flags.PVP) != StateFlag.State.DENY;
        } catch (NullPointerException e) {
            return true;
        }

        return false;
    }
    
    @Override
    public boolean allowsBreak(Location loc) {
        BukkitWorld world = new BukkitWorld(loc.getWorld());
        BlockVector3 v = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());

        try {
            RegionManager set = this.instance.getPlatform().getRegionContainer().get(world);

            if (set != null) return set.getApplicableRegions(v).queryState(null, Flags.BLOCK_BREAK) != StateFlag.State.DENY;
        } catch (NullPointerException e) {
            return true;
        }

        return false;
    }
    
    @Override
    public boolean allowsExplosions(Location loc) {
        BukkitWorld world = new BukkitWorld(loc.getWorld());
        BlockVector3 v = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());

        try {
            RegionManager set = this.instance.getPlatform().getRegionContainer().get(world);

            if (set != null) return set.getApplicableRegions(v).queryState(null, Flags.OTHER_EXPLOSION) != StateFlag.State.DENY && set.getApplicableRegions(v).queryState(null, Flags.TNT) != StateFlag.State.DENY;
        } catch (NullPointerException e) {
            return true;
        }

        return false;
    }
    
    @Override
    public boolean inRegion(String regionName, Location loc) {
        BukkitWorld world = new BukkitWorld(loc.getWorld());
        BlockVector3 v = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());

        try {
            RegionManager set = this.instance.getPlatform().getRegionContainer().get(world);

            if (set != null) {
                for (ProtectedRegion region : set.getApplicableRegions(v).getRegions()) {
                    if (regionName.equalsIgnoreCase(region.getId())) return true;
                }
            }
        } catch (NullPointerException e) {
            return false;
        }

        return false;
    }
    
    @Override
    public boolean isMember(Player player) {
        BukkitWorld world = new BukkitWorld(player.getLocation().getWorld());
        BlockVector3 v = BlockVector3.at(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());

        try {
            RegionManager set = this.instance.getPlatform().getRegionContainer().get(world);

            if (set != null) {
                for (ProtectedRegion region : set.getApplicableRegions(v).getRegions()) {
                    if (region.getMembers().contains(player.getUniqueId())) return true;
                }
            }
        } catch (NullPointerException e) {
            return false;
        }

        return false;
    }
    
    @Override
    public boolean isOwner(Player player) {
        BukkitWorld world = new BukkitWorld(player.getLocation().getWorld());
        BlockVector3 v = BlockVector3.at(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());

        try {
            RegionManager set = this.instance.getPlatform().getRegionContainer().get(world);

            if (set != null) {
                for (ProtectedRegion region : set.getApplicableRegions(v).getRegions()) {
                    if (region.getOwners().contains(player.getUniqueId())) return true;
                }
            }
        } catch (NullPointerException e) {
            return false;
        }

        return false;
    }
}