package me.BadBones69.CrazyEnchantments.Enchantments.Swords;

import java.util.HashMap;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

public class Rage implements Listener{
	static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments");
	@SuppressWarnings("static-access")
	public Rage(Plugin plugin){
		this.plugin = plugin;
	}
	HashMap<Player, Double> multi = new HashMap<Player, Double>();
	HashMap<Player, Integer> num = new HashMap<Player, Integer>();
	HashMap<Player, Integer> reset = new HashMap<Player, Integer>();
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(Api.isFriendly(e.getDamager(), e.getEntity()))return;
		if(!Api.isEnchantmentEnabled("Rage"))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if(!Api.allowsPVP(e.getDamager()))return;
		if(e.getEntity() instanceof LivingEntity){
			if(e.getDamager() instanceof Player){
				final Player damager = (Player) e.getDamager();
				if(Api.getItemInHand(damager).hasItemMeta()){
					if(!e.getEntity().isDead()){
						if(!Api.getItemInHand(damager).getItemMeta().hasLore())return;
						for(String lore : Api.getItemInHand(damager).getItemMeta().getLore()){
							if(lore.contains(Api.getEnchName("Rage"))){
								if(multi.containsKey(damager)){
									Bukkit.getScheduler().cancelTask(reset.get(damager));
									multi.put(damager, multi.get(damager) + (Api.getPower(lore, Api.getEnchName("Rage"))*0.1));
									if(multi.get(damager).intValue() == num.get(damager)){
										damager.sendMessage(Api.color("&3You are now doing &a" + num.get(damager) + "x &3Damage."));
										num.put(damager, num.get(damager)+1);
									}
									e.setDamage(e.getDamage() * multi.get(damager));
								}
								if(!multi.containsKey(damager)){
									multi.put(damager, 1.0);
									num.put(damager, 2);
									damager.sendMessage(Api.color("&aYour Rage is Building."));
								}
								reset.put(damager, Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
									@Override
									public void run() {
										multi.remove(damager);
										damager.sendMessage(Api.color("&cYour Rage has Cooled Down."));
									}
								}, 4*20));
							}
						}
					}
				}
			}
		}
	}
}