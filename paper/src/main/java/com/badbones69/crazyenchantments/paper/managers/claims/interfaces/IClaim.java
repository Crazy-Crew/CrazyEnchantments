package com.badbones69.crazyenchantments.paper.managers.claims.interfaces;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface IClaim {

    /**
     * Checks if a player can break blocks.
     *
     * @param player the player
     * @param block the block
     * @return true or false
     */
    boolean canBreak(@NotNull final Player player, @NotNull final Block block);

    /**
     * Checks if PVP is allowed at a location.
     *
     * @param location the location
     * @return true or false
     */
    boolean canPVP(@NotNull final Location location);

    /**
     * Checks if explosions are allowed at a location.
     *
     * @param location the location
     * @return true or false
     */
    boolean canExplode(@NotNull final Location location);

    /**
     * Check if a player is in a territory.
     *
     * @param player the player
     * @param name the name of the faction/region
     * @return true or false
     */
    boolean inTerritory(@NotNull final Player player, @NotNull final String name);

    /**
     * Check if a player is in a territory.
     *
     * @param player the player
     * @return true or false
     */
    default boolean inTerritory(@NotNull final Player player) {
        return inTerritory(player, "");
    }

    /**
     * Checks if player is owner of the region/claim
     *
     * @param player the player
     * @return true or false
     */
    boolean isOwner(@NotNull final Player player);

    /**
     * Checks if the player is a member of the region/claim
     *
     * @param player the player
     * @return true or false
     */
    boolean isMember(@NotNull final Player player);
}