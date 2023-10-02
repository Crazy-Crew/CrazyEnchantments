package com.badbones69.crazyenchantments.paper.controllers.settings;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.objects.BlockProcessInfo;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.objects.TelepathyDrop;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EnchantmentSettings {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Starter starter = this.plugin.getStarter();

    private final Methods methods = this.starter.getMethods();

    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    // Cool-downs
    private final HashMap<UUID, Calendar> allyCoolDown = new HashMap<>();

    /**
     * Add a player to the map.
     * @param player - The player to add.
     * @param calendar - The calendar instance.
     */
    public void addAllyCoolDown(Player player, Calendar calendar) {
        this.allyCoolDown.put(player.getUniqueId(), calendar);
    }

    /**
     * Remove a player from the map.
     * @param player - The player to remove.
     */
    public void removeAllyCoolDown(Player player) {
        this.allyCoolDown.remove(player.getUniqueId());
    }

    /**
     * Check if the map contains a player.
     * @param player - The player to check.
     * @return True if the player exists otherwise false.
     */
    public boolean containsAllyPlayer(Player player) {
        return this.allyCoolDown.containsKey(player.getUniqueId());
    }

    /**
     * @param player - The player to check.
     * @return Get the player from the map.
     */
    public Calendar getAllyPlayer(Player player) {
        return this.allyCoolDown.get(player.getUniqueId());
    }

    /**
     * Get the entire ally map.
     */
    public HashMap<UUID, Calendar> getAllyCoolDown() {
        return this.allyCoolDown;
    }

    // Timers
    private final HashMap<UUID, HashMap<CEnchantments, Calendar>> enchantTimer = new HashMap<>();

    /**
     * Add a player to the map.
     * @param player - The player to add.
     */
    public void addTimerPlayer(Player player, HashMap<CEnchantments, Calendar> enchantMap) {
        this.enchantTimer.put(player.getUniqueId(), enchantMap);
    }

    /**
     * Remove a player from the map.
     * @param player - The player to remove.
     */
    public void removeTimerPlayer(Player player) {
        this.enchantTimer.remove(player.getUniqueId());
    }

    /**
     * Check if the map contains a player.
     * @param player - The player to check.
     * @return True if the player exists otherwise false.
     */
    public boolean containsTimerPlayer(Player player) {
        return this.enchantTimer.containsKey(player.getUniqueId());
    }

    public HashMap<CEnchantments, Calendar> getTimerPlayer(Player player) {
        if (containsTimerPlayer(player)) return this.enchantTimer.get(player.getUniqueId());

        return null;
    }

    public HashMap<UUID, HashMap<CEnchantments, Calendar>> getEnchantTimer() {
        return this.enchantTimer;
    }

    // Fallen Players

    private final List<UUID> fallenPlayers = new ArrayList<>();

    public boolean containsFallenPlayer(Player player) {
        return this.fallenPlayers.contains(player.getUniqueId());
    }

    public void addFallenPlayer(Player player) {
        this.fallenPlayers.add(player.getUniqueId());
    }

    public void removeFallenPlayer(Player player) {
        this.fallenPlayers.remove(player.getUniqueId());
    }

    public List<UUID> getFallenPlayers() {
        return this.fallenPlayers;
    }

    // Hoes

    private final List<Material> harvesterCrops = Lists.newArrayList(Material.WHEAT, Material.CARROTS, Material.BEETROOTS, Material.POTATOES, Material.NETHER_WART, Material.COCOA);

    private final List<Material> seedlings = Lists.newArrayList(Material.WHEAT, Material.CARROTS, Material.BEETROOTS, Material.POTATOES, Material.NETHER_WART, Material.COCOA,
            Material.MELON_STEM, Material.CRIMSON_STEM, Material.PUMPKIN_STEM, Material.WARPED_STEM);

    private final HashMap<Material, Material> planterSeeds = new HashMap<>();

    public List<Material> getHarvesterCrops() {
        return this.harvesterCrops;
    }

    public List<Material> getSeedlings() {
        return this.seedlings;
    }

    public HashMap<Material, Material> getPlanterSeeds() {
        return this.planterSeeds;
    }

    public Material getPlanterSeed(Material material) {
        this.planterSeeds.put(Material.WHEAT_SEEDS, Material.WHEAT);
        this.planterSeeds.put(Material.BEETROOT_SEEDS, Material.BEETROOTS);
        this.planterSeeds.put(Material.POTATO, Material.POTATOES);
        this.planterSeeds.put(Material.CARROT, Material.CARROTS);
        this.planterSeeds.put(Material.NETHER_WART, Material.NETHER_WART);
        this.planterSeeds.put(Material.MELON_SEEDS, Material.MELON_STEM);
        this.planterSeeds.put(Material.PUMPKIN_SEEDS, Material.PUMPKIN_STEM);

        return material != null ? this.planterSeeds.get(material) : null;
    }

    // Telepathy
    public TelepathyDrop getTelepathyDrops(BlockProcessInfo processInfo) {
        ItemStack item = processInfo.getItem();
        Block block = processInfo.getBlock();
        List<CEnchantment> enchantments = this.enchantmentBookSettings.getEnchantmentsOnItem(item);
        List<Block> sugarCaneBlocks = new ArrayList<>();
        boolean isOre = isOre(block);
        boolean hasSilkTouch = item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);
        boolean hasFurnace = enchantments.contains(CEnchantments.FURNACE.getEnchantment());
        boolean hasAutoSmelt = enchantments.contains(CEnchantments.AUTOSMELT.getEnchantment());
        boolean hasExperience = enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment());
        ItemBuilder itemDrop = null;
        int xp = 0;

        for (ItemStack drop : processInfo.getDrops()) {

            // Amount is set to 0 as it adds to the drop amount and so it would add 1 to many.

            if (itemDrop == null) itemDrop = new ItemBuilder().setMaterial(drop.getType()).setAmount(0);

            if (!hasSilkTouch) {
                if (hasFurnace && isOre) {
                    itemDrop = ItemBuilder.convertItemStack(getOreDrop(block)).setAmount(0);
                } else if (hasAutoSmelt && isOre && CEnchantments.AUTOSMELT.chanceSuccessful(item)) {
                    itemDrop = ItemBuilder.convertItemStack(getOreDrop(block)).setAmount(crazyManager.getLevel(item, CEnchantments.AUTOSMELT));
                }

                if (hasOreXP(block)) {
                    xp = this.methods.percentPick(7, 3);
                    if (hasExperience && CEnchantments.EXPERIENCE.chanceSuccessful(item)) xp += this.methods.percentPick(7, 3) * this.crazyManager.getLevel(item, CEnchantments.EXPERIENCE);
                }
            }

            if (block.getType() == Material.SUGAR_CANE) {
                sugarCaneBlocks = getSugarCaneBlocks(block);
                drop.setAmount(sugarCaneBlocks.size());
            }

            itemDrop.addAmount(drop.getAmount());
        }

        if (itemDrop == null) {
            // In case the drop is still null as no drops were found.
            itemDrop = new ItemBuilder().setMaterial(block.getType());
        }

        // Coco drops 2-3 beans.
        if (block.getType() == Material.COCOA) itemDrop.setMaterial(Material.COCOA_BEANS).setAmount(this.crazyManager.getNMSSupport().isFullyGrown(block) ? new Random().nextInt(2) + 2 : 1);

        if (itemDrop.getMaterial() == Material.WHEAT || itemDrop.getMaterial() == Material.BEETROOT_SEEDS) {
            itemDrop.setAmount(new Random().nextInt(3)); // Wheat and BeetRoots drops 0-3 seeds.
        } else if (itemDrop.getMaterial() == Material.POTATO || itemDrop.getMaterial() == Material.CARROT) {
            itemDrop.setAmount(new Random().nextInt(4) + 1); // Carrots and Potatoes drop 1-4 of them self's.
        }

        return new TelepathyDrop(itemDrop.build(), xp, sugarCaneBlocks);
    }

    private List<Block> getSugarCaneBlocks(Block block) {
        List<Block> sugarCaneBlocks = new ArrayList<>();
        Block cane = block;

        while (cane.getType() == Material.SUGAR_CANE) {
            sugarCaneBlocks.add(cane);
            cane = cane.getLocation().add(0, 1, 0).getBlock();
        }

        Collections.reverse(sugarCaneBlocks);
        return sugarCaneBlocks;
    }

    private boolean hasOreXP(Block block) {
        return switch (block.getType()) {
            case COAL_ORE, DEEPSLATE_COAL_ORE,
                 DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE,
                 EMERALD_ORE, DEEPSLATE_EMERALD_ORE,
                 LAPIS_ORE, DEEPSLATE_LAPIS_ORE,
                 REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE,
                 NETHER_QUARTZ_ORE -> true;
            default -> false;
        };
    }

    private boolean isOre(Block block) {
        return switch (block.getType()) {
            case COAL_ORE, DEEPSLATE_COAL_ORE,
                 COPPER_ORE, DEEPSLATE_COPPER_ORE,
                 DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE,
                 EMERALD_ORE, DEEPSLATE_EMERALD_ORE,
                 GOLD_ORE, DEEPSLATE_GOLD_ORE,
                 IRON_ORE, DEEPSLATE_IRON_ORE,
                 LAPIS_ORE, DEEPSLATE_LAPIS_ORE,
                 REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE,
                 NETHER_GOLD_ORE,
                 NETHER_QUARTZ_ORE -> true;
            default -> false;
        };
    }

    private ItemStack getOreDrop(Block block) {
        Material material;

        switch (block.getType()) {
            case COAL_ORE, DEEPSLATE_COAL_ORE -> material = Material.COAL;
            case COPPER_ORE, DEEPSLATE_COPPER_ORE -> material = Material.COPPER_INGOT;
            case IRON_ORE, DEEPSLATE_IRON_ORE -> material = Material.IRON_INGOT;
            case GOLD_ORE, DEEPSLATE_GOLD_ORE -> material = Material.GOLD_INGOT;
            case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE -> material = Material.DIAMOND;
            case EMERALD_ORE, DEEPSLATE_EMERALD_ORE -> material = Material.EMERALD;
            case LAPIS_ORE, DEEPSLATE_LAPIS_ORE -> material = Material.LAPIS_LAZULI;
            case REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE -> material = Material.REDSTONE;
            case NETHER_GOLD_ORE -> material = Material.GOLD_NUGGET;
            case NETHER_QUARTZ_ORE -> material = Material.QUARTZ;
            default -> material = Material.AIR;
        }

        return new ItemStack(material);
    }
}