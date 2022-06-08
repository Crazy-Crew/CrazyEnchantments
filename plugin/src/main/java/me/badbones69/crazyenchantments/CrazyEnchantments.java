package me.badbones69.crazyenchantments;

import me.badbones69.crazyenchantments.api.CrazyManager;
import me.badbones69.crazyenchantments.api.FileManager;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.api.objects.CEPlayer;
import me.badbones69.crazyenchantments.commands.*;
import me.badbones69.crazyenchantments.controllers.*;
import me.badbones69.crazyenchantments.enchantments.*;
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

public class CrazyEnchantments extends JavaPlugin implements Listener {
    
    private CrazyManager ce = CrazyManager.getInstance();
    private FileManager fileManager = FileManager.getInstance();
    private boolean fixHealth;
    private Armor armor;
    
    @Override
    public void onEnable() {
        fileManager.logInfo(true).setup(this);
        ce.load();

        Methods.hasUpdate();
        CurrencyAPI.loadCurrency();

        fixHealth = Files.CONFIG.getFile().getBoolean("Settings.Reset-Players-Max-Health");

        for (Player player : Bukkit.getOnlinePlayers()) {
            ce.loadCEPlayer(player);
            if (fixHealth) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
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
        if (ce.isGkitzEnabled()) pm.registerEvents(new GKitzController(), this);

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
        pm.registerEvents(armor = new Armor(), this);
        pm.registerEvents(new Swords(), this);
        pm.registerEvents(new AllyEnchantments(), this);

        // if (SupportedPlugins.SILK_SPAWNERS.isPluginLoaded()) pm.registerEvents(new SilkSpawnerSupport(), this);

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
        
        if (ce.getAllyManager() != null) ce.getAllyManager().forceRemoveAllies();
        for (Player player : Bukkit.getOnlinePlayers()) {
            ce.unloadCEPlayer(player);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        ce.loadCEPlayer(player);
        ce.updatePlayerEffects(player);
        if (fixHealth) player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
    }
    
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        ce.unloadCEPlayer(e.getPlayer());
    }
}