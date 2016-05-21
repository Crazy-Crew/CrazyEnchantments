package me.BadBones69.CrazyEnchantments.Enchantments.Armor;

import java.util.Random;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Enlightened implements Listener{
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(!Api.isEnchantmentEnabled("Enlightened"))return;
		if(Api.isFriendly(e.getDamager(), e.getEntity()))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if(!Api.allowsPVP(e.getDamager()))return;
		if(e.getEntity() instanceof LivingEntity){
			if(e.getEntity() instanceof Player){
				Player player = (Player) e.getEntity();
				for(ItemStack armor : player.getEquipment().getArmorContents()){
					if(armor==null)return;
					if(armor.hasItemMeta()){
						if(!armor.getItemMeta().hasLore())return;
						for(String lore : armor.getItemMeta().getLore()){
							if(lore.contains(Api.getEnchName("Enlightened"))){
								Random number = new Random();
								Random num = new Random();
								double heal = Api.getPower(lore, Api.getEnchName("Enlightened"))+num.nextInt(1);
								int chance;
								for(int counter = 1; counter<=1; counter++){
									chance = 1 + number.nextInt(10);
									if(chance == 1){
										if(player.getHealth()+heal<player.getMaxHealth())player.setHealth(player.getHealth()+heal);
										if(player.getHealth()+heal>=player.getMaxHealth())player.setHealth(player.getMaxHealth());
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