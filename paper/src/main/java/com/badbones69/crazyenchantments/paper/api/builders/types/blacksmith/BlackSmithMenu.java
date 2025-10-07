package com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.builders.InventoryBuilder;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.BlackSmithResult;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;

public class BlackSmithMenu extends InventoryBuilder {

    public BlackSmithMenu(@NotNull final Player player, final int size, @NotNull final String title) {
        super(player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        Inventory inventory = getInventory();

        for (int slot : new int[]{0, 7, 8, 9, 16, 18, 25, 26}) {
            inventory.setItem(slot, BlackSmithManager.getGrayGlass());
        }

        for (int slot : new int[]{1,2,3,4,5,6,10,12,13,15,19,20,21,22,23,24}) {
            inventory.setItem(slot, BlackSmithManager.getRedGlass());
        }

        inventory.setItem(17, BlackSmithManager.getExitButton());

        return this;
    }

    public static class BlackSmithListener implements Listener {

        private final Sound click = Sound.UI_BUTTON_CLICK;
        private final Sound levelUp = Sound.ENTITY_PLAYER_LEVELUP;
        private final Sound villagerNo = Sound.ENTITY_VILLAGER_NO;

        private final List<Integer> resultBorder = Arrays.asList(0, 7, 8, 9, 16, 18, 25, 26);

        private final int mainSlot = 11;
        private final int subSlot = 14;
        private final int outputSlot = 17;

        @NotNull
        private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

        @NotNull
        private final Starter starter = this.plugin.getStarter();

        @NotNull
        private final Methods methods = this.starter.getMethods();

        @NotNull
        private final EnchantmentBookSettings settings = this.starter.getEnchantmentBookSettings();

        @EventHandler(ignoreCancelled = true)
        public void onInventoryClick(InventoryClickEvent event) {
            if (!(event.getInventory().getHolder(false) instanceof BlackSmithMenu holder)) return;

            Inventory inventory = holder.getInventory();

            InventoryView view = holder.getInventoryView();

            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();

            // If item is null, we return.
            if (item == null) return;

            Player player = holder.getPlayer();

            // If the click is in the player inventory.
            if (event.getClickedInventory() != view.getTopInventory()) {
                // If item is not 1, return.
                if (item.getAmount() != 1) return;

                if (!this.settings.getEnchantments(item).isEmpty() || this.settings.isEnchantmentBook(item)) {
                    if (inventory.getItem(this.mainSlot) == null) {
                        event.setCurrentItem(null);
                        inventory.setItem(this.mainSlot, item); // Moves clicked item to main slot.
                        playSound(player, this.click);

                        if (inventory.getItem(this.subSlot) != null) { // Sub item slot is not empty.
                            BlackSmithResult resultItem = new BlackSmithResult(player, inventory.getItem(this.mainSlot), inventory.getItem(this.subSlot));
                            setBorder(resultItem, inventory);
                        }
                    } else {
                        event.setCurrentItem(null);

                        // Sub item slot is not empty.
                        // Moves sub slot item to clicked items slot.
                        if (inventory.getItem(subSlot) != null) event.setCurrentItem(inventory.getItem(this.subSlot));

                        inventory.setItem(this.subSlot, item); // Moves clicked item to sub slot.
                        playSound(player, this.click);
                        BlackSmithResult resultItem = new BlackSmithResult(player, inventory.getItem(this.mainSlot), inventory.getItem(this.subSlot));
                        setBorder(resultItem, inventory);
                    }
                }
            } else {
                if (event.getRawSlot() == this.mainSlot || event.getRawSlot() == this.subSlot) { // Clicked either the main slot or sub slot.
                    event.setCurrentItem(null); // Sets the clicked slot to air.
                    this.methods.addItemToInventory(player, item);
                    inventory.setItem(outputSlot, BlackSmithManager.getExitButton());
                    resultBorder.forEach(slot -> inventory.setItem(slot, BlackSmithManager.getRedGlass()));
                    playSound(player, this.click);
                }

                if (event.getRawSlot() == this.outputSlot) {
                    if (inventory.getItem(this.mainSlot) != null && inventory.getItem(this.subSlot) != null) {
                        BlackSmithResult result = new BlackSmithResult(player, inventory.getItem(this.mainSlot), inventory.getItem(this.subSlot));

                        if (result.getCost() > 0) {
                            Currency currency = BlackSmithManager.getCurrency();

                            if (currency != null && player.getGameMode() != GameMode.CREATIVE) {
                                CurrencyAPI currencyAPI = this.plugin.getStarter().getCurrencyAPI();

                                if (currencyAPI.canBuy(player, currency, result.getCost())) {
                                    currencyAPI.takeCurrency(player, currency, result.getCost());
                                } else {
                                    String needed = String.valueOf(result.getCost() - currencyAPI.getCurrency(player, currency));

                                    this.methods.switchCurrency(player, currency, "%Money_Needed%", "%XP%", needed);
                                    return;
                                }
                            }

                            this.methods.addItemToInventory(player, result.getResultItem());

                            inventory.setItem(this.mainSlot, null);
                            inventory.setItem(this.subSlot, null);

                            playSound(player, this.levelUp);

                            inventory.setItem(this.outputSlot, BlackSmithManager.getExitButton());

                            for (int slot : resultBorder) {
                                inventory.setItem(slot, BlackSmithManager.getRedGlass());
                            }

                            return;
                        }

                        playSound(player, this.villagerNo);

                        return;
                    }

                    playSound(player, this.villagerNo);
                }
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onInventoryClose(InventoryCloseEvent event) {
            if (!(event.getInventory().getHolder(false) instanceof BlackSmithMenu holder)) return;

            Player player = holder.getPlayer();

            for (int slot : new int[]{this.mainSlot, this.subSlot}) {
                ItemStack itemStack = holder.getInventory().getItem(slot);

                if (itemStack != null) {
                    if (itemStack.getType() != Material.AIR) {
                        this.methods.addItemToInventory(player, itemStack);
                    }
                }
            }

            holder.getInventory().clear();
        }

        private void setBorder(@NotNull final BlackSmithResult item, @NotNull final Inventory inventory) {
            if (item.getCost() > 0) {
                ItemStack result = item.getResultItem();

                String value = String.valueOf(item.getCost());
                String message = Messages.replacePlaceholders("%Cost%", value, BlackSmithManager.getItemCost());

                for (int slot : resultBorder) {
                    inventory.setItem(slot, BlackSmithManager.getBlueGlass());
                }

                inventory.setItem(this.outputSlot, this.methods.addLore(result, message));
            } else {
                inventory.setItem(this.outputSlot, BlackSmithManager.getExitButton());

                for (int slot : resultBorder) {
                    inventory.setItem(slot, BlackSmithManager.getRedGlass());
                }
            }
        }

        private void playSound(@NotNull final Player player, @NotNull final Sound sound) {
            player.playSound(player.getLocation(), sound, 1, 1);
        }
    }
}