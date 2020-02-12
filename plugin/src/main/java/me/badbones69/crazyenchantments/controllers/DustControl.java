package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.Dust;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.GameMode;
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
    
    private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
    private Random random = new Random();
    
    private static void setLore(ItemStack item, int percent, String rate) {
        ItemMeta m = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        CEnchantment enchantment = null;
        for (CEnchantment en : ce.getRegisteredEnchantments()) {
            String ench = en.getCustomName();
            if (item.getItemMeta().getDisplayName().contains(ench)) {
                enchantment = en;
            }
        }
        for (String l : Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore")) {
            boolean line = true;
            if (l.contains("%Description%") || l.contains("%description%")) {
                if (enchantment != null) {
                    for (String L : enchantment.getInfoDescription()) {
                        lore.add(Methods.color(L));
                    }
                }
                line = false;
            }
            if (rate.equalsIgnoreCase("Success")) {
                l = l.replace("%Success_Rate%", percent + "").replace("%success_rate%", percent + "")
                .replace("%Destroy_Rate%", Methods.getPercent("%Destroy_Rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 0) + "")
                .replace("%destroy_rate%", Methods.getPercent("%destroy_rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 0) + "");
            } else {
                l = l.replace("%Destroy_Rate%", percent + "").replace("%destroy_rate%", percent + "")
                .replace("%Success_Rate%", Methods.getPercent("%Success_Rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 100) + "")
                .replace("%success_rate%", Methods.getPercent("%success_rate%", item, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 100) + "");
            }
            if (line) {
                lore.add(Methods.color(l));
            }
        }
        m.setLore(lore);
        item.setItemMeta(m);
    }
    
    public static boolean hasPercent(Dust dust, ItemStack item) {
        String arg = "";
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            List<String> lore = item.getItemMeta().getLore();
            List<String> fileLore = Files.CONFIG.getFile().getStringList("Settings.Dust." + dust.getConfigName() + ".Lore");
            int i = 0;
            if (lore != null && fileLore != null && lore.size() == fileLore.size()) {
                for (String l : fileLore) {
                    l = Methods.color(l);
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
        return Methods.isInt(arg);
    }
    
    public static Integer getPercent(Dust dust, ItemStack item) {
        String arg = "";
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            List<String> lore = item.getItemMeta().getLore();
            List<String> fileLore = Files.CONFIG.getFile().getStringList("Settings.Dust." + dust.getConfigName() + ".Lore");
            int i = 0;
            if (lore != null && fileLore != null && lore.size() == fileLore.size()) {
                for (String l : fileLore) {
                    l = Methods.color(l);
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
        if (Methods.isInt(arg)) {
            return Integer.parseInt(arg);
        } else {
            return 0;
        }
    }
    
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        Player player = (Player) e.getWhoClicked();
        if (inv != null && e.getCurrentItem() != null && e.getCursor() != null) {
            ItemStack book = e.getCurrentItem();
            ItemStack dust = e.getCursor();
            if (book.getAmount() == 1 && book.hasItemMeta() && dust.hasItemMeta() && book.getItemMeta().hasLore() && dust.getItemMeta().hasLore() && book.getItemMeta().hasDisplayName() &&
            dust.getItemMeta().hasDisplayName() && book.getType() == ce.getEnchantmentBookItem().getType()) {
                boolean toggle = false;
                String name = book.getItemMeta().getDisplayName();
                for (CEnchantment en : ce.getRegisteredEnchantments()) {
                    if (name.contains(Methods.color(en.getBookColor() + en.getCustomName()))) {
                        toggle = true;
                    }
                }
                if (!toggle) {
                    return;
                }
                if (dust.getItemMeta().getDisplayName().equals(Methods.color(Files.CONFIG.getFile().getString("Settings.Dust.SuccessDust.Name"))) &&
                dust.getType() == new ItemBuilder().setMaterial(Files.CONFIG.getFile().getString("Settings.Dust.SuccessDust.Item")).getMaterial()) {
                    int per = getPercent(Dust.SUCCESS_DUST, dust);
                    if (Methods.hasArgument("%success_rate%", Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"))) {
                        int total = Methods.getPercent("%success_rate%", book, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 100);
                        if (total >= 100) return;
                        if (player.getGameMode() == GameMode.CREATIVE && dust.getAmount() > 1) {
                            player.sendMessage(Methods.getPrefix() + Methods.color("&cPlease unstack the dust for them to work."));
                            return;
                        }
                        per += total;
                        if (per < 0) per = 0;
                        if (per > 100) per = 100;
                        e.setCancelled(true);
                        setLore(book, per, "Success");
                        player.setItemOnCursor(Methods.removeItem(dust));
                        player.updateInventory();
                    }
                    return;
                }
                if (dust.getItemMeta().getDisplayName().equals(Methods.color(Files.CONFIG.getFile().getString("Settings.Dust.DestroyDust.Name"))) &&
                dust.getType() == new ItemBuilder().setMaterial(Files.CONFIG.getFile().getString("Settings.Dust.DestroyDust.Item")).getMaterial()) {
                    int per = getPercent(Dust.DESTROY_DUST, dust);
                    if (Methods.hasArgument("%destroy_rate%", Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"))) {
                        int total = Methods.getPercent("%destroy_rate%", book, Files.CONFIG.getFile().getStringList("Settings.EnchantmentBookLore"), 0);
                        if (total <= 0) return;
                        if (player.getGameMode() == GameMode.CREATIVE && dust.getAmount() > 1) {
                            player.sendMessage(Methods.getPrefix() + Methods.color("&cPlease unstack the dust for them to work."));
                            return;
                        }
                        per = total - per;
                        if (per < 0) per = 0;
                        if (per > 100) per = 100;
                        e.setCancelled(true);
                        setLore(book, per, "Destroy");
                        player.setItemOnCursor(Methods.removeItem(dust));
                        player.updateInventory();
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
            ItemStack item = Methods.getItemInHand(player);
            if (item != null) {
                if (hasPercent(Dust.SUCCESS_DUST, item)) {
                    if (Methods.isSimilar(item, Dust.SUCCESS_DUST.getDust(getPercent(Dust.SUCCESS_DUST, item), 1))) {
                        e.setCancelled(true);
                    }
                } else if (hasPercent(Dust.DESTROY_DUST, item)) {
                    if (Methods.isSimilar(item, Dust.DESTROY_DUST.getDust(getPercent(Dust.DESTROY_DUST, item), 1))) {
                        e.setCancelled(true);
                    }
                } else if (hasPercent(Dust.MYSTERY_DUST, item) && Methods.isSimilar(item, Dust.MYSTERY_DUST.getDust(getPercent(Dust.MYSTERY_DUST, item), 1))) {
                    e.setCancelled(true);
                    Methods.setItemInHand(player, Methods.removeItem(item));
                    player.getInventory().addItem(pickDust().getDust(Methods.percentPick(getPercent(Dust.MYSTERY_DUST, item) + 1, 1), 1));
                    player.updateInventory();
                    player.playSound(player.getLocation(), ce.getSound("BLOCK_LAVA_POP", "LAVA_POP"), 1, 1);
                    if (config.getBoolean("Settings.Dust.MysteryDust.Firework.Toggle")) {
                        List<Color> colors = new ArrayList<>();
                        String colorString = config.getString("Settings.Dust.MysteryDust.Firework.Colors", "Black, Gray, Lime");
                        if (colorString.contains(", ")) {
                            for (String color : colorString.split(", ")) {
                                Color c = Methods.getColor(color);
                                if (c != null) {
                                    colors.add(c);
                                }
                            }
                        } else {
                            Color c = Methods.getColor(colorString);
                            if (c != null) {
                                colors.add(c);
                            }
                        }
                        Methods.fireWork(player.getLocation().add(0, 1, 0), colors);
                    }
                }
            }
        }
    }
    
    private Dust pickDust() {
        List<Dust> dusts = new ArrayList<>();
        if (Files.CONFIG.getFile().getBoolean("Settings.Dust.MysteryDust.Dust-Toggle.Success")) {
            dusts.add(Dust.SUCCESS_DUST);
        }
        if (Files.CONFIG.getFile().getBoolean("Settings.Dust.MysteryDust.Dust-Toggle.Destroy")) {
            dusts.add(Dust.DESTROY_DUST);
        }
        if (Files.CONFIG.getFile().getBoolean("Settings.Dust.MysteryDust.Dust-Toggle.Failed")) {
            dusts.add(Dust.FAILED_DUST);
        }
        return dusts.get(random.nextInt(dusts.size()));
    }
    
}