package me.BadBones69.CrazyEnchantments;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUI implements Listener{
	static void openGUI(Player player){
		Inventory inv = Bukkit.createInventory(null, 9, Api.getInvName());
		inv.setItem(0, Api.makeItem(Material.matchMaterial(Main.settings.getConfig().getString("Settings.Info.Item")), 1, 0, Main.settings.getConfig().getString("Settings.Info.GUIName")));
		inv.setItem(2, Api.makeItem(Material.matchMaterial(Main.settings.getConfig().getString("Settings.T1.Item")), 1, 0, Main.settings.getConfig().getString("Settings.T1.GUIName")
				, Main.settings.getConfig().getStringList("Settings.T1.GUILore")));
		inv.setItem(4, Api.makeItem(Material.matchMaterial(Main.settings.getConfig().getString("Settings.T2.Item")), 1, 0, Main.settings.getConfig().getString("Settings.T2.GUIName")
				, Main.settings.getConfig().getStringList("Settings.T2.GUILore")));
		inv.setItem(6, Api.makeItem(Material.matchMaterial(Main.settings.getConfig().getString("Settings.T3.Item")), 1, 0, Main.settings.getConfig().getString("Settings.T3.GUIName")
				, Main.settings.getConfig().getStringList("Settings.T3.GUILore")));
		inv.setItem(8, Api.makeItem(Material.matchMaterial(Main.settings.getConfig().getString("Settings.Info.Item")), 1, 0, Main.settings.getConfig().getString("Settings.Info.GUIName")));
		player.openInventory(inv);
	}
	void openInfo(Player player){
		Inventory inv = Bukkit.createInventory(null, 54, Api.getInvName());
		for(ItemStack i : addInfo()){
			inv.addItem(i);
		}
		player.openInventory(inv);
	}
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		ItemStack item = e.getCurrentItem();
		Inventory inv = e.getInventory();
		Player player = (Player) e.getWhoClicked();
		if(inv != null){
			if(inv.getName().equals(Api.getInvName())){
				e.setCancelled(true);
				if(item.hasItemMeta()){
					if(item.getItemMeta().hasDisplayName()){
						String name = item.getItemMeta().getDisplayName();
						if(name.equals(Api.color(Main.settings.getConfig().getString("Settings.Info.GUIName")))){
							openInfo(player);
							return;
						}
						if(name.equals(Api.color(Main.settings.getConfig().getString("Settings.T1.GUIName")))){
							if(player.getGameMode() != GameMode.CREATIVE){
								if(Api.getXPLvl(player)<Main.settings.getConfig().getInt("Settings.T1.XP")){
									player.sendMessage(Api.color("&cYou need &6" + Integer.toString(Main.settings.getConfig().getInt("Settings.T1.XP") - Api.getXPLvl(player)) + " &cmore XP Lvls."));
									return;
								}
								Api.takeXP(player, Main.settings.getConfig().getInt("Settings.T1.XP"));
							}
							player.getInventory().addItem(ECControl.pickT1());
							return;
						}
						if(name.equals(Api.color(Main.settings.getConfig().getString("Settings.T2.GUIName")))){
							if(player.getGameMode() != GameMode.CREATIVE){
								if(Api.getXPLvl(player)<Main.settings.getConfig().getInt("Settings.T2.XP")){
									player.sendMessage(Api.color("&cYou need &6" + Integer.toString(Main.settings.getConfig().getInt("Settings.T2.XP") - Api.getXPLvl(player)) + " &cmore XP Lvls."));
									return;
								}
								Api.takeXP(player, Main.settings.getConfig().getInt("Settings.T2.XP"));
							}
							player.getInventory().addItem(ECControl.pickT2());
						}
						if(name.equals(Api.color(Main.settings.getConfig().getString("Settings.T3.GUIName")))){
							if(player.getGameMode() != GameMode.CREATIVE){
								if(Api.getXPLvl(player)<Main.settings.getConfig().getInt("Settings.T3.XP")){
									player.sendMessage(Api.color("&cYou need &6" + Integer.toString(Main.settings.getConfig().getInt("Settings.T3.XP") - Api.getXPLvl(player)) + " &cmore XP Lvls."));
									return;
								}
								Api.takeXP(player, Main.settings.getConfig().getInt("Settings.T3.XP"));
							}
							player.getInventory().addItem(ECControl.pickT3());
						}
					}
				}
			}
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void addEnchantment(InventoryClickEvent e){
		Inventory inv = e.getInventory();
		Player player = (Player) e.getWhoClicked();
		if(inv != null){
			if(e.getCursor() != null&&e.getCurrentItem() != null){
				ItemStack c = e.getCursor();
				ItemStack item  = e.getCurrentItem();
				if(c.hasItemMeta()){
					if(c.getItemMeta().hasDisplayName()){
						String name = c.getItemMeta().getDisplayName();
						for(String en : ECControl.allEnchantments().keySet()){
							if(name.contains(en)){
								for(Material m : ECControl.allEnchantments().get(en)){
									if(item.getType() == m){
										if(c.getAmount() == 1){
											if(item.getItemMeta().hasLore()){
												for(String l:item.getItemMeta().getLore()){
													if(l.contains(en)){
														return;
													}
												}
											}
											e.setCancelled(true);
											if(Api.successChance(c) || player.getGameMode() == GameMode.CREATIVE){
												name = Api.removeColor(name);
												String[] breakdown = name.split(" ");
												String enchantment = en;
												String lvl = breakdown[1];
												String full = Api.color("&7"+enchantment+" "+lvl);
												e.setCursor(new ItemStack(Material.AIR));
												Api.addLore(item, full);
												player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
												return;
											}else{
												e.setCursor(new ItemStack(Material.AIR));
												player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
												return;
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
	}
	ArrayList<ItemStack> addInfo(){
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		ArrayList<ItemStack> swords = new ArrayList<ItemStack>();
		ArrayList<ItemStack> axes = new ArrayList<ItemStack>();
		ArrayList<ItemStack> bows = new ArrayList<ItemStack>();
		ArrayList<ItemStack> armor = new ArrayList<ItemStack>();
		ArrayList<ItemStack> helmets = new ArrayList<ItemStack>();
		ArrayList<ItemStack> boots = new ArrayList<ItemStack>();
		swords.add(Api.makeItem(Material.GOLD_SWORD, 1, 0, "&e&lViper", Arrays.asList("&c&lSwords Only", "&3Has a chance to give", "&3Your enemy Poison")));
		swords.add(Api.makeItem(Material.GOLD_SWORD, 1, 0, "&e&lSlowMo", Arrays.asList("&c&lSwords Only", "&3Has a chance to give", "&3Your enemy Slowness")));
		swords.add(Api.makeItem(Material.GOLD_SWORD, 1, 0, "&e&lVampire", Arrays.asList("&c&lSwords Only", "&3Has a chance to give", "&3To gain 1 heart when attaking")));
		swords.add(Api.makeItem(Material.GOLD_SWORD, 1, 0, "&e&lFast Turn", Arrays.asList("&c&lSwords Only", "&3Has a chance", "&3To deal more damage")));
		swords.add(Api.makeItem(Material.GOLD_SWORD, 1, 0, "&e&lBlindness", Arrays.asList("&c&lSwords Only", "&3Has a chance to give", "&3Your enemy Blindness")));
		swords.add(Api.makeItem(Material.GOLD_SWORD, 1, 0, "&e&lLife Steal", Arrays.asList("&c&lSwords Only", "&3Has a chance to take", "&3Your enemies health")));
		swords.add(Api.makeItem(Material.GOLD_SWORD, 1, 0, "&e&lLight Weight", Arrays.asList("&c&lSwords Only", "&3Has a chance to", "&3Give You Hast")));
		swords.add(Api.makeItem(Material.GOLD_SWORD, 1, 0, "&e&lDouble Damage", Arrays.asList("&c&lSwords Only", "&3Has a chance to", "&3Deal Double Damage")));
		axes.add(Api.makeItem(Material.GOLD_AXE, 1, 0, "&e&lRekt", Arrays.asList("&c&lAxes Only", "&3Has a chance to", "&3Deal Double Damage")));
		axes.add(Api.makeItem(Material.GOLD_AXE, 1, 0, "&e&lDizzy", Arrays.asList("&c&lAxes Only", "&3Has a chance to give", "&3Your enemy Confusion")));
		axes.add(Api.makeItem(Material.GOLD_AXE, 1, 0, "&e&lCursed", Arrays.asList("&c&lAxes Only", "&3Has a chance to give", "&3Your enemy Mining Fatigue")));
		axes.add(Api.makeItem(Material.GOLD_AXE, 1, 0, "&e&lFeedMe", Arrays.asList("&c&lAxes Only", "&3Has a chance to", "&3Give you food when you attack")));
		axes.add(Api.makeItem(Material.GOLD_AXE, 1, 0, "&e&lBlessed", Arrays.asList("&c&lAxes Only", "&3Has a chance to", "&3Remove all bad effects from you.")));
		axes.add(Api.makeItem(Material.GOLD_AXE, 1, 0, "&e&lBerserk", Arrays.asList("&c&lAxes Only", "&3Has a chance to", "&3Give you Strength and Mining Fatigue")));
		bows.add(Api.makeItem(Material.BOW, 1, 0, "&e&lBoom", Arrays.asList("&c&lBows Only", "&3Has a chance to", "&3Spawn Primed Tnt on hit")));
		bows.add(Api.makeItem(Material.BOW, 1, 0, "&e&lVenom", Arrays.asList("&c&lBows Only", "&3Has a chance to", "&3Give your enemy Poison")));
		bows.add(Api.makeItem(Material.BOW, 1, 0, "&e&lDoctor", Arrays.asList("&c&lBows Only", "&3Has a chance to", "&3To heal a player you hit")));
		bows.add(Api.makeItem(Material.BOW, 1, 0, "&e&lPiercing", Arrays.asList("&c&lBows Only", "&3Has a chance to", "&3Deal Double Damage")));
		helmets.add(Api.makeItem(Material.GOLD_HELMET, 1, 0, "&e&lMermaid", Arrays.asList("&c&lHelmets Only", "&3Will give you Water Breathing", "&3Once you put the helmet on")));
		helmets.add(Api.makeItem(Material.GOLD_HELMET, 1, 0, "&e&lGlowing", Arrays.asList("&c&lHelmets Only", "&3Will give you Night Vision", "&3Once you put the helmet on")));
		boots.add(Api.makeItem(Material.GOLD_BOOTS, 1, 0, "&e&lGears", Arrays.asList("&c&lBoots Only", "&3Will give you Speed Boost", "&3Once you put the Boots on")));
		boots.add(Api.makeItem(Material.GOLD_BOOTS, 1, 0, "&e&lSprings", Arrays.asList("&c&lBoots Only", "&3Will give you Jump Boost", "&3Once you put the Boots on")));
		boots.add(Api.makeItem(Material.GOLD_BOOTS, 1, 0, "&e&lAnti Gravity", Arrays.asList("&c&lBoots Only", "&3Will give you Higher Jump Boost", "&3Once you put the Boots on")));
		armor.add(Api.makeItem(Material.GOLD_CHESTPLATE, 1, 0, "&e&lHulk", Arrays.asList("&c&lArmor Only", "&3Will give you Strength and Slowness", "&3Once you put the Armor on")));
		armor.add(Api.makeItem(Material.GOLD_CHESTPLATE, 1, 0, "&e&lNinja", Arrays.asList("&c&lArmor Only", "&3Will give you Speed and Health Boost", "&3Once you put the Armor on")));
		armor.add(Api.makeItem(Material.GOLD_CHESTPLATE, 1, 0, "&e&lMolten", Arrays.asList("&c&lArmor Only", "&3Has a chance to", "&3Ignight your attacker")));
		armor.add(Api.makeItem(Material.GOLD_CHESTPLATE, 1, 0, "&e&lSavior", Arrays.asList("&c&lArmor Only", "&3Has a chance to", "&3Take less incoming damage at low health")));
		armor.add(Api.makeItem(Material.GOLD_CHESTPLATE, 1, 0, "&e&lFreezen", Arrays.asList("&c&lArmor Only", "&3Has a chance to", "&3Slow your attacker")));
		armor.add(Api.makeItem(Material.GOLD_CHESTPLATE, 1, 0, "&e&lFortify", Arrays.asList("&c&lArmor Only", "&3Has a chance to", "&3Give your attaker Weakness")));
		armor.add(Api.makeItem(Material.GOLD_CHESTPLATE, 1, 0, "&e&lOverLoad", Arrays.asList("&c&lArmor Only", "&3Will give you Health Boost", "&3Once you put the Armor on")));
		armor.add(Api.makeItem(Material.GOLD_CHESTPLATE, 1, 0, "&e&lPain Giver", Arrays.asList("&c&lArmor Only", "&3Has a chance to", "&3Give your attacker Poison")));
		armor.add(Api.makeItem(Material.GOLD_CHESTPLATE, 1, 0, "&e&lBurn Shield", Arrays.asList("&c&lArmor Only", "&3Will give you Fire Resistance", "&3Once you put the Armor on")));
		armor.add(Api.makeItem(Material.GOLD_CHESTPLATE, 1, 0, "&e&lEnlightened", Arrays.asList("&c&lArmor Only", "&3Has a chance to", "&3Heal you when being attacked")));
		armor.add(Api.makeItem(Material.GOLD_CHESTPLATE, 1, 0, "&e&lSelf Destruct", Arrays.asList("&c&lArmor Only", "&3When you die your", "&3Body will explode with uder destruction")));
		items.addAll(armor);
		items.addAll(helmets);
		items.addAll(boots);
		items.addAll(swords);
		items.addAll(axes);
		items.addAll(bows);
		return items;
	}
}