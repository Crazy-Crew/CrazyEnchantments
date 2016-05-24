package me.BadBones69.CrazyEnchantments.Enchantments;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.BadBones69.CrazyEnchantments.Api;

public class Swords implements Listener{
	static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments");
	@SuppressWarnings("static-access")
	public Swords(Plugin plugin){
		this.plugin = plugin;
	}
	HashMap<Player, Double> multi = new HashMap<Player, Double>();
	HashMap<Player, Integer> num = new HashMap<Player, Integer>();
	HashMap<Player, Integer> reset = new HashMap<Player, Integer>();
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(Api.isFriendly(e.getDamager(), e.getEntity()))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if(!Api.allowsPVP(e.getDamager()))return;
		if(e.getEntity() instanceof LivingEntity){
			if(e.getDamager() instanceof Player){
				final Player damager = (Player) e.getDamager();
				LivingEntity en = (LivingEntity) e.getEntity();
				if(Api.getItemInHand(damager).hasItemMeta()){
					if(Api.getItemInHand(damager).getItemMeta().hasLore()){
						if(!e.getEntity().isDead()){
							for(String lore : Api.getItemInHand(damager).getItemMeta().getLore()){
								if(lore.contains(Api.getEnchName("Disarmer"))){
									if(Api.isEnchantmentEnabled("Disarmer")){
										if(e.getEntity() instanceof Player){
											Player player = (Player) e.getEntity();
											int slot = Api.percentPick(4, 1);
											if(Api.randomPicker((13-Api.getPower(lore, Api.getEnchName("Disarmer"))))){
												if(slot == 1){
													if(player.getEquipment().getHelmet() != null){
														ItemStack item = player.getEquipment().getHelmet();
														player.getEquipment().setHelmet(null);
														player.getInventory().addItem(item);
													}
												}
												if(slot == 2){
													if(player.getEquipment().getChestplate() != null){
														ItemStack item = player.getEquipment().getChestplate();
														player.getEquipment().setChestplate(null);
														player.getInventory().addItem(item);
													}
												}
												if(slot == 3){
													if(player.getEquipment().getLeggings() != null){
														ItemStack item = player.getEquipment().getLeggings();
														player.getEquipment().setLeggings(null);
														player.getInventory().addItem(item);
													}
												}
												if(slot == 4){
													if(player.getEquipment().getBoots() != null){
														ItemStack item = player.getEquipment().getBoots();
														player.getEquipment().setBoots(null);
														player.getInventory().addItem(item);
													}
												}
											}
										}
									}
								}
								if(lore.contains(Api.getEnchName("Rage"))){
									if(Api.isEnchantmentEnabled("Rage")){
										int Cap = 4;
										if(multi.containsKey(damager)){
											Bukkit.getScheduler().cancelTask(reset.get(damager));
											if(multi.get(damager)<=Cap)multi.put(damager, multi.get(damager) + (Api.getPower(lore, Api.getEnchName("Rage"))*0.1));
											if(multi.get(damager).intValue() == num.get(damager)){
												damager.sendMessage(Api.color("&3You are now doing &a" + num.get(damager) + "x &3Damage."));
												num.put(damager, num.get(damager)+1);
											}
											e.setDamage(e.getDamage() * multi.get(damager));
										}
										if(!multi.containsKey(damager)){
											multi.put(damager, 1.0);
											num.put(damager, 2);
											damager.sendMessage(Api.color("&aYour Rage is Building."));
										}
										reset.put(damager, Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
											@Override
											public void run() {
												multi.remove(damager);
												damager.sendMessage(Api.color("&cYour Rage has Cooled Down."));
											}
										}, 4*20));
									}
								}
								if(lore.contains(Api.getEnchName("SkillSwipe"))){
									if(Api.isEnchantmentEnabled("SkillSwipe")){
										if(en instanceof Player){
											Player player = (Player) en;
											int amount = 4+Api.getPower(lore, Api.getEnchName("SkillSwipe"));
											if(player.getTotalExperience()>=amount){
												Api.takeTotalXP(player, amount);
												Api.takeTotalXP(damager, -amount);
												return;
											}
											if(player.getTotalExperience()<amount){
												player.setTotalExperience(0);
												Api.takeTotalXP(damager, -amount);
												return;
											}
										}
									}
								}
								if(lore.contains(Api.getEnchName("LifeSteal"))){
									if(Api.isEnchantmentEnabled("LifeSteal")){
										int steal = Api.getPower(lore, Api.getEnchName("LifeSteal"));
										if(Api.randomPicker(5)){
											if(damager.getHealth() + steal < damager.getMaxHealth()){
												damager.setHealth(damager.getHealth() + steal);
											}
											if(damager.getHealth() + steal >= damager.getMaxHealth()){
												damager.setHealth(damager.getMaxHealth());
											}
										}
									}
								}
								if(lore.contains(Api.getEnchName("Nutrition"))){
									if(Api.isEnchantmentEnabled("Nutrition")){
										if(Api.randomPicker(8)){
											if(damager.getSaturation()+(2*Api.getPower(lore, Api.getEnchName("Nutrition")))<=20){
												damager.setSaturation(damager.getSaturation()+(2*Api.getPower(lore, Api.getEnchName("Nutrition"))));
												return;
											}
											if(damager.getSaturation()+(2*Api.getPower(lore, Api.getEnchName("Nutrition")))>=20){
												damager.setSaturation(20);
												return;
											}
										}
									}
								}
								if(lore.contains(Api.getEnchName("Vampire"))){
									if(Api.isEnchantmentEnabled("Vampire")){
										if(Api.randomPicker(20-Api.getPower(lore, Api.getEnchName("Vampire")))){
											if(damager.getHealth() + e.getDamage() /2 < damager.getMaxHealth()){
												damager.setHealth(damager.getHealth() + e.getDamage() /2);
											}
											if(damager.getHealth() + e.getDamage() /2 >= damager.getMaxHealth()){
												damager.setHealth(damager.getMaxHealth());
											}
										}
									}
								}
								if(lore.contains(Api.getEnchName("Blindness"))){
									if(Api.isEnchantmentEnabled("Blindness")){
										if(Api.randomPicker(20-Api.getPower(lore, Api.getEnchName("Blindness")))){
											en.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3*20, Api.getPower(lore, Api.getEnchName("Blindness"))-1));
										}
									}
								}
								if(lore.contains(Api.getEnchName("Confusion"))){
									if(Api.isEnchantmentEnabled("Confusion")){
										if(Api.randomPicker(7)){
											en.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 5+(Api.getPower(lore, Api.getEnchName("Confusion")))*20, 0));
										}
									}
								}
								if(lore.contains(Api.getEnchName("DoubleDamage"))){
									if(Api.isEnchantmentEnabled("DoubleDamage")){
										if(Api.randomPicker((20-Api.getPower(lore, Api.getEnchName("DoubleDamage"))))){
											e.setDamage((e.getDamage()*2));
										}
									}
								}
								if(lore.contains(Api.getEnchName("Execute"))){
									if(Api.isEnchantmentEnabled("Execute")){
										if(en.getHealth()<=2){
											damager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 3+(Api.getPower(lore, Api.getEnchName("Execute")))*20, 3));
										}
									}
								}
								if(lore.contains(Api.getEnchName("FastTurn"))){
									if(Api.isEnchantmentEnabled("FastTurn")){
										if(Api.randomPicker((20-Api.getPower(lore, Api.getEnchName("FastTurn"))))){
											e.setDamage(e.getDamage() + (e.getDamage()/3));
										}
									}
								}
								if(lore.contains(Api.getEnchName("LightWeight"))){
									if(Api.isEnchantmentEnabled("LightWeight")){
										if(Api.randomPicker(8)){
											damager.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 5*20, Api.getPower(lore, Api.getEnchName("LightWeight"))-1));
										}
									}
								}
								if(lore.contains(Api.getEnchName("Obliterate"))){
									if(Api.isEnchantmentEnabled("Obliterate")){
										if(Api.randomPicker(11-Api.getPower(lore, Api.getEnchName("Obliterate")))){
											e.getEntity().setVelocity(damager.getLocation().getDirection().multiply(2).setY(1.25));
										}
									}
								}
								if(lore.contains(Api.getEnchName("Paralyze"))){
									if(Api.isEnchantmentEnabled("Paralyze")){
										if(Api.randomPicker(13-Api.getPower(lore, Api.getEnchName("Paralyze")))){
											en.getWorld().strikeLightning(en.getLocation());
											en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 2));
											en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3*20, 2));
										}
									}
								}
								if(lore.contains(Api.getEnchName("SlowMo"))){
									if(Api.isEnchantmentEnabled("SlowMo")){
										if(Api.randomPicker(20-Api.getPower(lore, Api.getEnchName("SlowMo")))){
											en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, Api.getPower(lore, Api.getEnchName("SlowMo"))));
										}
									}
								}
								if(lore.contains(Api.getEnchName("Snare"))){
									if(Api.isEnchantmentEnabled("Snare")){
										if(Api.randomPicker(11-(Api.getPower(lore, Api.getEnchName("Snare"))))){
											en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 0));
											en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3*20, 0));
										}
									}
								}
								if(lore.contains(Api.getEnchName("Trap"))){
									if(Api.isEnchantmentEnabled("Trap")){
										if(Api.randomPicker(11-(Api.getPower(lore, Api.getEnchName("Trap"))))){
											en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 2));
										}
									}
								}
								if(lore.contains(Api.getEnchName("Viper"))){
									if(Api.isEnchantmentEnabled("Viper")){
										if(Api.randomPicker(10)){
											en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5*20, Api.getPower(lore, Api.getEnchName("Viper"))));
										}
									}
								}
								if(lore.contains(Api.getEnchName("Wither"))){
									if(Api.isEnchantmentEnabled("Wither")){
										if(Api.randomPicker(11-(Api.getPower(lore, Api.getEnchName("Wither"))))){
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
	}
	@EventHandler
	public void onPlayerDamage(PlayerDeathEvent e){
		if(Api.isFriendly(e.getEntity().getKiller(), e.getEntity()))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if(e.getEntity().getKiller() instanceof Player){
			Player damager = (Player) e.getEntity().getKiller();
			Player player = e.getEntity();
			if(Api.getItemInHand(damager)!=null){
				ItemStack item = Api.getItemInHand(damager);
				if(item.hasItemMeta()){
					if(item.getItemMeta().hasLore()){
						for(String lore : item.getItemMeta().getLore()){
							if(lore.contains(Api.getEnchName("Headless"))){
								if(Api.isEnchantmentEnabled("Headless")){
									int power = Api.getPower(lore, Api.getEnchName("Headless"));
									if(Api.randomPicker(11-power)){
										ItemStack head = Api.makeItem("397:3", 1);
										SkullMeta m = (SkullMeta) head.getItemMeta();
										m.setOwner(player.getName());
										head.setItemMeta(m);
										e.getDrops().add(head);
									}
								}
							}
							if(lore.contains(Api.getEnchName("Inquisitive"))){
								if(Api.isEnchantmentEnabled("Inquisitive")){
									int chance=9-Api.getPower(lore, Api.getEnchName("Inquisitive"));
									if(Api.randomPicker(chance)){
										e.setDroppedExp(e.getDroppedExp()*2);
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