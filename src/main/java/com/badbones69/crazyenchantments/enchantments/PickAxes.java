package com.badbones69.crazyenchantments.enchantments;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.PluginSupport;
import com.badbones69.crazyenchantments.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.events.BlastUseEvent;
import com.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.api.support.anticheats.NoCheatPlusSupport;
import com.badbones69.crazyenchantments.api.objects.BlockProcessInfo;
import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.api.objects.TelepathyDrop;
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

public class PickAxes implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    private final PluginSupport pluginSupport = plugin.getStarter().getPluginSupport();

    private final Tools tools = plugin.getTools();

    private final Methods methods = plugin.getStarter().getMethods();

    private final Random random = new Random();
    private final HashMap<Player, HashMap<Block, BlockFace>> blocks = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK && CEnchantments.BLAST.isActivated()) {
            ItemStack item = methods.getItemInHand(player);
            Block block = event.getClickedBlock();

            if (crazyManager.hasEnchantment(item, CEnchantments.BLAST)) {
                HashMap<Block, BlockFace> blockFace = new HashMap<>();
                blockFace.put(block, event.getBlockFace());
                blocks.put(player, blockFace);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlastBreak(BlockBreakEvent e) {
        if (crazyManager.isIgnoredEvent(e) || !CEnchantments.BLAST.isActivated()) return;
        Player player = e.getPlayer();
        Block currentBlock = e.getBlock();
        ItemStack currentItem = methods.getItemInHand(player);

        if (!blocks.containsKey(player)) return;
        List<CEnchantment> enchantments = crazyManager.getEnchantmentsOnItem(currentItem);
        if (!blocks.get(player).containsKey(currentBlock)) return;

        if (!enchantments.contains(CEnchantments.BLAST.getEnchantment())) return;
        e.setCancelled(true);
        BlockFace face = blocks.get(player).get(currentBlock);
        blocks.remove(player);
        List<Block> blockList = getBlocks(currentBlock.getLocation(), face, (crazyManager.getLevel(currentItem, CEnchantments.BLAST) - 1));
        BlastUseEvent blastUseEvent = new BlastUseEvent(player, blockList);
        plugin.getServer().getPluginManager().callEvent(blastUseEvent);

        if (!blastUseEvent.isCancelled()) {
            Location originalBlockLocation = currentBlock.getLocation();
            List<BlockProcessInfo> finalBlockList = new ArrayList<>();

            for (Block block : blockList) {
                if (block.getType() != Material.AIR && (crazyManager.getBlockList().contains(block.getType()) || block.getLocation().equals(originalBlockLocation))) {
                    BlockBreakEvent event = new BlockBreakEvent(block, player);
                    crazyManager.addIgnoredEvent(event);
                    plugin.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) finalBlockList.add(new BlockProcessInfo(currentItem, block));

                    crazyManager.removeIgnoredEvent(event);
                }
            }

            //if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) NoCheatPlusSupport.allowPlayer(player);

            //if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
                //SpartanSupport.cancelFastBreak(player);
                //SpartanSupport.cancelNoSwing(player);
                //SpartanSupport.cancelBlockReach(player);
            //}

            int xp = 0;
            HashMap<ItemStack, Integer> drops = new HashMap<>();
            boolean damage = Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Blast-Full-Durability");
            boolean isOre = isOre(currentBlock.getType());
            boolean hasSilkTouch = currentItem.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);
            boolean hasTelepathy = enchantments.contains(CEnchantments.TELEPATHY.getEnchantment());
            boolean hasFurnace = enchantments.contains(CEnchantments.FURNACE.getEnchantment());
            boolean hasAutoSmelt = enchantments.contains(CEnchantments.AUTOSMELT.getEnchantment());
            boolean hasExperience = enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment());

            for (BlockProcessInfo processInfo : finalBlockList) {
                Block block = processInfo.getBlock();
                if (player.getGameMode() == GameMode.CREATIVE || !crazyManager.isDropBlocksBlast()) { // If the user is in creative mode.
                    block.setType(Material.AIR);
                } else { // If the user is in survival mode.
                    // This is to check if the original block the player broke was in the block list.
                    // If it is not then it should be broken and dropped on the ground.

                    if (block.getLocation().equals(originalBlockLocation) && !crazyManager.getBlockList().contains(block.getType())) {
                        block.breakNaturally();
                        continue;
                    }

                    if (hasTelepathy) {
                        TelepathyDrop drop = tools.getTelepathyDrops(processInfo);
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

            //if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) NoCheatPlusSupport.denyPlayer(player);

            for (Entry<ItemStack, Integer> item : drops.entrySet()) {
                item.getKey().setAmount(item.getValue());

                if (methods.isInventoryFull(player)) {
                    try {
                        player.getWorld().dropItem(player.getLocation(), item.getKey());
                    } catch (IllegalArgumentException ignore) {}
                } else {
                    player.getInventory().addItem(item.getKey());
                }
            }

            if (player.getGameMode() != GameMode.CREATIVE && xp > 0) {
                ExperienceOrb orb = currentBlock.getWorld().spawn(currentBlock.getLocation().add(.5, .5, .5), ExperienceOrb.class);
                orb.setExperience(xp);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        if (crazyManager.isIgnoredEvent(e)) return;

        Block block = e.getBlock();
        Player player = e.getPlayer();
        ItemStack item = methods.getItemInHand(player);
        List<CEnchantment> enchantments = crazyManager.getEnchantmentsOnItem(item);
        boolean isOre = isOre(block.getType());

        if (player.getGameMode() != GameMode.CREATIVE) {
            if (CEnchantments.AUTOSMELT.isActivated() && isOre &&
                    (enchantments.contains(CEnchantments.AUTOSMELT.getEnchantment()) && !(enchantments.contains(CEnchantments.BLAST.getEnchantment()) || enchantments.contains(CEnchantments.FURNACE.getEnchantment()) || enchantments.contains(CEnchantments.TELEPATHY.getEnchantment()))) &&
                    CEnchantments.AUTOSMELT.chanceSuccessful(item)) {
                EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.AUTOSMELT, item);
                plugin.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    int dropAmount = 0;
                    dropAmount += crazyManager.getLevel(item, CEnchantments.AUTOSMELT);

                    if (item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS) && methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) dropAmount += getRandomNumber(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));

                    tryCheck(block, item, enchantments, dropAmount);

                    e.setDropItems(false);

                    methods.removeDurability(item, player);
                }
            }

            if (CEnchantments.FURNACE.isActivated() && isOre && (enchantments.contains(CEnchantments.FURNACE.getEnchantment()) && !(enchantments.contains(CEnchantments.BLAST.getEnchantment()) || enchantments.contains(CEnchantments.TELEPATHY.getEnchantment())))) {
                EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.FURNACE, item);
                plugin.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    int dropAmount = 1;

                    if (item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS) && methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) dropAmount += getRandomNumber(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));

                    if (block.getType() == Material.REDSTONE_ORE || block.getType() == Material.COAL_ORE || block.getType() == Material.LAPIS_ORE) dropAmount += methods.percentPick(4, 1);

                    tryCheck(block, item, enchantments, dropAmount);
                }

                e.setDropItems(false);
                methods.removeDurability(item, player);
            }
        }

        if (CEnchantments.EXPERIENCE.isActivated() && !hasSilkTouch(item) && isOre && (enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment()) && !(enchantments.contains(CEnchantments.BLAST.getEnchantment()) || enchantments.contains(CEnchantments.TELEPATHY.getEnchantment())))) {
            int power = crazyManager.getLevel(item, CEnchantments.EXPERIENCE);

            if (CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
                EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.EXPERIENCE, item);
                plugin.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) e.setExpToDrop(e.getExpToDrop() + (power + 2));
            }
        }
    }

    private void tryCheck(Block block, ItemStack item, List<CEnchantment> enchantments, int dropAmount) {
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
            case DEEPSLATE_COAL_ORE,
                    DEEPSLATE_COPPER_ORE,
                    DEEPSLATE_DIAMOND_ORE,
                    DEEPSLATE_EMERALD_ORE,
                    DEEPSLATE_GOLD_ORE,
                    DEEPSLATE_IRON_ORE,
                    DEEPSLATE_LAPIS_ORE,
                    DEEPSLATE_REDSTONE_ORE,
                    COAL_ORE,
                    IRON_ORE,
                    GOLD_ORE,
                    DIAMOND_ORE,
                    EMERALD_ORE,
                    LAPIS_ORE,
                    REDSTONE_ORE,
                    COPPER_ORE,
                    NETHER_GOLD_ORE,
                    NETHER_QUARTZ_ORE ->
                    true;
            default -> false;
        };
    }

    private ItemStack getOreDrop(Material material) {
        return getOreDrop(material, 1);
    }

    private ItemStack getOreDrop(Material material, int amount) {
        ItemBuilder dropItem = new ItemBuilder().setAmount(amount);

        if (material == Material.NETHER_QUARTZ_ORE) {
            dropItem.setMaterial(Material.QUARTZ);
        } else {
            switch (material) {
                case DEEPSLATE_COAL_ORE, COAL_ORE -> dropItem.setMaterial(Material.COAL);
                case DEEPSLATE_COPPER_ORE, COPPER_ORE -> dropItem.setMaterial(Material.COPPER_INGOT);
                case DEEPSLATE_DIAMOND_ORE, DIAMOND_ORE -> dropItem.setMaterial(Material.DIAMOND);
                case DEEPSLATE_EMERALD_ORE, EMERALD_ORE -> dropItem.setMaterial(Material.EMERALD);
                case DEEPSLATE_GOLD_ORE, GOLD_ORE, NETHER_GOLD_ORE -> dropItem.setMaterial(Material.GOLD_INGOT);
                case DEEPSLATE_IRON_ORE, IRON_ORE -> dropItem.setMaterial(Material.IRON_INGOT);
                case DEEPSLATE_LAPIS_ORE, LAPIS_ORE -> dropItem.setMaterial(Material.LAPIS_LAZULI);
                case DEEPSLATE_REDSTONE_ORE, REDSTONE_ORE -> dropItem.setMaterial(Material.REDSTONE);
                default -> dropItem.setMaterial(Material.AIR);
            }
        }

        return dropItem.build();
    }

    private int getRandomNumber(int range) {
        return range > 1 ? random.nextInt(range) : 1;
    }
}