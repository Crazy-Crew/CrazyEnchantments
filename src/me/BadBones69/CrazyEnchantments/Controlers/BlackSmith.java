package me.BadBones69.CrazyEnchantments.Controlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.BadBones69.CrazyEnchantments.Api;
import me.BadBones69.CrazyEnchantments.Main;
import me.BadBones69.CrazyEnchantments.API.CEnchantments;
import me.BadBones69.CrazyEnchantments.API.CrazyEnchantments;

public class BlackSmith implements Listener{
	CrazyEnchantments CE = CrazyEnchantments.getInstance();
	public static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments");
	@SuppressWarnings("static-access")
	public BlackSmith(Plugin plugin){
		this.plugin = plugin;
	}
	public static void openBlackSmith(Player player){
		Inventory inv = Bukkit.createInventory(null, 27, Api.color(Main.settings.getConfig().getString("Settings.BlackSmith.GUIName")));
		List<Integer> other = new ArrayList<Integer>();
		List<Integer> result = new ArrayList<Integer>();
		other.add(1);other.add(2);other.add(3);other.add(4);other.add(5);other.add(6);
		other.add(10);other.add(12);other.add(13);other.add(15);
		other.add(19);other.add(20);other.add(21);other.add(22);other.add(23);other.add(24);
		result.add(7);result.add(8);result.add(9);
		result.add(16);;result.add(18);
		result.add(25);result.add(26);result.add(27);
		for(int i:other)inv.setItem(i-1, Api.makeItem(Material.STAINED_GLASS_PANE, 1, 7, " "));
		for(int i:result)inv.setItem(i-1, Api.makeItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
		if(Api.getVersion()<181){
			inv.setItem(16, Api.makeItem(Material.STAINED_CLAY, 1, 0, Main.settings.getConfig().getString("Settings.BlackSmith.Results.None")));
		}else{
			inv.setItem(16, Api.makeItem(Material.BARRIER, 1, 0, Main.settings.getConfig().getString("Settings.BlackSmith.Results.None")));
		}
		player.openInventory(inv);
	}
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		List<Integer> result = new ArrayList<Integer>();
		result.add(7);result.add(8);result.add(9);
		result.add(16);;result.add(18);
		result.add(25);result.add(26);result.add(27);
		Player player=(Player)e.getWhoClicked();
		Inventory inv = e.getInventory();
		if(inv!=null){
			if(inv.getName().equals(Api.color(Main.settings.getConfig().getString("Settings.BlackSmith.GUIName")))){
				e.setCancelled(true);
				if(e.getCurrentItem()!=null){
					ItemStack item = e.getCurrentItem();
					if(!inBlackSmith(e.getRawSlot())){// Click In Players Inventory
						if(item.getAmount()!=1)return;
						if(hasCustomEnchantment(item)||item.getType()==Material.BOOK){
							if(item.getType()==Material.BOOK){
								if(!item.hasItemMeta())return;
								if(!item.getItemMeta().hasDisplayName())return;
								boolean T=false;
								for(CEnchantments en : CE.getEnchantments()){
									if(item.getItemMeta().getDisplayName().contains(en.getCustomName())){
										T=true;
									}
								}
								if(!T)return;
							}
							if(inv.getItem(10)==null){
								e.setCurrentItem(new ItemStack(Material.AIR));
								inv.setItem(10, item);
								try{
									if(Api.getVersion()>=191)player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
									if(Api.getVersion()<191)player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
								}catch(Exception ex){}
							}else{
								e.setCurrentItem(new ItemStack(Material.AIR));
								if(inv.getItem(13)!=null){
									e.setCurrentItem(inv.getItem(13));
								}
								inv.setItem(13, item);
								try{
									if(Api.getVersion()>=191)player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
									if(Api.getVersion()<191)player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
								}catch(Exception ex){}
								if(getUpgradeCost(inv.getItem(10), inv.getItem(13))>0){
									inv.setItem(16, Api.addLore(getUpgradedItem(inv.getItem(10), inv.getItem(13)),
											Main.settings.getConfig().getString("Settings.BlackSmith.Results.Found")
											.replaceAll("%Cost%", getUpgradeCost(inv.getItem(10), inv.getItem(13))+"").replaceAll("%cost%", getUpgradeCost(inv.getItem(10), inv.getItem(13))+"")));
									for(int i:result)inv.setItem(i-1, Api.makeItem(Material.STAINED_GLASS_PANE, 1, 5, " "));
								}else{
									if(Api.getVersion()<181){
										inv.setItem(16, Api.makeItem(Material.STAINED_CLAY, 1, 0, Main.settings.getConfig().getString("Settings.BlackSmith.Results.None")));
									}else{
										inv.setItem(16, Api.makeItem(Material.BARRIER, 1, 0, Main.settings.getConfig().getString("Settings.BlackSmith.Results.None")));
									}
									for(int i:result)inv.setItem(i-1, Api.makeItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
								}
							}
						}
					}else{// Click In the Black Smith
						if(e.getRawSlot()==10||e.getRawSlot()==13){
							e.setCurrentItem(new ItemStack(Material.AIR));
							player.getInventory().addItem(item);
							if(Api.getVersion()<181){
								inv.setItem(16, Api.makeItem(Material.STAINED_CLAY, 1, 0, Main.settings.getConfig().getString("Settings.BlackSmith.Results.None")));
							}else{
								inv.setItem(16, Api.makeItem(Material.BARRIER, 1, 0, Main.settings.getConfig().getString("Settings.BlackSmith.Results.None")));
							}
							for(int i:result)inv.setItem(i-1, Api.makeItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
							try{
								if(Api.getVersion()>=191)player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
								if(Api.getVersion()<191)player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
							}catch(Exception ex){}
						}
						if(e.getRawSlot()==16){
							if(inv.getItem(10)!=null&&inv.getItem(13)!=null){
								if(getUpgradeCost(inv.getItem(10), inv.getItem(13))>0){
									int cost = getUpgradeCost(inv.getItem(10), inv.getItem(13));
									if(player.getGameMode()!=GameMode.CREATIVE){
										if(Main.settings.getConfig().getString("Settings.BlackSmith.Transaction.Money/XP").equalsIgnoreCase("XP")){
											if(Main.settings.getConfig().getString("Settings.BlackSmith.Transaction.Lvl/Total").equalsIgnoreCase("Total")){
												int total = Api.getTotalExperience(player);
												cost++;
												if(total<cost){
													player.sendMessage(Api.getPrefix()+Api.color(Main.settings.getMsg().getString("Messages.Need-More-Total-XP")
															.replaceAll("%XP%", cost-total+"").replaceAll("%xp%", cost-total+"")));
													return;
												}
												Api.takeTotalXP(player, cost);
											}else{
												int total = Api.getXPLvl(player);
												if(total<cost){
													player.sendMessage(Api.getPrefix()+Api.color(Main.settings.getMsg().getString("Messages.Need-More-XP-Lvls")
															.replaceAll("%XP%", cost-total+"").replaceAll("%xp%", cost-total+"")));
													return;
												}
												Api.takeLvlXP(player, cost);
											}
										}else{
											int total = (int) Api.getMoney(player);
											if(total<cost){
												player.sendMessage(Api.getPrefix()+Api.color(Main.settings.getMsg().getString("Messages.Need-More-Money")
														.replaceAll("%Money_Needed%", cost-total+"").replaceAll("%money_needed%", cost-total+"")));
												return;
											}
											Main.econ.withdrawPlayer(player, cost);
										}
									}
									player.getInventory().addItem(getUpgradedItem(inv.getItem(10),inv.getItem(13)));
									inv.setItem(10, new ItemStack(Material.AIR));
									inv.setItem(13, new ItemStack(Material.AIR));
									try{
										if(Api.getVersion()>=191){
											player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 1);
										}else{
											player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 1, 1);
										}
									}catch(Exception ex){}
									if(Api.getVersion()<181){
										inv.setItem(16, Api.makeItem(Material.STAINED_CLAY, 1, 0, Main.settings.getConfig().getString("Settings.BlackSmith.Results.None")));
									}else{
										inv.setItem(16, Api.makeItem(Material.BARRIER, 1, 0, Main.settings.getConfig().getString("Settings.BlackSmith.Results.None")));
									}
									for(int i:result)inv.setItem(i-1, Api.makeItem(Material.STAINED_GLASS_PANE, 1, 14, " "));
								}else{
									try{
										if(Api.getVersion()>=191)player.playSound(player.getLocation(), Sound.valueOf("ENTITY_VILLAGER_NO"), 1, 1);
										if(Api.getVersion()<191)player.playSound(player.getLocation(), Sound.valueOf("VILLAGER_NO"), 1, 1);
									}catch(Exception ex){}
								}
							}else{
								try{
									if(Api.getVersion()>=191)player.playSound(player.getLocation(), Sound.valueOf("ENTITY_VILLAGER_NO"), 1, 1);
									if(Api.getVersion()<191)player.playSound(player.getLocation(), Sound.valueOf("VILLAGER_NO"), 1, 1);
								}catch(Exception ex){}
							}
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void onInvClose(final InventoryCloseEvent e){
		final Inventory inv = e.getInventory();
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				if(inv!=null){
					if(inv.getName().equals(Api.color(Main.settings.getConfig().getString("Settings.BlackSmith.GUIName")))){
						List<Integer> slots = new ArrayList<Integer>();
						slots.add(10);slots.add(13);
						Boolean dead = e.getPlayer().isDead();
						for(int slot : slots){
							if(inv.getItem(slot)!=null){
								if(inv.getItem(slot).getType()!=Material.AIR){
									if(dead){
										e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), inv.getItem(slot));
									}else{
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
			}
		}, 0);
	}
	ItemStack getUpgradedItem(ItemStack master, ItemStack sub){
		ItemStack item = master.clone();
		if(master.getType()==Material.BOOK&&sub.getType()==Material.BOOK){
			if(Api.removeColor(master.getItemMeta().getDisplayName()).equalsIgnoreCase(Api.removeColor(sub.getItemMeta().getDisplayName()))){
				for(CEnchantments en : CE.getEnchantments()){
					String ench = en.getCustomName();
					if(master.getItemMeta().getDisplayName().contains(ench)){
						int power = CE.getBookPower(master, en);
						int max = 1;
						if(Main.settings.getEnchs().contains("Enchantments."+en.getName())){
							max=Main.settings.getEnchs().getInt("Enchantments."+en.getName()+".MaxPower");
						}/*else{
							max=Main.settings.getCustomEnchs().getInt("Enchantments."+en.getName()+".MaxPower");
						}*/
						if(power+1<=max){
							item=Api.addGlow(Api.makeItem(Material.BOOK, 1, 0, en.getBookColor()+ench+" "+Api.getPower(power+1), master.getItemMeta().getLore()));
						}
					}
				}
			}
		}
		if(master.getType()!=Material.BOOK&&sub.getType()!=Material.BOOK){
			List<String> d = new ArrayList<String>();//Dup Enchantments
			List<String> n = new ArrayList<String>();//New Enchantments
			List<String> m = new ArrayList<String>();//Master's Enchantments
			List<String> s = new ArrayList<String>();//Sub's Enchantments
			for(CEnchantments en : CE.getEnchantments()){
				String ench = en.getCustomName();
				for(String lore : master.getItemMeta().getLore()){
					if(lore.contains(ench)){
						m.add(lore);
					}
				}
				for(String lore : sub.getItemMeta().getLore()){
					if(lore.contains(ench)){
						s.add(lore);
					}
				}
			}
			for(String l : s){
				boolean T=false;
				for(String lore : m){
					if(l.contains(Api.removeColor(lore))){
						T=true;
						d.add(l);
					}
				}
				if(!T){
					n.add(l);
				}
			}
			for(String l : d){
				for(CEnchantments en : CE.getEnchantments()){
					String ench = en.getCustomName();
					if(l.contains(ench)){
						int power = CE.getPower(item, en);
						int max = 1;
						if(Main.settings.getEnchs().contains("Enchantments."+en.getName())){
							max=Main.settings.getEnchs().getInt("Enchantments."+en.getName()+".MaxPower");
						}/*else{
							max=Main.settings.getCustomEnchs().getInt("Enchantments."+en.getName()+".MaxPower");
						}*/
						if(power+1<=max){
							item=Api.replaceLore(item, l,en.getEnchantmentColor()+ench+" "+Api.getPower(power+1));
						}
					}
				}
			}
			for(String lore : n){
				boolean T=false;
				for(CEnchantments en : CE.getEnchantments()){
					String ench = en.getCustomName();
					if(lore.contains(ench)){
						for(String l : item.getItemMeta().getLore()){
							if(l.contains(ench))T=true;
						}
					}
				}
				if(!T){
					int maxEnchants = Main.settings.getConfig().getInt("Settings.EnchantmentOptions.MaxAmountOfEnchantments");
					if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle")){
						if(Api.getEnchAmount(item)<maxEnchants){
							item=Api.addLore(item, lore);
						}
					}
				}
			}
		}
		return item;
	}
	int getUpgradeCost(ItemStack master, ItemStack sub){
		int total = 0;
		if(master.getType()==Material.BOOK&&sub.getType()==Material.BOOK){
			if(Api.removeColor(master.getItemMeta().getDisplayName()).equalsIgnoreCase(Api.removeColor(sub.getItemMeta().getDisplayName()))){
				for(CEnchantments en : CE.getEnchantments()){
					String ench = en.getCustomName();
					if(master.getItemMeta().getDisplayName().contains(ench)){
						int power = CE.getBookPower(master, en);
						int max = 1;
						if(Main.settings.getEnchs().contains("Enchantments."+en.getName())){
							max=Main.settings.getEnchs().getInt("Enchantments."+en.getName()+".MaxPower");
						}/*else{
							max=Main.settings.getCustomEnchs().getInt("Enchantments."+en.getName()+".MaxPower");
						}*/
						if(power+1<=max){
							total+=Main.settings.getConfig().getInt("Settings.BlackSmith.Transaction.Costs.Book-Upgrade");
						}
					}
				}
			}
		}
		if(master.getType()!=Material.BOOK||sub.getType()!=Material.BOOK){
			ItemStack item = master.clone();
			List<String> d = new ArrayList<String>();//Dup Enchantments
			List<String> n = new ArrayList<String>();//New Enchantments
			List<String> m = new ArrayList<String>();//Master's Enchantments
			List<String> s = new ArrayList<String>();//Sub's Enchantments
			for(CEnchantments en : CE.getEnchantments()){
				String ench = en.getCustomName();
				for(String lore : master.getItemMeta().getLore()){
					if(lore.contains(ench)){
						m.add(lore);
					}
				}
				for(String lore : sub.getItemMeta().getLore()){
					if(lore.contains(ench)){
						s.add(lore);
					}
				}
			}
			for(String l : s){
				boolean T=false;
				for(String lore : m){
					if(l.contains(Api.removeColor(lore))){
						T=true;
						d.add(l);
					}
				}
				if(!T){
					n.add(l);
				}
			}
			for(String l : d){
				for(CEnchantments en : CE.getEnchantments()){
					String ench = en.getCustomName();
					if(l.contains(ench)){
						int power = CE.getPower(item, en);
						int max = 1;
						if(Main.settings.getEnchs().contains("Enchantments."+en.getName())){
							max=Main.settings.getEnchs().getInt("Enchantments."+en.getName()+".MaxPower");
						}/*else{
							max=Main.settings.getCustomEnchs().getInt("Enchantments."+en.getName()+".MaxPower");
						}*/
						if(power+1<=max){
							item=Api.replaceLore(item, l, CE.getEnchantmentColor(en)+ench+" "+Api.getPower(power+1));
							total+=Main.settings.getConfig().getInt("Settings.BlackSmith.Transaction.Costs.Power-Up");
						}
					}
				}
			}
			for(String lore : n){
				boolean T=false;
				for(CEnchantments en : CE.getEnchantments()){
					String ench = en.getCustomName();
					if(lore.contains(ench)){
						for(String l : item.getItemMeta().getLore()){
							if(l.contains(ench))T=true;
						}
					}
				}
				if(!T){
					int maxEnchants = Main.settings.getConfig().getInt("Settings.EnchantmentOptions.MaxAmountOfEnchantments");
					if(Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle")){
						if(Api.getEnchAmount(item)<maxEnchants){
							total+=Main.settings.getConfig().getInt("Settings.BlackSmith.Transaction.Costs.Add-Enchantment");
						}
					}
				}
			}
		}
		return total;
	}
	boolean hasCustomEnchantment(ItemStack item){
		if(item.hasItemMeta()){
			if(item.getItemMeta().hasLore()){
				for(String lore : item.getItemMeta().getLore()){
					for(CEnchantments en : CE.getEnchantments()){
						if(lore.contains(en.getCustomName())){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	boolean inBlackSmith(int slot){
		//The last slot in the tinker is 54
		if(slot<27)return true;
		return false;
	}
}