package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DustData;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.EnchantedBook;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DustControlListener implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final Methods methods = this.starter.getMethods();

    @NotNull
    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    private void setBookLore(@NotNull final ItemStack item, final int percent, @NotNull final String rate, @NotNull final CEnchantment enchantment, @NotNull final EnchantedBook data) {
        if (item.isEmpty()) return;

        final List<Component> lore = new ArrayList<>();

        if (rate.equalsIgnoreCase("Success")) {
            data.setSuccessChance(percent);
        } else {
            data.setDestroyChance(percent);
        }

        final YamlConfiguration config = FileKeys.config.getConfiguration();

        for (final String line : config.getStringList("Settings.EnchantmentBookLore")) {
            if (line.toLowerCase().contains("%description%")) {
                enchantment.getInfoDescription().forEach(lines -> lore.add(ColorUtils.legacyTranslateColourCodes(lines)));

                continue;
            }

            TextComponent lineToAdd = ColorUtils.legacyTranslateColourCodes(line.replaceAll("(%Success_Rate%|%success_rate%)", String.valueOf(data.getSuccessChance())).replaceAll("(%Destroy_Rate%|%destroy_rate%)", String.valueOf(data.getDestroyChance())));

            lore.add(lineToAdd);
        }

        item.editPersistentDataContainer(container -> container.set(DataKeys.stored_enchantments.getNamespacedKey(), PersistentDataType.STRING, Methods.getGson().toJson(data)));

        item.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(lore).build());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) { // dust use
        ItemStack book = event.getCurrentItem();

        if (book == null || book.isEmpty()) return;

        ItemStack dust = event.getCursor();

        if (book.isEmpty()) return;

        if (book.getAmount() > 1) return;

        Player player = (Player) event.getWhoClicked();

        final YamlConfiguration config = FileKeys.config.getConfiguration();

        final PersistentDataContainerView container = dust.getPersistentDataContainer();

        final DustData dustData = Methods.getGson().fromJson(container.get(DataKeys.dust.getNamespacedKey(), PersistentDataType.STRING), DustData.class);
        final EnchantedBook bookData = Methods.getGson().fromJson(container.get(DataKeys.stored_enchantments.getNamespacedKey(), PersistentDataType.STRING), EnchantedBook.class); //Once Books have PDC

        if (bookData == null || dustData == null) return;

        boolean toggle = false;

        CEnchantment enchantment = null;

        for (CEnchantment en : this.crazyManager.getRegisteredEnchantments()) {
            if (en.getName().equalsIgnoreCase(bookData.getName())) {
                enchantment = en;

                toggle = true;

                break;
            }
        }

        if (!toggle) return;

        if (dustData.getConfigName().equalsIgnoreCase(Dust.SUCCESS_DUST.getConfigName())) {
            int per = dustData.getChance();

            if (this.methods.hasArgument("%success_rate%", config.getStringList("Settings.EnchantmentBookLore"))) {
                int total = bookData.getSuccessChance();

                if (total >= 100) return;

                if (player.getGameMode() == GameMode.CREATIVE && dust.getAmount() > 1) {
                    player.sendMessage(ColorUtils.getPrefix() + ColorUtils.color("&cPlease unstack the dust for them to work."));
                    return;
                }

                per += total;

                if (per < 0) per = 0;
                if (per > 100) per = 100;

                event.setCancelled(true);

                setBookLore(book, per, "Success", enchantment, bookData);

                player.setItemOnCursor(this.methods.removeItem(dust));
            }

            return;
        }

        if (dustData.getConfigName().equalsIgnoreCase(Dust.DESTROY_DUST.getConfigName())) {
            int per = dustData.getChance();

            if (this.methods.hasArgument("%destroy_rate%", config.getStringList("Settings.EnchantmentBookLore"))) {
                int total = bookData.getDestroyChance();
                if (total <= 0) return;

                if (player.getGameMode() == GameMode.CREATIVE && dust.getAmount() > 1) {
                    player.sendMessage(ColorUtils.getPrefix() + ColorUtils.color("&cPlease unstack the dust for them to work."));
                    return;
                }

                per = total - per;

                if (per < 0) per = 0;
                if (per > 100) per = 100;

                event.setCancelled(true);

                setBookLore(book, per, "Destroy", enchantment, bookData);

                player.setItemOnCursor(this.methods.removeItem(dust));
            }
        }
    }

    @EventHandler
    public void openDust(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (openAnyHandDust(player, event, true)) return;

        openAnyHandDust(player, event, false);
    }

    private boolean openAnyHandDust(@NotNull final Player player, @NotNull final PlayerInteractEvent event, final boolean mainHand) {
        final PlayerInventory inventory = player.getInventory();

        final ItemStack item = mainHand ? inventory.getItemInMainHand() : inventory.getItemInOffHand();

        if (item.isEmpty()) return false;

        final DustData data = Methods.getGson().fromJson(item.getPersistentDataContainer().get(DataKeys.dust.getNamespacedKey(), PersistentDataType.STRING), DustData.class);

        if (data == null) return false;

        if (data.getConfigName().equals(Dust.SUCCESS_DUST.getConfigName())) {
            event.setCancelled(true);
        } else if (data.getConfigName().equals(Dust.DESTROY_DUST.getConfigName())) {
            event.setCancelled(true);
        } else if (data.getConfigName().equals(Dust.MYSTERY_DUST.getConfigName())) {
            event.setCancelled(true);

            if (this.methods.isInventoryFull(player)) {
                player.sendMessage(Messages.INVENTORY_FULL.getMessage());

                return true;
            }

            if (mainHand) {
                inventory.setItemInMainHand(this.methods.removeItem(item));
            } else {
                inventory.setItemInOffHand(this.methods.removeItem(item));
            }

            ItemStack item2 = pickDust().getDust(this.methods.percentPick(data.getChance() + 1, 1), 1);

            inventory.addItem(item2);

            player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1, 1);

            final YamlConfiguration config = FileKeys.config.getConfiguration();

            if (config.getBoolean("Settings.Dust.MysteryDust.Firework.Toggle", true)) {
                final List<Color> colors = new ArrayList<>();

                ColorUtils.color(colors, config.getString("Settings.Dust.MysteryDust.Firework.Colors", "Black, Gray, Lime"));

                this.methods.fireWork(player.getLocation().add(0, 1, 0), colors);
            }
        }

        return true;
    }

    private Dust pickDust() {
        List<Dust> dusts = new ArrayList<>();

        final YamlConfiguration config = FileKeys.config.getConfiguration();

        if (config.getBoolean("Settings.Dust.MysteryDust.Dust-Toggle.Success", true)) dusts.add(Dust.SUCCESS_DUST);

        if (config.getBoolean("Settings.Dust.MysteryDust.Dust-Toggle.Destroy", true)) dusts.add(Dust.DESTROY_DUST);

        if (config.getBoolean("Settings.Dust.MysteryDust.Dust-Toggle.Failed", true)) dusts.add(Dust.FAILED_DUST);

        return dusts.get(new Random().nextInt(dusts.size()));
    }
}