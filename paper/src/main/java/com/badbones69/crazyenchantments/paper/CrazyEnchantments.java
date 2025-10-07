package com.badbones69.crazyenchantments.paper;

import com.badbones69.crazyenchantments.paper.api.builders.types.BaseMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith.BlackSmithMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.tinkerer.TinkererMenu;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.utils.FileUtils;
import com.badbones69.crazyenchantments.paper.commands.CommandManager;
import com.badbones69.crazyenchantments.paper.config.ConfigOptions;
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
import com.badbones69.crazyenchantments.paper.listeners.ShopListener;
import com.badbones69.crazyenchantments.paper.listeners.server.WorldSwitchListener;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;

public class CrazyEnchantments extends JavaPlugin {

    private Starter starter;

    // Plugin Listeners.
    public final PluginManager pluginManager = getServer().getPluginManager();

    private FireworkDamageListener fireworkDamageListener;
    private ArmorEnchantments armorEnchantments;

    private final BossBarController bossBarController = new BossBarController(this);

    private PaperFileManager fileManager;
    private ConfigOptions options;
    private FusionPaper fusion;
    private CrazyInstance instance;

    @Override
    public void onEnable() {
        this.fusion = new FusionPaper(this);

        this.fileManager = this.fusion.getFileManager();

        final Path path = this.getDataPath();

        this.fileManager.addPaperFile(path.resolve("config.yml"))
                .addPaperFile(path.resolve("BlockList.yml"))
                .addPaperFile(path.resolve("Data.yml"))
                .addPaperFile(path.resolve("Enchantment-Types.yml"))
                .addPaperFile(path.resolve("Enchantments.yml"))
                .addPaperFile(path.resolve("GKitz.yml"))
                .addPaperFile(path.resolve("HeadMap.yml"))
                .addPaperFile(path.resolve("Messages.yml"))
                .addPaperFile(path.resolve("Tinker.yml"));

        this.options = new ConfigOptions();
        this.options.init(FileKeys.config.getConfiguration());

        if (this.options.isToggleMetrics()) { // Enable bStats
            new Metrics(this, 4494);
        }

        this.instance = new CrazyInstance();
        this.instance.init();

        this.starter = new Starter();
        this.starter.run();

        this.starter.getCurrencyAPI().loadCurrency();

        this.pluginManager.registerEvents(this.fireworkDamageListener = new FireworkDamageListener(), this);
        this.pluginManager.registerEvents(new ShopListener(), this);

        // Load what we need to properly enable the plugin.
        this.starter.getCrazyManager().load();

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

        if (this.starter.getCrazyManager().isGkitzEnabled()) {
            getLogger().info("G-Kitz support is now enabled.");

            this.pluginManager.registerEvents(new KitsMenu.KitsListener(), this);
        }

        CommandManager.load();
    }

    @Override
    public void onDisable() {
        getServer().getGlobalRegionScheduler().cancelTasks(this);
        getServer().getAsyncScheduler().cancelTasks(this);

        this.bossBarController.removeAllBossBars();

        if (this.armorEnchantments != null) this.armorEnchantments.stop();

        if (this.starter.getAllyManager() != null) this.starter.getAllyManager().forceRemoveAllies();

        getServer().getOnlinePlayers().forEach(this.starter.getCrazyManager()::unloadCEPlayer);
    }

    public Starter getStarter() {
        return this.starter;
    }

    // Plugin Listeners.
    public FireworkDamageListener getFireworkDamageListener() {
        return this.fireworkDamageListener;
    }

    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public BossBarController getBossBarController() {
        return bossBarController;
    }

    public @NotNull final PaperFileManager getFileManager() {
        return this.fileManager;
    }

    public @NotNull final CrazyInstance getInstance() {
        return this.instance;
    }

    public @NotNull final ConfigOptions getOptions() {
        return this.options;
    }

    public @NotNull final FusionPaper getFusion() {
        return this.fusion;
    }

    public final boolean isLogging() {
        return this.fusion.isVerbose();
    }
}