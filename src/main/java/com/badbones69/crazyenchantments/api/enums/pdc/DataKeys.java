package com.badbones69.crazyenchantments.api.enums.pdc;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import org.bukkit.NamespacedKey;

public enum DataKeys {
    DUST("Crazy_Dust", String.class),
    ENCHANTMENTS("CrazyEnchants", String.class),
    STORED_ENCHANTMENTS("Stored_Enchantments", String.class),
    PROTECTION_CRYSTAL("is_Protections_Crystal", Boolean.class),
    PROTECTED_ITEM("isProtected", Boolean.class),
    SCRAMBLER("isScrambler", Boolean.class),
    LOST_BOOK("Lost_Book_Type", String.class),
    NO_FIREWORK_DAMAGE("No_Damage", Boolean.class);

    private final static CrazyEnchantments plugin = CrazyEnchantments.getPlugin();
    private final String namSpaceKey;

    DataKeys(String nameSpaceKey, Object dataType) {
        this.namSpaceKey = nameSpaceKey;
    }

    public NamespacedKey getKey() {
        return new NamespacedKey(plugin, namSpaceKey);
    }
    public String getStringKey() {
        return this.namSpaceKey;
    }

}
