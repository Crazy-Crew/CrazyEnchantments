package com.ryderbelserion.crazyenchantments.paper.enchants.interfaces;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.TagEntry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.Set;

public interface CustomEnchantment {

    Path getPath();

    Key getKey();

    Component getDescription();

    int getAnvilCost();

    int getMaxLevel();

    int getWeight();

    EnchantmentRegistryEntry.EnchantmentCost getMinimumCost();

    EnchantmentRegistryEntry.EnchantmentCost getMaximumCost();

    Iterable<EquipmentSlotGroup> getActiveSlots();

    Set<TagEntry<ItemType>> getSupportedItems();

    Set<TagKey<Enchantment>> getEnchantTagKeys();

    default void init(@NotNull final JavaPlugin plugin) {

    }

    default void build() {

    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean isEnabled() {
        return true;
    }

    default boolean isCurse() {
        return false;
    }

    default TagKey<ItemType> getTagForSupportedItems() {
        return TagKey.create(RegistryKey.ITEM, Key.key( getKey().asString() + "_enchantable"));
    }

    default TagEntry<Enchantment> getTagEntry() {
        return TagEntry.valueEntry(TypedKey.create(RegistryKey.ENCHANTMENT, getKey()));
    }
}