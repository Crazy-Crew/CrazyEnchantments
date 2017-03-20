package me.BadBones69.CrazyEnchantments.Enchantments;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import me.BadBones69.CrazyEnchantments.Main;
import me.BadBones69.CrazyEnchantments.Methods;
import me.BadBones69.CrazyEnchantments.API.CEnchantments;
import me.BadBones69.CrazyEnchantments.API.Events.EnchantmentUseEvent;
import me.BadBones69.CrazyEnchantments.multisupport.SpartanSupport;
import me.BadBones69.CrazyEnchantments.multisupport.Support;

public class Bows implements Listener{

	private HashMap<Projectile, ItemStack> Arrow = new HashMap<Projectile, ItemStack>();
	private HashMap<Projectile, Entity> P = new HashMap<Projectile, Entity>();
	private HashMap<Projectile, ArrayList<CEnchantments>> Enchant = new HashMap<Projectile, ArrayList<CEnchantments>>();
	private ArrayList<Entity> Explode = new ArrayList<Entity>();
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBowShoot(final EntityShootBowEvent e){
		if(e.isCancelled())return;
		ItemStack bow = e.getBow();
		if(Main.CE.hasEnchantments(bow)){
			Boolean hasEnchantments = false;
			ArrayList<CEnchantments> enchants = new ArrayList<CEnchantments>();
			for(CEnchantments ench : getEnchantments()){
				Projectile arrow = (Projectile) e.getProjectile();
				if(Main.CE.hasEnchantment(bow, ench)){
					if(ench.isEnabled()){
						Arrow.put(arrow, bow);
						P.put(arrow, e.getEntity());
						enchants.add(ench);
						hasEnchantments = true;
					}
				}
			}
			if(hasEnchantments){
				Enchant.put((Projectile) e.getProjectile(), enchants);
			}
			if(Main.CE.hasEnchantment(bow, CEnchantments.MULTIARROW)){
				if(CEnchantments.MULTIARROW.isEnabled()){
					int power = Main.CE.getPower(bow, CEnchantments.MULTIARROW);
					if(Methods.randomPicker(3)){
						if(e.getEntity() instanceof Player){
							EnchantmentUseEvent event = new EnchantmentUseEvent((Player)e.getEntity(), CEnchantments.MULTIARROW, bow);
							Bukkit.getPluginManager().callEvent(event);
							if(!event.isCancelled()){
								for(int i=1;i<=power;i++){
									Arrow arrow = e.getEntity().getWorld().spawn(e.getProjectile().getLocation(), Arrow.class);
									arrow.setShooter(e.getEntity());
									arrow.setBounce(false);
									Vector v = new Vector(Vec(), 0, Vec());
									arrow.setVelocity(e.getProjectile().getVelocity().add(v));
									if(((Arrow)e.getProjectile()).isCritical())arrow.setCritical(true);
								}
							}
						}else{
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
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onland(ProjectileHitEvent e) {
		if(Arrow.containsKey(e.getEntity())){
			Entity arrow = e.getEntity();
			if(Enchant.get(arrow).contains(CEnchantments.BOOM)){
				if(CEnchantments.BOOM.isEnabled()){
					if(Methods.randomPicker(6-Main.CE.getPower(Arrow.get(arrow), CEnchantments.BOOM))){
						TNTPrimed tnt = arrow.getWorld().spawn(arrow.getLocation(), TNTPrimed.class);
						tnt.setFuseTicks(0);
						tnt.getWorld().playEffect(tnt.getLocation(), Effect.EXPLOSION_LARGE, 1);
						Explode.add(tnt);
						arrow.remove();
					}
				}
			}
			if(Enchant.get(arrow).contains(CEnchantments.LIGHTNING)){
				if(CEnchantments.LIGHTNING.isEnabled()){
					Location loc = arrow.getLocation();
					if(Methods.randomPicker(5)){
						loc.getWorld().strikeLightningEffect(loc);
						for(LivingEntity en : Methods.getNearbyEntities(loc, 2D, arrow)){
							if(Support.allowsPVP(en.getLocation())){
								if(!Support.isFriendly(P.get(arrow), en)){
									if(!P.get(arrow).getName().equalsIgnoreCase(en.getName())){
										en.damage(5D);
									}
								}
							}
						}
					}
					P.remove(arrow);
				}
			}
			Arrow.remove(arrow);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
 	public void onArrowDamage(EntityDamageByEntityEvent e){
		if(e.isCancelled())return;
		if(e.getDamager() instanceof Arrow){
			if(e.getEntity() instanceof LivingEntity){
				LivingEntity en = (LivingEntity) e.getEntity();
				Projectile arrow = (Projectile) e.getDamager();
				if(Arrow.containsKey(arrow)){
					ItemStack item = Arrow.get(arrow);
					if(Support.isFriendly(P.get(e.getDamager()), e.getEntity())){
						if(Enchant.get(arrow).contains(CEnchantments.DOCTOR)){
							if(CEnchantments.DOCTOR.isEnabled()){
								int heal = 2 + Main.CE.getPower(Arrow.get(arrow), CEnchantments.DOCTOR);
								if(en.getHealth()<en.getMaxHealth()){
									if(en instanceof Player){
										EnchantmentUseEvent event = new EnchantmentUseEvent((Player)e.getEntity(), CEnchantments.DOCTOR, item);
										Bukkit.getPluginManager().callEvent(event);
										if(!event.isCancelled()){
											if(en.getHealth() + heal < en.getMaxHealth()){
												en.setHealth(en.getHealth() + heal);
											}
											if(en.getHealth() + heal >= en.getMaxHealth()){
												en.setHealth(en.getMaxHealth());
											}
										}
									}else{
										if(en.getHealth() + heal < en.getMaxHealth()){
											en.setHealth(en.getHealth() + heal);
										}
										if(en.getHealth() + heal >= en.getMaxHealth()){
											en.setHealth(en.getMaxHealth());
										}
									}
								}
							}
						}
					}
					if(!Support.isFriendly(P.get(arrow), e.getEntity())){
						if(Enchant.get(arrow).contains(CEnchantments.PULL)){
							if(CEnchantments.PULL.isEnabled()){
								if(Methods.randomPicker(5 - Main.CE.getPower(Arrow.get(arrow), CEnchantments.PULL))){
									Vector v = P.get(arrow).getLocation().toVector().subtract(en.getLocation().toVector()).normalize().multiply(3).setY(2);
									if(en instanceof Player){
										EnchantmentUseEvent event = new EnchantmentUseEvent((Player)e.getEntity(), CEnchantments.PULL, item);
										Bukkit.getPluginManager().callEvent(event);
										if(!event.isCancelled()){
											if(Support.hasSpartan()){
												SpartanSupport.cancelSpeed((Player)e.getEntity());
												SpartanSupport.cancelFly((Player)e.getEntity());
												SpartanSupport.cancelClip((Player)e.getEntity());
												SpartanSupport.cancelNormalMovements((Player)e.getEntity());
												SpartanSupport.cancelNoFall((Player)e.getEntity());
												SpartanSupport.cancelJesus((Player)e.getEntity());
											}
											en.setVelocity(v);
										}
									}else{
										en.setVelocity(v);
									}
								}
							}
						}
						if(Enchant.get(arrow).contains(CEnchantments.ICEFREEZE)){
							if(CEnchantments.ICEFREEZE.isEnabled()){
								if(Methods.randomPicker(5)){
									if(en instanceof Player){
										EnchantmentUseEvent event = new EnchantmentUseEvent((Player)e.getEntity(), CEnchantments.ICEFREEZE, item);
										Bukkit.getPluginManager().callEvent(event);
										if(!event.isCancelled()){
											en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5*20, 1));
										}
									}else{
										en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5*20, 1));
									}
								}
							}
						}
						if(Enchant.get(arrow).contains(CEnchantments.PIERCING)){
							if(CEnchantments.PIERCING.isEnabled()){
								if(Methods.randomPicker(20-Main.CE.getPower(Arrow.get(arrow), CEnchantments.PIERCING))){
									if(en instanceof Player){
										EnchantmentUseEvent event = new EnchantmentUseEvent((Player)e.getEntity(), CEnchantments.PIERCING, item);
										Bukkit.getPluginManager().callEvent(event);
										if(!event.isCancelled()){
											e.setDamage(e.getDamage() *2);
										}
									}else{
										e.setDamage(e.getDamage() *2);
									}
								}
							}
						}
						if(Enchant.get(arrow).contains(CEnchantments.VENOM)){
							if(CEnchantments.VENOM.isEnabled()){
								if(Methods.randomPicker(10)){
									if(en instanceof Player){
										EnchantmentUseEvent event = new EnchantmentUseEvent((Player)e.getEntity(), CEnchantments.VENOM, item);
										Bukkit.getPluginManager().callEvent(event);
										if(!event.isCancelled()){
											en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 2*20, Main.CE.getPower(Arrow.get(arrow), CEnchantments.VENOM)-1));
										}
									}else{
										en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 2*20, Main.CE.getPower(Arrow.get(arrow), CEnchantments.VENOM)-1));
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
	public void onExplode(EntityExplodeEvent e){
		Entity en = e.getEntity();
		if(Explode.contains(en)){
			e.setCancelled(true);
			Explode.remove(en);
		}
	}
	
	private ArrayList<CEnchantments> getEnchantments(){
		ArrayList<CEnchantments> enchants = new ArrayList<CEnchantments>();
		enchants.add(CEnchantments.BOOM);
		enchants.add(CEnchantments.DOCTOR);
		enchants.add(CEnchantments.ICEFREEZE);
		enchants.add(CEnchantments.LIGHTNING);
		enchants.add(CEnchantments.PIERCING);
		enchants.add(CEnchantments.VENOM);
		enchants.add(CEnchantments.PULL);
		return enchants;
	}
	
	private float Vec(){
		float spread = (float) .2;
		float Vec = -spread + (float) (Math.random() * ((spread - -spread)));
		return Vec;
	}
	
}
