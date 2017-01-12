package me.BadBones69.CrazyEnchantments.Controlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.BadBones69.CrazyEnchantments.Main;
import me.BadBones69.CrazyEnchantments.Methods;

public class ProtectionCrystal implements Listener{
	
	private HashMap<Player, ArrayList<ItemStack>> PlayersItems = new HashMap<Player, ArrayList<ItemStack>>();
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		FileConfiguration config = Main.settings.getConfig();
		Player player = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		if(inv != null){
			ItemStack c = e.getCursor();
			ItemStack item = e.getCurrentItem();
			String protection = Methods.color(config.getString("Settings.ProtectionCrystal.Protected"));
			if(item == null) item = new ItemStack(Material.AIR);
			if(c == null) c = new ItemStack(Material.AIR);
			if(item.getType() != Material.AIR && c.getType() != Material.AIR){
				if(item.getAmount() == 1){
					if(player.getGameMode() == GameMode.CREATIVE && c.getAmount() != 1){
						player.sendMessage(Methods.getPrefix() + Methods.color("&cPlease unstack the crystals for them to work."));
						return;
					}
					if(!getCrystals().isSimilar(item)){
						if(!isProtected(item)){
							if(c.hasItemMeta()){
								if(c.getItemMeta().hasDisplayName()){
									if(c.getItemMeta().getDisplayName().equals(Methods.color(config.getString("Settings.ProtectionCrystal.Name")))){
										if(c.getType() == getCrystals().getType()){
											e.setCancelled(true);
											player.setItemOnCursor(Methods.removeItem(c));
											e.setCurrentItem(Methods.addLore(item, protection));
											player.updateInventory();
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
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent e){
		Player player = e.getEntity();
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		for(ItemStack item : e.getDrops()){
			if(isProtected(item)){
				if(isSuccessfull(player)){
					items.add(item);
				}else{
					drops.add(item);
				}
			}else{
				drops.add(item);
			}
		}
		e.getDrops().clear();
		e.getDrops().addAll(drops);
		PlayersItems.put(player, items);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e){
		Player player = e.getPlayer();
		String protection = Methods.color(Main.settings.getConfig().getString("Settings.ProtectionCrystal.Protected"));
		if(PlayersItems.containsKey(player)){
			for(ItemStack item : PlayersItems.get(player)){
				player.getInventory().addItem(Methods.removeLore(item, protection));
			}
			PlayersItems.remove(player);
		}
	}
	
	public static Boolean isProtected(ItemStack item){
		if(item.hasItemMeta()){
			if(item.getItemMeta().hasLore()){
				for(String lore : item.getItemMeta().getLore()){
					if(lore.contains(Methods.color(Main.settings.getConfig().getString("Settings.ProtectionCrystal.Protected")))){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static Boolean isSuccessfull(Player player){
		if(player.hasPermission("crazyenchantments.bypass")){
			return true;
		}
		FileConfiguration config = Main.settings.getConfig();
		if(config.contains("Settings.ProtectionCrystal.Chance")){
			if(config.getBoolean("Settings.ProtectionCrystal.Chance.Toggle")){
				if(Methods.randomPicker(config.getInt("Settings.ProtectionCrystal.Chance.Success-Chance"), 100)){
					return true;
				}else{
					return false;
				}
			}
		}
		return true;
	}
	
	public static ItemStack getCrystals(){
		FileConfiguration config = Main.settings.getConfig();
		String id = config.getString("Settings.ProtectionCrystal.Item");
		String name = config.getString("Settings.ProtectionCrystal.Name");
		List<String> lore = config.getStringList("Settings.ProtectionCrystal.Lore");
		Boolean toggle = config.getBoolean("Settings.ProtectionCrystal.Glowing");
		ItemStack item = Methods.addGlow(Methods.makeItem(id, 1, name, lore), toggle);
		return item;
	}
	
	public static ItemStack getCrystals(int amount){
		FileConfiguration config = Main.settings.getConfig();
		String id = config.getString("Settings.ProtectionCrystal.Item");
		String name = config.getString("Settings.ProtectionCrystal.Name");
		List<String> lore = config.getStringList("Settings.ProtectionCrystal.Lore");
		Boolean toggle = config.getBoolean("Settings.ProtectionCrystal.Glowing");
		ItemStack item = Methods.addGlow(Methods.makeItem(id, amount, name, lore), toggle);
		return item;
	}
	
	public static ItemStack removeProtection(ItemStack item){
		ArrayList<String> lore = new ArrayList<String>();
		ItemMeta m = item.getItemMeta();
		lore.addAll(m.getLore());
		lore.remove(Methods.color(Main.settings.getConfig().getString("Settings.ProtectionCrystal.Protected")));
		m.setLore(lore);
		item.setItemMeta(m);
		return item;
	}
	
}