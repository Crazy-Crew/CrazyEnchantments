package com.badbones69.crazyenchantments.paper;

import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.builders.types.BaseMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith.BlackSmithMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.tinkerer.TinkererMenu;
import com.badbones69.crazyenchantments.paper.commands.BlackSmithCommand;
import com.badbones69.crazyenchantments.paper.commands.CECommand;
import com.badbones69.crazyenchantments.paper.commands.CETab;
import com.badbones69.crazyenchantments.paper.commands.GkitzCommand;
import com.badbones69.crazyenchantments.paper.commands.GkitzTab;
import com.badbones69.crazyenchantments.paper.commands.TinkerCommand;
import com.badbones69.crazyenchantments.paper.controllers.BossBarController;
import com.badbones69.crazyenchantments.paper.controllers.LostBookController;
import com.badbones69.crazyenchantments.paper.enchantments.AllyEnchantments;
import com.badbones69.crazyenchantments.paper.enchantments.ArmorEnchantments;
import com.badbones69.crazyenchantments.paper.enchantments.AxeEnchantments;
import com.badbones69.crazyenchantments.paper.enchantments.BootEnchantments;
import com.badbones69.crazyenchantments.paper.enchantments.BowEnchantments;
import com.badbones69.crazyenchantments.paper.enchantments.HoeEnchantments;
import com.badbones69.crazyenchantments.paper.enchantments.PickaxeEnchantments;
import com.badbones69.crazyenchantments.paper.enchantments.SwordEnchantments;
import com.badbones69.crazyenchantments.paper.enchantments.ToolEnchantments;
import com.badbones69.crazyenchantments.paper.listeners.AuraListener;
import com.badbones69.crazyenchantments.paper.listeners.DustControlListener;
import com.badbones69.crazyenchantments.paper.listeners.FireworkDamageListener;
import com.badbones69.crazyenchantments.paper.listeners.MiscListener;
import com.badbones69.crazyenchantments.paper.listeners.ProtectionCrystalListener;
import com.badbones69.crazyenchantments.paper.listeners.ScramblerListener;
import com.badbones69.crazyenchantments.paper.listeners.ScrollListener;
import com.badbones69.crazyenchantments.paper.listeners.ShopListener;
import com.badbones69.crazyenchantments.paper.listeners.SlotCrystalListener;
import com.badbones69.crazyenchantments.paper.listeners.server.WorldSwitchListener;
import com.ryderbelserion.crazyenchantments.CrazyInstance;
import com.ryderbelserion.crazyenchantments.objects.ConfigOptions;
import com.ryderbelserion.fusion.core.files.FileManager;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyEnchantments extends JavaPlugin {

    private Starter starter;

    // Plugin Listeners.
    public final PluginManager pluginManager = getServer().getPluginManager();

    private ArmorEnchantments armorEnchantments;

    private BossBarController bossBarController;

    private CrazyManager crazyManager;

    private FileManager fileManager;
    private CrazyInstance instance;
    private ConfigOptions options;
    private FusionPaper fusion;

    @Override
    public void onEnable() {
        this.fusion = new FusionPaper(this);

        this.instance = new CrazyInstance(this.fileManager = this.fusion.getFileManager(), getDataPath());

        this.instance.init(); // initialize

        this.options = instance.getOptions();

        this.bossBarController = new BossBarController();

        this.starter = new Starter();
        this.starter.run();

        this.starter.getCurrencyAPI().loadCurrency();

        this.crazyManager = this.starter.getCrazyManager();

        final FileConfiguration tinker = Files.TINKER.getFile();

        if (!tinker.contains("Settings.Tinker-Version")) {
            tinker.set("Settings.Tinker-Version", 1.0);

            Files.TINKER.saveFile();
        }

        if (this.options.isMetricsEnabled()) new Metrics(this, 4494);

        // Load what we need to properly enable the plugin.
        this.crazyManager.load();

        this.pluginManager.registerEvents(new ScramblerListener(), this);
        this.pluginManager.registerEvents(new ScrollListener(), this);
        this.pluginManager.registerEvents(new SlotCrystalListener(), this);

        this.pluginManager.registerEvents(new FireworkDamageListener(), this);
        this.pluginManager.registerEvents(new ShopListener(), this);

        this.pluginManager.registerEvents(new MiscListener(), this);
        this.pluginManager.registerEvents(new DustControlListener(), this);

        this.pluginManager.registerEvents(new BlackSmithMenu.BlackSmithListener(), this);
        this.pluginManager.registerEvents(new KitsMenu.KitsListener(), this);
        this.pluginManager.registerEvents(new TinkererMenu.TinkererListener(), this);
        this.pluginManager.registerEvents(new BaseMenu.InfoMenuListener(), this);

        this.pluginManager.registerEvents(new PickaxeEnchantments(), this);
        this.pluginManager.registerEvents(new SwordEnchantments(), this);
        this.pluginManager.registerEvents(this.armorEnchantments = new ArmorEnchantments(), this);
        this.pluginManager.registerEvents(new AllyEnchantments(), this);
        this.pluginManager.registerEvents(new ToolEnchantments(), this);
        this.pluginManager.registerEvents(new BootEnchantments(), this);
        this.pluginManager.registerEvents(new AxeEnchantments(), this);
        this.pluginManager.registerEvents(new BowEnchantments(), this);
        this.pluginManager.registerEvents(new HoeEnchantments(), this);

        this.pluginManager.registerEvents(new ProtectionCrystalListener(), this);
        this.pluginManager.registerEvents(new FireworkDamageListener(), this);
        this.pluginManager.registerEvents(new AuraListener(), this);

        this.pluginManager.registerEvents(new LostBookController(), this);

        this.pluginManager.registerEvents(new WorldSwitchListener(), this);

        if (this.crazyManager.isGkitzEnabled()) {
            getLogger().info("G-Kitz support is now enabled.");

            this.pluginManager.registerEvents(new KitsMenu.KitsListener(), this);
        }

        registerCommand(getCommand("crazyenchantments"), new CETab(), new CECommand());

        registerCommand(getCommand("tinkerer"), null, new TinkerCommand());
        registerCommand(getCommand("blacksmith"), null, new BlackSmithCommand());

        registerCommand(getCommand("gkit"), new GkitzTab(), new GkitzCommand());
    }

    @Override
    public void onDisable() {
        final Server server = getServer();

        server.getGlobalRegionScheduler().cancelTasks(this);
        server.getAsyncScheduler().cancelTasks(this);

        this.bossBarController.removeAllBossBars();

        if (this.armorEnchantments != null) this.armorEnchantments.stop();

        if (this.starter.getAllyManager() != null) this.starter.getAllyManager().forceRemoveAllies();

        server.getOnlinePlayers().forEach(this.crazyManager::unloadCEPlayer);
    }

    private void registerCommand(final PluginCommand pluginCommand, final TabCompleter tabCompleter, final CommandExecutor commandExecutor) {
        if (pluginCommand != null) {
            pluginCommand.setExecutor(commandExecutor);

            if (tabCompleter != null) pluginCommand.setTabCompleter(tabCompleter);
        }
    }

    public final FileManager getFileManager() {
        return this.fileManager;
    }

    public final CrazyInstance getInstance() {
        return this.instance;
    }

    public final ConfigOptions getOptions() {
        return this.options;
    }

    public final FusionPaper getFusion() {
        return this.fusion;
    }

    public CrazyManager getCrazyManager() {
        return this.crazyManager;
    }

    public Starter getStarter() {
        return this.starter;
    }

    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public BossBarController getBossBarController() {
        return bossBarController;
    }

    public boolean isLogging() {
        return this.fusion.isVerbose();
    }
}