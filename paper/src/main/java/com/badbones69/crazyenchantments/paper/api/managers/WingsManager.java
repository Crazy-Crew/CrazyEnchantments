package com.badbones69.crazyenchantments.paper.api.managers;

import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WingsManager {

    private boolean isWingsEnabled;
    private boolean isCloudsEnabled;
    private boolean isEnemyCheckEnabled;
    private int enemyRadius;
    private final List<UUID> flyingPlayers = new ArrayList<>();
    private final List<String> whitelistWorlds = new ArrayList<>();
    private final List<String> blacklistWorlds = new ArrayList<>();
    private List<String> regions;
    private final List<String> limitlessFlightWorlds = new ArrayList<>();
    private boolean ownersCanFly;
    private boolean membersCanFly;
    private BukkitTask wingsTask;
    
    public void load() {
        this.isWingsEnabled = CEnchantments.WINGS.isActivated();
        FileConfiguration config = Files.CONFIG.getFile();
        String path = "Settings.EnchantmentOptions.Wings.";
        this.isCloudsEnabled = config.getBoolean(path + "Clouds");
        this.isEnemyCheckEnabled = config.getBoolean(path + "Enemy-Toggle");
        this.enemyRadius = config.getInt(path + "Distance", 10);
        this.whitelistWorlds.clear();
        config.getStringList(path + "Worlds.Whitelisted").forEach(world -> whitelistWorlds.add(world.toLowerCase()));
        this.blacklistWorlds.clear();
        config.getStringList(path + "Worlds.Blacklisted").forEach(world -> blacklistWorlds.add(world.toLowerCase()));
        this.limitlessFlightWorlds.clear();
        config.getStringList(path + "Worlds.Limitless-Flight-Worlds").forEach(world -> limitlessFlightWorlds.add(world.toLowerCase()));
        this.regions = config.getStringList(path + "Regions");
        this.ownersCanFly = config.getBoolean(path + "Owners-Can-Fly", true);
        this.membersCanFly = config.getBoolean(path + "Members-Can-Fly", true);
    }
    
    public boolean isWingsEnabled() {
        return this.isWingsEnabled;
    }
    
    public boolean isCloudsEnabled() {
        return this.isCloudsEnabled;
    }
    
    public boolean isEnemyCheckEnabled() {
        return this.isEnemyCheckEnabled;
    }
    
    public int getEnemyRadius() {
        return this.enemyRadius;
    }
    
    public String getBypassPermission() {
        return "crazyenchantments.bypass.wings";
    }
    
    public List<UUID> getFlyingPlayers() {
        return this.flyingPlayers;
    }
    
    public boolean isFlyingPlayer(Player player) {
        return this.flyingPlayers.contains(player.getUniqueId());
    }
    
    public void addFlyingPlayer(Player player) {
        if (!this.flyingPlayers.contains(player.getUniqueId())) this.flyingPlayers.add(player.getUniqueId());
    }
    
    public void removeFlyingPlayer(Player player) {
        this.flyingPlayers.remove(player.getUniqueId());
    }
    
    /**
     * Get the list of all worlds players with wings can fly limitless in.
     */
    public List<String> getLimitlessFlightWorlds() {
        return this.limitlessFlightWorlds;
    }
    
    /**
     * Check to see if a player is in a world with limitless flight.
     * @param player The player you wish to check.
     */
    public boolean inLimitlessFlightWorld(Player player) {
        return player != null && this.limitlessFlightWorlds.contains(player.getWorld().getName().toLowerCase());
    }
    
    /**
     * Get the list of all the whitelisted worlds for the Wings enchantment.
     */
    public List<String> getWhitelistedWorlds() {
        return this.whitelistWorlds;
    }
    
    /**
     * Check to see if a player is in a whitelisted world for the wings enchantment.
     * @param player The player you wish to check.
     */
    public boolean inWhitelistedWorld(Player player) {
        return player != null && this.whitelistWorlds.contains(player.getWorld().getName().toLowerCase());
    }
    
    /**
     * Get the list of all the blacklisted worlds for the Wings enchantment.
     */
    public List<String> getBlacklistedWorlds() {
        return this.blacklistWorlds;
    }
    
    /**
     * Check to see if a player is in a blacklisted world for the wings enchantment.
     * @param player The player you wish to check.
     */
    public boolean inBlacklistedWorld(Player player) {
        return player != null && this.blacklistWorlds.contains(player.getWorld().getName().toLowerCase());
    }
    
    public List<String> getRegions() {
        return this.regions;
    }
    
    public boolean canOwnersFly() {
        return this.ownersCanFly;
    }
    
    public boolean canMembersFly() {
        return this.membersCanFly;
    }
    
    public void setWingsTask(BukkitTask task) {
        endWingsTask();
        this.wingsTask = task;
    }
    
    public void endWingsTask() {
        if (this.wingsTask != null) this.wingsTask.cancel();
    }
}