package com.badbones69.crazyenchantments.api.objects;

import org.bukkit.potion.PotionEffectType;

public record PotionEffects(PotionEffectType potionEffect, int duration, int amplifier) {}