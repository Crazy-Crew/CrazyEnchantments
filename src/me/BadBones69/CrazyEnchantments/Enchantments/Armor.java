package me.BadBones69.CrazyEnchantments.Enchantments;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
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
import me.BadBones69.CrazyEnchantments.API.CEnchantments;
import me.BadBones69.CrazyEnchantments.API.CrazyEnchantments;
import me.BadBones69.CrazyEnchantments.API.Events.AngelUseEvent;
import me.BadBones69.CrazyEnchantments.API.Events.EnchantmentUseEvent;
import me.BadBones69.CrazyEnchantments.API.Events.HellForgedUseEvent;

public class Armor implements Listener{
	CrazyEnchantments CE = CrazyEnchantments.getInstance();
	static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments");
	@SuppressWarnings("static-access")
	public Armor(Plugin plugin){
		this.plugin = plugin;
	}
	ArrayList<Player> fall = new ArrayList<Player>();
	int time = 99999999*20;
	@EventHandler
 	public void onEquip(ArmorEquipEvent e){
		Player player = e.getPlayer();
		ItemStack NewItem = e.getNewArmorPiece();
		ItemStack OldItem = e.getOldArmorPiece();
		if(CE.hasEnchantments(NewItem)){
			if(CE.hasEnchantment(NewItem, CEnchantments.BURNSHIELD)){
				if(CEnchantments.BURNSHIELD.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.BURNSHIELD, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, time, 0));
					}
				}
			}
			if(CE.hasEnchantment(NewItem, CEnchantments.DRUNK)){
				if(CEnchantments.DRUNK.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.DRUNK, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						int power = CE.getPower(NewItem, CEnchantments.DRUNK);
						player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, time, power-1));
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, time, power-1));
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, time, power));
					}
				}
			}
			if(CE.hasEnchantment(NewItem, CEnchantments.HULK)){
				if(CEnchantments.HULK.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.HULK, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, time, CE.getPower(NewItem, CEnchantments.HULK)-1));
						player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, time, CE.getPower(NewItem, CEnchantments.HULK)-1));
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, time, CE.getPower(NewItem, CEnchantments.HULK)));
					}
				}
			}
			if(CE.hasEnchantment(NewItem, CEnchantments.VALOR)){
				if(CEnchantments.VALOR.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.VALOR, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, time, CE.getPower(NewItem, CEnchantments.VALOR)-1));
					}
				}
			}
			if(CE.hasEnchantment(NewItem, CEnchantments.OVERLOAD)){
				if(CEnchantments.OVERLOAD.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.OVERLOAD, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, time, CE.getPower(NewItem, CEnchantments.OVERLOAD)));
					}
				}
			}
			if(CE.hasEnchantment(NewItem, CEnchantments.NINJA)){
				if(CEnchantments.NINJA.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.NINJA, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, time, CE.getPower(NewItem, CEnchantments.NINJA)-1));
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, time, CE.getPower(NewItem, CEnchantments.NINJA)-1));
					}
				}
			}
			if(CE.hasEnchantment(NewItem, CEnchantments.INSOMNIA)){
				if(CEnchantments.INSOMNIA.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.INSOMNIA, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, time, 0));
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, time, 0));
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, time, 1));
					}
				}
			}
		}
		if(CE.hasEnchantments(OldItem)){
			if(CE.hasEnchantment(OldItem, CEnchantments.BURNSHIELD)){
				if(CEnchantments.BURNSHIELD.isEnabled()){
					player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
				}
			}
			if(CE.hasEnchantment(OldItem, CEnchantments.DRUNK)){
				if(CEnchantments.DRUNK.isEnabled()){
					player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
					player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
					player.removePotionEffect(PotionEffectType.SLOW);
				}
			}
			if(CE.hasEnchantment(OldItem, CEnchantments.HULK)){
				if(CEnchantments.HULK.isEnabled()){
					player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
					player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
					player.removePotionEffect(PotionEffectType.SLOW);
				}
			}
			if(CE.hasEnchantment(OldItem, CEnchantments.VALOR)){
				if(CEnchantments.VALOR.isEnabled()){
					player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				}
			}
			if(CE.hasEnchantment(OldItem, CEnchantments.OVERLOAD)){
				if(CEnchantments.OVERLOAD.isEnabled()){
					player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
				}
			}
			if(CE.hasEnchantment(OldItem, CEnchantments.NINJA)){
				if(CEnchantments.NINJA.isEnabled()){
					player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
					player.removePotionEffect(PotionEffectType.SPEED);
				}
			}
			if(CE.hasEnchantment(OldItem, CEnchantments.INSOMNIA)){
				if(CEnchantments.INSOMNIA.isEnabled()){
					player.removePotionEffect(PotionEffectType.CONFUSION);
					player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
					player.removePotionEffect(PotionEffectType.SLOW);
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
					if(CE.hasEnchantments(armor)){
						if(CE.hasEnchantment(armor, CEnchantments.ROCKET)){
							if(CEnchantments.ROCKET.isEnabled()){
								if(player.getHealth() <= 8){
									if(Api.randomPicker((8-CE.getPower(armor, CEnchantments.ROCKET)))){
										EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.ROCKET, armor);
										Bukkit.getPluginManager().callEvent(event);
										if(!event.isCancelled()){
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
						}
						if(CE.hasEnchantment(armor, CEnchantments.ENLIGHTENED)){
							if(CEnchantments.ENLIGHTENED.isEnabled()){
								if(Api.randomPicker(10)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.ENLIGHTENED, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										double heal = CE.getPower(armor, CEnchantments.ENLIGHTENED);
										if(player.getHealth()+heal<player.getMaxHealth()){
											player.setHealth(player.getHealth()+heal);
										}
										if(player.getHealth()+heal>=player.getMaxHealth()){
											player.setHealth(player.getMaxHealth());
										}
									}
								}
							}
						}
						if(CE.hasEnchantment(armor, CEnchantments.FORTIFY)){
							if(CEnchantments.FORTIFY.isEnabled()){
								if(Api.randomPicker(12)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.FORTIFY, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										damager.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5*20, CE.getPower(armor, CEnchantments.FORTIFY)));
									}
								}
							}
						}
						if(CE.hasEnchantment(armor, CEnchantments.FREEZE)){
							if(CEnchantments.FREEZE.isEnabled()){
								if(Api.randomPicker(10)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.FREEZE, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 1+CE.getPower(armor, CEnchantments.FREEZE)));
									}
								}
							}
						}
						if(CE.hasEnchantment(armor, CEnchantments.MOLTEN)){
							if(CEnchantments.MOLTEN.isEnabled()){
								if(Api.randomPicker(12)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.MOLTEN, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										damager.setFireTicks((CE.getPower(armor, CEnchantments.MOLTEN)*2)*20);
									}
								}
							}
						}
						if(CE.hasEnchantment(armor, CEnchantments.PAINGIVER)){
							if(CEnchantments.PAINGIVER.isEnabled()){
								if(Api.randomPicker(10)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.PAINGIVER, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 3*20, CE.getPower(armor, CEnchantments.PAINGIVER)));
									}
								}
							}
						}
						if(CE.hasEnchantment(armor, CEnchantments.SAVIOR)){
							if(CEnchantments.SAVIOR.isEnabled()){
								if(Api.randomPicker((9-CE.getPower(armor, CEnchantments.SAVIOR)))){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.SAVIOR, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										e.setDamage(e.getDamage()/2);
									}
								}
							}
						}
						if(CE.hasEnchantment(armor, CEnchantments.SMOKEBOMB)){
							if(CEnchantments.SMOKEBOMB.isEnabled()){
								if(Api.randomPicker((11-CE.getPower(armor, CEnchantments.SMOKEBOMB)))){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.SMOKEBOMB, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 1));
										damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3*20, 0));
									}
								}
							}
						}
						if(CE.hasEnchantment(armor, CEnchantments.VOODOO)){
							if(CEnchantments.VOODOO.isEnabled()){
								if(Api.randomPicker(7)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.VOODOO, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										damager.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5*20, CE.getPower(armor, CEnchantments.VOODOO)-1));
									}
								}
							}
						}
						if(CE.hasEnchantment(armor, CEnchantments.INSOMNIA)){
							if(CEnchantments.INSOMNIA.isEnabled()){
								if(Api.randomPicker(3)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.INSOMNIA, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										e.setDamage((e.getDamage()*2));
									}
								}
							}
						}
						if(CE.hasEnchantment(armor, CEnchantments.CACTUS)){
							if(CEnchantments.CACTUS.isEnabled()){
								if(Api.randomPicker(4)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.CACTUS, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										damager.damage(CE.getPower(armor, CEnchantments.CACTUS));
									}
								}
							}
						}
						if(CE.hasEnchantment(armor, CEnchantments.STORMCALLER)){
							if(CEnchantments.STORMCALLER.isEnabled()){
								if(Api.randomPicker((12-CE.getPower(armor, CEnchantments.STORMCALLER)))){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.STORMCALLER, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										damager.getWorld().strikeLightning(damager.getLocation());
									}
								}
							}
						}
					}
				}
				if(damager instanceof Player){
					for(ItemStack armor : damager.getEquipment().getArmorContents()){
						if(CE.hasEnchantments(armor)){
							if(CE.hasEnchantment(armor, CEnchantments.LEADERSHIP)){
								if(CEnchantments.LEADERSHIP.isEnabled()){
									if(Api.randomPicker(12)){
										if(Api.hasFactions()){
											int radius = 4+CE.getPower(armor, CEnchantments.LEADERSHIP);
											int players = 0;
											for(Entity en : damager.getNearbyEntities(radius, radius, radius)){
												if(en instanceof Player){
													Player o = (Player) en;
													if(Api.isFriendly(damager, o)){
														players++;
													}
												}
											}
											if(players>0){
												EnchantmentUseEvent event = new EnchantmentUseEvent((Player)damager, CEnchantments.LEADERSHIP, armor);
												Bukkit.getPluginManager().callEvent(event);
												if(!event.isCancelled()){
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
			for(ItemStack armor : player.getEquipment().getArmorContents()){
				if(CE.hasEnchantments(armor)){
					if(CE.hasEnchantment(armor, CEnchantments.NURSERY)){
						if(CEnchantments.NURSERY.isEnabled()){
							int heal = 1;
							if(Api.randomPicker((25-CE.getPower(armor, CEnchantments.NURSERY)))){
								if(player.getMaxHealth()>player.getHealth()){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.NURSERY, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										if(player.getHealth()+heal<=player.getMaxHealth()){
											player.setHealth(player.getHealth()+heal);
										}
										if(player.getHealth()+heal>=player.getMaxHealth()){
											player.setHealth(player.getMaxHealth());
										}
									}
								}
							}
						}
					}
					if(CE.hasEnchantment(armor, CEnchantments.IMPLANTS)){
						if(CEnchantments.IMPLANTS.isEnabled()){
							int food = 1;
							if(Api.randomPicker((25-CE.getPower(armor, CEnchantments.IMPLANTS)))){
								if(player.getFoodLevel()<20){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.IMPLANTS, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										if(player.getFoodLevel()+food<=20){
											player.setFoodLevel(player.getFoodLevel()+food);
										}
										if(player.getFoodLevel()+food>=20){
											player.setFoodLevel(20);
										}
									}
								}
							}
						}
					}
					if(CE.hasEnchantment(armor, CEnchantments.ANGEL)){
						if(CEnchantments.ANGEL.isEnabled()){
							if(Api.hasFactions()){
								int radius = 4+CE.getPower(armor, CEnchantments.ANGEL);
								for(Entity en : player.getNearbyEntities(radius, radius, radius)){
									if(en instanceof Player){
										Player o = (Player) en;
										if(Api.isFriendly(player, o)){
											AngelUseEvent event = new AngelUseEvent(player, armor);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()){
												o.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3*20, 0));
											}
										}
									}
								}
							}
						}
					}
					if(CE.hasEnchantment(armor, CEnchantments.HELLFORGED)){
						if(CEnchantments.HELLFORGED.isEnabled()){
							if(armor.getDurability()>0){
								if(Api.randomPicker(15)){
									int dur = armor.getDurability()-CE.getPower(armor, CEnchantments.HELLFORGED);
									if(armor.getDurability()>0){
										HellForgedUseEvent event = new HellForgedUseEvent(player, armor);
										Bukkit.getPluginManager().callEvent(event);
										if(!event.isCancelled()){
											if(dur>0){
												armor.setDurability((short)dur);
											}else{
												armor.setDurability((short)0);
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
				if(CE.hasEnchantments(item)){
					if(CE.hasEnchantment(item, CEnchantments.HELLFORGED)){
						if(CEnchantments.HELLFORGED.isEnabled()){
							if(item.getDurability()>0){
								if(Api.randomPicker(12)){
									int dur = item.getDurability()-CE.getPower(item, CEnchantments.HELLFORGED);
									if(item.getDurability()>0){
										HellForgedUseEvent event = new HellForgedUseEvent(player, item);
										Bukkit.getPluginManager().callEvent(event);
										if(!event.isCancelled()){
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
	@EventHandler
 	public void onDeath(PlayerDeathEvent e){
		Player player = e.getEntity();
		Player killer = player.getKiller();
		if(!Api.allowsPVP(player))return;
		for(ItemStack item : player.getEquipment().getArmorContents()){
			if(CE.hasEnchantments(item)){
				if(CE.hasEnchantment(item, CEnchantments.SELFDESTRUCT)){
					if(CEnchantments.SELFDESTRUCT.isEnabled()){
						EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.SELFDESTRUCT, item);
						Bukkit.getPluginManager().callEvent(event);
						if(!event.isCancelled()){
							Location loc = e.getEntity().getLocation();
							loc.getWorld().createExplosion(loc, CE.getPower(item, CEnchantments.SELFDESTRUCT));
						}
					}
				}
			}
		}
		if(killer instanceof Player){
			for(ItemStack item : killer.getEquipment().getArmorContents()){
				if(CE.hasEnchantments(item)){
					if(CE.hasEnchantment(item, CEnchantments.RECOVER)){
						if(CEnchantments.RECOVER.isEnabled()){
							EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.RECOVER, item);
							Bukkit.getPluginManager().callEvent(event);
							if(!event.isCancelled()){
								killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 8*20, 2));
								killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5*20, 1));
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