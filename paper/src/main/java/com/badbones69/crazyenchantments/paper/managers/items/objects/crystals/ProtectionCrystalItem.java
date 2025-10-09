package com.badbones69.crazyenchantments.paper.managers.items.objects.crystals;

import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.managers.items.interfaces.CustomItem;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProtectionCrystalItem implements CustomItem {

    private String protectionString;
    private ItemBuilder itemBuilder;

    public ProtectionCrystalItem() {
        init();
    }

    @Override
    public void init() {
        final YamlConfiguration config = FileKeys.config.getYamlConfiguration();

        this.itemBuilder = new ItemBuilder()
                .setMaterial(config.getString("Settings.ProtectionCrystal.Item", "EMERALD"))
                .setName(config.getString("Settings.ProtectionCrystal.Name", "&5&lProtection &b&lCrystal"))
                .setLore(config.getStringList("Settings.ProtectionCrystal.Lore"))
                .setGlow(config.getBoolean("Settings.ProtectionCrystal.Glowing", false))
                .addKey(DataKeys.protection_crystal.getNamespacedKey(), "true");

        this.protectionString = ColorUtils.color(config.getString("Settings.ProtectionCrystal.Protected", "&6Ancient Protection"));
    }

    @Override
    public @NotNull final ItemStack getItemStack(final int amount) {
        return this.itemBuilder.build();
    }

    @Override
    public boolean isItem(@NotNull final ItemStack itemStack) {
        return hasKey(itemStack, DataKeys.protection_crystal.getNamespacedKey());
    }

    @Override
    public boolean hasKey(@NotNull final ItemStack itemStack, @NotNull NamespacedKey key) {
        if (itemStack.isEmpty()) return false;

        return itemStack.getPersistentDataContainer().has(key);
    }

    @Override
    public void addKey(@NotNull final ItemStack itemStack, @NotNull final NamespacedKey key, @NotNull final String value) {
        final List<Component> itemLore = itemStack.lore();

        final List<Component> lore = itemLore != null ? itemLore : new ArrayList<>();

        itemStack.editPersistentDataContainer(container -> container.set(key, PersistentDataType.STRING, value));

        lore.add(ColorUtils.legacyTranslateColourCodes(this.protectionString));

        itemStack.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(lore).build());
    }

    @Override
    public void addKey(@NotNull final NamespacedKey key, @NotNull final String value) {
        this.itemBuilder.addKey(key, value);
    }

    @Override
    public void removeKey(@NotNull final ItemStack itemStack, @NotNull final NamespacedKey key) {
        itemStack.editPersistentDataContainer(container -> container.remove(key));

        final List<Component> lore = itemStack.lore();

        if (lore != null) {
            lore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).contains(ColorUtils.stripStringColour(this.protectionString)));

            itemStack.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(lore).build());
        }
    }
}