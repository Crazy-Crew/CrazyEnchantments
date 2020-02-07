package me.badbones69.crazyenchantments.commands;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.objects.CEPlayer;
import me.badbones69.crazyenchantments.api.objects.GKitz;
import me.badbones69.crazyenchantments.controllers.GKitzController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class GkitzCommand implements CommandExecutor {
    
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
        boolean isPlayer = sender instanceof Player;
        if (ce.isGkitzEnabled()) {
            if (args.length == 0) {
                if (!isPlayer) {
                    sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                    return true;
                }
                if (hasPermission(sender, "gkitz")) {
                    GKitzController.openGUI((Player) sender);
                }
            } else {
                if (args[0].equalsIgnoreCase("reset")) {// /GKitz reset <kit> [player]
                    if (hasPermission(sender, "reset")) {
                        GKitz kit = ce.getGKitFromName(args[1]);
                        Player player;
                        if (args.length >= 2) {
                            if (kit == null) {
                                HashMap<String, String> placeholders = new HashMap<>();
                                placeholders.put("%Kit%", args[1]);
                                placeholders.put("%Gkit%", args[1]);
                                sender.sendMessage(Messages.NOT_A_GKIT.getMessage(placeholders));
                                return true;
                            }
                            if (args.length >= 3) {
                                if (!Methods.isPlayerOnline(args[2], sender)) {
                                    return true;
                                } else {
                                    player = Methods.getPlayer(args[2]);
                                }
                            } else {
                                if (!isPlayer) {
                                    sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                                    return true;
                                } else {
                                    player = (Player) sender;
                                }
                            }
                            ce.getCEPlayer(player).removeCooldown(kit);
                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Player%", player.getName());
                            placeholders.put("%Gkit%", kit.getName());
                            placeholders.put("%Kit%", kit.getName());
                            sender.sendMessage(Messages.RESET_GKIT.getMessage(placeholders));
                        } else {
                            sender.sendMessage(Methods.getPrefix() + Methods.color("&c/GKitz Reset <Kit> [Player]"));
                        }
                    }
                } else {
                    if (hasPermission(sender, "gkitz")) {
                        boolean adminGive = false;// An admin is giving the kit.
                        GKitz kit = ce.getGKitFromName(args[0]);
                        Player player;
                        if (kit == null) {
                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Kit%", args[0]);
                            placeholders.put("%Gkit%", args[0]);
                            sender.sendMessage(Messages.NOT_A_GKIT.getMessage(placeholders));
                            return true;
                        }
                        if (args.length >= 2) {
                            if (!Methods.isPlayerOnline(args[1], sender)) {
                                return true;
                            } else {
                                if (hasPermission(sender, "crazyenchantments.gkitz.give")) {
                                    player = Methods.getPlayer(args[1]);// Targeting a player.
                                    adminGive = true;
                                } else {
                                    return true;
                                }
                            }
                        } else {
                            if (!isPlayer) {
                                sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                                return true;
                            } else {
                                player = (Player) sender;// The player is the sender.
                            }
                        }
                        CEPlayer cePlayer = ce.getCEPlayer(player);
                        HashMap<String, String> placeholders = new HashMap<>();
                        placeholders.put("%Player%", player.getName());
                        placeholders.put("%Kit%", kit.getDisplayItem().getItemMeta().getDisplayName());
                        if (cePlayer.hasGkitPermission(kit) || adminGive) {
                            if (cePlayer.canUseGKit(kit) || adminGive) {
                                cePlayer.giveGKit(kit);
                                player.sendMessage(Messages.RECEIVED_GKIT.getMessage(placeholders));
                                if (adminGive) {
                                    sender.sendMessage(Messages.GIVEN_GKIT.getMessage(placeholders));
                                } else {
                                    cePlayer.addCooldown(kit);
                                }
                            } else {
                                sender.sendMessage(Methods.getPrefix() + cePlayer.getCooldown(kit).getCooldownLeft(Messages.STILL_IN_COOLDOWN.getMessage(placeholders)));
                                return true;
                            }
                        } else {
                            sender.sendMessage(Messages.NO_GKIT_PERMISSION.getMessage(placeholders));
                            return true;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    private boolean hasPermission(CommandSender sender, String permission) {
        return Methods.hasPermission(sender, permission, true);
    }
    
}