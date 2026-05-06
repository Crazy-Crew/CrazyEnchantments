package com.badbones69.crazyenchantments.paper.commands.wrapper;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.commands.PaperCommand;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class BaseCommand extends PaperCommand {

    protected final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    protected final FusionPaper fusion = this.plugin.getFusion();

}
