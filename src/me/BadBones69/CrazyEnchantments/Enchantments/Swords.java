package me.BadBones69.CrazyEnchantments.Enchantments;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.BadBones69.CrazyEnchantments.Main;
import me.BadBones69.CrazyEnchantments.Methods;
import me.BadBones69.CrazyEnchantments.API.CEnchantments;
import me.BadBones69.CrazyEnchantments.API.Events.DisarmerUseEvent;
import me.BadBones69.CrazyEnchantments.API.Events.EnchantmentUseEvent;
import me.BadBones69.CrazyEnchantments.API.currencyapi.Currency;
import me.BadBones69.CrazyEnchantments.API.currencyapi.CurrencyAPI;
import me.BadBones69.CrazyEnchantments.multisupport.SpartanSupport;
import me.BadBones69.CrazyEnchantments.multisupport.Support;

public class Swords implements Listener{
	
	private static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments");
	
	private HashMap<Player, Double> multi = new HashMap<Player, Double>();
	private HashMap<Player, Integer> num = new HashMap<Player, Integer>();
	private HashMap<Player, Integer> reset = new HashMap<Player, Integer>();
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(e.isCancelled())return;
		if(Support.isFriendly(e.getDamager(), e.getEntity()))return;
		if(e.getEntity() instanceof LivingEntity){
			if(e.getDamager() instanceof Player){
				final Player damager = (Player) e.getDamager();
				LivingEntity en = (LivingEntity) e.getEntity();
				ItemStack It = Methods.getItemInHand(damager);
				if(!e.getEntity().isDead()){
					if(Main.CE.hasEnchantments(It)){
						if(Main.CE.hasEnchantment(It, CEnchantments.DISARMER)){
							if(CEnchantments.DISARMER.isEnabled()){
								if(e.getEntity() instanceof Player){
									Player player = (Player) e.getEntity();
									int slot = Methods.percentPick(4, 1);
									if(Methods.randomPicker((31-Main.CE.getPower(It, CEnchantments.DISARMER)))){
										if(slot == 1){
											if(player.getEquipment().getHelmet() != null){
												ItemStack item = player.getEquipment().getHelmet();
												DisarmerUseEvent event = new DisarmerUseEvent(player, damager, item);
												Bukkit.getPluginManager().callEvent(event);
												if(!event.isCancelled()){
													player.getEquipment().setHelmet(null);
													player.getInventory().addItem(item);
												}
											}
										}
										if(slot == 2){
											if(player.getEquipment().getChestplate() != null){
												ItemStack item = player.getEquipment().getChestplate();
												DisarmerUseEvent event = new DisarmerUseEvent(player, damager, item);
												Bukkit.getPluginManager().callEvent(event);
												if(!event.isCancelled()){
													player.getEquipment().setChestplate(null);
													player.getInventory().addItem(item);
												}
											}
										}
										if(slot == 3){
											if(player.getEquipment().getLeggings() != null){
												ItemStack item = player.getEquipment().getLeggings();
												DisarmerUseEvent event = new DisarmerUseEvent(player, damager, item);
												Bukkit.getPluginManager().callEvent(event);
												if(!event.isCancelled()){
													player.getEquipment().setLeggings(null);
													player.getInventory().addItem(item);
												}
											}
										}
										if(slot == 4){
											if(player.getEquipment().getBoots() != null){
												ItemStack item = player.getEquipment().getBoots();
												DisarmerUseEvent event = new DisarmerUseEvent(player, damager, item);
												Bukkit.getPluginManager().callEvent(event);
												if(!event.isCancelled()){
													player.getEquipment().setBoots(null);
													player.getInventory().addItem(item);
												}
											}
										}
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(It, CEnchantments.RAGE)){
							if(CEnchantments.RAGE.isEnabled()){
								EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.RAGE, It);
								Bukkit.getPluginManager().callEvent(event);
								if(!event.isCancelled()){
									if(multi.containsKey(damager)){
										if(reset.get(damager) != null){
											Bukkit.getScheduler().cancelTask(reset.get(damager));
										}
										if(multi.get(damager)<=Main.CE.getMaxRageLevel())multi.put(damager, multi.get(damager) + (Main.CE.getPower(It, CEnchantments.RAGE)*0.1));
										if(multi.get(damager).intValue() == num.get(damager)){
											damager.sendMessage(Methods.color(Main.settings.getMsg().getString("Messages.Rage.Rage-Up")
													.replaceAll("%Level%", num.get(damager) + "").replaceAll("%level%", num.get(damager) + "")));
											num.put(damager, num.get(damager)+1);
										}
										e.setDamage(e.getDamage() * multi.get(damager));
									}
									if(!multi.containsKey(damager)){
										multi.put(damager, 1.0);
										num.put(damager, 2);
										damager.sendMessage(Methods.color(Main.settings.getMsg().getString("Messages.Rage.Building")));
									}
									reset.put(damager, Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
										@Override
										public void run() {
											multi.remove(damager);
											damager.sendMessage(Methods.color(Main.settings.getMsg().getString("Messages.Rage.Cooled-Down")));
										}
									}, 4*20));
								}
							}
						}
						if(Main.CE.hasEnchantment(It, CEnchantments.SKILLSWIPE)){
							if(CEnchantments.SKILLSWIPE.isEnabled()){
								if(en instanceof Player){
									Player player = (Player) en;
									int amount = 4+Main.CE.getPower(It, CEnchantments.SKILLSWIPE);
									if(player.getTotalExperience()>0){
										EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.SKILLSWIPE, It);
										Bukkit.getPluginManager().callEvent(event);
										if(!event.isCancelled()){
											if(CurrencyAPI.getCurrency(player, Currency.XP_TOTAL) >= amount){
												CurrencyAPI.takeCurrency(player, Currency.XP_TOTAL, amount);
												CurrencyAPI.giveCurrency(damager, Currency.XP_TOTAL, amount);
											}else{
												player.setTotalExperience(0);
												CurrencyAPI.giveCurrency(damager, Currency.XP_TOTAL, amount);
											}
										}
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(It, CEnchantments.LIFESTEAL)){
							if(CEnchantments.LIFESTEAL.isEnabled()){
								int steal = Main.CE.getPower(It, CEnchantments.LIFESTEAL);
								if(Methods.randomPicker(5)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.LIFESTEAL, It);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										if(damager.getHealth() + steal < damager.getMaxHealth()){
											damager.setHealth(damager.getHealth() + steal);
										}
										if(damager.getHealth() + steal >= damager.getMaxHealth()){
											damager.setHealth(damager.getMaxHealth());
										}
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(It, CEnchantments.NUTRITION)){
							if(CEnchantments.NUTRITION.isEnabled()){
								if(Methods.randomPicker(8)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.NUTRITION, It);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										if(Support.hasSpartan()){
											SpartanSupport.cancelFastEat(damager);
										}
										if(damager.getSaturation()+(2*Main.CE.getPower(It, CEnchantments.NUTRITION))<=20){
											damager.setSaturation(damager.getSaturation()+(2*Main.CE.getPower(It, CEnchantments.NUTRITION)));
										}
										if(damager.getSaturation()+(2*Main.CE.getPower(It, CEnchantments.NUTRITION))>=20){
											damager.setSaturation(20);
										}
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(It, CEnchantments.VAMPIRE)){
							if(CEnchantments.VAMPIRE.isEnabled()){
								if(Methods.randomPicker(20-Main.CE.getPower(It, CEnchantments.VAMPIRE))){
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.VAMPIRE, It);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										if(damager.getHealth() + e.getDamage() /2 < damager.getMaxHealth()){
											damager.setHealth(damager.getHealth() + e.getDamage() /2);
										}
										if(damager.getHealth() + e.getDamage() /2 >= damager.getMaxHealth()){
											damager.setHealth(damager.getMaxHealth());
										}
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(It, CEnchantments.BLINDNESS)){
							if(CEnchantments.BLINDNESS.isEnabled()){
								if(Methods.randomPicker(20-Main.CE.getPower(It, CEnchantments.BLINDNESS))){
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.BLINDNESS, It);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										en.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3*20, Main.CE.getPower(It, CEnchantments.BLINDNESS)-1));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(It, CEnchantments.CONFUSION)){
							if(CEnchantments.CONFUSION.isEnabled()){
								if(Methods.randomPicker(7)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.CONFUSION, It);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										en.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 5+(Main.CE.getPower(It, CEnchantments.CONFUSION))*20, 0));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(It, CEnchantments.DOUBLEDAMAGE)){
							if(CEnchantments.DOUBLEDAMAGE.isEnabled()){
								if(Methods.randomPicker((20-Main.CE.getPower(It, CEnchantments.DOUBLEDAMAGE)))){
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.DOUBLEDAMAGE, It);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										e.setDamage((e.getDamage()*2));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(It, CEnchantments.EXECUTE)){
							if(CEnchantments.EXECUTE.isEnabled()){
								if(en.getHealth()<=2){
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.EXECUTE, It);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										damager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 3+(Main.CE.getPower(It, CEnchantments.EXECUTE))*20, 3));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(It, CEnchantments.FASTTURN)){
							if(CEnchantments.FASTTURN.isEnabled()){
								if(Methods.randomPicker((20-Main.CE.getPower(It, CEnchantments.FASTTURN)))){
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.FASTTURN, It);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										e.setDamage(e.getDamage() + (e.getDamage()/3));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(It, CEnchantments.LIGHTWEIGHT)){
							if(CEnchantments.LIGHTWEIGHT.isEnabled()){
								if(Methods.randomPicker(8)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.LIGHTWEIGHT, It);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										damager.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 5*20, Main.CE.getPower(It, CEnchantments.LIGHTWEIGHT)-1));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(It, CEnchantments.OBLITERATE)){
							if(CEnchantments.OBLITERATE.isEnabled()){
								if(Methods.randomPicker(11-Main.CE.getPower(It, CEnchantments.OBLITERATE))){
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.OBLITERATE, It);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										if(Support.hasSpartan()){
											if(e.getEntity() instanceof Player){
												SpartanSupport.cancelSpeed((Player)e.getEntity());
												SpartanSupport.cancelFly((Player)e.getEntity());
												SpartanSupport.cancelClip((Player)e.getEntity());
												SpartanSupport.cancelNormalMovements((Player)e.getEntity());
												SpartanSupport.cancelNoFall((Player)e.getEntity());
												SpartanSupport.cancelJesus((Player)e.getEntity());
											}
										}
										e.getEntity().setVelocity(damager.getLocation().getDirection().multiply(2).setY(1.25));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(It, CEnchantments.PARALYZE)){
							if(CEnchantments.PARALYZE.isEnabled()){
								if(Methods.randomPicker(13-Main.CE.getPower(It, CEnchantments.PARALYZE))){
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.PARALYZE, It);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										Location loc = en.getLocation();
										loc.getWorld().strikeLightningEffect(loc);
										for(LivingEntity En : Methods.getNearbyEntities(loc, 2D, damager)){
											if(Support.allowsPVP(En.getLocation())){
												if(!Support.isFriendly(damager, En)){
													En.damage(5D);
												}
											}
										}
										en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 2));
										en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3*20, 2));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(It, CEnchantments.SLOWMO)){
							if(CEnchantments.SLOWMO.isEnabled()){
								if(Methods.randomPicker(20-Main.CE.getPower(It, CEnchantments.SLOWMO))){
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.SLOWMO, It);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, Main.CE.getPower(It, CEnchantments.SLOWMO)));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(It, CEnchantments.SNARE)){
							if(CEnchantments.SNARE.isEnabled()){
								if(Methods.randomPicker(11-(Main.CE.getPower(It, CEnchantments.SNARE)))){
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.SNARE, It);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 0));
										en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3*20, 0));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(It, CEnchantments.TRAP)){
							if(CEnchantments.TRAP.isEnabled()){
								if(Methods.randomPicker(11-(Main.CE.getPower(It, CEnchantments.TRAP)))){
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.TRAP, It);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 2));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(It, CEnchantments.VIPER)){
							if(CEnchantments.VIPER.isEnabled()){
								if(Methods.randomPicker(10)){
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.VIPER, It);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5*20, Main.CE.getPower(It, CEnchantments.VIPER)));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(It, CEnchantments.WITHER)){
							if(CEnchantments.WITHER.isEnabled()){
								if(Methods.randomPicker(11-(Main.CE.getPower(It, CEnchantments.WITHER)))){
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.WITHER, It);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()){
										en.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 2*20, 2));
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent e){
		if(Support.isFriendly(e.getEntity().getKiller(), e.getEntity()))return;
		if(!Support.allowsPVP(e.getEntity().getLocation()))return;
		if(e.getEntity().getKiller() instanceof Player){
			Player damager = (Player) e.getEntity().getKiller();
			Player player = e.getEntity();
			ItemStack item = Methods.getItemInHand(damager);
			if(Main.CE.hasEnchantments(item)){
				if(Main.CE.hasEnchantment(item, CEnchantments.HEADLESS)){
					if(CEnchantments.HEADLESS.isEnabled()){
						int power = Main.CE.getPower(item, CEnchantments.HEADLESS);
						if(Methods.randomPicker(11-power)){
							EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.HEADLESS, item);
							Bukkit.getPluginManager().callEvent(event);
							if(!event.isCancelled()){
								ItemStack head = Methods.makeItem("397:3", 1);
								SkullMeta m = (SkullMeta) head.getItemMeta();
								m.setOwner(player.getName());
								head.setItemMeta(m);
								e.getDrops().add(head);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent e){
		if(Support.isFriendly(e.getEntity().getKiller(), e.getEntity()))return;
		if(e.getEntity().getKiller() instanceof Player){
			Player damager = (Player) e.getEntity().getKiller();
			ItemStack item = Methods.getItemInHand(damager);
			if(Main.CE.hasEnchantments(item)){
				if(Main.CE.hasEnchantment(item, CEnchantments.INQUISITIVE)){
					if(CEnchantments.INQUISITIVE.isEnabled()){
						if(Methods.randomPicker(3)){
							EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.INQUISITIVE, item);
							Bukkit.getPluginManager().callEvent(event);
							if(!event.isCancelled()){
								e.setDroppedExp(e.getDroppedExp() * (Main.CE.getPower(item, CEnchantments.INQUISITIVE) + 1));
							}
						}
					}
				}
			}
		}
	}
	
}