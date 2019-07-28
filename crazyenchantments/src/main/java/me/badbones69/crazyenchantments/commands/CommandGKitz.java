package me.badbones69.crazyenchantments.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.SettingsManager;
import me.badbones69.crazyenchantments.api.CEPlayer;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.GKitz;
import me.badbones69.crazyenchantments.controlers.GKitzControler;

public class CommandGKitz implements CommandExecutor {

	private FileConfiguration msg = settings.getMessages();
	private static SettingsManager settings = Main.settings;
	private CrazyEnchantments CE = Main.CE;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {

		if(args.length == 0) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
				return true;
			}
			if(!Methods.hasPermission(sender, "access", true)) return true;
			GKitzControler.openGUI((Player) sender);
			return true;
		}
		if(args.length >= 1) {
			GKitz kit = null;
			Player player = null;
			if(args[0].equalsIgnoreCase("reset")) {// /GKitz Reset <Kit> [Player]
				if(!Methods.hasPermission(sender, "reset", true)) return true;
				if(args.length >= 2) {
					if(CE.getGKitFromName(args[1]) != null) {
						kit = CE.getGKitFromName(args[1]);
					}else {
						sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Not-A-GKit").replaceAll("%Kit%", args[1]).replaceAll("%kit%", args[1])));
						return true;
					}
					if(args.length >= 3) {
						if(!Methods.isOnline(args[2], sender)) {
							return true;
						}else {
							player = Methods.getPlayer(args[2]);
						}
					}else {
						if(!(sender instanceof Player)) {
							sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
							return true;
						}else {
							player = (Player) sender;
						}
					}
					CE.getCEPlayer(player).removeCooldown(kit);
					sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Reset-GKit").replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName()).replaceAll("%GKit%", kit.getName()).replaceAll("%gkit%", kit.getName())));
					return true;
				}else {
					sender.sendMessage(Methods.getPrefix() + Methods.color("&c/GKitz Reset <Kit> [Player]"));
					return true;
				}
			}else {
				if(CE.getGKitFromName(args[0]) != null) {// /GKitz [Kit] [Player]
					kit = CE.getGKitFromName(args[0]);
				}else {
					sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Not-A-GKit").replaceAll("%Kit%", args[0]).replaceAll("%kit%", args[0])));
					return true;
				}
				if(args.length >= 2) {
					if(!Methods.hasPermission(sender, "gkitz", true)) {
						return true;
					}else {
						if(!Methods.isOnline(args[1], sender)) {
							return true;
						}else {
							player = Methods.getPlayer(args[1]);
						}
					}
				}else {
					if(!(sender instanceof Player)) {
						sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
						return true;
					}else {
						player = (Player) sender;
					}
				}
				CEPlayer p = CE.getCEPlayer(player);
				String name = kit.getDisplayItem().getItemMeta().getDisplayName();
				if(p.hasGkitPermission(kit) || args.length >= 2) {
					if(p.canUseGKit(kit) || sender.hasPermission("crazyenchantments.admin")) {
						p.giveGKit(kit);
						player.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Received-GKit").replaceAll("%Kit%", name).replaceAll("%kit%", name)));
						if(!player.getName().equalsIgnoreCase(sender.getName())) {
							sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Given-GKit").replaceAll("%Kit%", name).replaceAll("%kit%", name).replaceAll("%Player%", player.getName()).replaceAll("%player%", player.getName())));
						}
						if(args.length == 1) {
							p.addCooldown(kit);
						}
					}else {
						sender.sendMessage(Methods.getPrefix() + p.getCooldown(kit).getCooldownLeft(msg.getString("Messages.Still-In-Cooldown")).replaceAll("%Kit%", name).replaceAll("%kit%", name));
						return true;
					}
				}else {
					sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.No-GKit-Permission").replaceAll("%Kit%", kit.getName()).replaceAll("%kit%", kit.getName())));
					return true;
				}
				return true;
			}
		}
		sender.sendMessage(Methods.getPrefix() + Methods.color("&c/GKitz [Kit] [Player]"));
		return true;
	
	}
}
