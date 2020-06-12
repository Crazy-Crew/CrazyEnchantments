package me.badbones69.crazyenchantments.commands;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.enums.Dust;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.enums.Scrolls;
import me.badbones69.crazyenchantments.api.managers.InfoMenuManager;
import me.badbones69.crazyenchantments.api.objects.CEBook;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.Category;
import me.badbones69.crazyenchantments.api.objects.EnchantmentType;
import me.badbones69.crazyenchantments.controllers.ProtectionCrystal;
import me.badbones69.crazyenchantments.controllers.Scrambler;
import me.badbones69.crazyenchantments.controllers.ShopControl;
import org.bukkit.Bukkit;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CECommand implements CommandExecutor {
    
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    private FileManager fileManager = FileManager.getInstance();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        boolean isPlayer = sender instanceof Player;
        if (args.length == 0) {// /ce
            if (!isPlayer) {
                sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                return true;
            }
            if (hasPermission(sender, "gui")) {
                ShopControl.openGUI((Player) sender);
            }
            return true;
        } else {
            switch (args[0].toLowerCase()) {
                case "help":// /ce help
                    if (hasPermission(sender, "access")) {
                        sender.sendMessage(Messages.HELP.getMessage());
                    }
                    return true;
                case "reload":// /ce reload
                    if (hasPermission(sender, "reload")) {
                        ce.getCEPlayers().forEach(cePlayer -> ce.backupCEPlayer(cePlayer));
                        fileManager.setup(ce.getPlugin());
                        ce.load();
                        sender.sendMessage(Messages.CONFIG_RELOAD.getMessage());
                    }
                    return true;
                case "limit":
                    if (hasPermission(sender, "limit")) {
                        HashMap<String, String> placeholders = new HashMap<>();
                        placeholders.put("%bypass%", sender.hasPermission("crazyenchantments.bypass.limit") + "");
                        placeholders.put("%limit%", ce.getPlayerMaxEnchantments((Player) sender) + "");
                        placeholders.put("%vanilla%", ce.checkVanillaLimit() + "");
                        placeholders.put("%item%", ce.getEnchantmentAmount(Methods.getItemInHand((Player) sender)) + "");
                        sender.sendMessage(Messages.LIMIT_COMMAND.getMessage(placeholders));
                    }
                    return true;
                case "debug":// /ce debug
                    if (hasPermission(sender, "debug")) {
                        List<String> brokenEnchantments = new ArrayList<>();
                        List<String> brokenEnchantmentTypes = new ArrayList<>();
                        for (CEnchantments enchantment : CEnchantments.values()) {
                            if (!Files.ENCHANTMENTS.getFile().contains("Enchantments." + enchantment.getName())) {
                                brokenEnchantments.add(enchantment.getName());
                            }
                            if (enchantment.getType() == null) brokenEnchantmentTypes.add(enchantment.getName());
                        }
                        if (brokenEnchantments.isEmpty() && brokenEnchantmentTypes.isEmpty()) {
                            sender.sendMessage(Methods.getPrefix("&aAll enchantments are loaded."));
                        } else {
                            if (!brokenEnchantments.isEmpty()) {
                                int i = 1;
                                sender.sendMessage(Methods.getPrefix("&cMissing Enchantments:"));
                                sender.sendMessage(Methods.getPrefix("&7These enchantments are broken due to one of the following reasons:"));
                                for (String broke : brokenEnchantments) {
                                    sender.sendMessage(Methods.color("&c#" + i + ": &6" + broke));
                                    i++;
                                }
                                sender.sendMessage(Methods.color("&7- &cMissing from the Enchantments.yml"));
                                sender.sendMessage(Methods.color("&7- &c<Enchantment Name>: option was changed"));
                                sender.sendMessage(Methods.color("&7- &cYaml format has been broken."));
                            }
                            if (!brokenEnchantmentTypes.isEmpty()) {
                                int i = 1;
                                sender.sendMessage(Methods.getPrefix("&cEnchantments with null types:"));
                                sender.sendMessage(Methods.getPrefix("&7These enchantments are broken due to the enchantment type being null."));
                                for (String broke : brokenEnchantmentTypes) {
                                    sender.sendMessage(Methods.color("&c#" + i + ": &6" + broke));
                                    i++;
                                }
                            }
                        }
                        sender.sendMessage(Methods.getPrefix("&cEnchantment Types and amount of items in each:"));
                        for (EnchantmentType enchantmentType : InfoMenuManager.getInstance().getEnchantmentTypes()) {
                            sender.sendMessage(Methods.color("&c" + enchantmentType.getName() + ": &6" + enchantmentType.getEnchantableMaterials().size()));
                        }
                    }
                    return true;
                case "fix":// /ce fix
                    if (hasPermission(sender, "fix")) {
                        List<CEnchantments> brokenEnchantments = new ArrayList<>();
                        FileConfiguration file = Files.ENCHANTMENTS.getFile();
                        for (CEnchantments enchantment : CEnchantments.values()) {
                            if (!file.contains("Enchantments." + enchantment.getName())) {
                                brokenEnchantments.add(enchantment);
                            }
                        }
                        sender.sendMessage(Methods.color("&7Fixed a total of " + brokenEnchantments.size() + " enchantments."));
                        for (CEnchantments enchantment : brokenEnchantments) {
                            String path = "Enchantments." + enchantment.getName();
                            file.set(path + ".Enabled", true);
                            file.set(path + ".Name", enchantment.getName());
                            file.set(path + ".Color", "&7");
                            file.set(path + ".BookColor", "&b&l");
                            file.set(path + ".MaxPower", 1);
                            file.set(path + ".Enchantment-Type", enchantment.getType().getName());
                            file.set(path + ".Info.Name", "&e&l" + enchantment.getName() + " &7(&bI&7)");
                            file.set(path + ".Info.Description", enchantment.getDiscription());
                            List<String> categories = new ArrayList<>();
                            ce.getCategories().forEach(category -> categories.add(category.getName()));
                            file.set(path + ".Categories", categories);
                            Files.ENCHANTMENTS.saveFile();
                        }
                    }
                    return true;
                case "info":// /ce info [enchantment]
                    if (hasPermission(sender, "info")) {
                        if (args.length == 1) {
                            if (!isPlayer) {
                                sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                                return true;
                            }
                            ce.getInfoMenuManager().openInfoMenu((Player) sender);
                        } else {
                            EnchantmentType enchantmentType = ce.getInfoMenuManager().getFromName(args[1]);
                            if (enchantmentType != null) {
                                ce.getInfoMenuManager().openInfoMenu((Player) sender, enchantmentType);
                                return true;
                            }
                            CEnchantment enchantment = ce.getEnchantmentFromName(args[1]);
                            if (enchantment != null) {
                                sender.sendMessage(enchantment.getInfoName());
                                enchantment.getInfoDescription().forEach(sender :: sendMessage);
                                return true;
                            }
                            sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
                        }
                    }
                    return true;
                case "spawn":// /ce spawn <enchantment> [level:#/world:<world>/x:#/y:#/z:#]
                    if (hasPermission(sender, "spawn")) {
                        if (args.length >= 2) {
                            CEnchantment enchantment = ce.getEnchantmentFromName(args[1]);
                            Category category = ce.getCategory(args[1]);
                            Location location = isPlayer ? ((Player) sender).getLocation() : new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
                            int level = 1;
                            if (enchantment == null && category == null) {
                                sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
                                return true;
                            }
                            for (String optionString : args) {
                                try {
                                    String option = optionString.split(":")[0];
                                    String value = optionString.split(":")[1];
                                    boolean isInt = Methods.isInt(value);
                                    switch (option.toLowerCase()) {
                                        case "level":
                                            if (isInt) {
                                                level = Integer.parseInt(value);
                                            } else if (value.contains("-")) {
                                                level = Methods.getRandomNumber(value);
                                            }
                                            break;
                                        case "world":
                                            World world = Bukkit.getWorld(value);
                                            if (world != null) {
                                                location.setWorld(world);
                                            }
                                            break;
                                        case "x":
                                            if (isInt) {
                                                location.setX(Integer.parseInt(value));
                                            }
                                            break;
                                        case "y":
                                            if (isInt) {
                                                location.setY(Integer.parseInt(value));
                                            }
                                            break;
                                        case "z":
                                            if (isInt) {
                                                location.setZ(Integer.parseInt(value));
                                            }
                                            break;
                                        
                                    }
                                } catch (Exception ignore) {
                                }
                            }
                            location.getWorld().dropItemNaturally(location, category == null ? new CEBook(enchantment, level).buildBook() : category.getLostBook().getLostBook(category).build());
                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%World%", location.getWorld().getName());
                            placeholders.put("%X%", location.getBlockX() + "");
                            placeholders.put("%Y%", location.getBlockY() + "");
                            placeholders.put("%Z%", location.getBlockZ() + "");
                            sender.sendMessage(Messages.SPAWNED_BOOK.getMessage(placeholders));
                            return true;
                        }
                        sender.sendMessage(Methods.getPrefix() + Methods.color("&c/ce Spawn <Enchantment/Category> [(Level:#/Min-Max)/World:<World>/X:#/Y:#/Z:#]"));
                    }
                    return true;
                case "lostbook":// /ce lostbook <category> [amount] [player]
                case "lb":
                    if (hasPermission(sender, "lostbook")) {
                        if (args.length >= 2) {
                            if (args.length <= 3 && !isPlayer) {
                                sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                                return true;
                            }
                            int amount = 1;
                            Player player;
                            Category category = ce.getCategory(args[1]);
                            if (args.length >= 3) {
                                if (!Methods.isInt(args[2])) {
                                    sender.sendMessage(Messages.NOT_A_NUMBER.getMessage()
                                    .replace("%Arg%", args[2]).replace("%arg%", args[2]));
                                    return true;
                                }
                                amount = Integer.parseInt(args[2]);
                            }
                            if (args.length >= 4) {
                                if (!Methods.isPlayerOnline(args[3], sender)) {
                                    return true;
                                }
                                player = Methods.getPlayer(args[3]);
                            } else {
                                player = (Player) sender;
                            }
                            if (category != null) {
                                if (Methods.isInventoryFull(player)) {
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
                        sender.sendMessage(Methods.getPrefix() + Methods.color("&c/ce LostBook <Category> [Amount] [Player]"));
                    }
                    return true;
                case "scrambler":// /ce scrambler [amount] [player]
                case "s":
                    if (hasPermission(sender, "scrambler")) {
                        int amount = 1;
                        Player player;
                        if (args.length <= 2 && !isPlayer) {
                            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                            return true;
                        }
                        if (args.length >= 2) {
                            if (!Methods.isInt(args[1])) {
                                sender.sendMessage(Messages.NOT_A_NUMBER.getMessage()
                                .replace("%Arg%", args[1]).replace("%arg%", args[1]));
                                return true;
                            }
                            amount = Integer.parseInt(args[1]);
                        }
                        if (args.length >= 3) {
                            if (!Methods.isPlayerOnline(args[2], sender)) {
                                return true;
                            }
                            player = Methods.getPlayer(args[2]);
                        } else {
                            player = (Player) sender;
                        }
                        if (Methods.isInventoryFull(player)) {
                            sender.sendMessage(Messages.INVENTORY_FULL.getMessage());
                            return true;
                        }
                        player.getInventory().addItem(Scrambler.getScramblers(amount));
                        HashMap<String, String> placeholders = new HashMap<>();
                        placeholders.put("%Amount%", amount + "");
                        placeholders.put("%Player%", player.getName());
                        sender.sendMessage(Messages.GIVE_SCRAMBLER_CRYSTAL.getMessage(placeholders));
                        player.sendMessage(Messages.GET_SCRAMBLER.getMessage(placeholders));
                    }
                    return true;
                case "crystal":// /ce crystal [amount] [player]
                case "c":
                    if (hasPermission(sender, "crystal")) {
                        int amount = 1;
                        Player player;
                        if (args.length <= 2 && !isPlayer) {
                            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                            return true;
                        }
                        if (args.length >= 2) {
                            if (!Methods.isInt(args[1])) {
                                sender.sendMessage(Messages.NOT_A_NUMBER.getMessage()
                                .replace("%Arg%", args[1]).replace("%arg%", args[1]));
                                return true;
                            }
                            amount = Integer.parseInt(args[1]);
                        }
                        if (args.length >= 3) {
                            if (!Methods.isPlayerOnline(args[2], sender)) {
                                return true;
                            }
                            player = Methods.getPlayer(args[2]);
                        } else {
                            player = (Player) sender;
                        }
                        if (Methods.isInventoryFull(player)) {
                            sender.sendMessage(Messages.INVENTORY_FULL.getMessage());
                            return true;
                        }
                        player.getInventory().addItem(ProtectionCrystal.getCrystals(amount));
                        HashMap<String, String> placeholders = new HashMap<>();
                        placeholders.put("%Amount%", amount + "");
                        placeholders.put("%Player%", player.getName());
                        sender.sendMessage(Messages.GIVE_PROTECTION_CRYSTAL.getMessage(placeholders));
                        player.sendMessage(Messages.GET_PROTECTION_CRYSTAL.getMessage(placeholders));
                    }
                    return true;
                case "dust":// /ce Dust <Success/Destroy/Mystery> [Amount] [Player] [Percent]
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
                                if (!Methods.isInt(args[2])) {
                                    sender.sendMessage(Messages.NOT_A_NUMBER.getMessage()
                                    .replace("%Arg%", args[2]).replace("%arg%", args[2]));
                                    return true;
                                }
                                amount = Integer.parseInt(args[2]);
                            }
                            if (args.length >= 4) {
                                if (!Methods.isPlayerOnline(args[3], sender)) {
                                    return true;
                                }
                                player = Methods.getPlayer(args[3]);
                            } else {
                                if (!isPlayer) {
                                    sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                                    return true;
                                } else {
                                    player = (Player) sender;
                                }
                            }
                            if (args.length >= 5) {
                                if (!Methods.isInt(args[4])) {
                                    sender.sendMessage(Messages.NOT_A_NUMBER.getMessage()
                                    .replace("%Arg%", args[4]).replace("%arg%", args[4]));
                                    return true;
                                }
                                percent = Integer.parseInt(args[4]);
                            }
                            Dust dust = Dust.getFromName(args[1]);
                            if (dust != null) {
                                if (args.length >= 5) {
                                    player.getInventory().addItem(dust.getDust(percent, amount));
                                } else {
                                    player.getInventory().addItem(dust.getDust(amount));
                                }
                                HashMap<String, String> placeholders = new HashMap<>();
                                placeholders.put("%Amount%", amount + "");
                                placeholders.put("%Player%", player.getName());
                                switch (dust) {
                                    case SUCCESS_DUST:
                                        player.sendMessage(Messages.GET_SUCCESS_DUST.getMessage(placeholders));
                                        sender.sendMessage(Messages.GIVE_SUCCESS_DUST.getMessage(placeholders));
                                        break;
                                    case DESTROY_DUST:
                                        player.sendMessage(Messages.GET_DESTROY_DUST.getMessage(placeholders));
                                        sender.sendMessage(Messages.GIVE_DESTROY_DUST.getMessage(placeholders));
                                        break;
                                    case MYSTERY_DUST:
                                        player.sendMessage(Messages.GET_MYSTERY_DUST.getMessage(placeholders));
                                        sender.sendMessage(Messages.GIVE_MYSTERY_DUST.getMessage(placeholders));
                                        break;
                                }
                                return true;
                            }
                        }
                        sender.sendMessage(Methods.getPrefix() + Methods.color("&c/ce Dust <Success/Destroy/Mystery> <Amount> [Player] [Percent]"));
                    }
                    return true;
                case "scroll":// /ce scroll <scroll> [amount] [player]
                    if (hasPermission(sender, "scroll")) {
                        if (args.length >= 2) {
                            int i = 1;
                            String name = sender.getName();
                            if (args.length >= 3) {
                                if (!Methods.isInt(args[2])) {
                                    sender.sendMessage(Messages.NOT_A_NUMBER.getMessage()
                                    .replace("%Arg%", args[2]).replace("%arg%", args[2]));
                                    return true;
                                }
                                i = Integer.parseInt(args[2]);
                            }
                            if (args.length >= 4) {
                                name = args[3];
                                if (!Methods.isPlayerOnline(name, sender)) {
                                    return true;
                                }
                            } else {
                                if (!isPlayer) {
                                    sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                                    return true;
                                }
                            }
                            Scrolls scroll = Scrolls.getFromName(args[1]);
                            if (scroll != null) {
                                Methods.getPlayer(name).getInventory().addItem(scroll.getScroll(i));
                                return true;
                            }
                        }
                        sender.sendMessage(Methods.getPrefix() + Methods.color("&c/ce Scroll <White/Black/Transmog> [Amount] [Player]"));
                    }
                    return true;
                case "add":// /ce add <enchantment> [level]
                    if (hasPermission(sender, "add")) {
                        if (!isPlayer) {
                            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                            return true;
                        }
                        if (args.length >= 2) {
                            Player player = (Player) sender;
                            String level = "1";
                            if (args.length >= 3) {
                                if (!Methods.isInt(args[2])) {
                                    sender.sendMessage(Messages.NOT_A_NUMBER.getMessage().replace("%Arg%", args[2]).replace("%arg%", args[2]));
                                    return true;
                                }
                                level = args[2];
                            }
                            Enchantment vanillaEnchantment = Methods.getEnchantment(args[1]);
                            CEnchantment ceEnchantment = ce.getEnchantmentFromName(args[1]);
                            boolean isVanilla = vanillaEnchantment != null;
                            if (vanillaEnchantment == null && ceEnchantment == null) {
                                sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
                                return true;
                            }
                            if (Methods.getItemInHand(player).getType() == Material.AIR) {
                                sender.sendMessage(Messages.DOESNT_HAVE_ITEM_IN_HAND.getMessage());
                                return true;
                            }
                            if (isVanilla) {
                                ItemStack item = Methods.getItemInHand(player).clone();
                                item.addUnsafeEnchantment(vanillaEnchantment, Integer.parseInt(level));
                                Methods.setItemInHand(player, item);
                            } else {
                                Methods.setItemInHand(player, ce.addEnchantment(Methods.getItemInHand(player), ceEnchantment, Integer.parseInt(level)));
                            }
                            return true;
                        }
                        sender.sendMessage(Methods.getPrefix("&c/ce Add <Enchantment> [LvL]"));
                    }
                    return true;
                case "remove":// /ce remove <enchantment>
                    if (hasPermission(sender, "remove")) {
                        if (!isPlayer) {
                            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                            return true;
                        }
                        if (args.length >= 2) {
                            Player player = (Player) sender;
                            Enchantment vanillaEnchantment = Methods.getEnchantment(args[1]);
                            CEnchantment ceEnchantment = ce.getEnchantmentFromName(args[1]);
                            boolean isVanilla = vanillaEnchantment != null;
                            if (vanillaEnchantment == null && ceEnchantment == null) {
                                sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
                                return true;
                            }
                            if (Methods.getItemInHand(player).getType() == Material.AIR) {
                                sender.sendMessage(Messages.DOESNT_HAVE_ITEM_IN_HAND.getMessage());
                                return true;
                            }
                            ItemStack item = Methods.getItemInHand(player);
                            if (isVanilla) {
                                ItemStack clone = Methods.getItemInHand(player).clone();
                                clone.removeEnchantment(vanillaEnchantment);
                                Methods.setItemInHand(player, clone);
                                return true;
                            } else {
                                if (ce.hasEnchantment(item, ceEnchantment)) {
                                    Methods.setItemInHand(player, ce.removeEnchantment(item, ceEnchantment));
                                    HashMap<String, String> placeholders = new HashMap<>();
                                    placeholders.put("%Enchantment%", ceEnchantment.getCustomName());
                                    player.sendMessage(Messages.REMOVED_ENCHANTMENT.getMessage(placeholders));
                                    return true;
                                }
                            }
                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Enchantment%", args[1]);
                            sender.sendMessage(Messages.DOESNT_HAVE_ENCHANTMENT.getMessage(placeholders));
                        }
                        sender.sendMessage(Methods.getPrefix() + Methods.color("&c/ce Remove <Enchantment>"));
                    }
                    return true;
                case "book":// /ce book <enchantment> [level] [amount] [player]
                    if (hasPermission(sender, "book")) {
                        if (args.length >= 2) {
                            if (args.length == 2 && !isPlayer) {
                                sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                                return true;
                            }
                            CEnchantment enchantment = ce.getEnchantmentFromName(args[1]);
                            int level = 1;
                            int amount = 1;
                            Player player;
                            if (args.length >= 3) {
                                if (Methods.isInt(args[2])) {
                                    level = Integer.parseInt(args[2]);
                                } else if (args[2].contains("-")) {
                                    level = Methods.getRandomNumber(args[2]);
                                } else {
                                    sender.sendMessage(Messages.NOT_A_NUMBER.getMessage()
                                    .replace("%Arg%", args[2]).replace("%arg%", args[2]));
                                    return true;
                                }
                            }
                            if (args.length >= 4) {
                                if (!Methods.isInt(args[3])) {
                                    sender.sendMessage(Messages.NOT_A_NUMBER.getMessage()
                                    .replace("%Arg%", args[3]).replace("%arg%", args[3]));
                                    return true;
                                }
                                amount = Integer.parseInt(args[3]);
                            }
                            if (args.length >= 5) {
                                if (!Methods.isPlayerOnline(args[4], sender)) {
                                    return true;
                                }
                                player = Methods.getPlayer(args[4]);
                            } else {
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
                        sender.sendMessage(Methods.getPrefix() + Methods.color("&c/ce Book <Enchantment> [Lvl] [Amount] [Player]"));
                    }
                    return true;
                default:
                    sender.sendMessage(Methods.getPrefix("&cDo /ce Help for more info."));
                    return false;
            }
        }
    }
    
    private boolean hasPermission(CommandSender sender, String permission) {
        return Methods.hasPermission(sender, permission, true);
    }
    
}