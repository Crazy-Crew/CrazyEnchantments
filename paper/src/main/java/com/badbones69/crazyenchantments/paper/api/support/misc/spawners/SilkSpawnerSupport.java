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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SilkSpawnerSupport implements Listener {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Starter starter = this.plugin.getStarter();

    private final Methods methods = this.starter.getMethods();

    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    private final SilkUtil api = SilkUtil.hookIntoSilkSpanwers();

    @EventHandler(ignoreCancelled = true)
    public void onSpawnerBreak(SilkSpawnersSpawnerBreakEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();

        if (player == null) return;

        if (block == null) return;

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (this.enchantmentBookSettings.hasEnchantment(itemStack, CEnchantments.TELEPATHY.getEnchantment())) {
            String mob = this.api.getCreatureName(e.getEntityID()).toLowerCase().replace(" ", "");

            if (!player.hasPermission("silkspawners.silkdrop." + mob)) return;

            EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.TELEPATHY.getEnchantment(), itemStack);
            this.plugin.getServer().getPluginManager().callEvent(event);

            ItemStack spawnerItem = this.api.newSpawnerItem(e.getEntityID(), this.api.getCustomSpawnerName(this.api.getCreatureName(e.getEntityID())), 1, false);

            if (!this.methods.isInventoryFull(player)) player.getInventory().addItem(spawnerItem); else block.getWorld().dropItemNaturally(block.getLocation(), spawnerItem);

            block.setType(Material.AIR);
            e.setCancelled(true);
        }
    }
}