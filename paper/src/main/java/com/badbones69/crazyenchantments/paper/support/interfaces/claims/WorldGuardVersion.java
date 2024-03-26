package com.badbones69.crazyenchantments.paper.support.interfaces.claims;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface WorldGuardVersion {

    boolean allowsPVP(Location loc);

    boolean allowsBreak(Location loc);

    boolean allowsExplosions(Location loc);

    boolean inRegion(String regionName, Location loc);

    boolean isMember(Player player);

    boolean isOwner(Player player);

}