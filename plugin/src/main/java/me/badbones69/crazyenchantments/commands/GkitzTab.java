package me.badbones69.crazyenchantments.commands;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class GkitzTab implements TabCompleter {
    
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String commandLable, String[] args) {
        List<String> completions = new ArrayList<>();
        if (sender.hasPermission("crazyenchantments.reset")) {
            if (args.length == 1) {// /gkit
                completions.add("reset");
                ce.getGKitz().forEach(kit -> completions.add(kit.getName()));
                return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
            } else if (args.length == 2) {// /gkit reset
                if (args[0].equalsIgnoreCase("reset")) {
                    ce.getGKitz().forEach(kit -> completions.add(kit.getName()));
                }
                if (ce.getGKitFromName(args[0]) != null) {
                    Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
                }
                return StringUtil.copyPartialMatches(args[1], completions, new ArrayList<>());
            } else {// /gkit reset <kit>
                if (args[0].equalsIgnoreCase("reset")) {
                    Bukkit.getOnlinePlayers().forEach(player -> completions.add(player.getName()));
                }
                return StringUtil.copyPartialMatches(args[2], completions, new ArrayList<>());
            }
        }
        return completions;
    }
    
}