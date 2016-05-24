package me.BadBones69.CrazyEnchantments.Enchantments;

import java.util.ArrayList;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.BadBones69.CrazyEnchantments.Api;

public class Axes implements Listener{
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(!Api.allowsPVP(e.getEntity()))return;
		if(!Api.allowsPVP(e.getDamager()))return;
		if(Api.isFriendly(e.getDamager(), e.getEntity()))return;
		if(e.getEntity() instanceof LivingEntity){
			LivingEntity en = (LivingEntity) e.getEntity();
			if(e.getDamager() instanceof Player){
				Player damager = (Player) e.getDamager();
				if(Api.getItemInHand(damager).hasItemMeta()){
					if(Api.getItemInHand(damager).getItemMeta().hasLore()){
						if(!e.getEntity().isDead()){
							for(String lore : Api.getItemInHand(damager).getItemMeta().getLore()){
								if(lore.contains(Api.getEnchName("Berserk"))){
									if(Api.isEnchantmentEnabled("Berserk")){
										if(Api.randomPicker(12)){
											damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Api.getPower(lore, Api.getEnchName("Berserk"))+5*20, 1));
											damager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Api.getPower(lore, Api.getEnchName("Berserk"))+5*20, 0));
										}
									}
								}
								if(lore.contains(Api.getEnchName("Blessed"))){
									if(Api.isEnchantmentEnabled("Blessed")){
										if(Api.randomPicker((12-Api.getPower(lore, Api.getEnchName("Blessed"))))){
											removeBadPotions(damager);
										}
									}
								}
								if(lore.contains(Api.getEnchName("FeedMe"))){
									if(Api.isEnchantmentEnabled("FeedMe")){
										int food = 2*Api.getPower(lore, Api.getEnchName("FeedMe"));
										if(Api.randomPicker(10)){
											if(damager.getSaturation()+food<20){
												damager.setSaturation(damager.getSaturation()+food);
											}
											if(damager.getSaturation()+food>20){
												damager.setSaturation(20);
											}
										}
									}
								}
								if(lore.contains(Api.getEnchName("Rekt"))){
									if(Api.isEnchantmentEnabled("Rekt")){
										double damage = e.getDamage()*2;
										if(Api.randomPicker((20-Api.getPower(lore, Api.getEnchName("Rekt"))))){
											e.setDamage(damage);
										}
									}
								}
								if(lore.contains(Api.getEnchName("Cursed"))){
									if(Api.isEnchantmentEnabled("Cursed")){
										if(Api.randomPicker(10)){
											en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Api.getPower(lore, Api.getEnchName("Cursed"))+9*20, 1));
										}
									}
								}
								if(lore.contains(Api.getEnchName("Dizzy"))){
									if(Api.isEnchantmentEnabled("Dizzy")){
										if(Api.randomPicker(10)){
											en.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, Api.getPower(lore, Api.getEnchName("Dizzy"))+9*20, 0));
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
		if(Api.allowsPVP(e.getEntity()))return;
		if(e.getEntity().getKiller() instanceof Player){
			Player damager = (Player) e.getEntity().getKiller();
			Player player = e.getEntity();
			if(Api.getItemInHand(damager)!=null){
				ItemStack item = Api.getItemInHand(damager);
				if(item.hasItemMeta()){
					if(item.getItemMeta().hasLore()){
						for(String lore : item.getItemMeta().getLore()){
							if(lore.contains(Api.getEnchName("Decapitation"))){
								if(Api.isEnchantmentEnabled("Decapitation")){
									int power = Api.getPower(lore, Api.getEnchName("Decapitation"));
									if(Api.randomPicker(11-power)){
										ItemStack head = Api.makeItem("397:3", 1);
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
		}
	}
	void removeBadPotions(Player player){
		ArrayList<PotionEffectType> bad = new ArrayList<PotionEffectType>();
		bad.add(PotionEffectType.BLINDNESS);
		bad.add(PotionEffectType.CONFUSION);
		bad.add(PotionEffectType.HUNGER);
		bad.add(PotionEffectType.POISON);
		bad.add(PotionEffectType.SLOW);
		bad.add(PotionEffectType.SLOW_DIGGING);
		bad.add(PotionEffectType.WEAKNESS);
		bad.add(PotionEffectType.WITHER);
		for(PotionEffectType p : bad){
			if(player.hasPotionEffect(p)){
				player.removePotionEffect(p);
			}
		}
	}
}