package me.BadBones69.CrazyEnchantments.Enchantments.Bow;

import java.util.ArrayList;
import java.util.Random;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class Boom implements Listener{
	ArrayList<Projectile> arrow1 = new ArrayList<Projectile>();
	ArrayList<Projectile> arrow2 = new ArrayList<Projectile>();
	ArrayList<Projectile> arrow3 = new ArrayList<Projectile>();
	@EventHandler
	public void onBowShoot(EntityShootBowEvent e){
		if (e.getBow().hasItemMeta()) {
			if (e.getBow().getItemMeta().getLore().contains(Api.color("&7Boom I"))) {
				arrow1.add((Projectile) e.getProjectile());
			}
			if (e.getBow().getItemMeta().getLore().contains(Api.color("&7Boom II"))) {
				arrow2.add((Projectile) e.getProjectile());
			}
			if (e.getBow().getItemMeta().getLore().contains(Api.color("&7Boom III"))) {
				arrow3.add((Projectile) e.getProjectile());
			}
		}
	}
	@EventHandler
	public void onland(ProjectileHitEvent e) {
		if(arrow1.contains(e.getEntity())){
			Random number = new Random();
			int chance;
			for(int counter = 1; counter<=1; counter++){
				chance = 1 + number.nextInt(10);
				if(chance == 1){
					e.getEntity().getWorld().spawn(e.getEntity().getLocation(), TNTPrimed.class);
					arrow1.remove(e.getEntity());
					e.getEntity().remove();
				}
			}
		}
		if(arrow2.contains(e.getEntity())){
			Random number = new Random();
			int chance;
			for(int counter = 1; counter<=1; counter++){
				chance = 1 + number.nextInt(5);
				if(chance == 1){
					e.getEntity().getWorld().spawn(e.getEntity().getLocation(), TNTPrimed.class);
					arrow2.remove(e.getEntity());
					e.getEntity().remove();
				}
			}
		}
		if(arrow3.contains(e.getEntity())){
			Random number = new Random();
			int chance;
			for(int counter = 1; counter<=1; counter++){
				chance = 1 + number.nextInt(3);
				if(chance == 1){
					e.getEntity().getWorld().spawn(e.getEntity().getLocation(), TNTPrimed.class);
					arrow3.remove(e.getEntity());
					e.getEntity().remove();
				}
			}
		}
	}
}
