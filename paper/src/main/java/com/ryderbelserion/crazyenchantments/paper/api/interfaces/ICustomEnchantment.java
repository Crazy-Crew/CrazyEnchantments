package com.ryderbelserion.crazyenchantments.paper.api.interfaces;

import com.ryderbelserion.crazyenchantments.paper.CrazyEnchantmentsPlugin;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.TagEntry;
import me.youhavetrouble.yardwatch.Protection;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

public interface ICustomEnchantment {

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

    default void init(@NotNull final CrazyEnchantmentsPlugin plugin) {

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

    default boolean canBreakBlock(final boolean isYardWatchEnabled, @NotNull final Player player, @NotNull final Block block) {
        if (!isYardWatchEnabled) return true;

        final ServicesManager service = player.getServer().getServicesManager();

        final Collection<RegisteredServiceProvider<Protection>> protections = service.getRegistrations(Protection.class);

        boolean canBreakBlock = true;

        final BlockState state = block.getState(true);

        for (final RegisteredServiceProvider<Protection> protection : protections) {
            final Protection provider = protection.getProvider();

            if (provider.canBreakBlock(player, state)) {
                continue;
            }

            canBreakBlock = false;

            break;
        }

        return canBreakBlock;
    }
}