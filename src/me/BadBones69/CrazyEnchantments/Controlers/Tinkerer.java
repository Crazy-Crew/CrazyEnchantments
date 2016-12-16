package me.BadBones69.CrazyEnchantments.Controlers;

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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.BadBones69.CrazyEnchantments.Methods;
import me.BadBones69.CrazyEnchantments.Main;
import me.BadBones69.CrazyEnchantments.API.CEnchantments;
import me.BadBones69.CrazyEnchantments.API.EnchantmentType;
import me.BadBones69.CrazyEnchantments.API.Version;

public class Tinkerer implements Listener{
	
	public static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments");
	
	public static void openTinker(Player player){
		Inventory inv = Bukkit.createInventory(null, 54, Methods.color(Main.settings.getTinker().getString("Settings.GUIName")));
		inv.setItem(0, Methods.makeItem(Material.STAINED_GLASS_PANE, 1, 14, Main.settings.getTinker().getString("Settings.TradeButton")));
		ArrayList<Integer> slots = new ArrayList<Integer>();
		slots.add(4);slots.add(13);slots.add(22);slots.add(31);slots.add(40);slots.add(49);
		for(int i : slots)inv.setItem(i, Methods.makeItem(Material.STAINED_GLASS_PANE, 1, 0, " "));
		inv.setItem(8, Methods.makeItem(Material.STAINED_GLASS_PANE, 1, 14, Main.settings.getTinker().getString("Settings.TradeButton")));
		player.openInventory(inv);
	}
	
	@EventHandler
	public void onXPUse(PlayerInteractEvent e){
		Player player = e.getPlayer();
		if(e.getAction()==Action.RIGHT_CLICK_AIR||e.getAction()==Action.RIGHT_CLICK_BLOCK){
			if(Methods.getItemInHand(player)!=null){
				ItemStack item = Methods.getItemInHand(player);
				if(item.getType()==Methods.makeItem(Main.settings.getTinker().getString("Settings.BottleOptions.Item"), 1, "", Arrays.asList("")).getType()){
					if(item.hasItemMeta()){
						if(item.getItemMeta().hasLore()&&item.getItemMeta().hasDisplayName()){
							if(item.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getTinker().getString("Settings.BottleOptions.Name")))){
								e.setCancelled(true);
								Methods.setItemInHand(player, Methods.removeItem(item));
								if(Main.settings.getTinker().getString("Settings.Lvl/Total").equalsIgnoreCase("Total")){
									Methods.takeTotalXP(player, -getXP(item));
								}else{
									Methods.takeLvlXP(player, -getXP(item));
								}
								try{
									if(Version.getVersion().getVersionInteger()>=191){
										player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 1);
									}else{
										player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 1, 1);
									}
								}catch(Exception ex){}
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
		Player player = (Player) e.getWhoClicked();
		if(inv!=null){
			if(inv.getName().equals(Methods.color(Main.settings.getTinker().getString("Settings.GUIName")))){
				e.setCancelled(true);
				ItemStack It = e.getCurrentItem();
				if(It!=null){
					if(It.hasItemMeta()){
						if(It.getItemMeta().hasLore()||It.getItemMeta().hasDisplayName()||It.getItemMeta().hasEnchants()){
							if(It.getType()!=Material.AIR){
								// Recycling things
								if(It.getItemMeta().hasDisplayName()){
									if(It.getItemMeta().getDisplayName().equals(Methods.color(Main.settings.getTinker().getString("Settings.TradeButton")))){
										int total=0;
										for(int slot : getSlot().keySet()){
											if(inv.getItem(getSlot().get(slot))!=null){
												if(Main.settings.getTinker().getString("Settings.Money/XP").equalsIgnoreCase("Money")){
													ItemStack item = inv.getItem(slot);
													total=total+getTotalXP(item);
												}else{
													if(Methods.isInvFull(((Player)player))){
														player.getWorld().dropItem(player.getLocation(), inv.getItem(getSlot().get(slot)));
													}else{
														player.getInventory().addItem(inv.getItem(getSlot().get(slot)));
													}
												}
											}
											e.getInventory().setItem(slot, new ItemStack(Material.AIR));
											e.getInventory().setItem(getSlot().get(slot), new ItemStack(Material.AIR));
										}
										player.closeInventory();
										if(total!=0)Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give "+player.getName()+" "+total);
										player.sendMessage(Methods.getPrefix()+Methods.color(Main.settings.getMsg().getString("Messages.Tinker-Sold-Msg")));
										try{
											if(Version.getVersion().getVersionInteger()>=191){
												player.playSound(player.getLocation(), Sound.valueOf("ENTITY_VILLAGER_TRADING"), 1, 1);
											}else{
												player.playSound(player.getLocation(), Sound.valueOf("VILLAGER_YES"), 1, 1);
											}
										}catch(Exception ex){}
										return;
									}
								}
								if(It.getType()!=Material.STAINED_GLASS_PANE){// Adding/Taking Items
									if(It.getType()==Main.CE.getEnchantmentBookItem().getType()){// Adding a book
										Boolean custom = false;
										Boolean toggle = false;
										String enchant = "";
										for(CEnchantments en : Main.CE.getEnchantments()){
											if(It.getItemMeta().getDisplayName().contains(Methods.color(Main.CE.getBookColor(en)+en.getCustomName()))){
												enchant = en.getName();
												toggle = true;
											}
										}
										for(String en : Main.CustomE.getEnchantments()){
											if(It.getItemMeta().getDisplayName().contains(Methods.color(Main.CustomE.getBookColor(en)+Main.CustomE.getCustomName(en)))){
												enchant = en;
												custom = true;
												toggle = true;
											}
										}
										if(toggle){
											if(inTinker(e.getRawSlot())){// Clicking in the Tinkers
												e.setCurrentItem(new ItemStack(Material.AIR));
												player.getInventory().addItem(It);
												inv.setItem(getSlot().get(e.getRawSlot()), new ItemStack(Material.AIR));
												try{
													if(Version.getVersion().getVersionInteger()>=191){
														player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK "), 1, 1);
													}else{
														player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
													}
												}catch(Exception ex){}
											}else{// Clicking in their inventory
												if(player.getOpenInventory().getTopInventory().firstEmpty()==-1){
													player.sendMessage(Methods.getPrefix()+Methods.color(Main.settings.getMsg().getString("Messages.Tinker-Inventory-Full")));
													return;
												}
												e.setCurrentItem(new ItemStack(Material.AIR));
												if(custom){
													inv.setItem(getSlot().get(inv.firstEmpty()), DustControl.getDust("MysteryDust", 1, Main.settings.getTinker().getInt("Tinker.Custom-Enchantments."+enchant+".Book")));
												}else{
													inv.setItem(getSlot().get(inv.firstEmpty()), DustControl.getDust("MysteryDust", 1, Main.settings.getTinker().getInt("Tinker.Crazy-Enchantments."+enchant+".Book")));
												}
												inv.setItem(inv.firstEmpty(), It);
												try{
													if(Version.getVersion().getVersionInteger()>=191){
														player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK "), 1, 1);
													}else{
														player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
													}
												}catch(Exception ex){}
											}
										}
									}
									if(getTotalXP(It)>0){// Adding an item
										if(inTinker(e.getRawSlot())){// Clicking in the Tinkers
											if(getSlot().containsKey(e.getRawSlot())){
												e.setCurrentItem(new ItemStack(Material.AIR));
												player.getInventory().addItem(It);
												inv.setItem(getSlot().get(e.getRawSlot()), new ItemStack(Material.AIR));
												try{
													if(Version.getVersion().getVersionInteger()>=191){
														player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK "), 1, 1);
													}else{
														player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
													}
												}catch(Exception ex){}
											}
										}else{// Clicking in their inventory
											if(player.getOpenInventory().getTopInventory().firstEmpty()==-1){
												player.sendMessage(Methods.getPrefix()+Methods.color(Main.settings.getMsg().getString("Messages.Tinker-Inventory-Full")));
												return;
											}
											e.setCurrentItem(new ItemStack(Material.AIR));
											inv.setItem(getSlot().get(inv.firstEmpty()), getBottle(It));
											inv.setItem(inv.firstEmpty(), It);
											try{
												if(Version.getVersion().getVersionInteger()>=191){
													player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK "), 1, 1);
												}else{
													player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
												}
											}catch(Exception ex){}
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
	public void onInvClose(final InventoryCloseEvent e){
		final Inventory inv = e.getInventory();
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				if(inv!=null){
					if(inv.getName().equals(Methods.color(Main.settings.getTinker().getString("Settings.GUIName")))){
						Boolean dead = e.getPlayer().isDead();
						for(int slot : getSlot().keySet()){
							if(inv.getItem(slot)!=null){
								if(inv.getItem(slot).getType()!=Material.AIR){
									if(dead){
										e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), inv.getItem(slot));
									}else{
										if(Methods.isInvFull(((Player)e.getPlayer()))){
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
			}
		}, 0);
	}
	
	private ItemStack getBottle(ItemStack item){
		String id = Main.settings.getTinker().getString("Settings.BottleOptions.Item");
		String name = Main.settings.getTinker().getString("Settings.BottleOptions.Name");
		List<String> lore = new ArrayList<String>();
		for(String l : Main.settings.getTinker().getStringList("Settings.BottleOptions.Lore")){
			lore.add(l.replaceAll("%Total%", getTotalXP(item)+"").replaceAll("%total%", getTotalXP(item)+""));
		}
		return Methods.makeItem(id, 1, name, lore);
	}
	
	private HashMap<Integer, Integer> getSlot(){
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
	
	private boolean inTinker(int slot){
		//The last slot in the tinker is 54
		if(slot<54)return true;
		return false;
	}
	
	private int getTotalXP(ItemStack item){
		int total=0;
		if(EnchantmentType.ALL.getItems().contains(item.getType())||item.getType()==Main.CE.getEnchantmentBookItem().getType()){
			if(Main.CE.hasEnchantments(item)){
				for(CEnchantments en : Main.CE.getItemEnchantments(item)){
					total += Main.settings.getTinker().getInt("Tinker.Crazy-Enchantments."+en.getName()+".Items");
				}
			}
			if(Main.CustomE.hasEnchantments(item)){
				for(String en : Main.CustomE.getItemEnchantments(item)){
					if(Main.settings.getTinker().contains("Tinker.Custom-Enchantments." + en)){
						total += Main.settings.getTinker().getInt("Tinker.Custom-Enchantments." + en + ".Items");
					}
				}
			}
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasEnchants()){
					for(Enchantment en : item.getEnchantments().keySet()){
						total += Main.settings.getTinker().getInt("Tinker.Vanilla-Enchantments."+en.getName());
					}
				}
			}
		}
		return total;
	}
	
	private Integer getXP(ItemStack item){
		List<String> lore = item.getItemMeta().getLore();
		List<String> L = Main.settings.getTinker().getStringList("Settings.BottleOptions.Lore");
		String arg = "";
		int i = 0;
		for(String l : L){
			l = Methods.color(l);
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