package me.BadBones69.CrazyEnchantments;

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
				if(c.hasItemMeta()){
					if(c.getItemMeta().hasDisplayName()){
						if(c.getItemMeta().getDisplayName().equals(Api.color(Main.settings.getConfig().getString("Settings.WhiteScroll.Name")))){
							if(!Api.isProtected(item)){
								ArrayList<Material> types = new ArrayList<Material>();
								types.addAll(ECControl.isArmor());
								types.addAll(ECControl.isBoots());
								types.addAll(ECControl.isHelmet());
								types.addAll(ECControl.isSword());
								types.addAll(ECControl.isBow());
								types.addAll(ECControl.isAxe());
								if(types.contains(item.getType())){
									e.setCancelled(true);
									ArrayList<String> lore = new ArrayList<String>();
									String name = "";
									if(item.hasItemMeta()){
										if(item.getItemMeta().hasLore())lore.addAll(item.getItemMeta().getLore());
										if(item.getItemMeta().hasDisplayName())name=item.getItemMeta().getDisplayName();
									}
									lore.add(Main.settings.getConfig().getString("Settings.WhiteScroll.ProtectedName"));
									e.setCurrentItem(Api.makeItem(item.getType(), item.getAmount(), 0, name, lore));
									e.setCursor(new ItemStack(Material.AIR));
									player.updateInventory();
									return;
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
										player.getInventory().addItem(makeEnchantBook(enchs.get(RealLore), lvl.get(RealLore)));
									}
								}
							}
						}
					}
				}
			}
		}
	}
	ItemStack makeEnchantBook(String ench, String power){
		int Smax = Main.settings.getConfig().getInt("Settings.BlackScroll.SuccessChance.Max");
		int Smin = Main.settings.getConfig().getInt("Settings.BlackScroll.SuccessChance.Min");
		int Dmax = Main.settings.getConfig().getInt("Settings.BlackScroll.DestroyChance.Max");
		int Dmin = Main.settings.getConfig().getInt("Settings.BlackScroll.DestroyChance.Min");
		ArrayList<String> lore = new ArrayList<String>();
		if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.DestroyChance")){
			lore.add(Api.color("&4"+ECControl.percentPick(Dmax, Dmin)+"% Destroy Chance"));
		}
		if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.SuccessChance")){
			lore.add(Api.color("&a"+ECControl.percentPick(Smax, Smin)+"% Success Chance"));
		}
		if(Main.settings.getEnchs().contains("Enchantments."+ench)){
			ench=Main.settings.getEnchs().getString("Enchantments."+ench+".BookColor")+Main.settings.getEnchs().getString("Enchantments."+ench+".Name")+" "+power;
		}
		if(Main.settings.getCustomEnchs().contains("Enchantments."+ench)){
			ench=Main.settings.getCustomEnchs().getString("Enchantments."+ench+".BookColor")+Main.settings.getCustomEnchs().getString("Enchantments."+ench+".Name")+" "+power;
		}
		return Api.makeItem(Material.BOOK, 1, 0, ench, Api.addDiscription(), lore);
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