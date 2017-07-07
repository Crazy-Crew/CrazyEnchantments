package me.badbones69.crazyenchantments.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.events.ArmorEquipEvent;
import me.badbones69.crazyenchantments.multisupport.Support;

public class CustomEnchantments implements Listener{
	
	private HashMap<Projectile, Integer> Power = new HashMap<Projectile, Integer>();
	private HashMap<Projectile, CEnchantment> Enchant = new HashMap<Projectile, CEnchantment>();
	
	@EventHandler
 	public void onEquip(ArmorEquipEvent e){
		if(Main.settings.getCustomEnchs().contains("Enchantments")){
			Player player = e.getPlayer();
			ItemStack NewItem = e.getNewArmorPiece();
			ItemStack OldItem = e.getOldArmorPiece();
			if(Main.CE.hasEnchantments(NewItem)){
				for(CEnchantment ench : getEnchantments()){
					if(Main.CE.hasEnchantment(NewItem, ench)){
						if(ench.isActivated()){
							int power = Main.CE.getPower(NewItem, ench);
							int add = Main.settings.getCustomEnchs().getInt("Enchantments."+ench.getName()+".EnchantOptions.ArmorOptions.PowerIncrease");
							for(String po : Main.settings.getCustomEnchs().getStringList("Enchantments."+ench.getName()+".EnchantOptions.ArmorOptions.PotionEffects")){
								PotionEffectType potion = PotionEffectType.NIGHT_VISION;
								int amp = 0;
								int time = 55555;
								String[] b = po.split(", ");
								for(String B : b){
									for(String P : Methods.getPotions()){
										if(B.toLowerCase().startsWith(P.toLowerCase()+":")){
											potion = PotionEffectType.getByName(P);
											amp = Integer.parseInt(B.replaceAll(P+":", ""));
										}
									}
									if(B.toUpperCase().contains("Time:".toUpperCase())){
										time = Integer.parseInt(B.replaceAll("Time:", ""));
									}
								}
								if(power==1){
									player.addPotionEffect(new PotionEffect(potion, time*20, amp-1));
								}else{
									player.addPotionEffect(new PotionEffect(potion, time*20, amp+((power-1)*add)-1));
								}
							}
						}
					}
				}
			}
			if(Main.CE.hasEnchantments(OldItem)){
				for(CEnchantment ench : getEnchantments()){
					if(Main.CE.hasEnchantment(OldItem, ench)){
						if(ench.isActivated()){
							for(String po : Main.settings.getCustomEnchs().getStringList("Enchantments."+ench.getName()+".EnchantOptions.ArmorOptions.PotionEffects")){
								PotionEffectType potion = PotionEffectType.NIGHT_VISION;
								String[] b = po.split(", ");
								for(String B : b){
									for(String P : Methods.getPotions()){
										if(B.toLowerCase().startsWith(P.toLowerCase()+":")){
											potion = PotionEffectType.getByName(P);
										}
									}
								}
								if(player.hasPotionEffect(potion)){
									player.removePotionEffect(potion);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e){
		if(Main.settings.getCustomEnchs().contains("Enchantments")){
		if(e.isCancelled())return;
			if(e.getEntity() instanceof LivingEntity){
				if(e.getDamager() instanceof Player){
					Player damager = (Player) e.getDamager();
					LivingEntity damaged = (LivingEntity) e.getEntity();
					ItemStack item = Methods.getItemInHand(damager);
					if(!e.getEntity().isDead()){
						if(!Support.allowsPVP(e.getEntity().getLocation()))return;
						if(Main.CE.hasEnchantments(item)){
							for(CEnchantment ench : getEnchantments()){
								if(Main.CE.hasEnchantment(item, ench)){
									if(ench.isActivated()){
										//Damager Potion Control
										if(Main.settings.getCustomEnchs().contains("Enchantments."+ench.getName()+".EnchantOptions.WeaponOptions.Damager.PowerIncrease")){
											if(Main.settings.getCustomEnchs().contains("Enchantments."+ench.getName()+".EnchantOptions.WeaponOptions.Damager.PotionEffects")){
												int power = Main.CE.getPower(Methods.getItemInHand(damager), ench);
												int add = Main.settings.getCustomEnchs().getInt("Enchantments."+ench.getName()+".EnchantOptions.WeaponOptions.Damager.PowerIncrease");
												for(String po : Main.settings.getCustomEnchs().getStringList("Enchantments."+ench.getName()+".EnchantOptions.WeaponOptions.Damager.PotionEffects")){
													PotionEffectType potion = PotionEffectType.NIGHT_VISION;
													int amp = 0;
													int time = 55555;
													int cha = 100;
													String[] b = po.split(", ");
													for(String B : b){
														for(String P : Methods.getPotions()){
															if(B.toLowerCase().startsWith(P.toLowerCase())){
																potion = PotionEffectType.getByName(P);
																amp = Integer.parseInt(B.replaceAll(P.toString()+":", ""));
															}
														}
														if(B.toUpperCase().contains("Time:".toUpperCase())){
															time = Integer.parseInt(B.replaceAll("Time:", ""));
														}
														if(B.toUpperCase().contains("Chance:".toUpperCase())){
															cha = Integer.parseInt(B.replaceAll("Chance:", ""));
														}
													}
													Random number = new Random();
													int chance;
													for(int counter = 1; counter<=1; counter++){
														chance = 1 + number.nextInt(99);
														if(chance <= cha){
															damager.addPotionEffect(new PotionEffect(potion, time*20, amp+(power*add)));
														}
													}
												}
											}
										}
										//Damaged Potion Control
										if(Main.settings.getCustomEnchs().contains("Enchantments."+ench.getName()+".EnchantOptions.WeaponOptions.Damaged.PowerIncrease")){
											if(Main.settings.getCustomEnchs().contains("Enchantments."+ench.getName()+".EnchantOptions.WeaponOptions.Damaged.PotionEffects")){
												int power = Main.CE.getPower(Methods.getItemInHand(damager), ench);
												int add = Main.settings.getCustomEnchs().getInt("Enchantments."+ench.getName()+".EnchantOptions.WeaponOptions.Damaged.PowerIncrease");
												for(String po : Main.settings.getCustomEnchs().getStringList("Enchantments."+ench.getName()+".EnchantOptions.WeaponOptions.Damaged.PotionEffects")){
													PotionEffectType potion = PotionEffectType.NIGHT_VISION;
													int amp = 0;
													int time = 55555;
													int cha = 100;
													String[] b = po.split(", ");
													for(String B : b){
														for(String P : Methods.getPotions()){
															if(B.toLowerCase().startsWith(P.toLowerCase())){
																potion = PotionEffectType.getByName(P);
																amp = Integer.parseInt(B.replaceAll(P.toString()+":", ""));
															}
														}
														if(B.toUpperCase().contains("Time:".toUpperCase())){
															time = Integer.parseInt(B.replaceAll("Time:", ""));
														}
														if(B.toUpperCase().contains("Chance:".toUpperCase())){
															cha = Integer.parseInt(B.replaceAll("Chance:", ""));
														}
													}
													Random number = new Random();
													int chance;
													for(int counter = 1; counter<=1; counter++){
														chance = 1 + number.nextInt(99);
														if(chance <= cha){
															damaged.addPotionEffect(new PotionEffect(potion, time*20, amp+(power*add)));
														}
													}
												}
											}
										}
										//Damaged Damage Multiplyer Control.
										if(Main.settings.getCustomEnchs().contains("Enchantments."+ench.getName()+".EnchantOptions.WeaponOptions.Damaged.DamageMultiplyer")){
											Random number = new Random();
											int chance;
											int cha = Main.settings.getCustomEnchs().getInt("Enchantments."+ench.getName()+".EnchantOptions.WeaponOptions.Damaged.DamageMultiplyer.Chance");
											int power = Main.settings.getCustomEnchs().getInt("Enchantments."+ench.getName()+".EnchantOptions.WeaponOptions.Damaged.DamageMultiplyer.PowerIncrease");
											int multi = Main.settings.getCustomEnchs().getInt("Enchantments."+ench.getName()+".EnchantOptions.WeaponOptions.Damaged.DamageMultiplyer.Multiplyer");
											double damage = e.getDamage()*(multi+(Main.CE.getPower(Methods.getItemInHand(damager), ench)+power));
											for(int counter = 1; counter<=1; counter++){
												chance = 1 + number.nextInt(99);
												if(chance <= cha){
													e.setDamage(damage);
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
	public void onBowShoot(EntityShootBowEvent e){
		if(!Support.allowsPVP(e.getEntity().getLocation()))return;
		ItemStack item = e.getBow();
		if(Main.CE.hasEnchantments(item)){
			for(CEnchantment ench : getEnchantments()){
				if(Main.CE.hasEnchantment(item, ench)){
					if(ench.isActivated()){
						Power.put((Projectile) e.getProjectile(), Main.CE.getPower(e.getBow(), ench));
						Enchant.put((Projectile) e.getProjectile(), ench);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onland(ProjectileHitEvent e) {
		if(!Main.settings.getCustomEnchs().contains("Enchantments"))return;
		if(!Support.allowsPVP(e.getEntity().getLocation()))return;
		if(!Support.allowsExplotions(e.getEntity().getLocation()))return;
		if(Power.containsKey(e.getEntity())){
			CEnchantment ench = Enchant.get(e.getEntity());
			if(Main.settings.getCustomEnchs().contains("Enchantments."+ench.getName()+".EnchantOptions.BowOptions.OnHit.Ground.Explode")){
				int cha = Main.settings.getCustomEnchs().getInt("Enchantments."+ench.getName()+".EnchantOptions.BowOptions.OnHit.Ground.Explode.Chance");
				int power = Main.settings.getCustomEnchs().getInt("Enchantments."+ench.getName()+".EnchantOptions.BowOptions.OnHit.Ground.Explode.Power");
				int incr = Main.settings.getCustomEnchs().getInt("Enchantments."+ench.getName()+".EnchantOptions.BowOptions.OnHit.Ground.Explode.PowerIncrease");
				Random number = new Random();
				int chance;
				for(int counter = 1; counter<=1; counter++){
					chance = 1 + number.nextInt(99);
					if(chance<=cha){
						if(Power.get(e.getEntity())==1){
							e.getEntity().getWorld().createExplosion(e.getEntity().getLocation(), power);
						}else{
							e.getEntity().getWorld().createExplosion(e.getEntity().getLocation(), power+((Power.get(e.getEntity())-1)*incr));
						}
						e.getEntity().remove();
						Power.remove(e.getEntity());
						Enchant.remove(e.getEntity());
					}
				}
			}
		}
	}
	
	@EventHandler
 	public void onHit(EntityDamageByEntityEvent e){
		if(!Main.settings.getCustomEnchs().contains("Enchantments"))return;
		if(!Support.allowsPVP(e.getDamager().getLocation()))return;
		if(e.getDamager() instanceof Arrow){
			if(!(e.getEntity() instanceof LivingEntity))return;
			Projectile arrow = (Projectile) e.getDamager();
			CEnchantment ench = Enchant.get(arrow);
			if(Power.containsKey(arrow)){
				if(Main.settings.getCustomEnchs().contains("Enchantments."+ench.getName()+".EnchantOptions.BowOptions.OnHit.Damaged")){
					int power = Power.get(arrow);
					int add = Main.settings.getCustomEnchs().getInt("Enchantments."+ench.getName()+".EnchantOptions.BowOptions.OnHit.Damaged.PowerIncrease");
					for(String po : Main.settings.getCustomEnchs().getStringList("Enchantments."+ench.getName()+".EnchantOptions.BowOptions.OnHit.Damaged.PotionEffects")){
						PotionEffectType potion = PotionEffectType.NIGHT_VISION;
						int amp = 0;
						int time = 55555;
						int cha = 100;
						String[] b = po.split(", ");
						for(String B : b){
							for(String P : Methods.getPotions()){
								if(B.contains(P)){
									potion = PotionEffectType.getByName(P);
									amp = Integer.parseInt(B.replaceAll(P.toString()+":", ""));
								}
							}
							if(B.toUpperCase().contains("Time:".toUpperCase())){
								time = Integer.parseInt(B.replaceAll("Time:", ""));
							}
							if(B.toUpperCase().contains("Chance:".toUpperCase())){
								cha = Integer.parseInt(B.replaceAll("Chance:", ""));
							}
						}
						Random number = new Random();
						int chance;
						for(int counter = 1; counter<=1; counter++){
							chance = 1 + number.nextInt(99);
							if(chance <= cha){
								if(power==1){
									((LivingEntity)e.getEntity()).addPotionEffect(new PotionEffect(potion, time*20, amp-1));
								}else{
									((LivingEntity)e.getEntity()).addPotionEffect(new PotionEffect(potion, time*20, amp+((power-1)*add)-1));
								}
							}
						}
						Power.remove(arrow);
						Enchant.remove(arrow);
					}
				}
			}
		}
	}
	
	private ArrayList<CEnchantment> getEnchantments(){
		ArrayList<CEnchantment> enchantments = new ArrayList<CEnchantment>();
		for(String enchant : Main.settings.getCustomEnchs().getConfigurationSection("Enchantments").getKeys(false)){
			if(Main.CE.getEnchantmentFromName(enchant) != null){
				enchantments.add(Main.CE.getEnchantmentFromName(enchant));
			}
		}
		return enchantments;
	}
	
}