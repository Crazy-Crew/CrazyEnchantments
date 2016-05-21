package me.BadBones69.CrazyEnchantments.Enchantments.Swords;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class SkillSwipe implements Listener{
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(Api.isFriendly(e.getDamager(), e.getEntity()))return;
		if(!Api.isEnchantmentEnabled("SkillSwipe"))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if(!Api.allowsPVP(e.getDamager()))return;
		if(e.getEntity() instanceof LivingEntity){
			if(e.getDamager() instanceof Player){
				Player damager = (Player) e.getDamager();
				LivingEntity en = (LivingEntity) e.getEntity();
				if(en instanceof Player){
					Player player = (Player) en;
					if(Api.getItemInHand(damager).hasItemMeta()){
						if(!Api.getItemInHand(damager).getItemMeta().hasLore())return;
						if(!e.getEntity().isDead()){
							for(String lore : Api.getItemInHand(damager).getItemMeta().getLore()){
								if(lore.contains(Api.getEnchName("SkillSwipe"))){
									int amount = 4+Api.getPower(lore, Api.getEnchName("SkillSwipe"));
									if(player.getTotalExperience()>=amount){
										Api.takeTotalXP(player, amount);
										Api.takeTotalXP(damager, -amount);
										return;
									}
									if(player.getTotalExperience()<amount){
										player.setTotalExperience(0);
										Api.takeTotalXP(damager, -amount);
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