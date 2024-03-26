package com.badbones69.crazyenchantments.paper.api.objects;

import org.bukkit.potion.PotionEffectType;

public record PotionEffects(PotionEffectType potionEffect, int duration, int amplifier) {
}