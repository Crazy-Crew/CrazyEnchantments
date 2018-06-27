/*
 *      Copyright 2018 (c) Massive Statistics LLC - All Rights Reserved
 * This file may only be used in conjunction with the 'MassiveStats' service.
 */

package com.massivestats;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * MassiveStats collects plugin and server information for plugin authors.
 * You can learn more at our website: https://www.massivestats.com/
 *
 * @version 3.0
 * @author Sam Jakob Harker, Brianna Hazel O'Keefe
 */
@SuppressWarnings("all")
public class MassiveStats implements Listener {
	
	/* START: MASSIVESTATS SETTINGS */
	public static final int CLIENT_VERSION = 0; // v3.0
	public static final String API_URL = "https://report.massivestats.com/v2/";
	
	public static final String MASSIVE_UPDATE_PERMISSION = "massivestats.update";
	/* END: MASSIVESTATS SETTINGS */
	
	private MassiveStatsUpdateTask task = null;
	private int pingInterval;
	
	private MassiveStatsDataResponse lastResponse;
	private boolean listenerDisabled;
	
	private final JavaPlugin plugin;
	
	private Class jsonElement;
	private Class jsonParser;
	private Class jsonObject;
	private Class jsonPrimitive;
	
	/**
	 * @param plugin The plugin you wish to collect data for.
	 * @author Sam Jakob Harker
	 */
	public MassiveStats(JavaPlugin plugin) {
		this(plugin, 900); // default value: 900 seconds (= 15 minutes)
	}
	
	/**
	 * @param plugin The plugin you wish to collect data for.
	 * @param pingInterval Duration between requests.
	 * @author Sam Jakob Harker
	 */
	public MassiveStats(JavaPlugin plugin, int pingInterval) {
		try {
			jsonElement = Class.forName("com.google.gson.JsonElement");
			jsonParser = Class.forName("com.google.gson.JsonParser");
			jsonObject = Class.forName("com.google.gson.JsonObject");
			jsonPrimitive = Class.forName("com.google.gson.JsonPrimitive");
		}catch(ClassNotFoundException ex) {
			// Gson not included in classpath (so use NMS version)
			try {
				jsonElement = Class.forName("net.minecraft.util.com.google.gson.JsonElement");
				jsonParser = Class.forName("net.minecraft.util.com.google.gson.JsonParser");
				jsonObject = Class.forName("net.minecraft.util.com.google.gson.JsonObject");
				jsonPrimitive = Class.forName("net.minecraft.util.com.google.gson.JsonPrimitive");
			}catch(ClassNotFoundException ignored) {
				Bukkit.getLogger().severe("MassiveStats could not find an instance/version of Gson to use.");
				this.plugin = null;
				return;
			}
		}
		
		// Ensure the pingInterval that is set is reasonable.
		if(pingInterval < 10 || pingInterval > 86400) {
			pingInterval = 900;
		}
		
		// Ensure that a plugin instance has been provided.
		if(plugin == null) {
			throw new IllegalArgumentException("You must provide a plugin for MassiveStats to collect data for!");
		}
		
		// Set the ping interval.
		this.pingInterval = pingInterval;
		// Set the plugin reference.
		this.plugin = plugin;
		// and start sending data to the MassiveStats server immediately.
		start();
		
		// Register join/leave events for the plugin
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	/**
	 * Gets whether or not the built-in MassiveStats {@link org.bukkit.event.player.PlayerJoinEvent} listener is enabled.
	 * @return Whether or not the MassiveStats listener is enabled.
	 */
	public boolean isListenerDisabled() {
		return listenerDisabled;
	}
	
	/**
	 * Sets whether or not the built-in MassiveStats {@link org.bukkit.event.player.PlayerJoinEvent} listener is enabled.
	 * @param listenerDisabled Whether or not the MassiveStats listener is enabled.
	 */
	public void setListenerDisabled(boolean listenerDisabled) {
		this.listenerDisabled = listenerDisabled;
	}
	
	/**
	 * Start the MassiveStats reporting timer.
	 * If the timer is already running, this method will do nothing.
	 * @author Sam Jakob Harker
	 */
	public void start() {
		if(this.plugin == null) {
			Bukkit.getLogger().severe("MassiveStats could not find an instance/version of Gson to use and thus cannot start.");
			return;
		}
		
		if(task == null) {
			// If the API endpoint URL is invalid, don't start a new task to prevent the user from being spammed.
			try {
				new URL(MassiveStats.API_URL);
			}catch(MalformedURLException ex) {
				getPlugin()
				.getLogger().warning("You have specified an invalid API endpoint for MassiveStats.");
				return;
			}
			
			task = new MassiveStatsUpdateTask(this);
			task.runTaskTimerAsynchronously(plugin, 0L, pingInterval * 20L);
		}
	}
	
	/**
	 * Stop the MassiveStats reporting timer.
	 * Requests will no longer be sent to the server - or until {@link #start()} is invoked.
	 * @author Sam Jakob Harker
	 */
	public void stop() {
		if(task == null) {
			return;
		}
		
		task.cancel();
		task = null;
	}
	
	/**
	 * Sets the duration, in seconds, that MassiveStats should wait before sending another request to the server.
	 * @param pingInterval Duration between requests.
	 * @author Sam Jakob Harker
	 */
	public void setPingInterval(int pingInterval) {
		this.pingInterval = pingInterval;
		
		stop();
		start();
	}
	
	/**
	 * Returns the duration, in seconds, that MassiveStats will wait before sending another request to the server.
	 * @return Duration between requests.
	 * @author Sam Jakob Harker
	 */
	public int getPingInterval() {
		return pingInterval;
	}
	
	/**
	 * Returns the plugin that this MassiveStats instance is collecting data for.
	 * @return MassiveStats instance plugin.
	 * @author Sam Jakob Harker
	 */
	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	void setLastResponse(MassiveStatsDataResponse lastResponse) {
		this.lastResponse = lastResponse;
	}
	
	/**
	 * Returns the contents of the last response from the MassiveStats server.
	 * @return MassiveStats server response.
	 * @author Sam Jakob Harker
	 */
	public MassiveStatsDataResponse getLastResponse() {
		return lastResponse;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		// Ensure the listener should be active
		if(lastResponse == null || listenerDisabled) {
			return;
		}
		
		// Of course, only notify the user if the plugin is not up to date.
		if(lastResponse.isUpToDate()) {
			return;
		}
		
		// and only notify operators - or players with the correct permission.
		if(!event.getPlayer().isOp() && !event.getPlayer().hasPermission(MassiveStats.MASSIVE_UPDATE_PERMISSION)) {
			return;
		}
		
		event.getPlayer().sendMessage(lastResponse.getUpdateMessage());
	}
	
	Class getJsonElement() {
		return jsonElement;
	}
	
	Class getJsonParser() {
		return jsonParser;
	}
	
	Class getJsonObject() {
		return jsonObject;
	}
	
	Class getJsonPrimitive() {
		return jsonPrimitive;
	}
	
}

class MassiveStatsUpdateTask extends BukkitRunnable {
	
	private final MassiveStats instance;
	
	MassiveStatsUpdateTask(MassiveStats requester) {
		instance = requester;
	}
	
	@Override
	@SuppressWarnings("all")
	public void run() {
		try {
			// Generate the request payload and serialize it as JSON.
			String payload = new MassiveStatsDataRequest(instance).serialize();
			
			// Then create a new HttpsUrlConnection to the API server and open it.
			HttpsURLConnection connection = (HttpsURLConnection) new URL(MassiveStats.API_URL).openConnection();
			
			// Ensure that we don't hang the server with our 'dang shenanigans'.
			connection.setConnectTimeout(2500);
			connection.setReadTimeout(3500);
			
			// Set the all-important request headers before we begin POSTing...
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			connection.setRequestProperty("User-Agent", "Massive/" + MassiveStats.CLIENT_VERSION);
			
			// Open the output stream, write the payload, and then close the stream.
			connection.setDoOutput(true);
			DataOutputStream output = new DataOutputStream(connection.getOutputStream());
			output.writeBytes(payload);
			output.flush();
			output.close();
			
			// Ensure that the server was happy with our data.
			int responseCode = connection.getResponseCode();
			if(responseCode != 200) {
				throw new IOException();
			}
			
			// Now, read the server's response to our payload...
			BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder response = new StringBuilder();
			
			// ...line by line.
			String line;
			while((line = input.readLine()) != null) {
				response.append(line);
			}
			input.close();
			
			// Now, we parse the JSON object.
			try {
				if(response.toString().contains("ERR_DATA_MISSING")) {
					Bukkit.getLogger().severe("MassiveStats has encountered an error for the following plugin: "
					+ instance.getPlugin().getName());
					instance.stop();
					return;
				}
				
				Object parser = instance.getJsonParser().newInstance();
				
				// JsonElement
				Object serverResponseRaw =
				parser.getClass().getMethod("parse", String.class).invoke(parser, response.toString());
				
				// JsonObject
				Object serverResponse = serverResponseRaw.getClass().getMethod("getAsJsonObject", null)
				.invoke(serverResponseRaw, null);
				
				Method serverResponseGet = instance.getJsonObject().getMethod("get", String.class);
				
				Method getAsBoolean =
				instance.getJsonPrimitive().getMethod("getAsBoolean", null);
				Method getAsString =
				instance.getJsonPrimitive().getMethod("getAsString", null);
				
				if(serverResponseGet.invoke(serverResponse, "upToDate") == null) {
					Bukkit.getLogger().severe("MassiveStats has encountered an error for the following plugin: "
					+ instance.getPlugin().getName());
					instance.stop();
					return;
				}
				
				if(serverResponseGet.invoke(serverResponse, "notice") != null) {
					Bukkit.getLogger().severe(
					(String) getAsString.invoke(serverResponseGet.invoke(serverResponse, "notice"))
					);
					instance.stop();
					return;
				}
				
				boolean upToDate = (boolean) getAsBoolean.invoke(serverResponseGet.invoke(serverResponse, "upToDate"), null);
				String latestVersion = (String) getAsString.invoke(serverResponseGet.invoke(serverResponse, "latestVersion"), null);
				String updateMessage = ChatColor.translateAlternateColorCodes(
				'&', (String) getAsString.invoke(serverResponseGet.invoke(serverResponse, "updateMessage"), null)
				);
				
				instance.setLastResponse(new MassiveStatsDataResponse(
				upToDate, latestVersion, updateMessage
				));
				
			}catch(IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
				instance.getPlugin()
				.getLogger().warning("MassiveStats returned an invalid response for this plugin.");
			}
			
			// Finally, call an event to mark the update.
		}catch(MalformedURLException ex) {
			instance.getPlugin()
			.getLogger().warning("You have specified an invalid API endpoint for MassiveStats.");
		}catch(IOException ex) {
			instance.getPlugin()
			.getLogger().warning("MassiveStats was unable to communicate with its API endpoint.");
		}
	}
	
}

class MassiveStatsDataRequest {
	
	private Object jsonObject;
	
	MassiveStatsDataRequest(MassiveStats requester) {
		try {
			jsonObject = requester.getJsonObject().newInstance();
			
			Method add =
			requester.getJsonObject().newInstance().getClass().getMethod("add", String.class, requester.getJsonElement());
			Method addPropertyString =
			requester.getJsonObject().newInstance().getClass().getMethod("addProperty", String.class, String.class);
			Method addPropertyNumber =
			requester.getJsonObject().newInstance().getClass().getMethod("addProperty", String.class, Number.class);
			Method addPropertyBoolean =
			requester.getJsonObject().newInstance().getClass().getMethod("addProperty", String.class, Boolean.class);
			
			addPropertyNumber.invoke(jsonObject, "now", System.currentTimeMillis());
			
			/* PLUGIN DATA */
			Object pluginObject = jsonObject.getClass().newInstance();
			addPropertyString.invoke(pluginObject, "name", requester.getPlugin().getDescription().getName());
			addPropertyString.invoke(pluginObject, "version", requester.getPlugin().getDescription().getVersion());
			add.invoke(jsonObject, "plugin", pluginObject);
			
			/* SERVER DATA */
			Object minecraftServerObject = jsonObject.getClass().newInstance();
			addPropertyNumber.invoke(minecraftServerObject, "players", Bukkit.getServer().getOnlinePlayers().size());
			addPropertyBoolean.invoke(minecraftServerObject, "onlineMode", Bukkit.getServer().getOnlineMode());
			addPropertyString.invoke(minecraftServerObject, "version", Bukkit.getServer().getVersion());
			
			Object javaServerObject = jsonObject.getClass().newInstance();
			addPropertyString.invoke(javaServerObject, "version", System.getProperty("java.version"));
			
			Object osServerObject = jsonObject.getClass().newInstance();
			addPropertyString.invoke(osServerObject, "name", System.getProperty("os.name"));
			addPropertyString.invoke(osServerObject, "arch", System.getProperty("os.arch"));
			addPropertyString.invoke(osServerObject, "version", System.getProperty("os.version"));
			
			Object hardwareServerObject = jsonObject.getClass().newInstance();
			addPropertyNumber.invoke(hardwareServerObject, "cores", Runtime.getRuntime().availableProcessors());
			
			Object serverObject = jsonObject.getClass().newInstance();
			add.invoke(serverObject, "minecraft", minecraftServerObject);
			add.invoke(serverObject, "java", javaServerObject);
			add.invoke(serverObject, "os", osServerObject);
			add.invoke(serverObject, "hardware", hardwareServerObject);
			
			add.invoke(jsonObject, "server", serverObject);
			
			/* MASSIVE DATA */
			Object massiveObject = jsonObject.getClass().newInstance();
			addPropertyNumber.invoke(massiveObject, "version", MassiveStats.CLIENT_VERSION);
			addPropertyNumber.invoke(massiveObject, "pingInterval", requester.getPingInterval());
			
			//object.add("Massive", massiveObject);
			add.invoke(jsonObject, "Massive", massiveObject);
		}catch(IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("all")
	public String serialize() {
		//return object.toString();
		try {
			Method toString = jsonObject.getClass().getMethod("toString", null);
			return (String) toString.invoke(jsonObject);
		}catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}

@SuppressWarnings("unused") final class MassiveStatsDataResponse {
	
	private final boolean isUpToDate;
	private final String newVersion;
	private final String updateMessage;
	
	MassiveStatsDataResponse(boolean isUpToDate, String newVersion, String updateMessage) {
		this.isUpToDate = isUpToDate;
		
		if(!isUpToDate) {
			this.newVersion = newVersion;
			this.updateMessage = updateMessage;
			return;
		}
		
		this.newVersion = null;
		this.updateMessage = null;
	}
	
	/**
	 * Indicates whether or not this version of the plugin is the latest.
	 * True = This is the latest version of the plugin.
	 * False = There is an update available.
	 * @return Whether or not there is an update available.
	 */
	public boolean isUpToDate() {
		return isUpToDate;
	}
	
	/**
	 * Gets the name of the latest version. If this is the latest version, it returns null.
	 * @return The name of the latest version.
	 */
	public String getLatestVersion() {
		return newVersion;
	}
	
	/**
	 * Gets the message to display, convincing the user to update to the new version of the plugin.
	 * @return The update message to display.
	 */
	public String getUpdateMessage() {
		return updateMessage;
	}
	
}