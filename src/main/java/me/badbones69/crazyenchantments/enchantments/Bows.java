package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import me.badbones69.crazyenchantments.api.objects.EnchantedArrow;
import me.badbones69.crazyenchantments.multisupport.AACSupport;
import me.badbones69.crazyenchantments.multisupport.SpartanSupport;
import me.badbones69.crazyenchantments.multisupport.Support;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Bows implements Listener {
	
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	private List<EnchantedArrow> enchantedArrows = new ArrayList<>();
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBowShoot(final EntityShootBowEvent e) {
		if(e.isCancelled()) return;
		ItemStack bow = e.getBow();
		if(ce.hasEnchantments(bow)) {
			if(e.getProjectile() instanceof Arrow) {
				Arrow arrow = (Arrow) e.getProjectile();
				boolean hasEnchantments = ce.hasEnchantments(bow);
				if(hasEnchantments) {
					enchantedArrows.add(new EnchantedArrow(arrow, e.getEntity(), bow, ce.getEnchantmentsOnItem(bow)));
					if(ce.hasEnchantment(bow, CEnchantments.MULTIARROW)) {
						if(CEnchantments.MULTIARROW.isActivated()) {
							int power = ce.getLevel(bow, CEnchantments.MULTIARROW);
							if(CEnchantments.MULTIARROW.chanceSuccessful(bow)) {
								if(e.getEntity() instanceof Player) {
									EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.MULTIARROW, bow);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										for(int i = 1; i <= power; i++) {
											Arrow spawnedArrow = e.getEntity().getWorld().spawn(e.getProjectile().getLocation(), Arrow.class);
											spawnedArrow.setShooter(e.getEntity());
											spawnedArrow.setBounce(false);
											Vector v = new Vector(randomSpred(), 0, randomSpred());
											spawnedArrow.setVelocity(e.getProjectile().getVelocity().add(v));
											if(((Arrow) e.getProjectile()).isCritical()) {
												spawnedArrow.setCritical(true);
											}
											if(e.getProjectile().getFireTicks() > 0) {
												spawnedArrow.setFireTicks(e.getProjectile().getFireTicks());
											}
										}
									}
								}else {
									for(int i = 1; i <= power; i++) {
										Arrow spawnedArrow = e.getEntity().getWorld().spawn(e.getProjectile().getLocation(), Arrow.class);
										spawnedArrow.setShooter(e.getEntity());
										spawnedArrow.setBounce(false);
										Vector v = new Vector(randomSpred(), 0, randomSpred());
										spawnedArrow.setVelocity(e.getProjectile().getVelocity().add(v));
										if(((Arrow) e.getProjectile()).isCritical()) {
											spawnedArrow.setCritical(true);
										}
										if(e.getProjectile().getFireTicks() > 0) {
											spawnedArrow.setFireTicks(e.getProjectile().getFireTicks());
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
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onland(ProjectileHitEvent e) {
		if(e.getEntity() instanceof Arrow) {
			EnchantedArrow arrow = getEnchantedArrow((Arrow) e.getEntity());
			if(arrow != null) {
				if(arrow.getEnchantments().contains(CEnchantments.BOOM)) {
					if(CEnchantments.BOOM.isActivated()) {
						if(CEnchantments.BOOM.chanceSuccessful(arrow.getBow())) {
							Methods.explode(arrow.getShooter(), arrow.getArrow());
							arrow.getArrow().remove();
						}
					}
				}
				if(arrow.hasEnchantment(CEnchantments.LIGHTNING)) {
					if(CEnchantments.LIGHTNING.isActivated()) {
						Location loc = arrow.getArrow().getLocation();
						if(CEnchantments.LIGHTNING.chanceSuccessful(arrow.getBow())) {
							loc.getWorld().strikeLightningEffect(loc);
							for(LivingEntity entity : Methods.getNearbyLivingEntities(loc, 2D, arrow.getArrow())) {
								if(Support.allowsPVP(entity.getLocation())) {
									if(!Support.isFriendly(arrow.getShooter(), entity)) {
										if(!arrow.getShooter().getUniqueId().equals(entity.getUniqueId())) {
											entity.damage(5D);
										}
									}
								}
							}
						}
					}
				}
				//Unsure what this code was needed for.
				//new BukkitRunnable() {
				//	@Override
				//	public void run() {
				//		arrows.remove(arrow);
				//	}
				//}.runTaskLaterAsynchronously(Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments"), 5 * 20);
				enchantedArrows.remove(arrow);// Removes it from the list.
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onArrowDamage(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Arrow) {
			if(e.getEntity() instanceof LivingEntity) {
				LivingEntity en = (LivingEntity) e.getEntity();
				EnchantedArrow arrow = getEnchantedArrow((Arrow) e.getDamager());
				if(arrow != null) {
					ItemStack bow = arrow.getBow();
					if(Support.isFriendly(arrow.getShooter(), e.getEntity())) {
						if(arrow.hasEnchantment(CEnchantments.DOCTOR)) {
							if(CEnchantments.DOCTOR.isActivated()) {
								int heal = 1 + arrow.getLevel(CEnchantments.DOCTOR);
								if(en.getHealth() < en.getMaxHealth()) {
									if(en instanceof Player) {
										EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.DOCTOR, bow);
										Bukkit.getPluginManager().callEvent(event);
										if(!event.isCancelled()) {
											if(en.getHealth() + heal < en.getMaxHealth()) {
												en.setHealth(en.getHealth() + heal);
											}
											if(en.getHealth() + heal >= en.getMaxHealth()) {
												en.setHealth(en.getMaxHealth());
											}
										}
									}else {
										if(en.getHealth() + heal < en.getMaxHealth()) {
											en.setHealth(en.getHealth() + heal);
										}
										if(en.getHealth() + heal >= en.getMaxHealth()) {
											en.setHealth(en.getMaxHealth());
										}
									}
								}
							}
						}
					}
					if(!e.isCancelled()) {
						if(!Support.isFriendly(arrow.getShooter(), e.getEntity())) {
							if(arrow.hasEnchantment(CEnchantments.PULL)) {
								if(CEnchantments.PULL.isActivated()) {
									if(CEnchantments.PULL.chanceSuccessful(bow)) {
										Vector v = arrow.getShooter().getLocation().toVector().subtract(en.getLocation().toVector()).normalize().multiply(3);
										if(en instanceof Player) {
											EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.PULL, bow);
											Bukkit.getPluginManager().callEvent(event);
											Player player = (Player) e.getEntity();
											if(!event.isCancelled()) {
												if(SupportedPlugins.SPARTAN.isPluginLoaded()) {
													SpartanSupport.cancelSpeed(player);
													SpartanSupport.cancelFly(player);
													SpartanSupport.cancelClip(player);
													SpartanSupport.cancelNormalMovements(player);
													SpartanSupport.cancelNoFall(player);
													SpartanSupport.cancelJesus(player);
												}
												if(SupportedPlugins.AAC.isPluginLoaded()) {
													AACSupport.exemptPlayerTime(player);
												}
												en.setVelocity(v);
											}
										}else {
											en.setVelocity(v);
										}
									}
								}
							}
							if(arrow.hasEnchantment(CEnchantments.ICEFREEZE)) {
								if(CEnchantments.ICEFREEZE.isActivated()) {
									if(CEnchantments.ICEFREEZE.chanceSuccessful(bow)) {
										if(en instanceof Player) {
											EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.ICEFREEZE, bow);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5 * 20, 1));
											}
										}else {
											en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5 * 20, 1));
										}
									}
								}
							}
							if(arrow.hasEnchantment(CEnchantments.PIERCING)) {
								if(CEnchantments.PIERCING.isActivated()) {
									if(CEnchantments.PIERCING.chanceSuccessful(bow)) {
										if(en instanceof Player) {
											EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.PIERCING, bow);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												e.setDamage(e.getDamage() * 2);
											}
										}else {
											e.setDamage(e.getDamage() * 2);
										}
									}
								}
							}
							if(arrow.hasEnchantment(CEnchantments.VENOM)) {
								if(CEnchantments.VENOM.isActivated()) {
									if(CEnchantments.VENOM.chanceSuccessful(bow)) {
										if(en instanceof Player) {
											EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.VENOM, bow);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 2 * 20, arrow.getLevel(CEnchantments.VENOM) - 1));
											}
										}else {
											en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 2 * 20, arrow.getLevel(CEnchantments.VENOM) - 1));
										}
									}
								}
							}
						}
					}
					enchantedArrows.remove(arrow);
				}
			}
		}
	}
	
	private EnchantedArrow getEnchantedArrow(Arrow arrow) {
		for(EnchantedArrow enchantedArrow : enchantedArrows) {
			if(enchantedArrow.getArrow().equals(arrow)) {
				return enchantedArrow;
			}
		}
		return null;
	}
	
	private ArrayList<CEnchantments> getEnchantments() {
		ArrayList<CEnchantments> enchants = new ArrayList<>();
		enchants.add(CEnchantments.BOOM);
		enchants.add(CEnchantments.DOCTOR);
		enchants.add(CEnchantments.ICEFREEZE);
		enchants.add(CEnchantments.LIGHTNING);
		enchants.add(CEnchantments.PIERCING);
		enchants.add(CEnchantments.VENOM);
		enchants.add(CEnchantments.PULL);
		return enchants;
	}
	
	private float randomSpred() {
		float spread = (float) .2;
		return -spread + (float) (Math.random() * ((spread - -spread)));
	}
	
}