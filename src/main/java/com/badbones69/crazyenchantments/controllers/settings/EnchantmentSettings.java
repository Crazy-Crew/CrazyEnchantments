package com.badbones69.crazyenchantments.controllers.settings;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.objects.BlockProcessInfo;
import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.api.objects.TelepathyDrop;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EnchantmentSettings {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    // Cool-downs
    private final HashMap<UUID, Calendar> allyCoolDown = new HashMap<>();

    /**
     * Add a player to the map.
     * @param player - The player to add.
     * @param calendar - The calendar instance.
     */
    public void addAllyCooldown(Player player, Calendar calendar) {
        allyCoolDown.put(player.getUniqueId(), calendar);
    }

    /**
     * Remove a player from the map.
     * @param player - The player to remove.
     */
    public void removeAllyCooldown(Player player) {
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
        if (containsTimerPlayer(player)) return enchantTimer.get(player.getUniqueId());

        return null;
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

    private final Random random = new Random();

    public TelepathyDrop getTelepathyDrops(BlockProcessInfo processInfo) {
        ItemStack item = processInfo.getItem();
        Block block = processInfo.getBlock();
        List<CEnchantment> enchantments = crazyManager.getEnchantmentsOnItem(item);
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
                    xp = methods.percentPick(7, 3);
                    if (hasExperience && CEnchantments.EXPERIENCE.chanceSuccessful(item)) xp += methods.percentPick(7, 3) * crazyManager.getLevel(item, CEnchantments.EXPERIENCE);
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
        if (block.getType() == Material.COCOA) itemDrop.setMaterial(Material.COCOA_BEANS).setAmount(crazyManager.getNMSSupport().isFullyGrown(block) ? random.nextInt(2) + 2 : 1);

        if (itemDrop.getMaterial() == Material.WHEAT || itemDrop.getMaterial() == Material.BEETROOT_SEEDS) {
            itemDrop.setAmount(random.nextInt(3)); // Wheat and BeetRoots drops 0-3 seeds.
        } else if (itemDrop.getMaterial() == Material.POTATO || itemDrop.getMaterial() == Material.CARROT) {
            itemDrop.setAmount(random.nextInt(4) + 1); // Carrots and Potatoes drop 1-4 of them self's.
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
            case COAL_ORE, DIAMOND_ORE, EMERALD_ORE, LAPIS_ORE, REDSTONE_ORE -> true;
            default -> false;
        };
    }

    private boolean isOre(Block block) {
        return switch (block.getType()) {
            case COAL_ORE,
                    IRON_ORE,
                    GOLD_ORE,
                    DIAMOND_ORE,
                    EMERALD_ORE,
                    LAPIS_ORE,
                    REDSTONE_ORE,
                    NETHER_QUARTZ_ORE -> true;
            default -> false;
        };
    }

    private ItemStack getOreDrop(Block block) {
        ItemBuilder dropItem = new ItemBuilder();

        if (block.getType() == Material.NETHER_QUARTZ_ORE) {
            dropItem.setMaterial(Material.QUARTZ);
        } else {
            switch (block.getType()) {
                case COAL_ORE -> dropItem.setMaterial(Material.COAL);
                case IRON_ORE -> dropItem.setMaterial(Material.IRON_INGOT);
                case GOLD_ORE -> dropItem.setMaterial(Material.GOLD_INGOT);
                case DIAMOND_ORE -> dropItem.setMaterial(Material.DIAMOND);
                case EMERALD_ORE -> dropItem.setMaterial(Material.EMERALD);
                case LAPIS_ORE -> dropItem.setMaterial(Material.LAPIS_LAZULI);
                case REDSTONE_ORE -> dropItem.setMaterial(Material.REDSTONE);
                default -> dropItem.setMaterial(Material.AIR);
            }
        }

        return dropItem.build();
    }
}