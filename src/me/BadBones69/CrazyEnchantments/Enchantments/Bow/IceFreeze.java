package me.BadBones69.CrazyEnchantments.Enchantments.Bow;

import java.util.ArrayList;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class IceFreeze implements Listener{
	ArrayList<Projectile> arrow = new ArrayList<Projectile>();
	HashMap<Projectile, Entity> P = new HashMap<Projectile, Entity>();
	@EventHandler
	public void onBowShoot(EntityShootBowEvent e){
		if(!Api.isEnchantmentEnabled("IceFreeze"))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if (e.getBow().hasItemMeta()) {
			if(!e.getBow().getItemMeta().hasLore())return;
			if (e.getBow().getItemMeta().getLore().contains(Api.getEnchName("IceFreeze"))) {
				arrow.add((Projectile) e.getProjectile());
				P.put((Projectile) e.getProjectile(), e.getEntity());
			}
		}
	}
	@EventHandler
 	public void onArrowLand(EntityDamageByEntityEvent e){
		if(Api.isFriendly(P.get(e.getDamager()), e.getEntity()))return;
		if(!Api.isEnchantmentEnabled("IceFreeze"))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if(!Api.allowsPVP(e.getDamager()))return;
		if(e.getDamager() instanceof Arrow){
			Projectile A = (Projectile) e.getDamager();
			if(arrow.contains(A)){
				if(Api.randomPicker(5)){
					LivingEntity en = (LivingEntity) e.getEntity();
					en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5*20, 1));
				}
			}
		}
	}
}