package com.badbones69.crazyenchantments.api.objects;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.SkullCreator;
import de.tr7zw.changeme.nbtapi.NBTItem;
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
import java.util.stream.Collectors;

public class ItemBuilder {

    private NBTItem nbtItem;

    // Item Data
    private Material material;
    private int damage;
    private String itemName;
    private final List<String> itemLore;
    private int itemAmount;

    // Player
    private String player;

    // Skulls
    private boolean isHash;
    private boolean isURL;
    private boolean isHead;

    // Enchantments/Flags
    private boolean unbreakable;
    private boolean hideItemFlags;
    private boolean glowing;

    // Entities
    private final boolean isMobEgg;
    private EntityType entityType;

    // Potions
    private PotionType potionType;
    private Color potionColor;
    private boolean isPotion;

    // Armor
    private Color armorColor;
    private boolean isLeatherArmor;

    // Enchantments
    private HashMap<Enchantment, Integer> enchantments;

    // Shields
    private boolean isShield;

    // Banners
    private boolean isBanner;
    private List<Pattern> patterns;

    // Placeholders
    private HashMap<String, String> namePlaceholders;
    private HashMap<String, String> lorePlaceholders;

    // Misc
    private ItemStack referenceItem;
    private List<ItemFlag> itemFlags;

    // Custom Data
    private int customModelData;
    private boolean useCustomModelData;

    /**
     * Create a blank item builder.
     */
    public ItemBuilder() {
        this.nbtItem = null;
        this.material = Material.STONE;
        this.damage = 0;
        this.itemName = "";
        this.itemLore = new ArrayList<>();
        this.itemAmount = 1;
        this.player = "";

        this.isHash = false;
        this.isURL = false;
        this.isHead = false;

        this.unbreakable = false;
        this.hideItemFlags = false;
        this.glowing = false;

        this.isMobEgg = false;
        this.entityType = EntityType.BAT;

        this.potionType = null;
        this.potionColor = null;
        this.isPotion = false;

        this.armorColor = null;
        this.isLeatherArmor = false;

        this.enchantments = new HashMap<>();

        this.isShield = false;

        this.isBanner = false;
        this.patterns = new ArrayList<>();

        this.namePlaceholders = new HashMap<>();
        this.lorePlaceholders = new HashMap<>();

        this.itemFlags = new ArrayList<>();
    }

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final SkullCreator skullCreator = starter.getSkullCreator();

    /**
     * Deduplicate an item builder.
     *
     * @param itemBuilder The item builder to deduplicate.
     */
    public ItemBuilder(ItemBuilder itemBuilder) {
        this.nbtItem = itemBuilder.nbtItem;
        this.material = itemBuilder.material;
        this.damage = itemBuilder.damage;
        this.itemName = itemBuilder.itemName;
        this.itemLore = new ArrayList<>(itemBuilder.itemLore);
        this.itemAmount = itemBuilder.itemAmount;
        this.player = itemBuilder.player;

        this.referenceItem = itemBuilder.referenceItem;
        this.customModelData = itemBuilder.customModelData;
        this.useCustomModelData = itemBuilder.useCustomModelData;

        this.enchantments = new HashMap<>(itemBuilder.enchantments);

        this.isHash = itemBuilder.isHash;
        this.isURL = itemBuilder.isURL;
        this.isHead = itemBuilder.isHead;

        this.unbreakable = itemBuilder.unbreakable;
        this.hideItemFlags = itemBuilder.hideItemFlags;
        this.glowing = itemBuilder.glowing;

        this.isMobEgg = itemBuilder.isMobEgg;
        this.entityType = itemBuilder.entityType;

        this.potionType = itemBuilder.potionType;
        this.potionColor = itemBuilder.potionColor;
        this.isPotion = itemBuilder.isPotion;

        this.armorColor = itemBuilder.armorColor;
        this.isLeatherArmor = itemBuilder.isLeatherArmor;

        this.isShield = itemBuilder.isShield;

        this.isBanner = itemBuilder.isBanner;
        this.patterns = new ArrayList<>(itemBuilder.patterns);

        this.namePlaceholders = new HashMap<>(itemBuilder.namePlaceholders);
        this.lorePlaceholders = new HashMap<>(itemBuilder.lorePlaceholders);
        this.itemFlags = new ArrayList<>(itemBuilder.itemFlags);
    }

    /**
     * Gets the nbt item.
     */
    public NBTItem getNBTItem() {
        nbtItem = new NBTItem(build());

        return nbtItem;
    }

    /**
     * Gets the material.
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Checks if the item is a banner.
     */
    public boolean isBanner() {
        return isBanner;
    }

    /**
     * Checks if an item is a shield.
     */
    public boolean isShield() {
        return isShield;
    }

    /**
     * Checks if the item is a spawn mob egg.
     */
    public boolean isMobEgg() {
        return isMobEgg;
    }

    /**
     * Returns the player name.
     */
    public String getPlayerName() {
        return player;
    }

    /**
     * Get the entity type of the spawn mob egg.
     */
    public EntityType getEntityType() {
        return entityType;
    }

    /**
     * Get the name of the item.
     */
    public String getName() {
        return itemName;
    }

    /**
     * Get the lore on the item.
     */
    public List<String> getLore() {
        return itemLore;
    }

    /**
     * Returns the enchantments on the Item.
     */
    public HashMap<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    /**
     * Return a list of Item Flags.
     */
    public List<ItemFlag> getItemFlags() {
        return itemFlags;
    }

    /**
     * Checks if flags are hidden.
     */
    public boolean isItemFlagsHidden() {
        return hideItemFlags;
    }

    /**
     * Check if item is Leather Armor
     */
    public boolean isLeatherArmor() {
        return isLeatherArmor;
    }

    /**
     * Checks if item is glowing.
     */
    public boolean isGlowing() {
        return glowing;
    }

    /**
     * Checks if the item is unbreakable.
     */
    public boolean isUnbreakable() {
        return unbreakable;
    }

    /**
     * Get the patterns on the banners.
     */
    public List<Pattern> getPatterns() {
        return patterns;
    }

    /**
     * Get the item's name with all the placeholders added to it.
     *
     * @return The name with all the placeholders in it.
     */
    public String getUpdatedName() {
        String newName = itemName;

        for (String placeholder : namePlaceholders.keySet()) {
            newName = newName.replace(placeholder, namePlaceholders.get(placeholder)).replace(placeholder.toLowerCase(), namePlaceholders.get(placeholder));
        }

        return newName;
    }

    /**
     * Builder the item from all the information that was given to the builder.
     *
     * @return The result of all the info that was given to the builder as an ItemStack.
     */
    public ItemStack build() {

        if (nbtItem != null) referenceItem = nbtItem.getItem();

        ItemStack item = referenceItem != null ? referenceItem : new ItemStack(material);

        if (item.getType() != Material.AIR) {

            if (isHead) { // Has to go 1st due to it removing all data when finished.
                if (isHash) { // Sauce: https://github.com/deanveloper/SkullCreator
                    if (isURL) {
                        skullCreator.itemWithUrl(item, player);
                    } else {
                        skullCreator.itemWithBase64(item, player);
                    }
                }
            }

            item.setAmount(itemAmount);
            ItemMeta itemMeta = item.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(getUpdatedName());
            itemMeta.setLore(getUpdatedLore());

            if (itemMeta instanceof org.bukkit.inventory.meta.Damageable) ((org.bukkit.inventory.meta.Damageable) itemMeta).setDamage(damage);

            if (isPotion && (potionType != null || potionColor != null)) {
                PotionMeta potionMeta = (PotionMeta) itemMeta;

                if (potionType != null) potionMeta.setBasePotionData(new PotionData(potionType));

                if (potionColor != null) potionMeta.setColor(potionColor);
            }

            if (material == Material.TIPPED_ARROW && potionType != null) {
                PotionMeta potionMeta = (PotionMeta) itemMeta;
                potionMeta.setBasePotionData(new PotionData(potionType));
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

            if (useCustomModelData) itemMeta.setCustomModelData(customModelData);

            itemFlags.forEach(itemMeta :: addItemFlags);
            item.setItemMeta(itemMeta);
            hideItemFlags(item);
            item.addUnsafeEnchantments(enchantments);
            addGlow(item);
            NBTItem nbt = new NBTItem(item);

            if (isHead && !isHash) nbt.setString("SkullOwner", player);

            if (isMobEgg) {
                if (entityType != null) nbt.addCompound("EntityTag").setString("id", "minecraft:" + entityType.name());
            }

            return nbt.getItem();
        } else {
            return item;
        }
    }

    /*
      Class based extensions.
     */

    /**
     * Get a clone of the object.
     * @return a new cloned object.
     */
    public ItemBuilder copy() {
        try {
            return (ItemBuilder) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return new ItemBuilder();
    }

    /**
     * Set the type of item the builder is set to.
     *
     * @param material The material you wish to set.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder setMaterial(Material material) {
        this.material = material;
        this.isHead = material == Material.PLAYER_HEAD;

        return this;
    }

    /**
     * Set the type of item and its metadata in the builder.
     *
     * @param material The string must be in this form: %Material% or %Material%:%MetaData%
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder setMaterial(String material) {
        String metaData;

        if (material.contains(":")) { // Sets the durability or another value option.
            String[] b = material.split(":");
            material = b[0];
            metaData = b[1];

            if (metaData.contains("#")) { // <ID>:<Durability>#<CustomModelData>
                String modelData = metaData.split("#")[1];
                if (isInt(modelData)) { // Value is a number.
                    this.useCustomModelData = true;
                    this.customModelData = Integer.parseInt(modelData);
                }
            }

            metaData = metaData.replace("#" + customModelData, "");

            if (isInt(metaData)) { // Value is durability.
                this.damage = Integer.parseInt(metaData);
            } else { // Value is something else.
                this.potionType = getPotionType(PotionEffectType.getByName(metaData));
                this.potionColor = methods.getColor(metaData);
                this.armorColor = methods.getColor(metaData);
            }

        } else if (material.contains("#")) {
            String[] materialSplit = material.split("#");
            material = materialSplit[0];

            if (isInt(materialSplit[1])) { // Value is a number.
                this.useCustomModelData = true;
                this.customModelData = Integer.parseInt(materialSplit[1]);
            }
        }

        Material matchedMaterial = Material.matchMaterial(material);

        if (matchedMaterial != null) this.material = matchedMaterial;

        switch (this.material.name()) {
            case "PLAYER_HEAD" -> this.isHead = true;
            case "POTION", "SPLASH_POTION" -> this.isPotion = true;
            case "LEATHER_HELMET", "LEATHER_CHESTPLATE", "LEATHER_LEGGINGS", "LEATHER_BOOTS", "LEATHER_HORSE_ARMOR" -> this.isLeatherArmor = true;
            case "BANNER" -> this.isBanner = true;
            case "SHIELD" -> this.isShield = true;
        }

        if (this.material.name().contains("BANNER")) this.isBanner = true;

        return this;
    }

    /**
     * @param damage The damage value of the item.
     * @return The ItemBuilder with an updated damage value.
     */
    public ItemBuilder setDamage(int damage) {
        this.damage = damage;

        return this;
    }

    /**
     * Get the damage to the item.
     *
     * @return The damage to the item as an int.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @param itemName The name of the item.
     * @return The ItemBuilder with an updated name.
     */
    public ItemBuilder setName(String itemName) {
        if (itemName != null) this.itemName = methods.color(itemName);

        return this;
    }

    /**
     * @param placeholders The placeholders that will be used.
     * @return The ItemBuilder with updated placeholders.
     */
    public ItemBuilder setNamePlaceholders(HashMap<String, String> placeholders) {
        this.namePlaceholders = placeholders;

        return this;
    }

    /**
     * Add a placeholder to the name of the item.
     *
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
     *
     * @param placeholder The placeholder you wish to remove.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder removeNamePlaceholder(String placeholder) {
        this.namePlaceholders.remove(placeholder);

        return this;
    }

    /**
     * Set the lore of the item in the builder. This will auto force color in all the lores that contains color code. (&a, &c, &7, etc...)
     *
     * @param lore The lore of the item in the builder.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder setLore(List<String> lore) {
        if (lore != null) {
            this.itemLore.clear();

            lore.forEach(line -> this.itemLore.add(methods.color(line)));
        }

        return this;
    }

    /**
     * Add a line to the current lore of the item. This will auto force color in the lore that contains color code. (&a, &c, &7, etc...)
     *
     * @param lore The new line you wish to add.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder addLore(String lore) {
        if (lore != null) this.itemLore.add(methods.color(lore));

        return this;
    }

    /**
     * Set the placeholders that are in the lore of the item.
     *
     * @param placeholders The placeholders that you wish to use.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder setLorePlaceholders(HashMap<String, String> placeholders) {
        this.lorePlaceholders = placeholders;

        return this;
    }

    /**
     * Add a placeholder to the lore of the item.
     *
     * @param placeholder The placeholder you wish to replace.
     * @param argument The argument that will replace the placeholder.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder addLorePlaceholder(String placeholder, String argument) {
        this.lorePlaceholders.put(placeholder, argument);

        return this;
    }

    /**
     * Get the lore with all the placeholders added to it.
     *
     * @return The lore with all placeholders in it.
     */
    public List<String> getUpdatedLore() {
        List<String> newLore = new ArrayList<>();

        for (String item : itemLore) {
            for (String placeholder : lorePlaceholders.keySet()) {
                item = item.replace(placeholder, lorePlaceholders.get(placeholder)).replace(placeholder.toLowerCase(), lorePlaceholders.get(placeholder));
            }

            newLore.add(item);
        }

        return newLore;
    }

    /**
     * Remove a placeholder from the lore.
     *
     * @param placeholder The placeholder you wish to remove.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder removeLorePlaceholder(String placeholder) {
        this.lorePlaceholders.remove(placeholder);

        return this;
    }

    /**
     * @param entityType The entity type the mob spawn egg will be.
     * @return The ItemBuilder with an updated mob spawn egg.
     */
    public ItemBuilder setEntityType(EntityType entityType) {
        this.entityType = entityType;

        return this;
    }

    /**
     * Add patterns to the item.
     *
     * @param stringPattern The pattern you wish to add.
     */
    private void addPatterns(String stringPattern) {
        try {
            String[] split = stringPattern.split(":");

            for (PatternType pattern : PatternType.values()) {

                if (split[0].equalsIgnoreCase(pattern.name()) || split[0].equalsIgnoreCase(pattern.getIdentifier())) {
                    DyeColor color = getDyeColor(split[1]);

                    if (color != null) addPattern(new Pattern(color, pattern));

                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    /**
     * @param patterns The list of Patterns to add.
     * @return The ItemBuilder with updated patterns.
     */
    public ItemBuilder addPatterns(List<String> patterns) {
        patterns.forEach(this :: addPatterns);

        return this;
    }

    /**
     * @param pattern A pattern to add.
     * @return The ItemBuilder with an updated pattern.
     */
    public ItemBuilder addPattern(Pattern pattern) {
        patterns.add(pattern);

        return this;
    }

    /**
     * @param patterns Set a list of Patterns.
     * @return The ItemBuilder with an updated list of patterns.
     */
    public ItemBuilder setPattern(List<Pattern> patterns) {
        this.patterns = patterns;

        return this;
    }

    /**
     * The amount of the item stack in the builder.
     * @return The amount that is set in the builder.
     */
    public int getAmount() {
        return itemAmount;
    }

    /**
     * @param amount The amount of the item stack.
     * @return The ItemBuilder with an updated item count.
     */
    public ItemBuilder setAmount(Integer amount) {
        this.itemAmount = amount;

        return this;
    }

    /**
     * Get the amount of the item stack in the builder.
     * @param amount The amount that is in the item stack.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder addAmount(int amount) {
        this.itemAmount += amount;

        return this;
    }

    /**
     * Set the player that will be displayed on the head.
     *
     * @param playerName The player being displayed on the head.
     * @return The ItemBuilder with an updated Player Name.
     */
    public ItemBuilder setPlayerName(String playerName) {
        this.player = playerName;

        if (player != null && player.length() > 16) {
            this.isHash = true;
            this.isURL = player.startsWith("http");
        }

        return this;
    }

    /**
     * It will override any enchantments used in ItemBuilder.addEnchantment() below.
     *
     * @param enchantment A list of enchantments to add to the item.
     * @return The ItemBuilder with a list of updated enchantments.
     */
    public ItemBuilder setEnchantments(HashMap<Enchantment, Integer> enchantment) {
        if (enchantment != null) this.enchantments = enchantment;

        return this;
    }

    /**
     * Adds an enchantment to the item.
     *
     * @param enchantment The enchantment you wish to add.
     * @param level The level of the enchantment ( Unsafe levels included )
     * @return The ItemBuilder with updated enchantments.
     */
    public ItemBuilder addEnchantments(Enchantment enchantment, Integer level) {
        this.enchantments.put(enchantment, level);

        return this;
    }

    /**
     * Remove an enchantment from the item.
     *
     * @param enchantment The enchantment you wish to remove.
     * @return The ItemBuilder with updated enchantments.
     */
    public ItemBuilder removeEnchantments(Enchantment enchantment) {
        this.enchantments.remove(enchantment);

        return this;
    }

    /**
     * Set the flags that will be on the item in the builder.
     *
     * @param flagStrings The flag names as string you wish to add to the item in the builder.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder setFlagsFromStrings(List<String> flagStrings) {
        itemFlags.clear();

        for (String flagString : flagStrings) {
            ItemFlag flag = getFlag(flagString);

            if (flag != null) itemFlags.add(flag);
        }

        return this;
    }

    // Used for multiple Item Flags
    public ItemBuilder addItemFlags(List<String> flagStrings) {
        for (String flagString : flagStrings) {
            try {
                ItemFlag itemFlag = ItemFlag.valueOf(flagString.toUpperCase());

                if (itemFlag != null) addItemFlag(itemFlag);
            } catch (Exception ignored) {}
        }

        return this;
    }

    /**
     * Add a flag to the item in the builder.
     *
     * @param flagString The name of the flag you wish to add.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder addFlags(String flagString) {
        ItemFlag flag = getFlag(flagString);

        if (flag != null) itemFlags.add(flag);

        return this;
    }

    /**
     * Adds an ItemFlag to a map which is added to an item.
     *
     * @param itemFlag The flag to add.
     * @return The ItemBuilder with an updated ItemFlag.
     */
    public ItemBuilder addItemFlag(ItemFlag itemFlag) {
        if (itemFlag != null) itemFlags.add(itemFlag);

        return this;
    }

    /**
     * Adds multiple ItemFlags in a list to a map which get added to an item.
     *
     * @param itemFlags The list of flags to add.
     * @return The ItemBuilder with a list of ItemFlags.
     */
    public ItemBuilder setItemFlags(List<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;

        return this;
    }

    /**
     * @param hideItemFlags Hide item flags based on a boolean.
     * @return The ItemBuilder with an updated Boolean.
     */
    public ItemBuilder hideItemFlags(boolean hideItemFlags) {
        this.hideItemFlags = hideItemFlags;

        return this;
    }

    /**
     * @param item The item to hide flags on.
     * @return The ItemBuilder with an updated Item.
     */
    public ItemStack hideItemFlags(ItemStack item) {
        if (hideItemFlags) {
            if (item != null && item.hasItemMeta()) {
                ItemMeta itemMeta = item.getItemMeta();
                assert itemMeta != null;
                itemMeta.addItemFlags(ItemFlag.values());
                item.setItemMeta(itemMeta);
                return item;
            }
        }

        return item;
    }

    /**
     * Sets the converted item as a reference to try and save NBT tags and stuff.
     *
     * @param referenceItem The item that is being referenced.
     * @return The ItemBuilder with updated info.
     */
    private ItemBuilder setReferenceItem(ItemStack referenceItem) {
        this.referenceItem = referenceItem;

        return this;
    }

    /**
     * @param unbreakable Sets the item to be unbreakable.
     * @return The ItemBuilder with an updated Boolean.
     */
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;

        return this;
    }

    /**
     * @param glow Sets whether to make an item to glow or not.
     * @return The ItemBuilder with an updated Boolean.
     */
    public ItemBuilder setGlow(boolean glow) {
        this.glowing = glow;

        return this;
    }

    /**
     * The text that will be displayed on the item.
     *
     * @param texture The skull texture.
     * @param profileUUID The uuid of the profile.
     * @return The ItemBuilder.
     */
    public ItemBuilder texture(String texture, UUID profileUUID) {
        return this;
    }

    /**
     * @param texture The skull texture.
     * @return The ItemBuilder.
     */
    public ItemBuilder texture(String texture) {
        return this;
    }

    /**
     * @param texture The owner of the skull.
     * @return The ItemBuilder.
     */
    public ItemBuilder owner(String texture) {
        return this;
    }

    // Other misc shit

    /**
     * Convert an ItemStack to an ItemBuilder to allow easier editing of the ItemStack.
     *
     * @param item The ItemStack you wish to convert into an ItemBuilder.
     * @return The ItemStack as an ItemBuilder with all the info from the item.
     */
    public static ItemBuilder convertItemStack(ItemStack item) {
        ItemBuilder itemBuilder = new ItemBuilder().setReferenceItem(item).setAmount(item.getAmount()).setMaterial(item.getType()).setEnchantments(new HashMap<>(item.getEnchantments()));

        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();

            assert itemMeta != null;
            itemBuilder.setName(itemMeta.getDisplayName()).setLore(itemMeta.getLore());

            NBTItem nbt = new NBTItem(item);

            if (nbt.hasKey("Unbreakable")) itemBuilder.setUnbreakable(nbt.getBoolean("Unbreakable"));

            if (itemMeta instanceof org.bukkit.inventory.meta.Damageable) itemBuilder.setDamage(((org.bukkit.inventory.meta.Damageable) itemMeta).getDamage());
        }

        return itemBuilder;
    }

    /**
     * Converts a String to an ItemBuilder.
     *
     * @param itemString The String you wish to convert.
     * @return The String as an ItemBuilder.
     */
    public static ItemBuilder convertString(String itemString) {
        return convertString(itemString, null);
    }

    /**
     * Converts a string to an ItemBuilder with a placeholder for errors.
     *
     * @param itemString The String you wish to convert.
     * @param placeHolder The placeholder to use if there is an error.
     * @return The String as an ItemBuilder.
     */
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
                        itemBuilder.setPlayerName(value);
                        break;
                    case "unbreakable-item":

                        if (value.isEmpty() || value.equalsIgnoreCase("true")) itemBuilder.setUnbreakable(true);

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
                                    if (color != null) itemBuilder.addPattern(new Pattern(color, pattern));
                                    break;
                                }
                            }
                        } catch (Exception ignored) {}
                        break;
                }
            }
        } catch (Exception e) {
            itemBuilder.setMaterial(Material.RED_TERRACOTTA).setName("&c&lERROR").setLore(Arrays.asList("&cThere is an error", "&cFor : &c" + (placeHolder != null ? placeHolder : "")));
            e.printStackTrace();
        }

        return itemBuilder;
    }

    /**
     * Converts a list of Strings to a list of ItemBuilders.
     *
     * @param itemStrings The list of Strings.
     * @return The list of ItemBuilders.
     */
    public static List<ItemBuilder> convertStringList(List<String> itemStrings) {
        return convertStringList(itemStrings, null);
    }

    /**
     * Converts a list of Strings to a list of ItemBuilders with a placeholder for errors.
     *
     * @param itemStrings The list of Strings.
     * @param placeholder The placeholder for errors.
     * @return The list of ItemBuilders.
     */
    public static List<ItemBuilder> convertStringList(List<String> itemStrings, String placeholder) {
        return itemStrings.stream().map(itemString -> convertString(itemString, placeholder)).collect(Collectors.toList());
    }

    /**
     * Add glow to an item.
     *
     * @param item The item to add glow to.
     */
    private void addGlow(ItemStack item) {
        if (glowing) {
            try {
                if (item != null) {
                    if (item.getItemMeta() != null) {
                        if (item.getItemMeta().hasEnchants()) return;
                    }

                    item.addUnsafeEnchantment(Enchantment.LUCK, 1);
                    ItemMeta meta = item.getItemMeta();
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    item.setItemMeta(meta);
                }
            } catch (NoClassDefFoundError ignored) {}
        }
    }

    /**
     * Get the PotionEffect from a PotionEffectType.
     *
     * @param type The type of the potion effect.
     * @return The potion type.
     */
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

    /**
     * Get the dye color from a string.
     *
     * @param color The string of the color.
     * @return The dye color from the string.
     */
    public static DyeColor getDyeColor(String color) {
        if (color != null) {
            try {
                return DyeColor.valueOf(color.toUpperCase());
            } catch (Exception e) {
                try {
                    String[] rgb = color.split(",");
                    return DyeColor.getByColor(Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));
                } catch (Exception ignore) {}
            }
        }

        return null;
    }

    /**
     * Get the enchantment from a string.
     *
     * @param enchantmentName The string of the enchantment.
     * @return The enchantment from the string.
     */
    private static Enchantment getEnchantment(String enchantmentName) {
        enchantmentName = stripEnchantmentName(enchantmentName);
        for (Enchantment enchantment : Enchantment.values()) {
            try {
                if (stripEnchantmentName(enchantment.getKey().getKey()).equalsIgnoreCase(enchantmentName)) return enchantment;

                HashMap<String, String> enchantments = getEnchantmentList();

                if (stripEnchantmentName(enchantment.getName()).equalsIgnoreCase(enchantmentName) || (enchantments.get(enchantment.getName()) != null &&
                        stripEnchantmentName(enchantments.get(enchantment.getName())).equalsIgnoreCase(enchantmentName))) return enchantment;
            } catch (Exception ignore) {}
        }

        return null;
    }

    /**
     * Strip extra characters from an enchantment name.
     *
     * @param enchantmentName The enchantment name.
     * @return The stripped enchantment name.
     */
    private static String stripEnchantmentName(String enchantmentName) {
        return enchantmentName != null ? enchantmentName.replace("-", "").replace("_", "").replace(" ", "") : null;
    }

    /**
     * Get the list of enchantments and their in-Game names.
     *
     * @return The list of enchantments and their in-Game names.
     */
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

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    private ItemFlag getFlag(String flagString) {
        for (ItemFlag flag : ItemFlag.values()) {
            if (flag.name().equalsIgnoreCase(flagString)) return flag;
        }

        return null;
    }
}