package me.badbones69.crazyenchantments.api.objects;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.multisupport.SkullCreator;
import me.badbones69.crazyenchantments.multisupport.Version;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

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
    private static boolean useNewMaterial = ce.useNewMaterial();
    private Material material;
    private int damage;
    private String name;
    private List<String> lore;
    private int amount;
    private String player;
    private boolean isHash;
    private boolean isURL;
    private boolean isHead;
    private Map<Enchantment, Integer> enchantments;
    private boolean unbreakable;
    private boolean hideItemFlags;
    private boolean glowing;
    private ItemStack referenceItem;
    private boolean isMobEgg;
    private EntityType entityType;
    private boolean isTippedArrow;
    private PotionType potionType;
    private Color potionColor;
    private boolean isPotion;
    private Color armorColor;
    private boolean isLeatherArmor;
    private List<Pattern> patterns;
    private boolean isBanner;
    private boolean isShield;
    private int customModelData;
    private boolean useCustomModelData;
    private Map<String, String> namePlaceholders;
    private Map<String, String> lorePlaceholders;
    private List<ItemFlag> itemFlags;
    
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
        this.isTippedArrow = false;
        this.potionType = null;
        this.potionColor = null;
        this.isPotion = false;
        this.armorColor = null;
        this.isLeatherArmor = false;
        this.patterns = new ArrayList<>();
        this.isBanner = false;
        this.isShield = false;
        this.customModelData = 0;
        this.useCustomModelData = false;
        this.isMobEgg = false;
        this.namePlaceholders = new HashMap<>();
        this.lorePlaceholders = new HashMap<>();
        this.itemFlags = new ArrayList<>();
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
        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();
            itemBuilder.setName(itemMeta.getDisplayName())
            .setLore(itemMeta.getLore());
            NBTItem nbt = new NBTItem(item);
            if (nbt.hasKey("Unbreakable")) {
                itemBuilder.setUnbreakable(nbt.getBoolean("Unbreakable"));
            }
            if (useNewMaterial) {
                if (itemMeta instanceof Damageable) {
                    itemBuilder.setDamage(((Damageable) itemMeta).getDamage());
                }
            } else {
                itemBuilder.setDamage(item.getDurability());
            }
        }
        return itemBuilder;
    }
    
    public static ItemBuilder convertString(String itemString) {
        return convertString(itemString, null);
    }
    
    public static ItemBuilder convertString(String itemString, String placeHolder) {
        ItemBuilder itemBuilder = new ItemBuilder();
        try {
            for (String optionString : itemString.split(", ")) {
                String option = optionString.split(":")[0];
                String value = optionString.replace(option + ":", "").replace(option, "");
                switch (option.toLowerCase()) {
                    case "item":
                        itemBuilder.setMaterial(value);
                        break;
                    case "name":
                        itemBuilder.setName(value);
                        break;
                    case "amount":
                        try {
                            itemBuilder.setAmount(Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            itemBuilder.setAmount(1);
                        }
                        break;
                    case "lore":
                        itemBuilder.setLore(Arrays.asList(value.split(",")));
                        break;
                    case "player":
                        itemBuilder.setPlayer(value);
                        break;
                    case "unbreakable-item":
                        if (value.isEmpty() || value.equalsIgnoreCase("true")) {
                            itemBuilder.setUnbreakable(true);
                        }
                        break;
                    default:
                        Enchantment enchantment = getEnchantment(option);
                        if (enchantment != null && enchantment.getName() != null) {
                            try {
                                itemBuilder.addEnchantments(enchantment, Integer.parseInt(value));
                            } catch (NumberFormatException e) {
                                itemBuilder.addEnchantments(enchantment, 1);
                            }
                            break;
                        }
                        for (ItemFlag itemFlag : ItemFlag.values()) {
                            if (itemFlag.name().equalsIgnoreCase(option)) {
                                itemBuilder.addItemFlag(itemFlag);
                                break;
                            }
                        }
                        try {
                            for (PatternType pattern : PatternType.values()) {
                                if (option.equalsIgnoreCase(pattern.name()) || value.equalsIgnoreCase(pattern.getIdentifier())) {
                                    DyeColor color = getDyeColor(value);
                                    if (color != null) {
                                        itemBuilder.addPattern(new Pattern(color, pattern));
                                    }
                                    break;
                                }
                            }
                        } catch (Exception ignored) {
                        }
                        break;
                }
            }
        } catch (Exception e) {
            itemBuilder.setMaterial("RED_TERRACOTTA", "STAINED_CLAY:14").setName("&c&lERROR").setLore(Arrays.asList("&cThere is an error", "&cFor : &c" + (placeHolder != null ? placeHolder : "")));
            e.printStackTrace();
        }
        return itemBuilder;
    }
    
    public static List<ItemBuilder> convertStringList(List<String> itemStrings) {
        return convertStringList(itemStrings, null);
    }
    
    public static List<ItemBuilder> convertStringList(List<String> itemStrings, String placeholder) {
        return itemStrings.stream().map(itemString -> convertString(itemString, placeholder)).collect(Collectors.toList());
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
        this.isHead = material == (useNewMaterial ? Material.matchMaterial("PLAYER_HEAD") : Material.matchMaterial("SKULL_ITEM"));
        return this;
    }
    
    /**
     * Set the type of item and its metadata in the builder.
     * @param materialString The string must be in this form: %Material% or %Material%:%MetaData%
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder setMaterial(String materialString) {
        String metaDataString = "";
        if (materialString.contains(":")) {// Sets the durability or another value option.
            String[] split = materialString.split(":");
            materialString = split[0];
            metaDataString = split[1];
            if (metaDataString.contains("#")) {// <ID>:<Durability>#<CustomModelData>
                String modelData = metaDataString.split("#")[1];
                if (Methods.isInt(modelData)) {//Value is a number.
                    this.useCustomModelData = true;
                    this.customModelData = Integer.parseInt(modelData);
                }
            }
            metaDataString = metaDataString.replace("#" + customModelData, "");
            if (Methods.isInt(metaDataString)) {//Value is durability.
                this.damage = Integer.parseInt(metaDataString);
            } else {//Value is something else.
                for (String option : split) {
                    if (potionType == null) {
                        this.potionType = getPotionType(PotionEffectType.getByName(option));
                    }
                    if (potionColor == null) {
                        this.potionColor = getColor(option);
                    }
                    if (armorColor == null) {
                        this.armorColor = getColor(option);
                    }
                }
            }
        } else if (materialString.contains("#")) {
            String[] b = materialString.split("#");
            materialString = b[0];
            if (Methods.isInt(b[1])) {//Value is a number.
                this.useCustomModelData = true;
                this.customModelData = Integer.parseInt(b[1]);
            }
        }
        Material material = Material.matchMaterial(materialString);
        if (material != null) {// Sets the material.
            this.material = material;
            //1.9-1.12.2
            if (Version.isNewer(Version.v1_8_R3) && Version.isOlder(Version.v1_13_R2)) {
                if (material == Material.matchMaterial("MONSTER_EGG")) {
                    try {
                        this.entityType = EntityType.fromId(damage) != null ? EntityType.fromId(damage) : EntityType.valueOf(metaDataString);
                    } catch (Exception ignore) {
                    }
                    this.damage = 0;
                    this.isMobEgg = true;
                }
            }
        }
        switch (this.material.name()) {
            case "PLAYER_HEAD":
            case "SKULL_ITEM":
                this.isHead = true;
                break;
            case "TIPPED_ARROW":
                this.isTippedArrow = true;
                break;
            case "POTION":
            case "SPLASH_POTION":
                this.isPotion = true;
                break;
            case "LEATHER_HELMET":
            case "LEATHER_CHESTPLATE":
            case "LEATHER_LEGGINGS":
            case "LEATHER_BOOTS":
                this.isLeatherArmor = true;
                break;
            case "BANNER":
                this.isBanner = true;
                break;
            case "SHIELD":
                this.isShield = true;
                break;
        }
        //1.13+ added different banner names and so this is quicker then listing every banner color.
        if (this.material.name().contains("BANNER")) {
            this.isBanner = true;
        }
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
        if (name != null) {
            this.name = color(name);
        }
        return this;
    }
    
    /**
     * Set the placeholders for the name of the item.
     * @param placeholders The placeholders that will be used.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder setNamePlaceholders(Map<String, String> placeholders) {
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
        for (Entry<String, String> placeholder : namePlaceholders.entrySet()) {
            newName = newName.replace(placeholder.getKey(), placeholder.getValue()).replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
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
        if (lore != null) {
            this.lore.clear();
            for (String i : lore) {
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
        if (lore != null) {
            this.lore.add(color(lore));
        }
        return this;
    }
    
    /**
     * Set the placeholders that are in the lore of the item.
     * @param placeholders The placeholders that you wish to use.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder setLorePlaceholders(Map<String, String> placeholders) {
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
        for (String i : lore) {
            for (Entry<String, String> placeholder : lorePlaceholders.entrySet()) {
                i = i.replace(placeholder.getKey(), placeholder.getValue()).replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
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
    
    public boolean isTippedArrow() {
        return isTippedArrow;
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
    
    public Color getPotionColor() {
        return potionColor;
    }
    
    public void setPotionColor(Color potionColor) {
        this.potionColor = potionColor;
    }
    
    public boolean isPotion() {
        return isPotion;
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
    
    public boolean isLeatherArmor() {
        return isLeatherArmor;
    }
    
    public List<Pattern> getPatterns() {
        return patterns;
    }
    
    public ItemBuilder addPattern(String stringPattern) {
        try {
            String[] split = stringPattern.split(":");
            for (PatternType pattern : PatternType.values()) {
                if (split[0].equalsIgnoreCase(pattern.name()) || split[0].equalsIgnoreCase(pattern.getIdentifier())) {
                    DyeColor color = getDyeColor(split[1]);
                    if (color != null) {
                        addPattern(new Pattern(color, pattern));
                    }
                    break;
                }
            }
        } catch (Exception ignored) {
        }
        return this;
    }
    
    public ItemBuilder addPatterns(List<String> stringList) {
        stringList.forEach(this :: addPattern);
        return this;
    }
    
    public ItemBuilder addPattern(Pattern pattern) {
        patterns.add(pattern);
        return this;
    }
    
    public ItemBuilder setPatterns(List<Pattern> patterns) {
        this.patterns = patterns;
        return this;
    }
    
    public boolean isBanner() {
        return isBanner;
    }
    
    public boolean isShield() {
        return isShield;
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
     * Get the amount of the item stack in the builder.
     * @param amount The amount that is in the item stack.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder addAmount(int amount) {
        this.amount += amount;
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
        if (player != null && player.length() > 16) {
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
    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }
    
    /**
     * Set a list of enchantments that will go onto the item in the builder. These can have unsafe levels.
     * It will also override any enchantments used in the "ItemBuilder#addEnchantment()" method.
     * @param enchantments A list of enchantments that will go onto the item in the builder.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder setEnchantments(Map<Enchantment, Integer> enchantments) {
        if (enchantments != null) {
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
    
    public List<ItemFlag> getItemFlags() {
        return itemFlags;
    }
    
    public ItemBuilder addItemFlag(String flagString) {
        try {
            addItemFlag(ItemFlag.valueOf(flagString.toUpperCase()));
        } catch (Exception ignored) {
        }
        return this;
    }
    
    public ItemBuilder addItemFlags(List<String> flagStrings) {
        for (String flagString : flagStrings) {
            try {
                ItemFlag itemFlag = ItemFlag.valueOf(flagString.toUpperCase());
                if (itemFlag != null) {
                    addItemFlag(itemFlag);
                }
            } catch (Exception ignored) {
            }
        }
        return this;
    }
    
    public ItemBuilder addItemFlag(ItemFlag itemFlag) {
        if (itemFlag != null) {
            itemFlags.add(itemFlag);
        }
        return this;
    }
    
    public ItemBuilder setItemFlags(List<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }
    
    /**
     * Builder the item from all the information that was given to the builder.
     * @return The result of all the info that was given to the builder as an ItemStack.
     */
    public ItemStack build() {
        ItemStack item = referenceItem != null ? referenceItem : new ItemStack(material);
        item.setAmount(amount);
        if (item.getType() != Material.AIR) {
            //Has to go 1st due to it removing all data when finished.
            //Sauce: https://github.com/deanveloper/SkullCreator
            if (isHead && isHash) {
                if (isURL) {
                    SkullCreator.itemWithUrl(item, player);
                } else {
                    SkullCreator.itemWithBase64(item, player);
                }
            }
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(getUpdatedName());
            itemMeta.setLore(getUpdatedLore());
            if (Version.isSame(Version.v1_8_R3)) {
                if (isHead && !isHash && player != null && !player.equals("")) {
                    SkullMeta skullMeta = (SkullMeta) itemMeta;
                    skullMeta.setOwner(player);
                }
            }
            if (Version.isNewer(Version.v1_10_R1)) {
                itemMeta.setUnbreakable(unbreakable);
            }
            if (Version.isNewer(Version.v1_12_R1)) {
                if (itemMeta instanceof Damageable) {
                    ((Damageable) itemMeta).setDamage(damage);
                }
            } else {
                item.setDurability((short) damage);
            }
            if ((isTippedArrow || isPotion) && (potionType != null || potionColor != null)) {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                if (potionType != null) {
                    potionMeta.setBasePotionData(new PotionData(potionType));
                }
                if (potionColor != null) {
                    potionMeta.setColor(potionColor);
                }
            }
            if (isLeatherArmor && armorColor != null) {
                LeatherArmorMeta leatherMeta = (LeatherArmorMeta) itemMeta;
                leatherMeta.setColor(armorColor);
            }
            if (isBanner && !patterns.isEmpty()) {
                BannerMeta bannerMeta = (BannerMeta) itemMeta;
                bannerMeta.setPatterns(patterns);
            }
            if (isShield && !patterns.isEmpty()) {
                BlockStateMeta shieldMeta = (BlockStateMeta) itemMeta;
                Banner banner = (Banner) shieldMeta.getBlockState();
                banner.setPatterns(patterns);
                banner.update();
                shieldMeta.setBlockState(banner);
            }
            if (useCustomModelData) {
                itemMeta.setCustomModelData(customModelData);
            }
            itemFlags.forEach(itemMeta :: addItemFlags);
            item.setItemMeta(itemMeta);
            hideFlags(item);
            item.addUnsafeEnchantments(enchantments);
            addGlow(item);
            NBTItem nbt = new NBTItem(item);
            if (isHead && !isHash && player != null && !player.equals("") && Version.isNewer(Version.v1_8_R3)) {
                nbt.setString("SkullOwner", player);
            }
            if (isMobEgg && entityType != null) {
                nbt.addCompound("EntityTag").setString("id", "minecraft:" + entityType.name());
            }
            if (unbreakable && Version.isOlder(Version.v1_11_R1)) {
                nbt.setBoolean("Unbreakable", true);
                nbt.setInteger("HideFlags", 4);
            }
            return nbt.getItem();
        } else {
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
        } catch (CloneNotSupportedException e) {
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
    
    private final java.util.regex.Pattern HEX_PATTERN = java.util.regex.Pattern.compile("#[a-fA-F0-9]{6}");
    
    public String color(String message) {
        if (Version.isNewer(Version.v1_15_R1)) {
            Matcher matcher = HEX_PATTERN.matcher(message);
            StringBuffer buffer = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group()).toString());
            }
            return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    private ItemStack hideFlags(ItemStack item) {
        if (hideItemFlags && item != null && item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.values());
            item.setItemMeta(itemMeta);
            return item;
        }
        return item;
    }
    
    private ItemStack addGlow(ItemStack item) {
        if (glowing) {
            try {
                if (item != null) {
                    if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
                        return item;
                    }
                    item.addUnsafeEnchantment(Enchantment.LUCK, 1);
                    ItemMeta meta = item.getItemMeta();
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    item.setItemMeta(meta);
                }
                return item;
            } catch (NoClassDefFoundError e) {
                return item;
            }
        }
        return item;
    }
    
    private PotionType getPotionType(PotionEffectType type) {
        if (type != null) {
            if (type.equals(PotionEffectType.FIRE_RESISTANCE)) {
                return PotionType.FIRE_RESISTANCE;
            } else if (type.equals(PotionEffectType.HARM)) {
                return PotionType.INSTANT_DAMAGE;
            } else if (type.equals(PotionEffectType.HEAL)) {
                return PotionType.INSTANT_HEAL;
            } else if (type.equals(PotionEffectType.INVISIBILITY)) {
                return PotionType.INVISIBILITY;
            } else if (type.equals(PotionEffectType.JUMP)) {
                return PotionType.JUMP;
            } else if (type.equals(PotionEffectType.getByName("LUCK"))) {
                return PotionType.valueOf("LUCK");
            } else if (type.equals(PotionEffectType.NIGHT_VISION)) {
                return PotionType.NIGHT_VISION;
            } else if (type.equals(PotionEffectType.POISON)) {
                return PotionType.POISON;
            } else if (type.equals(PotionEffectType.REGENERATION)) {
                return PotionType.REGEN;
            } else if (type.equals(PotionEffectType.SLOW)) {
                return PotionType.SLOWNESS;
            } else if (type.equals(PotionEffectType.SPEED)) {
                return PotionType.SPEED;
            } else if (type.equals(PotionEffectType.INCREASE_DAMAGE)) {
                return PotionType.STRENGTH;
            } else if (type.equals(PotionEffectType.WATER_BREATHING)) {
                return PotionType.WATER_BREATHING;
            } else if (type.equals(PotionEffectType.WEAKNESS)) {
                return PotionType.WEAKNESS;
            }
        }
        return null;
    }
    
    private static Color getColor(String color) {
        if (color != null) {
            switch (color.toUpperCase()) {
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
            } catch (Exception ignore) {
            }
        }
        return null;
    }
    
    private static DyeColor getDyeColor(String color) {
        if (color != null) {
            try {
                return DyeColor.valueOf(color.toUpperCase());
            } catch (Exception e) {
                try {
                    String[] rgb = color.split(",");
                    return DyeColor.getByColor(Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));
                } catch (Exception ignore) {
                }
            }
        }
        return null;
    }
    
    private static Enchantment getEnchantment(String enchantmentName) {
        enchantmentName = stripEnchantmentName(enchantmentName);
        for (Enchantment enchantment : Enchantment.values()) {
            try {
                //MC 1.13+ has the correct names.
                if (Version.isNewer(Version.v1_12_R1)) {
                    if (stripEnchantmentName(enchantment.getKey().getKey()).equalsIgnoreCase(enchantmentName)) {
                        return enchantment;
                    }
                }
                HashMap<String, String> enchantments = getEnchantmentList();
                if (stripEnchantmentName(enchantment.getName()).equalsIgnoreCase(enchantmentName) || (enchantments.get(enchantment.getName()) != null &&
                stripEnchantmentName(enchantments.get(enchantment.getName())).equalsIgnoreCase(enchantmentName))) {
                    return enchantment;
                }
            } catch (Exception ignore) {//If any null enchantments are found they may cause errors.
            }
        }
        return null;
    }
    
    private static String stripEnchantmentName(String enchantmentName) {
        return enchantmentName != null ? enchantmentName.replace("-", "").replace("_", "").replace(" ", "") : null;
    }
    
    private static HashMap<String, String> getEnchantmentList() {
        HashMap<String, String> enchantments = new HashMap<>();
        enchantments.put("ARROW_DAMAGE", "Power");
        enchantments.put("ARROW_FIRE", "Flame");
        enchantments.put("ARROW_INFINITE", "Infinity");
        enchantments.put("ARROW_KNOCKBACK", "Punch");
        enchantments.put("DAMAGE_ALL", "Sharpness");
        enchantments.put("DAMAGE_ARTHROPODS", "Bane_Of_Arthropods");
        enchantments.put("DAMAGE_UNDEAD", "Smite");
        enchantments.put("DEPTH_STRIDER", "Depth_Strider");
        enchantments.put("DIG_SPEED", "Efficiency");
        enchantments.put("DURABILITY", "Unbreaking");
        enchantments.put("FIRE_ASPECT", "Fire_Aspect");
        enchantments.put("KNOCKBACK", "KnockBack");
        enchantments.put("LOOT_BONUS_BLOCKS", "Fortune");
        enchantments.put("LOOT_BONUS_MOBS", "Looting");
        enchantments.put("LUCK", "Luck_Of_The_Sea");
        enchantments.put("LURE", "Lure");
        enchantments.put("OXYGEN", "Respiration");
        enchantments.put("PROTECTION_ENVIRONMENTAL", "Protection");
        enchantments.put("PROTECTION_EXPLOSIONS", "Blast_Protection");
        enchantments.put("PROTECTION_FALL", "Feather_Falling");
        enchantments.put("PROTECTION_FIRE", "Fire_Protection");
        enchantments.put("PROTECTION_PROJECTILE", "Projectile_Protection");
        enchantments.put("SILK_TOUCH", "Silk_Touch");
        enchantments.put("THORNS", "Thorns");
        enchantments.put("WATER_WORKER", "Aqua_Affinity");
        enchantments.put("BINDING_CURSE", "Curse_Of_Binding");
        enchantments.put("MENDING", "Mending");
        enchantments.put("FROST_WALKER", "Frost_Walker");
        enchantments.put("VANISHING_CURSE", "Curse_Of_Vanishing");
        enchantments.put("SWEEPING_EDGE", "Sweeping_Edge");
        enchantments.put("RIPTIDE", "Riptide");
        enchantments.put("CHANNELING", "Channeling");
        enchantments.put("IMPALING", "Impaling");
        enchantments.put("LOYALTY", "Loyalty");
        return enchantments;
    }
    
}