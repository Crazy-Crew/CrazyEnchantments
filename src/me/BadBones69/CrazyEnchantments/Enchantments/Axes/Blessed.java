package me.BadBones69.CrazyEnchantments.Enchantments.Axes;

import java.util.ArrayList;
import java.util.Random;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

public class Blessed implements Listener{
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(e.isCancelled())return;
		if(e.getEntity() instanceof LivingEntity){
			if(e.getDamager() instanceof Player){
				Player damager = (Player) e.getDamager();
				if(damager.getItemInHand().hasItemMeta()){
					if(!damager.getItemInHand().getItemMeta().hasLore()){
						return;
					}
					if(!e.getEntity().isDead()){
						for(String lore : damager.getItemInHand().getItemMeta().getLore()){
							if(lore.contains(Api.getEnchName("Blessed"))){
								Random number = new Random();
								int chance;
								for(int counter = 1; counter<=1; counter++){
									chance = 1 + number.nextInt(20-Api.getPower(lore, Api.getEnchName("Blessed")));
									if(chance == 1){
										removeBadPotions(damager);
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