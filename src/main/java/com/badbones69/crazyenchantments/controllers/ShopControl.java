package com.badbones69.crazyenchantments.controllers;

import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.api.enums.Dust;
import com.badbones69.crazyenchantments.api.enums.Messages;
import com.badbones69.crazyenchantments.api.enums.Scrolls;
import com.badbones69.crazyenchantments.api.enums.ShopOption;
import com.badbones69.crazyenchantments.api.events.BuyBookEvent;
import com.badbones69.crazyenchantments.api.managers.ShopManager;
import com.badbones69.crazyenchantments.api.objects.CEBook;
import com.badbones69.crazyenchantments.api.objects.Category;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.api.objects.LostBook;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopControl implements Listener {
    
    private static CrazyManager ce = CrazyManager.getInstance();
    private static ShopManager shopManager = ce.getShopManager();
    private Material enchantmentTable = new ItemBuilder().setMaterial("ENCHANTING_TABLE").getMaterial();
    
    public static void openGUI(Player player) {
        player.openInventory(shopManager.getShopInventory(player));
    }
    
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        Inventory inventory = e.getInventory();
        Player player = (Player) e.getWhoClicked();
        if (inventory != null && e.getView().getTitle().equals(shopManager.getInventoryName())) {
            e.setCancelled(true);
            if (e.getRawSlot() >= inventory.getSize()) return;

            if (item != null) {
                for (Category category : ce.getCategories()) {
                    if (category.isInGUI() && item.isSimilar(category.getDisplayItem().build())) {

                        if (Methods.isInventoryFull(player)) {
                            player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                            return;
                        }

                        if (category.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                            if (CurrencyAPI.canBuy(player, category)) {
                                CurrencyAPI.takeCurrency(player, category);
                            } else {
                                String needed = (category.getCost() - CurrencyAPI.getCurrency(player, category.getCurrency())) + "";
                                Methods.switchCurrency(player, category.getCurrency(), "%Money_Needed%", "%XP%", needed);
                                return;
                            }
                        }

                        CEBook book = ce.getRandomEnchantmentBook(category);

                        if (book != null) {
                            BuyBookEvent event = new BuyBookEvent(ce.getCEPlayer(player), category.getCurrency(), category.getCost(), book);
                            Bukkit.getPluginManager().callEvent(event);
                            player.getInventory().addItem(book.buildBook());
                        } else {
                            player.sendMessage(Methods.getPrefix("&cThe category &6" + category.getName() + " &chas no enchantments assigned to it."));
                        }

                        return;
                    }
                    LostBook lostBook = category.getLostBook();

                    if (lostBook.isInGUI() && item.isSimilar(lostBook.getDisplayItem().build())) {

                        if (Methods.isInventoryFull(player)) {
                            player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                            return;
                        }

                        if (lostBook.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                            if (CurrencyAPI.canBuy(player, lostBook)) {
                                CurrencyAPI.takeCurrency(player, lostBook);
                            } else {
                                String needed = (lostBook.getCost() - CurrencyAPI.getCurrency(player, lostBook.getCurrency())) + "";
                                Methods.switchCurrency(player, lostBook.getCurrency(), "%Money_Needed%", "%XP%", needed);
                                return;
                            }
                        }

                        player.getInventory().addItem(lostBook.getLostBook(category).build());
                        return;
                    }
                }

                for (ShopOption option : ShopOption.values()) {
                    if (option.isInGUI() && item.isSimilar(option.getItem())) {
                        // If the option is buyable then it check to see if they player can buy it and take the money.

                        if (option.isBuyable()) {
                            if (Methods.isInventoryFull(player)) {
                                player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                                return;
                            }
                            if (option.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                                if (CurrencyAPI.canBuy(player, option)) {
                                    CurrencyAPI.takeCurrency(player, option);
                                } else {
                                    String needed = (option.getCost() - CurrencyAPI.getCurrency(player, option.getCurrency())) + "";
                                    Methods.switchCurrency(player, option.getCurrency(), "%Money_Needed%", "%XP%", needed);
                                    return;
                                }
                            }
                        }

                        switch (option) {
                            case GKITZ -> {
                                if (!Methods.hasPermission(player, "gkitz", true)) return;
                                GKitzController.openGUI(player);
                            }
                            case BLACKSMITH -> {
                                if (!Methods.hasPermission(player, "blacksmith", true)) return;
                                BlackSmith.openBlackSmith(player);
                            }
                            case TINKER -> {
                                if (!Methods.hasPermission(player, "tinker", true)) return;
                                Tinkerer.openTinker(player);
                            }
                            case INFO -> ce.getInfoMenuManager().openInfoMenu(player);
                            case PROTECTION_CRYSTAL -> player.getInventory().addItem(ProtectionCrystal.getCrystals());
                            case SUCCESS_DUST -> player.getInventory().addItem(Dust.SUCCESS_DUST.getDust());
                            case DESTROY_DUST -> player.getInventory().addItem(Dust.DESTROY_DUST.getDust());
                            case SCRAMBLER -> player.getInventory().addItem(Scrambler.getScramblers());
                            case BLACK_SCROLL -> player.getInventory().addItem(Scrolls.BLACK_SCROLL.getScroll());
                            case WHITE_SCROLL -> player.getInventory().addItem(Scrolls.WHITE_SCROLL.getScroll());
                            case TRANSMOG_SCROLL -> player.getInventory().addItem(Scrolls.TRANSMOG_SCROLL.getScroll());
                        }
                        return;
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onEnchantmentTableClick(PlayerInteractEvent e) {
        if (shopManager.isEnchantmentTableShop()) {
            Player player = e.getPlayer();
            Block block = e.getClickedBlock();
            if (block != null && e.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == enchantmentTable) {
                e.setCancelled(true);
                openGUI(player);
            }
        }
    }
}