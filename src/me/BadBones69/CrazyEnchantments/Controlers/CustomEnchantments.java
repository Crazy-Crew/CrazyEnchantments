package me.BadBones69.CrazyEnchantments.Controlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Material;
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

import ca.thederpygolems.armorequip.ArmorEquipEvent;
import me.BadBones69.CrazyEnchantments.Api;
import me.BadBones69.CrazyEnchantments.Main;

public class CustomEnchantments implements Listener{
	public static ArrayList<String> getEnchantments(){
		ArrayList<String> enchs = new ArrayList<String>();
		if(!Main.settings.getCustomEnchs().contains("Enchantments"))return enchs;
		for(String ench : Main.settings.getCustomEnchs().getConfigurationSection("Enchantments").getKeys(false)){
			enchs.add(ench);
		}
		return enchs;
	}
	@EventHandler
 	public void onEquip(ArmorEquipEvent e){
		if(!Main.settings.getCustomEnchs().contains("Enchantments"))return;
		Player player = e.getPlayer();
		ItemStack NewItem = e.getNewArmorPiece();
		ItemStack OldItem = e.getOldArmorPiece();
		if(e.getNewArmorPiece() != null && e.getNewArmorPiece().hasItemMeta() && e.getNewArmorPiece().getType() != Material.AIR){
			if(!NewItem.getItemMeta().hasLore())return;
			for(String lore : NewItem.getItemMeta().getLore()){
				for(String ench : getEnchantments()){
					if(lore.contains(Api.getEnchName(ench))){
						int power = Api.getPower(lore, ench);
						int add = Main.settings.getCustomEnchs().getInt("Enchantments."+ench+".EnchantOptions.ArmorOptions.PowerIncrease");
						for(String po : Main.settings.getCustomEnchs().getStringList("Enchantments."+ench+".EnchantOptions.ArmorOptions.PotionEffects")){
							PotionEffectType potion = PotionEffectType.NIGHT_VISION;
							int amp = 0;
							int time = 55555;
							String[] b = po.split(", ");
							for(String B : b){
								for(String P : Api.getPotions()){
									if(B.contains(P+":")){
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
		if(e.getOldArmorPiece() != null && e.getOldArmorPiece().hasItemMeta() && e.getOldArmorPiece().getType() != Material.AIR){
			if(!OldItem.getItemMeta().hasLore())return;
			for(String lore : OldItem.getItemMeta().getLore()){
				for(String ench : getEnchantments()){
					if(lore.contains(Api.getEnchName(ench))){
						for(String po : Main.settings.getCustomEnchs().getStringList("Enchantments."+ench+".EnchantOptions.ArmorOptions.PotionEffects")){
							PotionEffectType potion = PotionEffectType.NIGHT_VISION;
							String[] b = po.split(", ");
							for(String B : b){
								for(String P : Api.getPotions()){
									if(B.contains(P+":")){
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
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e){
		if(!Main.settings.getCustomEnchs().contains("Enchantments"))return;
		if(e.isCancelled())return;
		if(e.getEntity() instanceof LivingEntity){
			if(e.getDamager() instanceof Player){
				Player damager = (Player) e.getDamager();
				LivingEntity damaged = (LivingEntity) e.getEntity();
				if(Api.getItemInHand(damager).hasItemMeta()){
					if(!Api.getItemInHand(damager).getItemMeta().hasLore())return;
					if(!e.getEntity().isDead()){
						if(!Api.allowsPVP(e.getEntity()))return;
						for(String lore : Api.getItemInHand(damager).getItemMeta().getLore()){
							for(String ench : getEnchantments()){
								if(lore.contains(Api.getEnchName(ench))){
									//Damager Potion Control
									if(Main.settings.getCustomEnchs().contains("Enchantments."+ench+".EnchantOptions.WeaponOptions.Damager.PowerIncrease")){
										if(Main.settings.getCustomEnchs().contains("Enchantments."+ench+".EnchantOptions.WeaponOptions.Damager.PotionEffects")){
											int power = Api.getPower(lore, ench);
											int add = Main.settings.getCustomEnchs().getInt("Enchantments."+ench+".EnchantOptions.WeaponOptions.Damager.PowerIncrease");
											for(String po : Main.settings.getCustomEnchs().getStringList("Enchantments."+ench+".EnchantOptions.WeaponOptions.Damager.PotionEffects")){
												PotionEffectType potion = PotionEffectType.NIGHT_VISION;
												int amp = 0;
												int time = 55555;
												int cha = 100;
												String[] b = po.split(", ");
												for(String B : b){
													for(String P : Api.getPotions()){
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
														damager.addPotionEffect(new PotionEffect(potion, time*20, amp+(power*add)));
													}
												}
											}
										}
									}
									//Damaged Potion Control
									if(Main.settings.getCustomEnchs().contains("Enchantments."+ench+".EnchantOptions.WeaponOptions.Damaged.PowerIncrease")){
										if(Main.settings.getCustomEnchs().contains("Enchantments."+ench+".EnchantOptions.WeaponOptions.Damaged.PotionEffects")){
											int power = Api.getPower(lore, ench);
											int add = Main.settings.getCustomEnchs().getInt("Enchantments."+ench+".EnchantOptions.WeaponOptions.Damaged.PowerIncrease");
											for(String po : Main.settings.getCustomEnchs().getStringList("Enchantments."+ench+".EnchantOptions.WeaponOptions.Damaged.PotionEffects")){
												PotionEffectType potion = PotionEffectType.NIGHT_VISION;
												int amp = 0;
												int time = 55555;
												int cha = 100;
												String[] b = po.split(", ");
												for(String B : b){
													for(String P : Api.getPotions()){
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
														damaged.addPotionEffect(new PotionEffect(potion, time*20, amp+(power*add)));
													}
												}
											}
										}
									}
									//Damaged Damage Multiplyer Control.
									if(Main.settings.getCustomEnchs().contains("Enchantments."+ench+".EnchantOptions.WeaponOptions.Damaged.DamageMultiplyer")){
										Random number = new Random();
										int chance;
										int cha = Main.settings.getCustomEnchs().getInt("Enchantments."+ench+".EnchantOptions.WeaponOptions.Damaged.DamageMultiplyer.Chance");
										int power = Main.settings.getCustomEnchs().getInt("Enchantments."+ench+".EnchantOptions.WeaponOptions.Damaged.DamageMultiplyer.PowerIncrease");
										int multi = Main.settings.getCustomEnchs().getInt("Enchantments."+ench+".EnchantOptions.WeaponOptions.Damaged.DamageMultiplyer.Multiplyer");
										double damage = e.getDamage()*(multi+(Api.getPower(lore, Api.getEnchName(ench))+power));
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
	HashMap<Projectile, Integer> Power = new HashMap<Projectile, Integer>();
	HashMap<Projectile, String> Enchant = new HashMap<Projectile, String>();
	@EventHandler
	public void onBowShoot(EntityShootBowEvent e){
		if(!Api.allowsPVP(e.getEntity()))return;
		if (e.getBow().hasItemMeta()) {
			if(!e.getBow().getItemMeta().hasLore())return;
			for(String lore : e.getBow().getItemMeta().getLore()){
				for(String ench : getEnchantments()){
					if(lore.contains(Api.getEnchName(ench))){
						Power.put((Projectile) e.getProjectile(), Api.getPower(lore, Api.getEnchName(ench)));
						Enchant.put((Projectile) e.getProjectile(), ench);
					}
				}
			}
		}
	}
	@EventHandler
	public void onland(ProjectileHitEvent e) {
		if(!Main.settings.getCustomEnchs().contains("Enchantments"))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if(!Api.allowsExplotions(e.getEntity()))return;
		if(Power.containsKey(e.getEntity())){
			String ench = Enchant.get(e.getEntity());
			if(Main.settings.getCustomEnchs().contains("Enchantments."+ench+".EnchantOptions.BowOptions.OnHit.Ground.Explode")){
				int cha = Main.settings.getCustomEnchs().getInt("Enchantments."+ench+".EnchantOptions.BowOptions.OnHit.Ground.Explode.Chance");
				int power = Main.settings.getCustomEnchs().getInt("Enchantments."+ench+".EnchantOptions.BowOptions.OnHit.Ground.Explode.Power");
				int incr = Main.settings.getCustomEnchs().getInt("Enchantments."+ench+".EnchantOptions.BowOptions.OnHit.Ground.Explode.PowerIncrease");
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
		if(!Api.allowsPVP(e.getDamager()))return;
		if(e.getDamager() instanceof Arrow){
			if(!(e.getEntity() instanceof LivingEntity))return;
			Projectile arrow = (Projectile) e.getDamager();
			String ench = Enchant.get(arrow);
			if(Power.containsKey(arrow)){
				if(Main.settings.getCustomEnchs().contains("Enchantments."+ench+".EnchantOptions.BowOptions.OnHit.Damaged")){
					int power = Api.getPower(Enchant.get(arrow), ench);
					int add = Main.settings.getCustomEnchs().getInt("Enchantments."+ench+".EnchantOptions.BowOptions.OnHit.Damaged.PowerIncrease");
					for(String po : Main.settings.getCustomEnchs().getStringList("Enchantments."+ench+".EnchantOptions.BowOptions.OnHit.Damaged.PotionEffects")){
						PotionEffectType potion = PotionEffectType.NIGHT_VISION;
						int amp = 0;
						int time = 55555;
						int cha = 100;
						String[] b = po.split(", ");
						for(String B : b){
							for(String P : Api.getPotions()){
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
}