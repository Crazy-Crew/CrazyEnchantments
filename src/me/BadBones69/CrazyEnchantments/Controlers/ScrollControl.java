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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.BadBones69.CrazyEnchantments.Api;
import me.BadBones69.CrazyEnchantments.Main;
import me.BadBones69.CrazyEnchantments.API.CEBook;
import me.BadBones69.CrazyEnchantments.API.CEnchantments;
import me.BadBones69.CrazyEnchantments.API.CrazyEnchantments;
import me.BadBones69.CrazyEnchantments.API.EnchantmentType;

public class ScrollControl implements Listener{
	CrazyEnchantments CE = CrazyEnchantments.getInstance();
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlackScroll(InventoryClickEvent e){
		Player player = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		ItemStack item = e.getCurrentItem();
		ItemStack c = e.getCursor();
		ArrayList<CEnchantments> enchants = new ArrayList<CEnchantments>();
		HashMap<CEnchantments, Integer> lvl = new HashMap<CEnchantments, Integer>();
		if(inv != null){
			if(item!=null&&c!=null){
				if(c.hasItemMeta()){
					if(c.getItemMeta().hasDisplayName()){
						if(c.getItemMeta().getDisplayName().equals(Api.color(Main.settings.getConfig().getString("Settings.WhiteScroll.Name")))){
							if(c.getAmount()==1){
								if(!Api.isProtected(item)){
									ArrayList<Material> types = new ArrayList<Material>();
									types.addAll(EnchantmentType.ALL.getItems());
									if(types.contains(item.getType())){
										e.setCancelled(true);
										e.setCurrentItem(Api.addLore(item, Main.settings.getConfig().getString("Settings.WhiteScroll.ProtectedName")));
										e.setCursor(new ItemStack(Material.AIR));
										player.updateInventory();
										return;
									}
								}
							}
						}
					}
				}
				if(item.hasItemMeta()){
					if(c.hasItemMeta()){
						if(item.getItemMeta().hasLore()&&c.getItemMeta().hasDisplayName()){
							if(c.getItemMeta().getDisplayName().equals(Api.color(Main.settings.getConfig().getString("Settings.BlackScroll.Name")))){
								if(c.getAmount()==1){
									boolean i = false;
									if(CE.hasEnchantments(item)){
										for(CEnchantments en : CE.getEnchantments()){
											if(CE.hasEnchantment(item, en)){
												enchants.add(en);
												lvl.put(en, CE.getPower(item, en));
												i = true;
											}
										}
									}
									if(i){
										e.setCancelled(true);
										CEnchantments enchantment = pickEnchant(enchants);
										e.setCurrentItem(CE.removeEnchantment(item, enchantment));
										e.setCursor(new ItemStack(Material.AIR));
										CEBook book = new CEBook(enchantment, lvl.get(enchantment), 1);
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
	CEnchantments pickEnchant(List<CEnchantments> enchants){
		Random i = new Random();
		return enchants.get(i.nextInt(enchants.size()));
	}
	Integer percentPick(int max, int min){
		Random i = new Random();
		return min+i.nextInt(max-min);
	}
}