package com.badbones69.crazyenchantments;

import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.FileManager;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.api.managers.AllyManager;
import com.badbones69.crazyenchantments.api.support.misc.spawners.SilkSpawnerSupport;
import com.badbones69.crazyenchantments.commands.*;
import com.badbones69.crazyenchantments.controllers.*;
import com.badbones69.crazyenchantments.enchantments.*;
import com.badbones69.crazyenchantments.api.PluginSupport.SupportedPlugins;
import org.bstats.bukkit.Metrics;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyEnchantments extends JavaPlugin implements Listener {

    private static CrazyEnchantments plugin;

    private Starter starter;

    private Armor armor;

    private final Attribute generic = Attribute.GENERIC_MAX_HEALTH;

    private boolean isEnabled = false;

    @Override
    public void onEnable() {
        try {
            plugin = this;

            starter = new Starter();

            starter.run();

            starter.getFileManager().logInfo(true).setup();

            boolean metricsEnabled = Files.CONFIG.getFile().getBoolean("Settings.Toggle-Metrics");
            String metrics = Files.CONFIG.getFile().getString("Settings.Toggle-Metrics");

            if (metrics != null) {
                if (metricsEnabled) new Metrics(this, 4494);
            } else {
                getLogger().warning("Metrics was automatically enabled.");
                getLogger().warning("Please add Toggle-Metrics: false to the top of your config.yml");
                getLogger().warning("https://github.com/Crazy-Crew/Crazy-Crates/blob/main/src/main/resources/config.yml");
                getLogger().warning("An example if confused is linked above.");

                new Metrics(this, 4494);
            }

            starter.getCrazyManager().load();

        } catch (Exception e) {
            e.printStackTrace();

            isEnabled = false;

            return;
        }

        SupportedPlugins.printHooks();

        starter.getCurrencyAPI().loadCurrency();

        boolean patchHealth = Files.CONFIG.getFile().getBoolean("Settings.Reset-Players-Max-Health");

        for (Player player : getServer().getOnlinePlayers()) {
            starter.getCrazyManager().loadCEPlayer(player);

            if (patchHealth) player.getAttribute(generic).setBaseValue(player.getAttribute(generic).getBaseValue());
        }

        getServer().getScheduler().runTaskTimerAsynchronously(this, bukkitTask -> starter.getCrazyManager().getCEPlayers().forEach(starter.getCrazyManager()::backupCEPlayer), 5 * 20 * 60, 5 * 20 * 60);

        isEnabled = true;

        enable();
    }

    @Override
    public void onDisable() {
        if (!isEnabled) return;

        disable();
    }

    private EnchantmentControl enchantmentControl;
    private SignControl signControl;
    private DustControl dustControl;
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
    private ProtectionCrystal protectionCrystal;
    private Scrambler scrambler;

    private CommandChecker commandChecker;
    private FireworkDamage fireworkDamage;

    private void enable() {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(enchantmentControl = new EnchantmentControl(), this);
        pluginManager.registerEvents(signControl = new SignControl(), this);
        pluginManager.registerEvents(dustControl = new DustControl(), this);
        pluginManager.registerEvents(scrollControl = new ScrollControl(), this);
        pluginManager.registerEvents(shopControl = new ShopControl(), this);
        pluginManager.registerEvents(infoGUIControl = new InfoGUIControl(), this);

        pluginManager.registerEvents(lostBookController = new LostBookController(), this);

        pluginManager.registerEvents(bows = new Bows(), this);
        pluginManager.registerEvents(axes = new Axes(), this);
        pluginManager.registerEvents(tools = new Tools(), this);
        pluginManager.registerEvents(hoes = new Hoes(), this);
        pluginManager.registerEvents(helmets = new Helmets(), this);
        pluginManager.registerEvents(pickAxes = new PickAxes(), this);
        pluginManager.registerEvents(boots = new Boots(), this);
        pluginManager.registerEvents(swords = new Swords(), this);
        pluginManager.registerEvents(armor = new Armor(), this);

        pluginManager.registerEvents(allyEnchantments = new AllyEnchantments(), this);

        pluginManager.registerEvents(tinkerer = new Tinkerer(), this);
        pluginManager.registerEvents(auraListener = new AuraListener(), this);
        pluginManager.registerEvents(blackSmith = new BlackSmith(), this);
        pluginManager.registerEvents(armorListener = new ArmorListener(), this);
        pluginManager.registerEvents(protectionCrystal = new ProtectionCrystal(), this);
        pluginManager.registerEvents(scrambler = new Scrambler(), this);
        pluginManager.registerEvents(commandChecker = new CommandChecker(), this);
        pluginManager.registerEvents(fireworkDamage = new FireworkDamage(), this);

        if (starter.getCrazyManager().isGkitzEnabled()) {
            getLogger().info("Gkitz support is now enabled.");

            getServer().getPluginManager().registerEvents(new GKitzController(), this);
        }

        if (SupportedPlugins.SILKSPAWNERS.isPluginLoaded()) {
            getLogger().info("Silk Spawners support is now enabled.");

            getServer().getPluginManager().registerEvents(new SilkSpawnerSupport(), this);
        }

        getCommand("crazyenchantments").setExecutor(new CECommand());
        getCommand("crazyenchantments").setTabCompleter(new CETab());

        getCommand("tinkerer").setExecutor(new TinkerCommand());
        getCommand("blacksmith").setExecutor(new BlackSmithCommand());

        getCommand("gkit").setExecutor(new GkitzCommand());
        getCommand("gkit").setTabCompleter(new GkitzTab());
    }

    private void disable() {
        armor.stop();

        if (starter.getAllyManager() != null) starter.getAllyManager().forceRemoveAllies();

        getServer().getOnlinePlayers().forEach(starter.getCrazyManager()::unloadCEPlayer);
    }

    public static CrazyEnchantments getPlugin() {
        return plugin;
    }

    public Starter getStarter() {
        return starter;
    }

    public Scrambler getScrambler() {
        return scrambler;
    }

    public ProtectionCrystal getProtectionCrystal() {
        return protectionCrystal;
    }

    public AllyEnchantments getAllyEnchantments() {
        return allyEnchantments;
    }

    public Armor getArmor() {
        return armor;
    }

    public ArmorListener getArmorListener() {
        return armorListener;
    }

    public AuraListener getAuraListener() {
        return auraListener;
    }

    public Axes getAxes() {
        return axes;
    }

    public BlackSmith getBlackSmith() {
        return blackSmith;
    }

    public Boots getBoots() {
        return boots;
    }

    public Bows getBows() {
        return bows;
    }

    public CommandChecker getCommandChecker() {
        return commandChecker;
    }

    public DustControl getDustControl() {
        return dustControl;
    }

    public EnchantmentControl getEnchantmentControl() {
        return enchantmentControl;
    }

    public FireworkDamage getFireworkDamage() {
        return fireworkDamage;
    }

    public Helmets getHelmets() {
        return helmets;
    }

    public Hoes getHoes() {
        return hoes;
    }

    public InfoGUIControl getInfoGUIControl() {
        return infoGUIControl;
    }

    public LostBookController getLostBookController() {
        return lostBookController;
    }

    public PickAxes getPickAxes() {
        return pickAxes;
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

    public Swords getSwords() {
        return swords;
    }

    public Tinkerer getTinkerer() {
        return tinkerer;
    }

    public Tools getTools() {
        return tools;
    }
}