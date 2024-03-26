package com.badbones69.crazyenchantments.paper.api.managers;

import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.objects.BowEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.PotionEffects;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class BowEnchantmentManager {

    private final List<BowEnchantment> bowEnchantments = new ArrayList<>();

    public void load() {
        this.bowEnchantments.clear();

        if (CEnchantments.PIERCING.isActivated())
            this.bowEnchantments.add(new BowEnchantment(CEnchantments.PIERCING, 2, false));

        if (CEnchantments.ICEFREEZE.isActivated())
            this.bowEnchantments.add(new BowEnchantment(CEnchantments.ICEFREEZE, List.of(new PotionEffects(PotionEffectType.SLOW, 5 * 20, 1)), false));

        if (CEnchantments.VENOM.isActivated())
            this.bowEnchantments.add(new BowEnchantment(CEnchantments.VENOM, List.of(new PotionEffects(PotionEffectType.POISON, 2 * 20, -1)), true));

        if (CEnchantments.SNIPER.isActivated())
            this.bowEnchantments.add(new BowEnchantment(CEnchantments.SNIPER, List.of(new PotionEffects(PotionEffectType.POISON, 5 * 20, 1)), false));
    }

    public List<BowEnchantment> getBowEnchantments() {
        return this.bowEnchantments;
    }
}