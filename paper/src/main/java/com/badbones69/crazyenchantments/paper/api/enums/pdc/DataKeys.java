package com.badbones69.crazyenchantments.paper.api.enums.pdc;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public enum DataKeys {

    dust("crazy_dust", PersistentDataType.STRING),
    white_scroll_protection("white_scroll_protection", PersistentDataType.STRING),
    enchantments("crazy_enchants", PersistentDataType.STRING),
    stored_enchantments("stored_enchantments", PersistentDataType.STRING),
    protection_crystal("is_protections_crystal", PersistentDataType.BOOLEAN),
    protected_item("is_protected", PersistentDataType.BOOLEAN),
    scrambler("is_scrambler", PersistentDataType.BOOLEAN),
    lost_book("lost_book_type", PersistentDataType.STRING),
    no_firework_damage("no_firework_damage", PersistentDataType.BOOLEAN),
    scroll("crazy_scroll", PersistentDataType.STRING),
    experience("experience", PersistentDataType.STRING),
    limit_reducer("limit_reducer", PersistentDataType.INTEGER),
    slot_crystal("slot_crystal", PersistentDataType.BOOLEAN);

    @NotNull
    private final CrazyEnchantments plugin = CrazyEnchantments.get();

    private final String NamespacedKey;
    private final PersistentDataType type;

    DataKeys(String NamespacedKey, PersistentDataType type) {
        this.NamespacedKey = NamespacedKey;
        this.type = type;
    }

    public NamespacedKey getNamespacedKey() {
        return new NamespacedKey(this.plugin, this.plugin.getName().toLowerCase() + "_" + this.NamespacedKey);
    }

    public PersistentDataType getType() {
        return this.type;
    }
}