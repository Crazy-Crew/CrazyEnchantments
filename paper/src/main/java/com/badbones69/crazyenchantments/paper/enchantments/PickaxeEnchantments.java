package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.MassBlockBreakEvent;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.utils.EnchantUtils;
import com.badbones69.crazyenchantments.paper.api.utils.EventUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PickaxeEnchantments implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final Methods methods = this.starter.getMethods();

    @NotNull
    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    @NotNull
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    private final HashMap<Player, HashMap<Block, BlockFace>> blocks = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();

        if (block == null || block.isEmpty() || !this.crazyManager.getBlastBlockList().contains(block.getType())) return;

        HashMap<Block, BlockFace> blockFace = new HashMap<>();
        blockFace.put(block, event.getBlockFace());
        this.blocks.put(player, blockFace);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlastBreak(BlockBreakEvent event) {
        if (!event.isDropItems() || EventUtils.isIgnoredEvent(event)) return;

        Player player = event.getPlayer();
        Block initialBlock = event.getBlock();
        ItemStack currentItem = this.methods.getItemInHand(player);
        Map<CEnchantment, Integer> enchantments = enchantmentBookSettings.getEnchantments(currentItem);
        boolean damage = Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Blast-Full-Durability");

        if (!(this.blocks.containsKey(player) && this.blocks.get(player).containsKey(initialBlock))) return;
        if (!EnchantUtils.isMassBlockBreakActive(player, CEnchantments.BLAST, enchantments)) return;

        Set<Block> blockList = getBlocks(initialBlock.getLocation(), blocks.get(player).get(initialBlock), (enchantmentBookSettings.getLevel(currentItem, CEnchantments.BLAST.getEnchantment()) - 1));
        this.blocks.remove(player);

        if (massBlockBreakCheck(player, blockList)) return;
        event.setCancelled(true);

        for (Block block : blockList) {
            if (block.isEmpty() || !crazyManager.getBlastBlockList().contains(block.getType())) continue;
            if (this.methods.playerBreakBlock(player, block, currentItem, crazyManager.isDropBlocksBlast())) continue;
            if (damage) this.methods.removeDurability(currentItem, player);
        }
        if (!damage) this.methods.removeDurability(currentItem, player);
    }


    @EventHandler(priority =  EventPriority.LOW, ignoreCancelled = true)
    public void onVeinMinerBreak(BlockBreakEvent event) {
        if (!isOreBlock(event.getBlock().getType())
                || !event.isDropItems()
                || EventUtils.isIgnoredEvent(event))
            return;

        Player player = event.getPlayer();
        Block currentBlock = event.getBlock();
        ItemStack currentItem = methods.getItemInHand(player);
        Map<CEnchantment, Integer> enchantments = this.enchantmentBookSettings.getEnchantments(currentItem);
        boolean damage = Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.VeinMiner-Full-Durability", true);

        if (!EnchantUtils.isMassBlockBreakActive(player, CEnchantments.VEINMINER, enchantments)) return;

        HashSet<Block> blockList = getOreBlocks(currentBlock.getLocation(), enchantments.get(CEnchantments.VEINMINER.getEnchantment()));
        blockList.add(currentBlock);

        if (massBlockBreakCheck(player, blockList)) return;

        event.setCancelled(true);

        for (Block block : blockList) {
            if (block.isEmpty()) continue;
            if (this.methods.playerBreakBlock(player, block, currentItem, this.crazyManager.isDropBlocksVeinMiner())) continue;
            if (damage) this.methods.removeDurability(currentItem, player);
        }

        if (!damage) this.methods.removeDurability(currentItem, player);
    }

    private boolean massBlockBreakCheck(Player player, Set<Block> blockList) {
        MassBlockBreakEvent event = new MassBlockBreakEvent(player, blockList);
        this.plugin.getServer().getPluginManager().callEvent(event);

        return event.isCancelled();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDropAlter(BlockDropItemEvent event) {
        if (!isOreBlock(event.getBlockState().getType())) return;

        Player player = event.getPlayer();
        ItemStack item = this.methods.getItemInHand(player);
        Map<CEnchantment, Integer> enchants = this.enchantmentBookSettings.getEnchantments(item);

        List<Item> oldDrops = event.getItems();

        if (EnchantUtils.isEventActive(CEnchantments.AUTOSMELT, player, item, enchants)) {
            int level = enchants.get(CEnchantments.AUTOSMELT.getEnchantment());

            for (int j = 0; j < oldDrops.size(); j++) {
                Item entityItem  = oldDrops.get(j);
                ItemStack drop = entityItem.getItemStack();
                int amountToAdd = 0;

                if (!isOre(drop.getType())) continue;

                for (int i = 0; i < level; i++) {
                    if (CEnchantments.AUTOSMELT.chanceSuccessful(level)) ++amountToAdd;
                }

                drop = getOreDrop(drop, drop.getAmount() + amountToAdd);

                entityItem.setItemStack(drop);
                event.getItems().set(j, entityItem);
            }

            return;
        }

        if (EnchantUtils.isEventActive(CEnchantments.FURNACE, player, item, enchants)) {

            for (int j = 0; j < oldDrops.size(); j++) {
                Item entityItem  = oldDrops.get(j);
                ItemStack drop = entityItem.getItemStack();

                if (!isOre(drop.getType())) continue;
                drop = getOreDrop(drop, drop.getAmount());

                entityItem.setItemStack(drop);
                event.getItems().set(j, entityItem);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExperience(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (event.getExpToDrop() <= 0) return; // If block doesn't drop xp on break, return.

        ItemStack item = this.methods.getItemInHand(player);
        Map<CEnchantment, Integer> enchants = this.enchantmentBookSettings.getEnchantments(item);

        if (!EnchantUtils.isEventActive(CEnchantments.EXPERIENCE, player, item, enchants)) return;

        event.setExpToDrop(event.getExpToDrop() + (enchants.get(CEnchantments.EXPERIENCE.getEnchantment()) + 2));
    }

    private HashSet<Block> getOreBlocks(Location loc, int amount) {
        HashSet<Block> blocks = new HashSet<>(Set.of(loc.getBlock()));
        HashSet<Block> newestBlocks = new HashSet<>(Set.of(loc.getBlock()));

        int depth = 0;

        while (depth < amount) {
            HashSet<Block> tempBlocks = new HashSet<>();

            for (Block block1 : newestBlocks) {
                for (Block block : getSurroundingBlocks(block1.getLocation())) {
                    if (!blocks.contains(block) && isOreBlock(block.getType())) tempBlocks.add(block);
                }
            }

            blocks.addAll(tempBlocks);
            newestBlocks = tempBlocks;

            ++depth;
        }

        return blocks;
    } 
    
    private HashSet<Block> getSurroundingBlocks(Location loc) {
        HashSet<Block> locations = new HashSet<>();
        
        locations.add(loc.clone().add(0,1,0).getBlock());
        locations.add(loc.clone().add(0,-1,0).getBlock());
        locations.add(loc.clone().add(1,0,0).getBlock());
        locations.add(loc.clone().add(-1,0,0).getBlock());
        locations.add(loc.clone().add(0,0,1).getBlock());
        locations.add(loc.clone().add(0,0,-1).getBlock());
        
        return locations;
    }

    private HashSet<Block> getBlocks(Location loc, BlockFace blockFace, Integer depth) {
        Location loc2 = loc.clone();

        switch (blockFace) {
            case SOUTH -> {
                loc.add(-1, 1, -depth);
                loc2.add(1, -1, 0);
            }

            case WEST -> {
                loc.add(depth, 1, -1);
                loc2.add(0, -1, 1);
            }

            case EAST -> {
                loc.add(-depth, 1, 1);
                loc2.add(0, -1, -1);
            }

            case NORTH -> {
                loc.add(1, 1, depth);
                loc2.add(-1, -1, 0);
            }

            case UP -> {
                loc.add(-1, -depth, -1);
                loc2.add(1, 0, 1);
            }

            case DOWN -> {
                loc.add(1, depth, 1);
                loc2.add(-1, 0, -1);
            }

            default -> {}
        }

        return this.methods.getEnchantBlocks(loc, loc2);
    }

    private boolean isOre(Material material) {
        return switch (material) {
            case COAL,
                 RAW_COPPER,
                 DIAMOND,
                 EMERALD,
                 RAW_GOLD,
                 RAW_IRON,
                 LAPIS_LAZULI,
                 REDSTONE,
                 GOLD_NUGGET,
                 QUARTZ -> true;
            default -> false;
        };
    }

    private boolean isOreBlock(Material material) {
        return switch (material) {
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

    private ItemStack getOreDrop(ItemStack item, int amount) {
        Material material = item.getType();
        ItemStack returnItem;

        Material smeltedMaterial = switch (material) {
            case COAL -> Material.COAL;
            case RAW_COPPER -> Material.COPPER_INGOT;
            case DIAMOND -> Material.DIAMOND;
            case EMERALD -> Material.EMERALD;
            case RAW_GOLD -> Material.GOLD_INGOT;
            case RAW_IRON -> Material.IRON_INGOT;
            case LAPIS_LAZULI -> Material.LAPIS_LAZULI;
            case REDSTONE -> Material.REDSTONE;
            case GOLD_NUGGET -> Material.GOLD_NUGGET;
            case QUARTZ -> Material.QUARTZ;
            default -> Material.AIR;
        };

        returnItem = (material == smeltedMaterial) ? item : new ItemStack(smeltedMaterial);

        returnItem.setAmount(amount);
        return returnItem;
    }
}