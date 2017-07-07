package me.badbones69.crazyenchantments.controlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.WordUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CEBook;
import me.badbones69.crazyenchantments.api.CEnchantment;
import me.badbones69.crazyenchantments.api.EnchantmentType;

public class ScrollControl implements Listener{
	
	@EventHandler
	public void onScrollUse(InventoryClickEvent e){
		Player player = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		ItemStack item = e.getCurrentItem();
		ItemStack scroll = e.getCursor();
		if(inv != null){
			if(item == null) item = new ItemStack(Material.AIR);
			if(scroll == null) scroll = new ItemStack(Material.AIR);
			if(item.getType() != Material.AIR && scroll.getType() != Material.AIR){
				if(inv.getType() == InventoryType.CRAFTING){
					if(e.getRawSlot() < 9){
						return;
					}
				}
				if(scroll.isSimilar(getTransmogScroll(1))){
					if(player.getGameMode() == GameMode.CREATIVE && scroll.getAmount() > 1){
						player.sendMessage(Methods.getPrefix() + Methods.color("&cPlease unstack the scrolls for them to work."));
						return;
					}
					if(Main.CE.hasEnchantments(item)){
						if(item.isSimilar(orderEnchantments(item.clone()))){
							return;
						}
						e.setCancelled(true);
						ItemStack it = orderEnchantments(item);
						it = Methods.addGlow(it);
						e.setCurrentItem(it);
						player.setItemOnCursor(Methods.removeItem(scroll));
						player.updateInventory();
						return;
					}
				}
				if(scroll.isSimilar(getWhiteScroll(1))){
					if(player.getGameMode() == GameMode.CREATIVE && scroll.getAmount() > 1){
						player.sendMessage(Methods.getPrefix() + Methods.color("&cPlease unstack the scrolls for them to work."));
						return;
					}
					if(!Methods.isProtected(item)){
						ArrayList<Material> types = new ArrayList<Material>();
						types.addAll(EnchantmentType.ALL.getItems());
						if(types.contains(item.getType())){
							e.setCancelled(true);
							e.setCurrentItem(Methods.addLore(item, Main.settings.getConfig().getString("Settings.WhiteScroll.ProtectedName")));
							player.setItemOnCursor(Methods.removeItem(scroll));
							return;
						}
					}
				}
				if(scroll.isSimilar(getBlackScroll(1))){
					if(player.getGameMode() == GameMode.CREATIVE && scroll.getAmount() > 1){
						player.sendMessage(Methods.getPrefix() + Methods.color("&cPlease unstack the scrolls for them to work."));
						return;
					}
					HashMap<String, Integer> lvl = new HashMap<String, Integer>();
					ArrayList<CEnchantment> enchants = new ArrayList<CEnchantment>();
					Boolean i = false;
					if(Main.CE.hasEnchantments(item)){
						for(CEnchantment en : Main.CE.getRegisteredEnchantments()){
							if(Main.CE.hasEnchantment(item, en)){
								enchants.add(en);
								lvl.put(en.getName(), Main.CE.getPower(item, en));
								i = true;
							}
						}
					}
					if(i){
						e.setCancelled(true);
						player.setItemOnCursor(Methods.removeItem(scroll));
						if(Main.settings.getConfig().getBoolean("Settings.BlackScroll.Chance-Toggle")){
							if(!Methods.randomPicker(Main.settings.getConfig().getInt("Settings.BlackScroll.Chance"), 100)){
								player.sendMessage(Methods.getPrefix() + Methods.color(Main.settings.getMsg().getString("Messages.Black-Scroll-Unsuccessful")));
								return;
							}
						}
						CEnchantment enchantment = pickEnchant(enchants);
						e.setCurrentItem(Main.CE.removeEnchantment(item, enchantment));
						CEBook book = new CEBook(enchantment, lvl.get(enchantment.getName()), 1);
						player.getInventory().addItem(book.buildBook());
						player.updateInventory();
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
					if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.BlackScroll.Name")))){
						player.sendMessage(Methods.getPrefix()+Methods.color(Main.settings.getMsg().getString("Messages.Right-Click-Black-Scroll")));
					}
				}
			}
		}
	}
	
	public static ItemStack orderEnchantments(ItemStack item){
		HashMap<String, Integer> enchants = new HashMap<String, Integer>();
		HashMap<String, Integer> categories = new HashMap<String, Integer>();
		List<String> order = new ArrayList<String>();
		ArrayList<String> enchantments = new ArrayList<String>();
		for(CEnchantment en : Main.CE.getItemEnchantments(item)){
			enchantments.add(en.getName());
		}
		for(String ench : enchantments){
			int top = 0;
			if(Main.CE.getEnchantmentFromName(ench) != null){
				for(String cat : Main.CE.getEnchantmentFromName(ench).getCategories()){
					if(top < Main.CE.getCategoryRarity(cat)){
						top = Main.CE.getCategoryRarity(cat);
					}
				}
				enchants.put(ench, Main.CE.getPower(item, Main.CE.getEnchantmentFromName(ench)));
				Main.CE.removeEnchantment(item, Main.CE.getEnchantmentFromName(ench));
			}
			categories.put(ench, top);
			order.add(ench);
		}
		order = orderInts(order, categories);
		ItemMeta m = item.getItemMeta();
		ArrayList<String> lore = new ArrayList<String>();
		for(String ench : order){
			if(Main.CE.getEnchantmentFromName(ench) != null){
				CEnchantment en = Main.CE.getEnchantmentFromName(ench);
				lore.add(en.getColor() + en.getCustomName() + " " + Main.CE.convertPower(enchants.get(ench)));
			}
		}
		if(m.hasLore()){
			for(String l : m.getLore()){
				lore.add(l);
			}
		}
		m.setLore(lore);
		String name = Methods.color("&b" + WordUtils.capitalizeFully(item.getType().toString().replaceAll("_", " ").toLowerCase()));
		String enchs = Main.settings.getConfig().getString("Settings.TransmogScroll.Amount-of-Enchantments");
		if(m.hasDisplayName()){
			name = m.getDisplayName();
			for(int i = 0; i <= 100; i++){
				if(m.getDisplayName().endsWith(Methods.color(enchs.replaceAll("%Amount%", i + "").replaceAll("%amount%", i + "")))){
					name = m.getDisplayName().substring(0, m.getDisplayName().length() - (enchs.replaceAll("%Amount%", i + "").replaceAll("%amount%", i + "")).length());
				}
			}
		}
		int amount = order.size();
		if(Main.settings.getConfig().getBoolean("Settings.TransmogScroll.Count-Vanilla-Enchantments")){
			for(Enchantment ench : item.getEnchantments().keySet()){
				try{
					if(Methods.getEnchantments().contains(ench.getName())){
						amount ++;
					}
				}catch(Exception e){}
			}
		}
		if(Main.settings.getConfig().getBoolean("Settings.TransmogScroll.Amount-Toggle")){
			m.setDisplayName(name + Methods.color(enchs.replaceAll("%Amount%", amount + "").replaceAll("%amount%", amount + "")));
		}
		item.setItemMeta(m);
		return item;
	}
	
	public static List<String> orderInts(List<String> list, final Map<String, Integer> map){
	    Collections.sort(list, new Comparator<String>() {
	        @Override
	        public int compare(String a1, String a2) {
	        	Integer string1 = map.get(a1);
	        	Integer string2 = map.get(a2);
	            return string2.compareTo(string1);
	        }
	    });
	    return list;
	}
	
	public static ItemStack getBlackScroll(int amount){
		String name = Methods.color(Main.settings.getConfig().getString("Settings.BlackScroll.Name"));
		String id = Main.settings.getConfig().getString("Settings.BlackScroll.Item");
		return Methods.makeItem(id, amount, name, Main.settings.getConfig().getStringList("Settings.BlackScroll.Item-Lore"));
	}
	
	public static ItemStack getWhiteScroll(int amount){
		String name = Methods.color(Main.settings.getConfig().getString("Settings.WhiteScroll.Name"));
		String id = Main.settings.getConfig().getString("Settings.WhiteScroll.Item");
		return Methods.makeItem(id, amount, name, Main.settings.getConfig().getStringList("Settings.WhiteScroll.Item-Lore"));
	}
	
	public static ItemStack getTransmogScroll(int amount){
		String name = Methods.color(Main.settings.getConfig().getString("Settings.TransmogScroll.Name"));
		String id = Main.settings.getConfig().getString("Settings.TransmogScroll.Item");
		return Methods.makeItem(id, amount, name, Main.settings.getConfig().getStringList("Settings.TransmogScroll.Item-Lore"));
	}
	
	private CEnchantment pickEnchant(List<CEnchantment> enchants){
		Random i = new Random();
		return enchants.get(i.nextInt(enchants.size()));
	}
	
}