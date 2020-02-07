package me.badbones69.crazyenchantments.multisupport;

import de.candc.SilkSpawners;
import de.candc.api.SilkSpawnersAPI;
import de.candc.events.SpawnerBreakEvent;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class SilkSpawnersCandcSupport implements Listener {
    
    private SilkSpawnersAPI api = SilkSpawners.getApi();
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(SpawnerBreakEvent e) {
        if (e.isCancelled()) return;
        Player player = e.getPlayer();
        Block block = e.getSpawner();
        if (player != null && block != null && player.getGameMode() != GameMode.CREATIVE) {
            ItemStack item = Methods.getItemInHand(player);
            if (player.hasPermission("silkspawners.break") && CEnchantments.TELEPATHY.isActivated() && ce.hasEnchantments(item) && ce.hasEnchantment(item, CEnchantments.TELEPATHY.getEnchantment())) {
                EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.TELEPATHY.getEnchantment(), item);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return;
                }
                ItemStack it = api.getSpawner(e.getSpawnedEntity());
                if (!Methods.isInventoryFull(player)) {
                    player.getInventory().addItem(it);
                } else {
                    block.getWorld().dropItemNaturally(block.getLocation(), it);
                }
                block.setType(Material.AIR);
                e.setCancelled(true);
            }
        }
    }
    
}