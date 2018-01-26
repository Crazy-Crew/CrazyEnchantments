package me.badbones69.crazyenchantments.api;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CrazyEnchantments {
	
	private static CrazyEnchantments instance = new CrazyEnchantments();
	
	public static CrazyEnchantments getInstance() {
		return instance;
	}
	
	/**
	 * Only needs used when the player joines the server.
	 * This plugin does it automaticly, so there is no need to use it unless you have to.
	 * @param player The player you wish to load.
	 */
	public void loadCEPlayer(Player player) {
		FileConfiguration data = Main.settings.getData();
		String uuid = player.getUniqueId().toString();
		int souls = 0;
		boolean isActive = false;
		if(data.contains("Players." + uuid)) {
			if(data.contains("Players." + uuid + ".Souls-Information")) {
				souls = data.getInt("Players." + uuid + ".Souls-Information.Souls");
				isActive = data.getBoolean("Players." + uuid + ".Souls-Information.Is-Active");
			}
		}
		ArrayList<Cooldown> cooldowns = new ArrayList<Cooldown>();
		for(GKitz kit : getGKitz()) {
			if(data.contains("Players." + uuid + ".GKitz." + kit.getName())) {
				Calendar cooldown = Calendar.getInstance();
				cooldown.setTimeInMillis(data.getLong("Players." + uuid + ".GKitz." + kit.getName()));
				cooldowns.add(new Cooldown(kit, cooldown));
			}
		}
		DataStorage.addCEPlayer(new CEPlayer(player, souls, isActive, cooldowns));
	}
	
	/**
	 * Only needs used when the player leaves the server.
	 * This plugin removes the player automaticly, so don't use this method unless needed for some reason.
	 * @param player Player you wish to remove.
	 */
	public void unloadCEPlayer(Player player) {
		FileConfiguration data = Main.settings.getData();
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
			Main.settings.saveData();
		}
		DataStorage.removeCEPlayer(p);
	}
	
	/**
	 * This backup all the players data stored by this plugin.
	 * @param player The player you wish to backup.
	 */
	public void backupCEPlayer(Player player) {
		FileConfiguration data = Main.settings.getData();
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
			Main.settings.saveData();
		}
	}
	
	/**
	 * This backup all the players data stored by this plugin.
	 * @param player The player you wish to backup.
	 */
	public void backupCEPlayer(CEPlayer player) {
		FileConfiguration data = Main.settings.getData();
		String uuid = player.getPlayer().getUniqueId().toString();
		if(player != null) {
			if(player.getSouls() > 0) {
				data.set("Players." + uuid + ".Name", player.getPlayer().getName());
				data.set("Players." + uuid + ".Souls-Information.Souls", player.getSouls());
				data.set("Players." + uuid + ".Souls-Information.Is-Active", player.isSoulsActive());
			}
			for(Cooldown cooldown : player.getCooldowns()) {
				data.set("Players." + uuid + ".GKitz." + cooldown.getGKitz().getName(), cooldown.getCooldown().getTimeInMillis());
			}
			Main.settings.saveData();
		}
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
		return DataStorage.getGKitz();
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
		return DataStorage.getCEPlayers();
	}
	
	/**
	 *
	 * @return Returns the item the enchantment book will be.
	 */
	public ItemStack getEnchantmentBookItem() {
		return new ItemBuilder().setMaterial(Main.settings.getConfig().getString("Settings.Enchantment-Book-Item")).build();
	}
	
	/**
	 *
	 * @return List of all the enum enchantments.
	 */
	public ArrayList<CEnchantments> getEnchantments() {
		ArrayList<CEnchantments> enchs = new ArrayList<CEnchantments>();
		for(CEnchantments en : CEnchantments.values()) {
			enchs.add(en);
		}
		return enchs;
	}
	
	/**
	 *
	 * @param enchantment Enchantment that is being checked
	 * @return Returns true if its real and false if not
	 */
	public Boolean isEnchantment(String enchantment) {
		for(CEnchantments en : getEnchantments()) {
			if(enchantment.equalsIgnoreCase(en.getName()) || enchantment.equalsIgnoreCase(en.getCustomName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 *
	 * @param name The name or custom name of the enchantment.
	 * @return The enchantment.
	 */
	public CEnchantments getFromName(String name) {
		for(CEnchantments en : getEnchantments()) {
			if(en.getName().equalsIgnoreCase(name)) {
				return en;
			}
			//			try{
			//				if(en.getCustomName().equalsIgnoreCase(name)){
			//					return en;
			//				}
			//			}catch(Exception e){
			//				Bukkit.broadcastMessage("Enchantment failed to load: " + en.getName()); //This is used for Debugging.
			//			}
			if(en.getCustomName().equalsIgnoreCase(name)) {
				return en;
			}
		}
		return null;
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
						for(CEnchantments enchantment : getEnchantments()) {
							if(lore.startsWith(enchantment.getEnchantmentColor() + enchantment.getCustomName())) {
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
	public Boolean hasEnchantment(ItemStack item, CEnchantments enchantment) {
		if(item != null) {
			if(item.hasItemMeta()) {
				if(item.getItemMeta().hasLore()) {
					for(String lore : item.getItemMeta().getLore()) {
						if(lore.startsWith(enchantment.getEnchantmentColor() + enchantment.getCustomName())) {
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
	public String getHighestEnchantmentCategory(CEnchantments enchantment) {
		String top = "";
		int rarity = 0;
		for(String cat : getEnchantmentCategories(enchantment)) {
			if(getCategoryRarity(cat) >= rarity) {
				rarity = getCategoryRarity(cat);
				top = cat;
			}
		}
		return top;
	}
	
	/**
	 *
	 * @param enchantment The enchantment you want to check.
	 * @return All the categories the enchantment is in.
	 */
	public ArrayList<String> getEnchantmentCategories(CEnchantments enchantment) {
		ArrayList<String> cats = new ArrayList<String>();
		for(String c : Main.settings.getEnchs().getStringList("Enchantments." + enchantment.getName() + ".Categories")) {
			for(String C : getCategories()) {
				if(c.equalsIgnoreCase(C)) {
					cats.add(C);
				}
			}
		}
		return cats;
	}
	
	/**
	 * Get all the categories that can be used.
	 * @return List of all the categories.
	 */
	public ArrayList<String> getCategories() {
		ArrayList<String> categories = new ArrayList<String>();
		for(String category : Main.settings.getConfig().getConfigurationSection("Categories").getKeys(false)) {
			categories.add(category);
		}
		return categories;
	}
	
	/**
	 *
	 * @param category The category you want the rarity from.
	 * @return The level of the category's rarity.
	 */
	public Integer getCategoryRarity(String category) {
		int rarity = 0;
		FileConfiguration config = Main.settings.getConfig();
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
	public Boolean playerHasEnchantmentOn(Player player, ItemStack include, ItemStack exclude, CEnchantments enchantment) {
		for(ItemStack armor : player.getEquipment().getArmorContents()) {
			if(!armor.isSimilar(exclude)) {
				if(hasEnchantment(armor, enchantment)) {
					return true;
				}
			}
		}
		if(hasEnchantment(include, enchantment)) {
			return true;
		}
		return false;
	}
	
	/**
	 *
	 * @param player The player you want to check if they have the enchantment on their armor.
	 * @param item The item you want to exclude.
	 * @param enchantment The enchantment you are checking.
	 * @return True if a piece of armor has the enchantment and false if not.
	 */
	public Boolean playerHasEnchantmentOnExclude(Player player, ItemStack item, CEnchantments enchantment) {
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
	public Boolean playerHasEnchantmentOnInclude(Player player, ItemStack item, CEnchantments enchantment) {
		for(ItemStack armor : player.getEquipment().getArmorContents()) {
			if(hasEnchantment(armor, enchantment)) {
				return true;
			}
		}
		if(hasEnchantment(item, enchantment)) {
			return true;
		}
		return false;
	}
	
	/**
	 *
	 * @param player The player you want to get the highest level of an enchantment from.
	 * @param include The item you want to include.
	 * @param exclude The item you want to exclude.
	 * @param enchantment The enchantment you are checking.
	 * @return The highest level of the enchantment that the player currently has.
	 */
	public Integer getHighestEnchantmentLevel(Player player, ItemStack include, ItemStack exclude, CEnchantments enchantment) {
		int highest = 0;
		for(ItemStack armor : player.getEquipment().getArmorContents()) {
			if(!armor.isSimilar(exclude)) {
				if(hasEnchantment(armor, enchantment)) {
					int level = getPower(armor, enchantment);
					if(highest < level) {
						highest = level;
					}
				}
			}
		}
		if(hasEnchantment(include, enchantment)) {
			int level = getPower(include, enchantment);
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
	public Integer getHighestEnchantmentLevelExclude(Player player, ItemStack item, CEnchantments enchantment) {
		int highest = 0;
		for(ItemStack armor : player.getEquipment().getArmorContents()) {
			if(!armor.isSimilar(item)) {
				if(hasEnchantment(armor, enchantment)) {
					int level = getPower(armor, enchantment);
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
	public Integer getHighestEnchantmentLevelInclude(Player player, ItemStack item, CEnchantments enchantment) {
		int highest = 0;
		for(ItemStack armor : player.getEquipment().getArmorContents()) {
			if(hasEnchantment(armor, enchantment)) {
				int level = getPower(armor, enchantment);
				if(highest < level) {
					highest = level;
				}
			}
		}
		if(hasEnchantment(item, enchantment)) {
			int level = getPower(item, enchantment);
			if(highest < level) {
				highest = level;
			}
		}
		return highest;
	}
	
	/**
	 *
	 * @param item Item you want to add the enchantment to.
	 * @param enchant Enchantment you want added.
	 * @param level Tier of the enchantment.
	 * @return The item with the enchantment on it.
	 */
	public ItemStack addEnchantment(ItemStack item, CEnchantments enchant, Integer level) {
		if(hasEnchantment(item, enchant)) {
			removeEnchantment(item, enchant);
		}
		List<String> newLore = new ArrayList<>();
		List<String> lores = new ArrayList<>();
		HashMap<String, String> enchantments = new HashMap<String, String>();
		for(CEnchantments en : getItemEnchantments(item)) {
			enchantments.put(en.getName(), Methods.color(en.getEnchantmentColor() + en.getCustomName() + " " + convertPower(getPower(item, en))));
			removeEnchantment(item, en);
		}
		for(String en : Main.CustomE.getItemEnchantments(item)) {
			enchantments.put(en, Methods.color(Main.CustomE.getEnchantmentColor(en) + Main.CustomE.getCustomName(en) + " " + convertPower(Main.CustomE.getPower(item, en))));
			Main.CustomE.removeEnchantment(item, en);
		}
		ItemMeta meta = item.getItemMeta();
		if(meta != null) {
			if(meta.hasLore()) {
				lores.addAll(item.getItemMeta().getLore());
			}
		}
		enchantments.put(enchant.getName(), Methods.color(enchant.getEnchantmentColor() + enchant.getCustomName() + " " + convertPower(level)));
		for(String en : enchantments.keySet()) {
			newLore.add(enchantments.get(en));
		}
		newLore.addAll(lores);
		ItemBuilder newItem = ItemBuilder.convertItemStack(item);
		newItem.setLore(newLore);
		return newItem.build();
	}
	
	/**
	 *
	 * @param item Item you want to remove the enchantment from.
	 * @param enchant Enchantment you want removed.
	 * @return Item with out the enchantment.
	 */
	public ItemStack removeEnchantment(ItemStack item, CEnchantments enchant) {
		List<String> newLore = new ArrayList<String>();
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
	public ArrayList<CEnchantments> getItemEnchantments(ItemStack item) {
		ArrayList<CEnchantments> enchantments = new ArrayList<CEnchantments>();
		if(item != null) {
			if(item.hasItemMeta()) {
				if(item.getItemMeta().hasLore()) {
					for(String lore : item.getItemMeta().getLore()) {
						for(CEnchantments en : getEnchantments()) {
							if(lore.startsWith(en.getEnchantmentColor() + en.getCustomName())) {
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
					if(hasEnchantment(armor, ench)) {
						if(ench.isEnabled()) {
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
		HashMap<PotionEffectType, Integer> effects = new HashMap<PotionEffectType, Integer>();
		ArrayList<ItemStack> items = new ArrayList<>();
		for(ItemStack armor : player.getEquipment().getArmorContents()) {
			items.add(armor);
		}
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
						if(hasEnchantment(armor, ench)) {
							int power = getPower(armor, ench);
							if(!Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.UnSafe-Enchantments")) {
								if(power > getMaxPower(ench)) {
									power = getMaxPower(ench);
								}
							}
							for(PotionEffectType type : armorEffects.get(enchantment).keySet()) {
								if(armorEffects.get(ench).containsKey(type)) {
									if(effects.containsKey(type)) {
										int updated = effects.get(type);
										if(updated < (power + armorEffects.get(ench).get(type))) {
											effects.put(type, power + armorEffects.get(ench).get(type));
										}
									}else {
										effects.put(type, power + armorEffects.get(ench).get(type));
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
		HashMap<CEnchantments, HashMap<PotionEffectType, Integer>> enchants = new HashMap<CEnchantments, HashMap<PotionEffectType, Integer>>();
		enchants.put(CEnchantments.BURNSHIELD, new HashMap<PotionEffectType, Integer>());
		enchants.get(CEnchantments.BURNSHIELD).put(PotionEffectType.FIRE_RESISTANCE, -1);
		
		enchants.put(CEnchantments.DRUNK, new HashMap<PotionEffectType, Integer>());
		enchants.get(CEnchantments.DRUNK).put(PotionEffectType.INCREASE_DAMAGE, -1);
		enchants.get(CEnchantments.DRUNK).put(PotionEffectType.SLOW_DIGGING, -1);
		enchants.get(CEnchantments.DRUNK).put(PotionEffectType.SLOW, 0);
		
		enchants.put(CEnchantments.HULK, new HashMap<PotionEffectType, Integer>());
		enchants.get(CEnchantments.HULK).put(PotionEffectType.INCREASE_DAMAGE, -1);
		enchants.get(CEnchantments.HULK).put(PotionEffectType.DAMAGE_RESISTANCE, -1);
		enchants.get(CEnchantments.HULK).put(PotionEffectType.SLOW, 0);
		
		enchants.put(CEnchantments.VALOR, new HashMap<PotionEffectType, Integer>());
		enchants.get(CEnchantments.VALOR).put(PotionEffectType.DAMAGE_RESISTANCE, -1);
		
		enchants.put(CEnchantments.OVERLOAD, new HashMap<PotionEffectType, Integer>());
		enchants.get(CEnchantments.OVERLOAD).put(PotionEffectType.HEALTH_BOOST, 0);
		
		enchants.put(CEnchantments.NINJA, new HashMap<PotionEffectType, Integer>());
		enchants.get(CEnchantments.NINJA).put(PotionEffectType.HEALTH_BOOST, -1);
		enchants.get(CEnchantments.NINJA).put(PotionEffectType.SPEED, -1);
		
		enchants.put(CEnchantments.INSOMNIA, new HashMap<PotionEffectType, Integer>());
		enchants.get(CEnchantments.INSOMNIA).put(PotionEffectType.CONFUSION, -1);
		enchants.get(CEnchantments.INSOMNIA).put(PotionEffectType.SLOW_DIGGING, -1);
		enchants.get(CEnchantments.INSOMNIA).put(PotionEffectType.SLOW, 0);
		
		enchants.put(CEnchantments.ANTIGRAVITY, new HashMap<PotionEffectType, Integer>());
		enchants.get(CEnchantments.ANTIGRAVITY).put(PotionEffectType.JUMP, 1);
		
		enchants.put(CEnchantments.GEARS, new HashMap<PotionEffectType, Integer>());
		enchants.get(CEnchantments.GEARS).put(PotionEffectType.SPEED, -1);
		
		enchants.put(CEnchantments.SPRINGS, new HashMap<PotionEffectType, Integer>());
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
						for(CEnchantments en : getEnchantments()) {
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
		CEBook ceBook = new CEBook(getEnchantmentBookEnchantmnet(book), getBookPower(book, getEnchantmentBookEnchantmnet(book)), book.getAmount());
		ceBook.setSuccessRate(Methods.getPercent("%Success_Rate%", book, Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore")));
		ceBook.setDestoryRate(Methods.getPercent("%Destroy_Rate%", book, Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore")));
		ceBook.setGlowing(Main.settings.getConfig().getBoolean("Settings.Enchantment-Book-Glowing"));
		return ceBook;
	}
	
	/**
	 * Get the enchantment from an enchantment book.
	 * @param book The book you want the enchantment from.
	 * @return The enchantment the book is.
	 */
	public CEnchantments getEnchantmentBookEnchantmnet(ItemStack book) {
		if(book != null) {
			if(book.hasItemMeta()) {
				if(book.getItemMeta().hasDisplayName()) {
					if(book.getType() == getEnchantmentBookItem().getType()) {
						for(CEnchantments en : getEnchantments()) {
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
	 * @param book The book you are getting the power from.
	 * @param enchant The enchantment you want the power from.
	 * @return The power the enchantment has.
	 */
	public Integer getBookPower(ItemStack book, CEnchantments enchant) {
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
	 * @param item Item you are getting the power from.
	 * @param enchant The enchantment you want the power from.
	 * @return The power the enchantment has.
	 */
	public Integer getPower(ItemStack item, CEnchantments enchant) {
		int power = 0;
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
		line = line.replace(enchant.getEnchantmentColor() + enchant.getCustomName() + " ", "");
		if(Methods.isInt(line)) power = Integer.parseInt(line);
		if(line.equalsIgnoreCase("I")) power = 1;
		if(line.equalsIgnoreCase("II")) power = 2;
		if(line.equalsIgnoreCase("III")) power = 3;
		if(line.equalsIgnoreCase("IV")) power = 4;
		if(line.equalsIgnoreCase("V")) power = 5;
		if(line.equalsIgnoreCase("VI")) power = 6;
		if(line.equalsIgnoreCase("VII")) power = 7;
		if(line.equalsIgnoreCase("VIII")) power = 8;
		if(line.equalsIgnoreCase("IX")) power = 9;
		if(line.equalsIgnoreCase("X")) power = 10;
		if(!Main.settings.getConfig().getBoolean("Settings.EnchantmentOptions.UnSafe-Enchantments")) {
			if(power > getMaxPower(enchant)) {
				power = getMaxPower(enchant);
			}
		}
		return power;
	}
	
	/**
	 *
	 * @return The block list for blast.
	 */
	public ArrayList<Material> getBlockList() {
		return DataStorage.getBlockList();
	}
	
	/**
	 *
	 * @return The max rage stack level.
	 */
	public Integer getMaxRageLevel() {
		return DataStorage.getRageMaxLevel();
	}
	
	/**
	 * Gets the max enchantment level of an enchantment.
	 * @param enchant The enchantment you want to get.
	 * @return The max enchantment level.
	 */
	public Integer getMaxPower(CEnchantments enchant) {
		return Main.settings.getEnchs().getInt("Enchantments." + enchant.getName() + ".MaxPower");
	}
	
	/**
	 * This converts an integer into a roman numeral if its between 1-10 other wise it will just be the number as a string.
	 * @param i The integer you want to convert.
	 * @return The integer as a roman numeral if between 1-10 other wise the number as a string.
	 */
	public String convertPower(Integer i) {
		if(i <= 0) return "I";
		if(i == 1) return "I";
		if(i == 2) return "II";
		if(i == 3) return "III";
		if(i == 4) return "IV";
		if(i == 5) return "V";
		if(i == 6) return "VI";
		if(i == 7) return "VII";
		if(i == 8) return "VIII";
		if(i == 9) return "IX";
		if(i == 10) return "X";
		return i + "";
	}
	
	/**
	 * Reloads the gkit items.
	 * @param itemStrings The items as a string.
	 * @return A list of all the ItemStacks.
	 */
	public ArrayList<ItemStack> getKitItems(ArrayList<String> itemStrings) {
		return DataStorage.getKitItems(itemStrings);
	}
	
	/**
	 * Gets the plugin.
	 * @return The plugin as a Plugin object.
	 */
	public Plugin getPlugin() {
		return Bukkit.getPluginManager().getPlugin("CrazyEnchantments");
	}
	
}