package com.badbones69.crazyenchantments.paper.api.builders;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.api.utils.NumberUtils;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Server;
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
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;
import static com.badbones69.crazyenchantments.paper.api.utils.ColorUtils.getColor;

@SuppressWarnings("UnusedReturnValue")
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
    private Map<Enchantment, Integer> enchantments;

    private final Map<CEnchantment, Integer> crazyEnchantments;

    // Shields
    private boolean isShield;

    // Banners
    private boolean isBanner;
    private List<Pattern> patterns;

    // Maps
    private boolean isMap;
    private Color mapColor;

    // Placeholders
    private Map<String, String> namePlaceholders;
    private Map<String, String> lorePlaceholders;

    // Misc
    private ItemStack referenceItem;
    private List<ItemFlag> itemFlags;

    // Custom Data
    private int customModelData;
    private boolean useCustomModelData;
    private final Map<NamespacedKey, String> namespaces;

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

        this.namespaces = new HashMap<>();
    }

    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private static final CrazyInstance instance = plugin.getInstance();

    private final Server server = plugin.getServer();

    private final ComponentLogger logger = plugin.getComponentLogger();

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
        this.namespaces = itemBuilder.namespaces;
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
        //return this.itemName == null ? "" : ColorUtils.toLegacy(this.itemName); //todo() legacy trash
        return "";
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
    public Map<Enchantment, Integer> getEnchantments() {
        return this.enchantments;
    }

    /**
     * Returns the crazyEnchantments on the item.
     */
    public Map<CEnchantment, Integer> getCrazyEnchantments() {
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
    public final String getUpdatedName() {
        /*if (this.itemName == null) return "";
        String newName = ColorUtils.toLegacy(this.itemName); //todo() legacy trash

        for (final Map.Entry<String, String> placeholder : this.namePlaceholders.entrySet()) {
            newName = newName.replace(placeholder.getKey(), placeholder.getValue()).replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
        }*/

        return "";
    }

    /**
     * Builder the item from all the information that was given to the builder.
     *
     * @return The result of all the info that was given to the builder as an ItemStack.
     */
    public ItemStack build() {
        final ItemStack item = this.referenceItem != null ? this.referenceItem : new ItemStack(this.material);

        if (item.isEmpty()) return item;

        if (this.isHead) { // Has to go 1st due to it removing all data when finished.
            final ResolvableProfile.Builder builder = ResolvableProfile.resolvableProfile();

            if (this.isHash) { // Sauce: https://github.com/deanveloper/SkullCreator
                if (this.isURL) {
                    final PlayerProfile profile = this.server.createProfile(UUID.randomUUID(), null);

                    profile.setProperty(new ProfileProperty("", ""));

                    final PlayerTextures textures = profile.getTextures();

                    try {
                        textures.setSkin(URI.create(this.player).toURL(), PlayerTextures.SkinModel.CLASSIC);
                    } catch (final MalformedURLException exception) {
                        this.logger.warn("The url is malformed", exception);
                    }

                    profile.setTextures(textures);

                    builder.addProperties(profile.getProperties());
                } else {
                    builder.name(this.player);
                }

                item.setData(DataComponentTypes.PROFILE, builder.build());
            }

            item.setAmount(this.itemAmount);

            if (!this.namespaces.isEmpty()) {
                item.editPersistentDataContainer(container -> {
                    this.namespaces.forEach((key, value) -> {
                        container.set(key, PersistentDataType.STRING, value);
                    });
                });
            }

            final ItemMeta itemMeta = item.getItemMeta();

            List<Component> newLore = getUpdatedLore();

            //if (!getUpdatedName().isEmpty()) itemMeta.displayName(ColorUtils.legacyTranslateColourCodes(getUpdatedName())); //todo() legacy trash

            if (!newLore.isEmpty()) itemMeta.lore(newLore);

            if (this.useCustomModelData) itemMeta.setCustomModelData(this.customModelData);

            this.itemFlags.forEach(itemMeta :: addItemFlags);

            item.setItemMeta(itemMeta);

            hideItemFlags(item);

            item.addUnsafeEnchantments(this.enchantments);

            addGlow(item);

            return item;
        }

        item.setAmount(this.itemAmount);

        if (!this.namespaces.isEmpty()) {
            item.editPersistentDataContainer(container -> {
                this.namespaces.forEach((key, value) -> {
                    container.set(key, PersistentDataType.STRING, value);
                });
            });
        }

        if (!this.crazyEnchantments.isEmpty())  {
            addEnchantments(item, this.crazyEnchantments);
        }

        ItemMeta itemMeta = item.getItemMeta();

        List<Component> newLore = getUpdatedLore();

        //if (!getUpdatedName().isEmpty()) itemMeta.displayName(ColorUtils.legacyTranslateColourCodes(getUpdatedName())); //todo() legacy trash

        if (!newLore.isEmpty()) itemMeta.lore(newLore);

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

        if (this.useCustomModelData) itemMeta.setCustomModelData(this.customModelData); //todo() deprecated
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

    public void addEnchantments(final ItemStack itemStack, final Map<CEnchantment, Integer> enchantments) {
        instance.addEnchantments(itemStack, enchantments);
    }

    /**
     * Set the type of item the builder is set to.
     *
     * @param material The material you wish to set.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder setMaterial(final Material material) {
        this.material = material;
        this.isHead = material == Material.PLAYER_HEAD;

        return this;
    }

    public ItemBuilder setTrimMaterial(final TrimMaterial trimMaterial) {
        this.trimMaterial = trimMaterial;

        return this;
    }

    public ItemBuilder setTrimPattern(final TrimPattern trimPattern) {
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
    public ItemBuilder setDamage(final int damage) {
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
    public ItemBuilder setName(final String itemName) {
        //if (itemName != null && !itemName.isEmpty()) this.itemName = ColorUtils.legacyTranslateColourCodes(itemName); //todo() legacy trash

        return this;
    }

    public ItemBuilder setName(final Component itemName) {
        if (itemName != null) this.itemName = itemName;

        return this;
    }

    /**
     * @param placeholders The placeholders that will be used.
     * @return The ItemBuilder with updated placeholders.
     */
    public ItemBuilder setNamePlaceholders(final Map<String, String> placeholders) {
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
    public ItemBuilder addNamePlaceholder(final String placeholder, final String argument) {
        this.namePlaceholders.put(placeholder, argument);

        return this;
    }

    /**
     * Remove a placeholder from the list.
     *
     * @param placeholder The placeholder you wish to remove.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder removeNamePlaceholder(final String placeholder) {
        this.namePlaceholders.remove(placeholder);

        return this;
    }

    /**
     * Set the lore of the item in the builder. This will auto force color in all the lores that contains color code.
     *
     * @param lore The lore of the item in the builder.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder setLore(final List<String> lore) {
        return this;
        //return lore(lore.stream().map(ColorUtils::legacyTranslateColourCodes).collect(Collectors.toList())); //todo() legacy trash
    }

    /**
     * Set the lore of the item in the builder. This will auto force color in all the lores that contains color code.
     *
     * @param lore The lore of the item in the builder.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder lore(final List<Component> lore) {
        if (lore != null) {
            this.itemLore.clear();

            this.itemLore.addAll(lore);
        }

        return this;
    }

    /**
     * Add a line to the current lore of the item. This will auto force color in the lore that contains color code.
     *
     * @param lore The new line you wish to add.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder addLore(final String lore) {
        //if (lore != null) this.itemLore.add(ColorUtils.legacyTranslateColourCodes(lore)); //todo() legacy trash

        return this;
    }

    /**
     * Set the placeholders that are in the lore of the item.
     *
     * @param placeholders The placeholders that you wish to use.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder setLorePlaceholders(final Map<String, String> placeholders) {
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
    public ItemBuilder addLorePlaceholder(final String placeholder, final String argument) {
        this.lorePlaceholders.put(placeholder, argument);

        return this;
    }

    /**
     * Get the lore with all the placeholders added to it.
     *
     * @return The lore with all placeholders in it.
     */
    public List<Component> getUpdatedLore() {
        final List<Component> newLore = new ArrayList<>();

        if (this.itemLore.isEmpty()) return newLore;

        for (final Component line : this.itemLore) {
            //String newLine = ColorUtils.toLegacy(line); //todo() legacy trash

            //for (final Map.Entry<String, String> placeholder : this.lorePlaceholders.entrySet()) {
            //    newLine = newLine.replace(placeholder.getKey(), placeholder.getValue()).replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
            //}

            //newLore.add(ColorUtils.legacyTranslateColourCodes(newLine)); //todo() legacy trash
        }

        return newLore;
    }

    /**
     * Remove a placeholder from the lore.
     *
     * @param placeholder The placeholder you wish to remove.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder removeLorePlaceholder(final String placeholder) {
        this.lorePlaceholders.remove(placeholder);

        return this;
    }

    /**
     * @param entityType The entity type the mob spawn egg will be.
     * @return The ItemBuilder with an updated mob spawn egg.
     */
    public ItemBuilder setEntityType(final EntityType entityType) {
        this.entityType = entityType;

        return this;
    }

    private void addPattern(final String patternName, final String stringColour) {
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
    public ItemBuilder addPattern(final Pattern pattern) {
        this.patterns.add(pattern);

        return this;
    }

    /**
     * @param patterns Set a list of Patterns.
     * @return The ItemBuilder with an updated list of patterns.
     */
    public ItemBuilder setPattern(final List<Pattern> patterns) {
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
    public ItemBuilder setAmount(final int amount) {
        this.itemAmount = amount;

        return this;
    }

    /**
     * Get the amount of the item stack in the builder.
     * @param amount The amount that is in the item stack.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder addAmount(final int amount) {
        this.itemAmount += amount;

        return this;
    }

    /**
     * Set the player that will be displayed on the head.
     *
     * @param playerName The player being displayed on the head.
     * @return The ItemBuilder with an updated Player Name.
     */
    public ItemBuilder setPlayerName(@NotNull final String playerName) {
        if (playerName.isEmpty()) return this;

        this.player = playerName;

        if (this.player.length() > 16) {
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
    public ItemBuilder setEnchantments(final Map<Enchantment, Integer> enchantment) {
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
    public ItemBuilder addEnchantments(final Enchantment enchantment, final int level) {
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
    public ItemBuilder addCEEnchantments(final CEnchantment enchantment, final int level) {
        this.crazyEnchantments.put(enchantment, level);

        return this;
    }

    /**
     * Add a flag to the item in the builder.
     *
     * @param flagString The name of the flag you wish to add.
     * @return The ItemBuilder with updated info.
     */
    public ItemBuilder addFlags(final String flagString) {
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
    public ItemBuilder addItemFlag(final ItemFlag itemFlag) {
        if (itemFlag != null) this.itemFlags.add(itemFlag);

        return this;
    }

    /**
     * Adds multiple ItemFlags in a list to a map which get added to an item.
     *
     * @param itemFlags The list of flags to add.
     * @return The ItemBuilder with a list of ItemFlags.
     */
    public ItemBuilder setItemFlags(final List<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;

        return this;
    }

    /**
     * @param hideItemFlags Hide item flags based on a boolean.
     * @return The ItemBuilder with an updated Boolean.
     */
    public ItemBuilder hideItemFlags(final boolean hideItemFlags) {
        this.hideItemFlags = hideItemFlags;

        return this;
    }

    /**
     * @param item The item to hide flags on.
     * @return The ItemBuilder with an updated Item.
     */
    public ItemStack hideItemFlags(final ItemStack item) {
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
     * @param unbreakable Sets the item to be unbreakable.
     * @return The ItemBuilder with an updated Boolean.
     */
    public ItemBuilder setUnbreakable(final boolean unbreakable) {
        this.unbreakable = unbreakable;

        return this;
    }

    /**
     * @param glow Sets whether to make an item to glow or not.
     * @return The ItemBuilder with an updated Boolean.
     */
    public ItemBuilder setGlow(final boolean glow) {
        this.glowing = glow;

        return this;
    }

    // Other misc shit

    /**
     * Converts a String to an ItemBuilder.
     *
     * @param itemString The String you wish to convert.
     * @return The String as an ItemBuilder.
     */
    public static ItemBuilder convertString(final String itemString) {
        return convertString(itemString, null);
    }

    /**
     * Converts a string to an ItemBuilder with a placeholder for errors.
     *
     * @param itemString The String you wish to convert.
     * @param placeHolder The placeholder to use if there is an error.
     * @return The String as an ItemBuilder.
     */
    public static ItemBuilder convertString(String itemString, final String placeHolder) {
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

                        CEnchantment ceEnchant = instance.getEnchantmentFromName(option);

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
            itemBuilder.setMaterial(Material.RED_TERRACOTTA).setName("<red><b>ERROR")
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
        if (item != null && this.glowing) {
            item.editMeta(itemMeta -> itemMeta.setEnchantmentGlintOverride(true));
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
     * @param key the name spaced key value.
     * @param data the data that the key holds.
     * @return the ItemBuilder with an updated item count.
     */
    public ItemBuilder addKey(@NotNull final NamespacedKey key, final String data) {
        this.namespaces.put(key, data);

        return this;
    }
}