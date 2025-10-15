package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.managers.items.ItemManager;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScramblerListener implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final ItemManager itemManager = this.plugin.getItemManager();

    private final CrazyInstance instance = this.plugin.getInstance();

    private final ComponentLogger logger = this.plugin.getComponentLogger();

    private final HashMap<Player, ScheduledTask> roll = new HashMap<>();

    private void setGlass(@NotNull final Inventory inv) {
        for (int slot = 0; slot < 9; slot++) {
            if (slot != 4) {
                inv.setItem(slot, ColorUtils.getRandomPaneColor().setName(" ").build());
                inv.setItem(slot + 18, ColorUtils.getRandomPaneColor().setName(" ").build());
            } else {
                final int finalSlot = slot;

                this.itemManager.getItem("pointer_item").ifPresent(action -> {
                    final ItemStack itemStack = action.getItemStack();

                    inv.setItem(finalSlot, itemStack);
                    inv.setItem(finalSlot + 18, itemStack);
                });
            }
        }
    }

    public void openScrambler(@NotNull final Player player, @NotNull final ItemStack book) {
        Inventory inventory = this.plugin.getServer().createInventory(null, 27, this.itemManager.getScramblerGuiName());

        setGlass(inventory);

        for (int slot = 9; slot > 8 && slot < 18; slot++) {
            final ItemStack itemStack = this.instance.getScrambledBook(book);

            if (itemStack == null) break; //todo() debug

            inventory.setItem(slot, itemStack);
        }

        player.openInventory(inventory);

        startScrambler(player, inventory, book);
    }

    private void startScrambler(@NotNull final Player player, @NotNull final Inventory inventory, @NotNull final ItemStack book) {
        this.roll.put(player, new FoliaScheduler(this.plugin, null, player) {
            int time = 1;
            int full = 0;
            int open = 0;

            @Override
            public void run() {
                if (this.full <= 50) { // When spinning.
                    moveItems(inventory, book);

                    setGlass(inventory);

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
                        moveItems(inventory, book);

                        setGlass(inventory);

                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                    }

                    this.time++;

                    if (this.time == 60) { // When done
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                        cancel();

                        roll.remove(player);

                        final ItemStack item = inventory.getItem(13);

                        if (item != null) {
                            ItemStack clone;

                            clone = item.withType(instance.getEnchantmentBookItem().getType());

                            Methods.setDurability(item, Methods.getDurability(instance.getEnchantmentBookItem()));

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

    private void moveItems(@NotNull final Inventory inv, @NotNull final ItemStack book) {
        List<ItemStack> items = new ArrayList<>();

        for (int slot = 9; slot > 8 && slot < 17; slot++) {
            items.add(inv.getItem(slot));
        }

        ItemStack newBook = this.instance.getScrambledBook(book);

        if (newBook == null) return;

        newBook = newBook.withType(ColorUtils.getRandomPaneColor().getMaterial());

        inv.setItem(9, newBook);

        for (int amount = 0; amount < 8; amount++) {
            inv.setItem(amount + 10, items.get(amount));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onReRoll(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() == null) return;

        final ItemStack book = event.getCurrentItem();

        if (book == null || book.isEmpty()) return;

        final ItemStack scrambler = event.getCursor();

        if (scrambler.isEmpty()) return;

        if (book.getAmount() > 1 || scrambler.getAmount() > 1) return;

        final AtomicBoolean isScrambler = new AtomicBoolean(false);

        this.itemManager.getItem("scrambler_item").ifPresent(action -> isScrambler.set(action.isItem(scrambler)));

        if (!isScrambler.get() || !this.instance.isEnchantmentBook(book)) return;

        if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
            Messages.NEED_TO_USE_PLAYER_INVENTORY.sendMessage(player);

            return;
        }

        event.setCancelled(true);
        player.setItemOnCursor(ItemStack.empty());

        if (this.itemManager.isScramblerAnimation()) {
            event.setCurrentItem(ItemStack.empty());
            openScrambler(player, book);
        } else {
            event.setCurrentItem(this.instance.getScrambledBook(book));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(this.itemManager.getScramblerGuiName())) event.setCancelled(true); //todo() inventory holders
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

        final ItemStack mainHand = inventory.getItemInMainHand();
        final ItemStack offHand = inventory.getItemInOffHand();

        final ItemStack itemStack = mainHand.isEmpty() ? offHand : mainHand;

        final AtomicBoolean isScrambler = new AtomicBoolean(false);

        this.itemManager.getItem("scrambler_item").ifPresent(action -> isScrambler.set(action.isItem(itemStack)));

        if (isScrambler.get()) event.setCancelled(true);
    }
}