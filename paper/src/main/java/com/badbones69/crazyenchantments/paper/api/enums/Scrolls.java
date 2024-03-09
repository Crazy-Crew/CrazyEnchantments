package com.badbones69.crazyenchantments.paper.api.enums;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.properties.Property;
import com.badbones69.crazyenchantments.ConfigManager;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.objects.other.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.platform.impl.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Scrolls {

    BLACK_SCROLL(List.of("b", "black", "blackscroll"), "Black-Scroll", Config.black_scroll_item, Config.black_scroll_name, Config.black_scroll_lore, Config.black_scroll_glowing),
    WHITE_SCROLL(List.of("w", "white", "whitescroll"), "White-Scroll", Config.white_scroll_item, Config.white_scroll_name, Config.white_scroll_lore, Config.white_scroll_glowing),
    TRANSMOG_SCROLL(List.of("t", "transmog", "transmogscroll"), "Transmog-Scroll", Config.transmog_scroll_item, Config.transmog_scroll_name, Config.transmog_scroll_lore, Config.transmog_scroll_glowing);
    
    private static final HashMap<Scrolls, ItemBuilder> itemBuilderScrolls = new HashMap<>();

    private final String name;
    private final List<String> knownNames;
    private final List<String> itemLore;
    private final Material material;
    private final String itemName;
    private final boolean isGlowing;

    @NotNull
    private final SettingsManager config = ConfigManager.getConfig();
    
    Scrolls(List<String> knownNames, String name, Property<String> material, Property<String> displayName, Property<List<String>> lore, Property<Boolean> isGlowing) {
        this.knownNames = knownNames;
        this.name = name;

        this.material = Material.matchMaterial(this.config.getProperty(material));

        this.itemName = this.config.getProperty(displayName);

        this.itemLore = this.config.getProperty(lore);

        this.isGlowing = this.config.getProperty(isGlowing);
    }
    
    public static void loadScrolls() {
        itemBuilderScrolls.clear();

        for (Scrolls scroll : values()) {
            ItemBuilder itemStack = new ItemBuilder()
                    .setMaterial(scroll.material)
                    .setName(scroll.itemName)
                    .setLore(scroll.itemLore)
                    .setGlow(scroll.isGlowing);

            itemBuilderScrolls.put(scroll, itemStack);
        }
    }
    
    public static Scrolls getFromName(String nameString) {
        for (Scrolls scroll : Scrolls.values()) {
            if (scroll.getKnownNames().contains(nameString.toLowerCase())) return scroll;
        }

        return null;
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<String> getKnownNames() {
        return this.knownNames;
    }

    /**
     * @return The name that is stored on the item, and defines that the item is in fact a Scroll.
     */
    public String getConfigName() {
        return getName().replaceAll("-", "");
    }

    private static final NamespacedKey scroll = DataKeys.scroll.getNamespacedKey();

    public static Scrolls getFromPDC(ItemStack item) {
        PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
        if (!item.hasItemMeta() || !data.has(scroll)) return null;

        return getFromName(data.get(scroll, PersistentDataType.STRING));
    }

    public ItemStack getScroll() {
        ItemStack item = itemBuilderScrolls.get(this).build();
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(scroll, PersistentDataType.STRING, getConfigName());
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getScroll(int amount) {
        ItemStack item = itemBuilderScrolls.get(this).setAmount(amount).build();
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(scroll, PersistentDataType.STRING, getConfigName());
        item.setItemMeta(meta);
        return item;
    }

    private static final NamespacedKey whiteScrollProtectionKey = DataKeys.white_scroll_protection.getNamespacedKey();

    public static String getWhiteScrollProtectionName() {
        return ColorUtils.color(ConfigManager.getConfig().getProperty(Config.white_scroll_protected));
    }

    public static boolean hasWhiteScrollProtection(ItemStack item) {
        return item.hasItemMeta() && hasWhiteScrollProtection(item.getItemMeta());
    }

    public static boolean hasWhiteScrollProtection(ItemMeta meta) {
        return meta != null && hasWhiteScrollProtection(meta.getPersistentDataContainer());
    }

    public static boolean hasWhiteScrollProtection(PersistentDataContainer data) {
        return data != null && data.has(whiteScrollProtectionKey);
    }

    public static ItemStack addWhiteScrollProtection(ItemStack item) {
        assert item.hasItemMeta();
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = item.lore() != null ? item.lore() : new ArrayList<>();

        assert lore != null;
        lore.add(ColorUtils.legacyTranslateColourCodes(getWhiteScrollProtectionName()));
        meta.getPersistentDataContainer().set(whiteScrollProtectionKey, PersistentDataType.BOOLEAN, true);

        meta.lore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack removeWhiteScrollProtection(ItemStack item) {
        if (!item.hasItemMeta()) return item;

        ItemMeta meta = item.getItemMeta();

        if (meta.getPersistentDataContainer().has(whiteScrollProtectionKey, PersistentDataType.BOOLEAN)) meta.getPersistentDataContainer().remove(whiteScrollProtectionKey);

        if (item.lore() == null) {
            item.setItemMeta(meta);

            return item;
        }

        List<Component> lore = item.lore();

        if (lore != null) {
            lore.removeIf(loreComponent -> PlainTextComponentSerializer.plainText().serialize(loreComponent).replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "").contains(getWhiteScrollProtectionName().replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "")));
        }

        meta.lore(lore);
        item.setItemMeta(meta);

        return item;
    }
}