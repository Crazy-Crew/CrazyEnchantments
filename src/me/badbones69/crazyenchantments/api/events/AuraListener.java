package me.badbones69.crazyenchantments.api.events;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CEnchantments;

public class AuraListener implements Listener{
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMoveEvent(PlayerMoveEvent e){
		new BukkitRunnable(){
			Player player = e.getPlayer();
			ArrayList<Player> players = getNearByPlayers(player, 3);
			@Override
			public void run() {
				for(ItemStack item : e.getPlayer().getEquipment().getArmorContents()){// The player moving.
					if(Main.CE.hasEnchantments(item)){
						for(CEnchantments enchant : getAuraEnchantments()){
							if(Main.CE.hasEnchantment(item, Main.CE.getEnchantmentFromName(enchant.getName()))){
								int power = Main.CE.getPower(item, Main.CE.getEnchantmentFromName(enchant.getName()));
								if(players.size() > 0){
									for(Player other : players){
										new BukkitRunnable(){
											@Override
											public void run() {
												Bukkit.getPluginManager().callEvent(new AuraActiveEvent(player, other, enchant, power));
											}
										}.runTask(Methods.getPlugin());
									}
								}
							}
						}
					}
				}
				for(Player other : players){
					for(ItemStack item : other.getEquipment().getArmorContents()){// The other players moving.
						if(Main.CE.hasEnchantments(item)){
							for(CEnchantments enchant : getAuraEnchantments()){
								if(Main.CE.hasEnchantment(item, Main.CE.getEnchantmentFromName(enchant.getName()))){
									new BukkitRunnable(){
										@Override
										public void run() {
											Bukkit.getPluginManager().callEvent(new AuraActiveEvent(other, player, enchant, 
													Main.CE.getPower(item, Main.CE.getEnchantmentFromName(enchant.getName()))));
										}
									}.runTask(Methods.getPlugin());
								}
							}
						}
					}
				}
			}
		}.runTaskAsynchronously(Methods.getPlugin());
	}
	
	private ArrayList<CEnchantments> getAuraEnchantments(){
		ArrayList<CEnchantments> enchants = new ArrayList<CEnchantments>();
		enchants.add(CEnchantments.BLIZZARD);
		enchants.add(CEnchantments.ACIDRAIN);
		enchants.add(CEnchantments.SANDSTORM);
		enchants.add(CEnchantments.RADIANT);
		enchants.add(CEnchantments.INTIMIDATE);
		return enchants;
	}
	
	private ArrayList<Player> getNearByPlayers(Player player, int radius){
		ArrayList<Player> players = new ArrayList<Player>();
		for(Entity en : player.getNearbyEntities(radius, radius, radius)){
			if(en instanceof Player){
				if((Player)en != player){
					players.add((Player) en);
				}
			}
		}
		return players;
	}
	
}