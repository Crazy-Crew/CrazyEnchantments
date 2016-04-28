package me.BadBones69.CrazyEnchantments.Enchantments.Swords;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Nutrition implements Listener{
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(!Api.isEnchantmentEnabled("Nutrition"))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if(!Api.allowsPVP(e.getDamager()))return;
		if(e.isCancelled())return;
		if(e.getEntity() instanceof LivingEntity){
			if(e.getDamager() instanceof Player){
				Player damager = (Player) e.getDamager();
				if(Api.getItemInHand(damager).hasItemMeta()){
					if(!Api.getItemInHand(damager).getItemMeta().hasLore())return;
					if(!e.getEntity().isDead()){
						for(String lore : Api.getItemInHand(damager).getItemMeta().getLore()){
							if(lore.contains(Api.getEnchName("Nutrition"))){
								if(Api.randomPicker(8)){
									if(damager.getSaturation()+(2*Api.getPower(lore, Api.getEnchName("Nutrition")))<=20){
										damager.setSaturation(damager.getSaturation()+(2*Api.getPower(lore, Api.getEnchName("Nutrition"))));
										return;
									}
									if(damager.getSaturation()+(2*Api.getPower(lore, Api.getEnchName("Nutrition")))>=20){
										damager.setSaturation(20);
										return;
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