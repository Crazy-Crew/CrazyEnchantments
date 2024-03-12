package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.ShopMenu;
import com.badbones69.crazyenchantments.paper.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.badbones69.crazyenchantments.paper.api.enums.ShopOption;
import com.badbones69.crazyenchantments.paper.api.events.BuyBookEvent;
import com.badbones69.crazyenchantments.paper.api.managers.ShopManager;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.LostBook;
import com.badbones69.crazyenchantments.paper.api.objects.other.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ShopListener implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final CrazyManager crazyManager = starter.getCrazyManager();

    @NotNull
    private final Methods methods = starter.getMethods();

    // Settings.
    @NotNull
    private final ProtectionCrystalSettings protectionCrystalSettings = this.starter.getProtectionCrystalSettings();

    @NotNull
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    // Plugin Managers.
    @NotNull
    private final ShopManager shopManager = this.starter.getShopManager();

    // Plugin Listeners.
    @NotNull
    private final ScramblerListener scramblerListener = this.starter.getScramblerListener();
    @NotNull
    private final SlotCrystalListener slotCrystalListener = this.starter.getSlotCrystalListener();

    // Economy Management.
    @NotNull
    private final CurrencyAPI currencyAPI = this.starter.getCurrencyAPI();

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof ShopMenu holder)) return;

        if (item == null) return;

        Player player = holder.getPlayer();

        event.setCancelled(true);

        if (event.getClickedInventory() != player.getOpenInventory().getTopInventory()) return;

        for (Category category : this.enchantmentBookSettings.getCategories()) {
            if (category.isInGUI() && item.isSimilar(category.getDisplayItem().build())) {

                if (this.methods.isInventoryFull(player)) return;

                if (category.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                    if (this.currencyAPI.canBuy(player, category)) {
                        this.currencyAPI.takeCurrency(player, category);
                    } else {
                        String needed = String.valueOf(category.getCost() - this.currencyAPI.getCurrency(player, category.getCurrency()));
                        this.methods.switchCurrency(player, category.getCurrency(), "%Money_Needed%", "%XP%", needed);
                        return;
                    }
                }

                CEBook book = crazyManager.getRandomEnchantmentBook(category);

                if (book != null) {
                    BuyBookEvent buyBookEvent = new BuyBookEvent(this.crazyManager.getCEPlayer(player), category.getCurrency(), category.getCost(), book);
                    this.plugin.getServer().getPluginManager().callEvent(buyBookEvent);
                    player.getInventory().addItem(book.buildBook());
                } else {
                    player.sendMessage(ColorUtils.getPrefix("&cThe category &6" + category.getName() + " &chas no enchantments assigned to it."));
                }

                return;
            }
            LostBook lostBook = category.getLostBook();

            if (lostBook.isInGUI() && item.isSimilar(lostBook.getDisplayItem().build())) {
                if (this.methods.isInventoryFull(player)) return;

                if (lostBook.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                    if (this.currencyAPI.canBuy(player, lostBook)) {
                        this.currencyAPI.takeCurrency(player, lostBook);
                    } else {
                        String needed = String.valueOf(lostBook.getCost() - this.currencyAPI.getCurrency(player, lostBook.getCurrency()));
                        this.methods.switchCurrency(player, lostBook.getCurrency(), "%Money_Needed%", "%XP%", needed);
                        return;
                    }
                }

                player.getInventory().addItem(lostBook.getLostBook(category).build());
                return;
            }
        }

        for (ShopOption option : ShopOption.values()) {
            if (option.isInGui() && item.isSimilar(option.getItem())) {
                // If the option is buy-able then it check to see if they player can buy it and take the money.

                if (option.isBuyable()) {
                    if (this.methods.isInventoryFull(player)) return;

                    if (option.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                        if (this.currencyAPI.canBuy(player, option)) {
                            this.currencyAPI.takeCurrency(player, option);
                        } else {
                            String needed = String.valueOf(option.getCost() - this.currencyAPI.getCurrency(player, option.getCurrency()));
                            this.methods.switchCurrency(player, option.getCurrency(), "%Money_Needed%", "%XP%", needed);
                            return;
                        }
                    }
                }

                switch (option) {
                    case GKITZ -> {
                        if (!this.methods.hasPermission(player, "gkitz", true)) return;

                        if (!this.crazyManager.isGkitzEnabled()) return;

                        MenuManager.openKitsMenu(player);
                    }

                    case BLACKSMITH -> {
                        if (!this.methods.hasPermission(player, "blacksmith", true)) return;

                        MenuManager.openBlackSmithMenu(player);
                    }

                    case TINKER -> {
                        if (!this.methods.hasPermission(player, "tinker", true)) return;

                        MenuManager.openTinkererMenu(player);
                    }

                    case INFO -> MenuManager.openInfoMenu(player);
                    case PROTECTION_CRYSTAL -> player.getInventory().addItem(this.protectionCrystalSettings.getCrystals());
                    case SCRAMBLER -> player.getInventory().addItem(this.scramblerListener.getScramblers());
                    case SUCCESS_DUST -> player.getInventory().addItem(Dust.SUCCESS_DUST.getDust());
                    case DESTROY_DUST -> player.getInventory().addItem(Dust.DESTROY_DUST.getDust());
                    case BLACK_SCROLL -> player.getInventory().addItem(Scrolls.BLACK_SCROLL.getScroll());
                    case WHITE_SCROLL -> player.getInventory().addItem(Scrolls.WHITE_SCROLL.getScroll());
                    case TRANSMOG_SCROLL -> player.getInventory().addItem(Scrolls.TRANSMOG_SCROLL.getScroll());
                    case SLOT_CRYSTAL -> player.getInventory().addItem(this.slotCrystalListener.getSlotCrystal());
                }

                return;
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