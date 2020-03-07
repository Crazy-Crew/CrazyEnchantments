package me.badbones69.crazyenchantments.api.managers;

import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.objects.ArmorEnchantment;
import me.badbones69.crazyenchantments.api.objects.PotionEffects;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArmorEnchantmentManager {
    
    private static ArmorEnchantmentManager instance = new ArmorEnchantmentManager();
    private List<ArmorEnchantment> armorEnchantments = new ArrayList<>();
    
    public static ArmorEnchantmentManager getInstance() {
        return instance;
    }
    
    public void load() {
        armorEnchantments.clear();
        if (CEnchantments.FORTIFY.isActivated()) {
            armorEnchantments.add(new ArmorEnchantment(CEnchantments.FORTIFY, new PotionEffects(PotionEffectType.WEAKNESS, 5 * 20, 0), true));
        }
        if (CEnchantments.FREEZE.isActivated()) {
            armorEnchantments.add(new ArmorEnchantment(CEnchantments.FREEZE, new PotionEffects(PotionEffectType.SLOW, 3 * 20, 1), true));
        }
        if (CEnchantments.PAINGIVER.isActivated()) {
            armorEnchantments.add(new ArmorEnchantment(CEnchantments.PAINGIVER, new PotionEffects(PotionEffectType.POISON, 3 * 20, 0), true));
        }
        if (CEnchantments.SMOKEBOMB.isActivated()) {
            armorEnchantments.add(new ArmorEnchantment(CEnchantments.SMOKEBOMB, Arrays.asList(new PotionEffects(PotionEffectType.SLOW, 3 * 20, 1), new PotionEffects(PotionEffectType.BLINDNESS, 3 * 20, 0)), false));
        }
        if (CEnchantments.VOODOO.isActivated()) {
            armorEnchantments.add(new ArmorEnchantment(CEnchantments.VOODOO, new PotionEffects(PotionEffectType.WEAKNESS, 5 * 20, 0), true));
        }
    }
    
    public List<ArmorEnchantment> getArmorEnchantments() {
        return armorEnchantments;
    }
    
}