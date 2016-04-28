package me.BadBones69.CrazyEnchantments.Enchantments.Axes;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Cursed implements Listener{
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(!Api.allowsPVP(e.getEntity()))return;
		if(!Api.allowsPVP(e.getDamager()))return;
		if(!Api.isEnchantmentEnabled("Cursed"))return;
		if(e.isCancelled())return;
		if(e.getEntity() instanceof LivingEntity){
			if(e.getDamager() instanceof Player){
				Player damager = (Player) e.getDamager();
				LivingEntity en = (LivingEntity) e.getEntity();
				if(Api.getItemInHand(damager).hasItemMeta()){
					if(!Api.getItemInHand(damager).getItemMeta().hasLore())return;
					if(!e.getEntity().isDead()){
						if(!Api.allowsPVP(e.getEntity()))return;
						for(String lore : Api.getItemInHand(damager).getItemMeta().getLore()){
							if(lore.contains(Api.getEnchName("Cursed"))){
								if(Api.randomPicker(10)){
									en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, Api.getPower(lore, Api.getEnchName("Cursed"))+9*20, 1));
								}
							}
						}
					}
				}
			}
		}
	}
}