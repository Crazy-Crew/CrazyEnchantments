package me.badbones69.crazyenchantments;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SettingsManager {

	static SettingsManager instance = new SettingsManager();

	public static SettingsManager getInstance() {
		return instance;
	}

	Plugin p;

	FileConfiguration config;
	File cfile;

	FileConfiguration gkitz;
	File gfile;

	FileConfiguration data;
	File dfile;

	FileConfiguration enchs;
	File efile;

	FileConfiguration msg;
	File mfile;

	FileConfiguration cenchs;
	File cefile;

	FileConfiguration signs;
	File sfile;

	FileConfiguration tinker;
	File tfile;

	FileConfiguration blocklist;
	File blfile;

	public void setup(Plugin p) {
		if(!p.getDataFolder().exists()) {
			p.getDataFolder().mkdir();
		}
		cfile = new File(p.getDataFolder(), "config.yml");
		if(!cfile.exists()) {
			try {
				File en = new File(p.getDataFolder(), "/config.yml");
				InputStream E = getClass().getResourceAsStream("/config.yml");
				copyFile(E, en);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(cfile);

		gfile = new File(p.getDataFolder(), "GKitz.yml");
		if(!gfile.exists()) {
			try {
				File en = new File(p.getDataFolder(), "/GKitz.yml");
				InputStream E = getClass().getResourceAsStream("/GKitz.yml");
				copyFile(E, en);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		gkitz = YamlConfiguration.loadConfiguration(gfile);

		dfile = new File(p.getDataFolder(), "Data.yml");
		if(!dfile.exists()) {
			try {
				File en = new File(p.getDataFolder(), "/Data.yml");
				InputStream E = getClass().getResourceAsStream("/Data.yml");
				copyFile(E, en);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		data = YamlConfiguration.loadConfiguration(dfile);

		efile = new File(p.getDataFolder(), "Enchantments.yml");
		if(!efile.exists()) {
			try {
				File en = new File(p.getDataFolder(), "/Enchantments.yml");
				InputStream E = getClass().getResourceAsStream("/Enchantments.yml");
				copyFile(E, en);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		enchs = YamlConfiguration.loadConfiguration(efile);

		mfile = new File(p.getDataFolder(), "Messages.yml");
		if(!mfile.exists()) {
			try {
				File en = new File(p.getDataFolder(), "/Messages.yml");
				InputStream E = getClass().getResourceAsStream("/Messages.yml");
				copyFile(E, en);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		msg = YamlConfiguration.loadConfiguration(mfile);

		cefile = new File(p.getDataFolder(), "CustomEnchantments.yml");
		if(!cefile.exists()) {
			try {
				File en = new File(p.getDataFolder(), "/CustomEnchantments.yml");
				InputStream E = getClass().getResourceAsStream("/CustomEnchantments.yml");
				copyFile(E, en);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		cenchs = YamlConfiguration.loadConfiguration(cefile);

		sfile = new File(p.getDataFolder(), "Signs.yml");
		if(!sfile.exists()) {
			try {
				File en = new File(p.getDataFolder(), "/Signs.yml");
				InputStream E = getClass().getResourceAsStream("/Signs.yml");
				copyFile(E, en);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		signs = YamlConfiguration.loadConfiguration(sfile);

		tfile = new File(p.getDataFolder(), "Tinker.yml");
		if(!tfile.exists()) {
			try {
				File en = new File(p.getDataFolder(), "/Tinker.yml");
				InputStream E = getClass().getResourceAsStream("/Tinker.yml");
				copyFile(E, en);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		tinker = YamlConfiguration.loadConfiguration(tfile);

		blfile = new File(p.getDataFolder(), "BlockList.yml");
		if(!blfile.exists()) {
			try {
				File en = new File(p.getDataFolder(), "/BlockList.yml");
				InputStream E = getClass().getResourceAsStream("/BlockList.yml");
				copyFile(E, en);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		blocklist = YamlConfiguration.loadConfiguration(blfile);

	}

	public FileConfiguration getGKitz() {
		return gkitz;
	}

	public FileConfiguration getData() {
		return data;
	}

	public FileConfiguration getBlockList() {
		return blocklist;
	}

	public FileConfiguration getTinker() {
		return tinker;
	}

	public FileConfiguration getSigns() {
		return signs;
	}

	public FileConfiguration getCustomEnchs() {
		return cenchs;
	}

	public FileConfiguration getEnchs() {
		return enchs;
	}

	public FileConfiguration getMessages() {
		return msg;
	}

	public void saveData() {
		try {
			data.save(dfile);
		}catch(IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save Data.yml!");
		}
	}

	public void saveSigns() {
		try {
			signs.save(sfile);
		}catch(IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save Signs.yml!");
		}
	}

	public void saveEnchs() {
		try {
			enchs.save(efile);
		}catch(IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save Enchantments.yml!");
		}
	}

	public void reloadGKitz() {
		gkitz = YamlConfiguration.loadConfiguration(gfile);
	}

	public void reloadData() {
		data = YamlConfiguration.loadConfiguration(dfile);
	}

	public void reloadBlockList() {
		blocklist = YamlConfiguration.loadConfiguration(blfile);
	}

	public void reloadTinker() {
		tinker = YamlConfiguration.loadConfiguration(tfile);
	}

	public void reloadSigns() {
		signs = YamlConfiguration.loadConfiguration(sfile);
	}

	public void reloadMessages() {
		msg = YamlConfiguration.loadConfiguration(mfile);
	}

	public void reloadCustomEnchs() {
		cenchs = YamlConfiguration.loadConfiguration(cefile);
	}

	public void reloadEnchs() {
		enchs = YamlConfiguration.loadConfiguration(efile);
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public void saveConfig() {
		try {
			config.save(cfile);
		}catch(IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "Could not save config.yml!");
		}
	}

	public void reloadConfig() {
		config = YamlConfiguration.loadConfiguration(cfile);
	}

	public PluginDescriptionFile getDesc() {
		return p.getDescription();
	}

	public static void copyFile(InputStream in, File out) throws Exception { // https://bukkit.org/threads/extracting-file-from-jar.16962/
		InputStream fis = in;
		FileOutputStream fos = new FileOutputStream(out);
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		}catch(Exception e) {
			throw e;
		}finally {
			if(fis != null) {
				fis.close();
			}
			if(fos != null) {
				fos.close();
			}
		}
	}
}
