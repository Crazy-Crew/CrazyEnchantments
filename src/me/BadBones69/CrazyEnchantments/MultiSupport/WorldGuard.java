package me.BadBones69.CrazyEnchantments.MultiSupport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;

public class WorldGuard {
	public static boolean allowsPVP(Entity en){
		if(Bukkit.getServer().getPluginManager().getPlugin("WorldEdit")!=null&&Bukkit.getServer().getPluginManager().getPlugin("WorldGuard")!=null){
			Location loc = en.getLocation();
			ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(en.getWorld()).getApplicableRegions(loc);
			if (set.queryState(null, DefaultFlag.PVP)==StateFlag.State.DENY)return false;
		}
		return true;
	}
	public static boolean allowsBreak(Entity en){
		if(Bukkit.getServer().getPluginManager().getPlugin("WorldEdit")!=null&&Bukkit.getServer().getPluginManager().getPlugin("WorldGuard")!=null){
			Location loc = en.getLocation();
			ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(en.getWorld()).getApplicableRegions(loc);
			if (set.queryState(null, DefaultFlag.BLOCK_BREAK)==StateFlag.State.DENY)return false;
		}
		return true;
	}
	public static boolean allowsExplotions(Entity en){
		if(Bukkit.getServer().getPluginManager().getPlugin("WorldEdit")!=null&&Bukkit.getServer().getPluginManager().getPlugin("WorldGuard")!=null){
			Location loc = en.getLocation();
			ApplicableRegionSet set = WGBukkit.getPlugin().getRegionManager(en.getWorld()).getApplicableRegions(loc);
			if (set.queryState(null, DefaultFlag.OTHER_EXPLOSION)==StateFlag.State.DENY)return false;
		}
		return true;
	}
}