package me.BadBones69.CrazyEnchantments;

import java.util.ArrayList;
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

public class BS implements Listener{
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlackScroll(InventoryClickEvent e){
		Player player = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		ItemStack item = e.getCurrentItem();
		ItemStack c = e.getCursor();
		ArrayList<String> enchants = new ArrayList<String>();
		if(inv != null){
			if(item.hasItemMeta()&&c.hasItemMeta()){
				if(item.getItemMeta().hasLore()&&c.getItemMeta().hasDisplayName()){
					if(c.getItemMeta().getDisplayName().equals(Api.color(Main.settings.getConfig().getString("Settings.BlackScroll.Name")))){
						if(c.getAmount()==1){
							boolean i = false;
							for(String l : item.getItemMeta().getLore()){
								String L = Api.removeColor(l);
								L = L.replaceAll("I", "");
								L = L.replaceAll("V", "");
								L = L.substring(0, L.length()-1);
								for(String en : Enchantments()){
									if(en.contains(L)){
										enchants.add(l);
										i = true;
									}
								}
							}
							if(i){
								e.setCancelled(true);
								String RealLore = pickEnchant(enchants);
								e.setCurrentItem(Api.removeLore(item, RealLore));
								e.setCursor(new ItemStack(Material.AIR));
								player.getInventory().addItem(Api.setBook(RealLore));
							}
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void onClick(PlayerInteractEvent e){
		Player player = e.getPlayer();
		if(e.getItem()!=null){
			ItemStack item = e.getItem();
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasDisplayName()){
					if(item.getItemMeta().getDisplayName().equals(Api.color(Main.settings.getConfig().getString("Settings.BlackScroll.Name")))){
						player.sendMessage(Api.getPrefix()+Api.color("&7Black scrolls will remove a random enchantment from your item."));
					}
				}
			}
		}
	}
	String pickEnchant(List<String> enchants){
		Random i = new Random();
		return enchants.get(i.nextInt(enchants.size()));
	}
	ArrayList<String> Enchantments(){
		ArrayList<String> enchants = new ArrayList<String>();
		enchants.add("Gears");
		enchants.add("Rocket");
		enchants.add("Springs");
		enchants.add("Drunk");
		enchants.add("Inquisitive");
		enchants.add("Life Steal");
		enchants.add("Aquatic");
		enchants.add("Glowing");
		enchants.add("Headless");
		enchants.add("Lightning");
		enchants.add("Fire Shield");
		enchants.add("Life Steal");
		enchants.add("OverLoad");
		enchants.add("Piercing");
		enchants.add("Rage");
		enchants.add("Wither");
		return enchants;
	}
}