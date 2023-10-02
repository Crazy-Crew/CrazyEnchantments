package com.badbones69.crazyenchantments.paper.commands;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class GkitzTab implements TabCompleter {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final CrazyManager crazyManager = starter.getCrazyManager();
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();
        if (sender.hasPermission("crazyenchantments.reset")) {
            if (args.length == 1) { // /gkit
                completions.add("reset");
                crazyManager.getGKitz().forEach(kit -> completions.add(kit.getName()));

                return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
            } else if (args.length == 2) { // /gkit reset

                if (args[0].equalsIgnoreCase("reset")) crazyManager.getGKitz().forEach(kit -> completions.add(kit.getName()));

                if (crazyManager.getGKitFromName(args[0]) != null) plugin.getServer().getOnlinePlayers().forEach(player -> completions.add(player.getName()));

                return StringUtil.copyPartialMatches(args[1], completions, new ArrayList<>());
            } else { // /gkit reset <kit>

                if (args[0].equalsIgnoreCase("reset")) plugin.getServer().getOnlinePlayers().forEach(player -> completions.add(player.getName()));

                return StringUtil.copyPartialMatches(args[2], completions, new ArrayList<>());
            }
        }

        return completions;
    }
}