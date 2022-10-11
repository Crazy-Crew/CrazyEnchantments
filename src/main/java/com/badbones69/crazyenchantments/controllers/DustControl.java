package com.badbones69.crazyenchantments.controllers;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.enums.Dust;
import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DustControl implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    private final Methods methods = plugin.getStarter().getMethods();

    private final Random random = new Random();
    
    private void setLore(ItemStack item, int percent, String rate) {
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        CEnchantment enchantment = null;

        for (CEnchantment en : crazyManager.getRegisteredEnchantments()) {
            String ench = en.getCustomName();

            if (item.getItemMeta().getDisplayName().contains(ench)) enchantment = en;
        }

        for (String l : Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore")) {
            boolean line = true;

            if (l.contains("%Description%") || l.contains("%description%")) {
                if (enchantment != null) {
                    for (String L : enchantment.getInfoDescription()) {
                        lore.add(methods.color(L));
                    }
                }

                line = false;
            }

            if (rate.equalsIgnoreCase("Success")) {
                l = l.replace("%Success_Rate%", percent + "").replace("%success_rate%", percent + "")
                .replace("%Destroy_Rate%", methods.getPercent("%Destroy_Rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 0) + "")
                .replace("%destroy_rate%", methods.getPercent("%destroy_rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 0) + "");
            } else {
                l = l.replace("%Destroy_Rate%", percent + "").replace("%destroy_rate%", percent + "")
                .replace("%Success_Rate%", methods.getPercent("%Success_Rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 100) + "")
                .replace("%success_rate%", methods.getPercent("%success_rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 100) + "");
            }

            if (line) lore.add(methods.color(l));
        }

        assert meta != null;
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
    
    public boolean hasPercent(Dust dust, ItemStack item) {
        String arg = "";

        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            List<String> lore = item.getItemMeta().getLore();
            List<String> fileLore = Files.CONFIG.getFile().getStringList("Settings.Dust." + dust.getConfigName() + ".Lore");
            int i = 0;

            if (lore != null && lore.size() == fileLore.size()) {
                for (String l : fileLore) {
                    l = methods.color(l);
                    String lo = lore.get(i);

                    if (l.contains("%Percent%")) {
                        String[] b = l.split("%Percent%");
                        if (b.length >= 1) arg = lo.replace(b[0], "");
                        if (b.length >= 2) arg = arg.replace(b[1], "");
                        break;
                    }

                    if (l.contains("%percent%")) {
                        String[] b = l.split("%percent%");
                        if (b.length >= 1) arg = lo.replace(b[0], "");
                        if (b.length >= 2) arg = arg.replace(b[1], "");
                        break;
                    }

                    i++;
                }
            }
        }

        return methods.isInt(arg);
    }
    
    public int getPercent(Dust dust, ItemStack item) {
        String arg = "";

        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            List<String> lore = item.getItemMeta().getLore();
            List<String> fileLore = Files.CONFIG.getFile().getStringList("Settings.Dust." + dust.getConfigName() + ".Lore");
            int i = 0;

            if (lore != null && lore.size() == fileLore.size()) {
                for (String l : fileLore) {
                    l = methods.color(l);
                    String lo = lore.get(i);

                    if (l.contains("%Percent%")) {
                        String[] b = l.split("%Percent%");
                        if (b.length >= 1) arg = lo.replace(b[0], "");
                        if (b.length >= 2) arg = arg.replace(b[1], "");
                        break;
                    }

                    if (l.contains("%percent%")) {
                        String[] b = l.split("%percent%");
                        if (b.length >= 1) arg = lo.replace(b[0], "");
                        if (b.length >= 2) arg = arg.replace(b[1], "");
                        break;
                    }

                    i++;
                }
            }
        }

        if (methods.isInt(arg)) {
            return Integer.parseInt(arg);
        } else {
            return 0;
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (e.getCurrentItem() != null && e.getCursor() != null) {
            ItemStack book = e.getCurrentItem();
            ItemStack dust = e.getCursor();

            if (book.getAmount() == 1 && book.hasItemMeta() && dust.hasItemMeta() && book.getItemMeta().hasLore() && dust.getItemMeta().hasLore() && book.getItemMeta().hasDisplayName() &&
            dust.getItemMeta().hasDisplayName() && book.getType() == crazyManager.getEnchantmentBookItem().getType()) {
                boolean toggle = false;
                String name = book.getItemMeta().getDisplayName();

                for (CEnchantment en : crazyManager.getRegisteredEnchantments()) {
                    if (name.contains(methods.color(en.getBookColor() + en.getCustomName()))) toggle = true;
                }

                if (!toggle) return;

                if (dust.getItemMeta().getDisplayName().equals(methods.color(Files.CONFIG.getFile().getString("Settings.Dust.SuccessDust.Name"))) &&
                dust.getType() == new ItemBuilder().setMaterial(Files.CONFIG.getFile().getString("Settings.Dust.SuccessDust.Item")).getMaterial()) {
                    int per = getPercent(Dust.SUCCESS_DUST, dust);

                    if (methods.hasArgument("%success_rate%", Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"))) {
                        int total = methods.getPercent("%success_rate%", book, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 100);

                        if (total >= 100) return;

                        if (player.getGameMode() == GameMode.CREATIVE && dust.getAmount() > 1) {
                            player.sendMessage(methods.getPrefix() + methods.color("&cPlease unstack the dust for them to work."));
                            return;
                        }

                        per += total;
                        if (per < 0) per = 0;
                        if (per > 100) per = 100;
                        e.setCancelled(true);
                        setLore(book, per, "Success");
                        player.setItemOnCursor(methods.removeItem(dust));
                        player.updateInventory();
                    }

                    return;
                }

                if (dust.getItemMeta().getDisplayName().equals(methods.color(Files.CONFIG.getFile().getString("Settings.Dust.DestroyDust.Name"))) &&
                dust.getType() == new ItemBuilder().setMaterial(Files.CONFIG.getFile().getString("Settings.Dust.DestroyDust.Item")).getMaterial()) {
                    int per = getPercent(Dust.DESTROY_DUST, dust);

                    if (methods.hasArgument("%destroy_rate%", Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"))) {
                        int total = methods.getPercent("%destroy_rate%", book, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 0);
                        if (total <= 0) return;

                        if (player.getGameMode() == GameMode.CREATIVE && dust.getAmount() > 1) {
                            player.sendMessage(methods.getPrefix() + methods.color("&cPlease unstack the dust for them to work."));
                            return;
                        }

                        per = total - per;
                        if (per < 0) per = 0;
                        if (per > 100) per = 100;
                        e.setCancelled(true);
                        setLore(book, per, "Destroy");
                        player.setItemOnCursor(methods.removeItem(dust));
                        player.updateInventory();
                    }
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void openDust(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        FileConfiguration config = Files.CONFIG.getFile();

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = methods.getItemInHand(player);

            if (item != null) {
                if (hasPercent(Dust.SUCCESS_DUST, item)) {

                    if (methods.isSimilar(item, Dust.SUCCESS_DUST.getDust(getPercent(Dust.SUCCESS_DUST, item), 1))) e.setCancelled(true);

                } else if (hasPercent(Dust.DESTROY_DUST, item)) {

                    if (methods.isSimilar(item, Dust.DESTROY_DUST.getDust(getPercent(Dust.DESTROY_DUST, item), 1))) e.setCancelled(true);

                } else if (hasPercent(Dust.MYSTERY_DUST, item) && methods.isSimilar(item, Dust.MYSTERY_DUST.getDust(getPercent(Dust.MYSTERY_DUST, item), 1))) {
                    e.setCancelled(true);
                    methods.setItemInHand(player, methods.removeItem(item));
                    player.getInventory().addItem(pickDust().getDust(methods.percentPick(getPercent(Dust.MYSTERY_DUST, item) + 1, 1), 1));
                    player.updateInventory();
                    player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1, 1);

                    if (config.getBoolean("Settings.Dust.MysteryDust.Firework.Toggle")) {
                        List<Color> colors = new ArrayList<>();
                        String colorString = config.getString("Settings.Dust.MysteryDust.Firework.Colors", "Black, Gray, Lime");

                        checkString(colors, colorString, methods);

                        methods.fireWork(player.getLocation().add(0, 1, 0), colors);
                    }
                }
            }
        }
    }

    public void checkString(List<Color> colors, String colorString, Methods methods) {
        if (colorString.contains(", ")) {
            for (String color : colorString.split(", ")) {
                Color c = methods.getColor(color);

                if (c != null) colors.add(c);
            }
        } else {
            Color c = methods.getColor(colorString);

            if (c != null) colors.add(c);
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