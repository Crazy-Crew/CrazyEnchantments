package com.badbones69.crazyenchantments.paper.api.builders.types.tinkerer;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.builders.InventoryBuilder;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;

public class TinkererMenu extends InventoryBuilder {

    public TinkererMenu(@NotNull final Player player, final int size, @NotNull final String title) {
        super(player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        final YamlConfiguration configuration = FileKeys.tinker.getYamlConfiguration();

        final ItemStack button = new ItemBuilder().setMaterial(Material.RED_STAINED_GLASS_PANE)
                .setName(configuration.getString("Settings.TradeButton", "&eClick to accept the trade"))
                .setLore(configuration.getStringList("Settings.TradeButton-Lore"))
                .addKey(DataKeys.trade_button.getNamespacedKey(), "").build();

        getInventory().setItem(0, button);
        getInventory().setItem(8, button);

        ItemStack divider = new ItemBuilder().setMaterial(Material.WHITE_STAINED_GLASS_PANE).setName(" ").build();

        List.of(4, 13, 22, 31, 40, 49).forEach(slot -> getInventory().setItem(slot, divider));

        return this;
    }

    public static class TinkererListener implements Listener {

        @NotNull
        private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

        private final CrazyInstance instance = this.plugin.getInstance();

        private final CurrencyAPI api = this.plugin.getStarter().getCurrencyAPI();

        private final Map<Integer, Integer> slots = TinkererManager.getSlots();

        @EventHandler
        public void onExperienceUse(PlayerInteractEvent event) {
            if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

            Player player = event.getPlayer();

            final YamlConfiguration configuration = FileKeys.tinker.getYamlConfiguration();

            if (TinkererManager.useExperience(player, event, true, configuration)) return;

            TinkererManager.useExperience(player, event, false, configuration);
        }

        @EventHandler(ignoreCancelled = true)
        public void onInventoryClick(InventoryClickEvent event) {
            if (!(event.getInventory().getHolder() instanceof TinkererMenu holder)) return;

            Player player = holder.getPlayer();

            event.setCancelled(true);

            ItemStack current = event.getCurrentItem();

            if (current == null || current.isEmpty()) return;

            Inventory inventory = holder.getInventory();
            Inventory topInventory = player.getOpenInventory().getTopInventory();
            Inventory bottomInventory = player.getOpenInventory().getBottomInventory();

            // Recycling things.
            if (current.getPersistentDataContainer().has(DataKeys.trade_button.getNamespacedKey())) {
                int total = 0;
                boolean toggle = false;

                final YamlConfiguration configuration = FileKeys.tinker.getYamlConfiguration();

                final Currency currency = Currency.getCurrency(configuration.getString("Settings.Currency", "XP_LEVEL"));

                for (Map.Entry<Integer, Integer> slot : this.slots.entrySet()) {
                    ItemStack reward = inventory.getItem(slot.getValue());

                    if (reward != null) {
                        if (currency == Currency.VAULT) {
                            total = TinkererManager.getTotalXP(inventory.getItem(slot.getKey()), configuration);
                        } else {
                            bottomInventory.addItem(reward).values().forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
                        }

                        toggle = true;
                    }

                    inventory.setItem(slot.getKey(), null);
                    inventory.setItem(slot.getValue(), null);
                }

                player.closeInventory();

                if (total != 0) {
                    this.api.giveCurrency(player, currency, total);
                }

                if (toggle) player.sendMessage(Messages.TINKER_SOLD_MESSAGE.getMessage());

                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);

                return;
            }

            if (current.getType().toString().endsWith("STAINED_GLASS_PANE")) return;

            final YamlConfiguration configuration = FileKeys.tinker.getYamlConfiguration();

            // Adding/taking items.
            if (this.instance.isEnchantmentBook(current)) { // Adding a book.
                final CEBook book = this.instance.getBook(current);

                if (book == null) return;

                if (event.getClickedInventory() == topInventory) { // Clicking in the tinkers.
                    event.setCurrentItem(null);
                    bottomInventory.addItem(current);
                    inventory.setItem(this.slots.get(event.getRawSlot()), null);
                } else { // Clicking in their inventory.
                    if (isFirstEmpty(event, player, current, topInventory)) return;

                    inventory.setItem(this.slots.get(inventory.firstEmpty()), Dust.MYSTERY_DUST.getDust(TinkererManager.getMaxDustLevelFromBook(book, configuration), 1));
                    inventory.setItem(inventory.firstEmpty(), current);
                }

                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

                return;
            }

            int totalXP = TinkererManager.getTotalXP(current, configuration);

            if (totalXP > 0) {
                // Adding an item.
                if (event.getClickedInventory() == topInventory) { // Clicking in the tinkers.
                    if (this.slots.containsKey(event.getRawSlot())) {
                        event.setCurrentItem(null);
                        player.getInventory().addItem(current);
                        inventory.setItem(this.slots.get(event.getRawSlot()), null);
                    }
                } else {
                    // Clicking in their inventory.
                    if (isFirstEmpty(event, player, current, topInventory)) return;

                    inventory.setItem(this.slots.get(inventory.firstEmpty()), TinkererManager.getXPBottle(totalXP, configuration));
                    inventory.setItem(inventory.firstEmpty(), current);
                }

                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            }
        }

        private boolean isFirstEmpty(InventoryClickEvent event, Player player, ItemStack current, Inventory topInventory) {
            if (topInventory.firstEmpty() == -1) {
                player.sendMessage(Messages.TINKER_INVENTORY_FULL.getMessage());

                return true;
            }

            if (current.getAmount() > 1) {
                player.sendMessage(Messages.NEED_TO_UNSTACK_ITEM.getMessage());

                return true;
            }

            event.setCurrentItem(null);

            return false;
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onInvClose(final InventoryCloseEvent event) {
            if (!(event.getInventory().getHolder() instanceof TinkererMenu holder)) return;

            Player player = holder.getPlayer();

            new FoliaScheduler(this.plugin, null, player) {
                @Override
                public void run() {
                    final Inventory inventory = holder.getInventory();

                    for (final int slot : slots.keySet()) {
                        final ItemStack item = inventory.getItem(slot);

                        if (item == null || item.isEmpty()) continue;

                        if (player.isDead()) {
                            player.getWorld().dropItem(player.getLocation(), item);
                        } else {
                            player.getInventory().addItem(item).values().forEach(item2 -> player.getWorld().dropItem(player.getLocation(), item2));
                        }
                    }

                    holder.getInventory().clear();
                }
            }.execute();
        }
    }
}