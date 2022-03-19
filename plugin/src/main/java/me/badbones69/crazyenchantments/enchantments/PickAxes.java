package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.BlastUseEvent;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import me.badbones69.crazyenchantments.api.objects.BlockProcessInfo;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.api.objects.TelepathyDrop;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import me.badbones69.crazyenchantments.multisupport.Version;
import me.badbones69.crazyenchantments.multisupport.anticheats.AACSupport;
import me.badbones69.crazyenchantments.multisupport.anticheats.NoCheatPlusSupport;
import me.badbones69.premiumhooks.anticheat.SpartanSupport;
import org.bukkit.Bukkit;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

public class PickAxes implements Listener {
    
    private Random random = new Random();
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    private HashMap<Player, HashMap<Block, BlockFace>> blocks = new HashMap<>();
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.getAction() == Action.LEFT_CLICK_BLOCK && CEnchantments.BLAST.isActivated()) {
            ItemStack item = Methods.getItemInHand(player);
            Block block = e.getClickedBlock();
            if (ce.hasEnchantment(item, CEnchantments.BLAST)) {
                HashMap<Block, BlockFace> blockFace = new HashMap<>();
                blockFace.put(block, e.getBlockFace());
                blocks.put(player, blockFace);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlastBreak(BlockBreakEvent e) {
        if (e.isCancelled() || ce.isIgnoredEvent(e) || !CEnchantments.BLAST.isActivated()) return;
        Player player = e.getPlayer();
        Block block = e.getBlock();
        ItemStack item = Methods.getItemInHand(player);
        if (blocks.containsKey(player)) {
            List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(item);
            if (blocks.get(player).containsKey(block) && enchantments.contains(CEnchantments.BLAST.getEnchantment())) {
                e.setCancelled(true);
                BlockFace face = blocks.get(player).get(block);
                blocks.remove(player);
                List<Block> blockList = getBlocks(block.getLocation(), face, (ce.getLevel(item, CEnchantments.BLAST) - 1));
                BlastUseEvent blastUseEvent = new BlastUseEvent(player, blockList);
                Bukkit.getPluginManager().callEvent(blastUseEvent);
                if (!blastUseEvent.isCancelled()) {
                    Location originalBlockLocation = block.getLocation();
                    List<BlockProcessInfo> finalBlockList = new ArrayList<>();
                    for (Block b : blockList) {
                        if (b.getType() != Material.AIR && (ce.getBlockList().contains(b.getType()) || b.getLocation().equals(originalBlockLocation))) {
                            BlockBreakEvent event = new BlockBreakEvent(b, player);
                            ce.addIgnoredEvent(event);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) { //This stops players from breaking blocks that might be in protected areas.
                                finalBlockList.add(new BlockProcessInfo(item, b));
                            }
                            ce.removeIgnoredEvent(event);
                        }
                    }
                    new BukkitRunnable() { // Run async to help offload some lag.
                        @Override
                        public void run() {
                            HashMap<ItemStack, Integer> drops = new HashMap<>();
                            if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) {
                                NoCheatPlusSupport.exemptPlayer(player);
                            }
                            if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
                                SpartanSupport.cancelFastBreak(player);
                                SpartanSupport.cancelNoSwing(player);
                                SpartanSupport.cancelBlockReach(player);
                            }
                            if (SupportedPlugins.AAC.isPluginLoaded()) {
                                AACSupport.exemptPlayer(player);
                            }
                            int xp = 0;
                            boolean damage = Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Blast-Full-Durability");
                            boolean isOre = isOre(block.getType());
                            boolean hasSilkTouch = item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);
                            boolean hasTelepathy = enchantments.contains(CEnchantments.TELEPATHY.getEnchantment());
                            boolean hasFurnace = enchantments.contains(CEnchantments.FURNACE.getEnchantment());
                            boolean hasAutoSmelt = enchantments.contains(CEnchantments.AUTOSMELT.getEnchantment());
                            boolean hasExperience = enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment());
                            for (BlockProcessInfo processInfo : finalBlockList) {
                                Block block = processInfo.getBlock();
                                if (player.getGameMode() == GameMode.CREATIVE) { //If the user is in creative mode.
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            block.setType(Material.AIR);
                                        }
                                    }.runTask(ce.getPlugin());
                                } else { //If the user is in survival mode.
                                    //This is to check if the original block the player broke was in the block list.
                                    //If it is not then it should be broken and dropped on the ground.
                                    if (block.getLocation().equals(originalBlockLocation) && !ce.getBlockList().contains(block.getType())) {
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                block.breakNaturally();
                                            }
                                        }.runTask(ce.getPlugin());
                                        continue;
                                    }
                                    if (hasTelepathy) {
                                        TelepathyDrop drop = Tools.getTelepathyDrops(processInfo);
                                        drops.put(drop.getItem(), drops.getOrDefault(drop.getItem(), 0) + drop.getItem().getAmount());
                                        xp += drop.getXp();
                                    } else {
                                        if (hasFurnace && isOre) {
                                            ItemStack finalDrop = getOreDrop(block.getType());
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        block.getWorld().dropItem(block.getLocation(), finalDrop);
                                                    } catch (IllegalArgumentException ignore) {
                                                    }
                                                }
                                            }.runTask(ce.getPlugin());
                                        } else if (hasAutoSmelt && isOre) {
                                            for (ItemStack drop : block.getDrops(item)) {
                                                if (CEnchantments.AUTOSMELT.chanceSuccessful(item)) {
                                                    drop = getOreDrop(block.getType());
                                                    drop.setAmount(ce.getLevel(item, CEnchantments.AUTOSMELT));
                                                }
                                                ItemStack finalDrop = drop;
                                                new BukkitRunnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            block.getWorld().dropItem(block.getLocation(), finalDrop);
                                                        } catch (IllegalArgumentException ignore) {
                                                        }
                                                    }
                                                }.runTask(ce.getPlugin());
                                            }
                                        } else {
                                            for (ItemStack drop : block.getDrops(item)) {
                                                if (drop.getType() != Material.AIR) {
                                                    new BukkitRunnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                block.getWorld().dropItem(block.getLocation(), drop);
                                                            } catch (IllegalArgumentException ignore) {
                                                            }
                                                        }
                                                    }.runTask(ce.getPlugin());
                                                }
                                                if (drop.getType() == Material.REDSTONE_ORE || drop.getType() == Material.LAPIS_ORE || drop.getType() == Material.GLOWSTONE) {
                                                    break;
                                                }
                                            }
                                        }
                                        //This is found here as telepathy takes care of this part.
                                        if (!hasSilkTouch && isOre) {
                                            xp = Methods.percentPick(7, 3);
                                            if (hasExperience && CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
                                                xp += Methods.percentPick(7, 3) * ce.getLevel(item, CEnchantments.EXPERIENCE);
                                            }
                                        }
                                    }
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            block.setType(Material.AIR);
                                        }
                                    }.runTask(ce.getPlugin());
                                    if (damage) {
                                        Methods.removeDurability(item, player);
                                    }
                                }
                            }
                            if (!damage) {
                                Methods.removeDurability(item, player);
                            }
                            if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) {
                                NoCheatPlusSupport.unexemptPlayer(player);
                            }
                            if (SupportedPlugins.AAC.isPluginLoaded()) {
                                AACSupport.unexemptPlayer(player);
                            }
                            for (Entry<ItemStack, Integer> item : drops.entrySet()) {
                                item.getKey().setAmount(item.getValue());
                                if (Methods.isInventoryFull(player)) {
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                player.getWorld().dropItem(player.getLocation(), item.getKey());
                                            } catch (IllegalArgumentException ignore) {
                                            }
                                        }
                                    }.runTask(ce.getPlugin());
                                } else {
                                    player.getInventory().addItem(item.getKey());
                                }
                            }
                            if (player.getGameMode() != GameMode.CREATIVE && xp > 0) {
                                int finalXp = xp;
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        ExperienceOrb orb = block.getWorld().spawn(block.getLocation().add(.5, .5, .5), ExperienceOrb.class);
                                        orb.setExperience(finalXp);
                                    }
                                }.runTask(ce.getPlugin());
                            }
                        }
                    }.runTaskAsynchronously(ce.getPlugin());
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.isCancelled() || ce.isIgnoredEvent(e)) return;
        Block block = e.getBlock();
        Player player = e.getPlayer();
        ItemStack item = Methods.getItemInHand(player);
        List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(item);
        boolean isOre = isOre(block.getType());
        if (player.getGameMode() != GameMode.CREATIVE) {
            if (CEnchantments.AUTOSMELT.isActivated() && isOre &&
            (enchantments.contains(CEnchantments.AUTOSMELT.getEnchantment()) && !(enchantments.contains(CEnchantments.BLAST.getEnchantment()) || enchantments.contains(CEnchantments.FURNACE.getEnchantment()) || enchantments.contains(CEnchantments.TELEPATHY.getEnchantment()))) &&
            CEnchantments.AUTOSMELT.chanceSuccessful(item)) {
                EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.AUTOSMELT, item);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    int dropAmount = 0;
                    dropAmount += ce.getLevel(item, CEnchantments.AUTOSMELT);
                    if (item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS) && Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
                        dropAmount += getRandomNumber(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));
                    }
                    try {
                        block.getWorld().dropItem(block.getLocation().add(.5, 0, .5), getOreDrop(block.getType(), dropAmount));
                    } catch (IllegalArgumentException ignore) {
                    }
                    if (CEnchantments.EXPERIENCE.isActivated() && enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment()) && CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
                        int power = ce.getLevel(item, CEnchantments.EXPERIENCE);
                        if (isOre) {
                            ExperienceOrb orb = block.getWorld().spawn(block.getLocation(), ExperienceOrb.class);
                            orb.setExperience(Methods.percentPick(7, 3) * power);
                        }
                    }
                    if (Version.isNewer(Version.v1_11_R1)) {
                        e.setDropItems(false);
                    } else {
                        block.setType(Material.AIR);
                    }
                    Methods.removeDurability(item, player);
                }
            }
            if (CEnchantments.FURNACE.isActivated() && isOre && (enchantments.contains(CEnchantments.FURNACE.getEnchantment()) && !(enchantments.contains(CEnchantments.BLAST.getEnchantment()) || enchantments.contains(CEnchantments.TELEPATHY.getEnchantment())))) {
                EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.FURNACE, item);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    int dropAmount = 1;
                    if (item.getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS) && Methods.randomPicker(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS), 3)) {
                        dropAmount += getRandomNumber(item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));
                    }
                    if (block.getType() == Material.REDSTONE_ORE || block.getType() == Material.COAL_ORE || block.getType() == Material.LAPIS_ORE) {
                        dropAmount += Methods.percentPick(4, 1);
                    }
                    try {
                        block.getWorld().dropItem(block.getLocation().add(.5, 0, .5), getOreDrop(block.getType(), dropAmount));
                    } catch (IllegalArgumentException ignore) {
                    }
                    if (CEnchantments.EXPERIENCE.isActivated() && enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment()) && CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
                        int power = ce.getLevel(item, CEnchantments.EXPERIENCE);
                        if (isOre) {
                            ExperienceOrb orb = block.getWorld().spawn(block.getLocation(), ExperienceOrb.class);
                            orb.setExperience(Methods.percentPick(7, 3) * power);
                        }
                    }
                }
                if (Version.isNewer(Version.v1_11_R1)) {
                    e.setDropItems(false);
                } else {
                    block.setType(Material.AIR);
                }
                Methods.removeDurability(item, player);
            }
        }
        if (CEnchantments.EXPERIENCE.isActivated() && !hasSilkTouch(item) && isOre && (enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment()) && !(enchantments.contains(CEnchantments.BLAST.getEnchantment()) || enchantments.contains(CEnchantments.TELEPATHY.getEnchantment())))) {
            int power = ce.getLevel(item, CEnchantments.EXPERIENCE);
            if (CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
                EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.EXPERIENCE, item);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    e.setExpToDrop(e.getExpToDrop() + (power + 2));
                }
            }
        }
    }
    
    private boolean hasSilkTouch(ItemStack item) {
        return item.hasItemMeta() && item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);
    }
    
    private List<Block> getBlocks(Location loc, BlockFace blockFace, Integer depth) {
        Location loc2 = loc.clone();
        switch (blockFace) {
            case SOUTH:
                loc.add(-1, 1, -depth);
                loc2.add(1, -1, 0);
                break;
            case WEST:
                loc.add(depth, 1, -1);
                loc2.add(0, -1, 1);
                break;
            case EAST:
                loc.add(-depth, 1, 1);
                loc2.add(0, -1, -1);
                break;
            case NORTH:
                loc.add(1, 1, depth);
                loc2.add(-1, -1, 0);
                break;
            case UP:
                loc.add(-1, -depth, -1);
                loc2.add(1, 0, 1);
                break;
            case DOWN:
                loc.add(1, depth, 1);
                loc2.add(-1, 0, -1);
                break;
            default:
                break;
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
    
    private boolean isOre(Material material) {
        if (material == ce.getMaterial("NETHER_QUARTZ_ORE", "QUARTZ_ORE")) {
            return true;
        }
        switch (material) {
            case DEEPSLATE_COAL_ORE:
            case DEEPSLATE_COPPER_ORE:
            case DEEPSLATE_DIAMOND_ORE:
            case DEEPSLATE_EMERALD_ORE:
            case DEEPSLATE_GOLD_ORE:
            case DEEPSLATE_IRON_ORE:
            case DEEPSLATE_LAPIS_ORE:
            case DEEPSLATE_REDSTONE_ORE:
            case COAL_ORE:
            case IRON_ORE:
            case GOLD_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case LAPIS_ORE:
            case REDSTONE_ORE:
            case COPPER_ORE:
            case NETHER_GOLD_ORE:
                return true;
            default:
                return false;
        }
    }
    
    private ItemStack getOreDrop(Material material) {
        return getOreDrop(material, 1);
    }
    
    private ItemStack getOreDrop(Material material, int amount) {
        ItemBuilder dropItem = new ItemBuilder().setAmount(amount);
        if (material == ce.getMaterial("NETHER_QUARTZ_ORE", "QUARTZ_ORE")) {
            dropItem.setMaterial(Material.QUARTZ);
        } else {
            switch (material) {
                case DEEPSLATE_COAL_ORE:
                    dropItem.setMaterial(Material.DEEPSLATE_COAL_ORE);
                    break;
                case DEEPSLATE_COPPER_ORE:
                case COPPER_ORE:
                    dropItem.setMaterial(Material.COPPER_INGOT);
                    break;
                case DEEPSLATE_DIAMOND_ORE:
                    dropItem.setMaterial(Material.DEEPSLATE_DIAMOND_ORE);
                    break;
                case DEEPSLATE_EMERALD_ORE:
                    dropItem.setMaterial(Material.DEEPSLATE_EMERALD_ORE);
                    break;
                case DEEPSLATE_GOLD_ORE:
                case GOLD_ORE:
                case NETHER_GOLD_ORE:
                    dropItem.setMaterial(Material.GOLD_INGOT);
                    break;
                case DEEPSLATE_IRON_ORE:
                case IRON_ORE:
                    dropItem.setMaterial(Material.IRON_INGOT);
                    break;
                case DEEPSLATE_LAPIS_ORE:
                    dropItem.setMaterial(Material.DEEPSLATE_LAPIS_ORE);
                    break;
                case DEEPSLATE_REDSTONE_ORE:
                    dropItem.setMaterial(Material.DEEPSLATE_REDSTONE_ORE);
                    break;
                case COAL_ORE:
                    dropItem.setMaterial(Material.COAL);
                    break;
                case DIAMOND_ORE:
                    dropItem.setMaterial(Material.DIAMOND);
                    break;
                case EMERALD_ORE:
                    dropItem.setMaterial(Material.EMERALD);
                    break;
                case LAPIS_ORE:
                    dropItem.setMaterial("LAPIS_LAZULI", "INK_SACK:4");
                    break;
                case REDSTONE_ORE:
                    dropItem.setMaterial(Material.REDSTONE);
                    break;
                default:
                    dropItem.setMaterial(Material.AIR);
                    break;
            }
        }
        return dropItem.build();
    }
    
    private int getRandomNumber(int range) {
        return range > 1 ? random.nextInt(range > 0 ? (range) : 1) : 1;
    }
    
}