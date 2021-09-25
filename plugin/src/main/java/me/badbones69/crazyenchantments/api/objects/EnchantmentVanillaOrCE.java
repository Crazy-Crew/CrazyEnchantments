package me.badbones69.crazyenchantments.api.objects;

import org.bukkit.enchantments.Enchantment;

public class EnchantmentVanillaOrCE {
    private final CEnchantment cEnchantment;
    private final Enchantment vanilla;

    public EnchantmentVanillaOrCE(CEnchantment cEnchantment) {
        this.cEnchantment = cEnchantment;
        this.vanilla = null;
    }

    public EnchantmentVanillaOrCE(Enchantment vanilla) {
        this.cEnchantment = null;
        this.vanilla = vanilla;
    }

    public EnchantmentVanillaOrCE(CEnchantment cEnchantment, Enchantment vanilla) {
        this.cEnchantment = cEnchantment;
        this.vanilla = vanilla;
    }

    public CEnchantment getCEnchantment() {
        return cEnchantment;
    }

    public Enchantment getVanilla() {
        return vanilla;
    }

    public boolean isCEnchantment() {
        return cEnchantment != null;
    }

    public boolean isVanilla() {
        return vanilla != null;
    }
}
