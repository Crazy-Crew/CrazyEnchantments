package com.badbones69.crazyenchantments.paper.api.builders.types;

import com.badbones69.crazyenchantments.paper.api.builders.gui.types.StaticInventory;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.v2.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.managers.configs.types.TinkerConfig;
import com.ryderbelserion.fusion.core.api.enums.ItemState;
import com.ryderbelserion.fusion.paper.builders.ItemBuilder;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.Gui;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.GuiItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

                    boolean toggle = false;
                    int total = 0;

                    for (final Map.Entry<Integer, Integer> slots : this.slots.entrySet()) {
                        final int slot = slots.getValue();
                        final int key = slots.getKey();

                        final ItemStack itemStack = inventory.getItem(slot);

//                        if (itemStack != null) {
//                            if (currency == Currency.VAULT) {
//                                total = TinkererManager.getTotalXP(inventory.getItem(slot.getKey()), configuration);
//                            } else {
//                                bottomInventory.addItem(reward).values().forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
//                            }
//
//                            toggle = true;
//                        }

                        inventory.setItem(slot, null);
                        inventory.setItem(key, null);
                    }

                    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);

//                    if (total != 0) {
//                        this.api.giveCurrency(player, currency, total);
//                    }
//
//                    if (toggle) {
//                        Messages.TINKER_SOLD_MESSAGE.sendMessage(player);
//                    }
//
//                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
                });

        final Gui gui = getGui();

        gui.setItem(0, guiItem);
        gui.setItem(8, guiItem);

        int[] slots = {4, 13, 23, 31, 40, 49};

        final GuiItem builder = new ItemBuilder(ItemType.WHITE_STAINED_GLASS_PANE).withDisplayName(" ").asGuiItem(player);

        for (final int slot : slots) {
            gui.setItem(slot, builder);
        }

        gui.setDefaultClickAction(event -> {
//            event.setCancelled(true);
//
//            ItemStack current = event.getCurrentItem();
//
//            if (current == null || current.isEmpty()) return;
//
//            Inventory inventory = holder.getInventory();
//            Inventory topInventory = player.getOpenInventory().getTopInventory();
//            Inventory bottomInventory = player.getOpenInventory().getBottomInventory();

//            if (current.getType().toString().endsWith("STAINED_GLASS_PANE")) return;
//
//            final YamlConfiguration configuration = FileKeys.tinker.getYamlConfiguration();
//
//            // Adding/taking items.
//            if (this.instance.isEnchantmentBook(current)) { // Adding a book.
//                final CEBook book = this.instance.getBook(current);
//
//                if (book == null) return;
//
//                if (event.getClickedInventory() == topInventory) { // Clicking in the tinkers.
//                    event.setCurrentItem(null);
//                    bottomInventory.addItem(current);
//                    inventory.setItem(this.slots.get(event.getRawSlot()), null);
//                } else { // Clicking in their inventory.
//                    if (isFirstEmpty(event, player, current, topInventory)) return;
//
//                    inventory.setItem(this.slots.get(inventory.firstEmpty()), Dust.MYSTERY_DUST.getDust(TinkererManager.getMaxDustLevelFromBook(book, configuration), 1));
//                    inventory.setItem(inventory.firstEmpty(), current);
//                }
//
//                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
//
//                return;
//            }
//
//            int totalXP = TinkererManager.getTotalXP(current, configuration);
//
//            if (totalXP > 0) {
//                // Adding an item.
//                if (event.getClickedInventory() == topInventory) { // Clicking in the tinkers.
//                    if (this.slots.containsKey(event.getRawSlot())) {
//                        event.setCurrentItem(null);
//                        player.getInventory().addItem(current);
//                        inventory.setItem(this.slots.get(event.getRawSlot()), null);
//                    }
//                } else {
//                    // Clicking in their inventory.
//                    if (isFirstEmpty(event, player, current, topInventory)) return;
//
//                    inventory.setItem(this.slots.get(inventory.firstEmpty()), TinkererManager.getXPBottle(totalXP, configuration));
//                    inventory.setItem(inventory.firstEmpty(), current);
//                }
//
//                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
//            }
        });

        gui.setCloseGuiAction(event -> {
//            if (!(event.getInventory().getHolder() instanceof TinkererMenu holder)) return;
//
//            new FoliaScheduler(this.plugin, null, player) {
//                @Override
//                public void run() {
//                    final Inventory inventory = holder.getInventory();
//
//                    for (final int slot : slots.keySet()) {
//                        final ItemStack item = inventory.getItem(slot);
//
//                        if (item == null || item.isEmpty()) continue;
//
//                        if (player.isDead()) {
//                            player.getWorld().dropItem(player.getLocation(), item);
//                        } else {
//                            player.getInventory().addItem(item).values().forEach(item2 -> player.getWorld().dropItem(player.getLocation(), item2));
//                        }
//                    }
//
//                    holder.getInventory().clear();
//                }
//            }.execute();
        });

        gui.open(player);
    }

    private boolean isFirstEmpty(InventoryClickEvent event, Player player, ItemStack current, Inventory topInventory) {
        if (topInventory.firstEmpty() == -1) {
            Messages.TINKER_INVENTORY_FULL.sendMessage(player);

            return true;
        }

        if (current.getAmount() > 1) {
            Messages.NEED_TO_UNSTACK_ITEM.sendMessage(player);

            return true;
        }

        event.setCurrentItem(null);

        return false;
    }

    /**
     * @param xp Amount of XP to store.
     * @return XP Bottle with custom amount of xp stored in it.
     */
    public static ItemStack getXPBottle(final int xp, @NotNull final FileConfiguration config) {
        String id = config.getString("Settings.BottleOptions.Item");
        String name = config.getString("Settings.BottleOptions.Name");
        List<String> lore = new ArrayList<>();

        String amount = String.valueOf(xp);

        for (String l : config.getStringList("Settings.BottleOptions.Lore")) {
            lore.add(l.replace("%Total%", amount).replace("%total%", amount));
        }

        //assert id != null;
        //return new com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder().setMaterial(id).setName(name).setLore(lore).addKey(DataKeys.experience.getNamespacedKey(), xp).build();
        return ItemStack.of(Material.STONE);
    }

    public int getTotalXP(@Nullable final ItemStack item, @NotNull final FileConfiguration config) {
        int total = 0;

//        if (item == null || item.isEmpty()) {
//            return total;
//        }
//
//        final Map<CEnchantment, Integer> ceEnchants = instance.getEnchantments(item);
//
//        if (!ceEnchants.isEmpty()) { // CrazyEnchantments
//            for (final Map.Entry<CEnchantment, Integer> enchantment : ceEnchants.entrySet()) {
//                String[] values = config.getString("Tinker.Crazy-Enchantments." + enchantment.getKey().getName() + ".Items", "0").replaceAll(" ", "").split(",");
//                int baseAmount = Integer.parseInt(values[0]);
//                int multiplier = values.length < 2 ? 0 : Integer.parseInt(values[1]);
//                int enchantmentLevel = enchantment.getValue();
//
//                total += baseAmount + enchantmentLevel * multiplier;
//            }
//        }
//
//        //todo() test data component usage here
//        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) { // Vanilla Enchantments
//            for (Map.Entry<Enchantment, Integer> enchantment : item.getEnchantments().entrySet()) {
//                String[] values = config.getString("Tinker.Vanilla-Enchantments." + convertToLegacy(enchantment.getKey().getKey().value()).toUpperCase(), "0").replaceAll(" ", "").split(",");
//                int baseAmount = Integer.parseInt(values[0]); // TODO add converter to convert legacy to new enchant names.
//                int multiplier = values.length < 2 ? 0 : Integer.parseInt(values[1]);
//                int enchantmentLevel = enchantment.getValue();
//                total += baseAmount + enchantmentLevel * multiplier;
//            }
//        }

        return total;
    }

    private String convertToLegacy(@NotNull final String from) { // Stolen inverse of the above method. -TDL
        if (from.isEmpty()) {
            return null;
        }

        return switch (from.toLowerCase()) {
            case "protection" -> "protection_environmental";
            case "fire_protection" -> "protection_fire";
            case  "feather_falling" -> "protection_fall";
            case  "blast_protection" -> "protection_explosions";
            case  "projectile_protection" -> "protection_projectile";
            case  "respiration" -> "oxygen";
            case  "aqua_affinity" -> "water_worker";
            case  "sharpness" -> "damage_all";
            case  "smite" -> "damage_undead";
            case  "bane_of_arthropods" -> "damage_arthropods";
            case  "looting" -> "loot_bonus_mobs";
            case  "sweeping" -> "sweeping_edge";
            case  "efficiency" -> "dig_speed";
            case  "unbreaking" -> "durability";
            case  "fortune" -> "loot_bonus_blocks";
            case  "power" -> "arrow_damage";
            case  "punch" -> "arrow_knockback";
            case  "flame" -> "arrow_fire";
            case  "infinity" -> "arrow_infinite";
            case  "luck_of_the_sea" -> "luck";
            default -> from;
        };
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