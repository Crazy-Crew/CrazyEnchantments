package me.badbones69.crazyenchantments.api;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.enums.Dust;
import me.badbones69.crazyenchantments.api.enums.EnchantmentType;
import me.badbones69.crazyenchantments.api.enums.Scrolls;
import me.badbones69.crazyenchantments.api.objects.*;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import me.badbones69.crazyenchantments.multisupport.Version;
import me.badbones69.crazyenchantments.multisupport.worldguard.WorldGuardVersion;
import me.badbones69.crazyenchantments.multisupport.worldguard.WorldGuard_v6;
import me.badbones69.crazyenchantments.multisupport.worldguard.WorldGuard_v7;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class CrazyEnchantments {
	
	private static CrazyEnchantments instance = new CrazyEnchantments();
	private Integer rageMaxLevel;
	private Boolean gkitzToggle;
	private Boolean useNewMaterial;
	private Boolean breakRageOnDamage;
	private Boolean enchantStackedItems;
	private WorldGuardVersion worldGuardVersion;
	private ArrayList<GKitz> gkitz = new ArrayList<>();
	private ArrayList<CEPlayer> players = new ArrayList<>();
	private ArrayList<String> whitelisted = new ArrayList<>();
	private ArrayList<String> blacklisted = new ArrayList<>();
	private ArrayList<Material> blockList = new ArrayList<>();
	private ArrayList<CEnchantment> registeredEnchantments = new ArrayList<>();
	private FileManager fileManager = FileManager.getInstance();
	
	public static CrazyEnchantments getInstance() {
		return instance;
	}
	
	/**
	 * Loads all the data for Crazy Enchantments plugin.
	 *
	 * Do not use unless needed.
	 */
	public void load() {
		blockList.clear();
		gkitz.clear();
		registeredEnchantments.clear();
		whitelisted.clear();
		blacklisted.clear();
		Version version = Version.getCurrentVersion();
		useNewMaterial = version.isNewer(Version.v1_12_R1);
		FileConfiguration config = Files.CONFIG.getFile();
		FileConfiguration gkit = Files.GKITZ.getFile();
		FileConfiguration enchants = Files.ENCHANTMENTS.getFile();
		for(String id : Files.BLOCKLIST.getFile().getStringList("Block-List")) {
			try {
				blockList.add(new ItemBuilder().setMaterial(id).getMaterial());
			}catch(Exception e) {
			}
		}
		gkitzToggle = !config.contains("Settings.GKitz.Enabled") || config.getBoolean("Settings.GKitz.Enabled");
		rageMaxLevel = config.contains("Settings.EnchantmentOptions.MaxRageLevel") ? config.getInt("Settings.EnchantmentOptions.MaxRageLevel") : 4;
		breakRageOnDamage = !config.contains("Settings.EnchantmentOptions.Break-Rage-On-Damage") || config.getBoolean("Settings.EnchantmentOptions.Break-Rage-On-Damage");
		enchantStackedItems = config.contains("Settings.EnchantmentOptions.Enchant-Stacked-Items") && config.getBoolean("Settings.EnchantmentOptions.Enchant-Stacked-Items");
		for(String world : config.getStringList("Settings.EnchantmentOptions.Wings.Worlds.Whitelisted")) {
			whitelisted.add(world.toLowerCase());
		}
		for(String world : config.getStringList("Settings.EnchantmentOptions.Wings.Worlds.Blacklisted")) {
			blacklisted.add(world.toLowerCase());
		}
		for(CEnchantments enchant : CEnchantments.values()) {
			String name = enchant.getName();
			String path = "Enchantments." + name;
			if(enchants.contains(path)) {// To make sure the enchantment isn't broken.
				CEnchantment en = new CEnchantment(name)
				.setCustomName(enchants.getString(path + ".Name"))
				.setActivated(enchants.getBoolean(path + ".Enabled"))
				.setColor(enchants.getString(path + ".Color"))
				.setBookColor(enchants.getString(path + ".BookColor"))
				.setMaxLevel(enchants.getInt(path + ".MaxPower"))
				.setEnchantmentType(enchant.getType())
				.setInfoName(enchants.getString(path + ".Info.Name"))
				.setInfoDescription(enchants.getStringList(path + ".Info.Description"))
				.setCategories(enchants.getStringList(path + ".Categories"))
				.setChance(enchant.getChance())
				.setChanceIncrease(enchant.getChanceIncrease());
				if(enchants.contains(path + ".Enchantment-Type")) {// Sets the custom type set in the enchantments.yml.
					en.setEnchantmentType(EnchantmentType.getFromName(enchants.getString(path + ".Enchantment-Type")));
				}
				if(enchant.hasChanceSystem()) {
					if(enchants.contains(path + ".Chance-System.Base")) {
						en.setChance(enchants.getInt(path + ".Chance-System.Base"));
					}else {
						en.setChance(enchant.getChance());
					}
					if(enchants.contains(path + ".Chance-System.Increase")) {
						en.setChanceIncrease(enchants.getInt(path + ".Chance-System.Increase"));
					}else {
						en.setChanceIncrease(enchant.getChanceIncrease());
					}
				}
				en.registerEnchantment();
			}
		}
		if(gkitzToggle) {
			for(String kit : gkit.getConfigurationSection("GKitz").getKeys(false)) {
				int slot = gkit.getInt("GKitz." + kit + ".Display.Slot");
				String time = gkit.getString("GKitz." + kit + ".Cooldown");
				boolean autoEquip = gkit.getBoolean("GKitz." + kit + ".Auto-Equip");
				ItemStack displayItem = new ItemBuilder().setMaterial(gkit.getString("GKitz." + kit + ".Display.Item")).setName(gkit.getString("GKitz." + kit + ".Display.Name")).setLore(gkit.getStringList("GKitz." + kit + ".Display.Lore")).setGlowing(gkit.getBoolean("GKitz." + kit + ".Display.Glowing")).build();
				ArrayList<String> commands = (ArrayList<String>) gkit.getStringList("GKitz." + kit + ".Commands");
				ArrayList<String> itemStrings = (ArrayList<String>) gkit.getStringList("GKitz." + kit + ".Items");
				ArrayList<ItemStack> previewItems = getInfoGKit(itemStrings);
				previewItems.addAll(getInfoGKit((ArrayList<String>) gkit.getStringList("GKitz." + kit + ".Fake-Items")));
				gkitz.add(new GKitz(kit, slot, time, displayItem, previewItems, commands, getKitItems(itemStrings), itemStrings, autoEquip));
			}
		}
		//Loads the scrolls
		Scrolls.loadScrolls();
		//Loads the dust
		Dust.loadDust();
		if(SupportedPlugins.WORLD_GUARD.isPluginLoaded() && SupportedPlugins.WORLD_EDIT.isPluginLoaded()) {
			worldGuardVersion = version.isNewer(Version.v1_12_R1) ? new WorldGuard_v7() : new WorldGuard_v6();
		}
	}
	
	/**
	 * Only needs used when the player joins the server.
	 * This plugin does it automatically, so there is no need to use it unless you have to.
	 * @param player The player you wish to load.
	 */
	public void loadCEPlayer(Player player) {
		FileConfiguration data = Files.DATA.getFile();
		String uuid = player.getUniqueId().toString();
		int souls = 0;
		boolean isActive = false;
		if(data.contains("Players." + uuid)) {
			if(data.contains("Players." + uuid + ".Souls-Information")) {
				souls = data.getInt("Players." + uuid + ".Souls-Information.Souls");
				isActive = data.getBoolean("Players." + uuid + ".Souls-Information.Is-Active");
			}
		}
		ArrayList<Cooldown> cooldowns = new ArrayList<>();
		for(GKitz kit : getGKitz()) {
			if(data.contains("Players." + uuid + ".GKitz." + kit.getName())) {
				Calendar cooldown = Calendar.getInstance();
				cooldown.setTimeInMillis(data.getLong("Players." + uuid + ".GKitz." + kit.getName()));
				cooldowns.add(new Cooldown(kit, cooldown));
			}
		}
		addCEPlayer(new CEPlayer(player, souls, isActive, cooldowns));
	}
	
	/**
	 * Only needs used when the player leaves the server.
	 * This plugin removes the player automatically, so don't use this method unless needed for some reason.
	 * @param player Player you wish to remove.
	 */
	public void unloadCEPlayer(Player player) {
		FileConfiguration data = Files.DATA.getFile();
		String uuid = player.getUniqueId().toString();
		CEPlayer p = getCEPlayer(player);
		if(p != null) {
			if(p.getSouls() > 0) {
				data.set("Players." + uuid + ".Name", player.getName());
				data.set("Players." + uuid + ".Souls-Information.Souls", p.getSouls());
				data.set("Players." + uuid + ".Souls-Information.Is-Active", p.isSoulsActive());
			}
			for(Cooldown cooldown : p.getCooldowns()) {
				data.set("Players." + uuid + ".GKitz." + cooldown.getGKitz().getName(), cooldown.getCooldown().getTimeInMillis());
			}
			Files.DATA.saveFile();
		}
		removeCEPlayer(p);
	}
	
	/**
	 * This backup all the players data stored by this plugin.
	 * @param player The player you wish to backup.
	 */
	public void backupCEPlayer(Player player) {
		FileConfiguration data = Files.DATA.getFile();
		String uuid = player.getUniqueId().toString();
		CEPlayer p = getCEPlayer(player);
		if(p != null) {
			if(p.getSouls() > 0) {
				data.set("Players." + uuid + ".Name", player.getName());
				data.set("Players." + uuid + ".Souls-Information.Souls", p.getSouls());
				data.set("Players." + uuid + ".Souls-Information.Is-Active", p.isSoulsActive());
			}
			for(Cooldown cooldown : p.getCooldowns()) {
				data.set("Players." + uuid + ".GKitz." + cooldown.getGKitz().getName(), cooldown.getCooldown().getTimeInMillis());
			}
			Files.DATA.saveFile();
		}
	}
	
	/**
	 * This backup all the players data stored by this plugin.
	 * @param player The player you wish to backup.
	 */
	public void backupCEPlayer(CEPlayer player) {
		FileConfiguration data = Files.DATA.getFile();
		String uuid = player.getPlayer().getUniqueId().toString();
		if(player.getSouls() > 0) {
			data.set("Players." + uuid + ".Name", player.getPlayer().getName());
			data.set("Players." + uuid + ".Souls-Information.Souls", player.getSouls());
			data.set("Players." + uuid + ".Souls-Information.Is-Active", player.isSoulsActive());
		}
		for(Cooldown cooldown : player.getCooldowns()) {
			data.set("Players." + uuid + ".GKitz." + cooldown.getGKitz().getName(), cooldown.getCooldown().getTimeInMillis());
		}
		Files.DATA.saveFile();
	}
	
	/**
	 * Gets the plugin.
	 * @return The plugin as a Plugin object.
	 */
	public Plugin getPlugin() {
		return Bukkit.getPluginManager().getPlugin("CrazyEnchantments");
	}
	
	/**
	 * Get the World Guard support class.
	 * @return World Guard support class.
	 */
	public WorldGuardVersion getWorldGuardSupport() {
		return worldGuardVersion;
	}
	
	/**
	 * The material version needed to be used.
	 */
	public Boolean useNewMaterial() {
		return useNewMaterial;
	}
	
	/**
	 * Get the correct sound for the version of minecraft.
	 * @param newSound The sound from 1.9+
	 * @param oldSound The sound from 1.8.8-
	 * @return The Sound object of the current minecraft version.
	 */
	public Sound getSound(String newSound, String oldSound) {
		return Sound.valueOf(Version.getCurrentVersion().isNewer(Version.v1_8_R3) ? newSound : oldSound);
	}
	
	/**
	 * Get the correct material for the version of minecraft.
	 * @param newMaterial The material from 1.13+
	 * @param oldMaterial The material from 1.12.2-
	 * @return The Material object of the current minecraft version.
	 */
	public Material getMaterial(String newMaterial, String oldMaterial) {
		return Material.matchMaterial(useNewMaterial ? newMaterial : oldMaterial);
	}
	
	/**
	 * Check if the gkitz option is enabled.
	 * @return True if it is on and false if it is off.
	 */
	public Boolean isGkitzEnabled() {
		return gkitzToggle;
	}
	
	/**
	 * Get a GKit from its name.
	 * @param kit The kit you wish to get.
	 * @return The kit as a GKitz object.
	 */
	public GKitz getGKitFromName(String kit) {
		for(GKitz k : getGKitz()) {
			if(k.getName().equalsIgnoreCase(kit)) {
				return k;
			}
		}
		return null;
	}
	
	/**
	 * Get all loaded gkitz.
	 * @return All of the loaded gkitz.
	 */
	public ArrayList<GKitz> getGKitz() {
		return gkitz;
	}
	
	/**
	 * Add a new GKit to the plugin.
	 * @param kit The kit you wish to add.
	 */
	public void addGKit(GKitz kit) {
		gkitz.add(kit);
	}
	
	/**
	 * Remove a kit that is in the plugin.
	 * @param kit The kit you wish to remove.
	 */
	public void removeGKit(GKitz kit) {
		gkitz.remove(kit);
	}
	
	/**
	 * This converts a normal Player into a CEPlayer that is loaded.
	 * @param player The player you want to get as a CEPlayer.
	 * @return The player but as a CEPlayer. Will return null if not found.
	 */
	public CEPlayer getCEPlayer(Player player) {
		for(CEPlayer p : getCEPlayers()) {
			if(p.getPlayer() == player) {
				return p;
			}
		}
		return null;
	}
	
	/**
	 * This gets all the CEPlayer's that are loaded.
	 * @return All CEPlayer's that are loading and in a list.
	 */
	public ArrayList<CEPlayer> getCEPlayers() {
		return players;
	}
	
	/**
	 * Get the list of all the whitelisted worlds for the Wings enchantment.
	 */
	public ArrayList<String> getWhitelistedWorlds() {
		return whitelisted;
	}
	
	/**
	 * Check to see if a player is in a whitelisted world for the wings enchantment.
	 * @param player The player you wish to check.
	 */
	public Boolean inWhitelistedWorld(Player player) {
		return player != null && whitelisted.contains(player.getWorld().getName().toLowerCase());
	}
	
	/**
	 * Get the list of all the blacklisted worlds for the Wings enchantment.
	 */
	public ArrayList<String> getBlacklistedWorlds() {
		return blacklisted;
	}
	
	/**
	 * Check to see if a player is in a blacklisted world for the wings enchantment.
	 * @param player The player you wish to check.
	 */
	public Boolean inBlacklistedWorld(Player player) {
		return player != null && blacklisted.contains(player.getWorld().getName().toLowerCase());
	}
	
	/**
	 *
	 * @return Returns the item the enchantment book will be.
	 */
	public ItemStack getEnchantmentBookItem() {
		return new ItemBuilder().setMaterial(Files.CONFIG.getFile().getString("Settings.Enchantment-Book-Item")).build();
	}
	
	/**
	 *
	 * @param item Item you want to check to see if it has enchantments.
	 * @return True if it has enchantments / False if it doesn't have enchantments.
	 */
	public Boolean hasEnchantments(ItemStack item) {
		if(item != null) {
			if(item.hasItemMeta()) {
				if(item.getItemMeta().hasLore()) {
					for(String lore : item.getItemMeta().getLore()) {
						for(CEnchantment enchantment : getRegisteredEnchantments()) {
							if(lore.startsWith(enchantment.getColor() + enchantment.getCustomName())) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 *
	 * @param item Item that you want to check if it has an enchantment.
	 * @param enchantment The enchantment you want to check if the item has.
	 * @return True if the item has the enchantment / False if it doesn't have the enchantment.
	 */
	public Boolean hasEnchantment(ItemStack item, CEnchantment enchantment) {
		if(item != null) {
			if(item.hasItemMeta()) {
				if(item.getItemMeta().hasLore()) {
					for(String lore : item.getItemMeta().getLore()) {
						if(lore.startsWith(enchantment.getColor() + enchantment.getCustomName())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 *
	 * @param item Item that you want to check if it has an enchantment.
	 * @param enchantment The enchantment you want to check if the item has.
	 * @return True if the item has the enchantment / False if it doesn't have the enchantment.
	 */
	public Boolean hasEnchantment(ItemStack item, CEnchantments enchantment) {
		if(item != null) {
			if(item.hasItemMeta()) {
				if(item.getItemMeta().hasLore()) {
					for(String lore : item.getItemMeta().getLore()) {
						if(lore.startsWith(enchantment.getEnchantment().getColor() + enchantment.getEnchantment().getCustomName())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Get the highest category rarity the enchantment is in.
	 * @param enchantment The enchantment you are checking.
	 * @return The highest category based on the rarities.
	 */
	public String getHighestEnchantmentCategory(CEnchantment enchantment) {
		String top = "";
		int rarity = 0;
		for(String cat : enchantment.getCategories()) {
			if(getCategoryRarity(cat) >= rarity) {
				rarity = getCategoryRarity(cat);
				top = cat;
			}
		}
		return top;
	}
	
	/**
	 * Get all the categories that can be used.
	 * @return List of all the categories.
	 */
	public ArrayList<String> getCategories() {
		return new ArrayList<>(Files.CONFIG.getFile().getConfigurationSection("Categories").getKeys(false));
	}
	
	/**
	 *
	 * @param category The category you want the rarity from.
	 * @return The level of the category's rarity.
	 */
	public Integer getCategoryRarity(String category) {
		int rarity = 0;
		FileConfiguration config = Files.CONFIG.getFile();
		if(config.contains("Categories." + category)) {
			rarity = config.getInt("Categories." + category + ".Rarity");
		}
		return rarity;
	}
	
	/**
	 *
	 * @param player The player you want to check if they have the enchantment on their armor.
	 * @param include The item you want to include.
	 * @param exclude The item you want to exclude.
	 * @param enchantment The enchantment you are checking.
	 * @return True if a piece of armor has the enchantment and false if not.
	 */
	public Boolean playerHasEnchantmentOn(Player player, ItemStack include, ItemStack exclude, CEnchantment enchantment) {
		for(ItemStack armor : player.getEquipment().getArmorContents()) {
			if(!armor.isSimilar(exclude)) {
				if(hasEnchantment(armor, enchantment)) {
					return true;
				}
			}
		}
		return hasEnchantment(include, enchantment);
	}
	
	/**
	 *
	 * @param player The player you want to check if they have the enchantment on their armor.
	 * @param item The item you want to exclude.
	 * @param enchantment The enchantment you are checking.
	 * @return True if a piece of armor has the enchantment and false if not.
	 */
	public Boolean playerHasEnchantmentOnExclude(Player player, ItemStack item, CEnchantment enchantment) {
		for(ItemStack armor : player.getEquipment().getArmorContents()) {
			if(!armor.isSimilar(item)) {
				if(hasEnchantment(armor, enchantment)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 *
	 * @param player The player you want to check if they have the enchantment on their armor.
	 * @param item The item you want to include.
	 * @param enchantment The enchantment you are checking.
	 * @return True if a piece of armor has the enchantment and false if not.
	 */
	public Boolean playerHasEnchantmentOnInclude(Player player, ItemStack item, CEnchantment enchantment) {
		for(ItemStack armor : player.getEquipment().getArmorContents()) {
			if(hasEnchantment(armor, enchantment)) {
				return true;
			}
		}
		return hasEnchantment(item, enchantment);
	}
	
	/**
	 *
	 * @param player The player you want to get the highest level of an enchantment from.
	 * @param include The item you want to include.
	 * @param exclude The item you want to exclude.
	 * @param enchantment The enchantment you are checking.
	 * @return The highest level of the enchantment that the player currently has.
	 */
	public Integer getHighestEnchantmentLevel(Player player, ItemStack include, ItemStack exclude, CEnchantment enchantment) {
		int highest = 0;
		for(ItemStack armor : player.getEquipment().getArmorContents()) {
			if(!armor.isSimilar(exclude)) {
				if(hasEnchantment(armor, enchantment)) {
					int level = getLevel(armor, enchantment);
					if(highest < level) {
						highest = level;
					}
				}
			}
		}
		if(hasEnchantment(include, enchantment)) {
			int level = getLevel(include, enchantment);
			if(highest < level) {
				highest = level;
			}
		}
		return highest;
	}
	
	/**
	 *
	 * @param player The player you want to get the highest level of an enchantment from.
	 * @param item The item you want to exclude.
	 * @param enchantment The enchantment you are checking.
	 * @return The highest level of the enchantment that the player currently has.
	 */
	public Integer getHighestEnchantmentLevelExclude(Player player, ItemStack item, CEnchantment enchantment) {
		int highest = 0;
		for(ItemStack armor : player.getEquipment().getArmorContents()) {
			if(!armor.isSimilar(item)) {
				if(hasEnchantment(armor, enchantment)) {
					int level = getLevel(armor, enchantment);
					if(highest < level) {
						highest = level;
					}
				}
			}
		}
		return highest;
	}
	
	/**
	 *
	 * @param player The player you want to get the highest level of an enchantment from.
	 * @param item The item you want to include.
	 * @param enchantment The enchantment you are checking.
	 * @return The highest level of the enchantment that the player currently has.
	 */
	public Integer getHighestEnchantmentLevelInclude(Player player, ItemStack item, CEnchantment enchantment) {
		int highest = 0;
		for(ItemStack armor : player.getEquipment().getArmorContents()) {
			if(hasEnchantment(armor, enchantment)) {
				int level = getLevel(armor, enchantment);
				if(highest < level) {
					highest = level;
				}
			}
		}
		if(hasEnchantment(item, enchantment)) {
			int level = getLevel(item, enchantment);
			if(highest < level) {
				highest = level;
			}
		}
		return highest;
	}
	
	/**
	 * Get all the current registered enchantments.
	 * @return A list of all the registered enchantments in the plugin.
	 */
	public ArrayList<CEnchantment> getRegisteredEnchantments() {
		return new ArrayList<>(registeredEnchantments);
	}
	
	/**
	 * Get a CEnchantment enchantment from the name.
	 * @param enchantment The name of the enchantment.
	 * @return The enchantment as a CEnchantment but if not found will be null.
	 */
	public CEnchantment getEnchantmentFromName(String enchantment) {
		for(CEnchantment enchant : getRegisteredEnchantments()) {
			if(enchant.getName().equalsIgnoreCase(enchantment)) {
				return enchant;
			}
		}
		return null;
	}
	
	/**
	 * Register a new enchantment into the plugin.
	 * @param enchantment The enchantment you wish to register.
	 */
	public void registerEnchantment(CEnchantment enchantment) {
		registeredEnchantments.add(enchantment);
	}
	
	/**
	 * Unregister an enchantment that is registered into plugin.
	 * @param enchantment The enchantment you wish to unregister.
	 */
	public void unregisterEnchantment(CEnchantment enchantment) {
		registeredEnchantments.remove(enchantment);
	}
	
	/**
	 *
	 * @param item Item you want to add the enchantment to.
	 * @param enchant Enchantment you want added.
	 * @param level Tier of the enchantment.
	 * @return The item with the enchantment on it.
	 */
	public ItemStack addEnchantment(ItemStack item, CEnchantment enchant, Integer level) {
		if(hasEnchantment(item, enchant)) {
			removeEnchantment(item, enchant);
		}
		List<String> newLore = new ArrayList<>();
		List<String> lores = new ArrayList<>();
		HashMap<String, String> enchantments = new HashMap<>();
		for(CEnchantment en : getEnchantmentsOnItem(item)) {
			enchantments.put(en.getName(), Methods.color(en.getColor() + en.getCustomName() + " " + convertLevelString(getLevel(item, en))));
			removeEnchantment(item, en);
		}
		ItemMeta meta = item.getItemMeta();
		if(meta != null) {
			if(meta.hasLore()) {
				lores.addAll(item.getItemMeta().getLore());
			}
		}
		enchantments.put(enchant.getName(), Methods.color(enchant.getColor() + enchant.getCustomName() + " " + convertLevelString(level)));
		for(String en : enchantments.keySet()) {
			newLore.add(enchantments.get(en));
		}
		newLore.addAll(lores);
		if(meta != null) {
			meta.setLore(newLore);
		}
		item.setItemMeta(meta);
		return item;
	}
	
	/**
	 *
	 * @param item Item you want to remove the enchantment from.
	 * @param enchant Enchantment you want removed.
	 * @return Item with out the enchantment.
	 */
	public ItemStack removeEnchantment(ItemStack item, CEnchantment enchant) {
		List<String> newLore = new ArrayList<>();
		ItemMeta meta = item.getItemMeta();
		if(meta.hasLore()) {
			for(String lore : item.getItemMeta().getLore()) {
				if(!lore.contains(enchant.getCustomName())) {
					newLore.add(lore);
				}
			}
		}
		meta.setLore(newLore);
		item.setItemMeta(meta);
		return item;
	}
	
	/**
	 *
	 * @param item Item you want to get the enchantments from.
	 * @return A list of enchantments the item has.
	 */
	public ArrayList<CEnchantment> getEnchantmentsOnItem(ItemStack item) {
		ArrayList<CEnchantment> enchantments = new ArrayList<>();
		if(item != null) {
			if(item.hasItemMeta()) {
				if(item.getItemMeta().hasLore()) {
					for(String lore : item.getItemMeta().getLore()) {
						for(CEnchantment en : getRegisteredEnchantments()) {
							if(lore.startsWith(en.getColor() + en.getCustomName())) {
								if(!enchantments.contains(en)) {
									enchantments.add(en);
								}
							}
						}
					}
				}
			}
		}
		return enchantments;
	}
	
	/**
	 * Force an update of a players armor potion effects.
	 * @param player The player you are updating the effects of.
	 */
	public void updatePlayerEffects(Player player) {
		if(player != null) {
			for(CEnchantments ench : getEnchantmentPotions().keySet()) {
				for(ItemStack armor : player.getEquipment().getArmorContents()) {
					if(hasEnchantment(armor, ench.getEnchantment())) {
						if(ench.isActivated()) {
							HashMap<PotionEffectType, Integer> effects = getUpdatedEffects(player, armor, new ItemStack(Material.AIR), ench);
							for(PotionEffectType type : effects.keySet()) {
								if(effects.get(type) < 0) {
									player.removePotionEffect(type);
								}else {
									player.removePotionEffect(type);
									player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, effects.get(type)));
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 *
	 * @param player The player you are adding it to.
	 * @param include Include an item.
	 * @param exclude Exclude an item.
	 * @param enchantment The enchantment you want the max level effects from.
	 * @return The list of all the max potion effects based on all the armor on the player.
	 */
	public HashMap<PotionEffectType, Integer> getUpdatedEffects(Player player, ItemStack include, ItemStack exclude, CEnchantments enchantment) {
		HashMap<PotionEffectType, Integer> effects = new HashMap<>();
		ArrayList<ItemStack> items = new ArrayList<>(Arrays.asList(player.getEquipment().getArmorContents()));
		if(include == null) {
			include = new ItemStack(Material.AIR);
		}
		if(exclude == null) {
			exclude = new ItemStack(Material.AIR);
		}
		if(exclude.isSimilar(include)) {
			exclude = new ItemStack(Material.AIR);
		}
		items.add(include);
		HashMap<CEnchantments, HashMap<PotionEffectType, Integer>> armorEffects = getEnchantmentPotions();
		for(CEnchantments ench : armorEffects.keySet()) {
			for(ItemStack armor : items) {
				if(armor != null) {
					if(!armor.isSimilar(exclude)) {
						if(hasEnchantment(armor, getEnchantmentFromName(ench.getName()))) {
							int level = getLevel(armor, getEnchantmentFromName(ench.getName()));
							if(!Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.UnSafe-Enchantments")) {
								if(level > getEnchantmentFromName(ench.getName()).getMaxLevel()) {
									level = getEnchantmentFromName(ench.getName()).getMaxLevel();
								}
							}
							for(PotionEffectType type : armorEffects.get(enchantment).keySet()) {
								if(armorEffects.get(ench).containsKey(type)) {
									if(effects.containsKey(type)) {
										int updated = effects.get(type);
										if(updated < (level + armorEffects.get(ench).get(type))) {
											effects.put(type, level + armorEffects.get(ench).get(type));
										}
									}else {
										effects.put(type, level + armorEffects.get(ench).get(type));
									}
								}
							}
						}
					}
				}
			}
		}
		for(PotionEffectType type : armorEffects.get(enchantment).keySet()) {
			if(!effects.containsKey(type)) {
				effects.put(type, -1);
			}
		}
		return effects;
	}
	
	/**
	 *
	 * @return All the effects for each enchantment that needs it.
	 */
	public HashMap<CEnchantments, HashMap<PotionEffectType, Integer>> getEnchantmentPotions() {
		HashMap<CEnchantments, HashMap<PotionEffectType, Integer>> enchants = new HashMap<>();
		enchants.put(CEnchantments.BURNSHIELD, new HashMap<>());
		enchants.get(CEnchantments.BURNSHIELD).put(PotionEffectType.FIRE_RESISTANCE, -1);
		
		enchants.put(CEnchantments.DRUNK, new HashMap<>());
		enchants.get(CEnchantments.DRUNK).put(PotionEffectType.INCREASE_DAMAGE, -1);
		enchants.get(CEnchantments.DRUNK).put(PotionEffectType.SLOW_DIGGING, -1);
		enchants.get(CEnchantments.DRUNK).put(PotionEffectType.SLOW, 0);
		
		enchants.put(CEnchantments.HULK, new HashMap<>());
		enchants.get(CEnchantments.HULK).put(PotionEffectType.INCREASE_DAMAGE, -1);
		enchants.get(CEnchantments.HULK).put(PotionEffectType.DAMAGE_RESISTANCE, -1);
		enchants.get(CEnchantments.HULK).put(PotionEffectType.SLOW, 0);
		
		enchants.put(CEnchantments.VALOR, new HashMap<>());
		enchants.get(CEnchantments.VALOR).put(PotionEffectType.DAMAGE_RESISTANCE, -1);
		
		enchants.put(CEnchantments.OVERLOAD, new HashMap<>());
		enchants.get(CEnchantments.OVERLOAD).put(PotionEffectType.HEALTH_BOOST, 0);
		
		enchants.put(CEnchantments.NINJA, new HashMap<>());
		enchants.get(CEnchantments.NINJA).put(PotionEffectType.HEALTH_BOOST, -1);
		enchants.get(CEnchantments.NINJA).put(PotionEffectType.SPEED, -1);
		
		enchants.put(CEnchantments.INSOMNIA, new HashMap<>());
		enchants.get(CEnchantments.INSOMNIA).put(PotionEffectType.CONFUSION, -1);
		enchants.get(CEnchantments.INSOMNIA).put(PotionEffectType.SLOW_DIGGING, -1);
		enchants.get(CEnchantments.INSOMNIA).put(PotionEffectType.SLOW, 0);
		
		enchants.put(CEnchantments.ANTIGRAVITY, new HashMap<>());
		enchants.get(CEnchantments.ANTIGRAVITY).put(PotionEffectType.JUMP, 1);
		
		enchants.put(CEnchantments.GEARS, new HashMap<>());
		enchants.get(CEnchantments.GEARS).put(PotionEffectType.SPEED, -1);
		
		enchants.put(CEnchantments.SPRINGS, new HashMap<>());
		enchants.get(CEnchantments.SPRINGS).put(PotionEffectType.JUMP, -1);
		return enchants;
	}
	
	/**
	 * Check if an itemstack is an enchantment book.
	 * @param book The item you are checking.
	 * @return True if it is and false if not.
	 */
	public Boolean isEnchantmentBook(ItemStack book) {
		if(book != null) {
			if(book.hasItemMeta()) {
				if(book.getItemMeta().hasDisplayName()) {
					if(book.getType() == getEnchantmentBookItem().getType()) {
						for(CEnchantment en : getRegisteredEnchantments()) {
							if(book.getItemMeta().getDisplayName().startsWith(en.getBookColor() + en.getCustomName())) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * This method converts an ItemStack into a CEBook.
	 * @param book The ItemStack you are converting.
	 * @return The ItemStack but as a CEBook.
	 */
	public CEBook convertToCEBook(ItemStack book) {
		CEBook ceBook = new CEBook(getEnchantmentBookEnchantmnet(book), getBookLevel(book, getEnchantmentBookEnchantmnet(book)), book.getAmount());
		ceBook.setSuccessRate(Methods.getPercent("%success_rate%", book, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore")));
		ceBook.setDestroyRate(Methods.getPercent("%destroy_rate%", book, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore")));
		ceBook.setGlowing(Files.CONFIG.getFile().getBoolean("Settings.Enchantment-Book-Glowing"));
		return ceBook;
	}
	
	/**
	 * Get the enchantment from an enchantment book.
	 * @param book The book you want the enchantment from.
	 * @return The enchantment the book is.
	 */
	public CEnchantment getEnchantmentBookEnchantmnet(ItemStack book) {
		if(book != null) {
			if(book.hasItemMeta()) {
				if(book.getItemMeta().hasDisplayName()) {
					if(book.getType() == getEnchantmentBookItem().getType()) {
						for(CEnchantment en : getRegisteredEnchantments()) {
							if(book.getItemMeta().getDisplayName().startsWith(en.getBookColor() + en.getCustomName())) {
								return en;
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Get a players max amount of enchantments.
	 * @param player The player you are checking.
	 * @return The max amount of enchantments a player can have on an item.
	 */
	public Integer getPlayerMaxEnchantments(Player player) {
		int limit = 0;
		for(int i = 1; i < 100; i++) {
			if(player.hasPermission("crazyenchantments.limit." + i)) {
				if(limit < i) {
					limit = i;
				}
			}
		}
		for(PermissionAttachmentInfo Permission : player.getEffectivePermissions()) {
			String perm = Permission.getPermission().toLowerCase();
			if(perm.startsWith("crazyenchantments.limit.")) {
				perm = perm.replace("crazyenchantments.limit.", "");
				if(Methods.isInt(perm)) {
					if(limit < Integer.parseInt(perm)) {
						limit = Integer.parseInt(perm);
					}
				}
			}
		}
		return limit;
	}
	
	/**
	 *
	 * @param book The book you are getting the level from.
	 * @param enchant The enchantment you want the level from.
	 * @return The level the enchantment has.
	 */
	public Integer getBookLevel(ItemStack book, CEnchantment enchant) {
		String line = book.getItemMeta().getDisplayName().replace(enchant.getBookColor() + enchant.getCustomName() + " ", "");
		if(Methods.isInt(line)) return Integer.parseInt(line);
		if(line.equalsIgnoreCase("I")) return 1;
		if(line.equalsIgnoreCase("II")) return 2;
		if(line.equalsIgnoreCase("III")) return 3;
		if(line.equalsIgnoreCase("IV")) return 4;
		if(line.equalsIgnoreCase("V")) return 5;
		if(line.equalsIgnoreCase("VI")) return 6;
		if(line.equalsIgnoreCase("VII")) return 7;
		if(line.equalsIgnoreCase("VIII")) return 8;
		if(line.equalsIgnoreCase("IX")) return 9;
		if(line.equalsIgnoreCase("X")) return 10;
		return 1;
	}
	
	/**
	 *
	 * @param item Item you are getting the level from.
	 * @param enchant The enchantment you want the level from.
	 * @return The level the enchantment has.
	 */
	public Integer getLevel(ItemStack item, CEnchantment enchant) {
		int level = 0;
		String line = "";
		if(item.hasItemMeta()) {
			if(item.getItemMeta().hasLore()) {
				for(String lore : item.getItemMeta().getLore()) {
					if(lore.contains(enchant.getCustomName())) {
						line = lore;
						break;
					}
				}
			}
		}
		line = line.replace(enchant.getColor() + enchant.getCustomName() + " ", "");
		if(Methods.isInt(line)) level = Integer.parseInt(line);
		if(line.equalsIgnoreCase("I")) level = 1;
		if(line.equalsIgnoreCase("II")) level = 2;
		if(line.equalsIgnoreCase("III")) level = 3;
		if(line.equalsIgnoreCase("IV")) level = 4;
		if(line.equalsIgnoreCase("V")) level = 5;
		if(line.equalsIgnoreCase("VI")) level = 6;
		if(line.equalsIgnoreCase("VII")) level = 7;
		if(line.equalsIgnoreCase("VIII")) level = 8;
		if(line.equalsIgnoreCase("IX")) level = 9;
		if(line.equalsIgnoreCase("X")) level = 10;
		if(!Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.UnSafe-Enchantments")) {
			if(level > enchant.getMaxLevel()) {
				level = enchant.getMaxLevel();
			}
		}
		return level;
	}
	
	/**
	 *
	 * @param item Item you are getting the level from.
	 * @param enchant The enchantment you want the level from.
	 * @return The level the enchantment has.
	 */
	public Integer getLevel(ItemStack item, CEnchantments enchant) {
		int level;
		String line = "";
		if(item.hasItemMeta()) {
			if(item.getItemMeta().hasLore()) {
				for(String lore : item.getItemMeta().getLore()) {
					if(lore.contains(enchant.getCustomName())) {
						line = lore;
						break;
					}
				}
			}
		}
		level = convertLevelInteger(line.replace(enchant.getEnchantment().getColor() + enchant.getCustomName() + " ", ""));
		if(!Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.UnSafe-Enchantments")) {
			if(level > enchant.getEnchantment().getMaxLevel()) {
				level = enchant.getEnchantment().getMaxLevel();
			}
		}
		return level;
	}
	
	/**
	 *
	 * @return The block list for blast.
	 */
	public ArrayList<Material> getBlockList() {
		return blockList;
	}
	
	/**
	 *
	 * @return The max rage stack level.
	 */
	public int getRageMaxLevel() {
		return rageMaxLevel;
	}
	
	/**
	 * Set the max rage stack level.
	 * @param level The new max stack level of the rage enchantment.
	 */
	public void setRageMaxLevel(int level) {
		rageMaxLevel = level;
	}
	
	/**
	 * Set if a player takes damage the current rage stack on the player will be lost.
	 * @param toggle True if they lose the rage stack on damage and false if not.
	 */
	public void setBreakRageOnDamage(Boolean toggle) {
		breakRageOnDamage = toggle;
	}
	
	/**
	 * Check if players lose their current rage stack on damage.
	 * @return True if they do and false if not.
	 */
	public Boolean isBreakRageOnDamageOn() {
		return breakRageOnDamage;
	}
	
	/**
	 * Check if players can enchant a stack of items with an enchantment book.
	 */
	public Boolean enchantStackedItems() {
		return enchantStackedItems;
	}
	
	/**
	 * This converts an integer into a roman numeral if its between 1-10 other wise it will just be the number as a string.
	 * @param i The integer you want to convert.
	 * @return The integer as a roman numeral if between 1-10 other wise the number as a string.
	 */
	public String convertLevelString(Integer i) {
		switch(i) {
			case 0:
			case 1:
				return "I";
			case 2:
				return "II";
			case 3:
				return "III";
			case 4:
				return "IV";
			case 5:
				return "V";
			case 6:
				return "VI";
			case 7:
				return "VII";
			case 8:
				return "VIII";
			case 9:
				return "IX";
			case 10:
				return "X";
			default:
				return i + "";
			
		}
	}
	
	/**
	 * This converts a String into a number if using a roman numeral from I-X.
	 * @param i The string you want to convert.
	 * @return The roman numeral as a number.
	 */
	public Integer convertLevelInteger(String i) {
		switch(i) {
			case "I":
				return 1;
			case "II":
				return 2;
			case "III":
				return 3;
			case "IV":
				return 4;
			case "V":
				return 5;
			case "VI":
				return 6;
			case "VII":
				return 7;
			case "VIII":
				return 8;
			case "IX":
				return 9;
			case "X":
				return 10;
			default:
				if(Methods.isInt(i)) {
					return Integer.parseInt(i);
				}else {
					return 0;
				}
		}
	}
	
	private void addCEPlayer(CEPlayer player) {
		players.add(player);
	}
	
	private void removeCEPlayer(CEPlayer player) {
		players.remove(player);
	}
	
	/**
	 * Reloads the gkit items.
	 * @param itemStrings The items as a string.
	 * @return A list of all the ItemStacks.
	 */
	private ArrayList<ItemStack> getKitItems(ArrayList<String> itemStrings) {
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
					ArrayList<String> lore = new ArrayList<>();
					if(d.contains(",")) {
						for(String line : d.split(",")) {
							lore.add(line.replaceAll("%comma%", ","));
						}
					}else {
						lore.add(d);
					}
					item.setLore(lore);
				}else {
					for(Enchantment en : Enchantment.values()) {
						if(d.split(":")[0].equalsIgnoreCase(Methods.getEnchantmentName(en)) ||
						Enchantment.getByName(d.split(":")[0]) != null) {
							String level = d.split(":")[1];
							if(level.contains("-")) {
								int randomLevel = pickLevel(Integer.parseInt(d.split(":")[1].split("-")[0]),
								Integer.parseInt(d.split(":")[1].split("-")[1]));
								if(randomLevel > 0) {
									item.addEnchantment(en, randomLevel);
								}
							}else {
								item.addEnchantment(en, Integer.parseInt(d.split(":")[1]));
							}
							break;
						}
					}
					for(CEnchantment en : getRegisteredEnchantments()) {
						if(d.split(":")[0].equalsIgnoreCase(en.getName()) ||
						d.split(":")[0].equalsIgnoreCase(en.getCustomName())) {
							String level = d.split(":")[1];
							if(level.contains("-")) {
								int randomLevel = pickLevel(Integer.parseInt(d.split(":")[1].split("-")[0]),
								Integer.parseInt(d.split(":")[1].split("-")[1]));
								if(randomLevel > 0) {
									item.addCEEnchantment(en, randomLevel);
								}
							}else {
								item.addCEEnchantment(en, Integer.parseInt(d.split(":")[1]));
							}
							break;
						}
					}
				}
			}
			items.add(item.build());
		}
		return items;
	}
	
	private ArrayList<ItemStack> getInfoGKit(ArrayList<String> itemStrings) {
		ArrayList<ItemStack> items = new ArrayList<>();
		for(String item : itemStrings) {
			String type = "";
			int amount = 0;
			String name = "";
			ArrayList<String> lore = new ArrayList<>();
			ArrayList<String> customEnchantments = new ArrayList<>();
			HashMap<Enchantment, Integer> enchantments = new HashMap<>();
			for(String sub : item.split(", ")) {
				if(sub.startsWith("Item:")) {
					sub = sub.replace("Item:", "");
					type = sub;
				}else if(sub.startsWith("Amount:")) {
					sub = sub.replace("Amount:", "");
					if(Methods.isInt(sub)) {
						amount = Integer.parseInt(sub);
					}
				}else if(sub.startsWith("Name:")) {
					sub = sub.replaceAll("Name:", "");
					name = sub;
				}else if(sub.startsWith("Lore:")) {
					sub = sub.replace("Lore:", "");
					if(sub.contains(",")) {
						lore.addAll(Arrays.asList(sub.split(",")));
					}else {
						lore.add(sub);
					}
				}else {
					for(Enchantment en : Enchantment.values()) {
						if(sub.split(":")[0].equalsIgnoreCase(Methods.getEnchantmentName(en)) ||
						Enchantment.getByName(sub.split(":")[0]) != null) {
							String level = sub.split(":")[1];
							if(level.contains("-")) {
								customEnchantments.add("&7" + sub.split(":")[0] + " " + level);
							}else {
								enchantments.put(en, Integer.parseInt(level));
							}
							break;
						}
					}
					for(CEnchantment en : getRegisteredEnchantments()) {
						if(sub.split(":")[0].equalsIgnoreCase(en.getName()) ||
						sub.split(":")[0].equalsIgnoreCase(en.getCustomName())) {
							customEnchantments.add(en.getColor() + en.getCustomName() + " " + sub.split(":")[1]);
							break;
						}
					}
				}
			}
			lore.addAll(0, customEnchantments);
			items.add(new ItemBuilder().setMaterial(type).setAmount(amount).setName(name).setLore(lore).setEnchantments(enchantments).build());
		}
		return items;
	}
	
	private Integer pickLevel(int min, int max) {
		return min + new Random().nextInt((max + 1) - min);
	}
	
}