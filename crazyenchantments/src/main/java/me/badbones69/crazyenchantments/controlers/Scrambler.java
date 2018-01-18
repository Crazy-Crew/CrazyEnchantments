package me.badbones69.crazyenchantments.controlers;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CEBook;
import me.badbones69.crazyenchantments.api.CEnchantments;
import me.badbones69.crazyenchantments.api.CustomEBook;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.multisupport.Version;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Scrambler implements Listener {

	public static HashMap<Player, Integer> roll = new HashMap<>();
	public static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments");

	@EventHandler
	public void onReRoll(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		CustomEnchantments CustomE = Main.CustomE;
		if(inv != null) {
			ItemStack book = e.getCurrentItem();
			ItemStack sc = e.getCursor();
			if(book != null && sc != null) {
				if(book.getType() != Material.AIR && sc.getType() != Material.AIR) {
					if(book.getAmount() == 1 && sc.getAmount() == 1) {
						if(getScramblers().isSimilar(sc)) {
							if(Main.CE.isEnchantmentBook(book) || CustomE.isEnchantmentBook(book)) {
								e.setCancelled(true);
								player.setItemOnCursor(new ItemStack(Material.AIR));
								if(Main.settings.getConfig().getBoolean("Settings.Scrambler.GUI.Toggle")) {
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
		CustomEnchantments CustomE = Main.CustomE;
		if(Main.CE.isEnchantmentBook(book)) {
			CEnchantments en = Main.CE.getEnchantmentBookEnchantmnet(book);
			String cat = Main.CE.getHighestEnchantmentCategory(en);
			int lvl = Main.CE.getBookPower(book, en);
			CEBook eBook = new CEBook(en, lvl);
			int D = Methods.percentPick(Main.settings.getConfig().getInt("Categories." + cat + ".EnchOptions.DestroyPercent.Max"), Main.settings.getConfig().getInt("Categories." + cat + ".EnchOptions.DestroyPercent.Min"));
			int S = Methods.percentPick(Main.settings.getConfig().getInt("Categories." + cat + ".EnchOptions.SuccessPercent.Max"), Main.settings.getConfig().getInt("Categories." + cat + ".EnchOptions.SuccessPercent.Min"));
			eBook.setDestoryRate(D);
			eBook.setSuccessRate(S);
			newBook = eBook.buildBook();
		}else {
			String en = CustomE.getEnchantmentBookEnchantmnet(book);
			int lvl = CustomE.getBookPower(book, en);
			String cat = CustomE.getHighestEnchantmentCategory(en);
			CustomEBook eBook = new CustomEBook(en, lvl);
			int D = Methods.percentPick(Main.settings.getConfig().getInt("Categories." + cat + ".EnchOptions.DestroyPercent.Max"), Main.settings.getConfig().getInt("Categories." + cat + ".EnchOptions.DestroyPercent.Min"));
			int S = Methods.percentPick(Main.settings.getConfig().getInt("Categories." + cat + ".EnchOptions.SuccessPercent.Max"), Main.settings.getConfig().getInt("Categories." + cat + ".EnchOptions.SuccessPercent.Min"));
			eBook.setDestoryRate(D);
			eBook.setSuccessRate(S);
			newBook = eBook.buildBook();
		}
		return newBook;
	}

	/**
	 * Get the scrambler itemstack.
	 * @return The scramblers.
	 */
	public static ItemStack getScramblers() {
		FileConfiguration config = Main.settings.getConfig();
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
		FileConfiguration config = Main.settings.getConfig();
		String id = config.getString("Settings.Scrambler.Item");
		String name = config.getString("Settings.Scrambler.Name");
		List<String> lore = config.getStringList("Settings.Scrambler.Lore");
		Boolean toggle = config.getBoolean("Settings.Scrambler.Glowing");
		return new ItemBuilder().setMaterial(id).setAmount(amount).setName(name).setLore(lore).setGlowing(toggle).build();
	}

	private static void setGlass(Inventory inv) {
		for(int i = 0; i < 9; i++) {
			if(i != 4) {
				int color = new Random().nextInt(15);
				if(color == 8) {
					color = 1;
				}
				inv.setItem(i, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setMetaData(color).setName(" ").build());
				color = new Random().nextInt(15);
				if(color == 8) {
					color = 1;
				}
				inv.setItem(i + 18, new ItemBuilder().setMaterial(Material.STAINED_GLASS_PANE).setMetaData(color).setName(" ").build());

			}else {
				FileConfiguration config = Main.settings.getConfig();
				ItemStack pointer = new ItemBuilder().setMaterial(config.getString("Settings.Scrambler.GUI.Pointer.Item")).setName(config.getString("Settings.Scrambler.GUI.Pointer.Name")).setLore(config.getStringList("Settings.Scrambler.GUI.Pointer.Lore")).build();
				inv.setItem(i, pointer);
				inv.setItem(i + 18, pointer);
			}
		}
	}

	public static void openScrambler(Player player, ItemStack book) {
		Inventory inv = Bukkit.createInventory(null, 27, Methods.color(Main.settings.getConfig().getString("Settings.Scrambler.GUI.Name")));
		setGlass(inv);
		for(int i = 9; i > 8 && i < 18; i++) {
			inv.setItem(i, getNewScrambledBook(book));
		}
		player.openInventory(inv);
		startScrambler(player, inv, book);
	}

	private static void startScrambler(final Player player, final Inventory inv, final ItemStack book) {
		roll.put(player, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			int time = 1;
			int full = 0;
			int open = 0;

			@Override
			public void run() {
				if(full <= 50) {//When Spinning
					moveItems(inv, player, book);
					setGlass(inv);
					if(Version.getCurrentVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger()) {
						player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
					}else {
						player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
					}
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
						if(Version.getCurrentVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger()) {
							player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
						}else {
							player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
						}
					}
					time++;
					if(time >= 60) {// When done
						if(Version.getCurrentVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger()) {
							player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 1);
						}else {
							player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 1, 1);
						}
						Bukkit.getScheduler().cancelTask(roll.get(player));
						roll.remove(player);
						ItemStack item = inv.getItem(13).clone();
						item.setType(Main.CE.getEnchantmentBookItem().getType());
						item.setDurability(Main.CE.getEnchantmentBookItem().getDurability());
						if(Methods.isInvFull(player)) {
							player.getWorld().dropItem(player.getLocation(), item);
						}else {
							player.getInventory().addItem(item);
						}
						return;
					}
				}
			}
		}, 1, 1));
	}

	private static ArrayList<Integer> slowSpin() {
		ArrayList<Integer> slow = new ArrayList<Integer>();
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
		int color = new Random().nextInt(15);
		if(color == 8) {
			color = 1;
		}
		newBook.setType(Material.STAINED_GLASS);
		newBook.setDurability((short) color);
		inv.setItem(9, newBook);
		for(int i = 0; i < 8; i++) {
			inv.setItem(i + 10, items.get(i));
		}
	}

	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if(inv != null) {
			if(inv.getName().equals(Methods.color(Main.settings.getConfig().getString("Settings.Scrambler.GUI.Name")))) {
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