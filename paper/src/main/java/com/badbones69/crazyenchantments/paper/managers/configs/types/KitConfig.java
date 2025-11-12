package com.badbones69.crazyenchantments.paper.managers.configs.types;

import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.managers.configs.interfaces.IConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitConfig extends IConfig {

    private final List<String> guiCustomization;
    private final String inventoryName;
    private final int inventorySize;

    public KitConfig(@NotNull final ConfigurationSection section) {
        this.guiCustomization = section.getStringList("Settings.GUI-Customization");
        this.inventoryName = section.getString("Inventory-Name", "");
        this.inventorySize = section.getInt("GUI-Size", 27);
    }

    public @NotNull final Map<Integer, ItemBuilder> getGuiCustomization() {
        final Map<Integer, ItemBuilder> builders = new HashMap<>();

        for (final String item : this.guiCustomization) { //todo() migrate this from a StringList
            int slot = 0;

            for (final String option : item.split(", ")) {
                if (option.contains("Slot:")) {
                    slot = Integer.parseInt(option.replace("Slot:", ""));

                    break;
                }
            }

            slot--;

            builders.putIfAbsent(slot, ItemBuilder.convertString(item));
        }

        return builders;
    }

    public @NotNull final String getInventoryName() {
        return this.inventoryName;
    }

    public final int getInventorySize() {
        return this.inventorySize;
    }
}