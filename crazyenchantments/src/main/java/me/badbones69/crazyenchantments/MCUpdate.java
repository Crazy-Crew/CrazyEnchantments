package me.badbones69.crazyenchantments;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class MCUpdate implements Listener {

	private final static String VERSION = "1.1";

	private static final String BASE_URL = "http://report.mcupdate.org";

	/**
	 * Server received information.
	 */
	private static String updateMessage = "";
	private static boolean upToDate = true;
	private boolean checkUpdate = true;

	private Plugin pl;

	/**
	 * Interval of time to ping (seconds)
	 */
	private int PING_INTERVAL;

	/**
	 * The scheduled task
	 */
	private volatile BukkitTask task = null;

	/**
	 * Start up the MCUpdater.
	 *
	 * @param plugin
	 *            The plugin using this.
	 * @throws IOException
	 */
	public MCUpdate(Plugin plugin) throws IOException {
		if(plugin != null) {
			this.pl = plugin;
			// I should add a custom configuration for MCUpdate itself
			Bukkit.getPluginManager().registerEvents(this, plugin);
			setPingInterval(900);
		}
	}

	/**
	 *
	 * Start up the MCUpdater.
	 *
	 * @param plugin
	 *            The plugin using this.
	 * @param activate
	 *            Toggle if it starts the MCUpdater when used.
	 * @throws IOException
	 */
	public MCUpdate(Plugin plugin, Boolean activate) throws IOException {
		if(plugin != null) {
			this.pl = plugin;
			// I should add a custom configuration for MCUpdate itself
			Bukkit.getPluginManager().registerEvents(this, plugin);
			setPingInterval(900);
			if(activate) {
				startLogging();
			}
		}
	}

	/**
	 * Call when you wan't to start the updater.
	 *
	 * @return True if everything starts and false if it doesn't start.
	 */
	public boolean startLogging() {
		// Is MCUpdate already running?
		if(task == null) {
			// Begin hitting the server with glorious data
			task = pl.getServer().getScheduler().runTaskTimerAsynchronously(pl, () -> {
				report();
				if(!upToDate) {
					if(checkUpdate) {
						pl.getServer().getConsoleSender().sendMessage(format(updateMessage));
					}
				}
			}, 0, PING_INTERVAL * 20);
		}
		return true;
	}

	/**
	 * Call when you want to stop the updater.
	 *
	 * @return True if it successfully stoped and false if couldn't.
	 */
	public boolean stopLogging() {
		if(task != null) {
			try {
				task.cancel();
				return true;
			}catch(Exception e) {
			}
		}else {
			return true;
		}
		return false;
	}

	/**
	 * Check if MCUpdate is logging information.
	 *
	 * @return True if it is logging info and false if not.
	 */
	public Boolean isLogging() {
		return task != null;
	}

	/**
	 * Set if the updater uses the internal update checker.
	 *
	 * @param checkUpdate
	 *            True if you want to use the internal update checker and false if
	 *            not.
	 */
	public void checkUpdate(Boolean checkUpdate) {
		this.checkUpdate = checkUpdate;
	}

	/**
	 * Checks if the internal updater is active.
	 *
	 * @return True if the internal updater is activated and false if not.
	 */
	public Boolean needsUpdated() {
		return checkUpdate;
	}

	/**
	 * Set the rate the information is sent to MCUpdate.org.
	 *
	 * @param PING_INTERVAL
	 *            The rate at which the data is sent in seconds.
	 */
	public void setPingInterval(int PING_INTERVAL) {
		this.PING_INTERVAL = PING_INTERVAL;
	}

	/**
	 * Get the rate which the data is sent to MCUpdate.org.
	 *
	 * @return The rate the data is sent in seconds.
	 */
	public int getPingInterval() {
		return PING_INTERVAL;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(p.isOp() && !upToDate) {
			if(checkUpdate) {
				p.sendMessage(format(updateMessage));
			}
		}
	}

	private int getOnlinePlayers() {
		try {
			Method onlinePlayerMethod = Server.class.getMethod("getOnlinePlayers");
			if(onlinePlayerMethod.getReturnType().equals(Collection.class)) {
				return ((Collection<?>) onlinePlayerMethod.invoke(Bukkit.getServer())).size();
			}else {
				return ((Player[]) onlinePlayerMethod.invoke(Bukkit.getServer())).length;
			}
		}catch(Exception ex) {
		}
		return 0;
	}

	private void report() {
		String ver = pl.getDescription().getVersion();
		String name = pl.getDescription().getName();
		int playersOnline = this.getOnlinePlayers();
		boolean onlineMode = pl.getServer().getOnlineMode();
		String serverVersion = pl.getServer().getVersion();

		String osname = System.getProperty("os.name");
		String osarch = System.getProperty("os.arch");
		String osversion = System.getProperty("os.version");
		String java_version = System.getProperty("java.version");
		int coreCount = Runtime.getRuntime().availableProcessors();

		String report = "{ \"report\": {";
		report += toJson("plugin", name) + ",";
		report += toJson("version", ver) + ",";
		report += toJson("playersonline", playersOnline + "") + ",";
		report += toJson("onlinemode", onlineMode + "") + ",";
		report += toJson("serverversion", serverVersion) + ",";

		report += toJson("osname", osname) + ",";
		report += toJson("osarch", osarch) + ",";
		report += toJson("osversion", osversion) + ",";
		report += toJson("javaversion", java_version) + ",";
		report += toJson("corecount", coreCount + "") + "";

		report += "} }";

		byte[] data = report.getBytes();

		try {

			URL url = new URL(BASE_URL);
			URLConnection c = url.openConnection();
			c.setConnectTimeout(2500);
			c.setReadTimeout(3500);

			c.addRequestProperty("User-Agent", "MCUPDATE/" + VERSION);
			c.addRequestProperty("Content-Type", "application/json");
			c.addRequestProperty("Content-Length", Integer.toString(data.length));
			c.addRequestProperty("Accept", "application/json");
			c.addRequestProperty("Connection", "close");

			c.setDoOutput(true);

			OutputStream os = c.getOutputStream();
			os.write(data);
			os.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
			String endData = br.readLine().trim();

			String serverMessage = getString(endData, "message");
			String cVersion = getString(endData, "pl_Version");
			updateMessage = getString(endData, "update_Message");

			if(serverMessage != null) {
				if(!serverMessage.equals("ERROR")) {
					if(cVersion != null) {
						if(!ver.equals(cVersion)) {
							upToDate = false;
						}
					}
				}
			}
			br.close();

		}catch(Exception ignored) {
		}
	}

	private String getString(String data, String key) {
		String dat = data.replace("{ \"Response\": {\"", "");
		dat = dat.replace("\"} }", "");
		List<String> list = Arrays.asList(dat.split("\",\""));

		for(String stub : list) {
			List<String> list2 = Arrays.asList(stub.split("\":\""));
			if(key.equals(list2.get(0))) {
				return list2.get(1);
			}
		}
		return null;
	}

	private static String toJson(String key, String value) {
		return "\"" + key + "\":\"" + value + "\"";
	}

	private static String format(String format) {
		if(format != null) {
			return ChatColor.translateAlternateColorCodes('&', format);
		}else {
			return "";
		}
	}

}