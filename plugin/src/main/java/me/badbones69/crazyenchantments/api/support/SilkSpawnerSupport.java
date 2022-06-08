package me.badbones69.crazyenchantments.api.support;

/*
public class SilkSpawnerSupport implements Listener {

    private SilkUtil api = SilkUtil.hookIntoSilkSpawners();
    private CrazyManager ce = CrazyManager.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(SilkSpawnersSpawnerBreakEvent e) {
        if (e.isCancelled()) return;
        Player player = e.getPlayer();
        Block block = e.getBlock();
        if (player != null && block != null && player.getGameMode() != GameMode.CREATIVE) {
            ItemStack item = Methods.getItemInHand(player);
            if (ce.hasEnchantment(item, CEnchantments.TELEPATHY.getEnchantment())) {
                String mobName = api.getCreatureName(e.getEntityID()).toLowerCase().replace(" ", "");
                if (player.hasPermission("silkspawners.silkdrop." + mobName)) {
                    EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.TELEPATHY.getEnchantment(), item);
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        return;
                    }
                    ItemStack it = api.newSpawnerItem(e.getEntityID(), api.getCustomSpawnerName(api.getCreatureName(e.getEntityID())), 1, false);
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
}**/