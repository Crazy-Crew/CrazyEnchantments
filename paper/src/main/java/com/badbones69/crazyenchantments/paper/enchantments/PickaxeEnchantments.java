package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.BlastUseEvent;
import com.badbones69.crazyenchantments.paper.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.paper.api.support.anticheats.NoCheatPlusSupport;
import com.badbones69.crazyenchantments.paper.api.objects.BlockProcessInfo;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.objects.TelepathyDrop;
import com.badbones69.crazyenchantments.paper.api.support.anticheats.SpartanSupport;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentSettings;
import com.badbones69.crazyenchantments.paper.utilities.misc.EventUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.Map.Entry;

public class PickaxeEnchantments implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = plugin.getStarter().getMethods();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    // Settings.
    private final EnchantmentSettings enchantmentSettings = starter.getEnchantmentSettings();
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

        if (!isBlastActive(enchantmentBookSettings.getEnchantmentsOnItem(item), player, block)) return;

        HashMap<Block, BlockFace> blockFace = new HashMap<>();
        blockFace.put(block, event.getBlockFace());
        blocks.put(player, blockFace);

    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlastBreak(BlockBreakEvent event) {
        if (!event.isDropItems()) return;
        if (EventUtils.isIgnoredEvent(event) || !CEnchantments.BLAST.isActivated() || !crazyManager.getBlastBlockList().contains(event.getBlock().getType())) return;

        Player player = event.getPlayer();
        Block currentBlock = event.getBlock();
        ItemStack currentItem = methods.getItemInHand(player);

        if (!blocks.containsKey(player)) return;
        if (!blocks.get(player).containsKey(currentBlock)) return;

        List<CEnchantment> enchantments = enchantmentBookSettings.getEnchantmentsOnItem(currentItem);

        if (!isBlastActive(enchantments, player, null)) return;

        event.setCancelled(true);

        BlockFace face = blocks.get(player).get(currentBlock);
        blocks.remove(player);
        List<Block> blockList = getBlocks(currentBlock.getLocation(), face, (crazyManager.getLevel(currentItem, CEnchantments.BLAST) - 1));

        BlastUseEvent blastUseEvent = new BlastUseEvent(player, blockList);
        plugin.getServer().getPluginManager().callEvent(blastUseEvent);

        if (!blastUseEvent.isCancelled()) {
            Location originalBlockLocation = currentBlock.getLocation();
            List<BlockProcessInfo> finalBlockList = new ArrayList<>();

            for (Block block : blockList) {
                if (block.getType() != Material.AIR && (crazyManager.getBlastBlockList().contains(block.getType()) || block.getLocation().equals(originalBlockLocation))) {
                    BlockBreakEvent blastBreakTest = new BlockBreakEvent(block, player);
                    blastBreakTest.setDropItems(false);
                    EventUtils.addIgnoredEvent(blastBreakTest);
                    plugin.getServer().getPluginManager().callEvent(blastBreakTest);

                    if (!blastBreakTest.isCancelled()) finalBlockList.add(new BlockProcessInfo(currentItem, block));

                    EventUtils.removeIgnoredEvent(blastBreakTest);
                }
            }

            if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) noCheatPlusSupport.allowPlayer(player);

            if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
                spartanSupport.cancelFastBreak(player);
                spartanSupport.cancelNoSwing(player);
                spartanSupport.cancelBlockReach(player);
            }

            int xp = 0;
            HashMap<ItemStack, Integer> drops = new HashMap<>();
            boolean damage = Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Blast-Full-Durability");
            boolean hasSilkTouch = currentItem.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);
            boolean hasTelepathy = enchantments.contains(CEnchantments.TELEPATHY.getEnchantment());
            boolean hasFurnace = enchantments.contains(CEnchantments.FURNACE.getEnchantment());
            boolean hasAutoSmelt = enchantments.contains(CEnchantments.AUTOSMELT.getEnchantment());
            boolean hasExperience = enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment());

            for (BlockProcessInfo processInfo : finalBlockList) {
                Block block = processInfo.getBlock();
                boolean isOre = isOre(block.getType());
                if (player.getGameMode() == GameMode.CREATIVE || !crazyManager.isDropBlocksBlast()) { // If the user is in creative mode.
                    block.breakNaturally();
                } else { // If the user is in survival mode.
                    // This is to check if the original block the player broke was in the block list.
                    // If it is not then it should be broken and dropped on the ground.

                    if (block.getLocation().equals(originalBlockLocation) && !crazyManager.getBlastBlockList().contains(block.getType())) {
                        block.breakNaturally();
                        continue;
                    }

                    if (hasTelepathy) {
                        TelepathyDrop drop = enchantmentSettings.getTelepathyDrops(processInfo);
                        drops.put(drop.getItem(), drops.getOrDefault(drop.getItem(), 0) + drop.getItem().getAmount());
                        xp += drop.getXp();
                    } else {
                        if (hasFurnace && isOre) {
                            ItemStack finalDrop = getOreDrop(block.getType());

                            try {
                                block.getWorld().dropItem(block.getLocation(), finalDrop);
                            } catch (IllegalArgumentException ignore) {}
                        } else if (hasAutoSmelt && isOre) {
                            for (ItemStack drop : block.getDrops(currentItem)) {
                                if (CEnchantments.AUTOSMELT.chanceSuccessful(currentItem)) {
                                    drop = getOreDrop(block.getType());
                                    drop.setAmount(crazyManager.getLevel(currentItem, CEnchantments.AUTOSMELT));
                                }

                                ItemStack finalDrop = drop;

                                try {
                                    block.getWorld().dropItem(block.getLocation(), finalDrop);
                                } catch (IllegalArgumentException ignore) {}
                            }
                        } else {
                            for (ItemStack drop : block.getDrops(currentItem)) {
                                if (drop.getType() != Material.AIR) {
                                    try {
                                        block.getWorld().dropItem(block.getLocation(), drop);
                                    } catch (IllegalArgumentException ignore) {}
                                }

                                if (drop.getType() == Material.REDSTONE_ORE || drop.getType() == Material.LAPIS_ORE || drop.getType() == Material.GLOWSTONE) break;
                            }
                        }

                        // This is found here as telepathy takes care of this part.
                        if (!hasSilkTouch && isOre) {
                            xp = methods.percentPick(7, 3);

                            if (hasExperience && CEnchantments.EXPERIENCE.chanceSuccessful(currentItem)) xp += methods.percentPick(7, 3) * crazyManager.getLevel(currentItem, CEnchantments.EXPERIENCE);
                        }
                    }

                    block.setType(Material.AIR);

                    if (damage) methods.removeDurability(currentItem, player);
                }
            }

            if (!damage) methods.removeDurability(currentItem, player);

            if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) noCheatPlusSupport.allowPlayer(player);

            for (Entry<ItemStack, Integer> item : drops.entrySet()) {
                item.getKey().setAmount(item.getValue());

                if (item.getKey().getType().equals(Material.SPAWNER)) continue; // Removes the handling of spawners by this plugin.

                HashMap<Integer, ItemStack> rewardsToDrop = player.getInventory().addItem(item.getKey());

                if (!rewardsToDrop.isEmpty()) rewardsToDrop.forEach((index, reward) -> player.getWorld().dropItemNaturally(player.getLocation(), reward));
            }

            if (player.getGameMode() != GameMode.CREATIVE && xp > 0) {
                ExperienceOrb orb = currentBlock.getWorld().spawn(currentBlock.getLocation().add(.5, .5, .5), ExperienceOrb.class);
                orb.setExperience(xp);
            }
        }
    }

    @EventHandler(priority =  EventPriority.LOW, ignoreCancelled = true)
    public void onVeinMinerBreak(BlockBreakEvent event) {
        if (!isOre(event.getBlock().getType())) return;
        if (!event.isDropItems()) return;
        if (EventUtils.isIgnoredEvent(event) || !CEnchantments.VEINMINER.isActivated()) return;

        Player player = event.getPlayer();
        Block currentBlock = event.getBlock();
        ItemStack currentItem = methods.getItemInHand(player);

        List<CEnchantment> enchantments = enchantmentBookSettings.getEnchantmentsOnItem(currentItem);

        if (!enchantments.contains(CEnchantments.VEINMINER.getEnchantment())) return;

        List<Block> blockList = new ArrayList<>(getOreBlocks(currentBlock.getLocation(), crazyManager.getLevel(currentItem, CEnchantments.VEINMINER)));
        blockList.add(currentBlock);

        BlastUseEvent VeinMinerUseEvent = new BlastUseEvent(player, blockList);
        plugin.getServer().getPluginManager().callEvent(VeinMinerUseEvent);

        if (VeinMinerUseEvent.isCancelled()) return;

        event.setCancelled(true);

        List<BlockProcessInfo> finalBlockList = new ArrayList<>();

        for (Block block : blockList) {
            if (!block.isEmpty() && !block.getLocation().equals(currentBlock.getLocation())) {
                BlockBreakEvent event2 = new BlockBreakEvent(block, player);
                event2.setDropItems(false);
                EventUtils.addIgnoredEvent(event2);
                plugin.getServer().getPluginManager().callEvent(event2);

                if (!event2.isCancelled()) finalBlockList.add(new BlockProcessInfo(currentItem, block));

                EventUtils.removeIgnoredEvent(event2);
            }
        }

        if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) noCheatPlusSupport.allowPlayer(player);

        if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
            spartanSupport.cancelFastBreak(player);
            spartanSupport.cancelNoSwing(player);
            spartanSupport.cancelBlockReach(player);
        }

        int xp = 0;
        HashMap<ItemStack, Integer> drops = new HashMap<>();
        boolean damage = Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.VeinMiner-Full-Durability", true);
        boolean hasSilkTouch = currentItem.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);
        boolean hasTelepathy = enchantments.contains(CEnchantments.TELEPATHY.getEnchantment());
        boolean hasFurnace = enchantments.contains(CEnchantments.FURNACE.getEnchantment());
        boolean hasAutoSmelt = enchantments.contains(CEnchantments.AUTOSMELT.getEnchantment());
        boolean hasExperience = enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment());

        for (BlockProcessInfo processInfo : finalBlockList) {
            Block block = processInfo.getBlock();
            if (player.getGameMode() == GameMode.CREATIVE || !crazyManager.isDropBlocksVeinMiner()) { // If the user is in creative mode.
                block.breakNaturally();
            } else { // If the user is in survival mode.
                // This is to check if the original block the player broke was in the block list.
                // If it is not then it should be broken and dropped on the ground.

                if (hasTelepathy) {
                    TelepathyDrop drop = enchantmentSettings.getTelepathyDrops(processInfo);
                    drops.put(drop.getItem(), drops.getOrDefault(drop.getItem(), 0) + drop.getItem().getAmount());
                    xp += drop.getXp();
                } else {
                    if (hasFurnace) {
                        ItemStack finalDrop = getOreDrop(block.getType());

                        try {
                            block.getWorld().dropItem(block.getLocation(), finalDrop);
                        } catch (IllegalArgumentException ignore) {}
                    } else if (hasAutoSmelt) {
                        for (ItemStack drop : block.getDrops(currentItem)) {
                            if (CEnchantments.AUTOSMELT.chanceSuccessful(currentItem)) {
                                drop = getOreDrop(block.getType());
                                drop.setAmount(crazyManager.getLevel(currentItem, CEnchantments.AUTOSMELT));
                            }

                            ItemStack finalDrop = drop;

                            try {
                                block.getWorld().dropItem(block.getLocation(), finalDrop);
                            } catch (IllegalArgumentException ignore) {}
                        }
                    } else {
                        for (ItemStack drop : block.getDrops(currentItem)) {
                            if (drop.getType() != Material.AIR) {
                                try {
                                    block.getWorld().dropItem(block.getLocation(), drop);
                                } catch (IllegalArgumentException ignore) {}
                            }

                            if (drop.getType() == Material.REDSTONE_ORE || drop.getType() == Material.LAPIS_ORE || drop.getType() == Material.GLOWSTONE) break;
                        }
                    }

                    // This is found here as telepathy takes care of this part.
                    if (!hasSilkTouch) {
                        xp = methods.percentPick(7, 3);

                        if (hasExperience && CEnchantments.EXPERIENCE.chanceSuccessful(currentItem)) xp += methods.percentPick(7, 3) * crazyManager.getLevel(currentItem, CEnchantments.EXPERIENCE);
                    }
                }

                block.setType(Material.AIR);

                if (damage) methods.removeDurability(currentItem, player);
            }
        }

        if (!damage) methods.removeDurability(currentItem, player);

        if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) noCheatPlusSupport.allowPlayer(player);

        for (Entry<ItemStack, Integer> item : drops.entrySet()) {
            item.getKey().setAmount(item.getValue());

            if (item.getKey().getType().equals(Material.SPAWNER)) continue; // Removes the handling of spawners by this plugin.

            HashMap<Integer, ItemStack> rewardsToDrop = player.getInventory().addItem(item.getKey());

            if (!rewardsToDrop.isEmpty()) rewardsToDrop.forEach((index, reward) -> player.getWorld().dropItemNaturally(player.getLocation(), reward));

        }

        if (player.getGameMode() != GameMode.CREATIVE && xp > 0) {
            ExperienceOrb orb = currentBlock.getWorld().spawn(currentBlock.getLocation().add(.5, .5, .5), ExperienceOrb.class);
            orb.setExperience(xp);
        }


    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isDropItems()) return;
        if (EventUtils.isIgnoredEvent(event)) return;

        Block block = event.getBlock();
        Player player = event.getPlayer();
        ItemStack item = methods.getItemInHand(player);
        List<CEnchantment> enchantments = enchantmentBookSettings.getEnchantmentsOnItem(item);
        boolean isOre = isOre(block.getType());

        if (player.getGameMode() != GameMode.CREATIVE) {
            if (CEnchantments.AUTOSMELT.isActivated() &&
                isOre &&
                (enchantments.contains(CEnchantments.AUTOSMELT.getEnchantment()) &&
                    !(isBlastActive(enchantments, player, block) ||
                        enchantments.contains(CEnchantments.FURNACE.getEnchantment()) ||
                        enchantments.contains(CEnchantments.TELEPATHY.getEnchantment()))) &&
                CEnchantments.AUTOSMELT.chanceSuccessful(item)) {

                EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(player, CEnchantments.AUTOSMELT, item);
                plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

                if (!enchantmentUseEvent.isCancelled()) {
                    int dropAmount = 0;
                    dropAmount += crazyManager.getLevel(item, CEnchantments.AUTOSMELT);

                    if (item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS) && methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) dropAmount += getRandomNumber(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));

                    tryCheck(block, item, enchantments, dropAmount);

                    event.setDropItems(false);

                    methods.removeDurability(item, player);
                }
            }

            if (CEnchantments.FURNACE.isActivated() &&
                    isOre &&
                    (enchantments.contains(CEnchantments.FURNACE.getEnchantment()) &&
                    !((isBlastActive(enchantments, player, block)) ||
                            enchantments.contains(CEnchantments.TELEPATHY.getEnchantment())))) {

                EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(player, CEnchantments.FURNACE, item);
                plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

                if (!enchantmentUseEvent.isCancelled()) {
                    int dropAmount = 1;

                    if (item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS) && methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) dropAmount += getRandomNumber(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));

                    if (block.getType() == Material.REDSTONE_ORE || block.getType() == Material.COAL_ORE || block.getType() == Material.LAPIS_ORE) dropAmount += methods.percentPick(4, 1);

                    tryCheck(block, item, enchantments, dropAmount);
                }

                event.setDropItems(false);
                methods.removeDurability(item, player);
            }
        }

        if (CEnchantments.EXPERIENCE.isActivated() && !hasSilkTouch(item) &&
                isOre &&
                (enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment()) &&
                        !((isBlastActive(enchantments, player, block)) ||
                                enchantments.contains(CEnchantments.TELEPATHY.getEnchantment())))) {

            int power = crazyManager.getLevel(item, CEnchantments.EXPERIENCE);

            if (CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
                EnchantmentUseEvent enchantmentUseEvent = new EnchantmentUseEvent(player, CEnchantments.EXPERIENCE, item);
                plugin.getServer().getPluginManager().callEvent(enchantmentUseEvent);

                if (!enchantmentUseEvent.isCancelled()) event.setExpToDrop(event.getExpToDrop() + (power + 2));
            }
        }
    }

    private void tryCheck(Block block, ItemStack item, List<CEnchantment> enchantments, int dropAmount) {
        if (block.getType() == Material.SPAWNER) return; // No more handling Spawners!!!
        try {
            block.getWorld().dropItem(block.getLocation().add(.5, 0, .5), getOreDrop(block.getType(), dropAmount));
        } catch (IllegalArgumentException ignore) {}

        if (CEnchantments.EXPERIENCE.isActivated() && enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment()) && CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
            int power = crazyManager.getLevel(item, CEnchantments.EXPERIENCE);

            ExperienceOrb orb = block.getWorld().spawn(block.getLocation(), ExperienceOrb.class);
            orb.setExperience(methods.percentPick(7, 3) * power);
        }
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
    private List<Block> getBlocks(Location loc, BlockFace blockFace, Integer depth) {
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

    private ItemStack getOreDrop(Material material) {
        return getOreDrop(material, 1);
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

    private boolean isBlastActive(List<CEnchantment> enchantments, Player player, Block block) {
        return CEnchantments.BLAST.isActivated() &&
                enchantments.contains(CEnchantments.BLAST.getEnchantment()) &&
                player.hasPermission("crazyenchantments.blast.use") &&
                (block == null || crazyManager.getBlastBlockList().contains(block.getType()));
    }

}