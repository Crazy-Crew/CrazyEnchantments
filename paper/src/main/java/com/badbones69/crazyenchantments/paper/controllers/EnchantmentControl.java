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
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.Map;

public class EnchantmentControl implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    @EventHandler(ignoreCancelled = true)
    public void addEnchantment(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        ItemStack book = event.getCursor();

        if (item == null) return;
        if (book.getAmount() > 1 || item.getAmount() > 1) return;
        if (!enchantmentBookSettings.isEnchantmentBook(book) || enchantmentBookSettings.isEnchantmentBook(item)) return;

        CEBook ceBook = enchantmentBookSettings.getCEBook(book);
        CEnchantment enchantment = ceBook.getEnchantment();

        if (enchantment == null || !enchantment.canEnchantItem(item) || ceBook.getAmount() != 1) return;

        Player player = (Player) event.getWhoClicked();

        boolean creativeMode = player.getGameMode() == GameMode.CREATIVE;

        if (creativeMode) {
            player.sendMessage(Messages.PLAYER_IS_IN_CREATIVE_MODE.getMessage());
            return;
        }

        if (crazyManager.enchantStackedItems() || item.getAmount() == 1) {
            boolean hasWhiteScrollProtection = Scrolls.hasWhiteScrollProtection(item);
            boolean success = methods.randomPicker(ceBook.getSuccessRate(), 100);
            boolean destroy = methods.randomPicker(ceBook.getDestroyRate(), 100);
            int bookLevel = ceBook.getLevel();

            Map<CEnchantment, Integer> enchantments = enchantmentBookSettings.getEnchantments(item);
            boolean hasEnchantment = enchantments.containsKey(enchantment);
            boolean isLowerLevel = hasEnchantment && enchantments.get(enchantment) < bookLevel;

            if (hasEnchantment) {
                if (Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Armor-Upgrade.Toggle") && isLowerLevel) {
                    event.setCancelled(true);
                    PreBookApplyEvent preBookApplyEvent = new PreBookApplyEvent(player, item, ceBook, player.getGameMode() == GameMode.CREATIVE, success, destroy);
                    plugin.getServer().getPluginManager().callEvent(preBookApplyEvent);

                    if (!preBookApplyEvent.isCancelled()) {
                        if (success) {
                            BookApplyEvent bookApplyEvent = new BookApplyEvent(player, item, ceBook);
                            plugin.getServer().getPluginManager().callEvent(bookApplyEvent);

                            if (!bookApplyEvent.isCancelled()) {
                                event.setCurrentItem(crazyManager.addEnchantment(item, enchantment, bookLevel));
                                player.setItemOnCursor(new ItemStack(Material.AIR));
                                HashMap<String, String> placeholders = new HashMap<>();
                                placeholders.put("%Enchantment%", enchantment.getCustomName());
                                placeholders.put("%Level%", String.valueOf(bookLevel));
                                player.sendMessage(Messages.ENCHANTMENT_UPGRADE_SUCCESS.getMessage(placeholders));
                                player.playSound(player.getLocation(), enchantment.getSound(), 1, 1);
                                // ToDo potentially add pitch and volume options.
                            }

                            return;
                        } else if (destroy) {
                            BookDestroyEvent bookDestroyEvent = new BookDestroyEvent(player, item, ceBook);
                            plugin.getServer().getPluginManager().callEvent(bookDestroyEvent);

                            if (!bookDestroyEvent.isCancelled()) {

                                if (Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Armor-Upgrade.Enchantment-Break")) {
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
                                        ItemStack newItem = new ItemStack(Material.AIR);
                                        event.setCurrentItem(newItem);
                                        player.sendMessage(Messages.ITEM_DESTROYED.getMessage());
                                    }
                                }

                                player.setItemOnCursor(new ItemStack(Material.AIR));
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                            }

                            return;
                        } else {
                            BookFailEvent bookFailEvent = new BookFailEvent(player, item, ceBook);
                            plugin.getServer().getPluginManager().callEvent(bookFailEvent);

                            if (!bookFailEvent.isCancelled()) {
                                player.setItemOnCursor(new ItemStack(Material.AIR));
                                player.sendMessage(Messages.ENCHANTMENT_UPGRADE_FAILED.getMessage());
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                            }

                            return;
                        }
                    }
                }

                return;
            }

            if (!crazyManager.canAddEnchantment(player, item)) {
                player.sendMessage(Messages.HIT_ENCHANTMENT_MAX.getMessage());
                return;
            }

            event.setCancelled(true);

            if (success) {
                ItemStack newItem = crazyManager.addEnchantment(item, enchantment, ceBook.getLevel());
                event.setCurrentItem(newItem);
                player.setItemOnCursor(new ItemStack(Material.AIR));
                player.sendMessage(Messages.BOOK_WORKS.getMessage());
                player.playSound(player.getLocation(), enchantment.getSound(), 1, 1);
                return;
            }

            if (destroy) {
                if (Scrolls.hasWhiteScrollProtection(item)) {
                    event.setCurrentItem(Scrolls.removeWhiteScrollProtection(item));
                    player.setItemOnCursor(new ItemStack(Material.AIR));
                    player.sendMessage(Messages.ITEM_WAS_PROTECTED.getMessage());
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                    return;
                } else {
                    player.setItemOnCursor(new ItemStack(Material.AIR));
                    event.setCurrentItem(new ItemStack(Material.AIR));
                    player.sendMessage(Messages.ITEM_DESTROYED.getMessage());
                }

                return;
            }
        }

        player.sendMessage(Messages.BOOK_FAILED.getMessage());
        event.setCurrentItem(crazyManager.changeEnchantmentLimiter(item, 1));
        player.setItemOnCursor(new ItemStack(Material.AIR));
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
    }
    
    @EventHandler
    public void onDescriptionSend(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) && Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Right-Click-Book-Description")) {
            ItemStack item = methods.getItemInHand(event.getPlayer());

            if (enchantmentBookSettings.isEnchantmentBook(item)) {
                event.setCancelled(true);
                CEnchantment enchantment = enchantmentBookSettings.getCEBook(item).getEnchantment();
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

        if (event.getItem().getType() == Material.MILK_BUCKET) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    crazyManager.updatePlayerEffects(player);
                }
            }.runTaskLater(plugin, 5);
        }
    }
}