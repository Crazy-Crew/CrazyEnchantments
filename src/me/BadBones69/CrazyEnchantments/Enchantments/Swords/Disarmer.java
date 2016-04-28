package me.BadBones69.CrazyEnchantments.Enchantments.Swords;

import java.util.Random;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Disarmer implements Listener{
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(!Api.isEnchantmentEnabled("Disarmer"))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if(!Api.allowsPVP(e.getDamager()))return;
		if(e.getEntity() instanceof Player){
			Player player = (Player) e.getEntity();
			if(e.getDamager() instanceof Player){
				Player damager = (Player) e.getDamager();
				if(Api.getItemInHand(damager).hasItemMeta()){
					for(String lore :Api.getItemInHand(damager).getItemMeta().getLore()){
						if(lore.contains(Api.getEnchName("Disarmer"))){
							Random number = new Random();
							Random num = new Random();
							int slot = 1 + num.nextInt(4);
							int chance;
							for(int counter = 1; counter<=1; counter++){
								chance = 1 + number.nextInt(13-(Api.getPower(lore, Api.getEnchName("Disarmer"))));
								if(chance == 1){
									if(slot == 1){
										if(player.getEquipment().getHelmet() != null){
											ItemStack item = player.getEquipment().getHelmet();
											player.getEquipment().setHelmet(null);
											player.getInventory().addItem(item);
										}
									}
									if(slot == 2){
										if(player.getEquipment().getChestplate() != null){
											ItemStack item = player.getEquipment().getChestplate();
											player.getEquipment().setChestplate(null);
											player.getInventory().addItem(item);
										}
									}
									if(slot == 3){
										if(player.getEquipment().getLeggings() != null){
											ItemStack item = player.getEquipment().getLeggings();
											player.getEquipment().setLeggings(null);
											player.getInventory().addItem(item);
										}
									}
									if(slot == 4){
										if(player.getEquipment().getBoots() != null){
											ItemStack item = player.getEquipment().getBoots();
											player.getEquipment().setBoots(null);
											player.getInventory().addItem(item);
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
}