package me.badbones69.crazyenchantments.api;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class DataStorage {

	private static int rageMaxLevel;
	private static Boolean breakRageOnDamage;
	private static ArrayList<GKitz> gkitz = new ArrayList<>();
	private static ArrayList<CEPlayer> players = new ArrayList<>();
	private static ArrayList<Material> blockList = new ArrayList<>();

	public static void load() {
		blockList.clear();
		gkitz.clear();
		for(String id : Main.settings.getBlockList().getStringList("Block-List")) {
			try {
				blockList.add(new ItemBuilder().setMaterial(id).getMaterial());
			}catch(Exception e) {
			}
		}
		if(Main.settings.getConfig().contains("Settings.EnchantmentOptions.MaxRageLevel")) {
			rageMaxLevel = Main.settings.getConfig().getInt("Settings.EnchantmentOptions.MaxRageLevel");
		}else {
			rageMaxLevel = 4;
		}
		if(Main.settings.getConfig().contains("Settings.EnchantmentOptions.Break-Rage-On-Damage")) {
			breakRageOnDamage = Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.Break-Rage-On-Damage");
		}else {
			breakRageOnDamage = true;
		}
		FileConfiguration gkit = Main.settings.getGKitz();
		for(String kit : gkit.getConfigurationSection("GKitz").getKeys(false)) {
			int slot = gkit.getInt("GKitz." + kit + ".Display.Slot");
			String time = gkit.getString("GKitz." + kit + ".Cooldown");
			Boolean autoEquip = false;
			if(gkit.contains("GKitz." + kit + ".Auto-Equip")) {
				autoEquip = gkit.getBoolean("GKitz." + kit + ".Auto-Equip");
			}
			ItemStack displayItem = new ItemBuilder().setMaterial(gkit.getString("GKitz." + kit + ".Display.Item")).setName(gkit.getString("GKitz." + kit + ".Display.Name")).setLore(gkit.getStringList("GKitz." + kit + ".Display.Lore")).setGlowing(gkit.getBoolean("GKitz." + kit + ".Display.Glowing")).build();
			ArrayList<String> commands = (ArrayList<String>) gkit.getStringList("GKitz." + kit + ".Commands");
			ArrayList<String> itemStrings = (ArrayList<String>) gkit.getStringList("GKitz." + kit + ".Items");
			gkitz.add(new GKitz(kit, slot, time, displayItem, getInfoGKit(itemStrings), commands, getKitItems(itemStrings), itemStrings, autoEquip));
		}
	}

	public static int getRageMaxLevel() {
		return rageMaxLevel;
	}

	public static void setRageMaxLevel(int level) {
		rageMaxLevel = level;
	}

	public static Boolean isBreakRageOnDamageOn() {
		return breakRageOnDamage;
	}

	public static void setBreakRageOnDamage(Boolean toggle) {
		breakRageOnDamage = toggle;
	}

	public static ArrayList<Material> getBlockList() {
		return blockList;
	}

	public static ArrayList<CEPlayer> getCEPlayers() {
		return players;
	}

	public static void addCEPlayer(CEPlayer player) {
		players.add(player);
	}

	public static void removeCEPlayer(CEPlayer player) {
		players.remove(player);
	}

	public static ArrayList<GKitz> getGKitz() {
		return gkitz;
	}

	public static void addGKit(GKitz kit) {
		gkitz.add(kit);
	}

	public static void removeGKit(GKitz kit) {
		gkitz.remove(kit);
	}

	private static ArrayList<ItemStack> getInfoGKit(ArrayList<String> itemStrings) {
		ArrayList<ItemStack> items = new ArrayList<>();
		for(String i : itemStrings) {
			String type = "";
			int amount = 0;
			String name = "";
			ArrayList<String> lore = new ArrayList<String>();
			ArrayList<String> customEnchantments = new ArrayList<String>();
			HashMap<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
			for(String d : i.split(", ")) {
				if(d.startsWith("Item:")) {
					d = d.replace("Item:", "");
					type = d;
				}else if(d.startsWith("Amount:")) {
					d = d.replace("Amount:", "");
					if(Methods.isInt(d)) {
						amount = Integer.parseInt(d);
					}
				}else if(d.startsWith("Name:")) {
					d = d.replaceAll("Name:", "");
					name = d;
				}else if(d.startsWith("Lore:")) {
					d = d.replace("Lore:", "");
					if(d.contains(",")) {
						for(String D : d.split(",")) {
							lore.add(D);
						}
					}else {
						lore.add(d);
					}
				}else if(d.startsWith("Enchantments:")) {
					d = d.replace("Enchantments:", "");
					if(d.contains(",")) {
						for(String D : d.split(",")) {
							if(D.contains(":")) {
								if(D.contains("-")) {
									customEnchantments.add("&7" + D.replaceAll(":", " "));
								}else {
									if(Enchantment.getByName(D.split(":")[0]) != null) {
										enchantments.put(Enchantment.getByName(D.split(":")[0]), Integer.parseInt(D.split(":")[1]));
									}
									for(Enchantment en : Enchantment.values()) {
										if(Methods.getEnchantmentName(en).equalsIgnoreCase(D.split(":")[0])) {
											enchantments.put(en, Integer.parseInt(D.split(":")[1]));
										}
									}
								}
							}else {
								if(Enchantment.getByName(D) != null) {
									enchantments.put(Enchantment.getByName(D), 1);
								}
								for(Enchantment en : Enchantment.values()) {
									if(Methods.getEnchantmentName(en).equalsIgnoreCase(D)) {
										enchantments.put(en, 1);
									}
								}
							}
						}
					}else {
						if(d.contains(":")) {
							if(Enchantment.getByName(d.split(":")[0]) != null) {
								enchantments.put(Enchantment.getByName(d.split(":")[0]), Integer.parseInt(d.split(":")[1]));
							}
							for(Enchantment en : Enchantment.values()) {
								if(Methods.getEnchantmentName(en).equalsIgnoreCase(d.split(":")[0])) {
									enchantments.put(en, Integer.parseInt(d.split(":")[1]));
								}
							}
						}else {
							if(Enchantment.getByName(d) != null) {
								enchantments.put(Enchantment.getByName(d), 1);
							}
							for(Enchantment en : Enchantment.values()) {
								if(Methods.getEnchantmentName(en).equalsIgnoreCase(d)) {
									enchantments.put(en, 1);
								}
							}
						}
					}
				}else if(d.startsWith("CustomEnchantments:")) {
					d = d.replace("CustomEnchantments:", "");
					for(String D : d.split(",")) {
						customEnchantments.add("&7" + D.replaceAll(":", " "));
					}
				}
			}
			lore.addAll(0, customEnchantments);
			items.add(new ItemBuilder().setMaterial(type).setAmount(amount).setName(name).setLore(lore).setEnchantments(enchantments).build());
		}
		return items;
	}

	public static ArrayList<ItemStack> getKitItems(ArrayList<String> itemStrings) {
		ArrayList<ItemStack> items = new ArrayList<>();
		for(String i : itemStrings) {
			GKitzItem item = new GKitzItem();
			for(String d : i.split(", ")) {
				if(d.startsWith("Item:")) {
					item.setItem(d.replace("Item:", ""));
				}else if(d.startsWith("Amount:")) {
					if(Methods.isInt(d.replace("Amount:", ""))) {
						item.setAmount(Integer.parseInt(d.replace("Amount:", "")));
					}
				}else if(d.startsWith("Name:")) {
					item.setName(d.replace("Name:", ""));
				}else if(d.startsWith("Lore:")) {
					d = d.replace("Lore:", "");
					ArrayList<String> lore = new ArrayList<String>();
					if(d.contains(",")) {
						for(String D : d.split(",")) {
							lore.add(D);
						}
					}else {
						lore.add(d);
					}
					item.setLore(lore);
				}else if(d.startsWith("Enchantments:")) {
					d = d.replace("Enchantments:", "");
					if(d.contains(",")) {
						for(String D : d.split(",")) {
							if(D.contains(":")) {
								if(D.contains("-")) {
									Integer min = Integer.parseInt(D.split(":")[1].split("-")[0]);
									Integer max = Integer.parseInt(D.split(":")[1].split("-")[1]);
									int level = pickLevel(max, min);
									if(level > 0) {
										if(Enchantment.getByName(D.split(":")[0]) != null) {
											item.addEnchantment(Enchantment.getByName(D.split(":")[0]), level);
										}
										for(Enchantment en : Enchantment.values()) {
											if(Methods.getEnchantmentName(en).equalsIgnoreCase(D.split(":")[0])) {
												item.addEnchantment(en, level);
											}
										}
									}
								}else {
									if(Enchantment.getByName(D.split(":")[0]) != null) {
										item.addEnchantment(Enchantment.getByName(D.split(":")[0]), Integer.parseInt(D.split(":")[1]));
									}
									for(Enchantment en : Enchantment.values()) {
										if(Methods.getEnchantmentName(en).equalsIgnoreCase(D.split(":")[0])) {
											item.addEnchantment(en, Integer.parseInt(D.split(":")[1]));
										}
									}
								}
							}else {
								if(Enchantment.getByName(D) != null) {
									item.addEnchantment(Enchantment.getByName(D), 1);
								}
								for(Enchantment en : Enchantment.values()) {
									if(Methods.getEnchantmentName(en).equalsIgnoreCase(D.split(":")[0])) {
										item.addEnchantment(en, Integer.parseInt(D.split(":")[1]));
									}
								}
							}
						}
					}else {
						if(d.contains(":")) {
							if(d.contains("-")) {
								Integer min = Integer.parseInt(d.split(":")[1].split("-")[0]);
								Integer max = Integer.parseInt(d.split(":")[1].split("-")[1]);
								int level = pickLevel(max, min);
								if(level > 0) {
									if(Enchantment.getByName(d.split(":")[0]) != null) {
										item.addEnchantment(Enchantment.getByName(d.split(":")[0]), level);
									}
									for(Enchantment en : Enchantment.values()) {
										if(Methods.getEnchantmentName(en).equalsIgnoreCase(d.split(":")[0])) {
											item.addEnchantment(en, level);
										}
									}
								}
							}else {
								if(Enchantment.getByName(d.split(":")[0]) != null) {
									item.addEnchantment(Enchantment.getByName(d.split(":")[0]), Integer.parseInt(d.split(":")[1]));
								}
								for(Enchantment en : Enchantment.values()) {
									if(Methods.getEnchantmentName(en).equalsIgnoreCase(d.split(":")[0])) {
										item.addEnchantment(en, Integer.parseInt(d.split(":")[1]));
									}
								}
							}
						}else {
							if(Enchantment.getByName(d) != null) {
								item.addEnchantment(Enchantment.getByName(d), 1);
							}
							for(Enchantment en : Enchantment.values()) {
								if(Methods.getEnchantmentName(en).equalsIgnoreCase(d.split(":")[0])) {
									item.addEnchantment(en, Integer.parseInt(d.split(":")[1]));
								}
							}
						}
					}
				}else if(d.startsWith("CustomEnchantments:")) {
					d = d.replace("CustomEnchantments:", "");
					if(d.contains(",")) {
						for(String D : d.split(",")) {
							if(D.contains(":")) {
								if(D.contains("-")) {
									String enchant = D.split(":")[0];
									Integer min = Integer.parseInt(D.split(":")[1].split("-")[0]);
									Integer max = Integer.parseInt(D.split(":")[1].split("-")[1]);
									int level = pickLevel(max, min);
									if(Main.CE.isEnchantment(enchant)) {
										if(level > 0) {
											item.addCEEnchantment(Main.CE.getFromName(enchant), level);
										}
									}else if(Main.CustomE.isEnchantment(enchant)) {
										if(level > 0) {
											item.addCustomEnchantment(enchant, level);
										}
									}
								}else {
									String enchant = D.split(":")[0];
									if(Main.CE.isEnchantment(enchant)) {
										item.addCEEnchantment(Main.CE.getFromName(enchant), Integer.parseInt(D.split(":")[1]));
									}else if(Main.CustomE.isEnchantment(enchant)) {
										item.addCustomEnchantment(enchant, Integer.parseInt(d.split(":")[1]));
									}
								}
							}else {
								String enchant = D;
								if(Main.CE.isEnchantment(enchant)) {
									item.addCEEnchantment(Main.CE.getFromName(enchant), 1);
								}else if(Main.CustomE.isEnchantment(enchant)) {
									item.addCustomEnchantment(enchant, 1);
								}
							}
						}
					}else {
						if(d.contains(":")) {
							if(d.contains("-")) {
								String enchant = d.split(":")[0];
								Integer min = Integer.parseInt(d.split(":")[1].split("-")[0]);
								Integer max = Integer.parseInt(d.split(":")[1].split("-")[1]);
								int level = pickLevel(max, min);
								if(Main.CE.isEnchantment(enchant)) {
									if(level > 0) {
										item.addCEEnchantment(Main.CE.getFromName(enchant), level);
									}
								}else if(Main.CustomE.isEnchantment(enchant)) {
									if(level > 0) {
										item.addCustomEnchantment(enchant, level);
									}
								}
							}else {
								String enchant = d.split(":")[0];
								if(Main.CE.isEnchantment(enchant)) {
									item.addCEEnchantment(Main.CE.getFromName(enchant), Integer.parseInt(d.split(":")[1]));
								}else if(Main.CustomE.isEnchantment(enchant)) {
									item.addCustomEnchantment(enchant, Integer.parseInt(d.split(":")[1]));
								}
							}
						}else {
							String enchant = d;
							if(Main.CE.isEnchantment(enchant)) {
								item.addCEEnchantment(Main.CE.getFromName(enchant), 1);
							}else if(Main.CustomE.isEnchantment(enchant)) {
								item.addCustomEnchantment(enchant, 1);
							}
						}
					}
				}
			}
			items.add(item.build());
		}
		return items;
	}

	private static Integer pickLevel(int max, int min) {
		max++;
		return min + new Random().nextInt(max - min);
	}

}