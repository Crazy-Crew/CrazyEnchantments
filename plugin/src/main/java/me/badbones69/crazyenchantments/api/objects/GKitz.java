package me.badbones69.crazyenchantments.api.objects;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GKitz {
	
	private int slot;
	private String name;
	private String cooldown;
	private boolean autoEquip;
	private ItemStack displayItem;
	private List<String> commands;
	private List<ItemStack> preview;
	private List<String> itemStrings;
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	/**
	 * Create a new gkit.
	 * @param name The name of the gkit.
	 * @param slot The slot it will be on in the GUI.
	 * @param cooldown The cooldown that will be tied to it.
	 * @param displayItem The display item that will be in the GUI.
	 * @param preview The preview items.
	 * @param commands The commands that will be run.
	 * @param itemStrings The items as a string.
	 * @param autoEquip This is if the armor equips when given.
	 */
	public GKitz(String name, int slot, String cooldown, ItemStack displayItem, List<ItemStack> preview,
	List<String> commands, List<String> itemStrings, boolean autoEquip) {
		this.name = name;
		this.slot = slot;
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
	
	public List<ItemStack> getPreviewItems() {
		return this.preview;
	}
	
	public List<String> getCommands() {
		return this.commands;
	}
	
	public List<String> getItemStrings() {
		return this.itemStrings;
	}
	
	public boolean canAutoEquipt() {
		return this.autoEquip;
	}
	
	/**
	 * Get the items for the GKit. Needs to be done as it has to get random levels each time.
	 * @return A list of all the ItemStacks.
	 */
	public List<ItemStack> getKitItems() {
		List<ItemStack> items = new ArrayList<>();
		for(String itemString : itemStrings) {
			GKitzItem item = new GKitzItem(ItemBuilder.convertString(itemString));
			for(String option : itemString.split(", ")) {
				try {
					CEnchantment enchantment = ce.getEnchantmentFromName(option.split(":")[0]);
					String level = option.split(":")[1];
					if(enchantment != null) {
						if(level.contains("-")) {
							int randomLevel = ce.pickLevel(Integer.parseInt(level.split("-")[0]), Integer.parseInt(level.split("-")[1]));
							if(randomLevel > 0) {
								item.addCEEnchantment(enchantment, randomLevel);
							}
						}else {
							item.addCEEnchantment(enchantment, Integer.parseInt(level));
						}
					}
				}catch(Exception ignore) {
				}
			}
			items.add(item.build());
		}
		return items;
	}
	
}