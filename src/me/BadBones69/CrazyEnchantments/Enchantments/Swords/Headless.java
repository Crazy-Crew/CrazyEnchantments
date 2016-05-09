package me.BadBones69.CrazyEnchantments.Enchantments.Swords;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Headless implements Listener{
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerDamage(PlayerDeathEvent e){
		if(Api.isFriendly(e.getEntity().getKiller(), e.getEntity()))return;
		if(!Api.isEnchantmentEnabled("Headless"))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if(e.getEntity().getKiller() instanceof Player){
			Player damager = (Player) e.getEntity().getKiller();
			Player player = e.getEntity();
			if(Api.getItemInHand(damager)!=null){
				ItemStack item = Api.getItemInHand(damager);
				if(item.hasItemMeta()){
					if(item.getItemMeta().hasLore()){
						for(String lore : item.getItemMeta().getLore()){
							if(lore.contains(Api.getEnchName("Headless"))){
								int power = Api.getPower(lore, Api.getEnchName("Headless"));
								if(Api.randomPicker(11-power)){
									ItemStack head = new ItemStack(397, 1, (short)3);
									SkullMeta m = (SkullMeta) head.getItemMeta();
									m.setOwner(player.getName());
									head.setItemMeta(m);
									e.getDrops().add(head);
								}
							}
						}
					}
				}
			}
		}
	}
}