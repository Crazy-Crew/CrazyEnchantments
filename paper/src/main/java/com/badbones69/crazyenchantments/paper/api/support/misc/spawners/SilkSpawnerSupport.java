package com.badbones69.crazyenchantments.paper.api.support.misc.spawners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
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

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    private final SilkUtil api = SilkUtil.hookIntoSilkSpanwers();

    @EventHandler(ignoreCancelled = true)
    public void onSpawnerBreak(SilkSpawnersSpawnerBreakEvent e) { //TODO remove silkSpawner support. (If they were to handle drops properly, then there would never be a need)
        Player player = e.getPlayer();
        Block block = e.getBlock();

        if (player == null && block == null) return;

        assert player != null;
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (enchantmentBookSettings.getEnchantments(itemStack).containsKey(CEnchantments.TELEPATHY.getEnchantment())) {
            String mob = api.getCreatureName(e.getEntityID()).toLowerCase().replace(" ", "");

            if (!player.hasPermission("silkspawners.silkdrop." + mob)) return;

            EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.TELEPATHY.getEnchantment(), itemStack);
            plugin.getServer().getPluginManager().callEvent(event);

            ItemStack spawnerItem = api.newSpawnerItem(e.getEntityID(), api.getCustomSpawnerName(api.getCreatureName(e.getEntityID())), 1, false);

            methods.addItemToInventory(player, spawnerItem);

            assert block != null;
            block.setType(Material.AIR);
            e.setCancelled(true);
        }
    }
}