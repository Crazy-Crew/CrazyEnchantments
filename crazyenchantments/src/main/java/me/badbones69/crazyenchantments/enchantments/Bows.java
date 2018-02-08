package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.multisupport.AACSupport;
import me.badbones69.crazyenchantments.multisupport.SpartanSupport;
import me.badbones69.crazyenchantments.multisupport.Support;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Bows implements Listener {
	
	private CrazyEnchantments crazyEnchantments = CrazyEnchantments.getInstance();
	private HashMap<Entity, Entity> shooter = new HashMap<>();
	private HashMap<Entity, ItemStack> arrows = new HashMap<>();
	private HashMap<Entity, ArrayList<CEnchantments>> attachedEnchantments = new HashMap<>();
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBowShoot(EntityShootBowEvent e) {
		if(e.isCancelled()) return;
		ItemStack bow = e.getBow();
		if(crazyEnchantments.hasEnchantments(bow)) {
			Boolean hasEnchantments = false;
			ArrayList<CEnchantments> enchants = new ArrayList<>();
			if(e.getProjectile() instanceof Arrow) {
				Projectile arrow = (Projectile) e.getProjectile();
				for(CEnchantment ench : crazyEnchantments.getEnchantmentsOnItem(bow)) {
					if(ench.isActivated()) {
						CEnchantments enchantment = CEnchantments.getFromName(ench.getName());
						if(enchantment != null) {
							arrows.put(arrow, bow);
							shooter.put(arrow, e.getEntity());
							enchants.add(enchantment);
							hasEnchantments = true;
						}
					}
				}
			}
			if(hasEnchantments) {
				attachedEnchantments.put(e.getProjectile(), enchants);
			}
			if(crazyEnchantments.hasEnchantment(bow, CEnchantments.MULTIARROW)) {
				if(CEnchantments.MULTIARROW.isActivated()) {
					int power = crazyEnchantments.getPower(bow, CEnchantments.MULTIARROW);
					if(CEnchantments.MULTIARROW.chanceSuccessful(bow)) {
						if(e.getEntity() instanceof Player) {
							EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.MULTIARROW, bow);
							Bukkit.getPluginManager().callEvent(event);
							if(!event.isCancelled()) {
								for(int i = 1; i <= power; i++) {
									Arrow arrow = e.getEntity().getWorld().spawn(e.getProjectile().getLocation(), Arrow.class);
									arrow.setShooter(e.getEntity());
									arrow.setBounce(false);
									Vector v = new Vector(Vec(), 0, Vec());
									arrow.setVelocity(e.getProjectile().getVelocity().add(v));
									if(((Arrow) e.getProjectile()).isCritical()) {
										arrow.setCritical(true);
									}
									if(e.getProjectile().getFireTicks() > 0) {
										arrow.setFireTicks(e.getProjectile().getFireTicks());
									}
								}
							}
						}else {
							for(int i = 1; i <= power; i++) {
								Arrow arrow = e.getEntity().getWorld().spawn(e.getProjectile().getLocation(), Arrow.class);
								arrow.setShooter(e.getEntity());
								arrow.setBounce(false);
								Vector v = new Vector(Vec(), 0, Vec());
								arrow.setVelocity(e.getProjectile().getVelocity().add(v));
								if(((Arrow) e.getProjectile()).isCritical()) {
									arrow.setCritical(true);
								}
								if(e.getProjectile().getFireTicks() > 0) {
									arrow.setFireTicks(e.getProjectile().getFireTicks());
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
		if(arrows.containsKey(e.getEntity())) {
			if(shooter.containsKey(e.getEntity())) {
				Entity arrow = e.getEntity();
				List<CEnchantments> enchantments = attachedEnchantments.get(arrow);
				if(enchantments != null) {
					if(enchantments.contains(CEnchantments.BOOM)) {
						if(CEnchantments.BOOM.isActivated()) {
							if(CEnchantments.BOOM.chanceSuccessful(arrows.get(arrow))) {
								Methods.explode(shooter.get(arrow), arrow);
								arrow.remove();
							}
						}
					}
					if(enchantments.contains(CEnchantments.LIGHTNING)) {
						if(CEnchantments.LIGHTNING.isActivated()) {
							Location loc = arrow.getLocation();
							if(CEnchantments.LIGHTNING.chanceSuccessful(arrows.get(arrow))) {
								loc.getWorld().strikeLightningEffect(loc);
								for(LivingEntity entity : Methods.getNearbyLivingEntities(loc, 2D, arrow)) {
									if(Support.allowsPVP(entity.getLocation())) {
										if(!Support.isFriendly(shooter.get(arrow), entity)) {
											if(!shooter.get(arrow).getName().equalsIgnoreCase(entity.getName())) {
												entity.damage(5D);
											}
										}
									}
								}
							}
							shooter.remove(arrow);
						}
					}
					if(enchantments.contains(CEnchantments.STICKY_SHOT)) {
						if(CEnchantments.STICKY_SHOT.isActivated()) {
							if(CEnchantments.STICKY_SHOT.chanceSuccessful(arrows.get(arrow))) {
								if(Support.allowsPVP(arrow.getLocation())) {
									Block block = e.getHitBlock();
									Entity entity = e.getHitEntity();
									Boolean removeArrow = false;
									if(block != null) {
										Location location = block.getLocation().add(0, 1, 0);
										if(location.getBlock().getType() == Material.AIR) {
											setSpiderWeb(location);
											removeArrow = true;
										}
									}else if(entity != null) {
										Location location = entity.getLocation();
										if(location.getBlock().getType() == Material.AIR) {
											setSpiderWeb(location);
											removeArrow = true;
										}
									}
									if(removeArrow) {
										arrow.remove();
									}
								}
							}
						}
					}
				}
				new BukkitRunnable() {
					@Override
					public void run() {
						arrows.remove(arrow);
					}
				}.runTaskLaterAsynchronously(Methods.getPlugin(), 5 * 20);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onArrowDamage(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Arrow) {
			if(e.getEntity() instanceof LivingEntity) {
				LivingEntity en = (LivingEntity) e.getEntity();
				Projectile arrow = (Projectile) e.getDamager();
				if(arrows.containsKey(arrow)) {
					if(shooter.containsKey(arrow)) {
						ItemStack item = arrows.get(arrow);
						if(Support.isFriendly(shooter.get(e.getDamager()), e.getEntity())) {
							if(attachedEnchantments.get(arrow).contains(CEnchantments.DOCTOR)) {
								if(CEnchantments.DOCTOR.isActivated()) {
									int heal = 1 + crazyEnchantments.getPower(arrows.get(arrow), CEnchantments.DOCTOR);
									if(en.getHealth() < en.getMaxHealth()) {
										if(en instanceof Player) {
											EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.DOCTOR, item);
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
							if(!Support.isFriendly(shooter.get(arrow), e.getEntity())) {
								if(attachedEnchantments.get(arrow).contains(CEnchantments.PULL)) {
									if(CEnchantments.PULL.isActivated()) {
										if(CEnchantments.PULL.chanceSuccessful(item)) {
											Vector v = shooter.get(arrow).getLocation().toVector().subtract(en.getLocation().toVector()).normalize().multiply(3);
											if(en instanceof Player) {
												EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.PULL, item);
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
								if(attachedEnchantments.get(arrow).contains(CEnchantments.ICEFREEZE)) {
									if(CEnchantments.ICEFREEZE.isActivated()) {
										if(CEnchantments.ICEFREEZE.chanceSuccessful(item)) {
											if(en instanceof Player) {
												EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.ICEFREEZE, item);
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
								if(attachedEnchantments.get(arrow).contains(CEnchantments.PIERCING)) {
									if(CEnchantments.PIERCING.isActivated()) {
										if(CEnchantments.PIERCING.chanceSuccessful(item)) {
											if(en instanceof Player) {
												EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.PIERCING, item);
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
								if(attachedEnchantments.get(arrow).contains(CEnchantments.VENOM)) {
									if(CEnchantments.VENOM.isActivated()) {
										if(CEnchantments.VENOM.chanceSuccessful(item)) {
											if(en instanceof Player) {
												EnchantmentUseEvent event = new EnchantmentUseEvent((Player) e.getEntity(), CEnchantments.VENOM, item);
												Bukkit.getPluginManager().callEvent(event);
												if(!event.isCancelled()) {
													en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 2 * 20, crazyEnchantments.getPower(arrows.get(arrow), CEnchantments.VENOM) - 1));
												}
											}else {
												en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 2 * 20, crazyEnchantments.getPower(arrows.get(arrow), CEnchantments.VENOM) - 1));
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
	
	private void setSpiderWeb(Location location) {
		location.getBlock().setType(Material.WEB);
		new BukkitRunnable() {
			@Override
			public void run() {
				location.getBlock().setType(Material.AIR);
			}
		}.runTaskLater(Methods.getPlugin(), 100);
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
	
	private float Vec() {
		float spread = (float) .2;
		return -spread + (float) (Math.random() * ((spread - -spread)));
	}
	
}