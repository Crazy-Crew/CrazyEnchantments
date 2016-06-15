package me.BadBones69.CrazyEnchantments.Enchantments;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ca.thederpygolems.armorequip.ArmorEquipEvent;
import me.BadBones69.CrazyEnchantments.Api;
import me.BadBones69.CrazyEnchantments.Main;
import me.BadBones69.CrazyEnchantments.ParticleEffect;

public class Boots implements Listener{
	static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments");
	@SuppressWarnings("static-access")
	public Boots(Plugin plugin){
		this.plugin = plugin;
	}
	public static ArrayList<Player> Flying = new ArrayList<Player>();
	int time = 99999999*20;
	@EventHandler
 	public void onEquip(ArmorEquipEvent e){
		Player player = e.getPlayer();
		ItemStack NewItem = e.getNewArmorPiece();
		ItemStack OldItem = e.getOldArmorPiece();
		if(e.getNewArmorPiece() != null && e.getNewArmorPiece().hasItemMeta() && e.getNewArmorPiece().getType() != Material.AIR){
			if(NewItem.getItemMeta().hasLore()){
				for(String lore : NewItem.getItemMeta().getLore()){
					if(lore.contains(Api.getEnchName("AntiGravity"))){
						if(Api.isEnchantmentEnabled("AntiGravity")){
							player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, time, 1+Api.getPower(lore, Api.getEnchName("AntiGravity"))));
						}
					}
					if(lore.contains(Api.getEnchName("Gears"))){
						if(Api.isEnchantmentEnabled("Gears")){
							player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, time, Api.getPower(lore, Api.getEnchName("Gears"))-1));
						}
					}
					if(lore.contains(Api.getEnchName("Springs"))){
						if(Api.isEnchantmentEnabled("Springs")){
							player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, time, Api.getPower(lore, Api.getEnchName("Springs"))-1));
						}
					}
					if(lore.contains(Api.getEnchName("Wings"))){
						if(Api.isEnchantmentEnabled("Wings")){
							if(Api.inTerritory(player)){
								if(player.getGameMode()!=GameMode.CREATIVE){
									player.setAllowFlight(true);
								}
							}
						}
					}
				}
			}
		}
		if(e.getOldArmorPiece() != null && e.getOldArmorPiece().hasItemMeta() && e.getOldArmorPiece().getType() != Material.AIR){
			if(OldItem.getItemMeta().hasLore()){
				for(String lore : OldItem.getItemMeta().getLore()){
					if(lore.contains(Api.getEnchName("AntiGravity"))){
						if(Api.isEnchantmentEnabled("AntiGravity")){
							player.removePotionEffect(PotionEffectType.JUMP);
						}
					}
					if(lore.contains(Api.getEnchName("Gears"))){
						if(Api.isEnchantmentEnabled("Gears")){
							player.removePotionEffect(PotionEffectType.SPEED);
						}
					}
					if(lore.contains(Api.getEnchName("Springs"))){
						if(Api.isEnchantmentEnabled("Springs")){
							player.removePotionEffect(PotionEffectType.JUMP);
						}
					}
					if(lore.contains(Api.getEnchName("Wings"))){
						if(Api.isEnchantmentEnabled("Wings")){
							if(player.getGameMode()!=GameMode.CREATIVE){
								player.setAllowFlight(false);
							}
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void onFly(PlayerToggleFlightEvent e){
		Player player = e.getPlayer();
		if(Api.inTerritory(player)){
			ItemStack boots = player.getEquipment().getBoots();
			if(boots!=null){
				if(boots.hasItemMeta()){
					if(boots.getItemMeta().hasLore()){
						for(String lore : boots.getItemMeta().getLore()){
							if(lore.contains(Api.getEnchName("Wings"))){
								if(Api.isEnchantmentEnabled("Wings")){
									if(e.isFlying()){
										e.setCancelled(true);
										player.setFlying(true);
										Flying.add(player);
									}else{
										Flying.remove(player);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		Player player = e.getPlayer();
		ItemStack boots = player.getEquipment().getBoots();
		if(boots!=null){
			if(boots.hasItemMeta()){
				if(boots.getItemMeta().hasLore()){
					for(String lore : boots.getItemMeta().getLore()){
						if(lore.contains(Api.getEnchName("Wings"))){
							if(Api.isEnchantmentEnabled("Wings")){
								if(!Api.inTerritory(player)){
									if(player.isFlying()){
										if(player.getGameMode()!=GameMode.CREATIVE){
											player.setFlying(false);
											player.setAllowFlight(false);
											Flying.remove(player);
										}
									}
								}else{
									if(!player.getAllowFlight()){
										player.setAllowFlight(true);
									}
								}
								if(player.isFlying()){
									Flying.add(player);
								}
							}
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Player player = e.getPlayer();
		ItemStack boots = player.getEquipment().getBoots();
		if(boots!=null){
			if(boots.hasItemMeta()){
				if(boots.getItemMeta().hasLore()){
					for(String lore : boots.getItemMeta().getLore()){
						if(lore.contains(Api.getEnchName("Wings"))){
							if(Api.isEnchantmentEnabled("Wings")){
								if(Api.inTerritory(player)){
									player.setAllowFlight(true);
									Flying.add(player);
								}
							}
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		Player player = e.getPlayer();
		ItemStack boots = player.getEquipment().getBoots();
		if(boots!=null){
			if(boots.hasItemMeta()){
				if(boots.getItemMeta().hasLore()){
					for(String lore : boots.getItemMeta().getLore()){
						if(lore.contains(Api.getEnchName("Wings"))){
							if(Api.isEnchantmentEnabled("Wings")){
								player.setFlying(false);
								player.setAllowFlight(false);
								Flying.remove(player);
							}
						}
					}
				}
			}
		}
	}
	public static void onStart(){
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			@Override
			public void run() {
				if(!Main.settings.getConfig().contains("Settings.Clouds")||Main.settings.getConfig().getBoolean("Settings.Clouds")){
					for(Player player : Bukkit.getServer().getOnlinePlayers()){
						if(Flying.contains(player)){
							Location l = player.getLocation().subtract(0, .25, 0);
							if(player.isFlying()){
								ItemStack boots = player.getEquipment().getBoots();
								if(boots!=null){
									if(boots.hasItemMeta()){
										if(boots.getItemMeta().hasLore()){
											for(String lore : boots.getItemMeta().getLore()){
												if(lore.contains(Api.getEnchName("Wings"))){
													if(Api.isEnchantmentEnabled("Wings")){
														ParticleEffect.CLOUD.display((float) .25, (float) 0, (float) .25, 0, 10, l, 100);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}, 1, 1);
	}
}