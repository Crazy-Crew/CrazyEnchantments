package com.badbones69.crazyenchantments.listeners;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.api.enums.Dust;
import com.badbones69.crazyenchantments.api.enums.Messages;
import com.badbones69.crazyenchantments.api.enums.Scrolls;
import com.badbones69.crazyenchantments.api.enums.ShopOption;
import com.badbones69.crazyenchantments.api.events.BuyBookEvent;
import com.badbones69.crazyenchantments.api.managers.InfoMenuManager;
import com.badbones69.crazyenchantments.api.managers.ShopManager;
import com.badbones69.crazyenchantments.api.objects.CEBook;
import com.badbones69.crazyenchantments.api.objects.Category;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.api.objects.LostBook;
import com.badbones69.crazyenchantments.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.listeners.ProtectionCrystalListener;
import com.badbones69.crazyenchantments.listeners.ScramblerListener;
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

public class ShopListener implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    private final Methods methods = starter.getMethods();

    // Settings.
    private final ProtectionCrystalSettings protectionCrystalSettings = starter.getProtectionCrystalSettings();

    // Plugin Managers.
    private final ShopManager shopManager = starter.getShopManager();

    private final InfoMenuManager infoMenuManager = crazyManager.getInfoMenuManager();

    // Plugin Listeners.
    private final ScramblerListener scramblerListener = plugin.getScramblerListener();

    // Economy Management.
    private final CurrencyAPI currencyAPI = plugin.getStarter().getCurrencyAPI();

    private final Material enchantmentTable = new ItemBuilder().setMaterial("ENCHANTING_TABLE").getMaterial();

    public void openGUI(Player player) {
        player.openInventory(shopManager.getShopInventory(player));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        Inventory inventory = e.getInventory();
        Player player = (Player) e.getWhoClicked();

        if (inventory != null && e.getView().getTitle().equals(shopManager.getInventoryName())) {
            e.setCancelled(true);

            if (e.getRawSlot() >= inventory.getSize()) return;

            if (item != null) {
                for (Category category : crazyManager.getCategories()) {
                    if (category.isInGUI() && item.isSimilar(category.getDisplayItem().build())) {

                        if (methods.isInventoryFull(player)) {
                            player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                            return;
                        }

                        if (category.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                            if (currencyAPI.canBuy(player, category)) {
                                currencyAPI.takeCurrency(player, category);
                            } else {
                                String needed = (category.getCost() - currencyAPI.getCurrency(player, category.getCurrency())) + "";
                                methods.switchCurrency(player, category.getCurrency(), "%Money_Needed%", "%XP%", needed);
                                return;
                            }
                        }

                        CEBook book = crazyManager.getRandomEnchantmentBook(category);

                        if (book != null) {
                            BuyBookEvent event = new BuyBookEvent(crazyManager.getCEPlayer(player), category.getCurrency(), category.getCost(), book);
                            plugin.getServer().getPluginManager().callEvent(event);
                            player.getInventory().addItem(book.buildBook());
                        } else {
                            player.sendMessage(methods.getPrefix("&cThe category &6" + category.getName() + " &chas no enchantments assigned to it."));
                        }

                        return;
                    }
                    LostBook lostBook = category.getLostBook();

                    if (lostBook.isInGUI() && item.isSimilar(lostBook.getDisplayItem().build())) {

                        if (methods.isInventoryFull(player)) {
                            player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                            return;
                        }

                        if (lostBook.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                            if (currencyAPI.canBuy(player, lostBook)) {
                                currencyAPI.takeCurrency(player, lostBook);
                            } else {
                                String needed = (lostBook.getCost() - currencyAPI.getCurrency(player, lostBook.getCurrency())) + "";
                                methods.switchCurrency(player, lostBook.getCurrency(), "%Money_Needed%", "%XP%", needed);
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
                            if (methods.isInventoryFull(player)) {
                                player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                                return;
                            }

                            if (option.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                                if (currencyAPI.canBuy(player, option)) {
                                    currencyAPI.takeCurrency(player, option);
                                } else {
                                    String needed = (option.getCost() - currencyAPI.getCurrency(player, option.getCurrency())) + "";
                                    methods.switchCurrency(player, option.getCurrency(), "%Money_Needed%", "%XP%", needed);
                                    return;
                                }
                            }
                        }

                        switch (option) {
                            case GKITZ -> {
                                if (!methods.hasPermission(player, "gkitz", true)) return;
                                //GKitzController.openGUI(player);
                            }

                            case BLACKSMITH -> {
                                if (!methods.hasPermission(player, "blacksmith", true)) return;
                                //BlackSmith.openBlackSmith(player);
                            }

                            case TINKER -> {
                                if (!methods.hasPermission(player, "tinker", true)) return;
                                //Tinkerer.openTinker(player);
                            }

                            case INFO -> infoMenuManager.openInfoMenu(player);
                            case PROTECTION_CRYSTAL -> player.getInventory().addItem(protectionCrystalSettings.getCrystals());
                            case SCRAMBLER -> player.getInventory().addItem(scramblerListener.getScramblers());
                            case SUCCESS_DUST -> player.getInventory().addItem(Dust.SUCCESS_DUST.getDust());
                            case DESTROY_DUST -> player.getInventory().addItem(Dust.DESTROY_DUST.getDust());
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

    @EventHandler(ignoreCancelled = true)
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