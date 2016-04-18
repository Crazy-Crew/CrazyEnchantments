package me.BadBones69.CrazyEnchantments.Enchantments.Armor;

import java.util.Random;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PainGiver implements Listener{
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(e.isCancelled())return;
		if(e.getEntity() instanceof Player){
			if(e.getDamager() instanceof LivingEntity){
				Player player = (Player) e.getEntity();
				LivingEntity en = (LivingEntity) e.getDamager();
				for(ItemStack armor : player.getEquipment().getArmorContents()){
					if(armor.hasItemMeta()){
						if(!armor.getItemMeta().hasLore())return;
						if(armor.getItemMeta().getLore().contains(Api.color("&7PainGiver I"))){
							Random number = new Random();
							int chance;
							for(int counter = 1; counter<=1; counter++){
								chance = 1 + number.nextInt(10);
								if(chance == 1){
									en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5*20, 1));
								}
							}
						}
						if(armor.getItemMeta().getLore().contains(Api.color("&7PainGiver II"))){
							Random number = new Random();
							int chance;
							for(int counter = 1; counter<=1; counter++){
								chance = 1 + number.nextInt(10);
								if(chance == 1){
									en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 6*20, 2));
								}
							}
						}
						if(armor.getItemMeta().getLore().contains(Api.color("&7PainGiver III"))){
							Random number = new Random();
							int chance;
							for(int counter = 1; counter<=1; counter++){
								chance = 1 + number.nextInt(10);
								if(chance == 1){
									en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 8*20, 3));
								}
							}
						}
					}
				}
			}
		}
	}
}