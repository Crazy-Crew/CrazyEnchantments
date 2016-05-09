package me.BadBones69.CrazyEnchantments.Enchantments.Swords;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Confusion implements Listener{
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(Api.isFriendly(e.getDamager(), e.getEntity()))return;
		if(!Api.isEnchantmentEnabled("Confusion"))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if(!Api.allowsPVP(e.getDamager()))return;
		if(e.isCancelled())return;
		if(e.getEntity() instanceof LivingEntity){
			if(e.getDamager() instanceof Player){
				Player damager = (Player) e.getDamager();
				LivingEntity en = (LivingEntity) e.getEntity();
				if(Api.getItemInHand(damager)!=null){
					ItemStack item = Api.getItemInHand(damager);
					if(item.hasItemMeta()){
						if(!item.getItemMeta().hasLore())return;
						if(!e.getEntity().isDead()){
							for(String lore : item.getItemMeta().getLore()){
								if(lore.contains(Api.getEnchName("Confusion"))){
									if(Api.randomPicker(7)){
										en.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 5+(Api.getPower(lore, Api.getEnchName("Confusion")))*20, 0));
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