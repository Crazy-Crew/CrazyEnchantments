package com.badbones69.crazyenchantments.paper.controllers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.ryderbelserion.common.api.enums.messages.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.ryderbelserion.common.api.enums.Files;
import com.badbones69.crazyenchantments.paper.api.events.BookApplyEvent;
import com.badbones69.crazyenchantments.paper.api.events.BookDestroyEvent;
import com.badbones69.crazyenchantments.paper.api.events.BookFailEvent;
import com.badbones69.crazyenchantments.paper.api.events.PreBookApplyEvent;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.ryderbelserion.fusion.paper.builders.folia.FoliaScheduler;
import org.bukkit.Material;
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
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.HashMap;
import java.util.Map;

public class EnchantmentControl implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyPlatform platform = this.plugin.getPlatform();

    private final EnchantmentBookSettings bookSettings = this.platform.getBookSettings();

    private final CrazyManager crazyManager = this.plugin.getCrazyManager();

    @EventHandler(ignoreCancelled = true)
    public void useEnchantedBook(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }


        final ItemStack item = event.getCurrentItem();
        final ItemStack book = event.getCursor();

        if (item == null
           || book.getAmount() > 1
           || item.getAmount() > 1
           || !this.bookSettings.isEnchantmentBook(book)
           || this.bookSettings.isEnchantmentBook(item)
           || Methods.inCreativeMode(player)
        ) return;

        final CEBook ceBook = this.bookSettings.getCEBook(book);

        if (ceBook == null) return;

        final CEnchantment enchantment = ceBook.getEnchantment();

        if (enchantment == null || !enchantment.canEnchantItem(item)) return;

        final Map<CEnchantment, Integer> enchantments = this.bookSettings.getEnchantments(item);

        final boolean hasWhiteScrollProtection = Scrolls.hasWhiteScrollProtection(item);
        final boolean hasEnchantment = enchantments.containsKey(enchantment);

        final PreBookApplyEvent preApplyEvent = new PreBookApplyEvent(player, item, ceBook);

        if (Methods.isEventCancelled(preApplyEvent)) return;

        final CommentedConfigurationNode config = Files.CONFIG.getConfiguration();

        if (hasEnchantment) {
            if (!config.node("Settings", "EnchantmentOptions", "Armor-Upgrade", "Toggle").getBoolean(true)
                    || !(enchantments.get(enchantment) < ceBook.getLevel())) return;

            event.setCancelled(true);

            if (preApplyEvent.getSuccessful()) {
                if (!Methods.isEventCancelled(new BookApplyEvent(player, item, ceBook))) {
                    final ItemStack clone = item.clone();

                    this.bookSettings.addEnchantment(clone, enchantment, ceBook.getLevel());

                    event.setCurrentItem(clone);

                    player.setItemOnCursor(null);

                    final Map<String, String> placeholders = new HashMap<>();

                    placeholders.put("%Enchantment%", enchantment.getCustomName());
                    placeholders.put("%Level%", String.valueOf(ceBook.getLevel()));

                    Messages.enchantment_upgrade_success.sendMessage(player, placeholders);

                    player.playSound(player.getLocation(), enchantment.getSound(), 1, 1);
                    // ToDo potentially add pitch and volume options.
                }

                return;
            } else if (preApplyEvent.getDestroyed()) {
                if (!Methods.isEventCancelled(new BookDestroyEvent(player, item, ceBook))) {
                    if (config.node("Settings", "EnchantmentOptions", "Armor-Upgrade", "Enchantment-Break").getBoolean(true)) {
                        if (hasWhiteScrollProtection) {
                            event.setCurrentItem(Scrolls.removeWhiteScrollProtection(item));

                            Messages.item_was_protected.sendMessage(player);
                        } else {
                            event.setCurrentItem(this.bookSettings.removeEnchantment(item, enchantment));

                            Messages.enchantment_upgrade_destroyed.sendMessage(player);
                        }
                    } else {
                        if (hasWhiteScrollProtection) {
                            event.setCurrentItem(Scrolls.removeWhiteScrollProtection(item));

                            Messages.item_was_protected.sendMessage(player);
                        } else {
                            event.setCurrentItem(null);

                            Messages.item_destroyed.sendMessage(player);
                        }
                    }

                    player.setItemOnCursor(null);

                    Methods.playItemBreak(player, book);
                }

                return;
            } else {
                if (!Methods.isEventCancelled(new BookFailEvent(player, item, ceBook))) {
                    player.setItemOnCursor(null);

                    Messages.enchantment_upgrade_failed.sendMessage(player);

                    Methods.playItemBreak(player, book);
                }

                return;
            }

        }

        if (!this.crazyManager.canAddEnchantment(player, item)) {
            Messages.hit_enchantment_max.sendMessage(player);

            return;
        }

        for (CEnchantment enchant : enchantments.keySet()) {
            if (enchant.conflictsWith(enchantment)) {
                Messages.conflicting_enchant.sendMessage(player);

                return;
            }
        }

        event.setCancelled(true);

        if (preApplyEvent.getSuccessful()) {
            final ItemStack clone = item.clone();

            this.bookSettings.addEnchantment(clone, enchantment, ceBook.getLevel());

            event.setCurrentItem(clone);

            player.setItemOnCursor(null);

            Messages.book_works.sendMessage(player);

            player.playSound(player.getLocation(), enchantment.getSound(), 1, 1);

            return;
        }

        if (preApplyEvent.getDestroyed()) {
            if (hasWhiteScrollProtection) {
                Methods.playItemBreak(player, book);

                event.setCurrentItem(Scrolls.removeWhiteScrollProtection(item));

                Messages.item_was_protected.sendMessage(player);
            } else {
                Methods.playItemBreak(player, item);

                event.setCurrentItem(null);

                Messages.item_destroyed.sendMessage(player);
            }

            player.setItemOnCursor(null);

            return;
        }

        Messages.book_failed.sendMessage(player);

        Methods.playItemBreak(player, book);

        if (config.node("Settings", "EnchantmentOptions", "Limit", "Change-On-Fail").getBoolean(true)) {
            event.setCurrentItem(this.crazyManager.changeEnchantmentLimiter(item, 1));
        }

        player.setItemOnCursor(null);
    }

    @EventHandler
    public void onDescriptionSend(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.useInteractedBlock().equals(Event.Result.ALLOW)) return;

        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) &&
                Files.CONFIG.getConfiguration().node("Settings", "EnchantmentOptions", "Right-Click-Book-Description").getBoolean(true)) {
            final ItemStack item = Methods.getItemInHand(event.getPlayer());

            final CEBook book = this.bookSettings.getCEBook(item);

            if (book != null) {
                event.setCancelled(true);

                final CEnchantment enchantment = book.getEnchantment();

                final Player player = event.getPlayer();

                if (!enchantment.getInfoName().isEmpty()) player.sendMessage(enchantment.getInfoName());

                for (String descriptionLine : enchantment.getInfoDescription()) {
                    player.sendMessage(descriptionLine);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMilkDrink(PlayerItemConsumeEvent event) {
        final Player player = event.getPlayer();

        if (event.getItem().getType() != Material.MILK_BUCKET) return;

        new FoliaScheduler(this.plugin, null, player) {
            @Override
            public void run() {
                crazyManager.updatePlayerEffects(player);
            }
        }.runDelayed(5);
    }
}