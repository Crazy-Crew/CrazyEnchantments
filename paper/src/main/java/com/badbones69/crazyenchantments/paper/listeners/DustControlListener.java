package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DustData;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.EnchantedBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.google.gson.Gson;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

    private final Gson gson = new Gson();

    private void setBookLore(ItemStack item, int percent, String rate, CEnchantment enchantment, EnchantedBook data) {
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();

        if (rate.equalsIgnoreCase("Success")) {
            data.setSuccessChance(percent);
        } else {
            data.setDestroyChance(percent);
        }

        for (String line : Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore")) {
            if (line.toLowerCase().contains("%description%")) {
                enchantment.getInfoDescription().forEach(lines -> lore.add(ColorUtils.legacyTranslateColourCodes(lines)));
                continue;
            }

            TextComponent lineToAdd = ColorUtils.legacyTranslateColourCodes(line
                    .replaceAll("(%Success_Rate%|%success_rate%)", String.valueOf(data.getSuccessChance()))
                    .replaceAll("(%Destroy_Rate%|%destroy_rate%)", String.valueOf(data.getDestroyChance())));
            lore.add(lineToAdd);
        }

        meta.getPersistentDataContainer().set(DataKeys.stored_enchantments.getNamespacedKey(), PersistentDataType.STRING, this.gson.toJson(data));
        meta.lore(lore);

        item.setItemMeta(meta);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) { // dust use
        if (event.getCurrentItem() == null) return;

        Player player = (Player) event.getWhoClicked();

        FileConfiguration config = Files.CONFIG.getFile();

        ItemStack book = event.getCurrentItem();
        ItemStack dust = event.getCursor();

        if (!(book.hasItemMeta() && dust.hasItemMeta())) return;

        if (book.getAmount() > 1) return;

        // PDC Start
        DustData dustData = this.gson.fromJson(dust.getItemMeta().getPersistentDataContainer().get(DataKeys.dust.getNamespacedKey(), PersistentDataType.STRING), DustData.class);
        EnchantedBook bookData = this.gson.fromJson(book.getItemMeta().getPersistentDataContainer().get(DataKeys.stored_enchantments.getNamespacedKey(), PersistentDataType.STRING), EnchantedBook.class); //Once Books have PDC
        // PDC End

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
        FileConfiguration config = Files.CONFIG.getFile();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (openAnyHandDust(player, event, true, config)) return;

        openAnyHandDust(player, event, false, config);
    }
    
    private boolean openAnyHandDust(Player player, PlayerInteractEvent event, boolean mainHand, FileConfiguration config) {
        ItemStack item = mainHand ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();

        if (item.isEmpty() || !item.hasItemMeta()) return false;

        // PDC Start
        DustData data = gson.fromJson(item.getItemMeta().getPersistentDataContainer().get(DataKeys.dust.getNamespacedKey(), PersistentDataType.STRING), DustData.class);
        // PDC End
        if (data == null) return false;

        if (data.getConfigName().equals(Dust.SUCCESS_DUST.getConfigName())) {
            event.setCancelled(true);
        } else if (data.getConfigName().equals(Dust.DESTROY_DUST.getConfigName())) {
            event.setCancelled(true);
        } else if(data.getConfigName().equals(Dust.MYSTERY_DUST.getConfigName())) {
            event.setCancelled(true);

            if (methods.isInventoryFull(player)) {
                player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                return true;
            }

            if (mainHand) {
                player.getInventory().setItemInMainHand(this.methods.removeItem(item));
            } else {
                player.getInventory().setItemInOffHand(this.methods.removeItem(item));
            }

            ItemStack item2 = pickDust().getDust(this.methods.percentPick(data.getChance() + 1, 1), 1);

            player.getInventory().addItem(item2);

            player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1, 1);

            if (config.getBoolean("Settings.Dust.MysteryDust.Firework.Toggle")) {
                List<Color> colors = new ArrayList<>();
                String colorString = config.getString("Settings.Dust.MysteryDust.Firework.Colors", "Black, Gray, Lime");

                ColorUtils.color(colors, colorString);

                this.methods.fireWork(player.getLocation().add(0, 1, 0), colors);
            }
        }

        return true;
    }

    private Dust pickDust() {
        List<Dust> dusts = new ArrayList<>();

        FileConfiguration config = Files.CONFIG.getFile();

        if (config.getBoolean("Settings.Dust.MysteryDust.Dust-Toggle.Success")) dusts.add(Dust.SUCCESS_DUST);

        if (config.getBoolean("Settings.Dust.MysteryDust.Dust-Toggle.Destroy")) dusts.add(Dust.DESTROY_DUST);

        if (config.getBoolean("Settings.Dust.MysteryDust.Dust-Toggle.Failed")) dusts.add(Dust.FAILED_DUST);

        return dusts.get(new Random().nextInt(dusts.size()));
    }
}