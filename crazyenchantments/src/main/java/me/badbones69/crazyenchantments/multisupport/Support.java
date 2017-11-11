package me.badbones69.crazyenchantments.multisupport;

import me.badbones69.crazyenchantments.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Support {

	public static boolean inTerritory(Player player) {
		if(SupportedPlugins.FACTIONS_UUID.isPluginLoaded()) {
			if(FactionsUUID.inTerritory(player)) {
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
		if(SupportedPlugins.LEGACY_FACTIONS.isPluginLoaded()) {
			if(LegacyFactionsSupport.inTerritory(player)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isFriendly(Entity P, Entity O) {
		if(P instanceof Player && O instanceof Player) {
			Player player = (Player) P;
			Player other = (Player) O;
			if(SupportedPlugins.FACTIONS_UUID.isPluginLoaded()) {
				if(FactionsUUID.isFriendly(player, other)) {
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
		}
		return false;
	}

	public static boolean canBreakBlock(Player player, Block block) {
		if(SupportedPlugins.FACTIONS_UUID.isPluginLoaded()) {
			if(player != null) {
				if(!FactionsUUID.canBreakBlock(player, block)) {
					return false;
				}
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
		if(SupportedPlugins.LEGACY_FACTIONS.isPluginLoaded()) {
			if(!LegacyFactionsSupport.canBreakBlock(player, block)) {
				return false;
			}
		}
		return true;
	}

	public static boolean allowsPVP(Location loc) {
		if(SupportedPlugins.WORLD_EDIT.isPluginLoaded() && SupportedPlugins.WORLD_GUARD.isPluginLoaded()) {
			if(!WorldGuard.allowsPVP(loc)) {
				return false;
			}
		}
		return true;
	}

	public static boolean allowsBreak(Location loc) {
		if(SupportedPlugins.WORLD_EDIT.isPluginLoaded() && SupportedPlugins.WORLD_GUARD.isPluginLoaded()) {
			if(!WorldGuard.allowsBreak(loc)) {
				return false;
			}
		}
		return true;
	}

	public static boolean allowsExplotions(Location loc) {
		if(SupportedPlugins.WORLD_EDIT.isPluginLoaded() && SupportedPlugins.WORLD_GUARD.isPluginLoaded()) {
			if(!WorldGuard.allowsExplosions(loc)) {
				return false;
			}
		}
		return true;
	}

	public static boolean inWingsRegion(Player player) {
		if(SupportedPlugins.WORLD_EDIT.isPluginLoaded() && SupportedPlugins.WORLD_GUARD.isPluginLoaded()) {
			if(Main.settings.getConfig().contains("Settings.EnchantmentOptions.Wings.Regions")) {
				for(String rg : Main.settings.getConfig().getStringList("Settings.EnchantmentOptions.Wings.Regions")) {
					if(WorldGuard.inRegion(rg, player.getLocation())) {
						return true;
					}else {
						if(Main.settings.getConfig().contains("Settings.EnchantmentOptions.Wings.Members-Can-Fly")) {
							if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.Wings.Members-Can-Fly")) {
								if(WorldGuard.isMember(player)) {
									return true;
								}
							}
						}
						if(Main.settings.getConfig().contains("Settings.EnchantmentOptions.Wings.Owners-Can-Fly")) {
							if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.Wings.Owners-Can-Fly")) {
								if(WorldGuard.isOwner(player)) {
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
		FACTIONS_UUID("Factions"),
		FEUDAL("Feudal"),
		ACID_ISLAND("AcidIsland"),
		ASKYBLOCK("ASkyBlock"),
		KINGDOMS("Kingdoms"),
		SILK_SPAWNERS("SilkSpawners"),
		SPARTAN("Spartan"),
		MOB_STACKER("MobStacker"),
		MOB_STACKER_2("MobStacker2"),
		STACK_MOB("StackMob"),
		MEGA_SKILLS("MegaSkills");

		private String name;

		private SupportedPlugins(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public Boolean isPluginLoaded() {
			Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(name);
			if(this == SupportedPlugins.FACTIONS_MASSIVE_CRAFT) {
				if(plugin != null) {
					if(plugin.getDescription() != null) {
						if(plugin.getDescription().getWebsite() != null) {
							if(plugin.getDescription().getWebsite().equalsIgnoreCase("https://www.massivecraft.com/factions")) {
								return true;
							}
						}
					}
				}
				return false;
			}else if(this == SupportedPlugins.FACTIONS_UUID) {
				if(plugin != null) {
					if(plugin.getDescription().getAuthors() != null) {
						if(plugin.getDescription().getAuthors().contains("drtshock")) {
							return true;
						}
					}
				}
				return false;
			}
			return plugin != null;
		}

	}

}