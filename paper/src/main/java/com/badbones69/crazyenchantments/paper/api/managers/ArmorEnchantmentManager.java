package com.badbones69.crazyenchantments.paper.api.managers;

import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.objects.ArmorEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.PotionEffects;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArmorEnchantmentManager {

    private final List<ArmorEnchantment> armorEnchantments = new ArrayList<>();

    public void load() {
        this.armorEnchantments.clear();

        if (CEnchantments.FORTIFY.isActivated())
            this.armorEnchantments.add(new ArmorEnchantment(CEnchantments.FORTIFY, new PotionEffects(PotionEffectType.WEAKNESS, 5 * 20, 0), true));

        if (CEnchantments.FREEZE.isActivated())
            this.armorEnchantments.add(new ArmorEnchantment(CEnchantments.FREEZE, new PotionEffects(PotionEffectType.SLOW, 3 * 20, 1), true));

        if (CEnchantments.PAINGIVER.isActivated())
            this.armorEnchantments.add(new ArmorEnchantment(CEnchantments.PAINGIVER, new PotionEffects(PotionEffectType.POISON, 3 * 20, 0), true));

        if (CEnchantments.SMOKEBOMB.isActivated())
            this.armorEnchantments.add(new ArmorEnchantment(CEnchantments.SMOKEBOMB, Arrays.asList(new PotionEffects(PotionEffectType.SLOW, 3 * 20, 1), new PotionEffects(PotionEffectType.BLINDNESS, 3 * 20, 0)), false));

        if (CEnchantments.VOODOO.isActivated())
            this.armorEnchantments.add(new ArmorEnchantment(CEnchantments.VOODOO, new PotionEffects(PotionEffectType.WEAKNESS, 5 * 20, 0), true));
    }

    public List<ArmorEnchantment> getArmorEnchantments() {
        return this.armorEnchantments;
    }
}