package me.badbones69.crazyenchantments.api.enums;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.objects.FileManager.Files;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum Dust {
	
	SUCCESS_DUST("Success-Dust", "SuccessDust", Arrays.asList("s", "success")),
	DESTROY_DUST("Destory-Dust", "DestroyDust", Arrays.asList("d", "destory")),
	MYSTERY_DUST("Mystery-Dust", "MysteryDust", Arrays.asList("m", "mystery")),
	FAILED_DUST("Failed-Dust", "FailedDust", Arrays.asList("f, failed"));
	
	private String name;
	private String configName;
	private List<String> knownNames;
	private Integer max;
	private Integer min;
	private static HashMap<Dust, ItemBuilder> dust = new HashMap<>();
	
	private Dust(String name, String configName, List<String> knowNames) {
		this.name = name;
		this.knownNames = knowNames;
		this.configName = configName;
		this.max = Files.CONFIG.getFile().getInt("Settings.Dust." + configName + ".PercentRange.Max");
		if(!Files.CONFIG.getFile().contains("Settings.Dust." + configName + ".PercentRange.Min")) {
			this.min = max;
		}else {
			this.min = Files.CONFIG.getFile().getInt("Settings.Dust." + configName + ".PercentRange.Min");
		}
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
	
	public ItemStack getDust() {
		return dust.get(this)
		.addLorePlaceholder("%percent%", Methods.percentPick(max, min) + "")
		.addLorePlaceholder("%Percent%", Methods.percentPick(max, min) + "")
		.build();
	}
	
	public ItemStack getDust(int amount) {
		return dust.get(this)
		.addLorePlaceholder("%percent%", Methods.percentPick(max, min) + "")
		.addLorePlaceholder("%Percent%", Methods.percentPick(max, min) + "")
		.setAmount(amount).build();
	}
	
	public ItemStack getDust(int percent, int amount) {
		return dust.get(this)
		.addLorePlaceholder("%percent%", percent + "")
		.addLorePlaceholder("%Percent%", percent + "")
		.build();
	}
	
	public static void loadDust() {
		FileConfiguration config = Files.CONFIG.getFile();
		dust.clear();
		for(Dust dust : values()) {
			String path = "Settings.Dust." + dust.getConfigName() + ".";
			Dust.dust.put(dust, new ItemBuilder()
			.setName(config.getString(path + "Name"))
			.setLore(config.getStringList(path + "Lore"))
			.setMaterial(config.getString(path + "Item")));
		}
	}
	
}