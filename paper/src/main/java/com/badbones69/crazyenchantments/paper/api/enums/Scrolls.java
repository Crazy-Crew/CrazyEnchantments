package com.badbones69.crazyenchantments.paper.api.enums;

import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.objects.other.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum Scrolls {
    
    BLACK_SCROLL("Black-Scroll", "BlackScroll", Arrays.asList("b", "black", "blackscroll")),
    WHITE_SCROLL("White-Scroll", "WhiteScroll", Arrays.asList("w", "white", "whitescroll")),
    TRANSMOG_SCROLL("Transmog-Scroll", "TransmogScroll", Arrays.asList("t", "transmog", "transmogscroll"));
    
    private static final HashMap<Scrolls, ItemBuilder> itemBuilderScrolls = new HashMap<>();
    private final String name;
    private final String configName;
    private final List<String> knownNames;
    
    Scrolls(String name, String configName, List<String> knowNames) {
        this.name = name;
        this.knownNames = knowNames;
        this.configName = configName;
    }
    
    public static void loadScrolls() {
        FileConfiguration config = Files.CONFIG.getFile();
        itemBuilderScrolls.clear();

        for (Scrolls scroll : values()) {
            String path = "Settings." + scroll.getConfigName() + ".";
            itemBuilderScrolls.put(scroll, new ItemBuilder()
            .setName(config.getString(path + "Name", "Error getting name."))
            .setLore(config.getStringList(path + "Item-Lore"))
            .setMaterial(config.getString(path + "Item", "BOOK"))
            .setGlow(config.getBoolean(path + "Glowing", false)));
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
    
    public String getConfigName() {
        return this.configName;
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
        meta.getPersistentDataContainer().set(scroll, PersistentDataType.STRING, this.configName);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getScroll(int amount) {
        ItemStack item = itemBuilderScrolls.get(this).setAmount(amount).build();
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(scroll, PersistentDataType.STRING, this.configName);
        item.setItemMeta(meta);
        return item;
    }

    private static final NamespacedKey whiteScrollProtectionKey = DataKeys.white_scroll_protection.getNamespacedKey();

    public static String getWhiteScrollProtectionName() {
        String protectNamed;

        FileConfiguration config = Files.CONFIG.getFile();

        protectNamed = ColorUtils.color(config.getString("Settings.WhiteScroll.ProtectedName"));

        return protectNamed;
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

        lore.removeIf(loreComponent -> PlainTextComponentSerializer.plainText().serialize(loreComponent).replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "")
                .contains(getWhiteScrollProtectionName().replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "")));
        meta.lore(lore);

        meta.lore(lore);
        item.setItemMeta(meta);

        return item;
    }
}