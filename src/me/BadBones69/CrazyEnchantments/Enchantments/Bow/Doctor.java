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
	HashMap<Projectile, Entity> P = new HashMap<Projectile, Entity>();
	@EventHandler
	public void onBowShoot(EntityShootBowEvent e){
		if(!Api.isEnchantmentEnabled("Doctor"))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if (e.getBow().hasItemMeta()) {
			if(!e.getBow().getItemMeta().hasLore())return;
			for(String lore : e.getBow().getItemMeta().getLore()){
				if(lore.contains(Api.getEnchName("Doctor"))){
					Arrow.put((Projectile) e.getProjectile(), Api.getPower(lore, Api.getEnchName("Doctor")));
					P.put((Projectile) e.getProjectile(), e.getEntity());
				}
			}
		}
	}
	@EventHandler
 	public void onArrowLand(EntityDamageByEntityEvent e){
		if(!Api.isFriendly(P.get(e.getDamager()), e.getEntity()))return;
		if(!Api.isEnchantmentEnabled("Doctor"))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if(!Api.allowsPVP(e.getDamager()))return;
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