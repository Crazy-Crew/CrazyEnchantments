package com.badbones69.crazyenchantments.paper;

import com.badbones69.crazyenchantments.ConfigManager;
import com.badbones69.crazyenchantments.paper.controllers.BossBarController;
import com.badbones69.crazyenchantments.paper.platform.CommandManager;
import com.badbones69.crazyenchantments.paper.platform.PaperServer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyEnchantments extends JavaPlugin {

    protected final PluginManager pluginManager = getServer().getPluginManager();

    @Override
    public void onLoad() {
        ConfigManager.load(getDataFolder());
    }

    private Starter starter;

    @Override
    public void onEnable() {
        new PaperServer(this);

        this.starter = new Starter();
        this.starter.run();

        CommandManager.load();
    }

    @Override
    public void onDisable() {

    }

    public Starter getStarter() {
        return this.starter;
    }

    public BossBarController getBossBarController() {
        return null;
    }

    public boolean isLogging() {
        return true;
    }
}