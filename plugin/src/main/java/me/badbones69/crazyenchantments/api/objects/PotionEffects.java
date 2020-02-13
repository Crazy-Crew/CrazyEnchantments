package me.badbones69.crazyenchantments.api.objects;

import org.bukkit.potion.PotionEffectType;

public class PotionEffects {
    
    private PotionEffectType potionEffect;
    private int amplifire;
    private int duration;
    
    public PotionEffects(PotionEffectType potionEffect, int duration, int amplifire) {
        this.potionEffect = potionEffect;
        this.duration = duration;
        this.amplifire = amplifire;
    }
    
    public PotionEffectType getPotionEffect() {
        return potionEffect;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public int getAmplifire() {
        return amplifire;
    }
    
}