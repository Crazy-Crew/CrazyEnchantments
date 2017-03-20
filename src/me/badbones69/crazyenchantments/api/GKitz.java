package me.badbones69.crazyenchantments.api;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import me.badbones69.crazyenchantments.Main;

public class GKitz{
	
	private int slot;
	private String name;
	private String cooldown;
	private ItemStack displayItem;
	private ArrayList<String> commands;
	private ArrayList<ItemStack> items;
	private ArrayList<ItemStack> preview;
	private ArrayList<String> itemStrings;
	
	/**
	 * 
	 * @param name
	 * @param slot
	 * @param cooldown
	 * @param displayItem
	 * @param commands
	 * @param items
	 */
	public GKitz(String name, int slot, String cooldown, ItemStack displayItem, ArrayList<ItemStack> preview, ArrayList<String> commands, ArrayList<ItemStack> items, ArrayList<String> itemStrings){
		this.name = name;
		this.slot = slot;
		this.items = items;
		this.preview = preview;
		this.cooldown = cooldown;
		this.commands = commands;
		this.displayItem = displayItem;
		this.itemStrings = itemStrings;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getSlot(){
		return this.slot;
	}
	
	public String getCooldown(){
		return this.cooldown;
	}
	
	public ItemStack getDisplayItem(){
		return this.displayItem;
	}
	
	public ArrayList<ItemStack> getPreviewItems(){
		return this.preview;
	}
	
	public ArrayList<String> getCommands(){
		return this.commands;
	}
	
	public ArrayList<ItemStack> getItems(){
		this.items = Main.CE.getKitItems(itemStrings);
		return this.items;
	}
	
	public ArrayList<String> getItemStrings(){
		return this.itemStrings;
	}
	
}
