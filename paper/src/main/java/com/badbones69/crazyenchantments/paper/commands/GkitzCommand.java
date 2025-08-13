package com.badbones69.crazyenchantments.paper.commands;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.FileManager;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsMenu;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.CEPlayer;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class GkitzCommand implements CommandExecutor {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final Methods methods = this.starter.getMethods();

    @NotNull
    private final CrazyManager crazyManager = this.starter.getCrazyManager();
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        boolean isPlayer = sender instanceof Player;

        if (crazyManager.isGkitzEnabled()) {
            if (args.length == 0) {

                if (!isPlayer) {
                    sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                    return true;
                }

                Player player = (Player) sender;

                // Load it because if they are ever null.
                if (this.crazyManager.getCEPlayer(player) == null) this.crazyManager.loadCEPlayer(player);

                if (hasPermission(sender, "gkitz")) {
                    FileConfiguration gkitz = FileManager.Files.GKITZ.getFile();

                    player.openInventory(new KitsMenu(player, gkitz.getInt("Settings.GUI-Size"), gkitz.getString("Settings.Inventory-Name")).build().getInventory());
                }

            } else {
                if (args[0].equalsIgnoreCase("reset")) { // /gkitz reset <kit> [player]
                    if (hasPermission(sender, "reset")) {
                        Player player;
                        if (args.length < 2) {
                            sender.sendMessage(ColorUtils.getPrefix() + ColorUtils.color("&c/GKitz Reset <Kit> [Player]"));
                            return true;
                        }

                        GKitz kit = this.crazyManager.getGKitFromName(args[1]);

                        if (kit == null) {
                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Kit%", args[1]);
                            placeholders.put("%Gkit%", args[1]);
                            sender.sendMessage(Messages.NOT_A_GKIT.getMessage(placeholders));
                            return true;
                        }

                        if (args.length >= 3) {
                            if (!this.methods.isPlayerOnline(args[2], sender)) {
                                    return true;
                            } else {
                                player = this.methods.getPlayer(args[2]);
                            }
                        } else {
                            if (!isPlayer) {
                                sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
                                return true;
                            } else {
                                player = (Player) sender;
                            }
                        }

                        this.crazyManager.getCEPlayer(player).removeCoolDown(kit);
                        HashMap<String, String> placeholders = new HashMap<>();

                        placeholders.put("%Player%", player.getName());
                        placeholders.put("%Gkit%", kit.getName());
                        placeholders.put("%Kit%", kit.getName());
                        sender.sendMessage(Messages.RESET_GKIT.getMessage(placeholders));

                    }
                } else {
                    if (hasPermission(sender, "gkitz")) {
                        boolean adminGive = false; // An admin is giving the kit.
                        GKitz kit = this.crazyManager.getGKitFromName(args[0]);
                        Player player;

                        if (kit == null) {
                            HashMap<String, String> placeholders = new HashMap<>();
                            placeholders.put("%Kit%", args[0]);
                            placeholders.put("%Gkit%", args[0]);
                            sender.sendMessage(Messages.NOT_A_GKIT.getMessage(placeholders));
                            return true;
                        }

                        if (args.length >= 2) {
                            if (!this.methods.isPlayerOnline(args[1], sender)) {
                                return true;
                            } else {
                                if (hasPermission(sender, "crazyenchantments.gkitz.give")) {
                                    player = this.methods.getPlayer(args[1]); // Targeting a player.
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

                        CEPlayer cePlayer = this.crazyManager.getCEPlayer(player);
                        HashMap<String, String> placeholders = new HashMap<>();
                        placeholders.put("%Player%", player.getName());
                        placeholders.put("%Gkit%", kit.getName());
                        placeholders.put("%Kit%", kit.getName());

                        if (cePlayer.hasGkitPermission(kit) || adminGive) {
                            if (cePlayer.canUseGKit(kit) || adminGive) {
                                cePlayer.giveGKit(kit);
                                player.sendMessage(Messages.RECEIVED_GKIT.getMessage(placeholders));

                                if (adminGive) {
                                    sender.sendMessage(Messages.GIVEN_GKIT.getMessage(placeholders));
                                } else {
                                    cePlayer.addCoolDown(kit);
                                }
                            } else {
                                sender.sendMessage(ColorUtils.getPrefix() + cePlayer.getCoolDown(kit).getCoolDownLeft(Messages.STILL_IN_COOLDOWN.getMessage(placeholders)));
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
        return this.methods.hasPermission(sender, permission, true);
    }
}