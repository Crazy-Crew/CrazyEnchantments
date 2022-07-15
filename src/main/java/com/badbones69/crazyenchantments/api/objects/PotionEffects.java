package com.badbones69.crazyenchantments.api.objects;

import org.bukkit.potion.PotionEffectType;

public class PotionEffects {
    
    private final PotionEffectType potionEffect;
    private final int amplifier;
    private final int duration;
    
    public PotionEffects(PotionEffectType potionEffect, int duration, int amplifier) {
        this.potionEffect = potionEffect;
        this.duration = duration;
        this.amplifier = amplifier;
    }
    
    public PotionEffectType getPotionEffect() {
        return potionEffect;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public int getAmplifier() {
        return amplifier;
    }

}