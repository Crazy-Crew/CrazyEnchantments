package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.ParticleEffect;
import me.badbones69.crazyenchantments.api.CEnchantments;
import me.badbones69.crazyenchantments.api.events.ArmorEquipEvent;
import me.badbones69.crazyenchantments.multisupport.SpartanSupport;
import me.badbones69.crazyenchantments.multisupport.Support;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import me.badbones69.crazyenchantments.multisupport.Version;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class Boots implements Listener {

	private static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments");

	public static ArrayList<Player> Flying = new ArrayList<Player>();

	@EventHandler
	public void onEquip(ArmorEquipEvent e) {
		Player player = e.getPlayer();
		ItemStack NewItem = e.getNewArmorPiece();
		ItemStack OldItem = e.getOldArmorPiece();
		if(Main.CE.hasEnchantments(NewItem)) {
			if(Main.CE.hasEnchantment(NewItem, CEnchantments.WINGS)) {
				if(CEnchantments.WINGS.isEnabled()) {
					if(Support.inTerritory(player) || Support.inWingsRegion(player)) {
						if(player.getGameMode() != GameMode.CREATIVE) {
							if(Version.getCurrentVersion().comparedTo(Version.v1_8_R1) >= 1) {
								if(player.getGameMode() != GameMode.ADVENTURE) {
									player.setAllowFlight(true);
								}
							}else {
								player.setAllowFlight(true);
							}
						}
					}
				}
			}
		}
		if(Main.CE.hasEnchantments(OldItem)) {
			if(Main.CE.hasEnchantment(OldItem, CEnchantments.WINGS)) {
				if(CEnchantments.WINGS.isEnabled()) {
					if(player.getGameMode() != GameMode.CREATIVE) {
						if(Version.getCurrentVersion().comparedTo(Version.v1_8_R1) >= 1) {
							if(player.getGameMode() != GameMode.ADVENTURE) {
								player.setAllowFlight(false);
							}
						}else {
							player.setAllowFlight(false);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onFly(PlayerToggleFlightEvent e) {
		Player player = e.getPlayer();
		ItemStack boots = player.getEquipment().getBoots();
		if(Main.CE.hasEnchantments(boots)) {
			if(Main.CE.hasEnchantment(boots, CEnchantments.WINGS)) {
				if(CEnchantments.WINGS.isEnabled()) {
					if(Support.inTerritory(player) || Support.inWingsRegion(player)) {
						if(!areEnemiesNearBy(player)) {
							if(SupportedPlugins.SPARTAN.isPluginLoaded()) {
								SpartanSupport.cancelFly(player);
							}
							if(e.isFlying()) {
								if(player.getAllowFlight()) {
									e.setCancelled(true);
									player.setFlying(true);
									Flying.add(player);
								}
							}else {
								Flying.remove(player);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		ItemStack boots = player.getEquipment().getBoots();
		if(Main.CE.hasEnchantments(boots)) {
			if(Main.CE.hasEnchantment(boots, CEnchantments.WINGS)) {
				if(CEnchantments.WINGS.isEnabled()) {
					if(Support.inTerritory(player) || Support.inWingsRegion(player)) {
						if(!areEnemiesNearBy(player)) {
							if(!player.getAllowFlight()) {
								player.setAllowFlight(true);
							}
						}else {
							if(player.isFlying()) {
								if(player.getGameMode() != GameMode.CREATIVE) {
									if(Version.getCurrentVersion().comparedTo(Version.v1_8_R1) >= 1) {
										if(player.getGameMode() != GameMode.ADVENTURE) {
											player.setFlying(false);
											player.setAllowFlight(false);
											Flying.remove(player);
										}
									}else {
										player.setFlying(false);
										player.setAllowFlight(false);
										Flying.remove(player);
									}
								}
							}
						}
					}else {
						if(player.isFlying()) {
							if(player.getGameMode() != GameMode.CREATIVE) {
								if(Version.getCurrentVersion().comparedTo(Version.v1_8_R1) >= 1) {
									if(player.getGameMode() != GameMode.ADVENTURE) {
										player.setFlying(false);
										player.setAllowFlight(false);
										Flying.remove(player);
									}
								}else {
									player.setFlying(false);
									player.setAllowFlight(false);
									Flying.remove(player);
								}
							}
						}
					}
					if(player.isFlying()) {
						Flying.add(player);
					}
				}
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		ItemStack boots = player.getEquipment().getBoots();
		if(Main.CE.hasEnchantments(boots)) {
			if(Main.CE.hasEnchantment(boots, CEnchantments.WINGS)) {
				if(CEnchantments.WINGS.isEnabled()) {
					if(Support.inTerritory(player) || Support.inWingsRegion(player)) {
						if(!areEnemiesNearBy(player)) {
							if(SupportedPlugins.SPARTAN.isPluginLoaded()) {
								SpartanSupport.cancelFly(player);
							}
							player.setAllowFlight(true);
							Flying.add(player);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		ItemStack boots = player.getEquipment().getBoots();
		if(Main.CE.hasEnchantments(boots)) {
			if(Main.CE.hasEnchantment(boots, CEnchantments.WINGS)) {
				if(CEnchantments.WINGS.isEnabled()) {
					player.setFlying(false);
					player.setAllowFlight(false);
					Flying.remove(player);
				}
			}
		}
	}

	public static void onStart() {
		if(Main.settings.getConfig().contains("Settings.EnchantmentOptions.Wings.Clouds")) {
			if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.Wings.Clouds")) {
				new BukkitRunnable() {
					@Override
					public void run() {
						for(Player player : Bukkit.getServer().getOnlinePlayers()) {
							if(Flying.contains(player)) {
								Location l = player.getLocation().subtract(0, .25, 0);
								if(player.isFlying()) {
									ItemStack boots = player.getEquipment().getBoots();
									if(boots != null) {
										if(Main.CE.hasEnchantment(boots, CEnchantments.WINGS)) {
											if(CEnchantments.WINGS.isEnabled()) {
												ParticleEffect.CLOUD.display((float) .25, (float) 0, (float) .25, 0, 10, l, 100);
											}
										}
									}
								}
							}
						}
					}
				}.runTaskTimerAsynchronously(plugin, 1, 1);
			}
		}
	}

	private Boolean areEnemiesNearBy(Player player) {
		if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.Wings.Enemy-Toggle")) {
			for(Player p : getNearByPlayers(player, Main.settings.getConfig().getInt("Settings.EnchantmentOptions.Wings.Distance"))) {
				if(!Support.isFriendly(player, p)) {
					return true;
				}
			}
		}
		return false;
	}

	private ArrayList<Player> getNearByPlayers(Player player, int radius) {
		ArrayList<Player> players = new ArrayList<Player>();
		for(Entity en : player.getNearbyEntities(radius, radius, radius)) {
			if(en instanceof Player) {
				if((Player) en != player) {
					players.add((Player) en);
				}
			}
		}
		return players;
	}

}