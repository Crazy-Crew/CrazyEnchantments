package ca.thederpygolems.armorequip;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @Author Borlea
 * @Github https://github.com/borlea/
 * @Website http://thederpygolems.ca/
 */
public final class ArmorEquipEvent extends PlayerEvent implements Cancellable{

	private static final HandlerList handlers = new HandlerList();
	private boolean cancel = false;
	private final EquipMethod equipType;
	private final ArmorType type;
	private final ItemStack oldArmorPiece;
	private ItemStack newArmorPiece;

	/**
	 * Constructor for the ArmorEquipEvent.
	 *
	 * @param player The player who put on / removed the armor.
	 * @param type The ArmorType of the armor added
	 * @param oldArmorPiece The ItemStack of the armor removed.
	 * @param newArmorPiece The ItemStack of the armor added.
	 */
	public ArmorEquipEvent(final Player player, final EquipMethod equipType, final ArmorType type, final ItemStack oldArmorPiece, final ItemStack newArmorPiece){
		super(player);
		this.equipType = equipType;
		this.type = type;
		this.oldArmorPiece = oldArmorPiece;
		this.newArmorPiece = newArmorPiece;
	}

	/**
	 * Gets a list of handlers handling this event.
	 *
	 * @return A list of handlers handling this event.
	 */
	public final static HandlerList getHandlerList(){
		return handlers;
	}

	/**
	 * Gets a list of handlers handling this event.
	 *
	 * @return A list of handlers handling this event.
	 */
	@Override
	public final HandlerList getHandlers(){
		return handlers;
	}

	/**
	 * Sets if this event should be cancelled.
	 *
	 * @param cancel If this event should be cancelled.
	 */
	public final void setCancelled(final boolean cancel){
		this.cancel = cancel;
	}

	/**
	 * Gets if this event is cancelled.
	 *
	 * @return If this event is cancelled
	 */
	public final boolean isCancelled(){
		return cancel;
	}

	public final ArmorType getType(){
		return type;
	}

	/**
	 * Returns the last equipped armor piece, could be a piece of armor, an AIR material, or null.
	 */
	public final ItemStack getOldArmorPiece(){
		return oldArmorPiece;
	}

	/**
	 * Returns the newly equipped armor, could be a piece of armor, an AIR material, or null.
	 */
	public final ItemStack getNewArmorPiece(){
		return newArmorPiece;
	}

	public final void setNewArmorPiece(final ItemStack newArmorPiece){
		this.newArmorPiece = newArmorPiece;
	}

	/**
	 * Gets the method used to either equip or uneqiip an armor piece.
	 */
	public EquipMethod getMethod(){
		return equipType;
	}


	public enum EquipMethod{
		SHIFT_CLICK, DRAG, HOTBAR, DISPENSER, BROKE, DEATH;
	}
}