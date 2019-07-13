package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.objects.CEBook;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.Category;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Scrambler implements Listener {
	
	public static HashMap<Player, Integer> roll = new HashMap<>();
	private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	@EventHandler
	public void onReRoll(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		if(inv != null) {
			ItemStack book = e.getCurrentItem();
			ItemStack sc = e.getCursor();
			if(book != null && sc != null) {
				if(book.getType() != Material.AIR && sc.getType() != Material.AIR) {
					if(book.getAmount() == 1 && sc.getAmount() == 1) {
						if(getScramblers().isSimilar(sc)) {
							if(ce.isEnchantmentBook(book)) {
								e.setCancelled(true);
								player.setItemOnCursor(new ItemStack(Material.AIR));
								if(Files.CONFIG.getFile().getBoolean("Settings.Scrambler.GUI.Toggle")) {
									e.setCurrentItem(new ItemStack(Material.AIR));
									openScrambler(player, book);
								}else {
									e.setCurrentItem(getNewScrambledBook(book));
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Get a new book that has been scrambled.
	 * @param book The old book.
	 * @return A new scrambled book.
	 */
	public static ItemStack getNewScrambledBook(ItemStack book) {
		ItemStack newBook = new ItemStack(Material.AIR);
		if(ce.isEnchantmentBook(book)) {
			CEnchantment enchantment = ce.getEnchantmentBookEnchantment(book);
			Category category = ce.getHighestEnchantmentCategory(enchantment);
			int lvl = ce.getBookLevel(book, enchantment);
			CEBook eBook = new CEBook(enchantment, lvl);
			eBook.setDestroyRate(Methods.percentPick(category.getMaxDestroyRate(), category.getMinDestroyRate()));
			eBook.setSuccessRate(Methods.percentPick(category.getMaxSuccessRate(), category.getMinSuccessRate()));
			newBook = eBook.buildBook();
		}
		return newBook;
	}
	
	/**
	 * Get the scrambler itemstack.
	 * @return The scramblers.
	 */
	public static ItemStack getScramblers() {
		FileConfiguration config = Files.CONFIG.getFile();
		String id = config.getString("Settings.Scrambler.Item");
		String name = config.getString("Settings.Scrambler.Name");
		List<String> lore = config.getStringList("Settings.Scrambler.Lore");
		Boolean toggle = config.getBoolean("Settings.Scrambler.Glowing");
		return new ItemBuilder().setMaterial(id).setName(name).setLore(lore).setGlowing(toggle).build();
	}
	
	/**
	 * Get the scrambler itemstack.
	 * @param amount The amount you want.
	 * @return The scramblers.
	 */
	public static ItemStack getScramblers(int amount) {
		FileConfiguration config = Files.CONFIG.getFile();
		String id = config.getString("Settings.Scrambler.Item");
		String name = config.getString("Settings.Scrambler.Name");
		List<String> lore = config.getStringList("Settings.Scrambler.Lore");
		Boolean toggle = config.getBoolean("Settings.Scrambler.Glowing");
		return new ItemBuilder().setMaterial(id).setAmount(amount).setName(name).setLore(lore).setGlowing(toggle).build();
	}
	
	private static void setGlass(Inventory inv) {
		for(int i = 0; i < 9; i++) {
			if(i != 4) {
				inv.setItem(i, Methods.getRandomPaneColor().setName(" ").build());
				inv.setItem(i + 18, Methods.getRandomPaneColor().setName(" ").build());
				
			}else {
				FileConfiguration config = Files.CONFIG.getFile();
				ItemStack pointer = new ItemBuilder().setMaterial(config.getString("Settings.Scrambler.GUI.Pointer.Item")).setName(config.getString("Settings.Scrambler.GUI.Pointer.Name")).setLore(config.getStringList("Settings.Scrambler.GUI.Pointer.Lore")).build();
				inv.setItem(i, pointer);
				inv.setItem(i + 18, pointer);
			}
		}
	}
	
	public static void openScrambler(Player player, ItemStack book) {
		Inventory inv = Bukkit.createInventory(null, 27, Methods.color(Files.CONFIG.getFile().getString("Settings.Scrambler.GUI.Name")));
		setGlass(inv);
		for(int i = 9; i > 8 && i < 18; i++) {
			inv.setItem(i, getNewScrambledBook(book));
		}
		player.openInventory(inv);
		startScrambler(player, inv, book);
	}
	
	private static void startScrambler(final Player player, final Inventory inv, final ItemStack book) {
		roll.put(player, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("CrazyEnchantments"), new Runnable() {
			int time = 1;
			int full = 0;
			int open = 0;
			@Override
			public void run() {
				if(full <= 50) {//When Spinning
					moveItems(inv, player, book);
					setGlass(inv);
					player.playSound(player.getLocation(), ce.getSound("UI_BUTTON_CLICK", "CLICK"), 1, 1);
				}
				open++;
				if(open >= 5) {
					player.openInventory(inv);
					open = 0;
				}
				full++;
				if(full > 51) {
					if(slowSpin().contains(time)) {//When Slowing Down
						moveItems(inv, player, book);
						setGlass(inv);
						player.playSound(player.getLocation(), ce.getSound("UI_BUTTON_CLICK", "CLICK"), 1, 1);
					}
					time++;
					if(time >= 60) {// When done
						player.playSound(player.getLocation(), ce.getSound("ENTITY_PLAYER_LEVELUP", "LEVEL_UP"), 1, 1);
						Bukkit.getScheduler().cancelTask(roll.get(player));
						roll.remove(player);
						ItemStack item = inv.getItem(13).clone();
						item.setType(ce.getEnchantmentBookItem().getType());
						item.setDurability(ce.getEnchantmentBookItem().getDurability());
						if(Methods.isInventoryFull(player)) {
							player.getWorld().dropItem(player.getLocation(), item);
						}else {
							player.getInventory().addItem(item);
						}
					}
				}
			}
		}, 1, 1));
	}
	
	private static ArrayList<Integer> slowSpin() {
		ArrayList<Integer> slow = new ArrayList<>();
		int full = 120;
		int cut = 15;
		for(int i = 120; cut > 0; full--) {
			if(full <= i - cut || full >= i - cut) {
				slow.add(i);
				i = i - cut;
				cut--;
			}
		}
		return slow;
	}
	
	private static void moveItems(Inventory inv, Player player, ItemStack book) {
		ArrayList<ItemStack> items = new ArrayList<>();
		for(int i = 9; i > 8 && i < 17; i++) {
			items.add(inv.getItem(i));
		}
		ItemStack newBook = getNewScrambledBook(book);
		newBook.setType(Methods.getRandomPaneColor().getMaterial());
		inv.setItem(9, newBook);
		for(int i = 0; i < 8; i++) {
			inv.setItem(i + 10, items.get(i));
		}
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if(inv != null) {
			if(e.getView().getTitle().equals(Methods.color(Files.CONFIG.getFile().getString("Settings.Scrambler.GUI.Name")))) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onScramblerClick(PlayerInteractEvent e) {
		ItemStack item = Methods.getItemInHand(e.getPlayer());
		if(item != null) {
			if(Methods.isSimilar(item, getScramblers())) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if(roll.containsKey(player)) {
			Bukkit.getScheduler().cancelTask(roll.get(player));
			roll.remove(player);
		}
	}
	
}
