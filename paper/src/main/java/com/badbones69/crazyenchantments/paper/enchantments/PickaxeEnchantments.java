package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.MassBlockBreakEvent;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.utils.EnchantUtils;
import com.badbones69.crazyenchantments.paper.api.utils.EventUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.ryderbelserion.crazyenchantments.objects.ConfigOptions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
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
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PickaxeEnchantments implements Listener {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final ConfigOptions options = this.plugin.getOptions();

    private final Server server = this.plugin.getServer();

    private final PluginManager pluginManager = this.server.getPluginManager();

    private final Starter starter = this.plugin.getStarter();

    private final Methods methods = this.starter.getMethods();

    private final CrazyManager crazyManager = this.plugin.getCrazyManager();

    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    private final Map<Player, Map<Block, BlockFace>> blocks = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockClick(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        final Block block = event.getClickedBlock();

        if (block == null || block.isEmpty() || !this.crazyManager.getBlastBlockList().contains(block.getType())) return;

        final Map<Block, BlockFace> blockFace = new HashMap<>();

        blockFace.put(block, event.getBlockFace());

        this.blocks.put(player, blockFace);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlastBreak(BlockBreakEvent event) {
        if (!event.isDropItems() || EventUtils.isIgnoredEvent(event)) return;

        final Player player = event.getPlayer();

        final Block initialBlock = event.getBlock();

        final ItemStack currentItem = this.methods.getItemInHand(player);

        final Map<CEnchantment, Integer> enchantments = enchantmentBookSettings.getEnchantments(currentItem);

        boolean damage = this.options.isBlastFullDurability();

        if (!(this.blocks.containsKey(player) && this.blocks.get(player).containsKey(initialBlock))) return;
        if (!EnchantUtils.isMassBlockBreakActive(player, CEnchantments.BLAST, enchantments)) return;

        final Set<Block> blockList = getBlocks(initialBlock.getLocation(), blocks.get(player).get(initialBlock), (enchantmentBookSettings.getLevel(currentItem, CEnchantments.BLAST.getEnchantment()) - 1));

        this.blocks.remove(player);

        if (massBlockBreakCheck(player, blockList)) return;

        event.setCancelled(true);

        for (final Block block : blockList) {
            if (block.isEmpty() || !crazyManager.getBlastBlockList().contains(block.getType())) continue;
            if (this.methods.playerBreakBlock(player, block, currentItem, crazyManager.isDropBlocksBlast())) continue;
            if (damage) this.methods.removeDurability(currentItem, player);
        }

        if (!damage) this.methods.removeDurability(currentItem, player);
    }


    @EventHandler(priority =  EventPriority.LOW, ignoreCancelled = true)
    public void onVeinMinerBreak(BlockBreakEvent event) {
        if (!isOreBlock(event.getBlock().getType()) || !event.isDropItems() || EventUtils.isIgnoredEvent(event)) return;

        final Player player = event.getPlayer();
        final Block currentBlock = event.getBlock();
        final ItemStack currentItem = this.methods.getItemInHand(player);
        final Map<CEnchantment, Integer> enchantments = this.enchantmentBookSettings.getEnchantments(currentItem);
        final boolean damage = this.options.isVeinMinerFullDurability();

        if (!EnchantUtils.isMassBlockBreakActive(player, CEnchantments.VEINMINER, enchantments)) return;

        final Set<Block> blockList = getOreBlocks(currentBlock.getLocation(), enchantments.get(CEnchantments.VEINMINER.getEnchantment()));

        blockList.add(currentBlock);

        if (massBlockBreakCheck(player, blockList)) return;

        event.setCancelled(true);

        for (final Block block : blockList) {
            if (block.isEmpty()) continue;
            if (this.methods.playerBreakBlock(player, block, currentItem, this.crazyManager.isDropBlocksVeinMiner())) continue;
            if (damage) this.methods.removeDurability(currentItem, player);
        }

        if (!damage) this.methods.removeDurability(currentItem, player);
    }

    private boolean massBlockBreakCheck(Player player, Set<Block> blockList) {
        final MassBlockBreakEvent event = new MassBlockBreakEvent(player, blockList);

        this.pluginManager.callEvent(event);

        return event.isCancelled();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDropAlter(BlockDropItemEvent event) {
        if (!isOreBlock(event.getBlockState().getType())) return;

        final Player player = event.getPlayer();
        final ItemStack item = this.methods.getItemInHand(player);
        final Map<CEnchantment, Integer> enchants = this.enchantmentBookSettings.getEnchantments(item);

        final List<Item> oldDrops = event.getItems();

        if (EnchantUtils.isEventActive(CEnchantments.AUTOSMELT, player, item, enchants)) {
            int level = enchants.get(CEnchantments.AUTOSMELT.getEnchantment());

            for (int j = 0; j < oldDrops.size(); j++) {
                final Item entityItem  = oldDrops.get(j);

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
                final Item entityItem  = oldDrops.get(j);

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
        final Player player = event.getPlayer();

        if (event.getExpToDrop() <= 0) return; // If block doesn't drop xp on break, return.

        final ItemStack item = this.methods.getItemInHand(player);
        final Map<CEnchantment, Integer> enchants = this.enchantmentBookSettings.getEnchantments(item);

        if (!EnchantUtils.isEventActive(CEnchantments.EXPERIENCE, player, item, enchants)) return;

        event.setExpToDrop(event.getExpToDrop() + (enchants.get(CEnchantments.EXPERIENCE.getEnchantment()) + 2));
    }

    private Set<Block> getOreBlocks(final Location loc, final int amount) {
        final Set<Block> blocks = new HashSet<>(Set.of(loc.getBlock()));
        Set<Block> newestBlocks = new HashSet<>(Set.of(loc.getBlock()));

        int depth = 0;

        while (depth < amount) {
            Set<Block> tempBlocks = new HashSet<>();

            for (final Block block1 : newestBlocks) {
                for (final Block block : getSurroundingBlocks(block1.getLocation())) {
                    if (!blocks.contains(block) && isOreBlock(block.getType())) tempBlocks.add(block);
                }
            }

            blocks.addAll(tempBlocks);

            newestBlocks = tempBlocks;

            ++depth;
        }

        return blocks;
    } 
    
    private Set<Block> getSurroundingBlocks(final Location loc) {
        Set<Block> locations = new HashSet<>();
        
        locations.add(loc.clone().add(0,1,0).getBlock());
        locations.add(loc.clone().add(0,-1,0).getBlock());
        locations.add(loc.clone().add(1,0,0).getBlock());
        locations.add(loc.clone().add(-1,0,0).getBlock());
        locations.add(loc.clone().add(0,0,1).getBlock());
        locations.add(loc.clone().add(0,0,-1).getBlock());
        
        return locations;
    }

    private Set<Block> getBlocks(final Location loc, final BlockFace blockFace, int depth) {
        final Location loc2 = loc.clone();

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

    private boolean isOre(final Material material) { //todo() make all data driven, including the ones below because yes.
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

    private boolean isOreBlock(final Material material) {
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

    private ItemStack getOreDrop(final ItemStack item, final int amount) {
        final Material material = item.getType();

        ItemStack returnItem;

        final Material smeltedMaterial = switch (material) {
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