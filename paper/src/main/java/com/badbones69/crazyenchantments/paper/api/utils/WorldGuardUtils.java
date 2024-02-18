package com.badbones69.crazyenchantments.paper.api.utils;

import com.badbones69.crazyenchantments.paper.support.claims.WorldGuardSupport;
import com.badbones69.crazyenchantments.paper.support.interfaces.claims.WorldGuardVersion;

public class WorldGuardUtils {

    private WorldGuardVersion worldGuardVersion;

    public void init() {
        this.worldGuardVersion = new WorldGuardSupport();
    }

    /**
     * @return World Guard support class.
     */
    public WorldGuardVersion getWorldGuardSupport() {
        return this.worldGuardVersion;
    }
}