package me.badbones69.crazyenchantments.multisupport.worldguard;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuard_v7 implements WorldGuardVersion {
    
    @Override
    public boolean allowsPVP(Location loc) {
        BukkitWorld world = new BukkitWorld(loc.getWorld());
        BlockVector3 v = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
        try {
            ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world).getApplicableRegions(v);
            return set.queryState(null, Flags.PVP) != StateFlag.State.DENY;
        } catch (NullPointerException e) {
            return true;
        }
    }
    
    @Override
    public boolean allowsBreak(Location loc) {
        BukkitWorld world = new BukkitWorld(loc.getWorld());
        BlockVector3 v = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
        try {
            ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world).getApplicableRegions(v);
            return set.queryState(null, Flags.BLOCK_BREAK) != StateFlag.State.DENY;
        } catch (NullPointerException e) {
            return true;
        }
    }
    
    @Override
    public boolean allowsExplosions(Location loc) {
        BukkitWorld world = new BukkitWorld(loc.getWorld());
        BlockVector3 v = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
        try {
            ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world).getApplicableRegions(v);
            return set.queryState(null, Flags.OTHER_EXPLOSION) != StateFlag.State.DENY && set.queryState(null, Flags.TNT) != StateFlag.State.DENY;
        } catch (NullPointerException e) {
            return true;
        }
    }
    
    @Override
    public boolean inRegion(String regionName, Location loc) {
        BukkitWorld world = new BukkitWorld(loc.getWorld());
        BlockVector3 v = BlockVector3.at(loc.getX(), loc.getY(), loc.getZ());
        try {
            ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world).getApplicableRegions(v);
            for (ProtectedRegion region : set) {
                if (regionName.equalsIgnoreCase(region.getId())) {
                    return true;
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
            ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world).getApplicableRegions(v);
            for (ProtectedRegion region : set.getRegions()) {
                if (region.getMembers().contains(player.getUniqueId())) {
                    return true;
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
            ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world).getApplicableRegions(v);
            for (ProtectedRegion region : set.getRegions()) {
                if (region.getOwners().contains(player.getUniqueId())) {
                    return true;
                }
            }
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }
}