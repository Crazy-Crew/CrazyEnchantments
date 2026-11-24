package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.CrazyItems;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.ShopMenu;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.badbones69.crazyenchantments.paper.api.enums.ShopOption;
import com.badbones69.crazyenchantments.paper.api.events.BuyBookEvent;
import com.badbones69.crazyenchantments.paper.api.managers.ShopManager;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEOption;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.LostBook;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.ryderbelserion.common.api.enums.messages.Messages;
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
import org.bukkit.inventory.PlayerInventory;
import java.util.Map;
import java.util.Optional;

public class ShopListener implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyPlatform platform = this.plugin.getPlatform();

    private final CrazyItems items = this.platform.getCrazyItems();

    private final EnchantmentBookSettings bookSettings = this.platform.getBookSettings();

    private final CrazyManager crazyManager = this.plugin.getCrazyManager();

    private final ProtectionCrystalSettings protectionCrystalSettings = this.crazyManager.getProtectionCrystalSettings();

    private final ShopManager shopManager = this.crazyManager.getShopManager();

    private final CurrencyAPI currencyAPI = this.crazyManager.getCurrencyAPI();

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent event) {
        final ItemStack item = event.getCurrentItem();
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof ShopMenu holder)) return;

        if (item == null) return;

        final Player player = holder.getPlayer();

        event.setCancelled(true);

        if (event.getClickedInventory() != player.getOpenInventory().getTopInventory()) return;

        final PlayerInventory playerInventory = player.getInventory();

        for (Category category : this.bookSettings.getCategories()) {
            if (category.isInGUI() && item.isSimilar(category.getDisplayItem().build(player))) {
                if (Methods.isInventoryFull(player)) return;

                if (category.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                    if (this.currencyAPI.canBuy(player, category)) {
                        this.currencyAPI.takeCurrency(player, category);
                    } else {
                        final String needed = String.valueOf(category.getCost() - this.currencyAPI.getCurrency(player, category.getCurrency()));

                        Methods.switchCurrency(player, category.getCurrency(), "%Money_Needed%", "%XP%", needed);

                        return;
                    }
                }

                final CEBook book = this.crazyManager.getRandomEnchantmentBook(category);

                if (book != null) {
                    this.crazyManager.getCEPlayer(player).ifPresent(cePlayer -> {
                        BuyBookEvent buyBookEvent = new BuyBookEvent(cePlayer, category.getCurrency(), category.getCost(), book);

                        this.plugin.getServer().getPluginManager().callEvent(buyBookEvent);

                        playerInventory.addItem(book.buildBook(player));
                    });
                } else {
                    Messages.no_enchantments_category.sendMessage(player, Map.of("{category}", category.getName()));
                }

                return;
            }

            LostBook lostBook = category.getLostBook();

            if (lostBook.isInGUI() && item.isSimilar(lostBook.getDisplayItem().build(player))) {
                if (Methods.isInventoryFull(player)) return;

                if (lostBook.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                    if (this.currencyAPI.canBuy(player, lostBook)) {
                        this.currencyAPI.takeCurrency(player, lostBook);
                    } else {
                        final String needed = String.valueOf(lostBook.getCost() - this.currencyAPI.getCurrency(player, lostBook.getCurrency()));

                        Methods.switchCurrency(player, lostBook.getCurrency(), "%Money_Needed%", "%XP%", needed);

                        return;
                    }
                }

                playerInventory.addItem(lostBook.getLostBook(category).build(player));

                return;
            }
        }

        for (final ShopOption option : ShopOption.values()) {
            final Optional<CEOption> index = this.platform.getOption(option.getPath());

            if (index.isEmpty()) {
                continue;
            }

            final CEOption value = index.get();

            if (!value.inGUI()) {
                continue;
            }

            if (!item.isSimilar(value.itemBuilder().build(player))) { //todo() add pdc
                continue;
            }

            // If the option is buy-able then it check to see if they player can buy it and take the money.

            if (option.isBuyable()) {
                if (Methods.isInventoryFull(player)) {
                    return;
                }

                final Currency currency = value.currency();

                if (currency != null && player.getGameMode() != GameMode.CREATIVE) {
                    final int cost = value.cost();

                    if (this.currencyAPI.canBuy(player, option)) {
                        this.currencyAPI.takeCurrency(player, option);
                    } else {
                        final String needed = String.valueOf(cost - this.currencyAPI.getCurrency(player, currency));

                        Methods.switchCurrency(player, currency, "%Money_Needed%", "%XP%", needed);

                        return;
                    }
                }
            }

            switch (option) {
                case GKITZ -> {
                    if (!Methods.hasPermission(player, "gkitz", true)) return;

                    if (!this.crazyManager.isGkitzEnabled()) return;

                    MenuManager.openKitsMenu(player);
                }

                case BLACKSMITH -> {
                    if (!Methods.hasPermission(player, "blacksmith", true)) return;

                    MenuManager.openBlackSmithMenu(player);
                }

                case TINKER -> {
                    if (!Methods.hasPermission(player, "tinker", true)) return;

                    MenuManager.openTinkererMenu(player);
                }

                case INFO -> MenuManager.openInfoMenu(player);
                case PROTECTION_CRYSTAL -> playerInventory.addItem(this.protectionCrystalSettings.getCrystal(player));
                case SCRAMBLER -> playerInventory.addItem(this.items.getScramblers(player));
                case SUCCESS_DUST -> playerInventory.addItem(Dust.SUCCESS_DUST.getDust(player));
                case DESTROY_DUST -> playerInventory.addItem(Dust.DESTROY_DUST.getDust(player));
                case BLACK_SCROLL -> playerInventory.addItem(Scrolls.BLACK_SCROLL.getScroll(player));
                case WHITE_SCROLL -> playerInventory.addItem(Scrolls.WHITE_SCROLL.getScroll(player));
                case TRANSMOG_SCROLL -> playerInventory.addItem(Scrolls.TRANSMOG_SCROLL.getScroll(player));
                case SLOT_CRYSTAL -> playerInventory.addItem(this.items.getSlotCrystal(player));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnchantmentTableClick(PlayerInteractEvent event) {
        if (this.shopManager.isEnchantmentTableShop()) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();

            if (block != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == Material.ENCHANTING_TABLE) {
                event.setCancelled(true);

                player.openInventory(new ShopMenu(player, this.shopManager.getInventorySize(), this.shopManager.getInventoryName()).build().getInventory());
            }
        }
    }
}