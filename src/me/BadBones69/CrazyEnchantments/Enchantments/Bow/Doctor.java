package me.BadBones69.CrazyEnchantments.Enchantments.Bow;

import java.util.ArrayList;

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
	ArrayList<Projectile> arrow1 = new ArrayList<Projectile>();
	ArrayList<Projectile> arrow2 = new ArrayList<Projectile>();
	ArrayList<Projectile> arrow3 = new ArrayList<Projectile>();
	@EventHandler
	public void onBowShoot(EntityShootBowEvent e){
		if (e.getBow().hasItemMeta()) {
			if (e.getBow().getItemMeta().getLore().contains(Api.color("&7Doctor I"))) {
				arrow1.add((Projectile) e.getProjectile());
			}
			if (e.getBow().getItemMeta().getLore().contains(Api.color("&7Doctor II"))) {
				arrow2.add((Projectile) e.getProjectile());
			}
			if (e.getBow().getItemMeta().getLore().contains(Api.color("&7Doctor III"))) {
				arrow3.add((Projectile) e.getProjectile());
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
				if(arrow1.contains(arrow)){
					if(l.getHealth() + 3 < l.getMaxHealth()){
						l.setHealth(l.getHealth() + 3);
					}
					if(l.getHealth() + 3 >= l.getMaxHealth()){
						l.setHealth(l.getMaxHealth());
					}
				}
				if(arrow2.contains(arrow)){
					if(l.getHealth() + 5 < l.getMaxHealth()){
						l.setHealth(l.getHealth() + 5);
					}
					if(l.getHealth() + 5 >= l.getMaxHealth()){
						l.setHealth(l.getMaxHealth());
					}
				}
				if(arrow3.contains(arrow)){
					if(l.getHealth() + 7 < l.getMaxHealth()){
						l.setHealth(l.getHealth() + 7);
					}
					if(l.getHealth() + 7 >= l.getMaxHealth()){
						l.setHealth(l.getMaxHealth());
					}
				}
			}
		}
		return;
	}
}