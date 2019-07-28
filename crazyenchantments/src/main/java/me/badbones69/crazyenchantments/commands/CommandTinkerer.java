package me.badbones69.crazyenchantments.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.SettingsManager;
import me.badbones69.crazyenchantments.controlers.Tinkerer;

public class CommandTinkerer implements CommandExecutor {

	private FileConfiguration msg = settings.getMessages();
	private static SettingsManager settings = Main.settings;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Players-Only")));
			return true;
		}
		if(!Methods.hasPermission(sender, "tinker", true)) return true;
		Player player = (Player) sender;
		Tinkerer.openTinker(player);
		return true;
	}
}