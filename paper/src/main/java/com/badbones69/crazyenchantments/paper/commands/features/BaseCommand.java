package com.badbones69.crazyenchantments.paper.commands.features;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.FileManager;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.paper.listeners.ScramblerListener;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.plugin.java.JavaPlugin;

@Command(value = "crazyenchantments", alias = {"ce"})
public class BaseCommand {

    protected final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    protected final Starter starter = this.plugin.getStarter();

    protected final FileManager fileManager = this.starter.getFileManager();

    protected final Methods methods = this.starter.getMethods();

    protected final CrazyManager crazyManager = this.starter.getCrazyManager();

    // Settings.
    protected final ProtectionCrystalSettings protectionCrystalSettings = this.starter.getProtectionCrystalSettings();
    protected final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    // Listeners
    protected final ScramblerListener scramblerListener = this.starter.getScramblerListener();

}