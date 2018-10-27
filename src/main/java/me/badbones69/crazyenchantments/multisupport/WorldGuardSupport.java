package me.badbones69.crazyenchantments.multisupport;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardSupport {
	
	public static boolean allowsPVP(Location loc) {
		BukkitWorld world = new BukkitWorld(loc.getWorld());
		Vector v = new Vector(loc.getX(), loc.getY(), loc.getZ());
		try {
			ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world).getApplicableRegions(v);
			return set.queryState( null, Flags.PVP ) != StateFlag.State.DENY;
		} catch(NullPointerException e) {
			return true;
		}
	}
	
	public static boolean allowsBreak(Location loc) {
		BukkitWorld world = new BukkitWorld(loc.getWorld());
		Vector v = new Vector(loc.getX(), loc.getY(), loc.getZ());
		try {
			ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world).getApplicableRegions(v);
			return set.queryState(null, Flags.BLOCK_BREAK) != StateFlag.State.DENY;
		} catch(NullPointerException e) {
			return true;
		}
	}
	
	public static boolean allowsExplosions(Location loc) {
		BukkitWorld world = new BukkitWorld(loc.getWorld());
		Vector v = new Vector(loc.getX(), loc.getY(), loc.getZ());
		try {
			ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world).getApplicableRegions(v);
			return set.queryState( null, Flags.OTHER_EXPLOSION ) != StateFlag.State.DENY && set.queryState( null, Flags.TNT ) != StateFlag.State.DENY;
		} catch(NullPointerException e) {
			return true;
		}
	}
	
	public static boolean inRegion(String regionName, Location loc) {
		BukkitWorld world = new BukkitWorld(loc.getWorld());
		Vector v = new Vector(loc.getX(), loc.getY(), loc.getZ());
		try {
			ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world).getApplicableRegions(v);
			for( ProtectedRegion region : set ) {
				if( regionName.equalsIgnoreCase( region.getId() ) ) {
					return true;
				}
			}
		} catch(NullPointerException e) {
			return false;
		}
		return false;
	}
	
	public static Boolean isMember(Player player) {
		BukkitWorld world = new BukkitWorld(player.getLocation().getWorld());
		Vector v = new Vector(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
		try {
			ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world).getApplicableRegions(v);
			for( ProtectedRegion region : set.getRegions() ) {
				if( region.getMembers().contains( player.getUniqueId() ) ) {
					return true;
				}
			}
		} catch(NullPointerException e) {
			return false;
		}
		return false;
	}
	
	public static Boolean isOwner(Player player) {
		BukkitWorld world = new BukkitWorld(player.getLocation().getWorld());
		Vector v = new Vector(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
		try {
			ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().get(world).getApplicableRegions(v);
			for( ProtectedRegion region : set.getRegions() ) {
				if( region.getOwners().contains( player.getUniqueId() ) ) {
					return true;
				}
			}
		} catch(NullPointerException e) {
			return false;
		}
		return false;
	}
	
}
