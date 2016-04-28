package me.BadBones69.CrazyEnchantments.Enchantments.Axes;

import java.util.Random;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Rekt implements Listener{
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(!Api.isEnchantmentEnabled("Rekt"))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if(!Api.allowsPVP(e.getDamager()))return;
		if(e.isCancelled())return;
		double damage = e.getDamage()*2;
		if(e.getEntity() instanceof LivingEntity){
			if(e.getDamager() instanceof Player){
				Player damager = (Player) e.getDamager();
				if(Api.getItemInHand(damager).hasItemMeta()){
					if(!Api.getItemInHand(damager).getItemMeta().hasLore())return;
					if(!e.getEntity().isDead()){
						if(!Api.allowsPVP(e.getEntity()))return;
						for(String lore : Api.getItemInHand(damager).getItemMeta().getLore()){
							if(lore.contains(Api.getEnchName("Rekt"))){
								Random number = new Random();
								int chance;
								for(int counter = 1; counter<=1; counter++){
									chance = 1 + number.nextInt(20-Api.getPower(lore, Api.getEnchName("Rekt")));
									if(chance == 1){
										e.setDamage(damage);
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