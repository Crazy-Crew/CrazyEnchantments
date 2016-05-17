package me.BadBones69.CrazyEnchantments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class SettingsManager {

	static SettingsManager instance = new SettingsManager();

	public static SettingsManager getInstance() {
		return instance;
	}

	Plugin p;

	FileConfiguration config;
	File cfile;

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
	
	public void setup(Plugin p) {
		cfile = new File(p.getDataFolder(), "config.yml");
		config = p.getConfig();

		if (!p.getDataFolder().exists()) {
			p.getDataFolder().mkdir();
		}
		
		efile = new File(p.getDataFolder(), "Enchantments.yml");
		if (!efile.exists()) {
			try{
        		File en = new File(p.getDataFolder(), "/Enchantments.yml");
         		InputStream E = getClass().getResourceAsStream("/Enchantments.yml");
         		copyFile(E, en);
         	}catch (Exception e) {
         		e.printStackTrace();
         	}
		}
		enchs = YamlConfiguration.loadConfiguration(efile);
		
		mfile = new File(p.getDataFolder(), "Messages.yml");
		if (!mfile.exists()) {
			try{
        		File en = new File(p.getDataFolder(), "/Messages.yml");
         		InputStream E = getClass().getResourceAsStream("/Messages.yml");
         		copyFile(E, en);
         	}catch (Exception e) {
         		e.printStackTrace();
         	}
		}
		msg = YamlConfiguration.loadConfiguration(mfile);
		
		cefile = new File(p.getDataFolder(), "CustomEnchantments.yml");
		if (!cefile.exists()) {
			try{
        		File en = new File(p.getDataFolder(), "/CustomEnchantments.yml");
         		InputStream E = getClass().getResourceAsStream("/CustomEnchantments.yml");
         		copyFile(E, en);
         	}catch (Exception e) {
         		e.printStackTrace();
         	}
		}
		cenchs = YamlConfiguration.loadConfiguration(cefile);
		
		sfile = new File(p.getDataFolder(), "Signs.yml");
		if (!sfile.exists()) {
			try{
        		File en = new File(p.getDataFolder(), "/Signs.yml");
         		InputStream E = getClass().getResourceAsStream("/Signs.yml");
         		copyFile(E, en);
         	}catch (Exception e) {
         		e.printStackTrace();
         	}
		}
		signs = YamlConfiguration.loadConfiguration(sfile);
		
		tfile = new File(p.getDataFolder(), "Tinker.yml");
		if (!tfile.exists()) {
			try{
        		File en = new File(p.getDataFolder(), "/Tinker.yml");
         		InputStream E = getClass().getResourceAsStream("/Tinker.yml");
         		copyFile(E, en);
         	}catch (Exception e) {
         		e.printStackTrace();
         	}
		}
		tinker = YamlConfiguration.loadConfiguration(tfile);
		
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
	public FileConfiguration getMsg() {
		return msg;
	}
	public void saveTinker() {
		try {
			tinker.save(tfile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger()
					.severe(ChatColor.RED + "Could not save Tinker.yml!");
		}
	}
	public void saveSigns() {
		try {
			signs.save(sfile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger()
					.severe(ChatColor.RED + "Could not save Signs.yml!");
		}
	}
	public void saveCustomEnchs() {
		try {
			cenchs.save(cefile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger()
					.severe(ChatColor.RED + "Could not save CustomEnchantments.yml!");
		}
	}
	public void saveEnchs() {
		try {
			enchs.save(efile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger()
					.severe(ChatColor.RED + "Could not save Enchantments.yml!");
		}
	}
	public void saveMsg() {
		try {
			msg.save(mfile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger()
					.severe(ChatColor.RED + "Could not save Messages.yml!");
		}
	}
	public void reloadTinker() {
		tinker = YamlConfiguration.loadConfiguration(tfile);
	}
	public void reloadSigns() {
		signs = YamlConfiguration.loadConfiguration(sfile);
	}
	public void reloadMsg() {
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
		} catch (IOException e) {
			Bukkit.getServer().getLogger()
					.severe(ChatColor.RED + "Could not save config.yml!");
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
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }
}
