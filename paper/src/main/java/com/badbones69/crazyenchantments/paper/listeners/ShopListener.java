package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.badbones69.crazyenchantments.paper.api.enums.ShopOption;
import com.badbones69.crazyenchantments.paper.api.events.BuyBookEvent;
import com.badbones69.crazyenchantments.paper.api.managers.ShopManager;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.other.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.objects.LostBook;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
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

    private final CrazyEnchantments plugin = CrazyEnchantments.get();

    private final Starter starter = plugin.getStarter();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    private final Methods methods = starter.getMethods();

    // Settings.
    private final ProtectionCrystalSettings protectionCrystalSettings = starter.getProtectionCrystalSettings();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    // Plugin Managers.
    private final ShopManager shopManager = starter.getShopManager();

    // Plugin Listeners.
    private final ScramblerListener scramblerListener = starter.getScramblerListener();
    private final SlotCrystalListener slotCrystalListener = starter.getSlotCrystalListener();

    // Economy Management.
    private final CurrencyAPI currencyAPI = plugin.getStarter().getCurrencyAPI();

    private final Material enchantmentTable = new ItemBuilder().setMaterial("ENCHANTING_TABLE").getMaterial();

    public void openGUI(Player player) {
        player.openInventory(shopManager.getShopInventory(player));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();

        if (!event.getView().getTitle().equals(shopManager.getInventoryName())) return;
        if (item == null) return;

        event.setCancelled(true);

        if (event.getRawSlot() >= inventory.getSize()) return;

        for (Category category : enchantmentBookSettings.getCategories()) {
            if (category.isInGUI() && item.isSimilar(category.getDisplayItem().build())) {

                if (methods.isInventoryFull(player)) return;

                if (category.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                    if (currencyAPI.canBuy(player, category)) {
                        currencyAPI.takeCurrency(player, category);
                    } else {
                        String needed = String.valueOf(category.getCost() - currencyAPI.getCurrency(player, category.getCurrency()));
                        methods.switchCurrency(player, category.getCurrency(), "%Money_Needed%", "%XP%", needed);
                        return;
                    }
                }

                CEBook book = crazyManager.getRandomEnchantmentBook(category);

                if (book != null) {
                    BuyBookEvent buyBookEvent = new BuyBookEvent(crazyManager.getCEPlayer(player), category.getCurrency(), category.getCost(), book);
                    plugin.getServer().getPluginManager().callEvent(buyBookEvent);
                    player.getInventory().addItem(book.buildBook());
                } else {
                    player.sendMessage(ColorUtils.getPrefix("&cThe category &6" + category.getName() + " &chas no enchantments assigned to it."));
                }

                return;
            }
            LostBook lostBook = category.getLostBook();

            if (lostBook.isInGUI() && item.isSimilar(lostBook.getDisplayItem().build())) {

                if (methods.isInventoryFull(player)) return;

                if (lostBook.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                    if (currencyAPI.canBuy(player, lostBook)) {
                        currencyAPI.takeCurrency(player, lostBook);
                    } else {
                        String needed = String.valueOf(lostBook.getCost() - currencyAPI.getCurrency(player, lostBook.getCurrency()));
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
                // If the option is buy-able then it check to see if they player can buy it and take the money.

                if (option.isBuyable()) {
                    if (methods.isInventoryFull(player)) return;

                    if (option.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                        if (currencyAPI.canBuy(player, option)) {
                            currencyAPI.takeCurrency(player, option);
                        } else {
                            String needed = String.valueOf(option.getCost() - currencyAPI.getCurrency(player, option.getCurrency()));
                            methods.switchCurrency(player, option.getCurrency(), "%Money_Needed%", "%XP%", needed);
                            return;
                        }
                    }
                }

                switch (option) {
                    case GKITZ -> {
                        if (!methods.hasPermission(player, "gkitz", true)) return;

                        if (!crazyManager.isGkitzEnabled()) return;

                        MenuManager.openKitsMenu(player);
                    }

                    case BLACKSMITH -> {
                        if (!methods.hasPermission(player, "blacksmith", true)) return;

                        MenuManager.openBlackSmithMenu(player);
                    }

                    case TINKER -> {
                        if (!methods.hasPermission(player, "tinker", true)) return;

                        MenuManager.openTinkererMenu(player);
                    }

                    case INFO -> MenuManager.openInfoMenu(player);
                    case PROTECTION_CRYSTAL -> player.getInventory().addItem(protectionCrystalSettings.getCrystals());
                    case SCRAMBLER -> player.getInventory().addItem(scramblerListener.getScramblers());
                    case SUCCESS_DUST -> player.getInventory().addItem(Dust.SUCCESS_DUST.getDust());
                    case DESTROY_DUST -> player.getInventory().addItem(Dust.DESTROY_DUST.getDust());
                    case BLACK_SCROLL -> player.getInventory().addItem(Scrolls.BLACK_SCROLL.getScroll());
                    case WHITE_SCROLL -> player.getInventory().addItem(Scrolls.WHITE_SCROLL.getScroll());
                    case TRANSMOG_SCROLL -> player.getInventory().addItem(Scrolls.TRANSMOG_SCROLL.getScroll());
                    case SLOT_CRYSTAL -> player.getInventory().addItem(slotCrystalListener.getSlotCrystal());
                }

                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnchantmentTableClick(PlayerInteractEvent event) {
        if (shopManager.isEnchantmentTableShop()) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();

            if (block != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == enchantmentTable) {
                event.setCancelled(true);
                openGUI(player);
            }
        }
    }
}