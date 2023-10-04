package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.paper.api.objects.BlockProcessInfo;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.TelepathyDrop;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentSettings;
import com.badbones69.crazyenchantments.paper.utilities.misc.ItemUtils;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.List;
import java.util.Objects;

public class ToolEnchantments implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    // Settings.
    private final EnchantmentSettings enchantmentSettings = starter.getEnchantmentSettings();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    private final List<String> ignoreBlockTypes = Lists.newArrayList("air", "shulker_box", "chest", "head", "skull");

    @EventHandler()
    public void onPlayerClick(PlayerInteractEvent event) {
        //Check what hand is being used as the event fires for each hand.
        if (Objects.equals(event.getHand(), EquipmentSlot.HAND)) updateEffects(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isDropItems()) return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        if (ignoreBlockTypes(event.getBlock())) return;

        Player player = event.getPlayer();
        Material brokenBlock = event.getBlock().getType();
        ItemStack tool = methods.getItemInHand(player);

        List<CEnchantment> enchantments = enchantmentBookSettings.getEnchantmentsOnItem(tool);

        if (!enchantments.contains(CEnchantments.TELEPATHY.getEnchantment())) return;

        if ((enchantmentSettings.getHarvesterCrops().contains(brokenBlock) && enchantments.contains(CEnchantments.HARVESTER.getEnchantment())) ||
            (brokenBlock == Material.SPAWNER)) return;
        // This checks if the block is a spawner and if so the spawner classes will take care of this.
        // If Epic Spawners is enabled then telepathy will give the item from the API.
        // Otherwise, CE will ignore the spawner in this event.

        EnchantmentUseEvent useEvent = new EnchantmentUseEvent(player, CEnchantments.TELEPATHY, tool);
        plugin.getServer().getPluginManager().callEvent(useEvent);

        if (useEvent.isCancelled()) return;

        event.setExpToDrop(0);
        event.setDropItems(false);

        if (enchantmentSettings.getHarvesterCrops().contains(brokenBlock)) {
            ItemUtils.giveCropDrops(player, event.getBlock());
        } else {
            TelepathyDrop drop = enchantmentSettings.getTelepathyDrops(new BlockProcessInfo(tool, event.getBlock()));

            ItemUtils.giveDrops(player, drop.getItem());

            if (!(drop.getSugarCaneBlocks().isEmpty())) drop.getSugarCaneBlocks().forEach(cane -> cane.setType(Material.AIR));

            if (drop.hasXp()) event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(.5, .5, .5), ExperienceOrb.class).setExperience(drop.getXp());
        }

        methods.removeDurability(tool, player);
        event.setDropItems(false);
    }

    private void updateEffects(Player player) {
        ItemStack item = methods.getItemInHand(player);
        if (!enchantmentBookSettings.hasEnchantments(item)) return;

        List<CEnchantment> enchantments = enchantmentBookSettings.getEnchantmentsOnItem(item);
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