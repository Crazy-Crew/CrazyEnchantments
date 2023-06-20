package com.badbones69.crazyenchantments.api.enums.pdc;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import org.bukkit.NamespacedKey;

public class DustData {
    private final String name;
    private final int min;
    private final int max;
    private final int chance;
    private final NamespacedKey nameSpacedKey = new NamespacedKey(CrazyEnchantments.getPlugin(), "Crazy_Dust");

    public DustData(String dustConfigName, int min, int max, int chance) {
        this.name = dustConfigName;
        this.min = min;
        this.max = max;
        this.chance = chance;
    }

    public int getMax(){ return this.max; }

    public int getMin(){ return this.min; }

    public String getConfigName() { return this.name; }

    public int getChance() { return this.chance; }

    public NamespacedKey getKey() {
        return nameSpacedKey;
    }


}
