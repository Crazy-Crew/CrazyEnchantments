package me.BadBones69.CrazyEnchantments.Enchantments;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import me.BadBones69.CrazyEnchantments.Api;

public class Bows implements Listener{
	ArrayList<Projectile> arrow = new ArrayList<Projectile>();
	HashMap<Projectile, Integer> Arrow = new HashMap<Projectile, Integer>();
	HashMap<Projectile, Entity> P = new HashMap<Projectile, Entity>();
	HashMap<Projectile, String> Enchant = new HashMap<Projectile, String>();
	@EventHandler
	public void onBowShoot(final EntityShootBowEvent e){
		if(!Api.allowsPVP(e.getEntity()))return;
		if (e.getBow().hasItemMeta()) {
			if(e.getBow().getItemMeta().hasLore()){
				for(String lore : e.getBow().getItemMeta().getLore()){
					if(lore.contains(Api.getEnchName("Boom"))){
						if(Api.isEnchantmentEnabled("Boom")){
							Arrow.put((Projectile) e.getProjectile(), Api.getPower(lore, Api.getEnchName("Boom")));
							P.put((Projectile) e.getProjectile(), e.getEntity());
							Enchant.put((Projectile) e.getProjectile(), "Boom");
						}
					}
					if(lore.contains(Api.getEnchName("Doctor"))){
						if(Api.isEnchantmentEnabled("Doctor")){
							Arrow.put((Projectile) e.getProjectile(), Api.getPower(lore, Api.getEnchName("Doctor")));
							P.put((Projectile) e.getProjectile(), e.getEntity());
							Enchant.put((Projectile) e.getProjectile(), "Doctor");
						}
					}
					if(lore.contains(Api.getEnchName("IceFreeze"))){
						if(Api.isEnchantmentEnabled("IceFreeze")){
							arrow.add((Projectile) e.getProjectile());
							P.put((Projectile) e.getProjectile(), e.getEntity());
							Enchant.put((Projectile) e.getProjectile(), "IceFreeze");
						}
					}
					if(lore.contains(Api.getEnchName("Lightning"))) {
						if(Api.isEnchantmentEnabled("Lightning")){
							arrow.add((Projectile) e.getProjectile());
							P.put((Projectile) e.getProjectile(), e.getEntity());
							Enchant.put((Projectile) e.getProjectile(), "Lightning");
						}
					}
					if(lore.contains(Api.getEnchName("Piercing"))){
						if(Api.isEnchantmentEnabled("Piercing")){
							Arrow.put((Projectile) e.getProjectile(), Api.getPower(lore, Api.getEnchName("Piercing")));
							P.put((Projectile) e.getProjectile(), e.getEntity());
							Enchant.put((Projectile) e.getProjectile(), "Piercing");
						}
					}
					if(lore.contains(Api.getEnchName("Venom"))){
						if(Api.isEnchantmentEnabled("Venom")){
							Arrow.put((Projectile) e.getProjectile(), Api.getPower(lore, Api.getEnchName("Venom")));
							P.put((Projectile) e.getProjectile(), e.getEntity());
							Enchant.put((Projectile) e.getProjectile(), "Venom");
						}
					}
					if(lore.contains(Api.getEnchName("MultiArrow"))){
						if(Api.isEnchantmentEnabled("MultiArrow")){
							int power = Api.getPower(lore, Api.getEnchName("MultiArrow"));
							if(Api.randomPicker(2)){
								for(int i=1;i<=power;i++){
									Arrow arrow = e.getEntity().getWorld().spawn(e.getProjectile().getLocation(), Arrow.class);
									arrow.setShooter(e.getEntity());
									arrow.setBounce(false);
									Vector v = new Vector(Vec(), 0, Vec());
									arrow.setVelocity(e.getProjectile().getVelocity().add(v));
									if(((Arrow)e.getProjectile()).isCritical())arrow.setCritical(true);
								}
							}
						}
					}
				}
			}
		}
	}
	float Vec(){
		float spread = (float) .2;
		float Vec = -spread + (float) (Math.random() * ((spread - -spread)));
		return Vec;
	}
	@EventHandler
	public void onland(ProjectileHitEvent e) {
		if(!Api.allowsPVP(e.getEntity()))return;
		if(Arrow.containsKey(e.getEntity())){
			if(Enchant.get(e.getEntity()).equalsIgnoreCase("Boom")){
				if(Api.isEnchantmentEnabled("Boom")){
					if(Api.randomPicker(20-Api.getPower(Arrow.get(e.getEntity())+"", Api.getEnchName("Boom")))){
						e.getEntity().getWorld().spawn(e.getEntity().getLocation(), TNTPrimed.class);
						Arrow.remove(e.getEntity());
						e.getEntity().remove();
					}
				}
			}
			if(Enchant.get(e.getEntity()).equalsIgnoreCase("Lightning")){
				if(Api.isEnchantmentEnabled("Lightning")){
					Location loc = e.getEntity().getLocation();
					if(Api.randomPicker(5)){
						loc.getWorld().strikeLightning(loc);
					}
				}
			}
		}
	}
	@EventHandler
 	public void onArrowLand(EntityDamageByEntityEvent e){
		if(!Api.allowsPVP(e.getEntity()))return;
		if(!Api.allowsPVP(e.getDamager()))return;
		if(e.getDamager() instanceof Arrow){
			if(e.getEntity() instanceof LivingEntity){
				LivingEntity en = (LivingEntity) e.getEntity();
				Projectile arrow = (Projectile) e.getDamager();
				if(Arrow.containsKey(arrow)){
					if(Api.isFriendly(P.get(e.getDamager()), e.getEntity())){
						if(Enchant.get(e.getEntity()).equalsIgnoreCase("Doctor")){
							if(Api.isEnchantmentEnabled("Doctor")){
								int heal = 2+Api.getPower(Arrow.get(arrow)+"", Api.getEnchName("Doctor"));
								if(en.getHealth() + heal < en.getMaxHealth()){
									en.setHealth(en.getHealth() + heal);
								}
								if(en.getHealth() + heal >= en.getMaxHealth()){
									en.setHealth(en.getMaxHealth());
								}
							}
						}
					}
					if(!Api.isFriendly(P.get(e.getDamager()), e.getEntity())){
						if(Enchant.get(e.getEntity()).equalsIgnoreCase("IceFreeze")){
							if(Api.isEnchantmentEnabled("IceFreeze")){
								if(Api.randomPicker(5)){
									en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5*20, 1));
								}
							}
						}
						if(Enchant.get(e.getEntity()).equalsIgnoreCase("Piercing")){
							if(Api.isEnchantmentEnabled("Piercing")){
								if(Api.randomPicker(20-Api.getPower(Arrow.get(arrow)+"", Api.getEnchName("Piercing")))){
									e.setDamage(e.getDamage() *2);
								}
							}
						}
						if(Enchant.get(e.getEntity()).equalsIgnoreCase("Venom")){
							if(Api.isEnchantmentEnabled("Venom")){
								if(Api.randomPicker(10)){
									en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 2*20, Api.getPower(Arrow.get(arrow)+"", Api.getEnchName("Venom"))-1));
								}
							}
						}
					}
				}
			}
		}
		return;
	}
}