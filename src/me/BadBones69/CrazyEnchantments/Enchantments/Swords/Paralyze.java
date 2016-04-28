package me.BadBones69.CrazyEnchantments.Enchantments.Swords;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Paralyze implements Listener{
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(!Api.isEnchantmentEnabled("Paralyze"))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if(!Api.allowsPVP(e.getDamager()))return;
		if(e.isCancelled())return;
		if(e.getEntity() instanceof LivingEntity){
			if(e.getDamager() instanceof Player){
				Player damager = (Player) e.getDamager();
				LivingEntity en = (LivingEntity) e.getEntity();
				if(Api.getItemInHand(damager).hasItemMeta()){
					if(!Api.getItemInHand(damager).getItemMeta().hasLore())return;
					if(!e.getEntity().isDead()){
						for(String lore : Api.getItemInHand(damager).getItemMeta().getLore()){
							if(lore.equalsIgnoreCase(Api.getEnchName("Paralyze"))){
								if(Api.randomPicker(13-Api.getPower(lore, Api.getEnchName("Paralyze")))){
									en.getWorld().strikeLightning(en.getLocation());
									en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3*20, 2));
									en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 3*20, 2));
								}
							}
						}
					}
				}
			}
		}
	}
}