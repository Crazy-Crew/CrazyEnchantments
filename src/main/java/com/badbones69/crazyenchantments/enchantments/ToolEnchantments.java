package com.badbones69.crazyenchantments.enchantments;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.api.objects.BlockProcessInfo;
import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.api.objects.TelepathyDrop;
import com.badbones69.crazyenchantments.controllers.settings.EnchantmentSettings;
import com.google.common.collect.Lists;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
import java.util.*;

public class ToolEnchantments implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    private final EnchantmentSettings enchantmentSettings = plugin.getEnchantmentSettings();

    private final Methods methods = plugin.getStarter().getMethods();

    private final List<String> ignoreBlockTypes = Lists.newArrayList("air", "shulker_box", "chest", "head", "skull");

    @EventHandler(ignoreCancelled = true)
    public void onPlayerClick(PlayerInteractEvent e) {
        updateEffects(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (crazyManager.isIgnoredEvent(event) || ignoreBlockTypes(block)) return;

        ItemStack item = methods.getItemInHand(player);

        updateEffects(player);

        if (player.getGameMode() != GameMode.CREATIVE) {
            List<CEnchantment> enchantments = crazyManager.getEnchantmentsOnItem(item);

            if (enchantments.contains(CEnchantments.TELEPATHY.getEnchantment()) && !enchantments.contains(CEnchantments.BLAST.getEnchantment())) {
                // This checks if the player is breaking a crop with harvester one. The harvester enchantment will control what happens with telepathy here.
                if ((enchantmentSettings.getHarvesterCrops().contains(block.getType()) && enchantments.contains(CEnchantments.HARVESTER.getEnchantment())) ||
                        // This checks if the block is a spawner and if so the spawner classes will take care of this.
                        // If Epic Spawners is enabled then telepathy will give the item from the API.
                        // Otherwise, CE will ignore the spawner in this event.
                        (block.getType() == Material.SPAWNER)) return;

                EnchantmentUseEvent useEvent = new EnchantmentUseEvent(player, CEnchantments.TELEPATHY, item);
                plugin.getServer().getPluginManager().callEvent(useEvent);

                if (!useEvent.isCancelled()) {
                    event.setExpToDrop(0);
                    event.setDropItems(false);
                    TelepathyDrop drop = enchantmentSettings.getTelepathyDrops(new BlockProcessInfo(item, block));

                    if (methods.isInventoryFull(player)) {
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

                    methods.removeDurability(item, player);
                }
            }
        }
    }

    private void updateEffects(Player player) {
        ItemStack item = methods.getItemInHand(player);
        if (!crazyManager.hasEnchantments(item)) return;

        List<CEnchantment> enchantments = crazyManager.getEnchantmentsOnItem(item);
        int potionTime = 5 * 20;

        if (enchantments.contains(CEnchantments.HASTE.getEnchantment())) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.HASTE, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                int power = crazyManager.getLevel(item, CEnchantments.HASTE);
                player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, potionTime, power - 1));
            }
        }

        if (enchantments.contains(CEnchantments.OXYGENATE.getEnchantment())) {
            EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.OXYGENATE, item);
            plugin.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                player.removePotionEffect(PotionEffectType.WATER_BREATHING);
                player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, potionTime, 5));
            }
        }

    }

    private boolean ignoreBlockTypes(Block block) {
        for (String name : ignoreBlockTypes) {
            if (block.getType().name().toLowerCase().contains(name)) return true;
        }

        return false;
    }
}