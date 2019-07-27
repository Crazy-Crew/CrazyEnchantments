package me.badbones69.crazyenchantments.multisupport;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.multisupport.plotsquared.PlotSquaredVersion;
import me.badbones69.crazyenchantments.multisupport.worldguard.WorldGuardVersion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Support {
	
	private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
	private static WorldGuardVersion worldGuardVersion = ce.getWorldGuardSupport();
	private static PlotSquaredVersion plotSquaredVersion = ce.getPlotSquaredSupport();
	
	public static boolean inTerritory(Player player) {
		if(SupportedPlugins.FACTIONS_UUID.isPluginLoaded()) {
			if(FactionsUUID.inTerritory(player)) {
				return true;
			}
		}
		if(SupportedPlugins.FACTIONS3.isPluginLoaded()) {
			if(Factions3Support.inTerritory(player)) {
				return true;
			}
		}
		if(SupportedPlugins.FACTIONS_MASSIVE_CRAFT.isPluginLoaded()) {
			if(FactionsSupport.inTerritory(player)) {
				return true;
			}
		}
		if(SupportedPlugins.FEUDAL.isPluginLoaded()) {
			if(FeudalSupport.inTerritory(player)) {
				return true;
			}
		}
		if(SupportedPlugins.ASKYBLOCK.isPluginLoaded()) {
			if(ASkyBlockSupport.inTerritory(player)) {
				return true;
			}
		}
		if(SupportedPlugins.ACID_ISLAND.isPluginLoaded()) {
			if(AcidIslandSupport.inTerritory(player)) {
				return true;
			}
		}
		if(SupportedPlugins.KINGDOMS.isPluginLoaded()) {
			if(KingdomSupport.inTerritory(player)) {
				return true;
			}
		}
		if(SupportedPlugins.TOWNY.isPluginLoaded()) {
			if(TownySupport.inTerritory(player)) {
				return true;
			}
		}
		if(SupportedPlugins.GRIEF_PREVENTION.isPluginLoaded()) {
			if(GriefPreventionSupport.inTerritory(player)) {
				return true;
			}
		}
		if(SupportedPlugins.PLOT_SQUARED.isPluginLoaded()) {
			if(plotSquaredVersion.inTerritory(player)) {
				return true;
			}
		}
		return SupportedPlugins.LEGACY_FACTIONS.isPluginLoaded() && LegacyFactionsSupport.inTerritory(player);
	}
	
	public static boolean isFriendly(Entity p, Entity o) {
		if(p instanceof Player && o instanceof Player) {
			Player player = (Player) p;
			Player other = (Player) o;
			if(SupportedPlugins.FACTIONS_UUID.isPluginLoaded()) {
				if(FactionsUUID.isFriendly(player, other)) {
					return true;
				}
			}
			if(SupportedPlugins.FACTIONS3.isPluginLoaded()) {
				if(Factions3Support.isFriendly(player, other)) {
					return true;
				}
			}
			if(SupportedPlugins.FACTIONS_MASSIVE_CRAFT.isPluginLoaded()) {
				if(FactionsSupport.isFriendly(player, other)) {
					return true;
				}
			}
			if(SupportedPlugins.FEUDAL.isPluginLoaded()) {
				if(FeudalSupport.isFrendly(player, other)) {
					return true;
				}
			}
			if(SupportedPlugins.ASKYBLOCK.isPluginLoaded()) {
				if(ASkyBlockSupport.isFriendly(player, other)) {
					return true;
				}
			}
			if(SupportedPlugins.ACID_ISLAND.isPluginLoaded()) {
				if(AcidIslandSupport.isFriendly(player, other)) {
					return true;
				}
			}
			if(SupportedPlugins.KINGDOMS.isPluginLoaded()) {
				if(KingdomSupport.isFriendly(player, other)) {
					return true;
				}
			}
			if(SupportedPlugins.TOWNY.isPluginLoaded()) {
				if(TownySupport.isFriendly(player, other)) {
					return true;
				}
			}
			if(SupportedPlugins.LEGACY_FACTIONS.isPluginLoaded()) {
				if(LegacyFactionsSupport.isFriendly(player, other)) {
					return true;
				}
			}
			if(SupportedPlugins.MCMMO.isPluginLoaded()) {
				if(MCMMOParty.isFriendly(player, other)) {
					return true;
				}
			}
			if(SupportedPlugins.GRIEF_PREVENTION.isPluginLoaded()) {
				return GriefPreventionSupport.isFriendly(player, other);
			}
		}
		return false;
	}
	
	public static boolean canBreakBlock(Player player, Block block) {
		if(player != null) {
			if(SupportedPlugins.FACTIONS_UUID.isPluginLoaded()) {
				if(!FactionsUUID.canBreakBlock(player, block)) {
					return false;
				}
			}
			if(SupportedPlugins.FACTIONS3.isPluginLoaded()) {
				if(!Factions3Support.canBreakBlock(player, block)) {
					return false;
				}
			}
			if(SupportedPlugins.FACTIONS_MASSIVE_CRAFT.isPluginLoaded()) {
				if(!FactionsSupport.canBreakBlock(player, block)) {
					return false;
				}
			}
			if(SupportedPlugins.FEUDAL.isPluginLoaded()) {
				if(!FeudalSupport.canBreakBlock(player, block)) {
					return false;
				}
			}
			if(SupportedPlugins.KINGDOMS.isPluginLoaded()) {
				if(!KingdomSupport.canBreakBlock(player, block)) {
					return false;
				}
			}
			if(SupportedPlugins.GRIEF_PREVENTION.isPluginLoaded()) {
				if(!GriefPreventionSupport.canBreakBlock(player, block)) {
					return false;
				}
			}
			if(SupportedPlugins.PRECIOUS_STONES.isPluginLoaded()) {
				if(!PreciousStonesSupport.canBreakBlock(player, block)) {
					return false;
				}
			}
		}
		return !SupportedPlugins.LEGACY_FACTIONS.isPluginLoaded() || LegacyFactionsSupport.canBreakBlock(player, block);
	}
	
	public static boolean allowsPVP(Location loc) {
		return !SupportedPlugins.WORLD_EDIT.isPluginLoaded() || !SupportedPlugins.WORLD_GUARD.isPluginLoaded() || worldGuardVersion.allowsPVP(loc);
	}
	
	public static boolean allowsBreak(Location loc) {
		return !SupportedPlugins.WORLD_EDIT.isPluginLoaded() || !SupportedPlugins.WORLD_GUARD.isPluginLoaded() || worldGuardVersion.allowsBreak(loc);
	}
	
	public static boolean allowsExplotions(Location loc) {
		return !SupportedPlugins.WORLD_EDIT.isPluginLoaded() || !SupportedPlugins.WORLD_GUARD.isPluginLoaded() || worldGuardVersion.allowsExplosions(loc);
	}
	
	public static boolean inWingsRegion(Player player) {
		if(SupportedPlugins.WORLD_EDIT.isPluginLoaded() && SupportedPlugins.WORLD_GUARD.isPluginLoaded()) {
			if(Files.CONFIG.getFile().contains("Settings.EnchantmentOptions.Wings.Regions")) {
				for(String rg : Files.CONFIG.getFile().getStringList("Settings.EnchantmentOptions.Wings.Regions")) {
					if(worldGuardVersion.inRegion(rg, player.getLocation())) {
						return true;
					}else {
						if(Files.CONFIG.getFile().contains("Settings.EnchantmentOptions.Wings.Members-Can-Fly")) {
							if(Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Wings.Members-Can-Fly")) {
								if(worldGuardVersion.isMember(player)) {
									return true;
								}
							}
						}
						if(Files.CONFIG.getFile().contains("Settings.EnchantmentOptions.Wings.Owners-Can-Fly")) {
							if(Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Wings.Owners-Can-Fly")) {
								if(worldGuardVersion.isOwner(player)) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public static void noStack(Entity en) {
		if(SupportedPlugins.MOB_STACKER.isPluginLoaded()) {
			MobStacker.noStack(en);
		}
		if(SupportedPlugins.MOB_STACKER_2.isPluginLoaded()) {
			MobStacker2.noStack(en);
		}
		if(SupportedPlugins.STACK_MOB.isPluginLoaded()) {
			StackMobSupport.preventStacking(en);
		}
	}
	
	public enum SupportedPlugins {
		
		MCMMO("mcMMO"),
		GRIEF_PREVENTION("GriefPrevention"),
		LEGACY_FACTIONS("LegacyFactions"),
		TOWNY("Towny"),
		EPIC_SPAWNERS("EpicSpawners"),
		AAC("AAC"),
		DAKATA("DakataAntiCheat"),
		NO_CHEAT_PLUS("NoCheatPlus"),
		VAULT("Vault"),
		WORLD_EDIT("WorldEdit"),
		WORLD_GUARD("WorldGuard"),
		FACTIONS_MASSIVE_CRAFT("Factions"),
		FACTIONS3("Factions"),
		FACTIONS_UUID("Factions"),
		FEUDAL("Feudal"),
		ACID_ISLAND("AcidIsland"),
		ASKYBLOCK("ASkyBlock"),
		KINGDOMS("Kingdoms"),
		SILK_SPAWNERS("SilkSpawners"),
		SILK_SPAWNERS_CANDC("SilkSpawners"),
		SPARTAN("Spartan"),
		MOB_STACKER("MobStacker"),
		MOB_STACKER_2("MobStacker2"),
		STACK_MOB("StackMob"),
		MEGA_SKILLS("MegaSkills"),
		PRECIOUS_STONES("PreciousStones"),
		PLOT_SQUARED("PlotSquared");
		
		private String name;
		
		private SupportedPlugins(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public boolean isPluginLoaded() {
			Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(name);
			if(plugin != null) {
				if(this == SupportedPlugins.SILK_SPAWNERS) {
					if(plugin.getDescription().getAuthors() != null) {
						return plugin.getDescription().getAuthors().contains("xGhOsTkiLLeRx");
					}
					return false;
				}else if(this == SupportedPlugins.SILK_SPAWNERS_CANDC) {
					if(plugin.getDescription().getAuthors() != null) {
						return plugin.getDescription().getAuthors().contains("CandC_9_12");
					}
					return false;
				}else if(this == SupportedPlugins.FACTIONS_MASSIVE_CRAFT) {
					if(plugin.getDescription() != null) {
						if(plugin.getDescription().getWebsite() != null) {
							return plugin.getDescription().getWebsite().equalsIgnoreCase("https://www.massivecraft.com/factions");
						}
					}
					return false;
				}else if(this == SupportedPlugins.FACTIONS_UUID) {
					if(plugin.getDescription().getAuthors() != null) {
						return plugin.getDescription().getAuthors().contains("drtshock");
					}
					return false;
				}else if(this == SupportedPlugins.FACTIONS3) {
					if(plugin.getDescription().getAuthors() != null) {
						return plugin.getDescription().getAuthors().contains("Madus");
					}
					return false;
				}
			}
			return plugin != null;
		}
		
		public Plugin getPlugin() {
			return Bukkit.getServer().getPluginManager().getPlugin(name);
		}
		
	}
	
}
