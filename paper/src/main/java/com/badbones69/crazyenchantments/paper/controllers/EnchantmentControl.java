package com.badbones69.crazyenchantments.paper.controllers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
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
        final CommentedConfigurationNode config = Files.CONFIG.getConfiguration();

        final Player player = (Player) event.getWhoClicked();
        final ItemStack item = event.getCurrentItem();
        final ItemStack book = event.getCursor();

        if (item == null
           || book.getAmount() > 1
           || item.getAmount() > 1
           || !this.enchantmentBookSettings.isEnchantmentBook(book)
           || this.enchantmentBookSettings.isEnchantmentBook(item)
           || this.methods.inCreativeMode(player)
        ) return;

        final CEBook ceBook = enchantmentBookSettings.getCEBook(book);

        if (ceBook == null) return;

        final CEnchantment enchantment = ceBook.getEnchantment();

        if (enchantment == null || !enchantment.canEnchantItem(item)) return;

        final Map<CEnchantment, Integer> enchantments = this.enchantmentBookSettings.getEnchantments(item);

        final boolean hasWhiteScrollProtection = Scrolls.hasWhiteScrollProtection(item);
        final boolean hasEnchantment = enchantments.containsKey(enchantment);

        final PreBookApplyEvent preApplyEvent = new PreBookApplyEvent(player, item, ceBook);

        if (this.methods.isEventCancelled(preApplyEvent)) return;

        if (hasEnchantment) {
            if (!config.node("Settings", "EnchantmentOptions", "Armor-Upgrade", "Toggle").getBoolean(true)
                    || !(enchantments.get(enchantment) < ceBook.getLevel())) return;

            event.setCancelled(true);

            if (preApplyEvent.getSuccessful()) {
                if (!this.methods.isEventCancelled(new BookApplyEvent(player, item, ceBook))) {
                    final ItemStack clone = item.clone();

                    this.crazyManager.addEnchantment(clone, enchantment, ceBook.getLevel());

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
                if (!this.methods.isEventCancelled(new BookDestroyEvent(player, item, ceBook))) {
                    if (config.node("Settings", "EnchantmentOptions", "Armor-Upgrade", "Enchantment-Break").getBoolean(true)) {
                        if (hasWhiteScrollProtection) {
                            event.setCurrentItem(Scrolls.removeWhiteScrollProtection(item));

                            Messages.item_was_protected.sendMessage(player);
                        } else {
                            event.setCurrentItem(this.enchantmentBookSettings.removeEnchantment(item, enchantment));

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

                    this.methods.playItemBreak(player, book);
                }

                return;
            } else {
                if (!this.methods.isEventCancelled(new BookFailEvent(player, item, ceBook))) {
                    player.setItemOnCursor(null);

                    Messages.enchantment_upgrade_failed.sendMessage(player);

                    this.methods.playItemBreak(player, book);
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

            this.crazyManager.addEnchantment(clone, enchantment, ceBook.getLevel());

            event.setCurrentItem(clone);

            player.setItemOnCursor(null);

            Messages.book_works.sendMessage(player);

            player.playSound(player.getLocation(), enchantment.getSound(), 1, 1);

            return;
        }

        if (preApplyEvent.getDestroyed()) {
            if (hasWhiteScrollProtection) {
                this.methods.playItemBreak(player, book);

                event.setCurrentItem(Scrolls.removeWhiteScrollProtection(item));

                Messages.item_was_protected.sendMessage(player);
            } else {
                this.methods.playItemBreak(player, item);

                event.setCurrentItem(null);

                Messages.item_destroyed.sendMessage(player);
            }

            player.setItemOnCursor(null);

            return;
        }

        Messages.book_failed.sendMessage(player);

        this.methods.playItemBreak(player, book);

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
            final ItemStack item = this.methods.getItemInHand(event.getPlayer());

            final CEBook book = this.enchantmentBookSettings.getCEBook(item);

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