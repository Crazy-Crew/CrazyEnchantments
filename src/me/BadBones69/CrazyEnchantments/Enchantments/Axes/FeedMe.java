package me.BadBones69.CrazyEnchantments.Enchantments.Axes;

import java.util.Random;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class FeedMe implements Listener{
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
						if(damager.getItemInHand().getItemMeta().getLore().contains(Api.color("&7FeedMe I"))){
							Random number = new Random();
							int chance;
							for(int counter = 1; counter<=1; counter++){
								chance = 1 + number.nextInt(8);
								if(chance == 1){
									if(damager.getSaturation()<20){
										damager.setSaturation(damager.getSaturation()+2);
									}
									if(damager.getSaturation()+2>20){
										damager.setSaturation(20);
									}
								}
							}
						}
						if(damager.getItemInHand().getItemMeta().getLore().contains(Api.color("&7FeedMe II"))){
							Random number = new Random();
							int chance;
							for(int counter = 1; counter<=1; counter++){
								chance = 1 + number.nextInt(6);
								if(chance == 1){
									if(damager.getSaturation()<20){
										damager.setSaturation(damager.getSaturation()+4);
									}
									if(damager.getSaturation()+4>20){
										damager.setSaturation(20);
									}
								}
							}
						}
						if(damager.getItemInHand().getItemMeta().getLore().contains(Api.color("&7FeedMe III"))){
							Random number = new Random();
							int chance;
							for(int counter = 1; counter<=1; counter++){
								chance = 1 + number.nextInt(4);
								if(chance == 1){
									if(damager.getSaturation()<20){
										damager.setSaturation(damager.getSaturation()+6);
									}
									if(damager.getSaturation()+6>20){
										damager.setSaturation(20);
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
