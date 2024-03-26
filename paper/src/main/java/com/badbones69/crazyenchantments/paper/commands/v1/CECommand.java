package com.badbones69.crazyenchantments.paper.commands.v1;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.ShopMenu;
import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.badbones69.crazyenchantments.paper.api.managers.ShopManager;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.other.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.api.utils.NumberUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.paper.listeners.ScramblerListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

//TODO() Update commands
public class CECommand implements CommandExecutor {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Starter starter = this.plugin.getStarter();

    private final Methods methods = this.starter.getMethods();

    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    // Settings.
    private final ProtectionCrystalSettings protectionCrystalSettings = this.starter.getProtectionCrystalSettings();
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    // Listeners
    private final ScramblerListener scramblerListener = this.starter.getScramblerListener();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        boolean isPlayer = sender instanceof Player;

        if (args.length == 0) { // /ce
            if (!isPlayer) {
                sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                return true;
            }

            if (hasPermission(sender, "gui")) {
                Player player = (Player) sender;

                ShopManager shopManager = this.starter.getShopManager();

                player.openInventory(new ShopMenu(player, shopManager.getInventorySize(), shopManager.getInventoryName()).build().getInventory());
            }

            return true;
        }

        switch (args[0].toLowerCase()) {
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
                            if (!checkInt(sender, args[2])) return true;

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
                            if (!checkInt(sender, args[4])) return true;

                            percent = Integer.parseInt(args[4]);
                        }

                        Dust dust = Dust.getFromName(args[1]);

                        if (dust != null) {
                            this.methods.addItemToInventory(player, args.length >= 5 ? dust.getDust(percent, amount) : dust.getDust(amount));

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
                            if (!checkInt(sender, args[2])) return true;

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
                            this.methods.addItemToInventory(this.methods.getPlayer(name), scroll.getScroll(amount));
                            return true;
                        }
                    }

                    sender.sendMessage(ColorUtils.getPrefix() + ColorUtils.color("&c/ce Scroll <White/Black/Transmog> [Amount] [Player]"));
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
                            if (!checkInt(sender, args[2])) return true;

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
                            if (this.enchantmentBookSettings.getEnchantments(item).containsKey(ceEnchantment)) {
                                this.methods.setItemInHand(player, this.enchantmentBookSettings.removeEnchantment(item, ceEnchantment));
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

                    sender.sendMessage(ColorUtils.getPrefix() + ColorUtils.color("&c/ce Remove <Enchantment>"));
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

                        CEnchantment enchantment = this.crazyManager.getEnchantmentFromName(args[1]);
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
                            if (!checkInt(sender, args[3])) return true;

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
                        this.methods.addItemToInventory(player, new CEBook(enchantment, level, amount).buildBook());
                        return true;
                    }

                    sender.sendMessage(ColorUtils.getPrefix() + ColorUtils.color("&c/ce Book <Enchantment> [Lvl] [Amount] [Player]"));
                }

                return true;
            }

            default -> {
                sender.sendMessage(ColorUtils.getPrefix("&cDo /ce help for more info."));
                return false;
            }
        }
    }

    private boolean checkInt(CommandSender sender, String arg) {
        if (NumberUtils.isInt(arg)) return true;

        sender.sendMessage(Messages.NOT_A_NUMBER.getMessage().replace("%Arg%", arg).replace("%arg%", arg));
        return false;

    }

    private boolean hasPermission(CommandSender sender, String permission) {
        return this.methods.hasPermission(sender, permission, true);
    }
}