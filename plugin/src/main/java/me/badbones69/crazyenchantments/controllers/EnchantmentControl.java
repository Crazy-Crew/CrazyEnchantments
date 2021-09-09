package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.ArmorType;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.events.ArmorEquipEvent;
import me.badbones69.crazyenchantments.api.events.ArmorEquipEvent.EquipMethod;
import me.badbones69.crazyenchantments.api.events.BookDestroyEvent;
import me.badbones69.crazyenchantments.api.events.BookFailEvent;
import me.badbones69.crazyenchantments.api.events.PreBookApplyEvent;
import me.badbones69.crazyenchantments.api.objects.CEBook;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.multisupport.Version;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class EnchantmentControl implements Listener {
    
    private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
    
    private static HashMap<String, String> enchants = new HashMap<>();
    
    @EventHandler
    public void addEnchantment(InventoryClickEvent e) {
        if (e.getCursor() != null && e.getCurrentItem() != null) {
            ItemStack item = e.getCurrentItem();
            if (ce.isEnchantmentBook(e.getCursor())) {
                CEBook ceBook = ce.getCEBook(e.getCursor());
                CEnchantment enchantment = ceBook.getEnchantment();
                if (enchantment != null && enchantment.canEnchantItem(item) && ceBook.getAmount() == 1) {
                    Player player = (Player) e.getWhoClicked();
                    if (ce.enchantStackedItems() || item.getAmount() == 1) {
                        boolean success = Methods.randomPicker(ceBook.getSuccessRate(), 100);
                        boolean destroy = Methods.randomPicker(ceBook.getDestroyRate(), 100);
                        int bookLevel = ceBook.getLevel();
                        boolean hasEnchantment = false;
                        boolean isLowerLevel = false;
                        if (ce.hasEnchantment(item, enchantment)) {
                            hasEnchantment = true;
                            if (ce.getLevel(item, enchantment) < bookLevel) {
                                isLowerLevel = true;
                            }
                        }
                        if (hasEnchantment) {
                            if (Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Armor-Upgrade.Toggle") && isLowerLevel) {
                                e.setCancelled(true);
                                PreBookApplyEvent preBookApplyEvent = new PreBookApplyEvent(player, item, ceBook, player.getGameMode() == GameMode.CREATIVE, success, destroy);
                                Bukkit.getPluginManager().callEvent(preBookApplyEvent);
                                if (!preBookApplyEvent.isCancelled()) {
                                    if (success || player.getGameMode() == GameMode.CREATIVE) {
                                        BookFailEvent bookApplyEvent = new BookFailEvent(player, item, ceBook);
                                        Bukkit.getPluginManager().callEvent(bookApplyEvent);
                                        if (!bookApplyEvent.isCancelled()) {
                                            e.setCurrentItem(ce.addEnchantment(item, enchantment, bookLevel));
                                            player.setItemOnCursor(new ItemStack(Material.AIR));
                                            HashMap<String, String> placeholders = new HashMap<>();
                                            placeholders.put("%Enchantment%", enchantment.getCustomName());
                                            placeholders.put("%Level%", bookLevel + "");
                                            player.sendMessage(Messages.ENCHANTMENT_UPGRADE_SUCCESS.getMessage(placeholders));
                                            player.playSound(player.getLocation(), ce.getSound("ENTITY_PLAYER_LEVELUP", "LEVEL_UP"), 1, 1);
                                        }
                                        return;
                                    } else if (destroy) {
                                        BookDestroyEvent bookDestroyEvent = new BookDestroyEvent(player, item, ceBook);
                                        Bukkit.getPluginManager().callEvent(bookDestroyEvent);
                                        if (!bookDestroyEvent.isCancelled()) {
                                            if (Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Armor-Upgrade.Enchantment-Break")) {
                                                if (ce.hasWhiteScrollProtection(item)) {
                                                    e.setCurrentItem(ce.removeWhiteScrollProtection(item));
                                                    player.sendMessage(Messages.ITEM_WAS_PROTECTED.getMessage());
                                                } else {
                                                    ItemStack newItem = ce.removeEnchantment(item, enchantment);
                                                    if (e.getInventory().getType() == InventoryType.CRAFTING && e.getRawSlot() >= 5 && e.getRawSlot() <= 8) {
                                                        ArmorEquipEvent event = new ArmorEquipEvent(player, EquipMethod.DRAG, ArmorType.matchType(item), item, newItem);
                                                        Bukkit.getPluginManager().callEvent(event);
                                                    }
                                                    player.sendMessage(Messages.ENCHANTMENT_UPGRADE_DESTROYED.getMessage());
                                                }
                                            } else {
                                                if (ce.hasWhiteScrollProtection(item)) {
                                                    e.setCurrentItem(ce.removeWhiteScrollProtection(item));
                                                    player.sendMessage(Messages.ITEM_WAS_PROTECTED.getMessage());
                                                } else {
                                                    ItemStack newItem = new ItemStack(Material.AIR);
                                                    e.setCurrentItem(newItem);
                                                    if (e.getInventory().getType() == InventoryType.CRAFTING && e.getRawSlot() >= 5 && e.getRawSlot() <= 8) {
                                                        ArmorEquipEvent event = new ArmorEquipEvent(player, EquipMethod.BROKE, ArmorType.matchType(item), item, newItem);
                                                        Bukkit.getPluginManager().callEvent(event);
                                                    }
                                                    player.sendMessage(Messages.ITEM_DESTROYED.getMessage());
                                                }
                                            }
                                            player.setItemOnCursor(new ItemStack(Material.AIR));
                                            player.playSound(player.getLocation(), ce.getSound("ENTITY_ITEM_BREAK", "ITEM_BREAK"), 1, 1);
                                        }
                                        return;
                                    } else {
                                        BookFailEvent bookFailEvent = new BookFailEvent(player, item, ceBook);
                                        Bukkit.getPluginManager().callEvent(bookFailEvent);
                                        if (!bookFailEvent.isCancelled()) {
                                            player.setItemOnCursor(new ItemStack(Material.AIR));
                                            player.sendMessage(Messages.ENCHANTMENT_UPGRADE_FAILED.getMessage());
                                            player.playSound(player.getLocation(), ce.getSound("ENTITY_ITEM_BREAK", "ITEM_BREAK"), 1, 1);
                                        }
                                        return;
                                    }
                                }
                            }
                            return;
                        }
                        if (!ce.canAddEnchantment(player, item)) {
                            player.sendMessage(Messages.HIT_ENCHANTMENT_MAX.getMessage());
                            return;
                        }
                        e.setCancelled(true);
                        if (success || player.getGameMode() == GameMode.CREATIVE) {
                            ItemStack newItem = ce.addEnchantment(item, enchantment, ceBook.getLevel());
                            ItemStack oldItem = new ItemStack(Material.AIR);
                            e.setCurrentItem(newItem);
                            if (e.getInventory().getType() == InventoryType.CRAFTING && e.getRawSlot() >= 5 && e.getRawSlot() <= 8) {
                                ArmorEquipEvent event = new ArmorEquipEvent(player, EquipMethod.DRAG, ArmorType.matchType(item), oldItem, newItem);
                                Bukkit.getPluginManager().callEvent(event);
                            }
                            player.setItemOnCursor(new ItemStack(Material.AIR));
                            player.sendMessage(Messages.BOOK_WORKS.getMessage());
                            player.playSound(player.getLocation(), ce.getSound("ENTITY_PLAYER_LEVELUP", "LEVEL_UP"), 1, 1);
                            return;
                        }
                        if (destroy) {
                            if (ce.hasWhiteScrollProtection(item)) {
                                e.setCurrentItem(ce.removeWhiteScrollProtection(item));
                                player.setItemOnCursor(new ItemStack(Material.AIR));
                                player.sendMessage(Messages.ITEM_WAS_PROTECTED.getMessage());
                                player.playSound(player.getLocation(), ce.getSound("ENTITY_ITEM_BREAK", "ITEM_BREAK"), 1, 1);
                                return;
                            } else {
                                ItemStack newItem = new ItemStack(Material.AIR);
                                ItemStack oldItem = new ItemStack(Material.AIR);
                                player.setItemOnCursor(newItem);
                                if (e.getInventory().getType() == InventoryType.CRAFTING && e.getRawSlot() >= 5 && e.getRawSlot() <= 8) {
                                    ArmorEquipEvent event = new ArmorEquipEvent(player, EquipMethod.BROKE, ArmorType.matchType(item), item, newItem);
                                    Bukkit.getPluginManager().callEvent(event);
                                }
                                e.setCurrentItem(oldItem);
                                player.sendMessage(Messages.ITEM_DESTROYED.getMessage());
                            }
                            player.updateInventory();
                            return;
                        }
                    }
                    player.sendMessage(Messages.BOOK_FAILED.getMessage());
                    player.setItemOnCursor(new ItemStack(Material.AIR));
                    player.playSound(player.getLocation(), ce.getSound("ENTITY_ITEM_BREAK", "ITEM_BREAK"), 1, 1);
                    player.updateInventory();
                }
            }
        }
    }
    
    @EventHandler
    public void onDescriptionSend(PlayerInteractEvent e) {
        if (Version.isNewer(Version.v1_8_R3) && e.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if ((e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) && Files.CONFIG.getFile().getBoolean("Settings.EnchantmentOptions.Right-Click-Book-Description")) {
            ItemStack item = Methods.getItemInHand(e.getPlayer());
            if (ce.isEnchantmentBook(item)) {
                e.setCancelled(true);
                CEnchantment enchantment = ce.getCEBook(item).getEnchantment();
                Player player = e.getPlayer();
                if (enchantment.getInfoName().length() > 0) {
                    player.sendMessage(enchantment.getInfoName());
                }
                for (String descriptionLine : enchantment.getInfoDescription()) {
                    player.sendMessage(descriptionLine);
                }
            }
        }
    }
    
    @EventHandler
    public void onMilkDrink(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        if (e.getItem() != null && e.getItem().getType() == Material.MILK_BUCKET) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    ce.updatePlayerEffects(player);
                }
            }.runTaskLater(ce.getPlugin(), 5);
        }
    }
    
}