package com.badbones69.crazyenchantments.paper.controllers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.enums.v2.Messages;
import com.badbones69.crazyenchantments.paper.api.events.BookApplyEvent;
import com.badbones69.crazyenchantments.paper.api.events.BookDestroyEvent;
import com.badbones69.crazyenchantments.paper.api.events.BookFailEvent;
import com.badbones69.crazyenchantments.paper.api.events.PreBookApplyEvent;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class EnchantmentControl implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final CrazyInstance instance = this.plugin.getInstance();

    @NotNull
    private final Methods methods = null;

    @NotNull
    private final CrazyManager crazyManager = null;

    @EventHandler(ignoreCancelled = true)
    public void useEnchantedBook(InventoryClickEvent event) {
        final YamlConfiguration config = FileKeys.config.getYamlConfiguration();

        Player player = (Player) event.getWhoClicked();

        ItemStack item = event.getCurrentItem();
        ItemStack book = event.getCursor();

        if (item == null
           || book.getAmount() > 1
           || item.getAmount() > 1
           || !this.instance.isEnchantmentBook(book)
           || this.instance.isEnchantmentBook(item)
           || this.methods.inCreativeMode(player)
        ) return;

        final CEBook ceBook = this.instance.getBook(book);

        if (ceBook == null) return;

        CEnchantment enchantment = ceBook.getEnchantment();
        if (enchantment == null || !enchantment.canEnchantItem(item)) return;

        Map<CEnchantment, Integer> enchantments = this.instance.getEnchantments(item);
        boolean hasWhiteScrollProtection = Scrolls.hasWhiteScrollProtection(item);
        boolean hasEnchantment = enchantments.containsKey(enchantment);

        PreBookApplyEvent preApplyEvent = new PreBookApplyEvent(player, item, ceBook);

        if (this.methods.isEventCancelled(preApplyEvent)) return;

        if (hasEnchantment) {
            if (!config.getBoolean("Settings.EnchantmentOptions.Armor-Upgrade.Toggle")
               || !(enchantments.get(enchantment) < ceBook.getLevel())
            ) return;

            event.setCancelled(true);

            if (preApplyEvent.getSuccessful()) {
                if (!this.methods.isEventCancelled(new BookApplyEvent(player, item, ceBook))) {
                    final ItemStack clone = item.clone();

                    this.crazyManager.addEnchantment(clone, enchantment, ceBook.getLevel());

                    event.setCurrentItem(clone);

                    player.setItemOnCursor(null);

                    Messages.ENCHANTMENT_UPGRADE_SUCCESS.sendMessage(player, new HashMap<>() {{
                        put("{enchantment}", enchantment.getCustomName());
                        put("{level}", String.valueOf(ceBook.getLevel()));
                    }});

                    player.playSound(player.getLocation(), enchantment.getSound(), 1, 1);
                    // ToDo potentially add pitch and volume options.
                }

                return;
            } else if (preApplyEvent.getDestroyed()) {
                if (!this.methods.isEventCancelled(new BookDestroyEvent(player, item, ceBook))) {
                    if (config.getBoolean("Settings.EnchantmentOptions.Armor-Upgrade.Enchantment-Break", true)) {
                        if (hasWhiteScrollProtection) {
                            event.setCurrentItem(Scrolls.removeWhiteScrollProtection(item));

                            Messages.ITEM_WAS_PROTECTED.sendMessage(player);
                        } else {
                            event.setCurrentItem(instance.removeEnchantment(item, enchantment));

                            Messages.ENCHANTMENT_UPGRADE_DESTROYED.sendMessage(player);
                        }
                    } else {
                        if (hasWhiteScrollProtection) {
                            event.setCurrentItem(Scrolls.removeWhiteScrollProtection(item));

                            Messages.ITEM_WAS_PROTECTED.sendMessage(player);
                        } else {
                            event.setCurrentItem(null);

                            Messages.ITEM_DESTROYED.sendMessage(player);
                        }
                    }

                    player.setItemOnCursor(null);
                    this.methods.playItemBreak(player, book);
                }

                return;
            } else {
                if (!this.methods.isEventCancelled(new BookFailEvent(player, item, ceBook))) {
                    player.setItemOnCursor(null);

                    Messages.ENCHANTMENT_UPGRADE_FAILED.sendMessage(player);

                    this.methods.playItemBreak(player, book);
                }

                return;
            }
        }

        if (!this.crazyManager.canAddEnchantment(player, item)) {
            Messages.HIT_ENCHANTMENT_MAX.sendMessage(player);

            return;
        }

        for (CEnchantment enchant : enchantments.keySet()) {
            if (enchant.conflictsWith(enchantment)) {
                Messages.CONFLICTING_ENCHANT.sendMessage(player);

                return;
            }
        }

        event.setCancelled(true);

        if (preApplyEvent.getSuccessful()) {
            final ItemStack clone = item.clone();

            this.crazyManager.addEnchantment(clone, enchantment, ceBook.getLevel());

            event.setCurrentItem(clone);

            player.setItemOnCursor(null);

            Messages.BOOK_WORKS.sendMessage(player);

            player.playSound(player.getLocation(), enchantment.getSound(), 1, 1);

            return;
        }

        if (preApplyEvent.getDestroyed()) {
            if (hasWhiteScrollProtection) {
                this.methods.playItemBreak(player, book);

                event.setCurrentItem(Scrolls.removeWhiteScrollProtection(item));

                Messages.ITEM_WAS_PROTECTED.sendMessage(player);
            } else {
                this.methods.playItemBreak(player, item);

                event.setCurrentItem(null);

                Messages.ITEM_DESTROYED.sendMessage(player);
            }

            player.setItemOnCursor(null);

            return;
        }

        Messages.BOOK_FAILED.sendMessage(player);

        this.methods.playItemBreak(player, book);

        if (config.getBoolean("Settings.EnchantmentOptions.Limit.Change-On-Fail", true)) event.setCurrentItem(crazyManager.changeEnchantmentLimiter(item, 1));

        player.setItemOnCursor(null);
    }

    @EventHandler
    public void onDescriptionSend(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.useInteractedBlock().equals(Event.Result.ALLOW)) return;

        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && FileKeys.config.getYamlConfiguration().getBoolean("Settings.EnchantmentOptions.Right-Click-Book-Description", true)) {
            ItemStack item = this.methods.getItemInHand(event.getPlayer());

            final CEBook book = this.instance.getBook(item);

            if (book != null) {
                event.setCancelled(true);

                CEnchantment enchantment = book.getEnchantment();

                Player player = event.getPlayer();

                if (!enchantment.getInfoName().isEmpty()) player.sendMessage(enchantment.getInfoName());

                for (String descriptionLine : enchantment.getInfoDescription()) {
                    player.sendMessage(descriptionLine);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMilkDrink(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if (event.getItem().getType() != Material.MILK_BUCKET) return;

        new FoliaScheduler(this.plugin, null, player) {
            @Override
            public void run() {
                crazyManager.updatePlayerEffects(player);
            }
        }.runDelayed(5);
    }
}