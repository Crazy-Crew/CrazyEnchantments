package me.BadBones69.CrazyEnchantments.Enchantments;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
import me.BadBones69.CrazyEnchantments.API.CEnchantments;
import me.BadBones69.CrazyEnchantments.API.CrazyEnchantments;
import me.BadBones69.CrazyEnchantments.API.Events.EnchantmentUseEvent;

public class Boots implements Listener{
	CrazyEnchantments CE = CrazyEnchantments.getInstance();
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
		if(CE.hasEnchantments(NewItem)){
			if(CE.hasEnchantment(NewItem, CEnchantments.ANTIGRAVITY)){
				if(CEnchantments.ANTIGRAVITY.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.ANTIGRAVITY, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, time, 1+CE.getPower(NewItem, CEnchantments.ANTIGRAVITY)));
					}
				}
			}
			if(CE.hasEnchantment(NewItem, CEnchantments.GEARS)){
				if(CEnchantments.GEARS.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.GEARS, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, time, CE.getPower(NewItem, CEnchantments.GEARS)-1));
					}
				}
			}
			if(CE.hasEnchantment(NewItem, CEnchantments.SPRINGS)){
				if(CEnchantments.SPRINGS.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.SPRINGS, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, time, CE.getPower(NewItem, CEnchantments.SPRINGS)-1));
					}
				}
			}
			if(CE.hasEnchantment(NewItem, CEnchantments.WINGS)){
				if(CEnchantments.WINGS.isEnabled()){
					if(Api.inTerritory(player)){
						if(player.getGameMode()!=GameMode.CREATIVE){
							player.setAllowFlight(true);
						}
					}
				}
			}
		}
		if(CE.hasEnchantments(OldItem)){
			if(CE.hasEnchantment(OldItem, CEnchantments.ANTIGRAVITY)){
				if(CEnchantments.ANTIGRAVITY.isEnabled()){
					player.removePotionEffect(PotionEffectType.JUMP);
				}
			}
			if(CE.hasEnchantment(OldItem, CEnchantments.GEARS)){
				if(CEnchantments.GEARS.isEnabled()){
					player.removePotionEffect(PotionEffectType.SPEED);
				}
			}
			if(CE.hasEnchantment(OldItem, CEnchantments.SPRINGS)){
				if(CEnchantments.SPRINGS.isEnabled()){
					player.removePotionEffect(PotionEffectType.JUMP);
				}
			}
			if(CE.hasEnchantment(OldItem, CEnchantments.WINGS)){
				if(CEnchantments.WINGS.isEnabled()){
					if(player.getGameMode()!=GameMode.CREATIVE){
						player.setAllowFlight(false);
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
			if(CE.hasEnchantments(boots)){
				if(CE.hasEnchantment(boots, CEnchantments.WINGS)){
					if(CEnchantments.WINGS.isEnabled()){
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
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		Player player = e.getPlayer();
		ItemStack boots = player.getEquipment().getBoots();
		if(CE.hasEnchantments(boots)){
			if(CE.hasEnchantment(boots, CEnchantments.WINGS)){
				if(CEnchantments.WINGS.isEnabled()){
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
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Player player = e.getPlayer();
		ItemStack boots = player.getEquipment().getBoots();
		if(CE.hasEnchantments(boots)){
			if(CE.hasEnchantment(boots, CEnchantments.WINGS)){
				if(CEnchantments.WINGS.isEnabled()){
					if(Api.inTerritory(player)){
						player.setAllowFlight(true);
						Flying.add(player);
					}
				}
			}
		}
	}
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		Player player = e.getPlayer();
		ItemStack boots = player.getEquipment().getBoots();
		if(CE.hasEnchantments(boots)){
			if(CE.hasEnchantment(boots, CEnchantments.WINGS)){
				if(CEnchantments.WINGS.isEnabled()){
					player.setFlying(false);
					player.setAllowFlight(false);
					Flying.remove(player);
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
												if(lore.contains(CEnchantments.WINGS.getCustomName())){
													if(CEnchantments.WINGS.isEnabled()){
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