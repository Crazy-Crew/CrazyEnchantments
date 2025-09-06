package com.badbones69.crazyenchantments.paper.commands.v2.features.admin.validation;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class IEnchantValidator {

    protected final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    protected final Starter starter = this.plugin.getStarter();

    protected final FusionPaper fusion = this.plugin.getFusion();

    protected final StringUtils utils = this.fusion.getStringUtils();

    protected final EnchantmentBookSettings settings = this.starter.getEnchantmentBookSettings();

    protected final CommandSender sender;
    protected Player player = null;

    public IEnchantValidator(@NotNull final CommandSender sender) {
        this.sender = sender;

        if (this.sender instanceof Player human) {
            this.player = human;
        }
    }

    public abstract void run();
}