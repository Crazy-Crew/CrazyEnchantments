package com.badbones69.crazyenchantments.paper.support.interfaces.claims;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface WorldGuardVersion {
    
    boolean allowsPVP(@NotNull final Location loc);
    
    boolean allowsBreak(@NotNull final Location loc);
    
    boolean allowsExplosions(@NotNull final Location loc);
    
    boolean inRegion(@NotNull final String regionName, @NotNull final Location loc);
    
    boolean isMember(@NotNull final Player player);
    
    boolean isOwner(@NotNull final Player player);
    
}