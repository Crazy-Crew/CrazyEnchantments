package me.badbones69.crazyenchantments.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager;
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
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@CommandAlias("ace|acrazyenchantments|aenchanter")
public class CECommandACF extends BaseCommand {
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    private FileManager fileManager = FileManager.getInstance();

    /**
     * the default implementation of /ce with no arguments
     *
     * @param player the player to open the gui of
     */
    @Default
    @CommandPermission("gui")
    @Description("&9Opens up the menu")
    public void onCommand(Player player) {
        ShopControl.openGUI(player);
    }

    @Subcommand("help")
    @CommandPermission("access")
    @Description("&9Shows all crazy enchantment commands.")
    public void help(CommandSender sender) {
        sender.sendMessage(Messages.HELP.getMessage());
    }

    @Subcommand("reload")
    @CommandPermission("reload")
    @Description("&9Reloads all of the configuration files.")
    public void reload(CommandSender sender) {
        ce.getCEPlayers().forEach(cePlayer -> ce.backupCEPlayer(cePlayer));
        fileManager.setup(ce.getPlugin());
        ce.load();
        sender.sendMessage(Messages.CONFIG_RELOAD.getMessage());
    }

    @Subcommand("limit")
    @CommandPermission("limit")
    public void limit(Player sender) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%bypass%", sender.hasPermission("crazyenchantments.bypass.limit") + "");
        placeholders.put("%limit%", ce.getPlayerMaxEnchantments(sender) + "");
        placeholders.put("%vanilla%", ce.checkVanillaLimit() + "");
        placeholders.put("%item%", ce.getEnchantmentAmount(Methods.getItemInHand(sender)) + "");
        sender.sendMessage(Messages.LIMIT_COMMAND.getMessage(placeholders));
    }

    @Subcommand("debug")
    @CommandPermission("debug")
    @Description("&9Does a small debug for some errors.")
    public void debug(CommandSender sender) {
        List<String> brokenEnchantments = new ArrayList<>();
        List<String> brokenEnchantmentTypes = new ArrayList<>();
        for (CEnchantments enchantment : CEnchantments.values()) {
            if (!FileManager.Files.ENCHANTMENTS.getFile().contains("Enchantments." + enchantment.getName())) {
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

    @Subcommand("fix")
    @CommandPermission("fix")
    public void fix(CommandSender sender) {
        List<CEnchantments> brokenEnchantments = new ArrayList<>();
        FileConfiguration file = FileManager.Files.ENCHANTMENTS.getFile();
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
            FileManager.Files.ENCHANTMENTS.saveFile();
        }
    }

    @Subcommand("info")
    @CommandPermission("info")
    @Description("&9Shows info on all enchantments.")
    public boolean info(Player player, @Optional String enchantmentTypeString) {
        if (enchantmentTypeString == null) {
            // the argument doesn't exist
            ce.getInfoMenuManager().openInfoMenu(player);
            return true;
        } else {
            // the argument exists
            EnchantmentType enchantmentType = ce.getInfoMenuManager().getFromName(enchantmentTypeString);
            if (enchantmentType == null) {
                CEnchantment enchantment = ce.getEnchantmentFromName(enchantmentTypeString);
                if (enchantment == null) {
                    player.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
                    return false;
                } else {
                    player.sendMessage(enchantment.getInfoName());
                    enchantment.getInfoDescription().forEach(player::sendMessage);
                    return true;
                }
            } else {
                // enchantmentType exists
                ce.getInfoMenuManager().openInfoMenu(player, enchantmentType);
                return true;
            }
        }
    }

    @Subcommand("spawn")
    @CommandPermission("spawn")
    @Description("&9Drops an enchantment book at the specific coordinates.")
    public boolean spawn(CommandSender sender, @Optional Player player, @Optional @Single @Name("enchantment") String enchantmentArg, @Optional @Split @Name("[(level:#/min-max)/world:<world>/x:#/y:#/z:#]") String[] args) {
        CEnchantment enchantment = ce.getEnchantmentFromName(enchantmentArg);
        Category category = ce.getCategory(enchantmentArg);
        World world = Bukkit.getWorlds().get(0);
        Location location = player == null ? new Location(world, 0, 0, 0) : player.getLocation();
        int level = 1;
        if (enchantment == null && category == null) {
            sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
            return true;
        }
        for (String optionString : args) {
            try {
                String[] optionStringSplit = optionString.split(":");
                if (optionStringSplit.length != 2) continue;
                String option = optionStringSplit[0];
                String value = optionStringSplit[1];
                boolean isInt = Methods.isInt(value);
                switch (option.toLowerCase()) {
                    case "level":
                        if (isInt) {
                            level = Integer.parseInt(value);
                        } else if (value.contains("-")) {
                            level = Methods.getRandomNumber(value);
                        }
                        break;
                    case "worldProvided":
                        World worldProvided = Bukkit.getWorld(value);
                        if (worldProvided != null) {
                            location.setWorld(worldProvided);
                            world = worldProvided;
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
        world.dropItemNaturally(location, category == null ? new CEBook(enchantment, level).buildBook() : category.getLostBook().getLostBook(category).build());
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%World%", world.getName());
        placeholders.put("%X%", location.getBlockX() + "");
        placeholders.put("%Y%", location.getBlockY() + "");
        placeholders.put("%Z%", location.getBlockZ() + "");
        sender.sendMessage(Messages.SPAWNED_BOOK.getMessage(placeholders));
        return true;
    }

    @Subcommand("lostbook|lb")
    @CommandPermission("lostbook")
    @Description("&9Gives a player a lost book item.")
    public boolean lostbook(CommandSender sender, @Optional @Split @Name("enchantment") String[] args) {
        if (args != null && args.length != 0) {
            if (args.length <= 2 && !(sender instanceof Player)) {
                sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                return false;
            }
            int amount = 1;
            Player player;
            Category category = ce.getCategory(args[0]);
            if (args.length >= 2) {
                if (!Methods.isInt(args[1])) {
                    sender.sendMessage(Messages.NOT_A_NUMBER.getMessage()
                            .replace("%Arg%", args[1]).replace("%arg%", args[2]));
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
            if (category != null) {
                if (Methods.isInventoryFull(player)) {
                    player.getWorld().dropItemNaturally(player.getLocation(), category.getLostBook().getLostBook(category, amount).build());
                } else {
                    player.getInventory().addItem(category.getLostBook().getLostBook(category, amount).build());
                }
                return true;
            }
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%Category%", args[0]);
            sender.sendMessage(Messages.NOT_A_CATEGORY.getMessage(placeholders));
            return true;
        }
        sender.sendMessage(Methods.getPrefix() + Methods.color("&c/ce LostBook <Category> [Amount] [Player]"));
        return false;
    }

    @Subcommand("scrambler|s")
    @CommandPermission("scrambler")
    @Description("&9Gives a player a Scrambler item.")
    public boolean scrambler(CommandSender sender,
                             @Optional Player player,
                             @Name("amount") @Optional Integer amount,
                             @Name("player") @Optional OnlinePlayer playerProvided) {
        if (playerProvided != null) player = playerProvided.getPlayer();
        if (player == null) {
            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
            return false;
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
        return true;
    }

    @Subcommand("crystal|c")
    @CommandPermission("crystal")
    @Description("&9Gives a player a Protection Crystal item.")
    public boolean crystal(CommandSender sender,
                           @Optional Player player,
                           @Name("amount") @Optional Integer amount,
                           @Name("player") @Optional OnlinePlayer playerProvided) {
        if (playerProvided != null) player = playerProvided.getPlayer();
        if (player == null) {
            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
            return false;
        }
        if (Methods.isInventoryFull(player)) {
            sender.sendMessage(Messages.INVENTORY_FULL.getMessage());
            return false;
        }
        player.getInventory().addItem(ProtectionCrystal.getCrystals(amount));
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%Amount%", amount + "");
        placeholders.put("%Player%", player.getName());
        sender.sendMessage(Messages.GIVE_PROTECTION_CRYSTAL.getMessage(placeholders));
        player.sendMessage(Messages.GET_PROTECTION_CRYSTAL.getMessage(placeholders));
        return true;
    }

    @Subcommand("dust")
    @CommandPermission("dust")
    @Description("&9Give a player a dust item.")
    public boolean dust(CommandSender sender,
                        @Optional Player player,
                        @Values("Success|Destroy|Mystery") String dustType,
                        @Default(value = "1") int amount,
                        @Optional OnlinePlayer playerProvided,
                        @Optional Integer percent) {
        if (playerProvided != null) player = playerProvided.getPlayer();
        if (player == null) {
            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
            return false;
        }


        Dust dust = Dust.getFromName(dustType);
        if (dust != null) {
            player.getInventory().addItem(percent == null ? dust.getDust(amount) : dust.getDust(percent, amount));
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
        sender.sendMessage(Methods.getPrefix() + Methods.color("&c/ce Dust <Success/Destroy/Mystery> <Amount> [Player] [Percent]"));
        return false;
    }

    @Subcommand("scroll")
    @CommandPermission("scroll")
    @Description("&9Gives a player a scroll item.")
    public boolean scroll(CommandSender sender,
                          @Optional Player player,
                          @Values("White|Black|Transmog") String dustType,
                          @Default(value = "1") int amount,
                          @Optional OnlinePlayer playerProvided,
                          @Optional Integer percent) {
        if (playerProvided != null) player = playerProvided.getPlayer();
        if (player == null) {
            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
            return false;
        }
        Scrolls scroll = Scrolls.getFromName(dustType);
        if (scroll != null) {
            player.getInventory().addItem(scroll.getScroll(amount));
            return true;
        }
        sender.sendMessage(Methods.getPrefix() + Methods.color("&c/ce Scroll <White/Black/Transmog> [Amount] [Player]"));
        return false;
    }

    @Subcommand("add")
    @CommandPermission("add")
    @CommandCompletion("@enchantments level player")
    @Description("&9Adds an enchantment to the item in your hand.")
    public boolean add(CommandSender sender,
                       @Optional Player player,
                       @Name("enchantment") @Single String enchantment,
                       @Name("level") @Default(value = "1") int level,
                       @Name("player") @Optional OnlinePlayer playerProvided) {
        if (playerProvided != null) player = playerProvided.getPlayer();
        if (player == null) {
            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
            return false;
        }
        Enchantment vanillaEnchantment = Methods.getEnchantment(enchantment);
        CEnchantment ceEnchantment = ce.getEnchantmentFromName(enchantment);
        if (vanillaEnchantment == null && ceEnchantment == null) {
            sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
            return true;
        }
        if (Methods.getItemInHand(player).getType() == Material.AIR) {
            sender.sendMessage(Messages.DOESNT_HAVE_ITEM_IN_HAND.getMessage());
            return true;
        }
        if (vanillaEnchantment == null) {
            Methods.setItemInHand(player, ce.addEnchantment(Methods.getItemInHand(player), ceEnchantment, level));
        } else {
            ItemStack item = Methods.getItemInHand(player).clone();
            item.addUnsafeEnchantment(vanillaEnchantment, level);
            Methods.setItemInHand(player, item);
        }
        sender.sendMessage(Methods.getPrefix("&c/ce Add <Enchantment> [LvL]"));
        return false;
    }

    @Subcommand("remove")
    @CommandPermission("remove")
    @Description("&9Removes an enchantment from the item in your hand.")
    public boolean remove(CommandSender sender,
                          @Optional Player player,
                          @Name("enchantment") @Single String enchantment,
                          @Name("level") @Default(value = "1") int level,
                          @Name("player") @Optional OnlinePlayer playerProvided) {
        if (playerProvided != null) player = playerProvided.getPlayer();
        if (player == null) {
            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
            return false;
        }
        Enchantment vanillaEnchantment = Methods.getEnchantment(enchantment);
        CEnchantment ceEnchantment = ce.getEnchantmentFromName(enchantment);
        if (vanillaEnchantment == null && ceEnchantment == null) {
            sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
            return false;
        }
        if (Methods.getItemInHand(player).getType() == Material.AIR) {
            sender.sendMessage(Messages.DOESNT_HAVE_ITEM_IN_HAND.getMessage());
            return false;
        }
        ItemStack item = Methods.getItemInHand(player);
        if (vanillaEnchantment != null) {
            ItemStack clone = Methods.getItemInHand(player).clone();
            clone.removeEnchantment(vanillaEnchantment);
            Methods.setItemInHand(player, clone);
            return true;
        } else if (ce.hasEnchantment(item, ceEnchantment)) {
            Methods.setItemInHand(player, ce.removeEnchantment(item, ceEnchantment));
            HashMap<String, String> placeholders = new HashMap<>();
            placeholders.put("%Enchantment%", ceEnchantment.getCustomName());
            player.sendMessage(Messages.REMOVED_ENCHANTMENT.getMessage(placeholders));
            return true;
        }
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%Enchantment%", enchantment);
        sender.sendMessage(Messages.DOESNT_HAVE_ENCHANTMENT.getMessage(placeholders));
        return false;
    }

    @Subcommand("book")
    @CommandPermission("book")
    @Description("&9Gives a player an enchantment Book.")
    public boolean book(CommandSender sender,
                        @Optional Player player,
                        @Name("enchantment") @Single String enchantmentArg,
                        @Name("level") @Default(value = "1") int level,
                        @Name("amount") @Default(value = "1") int amount,
                        @Name("player") @Optional OnlinePlayer playerProvided) {
        if (playerProvided != null) player = playerProvided.getPlayer();
        if (player == null) {
            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
            return false;
        }
        CEnchantment enchantment = ce.getEnchantmentFromName(enchantmentArg);
        if (enchantment == null) {
            sender.sendMessage(Messages.NOT_AN_ENCHANTMENT.getMessage());
            return false;
        }
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%Player%", player.getName());
        sender.sendMessage(Messages.SEND_ENCHANTMENT_BOOK.getMessage(placeholders));
        player.getInventory().addItem(new CEBook(enchantment, level, amount).buildBook());
        return true;
    }
}