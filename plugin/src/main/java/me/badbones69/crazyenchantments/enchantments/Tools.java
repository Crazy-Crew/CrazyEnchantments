package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import me.badbones69.crazyenchantments.api.objects.BlockProcessInfo;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.api.objects.TelepathyDrop;
import me.badbones69.crazyenchantments.multisupport.Version;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Tools implements Listener {
    
    private static Random random = new Random();
    private int potionTime = 5 * 20;
    private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
    private static List<String> ignoreBlockTypes = Arrays.asList("air", "shulker_box", "chest", "head", "skull");
    
    @EventHandler
    public void onPlayerClick(PlayerInteractEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateEffects(e.getPlayer());
            }
        }.runTaskAsynchronously(ce.getPlugin());
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Player player = e.getPlayer();
        if (e.isCancelled() || ce.isIgnoredEvent(e) || ignoreBlockTypes(block)) {
            return;
        }
        ItemStack item = Methods.getItemInHand(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                updateEffects(player);
            }
        }.runTaskAsynchronously(ce.getPlugin());
        if (player.getGameMode() != GameMode.CREATIVE) {
            List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(item);
            if (enchantments.contains(CEnchantments.TELEPATHY.getEnchantment()) && !enchantments.contains(CEnchantments.BLAST.getEnchantment())) {
                //This checks if the player is breaking a crop with harvester one. The harvester enchantment will control what happens with telepathy here.
                if ((Hoes.getHarvesterCrops().contains(block.getType()) && enchantments.contains(CEnchantments.HARVESTER.getEnchantment())) ||
                //This checks if the block is a spawner and if so the spawner classes will take care of this.
                //If Epic Spawners is enabled then telepathy will give the item from the API.
                //Otherwise, CE will ignore the spawner in this event.
                (block.getType() == ce.getMaterial("SPAWNER", "MOB_SPAWNER"))) {
                    return;
                }
                EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.TELEPATHY, item);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    e.setExpToDrop(0);
                    //setDropItems was added in 1.12+
                    if (Version.isNewer(Version.v1_11_R1)) e.setDropItems(false);
                    TelepathyDrop drop = getTelepathyDrops(new BlockProcessInfo(item, block));
                    if (Methods.isInventoryFull(player)) {
                        player.getWorld().dropItem(player.getLocation(), drop.getItem());
                    } else {
                        player.getInventory().addItem(drop.getItem());
                    }
                    if (drop.getSugarCaneBlocks().isEmpty()) {
                        block.setType(Material.AIR);
                    } else {
                        drop.getSugarCaneBlocks().forEach(cane -> cane.setType(Material.AIR));
                    }
                    if (drop.hasXp()) {
                        ExperienceOrb orb = block.getWorld().spawn(block.getLocation().add(.5, .5, .5), ExperienceOrb.class);
                        orb.setExperience(drop.getXp());
                    }
                    Methods.removeDurability(item, player);
                }
            }
        }
    }
    
    @SuppressWarnings("squid:CallToDeprecatedMethod")
    public static TelepathyDrop getTelepathyDrops(BlockProcessInfo processInfo) {
        ItemStack item = processInfo.getItem();
        Block block = processInfo.getBlock();
        List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(item);
        List<Block> sugarCaneBlocks = new ArrayList<>();
        boolean isOre = isOre(block);
        boolean hasSilkTouch = item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);
        boolean hasFurnace = enchantments.contains(CEnchantments.FURNACE.getEnchantment());
        boolean hasAutoSmelt = enchantments.contains(CEnchantments.AUTOSMELT.getEnchantment());
        boolean hasExperience = enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment());
        ItemBuilder itemDrop = null;
        int xp = 0;

        for (ItemStack drop : processInfo.getDrops()) {
            if (itemDrop == null) {
                //Amount is set to 0 as it adds to the drop amount and so it would add 1 to many.
                itemDrop = new ItemBuilder().setMaterial(drop.getType()).setAmount(0);
            }
            if (!hasSilkTouch) {
                if (hasFurnace && isOre) {
                    itemDrop = ItemBuilder.convertItemStack(getOreDrop(block)).setAmount(0);
                } else if (hasAutoSmelt && isOre && CEnchantments.AUTOSMELT.chanceSuccessful(item)) {
                    itemDrop = ItemBuilder.convertItemStack(getOreDrop(block)).setAmount(ce.getLevel(item, CEnchantments.AUTOSMELT));
                }
                if (hasOreXP(block)) {
                    xp = Methods.percentPick(7, 3);
                    if (hasExperience && CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
                        xp += Methods.percentPick(7, 3) * ce.getLevel(item, CEnchantments.EXPERIENCE);
                    }
                }
            }
            if (block.getType() == ce.getMaterial("SUGAR_CANE", "SUGAR_CANE_BLOCK")) {
                sugarCaneBlocks = getSugarCaneBlocks(block);
                drop.setAmount(sugarCaneBlocks.size());
            }
            itemDrop.addAmount(drop.getAmount());
        }

        if (itemDrop == null) {
            //In case the drop is still null as no drops were found.
            itemDrop = new ItemBuilder().setMaterial(block.getType());
        }
        if (hasSilkTouch && Version.isOlder(Version.v1_14_R1)) {
            if (block.getType() == Material.ANVIL) {
                byte data = block.getData();
                if (data == 4) {
                    data = 1;
                } else if (data == 8) {
                    data = 2;
                }
                itemDrop.setMaterial(block.getType()).setDamage(data);
            } else {
                //Set the amount to 1 as some blocks like GlowStone try to give multiple blocks.
                itemDrop.setMaterial(block.getType()).setDamage(block.getData()).setAmount(1);
            }
        }
        if (block.getType() == Material.COCOA) {
            //Coco drops 2-3 beans.
            itemDrop.setMaterial("COCOA_BEANS", "INK_SACK:3")
                    .setAmount(ce.getNMSSupport().isFullyGrown(block) ? random.nextInt(2) + 2 : 1);
        }
        //Changes ink sacks to lapis if on 1.12.2-
        if (Version.isOlder(Version.v1_13_R2) && itemDrop.getMaterial() == Material.matchMaterial("INK_SACK") && itemDrop.getDamage() != 3) {
            itemDrop.setDamage(4);
        }
        if (itemDrop.getMaterial() == Material.WHEAT || itemDrop.getMaterial() == Material.matchMaterial("BEETROOT_SEEDS")) {
            itemDrop.setAmount(random.nextInt(3));//Wheat and BeetRoots drops 0-3 seeds.
        } else if (itemDrop.getMaterial() == ce.getMaterial("POTATO", "POTATO_ITEM") || itemDrop.getMaterial() == ce.getMaterial("CARROT", "CARROT_ITEM")) {
            itemDrop.setAmount(random.nextInt(4) + 1);//Carrots and Potatoes drop 1-4 of them self's.
        }
        return new TelepathyDrop(itemDrop.build(), xp, sugarCaneBlocks);
    }
    
    private static List<Block> getSugarCaneBlocks(Block block) {
        List<Block> sugarCaneBlocks = new ArrayList<>();
        Block cane = block;
        while (cane.getType() == ce.getMaterial("SUGAR_CANE", "SUGAR_CANE_BLOCK")) {
            sugarCaneBlocks.add(cane);
            cane = cane.getLocation().add(0, 1, 0).getBlock();
        }
        
        Collections.reverse(sugarCaneBlocks);
        return sugarCaneBlocks;
    }
    
    private void updateEffects(Player player) {
        ItemStack item = Methods.getItemInHand(player);
        if (ce.hasEnchantments(item)) {
            List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(item);
            if (enchantments.contains(CEnchantments.HASTE.getEnchantment())) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.HASTE, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            int power = ce.getLevel(item, CEnchantments.HASTE);
                            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, potionTime, power - 1));
                        }
                    }
                }.runTask(ce.getPlugin());
            }
            if (enchantments.contains(CEnchantments.OXYGENATE.getEnchantment())) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.OXYGENATE, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            player.removePotionEffect(PotionEffectType.WATER_BREATHING);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, potionTime, 5));
                        }
                    }
                }.runTask(ce.getPlugin());
            }
        }
    }
    
    private static boolean ignoreBlockTypes(Block block) {
        for (String name : ignoreBlockTypes) {
            if (block.getType().name().toLowerCase().contains(name)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean hasOreXP(Block block) {
        switch (block.getType()) {
            case COAL_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case LAPIS_ORE:
            case REDSTONE_ORE:
                return true;
            default:
                return false;
        }
    }
    
    private static boolean isOre(Block block) {
        if (block.getType() == ce.getMaterial("NETHER_QUARTZ_ORE", "QUARTZ_ORE")) {
            return true;
        }
        switch (block.getType()) {
            case COAL_ORE:
            case IRON_ORE:
            case GOLD_ORE:
            case DIAMOND_ORE:
            case EMERALD_ORE:
            case LAPIS_ORE:
            case REDSTONE_ORE:
                return true;
            default:
                return false;
        }
    }
    
    private static ItemStack getOreDrop(Block block) {
        ItemBuilder dropItem = new ItemBuilder();
        if (block.getType() == ce.getMaterial("NETHER_QUARTZ_ORE", "QUARTZ_ORE")) {
            dropItem.setMaterial(Material.QUARTZ);
        } else {
            switch (block.getType()) {
                case COAL_ORE:
                    dropItem.setMaterial(Material.COAL);
                    break;
                case IRON_ORE:
                    dropItem.setMaterial(Material.IRON_INGOT);
                    break;
                case GOLD_ORE:
                    dropItem.setMaterial(Material.GOLD_INGOT);
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
    
}