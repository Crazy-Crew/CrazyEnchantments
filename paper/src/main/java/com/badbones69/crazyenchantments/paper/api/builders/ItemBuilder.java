package com.badbones69.crazyenchantments.paper.api.builders;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.Enchant;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.api.utils.NumberUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.support.SkullCreator;
import com.google.gson.Gson;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;
import org.jline.utils.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.Collectors;
import static com.badbones69.crazyenchantments.paper.api.utils.ColorUtils.getColor;

public class ItemBuilder {


    // Item Data
    private Material material;
    private TrimMaterial trimMaterial;
    private TrimPattern trimPattern;
    private int damage;
    private Component itemName;
    private final List<Component> itemLore;
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

    private final HashMap<CEnchantment, Integer> crazyEnchantments;

    // Shields
    private boolean isShield;

    // Banners
    private boolean isBanner;
    private List<Pattern> patterns;

    // Maps
    private boolean isMap;
    private Color mapColor;

    // Placeholders
    private HashMap<String, String> namePlaceholders;
    private HashMap<String, String> lorePlaceholders;

    // Misc
    private ItemStack referenceItem;
    private List<ItemFlag> itemFlags;

    // Custom Data
    private int customModelData;
    private boolean useCustomModelData;
    private Map<NamespacedKey, String> stringPDC;

    /**
     * Create a blank item builder.
     */
    public ItemBuilder() {
        this.material = Material.STONE;
        this.trimMaterial = null;
        this.trimPattern = null;
        this.damage = 0;
        this.itemName = null;
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

        this.crazyEnchantments = new HashMap<>();

        this.isShield = false;

        this.isBanner = false;
        this.patterns = new ArrayList<>();

        this.isMap = false;
        this.mapColor = Color.RED;

        this.namePlaceholders = new HashMap<>();
        this.lorePlaceholders = new HashMap<>();

        this.itemFlags = new ArrayList<>();

        this.stringPDC = null;

    }

    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Starter starter = plugin.getStarter();

    private final SkullCreator skullCreator = this.starter.getSkullCreator();

    /**
     * Deduplicate an item builder.
     *
     * @param itemBuilder The item builder to deduplicate.
     */
    public ItemBuilder(ItemBuilder itemBuilder) {
        this.material = itemBuilder.material;
        this.trimMaterial = itemBuilder.trimMaterial;
        this.trimPattern = itemBuilder.trimPattern;
        this.damage = itemBuilder.damage;
        this.itemName = itemBuilder.itemName;
        this.itemLore = new ArrayList<>(itemBuilder.itemLore);
        this.itemAmount = itemBuilder.itemAmount;
        this.player = itemBuilder.player;

        this.referenceItem = itemBuilder.referenceItem;
        this.customModelData = itemBuilder.customModelData;
        this.useCustomModelData = itemBuilder.useCustomModelData;

        this.enchantments = new HashMap<>(itemBuilder.enchantments);

        this.crazyEnchantments = new HashMap<>(itemBuilder.crazyEnchantments);

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

        this.isMap = itemBuilder.isMap;
        this.mapColor = itemBuilder.mapColor;

        this.namePlaceholders = new HashMap<>(itemBuilder.namePlaceholders);
        this.lorePlaceholders = new HashMap<>(itemBuilder.lorePlaceholders);
        this.itemFlags = new ArrayList<>(itemBuilder.itemFlags);
        this.stringPDC = itemBuilder.stringPDC;
    }

    /**
     * Gets the material.
     */
    public Material getMaterial() {
        return this.material;
    }

    /**
     * Checks if the item is a banner.
     */
    public boolean isBanner() {
        return this.isBanner;
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
        return this.isMobEgg;
    }

    /**
     * Returns the player name.
     */
    public String getPlayerName() {
        return this.player;
    }

    /**
     * Get the entity type of the spawn mob egg.
     */
    public EntityType getEntityType() {
        return this.entityType;
    }

    /**
     * Get the name of the item.
     */
    public String getName() {
        return this.itemName == null ? "" : ColorUtils.toLegacy(this.itemName);
    }

    /**
     * Get the lore on the item.
     */
    public List<Component> getLore() {
        return this.itemLore;
    }

    /**
     * Returns the enchantments on the Item.
     */
    public HashMap<Enchantment, Integer> getEnchantments() {
        return this.enchantments;
    }

    /**
     * Returns the crazyEnchantments on the item.
     */
    public HashMap<CEnchantment, Integer> getCrazyEnchantments() {
        return this.crazyEnchantments;
    }

    /**
     * Return a list of Item Flags.
     */
    public List<ItemFlag> getItemFlags() {
        return this.itemFlags;
    }

    /**
     * Checks if flags are hidden.
     */
    public boolean isItemFlagsHidden() {
        return this.hideItemFlags;
    }

    /**
     * Check if item is Leather Armor
     */
    public boolean isLeatherArmor() {
        return this.isLeatherArmor;
    }

    /**
     * Checks if item is glowing.
     */
    public boolean isGlowing() {
        return this.glowing;
    }

    /**
     * Checks if the item is unbreakable.
     */
    public boolean isUnbreakable() {
        return this.unbreakable;
    }

    /**
     * Get the patterns on the banners.
     */
    public List<Pattern> getPatterns() {
        return this.patterns;
    }

    /**
     * Get the item's name with all the placeholders added to it.
     *
     * @return The name with all the placeholders in it.
     */
    public String getUpdatedName() {
        if (this.itemName == null) return "";
        String newName = ColorUtils.toLegacy(this.itemName);

        for (Map.Entry<String, String> placeholder : this.namePlaceholders.entrySet()) {
            newName = newName.replace(placeholder.getKey(), placeholder.getValue()).replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
        }

        return newName;
    }

    /**
     * Builder the item from all the information that was given to the builder.
     *
     * @return The result of all the info that was given to the builder as an ItemStack.
     */
    public ItemStack build() {

        ItemStack item = this.referenceItem != null ? this.referenceItem : new ItemStack(this.material);

        if (item.isEmpty()) return item;

        if (this.isHead) { // Has to go 1st due to it removing all data when finished.
            if (this.isHash) { // Sauce: https://github.com/deanveloper/SkullCreator
                if (this.isURL) {
                    this.skullCreator.itemWithUrl(item, this.player);
                } else {
                    this.skullCreator.itemWithBase64(item, this.player);
                }
            }

            item.setAmount(this.itemAmount);
            SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
            List<Component> newLore = getUpdatedLore();
            assert itemMeta != null;
            if (!getUpdatedName().isEmpty()) itemMeta.displayName(ColorUtils.legacyTranslateColourCodes(getUpdatedName()));
            if (!newLore.isEmpty()) itemMeta.lore(newLore);
            if (this.stringPDC != null) {
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                stringPDC.forEach((nameSpace, data) -> container.set(nameSpace, PersistentDataType.STRING, data));
            }

            if (this.useCustomModelData) itemMeta.setCustomModelData(this.customModelData);

            this.itemFlags.forEach(itemMeta :: addItemFlags);
            itemMeta.setOwner(this.player); //TODO Replace with better method.
            item.setItemMeta(itemMeta);
            hideItemFlags(item);
            item.addUnsafeEnchantments(this.enchantments);
            addGlow(item);

            return item;
        }

        item.setAmount(this.itemAmount);
        ItemMeta itemMeta = item.getItemMeta();
        List<Component> newLore = getUpdatedLore();
        assert itemMeta != null;
        if (!getUpdatedName().isEmpty()) itemMeta.displayName(ColorUtils.legacyTranslateColourCodes(getUpdatedName()));
        if (!newLore.isEmpty()) itemMeta.lore(newLore);
        if (this.stringPDC != null) {
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            stringPDC.forEach((nameSpace, data) -> container.set(nameSpace, PersistentDataType.STRING, data));
        }
        if (!this.crazyEnchantments.isEmpty()) itemMeta = addEnchantments(itemMeta, this.crazyEnchantments);

        if (itemMeta instanceof Damageable) ((Damageable) itemMeta).setDamage(this.damage);

        if (this.isPotion && (this.potionType != null || this.potionColor != null)) {
            PotionMeta potionMeta = (PotionMeta) itemMeta;

            if (this.potionType != null) potionMeta.setBasePotionType(this.potionType);

            if (this.potionColor != null) potionMeta.setColor(this.potionColor);
        }

        if (this.material == Material.TIPPED_ARROW && this.potionType != null) {
            Arrow arrowMeta = (Arrow) itemMeta;
            arrowMeta.setBasePotionType(this.potionType);
        }

        if (this.isLeatherArmor && this.armorColor != null) {
            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) itemMeta;
            leatherMeta.setColor(this.armorColor);
        }

        if (this.isBanner && !this.patterns.isEmpty()) {
            BannerMeta bannerMeta = (BannerMeta) itemMeta;
            bannerMeta.setPatterns(this.patterns);
        }

        if (this.isShield && !this.patterns.isEmpty()) {
            BlockStateMeta shieldMeta = (BlockStateMeta) itemMeta;
            Banner banner = (Banner) shieldMeta.getBlockState();
            banner.setPatterns(this.patterns);
            banner.update();
            shieldMeta.setBlockState(banner);
        }

        if (this.useCustomModelData) itemMeta.setCustomModelData(this.customModelData);
        if (this.unbreakable) itemMeta.setUnbreakable(true);

        this.itemFlags.forEach(itemMeta :: addItemFlags);
        item.setItemMeta(itemMeta);
        hideItemFlags(item);
        item.addUnsafeEnchantments(this.enchantments);
        addGlow(item);

        if (this.isMobEgg && itemMeta instanceof SpawnEggMeta) {
            ((SpawnEggMeta) itemMeta).setCustomSpawnedType(this.entityType);
        }

        return item;
    }

    public ItemMeta addEnchantments(ItemMeta meta, Map<CEnchantment, Integer> enchantments) { //TODO Stop CrazyManager from being null to replace this method.

        CrazyManager crazyManager = this.starter.getCrazyManager(); // Temp fix for this method being outdated.
        if (crazyManager != null) return crazyManager.addEnchantments(meta, enchantments); //TODO Replace whole method.

        EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();
        Gson gson = new Gson();
        Map<CEnchantment, Integer> currentEnchantments = enchantmentBookSettings.getEnchantments(meta);

        meta = enchantmentBookSettings.removeEnchantments(meta, enchantments.keySet().stream().filter(currentEnchantments::containsKey).toList());

        String data = meta.getPersistentDataContainer().get(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING);
        Enchant enchantData = data != null ? gson.fromJson(data, Enchant.class) : new Enchant(new HashMap<>());

        List<Component> lore = meta.lore();
        if (lore == null) lore = new ArrayList<>();

        for (Map.Entry<CEnchantment, Integer> entry : enchantments.entrySet()) {
            CEnchantment enchantment = entry.getKey();
            int level = entry.getValue();

            String loreString = enchantment.getCustomName() + " " + NumberUtils.convertLevelString(level);

            lore.add(ColorUtils.legacyTranslateColourCodes(loreString));

            for (Map.Entry<CEnchantment, Integer> x : enchantments.entrySet()) {
                enchantData.addEnchantment(x.getKey().getName(), x.getValue());
            }
        }

        meta.lore(lore);
        meta.getPersistentDataContainer().set(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING, gson.toJson(enchantData));

        return meta;
    }

    private boolean isArmor() {
        String name = this.material.name();

        return name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE") || name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS") || name.equals(Material.TURTLE_HELMET.name());

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
            Log.error(e);
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

    public ItemBuilder setTrimMaterial(TrimMaterial trimMaterial) {
        this.trimMaterial = trimMaterial;

        return this;
    }

    public ItemBuilder setTrimPattern(TrimPattern trimPattern) {
        this.trimPattern = trimPattern;

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
                if (NumberUtils.isInt(modelData)) { // Value is a number.
                    this.useCustomModelData = true;
                    this.customModelData = Integer.parseInt(modelData);
                }
            }

            metaData = metaData.replace("#" + customModelData, "");

            if (NumberUtils.isInt(metaData)) { // Value is durability.
                this.damage = Integer.parseInt(metaData);
            } else { // Value is something else.
                try {
                    this.potionType = getPotionType(PotionEffectType.getByName(metaData));
                } catch (Exception ignored) {}

                this.potionColor = getColor(metaData);
                this.armorColor = getColor(metaData);
                this.mapColor = getColor(metaData);
            }
        } else if (material.contains("#")) {
            String[] materialSplit = material.split("#");
            material = materialSplit[0];

            if (NumberUtils.isInt(materialSplit[1])) { // Value is a number.
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
            case "FILLED_MAP" -> this.isMap = true;
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
        return this.damage;
    }

    /**
     * @param itemName The name of the item.
     * @return The ItemBuilder with an updated name.
     */
    public ItemBuilder setName(String itemName) {
        if (itemName != null) this.itemName = ColorUtils.legacyTranslateColourCodes(itemName);

        return this;
    }
    public ItemBuilder setName(Component itemName) {
        if (itemName != null) this.itemName = itemName;

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
        return lore(lore.stream().map(ColorUtils::legacyTranslateColourCodes).collect(Collectors.toList()));
    }

    /**
     * Set the lore of the item in the builder. This will auto force color in all the lores that contains color code. (&a, &c, &7, etc...)
     *
     * @param lore The lore of the item in the builder.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder lore(List<Component> lore) {
        if (lore != null) {
            this.itemLore.clear();

            this.itemLore.addAll(lore);
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
        if (lore != null) this.itemLore.add(ColorUtils.legacyTranslateColourCodes(lore));

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
    public List<Component> getUpdatedLore() {
        List<Component> newLore = new ArrayList<>();
        if (this.itemLore.isEmpty()) return newLore;

        for (Component line : this.itemLore) {
            String newLine = ColorUtils.toLegacy(line);

            for (Map.Entry<String, String> placeholder : this.lorePlaceholders.entrySet()) {
                newLine = newLine.replace(placeholder.getKey(), placeholder.getValue()).replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
            }

            newLore.add(ColorUtils.legacyTranslateColourCodes(newLine));
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

    private void addPattern(String patternName, String stringColour) {
        NamespacedKey key;

        try {
            key = NamespacedKey.minecraft(patternName.toLowerCase());
        } catch (Exception ignored) {
            return;
        }

        PatternType pattern = RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN).get(key);
        DyeColor colour = getDyeColor(stringColour);

        if (pattern == null || colour == null) return;

        addPattern(new Pattern(colour, pattern));
    }

    /**
     * @param pattern A pattern to add.
     * @return The ItemBuilder with an updated pattern.
     */
    public ItemBuilder addPattern(Pattern pattern) {
        this.patterns.add(pattern);

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
        return this.itemAmount;
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

        if (this.player != null && this.player.length() > 16) {
            this.isHash = true;
            this.isURL = this.player.startsWith("http");
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
     * Adds an enchantment to the item.
     *
     * @param enchantment The enchantment you wish to add.
     * @param level The level of the enchantment ( Unsafe levels included )
     * @return The ItemBuilder with updated enchantments.
     */
    public ItemBuilder addCEEnchantments(CEnchantment enchantment, Integer level) {
        this.crazyEnchantments.put(enchantment, level);

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
        this.itemFlags.clear();

        for (String flagString : flagStrings) {
            ItemFlag flag = getFlag(flagString);

            if (flag != null) this.itemFlags.add(flag);
        }

        return this;
    }

    // Used for multiple Item Flags
    public ItemBuilder addItemFlags(List<String> flagStrings) {
        for (String flagString : flagStrings) {
            try {
                ItemFlag itemFlag = ItemFlag.valueOf(flagString.toUpperCase());

                addItemFlag(itemFlag);
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

        if (flag != null) this.itemFlags.add(flag);

        return this;
    }

    /**
     * Adds an ItemFlag to a map which is added to an item.
     *
     * @param itemFlag The flag to add.
     * @return The ItemBuilder with an updated ItemFlag.
     */
    public ItemBuilder addItemFlag(ItemFlag itemFlag) {
        if (itemFlag != null) this.itemFlags.add(itemFlag);

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
        if (this.hideItemFlags) {
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
            itemBuilder.setName(itemMeta.getDisplayName()).lore(itemMeta.lore());

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
        itemString = itemString.strip();
        try {
            for (String optionString : itemString.split(", ")) {
                String option = optionString.split(":")[0];
                String value = optionString.replace(option + ":", "").replace(option, "");

                switch (option.toLowerCase()) {
                    case "item" -> itemBuilder.setMaterial(value);
                    case "name" -> itemBuilder.setName(value);
                    case "amount" -> {
                        try {
                            itemBuilder.setAmount(Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            itemBuilder.setAmount(1);
                        }
                    }
                    case "damage" -> {
                        try {
                            itemBuilder.setDamage(Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            itemBuilder.setDamage(0);
                        }
                    }
                    case "lore" -> itemBuilder.setLore(List.of(value.split(",")));
                    case "player" -> itemBuilder.setPlayerName(value);
                    case "unbreakable-item" -> {
                        if (value.isEmpty() || value.equalsIgnoreCase("true")) itemBuilder.setUnbreakable(true);
                    }
                    case "trim-pattern" -> {
                        if (!value.isEmpty()) itemBuilder.setTrimPattern(Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(value.toLowerCase())));
                    }
                    case "trim-material" -> {
                        if (!value.isEmpty()) itemBuilder.setTrimMaterial(Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(value.toLowerCase())));
                    }
                    default -> {
                        if (value.contains("-")) {
                            String[] val = value.split("-");
                            if (val.length == 2 && NumberUtils.isInt(val[0]) && NumberUtils.isInt(val[1])) {
                                value = String.valueOf(getRandom(Integer.parseInt(val[0]), Integer.parseInt(val[1])));
                            }
                        }

                        int number = NumberUtils.isInt(value) ? Integer.parseInt(value) : 1;

                        Enchantment enchantment = getEnchantment(option);

                        if (enchantment != null) {
                            if (number != 0) itemBuilder.addEnchantments(enchantment, number);
                            continue;
                        }

                        CEnchantment ceEnchant = plugin.getStarter().getCrazyManager().getEnchantmentFromName(option);

                        if (ceEnchant != null) {
                            if (number != 0) itemBuilder.addCEEnchantments(ceEnchant, number);
                            continue;
                        }

                        for (ItemFlag itemFlag : ItemFlag.values()) {
                            if (itemFlag.name().equalsIgnoreCase(option)) {
                                itemBuilder.addItemFlag(itemFlag);
                                break;
                            }
                        }

                        itemBuilder.addPattern(option, value);

                    }
                }
            }
        } catch (Exception e) {
            itemBuilder.setMaterial(Material.RED_TERRACOTTA).setName("&c&lERROR")
                    .lore(Arrays.asList(Component.text("There was an error", NamedTextColor.RED),
                            Component.text("For : " + (placeHolder != null ? placeHolder : ""), NamedTextColor.RED)));
            plugin.getLogger().log(Level.WARNING, "There is an error with " + placeHolder, e);
        }

        return itemBuilder;
    }

    private static int getRandom(int min, int max) {
        Random random = new Random();
        return min + random.nextInt(++max - min);
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
        if (this.glowing) {
            try {
                if (item != null) {
                    ItemMeta itemMeta = item.getItemMeta();

                    if (itemMeta != null) {
                        if (itemMeta.hasEnchants()) return;
                    }

                    itemMeta.setEnchantmentGlintOverride(true);
                    item.setItemMeta(itemMeta);
                }
            } catch (NoClassDefFoundError ignored) {}
        }
    }

    /**
     * Get the PotionEffect from a PotionEffectType.
     *
     * @param type the type of the potion effect.
     * @return the potion type.
     */
    private PotionType getPotionType(PotionEffectType type) {
        if (type != null) {
            if (type.equals(PotionEffectType.FIRE_RESISTANCE)) {
                return PotionType.FIRE_RESISTANCE;
            } else if (type.equals(PotionEffectType.INSTANT_DAMAGE)) {
                return PotionType.HARMING;
            } else if (type.equals(PotionEffectType.INSTANT_HEALTH)) {
                return PotionType.HEALING;
            } else if (type.equals(PotionEffectType.INVISIBILITY)) {
                return PotionType.INVISIBILITY;
            } else if (type.equals(PotionEffectType.JUMP_BOOST)) {
                return PotionType.LEAPING;
            } else if (type.equals(PotionEffectType.LUCK)) {
                return PotionType.LUCK;
            } else if (type.equals(PotionEffectType.NIGHT_VISION)) {
                return PotionType.NIGHT_VISION;
            } else if (type.equals(PotionEffectType.POISON)) {
                return PotionType.POISON;
            } else if (type.equals(PotionEffectType.REGENERATION)) {
                return PotionType.REGENERATION;
            } else if (type.equals(PotionEffectType.SLOWNESS)) {
                return PotionType.SLOWNESS;
            } else if (type.equals(PotionEffectType.SPEED)) {
                return PotionType.SWIFTNESS;
            } else if (type.equals(PotionEffectType.STRENGTH)) {
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
    @Nullable
    private static Enchantment getEnchantment(String enchantmentName) {
        NamespacedKey key;

        try {
            key = NamespacedKey.minecraft(enchantmentName.toLowerCase());
        } catch (Exception ignored) {
            return null;
        }

        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(key);
    }

    private ItemFlag getFlag(String flagString) {
        for (ItemFlag flag : ItemFlag.values()) {
            if (flag.name().equalsIgnoreCase(flagString)) return flag;
        }

        return null;
    }

    /**
     * @param key The name spaced key value.
     * @param data The data that the key holds.
     * @return The ItemBuilder with an updated item count.
     */
    public ItemBuilder addStringPDC(NamespacedKey key, String data) {
        if (this.stringPDC == null) this.stringPDC = new HashMap<>();
        this.stringPDC.put(key, data);

        return this;
    }
}