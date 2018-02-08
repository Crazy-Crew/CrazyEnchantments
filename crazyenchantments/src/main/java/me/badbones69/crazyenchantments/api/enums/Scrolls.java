package me.badbones69.crazyenchantments.api.enums;

import me.badbones69.crazyenchantments.api.objects.FileManager.Files;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum Scrolls {
	
	BlACK_SCROLL("Black-Scroll", "BlackScroll", Arrays.asList("b", "black", "blackscroll")),
	WHITE_SCROLL("White-Scroll", "WhiteScroll", Arrays.asList("w", "white", "whitescroll")),
	TRANSMOG_SCROLL("Transmog-Scroll", "TransmogScroll", Arrays.asList("t", "transmog", "transmogscroll"));
	
	private String name;
	private String configName;
	private List<String> knownNames;
	private static HashMap<Scrolls, ItemBuilder> scrolls = new HashMap<>();
	
	private Scrolls(String name, String configName, List<String> knowNames) {
		this.name = name;
		this.knownNames = knowNames;
		this.configName = configName;
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getKnownNames() {
		return knownNames;
	}
	
	public String getConfigName() {
		return configName;
	}
	
	public ItemStack getScroll() {
		return scrolls.get(this).build();
	}
	
	public ItemStack getScroll(int amount) {
		return scrolls.get(this).setAmount(amount).build();
	}
	
	public static void loadScrolls() {
		FileConfiguration config = Files.CONFIG.getFile();
		scrolls.clear();
		for(Scrolls scroll : values()) {
			String path = "Settings." + scroll.getConfigName() + ".";
			scrolls.put(scroll, new ItemBuilder()
			.setName(config.getString(path + "Name"))
			.setLore(config.getStringList(path + "Item-Lore"))
			.setMaterial(config.getString(path + "Item"))
			.setGlowing(config.getBoolean(path + "Glowing")));
		}
	}
	
}