package com.badbones69.crazyenchantments.paper.api.managers;

import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.ryderbelserion.crazyenchantments.utils.ConfigUtils;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WingsManager {

    private final List<String> limitlessFlightWorlds = new ArrayList<>();
    private final List<String> whitelistWorlds = new ArrayList<>();
    private final List<String> blacklistWorlds = new ArrayList<>();
    private final List<UUID> flyingPlayers = new ArrayList<>();

    private boolean isEnemyCheckEnabled;
    private boolean isCloudsEnabled;
    private ScheduledTask wingsTask;
    private boolean isWingsEnabled;
    private boolean membersCanFly;
    private boolean ownersCanFly;
    private List<String> regions;
    private int enemyRadius;
    
    public void load(final CommentedConfigurationNode config) {
        this.isWingsEnabled = CEnchantments.WINGS.isActivated();

        this.isCloudsEnabled = config.node("Settings", "EnchantmentOptions", "Wings", "Clouds").getBoolean(true);
        this.isEnemyCheckEnabled = config.node("Settings", "EnchantmentOptions", "Wings", "Enemy-Toggle").getBoolean(true);
        this.enemyRadius = config.node("Settings", "EnchantmentOptions", "Wings", "Distance").getInt(10);

        this.whitelistWorlds.clear();

        ConfigUtils.getStringList(config, "Settings", "EnchantmentOptions", "Wings", "Worlds", "Whitelist").forEach(world -> this.whitelistWorlds.add(world.toLowerCase()));

        this.blacklistWorlds.clear();

        ConfigUtils.getStringList(config, "Settings", "EnchantmentOptions", "Wings", "Worlds", "Blacklisted").forEach(world -> this.blacklistWorlds.add(world.toLowerCase()));

        this.limitlessFlightWorlds.clear();

        ConfigUtils.getStringList(config, "Settings", "EnchantmentOptions", "Wings", "Worlds", "Limitless-Flight-Worlds").forEach(world -> this.limitlessFlightWorlds.add(world.toLowerCase()));

        this.regions = ConfigUtils.getStringList(config, "Settings", "EnchantmentOptions", "Wings", "Regions");

        this.ownersCanFly = config.node("Settings", "EnchantmentOptions", "Wings", "Owners-Can-Fly").getBoolean(true);
        this.membersCanFly = config.node("Settings", "EnchantmentOptions", "Wings", "Members-Can-Fly").getBoolean(true);
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

    public List<UUID> getFlyingPlayers() {
        return this.flyingPlayers;
    }
    
    public boolean isFlyingPlayer(final Player player) {
        return this.flyingPlayers.contains(player.getUniqueId());
    }
    
    public void addFlyingPlayer(final Player player) {
        if (!this.flyingPlayers.contains(player.getUniqueId())) this.flyingPlayers.add(player.getUniqueId());
    }
    
    public void removeFlyingPlayer(final Player player) {
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
    public boolean inLimitlessFlightWorld(final Player player) {
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
    public boolean inWhitelistedWorld(final Player player) {
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
    public boolean inBlacklistedWorld(final Player player) {
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
    
    public void setWingsTask(final ScheduledTask task) {
        endWingsTask();

        this.wingsTask = task;
    }
    
    public void endWingsTask() {
        if (this.wingsTask != null) this.wingsTask.cancel();
    }
}