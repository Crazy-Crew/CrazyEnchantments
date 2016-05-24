package me.BadBones69.CrazyEnchantments.Enchantments;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import ca.thederpygolems.armorequip.ArmorEquipEvent;
import me.BadBones69.CrazyEnchantments.Api;

public class Armor implements Listener{
	static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments");
	@SuppressWarnings("static-access")
	public Armor(Plugin plugin){
		this.plugin = plugin;
	}
	ArrayList<Player> fall = new ArrayList<Player>();
	@EventHandler
 	public void onEquip(ArmorEquipEvent e){
		Player player = e.getPlayer();
		ItemStack NewItem = e.getNewArmorPiece();
		ItemStack OldItem = e.getOldArmorPiece();
		if(e.getNewArmorPiece() != null && e.getNewArmorPiece().hasItemMeta() && e.getNewArmorPiece().getType() != Material.AIR){
			if(!NewItem.getItemMeta().hasLore()){
				for(String lore : NewItem.getItemMeta().getLore()){
					if(lore.contains(Api.getEnchName("BurnShield"))){
						if(Api.isEnchantmentEnabled("BurnShield")){
							player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 55555*20, 0));
						}
					}
					if(lore.contains(Api.getEnchName("Drunk"))){
						if(Api.isEnchantmentEnabled("Drunk")){
							int power = Api.getPower(lore, Api.getEnchName("Drunk"));
							player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 55555, power-1));
							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 55555, power-1));
							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 55555, power));
						}
					}
					if(lore.contains(Api.getEnchName("Hulk"))){
						if(Api.isEnchantmentEnabled("Hulk")){
							player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 55555, Api.getPower(lore, Api.getEnchName("Hulk"))-1));
							player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 55555, Api.getPower(lore, Api.getEnchName("Hulk"))-1));
							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 55555, Api.getPower(lore, Api.getEnchName("Hulk"))));
						}
					}
					if(lore.contains(Api.getEnchName("Valor"))){
						if(Api.isEnchantmentEnabled("Valor")){
							player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 55555, Api.getPower(lore, Api.getEnchName("Valor"))-1));
						}
					}
					if(lore.contains(Api.getEnchName("OverLoad"))){
						if(Api.isEnchantmentEnabled("OverLoad")){
							player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 55555*20, Api.getPower(lore, Api.getEnchName("OverLoad"))-1));
						}
					}
					if(lore.contains(Api.getEnchName("Ninja"))){
						if(Api.isEnchantmentEnabled("Ninja")){
							player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 55555*20, Api.getPower(lore, Api.getEnchName("Ninja"))-1));
							player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 55555*20, Api.getPower(lore, Api.getEnchName("Ninja"))-1));
						}
					}
					if(lore.contains(Api.getEnchName("Insomnia"))){
						if(Api.isEnchantmentEnabled("Insomnia")){
							player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 55555, 0));
							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 55555, 0));
							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 55555, 1));
						}
					}
				}
			}
		}
		if(e.getOldArmorPiece() != null && e.getOldArmorPiece().hasItemMeta() && e.getOldArmorPiece().getType() != Material.AIR){
			if(OldItem.getItemMeta().hasLore()){
				for(String lore : OldItem.getItemMeta().getLore()){
					if(lore.contains(Api.getEnchName("BurnShield"))){
						if(Api.isEnchantmentEnabled("BurnShield")){
							player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
						}
					}
					if(lore.contains(Api.getEnchName("Drunk"))){
						if(Api.isEnchantmentEnabled("Drunk")){
							player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
							player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
							player.removePotionEffect(PotionEffectType.SLOW);
						}
					}
					if(lore.contains(Api.getEnchName("Hulk"))){
						if(Api.isEnchantmentEnabled("Hulk")){
							player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
							player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
							player.removePotionEffect(PotionEffectType.SLOW);
						}
					}
					if(lore.contains(Api.getEnchName("Valor"))){
						if(Api.isEnchantmentEnabled("Valor")){
							player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
						}
					}
					if(lore.contains(Api.getEnchName("OverLoad"))){
						if(Api.isEnchantmentEnabled("OverLoad")){
							player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
						}
					}
					if(lore.contains(Api.getEnchName("Ninja"))){
						if(Api.isEnchantmentEnabled("Ninja")){
							player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
							player.removePotionEffect(PotionEffectType.SPEED);
						}
					}
					if(lore.contains(Api.getEnchName("Insomnia"))){
						if(Api.isEnchantmentEnabled("Insomnia")){
							player.removePotionEffect(PotionEffectType.CONFUSION);
							player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
							player.removePotionEffect(PotionEffectType.SLOW);
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(Api.isFriendly(e.getDamager(), e.getEntity()))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if(!Api.allowsPVP(e.getDamager()))return;
		if(e.getDamager() instanceof LivingEntity){
			if(e.getEntity() instanceof Player){
				final Player player = (Player) e.getEntity();
				final LivingEntity damager = (LivingEntity) e.getDamager();
				for(ItemStack armor : player.getEquipment().getArmorContents()){
					if(armor!=null){
						if(armor.hasItemMeta()){
							if(armor.getItemMeta().hasLore()){
								for(String lore : armor.getItemMeta().getLore()){
									if(lore.contains(Api.getEnchName("Rocket"))){
										if(Api.isEnchantmentEnabled("Rocket")){
											if(player.getHealth() <= 8){
												if(Api.randomPicker((8-Api.getPower(lore, Api.getEnchName("Rocket"))))){
													Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
														public void run(){
															Vector v = player.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize().setY(1);
															player.setVelocity(v);
														}
													}, 1);
													player.getWorld().playEffect(player.getLocation(), Effect.EXPLOSION_HUGE, 1);
													fall.add(player);
													Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
														public void run(){
															fall.remove(player);
														}
													}, 8*20);
												}
											}
										}
									}
									if(lore.contains(Api.getEnchName("Enlightened"))){
										if(Api.isEnchantmentEnabled("Enlightened")){
											if(Api.randomPicker(10)){
												double heal = Api.getPower(lore, Api.getEnchName("Enlightened"));
												if(player.getHealth()+heal<player.getMaxHealth())player.setHealth(player.getHealth()+heal);
												if(player.getHealth()+heal>=player.getMaxHealth())player.setHealth(player.getMaxHealth());
											}
										}
									}
									if(lore.contains(Api.getEnchName("Fortify"))){
										if(Api.isEnchantmentEnabled("Fortify")){
											if(Api.randomPicker(12)){
												damager.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5*20, Api.getPower(lore, Api.getEnchName("Fortify"))));
											}
										}
									}
									if(lore.contains(Api.getEnchName("Freeze"))){
										if(Api.isEnchantmentEnabled("Freeze")){
											if(Api.randomPicker(10)){
												damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 1+Api.getPower(lore, Api.getEnchName("Freeze"))));
											}
										}
									}
									if(lore.contains(Api.getEnchName("Molten"))){
										if(Api.isEnchantmentEnabled("Molten")){
											if(Api.randomPicker(12)){
												damager.setFireTicks((Api.getPower(lore, Api.getEnchName("Molten"))*2)*20);
											}
										}
									}
									if(lore.contains(Api.getEnchName("PainGiver"))){
										if(Api.isEnchantmentEnabled("PainGiver")){
											if(Api.randomPicker(10)){
												damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 3*20, Api.getPower(lore, Api.getEnchName("PainGiver"))));
											}
										}
									}
									if(lore.contains(Api.getEnchName("Savior"))){
										if(Api.isEnchantmentEnabled("Savior")){
											if(Api.randomPicker((9-Api.getPower(lore, Api.getEnchName("Savior"))))){
												e.setDamage(e.getDamage()/2);
											}
										}
									}
									if(lore.contains(Api.getEnchName("SmokeBomb"))){
										if(Api.isEnchantmentEnabled("SmokeBomb")){
											if(Api.randomPicker((11-Api.getPower(lore, Api.getEnchName("SmokeBomb"))))){
												damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 1));
												damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3*20, 0));
											}
										}
									}
									if(lore.contains(Api.getEnchName("Voodoo"))){
										if(Api.isEnchantmentEnabled("Voodoo")){
											if(Api.randomPicker(7)){
												damager.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5*20, Api.getPower(lore, Api.getEnchName("Voodoo"))-1));
											}
										}
									}
									if(lore.contains(Api.getEnchName("Insomnia"))){
										if(Api.isEnchantmentEnabled("Insomnia")){
											if(Api.randomPicker(3)){
												e.setDamage((e.getDamage()*2));
											}
										}
									}
									if(lore.contains(Api.getEnchName("Cactus"))){
										if(Api.isEnchantmentEnabled("Cactus")){
											if(Api.randomPicker(4)){
												damager.damage(Api.getPower(lore, Api.getEnchName("Cactus")));
											}
										}
									}
									if(lore.contains(Api.getEnchName("StormCaller"))){
										if(Api.isEnchantmentEnabled("StormCaller")){
											if(Api.randomPicker((12-Api.getPower(lore, Api.getEnchName("StormCaller"))))){
												damager.getWorld().strikeLightning(damager.getLocation());
											}
										}
									}
								}
							}
						}
					}
				}
				if(damager instanceof Player){
					for(ItemStack armor : damager.getEquipment().getArmorContents()){
						if(armor!=null){
							if(armor.hasItemMeta()){
								if(armor.getItemMeta().hasLore()){
									for(String lore : armor.getItemMeta().getLore()){
										if(lore.contains(Api.getEnchName("Leadership"))){
											if(Api.isEnchantmentEnabled("Leadership")){
												if(Api.randomPicker(12)){
													if(Api.hasFactions()){
														int radius = 4+Api.getPower(lore, Api.getEnchName("Leadership"));
														int players = 0;
														for(Entity en : damager.getNearbyEntities(radius, radius, radius)){
															if(en instanceof Player){
																Player o = (Player) en;
																if(Api.isFriendly(damager, o))players++;
															}
														}
														if(players>0){
															e.setDamage(e.getDamage()+(players/2));
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
			}
		}
	}
	@EventHandler
	public void onMovment(PlayerMoveEvent e){
		Player player = e.getPlayer();
		int X = e.getFrom().getBlockX();
		int Y = e.getFrom().getBlockY();
		int Z = e.getFrom().getBlockZ();
		int x = e.getTo().getBlockX();
		int y = e.getTo().getBlockY();
		int z = e.getTo().getBlockZ();
		if(x!=X||y!=Y|z!=Z){
			for(ItemStack i : player.getEquipment().getArmorContents()){
				if(i!=null){
					if(i.hasItemMeta()){
						if(i.getItemMeta().hasLore()){
							for(String lore : i.getItemMeta().getLore()){
								if(lore.contains(Api.getEnchName("Nursery"))){
									if(Api.isEnchantmentEnabled("Nursery")){
										int heal = 1;
										if(Api.randomPicker((25-Api.getPower(lore, Api.getEnchName("Nursery"))))){
											if(player.getHealth()+heal<=player.getMaxHealth()){
												player.setHealth(player.getHealth()+heal);
											}
											if(player.getHealth()+heal>=player.getMaxHealth()){
												player.setHealth(player.getMaxHealth());
											}
										}
									}
								}
								if(lore.contains(Api.getEnchName("Implants"))){
									if(Api.isEnchantmentEnabled("Implants")){
										int food = 1;
										if(Api.randomPicker((25-Api.getPower(lore, Api.getEnchName("Implants"))))){
											if(player.getFoodLevel()+food<=20){
												player.setFoodLevel(player.getFoodLevel()+food);
											}
											if(player.getFoodLevel()+food>=20){
												player.setFoodLevel(20);
											}
										}
									}
								}
								if(lore.contains(Api.getEnchName("Angel"))){
									if(Api.isEnchantmentEnabled("Angel")){
										if(Api.hasFactions()){
											int radius = 4+Api.getPower(lore, Api.getEnchName("Angel"));
											for(Entity en : player.getNearbyEntities(radius, radius, radius)){
												if(en instanceof Player){
													Player o = (Player) en;
													if(Api.isFriendly(player, o)){
														o.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3*20, 0));
													}
												}
											}
										}
									}
								}
								if(lore.contains(Api.getEnchName("HellForged"))){
									if(Api.isEnchantmentEnabled("HellForged")){
										if(i.getDurability()>0){
											if(Api.randomPicker(12)){
												int dur = i.getDurability()-Api.getPower(lore, Api.getEnchName("HellForged"));
												if(dur>0){
													i.setDurability((short)dur);
												}else{
													i.setDurability((short)0);
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
			for(ItemStack item : player.getInventory().getContents()){
				if(item!=null){
					if(item.hasItemMeta()){
						if(item.getItemMeta().hasLore()){
							for(String lore : item.getItemMeta().getLore()){
								if(lore.contains(Api.getEnchName("HellForged"))){
									if(Api.isEnchantmentEnabled("HellForged")){
										if(item.getDurability()>0){
											if(Api.randomPicker(12)){
												int dur = item.getDurability()-Api.getPower(lore, Api.getEnchName("HellForged"));
												if(dur>0){
													item.setDurability((short)dur);
												}else{
													item.setDurability((short)0);
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
	}
	@EventHandler
 	public void onDeath(PlayerDeathEvent e){
		Player player = e.getEntity();
		Player killer = player.getKiller();
		if(!Api.allowsPVP(player))return;
		for(ItemStack item : player.getEquipment().getArmorContents()){
			if(item==null)return;
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasLore()){
					for(String l : item.getItemMeta().getLore()){
						if(l.contains(Api.getEnchName("SelfDestruct"))){
							if(Api.isEnchantmentEnabled("SelfDestruct")){
								Location loc = e.getEntity().getLocation();
								loc.getWorld().createExplosion(loc, Api.getPower(l, Api.getEnchName("SelfDestruct")));
							}
						}
					}
				}
			}
		}
		if(killer instanceof Player){
			for(ItemStack item : killer.getEquipment().getArmorContents()){
				if(item==null)return;
				if(item.hasItemMeta()){
					if(item.getItemMeta().hasLore()){
						for(String l : item.getItemMeta().getLore()){
							if(l.contains(Api.getEnchName("Recover"))){
								if(Api.isEnchantmentEnabled("Recover")){
									killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 8*20, 2));
									killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5*20, 1));
								}
							}
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void onPlayerFallDamage(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			if(e.getCause() == DamageCause.FALL){
				if(fall.contains((Player)e.getEntity())){
					e.setCancelled(true);
				}
			}
		}
	}
}