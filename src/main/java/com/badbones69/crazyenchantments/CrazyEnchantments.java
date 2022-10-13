package com.badbones69.crazyenchantments;

import com.badbones69.crazyenchantments.api.support.anticheats.NoCheatPlusSupport;
import com.badbones69.crazyenchantments.api.support.claims.SuperiorSkyBlockSupport;
import com.badbones69.crazyenchantments.api.support.misc.OraxenSupport;
import com.badbones69.crazyenchantments.controllers.*;
import com.badbones69.crazyenchantments.enchantments.*;
import com.badbones69.crazyenchantments.listeners.ArmorListener;
import com.badbones69.crazyenchantments.listeners.AuraListener;
import com.badbones69.crazyenchantments.listeners.DustControlListener;
import com.badbones69.crazyenchantments.listeners.ProtectionCrystalListener;
import com.badbones69.crazyenchantments.listeners.server.ServerReadyListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyEnchantments extends JavaPlugin implements Listener {

    private static CrazyEnchantments plugin;

    private Starter starter;

    private boolean isEnabled = false;

    // Listeners
    private ProtectionCrystalListener protectionCrystalListener;

    PluginManager pluginManager = getServer().getPluginManager();

    @Override
    public void onEnable() {
        try {
            plugin = this;

            starter = new Starter();

            // Create all instances we need.
            starter.run();

            // Set up all our files.
            starter.getFileManager().setLog(true).setup();

            // Register any events we need to before we load CrazyManager!
            pluginManager.registerEvents(new ServerReadyListener(), this);

            pluginManager.registerEvents(protectionCrystalListener = new ProtectionCrystalListener(), this);
        } catch (Exception e) {
            e.printStackTrace();

            isEnabled = false;

            return;
        }

        isEnabled = true;

        enable();
    }

    @Override
    public void onDisable() {
        if (!isEnabled) return;

        //disable();
    }

    private EnchantmentControl enchantmentControl;
    private SignControl signControl;
    private ScrollControl scrollControl;
    private ShopControl shopControl;
    private InfoGUIControl infoGUIControl;

    private LostBookController lostBookController;

    private Bows bows;
    private Axes axes;
    private Tools tools;
    private Hoes hoes;
    private Helmets helmets;
    private PickAxes pickAxes;
    private Boots boots;
    private Swords swords;

    private AllyEnchantments allyEnchantments;
    private Tinkerer tinkerer;

    private AuraListener auraListener;
    private ArmorListener armorListener;

    private BlackSmith blackSmith;
    private Scrambler scrambler;

    private CommandChecker commandChecker;
    private FireworkDamage fireworkDamage;

    // Listeners

    private void enable() {
        // Load what we need to properly enable the plugin.
        starter.getCrazyManager().load();

        pluginManager.registerEvents(new DustControlListener(), this);

        //pluginManager.registerEvents(enchantmentControl = new EnchantmentControl(), this);
        //pluginManager.registerEvents(signControl = new SignControl(), this);
        //pluginManager.registerEvents(scrollControl = new ScrollControl(), this);
        //pluginManager.registerEvents(shopControl = new ShopControl(), this);
        //pluginManager.registerEvents(infoGUIControl = new InfoGUIControl(), this);

        //pluginManager.registerEvents(lostBookController = new LostBookController(), this);

        //pluginManager.registerEvents(bows = new Bows(), this);
        //pluginManager.registerEvents(axes = new Axes(), this);
        //pluginManager.registerEvents(tools = new Tools(), this);
        //pluginManager.registerEvents(hoes = new Hoes(), this);
        //pluginManager.registerEvents(helmets = new Helmets(), this);
        //pluginManager.registerEvents(pickAxes = new PickAxes(), this);
        //pluginManager.registerEvents(boots = new Boots(), this);
        //pluginManager.registerEvents(swords = new Swords(), this);
        //pluginManager.registerEvents(armor = new Armor(), this);

        //pluginManager.registerEvents(allyEnchantments = new AllyEnchantments(), this);

        //pluginManager.registerEvents(tinkerer = new Tinkerer(), this);
        //pluginManager.registerEvents(auraListener = new AuraListener(), this);
        //pluginManager.registerEvents(blackSmith = new BlackSmith(), this);
        //pluginManager.registerEvents(armorListener = new ArmorListener(), this);
        //pluginManager.registerEvents(protectionCrystal = new ProtectionCrystal(), this);
        //pluginManager.registerEvents(scrambler = new Scrambler(), this);
        //pluginManager.registerEvents(commandChecker = new CommandChecker(), this);
        //pluginManager.registerEvents(fireworkDamage = new FireworkDamage(), this);

        //if (starter.getCrazyManager().isGkitzEnabled()) {
        //    getLogger().info("Gkitz support is now enabled.");

        //    getServer().getPluginManager().registerEvents(new GKitzController(), this);
        //}

        //if (SupportedPlugins.SILKSPAWNERS.isPluginLoaded()) {
        //    getLogger().info("Silk Spawners support is now enabled.");

        //    getServer().getPluginManager().registerEvents(new SilkSpawnerSupport(), this);
        //}

        //if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) noCheatPlusSupport = new NoCheatPlusSupport();

        //if (SupportedPlugins.ORAXEN.isPluginLoaded()) oraxenSupport = new OraxenSupport();

        //if (SupportedPlugins.SUPERIORSKYBLOCK.isPluginLoaded()) superiorSkyBlockSupport = new SuperiorSkyBlockSupport();

        //getCommand("crazyenchantments").setExecutor(new CECommand());
        //getCommand("crazyenchantments").setTabCompleter(new CETab());

        //getCommand("tinkerer").setExecutor(new TinkerCommand());
        //getCommand("blacksmith").setExecutor(new BlackSmithCommand());

        //getCommand("gkit").setExecutor(new GkitzCommand());
        //getCommand("gkit").setTabCompleter(new GkitzTab());
    }

    private void disable() {
        //armor.stop();

        //if (starter.getAllyManager() != null) starter.getAllyManager().forceRemoveAllies();

        //getServer().getOnlinePlayers().forEach(starter.getCrazyManager()::unloadCEPlayer);
    }

    public static CrazyEnchantments getPlugin() {
        return plugin;
    }

    public Starter getStarter() {
        return starter;
    }

    /**
     * Listeners
     */

    public ArmorListener getArmorListener() {
        return armorListener;
    }

    public AuraListener getAuraListener() {
        return auraListener;
    }

    public ProtectionCrystalListener getProtectionCrystalListener() {
        return protectionCrystalListener;
    }

    /**
     * Armor Listeners
     */

    public Helmets getHelmets() {
        return helmets;
    }

    public Armor getArmor() {
        return null;
    }

    public Boots getBoots() {
        return boots;
    }

    public Axes getAxes() {
        return axes;
    }

    public Bows getBows() {
        return bows;
    }

    public Hoes getHoes() {
        return hoes;
    }

    public PickAxes getPickAxes() {
        return pickAxes;
    }

    public Swords getSwords() {
        return swords;
    }

    public Tools getTools() {
        return tools;
    }

    /**
     * Plugin Controllers
     */

    public EnchantmentControl getEnchantmentControl() {
        return enchantmentControl;
    }

    public InfoGUIControl getInfoGUIControl() {
        return infoGUIControl;
    }

    public LostBookController getLostBookController() {
        return lostBookController;
    }

    public ScrollControl getScrollControl() {
        return scrollControl;
    }

    public SignControl getSignControl() {
        return signControl;
    }

    public ShopControl getShopControl() {
        return shopControl;
    }

    /**
     * Plugin Support
     */

    public NoCheatPlusSupport getNoCheatPlusSupport() {
        return null;
    }

    public OraxenSupport getOraxenSupport() {
        return null;
    }

    public SuperiorSkyBlockSupport getSuperiorSkyBlockSupport() {
        return null;
    }

    /**
     * Misc
     */

    public CommandChecker getCommandChecker() {
        return commandChecker;
    }

    public FireworkDamage getFireworkDamage() {
        return fireworkDamage;
    }

    public Tinkerer getTinkerer() {
        return tinkerer;
    }

    public Scrambler getScrambler() {
        return scrambler;
    }

    public BlackSmith getBlackSmith() {
        return blackSmith;
    }

    public AllyEnchantments getAllyEnchantments() {
        return allyEnchantments;
    }
}