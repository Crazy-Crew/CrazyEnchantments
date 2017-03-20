package me.badbones69.crazyenchantments.api;

import java.util.ArrayList;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.badbones69.crazyenchantments.Methods;

public class CEPlayer {
	
	private int souls;
	private Player player;
	private Boolean soulsActive;
	private ArrayList<Cooldown> cooldowns;
	
	/**
	 * Used to make a new CEPlayer.
	 * @param player The player.
	 * @param souls How many souls they have.
	 * @param soulsActive If the soul uses is active.
	 * @param cooldowns The cooldowns the player has.
	 */
	public CEPlayer(Player player, int souls, boolean soulsActive, ArrayList<Cooldown> cooldowns){
		this.souls = souls;
		this.player = player;
		this.cooldowns = cooldowns;
		this.soulsActive = soulsActive;
	}
	
	/**
	 * Get the player from the CEPlayer.
	 * @return Player from the CEPlayer.
	 */
	public Player getPlayer(){
		return this.player;
	}
	
	/**
	 * Get how many souls the player has.
	 * @return The amount of souls the player has.
	 */
	public int getSouls(){
		return this.souls;
	}
	
	/**
	 * Set the amount of souls the player has.
	 * @param souls The new amount of souls the player has.
	 */
	public void setSouls(int souls){
		this.souls = souls;
	}
	
	/**
	 * Add 1 soul to the player.
	 */
	public void addSoul(){
		this.souls++;
	}
	
	/**
	 * Add extra souls to the player.
	 * @param souls Amount of souls you want to add.
	 */
	public void addSouls(int souls){
		this.souls += souls;
	}
	
	/**
	 * Take 1 soul from the player.
	 */
	public void useSoul(){
		this.souls--;
	}
	
	/**
	 * Take souls from the player.
	 * @param souls Amount of souls you are taking.
	 */
	public void useSouls(int souls){
		this.souls -= souls;
		if(this.souls < 0){
			this.souls = 0;
		}
	}
	
	/**
	 * Find out if the players souls are active.
	 * @return Ture if active and false if not.
	 */
	public Boolean isSoulsActive(){
		return soulsActive;
	}
	
	/**
	 * Set if the players souls are active.
	 * @param soulsActive True if you want to activate them and false if not.
	 */
	public void setSoulsActive(Boolean soulsActive){
		this.soulsActive = soulsActive;
	}
	
	public void giveGKit(GKitz kit){
		for(ItemStack item : kit.getItems()){
			if(Methods.isInvFull(player)){
				player.getWorld().dropItemNaturally(player.getLocation(), item);
			}else{
				player.getInventory().addItem(item);
			}
		}
		for(String cmd : kit.getCommands()){
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd
					.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName()));
		}
	}
	
	public Boolean hasGkitPermission(GKitz kit){
		if(player.hasPermission("crazyenchantments.bypass")){
			return true;
		}else{
			return player.hasPermission("crazyenchantments.gkitz." + kit.getName().toLowerCase());
		}
	}
	
	public Boolean canUseGKit(GKitz kit){
		if(player.hasPermission("crazyenchantments.bypass")){
			return true;
		}else{
			if(player.hasPermission("crazyenchantments.gkitz." + kit.getName().toLowerCase())){
				for(Cooldown cooldown : getCooldowns()){
					if(cooldown.getGKitz() == kit){
						return cooldown.isCooldownOver();
					}
				}
			}else{
				return false;
			}
		}
		return true;
	}
	
	public ArrayList<Cooldown> getCooldowns(){
		return this.cooldowns;
	}
	
	public Cooldown getCooldown(GKitz kit){
		for(Cooldown cooldown : cooldowns){
			if(cooldown.getGKitz() == kit){
				return cooldown;
			}
		}
		return null;
	}
	
	public void addCooldown(Cooldown cooldown){
		this.cooldowns.add(cooldown);
	}
	
	public void addCooldown(GKitz kit){
		Calendar cooldown = Calendar.getInstance();
		for(String i : kit.getCooldown().split(" ")){
			if(i.contains("D")||i.contains("d")){
				cooldown.add(Calendar.DATE, Integer.parseInt(i.replaceAll("D", "").replaceAll("d", "")));
			}
			if(i.contains("H")||i.contains("h")){
				cooldown.add(Calendar.HOUR, Integer.parseInt(i.replaceAll("H", "").replaceAll("h", "")));
			}
			if(i.contains("M")||i.contains("m")){
				cooldown.add(Calendar.MINUTE, Integer.parseInt(i.replaceAll("M", "").replaceAll("m", "")));
			}
			if(i.contains("S")||i.contains("s")){
				cooldown.add(Calendar.SECOND, Integer.parseInt(i.replaceAll("S", "").replaceAll("s", "")));
			}
		}
		cooldowns.add(new Cooldown(kit, cooldown));
	}
	
	public void removeCooldown(Cooldown cooldown){
		this.cooldowns.remove(cooldown);
	}
	
}