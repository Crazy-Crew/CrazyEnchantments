package me.BadBones69.CrazyEnchantments.multisupport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.BadBones69.CrazyEnchantments.Main;

public class Support {
	
	public static boolean hasWorldEdit(){
		if(Bukkit.getServer().getPluginManager().getPlugin("WorldEdit")!=null){
			return true;
		}
		return false;
	}
	public static boolean hasWorldGuard(){
		if(Bukkit.getServer().getPluginManager().getPlugin("WorldGuard")!=null){
			return true;
		}
		return false;
	}
	public static boolean hasFactions(){
		if(Bukkit.getServer().getPluginManager().getPlugin("Factions")!=null){
			return true;
		}
		return false;
	}
	public static boolean hasFeudal(){
		if(Bukkit.getServer().getPluginManager().getPlugin("Feudal")!=null){
			return true;
		}
		return false;
	}
	public static boolean hasASkyBlock(){
		if(Bukkit.getServer().getPluginManager().getPlugin("ASkyBlock")!=null){
			return true;
		}
		return false;
	}
	public static boolean hasKingdoms(){
		if(Bukkit.getServer().getPluginManager().getPlugin("Kingdoms")!=null){
			return true;
		}
		return false;
	}
	public static boolean hasSpartan(){
		if(Bukkit.getServer().getPluginManager().getPlugin("Spartan")!=null){
			return true;
		}
		return false;
	}
	public static boolean hasMobStacker() {
		if(Bukkit.getServer().getPluginManager().getPlugin("MobStacker")!=null){
			return true;
		}
		return false;
	}
	public static boolean hasMobStacker2() {
		if(Bukkit.getServer().getPluginManager().getPlugin("MobStacker2")!=null){
			return true;
		}
		return false;
	}
	
	public static boolean inTerritory(Player player){
		if(hasFactions()){
			Plugin factions = Bukkit.getServer().getPluginManager().getPlugin("Factions");
			if(factions.getDescription().getAuthors().contains("drtshock")){
				if(FactionsUUID.inTerritory(player)){
					return true;
				}
			}
			if(factions.getDescription().getWebsite()!=null){
				if(factions.getDescription().getWebsite().equalsIgnoreCase("https://www.massivecraft.com/factions")){
					if(FactionsSupport.inTerritory(player)){
						return true;
					}
				}
			}
		}
		if(hasFeudal()){
			if(FeudalSupport.inTerritory(player)){
				return true;
			}
		}
		if(hasASkyBlock()){
			if(ASkyBlockSupport.inTerritory(player)){
				return true;
			}
		}
		if(hasKingdoms()){
			if(KingdomSupport.inTerritory(player)){
				return true;
			}
		}
		return false;
	}
	public static boolean isFriendly(Entity P, Entity O){
		if(P instanceof Player&&O instanceof Player){
			Player player = (Player) P;
			Player other = (Player) O;
			if(hasFactions()){
				Plugin factions = Bukkit.getServer().getPluginManager().getPlugin("Factions");
				if(factions.getDescription().getAuthors().contains("drtshock")){
					if(FactionsUUID.isFriendly(player, other))return true;
				}
				if(factions.getDescription().getWebsite()!=null){
					if(factions.getDescription().getWebsite().equalsIgnoreCase("https://www.massivecraft.com/factions")){
						if(FactionsSupport.isFriendly(player, other))return true;
					}
				}
			}
			if(hasFeudal()){
				if(FeudalSupport.isFrendly(player, other)){
					return true;
				}
			}
			if(hasASkyBlock()){
				if(ASkyBlockSupport.isFriendly(player, other)){
					return true;
				}
			}
			if(hasKingdoms()){
				if(KingdomSupport.isFriendly(player, other)){
					return true;
				}
			}
		}
		return false;
	}
	public static boolean canBreakBlock(Player player, Block block){
		if(hasFactions()){
			Plugin factions = Bukkit.getServer().getPluginManager().getPlugin("Factions");
			if(player!=null){
				if(factions.getDescription().getAuthors().contains("drtshock")){
					if(FactionsUUID.canBreakBlock(player, block))return true;
					if(!FactionsUUID.canBreakBlock(player, block))return false;
				}
				if(factions.getDescription().getWebsite()!=null){
					if(factions.getDescription().getWebsite().equalsIgnoreCase("https://www.massivecraft.com/factions")){
						if(FactionsSupport.canBreakBlock(player, block))return true;
						if(!FactionsSupport.canBreakBlock(player, block))return false;
					}
				}
			}
		}
		if(hasFeudal()){
			if(FeudalSupport.canBreakBlock(player, block)){
				return true;
			}else{
				return false;
			}
		}
		if(hasKingdoms()){
			if(KingdomSupport.canBreakBlock(player, block)){
				return true;
			}
		}
		return true;
	}
	public static boolean allowsPVP(Location loc){
		if(hasWorldEdit() && hasWorldGuard()){
			if(WorldGuard.allowsPVP(loc))return true;
			if(!WorldGuard.allowsPVP(loc))return false;
		}
		return true;
	}
	public static boolean allowsBreak(Location loc){
		if(hasWorldEdit() && hasWorldGuard()){
			if(WorldGuard.allowsBreak(loc))return true;
			if(!WorldGuard.allowsBreak(loc))return false;
		}
		return true;
	}
	public static boolean allowsExplotions(Location loc){
		if(hasWorldEdit() && hasWorldGuard()){
			if(WorldGuard.allowsExplosions(loc))return true;
			if(!WorldGuard.allowsExplosions(loc))return false;
		}
		return true;
	}
	public static boolean inWingsRegion(Location loc){
		if(Main.settings.getConfig().contains("Settings.EnchantmentOptions.Wings.Regions")){
			for(String rg : Main.settings.getConfig().getStringList("Settings.EnchantmentOptions.Wings.Regions")){
				if(hasWorldEdit() && hasWorldGuard()){
					if(WorldGuard.inRegion(rg, loc)){
						return true;
					}
				}
			}
		}
		return false;
	}
	public static void noStack(Entity en) {
		if (hasMobStacker()) {
			MobStacker.noStack(en);
			return;
		}
		if (hasMobStacker2()) {
			MobStacker2.noStack(en);
			return;
		}
	}
}
