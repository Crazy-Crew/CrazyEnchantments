package com.badbones69.crazyenchantments.paper.commands.features;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.managers.configs.ConfigManager;
import com.badbones69.crazyenchantments.paper.managers.CategoryManager;
import com.badbones69.crazyenchantments.paper.managers.items.ItemManager;
import com.ryderbelserion.fusion.paper.FusionPaper;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.plugin.java.JavaPlugin;
import java.nio.file.Path;

@Command(value = "crazyenchantments", alias = {"ce"})
public class BaseCommand {

    protected final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    protected final CategoryManager categoryManager = this.plugin.getCategoryManager();

    protected final ItemManager itemManager = this.plugin.getItemManager();

    protected final CrazyInstance instance = this.plugin.getInstance();

    protected final ConfigManager options = this.plugin.getConfigManager();

    protected final FusionPaper fusion = this.plugin.getFusion();

    protected final Path path = this.plugin.getDataPath();

    protected final Methods methods = null;

    protected final CrazyManager crazyManager = null;

}