package com.badbones69.crazyenchantments.listeners;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.economy.Currency;
import com.badbones69.crazyenchantments.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.api.enums.Dust;
import com.badbones69.crazyenchantments.api.enums.Messages;
import com.badbones69.crazyenchantments.api.enums.Scrolls;
import com.badbones69.crazyenchantments.api.events.BuyBookEvent;
import com.badbones69.crazyenchantments.api.objects.CEBook;
import com.badbones69.crazyenchantments.api.objects.Category;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.listeners.ProtectionCrystalListener;
import com.badbones69.crazyenchantments.listeners.ScramblerListener;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.*;
import java.util.Map.Entry;

public class SignListener implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    // Settings.
    private final ProtectionCrystalSettings protectionCrystalSettings = starter.getProtectionCrystalSettings();

    // Plugin Listeners.
    private final ScramblerListener scramblerListener = plugin.getScramblerListener();

    // Economy Management.
    private final CurrencyAPI currencyAPI = starter.getCurrencyAPI();

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        
        Location location = e.getClickedBlock().getLocation();
        Player player = e.getPlayer();

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (e.getClickedBlock().getState() instanceof Sign) {
            FileConfiguration config = Files.CONFIG.getFile();

            for (String l : Files.SIGNS.getFile().getConfigurationSection("Locations").getKeys(false)) {
                String type = Files.SIGNS.getFile().getString("Locations." + l + ".Type");

                if (location.equals(getLocation(l))) {
                    if (methods.isInventoryFull(player)) {
                        player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                        return;
                    }

                    List<String> options = new ArrayList<>();
                    options.add("ProtectionCrystal");
                    options.add("Scrambler");
                    options.add("DestroyDust");
                    options.add("SuccessDust");
                    options.add("BlackScroll");
                    options.add("WhiteScroll");
                    options.add("TransmogScroll");

                    for (String o : options) {
                        if (o.equalsIgnoreCase(type)) {
                            if (player.getGameMode() != GameMode.CREATIVE && Currency.isCurrency(config.getString("Settings.Costs." + o + ".Currency"))) {
                                Currency currency = Currency.getCurrency(config.getString("Settings.Costs." + o + ".Currency"));
                                int cost = config.getInt("Settings.Costs." + o + ".Cost");

                                if (currencyAPI.canBuy(player, currency, cost)) {
                                    currencyAPI.takeCurrency(player, currency, cost);
                                } else {
                                    String needed = (cost - currencyAPI.getCurrency(player, currency)) + "";

                                    if (currency != null) methods.switchCurrency(player, currency, "%Money_Needed%", "%XP%", needed);

                                    return;
                                }
                            }

                            if (config.contains("Settings.SignOptions." + o + "Style.Buy-Message")) player.sendMessage(methods.color(methods.getPrefix() + config.getString("Settings.SignOptions." + o + "Style.Buy-Message")));

                            switch (o) {
                                case "ProtectionCrystal" -> player.getInventory().addItem(protectionCrystalSettings.getCrystals());
                                case "Scrambler" -> player.getInventory().addItem(scramblerListener.getScramblers());
                                case "DestroyDust" -> player.getInventory().addItem(Dust.DESTROY_DUST.getDust());
                                case "SuccessDust" -> player.getInventory().addItem(Dust.SUCCESS_DUST.getDust());
                                case "BlackScroll" -> player.getInventory().addItem(Scrolls.BLACK_SCROLL.getScroll());
                                case "WhiteScroll" -> player.getInventory().addItem(Scrolls.WHITE_SCROLL.getScroll());
                                case "TransmogScroll" -> player.getInventory().addItem(Scrolls.TRANSMOG_SCROLL.getScroll());
                            }

                            return;
                        }
                    }

                    Category category = crazyManager.getCategory(type);

                    if (category != null) {
                        if (category.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                            if (currencyAPI.canBuy(player, category)) {
                                currencyAPI.takeCurrency(player, category);
                            } else {
                                String needed = (category.getCost() - currencyAPI.getCurrency(player, category.getCurrency())) + "";
                                methods.switchCurrency(player, category.getCurrency(), "%Money_Needed%", "%XP%", needed);
                                return;
                            }
                        }

                        CEBook book = crazyManager.getRandomEnchantmentBook(category);

                        if (book != null) {
                            ItemBuilder itemBuilder = book.getItemBuilder();

                            if (config.contains("Settings.SignOptions.CategoryShopStyle.Buy-Message")) {
                                player.sendMessage(methods.color(methods.getPrefix() + config.getString("Settings.SignOptions.CategoryShopStyle.Buy-Message")
                                .replace("%BookName%", itemBuilder.getName()).replace("%bookname%", itemBuilder.getName())
                                .replace("%Category%", category.getName()).replace("%category%", category.getName())));
                            }

                            BuyBookEvent event = new BuyBookEvent(crazyManager.getCEPlayer(player), category.getCurrency(), category.getCost(), book);
                            plugin.getServer().getPluginManager().callEvent(event);
                            player.getInventory().addItem(itemBuilder.build());
                        } else {
                            player.sendMessage(methods.getPrefix("&cThe category &6" + category.getName() + " &chas no enchantments assigned to it."));
                        }

                        return;
                    }
                }
            }
        }
    }

    private Location getLocation(String l) {
        World world = plugin.getServer().getWorld(Files.SIGNS.getFile().getString("Locations." + l + ".World"));
        int x = Files.SIGNS.getFile().getInt("Locations." + l + ".X");
        int y = Files.SIGNS.getFile().getInt("Locations." + l + ".Y");
        int z = Files.SIGNS.getFile().getInt("Locations." + l + ".Z");

        return new Location(world, x, y, z);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if (!crazyManager.isIgnoredEvent(e)) {
            Player player = e.getPlayer();
            Location location = e.getBlock().getLocation();

            for (String locationName : Files.SIGNS.getFile().getConfigurationSection("Locations").getKeys(false)) {
                if (location.equals(getLocation(locationName))) {
                    Files.SIGNS.getFile().set("Locations." + locationName, null);
                    Files.SIGNS.saveFile();
                    player.sendMessage(Messages.BREAK_ENCHANTMENT_SHOP_SIGN.getMessage());
                    return;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignMake(SignChangeEvent e) {
        Player player = e.getPlayer();
        Location loc = e.getBlock().getLocation();
        FileConfiguration signs = Files.SIGNS.getFile();
        String id = new Random().nextInt(Integer.MAX_VALUE) + "";

        for (int i = 0; i < 200; i++) {
            if (signs.contains("Locations." + id)) {
                id = new Random().nextInt(Integer.MAX_VALUE) + "";
            } else {
                break;
            }
        }

        String line1 = e.getLine(0);
        String line2 = e.getLine(1);

        if (methods.hasPermission(player, "sign", false) && line1.equalsIgnoreCase("{CrazyEnchant}")) {
            for (Category category : crazyManager.getCategories()) {
                assert line2 != null;
                if (line2.equalsIgnoreCase("{" + category.getName() + "}")) {
                    e.setLine(0, placeHolders(Objects.requireNonNull(Files.CONFIG.getFile().getString("Settings.SignOptions.CategoryShopStyle.Line1")), category));
                    e.setLine(1, placeHolders(Objects.requireNonNull(Files.CONFIG.getFile().getString("Settings.SignOptions.CategoryShopStyle.Line2")), category));
                    e.setLine(2, placeHolders(Objects.requireNonNull(Files.CONFIG.getFile().getString("Settings.SignOptions.CategoryShopStyle.Line3")), category));
                    e.setLine(3, placeHolders(Objects.requireNonNull(Files.CONFIG.getFile().getString("Settings.SignOptions.CategoryShopStyle.Line4")), category));
                    signs.set("Locations." + id + ".Type", category.getName());
                    checkSignLocation(loc, signs, id);
                    return;
                }
            }

            HashMap<String, String> types = new HashMap<>();
            types.put("Crystal", "ProtectionCrystal");
            types.put("Scrambler", "Scrambler");
            types.put("DestroyDust", "DestroyDust");
            types.put("SuccessDust", "SuccessDust");
            types.put("BlackScroll", "BlackScroll");
            types.put("WhiteScroll", "WhiteScroll");
            types.put("TransmogScroll", "TransmogScroll");

            for (Entry<String, String> type : types.entrySet()) {
                assert line2 != null;
                if (line2.equalsIgnoreCase("{" + type.getKey() + "}")) {
                    e.setLine(0, methods.color(Files.CONFIG.getFile().getString("Settings.SignOptions." + type.getValue() + "Style.Line1")));
                    e.setLine(1, methods.color(Files.CONFIG.getFile().getString("Settings.SignOptions." + type.getValue() + "Style.Line2")));
                    e.setLine(2, methods.color(Files.CONFIG.getFile().getString("Settings.SignOptions." + type.getValue() + "Style.Line3")));
                    e.setLine(3, methods.color(Files.CONFIG.getFile().getString("Settings.SignOptions." + type.getValue() + "Style.Line4")));
                    signs.set("Locations." + id + ".Type", type.getValue());
                    checkSignLocation(loc, signs, id);
                }
            }
        }
    }

    private void checkSignLocation(Location loc, FileConfiguration signs, String id) {
        signs.set("Locations." + id + ".World", Objects.requireNonNull(loc.getWorld()).getName());
        signs.set("Locations." + id + ".X", loc.getBlockX());
        signs.set("Locations." + id + ".Y", loc.getBlockY());
        signs.set("Locations." + id + ".Z", loc.getBlockZ());
        Files.SIGNS.saveFile();
    }

    private String placeHolders(String msg, Category category) {
        return methods.color(msg
        .replace("%category%", category.getName()).replace("%Category%", category.getName())
        .replace("%cost%", category.getCost() + "").replace("%Cost%", category.getCost() + "")
        .replace("%xp%", category.getCost() + "").replace("%XP%", category.getCost() + ""));
    }
}