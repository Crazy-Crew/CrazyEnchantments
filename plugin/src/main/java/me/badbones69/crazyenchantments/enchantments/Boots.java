package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.ArmorEquipEvent;
import me.badbones69.crazyenchantments.multisupport.SpartanSupport;
import me.badbones69.crazyenchantments.multisupport.Support;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import me.badbones69.crazyenchantments.multisupport.Version;
import me.badbones69.crazyenchantments.multisupport.particles.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class Boots implements Listener {
	
	private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
	private static ArrayList<Player> flying = new ArrayList<>();
	
	public static void startWings() {
		if(Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Wings.Clouds")) {
			new BukkitRunnable() {
				@Override
				public void run() {
					for(Player player : Bukkit.getServer().getOnlinePlayers()) {
						if(flying.contains(player)) {
							Location l = player.getLocation().subtract(0, .25, 0);
							if(player.isFlying()) {
								ItemStack boots = player.getEquipment().getBoots();
								if(boots != null) {
									if(ce.hasEnchantment(boots, CEnchantments.WINGS)) {
										if(CEnchantments.WINGS.isActivated()) {
											if(Version.getCurrentVersion().isNewer(Version.v1_8_R3)) {
												player.getWorld().spawnParticle(Particle.CLOUD, l, 100, .25, 0, .25, 0);
											}else {
												ParticleEffect.CLOUD.display(.25F, 0, .25F, 0, 100, player.getLocation(), 100);
											}
										}
									}
								}
							}
						}
					}
				}
			}.runTaskTimerAsynchronously(ce.getPlugin(), 1, 1);
		}
	}
	
	@EventHandler
	public void onEquip(ArmorEquipEvent e) {
		Player player = e.getPlayer();
		ItemStack NewItem = e.getNewArmorPiece();
		ItemStack OldItem = e.getOldArmorPiece();
		if(ce.hasEnchantments(NewItem)) {
			if(ce.hasEnchantment(NewItem, CEnchantments.WINGS)) {
				if(CEnchantments.WINGS.isActivated()) {
					if((Support.inTerritory(player) || Support.inWingsRegion(player) || ce.inWhitelistedWorld(player)) && !ce.inBlacklistedWorld(player)) {
						if(player.getGameMode() != GameMode.CREATIVE) {
							if(player.getGameMode() != GameMode.ADVENTURE) {
								player.setAllowFlight(true);
							}
						}
					}
				}
			}
		}
		if(ce.hasEnchantments(OldItem)) {
			if(ce.hasEnchantment(OldItem, CEnchantments.WINGS)) {
				if(CEnchantments.WINGS.isActivated()) {
					if(player.getGameMode() != GameMode.CREATIVE) {
						if(player.getGameMode() != GameMode.ADVENTURE) {
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
		if(ce.hasEnchantments(boots)) {
			if(ce.hasEnchantment(boots, CEnchantments.WINGS)) {
				if(CEnchantments.WINGS.isActivated()) {
					if((Support.inTerritory(player) || Support.inWingsRegion(player) || ce.inWhitelistedWorld(player)) && !ce.inBlacklistedWorld(player)) {
						if(!areEnemiesNearBy(player)) {
							if(SupportedPlugins.SPARTAN.isPluginLoaded()) {
								SpartanSupport.cancelFly(player);
							}
							if(e.isFlying()) {
								if(player.getAllowFlight()) {
									e.setCancelled(true);
									player.setFlying(true);
									flying.add(player);
								}
							}else {
								flying.remove(player);
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
		if(ce.hasEnchantments(boots)) {
			if(ce.hasEnchantment(boots, CEnchantments.WINGS)) {
				if(CEnchantments.WINGS.isActivated()) {
					if((Support.inTerritory(player) || Support.inWingsRegion(player) || ce.inWhitelistedWorld(player)) && !ce.inBlacklistedWorld(player)) {
						if(!areEnemiesNearBy(player)) {
							if(!player.getAllowFlight()) {
								player.setAllowFlight(true);
							}
						}else {
							if(player.isFlying()) {
								if(player.getGameMode() != GameMode.CREATIVE) {
									if(player.getGameMode() != GameMode.ADVENTURE) {
										player.setFlying(false);
										player.setAllowFlight(false);
										flying.remove(player);
									}
								}
							}
						}
					}else {
						if(player.isFlying()) {
							if(player.getGameMode() != GameMode.CREATIVE) {
								if(player.getGameMode() != GameMode.ADVENTURE) {
									player.setFlying(false);
									player.setAllowFlight(false);
									flying.remove(player);
								}
							}
						}
					}
					if(player.isFlying()) {
						flying.add(player);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		ItemStack boots = player.getEquipment().getBoots();
		if(ce.hasEnchantments(boots)) {
			if(ce.hasEnchantment(boots, CEnchantments.WINGS)) {
				if(CEnchantments.WINGS.isActivated()) {
					if(Support.inTerritory(player) || Support.inWingsRegion(player) || ce.inWhitelistedWorld(player)) {
						if(!areEnemiesNearBy(player)) {
							if(!ce.inBlacklistedWorld(player)) {
								if(SupportedPlugins.SPARTAN.isPluginLoaded()) {
									SpartanSupport.cancelFly(player);
								}
								player.setAllowFlight(true);
								flying.add(player);
							}
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
		if(ce.hasEnchantments(boots)) {
			if(ce.hasEnchantment(boots, CEnchantments.WINGS)) {
				if(CEnchantments.WINGS.isActivated()) {
					player.setFlying(false);
					player.setAllowFlight(false);
					flying.remove(player);
				}
			}
		}
	}
	
	private boolean areEnemiesNearBy(Player player) {
		if(Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Wings.Enemy-Toggle")) {
			for(Player otherPlayer : getNearByPlayers(player, Files.CONFIG.getFile().getInt("Settings.EnchantmentOptions.Wings.Distance"))) {
				if(!Support.isFriendly(player, otherPlayer)) {
					if(!player.hasPermission("crazyenchantments.bypass.wings")) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private ArrayList<Player> getNearByPlayers(Player player, int radius) {
		ArrayList<Player> players = new ArrayList<>();
		for(Entity entity : player.getNearbyEntities(radius, radius, radius)) {
			if(entity instanceof Player) {
				if(entity != player) {
					players.add((Player) entity);
				}
			}
		}
		return players;
	}
	
}