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
		if(e.isCancelled())return;
		if(e.getEntity() instanceof LivingEntity){
			if(e.getEntity() instanceof Player){
				Player player = (Player) e.getEntity();
				for(ItemStack armor : player.getEquipment().getArmorContents()){
					if(armor.hasItemMeta()){
						if(!armor.getItemMeta().hasLore())return;
						if(armor.getItemMeta().getLore().contains(Api.color("&7Enlightened I"))){
							Random number = new Random();
							Random num = new Random();
							double heal = 1+num.nextInt(1);
							int chance;
							for(int counter = 1; counter<=1; counter++){
								chance = 1 + number.nextInt(10);
								if(chance == 1){
									if(player.getHealth()+heal<player.getMaxHealth())player.setHealth(player.getHealth()+heal);
									if(player.getHealth()+heal>=player.getMaxHealth())player.setHealth(player.getMaxHealth());
								}
							}
						}
						if(armor.getItemMeta().getLore().contains(Api.color("&7Enlightened II"))){
							Random number = new Random();
							Random num = new Random();
							double heal = 1+num.nextInt(2);
							int chance;
							for(int counter = 1; counter<=1; counter++){
								chance = 1 + number.nextInt(10);
								if(chance == 1){
									if(player.getHealth()+heal<player.getMaxHealth())player.setHealth(player.getHealth()+heal);
									if(player.getHealth()+heal>=player.getMaxHealth())player.setHealth(player.getMaxHealth());
								}
							}
						}
						if(armor.getItemMeta().getLore().contains(Api.color("&7Enlightened III"))){
							Random number = new Random();
							Random num = new Random();
							double heal = 1+num.nextInt(3);
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