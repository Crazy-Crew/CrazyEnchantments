package me.BadBones69.CrazyEnchantments.Controlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.BadBones69.CrazyEnchantments.Main;
import me.BadBones69.CrazyEnchantments.Methods;
import me.BadBones69.CrazyEnchantments.API.CEBook;
import me.BadBones69.CrazyEnchantments.API.CEnchantments;
import me.BadBones69.CrazyEnchantments.API.CustomEBook;
import me.BadBones69.CrazyEnchantments.API.EnchantmentType;

public class ScrollControl implements Listener{
	
	@EventHandler
	public void onBlackScroll(InventoryClickEvent e){
		Player player = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		ItemStack item = e.getCurrentItem();
		ItemStack c = e.getCursor();
		if(inv != null){
			if(item!=null&&c!=null){
				if(c.hasItemMeta()){
					if(c.getItemMeta().hasDisplayName()){
						if(c.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.WhiteScroll.Name")))){
							if(c.getAmount()==1){
								if(!Methods.isProtected(item)){
									ArrayList<Material> types = new ArrayList<Material>();
									types.addAll(EnchantmentType.ALL.getItems());
									if(types.contains(item.getType())){
										e.setCancelled(true);
										e.setCurrentItem(Methods.addLore(item, Main.settings.getConfig().getString("Settings.WhiteScroll.ProtectedName")));
										player.setItemOnCursor(new ItemStack(Material.AIR));
										player.updateInventory();
										return;
									}
								}
							}
						}
					}
				}
				if(inv.getType() == InventoryType.CRAFTING){
					if(e.getRawSlot() < 9){
						return;
					}
				}
				if(item.hasItemMeta()){
					if(c.hasItemMeta()){
						if(item.getItemMeta().hasLore()&&c.getItemMeta().hasDisplayName()){
							if(c.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getConfig().getString("Settings.BlackScroll.Name")))){
								if(c.getAmount()==1){
									ArrayList<String> customEnchants = new ArrayList<String>();
									HashMap<String, Integer> lvl = new HashMap<String, Integer>();
									ArrayList<CEnchantments> enchants = new ArrayList<CEnchantments>();
									Boolean i = false;
									Boolean custom = false;
									if(Main.CE.hasEnchantments(item)){
										for(CEnchantments en : Main.CE.getEnchantments()){
											if(Main.CE.hasEnchantment(item, en)){
												enchants.add(en);
												lvl.put(en.getName(), Main.CE.getPower(item, en));
												i = true;
											}
										}
									}
									if(Main.CustomE.hasEnchantments(item)){
										for(String en : Main.CustomE.getEnchantments()){
											if(Main.CustomE.hasEnchantment(item, en)){
												customEnchants.add(en);
												lvl.put(en, Main.CustomE.getPower(item, en));
												i = true;
												custom = true;
											}
										}
									}
									if(i){
										e.setCancelled(true);
										player.setItemOnCursor(new ItemStack(Material.AIR));
										if(custom){
											String enchantment = pickCustomEnchant(customEnchants);
											e.setCurrentItem(Main.CustomE.removeEnchantment(item, enchantment));
											CustomEBook book = new CustomEBook(enchantment, lvl.get(enchantment), 1);
											player.getInventory().addItem(book.buildBook());
										}else{
											CEnchantments enchantment = pickEnchant(enchants);
											e.setCurrentItem(Main.CE.removeEnchantment(item, enchantment));
											CEBook book = new CEBook(enchantment, lvl.get(enchantment.getName()), 1);
											player.getInventory().addItem(book.buildBook());
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
	
	private CEnchantments pickEnchant(List<CEnchantments> enchants){
		Random i = new Random();
		return enchants.get(i.nextInt(enchants.size()));
	}
	
	private String pickCustomEnchant(List<String> enchants){
		Random i = new Random();
		return enchants.get(i.nextInt(enchants.size()));
	}
}