package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.multisupport.Version;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

public class Tools implements Listener {
    
    private Random random = new Random();
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    
    @EventHandler
    public void onPlayerClick(PlayerInteractEvent e) {
        updateEffects(e.getPlayer());
    }
    
    @SuppressWarnings("squid:CallToDeprecatedMethod")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Player player = e.getPlayer();
        if (e.isCancelled() || ce.isIgnoredEvent(e) || ignoreBlock(block)) {
            return;
        }
        updateEffects(player);
        if (player.getGameMode() != GameMode.CREATIVE) {
            ItemStack item = Methods.getItemInHand(player);
            List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(item);
            if (CEnchantments.TELEPATHY.isActivated() && enchantments.contains(CEnchantments.TELEPATHY.getEnchantment()) && !enchantments.contains(CEnchantments.BLAST.getEnchantment())) {
                //This checks if the player is breaking a crop with harvester one. The harvester enchantment will control what happens with telepathy here.
                if ((Hoes.getHarvesterCrops().contains(block.getType()) && enchantments.contains(CEnchantments.HARVESTER.getEnchantment())) ||
                //This checks if the block is a spawner and if so the spawner classes will take care of this.
                (block.getType() == (ce.useNewMaterial() ? Material.matchMaterial("SPAWNER") : Material.matchMaterial("MOB_SPAWNER")) && item.getItemMeta().hasEnchants() && item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH))) {
                    return;
                }
                EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.TELEPATHY, item);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    HashMap<ItemStack, Integer> drops = new HashMap<>();
                    for (ItemStack drop : block.getDrops(item)) {
                        if (enchantments.contains(CEnchantments.FURNACE.getEnchantment()) && isOre(block.getType())) {
                            drop = getOreDrop(block.getType());
                        } else if (enchantments.contains(CEnchantments.AUTOSMELT.getEnchantment()) && isOre(block.getType())) {
                            if (CEnchantments.AUTOSMELT.chanceSuccessful(item)) {
                                drop = getOreDrop(block.getType());
                                drop.setAmount(1 + ce.getLevel(item, CEnchantments.AUTOSMELT));
                            }
                        } else {
                            if (item.getItemMeta().hasEnchants()) {
                                if (!item.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH) && getXPOres().contains(block.getType()) && !enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment())) {
                                    ExperienceOrb orb = block.getWorld().spawn(block.getLocation().add(.5, .5, .5), ExperienceOrb.class);
                                    orb.setExperience(Methods.percentPick(7, 3));
                                }
                            } else {
                                if (getXPOres().contains(block.getType()) && !enchantments.contains(CEnchantments.EXPERIENCE.getEnchantment())) {
                                    ExperienceOrb orb = block.getWorld().spawn(block.getLocation().add(.5, .5, .5), ExperienceOrb.class);
                                    orb.setExperience(Methods.percentPick(7, 3));
                                }
                            }
                        }
                        if (hasExperience && !hasSilkTouch && CEnchantments.EXPERIENCE.chanceSuccessful(item)) {
                            int power = ce.getLevel(item, CEnchantments.EXPERIENCE);
                            if (isOre(block.getType())) {
                                ExperienceOrb orb = block.getWorld().spawn(block.getLocation().add(.5, .5, .5), ExperienceOrb.class);
                                orb.setExperience(Methods.percentPick(7, 3) * power);
                            }
                        }
                        if (block.getType() == Material.SUGAR_CANE) {
                            drop.setAmount(0);
                            Location loc = block.getLocation();
                            for (; loc.getBlock().getType() == Material.SUGAR_CANE; loc.add(0, 1, 0)) ;
                            loc.subtract(0, 1, 0);
                            for (; loc.getBlock().getType() == Material.SUGAR_CANE; loc.subtract(0, 1, 0)) {
                                drop.setAmount(drop.getAmount() + 1);
                                loc.getBlock().setType(Material.AIR);
                            }
                        }
                        int amount = drop.getAmount();
                        if (drops.containsKey(drop)) {
                            drops.put(drop, drops.get(drop) + amount);
                        } else {
                            drops.put(drop, amount);
                        }
                    }
                    if (block.getType() == Material.COCOA) {
                        drops.put(new ItemBuilder().setMaterial("COCOA_BEANS", "INK_SACK:3").build(),
                        ce.getNMSSupport().isFullyGrown(block) ? random.nextInt(2) + 2 : 1);//Coco drops 2-3 beans.
                    }
                    for (Entry<ItemStack, Integer> dropList : drops.entrySet()) {
                        ItemStack droppedItem = dropList.getKey();
                        if (!ce.useNewMaterial() && droppedItem.getType() == Material.matchMaterial("INK_SACK") && droppedItem.getDurability() != 3) {//Changes ink sacks to lapis if on 1.12.2-
                            droppedItem.setDurability((short) 4);
                        }
                        if (droppedItem.getType() == Material.WHEAT || droppedItem.getType() == Material.matchMaterial("BEETROOT_SEEDS")) {
                            droppedItem.setAmount(random.nextInt(3));//Wheat and BeetRoots drops 0-3 seeds.
                        } else if (droppedItem.getType() == ce.getMaterial("POTATO", "POTATO_ITEM") ||
                        droppedItem.getType() == ce.getMaterial("CARROT", "CARROT_ITEM")) {
                            droppedItem.setAmount(random.nextInt(4) + 1);//Carrots and Potatoes drop 1-4 of them self's.
                        } else {
                            droppedItem.setAmount(drops.get(droppedItem));
                        }
                        if (Methods.isInventoryFull(player)) {
                            player.getWorld().dropItem(player.getLocation(), droppedItem);
                        } else {
                            player.getInventory().addItem(droppedItem);
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
        }
    }
    
    private void updateEffects(Player player) {
        ItemStack item = Methods.getItemInHand(player);
        if (ce.hasEnchantments(item)) {
            int time = 5 * 20;
            List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(item);
            if (CEnchantments.HASTE.isActivated() && enchantments.contains(CEnchantments.HASTE.getEnchantment())) {
                EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.HASTE, item);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    int power = ce.getLevel(item, CEnchantments.HASTE);
                    player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, time, power - 1));
                }
            }
            if (CEnchantments.OXYGENATE.isActivated() && enchantments.contains(CEnchantments.OXYGENATE.getEnchantment())) {
                EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.OXYGENATE, item);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    player.removePotionEffect(PotionEffectType.WATER_BREATHING);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, time, 5));
                }
            }
        }
    }
    
    private boolean ignoreBlock(Block block) {
        switch (block.getType().name().toLowerCase()) {
            case "air":
            case "shulker_box":
            case "chest":
            case "head":
            case "skull":
                return true;
            default:
                return false;
        }
    }
    
    private ArrayList<Material> getXPOres() {
        ArrayList<Material> ores = new ArrayList<>();
        ores.add(Material.COAL_ORE);
        ores.add(Material.QUARTZ);
        ores.add(Material.DIAMOND_ORE);
        ores.add(Material.EMERALD_ORE);
        ores.add(Material.REDSTONE_ORE);
        ores.add(Material.LAPIS_ORE);
        return ores;
    }
    
    private boolean isOre(Material material) {
        if (material == ce.getMaterial("NETHER_QUARTZ_ORE", "QUARTZ_ORE")) {
            return true;
        }
        switch (material) {
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
    
    private ItemStack getOreDrop(Material material) {
        ItemBuilder dropItem = new ItemBuilder();
        if (material == ce.getMaterial("NETHER_QUARTZ_ORE", "QUARTZ_ORE")) {
            dropItem.setMaterial(Material.QUARTZ);
        } else {
            switch (material) {
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