package me.BadBones69.CrazyEnchantments.Enchantments.Tools;

import java.util.HashMap;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Oxygenate implements Listener{
	private HashMap<Player, Boolean> effect = new HashMap<Player, Boolean>();
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if(!Api.isEnchantmentEnabled("Oxygenate"))return;
		Player player = e.getPlayer();
		if(Api.getItemInHand(player)!=null){
			ItemStack item = Api.getItemInHand(player);
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasLore()){
					for(String lore : item.getItemMeta().getLore()){
						if(lore.contains(Api.getEnchName("Oxygenate"))){
							effect.put(player, true);
							player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 55555*20, 5));
							return;
						}
					}
				}
			}
		}
		if(effect.containsKey(player)){
			if(effect.get(player)){
				effect.put(player, false);
				player.removePotionEffect(PotionEffectType.WATER_BREATHING);
				return;
			}
		}
	}
}