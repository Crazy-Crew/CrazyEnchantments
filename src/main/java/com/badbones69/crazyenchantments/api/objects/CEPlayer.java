package com.badbones69.crazyenchantments.api.objects;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.enums.ArmorType;
import com.badbones69.crazyenchantments.api.events.ArmorEquipEvent;
import com.badbones69.crazyenchantments.api.events.ArmorEquipEvent.EquipMethod;
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
    private final List<Cooldown> cooldowns;
    private Double rageMultiplier;
    private boolean hasRage;
    private int rageLevel;
    private BukkitTask rageTask;
    
    /**
     * Used to make a new CEPlayer.
     * @param player The player.
     * @param souls How many souls they have.
     * @param soulsActive If the soul uses is active.
     * @param cooldowns The cooldowns the player has.
     */
    public CEPlayer(Player player, int souls, boolean soulsActive, List<Cooldown> cooldowns) {
        this.souls = souls;
        this.player = player;
        this.cooldowns = cooldowns;
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
                if (item.getType().toString().toLowerCase().contains("helmet")) {

                    if (player.getEquipment().getHelmet() == null || player.getEquipment().getHelmet().getType() == Material.AIR) {
                        ArmorEquipEvent event = new ArmorEquipEvent(player, EquipMethod.DRAG, ArmorType.HELMET, new ItemStack(Material.AIR), item);
                        plugin.getServer().getPluginManager().callEvent(event);

                        if (!event.isCancelled()) player.getEquipment().setHelmet(item);

                        continue;
                    }

                } else if (item.getType().toString().toLowerCase().contains("chestplate")) {

                    if (player.getEquipment().getChestplate() == null || player.getEquipment().getChestplate().getType() == Material.AIR) {
                        ArmorEquipEvent event = new ArmorEquipEvent(player, EquipMethod.DRAG, ArmorType.CHESTPLATE, new ItemStack(Material.AIR), item);
                        plugin.getServer().getPluginManager().callEvent(event);

                        if (!event.isCancelled()) player.getEquipment().setChestplate(item);

                        continue;
                    }
                } else if (item.getType().toString().toLowerCase().contains("leggings")) {

                    if (player.getEquipment().getLeggings() == null || player.getEquipment().getLeggings().getType() == Material.AIR) {
                        ArmorEquipEvent event = new ArmorEquipEvent(player, EquipMethod.DRAG, ArmorType.LEGGINGS, new ItemStack(Material.AIR), item);
                        plugin.getServer().getPluginManager().callEvent(event);

                        if (!event.isCancelled()) player.getEquipment().setLeggings(item);

                        continue;
                    }
                } else if (item.getType().toString().toLowerCase().contains("boots") && (player.getEquipment().getBoots() == null || player.getEquipment().getBoots().getType() == Material.AIR)) {

                    ArmorEquipEvent event = new ArmorEquipEvent(player, EquipMethod.DRAG, ArmorType.BOOTS, new ItemStack(Material.AIR), item);
                    plugin.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) player.getEquipment().setBoots(item);

                    continue;
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
     * If the player can use the gkit. Checks their cooldowns and permissions.
     * @param kit The gkit you want to check.
     * @return True if they don't have a cooldown and they have permission.
     */
    public boolean canUseGKit(GKitz kit) {
        if (player.hasPermission("crazyenchantments.bypass.gkitz")) {
            return true;
        } else {
            if (player.hasPermission("crazyenchantments.gkitz." + kit.getName().toLowerCase())) {
                for (Cooldown cooldown : getCooldowns()) {
                    if (cooldown.getGKitz() == kit) return cooldown.isCooldownOver();
                }
            } else {
                return false;
            }
        }

        return true;
    }
    
    /**
     * Get all the cooldowns the player has.
     * @return The cooldowns the player has.
     */
    public List<Cooldown> getCooldowns() {
        return this.cooldowns;
    }
    
    /**
     * Get a cooldown of a gkit.
     * @param kit The gkit you are checking.
     * @return The cooldown object the player has.
     */
    public Cooldown getCooldown(GKitz kit) {
        for (Cooldown cooldown : cooldowns) {
            if (cooldown.getGKitz() == kit) return cooldown;
        }

        return null;
    }
    
    /**
     * Add a cooldown to a player.
     * @param cooldown The cooldown you are adding.
     */
    public void addCooldown(Cooldown cooldown) {
        List<Cooldown> playerCooldowns = new ArrayList<>();

        for (Cooldown c : getCooldowns()) {
            if (c.getGKitz().getName().equalsIgnoreCase(cooldown.getGKitz().getName())) playerCooldowns.add(c);
        }

        this.cooldowns.removeAll(playerCooldowns);
        this.cooldowns.add(cooldown);
    }
    
    /**
     * Add a cooldown of a gkit to a player.
     * @param kit The gkit you want to get the cooldown for.
     */
    public void addCooldown(GKitz kit) {
        Calendar cooldown = Calendar.getInstance();

        for (String i : kit.getCooldown().split(" ")) {

            if (i.contains("D") || i.contains("d")) cooldown.add(Calendar.DATE, Integer.parseInt(i.replace("D", "").replace("d", "")));

            if (i.contains("H") || i.contains("h")) cooldown.add(Calendar.HOUR, Integer.parseInt(i.replace("H", "").replace("h", "")));

            if (i.contains("M") || i.contains("m")) cooldown.add(Calendar.MINUTE, Integer.parseInt(i.replace("M", "").replace("m", "")));

            if (i.contains("S") || i.contains("s")) cooldown.add(Calendar.SECOND, Integer.parseInt(i.replace("S", "").replace("s", "")));
        }

        addCooldown(new Cooldown(kit, cooldown));
    }
    
    /**
     * Remove a cooldown from a player.
     * @param cooldown The cooldown you want to remove.
     */
    public void removeCooldown(Cooldown cooldown) {
        this.cooldowns.remove(cooldown);
    }
    
    /**
     * Remove a cooldown from a player.
     * @param kit The gkit cooldown you want to remove.
     */
    public void removeCooldown(GKitz kit) {
        List<Cooldown> playerCooldowns = new ArrayList<>();

        for (Cooldown cooldown : getCooldowns()) {
            if (cooldown.getGKitz().getName().equalsIgnoreCase(kit.getName())) playerCooldowns.add(cooldown);
        }

        this.cooldowns.removeAll(playerCooldowns);
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