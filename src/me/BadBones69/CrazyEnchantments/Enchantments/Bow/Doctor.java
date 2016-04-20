package me.BadBones69.CrazyEnchantments.Enchantments.Bow;

import java.util.HashMap;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

public class Doctor implements Listener{
	HashMap<Projectile, Integer> Arrow = new HashMap<Projectile, Integer>();
	@EventHandler
	public void onBowShoot(EntityShootBowEvent e){
		if (e.getBow().hasItemMeta()) {
			for(String lore : e.getBow().getItemMeta().getLore()){
				if(lore.contains(Api.getEnchName("Doctor"))){
					Arrow.put((Projectile) e.getProjectile(), Api.getPower(lore, Api.getEnchName("Doctor")));
				}
			}
		}
	}
	@EventHandler
 	public void onArrowLand(EntityDamageByEntityEvent e){
		if(e.isCancelled())return;
		Entity en = e.getEntity();
		if(e.getDamager() instanceof Arrow){
			if(en instanceof LivingEntity){
				LivingEntity l = (LivingEntity) en;
				Projectile arrow = (Projectile) e.getDamager();
				if(Arrow.containsKey(arrow)){
					int heal = 2+Api.getPower(Arrow.get(arrow)+"", Api.getEnchName("Doctor"));
					if(l.getHealth() + heal < l.getMaxHealth()){
						l.setHealth(l.getHealth() + heal);
					}
					if(l.getHealth() + heal >= l.getMaxHealth()){
						l.setHealth(l.getMaxHealth());
					}
				}
			}
		}
		return;
	}
}