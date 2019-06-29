package me.badbones69.crazyenchantments.api.enums;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum CEnchantments {
	
	//	----------------Boots----------------  \\
	GEARS("Gears", EnchantmentType.BOOTS),
	WINGS("Wings", EnchantmentType.BOOTS),
	ROCKET("Rocket", EnchantmentType.BOOTS, 15, 5),
	SPRINGS("Springs", EnchantmentType.BOOTS),
	ANTIGRAVITY("AntiGravity", EnchantmentType.BOOTS),
	//	----------------Bows----------------  \\
	BOOM("Boom", EnchantmentType.BOW, 20, 10),
	PULL("Pull", EnchantmentType.BOW, 25, 10),
	VENOM("Venom", EnchantmentType.BOW, 10, 5),
	DOCTOR("Doctor", EnchantmentType.BOW),
	PIERCING("Piercing", EnchantmentType.BOW, 5, 5),
	ICEFREEZE("IceFreeze", EnchantmentType.BOW, 25, 10),
	LIGHTNING("Lightning", EnchantmentType.BOW, 25, 10),
	MULTIARROW("MultiArrow", EnchantmentType.BOW, 25, 10),
	//	----------------Helmets----------------  \\
	GLOWING("Glowing", EnchantmentType.HELMET),
	MERMAID("Mermaid", EnchantmentType.HELMET),
	IMPLANTS("Implants", EnchantmentType.HELMET, 5, 5),
	COMMANDER("Commander", EnchantmentType.HELMET),
	//	----------------Swords----------------  \\
	TRAP("Trap", EnchantmentType.SWORD, 10, 5),
	RAGE("Rage", EnchantmentType.SWORD),
	VIPER("Viper", EnchantmentType.SWORD, 10, 5),
	SNARE("Snare", EnchantmentType.SWORD, 10, 3),
	SLOWMO("SlowMo", EnchantmentType.SWORD, 5, 5),
	WITHER("Wither", EnchantmentType.SWORD, 10, 3),
	VAMPIRE("Vampire", EnchantmentType.SWORD, 5, 5),
	EXECUTE("Execute", EnchantmentType.SWORD),
	FASTTURN("FastTurn", EnchantmentType.SWORD, 5, 5),
	DISARMER("Disarmer", EnchantmentType.SWORD, 5, 1),
	HEADLESS("Headless", EnchantmentType.SWORD, 10, 10),
	PARALYZE("Paralyze", EnchantmentType.SWORD, 15, 5),
	BLINDNESS("Blindness", EnchantmentType.SWORD, 5, 1),
	LIFESTEAL("LifeSteal", EnchantmentType.SWORD, 15, 5),
	CONFUSION("Confusion", EnchantmentType.SWORD, 15, 5),
	NUTRITION("Nutrition", EnchantmentType.SWORD, 15, 5),
	SKILLSWIPE("SkillSwipe", EnchantmentType.SWORD, 5, 5),
	OBLITERATE("Obliterate", EnchantmentType.SWORD, 10, 5),
	INQUISITIVE("Inquisitive", EnchantmentType.SWORD, 50, 25),
	LIGHTWEIGHT("LightWeight", EnchantmentType.SWORD, 15, 5),
	DOUBLEDAMAGE("DoubleDamage", EnchantmentType.SWORD, 5, 1),
	//	----------------Armor----------------  \\
	HULK("Hulk", EnchantmentType.ARMOR),
	VALOR("Valor", EnchantmentType.ARMOR),
	DRUNK("Drunk", EnchantmentType.ARMOR),
	NINJA("Ninja", EnchantmentType.ARMOR),
	ANGEL("Angel", EnchantmentType.ARMOR),
	TAMER("Tamer", EnchantmentType.ARMOR),
	GUARDS("Guards", EnchantmentType.ARMOR),
	VOODOO("Voodoo", EnchantmentType.ARMOR, 15, 5),
	MOLTEN("Molten", EnchantmentType.ARMOR, 10, 1),
	SAVIOR("Savior", EnchantmentType.ARMOR, 15, 5),
	CACTUS("Cactus", EnchantmentType.ARMOR, 25, 25),
	FREEZE("Freeze", EnchantmentType.ARMOR, 10, 5),
	RECOVER("Recover", EnchantmentType.ARMOR),
	NURSERY("Nursery", EnchantmentType.ARMOR, 5, 5),
	RADIANT("Radiant", EnchantmentType.ARMOR),
	FORTIFY("Fortify", EnchantmentType.ARMOR, 10, 5),
	OVERLOAD("OverLoad", EnchantmentType.ARMOR),
	BLIZZARD("Blizzard", EnchantmentType.ARMOR),
	INSOMNIA("Insomnia", EnchantmentType.ARMOR, 10, 5),
	ACIDRAIN("AcidRain", EnchantmentType.ARMOR, 5, 5),
	SANDSTORM("SandStorm", EnchantmentType.ARMOR, 5, 5),
	SMOKEBOMB("SmokeBomb", EnchantmentType.ARMOR, 5, 5),
	PAINGIVER("PainGiver", EnchantmentType.ARMOR, 10, 5),
	INTIMIDATE("Intimidate", EnchantmentType.ARMOR),
	BURNSHIELD("BurnShield", EnchantmentType.ARMOR),
	LEADERSHIP("Leadership", EnchantmentType.ARMOR, 10, 5),
	INFESTATION("Infestation", EnchantmentType.ARMOR),
	NECROMANCER("Necromancer", EnchantmentType.ARMOR),
	STORMCALLER("StormCaller", EnchantmentType.ARMOR, 10, 5),
	ENLIGHTENED("Enlightened", EnchantmentType.ARMOR, 10, 5),
	SELFDESTRUCT("SelfDestruct", EnchantmentType.ARMOR),
	//	----------------Axes----------------  \\
	REKT("Rekt", EnchantmentType.AXE, 5, 1),
	DIZZY("Dizzy", EnchantmentType.AXE, 10, 5),
	CURSED("Cursed", EnchantmentType.AXE, 10, 5),
	FEEDME("FeedMe", EnchantmentType.AXE, 10, 5),
	BERSERK("Berserk", EnchantmentType.AXE, 10, 1),
	BLESSED("Blessed", EnchantmentType.AXE, 10, 5),
	DECAPITATION("Decapitation", EnchantmentType.AXE, 10, 10),
	//	----------------PickAxes----------------  \\
	BLAST("Blast", EnchantmentType.PICKAXE),
	AUTOSMELT("AutoSmelt", EnchantmentType.PICKAXE, 25, 25),
	EXPERIENCE("Experience", EnchantmentType.PICKAXE, 25, 25),
	FURNACE("Furnace", EnchantmentType.PICKAXE),
	//	----------------Tools----------------  \\
	HASTE("Haste", EnchantmentType.TOOL),
	TELEPATHY("Telepathy", EnchantmentType.TOOL),
	OXYGENATE("Oxygenate", EnchantmentType.TOOL),
	//	----------------All----------------  \\
	HELLFORGED("HellForged", EnchantmentType.ALL, 5, 5),
	//	----------------New Enchantments----------------  \\
	STICKY_SHOT("Sticky-Shot", EnchantmentType.BOW, 10, 10),
	DISORDER("Disorder", EnchantmentType.SWORD, 1, 0),
	CHARGE("Charge", EnchantmentType.SWORD),
	REVENGE("Revenge", EnchantmentType.SWORD),
	BATTLECRY("BattleCry", EnchantmentType.AXE, 10, 5),
	FAMISHED("Famished", EnchantmentType.SWORD, 10, 5),
	GREENTHUMB("GreenThumb", EnchantmentType.HOE, 10, 10);
	
	private String name;
	private EnchantmentType type;
	private Boolean hasChanceSystem;
	private Integer chance;
	private Integer chanceIncrease;
	
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	/**
	 *
	 * @param name Name of the enchantment.
	 * @param type Type of items it goes on.
	 */
	private CEnchantments(String name, EnchantmentType type) {
		this.name = name;
		this.type = type;
		this.chance = 0;
		this.chanceIncrease = 0;
		this.hasChanceSystem = false;
	}
	
	/**
	 *
	 * @param name Name of the enchantment.
	 * @param type Type of items it goes on.
	 * @param chance The chance the enchantment has to active.
	 * @param chanceIncrease The amount the chance increases by every level.
	 */
	private CEnchantments(String name, EnchantmentType type, Integer chance, Integer chanceIncrease) {
		this.name = name;
		this.type = type;
		this.chance = chance;
		this.chanceIncrease = chanceIncrease;
		this.hasChanceSystem = true;
	}
	
	/**
	 *
	 * @return The name of the enchantment.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 *
	 * @return The custom name in the Enchantment.yml.
	 */
	public String getCustomName() {
		return getEnchantment().getCustomName();
	}
	
	/**
	 * Get the chance the enchantment will activate.
	 *
	 * Not all enchantments have a chance to activate.
	 *
	 * @return The chance of the enchantment activating.
	 */
	public Integer getChance() {
		return chance;
	}
	
	/**
	 * Get the amount the enchantment chance increases by every level.
	 *
	 * Not all enchantments have a chance to activate.
	 *
	 * @return The amount the chance increases by every level.
	 */
	public Integer getChanceIncrease() {
		return chanceIncrease;
	}
	
	/**
	 *
	 * @return The description of the enchantment in the Enchantments.yml.
	 */
	public List<String> getDiscription() {
		return getEnchantment().getInfoDescription();
	}
	
	/**
	 *
	 * @return Return the color that goes on the Enchantment Book.
	 */
	public String getBookColor() {
		return Methods.color(getEnchantment().getBookColor());
	}
	
	/**
	 *
	 * @return Returns the color that goes on the Enchanted Item.
	 */
	public String getEnchantmentColor() {
		return Methods.color(getEnchantment().getColor());
	}
	
	/**
	 *
	 * @return The type the enchantment is.
	 */
	public EnchantmentType getType() {
		if(getEnchantment() == null || getEnchantment().getEnchantmentType() == null) {
			return type;
		}else {
			return getEnchantment().getEnchantmentType();
		}
	}
	
	/**
	 *
	 * @return True if the enchantment is enabled and false if not.
	 */
	public Boolean isActivated() {
		return getEnchantment().isActivated();
	}
	
	/**
	 * Get the enchantment that this is tied to.
	 * @return The enchantment this is tied to.
	 */
	public CEnchantment getEnchantment() {
		return ce.getEnchantmentFromName(name);
	}
	
	/**
	 * Get the level of the enchantment on an item.
	 * @param item The item that is being checked.
	 * @return The level of the enchantment that is on the item.
	 */
	public Integer getLevel(ItemStack item) {
		return getEnchantment().getPower(item);
	}
	
	/**
	 * Check to see if the enchantment's chance is successful.
	 * @return True if the chance was successful and false if not.
	 */
	public Boolean chanceSuccessful() {
		int chance = getEnchantment().getChance();
		return chance >= 100 || chance <= 0 || (new Random().nextInt(100) + 1) <= chance;
	}
	
	/**
	 * Check to see if the enchantment's chance is successful.
	 * @param item The item being checked.
	 * @return True if the chance was successful and false if not.
	 */
	public Boolean chanceSuccessful(ItemStack item) {
		return ce.getEnchantmentFromName(name).chanceSuccesful(getLevel(item));
	}
	
	/**
	 * Check if the CEnchantments uses a chance system.
	 */
	public Boolean hasChanceSystem() {
		return hasChanceSystem;
	}
	
	/**
	 * Get a CEnchantments from the enchantment name.
	 * @param enchant The name of the enchantment.
	 * @return Returns the CEnchantments but if not found it will be null.
	 */
	public static CEnchantments getFromName(String enchant) {
		for(CEnchantments ench : values()) {
			if(ench.getName().equalsIgnoreCase(enchant) || ench.getCustomName().equalsIgnoreCase(enchant)) {
				return ench;
			}
		}
		return null;
	}
	
	public static List<CEnchantments> getFromeNames(List<CEnchantment> enchantments) {
		List<CEnchantments> cEnchantments = new ArrayList<>();
		for(CEnchantment cEnchantment : enchantments) {
			CEnchantments enchantment = getFromName(cEnchantment.getName());
			if(enchantment != null) {
				cEnchantments.add(enchantment);
			}
		}
		return cEnchantments;
	}
	
}