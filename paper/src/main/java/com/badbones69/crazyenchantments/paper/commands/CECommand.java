package com.badbones69.crazyenchantments.paper.commands;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.FileManager;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.PluginSupport;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.Enchant;
import com.badbones69.crazyenchantments.paper.api.managers.guis.InfoMenuManager;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.configs.ConvertTinker;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.paper.listeners.ScramblerListener;
import com.badbones69.crazyenchantments.paper.listeners.ShopListener;
import com.badbones69.crazyenchantments.paper.utilities.misc.ColorUtils;
import com.badbones69.crazyenchantments.paper.utilities.misc.NumberUtils;
import com.google.gson.Gson;
import com.ryderbelserion.cluster.bukkit.utils.LegacyUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CECommand implements CommandExecutor {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Starter starter = this.plugin.getStarter();

    private final FileManager fileManager = this.plugin.getFileManager();

    private final Methods methods = this.starter.getMethods();

    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    // Settings.
    private final ProtectionCrystalSettings protectionCrystalSettings = this.starter.getProtectionCrystalSettings();
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    // Plugin Support.
    private final PluginSupport pluginSupport = this.starter.getPluginSupport();

    // Plugin Managers.
    private final InfoMenuManager infoMenuManager = this.starter.getInfoMenuManager();

    // Listeners
    private final ScramblerListener scramblerListener = this.starter.getScramblerListener();

    // Economy Management.
    private final ShopListener shopListener = this.plugin.getShopListener();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        boolean isPlayer = sender instanceof Player;

        if (args.length == 0) { // /ce
            if (!isPlayer) {
                sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                return true;
            }

            if (hasPermission(sender, "gui")) this.shopListener.openGUI((Player) sender);

            return true;
        } else {
            switch (args[0].toLowerCase()) {
                case "updateenchants" -> {
                    if (!isPlayer) return true;
                    if (!hasPermission(sender, "updateenchants")) return true;

                    Gson gson = new Gson();
                    Player player = (Player) sender;
                    Enchant enchants = new Enchant(null);
                    ArrayList<Component> lore = new ArrayList<>();
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (!item.hasItemMeta() || item.lore() == null) return true;
                    ItemMeta meta = item.getItemMeta();

                    if (meta.getPersistentDataContainer().has(DataKeys.ENCHANTMENTS.getKey())) {
                        enchants = gson.fromJson(meta.getPersistentDataContainer().get(DataKeys.ENCHANTMENTS.getKey(), PersistentDataType.STRING), Enchant.class);
                    }

                    for (Component line : meta.lore()) {
                        String strippedName = ColorUtils.toPlainText(line);
                        boolean addedLine = false;

                        for (CEnchantment activeEnchant : this.enchantmentBookSettings.getRegisteredEnchantments()) {
                            if (!strippedName.toLowerCase().contains(activeEnchant.getCustomName().toLowerCase().replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "")) &&
                                !strippedName.toLowerCase().contains(activeEnchant.getName().toLowerCase())) continue;

                            if (enchants.hasEnchantment(activeEnchant.getName())) break;

                            enchants.addEnchantment(activeEnchant.getName(), NumberUtils.convertLevelInteger(strippedName.split(" ")[strippedName.split(" ").length-1]));
                            lore.add(ColorUtils.legacyTranslateColourCodes(activeEnchant.getCustomName() + " " + NumberUtils.toRoman(enchants.getLevel(activeEnchant.getName()))));
                            addedLine = true;
                            break;
                        }
                        if (!addedLine) lore.add(line);
                    }

                    meta.lore(lore);
                    if (!enchants.isEmpty()) meta.getPersistentDataContainer().set(DataKeys.ENCHANTMENTS.getKey(), PersistentDataType.STRING, gson.toJson(enchants));
                    item.setItemMeta(meta);
                    player.getInventory().setItemInMainHand(item);
                    return true;
                }

                case "convert" -> {
                    if (hasPermission(sender, "convert")) {
                        List.of(
                                "&8&m=======================================================",
                                "&eTrying to update config files.",
                                "&eIf you have any issues, Please contact Discord Support.",
                                "https://discord.gg/badbones-s-live-chat-182615261403283459",
                                "&eMake sure to check console for more information.",
                                "&8&m======================================================="
                        ).forEach( line -> sender.sendMessage(LegacyUtils.color(line)));
                        ConvertTinker.convert();
                    }

                    return true;
                }

                case "help" -> { // /ce help
                    if (hasPermission(sender, "access")) sender.sendMessage(Messages.HELP.getMessage());

                    return true;
                }

                case "reload" -> { // /ce reload
                    if (hasPermission(sender, "reload")) {
                        this.crazyManager.getCEPlayers().forEach(name -> this.crazyManager.backupCEPlayer(name.getPlayer()));
                        this.fileManager.setup();
                        this.crazyManager.load();
                        sender.sendMessage(Messages.CONFIG_RELOAD.getMessage());

                        this.pluginSupport.updateHooks();
                    }

                    return true;
                }
                case "limit" -> {
                    if (hasPermission(sender, "limit")) {
                        HashMap<String, String> placeholders = new HashMap<>();

                        placeholders.put("%bypass%", String.valueOf(sender.hasPermission("crazyenchantments.bypass.limit")));

                        assert sender instanceof Player;
                        placeholders.put("%limit%", String.valueOf(this.crazyManager.getPlayerMaxEnchantments((Player) sender)));
                        placeholders.put("%vanilla%", String.valueOf(this.crazyManager.checkVanillaLimit()));
                        placeholders.put("%item%", String.valueOf(this.enchantmentBookSettings.getEnchantmentAmount(this.methods.getItemInHand((Player) sender), this.crazyManager.checkVanillaLimit())));

                        sender.sendMessage(Messages.LIMIT_COMMAND.getMessage(placeholders));
                    }

                    return true;
                }
                case "debug" -> { // /ce debug
                    if (hasPermission(sender, "debug")) {
                        List<String> brokenEnchantments = new ArrayList<>();
                        List<String> brokenEnchantmentTypes = new ArrayList<>();

                        for (CEnchantments enchantment : CEnchantments.values()) {
                            if (!Files.ENCHANTMENTS.getFile().contains("Enchantments." + enchantment.getName())) brokenEnchantments.add(enchantment.getName());

                            if (enchantment.getType() == null) brokenEnchantmentTypes.add(enchantment.getName());
                        }

                        if (brokenEnchantments.isEmpty() && brokenEnchantmentTypes.isEmpty()) {
                            sender.sendMessage(ColorUtils.getPrefix("&aAll enchantments are loaded."));
                        } else {

                            if (!brokenEnchantments.isEmpty()) {
                                int amount = 1;
                                sender.sendMessage(ColorUtils.getPrefix("&cMissing Enchantments:"));
                                sender.sendMessage(ColorUtils.getPrefix("&7These enchantments are broken due to one of the following reasons:"));

                                for (String broke : brokenEnchantments) {
                                    sender.sendMessage(LegacyUtils.color("&c#" + amount + ": &6" + broke));
                                    amount++;
                                }

                                sender.sendMessage(LegacyUtils.color("&7- &cMissing from the Enchantments.yml"));
                                sender.sendMessage(LegacyUtils.color("&7- &c<Enchantment Name>: option was changed"));
                                sender.sendMessage(LegacyUtils.color("&7- &cYaml format has been broken."));
                            }

                            if (!brokenEnchantmentTypes.isEmpty()) {
                                int i = 1;
                                sender.sendMessage(ColorUtils.getPrefix("&cEnchantments with null types:"));
                                sender.sendMessage(ColorUtils.getPrefix("&7These enchantments are broken due to the enchantment type being null."));

                                for (String broke : brokenEnchantmentTypes) {
                                    sender.sendMessage(LegacyUtils.color("&c#" + i + ": &6" + broke));
                                    i++;
                                }
                            }
                        }

                        sender.sendMessage(ColorUtils.getPrefix("&cEnchantment Types and amount of items in each:"));

                        this.infoMenuManager.getEnchantmentTypes().forEach(type -> sender.sendMessage(LegacyUtils.color("&c" + type.getName() + ": &6" + type.getEnchantmentMaterials().size())));
                    }

                    return true;
                }
                case "fix" -> { // /ce fix
                    if (hasPermission(sender, "fix")) {
                        List<CEnchantments> brokenEnchantments = new ArrayList<>();
                        FileConfiguration file = Files.ENCHANTMENTS.getFile();

                        for (CEnchantments enchantment : CEnchantments.values()) {
                            if (!file.contains("Enchantments." + enchantment.getName())) brokenEnchantments.add(enchantment);
                        }

                        sender.sendMessage(LegacyUtils.color("&7Fixed a total of " + brokenEnchantments.size() + " enchantments."));

                        for (CEnchantments enchantment : brokenEnchantments) {
                            String path = "Enchantments." + enchantment.getName();
                            file.set(path + ".Enabled", true);
                            file.set(path + ".Name", enchantment.getName());
                            file.set(path + ".Color", "&7");
                            file.set(path + ".BookColor", "&b&l");
                            file.set(path + ".MaxPower", 1);
                            file.set(path + ".Enchantment-Type", enchantment.getType().getName());
                            file.set(path + ".Info.Name", "&e&l" + enchantment.getName() + " &7(&bI&7)");
                            file.set(path + ".Info.Description", enchantment.getDescription());
                            List<String> categories = new ArrayList<>();
                            this.enchantmentBookSettings.getCategories().forEach(category -> categories.add(category.getName()));
                            file.set(path + ".Categories", categories);
                            Files.ENCHANTMENTS.saveFile();
                        }
                    }

                    return true;
                }
                case "info" -> { // /ce info [enchantment]
                    if (hasPermission(sender, "info")) {
                        if (args.length == 1) {

                            if (!isPlayer) {
                                sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                                return true;
                            }

                            this.infoMenuManager.openInfoMenu((Player) sender);
                        } else {
                            EnchantmentType enchantmentType = methods.getFromName(args[1]);

                            if (enchantmentType != null) {
                                assert sender instanceof Player;
                                this.infoMenuManager.openInfoMenu((Player) sender, enchantmentType);
                                return true;
                            }

                            CEnchantment enchantment = this.crazyManager.getEnchantmentFromName(args[1]);
                            if (enchantment != null) {
                                sender.sendMessage(enchantment.getInfoName());
                                enchantment.getInfoDescription().forEach(sender::sendMessage);
                                return true;
                            }

                            sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
                        }
                    }

                    return true;
                }
                case "spawn" -> { // /ce spawn <enchantment> [level:#/world:<world>/x:#/y:#/z:#]
                    if (hasPermission(sender, "spawn")) {
                        if (args.length >= 2) {
                            CEnchantment enchantment = this.crazyManager.getEnchantmentFromName(args[1]);
                            Category category = this.enchantmentBookSettings.getCategory(args[1]);
                            Location location = isPlayer ? ((Player) sender).getLocation() : new Location(this.plugin.getServer().getWorlds().get(0), 0, 0, 0);
                            int level = 1;

                            if (enchantment == null && category == null) {
                                sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
                                return true;
                            }

                            for (String optionString : args) {
                                try {
                                    String option = optionString.split(":")[0];
                                    String value = optionString.split(":")[1];
                                    boolean isInt = NumberUtils.isInt(value);

                                    switch (option.toLowerCase()) {
                                        case "level" -> {
                                            if (isInt) {
                                                level = Integer.parseInt(value);
                                            } else if (value.contains("-")) {
                                                level = this.methods.getRandomNumber(value);
                                            }
                                        }

                                        case "world" -> {
                                            World world = this.plugin.getServer().getWorld(value);
                                            if (world != null) location.setWorld(world);
                                        }

                                        case "x" -> {
                                            if (isInt) location.setX(Integer.parseInt(value));
                                        }

                                        case "y" -> {
                                            if (isInt) location.setY(Integer.parseInt(value));
                                        }

                                        case "z" -> {
                                            if (isInt) location.setZ(Integer.parseInt(value));
                                        }
                                    }
                                } catch (Exception ignore) {}
                            }

                            location.getWorld().dropItemNaturally(location, category == null ? new CEBook(enchantment, level).buildBook() : category.getLostBook().getLostBook(category).build());
                            HashMap<String, String> placeholders = new HashMap<>();

                            placeholders.put("%World%", location.getWorld().getName());
                            placeholders.put("%X%", String.valueOf(location.getBlockX()));
                            placeholders.put("%Y%", String.valueOf(location.getBlockY()));
                            placeholders.put("%Z%", String.valueOf(location.getBlockZ()));

                            sender.sendMessage(Messages.SPAWNED_BOOK.getMessage(placeholders));

                            return true;
                        }

                        sender.sendMessage(ColorUtils.getPrefix() + LegacyUtils.color("&c/ce Spawn <Enchantment/Category> [(Level:#/Min-Max)/World:<World>/X:#/Y:#/Z:#]"));
                    }

                    return true;
                }

                case "lostbook", "lb" -> { // /ce lostbook <category> [amount] [player]
                    if (hasPermission(sender, "lostbook")) {
                        if (args.length >= 2) {

                            if (args.length <= 3 && !isPlayer) {
                                sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                                return true;
                            }

                            int amount = 1;
                            Player player;
                            Category category = enchantmentBookSettings.getCategory(args[1]);

                            if (args.length >= 3) {
                                if (!NumberUtils.isInt(args[2])) {
                                    sender.sendMessage(Messages.NOT_A_NUMBER.getMessage().replace("%Arg%", args[2]).replace("%arg%", args[2]));
                                    return true;
                                }

                                amount = Integer.parseInt(args[2]);
                            }

                            if (args.length >= 4) {
                                if (!this.methods.isPlayerOnline(args[3], sender)) return true;

                                player = this.methods.getPlayer(args[3]);
                            } else {
                                player = (Player) sender;
                            }

                            if (category != null) {
                                if (this.methods.isInventoryFull(player)) {
                                    player.getWorld().dropItemNaturally(player.getLocation(), category.getLostBook().getLostBook(category, amount).build());
                                } else {
                                    player.getInventory().addItem(category.getLostBook().getLostBook(category, amount).build());
                                }

                                return true;
                            }

                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Category%", args[1]);
                            sender.sendMessage(Messages.NOT_A_CATEGORY.getMessage(placeholders));
                            return true;
                        }

                        sender.sendMessage(ColorUtils.getPrefix() + LegacyUtils.color("&c/ce LostBook <Category> [Amount] [Player]"));
                    }

                    return true;
                }

                case "scrambler", "s" -> { // /ce scrambler [amount] [player]
                    if (hasPermission(sender, "scrambler")) {
                        int amount = 1;
                        Player player;

                        if (args.length <= 2 && !isPlayer) {
                            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                            return true;
                        }

                        if (args.length >= 2) {
                            if (!NumberUtils.isInt(args[1])) {
                                sender.sendMessage(Messages.NOT_A_NUMBER.getMessage().replace("%Arg%", args[1]).replace("%arg%", args[1]));
                                return true;
                            }

                            amount = Integer.parseInt(args[1]);
                        }

                        if (args.length >= 3) {
                            if (!this.methods.isPlayerOnline(args[2], sender)) return true;
                            player = this.methods.getPlayer(args[2]);
                        } else {
                            player = (Player) sender;
                        }

                        if (methods.isInventoryFull(player)) {
                            sender.sendMessage(Messages.INVENTORY_FULL.getMessage());
                            return true;
                        }

                        player.getInventory().addItem(this.scramblerListener.getScramblers(amount));
                        HashMap<String, String> placeholders = new HashMap<>();
                        placeholders.put("%Amount%", String.valueOf(amount));
                        placeholders.put("%Player%", player.getName());
                        sender.sendMessage(Messages.GIVE_SCRAMBLER_CRYSTAL.getMessage(placeholders));
                        player.sendMessage(Messages.GET_SCRAMBLER.getMessage(placeholders));
                    }

                    return true;
                }

                case "crystal", "c" -> { // /ce crystal [amount] [player]
                    if (hasPermission(sender, "crystal")) {
                        int amount = 1;
                        Player player;

                        if (args.length <= 2 && !isPlayer) {
                            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                            return true;
                        }

                        if (args.length >= 2) {
                            if (!NumberUtils.isInt(args[1])) {
                                sender.sendMessage(Messages.NOT_A_NUMBER.getMessage().replace("%Arg%", args[1]).replace("%arg%", args[1]));
                                return true;
                            }

                            amount = Integer.parseInt(args[1]);
                        }

                        if (args.length >= 3) {
                            if (!this.methods.isPlayerOnline(args[2], sender)) return true;
                            player = this.methods.getPlayer(args[2]);
                        } else {
                            player = (Player) sender;
                        }

                        if (this.methods.isInventoryFull(player)) {
                            sender.sendMessage(Messages.INVENTORY_FULL.getMessage());
                            return true;
                        }

                        player.getInventory().addItem(this.protectionCrystalSettings.getCrystals(amount));
                        HashMap<String, String> placeholders = new HashMap<>();
                        placeholders.put("%Amount%", String.valueOf(amount));
                        placeholders.put("%Player%", player.getName());
                        sender.sendMessage(Messages.GIVE_PROTECTION_CRYSTAL.getMessage(placeholders));
                        player.sendMessage(Messages.GET_PROTECTION_CRYSTAL.getMessage(placeholders));
                    }

                    return true;
                }

                case "dust" -> { // /ce dust <Success/Destroy/Mystery> [Amount] [Player] [Percent]
                    if (hasPermission(sender, "dust")) {
                        if (args.length >= 2) {
                            Player player;
                            int amount = 1;
                            int percent = 0;

                            if (args.length == 2 && !isPlayer) {
                                sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                                return true;
                            }

                            if (args.length >= 3) {
                                if (!NumberUtils.isInt(args[2])) {
                                    sender.sendMessage(Messages.NOT_A_NUMBER.getMessage().replace("%Arg%", args[2]).replace("%arg%", args[2]));
                                    return true;
                                }

                                amount = Integer.parseInt(args[2]);
                            }

                            if (args.length >= 4) {
                                if (!this.methods.isPlayerOnline(args[3], sender)) return true;

                                player = this.methods.getPlayer(args[3]);
                            } else {
                                if (!isPlayer) {
                                    sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                                    return true;
                                } else {
                                    player = (Player) sender;
                                }
                            }

                            if (args.length >= 5) {
                                if (!NumberUtils.isInt(args[4])) {
                                    sender.sendMessage(Messages.NOT_A_NUMBER.getMessage().replace("%Arg%", args[4]).replace("%arg%", args[4]));
                                    return true;
                                }

                                percent = Integer.parseInt(args[4]);
                            }

                            Dust dust = Dust.getFromName(args[1]);

                            if (dust != null) {

                                player.getInventory().addItem(args.length >= 5 ? dust.getDust(percent, amount) : dust.getDust(amount));

                                HashMap<String, String> placeholders = new HashMap<>();
                                placeholders.put("%Amount%", String.valueOf(amount));
                                placeholders.put("%Player%", player.getName());

                                switch (dust) {
                                    case SUCCESS_DUST -> {
                                        player.sendMessage(Messages.GET_SUCCESS_DUST.getMessage(placeholders));
                                        sender.sendMessage(Messages.GIVE_SUCCESS_DUST.getMessage(placeholders));
                                    }

                                    case DESTROY_DUST -> {
                                        player.sendMessage(Messages.GET_DESTROY_DUST.getMessage(placeholders));
                                        sender.sendMessage(Messages.GIVE_DESTROY_DUST.getMessage(placeholders));
                                    }

                                    case MYSTERY_DUST -> {
                                        player.sendMessage(Messages.GET_MYSTERY_DUST.getMessage(placeholders));
                                        sender.sendMessage(Messages.GIVE_MYSTERY_DUST.getMessage(placeholders));
                                    }
                                }

                                return true;
                            }
                        }

                        sender.sendMessage(ColorUtils.legacyTranslateColourCodes(ColorUtils.getPrefix() + "&c/ce Dust <Success/Destroy/Mystery> <Amount> [Player] [Percent]"));
                    }

                    return true;
                }

                case "scroll" -> { // /ce scroll <scroll> [amount] [player]
                    if (hasPermission(sender, "scroll")) {
                        if (args.length >= 2) {
                            int amount = 1;
                            String name = sender.getName();

                            if (args.length >= 3) {
                                if (!NumberUtils.isInt(args[2])) {
                                    sender.sendMessage(Messages.NOT_A_NUMBER.getMessage().replace("%Arg%", args[2]).replace("%arg%", args[2]));
                                    return true;
                                }

                                amount = Integer.parseInt(args[2]);
                            }

                            if (args.length >= 4) {
                                name = args[3];

                                if (!this.methods.isPlayerOnline(name, sender)) return true;
                            } else {
                                if (!isPlayer) {
                                    sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                                    return true;
                                }
                            }

                            Scrolls scroll = Scrolls.getFromName(args[1]);

                            if (scroll != null) {
                                this.methods.getPlayer(name).getInventory().addItem(scroll.getScroll(amount));
                                return true;
                            }
                        }

                        sender.sendMessage(ColorUtils.getPrefix() + LegacyUtils.color("&c/ce Scroll <White/Black/Transmog> [Amount] [Player]"));
                    }

                    return true;
                }

                case "add" -> { // /ce add <enchantment> [level]
                    if (hasPermission(sender, "add")) {

                        if (!isPlayer) {
                            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                            return true;
                        }

                        if (args.length >= 2) {
                            Player player = (Player) sender;
                            String level = "1";

                            if (args.length >= 3) {
                                if (!NumberUtils.isInt(args[2])) {
                                    sender.sendMessage(Messages.NOT_A_NUMBER.getMessage().replace("%Arg%", args[2]).replace("%arg%", args[2]));
                                    return true;
                                }

                                level = args[2];
                            }

                            Enchantment vanillaEnchantment = this.methods.getEnchantment(args[1]);
                            CEnchantment ceEnchantment = this.crazyManager.getEnchantmentFromName(args[1]);
                            boolean isVanilla = vanillaEnchantment != null;

                            if (vanillaEnchantment == null && ceEnchantment == null) {
                                sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
                                return true;
                            }

                            if (this.methods.getItemInHand(player).getType() == Material.AIR) {
                                sender.sendMessage(Messages.DOESNT_HAVE_ITEM_IN_HAND.getMessage());
                                return true;
                            }

                            if (isVanilla) {
                                ItemStack item = this.methods.getItemInHand(player).clone();
                                item.addUnsafeEnchantment(vanillaEnchantment, Integer.parseInt(level));
                                this.methods.setItemInHand(player, item);
                            } else {
                                this.methods.setItemInHand(player, this.crazyManager.addEnchantment(this.methods.getItemInHand(player), ceEnchantment, Integer.parseInt(level)));
                            }

                            return true;
                        }

                        sender.sendMessage(ColorUtils.getPrefix("&c/ce add <Enchantment> [LvL]"));
                    }

                    return true;
                }

                case "remove" -> { // /ce remove <enchantment>
                    if (hasPermission(sender, "remove")) {

                        if (!isPlayer) {
                            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                            return true;
                        }

                        if (args.length >= 2) {
                            Player player = (Player) sender;
                            Enchantment vanillaEnchantment = this.methods.getEnchantment(args[1]);
                            CEnchantment ceEnchantment = this.crazyManager.getEnchantmentFromName(args[1]);
                            boolean isVanilla = vanillaEnchantment != null;

                            if (vanillaEnchantment == null && ceEnchantment == null) {
                                sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
                                return true;
                            }

                            if (this.methods.getItemInHand(player).getType() == Material.AIR) {
                                sender.sendMessage(Messages.DOESNT_HAVE_ITEM_IN_HAND.getMessage());
                                return true;
                            }

                            ItemStack item = this.methods.getItemInHand(player);

                            if (isVanilla) {
                                ItemStack clone = this.methods.getItemInHand(player).clone();
                                clone.removeEnchantment(vanillaEnchantment);
                                this.methods.setItemInHand(player, clone);
                                return true;
                            } else {
                                if (enchantmentBookSettings.hasEnchantment(item, ceEnchantment)) {
                                    this.methods.setItemInHand(player, enchantmentBookSettings.removeEnchantment(item, ceEnchantment));
                                    HashMap<String, String> placeholders = new HashMap<>();
                                    placeholders.put("%Enchantment%", ceEnchantment.getCustomName());
                                    player.sendMessage(Messages.REMOVED_ENCHANTMENT.getMessage(placeholders).replaceAll("&", ""));
                                    return true;
                                }
                            }

                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Enchantment%", args[1]);
                            sender.sendMessage(Messages.DOESNT_HAVE_ENCHANTMENT.getMessage(placeholders));
                        }

                        sender.sendMessage(ColorUtils.getPrefix() + LegacyUtils.color("&c/ce Remove <Enchantment>"));
                    }

                    return true;
                }

                case "book" -> { // /ce book <enchantment> [level] [amount] [player]
                    if (hasPermission(sender, "book")) {
                        if (args.length >= 2) {

                            if (args.length == 2 && !isPlayer) {
                                sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                                return true;
                            }

                            CEnchantment enchantment = crazyManager.getEnchantmentFromName(args[1]);
                            int level = 1;
                            int amount = 1;
                            Player player;

                            if (args.length >= 3) {
                                if (NumberUtils.isInt(args[2])) {
                                    level = Integer.parseInt(args[2]);
                                } else if (args[2].contains("-")) {
                                    level = this.methods.getRandomNumber(args[2]);
                                } else {
                                    sender.sendMessage(Messages.NOT_A_NUMBER.getMessage().replace("%Arg%", args[2]).replace("%arg%", args[2]));
                                    return true;
                                }
                            }

                            if (args.length >= 4) {
                                if (!NumberUtils.isInt(args[3])) {
                                    sender.sendMessage(Messages.NOT_A_NUMBER.getMessage().replace("%Arg%", args[3]).replace("%arg%", args[3]));
                                    return true;
                                }

                                amount = Integer.parseInt(args[3]);
                            }

                            if (args.length >= 5) {
                                if (!this.methods.isPlayerOnline(args[4], sender)) return true;

                                player = this.methods.getPlayer(args[4]);
                            } else {
                                assert sender instanceof Player;
                                player = (Player) sender;
                            }

                            if (enchantment == null) {
                                sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
                                return true;
                            }

                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Player%", player.getName());
                            sender.sendMessage(Messages.SEND_ENCHANTMENT_BOOK.getMessage(placeholders));
                            player.getInventory().addItem(new CEBook(enchantment, level, amount).buildBook());
                            return true;
                        }

                        sender.sendMessage(ColorUtils.getPrefix() + LegacyUtils.color("&c/ce Book <Enchantment> [Lvl] [Amount] [Player]"));
                    }

                    return true;
                }

                default -> {
                    sender.sendMessage(ColorUtils.getPrefix("&cDo /ce help for more info."));
                    return false;
                }
            }
        }
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        return this.methods.hasPermission(sender, permission, true);
    }
}