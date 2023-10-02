package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import java.util.Collections;
import java.util.List;

public class ArmorEnchantment {
    
    private final CEnchantments enchantment;
    private List<PotionEffects> potionEffects;
    private double damageAmplifier;
    private final boolean isLevelAddedToAmplifier;
    private final boolean isPotionEnchantment;
    
    /**
     * Create an armor enchantment that does damage amplifying.
     * @param enchantment The enchantment that the settings are for.
     * @param damageAmplifier The damage amplifier that is added to the damage.
     * @param isLevelAddedToAmplifier If the level of the enchantment on the item is added to the damage amplifier.
     */
    public ArmorEnchantment(CEnchantments enchantment, double damageAmplifier, boolean isLevelAddedToAmplifier) {
        this.enchantment = enchantment;
        this.damageAmplifier = damageAmplifier;
        this.isLevelAddedToAmplifier = isLevelAddedToAmplifier;
        this.isPotionEnchantment = false;
    }
    
    /**
     * Crate an armor enchantment that adds potion effects to the target.
     * @param enchantment The enchantment that the settings are for.
     * @param potionEffect The effects that are added to the entity that is hit.
     * @param isLevelAddedToAmplifier If the level of the enchantment on the item is added to the potion amplifier.
     */
    public ArmorEnchantment(CEnchantments enchantment, PotionEffects potionEffect, boolean isLevelAddedToAmplifier) {
        this(enchantment, Collections.singletonList(potionEffect), isLevelAddedToAmplifier);
    }
    
    /**
     * Crate an armor enchantment that adds potion effects to the target.
     * @param enchantment The enchantment that the settings are for.
     * @param potionEffects The effects that are added to the entity that is hit.
     * @param isLevelAddedToAmplifier If the level of the enchantment on the item is added to the potion amplifier.
     */
    public ArmorEnchantment(CEnchantments enchantment, List<PotionEffects> potionEffects, boolean isLevelAddedToAmplifier) {
        this.enchantment = enchantment;
        this.potionEffects = potionEffects;
        this.isLevelAddedToAmplifier = isLevelAddedToAmplifier;
        this.isPotionEnchantment = true;
    }
    
    /**
     * The enchantment the settings are for.
     * @return The CEnchantments object.
     */
    public CEnchantments getEnchantment() {
        return this.enchantment;
    }
    
    /**
     * Get the {@link CEnchantment} attached to the {@link CEnchantments}
     * @return The {@link CEnchantment} attached to the {@link CEnchantments}
     */
    public CEnchantment getCEnchantment() {
        return this.enchantment.getEnchantment();
    }
    
    /**
     * Get the effects that will be placed on the entity that is hit.
     * @return The list of {@link PotionEffects} that will be on the player.
     */
    public List<PotionEffects> getPotionEffects() {
        return this.potionEffects;
    }
    
    /**
     * Get the damage amplifier that will be added to the damage dealt to the entity.
     * @return The damage added to the existing damage that the entity will receive.
     */
    public double getDamageAmplifier() {
        return this.damageAmplifier;
    }
    
    /**
     * Check if the enchantments level is going to be added to the amplifiers.
     * @return True if it does add to the amplifier and false if not.
     */
    public boolean isLevelAddedToAmplifier() {
        return this.isLevelAddedToAmplifier;
    }
    
    /**
     * Check if the enchantment is a potion effect that is added to the entity or if it effects the damage.
     * @return True if it adds potion effects and false if it adds damage.
     */
    public boolean isPotionEnchantment() {
        return this.isPotionEnchantment;
    }
}