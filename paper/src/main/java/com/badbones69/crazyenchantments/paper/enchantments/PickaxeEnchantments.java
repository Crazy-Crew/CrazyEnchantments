package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.MassBlockBreakEvent;
import com.badbones69.crazyenchantments.paper.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.paper.api.support.anticheats.NoCheatPlusSupport;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.support.anticheats.SpartanSupport;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.utilities.misc.EventUtils;
import com.palmergames.bukkit.towny.utils.EntityTypeUtil;
import net.minecraft.world.entity.item.ItemEntity;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
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

import java.util.*;

public class PickaxeEnchantments implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = plugin.getStarter().getMethods();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    // Plugin Support.
    private final NoCheatPlusSupport noCheatPlusSupport = starter.getNoCheatPlusSupport();
    private final SpartanSupport spartanSupport = starter.getSpartanSupport();

    private final HashMap<Player, HashMap<Block, BlockFace>> blocks = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        ItemStack item = methods.getItemInHand(player);
        Block block = event.getClickedBlock();

        if (!isBlastActive(enchantmentBookSettings.getEnchantments(item), player, block)) return;

        HashMap<Block, BlockFace> blockFace = new HashMap<>();
        blockFace.put(block, event.getBlockFace());
        blocks.put(player, blockFace);

    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlastBreak(BlockBreakEvent event) {
        if (!event.isDropItems() || EventUtils.isIgnoredEvent(event)) return;

        Player player = event.getPlayer();
        Block initialBlock = event.getBlock();
        ItemStack currentItem = methods.getItemInHand(player);
        Map<CEnchantment, Integer> enchantments = enchantmentBookSettings.getEnchantments(currentItem);
        boolean damage = Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Blast-Full-Durability");

        if (!(blocks.containsKey(player) && blocks.get(player).containsKey(initialBlock))) return;
        if (!isBlastActive(enchantments, player, initialBlock)) return;

        Set<Block> blockList = getBlocks(initialBlock.getLocation(), blocks.get(player).get(initialBlock), (crazyManager.getLevel(currentItem, CEnchantments.BLAST) - 1));
        blocks.remove(player);

        MassBlockBreakEvent blastUseEvent = new MassBlockBreakEvent(player, blockList);
        plugin.getServer().getPluginManager().callEvent(blastUseEvent);

        if (blastUseEvent.isCancelled()) return;
        event.setCancelled(true);

        for (Block block : blockList) {
            if (block.isEmpty() || !crazyManager.getBlastBlockList().contains(block.getType())) continue;

            BlockBreakEvent blastBreak = new BlockBreakEvent(block, player);

            if (!crazyManager.isDropBlocksBlast()) blastBreak.setDropItems(false);
            EventUtils.addIgnoredEvent(blastBreak);
            plugin.getServer().getPluginManager().callEvent(blastBreak);
            EventUtils.removeIgnoredEvent(blastBreak);

            if (blastBreak.isCancelled()) continue;
            if (damage) methods.removeDurability(currentItem, player);
            if (blastBreak.isDropItems()) {
                block.breakNaturally(currentItem, true, true);
            } else {
                block.setType(Material.AIR);
            }

        }
        if (!damage) methods.removeDurability(currentItem, player);

        antiCheat(player);
    }


    @EventHandler(priority =  EventPriority.LOW, ignoreCancelled = true)
    public void onVeinMinerBreak(BlockBreakEvent event) {
        if (!isOre(event.getBlock().getType())
                || !event.isDropItems()
                || EventUtils.isIgnoredEvent(event)
                || !CEnchantments.VEINMINER.isActivated())
            return;

        Player player = event.getPlayer();
        Block currentBlock = event.getBlock();
        ItemStack currentItem = methods.getItemInHand(player);
        Map<CEnchantment, Integer> enchantments = enchantmentBookSettings.getEnchantments(currentItem);
        boolean damage = Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.VeinMiner-Full-Durability", true);

        if (!enchantments.containsKey(CEnchantments.VEINMINER.getEnchantment())) return;

        HashSet<Block> blockList = getOreBlocks(currentBlock.getLocation(), enchantments.get(CEnchantments.VEINMINER.getEnchantment()));
        blockList.add(currentBlock);

        MassBlockBreakEvent VeinMinerUseEvent = new MassBlockBreakEvent(player, blockList);
        plugin.getServer().getPluginManager().callEvent(VeinMinerUseEvent);

        if (VeinMinerUseEvent.isCancelled()) return;

        event.setCancelled(true);

        for (Block block : blockList) {
            if (block.isEmpty() || !crazyManager.getBlastBlockList().contains(block.getType())) continue;

            BlockBreakEvent veinMinerBreak = new BlockBreakEvent(block, player);

            if (!crazyManager.isDropBlocksVeinMiner()) veinMinerBreak.setDropItems(false);
            EventUtils.addIgnoredEvent(veinMinerBreak);
            plugin.getServer().getPluginManager().callEvent(veinMinerBreak);
            EventUtils.removeIgnoredEvent(veinMinerBreak);

            if (veinMinerBreak.isCancelled()) continue;
            if (damage) methods.removeDurability(currentItem, player);
            if (veinMinerBreak.isDropItems()) {
                block.breakNaturally(currentItem, true, true);
            } else {
                block.setType(Material.AIR);
            }

        }
        if (!damage) methods.removeDurability(currentItem, player);

        antiCheat(player);

    }

    private void antiCheat(Player player) {
        if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) noCheatPlusSupport.allowPlayer(player);

        if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
            spartanSupport.cancelFastBreak(player);
            spartanSupport.cancelNoSwing(player);
            spartanSupport.cancelBlockReach(player);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDropAlter(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = methods.getItemInHand(player);
        Map<CEnchantment, Integer> enchants = enchantmentBookSettings.getEnchantments(item);
        boolean isOre = isOre(event.getBlock().getType());
        List<Item> oldDrops = event.getItems();
        List<Item> newDrops = new ArrayList<>();

        if (isEventActive(CEnchantments.AUTOSMELT, player, item, enchants) && isOre) {

            int level = enchants.get(CEnchantments.AUTOSMELT.getEnchantment());

            for (Item i : oldDrops) {
                ItemStack drop = i.getItemStack();

                drop = getOreDrop(drop.getType(), drop.getAmount() + level);

                i.setItemStack(drop);
                newDrops.add(i);
            }
            event.getItems().clear();
            event.getItems().addAll(newDrops);
            return;
        }

        if (isEventActive(CEnchantments.FURNACE, player, item, enchants) && isOre) {

            for (Item i : oldDrops) {
                ItemStack drop = i.getItemStack();

                drop = getOreDrop(drop.getType(), drop.getAmount());

                i.setItemStack(drop);
                newDrops.add(i);
            }

            event.getItems().clear();
            event.getItems().addAll(newDrops);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExperience(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = methods.getItemInHand(player);
        Map<CEnchantment, Integer> enchants = enchantmentBookSettings.getEnchantments(item);

        if (!isEventActive(CEnchantments.EXPERIENCE, player, item, enchants)) return;
        event.setExpToDrop(event.getExpToDrop() + (enchants.get(CEnchantments.EXPERIENCE.getEnchantment()) + 2));
    }

    // TODO Check logic. Use a single method for all classes.
    private boolean isEventActive(CEnchantments enchant, Player damager, ItemStack armor, Map<CEnchantment, Integer> enchants) {
        if (!(enchants.containsKey(enchant.getEnchantment()) &&
                (!enchant.hasChanceSystem() || enchant.chanceSuccessful(armor)))) return false;

        EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, enchant.getEnchantment(), armor);
        plugin.getServer().getPluginManager().callEvent(useEvent);

        return !useEvent.isCancelled();
    }
    private boolean isBlastActive(Map<CEnchantment, Integer> enchantments, Player player, Block block) {
        return CEnchantments.BLAST.isActivated() &&
                enchantments.containsKey(CEnchantments.BLAST.getEnchantment()) &&
                player.hasPermission("crazyenchantments.blast.use") &&
                (block == null || crazyManager.getBlastBlockList().contains(block.getType()));
    }

    private void tryToGiveOreDrops(Block block, ItemStack item, Map<CEnchantment, Integer> enchantments, int dropAmount) {
        if (block.getType() == Material.SPAWNER) return; // No more handling Spawners!!!
        try {
            block.getWorld().dropItem(block.getLocation().add(.5, 0, .5), getOreDrop(block.getType(), dropAmount));
        } catch (IllegalArgumentException ignore) {}

//        if (CEnchantments.EXPERIENCE.isActivated() && enchantments.containsKey(CEnchantments.EXPERIENCE.getEnchantment()) && CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
//            int power = crazyManager.getLevel(item, CEnchantments.EXPERIENCE);
//
//            ExperienceOrb orb = block.getWorld().spawn(block.getLocation(), ExperienceOrb.class);
//            orb.setExperience(methods.percentPick(7, 3) * power);
//        }
    }

    private boolean hasSilkTouch(ItemStack item) {
        return item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasEnchant(Enchantment.SILK_TOUCH);
    }

    private HashSet<Block> getOreBlocks(Location loc, int amount) {
        HashSet<Block> blocks = new HashSet<>(Set.of(loc.getBlock()));
        HashSet<Block> newestBlocks = new HashSet<>(Set.of(loc.getBlock()));

        int depth = 0;

        while (depth < amount) {
            HashSet<Block> tempBlocks = new HashSet<>();

            for (Block block1 : newestBlocks) {
                for (Block block : getSurroundingBlocks(block1.getLocation())) {
                    if (!blocks.contains(block) && isOre(block.getType())) tempBlocks.add(block);
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

        return methods.getEnchantBlocks(loc, loc2);
    }

    private boolean isOre(Material material) {

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
    private ItemStack getOreDrop(Material material, int amount) {
        ItemBuilder dropItem = new ItemBuilder().setAmount(amount);

        switch (material) {
            case COAL_ORE, DEEPSLATE_COAL_ORE -> dropItem.setMaterial(Material.COAL);
            case COPPER_ORE, DEEPSLATE_COPPER_ORE -> dropItem.setMaterial(Material.COPPER_INGOT);
            case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE -> dropItem.setMaterial(Material.DIAMOND);
            case EMERALD_ORE, DEEPSLATE_EMERALD_ORE -> dropItem.setMaterial(Material.EMERALD);
            case GOLD_ORE, DEEPSLATE_GOLD_ORE -> dropItem.setMaterial(Material.GOLD_INGOT);
            case IRON_ORE, DEEPSLATE_IRON_ORE -> dropItem.setMaterial(Material.IRON_INGOT);
            case LAPIS_ORE, DEEPSLATE_LAPIS_ORE -> dropItem.setMaterial(Material.LAPIS_LAZULI);
            case REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE -> dropItem.setMaterial(Material.REDSTONE);
            case NETHER_GOLD_ORE -> dropItem.setMaterial(Material.GOLD_NUGGET);
            case NETHER_QUARTZ_ORE -> dropItem.setMaterial(Material.QUARTZ);
            default -> dropItem.setMaterial(Material.AIR);
        }

        return dropItem.build();
    }

    private int getRandomNumber(int range) {
        Random random = new Random();

        return range > 1 ? random.nextInt(range) : 1;
    }

}