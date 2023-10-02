package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.badbones69.crazyenchantments.paper.api.enums.ShopOption;
import com.badbones69.crazyenchantments.paper.api.events.BuyBookEvent;
import com.badbones69.crazyenchantments.paper.api.managers.guis.InfoMenuManager;
import com.badbones69.crazyenchantments.paper.api.managers.ShopManager;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.objects.LostBook;
import com.badbones69.crazyenchantments.paper.controllers.BlackSmith;
import com.badbones69.crazyenchantments.paper.controllers.Tinkerer;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.paper.utilities.misc.ColorUtils;
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

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Starter starter = this.plugin.getStarter();

    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    private final Methods methods = this.starter.getMethods();

    // Settings.
    private final ProtectionCrystalSettings protectionCrystalSettings = this.starter.getProtectionCrystalSettings();

    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    // Plugin Managers.
    private final ShopManager shopManager = this.starter.getShopManager();

    private final InfoMenuManager infoMenuManager = this.starter.getInfoMenuManager();

    private final Tinkerer tinkerer = this.plugin.getTinkerer();

    private final BlackSmith blackSmith = this.plugin.getBlackSmith();

    // Plugin Listeners.
    private final ScramblerListener scramblerListener = this.starter.getScramblerListener();

    // Economy Management.
    private final CurrencyAPI currencyAPI = this.plugin.getStarter().getCurrencyAPI();

    private final Material enchantmentTable = new ItemBuilder().setMaterial("ENCHANTING_TABLE").getMaterial();

    public void openGUI(Player player) {
        player.openInventory(this.shopManager.getShopInventory(player));
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();

        if (!event.getView().getTitle().equals(this.shopManager.getInventoryName())) return;
        if (item == null) return;

        event.setCancelled(true);

        if (event.getRawSlot() >= inventory.getSize()) return;

        for (Category category : this.enchantmentBookSettings.getCategories()) {
            if (category.isInGUI() && item.isSimilar(category.getDisplayItem().build())) {

                if (this.methods.isInventoryFull(player)) {
                    player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                    return;
                }

                if (category.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                    if (this.currencyAPI.canBuy(player, category)) {
                        this.currencyAPI.takeCurrency(player, category);
                    } else {
                        String needed = String.valueOf(category.getCost() - this.currencyAPI.getCurrency(player, category.getCurrency()));
                        this.methods.switchCurrency(player, category.getCurrency(), "%Money_Needed%", "%XP%", needed);
                        return;
                    }
                }

                CEBook book = this.crazyManager.getRandomEnchantmentBook(category);

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

                if (this.methods.isInventoryFull(player)) {
                    player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                    return;
                }

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
            if (option.isInGUI() && item.isSimilar(option.getItem())) {
                // If the option is buy-able then it check to see if they player can buy it and take the money.

                if (option.isBuyable()) {
                    if (this.methods.isInventoryFull(player)) {
                        player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                        return;
                    }

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
                        this.plugin.getgKitzController().openGUI(player);
                    }

                    case BLACKSMITH -> {
                        if (!this.methods.hasPermission(player, "blacksmith", true)) return;
                        this.blackSmith.openBlackSmith(player);
                    }

                    case TINKER -> {
                        if (!this.methods.hasPermission(player, "tinker", true)) return;
                        this.tinkerer.openTinker(player);
                    }

                    case INFO -> this.infoMenuManager.openInfoMenu(player);
                    case PROTECTION_CRYSTAL -> player.getInventory().addItem(this.protectionCrystalSettings.getCrystals());
                    case SCRAMBLER -> player.getInventory().addItem(this.scramblerListener.getScramblers());
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

    @EventHandler(ignoreCancelled = true)
    public void onEnchantmentTableClick(PlayerInteractEvent event) {
        if (this.shopManager.isEnchantmentTableShop()) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();

            if (block != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == enchantmentTable) {
                event.setCancelled(true);
                openGUI(player);
            }
        }
    }
}