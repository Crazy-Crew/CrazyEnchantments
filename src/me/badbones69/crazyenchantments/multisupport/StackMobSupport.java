package me.BadBones69.CrazyEnchantments.multisupport;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.BadBones69.CrazyEnchantments.Enchantments.Armor;
import uk.antiperson.stackmob.api.events.EntityStackEvent;

public class StackMobSupport implements Listener{
	
	@EventHandler
	public void onStack(EntityStackEvent e){
		for(Player player : Armor.getAllies().keySet()){
			if(Armor.getAllies().get(player).contains(e.getFirstEntity().getEntity()) || Armor.getAllies().get(player).contains(e.getOtherEntity().getEntity())){
				e.setCancelled(true);
			}
		}
	}
	
}
