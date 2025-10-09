package com.badbones69.crazyenchantments.paper.commands.features.admin.migration.interfaces;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.config.ConfigOptions;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class IEnchantMigration {

    protected final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    protected final CrazyInstance instance = this.plugin.getInstance();

    protected final ConfigOptions options = this.plugin.getOptions();

    protected final Starter starter = this.plugin.getStarter();

    protected final FusionPaper fusion = this.plugin.getFusion();

    protected final StringUtils utils = this.fusion.getStringUtils();

    protected final CommandSender sender;
    protected Player player = null;

    public IEnchantMigration(@NotNull final CommandSender sender) {
        this.sender = sender;

        if (this.sender instanceof Player human) {
            this.player = human;
        }
    }

    public abstract void run();
}