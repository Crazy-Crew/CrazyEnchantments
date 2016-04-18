package me.BadBones69.CrazyEnchantments.Enchantments.Swords;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlowMo implements Listener{
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(e.isCancelled())return;
		if(e.getEntity() instanceof LivingEntity){
			if(e.getDamager() instanceof Player){
				Player damager = (Player) e.getDamager();
				LivingEntity en = (LivingEntity) e.getEntity();
				if(damager.getItemInHand().hasItemMeta()){
					if(!damager.getItemInHand().getItemMeta().hasLore())return;
					if(!e.getEntity().isDead()){
						if(damager.getItemInHand().getItemMeta().getLore().contains(Api.color("&7SlowMo I"))){
							en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5*20, 1));
						}
						if(damager.getItemInHand().getItemMeta().getLore().contains(Api.color("&7SlowMo II"))){
							en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 8*20, 2));
						}
						if(damager.getItemInHand().getItemMeta().getLore().contains(Api.color("&7SlowMo III"))){
							en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 11*20, 3));
						}
					}
				}
			}
		}
	}
}
