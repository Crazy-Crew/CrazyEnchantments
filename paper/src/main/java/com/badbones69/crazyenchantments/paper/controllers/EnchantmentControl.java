package com.badbones69.crazyenchantments.paper.controllers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.badbones69.crazyenchantments.paper.api.events.BookApplyEvent;
import com.badbones69.crazyenchantments.paper.api.events.BookDestroyEvent;
import com.badbones69.crazyenchantments.paper.api.events.BookFailEvent;
import com.badbones69.crazyenchantments.paper.api.events.PreBookApplyEvent;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
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

    @NotNull
    private final Starter starter = plugin.getStarter();

    @NotNull
    private final Methods methods = starter.getMethods();

    @NotNull
    private final CrazyManager crazyManager = starter.getCrazyManager();

    @NotNull
    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    @EventHandler(ignoreCancelled = true)
    public void useEnchantedBook(InventoryClickEvent event) {
        FileConfiguration config = Files.CONFIG.getFile();
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        ItemStack book = event.getCursor();

        if (item == null
           || book.getAmount() > 1
           || item.getAmount() > 1
           || !enchantmentBookSettings.isEnchantmentBook(book)
           || enchantmentBookSettings.isEnchantmentBook(item)
           || methods.inCreativeMode(player)
        ) return;

        CEBook ceBook = enchantmentBookSettings.getCEBook(book);
        if (ceBook == null) return;

        CEnchantment enchantment = ceBook.getEnchantment();
        if (enchantment == null || !enchantment.canEnchantItem(item)) return;

        Map<CEnchantment, Integer> enchantments = enchantmentBookSettings.getEnchantments(item);
        boolean hasWhiteScrollProtection = Scrolls.hasWhiteScrollProtection(item);
        boolean hasEnchantment = enchantments.containsKey(enchantment);

        PreBookApplyEvent preApplyEvent = new PreBookApplyEvent(player, item, ceBook);
        if (methods.isEventCancelled(preApplyEvent)) return;

        if (hasEnchantment) {
            if (!config.getBoolean("Settings.EnchantmentOptions.Armor-Upgrade.Toggle")
               || !(enchantments.get(enchantment) < ceBook.getLevel())
            ) return;

            event.setCancelled(true);

            if (preApplyEvent.getSuccessful()) {
                if (!methods.isEventCancelled(new BookApplyEvent(player, item, ceBook))) {
                    final ItemStack clone = item.clone();

                    this.crazyManager.addEnchantment(clone, enchantment, ceBook.getLevel());

                    event.setCurrentItem(clone);

                    player.setItemOnCursor(null);

                    final Map<String, String> placeholders = new HashMap<>();

                    placeholders.put("%Enchantment%", enchantment.getCustomName());
                    placeholders.put("%Level%", String.valueOf(ceBook.getLevel()));

                    player.sendMessage(Messages.ENCHANTMENT_UPGRADE_SUCCESS.getMessage(placeholders));

                    player.playSound(player.getLocation(), enchantment.getSound(), 1, 1);
                    // ToDo potentially add pitch and volume options.
                }

                return;
            } else if (preApplyEvent.getDestroyed()) {
                if (!methods.isEventCancelled(new BookDestroyEvent(player, item, ceBook))) {
                    if (config.getBoolean("Settings.EnchantmentOptions.Armor-Upgrade.Enchantment-Break")) {
                        if (hasWhiteScrollProtection) {
                            event.setCurrentItem(Scrolls.removeWhiteScrollProtection(item));
                            player.sendMessage(Messages.ITEM_WAS_PROTECTED.getMessage());
                        } else {
                            event.setCurrentItem(enchantmentBookSettings.removeEnchantment(item, enchantment));
                            player.sendMessage(Messages.ENCHANTMENT_UPGRADE_DESTROYED.getMessage());
                        }
                    } else {
                        if (hasWhiteScrollProtection) {
                            event.setCurrentItem(Scrolls.removeWhiteScrollProtection(item));
                            player.sendMessage(Messages.ITEM_WAS_PROTECTED.getMessage());
                        } else {
                            event.setCurrentItem(null);
                            player.sendMessage(Messages.ITEM_DESTROYED.getMessage());
                        }
                    }

                    player.setItemOnCursor(null);
                    methods.playItemBreak(player, book);
                }

                return;
            } else {
                if (!methods.isEventCancelled(new BookFailEvent(player, item, ceBook))) {
                    player.setItemOnCursor(null);
                    player.sendMessage(Messages.ENCHANTMENT_UPGRADE_FAILED.getMessage());
                    methods.playItemBreak(player, book);
                }

                return;
            }

        }

        if (!crazyManager.canAddEnchantment(player, item)) {
            player.sendMessage(Messages.HIT_ENCHANTMENT_MAX.getMessage());
            return;
        }

        for (CEnchantment enchant : enchantments.keySet()) {
            if (enchant.conflictsWith(enchantment)) {
                player.sendMessage(Messages.CONFLICTING_ENCHANT.getMessage());
                return;
            }
        }

        event.setCancelled(true);

        if (preApplyEvent.getSuccessful()) {
            final ItemStack clone = item.clone();

            this.crazyManager.addEnchantment(clone, enchantment, ceBook.getLevel());

            event.setCurrentItem(clone);

            player.setItemOnCursor(null);

            player.sendMessage(Messages.BOOK_WORKS.getMessage());

            player.playSound(player.getLocation(), enchantment.getSound(), 1, 1);

            return;
        }

        if (preApplyEvent.getDestroyed()) {
            if (hasWhiteScrollProtection) {
                methods.playItemBreak(player, book);
                event.setCurrentItem(Scrolls.removeWhiteScrollProtection(item));
                player.sendMessage(Messages.ITEM_WAS_PROTECTED.getMessage());
            } else {
                methods.playItemBreak(player, item);
                event.setCurrentItem(null);
                player.sendMessage(Messages.ITEM_DESTROYED.getMessage());
            }

            player.setItemOnCursor(null);
            return;
        }

        player.sendMessage(Messages.BOOK_FAILED.getMessage());
        methods.playItemBreak(player, book);
        if (config.getBoolean("Settings.EnchantmentOptions.Limit.Change-On-Fail", true)) event.setCurrentItem(crazyManager.changeEnchantmentLimiter(item, 1));
        player.setItemOnCursor(null);
    }

    @EventHandler
    public void onDescriptionSend(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.useInteractedBlock().equals(Event.Result.ALLOW)) return;

        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Right-Click-Book-Description")) {
            ItemStack item = methods.getItemInHand(event.getPlayer());

            CEBook book = enchantmentBookSettings.getCEBook(item);

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