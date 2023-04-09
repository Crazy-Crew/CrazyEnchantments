package com.badbones69.crazyenchantments.api.objects;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.enums.ArmorType;
import com.badbones69.crazyenchantments.api.events.ArmorEquipEvent;
import com.badbones69.crazyenchantments.api.events.ArmorEquipEvent.EquipMethod;
import com.badbones69.crazyenchantments.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.api.objects.gkitz.GkitCoolDown;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CEPlayer {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Methods methods = plugin.getStarter().getMethods();
    
    private int souls;
    private final Player player;
    private boolean soulsActive;
    private final List<GkitCoolDown> gkitCoolDowns;
    private Double rageMultiplier;
    private boolean hasRage;
    private int rageLevel;
    private BukkitTask rageTask;
    
    /**
     * Used to make a new CEPlayer.
     * @param player The player.
     * @param souls How many souls they have.
     * @param soulsActive If the soul uses is active.
     * @param gkitCoolDowns The cool-downs the player has.
     */
    public CEPlayer(Player player, int souls, boolean soulsActive, List<GkitCoolDown> gkitCoolDowns) {
        this.souls = souls;
        this.player = player;
        this.gkitCoolDowns = gkitCoolDowns;
        this.soulsActive = soulsActive;
        this.hasRage = false;
        this.rageLevel = 0;
        this.rageMultiplier = 0.0;
        this.rageTask = null;
    }
    
    /**
     * Get the player from the CEPlayer.
     * @return Player from the CEPlayer.
     */
    public Player getPlayer() {
        return this.player;
    }
    
    /**
     * Get how many souls the player has.
     * @return The amount of souls the player has.
     */
    public int getSouls() {
        return this.souls;
    }
    
    /**
     * Set the amount of souls the player has.
     * @param souls The new amount of souls the player has.
     */
    public void setSouls(int souls) {
        this.souls = souls;
    }
    
    /**
     * Add 1 soul to the player.
     */
    public void addSoul() {
        this.souls++;
    }
    
    /**
     * Add extra souls to the player.
     * @param souls Amount of souls you want to add.
     */
    public void addSouls(int souls) {
        this.souls += souls;
    }
    
    /**
     * Take 1 soul from the player.
     */
    public void useSoul() {
        this.souls--;
    }
    
    /**
     * Take souls from the player.
     * @param souls Amount of souls you are taking.
     */
    public void useSouls(int souls) {
        this.souls -= souls;

        if (this.souls < 0) this.souls = 0;
    }
    
    /**
     * Find out if the players souls are active.
     * @return True if active and false if not.
     */
    public boolean isSoulsActive() {
        return soulsActive;
    }
    
    /**
     * Set if the players souls are active.
     * @param soulsActive True if you want to activate them and false if not.
     */
    public void setSoulsActive(boolean soulsActive) {
        this.soulsActive = soulsActive;
    }
    
    /**
     * Give a player a gkit.
     * @param kit The gkit you wish to give them.
     */
    public void giveGKit(GKitz kit) {
        for (ItemStack item : kit.getKitItems()) {
            if (kit.canAutoEquip()) {

                switch (item.getType().toString().toLowerCase()) {
                    case "helmet" -> {
                        if (player.getEquipment().getHelmet() != null) break;
                        player.getEquipment().setHelmet(item);
                        continue;
                    }
                    case "Chestplate" -> {
                        if (player.getEquipment().getChestplate() != null) break;
                        player.getEquipment().setChestplate(item);
                        continue;
                    }
                    case "leggings" -> {
                        if (player.getEquipment().getLeggings() != null) break;
                        player.getEquipment().setLeggings(item);
                        continue;
                    }
                    case "boots" -> {
                        if (player.getEquipment().getBoots() != null) break;
                        player.getEquipment().setBoots(item);
                        continue;
                    }
                }

            }

            if (methods.isInventoryFull(player)) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            } else {
                player.getInventory().addItem(item);
            }

        }

        for (String cmd : kit.getCommands()) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd
            .replace("%Player%", player.getName()).replace("%player%", player.getName()));
        }
    }
    
    /**
     * If the player has permission to use the gkit.
     * @param kit The gkit you are checking.
     * @return True if they can use it and false if they can't.
     */
    public boolean hasGkitPermission(GKitz kit) {
        return player.hasPermission("crazyenchantments.bypass.gkitz") || player.hasPermission("crazyenchantments.gkitz." + kit.getName().toLowerCase());
    }
    
    /**
     * If the player can use the gkit. Checks their cool-downs and permissions.
     * @param kit The gkit you want to check.
     * @return True if they don't have a cool-down, and they have permission.
     */
    public boolean canUseGKit(GKitz kit) {
        if (player.hasPermission("crazyenchantments.bypass.gkitz")) {
            return true;
        } else {
            if (player.hasPermission("crazyenchantments.gkitz." + kit.getName().toLowerCase())) {
                for (GkitCoolDown gkitCooldown : getCoolDowns()) {
                    if (gkitCooldown.getGKitz() == kit) return gkitCooldown.isCoolDownOver();
                }
            } else {
                return false;
            }
        }

        return true;
    }
    
    /**
     * Get all the cool-downs the player has.
     * @return The cool-downs the player has.
     */
    public List<GkitCoolDown> getCoolDowns() {
        return this.gkitCoolDowns;
    }
    
    /**
     * Get a cool-down of a gkit.
     * @param kit The gkit you are checking.
     * @return The cool-down object the player has.
     */
    public GkitCoolDown getCoolDown(GKitz kit) {
        for (GkitCoolDown gkitCoolDown : gkitCoolDowns) {
            if (gkitCoolDown.getGKitz() == kit) return gkitCoolDown;
        }

        return null;
    }
    
    /**
     * Add a cool-down to a player.
     * @param gkitCoolDown The cool-down you are adding.
     */
    public void addCoolDown(GkitCoolDown gkitCoolDown) {
        List<GkitCoolDown> playerGkitCoolDowns = new ArrayList<>();

        for (GkitCoolDown c : getCoolDowns()) {
            if (c.getGKitz().getName().equalsIgnoreCase(gkitCoolDown.getGKitz().getName())) playerGkitCoolDowns.add(c);
        }

        this.gkitCoolDowns.removeAll(playerGkitCoolDowns);
        this.gkitCoolDowns.add(gkitCoolDown);
    }
    
    /**
     * Add a cool-down of a gkit to a player.
     * @param kit The gkit you want to get the cool-down for.
     */
    public void addCoolDown(GKitz kit) {
        Calendar coolDown = Calendar.getInstance();

        for (String i : kit.getCooldown().split(" ")) {

            if (i.contains("D") || i.contains("d")) coolDown.add(Calendar.DATE, Integer.parseInt(i.replace("D", "").replace("d", "")));

            if (i.contains("H") || i.contains("h")) coolDown.add(Calendar.HOUR, Integer.parseInt(i.replace("H", "").replace("h", "")));

            if (i.contains("M") || i.contains("m")) coolDown.add(Calendar.MINUTE, Integer.parseInt(i.replace("M", "").replace("m", "")));

            if (i.contains("S") || i.contains("s")) coolDown.add(Calendar.SECOND, Integer.parseInt(i.replace("S", "").replace("s", "")));
        }

        addCoolDown(new GkitCoolDown(kit, coolDown));
    }
    
    /**
     * Remove a cool-down from a player.
     * @param gkitCoolDown The cool-down you want to remove.
     */
    public void removeCoolDown(GkitCoolDown gkitCoolDown) {
        this.gkitCoolDowns.remove(gkitCoolDown);
    }
    
    /**
     * Remove a cool-down from a player.
     * @param kit The gkit cool-down you want to remove.
     */
    public void removeCoolDown(GKitz kit) {
        List<GkitCoolDown> playerGkitCoolDowns = new ArrayList<>();

        for (GkitCoolDown gkitCoolDown : getCoolDowns()) {
            if (gkitCoolDown.getGKitz().getName().equalsIgnoreCase(kit.getName())) playerGkitCoolDowns.add(gkitCoolDown);
        }

        this.gkitCoolDowns.removeAll(playerGkitCoolDowns);
    }
    
    /**
     * Get the player's rage damage multiplier.
     */
    public Double getRageMultiplier() {
        return rageMultiplier;
    }
    
    /**
     * Set the player's rage damage multiplier.
     * @param rageMultiplier The player's new rage damage multiplier.
     */
    public void setRageMultiplier(Double rageMultiplier) {
        this.rageMultiplier = rageMultiplier;
    }
    
    /**
     * Check if the player is in rage.
     */
    public boolean hasRage() {
        return hasRage;
    }
    
    /**
     * Toggle on/off the player's rage.
     * @param hasRage If the player has rage.
     */
    public void setRage(boolean hasRage) {
        this.hasRage = hasRage;
    }
    
    /**
     * Get the level of rage the player is in.
     */
    public int getRageLevel() {
        return rageLevel;
    }
    
    /**
     * Set the level of rage the player is in.
     * @param rageLevel The player's new rage level.
     */
    public void setRageLevel(int rageLevel) {
        this.rageLevel = rageLevel;
    }
    
    /**
     * Get the cooldown task the player's rage has till they calm down.
     */
    public BukkitTask getRageTask() {
        return rageTask;
    }
    
    /**
     * Set the new cooldown task for the player's rage.
     * @param rageTask The new cooldown task for the player.
     */
    public void setRageTask(BukkitTask rageTask) {
        this.rageTask = rageTask;
    }
}