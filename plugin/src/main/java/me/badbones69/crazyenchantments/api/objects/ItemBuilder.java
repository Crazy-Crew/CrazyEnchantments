package me.badbones69.crazyenchantments.api.objects;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.multisupport.SkullCreator;
import me.badbones69.crazyenchantments.multisupport.Version;
import me.badbones69.crazyenchantments.multisupport.nbttagapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * The ItemBuilder is designed to make creating items easier by creating an easy to use Builder.
 * This will allow you to covert an existing ItemStack into an ItemBuilder to allow you to edit
 * an existing ItemStack or make a new ItemStack from scratch.
 *
 * @author BadBones69
 *
 */
public class ItemBuilder implements Cloneable {
	
	private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
	private static Version version = Version.getCurrentVersion();
	private Material material;
	private int damage;
	private String name;
	private List<String> lore;
	private int amount;
	private String player;
	private boolean isHash;
	private boolean isURL;
	private boolean isHead;
	private HashMap<Enchantment, Integer> enchantments;
	private boolean unbreakable;
	private boolean hideItemFlags;
	private boolean glowing;
	private ItemStack referenceItem;
	private boolean isMobEgg;
	private EntityType entityType;
	private PotionType potionType;
	private Color armorColor;
	private int customModelData;
	private boolean useCustomModelData;
	private HashMap<String, String> namePlaceholders;
	private HashMap<String, String> lorePlaceholders;
	
	/**
	 * The initial starting point for making an item.
	 */
	public ItemBuilder() {
		this.material = Material.STONE;
		this.damage = 0;
		this.name = "";
		this.lore = new ArrayList<>();
		this.amount = 1;
		this.player = "";
		this.isHash = false;
		this.isURL = false;
		this.isHead = false;
		this.enchantments = new HashMap<>();
		this.unbreakable = false;
		this.hideItemFlags = false;
		this.glowing = false;
		this.entityType = EntityType.BAT;
		this.potionType = null;
		this.armorColor = null;
		this.customModelData = 0;
		this.useCustomModelData = false;
		this.isMobEgg = false;
		this.namePlaceholders = new HashMap<>();
		this.lorePlaceholders = new HashMap<>();
	}
	
	/**
	 * Convert an ItemStack to an ItemBuilder to allow easier editing of the ItemStack.
	 * @param item The ItemStack you wish to convert into an ItemBuilder.
	 * @return The ItemStack as an ItemBuilder with all the info from the item.
	 */
	public static ItemBuilder convertItemStack(ItemStack item) {
		ItemBuilder itemBuilder = new ItemBuilder()
		.setReferenceItem(item)
		.setAmount(item.getAmount())
		.setMaterial(item.getType())
		.setEnchantments(new HashMap<>(item.getEnchantments()));
		if(item.hasItemMeta()) {
			ItemMeta itemMeta = item.getItemMeta();
			itemBuilder.setName(itemMeta.getDisplayName())
			.setLore(itemMeta.getLore());
			NBTItem nbt = new NBTItem(item);
			if(nbt.hasKey("Unbreakable")) {
				itemBuilder.setUnbreakable(nbt.getBoolean("Unbreakable"));
			}
			if(version.isNewer(Version.v1_12_R1)) {
				if(itemMeta instanceof org.bukkit.inventory.meta.Damageable) {
					itemBuilder.setDamage(((org.bukkit.inventory.meta.Damageable) itemMeta).getDamage());
				}
			}else {
				itemBuilder.setDamage(item.getDurability());
			}
		}
		return itemBuilder;
	}
	
	/**
	 * Get the type of item as a Material the builder is set to.
	 * @return The type of material the builder is set to.
	 */
	public Material getMaterial() {
		return material;
	}
	
	/**
	 * Set the type of item the builder is set to.
	 * @param material The material you wish to set.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setMaterial(Material material) {
		this.material = material;
		this.isHead = material == (ce.useNewMaterial() ? Material.matchMaterial("PLAYER_HEAD") : Material.matchMaterial("SKULL_ITEM"));
		return this;
	}
	
	/**
	 * Set the type of item and its metadata in the builder.
	 * @param material The string must be in this form: %Material% or %Material%:%MetaData%
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setMaterial(String material) {
		if(material.contains(":")) {// Sets the durability or another value option.
			String[] b = material.split(":");
			material = b[0];
			String value = b[1];
			if(value.contains("#")) {// <ID>:<Durability>#<CustomModelData>
				String modelData = value.split("#")[1];
				if(Methods.isInt(modelData)) {//Value is a number.
					this.useCustomModelData = true;
					this.customModelData = Integer.parseInt(modelData);
				}
			}
			value = value.replace("#" + customModelData, "");
			if(Methods.isInt(value)) {//Value is durability.
				this.damage = Integer.parseInt(value);
			}else {//Value is something else.
				this.potionType = getPotionType(PotionEffectType.getByName(value));
				this.armorColor = getColor(value);
			}
		}else if(material.contains("#")) {
			String[] b = material.split("#");
			material = b[0];
			if(Methods.isInt(b[1])) {//Value is a number.
				this.useCustomModelData = true;
				this.customModelData = Integer.parseInt(b[1]);
			}
		}
		Material m = Material.matchMaterial(material);
		if(m != null) {// Sets the material.
			this.material = m;
			//1.9-1.12.2
			if(version.isNewer(Version.v1_8_R3) && version.isOlder(Version.v1_13_R2)) {
				if(m == Material.matchMaterial("MONSTER_EGG")) {
					this.entityType = EntityType.fromId(damage);
					this.damage = 0;
					this.isMobEgg = true;
				}
			}
		}
		this.isHead = this.material == (ce.useNewMaterial() ? Material.matchMaterial("PLAYER_HEAD") : Material.matchMaterial("SKULL_ITEM"));
		return this;
	}
	
	/**
	 * Set the type of item and its metadata in the builder.
	 * @param newMaterial The 1.13+ string must be in this form: %Material% or %Material%:%MetaData%
	 * @param oldMaterial The 1.12.2- string must be in this form: %Material% or %Material%:%MetaData%
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setMaterial(String newMaterial, String oldMaterial) {
		return setMaterial(ce.useNewMaterial() ? newMaterial : oldMaterial);
	}
	
	/**
	 * Get the damage of the item.
	 * @return The damage of the item as an int.
	 */
	public int getDamage() {
		return damage;
	}
	
	/**
	 * Set the items damage value.
	 * @param damage The damage value of the item.
	 */
	public ItemBuilder setDamage(int damage) {
		this.damage = damage;
		return this;
	}
	
	/**
	 * Get the name the of the item in the builder.
	 * @return The name as a string that is already been color converted.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the name of the item in the builder. This will auto force color the name if it contains color code. (&a, &c, &7, etc...)
	 * @param name The name of the item in the builder.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setName(String name) {
		if(name != null) {
			this.name = color(name);
		}
		return this;
	}
	
	/**
	 * Set the placeholders for the name of the item.
	 * @param placeholders The placeholders that will be used.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setNamePlaceholders(HashMap<String, String> placeholders) {
		this.namePlaceholders = placeholders;
		return this;
	}
	
	/**
	 * Add a placeholder to the name of the item.
	 * @param placeholder The placeholder that will be replaced.
	 * @param argument The argument you wish to replace the placeholder with.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder addNamePlaceholder(String placeholder, String argument) {
		this.namePlaceholders.put(placeholder, argument);
		return this;
	}
	
	/**
	 * Remove a placeholder from the list.
	 * @param placeholder The placeholder you wish to remove.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder removeNamePlaceholder(String placeholder) {
		this.namePlaceholders.remove(placeholder);
		return this;
	}
	
	/**
	 * Get the item's name with all the placeholders added to it.
	 * @return The name with all the placeholders in it.
	 */
	public String getUpdatedName() {
		String newName = name;
		for(String placeholder : namePlaceholders.keySet()) {
			newName = newName.replaceAll(placeholder, namePlaceholders.get(placeholder))
			.replaceAll(placeholder.toLowerCase(), namePlaceholders.get(placeholder));
		}
		return newName;
	}
	
	/**
	 * Get the lore of the item in the builder.
	 * @return The lore of the item in the builder. This will already be color coded.
	 */
	public List<String> getLore() {
		return lore;
	}
	
	/**
	 * Set the lore of the item in the builder. This will auto force color in all the lores that contains color code. (&a, &c, &7, etc...)
	 * @param lore The lore of the item in the builder.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setLore(List<String> lore) {
		if(lore != null) {
			this.lore.clear();
			for(String i : lore) {
				this.lore.add(color(i));
			}
		}
		return this;
	}
	
	/**
	 * Add a line to the current lore of the item. This will auto force color in the lore that contains color code. (&a, &c, &7, etc...)
	 * @param lore The new line you wish to add.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder addLore(String lore) {
		if(lore != null) {
			this.lore.add(color(lore));
		}
		return this;
	}
	
	/**
	 * Set the placeholders that are in the lore of the item.
	 * @param placeholders The placeholders that you wish to use.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setLorePlaceholders(HashMap<String, String> placeholders) {
		this.lorePlaceholders = placeholders;
		return this;
	}
	
	/**
	 * Add a placeholder to the lore of the item.
	 * @param placeholder The placeholder you wish to replace.
	 * @param argument The argument that will replace the placeholder.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder addLorePlaceholder(String placeholder, String argument) {
		this.lorePlaceholders.put(placeholder, argument);
		return this;
	}
	
	/**
	 * Add a placeholder to the lore of the item.
	 * @param placeholder The placeholder you wish to replace.
	 * @param argument The argument that will replace the placeholder.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder addLorePlaceholder(String placeholder, int argument) {
		this.lorePlaceholders.put(placeholder, argument + "");
		return this;
	}
	
	/**
	 * Remove a placeholder from the lore.
	 * @param placeholder The placeholder you wish to remove.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder removeLorePlaceholder(String placeholder) {
		this.lorePlaceholders.remove(placeholder);
		return this;
	}
	
	/**
	 * Get the lore with all the placeholders added to it.
	 * @return The lore with all placeholders in it.
	 */
	public List<String> getUpdatedLore() {
		List<String> newLore = new ArrayList<>();
		for(String i : lore) {
			for(String placeholder : lorePlaceholders.keySet()) {
				i = i.replaceAll(placeholder, lorePlaceholders.get(placeholder))
				.replaceAll(placeholder.toLowerCase(), lorePlaceholders.get(placeholder));
			}
			newLore.add(i);
		}
		return newLore;
	}
	
	/**
	 * Get the entity type of the mob egg.
	 * @return The EntityType of the mob egg.
	 */
	public EntityType getEntityType() {
		return entityType;
	}
	
	/**
	 * Sets the type of mob egg.
	 * @param entityType The entity type the mob egg will be.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setEntityType(EntityType entityType) {
		this.entityType = entityType;
		return this;
	}
	
	/**
	 * Get the type of potion effect on the item. Only works on Tipped Arrows.
	 * @return The PotionType set to the item.
	 */
	public PotionType getPotionType() {
		return potionType;
	}
	
	/**
	 * Set the PotionType on the item.
	 * @param potionType The PotionType added to the item.
	 */
	public void setPotionType(PotionType potionType) {
		this.potionType = potionType;
	}
	
	/**
	 * Get the color leather armor is set to.
	 * @return The Color the armor is set to.
	 */
	public Color getArmorColor() {
		return armorColor;
	}
	
	/**
	 * Set the color the Leather Armor is going to be.
	 * @param armorColor The color of the leather armor.
	 */
	public void setArmorColor(Color armorColor) {
		this.armorColor = armorColor;
	}
	
	/**
	 * Check if the current item is a mob egg.
	 * @return True if it is and false if not.
	 */
	public boolean isMobEgg() {
		return isMobEgg;
	}
	
	/**
	 * The amount of the item stack in the builder.
	 * @return The amount that is set in the builder.
	 */
	public int getAmount() {
		return amount;
	}
	
	/**
	 * Get the amount of the item stack in the builder.
	 * @param amount The amount that is in the item stack.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setAmount(int amount) {
		this.amount = amount;
		return this;
	}
	
	/**
	 * Get the name of the player being used as a head.
	 * @return The name of the player being used on the head.
	 */
	public String getPlayer() {
		return player;
	}
	
	/**
	 * Set the player that will be displayed on the head.
	 * @param player The player being displayed on the head.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setPlayer(String player) {
		this.player = player;
		if(player != null && player.length() > 16) {
			this.isHash = true;
			this.isURL = player.startsWith("http");
		}
		return this;
	}
	
	/**
	 * Check if the item is a player heads.
	 * @return True if it is a player head and false if not.
	 */
	public boolean isHead() {
		return isHead;
	}
	
	/**
	 * Check if the player name is a Base64.
	 * @return True if it is a Base64 and false if not.
	 */
	public boolean isHash() {
		return isHash;
	}
	
	/**
	 * Check if the hash is a url or a Base64.
	 * @return True if it is a url and false if it is a Base64.
	 */
	public boolean isURL() {
		return isURL;
	}
	
	/**
	 * Get the enchantments that are on the item in the builder.
	 * @return The enchantments that are on the item in the builder.
	 */
	public HashMap<Enchantment, Integer> getEnchantments() {
		return enchantments;
	}
	
	/**
	 * Set a list of enchantments that will go onto the item in the builder. These can have unsafe levels.
	 * It will also override any enchantments used in the "ItemBuilder#addEnchantment()" method.
	 * @param enchantments A list of enchantments that will go onto the item in the builder.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setEnchantments(HashMap<Enchantment, Integer> enchantments) {
		if(enchantments != null) {
			this.enchantments = enchantments;
		}
		return this;
	}
	
	/**
	 * Add an enchantment to the item in the builder.
	 * @param enchantment The enchantment you wish to add.
	 * @param level The level of the enchantment. This can be unsafe levels.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder addEnchantments(Enchantment enchantment, int level) {
		this.enchantments.put(enchantment, level);
		return this;
	}
	
	/**
	 * Remove an enchantment from the item in the builder.
	 * @param enchantment The enchantment you wish to remove.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder removeEnchantments(Enchantment enchantment) {
		this.enchantments.remove(enchantment);
		return this;
	}
	
	/**
	 * Check if the item in the builder is unbreakable.
	 * @return The ItemBuilder with updated info.
	 */
	public boolean isUnbreakable() {
		return unbreakable;
	}
	
	/**
	 * Set if the item in the builder to be unbreakable or not.
	 * @param unbreakable True will set it to be unbreakable and false will make it able to take damage.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setUnbreakable(boolean unbreakable) {
		this.unbreakable = unbreakable;
		return this;
	}
	
	/**
	 * Set if the item should hide item flags or not
	 * @param hideItemFlags true the item will hide item flags. false will show them.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder hideItemFlags(boolean hideItemFlags) {
		this.hideItemFlags = hideItemFlags;
		return this;
	}
	
	/**
	 * Check if the item in the builder has hidden item flags.
	 * @return The ItemBuilder with updated info.
	 */
	public boolean areItemFlagsHidden() {
		return hideItemFlags;
	}
	
	/**
	 * Check if the item in the builder is glowing.
	 * @return The ItemBuilder with updated info.
	 */
	public boolean isGlowing() {
		return glowing;
	}
	
	/**
	 * Set if the item in the builder to be glowing or not.
	 * @param glowing True will set the item to have a glowing effect.
	 * @return The ItemBuilder with updated info.
	 */
	public ItemBuilder setGlowing(boolean glowing) {
		this.glowing = glowing;
		return this;
	}
	
	/**
	 * Builder the item from all the information that was given to the builder.
	 * @return The result of all the info that was given to the builder as an ItemStack.
	 */
	public ItemStack build() {
		ItemStack item = referenceItem != null ? referenceItem : new ItemStack(material, amount);
		if(item.getType() != Material.AIR) {
			if(isHead) {//Has to go 1st due to it removing all data when finished.
				if(isHash) {//Sauce: https://github.com/deanveloper/SkullCreator
					if(isURL) {
						SkullCreator.itemWithUrl(item, player);
					}else {
						SkullCreator.itemWithBase64(item, player);
					}
				}
			}
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(getUpdatedName());
			itemMeta.setLore(getUpdatedLore());
			if(version.isSame(Version.v1_8_R3)) {
				if(isHead && !isHash && player != null && !player.equals("")) {
					SkullMeta skullMeta = (SkullMeta) itemMeta;
					skullMeta.setOwner(player);
				}
			}
			if(version.isNewer(Version.v1_10_R1)) {
				itemMeta.setUnbreakable(unbreakable);
			}
			if(version.isNewer(Version.v1_12_R1)) {
				if(itemMeta instanceof org.bukkit.inventory.meta.Damageable) {
					((org.bukkit.inventory.meta.Damageable) itemMeta).setDamage(damage);
				}
			}else {
				item.setDurability((short) damage);
			}
			if(potionType != null) {
				PotionMeta potionMeta = (PotionMeta) itemMeta;
				potionMeta.setBasePotionData(new PotionData(potionType));
			}
			if(armorColor != null) {
				LeatherArmorMeta leatherMeta = (LeatherArmorMeta) itemMeta;
				leatherMeta.setColor(armorColor);
			}
			if(useCustomModelData) {
				itemMeta.setCustomModelData(customModelData);
			}
			item.setItemMeta(itemMeta);
			hideFlags(item);
			item.addUnsafeEnchantments(enchantments);
			addGlow(item);
			NBTItem nbt = new NBTItem(item);
			if(isHead) {
				if(!isHash && player != null && !player.equals("") && version.isNewer(Version.v1_8_R3)) {
					nbt.setString("SkullOwner", player);
				}
			}
			if(isMobEgg) {
				if(entityType != null) {
					nbt.addCompound("EntityTag").setString("id", "minecraft:" + entityType.name());
				}
			}
			if(version.isOlder(Version.v1_11_R1)) {
				if(unbreakable) {
					nbt.setBoolean("Unbreakable", true);
					nbt.setInteger("HideFlags", 4);
				}
			}
			return nbt.getItem();
		}else {
			return item;
		}
	}
	
	/**
	 * Get a clone of the object.
	 * @return a new cloned object.
	 */
	public ItemBuilder clone() {
		try {
			return (ItemBuilder) super.clone();
		}catch(CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return new ItemBuilder();
	}
	
	/**
	 * Sets the converted item as a reference to try and save NBT tags and stuff.
	 * @param referenceItem The item that is being referenced.
	 * @return The ItemBuilder with updated info.
	 */
	private ItemBuilder setReferenceItem(ItemStack referenceItem) {
		this.referenceItem = referenceItem;
		return this;
	}
	
	private String color(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	private ItemStack hideFlags(ItemStack item) {
		if(hideItemFlags) {
			if(item != null && item.hasItemMeta()) {
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.addItemFlags(ItemFlag.values());
				item.setItemMeta(itemMeta);
				return item;
			}
		}
		return item;
	}
	
	private ItemStack addGlow(ItemStack item) {
		if(glowing) {
			try {
				if(item != null) {
					if(item.hasItemMeta()) {
						if(item.getItemMeta().hasEnchants()) {
							return item;
						}
					}
					item.addUnsafeEnchantment(Enchantment.LUCK, 1);
					ItemMeta meta = item.getItemMeta();
					meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
					item.setItemMeta(meta);
				}
				return item;
			}catch(NoClassDefFoundError e) {
				return item;
			}
		}
		return item;
	}
	
	private PotionType getPotionType(PotionEffectType type) {
		if(type != null) {
			if(type.equals(PotionEffectType.FIRE_RESISTANCE)) {
				return PotionType.FIRE_RESISTANCE;
			}else if(type.equals(PotionEffectType.HARM)) {
				return PotionType.INSTANT_DAMAGE;
			}else if(type.equals(PotionEffectType.HEAL)) {
				return PotionType.INSTANT_HEAL;
			}else if(type.equals(PotionEffectType.INVISIBILITY)) {
				return PotionType.INVISIBILITY;
			}else if(type.equals(PotionEffectType.JUMP)) {
				return PotionType.JUMP;
			}else if(type.equals(PotionEffectType.getByName("LUCK"))) {
				return PotionType.valueOf("LUCK");
			}else if(type.equals(PotionEffectType.NIGHT_VISION)) {
				return PotionType.NIGHT_VISION;
			}else if(type.equals(PotionEffectType.POISON)) {
				return PotionType.POISON;
			}else if(type.equals(PotionEffectType.REGENERATION)) {
				return PotionType.REGEN;
			}else if(type.equals(PotionEffectType.SLOW)) {
				return PotionType.SLOWNESS;
			}else if(type.equals(PotionEffectType.SPEED)) {
				return PotionType.SPEED;
			}else if(type.equals(PotionEffectType.INCREASE_DAMAGE)) {
				return PotionType.STRENGTH;
			}else if(type.equals(PotionEffectType.WATER_BREATHING)) {
				return PotionType.WATER_BREATHING;
			}else if(type.equals(PotionEffectType.WEAKNESS)) {
				return PotionType.WEAKNESS;
			}
		}
		return null;
	}
	
	private Color getColor(String color) {
		if(color != null) {
			switch(color.toUpperCase()) {
				case "AQUA":
					return Color.AQUA;
				case "BLACK":
					return Color.BLACK;
				case "BLUE":
					return Color.BLUE;
				case "FUCHSIA":
					return Color.FUCHSIA;
				case "GRAY":
					return Color.GRAY;
				case "GREEN":
					return Color.GREEN;
				case "LIME":
					return Color.LIME;
				case "MAROON":
					return Color.MAROON;
				case "NAVY":
					return Color.NAVY;
				case "OLIVE":
					return Color.OLIVE;
				case "ORANGE":
					return Color.ORANGE;
				case "PURPLE":
					return Color.PURPLE;
				case "RED":
					return Color.RED;
				case "SILVER":
					return Color.SILVER;
				case "TEAL":
					return Color.TEAL;
				case "WHITE":
					return Color.WHITE;
				case "YELLOW":
					return Color.YELLOW;
			}
			try {
				String[] rgb = color.split(",");
				return Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
			}catch(Exception ignore) {
			}
		}
		return null;
	}
	
}