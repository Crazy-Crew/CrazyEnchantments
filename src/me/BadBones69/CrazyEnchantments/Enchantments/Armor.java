package me.BadBones69.CrazyEnchantments.Enchantments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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
import me.BadBones69.CrazyEnchantments.Main;
import me.BadBones69.CrazyEnchantments.API.CEnchantments;
import me.BadBones69.CrazyEnchantments.API.Events.AngelUseEvent;
import me.BadBones69.CrazyEnchantments.API.Events.AuraActiveEvent;
import me.BadBones69.CrazyEnchantments.API.Events.EnchantmentUseEvent;
import me.BadBones69.CrazyEnchantments.API.Events.HellForgedUseEvent;

public class Armor implements Listener{
	
	private static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments");
	
	private ArrayList<Player> fall = new ArrayList<Player>();
	private HashMap<Player, HashMap<CEnchantments, Calendar>> timer = new HashMap<Player, HashMap<CEnchantments, Calendar>>();
	private int time = 99999999*20;
	
	@EventHandler
 	public void onEquip(ArmorEquipEvent e){
		Player player = e.getPlayer();
		ItemStack NewItem = e.getNewArmorPiece();
		ItemStack OldItem = e.getOldArmorPiece();
		if(Main.CE.hasEnchantments(NewItem)){
			if(Main.CE.hasEnchantment(NewItem, CEnchantments.BURNSHIELD)){
				if(CEnchantments.BURNSHIELD.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.BURNSHIELD, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, time, 0));
					}
				}
			}
			if(Main.CE.hasEnchantment(NewItem, CEnchantments.DRUNK)){
				if(CEnchantments.DRUNK.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.DRUNK, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						int power = Main.CE.getPower(NewItem, CEnchantments.DRUNK);
						player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, time, power-1));
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, time, power-1));
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, time, power));
					}
				}
			}
			if(Main.CE.hasEnchantment(NewItem, CEnchantments.HULK)){
				if(CEnchantments.HULK.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.HULK, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, time, Main.CE.getPower(NewItem, CEnchantments.HULK)-1));
						player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, time, Main.CE.getPower(NewItem, CEnchantments.HULK)-1));
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, time, Main.CE.getPower(NewItem, CEnchantments.HULK)));
					}
				}
			}
			if(Main.CE.hasEnchantment(NewItem, CEnchantments.VALOR)){
				if(CEnchantments.VALOR.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.VALOR, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, time, Main.CE.getPower(NewItem, CEnchantments.VALOR)-1));
					}
				}
			}
			if(Main.CE.hasEnchantment(NewItem, CEnchantments.OVERLOAD)){
				if(CEnchantments.OVERLOAD.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.OVERLOAD, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, time, Main.CE.getPower(NewItem, CEnchantments.OVERLOAD)));
					}
				}
			}
			if(Main.CE.hasEnchantment(NewItem, CEnchantments.NINJA)){
				if(CEnchantments.NINJA.isEnabled()){
					EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.NINJA, NewItem);
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()){
						player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, time, Main.CE.getPower(NewItem, CEnchantments.NINJA)-1));
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, time, Main.CE.getPower(NewItem, CEnchantments.NINJA)-1));
					}
				}
			}
			if(Main.CE.hasEnchantment(NewItem, CEnchantments.INSOMNIA)){
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
		if(Main.CE.hasEnchantments(OldItem)){
			if(Main.CE.hasEnchantment(OldItem, CEnchantments.BURNSHIELD)){
				if(CEnchantments.BURNSHIELD.isEnabled()){
					player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
				}
			}
			if(Main.CE.hasEnchantment(OldItem, CEnchantments.DRUNK)){
				if(CEnchantments.DRUNK.isEnabled()){
					player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
					player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
					player.removePotionEffect(PotionEffectType.SLOW);
				}
			}
			if(Main.CE.hasEnchantment(OldItem, CEnchantments.HULK)){
				if(CEnchantments.HULK.isEnabled()){
					player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
					player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
					player.removePotionEffect(PotionEffectType.SLOW);
				}
			}
			if(Main.CE.hasEnchantment(OldItem, CEnchantments.VALOR)){
				if(CEnchantments.VALOR.isEnabled()){
					player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				}
			}
			if(Main.CE.hasEnchantment(OldItem, CEnchantments.OVERLOAD)){
				if(CEnchantments.OVERLOAD.isEnabled()){
					player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
				}
			}
			if(Main.CE.hasEnchantment(OldItem, CEnchantments.NINJA)){
				if(CEnchantments.NINJA.isEnabled()){
					player.removePotionEffect(PotionEffectType.HEALTH_BOOST);
					player.removePotionEffect(PotionEffectType.SPEED);
				}
			}
			if(Main.CE.hasEnchantment(OldItem, CEnchantments.INSOMNIA)){
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
		if(!Api.allowsPVP(e.getEntity().getLocation()))return;
		if(!Api.allowsPVP(e.getDamager().getLocation()))return;
		if(e.getDamager() instanceof LivingEntity){
			if(e.getEntity() instanceof Player){
				final Player player = (Player) e.getEntity();
				final LivingEntity damager = (LivingEntity) e.getDamager();
				for(ItemStack armor : player.getEquipment().getArmorContents()){
					if(Main.CE.hasEnchantments(armor)){
						if(Main.CE.hasEnchantment(armor, CEnchantments.ROCKET)){
							if(CEnchantments.ROCKET.isEnabled()){
								if(player.getHealth() <= 8){
									if(Api.randomPicker((8-Main.CE.getPower(armor, CEnchantments.ROCKET)))){
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
						if(Main.CE.hasEnchantment(armor, CEnchantments.ENLIGHTENED)){
							if(CEnchantments.ENLIGHTENED.isEnabled()){
								if(Api.randomPicker(10)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.ENLIGHTENED, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										double heal = Main.CE.getPower(armor, CEnchantments.ENLIGHTENED);
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
						if(Main.CE.hasEnchantment(armor, CEnchantments.FORTIFY)){
							if(CEnchantments.FORTIFY.isEnabled()){
								if(Api.randomPicker(12)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.FORTIFY, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										damager.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5*20, Main.CE.getPower(armor, CEnchantments.FORTIFY)));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(armor, CEnchantments.FREEZE)){
							if(CEnchantments.FREEZE.isEnabled()){
								if(Api.randomPicker(10)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.FREEZE, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 1+Main.CE.getPower(armor, CEnchantments.FREEZE)));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(armor, CEnchantments.MOLTEN)){
							if(CEnchantments.MOLTEN.isEnabled()){
								if(Api.randomPicker(12)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.MOLTEN, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										damager.setFireTicks((Main.CE.getPower(armor, CEnchantments.MOLTEN)*2)*20);
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(armor, CEnchantments.PAINGIVER)){
							if(CEnchantments.PAINGIVER.isEnabled()){
								if(Api.randomPicker(10)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.PAINGIVER, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 3*20, Main.CE.getPower(armor, CEnchantments.PAINGIVER)));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(armor, CEnchantments.SAVIOR)){
							if(CEnchantments.SAVIOR.isEnabled()){
								if(Api.randomPicker((9-Main.CE.getPower(armor, CEnchantments.SAVIOR)))){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.SAVIOR, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										e.setDamage(e.getDamage()/2);
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(armor, CEnchantments.SMOKEBOMB)){
							if(CEnchantments.SMOKEBOMB.isEnabled()){
								if(Api.randomPicker((11-Main.CE.getPower(armor, CEnchantments.SMOKEBOMB)))){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.SMOKEBOMB, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 1));
										damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3*20, 0));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(armor, CEnchantments.VOODOO)){
							if(CEnchantments.VOODOO.isEnabled()){
								if(Api.randomPicker(7)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.VOODOO, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										damager.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5*20, Main.CE.getPower(armor, CEnchantments.VOODOO)-1));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(armor, CEnchantments.INSOMNIA)){
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
						if(Main.CE.hasEnchantment(armor, CEnchantments.CACTUS)){
							if(CEnchantments.CACTUS.isEnabled()){
								if(Api.randomPicker(4)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.CACTUS, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										damager.damage(Main.CE.getPower(armor, CEnchantments.CACTUS));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(armor, CEnchantments.STORMCALLER)){
							if(CEnchantments.STORMCALLER.isEnabled()){
								if(Api.randomPicker((12-Main.CE.getPower(armor, CEnchantments.STORMCALLER)))){
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.STORMCALLER, armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										Location loc = damager.getLocation();
										loc.getWorld().strikeLightningEffect(loc);
										for(LivingEntity en : Api.getNearbyEntities(loc, 2D, damager)){
											if(Api.allowsPVP(en.getLocation())){
												if(!Api.isFriendly(player, en)){
													en.damage(5D);
												}
											}
										}
										damager.damage(5D);
									}
								}
							}
						}
					}
				}
				if(damager instanceof Player){
					for(ItemStack armor : damager.getEquipment().getArmorContents()){
						if(Main.CE.hasEnchantments(armor)){
							if(Main.CE.hasEnchantment(armor, CEnchantments.LEADERSHIP)){
								if(CEnchantments.LEADERSHIP.isEnabled()){
									if(Api.randomPicker(12)){
										if(Api.hasFactions()||Api.hasFeudal()){
											int radius = 4+Main.CE.getPower(armor, CEnchantments.LEADERSHIP);
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
	public void onAura(AuraActiveEvent e){
		Player player = e.getPlayer();
		Player other = e.getOther();
		CEnchantments enchant = e.getEnchantment();
		int power = e.getPower();
		if(Api.allowsPVP(other.getLocation())){
			if(!Api.isFriendly(player, other)){
				Calendar cal = Calendar.getInstance();
				HashMap<CEnchantments, Calendar> eff = new HashMap<CEnchantments, Calendar>();
				if(timer.containsKey(other)){
					eff = timer.get(other);
				}
				switch(enchant){
				case BLIZZARD:
					other.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5*20, power - 1));
					break;
				case INTIMIDATE:
					other.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 3*20, power - 1));
					break;
				case ACIDRAIN:
					if(!timer.containsKey(other) || (timer.containsKey(other) && !timer.get(other).containsKey(enchant))
							|| (timer.containsKey(other) && timer.get(other).containsKey(enchant) && cal.after(timer.get(other).get(enchant)))){
						if(Api.randomPicker(45)){
							other.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 4*20, 1));
							int time = 35 - (power * 5);
							//time > 0 ? time : 0
							//Means if "time" is less then 0 put the time as "time" other wise set it to 0.
							Calendar c = cal;
							c.add(Calendar.SECOND, time > 0 ? time : 5);
							eff.put(enchant, c);
						}
					}
					break;
				case SANDSTORM:
					if(!timer.containsKey(other) || (timer.containsKey(other) && !timer.get(other).containsKey(enchant))
							|| (timer.containsKey(other) && timer.get(other).containsKey(enchant) && cal.after(timer.get(other).get(enchant)))){
						if(Api.randomPicker(38)){
							other.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10*20, 0));
							int time = 35 - (power * 5);
							//time > 0 ? time : 0
							//Means if "time" is less then 0 put the time as "time" other wise set it to 0.
							Calendar c = cal;
							c.add(Calendar.SECOND, time > 0 ? time : 5);
							eff.put(enchant, c);
						}
					}
					break;
				case RADIANT:
					if(!timer.containsKey(other) || (timer.containsKey(other) && !timer.get(other).containsKey(enchant))
							|| (timer.containsKey(other) && timer.get(other).containsKey(enchant) && cal.after(timer.get(other).get(enchant)))){
						if(Api.randomPicker(25)){
							other.setFireTicks(5 * 20);
							int time = 20 - (power * 5);
							//time > 0 ? time : 0
							//Means if "time" is less then 0 put the time as "time" other wise set it to 0.
							Calendar c = cal;
							c.add(Calendar.SECOND, time > 0 ? time : 0);
							eff.put(enchant, c);
						}
					}
					break;
				default:
					break;
				}
				timer.put(other, eff);
			}
		}
	}
	
	@EventHandler
	public void onMovement(PlayerMoveEvent e){
		Player player = e.getPlayer();
		int X = e.getFrom().getBlockX();
		int Y = e.getFrom().getBlockY();
		int Z = e.getFrom().getBlockZ();
		int x = e.getTo().getBlockX();
		int y = e.getTo().getBlockY();
		int z = e.getTo().getBlockZ();
		if(x!=X||y!=Y|z!=Z){
			for(ItemStack armor : player.getEquipment().getArmorContents()){
				if(Main.CE.hasEnchantments(armor)){
					if(Main.CE.hasEnchantment(armor, CEnchantments.NURSERY)){
						if(CEnchantments.NURSERY.isEnabled()){
							int heal = 1;
							if(Api.randomPicker((25-Main.CE.getPower(armor, CEnchantments.NURSERY)))){
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
					if(Main.CE.hasEnchantment(armor, CEnchantments.IMPLANTS)){
						if(CEnchantments.IMPLANTS.isEnabled()){
							int food = 1;
							if(Api.randomPicker((25-Main.CE.getPower(armor, CEnchantments.IMPLANTS)))){
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
					if(Main.CE.hasEnchantment(armor, CEnchantments.ANGEL)){
						if(CEnchantments.ANGEL.isEnabled()){
							if(Api.hasFactions()||Api.hasFeudal()){
								int radius = 4+Main.CE.getPower(armor, CEnchantments.ANGEL);
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
					if(Main.CE.hasEnchantment(armor, CEnchantments.HELLFORGED)){
						if(CEnchantments.HELLFORGED.isEnabled()){
							if(armor.getDurability()>0){
								if(Api.randomPicker(15)){
									int dur = armor.getDurability()-Main.CE.getPower(armor, CEnchantments.HELLFORGED);
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
				if(Main.CE.hasEnchantments(item)){
					if(Main.CE.hasEnchantment(item, CEnchantments.HELLFORGED)){
						if(CEnchantments.HELLFORGED.isEnabled()){
							if(item.getDurability()>0){
								if(Api.randomPicker(12)){
									int dur = item.getDurability()-Main.CE.getPower(item, CEnchantments.HELLFORGED);
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
		if(!Api.allowsPVP(player.getLocation()))return;
		for(ItemStack item : player.getEquipment().getArmorContents()){
			if(Main.CE.hasEnchantments(item)){
				if(Main.CE.hasEnchantment(item, CEnchantments.SELFDESTRUCT)){
					if(CEnchantments.SELFDESTRUCT.isEnabled()){
						EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.SELFDESTRUCT, item);
						Bukkit.getPluginManager().callEvent(event);
						if(!event.isCancelled()){
							Location loc = e.getEntity().getLocation();
							loc.getWorld().createExplosion(loc, Main.CE.getPower(item, CEnchantments.SELFDESTRUCT));
						}
					}
				}
			}
		}
		if(killer instanceof Player){
			for(ItemStack item : killer.getEquipment().getArmorContents()){
				if(Main.CE.hasEnchantments(item)){
					if(Main.CE.hasEnchantment(item, CEnchantments.RECOVER)){
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