package me.badbones69.crazyenchantments.api.managers;

import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class WingsManager {
    
    public static final WingsManager instance = new WingsManager();
    private boolean isWingsEnabled;
    private boolean isCloudsEnabled;
    private boolean isEnemeyCheckEnabled;
    private int enemyRadius;
    private List<Player> flyingPlayers = new ArrayList<>();
    private List<String> whitelistWorlds = new ArrayList<>();
    private List<String> blacklistWorlds = new ArrayList<>();
    private List<String> regions;
    private List<String> limitlessFlightWorlds = new ArrayList<>();
    private boolean ownersCanFly;
    private boolean membersCanFly;
    private BukkitTask wingsTask;
    
    public static WingsManager getInstance() {
        return instance;
    }
    
    public void load() {
        isWingsEnabled = CEnchantments.WINGS.isActivated();
        FileConfiguration config = Files.CONFIG.getFile();
        String path = "Settings.EnchantmentOptions.Wings.";
        isCloudsEnabled = config.getBoolean(path + "Clouds");
        isEnemeyCheckEnabled = config.getBoolean(path + "Enemy-Toggle");
        enemyRadius = config.getInt(path + "Distance", 10);
        whitelistWorlds.clear();
        config.getStringList(path + "Worlds.Whitelisted").forEach(world -> whitelistWorlds.add(world.toLowerCase()));
        blacklistWorlds.clear();
        config.getStringList(path + "Worlds.Blacklisted").forEach(world -> blacklistWorlds.add(world.toLowerCase()));
        limitlessFlightWorlds.clear();
        config.getStringList(path + "Worlds.Limitless-Flight-Worlds").forEach(world -> limitlessFlightWorlds.add(world.toLowerCase()));
        regions = config.getStringList(path + "Regions");
        ownersCanFly = config.getBoolean(path + "Owners-Can-Fly", true);
        membersCanFly = config.getBoolean(path + "Members-Can-Fly", true);
    }
    
    public boolean isWingsEnabled() {
        return isWingsEnabled;
    }
    
    public boolean isCloudsEnabled() {
        return isCloudsEnabled;
    }
    
    public boolean isEnemeyCheckEnabled() {
        return isEnemeyCheckEnabled;
    }
    
    public int getEnemyRadius() {
        return enemyRadius;
    }
    
    public String getBypassPermission() {
        return "crazyenchantments.bypass.wings";
    }
    
    public List<Player> getFlyingPlayers() {
        return flyingPlayers;
    }
    
    public boolean isFlyingPlayer(Player player) {
        return flyingPlayers.contains(player);
    }
    
    public void addFlyingPlayer(Player player) {
        if (!flyingPlayers.contains(player)) {
            flyingPlayers.add(player);
        }
    }
    
    public void removeFlyingPlayer(Player player) {
        flyingPlayers.remove(player);
    }
    
    /**
     * Get the list of all worlds players with wings can fly limitless in.
     */
    public List<String> getLimitlessFlightWorlds() {
        return limitlessFlightWorlds;
    }
    
    /**
     * Check to see if a player is in a world with limitless flight.
     * @param player The player you wish to check.
     */
    public boolean inLimitlessFlightWorld(Player player) {
        return player != null && limitlessFlightWorlds.contains(player.getWorld().getName().toLowerCase());
    }
    
    /**
     * Get the list of all the whitelisted worlds for the Wings enchantment.
     */
    public List<String> getWhitelistedWorlds() {
        return whitelistWorlds;
    }
    
    /**
     * Check to see if a player is in a whitelisted world for the wings enchantment.
     * @param player The player you wish to check.
     */
    public boolean inWhitelistedWorld(Player player) {
        return player != null && whitelistWorlds.contains(player.getWorld().getName().toLowerCase());
    }
    
    /**
     * Get the list of all the blacklisted worlds for the Wings enchantment.
     */
    public List<String> getBlacklistedWorlds() {
        return blacklistWorlds;
    }
    
    /**
     * Check to see if a player is in a blacklisted world for the wings enchantment.
     * @param player The player you wish to check.
     */
    public boolean inBlacklistedWorld(Player player) {
        return player != null && blacklistWorlds.contains(player.getWorld().getName().toLowerCase());
    }
    
    public List<String> getRegions() {
        return regions;
    }
    
    public boolean canOwnersFly() {
        return ownersCanFly;
    }
    
    public boolean canMembersFly() {
        return membersCanFly;
    }
    
    public void setWingsTask(BukkitTask task) {
        endWingsTask();
        wingsTask = task;
    }
    
    public void endWingsTask() {
        if (wingsTask != null) {
            wingsTask.cancel();
        }
    }
    
}