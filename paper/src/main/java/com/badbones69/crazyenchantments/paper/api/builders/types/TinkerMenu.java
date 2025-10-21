package com.badbones69.crazyenchantments.paper.api.builders.types;

import com.badbones69.crazyenchantments.objects.User;
import com.badbones69.crazyenchantments.paper.api.builders.gui.types.StaticInventory;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.managers.configs.types.guis.objects.TinkerEnchantInfo;
import com.badbones69.crazyenchantments.paper.managers.currency.enums.Currency;
import com.badbones69.crazyenchantments.paper.api.enums.DataKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.managers.configs.types.guis.TinkerConfig;
import com.ryderbelserion.fusion.core.api.enums.ItemState;
import com.ryderbelserion.fusion.paper.builders.ItemBuilder;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.Gui;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.GuiItem;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazyenchantments.constants.MessageKeys;

import java.util.HashMap;
import java.util.Map;

public class TinkerMenu extends StaticInventory {

    private final ItemBuilder itemBuilder = new ItemBuilder(ItemType.STONE);
    private final Map<Integer, Integer> slots = getSlots();
    private final TinkerConfig config;

    // 54, Tinker file for title
    public TinkerMenu(@NotNull final Player player, @NotNull final String title, final int size) {
        super(player, title, size);

        this.config = this.configManager.getTinkerConfig();
    }

    @Override
    public void open() {
        final Player player = getPlayer();

        final Currency currency = this.config.getCurrency();

        final GuiItem guiItem = this.itemBuilder.displayLore(this.config.asTradeButtonLoreComponents(player))
                .displayName(this.config.asTradeButtonComponent(player), ItemState.ITEM_NAME)
                .setPersistentString(DataKeys.trade_button.getNamespacedKey(), "").asGuiItem(player, event -> {
                    final Inventory inventory = event.getInventory();

                    final ItemStack current = event.getCurrentItem();

                    event.setCancelled(true);

                    if (current == null || current.isEmpty()) return;

                    final Inventory bottomInventory = player.getOpenInventory().getBottomInventory();

                    boolean toggle = false;
                    double total = 0;

                    for (final Map.Entry<Integer, Integer> slots : this.slots.entrySet()) {
                        final int slot = slots.getValue();
                        final int key = slots.getKey();

                        final ItemStack itemStack = inventory.getItem(slot);

                        if (itemStack != null) {
                            switch (currency) {
                                case VAULT, XP_LEVEL, XP_TOTAL -> total = this.currencyManager.getAmount(currency, player);

                                default -> bottomInventory.addItem(itemStack).values().forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
                            }

                            toggle = true;
                        }

                        inventory.setItem(slot, null);
                        inventory.setItem(key, null);
                    }

                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);

                    if (total > 0) {
                        this.currencyManager.addAmount(currency, player, total);
                    }

                    if (toggle) {
                        this.userRegistry.getUser(player).sendMessage(MessageKeys.tinker_sold_msg);
                    }

                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1f, 1f);
                });

        final Gui gui = getGui();

        gui.setItem(0, guiItem);
        gui.setItem(8, guiItem);

        int[] slots = {4, 13, 23, 31, 40, 49};

        final GuiItem builder = new ItemBuilder(ItemType.WHITE_STAINED_GLASS_PANE).withDisplayName(" ").asGuiItem(player);

        for (final int slot : slots) {
            gui.setItem(slot, builder);
        }

        gui.setDefaultTopClickAction(event -> {
            final ItemStack current = event.getCurrentItem();

            event.setCancelled(true);

            if (current == null || current.isEmpty()) return;

            if (current.getType().toString().endsWith("STAINED_GLASS_PANE")) return;

            final Inventory bottom = event.getView().getBottomInventory();
            final Inventory inventory = event.getInventory();

            final int rawSlot = event.getRawSlot();

            if (this.instance.isEnchantmentBook(current)) {
                final CEBook book = this.instance.getBook(current);

                if (book == null) return;

                event.setCurrentItem(null);

                bottom.addItem(current);

                if (this.slots.containsKey(rawSlot)) {
                    inventory.setItem(this.slots.get(rawSlot), null);
                }

                inventory.setItem(this.slots.get(rawSlot), null);

                return;
            }

            int xp = getTotalXP(current);

            if (xp <= 0) {
                return;
            }

            if (this.slots.containsKey(rawSlot)) {
                event.setCurrentItem(null);

                player.getInventory().addItem(current);

                inventory.setItem(this.slots.get(rawSlot), null);
            } else {
                if (isFirstEmpty(player, current, event)) return;

                final int slot = inventory.firstEmpty();

                if (this.slots.containsKey(slot)) {
                    this.itemManager.getItem("tinker_exp_bottle").ifPresent(action -> {
                        inventory.setItem(this.slots.get(slot), action.getItemStack(player));

                        inventory.setItem(slot, current);
                    });
                }
            }

            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
        });

        gui.setPlayerInventoryAction(event -> {
            final ItemStack current = event.getCurrentItem();

            if (current == null || current.isEmpty()) return;

            if (!this.instance.isEnchantmentBook(current)) return;

            final CEBook book = this.instance.getBook(current);

            if (book == null) return;

            final Inventory inventory = event.getInventory();

            if (isFirstEmpty(player, current, event)) return;

            final int slot = inventory.firstEmpty();

            //inventory.setItem(this.slots.get(firstEmpty), Dust.MYSTERY_DUST.getDust(TinkererManager.getMaxDustLevelFromBook(book, configuration), 1));
            inventory.setItem(slot, current);

            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
        });

        gui.setCloseGuiAction(event -> {
            if (!(event.getPlayer() instanceof Player entity)) return;

            new FoliaScheduler(this.plugin, null, entity) {
                @Override
                public void run() {
                    final PlayerInventory playerInventory = entity.getInventory();
                    final Inventory inventory = event.getInventory();

                    for (final int slot : slots) {
                        final ItemStack itemStack = inventory.getItem(slot);

                        if (itemStack == null || itemStack.isEmpty()) continue;

                        final Location location = player.getLocation();
                        final World world = entity.getWorld();

                        if (entity.isDead()) {
                            world.dropItem(location, itemStack);

                            continue;
                        }

                        playerInventory.addItem(itemStack).values().forEach(item -> world.dropItem(location, item));
                    }

                    inventory.clear();
                }
            }.execute();
        });

        gui.open(player);
    }

    private boolean isFirstEmpty(final Player player, final ItemStack itemStack, final InventoryClickEvent event) {
        final Inventory inventory = event.getInventory();

        final User user = this.userRegistry.getUser(player);

        if (inventory.firstEmpty() == -1) {
            user.sendMessage(MessageKeys.tinker_inventory_full);

            return true;
        }

        if (itemStack.getAmount() > 1) {
            user.sendMessage(MessageKeys.need_to_unstack_item);

            return true;
        }

        event.setCurrentItem(null);

        return false;
    }

    public int getTotalXP(@Nullable final ItemStack item) {
        int total = 0;

        if (item == null || item.isEmpty()) {
            return total;
        }

        final Map<CEnchantment, Integer> customEnchants = this.instance.getEnchantments(item);

        if (!customEnchants.isEmpty()) {
            final Map<String, TinkerEnchantInfo> map = this.config.getEnchantments();

            for (final Map.Entry<CEnchantment, Integer> enchantment : customEnchants.entrySet()) {
                final TinkerEnchantInfo info = map.get(enchantment.getKey().getName());

                total += info.amount() + enchantment.getValue() * info.multiplier();
            }
        }

        if (item.hasData(DataComponentTypes.ENCHANTMENTS)) {
            final Map<String, TinkerEnchantInfo> map = this.config.getEnchantments();
            final Map<Enchantment, Integer> enchantments = item.getEnchantments();

            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                final TinkerEnchantInfo info = map.get(entry.getKey().getKey().asMinimalString());

                total += info.amount() + entry.getValue() * info.multiplier();
            }
        }

        return total;
    }

    public int getMaxDustLevelFromBook(@NotNull final CEBook book, @NotNull final FileConfiguration config) {
        String path = "Tinker.Crazy-Enchantments." + book.getEnchantment().getName() + ".Book";

        if (!config.contains(path)) return 1;

        String[] values = config.getString(path, "0").replaceAll(" ", "").split(",");

        int baseAmount = Integer.parseInt(values[0]);
        int multiplier = values.length < 2 ? 0 : Integer.parseInt(values[1]);

        return baseAmount + book.getLevel() * multiplier;
    }

    public Map<Integer, Integer> getSlots() {
        return new HashMap<>(23) {{
            put(1, 5);
            put(2, 6);
            put(3, 7);
            put(9, 14);
            put(10, 15);
            put(11, 16);
            put(12, 17);
            put(18, 23);
            put(19, 24);
            put(20, 25);
            put(21, 26);
            put(27, 32);
            put(28, 33);
            put(29, 34);
            put(30, 35);
            put(36, 41);
            put(37, 42);
            put(38, 43);
            put(39, 44);
            put(45, 50);
            put(46, 51);
            put(47, 52);
            put(48, 53);
        }};
    }
}