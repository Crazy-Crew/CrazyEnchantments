package me.badbones69.crazyenchantments.api.enums;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;

public enum Messages {
	
	CONFIG_RELOAD("Config-Reload", "&7You have reloaded the Config.yml"),
	NEED_TO_UNSTACK_ITEM("Need-To-UnStack-Item", "&cYou need to unstack that item for it to be used."),
	NOT_AN_ENCHANTMENT("Not-An-Enchantment", "&cThat is not an enchantment"),
	RIGHT_CLICK_BLACK_SCROLL("Right-Click-Black-Scroll", "&7Black scrolls will remove a random enchantment from your item."),
	BLACK_SCROLL_UNSUCCESSFUL("Black-Scroll-Unsuccessful", "&cThe black scroll was unsuccessful. Please try again with another one."),
	NEED_MORE_XP_LEVELS("Need-More-XP-Lvls", "&cYou need &6%XP% &cmore XP Lvls."),
	NEED_MORE_TOTAL_XP("Need-More-Total-XP", "&cYou need &6%XP% &cmore Total XP."),
	NEED_MORE_MONEY("Need-More-Money", "&cYou are in need of &a$%Money_Needed%&c."),
	HIT_ENCHANTMENT_MAX("Hit-Enchantment-Max", "&cYou have hit the max amount of enchantment an item can have."),
	INVENTORY_FULL("Inventory-Full", "&cYour inventory is too full. Please open up some space to buy that."),
	TINKER_INVENTORY_FULL("Tinker-Inventory-Full", "&cThe inventory is full. Sell all or remove items."),
	TINKER_SOLD_MESSAGE("Tinker-Sold-Msg", "&7Thank you for trading at &7&lThe &4&lCrazy &c&lTinkerer&7."),
	PLAYERS_ONLY("Players-Only", "&cOnly players can use this command."),
	NO_PERMISSION("No-Perm", "&cYou do not have permission to use that command!"),
	NOT_ONLINE("Not-Online", "&cThat player is not online at this time."),
	REMOVED_ENCHANTMENT("Remove-Enchantment", "&7You have removed the enchantment &a%Enchantment% &7from this item."),
	DOESNT_HAVE_ENCHANTMENT("Doesnt-Have-Enchantment", "&cYour item does not contain the enchantment &6%Enchantment%&c."),
	DOESNT_HAVE_ITEM_IN_HAND("Doesnt-Have-Item-In-Hand", "&cYou must have an item in your hand."),
	NOT_A_NUMBER("Not-A-Number", "&c%Arg% is not a number."),
	GET_SUCCESS_DUST("Get-Success-Dust", "&7You have gained &a%amount% &7Success Dust."),
	GIVE_SUCCESS_DUST("Give-Success-Dust", "&7You have given &a%amount% &7Success Dust to &6%player%&7."),
	GET_DESTROY_DUST("Get-Destroy-Dust", "&7You have gained &a%amount% &7Destroy Dust."),
	GIVE_DESTROY_DUST("Give-Destroy-Dust", "&7You have given &a%amount% &7Destroy Dust to &6%player%&7."),
	GET_MYSTERY_DUST("Get-Mystery-Dust", "&7You have gained &a%amount% &7Mystery Dust."),
	GIVE_MYSTERY_DUST("Give-Mystery-Dust", "&7You have given &a%amount% &7Mystery Dust to &6%player%&7."),
	NOT_A_CATEGORY("Not-A-Category", "&6%category% &cis not a category."),
	CLEAN_LOST_BOOK("Clean-Lost-Book", "&7You have cleaned a lost book and found %found%&7."),
	BOOK_WORKS("Book-Works", "&aYour item loved this book and accepted it."),
	BOOK_FAILED("Book-Failed", "&cYour item must not have liked that enchantment."),
	ITEM_DESTROYED("Item-Destroyed", "&cOh no the destroy rate was too much for the item."),
	ITEM_WAS_PROTECTED("Item-Was-Protected", "&cLuckily your item was blessed with davine Protection and didn't break."),
	GIVE_PROTECTION_CRYSTAL("Give-Protection-Crystal", "&7You have given %player% %amount% Protection Crystals."),
	GET_PROTECTION_CRYSTAL("Get-Protection-Crystal", "&7You have gained %amount% Protection Crystals."),
	GIVE_SCRAMBLER_CRYSTAL("Give-Scrambler-Crystal", "&7You have given %player% %amount% &e&lGrand Scramblers&7."),
	GET_SCRAMBLER("Get-Scrambler-Crystal", "&7You have gained %amount% &e&lGrand Scramblers&7."),
	BREAK_ENCHANTMENT_SHOP_SIGN("Break-Enchantment-Shop-Sign", "&cYou have removed a Crazy Enchantment Shop Sign."),
	SEND_ENCHANTMENT_BOOK("Send-Enchantment-Book", "&7You have sent &6%player% &7an Crazy Enchantment Book."),
	NOT_A_GKIT("Not-A-GKit", "&c%kit% is not a GKit."),
	STILL_IN_COOLDOWN("Still-In-Cooldown", "&cYou still have %day%d %hour%h %minute%m %second%s cooldown left on %kit%&c."),
	GIVEN_GKIT("Given-GKit", "&7You have given &6%player%&7 a %kit%&7 GKit."),
	RECEIVED_GKIT("Received-GKit", "&7You have received a %kit%&7 GKit."),
	NO_GKIT_PERMISSION("No-GKit-Permission", "&cYou do not have permission to use the %kit% GKit."),
	SPAWNED_BOOK("Spawned-Book", "&7You have spawned a book at &6%World%, %X%, %Y%, %Z%&7."),
	RESET_GKIT("Reset-GKit", "&7You have reset %player%'s %GKit% GKit cooldown."),
	GKIT_NOT_ENABLED("GKitz-Not-Enabled", "&cGKitz is currently not enabled."),
	DISORDERED_ENEMY_HOT_BAR("Disordered-Enemy-Hot-Bar", "&7Disordered enemies hot bar."),
	ENCHANTMENT_UPGRADE_SUCCESS("Enchantment-Upgrade.Success", "&7You have just upgraded &6%Enchantment%&7 to level &6%Level%&7."),
	ENCHANTMENT_UPGRADE_DESTROYED("Enchantment-Upgrade.Destroyed", "&cYour upgrade failed and the lower level enchantment was lost."),
	ENCHANTMENT_UPGRADE_FAILED("Enchantment-Upgrade.Failed", "&cThe book failed to upgrade to the item."),
	RAGE_BUILDING("Rage.Building", "&7[&c&lRage&7]: &aKeep it up, your rage is building."),
	RAGE_COOLED_DOWN("Rage.Cooled-Down", "&7[&c&lRage&7]: &cYour Rage has just cooled down."),
	RAGE_RAGE_UP("Rage.Rage-Up", "&7[&c&lRage&7]: &7You are now doing &a%Level%x &7Damage."),
	RAGE_DAMAGED("Rage.Damaged", "&7[&c&lRage&7]: &cYou have been hurt and it broke your Rage Multiplier!");
	
	private String path;
	private String defaultMessage;
	private List<String> defaultListMessage;
	
	private Messages(String path, String defaultMessage) {
		this.path = path;
		this.defaultMessage = defaultMessage;
	}
	
	private Messages(String path, List<String> defaultListMessage) {
		this.path = path;
		this.defaultListMessage = defaultListMessage;
	}
	
	public String getMessage() {
		if(isList()) {
			if(exists()) {
				return Methods.color(convertList(Files.MESSAGES.getFile().getStringList("Messages." + path)));
			}else {
				return Methods.color(convertList(getDefaultListMessage()));
			}
		}else {
			if(exists()) {
				return Methods.getPrefix(Files.MESSAGES.getFile().getString("Messages." + path));
			}else {
				return Methods.getPrefix(getDefaultMessage());
			}
		}
	}
	
	public String getMessage(HashMap<String, String> placeholders) {
		String message;
		if(isList()) {
			if(exists()) {
				message = Methods.color(convertList(Files.MESSAGES.getFile().getStringList("Messages." + path), placeholders));
			}else {
				message = Methods.color(convertList(getDefaultListMessage(), placeholders));
			}
		}else {
			if(exists()) {
				message = Methods.getPrefix(Files.MESSAGES.getFile().getString("Messages." + path));
			}else {
				message = Methods.getPrefix(getDefaultMessage());
			}
			for(String ph : placeholders.keySet()) {
				if(message.contains(ph)) {
					message = message.replaceAll(ph, placeholders.get(ph));
				}
			}
		}
		return message;
	}
	
	public String getMessageNoPrefix() {
		if(isList()) {
			if(exists()) {
				return Methods.color(convertList(Files.MESSAGES.getFile().getStringList("Messages." + path)));
			}else {
				return Methods.color(convertList(getDefaultListMessage()));
			}
		}else {
			if(exists()) {
				return Methods.color(Files.MESSAGES.getFile().getString("Messages." + path));
			}else {
				return Methods.color(getDefaultMessage());
			}
		}
	}
	
	public String getMessageNoPrefix(HashMap<String, String> placeholders) {
		String message;
		if(isList()) {
			if(exists()) {
				message = Methods.color(convertList(Files.MESSAGES.getFile().getStringList("Messages." + path), placeholders));
			}else {
				message = Methods.color(convertList(getDefaultListMessage(), placeholders));
			}
		}else {
			if(exists()) {
				message = Methods.color(Files.MESSAGES.getFile().getString("Messages." + path));
			}else {
				message = Methods.color(getDefaultMessage());
			}
			for(String ph : placeholders.keySet()) {
				if(message.contains(ph)) {
					message = message.replaceAll(ph, placeholders.get(ph));
				}
			}
		}
		return message;
	}
	
	public static String convertList(List<String> list) {
		String message = "";
		for(String m : list) {
			message += Methods.color(m) + "\n";
		}
		return message;
	}
	
	public static String convertList(List<String> list, HashMap<String, String> placeholders) {
		String message = "";
		for(String m : list) {
			message += Methods.color(m) + "\n";
		}
		for(String ph : placeholders.keySet()) {
			message = Methods.color(message.replaceAll(ph, placeholders.get(ph)));
		}
		return message;
	}
	
	public static void addMissingMessages() {
		FileConfiguration messages = Files.MESSAGES.getFile();
		boolean saveFile = false;
		for(Messages message : values()) {
			if(!messages.contains("Messages." + message.getPath())) {
				System.out.println(message.getPath());
				saveFile = true;
				if(message.getDefaultMessage() != null) {
					messages.set("Messages." + message.getPath(), message.getDefaultMessage());
				}else {
					messages.set("Messages." + message.getPath(), message.getDefaultListMessage());
				}
			}
		}
		if(saveFile) {
			Files.MESSAGES.saveFile();
		}
	}
	
	private Boolean exists() {
		return Files.MESSAGES.getFile().contains("Messages." + path);
	}
	
	private Boolean isList() {
		if(Files.MESSAGES.getFile().contains("Messages." + path)) {
			return !Files.MESSAGES.getFile().getStringList("Messages." + path).isEmpty();
		}else {
			return defaultMessage == null;
		}
	}
	
	private String getPath() {
		return path;
	}
	
	private String getDefaultMessage() {
		return defaultMessage;
	}
	
	private List<String> getDefaultListMessage() {
		return defaultListMessage;
	}
	
}
