package com.badbones69.crazyenchantments.paper.api.objects.items;

import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.ryderbelserion.crazyenchantments.utils.ConfigUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class ScramblerData {

    private ItemBuilder scramblerItem;
    private ItemBuilder pointer;
    private boolean animationToggle;
    private String guiName;

    public void loadScrambler(final CommentedConfigurationNode config) {
        this.scramblerItem = new ItemBuilder().setMaterial(config.node("Settings", "Scrambler", "Item").getString("SUNFLOWER"))
                .setName(config.node("Settings", "Scrambler", "Name").getString("&e&lThe Grand Scrambler"))
                .setLore(ConfigUtils.getStringList(config, "Settings", "Scrambler", "Lore"))
                .setGlow(config.node("Settings", "Scrambler", "Glowing").getBoolean(false));

        this.pointer = new ItemBuilder().setMaterial(config.node("Settings", "Scrambler", "GUI", "Pointer", "Item").getString("REDSTONE_TORCH"))
                .setName(config.node("Settings", "Scrambler", "GUI", "Pointer", "Name").getString("&c&lPointer"))
                .setLore(ConfigUtils.getStringList(config, "Settings", "Scrambler", "GUI", "Pointer", "Lore"));

        this.animationToggle = config.node("Settings", "Scrambler", "GUI", "Toggle").getBoolean(true);

        final String name = config.node("Settings", "Scrambler", "GUI", "Name").getString("&8Rolling the &eScrambler");

        this.guiName = name.isEmpty() ? name : ColorUtils.color(name); // only color if not empty.
    }

    /**
     * Get the scrambler item stack.
     * @return The scramblers.
     */
    public ItemStack getScramblers() {
        return getScramblers(1);
    }

    /**
     * Get the scrambler item stack.
     * @param amount The amount you want.
     * @return The scramblers.
     */
    public ItemStack getScramblers(final int amount) {
        final ItemStack item = this.scramblerItem.setAmount(amount).build();

        item.editPersistentDataContainer(container -> {
            container.set(DataKeys.scrambler.getNamespacedKey(), PersistentDataType.BOOLEAN, true);
        });

        return item;
    }

    public boolean isScrambler(final ItemStack item) {
        return item.getPersistentDataContainer().has(DataKeys.scrambler.getNamespacedKey());
    }

    public String getGuiName() {
        return this.guiName;
    }

    public ItemBuilder getPointer() {
        return this.pointer;
    }

    public boolean isAnimationToggle() {
        return this.animationToggle;
    }
}