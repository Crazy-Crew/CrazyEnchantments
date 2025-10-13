package com.badbones69.crazyenchantments.paper.api.builders.types;

import com.badbones69.crazyenchantments.paper.api.builders.gui.types.StaticInventory;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.paper.api.objects.BlackSmithResult;
import com.badbones69.crazyenchantments.paper.managers.configs.types.BlackSmithConfig;
import com.ryderbelserion.fusion.paper.builders.ItemBuilder;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.Gui;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.GuiItem;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class BlackSmithMenu extends StaticInventory {

    private final BlackSmithConfig config;

    private final ItemStack redGlass,blueGlass,grayGlass;

    public BlackSmithMenu(@NotNull final Player player, @NotNull final String title, final int size) {
        super(player, title, size);

        this.redGlass = new ItemBuilder(ItemType.RED_STAINED_GLASS).withDisplayName(" ").asItemStack();
        this.blueGlass = new ItemBuilder(ItemType.LIGHT_BLUE_STAINED_GLASS).withDisplayName(" ").asItemStack();
        this.grayGlass = new ItemBuilder(ItemType.GRAY_STAINED_GLASS).withDisplayName(" ").asItemStack();

        this.config = this.configManager.getBlackSmithConfig();
    }

    private final Sound villager_no = Sound.ENTITY_VILLAGER_NO;
    private final Sound level_up = Sound.ENTITY_PLAYER_LEVELUP;
    private final Sound click = Sound.UI_BUTTON_CLICK;

    private final int[] resultBorder = new int[]{0, 7, 8, 9, 16, 18, 25, 26};

    private final int inputSlot = 11;
    private final int secondaryInputSlot = 14;
    private final int outputSlot = 17;

    @Override
    public void open() {
        final Gui gui = this.getGui();

        for (int slot : new int[]{0, 7, 8, 9, 16, 18, 25, 26}) {
            gui.setItem(slot, new GuiItem(this.grayGlass));
        }

        for (int slot : new int[]{1, 2, 3, 4, 5, 6, 10, 12, 13, 15, 19, 20, 21, 22, 23, 24}) {
            gui.setItem(slot, new GuiItem(this.redGlass));
        }

        this.itemManager.getItem("close_item").ifPresent(action -> gui.setItem(17, action.asGuiItem()));

        gui.setDefaultTopClickAction(event -> {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            final Inventory inventory = event.getInventory();
            final int rawSlot = event.getRawSlot();

            if (rawSlot == this.inputSlot || rawSlot == this.secondaryInputSlot) {
                event.setCurrentItem(null);

                inventory.setItem(this.outputSlot, null); // exit button

                for (int slot : this.resultBorder) {
                    inventory.setItem(slot, this.redGlass);
                }

                playSound(player, this.click);
            }

            if (rawSlot == this.outputSlot) {
                final ItemStack inputItem = inventory.getItem(this.inputSlot);
                final ItemStack subItem = inventory.getItem(this.secondaryInputSlot);

                if (inputItem != null && subItem != null) {
                    final BlackSmithResult result = new BlackSmithResult(player, inputItem, subItem);

                    final int cost = result.getCost();

                    if (cost > 0) {
                        final Currency currency = this.config.getTransactionCurrency(); // supply currency

                        final CurrencyAPI currencyAPI = this.plugin.getStarter().getCurrencyAPI();

                        if (currencyAPI.canBuy(player, currency, cost)) {
                            currencyAPI.takeCurrency(player, currency, cost);
                        } else {
                            final String needed = String.valueOf(cost - currencyAPI.getCurrency(player, currency));

                            //this.methods.switchCurrency(player, currency, "%Money_Needed%", "%XP%", needed);

                            return;
                        }

                        /*this.methods.addItemToInventory(player, result.getResultItem());

                        inventory.setItem(this.mainSlot, null);
                        inventory.setItem(this.subSlot, null);

                        playSound(player, this.levelUp);

                        inventory.setItem(this.outputSlot, BlackSmithManager.getExitButton());

                        for (int slot : resultBorder) {
                            inventory.setItem(slot, BlackSmithManager.getRedGlass());
                        }*/

                        return;
                    }

                    playSound(player, this.villager_no);

                    return;
                }

                playSound(player, this.villager_no);
            }
        });

        gui.setPlayerInventoryAction(event -> { // move item from bottom inventory to top inventory
            if (!(event.getWhoClicked() instanceof Player player)) return;

            final ItemStack itemStack = event.getCurrentItem();

            if (itemStack == null || itemStack.isEmpty()) return;

            if (itemStack.getAmount() < 1) return;

            if (this.instance.getEnchantments(itemStack).isEmpty() || !this.instance.isEnchantmentBook(itemStack)) return;

            final Inventory inventory = event.getInventory();

            final ItemStack inputItem = inventory.getItem(this.inputSlot);

            if (inputItem == null) {
                event.setCurrentItem(null);

                inventory.setItem(this.inputSlot, itemStack);

                playSound(player, this.click);

                final ItemStack secondaryInput = inventory.getItem(this.secondaryInputSlot);

                if (secondaryInput != null) {
                    final BlackSmithResult result = new BlackSmithResult(player, inventory.getItem(this.inputSlot), secondaryInput);

                    updateBorder(inventory, result);

                    return;
                }

                return;
            }

            event.setCurrentItem(null);

            final ItemStack secondaryInput = inventory.getItem(this.secondaryInputSlot);

            if (secondaryInput != null) {
                event.setCurrentItem(secondaryInput);
            }

            inventory.setItem(this.secondaryInputSlot, itemStack);

            playSound(player, this.click);

            final ItemStack otherInput = inventory.getItem(this.secondaryInputSlot);

            if (otherInput != null) {
                final BlackSmithResult result = new BlackSmithResult(player, inventory.getItem(this.inputSlot), otherInput);

                updateBorder(inventory, result);
            }
        });

        gui.setCloseGuiAction(event -> {
            if (!(event.getPlayer() instanceof Player player)) return;

            final Inventory inventory = event.getInventory();

            for (final int slot : new int[]{this.inputSlot, this.secondaryInputSlot}) {
                final ItemStack itemStack = inventory.getItem(slot);

                if (itemStack == null || itemStack.isEmpty()) continue;

                //this.methods.addItemToInventory(player, itemStack);
            }

            inventory.clear();
        });

        gui.open(getPlayer());
    }

    private void updateBorder(@NotNull final Inventory inventory, @NotNull final BlackSmithResult result) {
        final int cost = result.getCost();

        if (cost > 0) {
            final ItemStack item = result.getResultItem();

            if (item.isEmpty()) return;

            final String value = String.valueOf(cost);
            final String message = "{cost}".replaceAll("\\{cost}",value);

            for (final int slot : this.resultBorder) {
                inventory.setItem(slot, this.blueGlass);
            }

            editLore(item, List.of(Component.text(message)));

            return;
        }

        inventory.setItem(this.outputSlot, null); //todo() exit button

        for (final int slot : this.resultBorder) {
            inventory.setItem(slot, this.redGlass);
        }
    }

    private void playSound(@NotNull final Player player, @NotNull final Sound sound) {
        player.playSound(player.getLocation(), sound, 1, 1);
    }

    public void editLore(@NotNull final ItemStack itemStack, @NotNull final List<Component> lore) {
        ItemLore itemLore = itemStack.getData(DataComponentTypes.LORE);

        if (itemLore != null) {
            final List<Component> lines = new ArrayList<>(itemLore.lines());

            lines.addAll(lore);

            itemLore = ItemLore.lore().addLines(lines).build();
        } else {
            itemLore = ItemLore.lore().addLines(lore).build();
        }

        itemStack.setData(DataComponentTypes.LORE, itemLore);
    }
}