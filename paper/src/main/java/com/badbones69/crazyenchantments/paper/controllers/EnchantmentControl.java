package com.badbones69.crazyenchantments.paper.controllers;

import com.badbones69.crazyenchantments.objects.User;
import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.shop.Scrolls;
import com.badbones69.crazyenchantments.paper.api.enums.files.FileKeys;
import com.badbones69.crazyenchantments.paper.api.events.BookApplyEvent;
import com.badbones69.crazyenchantments.paper.api.events.BookDestroyEvent;
import com.badbones69.crazyenchantments.paper.api.events.BookFailEvent;
import com.badbones69.crazyenchantments.paper.api.events.PreBookApplyEvent;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.registry.UserRegistry;
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
import us.crazycrew.crazyenchantments.constants.MessageKeys;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentControl implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final CrazyInstance instance = this.plugin.getInstance();

    private final UserRegistry userRegistry = this.instance.getUserRegistry();

    @NotNull
    private final CrazyManager crazyManager = null;

    @EventHandler(ignoreCancelled = true)
    public void useEnchantedBook(InventoryClickEvent event) {
        final YamlConfiguration config = FileKeys.config.getPaperConfiguration();

        Player player = (Player) event.getWhoClicked();

        final User user = this.userRegistry.getUser(player);

        ItemStack item = event.getCurrentItem();
        ItemStack book = event.getCursor();

        if (item == null
           || book.getAmount() > 1
           || item.getAmount() > 1
           || !this.instance.isEnchantmentBook(book)
           || this.instance.isEnchantmentBook(item)
           || Methods.inCreativeMode(player)
        ) return;

        final CEBook ceBook = this.instance.getBook(book);

        if (ceBook == null) return;

        CEnchantment enchantment = ceBook.getEnchantment();
        if (enchantment == null || !enchantment.canEnchantItem(item)) return;

        Map<CEnchantment, Integer> enchantments = this.instance.getEnchantments(item);
        boolean hasWhiteScrollProtection = Scrolls.hasWhiteScrollProtection(item);
        boolean hasEnchantment = enchantments.containsKey(enchantment);

        PreBookApplyEvent preApplyEvent = new PreBookApplyEvent(player, item, ceBook);

        if (Methods.isEventCancelled(preApplyEvent)) return;

        if (hasEnchantment) {
            if (!config.getBoolean("Settings.EnchantmentOptions.Armor-Upgrade.Toggle")
               || !(enchantments.get(enchantment) < ceBook.getLevel())
            ) return;

            event.setCancelled(true);

            if (preApplyEvent.getSuccessful()) {
                if (!Methods.isEventCancelled(new BookApplyEvent(player, item, ceBook))) {
                    final ItemStack clone = item.clone();

                    this.crazyManager.addEnchantment(clone, enchantment, ceBook.getLevel());

                    event.setCurrentItem(clone);

                    player.setItemOnCursor(null);

                    user.sendMessage(MessageKeys.enchantment_upgrade_success, new HashMap<>() {{
                        put("{enchantment}", enchantment.getCustomName());
                        put("{level}", String.valueOf(ceBook.getLevel()));
                    }});

                    player.playSound(player.getLocation(), enchantment.getSound(), 1, 1);
                    // ToDo potentially add pitch and volume options.
                }

                return;
            } else if (preApplyEvent.getDestroyed()) {
                if (!Methods.isEventCancelled(new BookDestroyEvent(player, item, ceBook))) {
                    if (config.getBoolean("Settings.EnchantmentOptions.Armor-Upgrade.Enchantment-Break", true)) {
                        if (hasWhiteScrollProtection) {
                            event.setCurrentItem(Scrolls.removeWhiteScrollProtection(item));

                            user.sendMessage(MessageKeys.item_was_protected);
                        } else {
                            event.setCurrentItem(instance.removeEnchantment(item, enchantment));

                            user.sendMessage(MessageKeys.enchantment_upgrade_destroyed);
                        }
                    } else {
                        if (hasWhiteScrollProtection) {
                            event.setCurrentItem(Scrolls.removeWhiteScrollProtection(item));

                            user.sendMessage(MessageKeys.item_was_protected);
                        } else {
                            event.setCurrentItem(null);

                            user.sendMessage(MessageKeys.item_destroyed);
                        }
                    }

                    player.setItemOnCursor(null);
                    Methods.playItemBreak(player, book);
                }

                return;
            } else {
                if (!Methods.isEventCancelled(new BookFailEvent(player, item, ceBook))) {
                    player.setItemOnCursor(null);

                    user.sendMessage(MessageKeys.enchantment_upgrade_failed);

                    Methods.playItemBreak(player, book);
                }

                return;
            }
        }

        if (!this.crazyManager.canAddEnchantment(player, item)) {
            user.sendMessage(MessageKeys.hit_enchantment_max);

            return;
        }

        for (CEnchantment enchant : enchantments.keySet()) {
            if (enchant.conflictsWith(enchantment)) {
                user.sendMessage(MessageKeys.conflicting_enchant);

                return;
            }
        }

        event.setCancelled(true);

        if (preApplyEvent.getSuccessful()) {
            final ItemStack clone = item.clone();

            this.crazyManager.addEnchantment(clone, enchantment, ceBook.getLevel());

            event.setCurrentItem(clone);

            player.setItemOnCursor(null);

            user.sendMessage(MessageKeys.book_works);

            player.playSound(player.getLocation(), enchantment.getSound(), 1, 1);

            return;
        }

        if (preApplyEvent.getDestroyed()) {
            if (hasWhiteScrollProtection) {
                Methods.playItemBreak(player, book);

                event.setCurrentItem(Scrolls.removeWhiteScrollProtection(item));

                user.sendMessage(MessageKeys.item_was_protected);
            } else {
                Methods.playItemBreak(player, item);

                event.setCurrentItem(null);

                user.sendMessage(MessageKeys.item_destroyed);
            }

            player.setItemOnCursor(null);

            return;
        }

        user.sendMessage(MessageKeys.book_failed);

        Methods.playItemBreak(player, book);

        if (config.getBoolean("Settings.EnchantmentOptions.Limit.Change-On-Fail", true)) event.setCurrentItem(crazyManager.changeEnchantmentLimiter(item, 1));

        player.setItemOnCursor(null);
    }

    @EventHandler
    public void onDescriptionSend(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.useInteractedBlock().equals(Event.Result.ALLOW)) return;

        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && FileKeys.config.getPaperConfiguration().getBoolean("Settings.EnchantmentOptions.Right-Click-Book-Description", true)) {
            ItemStack item = Methods.getItemInHand(event.getPlayer());

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