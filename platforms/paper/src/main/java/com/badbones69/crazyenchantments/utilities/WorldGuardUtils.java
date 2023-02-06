package com.badbones69.crazyenchantments.utilities;

import com.badbones69.crazyenchantments.api.PluginSupport;
import com.badbones69.crazyenchantments.api.support.claims.WorldGuardSupport;
import com.badbones69.crazyenchantments.api.support.interfaces.claims.WorldGuardVersion;

public class WorldGuardUtils {

    private WorldGuardVersion worldGuardVersion;

    public void init() {
        if (PluginSupport.SupportedPlugins.WORLDGUARD.isPluginLoaded() && PluginSupport.SupportedPlugins.WORLDEDIT.isPluginLoaded()) worldGuardVersion = new WorldGuardSupport();
    }

    /**
     * @return World Guard support class.
     */
    public WorldGuardVersion getWorldGuardSupport() {
        return worldGuardVersion;
    }
}