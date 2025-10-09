package com.badbones69.crazyenchantments.paper;

import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.builders.types.BaseMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith.BlackSmithMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.tinkerer.TinkererMenu;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
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
import com.badbones69.crazyenchantments.paper.listeners.*;
import com.badbones69.crazyenchantments.paper.listeners.server.WorldSwitchListener;
import com.badbones69.crazyenchantments.paper.managers.CategoryManager;
import com.badbones69.crazyenchantments.paper.managers.items.ItemManager;
import com.badbones69.crazyenchantments.paper.managers.KitsManager;
import com.ryderbelserion.fusion.core.files.enums.FileType;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.List;

public class CrazyEnchantments extends JavaPlugin {

    public static @NotNull CrazyEnchantments getPlugin() {
        return JavaPlugin.getPlugin(CrazyEnchantments.class);
    }

    private CategoryManager categoryManager;
    private PaperFileManager fileManager;
    private ItemManager itemManager;
    private KitsManager kitsManager;
    private CrazyInstance instance;
    private ConfigOptions options;
    private FusionPaper fusion;

    // ryder start, delete all this.
    private Starter starter;

    // Plugin Listeners.
    public final PluginManager pluginManager = getServer().getPluginManager();

    private ArmorEnchantments armorEnchantments;

    private final BossBarController bossBarController = new BossBarController(this);
    // ryder end

    @Override
    public void onEnable() {
        this.fusion = new FusionPaper(this);
        this.fusion.init();

        this.fileManager = this.fusion.getFileManager();

        final Path path = this.getDataPath();

        List.of(
                "config.yml",
                "Data.yml",
                "Enchantment-Types.yml",
                "Enchantments.yml",
                "GKitz.yml",
                "HeadMap.yml",
                "Messages.yml",
                "Tinker.yml"
        ).forEach(file -> this.fileManager.addPaperFile(path.resolve(file)));

        this.fileManager.addFile(path.resolve("blocks.json"), FileType.JSON);

        this.options = new ConfigOptions();
        this.options.init(FileKeys.config.getYamlConfiguration());

        if (this.options.isToggleMetrics()) { // Enable bStats
            new Metrics(this, 4494);
        }

        this.instance = new CrazyInstance();
        this.instance.init();

        this.categoryManager = this.instance.getCategoryManager();
        this.itemManager = this.instance.getItemManager();
        this.kitsManager = this.instance.getKitsManager();

        this.starter = new Starter();
        this.starter.run();

        this.starter.getCurrencyAPI().loadCurrency();

        this.pluginManager.registerEvents(new FireworkDamageListener(), this);
        this.pluginManager.registerEvents(new ShopListener(), this);

        // Load what we need to properly enable the plugin.
        this.starter.getCrazyManager().load();

        this.pluginManager.registerEvents(new MiscListener(), this);
        this.pluginManager.registerEvents(new DustControlListener(), this);

        this.pluginManager.registerEvents(new BlackSmithMenu.BlackSmithListener(), this);
        this.pluginManager.registerEvents(new KitsMenu.KitsListener(), this);
        this.pluginManager.registerEvents(new TinkererMenu.TinkererListener(), this);
        this.pluginManager.registerEvents(new BaseMenu.InfoMenuListener(), this);

        this.pluginManager.registerEvents(new SlotCrystalListener(), this);
        this.pluginManager.registerEvents(new ScramblerListener(), this);
        this.pluginManager.registerEvents(new ScrollListener(), this);

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

        CommandManager.load();
    }

    @Override
    public void onDisable() {
        final Server server = getServer();

        server.getGlobalRegionScheduler().cancelTasks(this);
        server.getAsyncScheduler().cancelTasks(this);

        this.bossBarController.removeAllBossBars();

        if (this.armorEnchantments != null) this.armorEnchantments.stop();

        if (this.starter.getAllyManager() != null) this.starter.getAllyManager().forceRemoveAllies();

        server.getOnlinePlayers().forEach(this.starter.getCrazyManager()::unloadCEPlayer);
    }

    public @NotNull final Starter getStarter() {
        return this.starter;
    }

    public @NotNull final PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public @NotNull final BossBarController getBossBarController() {
        return bossBarController;
    }

    public @NotNull final CategoryManager getCategoryManager() {
        return this.categoryManager;
    }

    public @NotNull final PaperFileManager getFileManager() {
        return this.fileManager;
    }

    public @NotNull final ItemManager getItemManager() {
        return this.itemManager;
    }

    public @NotNull final KitsManager getKitsManager() {
        return this.kitsManager;
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