package com.badbones69.crazyenchantments.listeners;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.enums.ArmorType;
import com.badbones69.crazyenchantments.api.events.ArmorEquipEvent;
import com.badbones69.crazyenchantments.api.events.ArmorEquipEvent.EquipMethod;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arnah
 * @since Jul 30, 2015
 */
public class ArmorListener implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    private final List<String> blockedMaterials;

    public ArmorListener() {
        this.blockedMaterials = getBlocks();
    }

    @EventHandler(ignoreCancelled = true)
    public final void onInventoryClick(InventoryClickEvent e) {
        boolean shift = false;
        boolean numberKey = false;
        Player player = (Player) e.getWhoClicked();

        if (e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT)) shift = true;

        if (e.getClick().equals(ClickType.NUMBER_KEY)) numberKey = true;

        if ((e.getSlotType() != SlotType.ARMOR || e.getSlotType() != SlotType.QUICKBAR) &&
        !(e.getInventory().getType().equals(InventoryType.CRAFTING) || e.getInventory().getType().equals(InventoryType.PLAYER))) return;

        if (!(e.getWhoClicked() instanceof Player) || e.getCurrentItem() == null) return;

        ArmorType newArmorType = ArmorType.matchType(shift ? e.getCurrentItem() : e.getCursor());

        // Used for drag and drop checking to make sure you aren't trying to place a helmet in the boots place.
        if (!shift && newArmorType != null && e.getRawSlot() != newArmorType.getSlot()) return;

        if (e.getInventory().getType() == InventoryType.CRAFTING) {
            // Stops the activation when a player shift clicks from their small crafting option.
            if (e.getRawSlot() >= 0 && e.getRawSlot() <= 4) return;
        }

        if (shift) {
            newArmorType = ArmorType.matchType(e.getCurrentItem());
            if (newArmorType != null) {
                boolean equipping = e.getRawSlot() != newArmorType.getSlot();

                if (newArmorType.equals(ArmorType.HELMET) && (equipping == (e.getWhoClicked().getInventory().getHelmet() == null)) ||
                newArmorType.equals(ArmorType.CHESTPLATE) && (equipping == (e.getWhoClicked().getInventory().getChestplate() == null)) ||
                newArmorType.equals(ArmorType.LEGGINGS) && (equipping == (e.getWhoClicked().getInventory().getLeggings() == null)) ||
                newArmorType.equals(ArmorType.BOOTS) && (equipping == (e.getWhoClicked().getInventory().getBoots() == null))) {
                    ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(player, EquipMethod.SHIFT_CLICK, newArmorType,
                    equipping ? null : e.getCurrentItem(), equipping ? e.getCurrentItem() : null);
                    plugin.getServer().getPluginManager().callEvent(armorEquipEvent);

                    if (armorEquipEvent.isCancelled()) e.setCancelled(true);
                }
            }
        } else {
            ItemStack newArmorPiece = e.getCursor();

            if (newArmorPiece == null) newArmorPiece = new ItemStack(Material.AIR);

            ItemStack oldArmorPiece = e.getCurrentItem();

            if (oldArmorPiece == null) oldArmorPiece = new ItemStack(Material.AIR);

            if (numberKey) {
                if (e.getInventory().getType().equals(InventoryType.PLAYER)) { // Prevents shit in the 2by2 crafting
                    // e.getClickedInventory() == The players inventory
                    // e.getHotBarButton() == key people are pressing to equip or unequip the item to or from.
                    // e.getRawSlot() == The slot the item is going to.
                    // e.getSlot() == Armor slot, can't use e.getRawSlot() as that gives a hotbar slot ;-;
                    ItemStack hotBarItem = e.getInventory().getItem(e.getHotbarButton());

                    if (hotBarItem != null) { // Equipping
                        newArmorType = ArmorType.matchType(hotBarItem);
                        newArmorPiece = hotBarItem;
                        oldArmorPiece = e.getInventory().getItem(e.getSlot());
                    } else { // Unequipping
                        newArmorType = ArmorType.matchType(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR ? e.getCurrentItem() : e.getCursor());
                    }
                }
            } else {
                // e.getCurrentItem() == Unequip
                // e.getCursor() == Equip
                newArmorType = ArmorType.matchType(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR ? e.getCurrentItem() : e.getCursor());
            }

            if (newArmorType != null && e.getRawSlot() == newArmorType.getSlot()) {
                EquipMethod method = EquipMethod.DRAG;

                if (e.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberKey) method = EquipMethod.HOTBAR_SWAP;

                final ItemStack It = newArmorPiece.clone();
                final ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(player, method, newArmorType, oldArmorPiece, newArmorPiece);

                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    ItemStack I = e.getWhoClicked().getInventory().getItem(e.getSlot());

                    if (e.getInventory().getType().equals(InventoryType.PLAYER)) {

                        if (e.getSlot() == ArmorType.HELMET.getSlot()) I = e.getWhoClicked().getEquipment().getHelmet();

                        if (e.getSlot() == ArmorType.CHESTPLATE.getSlot()) I = e.getWhoClicked().getEquipment().getChestplate();

                        if (e.getSlot() == ArmorType.LEGGINGS.getSlot()) I = e.getWhoClicked().getEquipment().getLeggings();

                        if (e.getSlot() == ArmorType.BOOTS.getSlot()) I = e.getWhoClicked().getEquipment().getBoots();
                    }

                    if (I == null) {
                        if (e.getInventory().getType().equals(InventoryType.PLAYER)) {
                            I = new ItemStack(Material.AIR, 1);
                        } else {
                            I = new ItemStack(Material.AIR, 0);
                        }
                    }

                    // I == the old item
                    // It == the new item
                    if (I.isSimilar(It) || (I.getType() == Material.AIR && It.getType() == Material.AIR)) {
                        plugin.getServer().getPluginManager().callEvent(armorEquipEvent);

                        if (armorEquipEvent.isCancelled()) e.setCancelled(true);
                    }
                }, 0);
            } else {
                if (e.getHotbarButton() >= 0) {
                    newArmorPiece = e.getWhoClicked().getInventory().getItem(e.getHotbarButton());

                    if (oldArmorPiece != null) {
                        if (ArmorType.matchType(oldArmorPiece) != null || oldArmorPiece.getType() == Material.AIR) {
                            if (ArmorType.matchType(newArmorPiece) != null || newArmorPiece == null) {

                                if (ArmorType.matchType(oldArmorPiece) != null) {
                                    if (e.getRawSlot() != ArmorType.matchType(oldArmorPiece).getSlot()) return;
                                }

                                if (ArmorType.matchType(newArmorPiece) != null) {
                                    if (e.getRawSlot() != ArmorType.matchType(newArmorPiece).getSlot()) return;
                                }

                                EquipMethod method = EquipMethod.DRAG;

                                if (e.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberKey) method = EquipMethod.HOTBAR_SWAP;

                                ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(player, method, newArmorType, oldArmorPiece, newArmorPiece);
                                plugin.getServer().getPluginManager().callEvent(armorEquipEvent);

                                if (armorEquipEvent.isCancelled()) e.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerInteractEvent(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL) return;
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if (e.useInteractedBlock() == Event.Result.DENY || e.useItemInHand() == Event.Result.DENY) return;

            final Player player = e.getPlayer();

            if (e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK) { // Having both of these checks is useless, might as well do it though.
                // Some blocks have actions when you right-click them which stops the client from equipping the armor in hand.
                Material mat = e.getClickedBlock().getType();

                for (String s : blockedMaterials) {
                    if (mat.name().toLowerCase().contains(s.toLowerCase())) return;
                }
            }

            ArmorType newArmorType = ArmorType.matchType(e.getItem());

            if (newArmorType != null) {
                if (newArmorType.equals(ArmorType.HELMET) && e.getPlayer().getInventory().getHelmet() == null || newArmorType.equals(ArmorType.CHESTPLATE) && e.getPlayer().getInventory().getChestplate() == null || newArmorType.equals(ArmorType.LEGGINGS) && e.getPlayer().getInventory().getLeggings() == null || newArmorType.equals(ArmorType.BOOTS) && e.getPlayer().getInventory().getBoots() == null) {
                    ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(e.getPlayer(), EquipMethod.HOTBAR, ArmorType.matchType(e.getItem()), null, e.getItem());
                    plugin.getServer().getPluginManager().callEvent(armorEquipEvent);

                    if (armorEquipEvent.isCancelled()) {
                        e.setCancelled(true);
                        player.updateInventory();
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void dispenserFireEvent(BlockDispenseEvent e) {
        if (crazyManager.isIgnoredEvent(e)) return;

        ArmorType type = ArmorType.matchType(e.getItem());

        if (ArmorType.matchType(e.getItem()) != null) {
            Location loc = e.getBlock().getLocation();
            for (Player p : loc.getWorld().getPlayers()) {
                if (loc.getBlockY() - p.getLocation().getBlockY() >= -1 && loc.getBlockY() - p.getLocation().getBlockY() <= 1) {
                    if (p.getInventory().getHelmet() == null
                            && type.equals(ArmorType.HELMET) || p.getInventory().getChestplate() == null
                            && type.equals(ArmorType.CHESTPLATE) || p.getInventory().getLeggings() == null
                            && type.equals(ArmorType.LEGGINGS) || p.getInventory().getBoots() == null
                            && type.equals(ArmorType.BOOTS)) {

                        if (e.getBlock().getState() instanceof org.bukkit.block.Dispenser dispenser) {
                            org.bukkit.material.Dispenser dis = (org.bukkit.material.Dispenser) dispenser.getData();
                            BlockFace directionFacing = dis.getFacing();

                            // Someone told me not to do big if checks because it's hard to read, look at me doing it -_-
                            if (directionFacing == BlockFace.EAST && p.getLocation().getBlockX() != loc.getBlockX()
                                    && p.getLocation().getX() <= loc.getX() + 2.3 && p.getLocation().getX() >= loc.getX()
                                    || directionFacing == BlockFace.WEST && p.getLocation().getX() >= loc.getX() - 1.3
                                    && p.getLocation().getX() <= loc.getX() || directionFacing == BlockFace.SOUTH
                                    && p.getLocation().getBlockZ() != loc.getBlockZ() && p.getLocation().getZ() <= loc.getZ() + 2.3
                                    && p.getLocation().getZ() >= loc.getZ() || directionFacing == BlockFace.NORTH
                                    && p.getLocation().getZ() >= loc.getZ() - 1.3
                                    && p.getLocation().getZ() <= loc.getZ()) {

                                ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, EquipMethod.DISPENSER, ArmorType.matchType(e.getItem()), null, e.getItem());
                                plugin.getServer().getPluginManager().callEvent(armorEquipEvent);

                                if (armorEquipEvent.isCancelled()) e.setCancelled(true);

                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void itemBreakEvent(PlayerItemBreakEvent e) {
        ArmorType type = ArmorType.matchType(e.getBrokenItem());

        if (type != null) {
            Player p = e.getPlayer();
            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, EquipMethod.BROKE, type, e.getBrokenItem(), null);
            plugin.getServer().getPluginManager().callEvent(armorEquipEvent);

            if (armorEquipEvent.isCancelled()) {
                ItemStack item = e.getBrokenItem().clone();

                methods.setDurability(item, methods.getDurability(item) - 1);

                if (type.equals(ArmorType.HELMET)) {
                    p.getInventory().setHelmet(item);
                } else if (type.equals(ArmorType.CHESTPLATE)) {
                    p.getInventory().setChestplate(item);
                } else if (type.equals(ArmorType.LEGGINGS)) {
                    p.getInventory().setLeggings(item);
                } else if (type.equals(ArmorType.BOOTS)) {
                    p.getInventory().setBoots(item);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void playerDeathEvent(PlayerDeathEvent e) {
        Player player = e.getEntity();

        for (ItemStack i : player.getInventory().getArmorContents()) {
            if (i != null && !i.getType().equals(Material.AIR)) {
                plugin.getServer().getPluginManager().callEvent(new ArmorEquipEvent(player, EquipMethod.DEATH, ArmorType.matchType(i), i, null));
                // No way to cancel a death event.
            }
        }
    }

    /**
     * A utility method to support versions that use null or air ItemStacks.
     */
    public boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }

    private List<String> getBlocks() {
        List<String> blocks = new ArrayList<>();
        blocks.add("DAYLIGHT_DETECTOR");
        blocks.add("DAYLIGHT_DETECTOR_INVERTED");
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
        blocks.add("SHULKER_BOX");
        return blocks;
    }
}