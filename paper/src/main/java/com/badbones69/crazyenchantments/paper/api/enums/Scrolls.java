package com.badbones69.crazyenchantments.paper.api.enums;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.ryderbelserion.crazyenchantments.objects.ConfigOptions;
import com.ryderbelserion.crazyenchantments.utils.ConfigUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
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
    
    Scrolls(final String name, final String configName, final List<String> knowNames) {
        this.name = name;
        this.knownNames = knowNames;
        this.configName = configName;
    }
    
    public static void loadScrolls(final CommentedConfigurationNode config) {
        itemBuilderScrolls.clear();

        for (final Scrolls scroll : values()) {
            final CommentedConfigurationNode child = config.node(scroll.getConfigName());

            itemBuilderScrolls.put(scroll, new ItemBuilder().setName(config.node("Name").getString("Error getting name."))
            .setLore(ConfigUtils.getStringList(child, "Item-Lore"))
            .setMaterial(child.node("Item").getString("BOOK"))
            .setGlow(config.node("Glowing").getBoolean(false)));
        }
    }
    
    public static Scrolls getFromName(final String nameString) {
        for (final Scrolls scroll : Scrolls.values()) {
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
     *
     * @return The name that is stored on the item, and defines that the item is in fact a Scroll.
     */
    public String getConfigName() {
        return this.configName;
    }

    private static final NamespacedKey scroll = DataKeys.scroll.getNamespacedKey();

    public static Scrolls getFromPDC(final ItemStack item) {
        final PersistentDataContainerView data = item.getPersistentDataContainer();

        if (!data.has(scroll)) return null;

        return getFromName(data.get(scroll, PersistentDataType.STRING));
    }

    public ItemStack getScroll() {
        final ItemStack item = itemBuilderScrolls.get(this).build();

        item.editPersistentDataContainer(container -> {
            container.set(scroll, PersistentDataType.STRING, this.configName);
        });

        return item;
    }

    public ItemStack getScroll(final int amount) {
        final ItemStack item = itemBuilderScrolls.get(this).setAmount(amount).build();

        item.editPersistentDataContainer(container -> {
            container.set(scroll, PersistentDataType.STRING, this.configName);
        });

        return item;
    }

    private static final NamespacedKey whiteScrollProtectionKey = DataKeys.white_scroll_protection.getNamespacedKey();

    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private static final ConfigOptions options = plugin.getOptions();

    public static String getWhiteScrollProtectionName() {
        return ColorUtils.color(options.getWhiteScrollProtectedName());
    }

    public static boolean hasWhiteScrollProtection(@NotNull final ItemStack item) {
        return item.hasItemMeta() && hasWhiteScrollProtection(item.getPersistentDataContainer());
    }

    public static boolean hasWhiteScrollProtection(@Nullable final PersistentDataContainerView data) {
        return data != null && data.has(whiteScrollProtectionKey);
    }

    public static ItemStack addWhiteScrollProtection(@NotNull final ItemStack item) {
        final ItemLore itemLore = item.getData(DataComponentTypes.LORE);

        List<Component> lore = new ArrayList<>(itemLore != null ? itemLore.styledLines() : new ArrayList<>());

        lore.add(ColorUtils.legacyTranslateColourCodes(getWhiteScrollProtectionName()));

        item.editPersistentDataContainer(container -> container.set(whiteScrollProtectionKey, PersistentDataType.BOOLEAN, true));

        item.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(lore).build());

        return item;
    }

    public static ItemStack removeWhiteScrollProtection(@NotNull final ItemStack item) {
        if (item.getPersistentDataContainer().has(whiteScrollProtectionKey, PersistentDataType.BOOLEAN)) {
            item.editPersistentDataContainer(container -> container.remove(whiteScrollProtectionKey));
        }

        final List<Component> lore = item.lore();

        if (lore == null || lore.isEmpty()) {
            return item;
        }

        lore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).contains(ColorUtils.stripStringColour(getWhiteScrollProtectionName())));

        item.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(lore).build());

        return item;
    }
}