package com.badbones69.crazyenchantments.paper.api.enums.pdc;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public enum DataKeys {

    DUST("Crazy_Dust", String.class),
    ENCHANTMENTS("CrazyEnchants", String.class),
    STORED_ENCHANTMENTS("Stored_Enchantments", String.class),
    PROTECTION_CRYSTAL("is_Protections_Crystal", Boolean.class),
    PROTECTED_ITEM("isProtected", Boolean.class),
    SCRAMBLER("isScrambler", Boolean.class),
    LOST_BOOK("Lost_Book_Type", String.class),
    NO_FIREWORK_DAMAGE("No_Damage", Boolean.class),
    SCROLL("Crazy_Scroll", String.class);

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);
    private final String namSpaceKey;

    DataKeys(String nameSpaceKey, Object dataType) {
        this.namSpaceKey = nameSpaceKey;
    }

    public NamespacedKey getKey() {
        return new NamespacedKey(this.plugin, this.namSpaceKey);
    }

    public String getStringKey() {
        return this.namSpaceKey;
    }
}