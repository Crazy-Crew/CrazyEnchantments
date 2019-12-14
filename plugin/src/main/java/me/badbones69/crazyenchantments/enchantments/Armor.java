package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.*;
import me.badbones69.crazyenchantments.controllers.ProtectionCrystal;
import me.badbones69.crazyenchantments.multisupport.*;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import me.badbones69.crazyenchantments.multisupport.particles.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Armor implements Listener {
	
	private static HashMap<Player, List<LivingEntity>> mobs = new HashMap<>();
	private List<Player> fall = new ArrayList<>();
	private HashMap<Player, HashMap<CEnchantments, Calendar>> timer = new HashMap<>();
	private HashMap<Player, Calendar> mobTimer = new HashMap<>();
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	public static HashMap<Player, List<LivingEntity>> getAllies() {
		return mobs;
	}
	
	public static void removeAllies() {
		for(Player player : mobs.keySet()) {
			for(LivingEntity ally : mobs.get(player)) {
				ally.remove();
			}
		}
	}
	
	@EventHandler
	public void onEquip(ArmorEquipEvent e) {
		Player player = e.getPlayer();
		ItemStack newItem = e.getNewArmorPiece();
		ItemStack oldItem = e.getOldArmorPiece();
		if(ce.hasEnchantments(oldItem)) {// Removing the potion effects.
			for(CEnchantments ench : ce.getEnchantmentPotions().keySet()) {
				if(ce.hasEnchantment(oldItem, ench.getEnchantment())) {
					if(ench.isActivated()) {
						HashMap<PotionEffectType, Integer> effects = ce.getUpdatedEffects(player, new ItemStack(Material.AIR), oldItem, ench);
						for(PotionEffectType type : effects.keySet()) {
							if(effects.get(type) < 0) {
								player.removePotionEffect(type);
							}else {
								player.removePotionEffect(type);
								player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, effects.get(type)));
							}
						}
					}
				}
			}
		}
		if(ce.hasEnchantments(newItem)) {// Adding the potion effects.
			for(CEnchantments ench : ce.getEnchantmentPotions().keySet()) {
				if(ce.hasEnchantment(newItem, ench.getEnchantment())) {
					if(ench.isActivated()) {
						EnchantmentUseEvent event = new EnchantmentUseEvent(player, ench.getEnchantment(), newItem);
						Bukkit.getPluginManager().callEvent(event);
						if(!event.isCancelled()) {
							HashMap<PotionEffectType, Integer> effects = ce.getUpdatedEffects(player, newItem, oldItem, ench);
							for(PotionEffectType type : effects.keySet()) {
								if(effects.get(type) < 0) {
									player.removePotionEffect(type);
								}else {
									player.removePotionEffect(type);
									player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, effects.get(type)));
								}
							}
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		if(e.isCancelled() || ce.isIgnoredEvent(e) || ce.isIgnoredUUID(e.getDamager().getUniqueId())) return;
		if(Support.isFriendly(e.getDamager(), e.getEntity())) return;
		if(e.getDamager() instanceof LivingEntity) {
			if(e.getEntity() instanceof Player) {
				final Player player = (Player) e.getEntity();
				final LivingEntity damager = (LivingEntity) e.getDamager();
				for(ItemStack armor : player.getEquipment().getArmorContents()) {
					if(ce.hasEnchantments(armor)) {
						if(ce.hasEnchantment(armor, CEnchantments.ROCKET.getEnchantment())) {
							if(CEnchantments.ROCKET.isActivated()) {
								if(player.getHealth() <= 8) {
									if(CEnchantments.ROCKET.chanceSuccessful(armor)) {
										EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.ROCKET.getEnchantment(), armor);
										Bukkit.getPluginManager().callEvent(event);
										if(SupportedPlugins.AAC.isPluginLoaded()) {
											AACSupport.exemptPlayerTime(player);
										}
										if(!event.isCancelled()) {
											Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ce.getPlugin(), () -> {
												Vector v = player.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize().setY(1);
												player.setVelocity(v);
											}, 1);
											if(Version.getCurrentVersion().isNewer(Version.v1_8_R3)) {
												player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, player.getLocation(), 1);
											}else {
												ParticleEffect.EXPLOSION_HUGE.display(0, 0, 0, 1, 1, player.getLocation(), 100);
											}
											fall.add(player);
											Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ce.getPlugin(), () -> fall.remove(player), 8 * 20);
										}
									}
								}
							}
						}
						if(ce.hasEnchantment(armor, CEnchantments.ENLIGHTENED)) {
							if(CEnchantments.ENLIGHTENED.isActivated()) {
								if(CEnchantments.ENLIGHTENED.chanceSuccessful(armor)) {
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.ENLIGHTENED.getEnchantment(), armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										double heal = ce.getLevel(armor, CEnchantments.ENLIGHTENED);
										if(player.getHealth() + heal < player.getMaxHealth()) {
											player.setHealth(player.getHealth() + heal);
										}
										if(player.getHealth() + heal >= player.getMaxHealth()) {
											player.setHealth(player.getMaxHealth());
										}
									}
								}
							}
						}
						if(ce.hasEnchantment(armor, CEnchantments.FORTIFY)) {
							if(CEnchantments.FORTIFY.isActivated()) {
								if(CEnchantments.FORTIFY.chanceSuccessful(armor)) {
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.FORTIFY.getEnchantment(), armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										damager.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5 * 20, ce.getLevel(armor, CEnchantments.FORTIFY)));
									}
								}
							}
						}
						if(ce.hasEnchantment(armor, CEnchantments.FREEZE)) {
							if(CEnchantments.FREEZE.isActivated()) {
								if(CEnchantments.FREEZE.chanceSuccessful(armor)) {
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.FREEZE.getEnchantment(), armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 1 + ce.getLevel(armor, CEnchantments.FREEZE)));
									}
								}
							}
						}
						if(ce.hasEnchantment(armor, CEnchantments.MOLTEN)) {
							if(CEnchantments.MOLTEN.isActivated()) {
								if(CEnchantments.MOLTEN.chanceSuccessful(armor)) {
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.MOLTEN.getEnchantment(), armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										damager.setFireTicks((ce.getLevel(armor, CEnchantments.MOLTEN) * 2) * 20);
									}
								}
							}
						}
						if(ce.hasEnchantment(armor, CEnchantments.PAINGIVER)) {
							if(CEnchantments.PAINGIVER.isActivated()) {
								if(CEnchantments.PAINGIVER.chanceSuccessful(armor)) {
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.PAINGIVER.getEnchantment(), armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 3 * 20, ce.getLevel(armor, CEnchantments.PAINGIVER)));
									}
								}
							}
						}
						if(ce.hasEnchantment(armor, CEnchantments.SAVIOR)) {
							if(CEnchantments.SAVIOR.isActivated()) {
								if(CEnchantments.SAVIOR.chanceSuccessful(armor)) {
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.SAVIOR.getEnchantment(), armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										e.setDamage(e.getDamage() / 2);
									}
								}
							}
						}
						if(ce.hasEnchantment(armor, CEnchantments.SMOKEBOMB)) {
							if(CEnchantments.SMOKEBOMB.isActivated()) {
								if(CEnchantments.SMOKEBOMB.chanceSuccessful(armor)) {
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.SMOKEBOMB.getEnchantment(), armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 1));
										damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, 0));
									}
								}
							}
						}
						if(ce.hasEnchantment(armor, CEnchantments.VOODOO)) {
							if(CEnchantments.VOODOO.isActivated()) {
								if(CEnchantments.VOODOO.chanceSuccessful(armor)) {
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.VOODOO.getEnchantment(), armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										damager.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5 * 20, ce.getLevel(armor, CEnchantments.VOODOO) - 1));
									}
								}
							}
						}
						if(ce.hasEnchantment(armor, CEnchantments.INSOMNIA)) {
							if(CEnchantments.INSOMNIA.isActivated()) {
								if(CEnchantments.INSOMNIA.chanceSuccessful(armor)) {
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.INSOMNIA.getEnchantment(), armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										e.setDamage((e.getDamage() * 2));
									}
								}
							}
						}
						if(ce.hasEnchantment(armor, CEnchantments.CACTUS)) {
							if(CEnchantments.CACTUS.isActivated()) {
								if(CEnchantments.CACTUS.chanceSuccessful(armor)) {
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.CACTUS.getEnchantment(), armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										damager.damage(ce.getLevel(armor, CEnchantments.CACTUS));
									}
								}
							}
						}
						if(ce.hasEnchantment(armor, CEnchantments.STORMCALLER)) {
							if(CEnchantments.STORMCALLER.isActivated()) {
								if(CEnchantments.STORMCALLER.chanceSuccessful(armor)) {
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.STORMCALLER.getEnchantment(), armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										Location loc = damager.getLocation();
										loc.getWorld().spigot().strikeLightningEffect(loc, true);
										int lightningSoundRange = Files.CONFIG.getFile().getInt("Settings.EnchantmentOptions.Lightning-Sound-Range", 160);
										try {
											loc.getWorld().playSound(loc, ce.getSound("ENTITY_LIGHTNING_BOLT_IMPACT", "ENTITY_LIGHTNING_IMPACT"), (float) lightningSoundRange / 16f, 1);
										}catch(Exception ignore) {
										}
										if(SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) {
											NoCheatPlusSupport.exemptPlayer(player);
										}
										if(SupportedPlugins.SPARTAN.isPluginLoaded()) {
											SpartanSupport.cancelNoSwing(player);
										}
										if(SupportedPlugins.AAC.isPluginLoaded()) {
											AACSupport.exemptPlayer(player);
										}
										for(LivingEntity en : Methods.getNearbyLivingEntities(loc, 2D, player)) {
											EntityDamageByEntityEvent damageByEntityEvent = new EntityDamageByEntityEvent(player, en, DamageCause.CUSTOM, 5D);
											ce.addIgnoredEvent(damageByEntityEvent);
											ce.addIgnoredUUID(player.getUniqueId());
											Bukkit.getPluginManager().callEvent(damageByEntityEvent);
											if(!damageByEntityEvent.isCancelled()) {
												if(Support.allowsPVP(en.getLocation())) {
													if(!Support.isFriendly(player, en)) {
														en.damage(5D);
													}
												}
											}
											ce.removeIgnoredEvent(damageByEntityEvent);
											ce.removeIgnoredUUID(player.getUniqueId());
										}
										damager.damage(5D);
										if(SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) {
											NoCheatPlusSupport.unexemptPlayer(player);
										}
										if(SupportedPlugins.AAC.isPluginLoaded()) {
											AACSupport.unexemptPlayer(player);
										}
									}
								}
							}
						}
					}
				}
				if(damager instanceof Player) {
					for(ItemStack armor : damager.getEquipment().getArmorContents()) {
						if(ce.hasEnchantments(armor)) {
							if(ce.hasEnchantment(armor, CEnchantments.LEADERSHIP)) {
								if(CEnchantments.LEADERSHIP.isActivated()) {
									if(CEnchantments.LEADERSHIP.chanceSuccessful(armor)) {
										if(SupportedPlugins.FACTIONS_MASSIVE_CRAFT.isPluginLoaded() || SupportedPlugins.FACTIONS_UUID.isPluginLoaded()) {
											int radius = 4 + ce.getLevel(armor, CEnchantments.LEADERSHIP);
											int players = 0;
											for(Entity en : damager.getNearbyEntities(radius, radius, radius)) {
												if(en instanceof Player) {
													Player o = (Player) en;
													if(Support.isFriendly(damager, o)) {
														players++;
													}
												}
											}
											if(players > 0) {
												EnchantmentUseEvent event = new EnchantmentUseEvent((Player) damager, CEnchantments.LEADERSHIP.getEnchantment(), armor);
												Bukkit.getPluginManager().callEvent(event);
												if(!event.isCancelled()) {
													e.setDamage(e.getDamage() + (players / 2));
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
	public void onAura(AuraActiveEvent e) {
		Player player = e.getPlayer();
		Player other = e.getOther();
		if(!player.canSee(other) || !other.canSee(player)) return;
		if(Support.isVanished(player) || Support.isVanished(other)) return;
		CEnchantments enchant = e.getEnchantment();
		int power = e.getPower();
		if(!Methods.hasPermission(other, "bypass.aura", false)) {
			if(Support.allowsPVP(other.getLocation())) {
				if(!Support.isFriendly(player, other)) {
					Calendar cal = Calendar.getInstance();
					HashMap<CEnchantments, Calendar> eff = new HashMap<>();
					if(timer.containsKey(other)) {
						eff = timer.get(other);
					}
					switch(enchant) {
						case BLIZZARD:
							if(CEnchantments.BLIZZARD.isActivated()) {
								other.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5 * 20, power - 1));
							}
							break;
						case INTIMIDATE:
							if(CEnchantments.INTIMIDATE.isActivated()) {
								other.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 3 * 20, power - 1));
							}
							break;
						case ACIDRAIN:
							if(CEnchantments.ACIDRAIN.isActivated()) {
								if(!timer.containsKey(other) || (timer.containsKey(other) && !timer.get(other).containsKey(enchant)) || (timer.containsKey(other) && timer.get(other).containsKey(enchant) && cal.after(timer.get(other).get(enchant)))) {
									if(CEnchantments.ACIDRAIN.chanceSuccessful()) {
										other.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 4 * 20, 1));
										int time = 35 - (power * 5);
										cal.add(Calendar.SECOND, time > 0 ? time : 5);
										eff.put(enchant, cal);
									}
								}
							}
							break;
						case SANDSTORM:
							if(CEnchantments.SANDSTORM.isActivated()) {
								if(!timer.containsKey(other) || (timer.containsKey(other) && !timer.get(other).containsKey(enchant)) || (timer.containsKey(other) && timer.get(other).containsKey(enchant) && cal.after(timer.get(other).get(enchant)))) {
									if(CEnchantments.SANDSTORM.chanceSuccessful()) {
										other.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 0));
										int time = 35 - (power * 5);
										cal.add(Calendar.SECOND, time > 0 ? time : 5);
										eff.put(enchant, cal);
									}
								}
							}
							break;
						case RADIANT:
							if(CEnchantments.RADIANT.isActivated()) {
								if(!timer.containsKey(other) || (timer.containsKey(other) && !timer.get(other).containsKey(enchant)) || (timer.containsKey(other) && timer.get(other).containsKey(enchant) && cal.after(timer.get(other).get(enchant)))) {
									if(CEnchantments.RADIANT.chanceSuccessful()) {
										other.setFireTicks(5 * 20);
										int time = 20 - (power * 5);
										cal.add(Calendar.SECOND, Math.max(time, 0));
										eff.put(enchant, cal);
									}
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
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMovement(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		int X = e.getFrom().getBlockX();
		int Y = e.getFrom().getBlockY();
		int Z = e.getFrom().getBlockZ();
		int x = e.getTo().getBlockX();
		int y = e.getTo().getBlockY();
		int z = e.getTo().getBlockZ();
		if(x != X || y != Y | z != Z) {
			for(ItemStack armor : player.getEquipment().getArmorContents()) {
				if(ce.hasEnchantments(armor)) {
					if(ce.hasEnchantment(armor, CEnchantments.NURSERY)) {
						if(CEnchantments.NURSERY.isActivated()) {
							int heal = 1;
							if(CEnchantments.NURSERY.chanceSuccessful(armor)) {
								if(player.getMaxHealth() > player.getHealth()) {
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.NURSERY.getEnchantment(), armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										if(player.getHealth() + heal <= player.getMaxHealth()) {
											player.setHealth(player.getHealth() + heal);
										}
										if(player.getHealth() + heal >= player.getMaxHealth()) {
											player.setHealth(player.getMaxHealth());
										}
									}
								}
							}
						}
					}
					if(ce.hasEnchantment(armor, CEnchantments.IMPLANTS)) {
						if(CEnchantments.IMPLANTS.isActivated()) {
							int food = 1;
							if(CEnchantments.IMPLANTS.chanceSuccessful(armor)) {
								if(player.getFoodLevel() < 20) {
									EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.IMPLANTS.getEnchantment(), armor);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										if(SupportedPlugins.SPARTAN.isPluginLoaded()) {
											SpartanSupport.cancelFastEat(player);
										}
										if(player.getFoodLevel() + food <= 20) {
											player.setFoodLevel(player.getFoodLevel() + food);
										}
										if(player.getFoodLevel() + food >= 20) {
											player.setFoodLevel(20);
										}
									}
								}
							}
						}
					}
					if(ce.hasEnchantment(armor, CEnchantments.ANGEL)) {
						if(CEnchantments.ANGEL.isActivated()) {
							if(SupportedPlugins.FACTIONS_MASSIVE_CRAFT.isPluginLoaded() || SupportedPlugins.FACTIONS_UUID.isPluginLoaded()) {
								int radius = 4 + ce.getLevel(armor, CEnchantments.ANGEL);
								for(Entity en : player.getNearbyEntities(radius, radius, radius)) {
									if(en instanceof Player) {
										Player o = (Player) en;
										if(Support.isFriendly(player, o)) {
											AngelUseEvent event = new AngelUseEvent(player, armor);
											Bukkit.getPluginManager().callEvent(event);
											if(!event.isCancelled()) {
												o.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3 * 20, 0));
											}
										}
									}
								}
							}
						}
					}
					if(ce.hasEnchantment(armor, CEnchantments.HELLFORGED)) {
						if(CEnchantments.HELLFORGED.isActivated()) {
							if(armor.getDurability() > 0) {
								if(CEnchantments.HELLFORGED.chanceSuccessful(armor)) {
									int durribility = armor.getDurability() - ce.getLevel(armor, CEnchantments.HELLFORGED);
									if(armor.getDurability() > 0) {
										HellForgedUseEvent event = new HellForgedUseEvent(player, armor);
										Bukkit.getPluginManager().callEvent(event);
										if(!event.isCancelled()) {
											if(durribility > 0) {
												armor.setDurability((short) durribility);
											}else {
												armor.setDurability((short) 0);
											}
										}
									}
								}
							}
						}
					}
				}
			}
			for(ItemStack item : player.getInventory().getContents()) {
				if(ce.hasEnchantments(item)) {
					if(ce.hasEnchantment(item, CEnchantments.HELLFORGED)) {
						if(CEnchantments.HELLFORGED.isActivated()) {
							if(item.getDurability() > 0) {
								if(CEnchantments.HELLFORGED.chanceSuccessful(item)) {
									int durribility = item.getDurability() - ce.getLevel(item, CEnchantments.HELLFORGED);
									if(item.getDurability() > 0) {
										HellForgedUseEvent event = new HellForgedUseEvent(player, item);
										Bukkit.getPluginManager().callEvent(event);
										if(!event.isCancelled()) {
											if(durribility > 0) {
												item.setDurability((short) durribility);
											}else {
												item.setDurability((short) 0);
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
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		if(!(player.getKiller() instanceof Player)) return;
		Player killer = player.getKiller();
		if(!Support.allowsPVP(player.getLocation())) return;
		for(ItemStack item : player.getEquipment().getArmorContents()) {
			if(ce.hasEnchantments(item)) {
				if(ce.hasEnchantment(item, CEnchantments.SELFDESTRUCT.getEnchantment())) {
					if(CEnchantments.SELFDESTRUCT.isActivated()) {
						EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.SELFDESTRUCT.getEnchantment(), item);
						Bukkit.getPluginManager().callEvent(event);
						if(!event.isCancelled()) {
							Methods.explode(player);
							List<ItemStack> items = new ArrayList<>();
							for(ItemStack drop : e.getDrops()) {
								if(drop != null) {
									if(ProtectionCrystal.isProtected(drop)) {
										if(ProtectionCrystal.isProtectionSuccessful(player)) {
											items.add(drop);
										}
									}
								}
							}
							e.getDrops().clear();
							e.getDrops().addAll(items);
						}
					}
				}
			}
		}
		for(ItemStack item : killer.getEquipment().getArmorContents()) {
			if(ce.hasEnchantments(item)) {
				if(ce.hasEnchantment(item, CEnchantments.RECOVER)) {
					if(CEnchantments.RECOVER.isActivated()) {
						EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.RECOVER.getEnchantment(), item);
						Bukkit.getPluginManager().callEvent(event);
						if(!event.isCancelled()) {
							killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 8 * 20, 2));
							killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 1));
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerFallDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			if(e.getCause() == DamageCause.FALL) {
				if(fall.contains(e.getEntity())) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onAllyTarget(EntityTargetEvent e) {
		if(e.getEntity() instanceof LivingEntity) {
			LivingEntity en = (LivingEntity) e.getEntity();
			for(Player player : mobs.keySet()) {
				if(mobs.get(player).contains(en)) {
					if(player != null && e.getTarget() != null) {
						if(player.getName() != null && e.getTarget().getName() != null) {
							if(player.getName().equals(e.getTarget().getName())) {
								e.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onAllySpawn(EntityDamageByEntityEvent e) {
		if(!e.isCancelled() && !ce.isIgnoredEvent(e)) {
			if(e.getEntity() instanceof Player && e.getDamager() instanceof LivingEntity) {// Player gets attacked
				Player player = (Player) e.getEntity();
				LivingEntity en = (LivingEntity) e.getDamager();
				if(!mobs.containsKey(player)) {
					for(ItemStack item : player.getEquipment().getArmorContents()) {
						if(ce.hasEnchantments(item)) {// Spawn allies when getting attacked
							if(!mobTimer.containsKey(player) || (mobTimer.containsKey(player) && Calendar.getInstance().after(mobTimer.get(player)))) {
								if(ce.hasEnchantment(item, CEnchantments.TAMER)) {
									if(CEnchantments.TAMER.isActivated()) {
										if(!mobs.containsKey(player)) {
											int power = ce.getLevel(item, CEnchantments.TAMER);
											spawnAllies(player, en, EntityType.WOLF, power);
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.GUARDS)) {
									if(CEnchantments.GUARDS.isActivated()) {
										if(!mobs.containsKey(player)) {
											int power = ce.getLevel(item, CEnchantments.GUARDS);
											spawnAllies(player, en, EntityType.IRON_GOLEM, power);
										}
									}
								}
								if(en instanceof Player) {
									if(ce.hasEnchantment(item, CEnchantments.NECROMANCER)) {
										if(CEnchantments.NECROMANCER.isActivated()) {
											if(!mobs.containsKey(player)) {
												int power = ce.getLevel(item, CEnchantments.NECROMANCER);
												spawnAllies(player, en, EntityType.ZOMBIE, power * 2);
											}
										}
									}
									if(ce.hasEnchantment(item, CEnchantments.INFESTATION)) {
										if(CEnchantments.INFESTATION.isActivated()) {
											if(!mobs.containsKey(player)) {
												int power = ce.getLevel(item, CEnchantments.INFESTATION);
												spawnAllies(player, en, EntityType.ENDERMITE, power * 3);
												spawnAllies(player, en, EntityType.SILVERFISH, power * 3);
											}
										}
									}
								}
							}
						}
					}
				}else {
					attackEnemy(player, en);
				}
			}
			if(e.getEntity() instanceof LivingEntity && e.getDamager() instanceof Player) {// Player attacks
				Player player = (Player) e.getDamager();
				LivingEntity en = (LivingEntity) e.getEntity();
				if(mobs.containsKey(player)) {// If player hurts ally
					if(mobs.get(player).contains(en)) {
						e.setCancelled(true);
						return;
					}
				}
				if(!mobs.containsKey(player)) {
					for(ItemStack item : player.getEquipment().getArmorContents()) {
						if(ce.hasEnchantments(item)) {// Spawn allies when attacking
							if(!mobTimer.containsKey(player) || (mobTimer.containsKey(player) && Calendar.getInstance().after(mobTimer.get(player)))) {
								if(ce.hasEnchantment(item, CEnchantments.TAMER)) {
									if(CEnchantments.INFESTATION.isActivated()) {
										if(!mobs.containsKey(player)) {
											int power = ce.getLevel(item, CEnchantments.TAMER);
											spawnAllies(player, en, EntityType.WOLF, power);
										}
									}
								}
								if(ce.hasEnchantment(item, CEnchantments.GUARDS)) {
									if(CEnchantments.INFESTATION.isActivated()) {
										if(!mobs.containsKey(player)) {
											int power = ce.getLevel(item, CEnchantments.GUARDS);
											spawnAllies(player, en, EntityType.IRON_GOLEM, power);
										}
									}
								}
								if(en instanceof Player) {
									if(ce.hasEnchantment(item, CEnchantments.NECROMANCER)) {
										if(CEnchantments.INFESTATION.isActivated()) {
											if(!mobs.containsKey(player)) {
												int power = ce.getLevel(item, CEnchantments.NECROMANCER);
												spawnAllies(player, en, EntityType.ZOMBIE, power * 2);
											}
										}
									}
									if(ce.hasEnchantment(item, CEnchantments.INFESTATION)) {
										if(CEnchantments.INFESTATION.isActivated()) {
											if(!mobs.containsKey(player)) {
												int power = ce.getLevel(item, CEnchantments.INFESTATION);
												spawnAllies(player, en, EntityType.ENDERMITE, power * 3);
												spawnAllies(player, en, EntityType.SILVERFISH, power * 3);
											}
										}
									}
								}
							}
						}
					}
				}else {
					attackEnemy(player, en);
				}
			}
		}
	}
	
	@EventHandler
	public void onAllyDeath(EntityDeathEvent e) {
		for(Player player : mobs.keySet()) {
			if(mobs.get(player).contains(e.getEntity())) {
				e.setDroppedExp(0);
				e.getDrops().clear();
			}
		}
	}
	
	@EventHandler
	public void onAllyDespawn(ChunkUnloadEvent e) {
		if(e.getChunk().getEntities().length > 0) {
			for(Entity en : e.getChunk().getEntities()) {
				if(en instanceof LivingEntity) {
					LivingEntity En = (LivingEntity) en;
					for(Player player : mobs.keySet()) {
						if(mobs.get(player).contains(En)) {
							mobs.get(player).remove(En);
							En.remove();
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if(mobs.containsKey(player)) {
			for(LivingEntity en : mobs.get(player)) {
				en.remove();
			}
			mobs.remove(player);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void spawnAllies(final Player player, LivingEntity enemy, EntityType mob, Integer amount) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 2);
		mobTimer.put(player, cal);
		for(int i = 0; i < amount; i++) {
			LivingEntity en = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), mob);
			switch(mob) {
				case WOLF:
					en.setMaxHealth(16);
					en.setHealth(16);
					Support.noStack(en);
					break;
				case IRON_GOLEM:
					en.setMaxHealth(200);
					en.setHealth(200);
					Support.noStack(en);
					en.setCanPickupItems(false);
					break;
				case ZOMBIE:
					en.setMaxHealth(45);
					en.setHealth(45);
					Support.noStack(en);
					en.setCanPickupItems(false);
					break;
				case ENDERMITE:
				case SILVERFISH:
					en.setMaxHealth(10);
					en.setHealth(10);
					Support.noStack(en);
					break;
				default:
					break;
			}
			en.setCustomName(Methods.color("&6" + player.getName() + "'s " + en.getName()));
			en.setCustomNameVisible(true);
			if(!mobs.containsKey(player)) {
				List<LivingEntity> E = new ArrayList<>();
				E.add(en);
				mobs.put(player, E);
			}else {
				mobs.get(player).add(en);
			}
		}
		attackEnemy(player, enemy);
		Bukkit.getScheduler().runTaskLater(ce.getPlugin(), () -> {
			if(mobs.containsKey(player)) {
				for(LivingEntity en : mobs.get(player)) {
					en.remove();
				}
				mobs.remove(player);
			}
		}, 60 * 20);
	}
	
	private void attackEnemy(Player player, LivingEntity enemy) {
		for(LivingEntity ally : mobs.get(player)) {
			switch(ally.getType()) {
				case IRON_GOLEM:
					IronGolem iron = (IronGolem) ally;
					iron.setTarget(enemy);
					break;
				case WOLF:
					Wolf wolf = (Wolf) ally;
					wolf.setTarget(enemy);
					break;
				case ZOMBIE:
					Zombie zom = (Zombie) ally;
					zom.setTarget(enemy);
					break;
				case ENDERMITE:
					Endermite mite = (Endermite) ally;
					mite.setTarget(enemy);
					break;
				case SILVERFISH:
					Silverfish sfish = (Silverfish) ally;
					sfish.setTarget(enemy);
					break;
				default:
					break;
			}
		}
	}
	
}