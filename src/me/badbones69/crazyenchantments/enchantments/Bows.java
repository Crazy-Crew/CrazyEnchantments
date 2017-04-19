package me.badbones69.crazyenchantments.enchantments;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.ParticleEffect;
import me.badbones69.crazyenchantments.api.CEnchantments;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import me.badbones69.crazyenchantments.multisupport.SpartanSupport;
import me.badbones69.crazyenchantments.multisupport.Support;

public class Bows implements Listener{

	private HashMap<Projectile, Entity> P = new HashMap<Projectile, Entity>();
	private HashMap<Projectile, ItemStack> Arrow = new HashMap<Projectile, ItemStack>();
	private HashMap<Projectile, ArrayList<CEnchantments>> Enchant = new HashMap<Projectile, ArrayList<CEnchantments>>();
	
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
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onland(ProjectileHitEvent e) {
		if(Arrow.containsKey(e.getEntity())){
			if(P.containsKey(e.getEntity())){
				final Entity arrow = e.getEntity();
				if(Enchant.get(arrow).contains(CEnchantments.BOOM)){
					if(CEnchantments.BOOM.isEnabled()){
						if(Methods.randomPicker(6-Main.CE.getPower(Arrow.get(arrow), CEnchantments.BOOM))){
							ParticleEffect.FLAME.display(0, 0, 0, 1, 200, arrow.getLocation().add(0,1,0), 100);
							ParticleEffect.EXPLOSION_HUGE.display(0, 0, 0, 0, 2, arrow.getLocation().add(0,1,0), 100);
							ParticleEffect.CLOUD.display((float).4, (float).5, (float).4, 1, 30, arrow.getLocation().add(0,1,0), 100);
							for(LivingEntity en : Methods.getNearbyEntities(arrow.getLocation(), 3D, arrow)){
								if(Support.allowsPVP(en.getLocation())){
									if(!Support.isFriendly(P.get(arrow), en)){
										if(!P.get(arrow).getName().equalsIgnoreCase(en.getName())){
											en.damage(5D);
										}
									}
								}
							}
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
				new BukkitRunnable(){
					@Override
					public void run() {
						if(Arrow.containsKey(arrow)){
							Arrow.remove(arrow);
						}
					}
				}.runTaskLaterAsynchronously(Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments"), 5*20);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
 	public void onArrowDamage(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Arrow){
			if(e.getEntity() instanceof LivingEntity){
				LivingEntity en = (LivingEntity) e.getEntity();
				Projectile arrow = (Projectile) e.getDamager();
				if(Arrow.containsKey(arrow)){
					if(P.containsKey(arrow)){
						ItemStack item = Arrow.get(arrow);
						if(Support.isFriendly(P.get(e.getDamager()), e.getEntity())){
							if(Enchant.get(arrow).contains(CEnchantments.DOCTOR)){
								if(CEnchantments.DOCTOR.isEnabled()){
									int heal = 1 + Main.CE.getPower(Arrow.get(arrow), CEnchantments.DOCTOR);
									if (en.getHealth() < en.getMaxHealth()) {
										if (en instanceof Player) {
											EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.DOCTOR, item);
											Bukkit.getPluginManager().callEvent(event);
											if (!event.isCancelled()) {
												if (en.getHealth() + heal < en.getMaxHealth()) {
													en.setHealth(en.getHealth() + heal);
												}
												if (en.getHealth() + heal >= en.getMaxHealth()) {
													en.setHealth(en.getMaxHealth());
												}
											}
										}else {
											if (en.getHealth() + heal < en.getMaxHealth()) {
												en.setHealth(en.getHealth() + heal);
											}
											if (en.getHealth() + heal >= en.getMaxHealth()) {
												en.setHealth(en.getMaxHealth());
											}
										}
									}
								}
							}
						}
						if(!e.isCancelled()){
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