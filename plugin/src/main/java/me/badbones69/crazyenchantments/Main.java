package me.badbones69.crazyenchantments;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.api.objects.CEPlayer;
import me.badbones69.crazyenchantments.commands.*;
import me.badbones69.crazyenchantments.controllers.*;
import me.badbones69.crazyenchantments.enchantments.*;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import me.badbones69.crazyenchantments.multisupport.anticheats.AACSupport;
import me.badbones69.premiumhooks.anticheat.DakataAntiCheatSupport;
import me.badbones69.premiumhooks.spawners.SilkSpawnerSupport;
import me.badbones69.premiumhooks.spawners.SilkSpawnersCandcSupport;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {
    
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    private FileManager fileManager = FileManager.getInstance();
    private boolean fixHealth;
    private Armor armor;
    
    @Override
    public void onEnable() {
        fileManager.logInfo(true).setup(this);
        ce.load();
        SupportedPlugins.printHooks();
        Methods.hasUpdate();
        CurrencyAPI.loadCurrency();
        fixHealth = Files.CONFIG.getFile().getBoolean("Settings.Reset-Players-Max-Health");
        for (Player player : Bukkit.getOnlinePlayers()) {
            ce.loadCEPlayer(player);
            if (fixHealth) {
                if (ce.useHealthAttributes()) {
                    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
                } else {
                    player.setMaxHealth(20);
                }
            }
        }
        registerCommand(); //Register Commands
        //==========================================================================\\
        registerListener(); // Register Events
        //==========================================================================\\
        new Metrics(this);// Starts up bStats
        new BukkitRunnable() {
            @Override
            public void run() {
                for (CEPlayer player : ce.getCEPlayers()) {
                    ce.backupCEPlayer(player);
                }
            }
        }.runTaskTimerAsynchronously(this, 5 * 20 * 60, 5 * 20 * 60);
    }
    
    @Override
    public void onDisable() {
        armor.stop();
        
        if (ce.getAllyManager() != null) {
            ce.getAllyManager().forceRemoveAllies();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            ce.unloadCEPlayer(player);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        ce.loadCEPlayer(player);
        ce.updatePlayerEffects(player);
        if (fixHealth) {
            if (ce.useHealthAttributes()) {
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            } else {
                player.setMaxHealth(20);
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getName().equals("BadBones69")) {
                    player.sendMessage(Methods.getPrefix() + Methods.color("&7This server is running your Crazy Enchantments Plugin. "
                    + "&7It is running version &av" + ce.getPlugin().getDescription().getVersion() + "&7."));
                }
                if (player.isOp()) {
                    Methods.hasUpdate(player);
                }
            }
        }.
        runTaskLaterAsynchronously(this, 20);
    }
    
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        ce.unloadCEPlayer(e.getPlayer());
    }

    public void registerCommand() {
        setCommand("crazyenchantments", new CECommand());
        getCommand("crazyenchantments").setTabCompleter(new CETab());
        setCommand("tinkerer", new TinkerCommand());
        setCommand("blacksmith", new BlackSmithCommand());
        setCommand("gkit", new GkitzCommand());
        getCommand("gkit").setTabCompleter(new GkitzTab());
    }

    public void registerListener() {
        setEvents(this);
        setEvents(new ShopControl());
        setEvents(new InfoGUIControl());
        if (ce.isGkitzEnabled()) {
            setEvents(new GKitzController());
        }
        setEvents(new LostBookController());
        setEvents(new EnchantmentControl());
        setEvents(new SignControl());
        setEvents(new DustControl());
        setEvents(new Tinkerer());
        setEvents(new AuraListener());
        setEvents(new ScrollControl());
        setEvents(new BlackSmith());
        setEvents(new ArmorListener());
        setEvents(new ProtectionCrystal());
        setEvents(new Scrambler());
        setEvents(new CommandChecker());
        setEvents(new FireworkDamage());
        //==========================================================================\\
        setEvents(new Bows());
        setEvents(new Axes());
        setEvents(new Tools());
        setEvents(new Hoes());
        setEvents(new Helmets());
        setEvents(new PickAxes());
        setEvents(new Boots());
        setEvents(armor = new Armor());
        setEvents(new Swords());
        setEvents(new AllyEnchantments());
        if (SupportedPlugins.AAC.isPluginLoaded()) {
            setEvents(new AACSupport());
        }
        if (SupportedPlugins.SILK_SPAWNERS.isPluginLoaded()) {
            setEvents(new SilkSpawnerSupport());
        }
        if (SupportedPlugins.SILK_SPAWNERS_CANDC.isPluginLoaded()) {
            setEvents(new SilkSpawnersCandcSupport());
        }
        if (SupportedPlugins.DAKATA.isPluginLoaded()) {
            setEvents(new DakataAntiCheatSupport());
        }
    }

    public void setCommand(String cmd, CommandExecutor e) {
        getCommand(cmd).setExecutor(e);
    }

    public void setEvents(Listener l) {
        Bukkit.getServer().getPluginManager().registerEvents(l, this);
    }


    
}