package me.BadBones69.CrazyEnchantments.Enchantments.Swords;

import java.util.Random;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class LifeSteal implements Listener{
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(!Api.isEnchantmentEnabled("LifeSteal"))return;
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
							if(lore.contains(Api.getEnchName("LifeSteal"))){
								Random number = new Random();
								int chance;
								int steal = Api.getPower(lore, Api.getEnchName("LifeSteal"));
								for(int counter = 1; counter<=1; counter++){
									chance = 1 + number.nextInt(5);
									if(chance == 1){
										if(damager.getHealth() + steal < damager.getMaxHealth()){
											damager.setHealth(damager.getHealth() + steal);
										}
										if(damager.getHealth() + steal >= damager.getMaxHealth()){
											damager.setHealth(damager.getMaxHealth());
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
