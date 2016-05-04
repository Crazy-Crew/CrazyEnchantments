package me.BadBones69.CrazyEnchantments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Tinkerer implements Listener{
	public static void openTinker(Player player){
		Inventory inv = Bukkit.createInventory(null, 54, Api.color(Main.settings.getTinker().getString("Settings.GUIName")));
		inv.setItem(0, Api.makeItem(Material.STAINED_GLASS_PANE, 1, 14, Main.settings.getTinker().getString("Settings.TradeButton")));
		ArrayList<Integer> slots = new ArrayList<Integer>();
		slots.add(4);slots.add(13);slots.add(22);slots.add(31);slots.add(40);slots.add(49);
		for(int i : slots)inv.setItem(i, Api.makeItem(Material.STAINED_GLASS_PANE, 1, 0, " "));
		inv.setItem(8, Api.makeItem(Material.STAINED_GLASS_PANE, 1, 14, Main.settings.getTinker().getString("Settings.TradeButton")));
		player.openInventory(inv);
	}
	@EventHandler
	public void onXPUse(PlayerInteractEvent e){
		Player player = e.getPlayer();
		if(e.getAction()==Action.RIGHT_CLICK_AIR||e.getAction()==Action.RIGHT_CLICK_BLOCK){
			if(Api.getItemInHand(player)!=null){
				ItemStack item = Api.getItemInHand(player);
				if(item.getType()==Api.makeItem(Main.settings.getTinker().getString("Settings.BottleOptions.Item"), 1, "", Arrays.asList("")).getType()){
					if(item.hasItemMeta()){
						if(item.getItemMeta().hasLore()&&item.getItemMeta().hasDisplayName()){
							if(item.getItemMeta().getDisplayName().equals(Api.color(Main.settings.getTinker().getString("Settings.BottleOptions.Name")))){
								e.setCancelled(true);
								Api.setItemInHand(player, Api.removeItem(item));
								if(Main.settings.getTinker().getString("Settings.Lvl/Total").equalsIgnoreCase("Total")){
									Api.takeTotalXP(player, -getXP(item));
								}else{
									Api.takeLvlXP(player, -getXP(item));
								}
								if(Api.getVersion()>=191){
									player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 1);
								}else{
									player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 1, 1);
								}
								return;
							}
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		Inventory inv = e.getInventory();
		if(inv!=null){
			if(inv.getName().equals(Api.color(Main.settings.getTinker().getString("Settings.GUIName")))){
				e.setCancelled(true);
				if(e.getCurrentItem().hasItemMeta()){
					if(e.getCurrentItem().getItemMeta().hasLore()||e.getCurrentItem().getItemMeta().hasDisplayName()||e.getCurrentItem().getItemMeta().hasEnchants()){
						if(e.getCurrentItem().getType()!=Material.AIR){
							// Recycling things
							if(e.getCurrentItem().getItemMeta().hasDisplayName()){
								if(e.getCurrentItem().getItemMeta().getDisplayName().equals(Api.color(Main.settings.getTinker().getString("Settings.TradeButton")))){
									Player player = (Player) e.getWhoClicked();
									int total=0;
									for(int slot : getSlot().keySet()){
										if(inv.getItem(getSlot().get(slot))!=null){
											if(Main.settings.getTinker().getString("Settings.Money/XP").equalsIgnoreCase("Money")){
												ItemStack item = inv.getItem(slot);
												total=total+getTotalXP(item);
											}else{
												if(Api.isInvFull(((Player)e.getWhoClicked()))){
													e.getWhoClicked().getWorld().dropItem(e.getWhoClicked().getLocation(), inv.getItem(getSlot().get(slot)));
												}else{
													e.getWhoClicked().getInventory().addItem(inv.getItem(getSlot().get(slot)));
												}
											}
										}
										e.getInventory().setItem(slot, new ItemStack(Material.AIR));
										e.getInventory().setItem(getSlot().get(slot), new ItemStack(Material.AIR));
									}
									e.getWhoClicked().closeInventory();
									if(total!=0)Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give "+player.getName()+" "+total);
									e.getWhoClicked().sendMessage(Api.getPrefix()+Api.color(Main.settings.getMsg().getString("Messages.Tinker-Sold-Msg")));
									if(Api.getVersion()>=191){
										player.playSound(player.getLocation(), Sound.valueOf("ENTITY_VILLAGER_TRADING"), 1, 1);
									}else{
										player.playSound(player.getLocation(), Sound.valueOf("VILLAGER_YES"), 1, 1);
									}
									return;
								}
							}
							if(e.getCurrentItem().getType()!=Material.STAINED_GLASS_PANE){// Adding/Taking Items
								ItemStack item = e.getCurrentItem();
								if(item.getType()==Material.BOOK){// Adding a book
									for(String en : ECControl.allEnchantments().keySet()){
										if(item.getItemMeta().getDisplayName().contains(Api.color(Api.getEnchBookColor(en)+Api.getEnchName(en)))){
											Player player = (Player) e.getWhoClicked();
											if(inTinker(e.getRawSlot())){// Clicking in the Tinkers
												e.setCurrentItem(new ItemStack(Material.AIR));
												e.getWhoClicked().getInventory().addItem(item);
												inv.setItem(getSlot().get(e.getRawSlot()), new ItemStack(Material.AIR));
												if(Api.getVersion()>=191){
													player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK "), 1, 1);
												}else{
													player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
												}
											}else{// Clicking in their inventory
												if(e.getWhoClicked().getOpenInventory().getTopInventory().firstEmpty()==-1){
													e.getWhoClicked().sendMessage(Api.getPrefix()+Api.color(Main.settings.getMsg().getString("Messages.Tinker-Inventory-Full")));
													return;
												}
												e.setCurrentItem(new ItemStack(Material.AIR));
												inv.setItem(getSlot().get(inv.firstEmpty()), DustControl.getDust("MysteryDust", 1, Main.settings.getTinker().getInt("Tinker.Crazy-Enchantments."+en+".Book")));
												inv.setItem(inv.firstEmpty(), item);
												if(Api.getVersion()>=191){
													player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK "), 1, 1);
												}else{
													player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
												}
											}
										}
									}
								}
								if(getTotalXP(e.getCurrentItem())>0){// Adding an item
									Player player = (Player) e.getWhoClicked();
									if(inTinker(e.getRawSlot())){// Clicking in the Tinkers
										e.setCurrentItem(new ItemStack(Material.AIR));
										e.getWhoClicked().getInventory().addItem(item);
										inv.setItem(getSlot().get(e.getRawSlot()), new ItemStack(Material.AIR));
										if(Api.getVersion()>=191){
											player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK "), 1, 1);
										}else{
											player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
										}
									}else{// Clicking in their inventory
										if(e.getWhoClicked().getOpenInventory().getTopInventory().firstEmpty()==-1){
											e.getWhoClicked().sendMessage(Api.getPrefix()+Api.color(Main.settings.getMsg().getString("Messages.Tinker-Inventory-Full")));
											return;
										}
										e.setCurrentItem(new ItemStack(Material.AIR));
										inv.setItem(getSlot().get(inv.firstEmpty()), getBottle(item));
										inv.setItem(inv.firstEmpty(), item);
										if(Api.getVersion()>=191){
											player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK "), 1, 1);
										}else{
											player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
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
	public void onInvClose(InventoryCloseEvent e){
		Inventory inv = e.getInventory();
		if(inv!=null){
			if(inv.getName().equals(Api.color(Main.settings.getTinker().getString("Settings.GUIName")))){
				for(int slot : getSlot().keySet()){
					if(inv.getItem(slot)!=null){
						if(inv.getItem(slot).getType()!=Material.AIR){
							if(Api.isInvFull(((Player)e.getPlayer()))){
								e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), inv.getItem(slot));
							}else{
								e.getPlayer().getInventory().addItem(inv.getItem(slot));
							}
						}
					}
				}
			}
		}
	}
	ItemStack getBottle(ItemStack item){
		String id = Main.settings.getTinker().getString("Settings.BottleOptions.Item");
		String name = Main.settings.getTinker().getString("Settings.BottleOptions.Name");
		List<String> lore = new ArrayList<String>();
		for(String l : Main.settings.getTinker().getStringList("Settings.BottleOptions.Lore")){
			lore.add(l.replaceAll("%Total%", getTotalXP(item)+"").replaceAll("%total%", getTotalXP(item)+""));
		}
		return Api.makeItem(id, 1, name, lore);
	}
	HashMap<Integer, Integer> getSlot(){
		HashMap<Integer, Integer> slots = new HashMap<Integer, Integer>();
		slots.put(1, 5);
		slots.put(2, 6);
		slots.put(3, 7);
		slots.put(9, 14);
		slots.put(10, 15);
		slots.put(11, 16);
		slots.put(12, 17);
		slots.put(18, 23);
		slots.put(19, 24);
		slots.put(20, 25);
		slots.put(21, 26);
		slots.put(27, 32);
		slots.put(28, 33);
		slots.put(29, 34);
		slots.put(30, 35);
		slots.put(36, 41);
		slots.put(37, 42);
		slots.put(38, 43);
		slots.put(39, 44);
		slots.put(45, 50);
		slots.put(46, 51);
		slots.put(47, 52);
		slots.put(48, 53);
		return slots;
	}
	boolean inTinker(int slot){
		//The last slot in the tinker is 54
		if(slot<54)return true;
		return false;
	}
	int getTotalXP(ItemStack item){
		int total=0;
		if(item.hasItemMeta()){
			if(item.getItemMeta().hasLore()){
				for(String lore : item.getItemMeta().getLore()){
					for(String en : ECControl.allEnchantments().keySet()){
						if(lore.contains(Api.getEnchName(en))){
							total=total+Main.settings.getTinker().getInt("Tinker.Crazy-Enchantments."+en+".Items");
						}
					}
				}
			}
		}
		if(item.hasItemMeta()){
			if(item.getItemMeta().hasEnchants()){
				for(Enchantment en : item.getEnchantments().keySet()){
					total=total+Main.settings.getTinker().getInt("Tinker.Vanilla-Enchantments."+en.getName());
				}
			}
		}
		return total;
	}
	Integer getXP(ItemStack item){
		List<String> lore = item.getItemMeta().getLore();
		List<String> L = Main.settings.getTinker().getStringList("Settings.BottleOptions.Lore");
		String arg = "";
		int i = 0;
		for(String l : L){
			l = Api.color(l);
			String lo = lore.get(i);
			if(l.contains("%Total%")){
				String[] b = l.split("%Total%");
				if(b.length>=1)arg = lo.replace(b[0], "");
				if(b.length>=2)arg = arg.replace(b[1], "");
			}
			if(l.contains("%total%")){
				String[] b = l.split("%total%");
				if(b.length>=1)arg = lo.replace(b[0], "");
				if(b.length>=2)arg = arg.replace(b[1], "");
			}
			i++;
		}
		return Integer.parseInt(arg);
	}
}