package me.badbones69.crazyenchantments;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.badbones69.crazyenchantments.api.CEPlayer;
import me.badbones69.crazyenchantments.api.CEnchantments;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.DataStorage;
import me.badbones69.crazyenchantments.api.Metrics;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.api.events.AuraListener;
import me.badbones69.crazyenchantments.commands.CommandBlackSmith;
import me.badbones69.crazyenchantments.commands.CommandCE;
import me.badbones69.crazyenchantments.commands.CommandGKitz;
import me.badbones69.crazyenchantments.commands.CommandTinkerer;
import me.badbones69.crazyenchantments.controlers.BlackSmith;
import me.badbones69.crazyenchantments.controlers.CommandChecker;
import me.badbones69.crazyenchantments.controlers.CustomEnchantments;
import me.badbones69.crazyenchantments.controlers.DustControl;
import me.badbones69.crazyenchantments.controlers.EnchantmentControl;
import me.badbones69.crazyenchantments.controlers.FireworkDamageAPI;
import me.badbones69.crazyenchantments.controlers.GKitzControler;
import me.badbones69.crazyenchantments.controlers.InfoGUIControl;
import me.badbones69.crazyenchantments.controlers.LostBook;
import me.badbones69.crazyenchantments.controlers.ProtectionCrystal;
import me.badbones69.crazyenchantments.controlers.Scrambler;
import me.badbones69.crazyenchantments.controlers.ScrollControl;
import me.badbones69.crazyenchantments.controlers.ShopControler;
import me.badbones69.crazyenchantments.controlers.SignControl;
import me.badbones69.crazyenchantments.controlers.Tinkerer;
import me.badbones69.crazyenchantments.enchantments.Armor;
import me.badbones69.crazyenchantments.enchantments.Axes;
import me.badbones69.crazyenchantments.enchantments.Boots;
import me.badbones69.crazyenchantments.enchantments.Bows;
import me.badbones69.crazyenchantments.enchantments.Helmets;
import me.badbones69.crazyenchantments.enchantments.PickAxes;
import me.badbones69.crazyenchantments.enchantments.Swords;
import me.badbones69.crazyenchantments.enchantments.Tools;
import me.badbones69.crazyenchantments.multisupport.AACSupport;
import me.badbones69.crazyenchantments.multisupport.DakataAntiCheatSupport;
import me.badbones69.crazyenchantments.multisupport.SilkSpawners;
import me.badbones69.crazyenchantments.multisupport.StackMobSupport;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import me.badbones69.crazyenchantments.multisupport.Version;
import me.badbones69.crazyenchantments.multisupport.armorequip.ArmorListener;

public class Main extends JavaPlugin implements Listener {
	
	public static CrazyEnchantments CE = CrazyEnchantments.getInstance();
	public static SettingsManager settings = SettingsManager.getInstance();
	public static CustomEnchantments CustomE = CustomEnchantments.getInstance();
	private static Main instance;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		instance = this;
		settings.setup(this);
		Methods.hasUpdate();
		Boots.onStart();
		CEnchantments.load();
		DataStorage.load();
		CustomE.update();
		CurrencyAPI.loadCurrency();
		for(Player player : Bukkit.getOnlinePlayers()) {
			CE.loadCEPlayer(player);
			if(settings.getConfig().contains("Settings.Reset-Players-Max-Health")) {
				if(settings.getConfig().getBoolean("Settings.Reset-Players-Max-Health")) {
					player.setMaxHealth(20);
				}
			}
		}
		initCommands();
		PluginManager pm = Bukkit.getServer().getPluginManager();
		//==========================================================================\\
		pm.registerEvents(this, this);
		pm.registerEvents(new ShopControler(), this);
		pm.registerEvents(new InfoGUIControl(), this);
		pm.registerEvents(new GKitzControler(), this);
		pm.registerEvents(new LostBook(), this);
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
		pm.registerEvents(new CustomEnchantments(), this);
		pm.registerEvents(new CommandChecker(), this);
		try {
			if(Version.getCurrentVersion().comparedTo(Version.v1_11_R1) >= 0) {
				pm.registerEvents(new FireworkDamageAPI(this), this);
			}
		}catch(Exception e) {
		}
		//==========================================================================\\
		pm.registerEvents(new Bows(), this);
		pm.registerEvents(new Axes(), this);
		pm.registerEvents(new Tools(), this);
		pm.registerEvents(new Helmets(), this);
		pm.registerEvents(new PickAxes(), this);
		pm.registerEvents(new Boots(), this);
		pm.registerEvents(new Armor(), this);
		pm.registerEvents(new Swords(), this);
		if(SupportedPlugins.AAC.isPluginLoaded()) {
			pm.registerEvents(new AACSupport(), this);
		}
		if(SupportedPlugins.SILK_SPAWNERS.isPluginLoaded()) {
			pm.registerEvents(new SilkSpawners(), this);
		}
		if(SupportedPlugins.STACK_MOB.isPluginLoaded()) {
			pm.registerEvents(new StackMobSupport(), this);
		}
		if(SupportedPlugins.DAKATA.isPluginLoaded()) {
			pm.registerEvents(new DakataAntiCheatSupport(), this);
		}
		//==========================================================================\\
		new Metrics(this);
		new BukkitRunnable() {
			@Override
			public void run() {
				for(CEPlayer player : CE.getCEPlayers()) {
					CE.backupCEPlayer(player);
				}
				//				if(settings.getConfig().contains("Settings.Player-Info-Backup-Message")) {
				//					if(settings.getConfig().getBoolean("Settings.Player-Info-Backup-Message")) {
				//						Bukkit.getLogger().log(Level.INFO, "[Crazy Enchantments]>> All player data has been backed up. Next back up is in 5 minutes.");
				//					}
				//				}else {
				//					Bukkit.getLogger().log(Level.INFO, "[Crazy Enchantments]>> All player data has been backed up. Next back up is in 5 minutes.");
				//				}
			}
		}.runTaskTimerAsynchronously(this, 5 * 20 * 60, 5 * 20 * 60);
	}
	
	@Override
	public void onDisable() {
		Armor.removeAllies();
		for(Player player : Bukkit.getOnlinePlayers()) {
			CE.unloadCEPlayer(player);
		}
	}
	
	private void initCommands() {
		getCommand("blacksmith").setExecutor(new CommandBlackSmith());
		getCommand("tinkerer").setExecutor(new CommandTinkerer());
		getCommand("crazyenchantments").setExecutor(new CommandCE());
		getCommand("gkit").setExecutor(new CommandGKitz());
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		final Player player = e.getPlayer();
		CE.loadCEPlayer(player);
		if(settings.getConfig().contains("Settings.Reset-Players-Max-Health")) {
			if(settings.getConfig().getBoolean("Settings.Reset-Players-Max-Health")) {
				player.setMaxHealth(20);
			}
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				if(player.getName().equals("BadBones69")) {
					player.sendMessage(Methods.getPrefix() + Methods.color("&7This server is running your Crazy Enchantments Plugin. " + "&7It is running version &av" + Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments").getDescription().getVersion() + "&7."));
				}
				if(player.isOp()) {
					if(settings.getConfig().contains("Settings.Update-Checker")) {
						if(settings.getConfig().getBoolean("Settings.Update-Checker")) {
							Methods.hasUpdate(player);
						}
					}else {
						Methods.hasUpdate(player);
					}
				}
			}
		}.runTaskLaterAsynchronously(this, 20);
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		CE.unloadCEPlayer(e.getPlayer());
	}
}