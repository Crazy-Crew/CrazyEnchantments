package me.badbones69.crazyenchantments.commands;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.controllers.BlackSmith;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BlackSmithCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Messages.PLAYERS_ONLY.getMessage());
			return true;
		}
		if(!Methods.hasPermission(sender, "blacksmith", true)) {
			return true;
		}
		BlackSmith.openBlackSmith((Player) sender);
		return true;
	}
	
}