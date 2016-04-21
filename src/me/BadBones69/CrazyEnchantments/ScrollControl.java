package me.BadBones69.CrazyEnchantments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ScrollControl implements Listener{
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlackScroll(InventoryClickEvent e){
		Player player = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		ItemStack item = e.getCurrentItem();
		ItemStack c = e.getCursor();
		ArrayList<String> enchants = new ArrayList<String>();
		HashMap<String, String> enchs = new HashMap<String, String>();
		HashMap<String, String> lvl = new HashMap<String, String>();
		if(inv != null){
			if(item!=null&&c!=null){
				if(item.hasItemMeta()){
					if(c.hasItemMeta()){
						if(item.getItemMeta().hasLore()&&c.getItemMeta().hasDisplayName()){
							if(c.getItemMeta().getDisplayName().equals(Api.color(Main.settings.getConfig().getString("Settings.BlackScroll.Name")))){
								if(c.getAmount()==1){
									boolean i = false;
									for(String l : item.getItemMeta().getLore()){
										for(String en : ECControl.allEnchantments().keySet()){
											if(l.contains(Api.getEnchName(en))){
												enchants.add(l);
												lvl.put(l, l.substring(l.lastIndexOf(" ")+1));
												enchs.put(l, en);
												i = true;
											}
										}
									}
									if(i){
										e.setCancelled(true);
										String RealLore = pickEnchant(enchants);
										e.setCurrentItem(Api.removeLore(item, RealLore));
										e.setCursor(new ItemStack(Material.AIR));
										player.getInventory().addItem(makeEnchantBook(Main.settings.getConfig().getInt("Settings.BlackScroll.SuccessChance.Max"),
												Main.settings.getConfig().getInt("Settings.BlackScroll.SuccessChance.Min"), enchs.get(RealLore), lvl.get(RealLore)));
									}
								}
							}
						}
					}
				}
			}
		}
	}
	ItemStack makeEnchantBook(int max, int min, String ench, String power){
		ench=Main.settings.getEnchs().getString("Enchantments."+ench+".BookColor")+Main.settings.getEnchs().getString("Enchantments."+ench+".Name")+" "+power;
		return Api.makeItem(Material.BOOK, 1, 0, ench,
				Api.addDiscription(), Arrays.asList(Api.color("&a"+ECControl.percentPick(max, min)+"% Success Chance")));
	}
	@EventHandler
	public void onClick(PlayerInteractEvent e){
		Player player = e.getPlayer();
		if(e.getItem()!=null){
			ItemStack item = e.getItem();
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasDisplayName()){
					if(item.getItemMeta().getDisplayName().equals(Api.color(Main.settings.getConfig().getString("Settings.BlackScroll.Name")))){
						player.sendMessage(Api.getPrefix()+Api.color(Main.settings.getMsg().getString("Messages.Right-Click-Black-Scroll")));
					}
				}
			}
		}
	}
	String pickEnchant(List<String> enchants){
		Random i = new Random();
		return enchants.get(i.nextInt(enchants.size()));
	}
}