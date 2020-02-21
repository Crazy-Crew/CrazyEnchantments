package me.badbones69.crazyenchantments;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.api.objects.CEPlayer;
import me.badbones69.crazyenchantments.commands.*;
import me.badbones69.crazyenchantments.controllers.*;
import me.badbones69.crazyenchantments.enchantments.*;
import me.badbones69.crazyenchantments.multisupport.*;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin implements Listener {
    
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    private FileManager fileManager = FileManager.getInstance();
    private boolean fixHealth;
    
    @Override
    public void onEnable() {
        fileManager.logInfo(true).setup(this);
        ce.load();
        Methods.hasUpdate();
        CurrencyAPI.loadCurrency();
        fixHealth = Files.CONFIG.getFile().getBoolean("Settings.Reset-Players-Max-Health");
        for (Player player : Bukkit.getOnlinePlayers()) {
            ce.loadCEPlayer(player);
            if (fixHealth) {
                if (Version.isNewer(Version.v1_8_R3)) {
                    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
                } else {
                    player.setMaxHealth(20);
                }
            }
        }
        getCommand("crazyenchantments").setExecutor(new CECommand());
        getCommand("crazyenchantments").setTabCompleter(new CETab());
        getCommand("tinkerer").setExecutor(new TinkerCommand());
        getCommand("blacksmith").setExecutor(new BlackSmithCommand());
        getCommand("gkit").setExecutor(new GkitzCommand());
        getCommand("gkit").setTabCompleter(new GkitzTab());
        PluginManager pm = Bukkit.getServer().getPluginManager();
        //==========================================================================\\
        pm.registerEvents(this, this);
        pm.registerEvents(new ShopControl(), this);
        pm.registerEvents(new InfoGUIControl(), this);
        if (ce.isGkitzEnabled()) {
            pm.registerEvents(new GKitzController(), this);
        }
        pm.registerEvents(new LostBookController(), this);
        pm.registerEvents(new EnchantmentControl(), this);
        pm.registerEvents(new SignControl(), this);
        pm.registerEvents(new DustControl(), this);
        pm.registerEvents(new Tinkerer(), this);
        pm.registerEvents(new AuraListener(), this);
        pm.registerEvents(new ScrollControl(), this);
        pm.registerEvents(new BlackSmith(), this);
        pm.registerEvents(new ArmorListener(), this);
        pm.registerEvents(new ProtectionCrystal(), this);
        pm.registerEvents(new Scrambler(), this);
        pm.registerEvents(new CommandChecker(), this);
        pm.registerEvents(new FireworkDamage(), this);
        //==========================================================================\\
        pm.registerEvents(new Bows(), this);
        pm.registerEvents(new Axes(), this);
        pm.registerEvents(new Tools(), this);
        pm.registerEvents(new Hoes(), this);
        pm.registerEvents(new Helmets(), this);
        pm.registerEvents(new PickAxes(), this);
        pm.registerEvents(new Boots(), this);
        pm.registerEvents(new Armor(), this);
        pm.registerEvents(new Swords(), this);
        if (SupportedPlugins.AAC.isPluginLoaded()) {
            pm.registerEvents(new AACSupport(), this);
        }
        if (SupportedPlugins.SILK_SPAWNERS.isPluginLoaded()) {
            pm.registerEvents(new SilkSpawnerSupport(), this);
        }
        if (SupportedPlugins.SILK_SPAWNERS_CANDC.isPluginLoaded()) {
            pm.registerEvents(new SilkSpawnersCandcSupport(), this);
        }
        if (SupportedPlugins.DAKATA.isPluginLoaded()) {
            pm.registerEvents(new DakataAntiCheatSupport(), this);
        }
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
        Armor.removeAllies();
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
            if (Version.isNewer(Version.v1_8_R3)) {
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
    
}