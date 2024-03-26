package com.badbones69.crazyenchantments.paper.platform.items;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenchantments.ConfigManager;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractItem {

    protected final SettingsManager config = ConfigManager.getConfig();

    public abstract AbstractItem setAmount(int amount);

    public abstract ItemStack getItem();

    public abstract boolean isItem();

}