package com.badbones69.crazyenchantments.enchantments;

import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Hoes implements Listener {
    
    private static List<Material> harvesterCrops;
    private final CrazyManager crazyManager = CrazyManager.getInstance();
    private List<Material> seedlings;
    private final Random random = new Random();
    private final Material soilBlock = Material.FARMLAND;
    private final Material grassBlock = Material.GRASS_BLOCK;
    private HashMap<Material, Material> planterSeeds;
    private HashMap<UUID, HashMap<Block, BlockFace>> blocks = new HashMap<>();
    
    /**
     * Only has crop blocks
     */
    @SuppressWarnings("squid:S1192")
    public static List<Material> getHarvesterCrops() {
        if (harvesterCrops == null) {
            harvesterCrops = new ArrayList<>();
            harvesterCrops.addAll(Arrays.asList(Material.WHEAT, Material.matchMaterial("CARROTS"), Material.matchMaterial("BEETROOTS"), Material.matchMaterial("POTATOES"), Material.matchMaterial("NETHER_WART")));
            harvesterCrops.add(Material.COCOA);
        }

        return harvesterCrops;
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (e.getHand() != EquipmentSlot.HAND) {return;}

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack hoe = Methods.getItemInHand(player);
            Block block = e.getClickedBlock();
            List<CEnchantment> enchantments = crazyManager.getEnchantmentsOnItem(hoe);

            // Crop is not fully grown
            if (CEnchantments.GREENTHUMB.isActivated() && enchantments.contains(CEnchantments.GREENTHUMB.getEnchantment()) &&
            getSeedlings().contains(block.getType()) && !crazyManager.getNMSSupport().isFullyGrown(block)) {
                fullyGrowPlant(hoe, block, player);

                if (player.getGameMode() != GameMode.CREATIVE) { // Take durability from players not in Creative
                    Methods.removeDurability(hoe, player);
                }
            }

            if (block.getType() == grassBlock || block.getType() == Material.DIRT || block.getType() == Material.SOUL_SAND || block.getType() == soilBlock) {
                boolean hasGreenThumb = CEnchantments.GREENTHUMB.isActivated() && enchantments.contains(CEnchantments.GREENTHUMB.getEnchantment());

                if (enchantments.contains(CEnchantments.TILLER.getEnchantment())) {
                    for (Block soil : getSoil(player, block)) {

                        if (soil.getType() != soilBlock && soil.getType() != Material.SOUL_SAND) {
                            soil.setType(soilBlock);
                        }

                        if (soil.getType() != Material.SOUL_SAND) {
                            for (Block water : getAreaBlocks(soil, 4)) {
                                if (water.getType() == Material.WATER) {
                                    crazyManager.getNMSSupport().hydrateSoil(soil);
                                    break;
                                }
                            }
                        }

                        if (enchantments.contains(CEnchantments.PLANTER.getEnchantment())) {
                            plantSeedSuccess(hoe, soil, player, hasGreenThumb);
                        }

                        // Take durability from the hoe for each block set to a soil.
                        if (player.getGameMode() != GameMode.CREATIVE) { // Take durability from players not in Creative
                            Methods.removeDurability(hoe, player);
                        }
                    }
                }

                // Take durability from players not in Creative
                // Checking else to make sure the item does have Tiller.
                if (player.getGameMode() != GameMode.CREATIVE && CEnchantments.PLANTER.isActivated() && enchantments.contains(CEnchantments.PLANTER.getEnchantment()) &&
                !enchantments.contains(CEnchantments.TILLER.getEnchantment()) && plantSeedSuccess(hoe, block, player, hasGreenThumb)) {
                    Methods.removeDurability(hoe, player);
                }
            }
        } else if (e.getAction() == Action.LEFT_CLICK_BLOCK && CEnchantments.HARVESTER.isActivated() && crazyManager.hasEnchantment(Methods.getItemInHand(player), CEnchantments.HARVESTER)) {
            HashMap<Block, BlockFace> blockFace = new HashMap<>();
            blockFace.put(e.getClickedBlock(), e.getBlockFace());
            blocks.put(player.getUniqueId(), blockFace);
        }
    }
    
    @SuppressWarnings("squid:S1192")
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!e.isCancelled() && !crazyManager.isIgnoredEvent(e)) {
            Player player = e.getPlayer();
            Block plant = e.getBlock();

            if (getHarvesterCrops().contains(plant.getType())) {
                ItemStack hoe = Methods.getItemInHand(player);
                List<CEnchantment> enchantments = crazyManager.getEnchantmentsOnItem(hoe);

                if (blocks.containsKey(player.getUniqueId()) && !enchantments.isEmpty() && CEnchantments.HARVESTER.isActivated() && enchantments.contains(CEnchantments.HARVESTER.getEnchantment())) {
                    BlockFace blockFace = blocks.get(player.getUniqueId()).get(plant);
                    blocks.remove(player.getUniqueId());

                    if (crazyManager.getNMSSupport().isFullyGrown(plant)) {
                        boolean hasTelepathy = enchantments.contains(CEnchantments.TELEPATHY.getEnchantment());

                        for (Block crop : getAreaCrops(player, plant, blockFace)) {
                            if (hasTelepathy) {
                                List<ItemStack> droppedItems = new ArrayList<>();

                                if (crop.getType() == Material.COCOA) {
                                    droppedItems.add(new ItemBuilder().setMaterial(Material.COCOA_BEANS).setAmount(random.nextInt(2) + 2).build()); // Coco drops 2-3 beans.
                                } else if (crop.getType() == Material.WHEAT) {
                                    droppedItems.add(new ItemBuilder().setMaterial(Material.WHEAT).build());
                                    droppedItems.add(new ItemBuilder().setMaterial(Material.WHEAT_SEEDS).setAmount(random.nextInt(3)).build()); // Wheat drops 0-3 seeds.
                                } else if (crop.getType() == Material.BEETROOTS) {
                                    droppedItems.add(new ItemBuilder().setMaterial(Material.BEETROOT).build());
                                    droppedItems.add(new ItemBuilder().setMaterial(Material.BEETROOT_SEEDS).setAmount(random.nextInt(3)).build()); // BeetRoots drops 0-3 seeds.
                                } else if (crop.getType() == Material.POTATO) {
                                    droppedItems.add(new ItemBuilder().setMaterial(Material.POTATO).setAmount(random.nextInt(4) + 1).build()); // Potatoes drop 1-4 of them self's.
                                } else if (crop.getType() == Material.CARROTS) {
                                    droppedItems.add(new ItemBuilder().setMaterial(Material.CARROT).setAmount(random.nextInt(4) + 1).build()); // Carrots drop 1-4 of them self's.
                                } else if (crop.getType() == Material.NETHER_WART) {
                                    droppedItems.add(new ItemBuilder().setMaterial(Material.NETHER_WART).setAmount(random.nextInt(3) + 2).build()); // Nether Warts drop 2-4 of them self's.
                                }

                                if (!droppedItems.isEmpty()) {
                                    for (ItemStack droppedItem : droppedItems) {
                                        if (droppedItem.getAmount() > 0) {
                                            if (Methods.isInventoryFull(player)) {
                                                player.getWorld().dropItem(player.getLocation(), droppedItem);
                                            } else {
                                                player.getInventory().addItem(droppedItem);
                                            }
                                        }
                                    }

                                    e.setDropItems(false);
                                    crop.setType(Material.AIR);
                                    continue;
                                }
                            }

                            crop.breakNaturally();
                        }
                    }
                }
            }
        }
    }
    
    private void fullyGrowPlant(ItemStack hoe, Block block, Player player) {
        if (CEnchantments.GREENTHUMB.chanceSuccessful(hoe) || player.getGameMode() == GameMode.CREATIVE) {
            crazyManager.getNMSSupport().fullyGrowPlant(block);
            player.getLocation().getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation(), 20, .25F, .25F, .25F);
        }
    }
    
    private boolean plantSeedSuccess(ItemStack hoe, Block soil, Player player, boolean hasGreenThumb) {
        boolean isSoulSand = soil.getType() == Material.SOUL_SAND;
        Material seedType;
        ItemStack playerSeedItem;
        Block plant = soil.getLocation().add(0, 1, 0).getBlock();

        if (plant.getType() == Material.AIR) {
            seedType = getPlanterSeed(player.getEquipment().getItemInOffHand());
            playerSeedItem = player.getEquipment().getItemInOffHand();

            if (isSoulSand) { // If on soul sand we want it to plant Nether Warts not normal seeds.
                if (playerSeedItem != null && playerSeedItem.getType() != Material.NETHER_WART) {
                    seedType = null;
                }
            } else {
                if (playerSeedItem != null && playerSeedItem.getType() == Material.NETHER_WART) {
                    seedType = null; // Makes sure nether warts are not put on soil.
                }
            }

            if (seedType == null) {
                for (int slot = 0; slot < 9; slot++) {
                    seedType = getPlanterSeed(player.getInventory().getItem(slot));
                    playerSeedItem = player.getInventory().getItem(slot);

                    if (isSoulSand) { // If on soul sand we want it to plant Nether Warts not normal seeds.
                        if (playerSeedItem != null && playerSeedItem.getType() != Material.NETHER_WART) {
                            seedType = null;
                        }
                    } else {
                        if (playerSeedItem != null && playerSeedItem.getType() == Material.NETHER_WART) {
                            seedType = null; // Makes sure nether warts are not put on soil.
                        }
                    }

                    if (seedType != null) {
                        break;
                    }
                }
            }

            if (seedType != null) {
                if (soil.getType() != soilBlock && !isSoulSand) {
                    soil.setType(soilBlock);
                }

                if (player.getGameMode() != GameMode.CREATIVE) {
                    Methods.removeItem(playerSeedItem, player); // Take seed from player
                }

                plant.setType(seedType);

                if (hasGreenThumb) {
                    fullyGrowPlant(hoe, plant, player);
                }

                return true;
            }
        }

        return false;
    }
    
    /**
     * Includes crop blocks and stems
     */
    private List<Material> getSeedlings() {
        if (seedlings == null) {
            seedlings = new ArrayList<>();
                seedlings.addAll(Arrays.asList(Material.WHEAT,
                Material.CARROTS,
                Material.MELON_STEM,
                Material.PUMPKIN_STEM,
                Material.COCOA,
                Material.BEETROOTS,
                Material.POTATOES,
                Material.NETHER_WART));
        }

        return seedlings;
    }
    
    private Material getPlanterSeed(ItemStack item) {
        return item != null ? getPlanterSeed(item.getType()) : null;
    }
    
    private Material getPlanterSeed(Material material) {
        if (planterSeeds == null) {
            planterSeeds = new HashMap<>(); // Key == Item : Value == BlockType
            planterSeeds.put(Material.WHEAT_SEEDS, Material.WHEAT);
            planterSeeds.put(Material.BEETROOT_SEEDS, Material.BEETROOTS);
            planterSeeds.put(Material.POTATO, Material.POTATOES);
            planterSeeds.put(Material.CARROT, Material.CARROTS);
            planterSeeds.put(Material.NETHER_WART, Material.NETHER_WART);
            planterSeeds.put(Material.MELON_SEEDS, Material.MELON_STEM);
            planterSeeds.put(Material.PUMPKIN_SEEDS, Material.PUMPKIN_STEM);
        }

        return material != null ? planterSeeds.get(material) : null;
    }
    
    private List<Block> getAreaCrops(Player player, Block block, BlockFace blockFace) {
        List<Block> blockList = new ArrayList<>();

        for (Block crop : getAreaBlocks(block, blockFace, 0, 1)) { // Radius of 1 is 3x3
            if (getHarvesterCrops().contains(crop.getType()) && crazyManager.getNMSSupport().isFullyGrown(crop)) {
                BlockBreakEvent event = new BlockBreakEvent(crop, player);
                crazyManager.addIgnoredEvent(event);
                crazyManager.getPlugin().getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) { // This stops players from breaking blocks that might be in protected areas.
                    blockList.add(crop);
                    crazyManager.removeIgnoredEvent(event);
                }
            }
        }

        return blockList;
    }
    
    private List<Block> getSoil(Player player, Block block) {
        List<Block> soilBlocks = new ArrayList<>();
        for (Block soil : getAreaBlocks(block)) {
            if (soil.getType() == grassBlock || soil.getType() == Material.DIRT || soil.getType() == Material.SOUL_SAND || soil.getType() == soilBlock) {
                BlockBreakEvent event = new BlockBreakEvent(soil, player);
                crazyManager.addIgnoredEvent(event);
                crazyManager.getPlugin().getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) { // This stops players from breaking blocks that might be in protected areas.
                    soilBlocks.add(soil);
                    crazyManager.removeIgnoredEvent(event);
                }
            }
        }

        return soilBlocks;
    }
    
    private List<Block> getAreaBlocks(Block block) {
        return getAreaBlocks(block, BlockFace.UP, 0, 1); // Radius of 1 is 3x3
    }
    
    private List<Block> getAreaBlocks(Block block, int radius) {
        return getAreaBlocks(block, BlockFace.UP, 0, radius);
    }
    
    private List<Block> getAreaBlocks(Block block, BlockFace blockFace, int depth, int radius) {
        Location loc = block.getLocation();
        Location loc2 = block.getLocation();

        switch (blockFace) {
            case SOUTH -> {
                loc.add(-radius, radius, -depth);
                loc2.add(radius, -radius, 0);
            }

            case WEST -> {
                loc.add(depth, radius, -radius);
                loc2.add(0, -radius, radius);
            }

            case EAST -> {
                loc.add(-depth, radius, radius);
                loc2.add(0, -radius, -radius);
            }

            case NORTH -> {
                loc.add(radius, radius, depth);
                loc2.add(-radius, -radius, 0);
            }

            case UP -> {
                loc.add(-radius, -depth, -radius);
                loc2.add(radius, 0, radius);
            }

            case DOWN -> {
                loc.add(radius, depth, radius);
                loc2.add(-radius, 0, -radius);
            }

            default -> {}
        }

        List<Block> blockList = new ArrayList<>();
        int topBlockX = (Math.max(loc.getBlockX(), loc2.getBlockX()));
        int bottomBlockX = (Math.min(loc.getBlockX(), loc2.getBlockX()));
        int topBlockY = (Math.max(loc.getBlockY(), loc2.getBlockY()));
        int bottomBlockY = (Math.min(loc.getBlockY(), loc2.getBlockY()));
        int topBlockZ = (Math.max(loc.getBlockZ(), loc2.getBlockZ()));
        int bottomBlockZ = (Math.min(loc.getBlockZ(), loc2.getBlockZ()));

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    blockList.add(loc.getWorld().getBlockAt(x, y, z));
                }
            }
        }

        return blockList;
    }
}