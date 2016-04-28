package me.BadBones69.CrazyEnchantments.Enchantments.Bow;

import java.util.ArrayList;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.Location;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class Lightning implements Listener{
	ArrayList<Projectile> arrow = new ArrayList<Projectile>();
	@EventHandler
	public void onBowShoot(EntityShootBowEvent e){
		if(!Api.isEnchantmentEnabled("Lightning"))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if (e.getBow().hasItemMeta()) {
			if(e.getBow().getItemMeta().hasLore()){
				if (e.getBow().getItemMeta().getLore().contains(Api.getEnchName("Lightning"))) {
					arrow.add((Projectile) e.getProjectile());
				}
			}
		}
	}
	@EventHandler
 	public void onArrowLand(ProjectileHitEvent e){
		if(!Api.isEnchantmentEnabled("Lightning"))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		Projectile proj = e.getEntity();
		Location loc = proj.getLocation();
		if(arrow.contains(proj)){
			if(Api.randomPicker(5)){
				loc.getWorld().strikeLightning(loc);
			}
		}
		return;
	}
}