package com.badbones69.crazyenchantments.paper.managers.items.objects.crystals;

import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.managers.items.interfaces.CustomItem;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class SlotCrystalItem implements CustomItem {

    private ItemBuilder itemBuilder;

    public SlotCrystalItem() {
        init();
    }

    @Override
    public void init() {
        final YamlConfiguration config = FileKeys.config.getYamlConfiguration();

        this.itemBuilder = new ItemBuilder()
                .setMaterial(config.getString("Settings.Slot_Crystal.Item", "RED_WOOL"))
                .setName(config.getString("Settings.Slot_Crystal.Name", "&5&lSlot &b&lCrystal"))
                .setLore(config.getStringList("Settings.Slot_Crystal.Lore"))
                .setGlow(config.getBoolean("Settings.Slot_Crystal.Glowing", false))
                .addKey(DataKeys.slot_crystal.getNamespacedKey(), "true");
    }

    @Override
    public @NotNull final ItemStack getItemStack(final int amount) {
        return this.itemBuilder.build();
    }

    @Override
    public boolean isItem(@NotNull final ItemStack itemStack) {
        return hasKey(itemStack, DataKeys.slot_crystal.getNamespacedKey());
    }

    @Override
    public boolean hasKey(@NotNull final ItemStack itemStack, @NotNull NamespacedKey key) {
        if (itemStack.isEmpty()) return false;

        return itemStack.getPersistentDataContainer().has(key);
    }

    @Override
    public void addKey(@NotNull final ItemStack itemStack, @NotNull final NamespacedKey key, @NotNull final String value) {
        itemStack.editPersistentDataContainer(container -> container.set(key, PersistentDataType.STRING, value));
    }

    @Override
    public void addKey(@NotNull final NamespacedKey key, @NotNull final String value) {
        this.itemBuilder.addKey(key, value);
    }

    @Override
    public void removeKey(@NotNull final ItemStack itemStack, @NotNull final NamespacedKey key) {
        itemStack.editPersistentDataContainer(container -> container.remove(key));
    }
}