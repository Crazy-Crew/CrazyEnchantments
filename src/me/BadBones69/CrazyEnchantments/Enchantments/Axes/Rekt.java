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
		if(e.isCancelled())return;
		double damage = e.getDamage()*2;
		if(e.getEntity() instanceof LivingEntity){
			if(e.getDamager() instanceof Player){
				Player damager = (Player) e.getDamager();
				if(damager.getItemInHand().hasItemMeta()){
					if(!damager.getItemInHand().getItemMeta().hasLore())return;
					if(!e.getEntity().isDead()){
						if(damager.getItemInHand().getItemMeta().getLore().contains(Api.color("&7Rekt I"))){
							Random number = new Random();
							int chance;
							for(int counter = 1; counter<=1; counter++){
								chance = 1 + number.nextInt(7);
								if(chance == 1){
									e.setDamage(damage);
								}
							}
						}
						if(damager.getItemInHand().getItemMeta().getLore().contains(Api.color("&7Rekt II"))){
							Random number = new Random();
							int chance;
							for(int counter = 1; counter<=1; counter++){
								chance = 1 + number.nextInt(5);
								if(chance == 1){
									e.setDamage(damage);
								}
							}
						}
						if(damager.getItemInHand().getItemMeta().getLore().contains(Api.color("&7Rekt III"))){
							Random number = new Random();
							int chance;
							for(int counter = 1; counter<=1; counter++){
								chance = 1 + number.nextInt(3);
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