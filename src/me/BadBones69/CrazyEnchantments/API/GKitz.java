package me.badbones69.crazyenchantments.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;

public class GKitz implements Listener{
	
	private static ArrayList<String> gkitz = new ArrayList<String>();
	private static HashMap<String, String> names = new HashMap<String, String>();
	private static HashMap<String, String> times = new HashMap<String, String>();
	private static HashMap<String, List<String>> commands = new HashMap<String, List<String>>();
	private static HashMap<String, ArrayList<ItemStack>> items = new HashMap<String, ArrayList<ItemStack>>();
	private static HashMap<Player, HashMap<String, Calendar>> cooldown = new HashMap<Player, HashMap<String, Calendar>>();
	
	/**
	 * Loads all the GKitz information.
	 */
	public static void load(){
		gkitz.clear();
		names.clear();
		times.clear();
		items.clear();
		commands.clear();
		cooldown.clear();
		FileConfiguration file = Main.settings.getGKitz();
		for(String kit : file.getConfigurationSection("GKitz").getKeys(false)){
			gkitz.add(kit);
			names.put(kit, Methods.color(file.getString("GKitz." + kit + ".Display.Name")));
			times.put(kit, file.getString("GKitz." + kit + ".Cooldown"));
			if(file.contains("GKitz." + kit + ".Commands")){
				commands.put(kit, file.getStringList("GKitz." + kit + ".Commands"));
			}
		}
		reloadKits();
		for(Player player : Bukkit.getServer().getOnlinePlayers()){
			loadPlayerCooldown(player);
		}
	}
	
	/**
	 * Unloads all online players and saves their cooldowns
	 */
	public static void unload(){
		for(Player player : Bukkit.getServer().getOnlinePlayers()){
			unloadPlayerCooldown(player);
		}
	}
	
	/**
	 * Reloads all the kit items.
	 */
	public static void reloadKits(){
		FileConfiguration file = Main.settings.getGKitz();
		for(String kit : file.getConfigurationSection("GKitz").getKeys(false)){
			ArrayList<ItemStack> its = new ArrayList<ItemStack>();
			for(String i : file.getStringList("GKitz." + kit + ".Items")){
				GKitzItem item = new GKitzItem();
				for(String d : i.split(", ")){
					if(d.startsWith("Item:")){
						item.setItem(d.replace("Item:", ""));
					}else if(d.startsWith("Amount:")){
						if(Methods.isInt(d.replace("Amount:", ""))){
							item.setAmount(Integer.parseInt(d.replace("Amount:", "")));
						}
					}
					else if(d.startsWith("Name:")){
						item.setName(d.replace("Name:", ""));
					}
					else if(d.startsWith("Lore:")){
						d = d.replace("Lore:", "");
						ArrayList<String> lore = new ArrayList<String>();
						if(d.contains(",")){
							for(String D : d.split(",")){
								lore.add(D);
							}
						}else{
							lore.add(d);
						}
						item.setLore(lore);
					}else if(d.startsWith("Enchantments:")){
						d = d.replace("Enchantments:", "");
						if(d.contains(",")){
							for(String D : d.split(",")){
								if(D.contains(":")){
									if(D.contains("-")){
										Integer min = Integer.parseInt(D.split(":")[1].split("-")[0]);
										Integer max = Integer.parseInt(D.split(":")[1].split("-")[1]);
										int level = levelPick(max, min);
										if(level > 0){
											if(Enchantment.getByName(D.split(":")[0]) != null){
												item.addEnchantment(Enchantment.getByName(D.split(":")[0]), level);
											}
											for(Enchantment en : Enchantment.values()){
												if(Methods.getEnchantmentName(en).equalsIgnoreCase(D.split(":")[0])){
													item.addEnchantment(en, level);
												}
											}
										}
									}else{
										if(Enchantment.getByName(D.split(":")[0]) != null){
											item.addEnchantment(Enchantment.getByName(D.split(":")[0]), Integer.parseInt(D.split(":")[1]));
										}
										for(Enchantment en : Enchantment.values()){
											if(Methods.getEnchantmentName(en).equalsIgnoreCase(D.split(":")[0])){
												item.addEnchantment(en, Integer.parseInt(D.split(":")[1]));
											}
										}
									}
								}else{
									if(Enchantment.getByName(D) != null){
										item.addEnchantment(Enchantment.getByName(D), 1);
									}
									for(Enchantment en : Enchantment.values()){
										if(Methods.getEnchantmentName(en).equalsIgnoreCase(D.split(":")[0])){
											item.addEnchantment(en, Integer.parseInt(D.split(":")[1]));
										}
									}
								}
							}
						}else{
							if(d.contains(":")){
								if(d.contains("-")){
									Integer min = Integer.parseInt(d.split(":")[1].split("-")[0]);
									Integer max = Integer.parseInt(d.split(":")[1].split("-")[1]);
									int level = levelPick(max, min);
									if(level > 0){
										if(Enchantment.getByName(d.split(":")[0]) != null){
											item.addEnchantment(Enchantment.getByName(d.split(":")[0]), level);
										}
										for(Enchantment en : Enchantment.values()){
											if(Methods.getEnchantmentName(en).equalsIgnoreCase(d.split(":")[0])){
												item.addEnchantment(en, level);
											}
										}
									}
								}else{
									if(Enchantment.getByName(d.split(":")[0]) != null){
										item.addEnchantment(Enchantment.getByName(d.split(":")[0]), Integer.parseInt(d.split(":")[1]));
									}
									for(Enchantment en : Enchantment.values()){
										if(Methods.getEnchantmentName(en).equalsIgnoreCase(d.split(":")[0])){
											item.addEnchantment(en, Integer.parseInt(d.split(":")[1]));
										}
									}
								}
							}else{
								if(Enchantment.getByName(d) != null){
									item.addEnchantment(Enchantment.getByName(d), 1);
								}
								for(Enchantment en : Enchantment.values()){
									if(Methods.getEnchantmentName(en).equalsIgnoreCase(d.split(":")[0])){
										item.addEnchantment(en, Integer.parseInt(d.split(":")[1]));
									}
								}
							}
						}
					}else if(d.startsWith("CustomEnchantments:")){
						d = d.replace("CustomEnchantments:", "");
						if(d.contains(",")){
							for(String D : d.split(",")){
								if(D.contains(":")){
									if(D.contains("-")){
										String enchant = D.split(":")[0];
										Integer min = Integer.parseInt(D.split(":")[1].split("-")[0]);
										Integer max = Integer.parseInt(D.split(":")[1].split("-")[1]);
										int level = levelPick(max, min);
										if(Main.CE.isEnchantment(enchant)){
											if(level > 0){
												item.addCEEnchantment(Main.CE.getFromName(enchant), level);
											}
										}else if(Main.CustomE.isEnchantment(enchant)){
											if(level > 0){
												item.addCustomEnchantment(enchant, level);
											}
										}
									}else{
										String enchant = D.split(":")[0];
										if(Main.CE.isEnchantment(enchant)){
											item.addCEEnchantment(Main.CE.getFromName(enchant), Integer.parseInt(D.split(":")[1]));
										}else if(Main.CustomE.isEnchantment(enchant)){
											item.addCustomEnchantment(enchant, Integer.parseInt(d.split(":")[1]));
										}
									}
								}else{
									String enchant = D;
									if(Main.CE.isEnchantment(enchant)){
										item.addCEEnchantment(Main.CE.getFromName(enchant), 1);
									}else if(Main.CustomE.isEnchantment(enchant)){
										item.addCustomEnchantment(enchant, 1);
									}
								}
							}
						}else{
							if(d.contains(":")){
								if(d.contains("-")){
									String enchant = d.split(":")[0];
									Integer min = Integer.parseInt(d.split(":")[1].split("-")[0]);
									Integer max = Integer.parseInt(d.split(":")[1].split("-")[1]);
									int level = levelPick(max, min);
									if(Main.CE.isEnchantment(enchant)){
										if(level > 0){
											item.addCEEnchantment(Main.CE.getFromName(enchant), level);
										}
									}else if(Main.CustomE.isEnchantment(enchant)){
										if(level > 0){
											item.addCustomEnchantment(enchant, level);
										}
									}
								}else{
									String enchant = d.split(":")[0];
									if(Main.CE.isEnchantment(enchant)){
										item.addCEEnchantment(Main.CE.getFromName(enchant), Integer.parseInt(d.split(":")[1]));
									}else if(Main.CustomE.isEnchantment(enchant)){
										item.addCustomEnchantment(enchant, Integer.parseInt(d.split(":")[1]));
									}
								}
							}else{
								String enchant = d;
								if(Main.CE.isEnchantment(enchant)){
									item.addCEEnchantment(Main.CE.getFromName(enchant), 1);
								}else if(Main.CustomE.isEnchantment(enchant)){
									item.addCustomEnchantment(enchant, 1);
								}
							}
						}
					}
				}
				its.add(item.build());
			}
			items.put(kit, its);
		}
	}
	
	public static ArrayList<ItemStack> getInfoGKit(String kit){
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		FileConfiguration gkitz = Main.settings.getGKitz();
		for(String i : gkitz.getStringList("GKitz." + kit + ".Items")){
			String type = "";
			int amount = 0;
			String name = "";
			ArrayList<String> lore = new ArrayList<String>();
			ArrayList<String> customEnchantments = new ArrayList<String>();
			HashMap<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
			for(String d : i.split(", ")){
				if(d.startsWith("Item:")){
					d = d.replace("Item:", "");
					type = d;
				}else if(d.startsWith("Amount:")){
					d = d.replace("Amount:", "");
					if(Methods.isInt(d)){
						amount = Integer.parseInt(d);
					}
				}else if(d.startsWith("Name:")){
					d = d.replaceAll("Name:", "");
					name = d;
				}else if(d.startsWith("Lore:")){
					d = d.replace("Lore:", "");
					if(d.contains(",")){
						for(String D : d.split(",")){
							lore.add(D);
						}
					}else{
						lore.add(d);
					}
				}else if(d.startsWith("Enchantments:")){
					d = d.replace("Enchantments:", "");
					if(d.contains(",")){
						for(String D : d.split(",")){
							if(D.contains(":")){
								if(D.contains("-")){
									customEnchantments.add("&7" + D.replaceAll(":", " "));
								}else{
									if(Enchantment.getByName(D.split(":")[0]) != null){
										enchantments.put(Enchantment.getByName(D.split(":")[0]), Integer.parseInt(D.split(":")[1]));
									}
									for(Enchantment en : Enchantment.values()){
										if(Methods.getEnchantmentName(en).equalsIgnoreCase(D.split(":")[0])){
											enchantments.put(en, Integer.parseInt(D.split(":")[1]));
										}
									}
								}
							}else{
								if(Enchantment.getByName(D) != null){
									enchantments.put(Enchantment.getByName(D), 1);
								}
								for(Enchantment en : Enchantment.values()){
									if(Methods.getEnchantmentName(en).equalsIgnoreCase(D)){
										enchantments.put(en, 1);
									}
								}
							}
						}
					}else{
						if(d.contains(":")){
							if(Enchantment.getByName(d.split(":")[0]) != null){
								enchantments.put(Enchantment.getByName(d.split(":")[0]), Integer.parseInt(d.split(":")[1]));
							}
							for(Enchantment en : Enchantment.values()){
								if(Methods.getEnchantmentName(en).equalsIgnoreCase(d.split(":")[0])){
									enchantments.put(en, Integer.parseInt(d.split(":")[1]));
								}
							}
						}else{
							if(Enchantment.getByName(d) != null){
								enchantments.put(Enchantment.getByName(d), 1);
							}
							for(Enchantment en : Enchantment.values()){
								if(Methods.getEnchantmentName(en).equalsIgnoreCase(d)){
									enchantments.put(en, 1);
								}
							}
						}
					}
				}else if(d.startsWith("CustomEnchantments:")){
					d = d.replace("CustomEnchantments:", "");
					for(String D : d.split(",")){
						customEnchantments.add("&7" + D.replaceAll(":", " "));
					}
				}
			}
			lore.addAll(0, customEnchantments);
			items.add(Methods.makeItem(type, amount, name, lore, enchantments));
		}
		return items;
	}
	
	/**
	 * 
	 * @param player The player who is getting their cool down loaded
	 */
	public static void loadPlayerCooldown(Player player){
		String uuid = player.getUniqueId().toString();
		FileConfiguration data = Main.settings.getData();
		HashMap<String, Calendar> cd = new HashMap<String, Calendar>();
		if(data.contains("Players." + uuid)){
			for(String kit : getGKitz()){
				if(data.contains("Players." + uuid + ".GKitz." + kit)){
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(data.getLong("Players." + uuid + ".GKitz." + kit));
					cd.put(kit, cal);
				}
			}
			cooldown.put(player, cd);
		}
	}
	
	/**
	 * 
	 * @param player The player who is getting their cool down unloaded
	 */
	public static void unloadPlayerCooldown(Player player){
		String uuid = player.getUniqueId().toString();
		FileConfiguration data = Main.settings.getData();
		if(cooldown.containsKey(player)){
			if(cooldown.get(player).keySet().size() > 0){
				data.set("Players." + uuid + ".Name", player.getName());
			}
			for(String kit : cooldown.get(player).keySet()){
				data.set("Players." + uuid + ".GKitz." + kit, cooldown.get(player).get(kit).getTimeInMillis());
			}
			Main.settings.saveData();
			cooldown.remove(player);
		}
	}
	
	/**
	 * 
	 * @param kit GKit you want the name for
	 * @return Returns the GKitz's display name
	 */
	public static String getGKitDisplayName(String kit){
		return names.get(kit);
	}
	
	/**
	 * 
	 * @return Returns all the available GKitz
	 */
	public static ArrayList<String> getGKitz(){
		return gkitz;
	}
	
	/**
	 * 
	 * @param kit Kit you are checking
	 * @return Returns true if a Gkit and false if not
	 */
	public static Boolean isGKit(String kit){
		for(String k : gkitz){
			if(k.equalsIgnoreCase(kit)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param kit Kit you are checking
	 * @return Returns the GKitz's name
	 */
	public static String getGKitName(String kit){
		for(String k : gkitz){
			if(k.equalsIgnoreCase(kit)){
				return k;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param kit Kit you wish to have
	 * @return Returns all the items in the kit
	 */
	public static ArrayList<ItemStack> getGKit(String kit){
		reloadKits();
		return items.get(kit);
	}
	
	/**
	 * 
	 * @param player Player you are giving the GKit to
	 * @param kit GKit you wish to give
	 */
	public static void giveKit(Player player, String kit){
		for(ItemStack item : getGKit(kit)){
			if(Methods.isInvFull(player)){
				player.getWorld().dropItem(player.getLocation(), item);
			}else{
				player.getInventory().addItem(item);
			}
		}
	}
	
	/**
	 * 
	 * @param player Player you wish to send the commands to
	 * @param kit GKit you wish to use
	 */
	public static void runKitCommands(Player player, String kit){
		if(commands.containsKey(kit)){
			for(String cmd : commands.get(kit)){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName()));
			}
		}
	}
	
	/**
	 * 
	 * @param player Player you are checking
	 * @param kit Kit you want to check
	 * @return Returns true if they do have permission and false if not
	 */
	public static Boolean hasGKitPermission(Player player, String kit){
		if(player.hasPermission("crazyenchantments.gkitz." + kit) || player.hasPermission("crazyenchantments.bypass")){
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param player Player you are checking the cool down on
	 * @param kit The kit you are checking
	 * @return Returns true if the cool down is over and false if not
	 */
	public static Boolean canGetGKit(Player player, String kit){
		if(player.hasPermission("crazyenchantments.bypass")){
			return true;
		}
		Calendar cal = Calendar.getInstance();
		if(cooldown.containsKey(player)){
			if(cooldown.get(player).containsKey(kit)){
				if(cal.after(cooldown.get(player).get(kit))){
					return true;
				}
			}else{
				return true;
			}
		}else{
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param player Player you are adding the cool down to
	 * @param kit The kit you wish to add the cool down to
	 */
	public static void addCooldown(Player player, String kit){
		if(cooldown.containsKey(player)){
			cooldown.get(player).put(kit, getCooldown(kit));
		}else{
			HashMap<String, Calendar> cd = new HashMap<String, Calendar>();
			cd.put(kit, getCooldown(kit));
			cooldown.put(player, cd);
		}
	}
	
	/**
	 * 
	 * @param cal The cooldown time
	 * @param msg The message you are adding the placeholders to
	 * @return A message that has replaced the placeholder
	 */
	public static String getCooldownLeft(Calendar cal, String msg){
		Calendar C = Calendar.getInstance();
		int total = ((int) (cal.getTimeInMillis()/1000) - (int) (C.getTimeInMillis()/1000));
		int D = 0;
		int H = 0;
		int M = 0;
		int S = 0;
		for(;total > 86400; total -= 86400 ,D++);
		for(;total > 3600; total -= 3600, H++);
		for(;total > 60; total -= 60, M++);
		S += total;
		return Methods.color(msg.replaceAll("%Day%", D + "").replaceAll("%day%", D + "")
				.replaceAll("%Hour%", H + "").replaceAll("%hour%", H + "")
				.replaceAll("%Minute%", M + "").replaceAll("%minute%", M + "")
				.replaceAll("%Second%", S + "").replaceAll("%second%", S + ""));
	}
	
	/**
	 * 
	 * @param player Player you wish to get the cooldown from
	 * @param kit GKit you wish to get the cooldown from
	 * @return A calendar with when the cooldown ends
	 */
	public static Calendar getCooldown(Player player, String kit){
		return cooldown.get(player).get(kit);
	}
	
	private static Calendar getCooldown(String kit){
		Calendar cal = Calendar.getInstance();
		String time = times.get(kit);
		for(String i : time.split(" ")){
			if(i.contains("D")||i.contains("d")){
				cal.add(Calendar.DATE, Integer.parseInt(i.replaceAll("D", "").replaceAll("d", "")));
			}
			if(i.contains("H")||i.contains("h")){
				cal.add(Calendar.HOUR, Integer.parseInt(i.replaceAll("H", "").replaceAll("h", "")));
			}
			if(i.contains("M")||i.contains("m")){
				cal.add(Calendar.MINUTE, Integer.parseInt(i.replaceAll("M", "").replaceAll("m", "")));
			}
			if(i.contains("S")||i.contains("s")){
				cal.add(Calendar.SECOND, Integer.parseInt(i.replaceAll("S", "").replaceAll("s", "")));
			}
		}
		return cal;
	}
	
	private static Integer levelPick(int max, int min){
		max++;
		Random i = new Random();
		return min+i.nextInt(max-min);
	}
	
	@EventHandler
	public static void onPlayerJoin(PlayerJoinEvent e){
		loadPlayerCooldown(e.getPlayer());
	}
	
	@EventHandler
	public static void onPlayerLeave(PlayerQuitEvent e){
		unloadPlayerCooldown(e.getPlayer());
	}
	
}