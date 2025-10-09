package com.badbones69.crazyenchantments.paper.managers.items.objects;

import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.managers.items.interfaces.CustomItem;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PointerItem implements CustomItem {

    private ItemBuilder itemBuilder;

    public PointerItem() {
        init();
    }

    @Override
    public void init() {
        final YamlConfiguration config = FileKeys.config.getYamlConfiguration();

        this.itemBuilder = new ItemBuilder().setMaterial(config.getString("Settings.Scrambler.GUI.Pointer.Item", "REDSTONE_TORCH"))
                .setName(config.getString("Settings.Scrambler.GUI.Pointer.Name", "&c&lPointer"))
                .setLore(config.getStringList("Settings.Scrambler.GUI.Pointer.Lore"));
    }

    @Override
    public @NotNull final ItemStack getItemStack(final int amount) {
        return this.itemBuilder.build();
    }

    @Override
    public boolean isItem(@NotNull final ItemStack itemStack) {
        return true;
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