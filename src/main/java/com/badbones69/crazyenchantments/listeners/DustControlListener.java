package com.badbones69.crazyenchantments.listeners;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.enums.Dust;
import com.badbones69.crazyenchantments.api.enums.pdc.DustData;
import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.utilities.misc.ColorUtils;
import com.google.gson.Gson;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DustControlListener implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    private final Random random = new Random();

    private void setLore(ItemStack item, int percent, String rate) {
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        CEnchantment enchantment = null;

        for (CEnchantment registeredEnchantments : crazyManager.getRegisteredEnchantments()) {
            String ench = registeredEnchantments.getCustomName();

            if (item.getItemMeta().getDisplayName().contains(ench)) enchantment = registeredEnchantments;
        }

        for (String lores : Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore")) {
            boolean hasLine = true;

            if (lores.contains("%Description%") || lores.contains("%description%")) {
                if (enchantment != null) {
                    enchantment.getInfoDescription().forEach(lines -> lore.add(ColorUtils.color(lines)));
                }

                hasLine = false;
            }

            if (rate.equalsIgnoreCase("Success")) {
                lores = lores.replace("%Success_Rate%", String.valueOf(percent)).replace("%success_rate%", String.valueOf(percent))
                .replace("%Destroy_Rate%", String.valueOf(enchantmentBookSettings.getPercent("%Destroy_Rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 0)))
                .replace("%destroy_rate%", String.valueOf(enchantmentBookSettings.getPercent("%destroy_rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 0)));
            } else {
                lores = lores.replace("%Destroy_Rate%", String.valueOf(percent)).replace("%destroy_rate%", String.valueOf(percent))
                .replace("%Success_Rate%", String.valueOf(enchantmentBookSettings.getPercent("%Success_Rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 100)))
                .replace("%success_rate%", String.valueOf(enchantmentBookSettings.getPercent("%success_rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 100)));
            }

            if (hasLine) lore.add(ColorUtils.color(lores));
        }

        assert meta != null;
        meta.setLore(lore);

        item.setItemMeta(meta);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) { //Dust Use
        Player player = (Player) e.getWhoClicked();

        if (e.getCurrentItem() != null && e.getCursor() != null) {
            ItemStack book = e.getCurrentItem();
            ItemStack dust = e.getCursor();

        // PDC Start
            Gson g = new Gson();

            NamespacedKey dustKey = new NamespacedKey(plugin, "Crazy_Dust");
            //NamespacedKey bookKey = new NamespacedKey(plugin, "Crazy_Books");

            DustData dustData = g.fromJson(dust.getItemMeta().getPersistentDataContainer().get(dustKey, PersistentDataType.STRING), DustData.class);
            //DustData bookData = g.fromJson(book.getItemMeta().getPersistentDataContainer().get(bookKey, PersistentDataType.STRING), DustData.class); //Once Books have PDC
        // PDC End

            if (book.getAmount() == 1 && book.hasItemMeta() && dust.hasItemMeta() && book.getItemMeta().hasLore() && dust.getItemMeta().hasLore() && book.getItemMeta().hasDisplayName() &&
            dust.getItemMeta().hasDisplayName() && book.getType() == enchantmentBookSettings.getEnchantmentBookItem().getType()) {
                boolean toggle = false;
                String name = book.getItemMeta().getDisplayName();

                for (CEnchantment en : crazyManager.getRegisteredEnchantments()) {
                    if (name.contains(ColorUtils.color(en.getBookColor() + en.getCustomName()))) toggle = true;
                }

                if (!toggle) return;


                if (dustData.getConfigName().equalsIgnoreCase(Dust.SUCCESS_DUST.getConfigName())) {
                    int per = dustData.getChance();

                    if (methods.hasArgument("%success_rate%", Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"))) {
                        int total = enchantmentBookSettings.getPercent("%success_rate%", book, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 100);

                        if (total >= 100) return;

                        if (player.getGameMode() == GameMode.CREATIVE && dust.getAmount() > 1) {
                            player.sendMessage(ColorUtils.getPrefix() + ColorUtils.color("&cPlease unstack the dust for them to work."));
                            return;
                        }

                        per += total;

                        if (per < 0) per = 0;
                        if (per > 100) per = 100;

                        e.setCancelled(true);

                        setLore(book, per, "Success");

                        player.setItemOnCursor(methods.removeItem(dust));
                    }

                    return;
                }

                if (dustData.getConfigName().equalsIgnoreCase(Dust.DESTROY_DUST.getConfigName())) {
                    int per = dustData.getChance();

                    if (methods.hasArgument("%destroy_rate%", Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"))) {
                        int total = enchantmentBookSettings.getPercent("%destroy_rate%", book, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 0);
                        if (total <= 0) return;

                        if (player.getGameMode() == GameMode.CREATIVE && dust.getAmount() > 1) {
                            player.sendMessage(ColorUtils.getPrefix() + ColorUtils.color("&cPlease unstack the dust for them to work."));
                            return;
                        }

                        per = total - per;

                        if (per < 0) per = 0;
                        if (per > 100) per = 100;

                        e.setCancelled(true);

                        setLore(book, per, "Destroy");

                        player.setItemOnCursor(methods.removeItem(dust));
                    }
                }
            }
        }
    }

    @EventHandler
    public void openDust(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        FileConfiguration config = Files.CONFIG.getFile();

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = methods.getItemInHand(player);

        // PDC Start
            Gson g = new Gson();

            NamespacedKey key = new NamespacedKey(plugin, "Crazy_Dust");

            DustData data = g.fromJson(item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING), DustData.class);

        // PDC End

            if (data.getConfigName().equals(Dust.SUCCESS_DUST.getConfigName())) {
                e.setCancelled(true);
            } else if (data.getConfigName().equals(Dust.DESTROY_DUST.getConfigName())) {
                e.setCancelled(true);
            } else if(data.getConfigName().equals(Dust.MYSTERY_DUST.getConfigName())) {
                e.setCancelled(true);
                methods.setItemInHand(player, methods.removeItem(item));

                ItemStack item2 = pickDust().getDust(methods.percentPick(data.getChance() + 1, 1), 1);

                player.getInventory().addItem(item2);

                player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1, 1);

                if (config.getBoolean("Settings.Dust.MysteryDust.Firework.Toggle")) {
                    List<Color> colors = new ArrayList<>();
                    String colorString = config.getString("Settings.Dust.MysteryDust.Firework.Colors", "Black, Gray, Lime");

                    ColorUtils.color(colors, colorString);

                    methods.fireWork(player.getLocation().add(0, 1, 0), colors);
                }
            }
        }
    }

    private Dust pickDust() {
        List<Dust> dusts = new ArrayList<>();

        if (Files.CONFIG.getFile().getBoolean("Settings.Dust.MysteryDust.Dust-Toggle.Success")) dusts.add(Dust.SUCCESS_DUST);

        if (Files.CONFIG.getFile().getBoolean("Settings.Dust.MysteryDust.Dust-Toggle.Destroy")) dusts.add(Dust.DESTROY_DUST);

        if (Files.CONFIG.getFile().getBoolean("Settings.Dust.MysteryDust.Dust-Toggle.Failed")) dusts.add(Dust.FAILED_DUST);

        return dusts.get(random.nextInt(dusts.size()));
    }
}