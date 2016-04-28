package me.BadBones69.CrazyEnchantments.Enchantments.Armor;

import java.util.Random;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Molten implements Listener{
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(!Api.isEnchantmentEnabled("Molten"))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if(!Api.allowsPVP(e.getDamager()))return;
		if(e.isCancelled())return;
		if(e.getEntity() instanceof Player){
			if(e.getDamager() instanceof LivingEntity){
				Player player = (Player) e.getEntity();
				LivingEntity en = (LivingEntity) e.getDamager();
				for(ItemStack armor : player.getEquipment().getArmorContents()){
					if(armor.hasItemMeta()){
						if(!armor.getItemMeta().hasLore())return;
						for(String lore : armor.getItemMeta().getLore()){
							if(lore.contains(Api.getEnchName("Molten"))){
								Random number = new Random();
								int chance;
								for(int counter = 1; counter<=1; counter++){
									chance = 1 + number.nextInt(12);
									if(chance == 1){
										en.setFireTicks(Api.getPower(lore, Api.getEnchName("Molten"))+2*20);
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