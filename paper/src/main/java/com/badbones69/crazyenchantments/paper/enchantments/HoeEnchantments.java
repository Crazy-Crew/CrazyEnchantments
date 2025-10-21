package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.utils.EnchantUtils;
import com.badbones69.crazyenchantments.paper.api.utils.EventUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class HoeEnchantments implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final CrazyInstance instance = this.plugin.getInstance();

    @NotNull
    private final CrazyManager crazyManager = null;

    private final Map<UUID, Map<Block, BlockFace>> blocks = new HashMap<>();

    private final Set<Material> harvesterCrops = Set.of(Material.WHEAT, Material.CARROTS, Material.BEETROOTS, Material.POTATOES, Material.NETHER_WART, Material.COCOA);

    private final Set<Material> seedlings = Set.of(Material.WHEAT, Material.CARROTS, Material.BEETROOTS, Material.POTATOES, Material.NETHER_WART, Material.COCOA,
            Material.MELON_STEM, Material.CRIMSON_STEM, Material.PUMPKIN_STEM, Material.WARPED_STEM);

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getHand() != EquipmentSlot.HAND) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack hoe = Methods.getItemInHand(player);

            Block block = event.getClickedBlock();

            if (block == null) return;

            Map<CEnchantment, Integer> enchantments = this.instance.getEnchantments(hoe);

            // Crop is not fully grown.
            if (this.seedlings.contains(block.getType()) && !this.crazyManager.getNMSSupport().isFullyGrown(block) && EnchantUtils.isEventActive(CEnchantments.GREENTHUMB, player, hoe, enchantments)) {
                fullyGrowPlant(block);

                if (player.getGameMode() != GameMode.CREATIVE) Methods.removeDurability(hoe, player);
            }

            if (block.getType() == Material.GRASS_BLOCK || block.getType() == Material.DIRT || block.getType() == Material.SOUL_SAND || block.getType() == Material.FARMLAND) {
                boolean hasGreenThumb = CEnchantments.GREENTHUMB.isActivated() && enchantments.containsKey(CEnchantments.GREENTHUMB.getEnchantment());

                if (EnchantUtils.isEventActive(CEnchantments.TILLER, player, hoe, enchantments)) {
                    for (Block soil : getSoil(player, block)) {

                        if (soil.getType() != Material.FARMLAND && soil.getType() != Material.SOUL_SAND) soil.setType(Material.FARMLAND);

                        if (soil.getType() != Material.SOUL_SAND) {
                            for (Block water : getAreaBlocks(soil, 4)) {
                                if (water.getType() == Material.WATER) {
                                    this.crazyManager.getNMSSupport().hydrateSoil(soil);

                                    break;
                                }
                            }
                        }

                        if (enchantments.containsKey(CEnchantments.PLANTER.getEnchantment())) plantSeedSuccess(soil, player, hasGreenThumb);

                        // Take durability from the hoe for each block set to a soil.
                        if (player.getGameMode() != GameMode.CREATIVE) Methods.removeDurability(hoe, player);
                    }
                }

                // Take durability from players not in Creative
                // Checking else to make sure the item does have Tiller.
                if (player.getGameMode() != GameMode.CREATIVE && CEnchantments.PLANTER.isActivated() && enchantments.containsKey(CEnchantments.PLANTER.getEnchantment()) && !enchantments.containsKey(CEnchantments.TILLER.getEnchantment())
                        && plantSeedSuccess(block, player, hasGreenThumb)) Methods.removeDurability(hoe, player);
            }
        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK && CEnchantments.HARVESTER.isActivated()
                && this.instance.getEnchantments(Methods.getItemInHand(player)).containsKey(CEnchantments.HARVESTER.getEnchantment())) {
            Map<Block, BlockFace> blockFace = new HashMap<>();

            blockFace.put(event.getClickedBlock(), event.getBlockFace());

            this.blocks.put(player.getUniqueId(), blockFace);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isDropItems()) return;

        if (!event.isCancelled() && !EventUtils.isIgnoredEvent(event)) {
            Player player = event.getPlayer();
            Block plant = event.getBlock();

            if (!this.harvesterCrops.contains(plant.getType())) return;

            ItemStack hoe = Methods.getItemInHand(player);

            Map<CEnchantment, Integer> enchantments = this.instance.getEnchantments(hoe);

            if (!this.blocks.containsKey(player.getUniqueId())) return;

            if (!EnchantUtils.isEventActive(CEnchantments.HARVESTER, player, hoe, enchantments)) return;

            BlockFace blockFace = this.blocks.get(player.getUniqueId()).get(plant);

            this.blocks.remove(player.getUniqueId());

            if (!this.crazyManager.getNMSSupport().isFullyGrown(plant)) return;

            getAreaCrops(plant, blockFace).forEach(block -> Methods.playerBreakBlock(player, block, hoe, true));
        }
    }

    private void fullyGrowPlant(@NotNull final Block block) {
        this.crazyManager.getNMSSupport().fullyGrowPlant(block);

        block.getLocation().getWorld().spawnParticle(Particle.HAPPY_VILLAGER, block.getLocation(), 20, .25F, .25F, .25F);
    }

    private boolean plantSeedSuccess(@NotNull final Block soil, @NotNull final Player player, final boolean hasGreenThumb) {
        boolean isSoulSand = soil.getType() == Material.SOUL_SAND;
        Material seedType;
        ItemStack playerSeedItem;
        Block plant = soil.getLocation().add(0, 1, 0).getBlock();

        if (!plant.isEmpty()) return false;

        final EntityEquipment equipment = player.getEquipment();

        seedType = getPlanterSeed(equipment.getItemInOffHand());
        playerSeedItem = equipment.getItemInOffHand();

        if (isSoulSand) { // If on soul sand we want it to plant Nether Warts not normal seeds.
            if (playerSeedItem.getType() != Material.NETHER_WART) seedType = null;
        } else {
            if (playerSeedItem.getType() == Material.NETHER_WART) seedType = null;
        }

        if (seedType == null) {
            for (int slot = 0; slot < 9; slot++) {
                final ItemStack itemStack = player.getInventory().getItem(slot);

                seedType = getPlanterSeed(itemStack == null ? ItemStack.empty() : itemStack);
                playerSeedItem = itemStack == null ? ItemStack.empty() : itemStack;

                if (isSoulSand) { // If on soul sand we want it to plant Nether Warts not normal seeds.
                    if (playerSeedItem.getType() != Material.NETHER_WART) seedType = null;
                } else {
                    if (playerSeedItem.getType() == Material.NETHER_WART) seedType = null; // Makes sure nether warts are not put on soil.
                }

                if (seedType != null) break;
            }
        }

        if (seedType != null) {
            if (soil.getType() != Material.FARMLAND && !isSoulSand) soil.setType(Material.FARMLAND);

            if (player.getGameMode() != GameMode.CREATIVE && !playerSeedItem.isEmpty()) Methods.removeItem(playerSeedItem, player); // Take seed from player

            plant.setType(seedType);

            if (hasGreenThumb) fullyGrowPlant(plant); // TODO re-add enchant check

            return true;
        }

        return false;
    }

    private final Map<Material, Material> planterSeeds = new HashMap<>(){{
        put(Material.WHEAT_SEEDS, Material.WHEAT);
        put(Material.BEETROOT_SEEDS, Material.BEETROOTS);
        put(Material.POTATO, Material.POTATOES);
        put(Material.CARROT, Material.CARROTS);
        put(Material.NETHER_WART, Material.NETHER_WART);
        put(Material.MELON_SEEDS, Material.MELON_STEM);
        put(Material.PUMPKIN_SEEDS, Material.PUMPKIN_STEM);
    }};

    public Material getPlanterSeed(@NotNull final ItemStack item) {
        if (item.isEmpty()) return null;

        return this.planterSeeds.get(item.getType());
    }

    private List<Block> getAreaCrops(@NotNull final Block block, @NotNull final BlockFace blockFace) {
        List<Block> blockList = new ArrayList<>();

        for (Block crop : getAreaBlocks(block, blockFace, 1)) { // Radius of 1 is 3x3
            if (this.harvesterCrops.contains(crop.getType()) && this.crazyManager.getNMSSupport().isFullyGrown(crop)) {
                blockList.add(crop);
            }
        }

        return blockList;
    }

    private List<Block> getSoil(@NotNull final Player player, @NotNull final Block block) {
        List<Block> soilBlocks = new ArrayList<>();

        for (Block soil : getAreaBlocks(block, 1)) {
            if (soil.getType() == Material.GRASS_BLOCK || soil.getType() == Material.DIRT || soil.getType() == Material.SOUL_SAND || soil.getType() == Material.FARMLAND) {

                BlockBreakEvent useEvent = new BlockBreakEvent(soil, player);
                EventUtils.addIgnoredEvent(useEvent);

                this.plugin.getServer().getPluginManager().callEvent(useEvent);

                if (!useEvent.isCancelled()) { // This stops players from breaking blocks that might be in protected areas.
                    soilBlocks.add(soil);
                    EventUtils.removeIgnoredEvent(useEvent);
                }
            }
        }

        return soilBlocks;
    }

    private Set<Block> getAreaBlocks(@NotNull final Block block, final int radius) {
        return getAreaBlocks(block, BlockFace.UP, radius);
    }

    private Set<Block> getAreaBlocks(@NotNull final Block block, @NotNull final BlockFace blockFace, final int radius) {
        Location loc = block.getLocation();
        Location loc2 = block.getLocation();

        switch (blockFace) {
            case SOUTH -> {
                loc.add(-radius, radius, -0);
                loc2.add(radius, -radius, 0);
            }

            case WEST -> {
                loc.add(0, radius, -radius);
                loc2.add(0, -radius, radius);
            }

            case EAST -> {
                loc.add(-0, radius, radius);
                loc2.add(0, -radius, -radius);
            }

            case NORTH -> {
                loc.add(radius, radius, 0);
                loc2.add(-radius, -radius, 0);
            }

            case UP -> {
                loc.add(-radius, -0, -radius);
                loc2.add(radius, 0, radius);
            }

            case DOWN -> {
                loc.add(radius, 0, radius);
                loc2.add(-radius, 0, -radius);
            }

            default -> {}
        }

        return Methods.getEnchantBlocks(loc, loc2);
    }
}