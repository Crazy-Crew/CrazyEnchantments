package me.badbones69.crazyenchantments.api;

import me.badbones69.crazyenchantments.Main;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class GKitz {

	private int slot;
	private String name;
	private String cooldown;
	private Boolean autoEquip;
	private ItemStack displayItem;
	private ArrayList<String> commands;
	private ArrayList<ItemStack> items;
	private ArrayList<ItemStack> preview;
	private ArrayList<String> itemStrings;

	/**
	 * Create a new gkit.
	 * @param name The name of the gkit.
	 * @param slot The slot it will be on in the GUI.
	 * @param cooldown The cooldown that will be tied to it.
	 * @param displayItem The display item that will be in the GUI.
	 * @param preview The preview items.
	 * @param commands The commands that will be run.
	 * @param items The items that will be given.
	 * @param itemStrings The items as a string.
	 * @param autoEquip This is if the armor equipts when given.
	 */
	public GKitz(String name, int slot, String cooldown, ItemStack displayItem, ArrayList<ItemStack> preview, ArrayList<String> commands, ArrayList<ItemStack> items, ArrayList<String> itemStrings, Boolean autoEquip) {
		this.name = name;
		this.slot = slot;
		this.items = items;
		this.preview = preview;
		this.cooldown = cooldown;
		this.commands = commands;
		this.autoEquip = autoEquip;
		this.displayItem = displayItem;
		this.itemStrings = itemStrings;
	}

	public String getName() {
		return this.name;
	}

	public int getSlot() {
		return this.slot;
	}

	public String getCooldown() {
		return this.cooldown;
	}

	public ItemStack getDisplayItem() {
		return this.displayItem;
	}

	public ArrayList<ItemStack> getPreviewItems() {
		return this.preview;
	}

	public ArrayList<String> getCommands() {
		return this.commands;
	}

	public ArrayList<ItemStack> getItems() {
		this.items = Main.CE.getKitItems(itemStrings);
		return this.items;
	}

	public ArrayList<String> getItemStrings() {
		return this.itemStrings;
	}

	public Boolean canAutoEquipt() {
		return this.autoEquip;
	}

}