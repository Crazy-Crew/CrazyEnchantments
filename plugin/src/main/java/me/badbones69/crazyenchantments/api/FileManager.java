package me.badbones69.crazyenchantments.api;

import me.badbones69.crazyenchantments.multisupport.Version;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author BadBones69
 * @version v1.0
 *
 */
public class FileManager {
	
	private Plugin plugin;
	private String prefix = "";
	private Boolean log = false;
	private HashMap<Files, File> files = new HashMap<>();
	private ArrayList<String> homeFolders = new ArrayList<>();
	private ArrayList<CustomFile> customFiles = new ArrayList<>();
	private HashMap<String, String> autoGenerateFiles = new HashMap<>();
	private HashMap<Files, FileConfiguration> configurations = new HashMap<>();
	
	private static FileManager instance = new FileManager();
	
	public static FileManager getInstance() {
		return instance;
	}
	
	/**
	 * Sets up the plugin and loads all necessary files.
	 * @param plugin The plugin this is getting loading for.
	 */
	public FileManager setup(Plugin plugin) {
		prefix = "[" + plugin.getName() + "] ";
		this.plugin = plugin;
		if(!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdirs();
		}
		files.clear();
		customFiles.clear();
		//Loads all the normal static files.
		for(Files file : Files.values()) {
			File newFile = new File(plugin.getDataFolder(), file.getFileLocation());
			if(log) System.out.println(prefix + "Loading the " + file.getFileName());
			if(!newFile.exists()) {
				try {
					File serverFile = new File(plugin.getDataFolder(), "/" + file.getFileLocation());
					InputStream jarFile = getClass().getResourceAsStream("/" + file.getFileJar());
					copyFile(jarFile, serverFile);
				}catch(Exception e) {
					if(log) System.out.println(prefix + "Failed to load " + file.getFileName());
					e.printStackTrace();
					continue;
				}
			}
			files.put(file, newFile);
			configurations.put(file, YamlConfiguration.loadConfiguration(newFile));
			if(log) System.out.println(prefix + "Successfully loaded " + file.getFileName());
		}
		//Starts to load all the custom files.
		if(homeFolders.size() > 0) {
			if(log) System.out.println(prefix + "Loading custom files.");
			for(String homeFolder : homeFolders) {
				File homeFile = new File(plugin.getDataFolder(), "/" + homeFolder);
				if(homeFile.exists()) {
					String[] list = homeFile.list();
					if(list != null) {
						for(String name : list) {
							if(name.endsWith(".yml")) {
								CustomFile file = new CustomFile(name, homeFolder, plugin);
								if(file.exists()) {
									customFiles.add(file);
									if(log) System.out.println(prefix + "Loaded new custom file: " + homeFolder + "/" + name + ".");
								}
							}
						}
					}
					
				}else {
					homeFile.mkdir();
					if(log) System.out.println(prefix + "The folder " + homeFolder + "/ was not found so it was created.");
					for(String fileName : autoGenerateFiles.keySet()) {
						if(autoGenerateFiles.get(fileName).equalsIgnoreCase(homeFolder)) {
							homeFolder = autoGenerateFiles.get(fileName);
							try {
								File serverFile = new File(plugin.getDataFolder(), homeFolder + "/" + fileName);
								InputStream jarFile = getClass().getResourceAsStream(homeFolder + "/" + fileName);
								copyFile(jarFile, serverFile);
								if(fileName.toLowerCase().endsWith(".yml")) {
									customFiles.add(new CustomFile(fileName, homeFolder, plugin));
								}
								if(log) System.out.println(prefix + "Created new default file: " + homeFolder + "/" + fileName + ".");
							}catch(Exception e) {
								if(log) System.out.println(prefix + "Failed to create new default file: " + homeFolder + "/" + fileName + "!");
								e.printStackTrace();
							}
						}
					}
				}
			}
			if(log) System.out.println(prefix + "Finished loading custom files.");
		}
		return this;
	}
	
	/**
	 * Turn on the logger system for the FileManager.
	 * @param log True to turn it on and false for it to be off.
	 */
	public FileManager logInfo(Boolean log) {
		this.log = log;
		return this;
	}
	
	/**
	 * Check if the logger is logging in console.
	 * @return True if it is and false if it isn't.
	 */
	public Boolean isLogging() {
		return log;
	}
	
	/**
	 * Register a folder that has custom files in it. Make sure to have a "/" in front of the folder name.
	 * @param homeFolder The folder that has custom files in it.
	 */
	public FileManager registerCustomFilesFolder(String homeFolder) {
		homeFolders.add(homeFolder);
		return this;
	}
	
	/**
	 * Unregister a folder that has custom files in it. Make sure to have a "/" in front of the folder name.
	 * @param homeFolder The folder with custom files in it.
	 */
	public FileManager unregisterCustomFilesFolder(String homeFolder) {
		homeFolders.remove(homeFolder);
		return this;
	}
	
	/**
	 * Register a file that needs to be generated when it's home folder doesn't exist. Make sure to have a "/" in front of the home folder's name.
	 * @param fileName The name of the file you want to auto-generate when the folder doesn't exist.
	 * @param homeFolder The folder that has custom files in it.
	 */
	public FileManager registerDefaultGenerateFiles(String fileName, String homeFolder) {
		autoGenerateFiles.put(fileName, homeFolder);
		return this;
	}
	
	/**
	 * Unregister a file that doesn't need to be generated when it's home folder doesn't exist. Make sure to have a "/" in front of the home folder's name.
	 * @param fileName The file that you want to remove from auto-generating.
	 */
	public FileManager unregisterDefaultGenerateFiles(String fileName) {
		autoGenerateFiles.remove(fileName);
		return this;
	}
	
	/**
	 * Gets the file from the system.
	 * @return The file from the system.
	 */
	public FileConfiguration getFile(Files file) {
		return configurations.get(file);
	}
	
	/**
	 * Get a custom file from the loaded custom files instead of a hardcoded one.
	 * This allows you to get custom files like Per player data files.
	 * @param name Name of the crate you want. (Without the .yml)
	 * @return The custom file you wanted otherwise if not found will return null.
	 */
	public CustomFile getFile(String name) {
		for(CustomFile file : customFiles) {
			if(file.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase())) {
				return file;
			}
		}
		return null;
	}
	
	/**
	 * Saves the file from the loaded state to the file system.
	 */
	public void saveFile(Files file) {
		try {
			configurations.get(file).save(files.get(file));
		}catch(IOException e) {
			System.out.println(prefix + "Could not save " + file.getFileName() + "!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Save a custom file.
	 * @param name The name of the custom file.
	 */
	public void saveFile(String name) {
		CustomFile file = getFile(name);
		if(file != null) {
			try {
				file.getFile().save(new File(plugin.getDataFolder(), file.getHomeFolder() + "/" + file.getFileName()));
				if(log) System.out.println(prefix + "Successfuly saved the " + file.getFileName() + ".");
			}catch(Exception e) {
				System.out.println(prefix + "Could not save " + file.getFileName() + "!");
				e.printStackTrace();
			}
		}else {
			if(log) System.out.println(prefix + "The file " + name + ".yml could not be found!");
		}
	}
	
	/**
	 * Save a custom file.
	 * @param file The custom file you are saving.
	 * @return True if the file saved correct and false if there was an error.
	 */
	public Boolean saveFile(CustomFile file) {
		return file.saveFile();
	}
	
	/**
	 * Overrides the loaded state file and loads the file systems file.
	 */
	public void reloadFile(Files file) {
		configurations.put(file, YamlConfiguration.loadConfiguration(files.get(file)));
	}
	
	/**
	 * Overrides the loaded state file and loads the file systems file.
	 */
	public void reloadFile(String name) {
		CustomFile file = getFile(name);
		if(file != null) {
			try {
				file.file = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "/" + file.getHomeFolder() + "/" + file.getFileName()));
				if(log) System.out.println(prefix + "Successfully reload the " + file.getFileName() + ".");
			}catch(Exception e) {
				System.out.println(prefix + "Could not reload the " + file.getFileName() + "!");
				e.printStackTrace();
			}
		}else {
			if(log) System.out.println(prefix + "The file " + name + ".yml could not be found!");
		}
	}
	
	/**
	 * Overrides the loaded state file and loads the filesystems file.
	 * @return True if it reloaded correct and false if the file wasn't found.
	 */
	public Boolean reloadFile(CustomFile file) {
		return file.reloadFile();
	}
	
	/**
	 * Was found here: https://bukkit.org/threads/extracting-file-from-jar.16962
	 */
	private void copyFile(InputStream in, File out) throws Exception {
		try(FileOutputStream fos = new FileOutputStream(out)) {
			byte[] buf = new byte[1024];
			int i;
			while((i = in.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		}finally {
			if(in != null) {
				in.close();
			}
			
		}
	}
	
	public enum Files {
		
		//ENUM_NAME("fileName.yml", "fileLocation.yml"),
		//ENUM_NAME("fileName.yml", "newFileLocation.yml", "oldFileLocation.yml"),
		CONFIG("config.yml", "config.yml", "config1.13-Up.yml", "config1.12.2-Down.yml"),
		BLOCKLIST("BlockList.yml", "BlockList.yml", "BlockList1.13-Up.yml", "BlockList1.12.2-Down.yml"),
		DATA("Data.yml", "Data.yml"),
		ENCHANTMENTS("Enchantments.yml", "Enchantments.yml"),
		GKITZ("GKitz.yml", "GKitz.yml", "GKitz1.13-Up.yml", "GKitz1.12.2-Down.yml"),
		MESSAGES("Messages.yml", "Messages.yml", "Messages1.13-Up.yml", "Messages1.12.2-Down.yml"),
		SIGNS("Signs.yml", "Signs.yml"),
		TINKER("Tinker.yml", "Tinker.yml", "Tinker1.13-Up.yml", "Tinker1.12.2-Down.yml");
		
		private String fileName;
		private String fileJar;
		private String fileLocation;
		
		/**
		 * The files that the server will try and load.
		 * @param fileName The file name that will be in the plugin's folder.
		 * @param fileLocation The location the file in the plugin's folder.
		 */
		private Files(String fileName, String fileLocation) {
			this(fileName, fileLocation, fileLocation);
		}
		
		/**
		 * The files that the server will try and load.
		 * @param fileName The file name that will be in the plugin's folder.
		 * @param fileLocation The location of the file will be in the plugin's folder.
		 * @param fileJar The location of the file in the jar.
		 */
		private Files(String fileName, String fileLocation, String fileJar) {
			this.fileName = fileName;
			this.fileLocation = fileLocation;
			this.fileJar = fileJar;
		}
		
		/**
		 * The files that the server will try and load.
		 * @param fileName The file name that will be in the plugin's folder.
		 * @param fileLocation The location of the file will be in the plugin's folder.
		 * @param newFileJar The location of the 1.13+ file version in the jar.
		 * @param oldFileJar The location of the 1.12.2- file version in the jar.
		 */
		private Files(String fileName, String fileLocation, String newFileJar, String oldFileJar) {
			this(fileName, fileLocation, Version.getCurrentVersion().isNewer(Version.v1_12_R1) ? newFileJar : oldFileJar);
		}
		
		/**
		 * Get the name of the file.
		 * @return The name of the file.
		 */
		public String getFileName() {
			return fileName;
		}
		
		/**
		 * Get the location of the file in the plugin's folder.
		 * @return The location of the file in the plugin's folder.
		 */
		public String getFileLocation() {
			return fileLocation;
		}
		
		/**
		 * Get the location of the file in the jar.
		 * @return The location of the file in the jar.
		 */
		public String getFileJar() {
			return fileJar;
		}
		
		/**
		 * Gets the file from the system.
		 * @return The file from the system.
		 */
		public FileConfiguration getFile() {
			return getInstance().getFile(this);
		}
		
		/**
		 * Saves the file from the loaded state to the file system.
		 */
		public void saveFile() {
			getInstance().saveFile(this);
		}
		
		/**
		 * Overrides the loaded state file and loads the file systems file.
		 */
		public void relaodFile() {
			getInstance().reloadFile(this);
		}
		
	}
	
	public class CustomFile {
		
		private String name;
		private Plugin plugin;
		private String fileName;
		private String homeFolder;
		private FileConfiguration file;
		
		/**
		 * A custom file that is being made.
		 * @param name Name of the file.
		 * @param homeFolder The home folder of the file.
		 * @param plugin The plugin the files belong to.
		 */
		public CustomFile(String name, String homeFolder, Plugin plugin) {
			this.name = name.replace(".yml", "");
			this.plugin = plugin;
			this.fileName = name;
			this.homeFolder = homeFolder;
			if(new File(plugin.getDataFolder(), "/" + homeFolder).exists()) {
				if(new File(plugin.getDataFolder(), "/" + homeFolder + "/" + name).exists()) {
					file = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "/" + homeFolder + "/" + name));
				}else {
					file = null;
				}
			}else {
				new File(plugin.getDataFolder(), "/" + homeFolder).mkdir();
				if(log) System.out.println(prefix + "The folder " + homeFolder + "/ was not found so it was created.");
				file = null;
			}
		}
		
		/**
		 * Get the name of the file without the .yml part.
		 * @return The name of the file without the .yml.
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Get the full name of the file.
		 * @return Full name of the file.
		 */
		public String getFileName() {
			return fileName;
		}
		
		/**
		 * Get the name of the home folder of the file.
		 * @return The name of the home folder the files are in.
		 */
		public String getHomeFolder() {
			return homeFolder;
		}
		
		/**
		 * Get the plugin the file belongs to.
		 * @return The plugin the file belongs to.
		 */
		public Plugin getPlugin() {
			return plugin;
		}
		
		/**
		 * Get the ConfigurationFile.
		 * @return The ConfigurationFile of this file.
		 */
		public FileConfiguration getFile() {
			return file;
		}
		
		/**
		 * Check if the file actually exists in the file system.
		 * @return True if it does and false if it doesn't.
		 */
		public Boolean exists() {
			return file != null;
		}
		
		/**
		 * Save the custom file.
		 * @return True if it saved correct and false if something went wrong.
		 */
		public Boolean saveFile() {
			if(file != null) {
				try {
					file.save(new File(plugin.getDataFolder(), homeFolder + "/" + fileName));
					if(log) System.out.println(prefix + "Successfuly saved the " + fileName + ".");
					return true;
				}catch(Exception e) {
					System.out.println(prefix + "Could not save " + fileName + "!");
					e.printStackTrace();
					return false;
				}
			}else {
				if(log) System.out.println(prefix + "There was a null custom file that could not be found!");
			}
			return false;
		}
		
		/**
		 * Overrides the loaded state file and loads the filesystems file.
		 * @return True if it reloaded correct and false if the file wasn't found or errored.
		 */
		public Boolean reloadFile() {
			if(file != null) {
				try {
					file = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "/" + homeFolder + "/" + fileName));
					if(log) System.out.println(prefix + "Successfuly reload the " + fileName + ".");
					return true;
				}catch(Exception e) {
					System.out.println(prefix + "Could not reload the " + fileName + "!");
					e.printStackTrace();
				}
			}else {
				if(log) System.out.println(prefix + "There was a null custom file that was not found!");
			}
			return false;
		}
		
	}
	
}