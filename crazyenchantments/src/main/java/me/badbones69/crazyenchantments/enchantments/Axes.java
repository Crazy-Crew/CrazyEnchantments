package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CEnchantments;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.multisupport.SpartanSupport;
import me.badbones69.crazyenchantments.multisupport.Support;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class Axes implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		if(e.isCancelled()) return;
		if(Support.isFriendly(e.getDamager(), e.getEntity())) return;
		if(e.getEntity() instanceof LivingEntity) {
			LivingEntity en = (LivingEntity) e.getEntity();
			if(e.getDamager() instanceof Player) {
				Player damager = (Player) e.getDamager();
				ItemStack item = Methods.getItemInHand(damager);
				if(!e.getEntity().isDead()) {
					if(Main.CE.hasEnchantments(item)) {
						if(Main.CE.hasEnchantment(item, CEnchantments.BERSERK)) {
							if(CEnchantments.BERSERK.isEnabled()) {
								if(Methods.randomPicker(12)) {
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.BERSERK, item);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, (Main.CE.getPower(item, CEnchantments.BERSERK) + 5) * 20, 1));
										damager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (Main.CE.getPower(item, CEnchantments.BERSERK) + 5) * 20, 0));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(item, CEnchantments.BLESSED)) {
							if(CEnchantments.BLESSED.isEnabled()) {
								if(Methods.randomPicker((12 - Main.CE.getPower(item, CEnchantments.BLESSED)))) {
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.BLESSED, item);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										removeBadPotions(damager);
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(item, CEnchantments.FEEDME)) {
							if(CEnchantments.FEEDME.isEnabled()) {
								int food = 2 * Main.CE.getPower(item, CEnchantments.FEEDME);
								if(Methods.randomPicker(10)) {
									if(damager.getFoodLevel() < 20) {
										EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.FEEDME, item);
										Bukkit.getPluginManager().callEvent(event);
										if(!event.isCancelled()) {
											if(SupportedPlugins.SPARTAN.isPluginLoaded()) {
												SpartanSupport.cancelFastEat(damager);
											}
											if(damager.getFoodLevel() + food < 20) {
												damager.setFoodLevel((int) (damager.getSaturation() + food));
											}
											if(damager.getFoodLevel() + food > 20) {
												damager.setFoodLevel(20);
											}
										}
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(item, CEnchantments.REKT)) {
							if(CEnchantments.REKT.isEnabled()) {
								double damage = e.getDamage() * 2;
								if(Methods.randomPicker((20 - Main.CE.getPower(item, CEnchantments.REKT)))) {
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.REKT, item);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										e.setDamage(damage);
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(item, CEnchantments.CURSED)) {
							if(CEnchantments.CURSED.isEnabled()) {
								if(Methods.randomPicker(10)) {
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.CURSED, item);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, (Main.CE.getPower(item, CEnchantments.CURSED) + 9) * 20, 1));
									}
								}
							}
						}
						if(Main.CE.hasEnchantment(item, CEnchantments.DIZZY)) {
							if(CEnchantments.DIZZY.isEnabled()) {
								if(Methods.randomPicker(10)) {
									EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.DIZZY, item);
									Bukkit.getPluginManager().callEvent(event);
									if(!event.isCancelled()) {
										en.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (Main.CE.getPower(item, CEnchantments.DIZZY) + 9) * 20, 0));
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
	public void onPlayerDamage(PlayerDeathEvent e) {
		if(!Support.allowsPVP(e.getEntity().getLocation())) return;
		if(e.getEntity().getKiller() instanceof Player) {
			Player damager = (Player) e.getEntity().getKiller();
			Player player = e.getEntity();
			ItemStack item = Methods.getItemInHand(damager);
			if(Main.CE.hasEnchantments(item)) {
				if(Main.CE.hasEnchantment(item, CEnchantments.DECAPITATION)) {
					if(CEnchantments.DECAPITATION.isEnabled()) {
						int power = Main.CE.getPower(item, CEnchantments.DECAPITATION);
						if(Methods.randomPicker(11 - power)) {
							EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.DECAPITATION, item);
							Bukkit.getPluginManager().callEvent(event);
							if(!event.isCancelled()) {
								ItemStack head = new ItemBuilder().setMaterial(Material.SKULL).setMetaData((short) 3).build();
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

	private void removeBadPotions(Player player) {
		ArrayList<PotionEffectType> bad = new ArrayList<PotionEffectType>();
		bad.add(PotionEffectType.BLINDNESS);
		bad.add(PotionEffectType.CONFUSION);
		bad.add(PotionEffectType.HUNGER);
		bad.add(PotionEffectType.POISON);
		bad.add(PotionEffectType.SLOW);
		bad.add(PotionEffectType.SLOW_DIGGING);
		bad.add(PotionEffectType.WEAKNESS);
		bad.add(PotionEffectType.WITHER);
		for(PotionEffectType p : bad) {
			if(player.hasPotionEffect(p)) {
				player.removePotionEffect(p);
			}
		}
	}
}