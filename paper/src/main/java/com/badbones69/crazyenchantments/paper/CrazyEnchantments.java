package com.badbones69.crazyenchantments.paper;

import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.badbones69.crazyenchantments.paper.api.builders.types.BaseMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith.BlackSmithMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.tinkerer.TinkererMenu;
import com.ryderbelserion.common.api.enums.Files;
import com.badbones69.crazyenchantments.paper.commands.api.CommandManager;
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
import com.ryderbelserion.fusion.api.enums.Level;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class CrazyEnchantments extends JavaPlugin {

    public static CrazyEnchantments getPlugin() {
        return JavaPlugin.getPlugin(CrazyEnchantments.class);
    }

    private Starter starter;

    // Plugin Listeners.
    public final PluginManager pluginManager = getServer().getPluginManager();

    private FireworkDamageListener fireworkDamageListener;
    private ArmorEnchantments armorEnchantments;

    private final BossBarController bossBarController = new BossBarController(this);

    private CrazyPlatform platform;

    @Override
    public void onEnable() {
        this.platform = new CrazyPlatform(new FusionPaper(this), getDataPath());
        this.platform.init();

        this.starter = new Starter();
        this.starter.run();

        this.starter.getCurrencyAPI().loadCurrency();

        final @NotNull CommentedConfigurationNode config = Files.CONFIG.getConfiguration();
        final @NotNull CommentedConfigurationNode tinker = Files.TINKER.getConfiguration();

        boolean isSave = false;

        if (!config.hasChild("Settings", "CESuccessOverride")) {
            Methods.setNode(config, "Settings.CESuccessOverride", Integer.class, -1);

            isSave = true;
        }

        if (!config.hasChild("Settings", "CEFailureOverride")) {
            Methods.setNode(config, "Settings.CEFailureOverride", Integer.class, -1);

            isSave = true;
        }

        if (!config.hasChild("Settings", "Toggle-Metrics")) {
            Methods.setNode(config, "Settings.Toggle-Metrics", Boolean.class, false);

            isSave = true;
        }

        if (!config.hasChild("Settings", "Refresh-Potion-Effects-On-World-Change")) {
            Methods.setNode(config, "Settings.Refresh-Potion-Effects-On-World-Change", Boolean.class, false);

            isSave = true;
        }

        if (isSave) {
            Files.CONFIG.save();
        }

        if (config.node("Settings", "Toggle-Metrics").getBoolean(false)) new Metrics(this, 4494);

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
            this.platform.getFusion().log(Level.warn, "G-Kitz Support is now enabled!");

            this.pluginManager.registerEvents(new KitsMenu.KitsListener(), this);
        }

        CommandManager.load();
    }

    @Override
    public void onDisable() {
        final Server server = getServer();

        server.getGlobalRegionScheduler().cancelTasks(this);
        server.getAsyncScheduler().cancelTasks(this);

        if (this.starter != null) {
            server.getOnlinePlayers().forEach(this.starter.getCrazyManager()::unloadCEPlayer);

            this.bossBarController.removeAllBossBars();

            if (this.armorEnchantments != null) this.armorEnchantments.stop();

            if (this.starter.getAllyManager() != null) this.starter.getAllyManager().forceRemoveAllies();
        }
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

    public @NotNull final CrazyPlatform getPlatform() {
        return this.platform;
    }
}