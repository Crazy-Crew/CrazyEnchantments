package me.badbones69.crazyenchantments.multisupport;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuard {

	public static boolean allowsPVP(Location loc) {
		ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(loc.getWorld()).getApplicableRegions(loc);
		if(set.queryState(null, DefaultFlag.PVP) == StateFlag.State.DENY) {
			return false;
		}
		return true;
	}

	public static boolean allowsBreak(Location loc) {
		ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(loc.getWorld()).getApplicableRegions(loc);
		if(set.queryState(null, DefaultFlag.BLOCK_BREAK) == StateFlag.State.DENY) {
			return false;
		}
		return true;
	}

	public static boolean allowsExplosions(Location loc) {
		ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(loc.getWorld()).getApplicableRegions(loc);
		if(set.queryState(null, DefaultFlag.OTHER_EXPLOSION) == StateFlag.State.DENY || set.queryState(null, DefaultFlag.TNT) == StateFlag.State.DENY) {
			return false;
		}
		return true;
	}

	public static boolean inRegion(String regionName, Location loc) {
		ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(loc.getWorld()).getApplicableRegions(loc);
		for(ProtectedRegion region : set) {
			if(regionName.equalsIgnoreCase(region.getId())) {
				return true;
			}
		}
		return false;
	}

	public static Boolean isMember(Player player) {
		ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation());
		for(ProtectedRegion region : set.getRegions()) {
			if(region.getMembers().contains(player.getUniqueId())) {
				return true;
			}
		}
		return false;
	}

	public static Boolean isOwner(Player player) {
		ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(player.getWorld()).getApplicableRegions(player.getLocation());
		for(ProtectedRegion region : set.getRegions()) {
			if(region.getOwners().contains(player.getUniqueId())) {
				return true;
			}
		}
		return false;
	}

}