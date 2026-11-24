package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.CrazyItems;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.ryderbelserion.common.api.enums.messages.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.ryderbelserion.fusion.paper.builders.folia.FoliaScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScramblerListener implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Server server = this.plugin.getServer();

    private final CrazyPlatform platform = this.plugin.getPlatform();

    private final CrazyItems items = this.platform.getCrazyItems();

    private final EnchantmentBookSettings bookSettings = this.platform.getBookSettings();

    private final ComponentLogger logger = this.plugin.getComponentLogger();

    private final HashMap<Player, ScheduledTask> roll = new HashMap<>();

    public void openScrambler(Player player, ItemStack book) {
        final Inventory inventory = this.server.createInventory(null, 27, this.items.getGuiName());

        this.items.setGlass(inventory);

        for (int slot = 9; slot > 8 && slot < 18; slot++) {
            inventory.setItem(slot, this.bookSettings.getNewScrambledBook(player, book));
        }

        player.openInventory(inventory);
        startScrambler(player, inventory, book);
    }

    private void startScrambler(final Player player, final Inventory inventory, final ItemStack book) {
        this.roll.put(player, new FoliaScheduler(this.plugin, null, player) {
            int time = 1;
            int full = 0;
            int open = 0;

            @Override
            public void run() {
                if (this.full <= 50) { // When spinning.
                    moveItems(player, inventory, book);

                    items.setGlass(inventory);

                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                }

                this.open++;

                if (this.open >= 5) {
                    player.openInventory(inventory);

                    this.open = 0;
                }

                this.full++;

                if (this.full > 51) {
                    if (slowSpin().contains(this.time)) { // When Slowing Down
                        moveItems(player, inventory, book);

                        items.setGlass(inventory);

                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                    }

                    this.time++;

                    if (this.time == 60) { // When done
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                        cancel();

                        roll.remove(player);

                        final ItemStack item = inventory.getItem(13);

                        if (item != null) { //todo() what
                            ItemStack clone;

                            clone = item.withType(bookSettings.getEnchantmentBookItem(player).getType());

                            Methods.setDurability(item, Methods.getDurability(bookSettings.getEnchantmentBookItem(player)));

                            Methods.addItemToInventory(player, clone);
                        } else {
                            logger.error("The item at slot 13 is null, We cannot continue!");
                        }

                    } else if (this.time > 60) { // Just in case the cancel fails.
                        cancel();
                    }
                }
            }
        }.runAtFixedRate(1, 1));
    }

    private List<Integer> slowSpin() {
        List<Integer> slow = new ArrayList<>();
        int full = 120;
        int cut = 15;

        for (int amount = 120; cut > 0; full--) {
            if (full <= amount - cut || full >= amount - cut) {
                slow.add(amount);
                amount = amount - cut;
                cut--;
            }
        }

        return slow;
    }

    private void moveItems(Player player, Inventory inv, ItemStack book) {
        final List<ItemStack> items = new ArrayList<>();

        for (int slot = 9; slot > 8 && slot < 17; slot++) {
            items.add(inv.getItem(slot));
        }

        final ItemStack newBook = this.bookSettings.getNewScrambledBook(player, book);

        if (newBook != null) {
            newBook.setType(ColorUtils.getRandomPaneColor().getMaterial());
            inv.setItem(9, newBook);
        }

        for (int amount = 0; amount < 8; amount++) {
            inv.setItem(amount + 10, items.get(amount));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onReRoll(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        final Inventory inventory = event.getClickedInventory();

        if (inventory == null) return;

        final ItemStack currentItem = event.getCurrentItem();

        ItemStack book = currentItem != null ? currentItem : ItemStack.empty();

        ItemStack scrambler = event.getCursor();

        if (book.isEmpty() || scrambler.isEmpty()) return;

        if (book.getAmount() != 1 || scrambler.getAmount() != 1) return;

        if (!this.items.isScrambler(scrambler) || !this.bookSettings.isEnchantmentBook(book)) return;

        if (inventory.getType() != InventoryType.PLAYER) {
            Messages.need_to_use_player_inventory.sendMessage(player);

            return;
        }

        event.setCancelled(true);

        player.setItemOnCursor(ItemStack.empty());

        if (this.items.isAnimationToggle()) {
            event.setCurrentItem(ItemStack.empty());
            openScrambler(player, book);
        } else {
            event.setCurrentItem(this.bookSettings.getNewScrambledBook(player, book));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(this.items.getGuiName())) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onScramblerClick(PlayerInteractEvent event) {
        ItemStack item = Methods.getItemInHand(event.getPlayer());

        if (item.isEmpty()) return;

        if (item.getPersistentDataContainer().has(DataKeys.scrambler.getNamespacedKey())) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        try {
            this.roll.get(player).cancel();
            this.roll.remove(player);
        } catch (Exception ignored) {}
    }

    @EventHandler(ignoreCancelled = true)
    public void onScrollClick(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final PlayerInventory inventory = player.getInventory();

        if (this.items.isScrambler(inventory.getItemInMainHand()) || this.items.isScrambler(inventory.getItemInOffHand())) {
            event.setCancelled(true);
        }
    }
}