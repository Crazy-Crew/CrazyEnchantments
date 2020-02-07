package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.api.enums.Dust;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.enums.Scrolls;
import me.badbones69.crazyenchantments.api.enums.ShopOption;
import me.badbones69.crazyenchantments.api.events.BuyBookEvent;
import me.badbones69.crazyenchantments.api.managers.ShopManager;
import me.badbones69.crazyenchantments.api.objects.CEBook;
import me.badbones69.crazyenchantments.api.objects.Category;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.api.objects.LostBook;
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

import java.util.HashMap;

public class ShopControl implements Listener {
    
    private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
    private static ShopManager shopManager = ce.getShopManager();
    private Material enchantmentTable = new ItemBuilder().setMaterial("ENCHANTING_TABLE", "ENCHANTMENT_TABLE").getMaterial();
    
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
                                HashMap<String, String> placeholders = new HashMap<>();
                                placeholders.put("%Money_Needed%", needed);
                                placeholders.put("%XP%", needed);
                                switch (category.getCurrency()) {
                                    case VAULT:
                                        player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(placeholders));
                                        break;
                                    case XP_LEVEL:
                                        player.sendMessage(Messages.NEED_MORE_XP_LEVELS.getMessage(placeholders));
                                        break;
                                    case XP_TOTAL:
                                        player.sendMessage(Messages.NEED_MORE_TOTAL_XP.getMessage(placeholders));
                                        break;
                                }
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
                                HashMap<String, String> placeholders = new HashMap<>();
                                placeholders.put("%Money_Needed%", needed);
                                placeholders.put("%XP%", needed);
                                switch (lostBook.getCurrency()) {
                                    case VAULT:
                                        player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(placeholders));
                                        break;
                                    case XP_LEVEL:
                                        player.sendMessage(Messages.NEED_MORE_XP_LEVELS.getMessage(placeholders));
                                        break;
                                    case XP_TOTAL:
                                        player.sendMessage(Messages.NEED_MORE_TOTAL_XP.getMessage(placeholders));
                                        break;
                                }
                                return;
                            }
                        }
                        player.getInventory().addItem(lostBook.getLostBook(category).build());
                        return;
                    }
                }
                for (ShopOption option : ShopOption.values()) {
                    if (option.isInGUI() && item.isSimilar(option.getItem())) {
                        //If the option is buyable then it check to see if they player can buy it and take the money.
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
                                    HashMap<String, String> placeholders = new HashMap<>();
                                    placeholders.put("%Money_Needed%", needed);
                                    placeholders.put("%XP%", needed);
                                    switch (option.getCurrency()) {
                                        case VAULT:
                                            player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(placeholders));
                                            break;
                                        case XP_LEVEL:
                                            player.sendMessage(Messages.NEED_MORE_XP_LEVELS.getMessage(placeholders));
                                            break;
                                        case XP_TOTAL:
                                            player.sendMessage(Messages.NEED_MORE_TOTAL_XP.getMessage(placeholders));
                                            break;
                                    }
                                    return;
                                }
                            }
                        }
                        switch (option) {
                            case GKITZ:
                                if (!Methods.hasPermission(player, "gkitz", true)) return;
                                GKitzController.openGUI(player);
                                break;
                            case BLACKSMITH:
                                if (!Methods.hasPermission(player, "blacksmith", true)) return;
                                BlackSmith.openBlackSmith(player);
                                break;
                            case TINKER:
                                if (!Methods.hasPermission(player, "tinker", true)) return;
                                Tinkerer.openTinker(player);
                                break;
                            case INFO:
                                ce.getInfoMenuManager().openInfoMenu(player);
                                break;
                            case PROTECTION_CRYSTAL:
                                player.getInventory().addItem(ProtectionCrystal.getCrystals());
                                break;
                            case SUCCESS_DUST:
                                player.getInventory().addItem(Dust.SUCCESS_DUST.getDust());
                                break;
                            case DESTROY_DUST:
                                player.getInventory().addItem(Dust.DESTROY_DUST.getDust());
                                break;
                            case SCRAMBLER:
                                player.getInventory().addItem(Scrambler.getScramblers());
                                break;
                            case BLACK_SCROLL:
                                player.getInventory().addItem(Scrolls.BLACK_SCROLL.getScroll());
                                break;
                            case WHITE_SCROLL:
                                player.getInventory().addItem(Scrolls.WHITE_SCROLL.getScroll());
                                break;
                            case TRANSMOG_SCROLL:
                                player.getInventory().addItem(Scrolls.TRANSMOG_SCROLL.getScroll());
                                break;
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