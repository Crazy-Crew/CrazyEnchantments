package com.badbones69.crazyenchantments.api.multisupport.misc.spawners;

import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerBreakEvent;
import de.dustplanet.util.SilkUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class SilkSpawnerSupport implements Listener {

    private final SilkUtil api = SilkUtil.hookIntoSilkSpanwers();

    private final CrazyManager crazyManager = CrazyManager.getInstance();

    @EventHandler
    public void onSpawnerBreak(SilkSpawnersSpawnerBreakEvent e) {
        if (e.isCancelled()) return;

        Player player = e.getPlayer();
        Block block = e.getBlock();

        if (player == null && block == null && player.getGameMode() == GameMode.CREATIVE) return;

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (crazyManager.hasEnchantment(itemStack, CEnchantments.TELEPATHY.getEnchantment())) {
            String mob = api.getCreatureName(e.getEntityID()).toLowerCase().replace(" ", "");

            if (!player.hasPermission("silkspawners.silkdrop." + mob)) return;

            EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.TELEPATHY.getEnchantment(), itemStack);
            crazyManager.getPlugin().getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) return;

            ItemStack spawnerItem = api.newSpawnerItem(e.getEntityID(), api.getCustomSpawnerName(api.getCreatureName(e.getEntityID())), 1, false);

            if (!Methods.isInventoryFull(player)) player.getInventory().addItem(spawnerItem); else block.getWorld().dropItemNaturally(block.getLocation(), spawnerItem);

            block.setType(Material.AIR);
            e.setCancelled(true);
        }
    }
}