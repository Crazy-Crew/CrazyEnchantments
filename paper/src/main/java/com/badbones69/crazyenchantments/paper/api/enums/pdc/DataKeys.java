package com.badbones69.crazyenchantments.paper.api.enums.pdc;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public enum DataKeys {

    dust("Crazy_Dust", PersistentDataType.STRING),
    white_scroll_protection("White_Scroll_Protection", PersistentDataType.STRING),
    enchantments("CrazyEnchants", PersistentDataType.STRING),
    stored_enchantments("Stored_Enchantments", PersistentDataType.STRING),
    protection_crystal("is_Protections_Crystal", PersistentDataType.BOOLEAN),
    protected_item("isProtected", PersistentDataType.BOOLEAN),
    scrambler("isScrambler", PersistentDataType.BOOLEAN),
    lost_book("Lost_Book_Type", PersistentDataType.STRING),
    no_firework_damage("no_firework_damage", PersistentDataType.BOOLEAN),
    scroll("Crazy_Scroll", PersistentDataType.STRING),
    experience("Experience", PersistentDataType.STRING),
    limit_reducer("Limit_Reducer", PersistentDataType.INTEGER),
    slot_crystal("Slot_Crystal", PersistentDataType.BOOLEAN);

    @NotNull
    private final CrazyEnchantments plugin = CrazyEnchantments.get();

    private final String NamespacedKey;
    private final PersistentDataType type;

    DataKeys(String NamespacedKey, PersistentDataType type) {
        this.NamespacedKey = NamespacedKey;
        this.type = type;
    }

    public NamespacedKey getNamespacedKey() {
        return new NamespacedKey(this.plugin, this.NamespacedKey);
    }

    public PersistentDataType getType() {
        return this.type;
    }
}