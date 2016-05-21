package me.BadBones69.CrazyEnchantments.Enchantments.Armor;

import java.util.ArrayList;
import java.util.Random;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class Savior implements Listener{
	ArrayList<Player> fall = new ArrayList<Player>();
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(!Api.isEnchantmentEnabled("Savior"))return;
		if(Api.isFriendly(e.getDamager(), e.getEntity()))return;
		if(e.getEntity() instanceof LivingEntity){
			if(e.getEntity() instanceof Player){
				final Player player = (Player) e.getEntity();
				for(ItemStack armor : player.getEquipment().getArmorContents()){
					if(armor==null)return;
					if(armor.hasItemMeta()){
						if(!armor.getItemMeta().hasLore())return;
						for(String lore : armor.getItemMeta().getLore()){
							if(lore.contains(Api.getEnchName("Savior"))){
								if(player.getHealth() <= 8){
									Random number = new Random();
									int chance;
									for(int counter = 1; counter<=1; counter++){
										chance = 1 + number.nextInt(8-Api.getPower(lore, Api.getEnchName("Savior")));
										if(chance == 1){
											e.setDamage(e.getDamage()/2);
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