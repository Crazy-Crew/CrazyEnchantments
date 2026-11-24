package com.badbones69.crazyenchantments.paper;

import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.badbones69.crazyenchantments.paper.api.builders.types.BaseMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.blacksmith.BlackSmithMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsMenu;
import com.badbones69.crazyenchantments.paper.api.builders.types.tinkerer.TinkererMenu;
import com.badbones69.crazyenchantments.paper.api.managers.AllyManager;
import com.badbones69.crazyenchantments.paper.controllers.EnchantmentControl;
import com.badbones69.crazyenchantments.paper.listeners.ScramblerListener;
import com.badbones69.crazyenchantments.paper.listeners.ScrollListener;
import com.badbones69.crazyenchantments.paper.listeners.SlotCrystalListener;
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
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.List;

public class CrazyEnchantments extends JavaPlugin {

    public static CrazyEnchantments getPlugin() {
        return JavaPlugin.getPlugin(CrazyEnchantments.class);
    }

    private final PluginManager pluginManager = getServer().getPluginManager();
    private final BossBarController bossBarController = new BossBarController(this);

    private ArmorEnchantments armorEnchantments;
    private CrazyPlatform platform;
    private CrazyManager crazyManager;

    @Override
    public void onEnable() {
        this.platform = new CrazyPlatform(new FusionPaper(this), getDataPath());
        this.platform.init();

        this.crazyManager = new CrazyManager();
        this.crazyManager.load();

        final CommentedConfigurationNode config = Files.CONFIG.getConfiguration();

        if (config.node("Settings", "Toggle-Metrics").getBoolean(false)) new Metrics(this, 4494);

        List.of(
                new FireworkDamageListener(),
                new ShopListener(),

                new EnchantmentControl(),

                new MiscListener(),

                new DustControlListener(),

                new BlackSmithMenu.BlackSmithListener(),
                new KitsMenu.KitsListener(),
                new TinkererMenu.TinkererListener(),
                new BaseMenu.InfoMenuListener(),

                new ScrollListener(),
                new ScramblerListener(),

                new PickaxeEnchantments(),
                new SwordEnchantments(),
                new AllyEnchantments(),
                new ToolEnchantments(),
                new BootEnchantments(),
                new AxeEnchantments(),
                new BowEnchantments(),
                new HoeEnchantments(),

                new ProtectionCrystalListener(),
                new FireworkDamageListener(),
                new SlotCrystalListener(),
                new AuraListener(),

                new LostBookController(),
                new WorldSwitchListener()
        ).forEach(listener -> this.pluginManager.registerEvents(listener, this));

        this.pluginManager.registerEvents(this.armorEnchantments = new ArmorEnchantments(), this);

        if (this.crazyManager.isGkitzEnabled()) {
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

        if (this.crazyManager != null) {
            server.getOnlinePlayers().forEach(this.crazyManager::unloadCEPlayer);

            this.bossBarController.removeAllBossBars();

            if (this.armorEnchantments != null) {
                this.armorEnchantments.stop();
            }

            final AllyManager allyManager = this.crazyManager.getAllyManager();

            if (allyManager != null) {
                allyManager.forceRemoveAllies();
            }
        }
    }

    public @NonNull final BossBarController getBossBarController() {
        return bossBarController;
    }

    public @NonNull final PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public @NonNull final CrazyManager getCrazyManager() {
        return this.crazyManager;
    }

    public @NonNull final CrazyPlatform getPlatform() {
        return this.platform;
    }
}