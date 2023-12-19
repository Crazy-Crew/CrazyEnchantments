package com.badbones69.crazyenchantments.paper.controllers.settings;

import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class EnchantmentSettings {
    // Cool-downs
    private final HashMap<UUID, Calendar> allyCoolDown = new HashMap<>();

    /**
     * Add a player to the map.
     * @param player - The player to add.
     * @param calendar - The calendar instance.
     */
    public void addAllyCoolDown(Player player, Calendar calendar) {
        allyCoolDown.put(player.getUniqueId(), calendar);
    }

    /**
     * Remove a player from the map.
     * @param player - The player to remove.
     */
    public void removeAllyCoolDown(Player player) {
        allyCoolDown.remove(player.getUniqueId());
    }

    /**
     * Check if the map contains a player.
     * @param player - The player to check.
     * @return True if the player exists otherwise false.
     */
    public boolean containsAllyPlayer(Player player) {
        return allyCoolDown.containsKey(player.getUniqueId());
    }

    /**
     * @param player - The player to check.
     * @return Get the player from the map.
     */
    public Calendar getAllyPlayer(Player player) {
        return allyCoolDown.get(player.getUniqueId());
    }

    /**
     * Get the entire ally map.
     */
    public HashMap<UUID, Calendar> getAllyCoolDown() {
        return allyCoolDown;
    }

    // Timers
    private final HashMap<UUID, HashMap<CEnchantments, Calendar>> enchantTimer = new HashMap<>();

    /**
     * Add a player to the map.
     * @param player - The player to add.
     */
    public void addTimerPlayer(Player player, HashMap<CEnchantments, Calendar> enchantMap) {
        enchantTimer.put(player.getUniqueId(), enchantMap);
    }

    /**
     * Remove a player from the map.
     * @param player - The player to remove.
     */
    public void removeTimerPlayer(Player player) {
        enchantTimer.remove(player.getUniqueId());
    }

    /**
     * Check if the map contains a player.
     * @param player - The player to check.
     * @return True if the player exists otherwise false.
     */
    public boolean containsTimerPlayer(Player player) {
        return enchantTimer.containsKey(player.getUniqueId());
    }

    public HashMap<CEnchantments, Calendar> getTimerPlayer(Player player) {
        return enchantTimer.get(player.getUniqueId());
    }

    public HashMap<UUID, HashMap<CEnchantments, Calendar>> getEnchantTimer() {
        return enchantTimer;
    }

    // Fallen Players

    private final List<UUID> fallenPlayers = new ArrayList<>();

    public boolean containsFallenPlayer(Player player) {
        return fallenPlayers.contains(player.getUniqueId());
    }

    public void addFallenPlayer(Player player) {
        fallenPlayers.add(player.getUniqueId());
    }

    public void removeFallenPlayer(Player player) {
        fallenPlayers.remove(player.getUniqueId());
    }

    public List<UUID> getFallenPlayers() {
        return fallenPlayers;
    }

    // Hoes

    private final List<Material> harvesterCrops = Lists.newArrayList(Material.WHEAT, Material.CARROTS, Material.BEETROOTS, Material.POTATOES, Material.NETHER_WART, Material.COCOA);

    private final List<Material> seedlings = Lists.newArrayList(Material.WHEAT, Material.CARROTS, Material.BEETROOTS, Material.POTATOES, Material.NETHER_WART, Material.COCOA,
            Material.MELON_STEM, Material.CRIMSON_STEM, Material.PUMPKIN_STEM, Material.WARPED_STEM);

    private final HashMap<Material, Material> planterSeeds = new HashMap<>();

    public List<Material> getHarvesterCrops() {
        return harvesterCrops;
    }

    public List<Material> getSeedlings() {
        return seedlings;
    }

    public HashMap<Material, Material> getPlanterSeeds() {
        return planterSeeds;
    }

    public Material getPlanterSeed(Material material) {
        planterSeeds.put(Material.WHEAT_SEEDS, Material.WHEAT);
        planterSeeds.put(Material.BEETROOT_SEEDS, Material.BEETROOTS);
        planterSeeds.put(Material.POTATO, Material.POTATOES);
        planterSeeds.put(Material.CARROT, Material.CARROTS);
        planterSeeds.put(Material.NETHER_WART, Material.NETHER_WART);
        planterSeeds.put(Material.MELON_SEEDS, Material.MELON_STEM);
        planterSeeds.put(Material.PUMPKIN_SEEDS, Material.PUMPKIN_STEM);

        return material != null ? planterSeeds.get(material) : null;
    }

    // Telepathy

}