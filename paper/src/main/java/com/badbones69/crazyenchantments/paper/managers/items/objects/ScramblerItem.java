package com.badbones69.crazyenchantments.paper.managers.items.objects;

import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.managers.items.interfaces.CustomItem;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ScramblerItem implements CustomItem {

    private ItemBuilder itemBuilder;

    public ScramblerItem() {
        init();
    }

    @Override
    public void init() {
        final YamlConfiguration config = FileKeys.config.getYamlConfiguration();

        this.itemBuilder = new ItemBuilder()
                .setMaterial(config.getString("Settings.Scrambler.Item", "SUNFLOWER"))
                .setName(config.getString("Settings.Scrambler.Name", "'&e&lThe Grand Scrambler"))
                .setLore(config.getStringList("Settings.Scrambler.Lore"))
                .setGlow(config.getBoolean("Settings.Scrambler.Glowing", false));
    }

    @Override
    public @NotNull final ItemStack getItemStack(final int amount) {
        return this.itemBuilder.build();
    }

    @Override
    public boolean isItem(@NotNull final ItemStack itemStack) {
        if (itemStack.isEmpty()) return false;

        return itemStack.getPersistentDataContainer().has(DataKeys.scrambler.getNamespacedKey());
    }
}