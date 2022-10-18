package com.badbones69.crazyenchantments.commands;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.enums.Messages;
import com.badbones69.crazyenchantments.api.objects.CEPlayer;
import com.badbones69.crazyenchantments.api.objects.GKitz;
import com.badbones69.crazyenchantments.controllers.GKitzController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class GkitzCommand implements CommandExecutor {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final CrazyManager crazyManager = starter.getCrazyManager();
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        boolean isPlayer = sender instanceof Player;

        if (crazyManager.isGkitzEnabled()) {
            if (args.length == 0) {

                if (!isPlayer) {
                    sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                    return true;
                }

                // if (hasPermission(sender, "gkitz")) GKitzController.openGUI((Player) sender);

            } else {
                if (args[0].equalsIgnoreCase("reset")) { // /gkitz reset <kit> [player]
                    if (hasPermission(sender, "reset")) {
                        GKitz kit = crazyManager.getGKitFromName(args[1]);
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
                                if (!methods.isPlayerOnline(args[2], sender)) {
                                    return true;
                                } else {
                                    player = methods.getPlayer(args[2]);
                                }
                            } else {
                                if (!isPlayer) {
                                    sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                                    return true;
                                } else {
                                    player = (Player) sender;
                                }
                            }

                            crazyManager.getCEPlayer(player).removeCooldown(kit);
                            HashMap<String, String> placeholders = new HashMap<>();

                            placeholders.put("%Player%", player.getName());
                            placeholders.put("%Gkit%", kit.getName());
                            placeholders.put("%Kit%", kit.getName());

                            sender.sendMessage(Messages.RESET_GKIT.getMessage(placeholders));
                        } else {
                            sender.sendMessage(methods.getPrefix() + methods.color("&c/GKitz Reset <Kit> [Player]"));
                        }
                    }
                } else {
                    if (hasPermission(sender, "gkitz")) {
                        boolean adminGive = false; // An admin is giving the kit.
                        GKitz kit = crazyManager.getGKitFromName(args[0]);
                        Player player;

                        if (kit == null) {
                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Kit%", args[0]);
                            placeholders.put("%Gkit%", args[0]);
                            sender.sendMessage(Messages.NOT_A_GKIT.getMessage(placeholders));
                            return true;
                        }

                        if (args.length >= 2) {
                            if (!methods.isPlayerOnline(args[1], sender)) {
                                return true;
                            } else {
                                if (hasPermission(sender, "crazyenchantments.gkitz.give")) {
                                    player = methods.getPlayer(args[1]); // Targeting a player.
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
                                player = (Player) sender; // The player is the sender.
                            }
                        }

                        CEPlayer cePlayer = crazyManager.getCEPlayer(player);
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
                                sender.sendMessage(methods.getPrefix() + cePlayer.getCooldown(kit).getCooldownLeft(Messages.STILL_IN_COOLDOWN.getMessage(placeholders)));
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
        return methods.hasPermission(sender, permission, true);
    }
}