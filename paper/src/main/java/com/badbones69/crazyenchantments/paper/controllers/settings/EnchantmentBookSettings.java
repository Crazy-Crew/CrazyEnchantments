package com.badbones69.crazyenchantments.paper.controllers.settings;

import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.Enchant;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class EnchantmentBookSettings {

    /**
     *
     * @return True if unsafe enchantments are enabled.
     */
    public boolean useUnsafeEnchantments() {
        final YamlConfiguration config = FileKeys.config.getYamlConfiguration();

        return config.getBoolean("Settings.EnchantmentOptions.UnSafe-Enchantments", true);
    }

    /**
     * @param item Item you are getting the level from.
     * @param enchant The enchantment you want the level from.
     * @return The level the enchantment has.
     */
    public int getLevel(@NotNull final ItemStack item, @NotNull final CEnchantment enchant) {
        final String data = item.getPersistentDataContainer().get(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING);

        int level = data == null ? 0 : Methods.getGson().fromJson(data, Enchant.class).getLevel(enchant.getName());

        if (!useUnsafeEnchantments() && level > enchant.getMaxLevel()) level = enchant.getMaxLevel();

        return level;
    }
}