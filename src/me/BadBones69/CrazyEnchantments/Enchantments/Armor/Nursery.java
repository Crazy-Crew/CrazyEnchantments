package me.BadBones69.CrazyEnchantments.Enchantments.Armor;

import java.util.Random;

import me.BadBones69.CrazyEnchantments.Api;

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
				if(i!=null){
					if(i.hasItemMeta()){
						if(i.getItemMeta().hasLore()){
							for(String lore : i.getItemMeta().getLore()){
								if(lore.contains(Api.getEnchName("Nursery"))){
									Random number = new Random();
									int chance;
									int heal = 1+Api.getPower(lore, Api.getEnchName("Nursery"));
									for(int counter = 1; counter<=1; counter++){
										chance = 1 + number.nextInt(24);
										if(chance == 1){
											if(player.getHealth()+heal<=player.getMaxHealth()){
												player.setHealth(player.getHealth()+heal);
											}
											if(player.getHealth()+heal>=player.getMaxHealth()){
												player.setHealth(player.getMaxHealth());
											}
											if(player.getSaturation()+heal<=20){
												player.setSaturation(player.getSaturation()+heal);
											}
											if(player.getSaturation()+heal>=20){
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
	}
}
