package com.badbones69.crazyenchantments.paper;

import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.builders.types.BaseMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith.BlackSmithMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.tinkerer.TinkererMenu;
import com.badbones69.crazyenchantments.paper.commands.CommandManager;
import com.badbones69.crazyenchantments.paper.controllers.BossBarController;
import com.badbones69.crazyenchantments.paper.controllers.CommandChecker;
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
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyEnchantments extends JavaPlugin {

    private Starter starter;

    // Plugin Listeners.
    public final PluginManager pluginManager = getServer().getPluginManager();

    private FireworkDamageListener fireworkDamageListener;
    private ShopListener shopListener;
    private ArmorEnchantments armorEnchantments;

    private final BossBarController bossBarController = new BossBarController(this);

    @Override
    public void onEnable() {
        this.starter = new Starter();
        this.starter.run();

        this.starter.getCurrencyAPI().loadCurrency();

        FileConfiguration config = Files.CONFIG.getFile();
        FileConfiguration tinker = Files.TINKER.getFile();

        if (!config.contains("Settings.Toggle-Metrics")) {
            config.set("Settings.Toggle-Metrics", false);

            Files.CONFIG.saveFile();
        }

        if (!config.contains("Settings.Refresh-Potion-Effects-On-World-Change")) {
            config.set("Settings.Refresh-Potion-Effects-On-World-Change", false);
            
            Files.CONFIG.saveFile();
        }

        if (!tinker.contains("Settings.Tinker-Version")) {
            tinker.set("Settings.Tinker-Version", 1.0);

            Files.TINKER.saveFile();
        }

        if (config.getBoolean("Settings.Toggle-Metrics")) new Metrics(this, 4494);

        // Load the new commands.
        CommandManager.load();

        this.pluginManager.registerEvents(this.fireworkDamageListener = new FireworkDamageListener(), this);
        this.pluginManager.registerEvents(this.shopListener = new ShopListener(), this);

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
        this.pluginManager.registerEvents(new CommandChecker(), this);

        this.pluginManager.registerEvents(new WorldSwitchListener(), this);

        if (this.starter.getCrazyManager().isGkitzEnabled()) {
            getLogger().info("G-Kitz support is now enabled.");

            this.pluginManager.registerEvents(new KitsMenu.KitsListener(), this);
        }
    }

    @Override
    public void onDisable() {
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

    public ShopListener getShopListener() {
        return this.shopListener;
    }

    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public BossBarController getBossBarController() {
        return bossBarController;
    }

    public boolean isLogging() {
        return true;
    }
}