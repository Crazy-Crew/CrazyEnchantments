package me.BadBones69.CrazyEnchantments.Enchantments.Armor;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class Nursery implements Listener{
	@EventHandler
	public void onMovment(PlayerMoveEvent e){
		Player player = e.getPlayer();
		int X = e.getFrom().getBlockX();
		int Y = e.getFrom().getBlockY();
		int Z = e.getFrom().getBlockZ();
		int x = player.getLocation().getBlockX();
		int y = player.getLocation().getBlockY();
		int z = player.getLocation().getBlockZ();
		if(x!=X||y!=Y|z!=Z){
			for(ItemStack i : player.getEquipment().getArmorContents()){
				if(i.hasItemMeta()){
					if(i.getItemMeta().hasLore()){
						if(i.getItemMeta().getLore().contains("Nursery I")){
							Random number = new Random();
							int chance;
							for(int counter = 1; counter<=1; counter++){
								chance = 1 + number.nextInt(24);
								if(chance == 1){
									if(player.getHealth()+3<=player.getMaxHealth()){
										player.setHealth(player.getHealth()+3);
									}
									if(player.getHealth()+3>=player.getMaxHealth()){
										player.setHealth(player.getMaxHealth());
									}
									if(player.getSaturation()+3<=20){
										player.setSaturation(player.getSaturation()+3);
									}
									if(player.getSaturation()+3>=20){
										player.setSaturation(20);
									}
								}
							}
						}
						if(i.getItemMeta().getLore().contains("Nursery II")){
							Random number = new Random();
							int chance;
							for(int counter = 1; counter<=1; counter++){
								chance = 1 + number.nextInt(24);
								if(chance == 1){
									if(player.getHealth()+5<=player.getMaxHealth()){
										player.setHealth(player.getHealth()+5);
									}
									if(player.getHealth()+5>=player.getMaxHealth()){
										player.setHealth(player.getMaxHealth());
									}
									if(player.getSaturation()+5<=20){
										player.setSaturation(player.getSaturation()+5);
									}
									if(player.getSaturation()+5>=20){
										player.setSaturation(20);
									}
								}
							}
						}
						if(i.getItemMeta().getLore().contains("Nursery III")){
							Random number = new Random();
							int chance;
							for(int counter = 1; counter<=1; counter++){
								chance = 1 + number.nextInt(24);
								if(chance == 1){
									if(player.getHealth()+7<=player.getMaxHealth()){
										player.setHealth(player.getHealth()+7);
									}
									if(player.getHealth()+7>=player.getMaxHealth()){
										player.setHealth(player.getMaxHealth());
									}
									if(player.getSaturation()+7<=20){
										player.setSaturation(player.getSaturation()+7);
									}
									if(player.getSaturation()+7>=20){
										player.setSaturation(20);
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
