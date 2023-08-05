package com.badbones69.crazyenchantments.paper.utilities;

import com.badbones69.crazyenchantments.paper.api.support.claims.WorldGuardSupport;
import com.badbones69.crazyenchantments.paper.api.support.interfaces.claims.WorldGuardVersion;

public class WorldGuardUtils {

    private WorldGuardVersion worldGuardVersion;

    public void init() {
        worldGuardVersion = new WorldGuardSupport();
    }

    /**
     * @return World Guard support class.
     */
    public WorldGuardVersion getWorldGuardSupport() {
        return worldGuardVersion;
    }
}