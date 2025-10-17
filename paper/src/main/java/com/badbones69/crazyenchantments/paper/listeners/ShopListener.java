package com.badbones69.crazyenchantments.paper.listeners;

import org.bukkit.event.Listener;

public class ShopListener implements Listener {

    /*@NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final CategoryManager categoryManager = this.plugin.getCategoryManager();

    private final ItemManager itemManager = this.plugin.getItemManager();

    private final ConfigManager options = this.plugin.getConfigManager();

    @NotNull
    private final CrazyManager crazyManager = null;

    // Plugin Managers.
    @NotNull
    private final ShopManager shopManager = null;

    // Economy Management.
    @NotNull
    private final CurrencyAPI currencyAPI = null;

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof ShopMenu holder)) return;

        if (item == null || item.isEmpty()) return;

        Player player = holder.getPlayer();

        event.setCancelled(true);

        if (event.getClickedInventory() != player.getOpenInventory().getTopInventory()) return;

        final Collection<Category> categories = this.categoryManager.getCategories().values();

        final PlayerInventory playerInventory = player.getInventory();

        for (Category category : categories) {
            if (category.isInGUI() && item.isSimilar(category.getDisplayItem().build())) {
                if (Methods.isInventoryFull(player)) return;

                if (category.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                    if (this.currencyAPI.canBuy(player, category)) {
                        this.currencyAPI.takeCurrency(player, category);
                    } else {
                        String needed = String.valueOf(category.getCost() - this.currencyAPI.getCurrency(player, category.getCurrency()));

                        Methods.switchCurrency(player, category.getCurrency(), "{money_needed}", "{xp}", needed);

                        return;
                    }
                }

                CEBook book = crazyManager.getRandomEnchantmentBook(category);

                if (book != null) {
                    BuyBookEvent buyBookEvent = new BuyBookEvent(this.crazyManager.getCEPlayer(player), category.getCurrency(), category.getCost(), book);
                    this.plugin.getServer().getPluginManager().callEvent(buyBookEvent);

                    playerInventory.addItem(book.buildBook());
                } else {
                    player.sendMessage(ColorUtils.getPrefix("<red>The category <gold>" + category.getName() + " <red>has no enchantments assigned to it."));
                }

                return;
            }

            LostBook lostBook = category.getLostBook();

            if (lostBook.isInGUI() && item.isSimilar(lostBook.getDisplayItem().build())) {
                if (Methods.isInventoryFull(player)) return;

                if (lostBook.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                    if (this.currencyAPI.canBuy(player, lostBook)) {
                        this.currencyAPI.takeCurrency(player, lostBook);
                    } else {
                        String needed = String.valueOf(lostBook.getCost() - this.currencyAPI.getCurrency(player, lostBook.getCurrency()));

                        Methods.switchCurrency(player, lostBook.getCurrency(), "{money_needed}", "{xp}", needed);

                        return;
                    }
                }

                playerInventory.addItem(lostBook.getLostBook(category).build());

                return;
            }
        }

        for (ShopOption option : ShopOption.values()) {
            if (option.isInGUI() && item.isSimilar(option.getItem())) {
                // If the option is buy-able then it check to see if they player can buy it and take the money.

                if (option.isBuyable()) {
                    if (Methods.isInventoryFull(player)) return;

                    if (option.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                        if (this.currencyAPI.canBuy(player, option)) {
                            this.currencyAPI.takeCurrency(player, option);
                        } else {
                            String needed = String.valueOf(option.getCost() - this.currencyAPI.getCurrency(player, option.getCurrency()));

                            Methods.switchCurrency(player, option.getCurrency(), "{money_needed}", "{xp}", needed);

                            return;
                        }
                    }
                }

                switch (option) {
                    case GKITZ -> {
                        if (!Methods.hasPermission(player, "gkitz", true)) return;

                        if (!this.options.isGkitzToggle()) return;

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
                    case PROTECTION_CRYSTAL -> this.itemManager.getItem("protection_crystal_item").ifPresent(action -> playerInventory.addItem(action.getItemStack()));
                    case SCRAMBLER -> this.itemManager.getItem("scrambler_item").ifPresent(action -> playerInventory.addItem(action.getItemStack()));
                    case SUCCESS_DUST -> playerInventory.addItem(Dust.SUCCESS_DUST.getDust());
                    case DESTROY_DUST -> playerInventory.addItem(Dust.DESTROY_DUST.getDust());
                    case BLACK_SCROLL -> playerInventory.addItem(Scrolls.BLACK_SCROLL.getScroll());
                    case WHITE_SCROLL -> playerInventory.addItem(Scrolls.WHITE_SCROLL.getScroll());
                    case TRANSMOG_SCROLL -> playerInventory.addItem(Scrolls.TRANSMOG_SCROLL.getScroll());
                    case SLOT_CRYSTAL -> this.itemManager.getItem("slot_crystal_item").ifPresent(action -> playerInventory.addItem(action.getItemStack()));
                }

                return;
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnchantmentTableClick(PlayerInteractEvent event) {
        if (this.shopManager.isEnchantmentTableShop()) {
            final Player player = event.getPlayer();
            final Block block = event.getClickedBlock();

            if (block != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == Material.ENCHANTING_TABLE) {
                event.setCancelled(true);

                new ShopMenu(player, this.shopManager.getInventoryName(), this.shopManager.getInventorySize()).open();
            }
        }
    }*/
}