package me.BadBones69.CrazyEnchantments.API.Events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.BadBones69.CrazyEnchantments.API.Version;
import me.BadBones69.CrazyEnchantments.API.Events.ArmorEquipEvent.EquipMethod;

/**
 * @Author Borlea
 * @Github https://github.com/borlea/
 * @Website http://codingforcookies.com/
 * @since Jul 30, 2015 6:43:34 PM
 */
public class ArmorListener implements Listener{
	
	private Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyEnchantments");
	
	private final List<String> blockedMaterials;

	public ArmorListener(){
		this.blockedMaterials = getBlocks();
	}

	@EventHandler
	public final void onInventoryClick(final InventoryClickEvent e){
		boolean shift = false, numberkey = false;
		if(e.isCancelled()) return;
		if(e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT)){
			shift = true;
		}
		if(e.getClick().equals(ClickType.NUMBER_KEY)){
			numberkey = true;
		}
		if((e.getSlotType() != SlotType.ARMOR || e.getSlotType() != SlotType.QUICKBAR) && !(e.getInventory().getType().equals(InventoryType.CRAFTING)||e.getInventory().getType().equals(InventoryType.PLAYER))) return;
		if(!(e.getWhoClicked() instanceof Player)) return;
		if(e.getCurrentItem() == null) return;
		ArmorType newArmorType = ArmorType.matchType(shift ? e.getCurrentItem() : e.getCursor());
		if(!shift && newArmorType != null && e.getRawSlot() != newArmorType.getSlot()){
			// Used for drag and drop checking to make sure you aren't trying to place a helmet in the boots place.
			return;
		}
		if(shift){
			newArmorType = ArmorType.matchType(e.getCurrentItem());
			if(newArmorType != null){
				boolean equipping = true;
				if(e.getRawSlot() == newArmorType.getSlot()){
					equipping = false;
				}
				if(newArmorType.equals(ArmorType.HELMET) && (equipping ? e.getWhoClicked().getInventory().getHelmet() == null : e.getWhoClicked().getInventory().getHelmet() != null) || newArmorType.equals(ArmorType.CHESTPLATE) && (equipping ? e.getWhoClicked().getInventory().getChestplate() == null : e.getWhoClicked().getInventory().getChestplate() != null) || newArmorType.equals(ArmorType.LEGGINGS) && (equipping ? e.getWhoClicked().getInventory().getLeggings() == null : e.getWhoClicked().getInventory().getLeggings() != null) || newArmorType.equals(ArmorType.BOOTS) && (equipping ? e.getWhoClicked().getInventory().getBoots() == null : e.getWhoClicked().getInventory().getBoots() != null)){
					ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), EquipMethod.SHIFT_CLICK, newArmorType, equipping ? null : e.getCurrentItem(), equipping ? e.getCurrentItem() : null);
					Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
					if(armorEquipEvent.isCancelled()){
						e.setCancelled(true);
					}
				}
			}
		}else{
			ItemStack newArmorPiece = e.getCursor();
			ItemStack oldArmorPiece = e.getCurrentItem();
			if(numberkey){
				if(e.getInventory().getType().equals(InventoryType.PLAYER)){// Prevents shit in the 2by2 crafting
					// e.getClickedInventory() == The players inventory
					// e.getHotBarButton() == key people are pressing to equip or unequip the item to or from.
					// e.getRawSlot() == The slot the item is going to.
					// e.getSlot() == Armor slot, can't use e.getRawSlot() as that gives a hotbar slot ;-;
					ItemStack hotbarItem = e.getInventory().getItem(e.getHotbarButton());
					if(hotbarItem != null){// Equipping
						newArmorType = ArmorType.matchType(hotbarItem);
						newArmorPiece = hotbarItem;
						oldArmorPiece = e.getInventory().getItem(e.getSlot());
					}else{// Unequipping
						newArmorType = ArmorType.matchType(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR ? e.getCurrentItem() : e.getCursor());
					}
				}
			}else{
				// e.getCurrentItem() == Unequip
				// e.getCursor() == Equip
				newArmorType = ArmorType.matchType(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR ? e.getCurrentItem() : e.getCursor());
			}
			if(newArmorType != null && e.getRawSlot() == newArmorType.getSlot()){
				EquipMethod method = EquipMethod.DRAG;
				if(e.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberkey) method = EquipMethod.HOTBAR_SWAP;
				final ItemStack It = newArmorPiece.clone();
				final ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), method, newArmorType, oldArmorPiece, newArmorPiece);
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
					@Override
					public void run() {
						ItemStack I = e.getWhoClicked().getInventory().getItem(e.getSlot());
						if(e.getInventory().getType().equals(InventoryType.PLAYER)){
							if(e.getSlot()==ArmorType.HELMET.getSlot()){
								I = e.getWhoClicked().getEquipment().getHelmet();
							}
							if(e.getSlot()==ArmorType.CHESTPLATE.getSlot()){
								I = e.getWhoClicked().getEquipment().getChestplate();
							}
							if(e.getSlot()==ArmorType.LEGGINGS.getSlot()){
								I = e.getWhoClicked().getEquipment().getLeggings();
							}
							if(e.getSlot()==ArmorType.BOOTS.getSlot()){
								I = e.getWhoClicked().getEquipment().getBoots();
							}
						}
						if(I==null){
							if(e.getInventory().getType().equals(InventoryType.CRAFTING)){
								I = new ItemStack(Material.AIR, 0);
							}
							if(e.getInventory().getType().equals(InventoryType.PLAYER)){
								I = new ItemStack(Material.AIR, 1);
							}
						}
						String it = It+"";
						String i = I+"";
						if(i.equalsIgnoreCase(it)){
							Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
							if(armorEquipEvent.isCancelled()){
								e.setCancelled(true);
							}
						}
					}
				}, 0);
			}else{
				if(Version.getVersion().getVersionInteger()<1101 && (ArmorType.matchType(oldArmorPiece) != null && e.getRawSlot() == ArmorType.matchType(oldArmorPiece).getSlot())){
					EquipMethod method = EquipMethod.DRAG;
					if(e.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberkey) method = EquipMethod.HOTBAR_SWAP;
					ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), method, newArmorType, oldArmorPiece, newArmorPiece);
					Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
					if(armorEquipEvent.isCancelled()){
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent e){
		if(e.getAction() == Action.PHYSICAL) return;
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
			final Player player = e.getPlayer();
			if(e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK){// Having both of these checks is useless, might as well do it though.
				// Some blocks have actions when you right click them which stops the client from equipping the armor in hand.
				Material mat = e.getClickedBlock().getType();
				for(String s : blockedMaterials){
					if(mat.name().equalsIgnoreCase(s)) return;
				}
			}
			ArmorType newArmorType = ArmorType.matchType(e.getItem());
			if(newArmorType != null){
				if(newArmorType.equals(ArmorType.HELMET) && e.getPlayer().getInventory().getHelmet() == null || newArmorType.equals(ArmorType.CHESTPLATE) && e.getPlayer().getInventory().getChestplate() == null || newArmorType.equals(ArmorType.LEGGINGS) && e.getPlayer().getInventory().getLeggings() == null || newArmorType.equals(ArmorType.BOOTS) && e.getPlayer().getInventory().getBoots() == null){
					ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(e.getPlayer(), EquipMethod.HOTBAR, ArmorType.matchType(e.getItem()), null, e.getItem());
					Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
					if(armorEquipEvent.isCancelled()){
						e.setCancelled(true);
						player.updateInventory();
					}
				}
			}
		}
	}

	@EventHandler
	public void dispenserFireEvent(BlockDispenseEvent e){
		ArmorType type = ArmorType.matchType(e.getItem());
		if(ArmorType.matchType(e.getItem()) != null){
			Location loc = e.getBlock().getLocation();
			for(Player p : loc.getWorld().getPlayers()){
				if(loc.getBlockY() - p.getLocation().getBlockY() >= -1 && loc.getBlockY() - p.getLocation().getBlockY() <= 1){
					if(p.getInventory().getHelmet() == null && type.equals(ArmorType.HELMET) || p.getInventory().getChestplate() == null && type.equals(ArmorType.CHESTPLATE) || p.getInventory().getLeggings() == null && type.equals(ArmorType.LEGGINGS) || p.getInventory().getBoots() == null && type.equals(ArmorType.BOOTS)){
						org.bukkit.block.Dispenser dispenser = (org.bukkit.block.Dispenser) e.getBlock().getState();
						org.bukkit.material.Dispenser dis = (org.bukkit.material.Dispenser) dispenser.getData();
						BlockFace directionFacing = dis.getFacing();
						// Someone told me not to do big if checks because it's hard to read, look at me doing it -_-
						if(directionFacing == BlockFace.EAST && p.getLocation().getBlockX() != loc.getBlockX() && p.getLocation().getX() <= loc.getX() + 2.3 && p.getLocation().getX() >= loc.getX() || directionFacing == BlockFace.WEST && p.getLocation().getX() >= loc.getX() - 1.3 && p.getLocation().getX() <= loc.getX() || directionFacing == BlockFace.SOUTH && p.getLocation().getBlockZ() != loc.getBlockZ() && p.getLocation().getZ() <= loc.getZ() + 2.3 && p.getLocation().getZ() >= loc.getZ() || directionFacing == BlockFace.NORTH && p.getLocation().getZ() >= loc.getZ() - 1.3 && p.getLocation().getZ() <= loc.getZ()){
							ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, EquipMethod.DISPENSER, ArmorType.matchType(e.getItem()), null, e.getItem());
							Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
							if(armorEquipEvent.isCancelled()){
								e.setCancelled(true);
							}
							return;
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void itemBreakEvent(PlayerItemBreakEvent e){
		ArmorType type = ArmorType.matchType(e.getBrokenItem());
		if(type != null){
			Player p = e.getPlayer();
			ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, EquipMethod.BROKE, type, e.getBrokenItem(), null);
			Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
			if(armorEquipEvent.isCancelled()){
				ItemStack i = e.getBrokenItem().clone();
				i.setAmount(1);
				i.setDurability((short) (i.getDurability() - 1));
				if(type.equals(ArmorType.HELMET)){
					p.getInventory().setHelmet(i);
				}else if(type.equals(ArmorType.CHESTPLATE)){
					p.getInventory().setChestplate(i);
				}else if(type.equals(ArmorType.LEGGINGS)){
					p.getInventory().setLeggings(i);
				}else if(type.equals(ArmorType.BOOTS)){
					p.getInventory().setBoots(i);
				}
			}
		}
	}

	@EventHandler
	public void playerDeathEvent(PlayerDeathEvent e){
		Player p = e.getEntity();
		for(ItemStack i : p.getInventory().getArmorContents()){
			if(i != null && !i.getType().equals(Material.AIR)){
				Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(p, EquipMethod.DEATH, ArmorType.matchType(i), i, null));
				// No way to cancel a death event.
			}
		}
	}

	private List<String> getBlocks(){
		List<String> blocks = new ArrayList<String>();
		blocks.add("FURNACE");
		blocks.add("CHEST");
		blocks.add("TRAPPED_CHEST");
		blocks.add("BEACON");
		blocks.add("DISPENSER");
		blocks.add("DROPPER");
		blocks.add("HOPPER");
		blocks.add("WORKBENCH");
		blocks.add("ENCHANTMENT_TABLE");
		blocks.add("ENDER_CHEST");
		blocks.add("ANVIL");
		blocks.add("BED_BLOCK");
		blocks.add("FENCE_GATE");
		blocks.add("SPRUCE_FENCE_GATE");
		blocks.add("BIRCH_FENCE_GATE");
		blocks.add("ACACIA_FENCE_GATE");
		blocks.add("JUNGLE_FENCE_GATE");
		blocks.add("DARK_OAK_FENCE_GATE");
		blocks.add("IRON_DOOR_BLOCK");
		blocks.add("WOODEN_DOOR");
		blocks.add("SPRUCE_DOOR");
		blocks.add("BIRCH_DOOR");
		blocks.add("JUNGLE_DOOR");
		blocks.add("ACACIA_DOOR");
		blocks.add("DARK_OAK_DOOR");
		blocks.add("WOOD_BUTTON");
		blocks.add("STONE_BUTTON");
		blocks.add("TRAP_DOOR");
		blocks.add("IRON_TRAPDOOR");
		blocks.add("DIODE_BLOCK_OFF");
		blocks.add("DIODE_BLOCK_ON");
		blocks.add("REDSTONE_COMPARATOR_OFF");
		blocks.add("REDSTONE_COMPARATOR_ON");
		blocks.add("FENCE");
		blocks.add("SPRUCE_FENCE");
		blocks.add("BIRCH_FENCE");
		blocks.add("JUNGLE_FENCE");
		blocks.add("DARK_OAK_FENCE");
		blocks.add("ACACIA_FENCE");
		blocks.add("NETHER_FENCE");
		blocks.add("BREWING_STAND");
		blocks.add("CAULDRON");
		blocks.add("SIGN_POST");
		blocks.add("WALL_SIGN");
		blocks.add("SIGN");
		blocks.add("DRAGON_EGG");
		blocks.add("LEVER");
		return blocks;
	}

}