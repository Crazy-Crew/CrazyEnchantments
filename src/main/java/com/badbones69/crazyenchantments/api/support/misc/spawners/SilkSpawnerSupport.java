package com.badbones69.crazyenchantments.api.support.misc.spawners;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerBreakEvent;
import de.dustplanet.util.SilkUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class SilkSpawnerSupport implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    private final Methods methods = plugin.getStarter().getMethods();

    private final SilkUtil api = SilkUtil.hookIntoSilkSpanwers();

    @EventHandler(ignoreCancelled = true)
    public void onSpawnerBreak(SilkSpawnersSpawnerBreakEvent e) {

        Player player = e.getPlayer();
        Block block = e.getBlock();

        if (player == null && block == null) return;

        assert player != null;
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (crazyManager.hasEnchantment(itemStack, CEnchantments.TELEPATHY.getEnchantment())) {
            String mob = api.getCreatureName(e.getEntityID()).toLowerCase().replace(" ", "");

            if (!player.hasPermission("silkspawners.silkdrop." + mob)) return;

            EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.TELEPATHY.getEnchantment(), itemStack);
            plugin.getServer().getPluginManager().callEvent(event);

            ItemStack spawnerItem = api.newSpawnerItem(e.getEntityID(), api.getCustomSpawnerName(api.getCreatureName(e.getEntityID())), 1, false);

            if (!methods.isInventoryFull(player)) player.getInventory().addItem(spawnerItem); else block.getWorld().dropItemNaturally(block.getLocation(), spawnerItem);

            assert block != null;
            block.setType(Material.AIR);
            e.setCancelled(true);
        }
    }
}