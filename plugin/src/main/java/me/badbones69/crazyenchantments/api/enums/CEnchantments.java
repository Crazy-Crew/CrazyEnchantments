package me.badbones69.crazyenchantments.api.enums;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.EnchantmentType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum CEnchantments {
	
	//	----------------Boots----------------  \\
	GEARS("Gears", EnchantmentType.getFromName("Boots")),
	WINGS("Wings", EnchantmentType.getFromName("Boots")),
	ROCKET("Rocket", EnchantmentType.getFromName("Boots"), 15, 5),
	SPRINGS("Springs", EnchantmentType.getFromName("Boots")),
	ANTIGRAVITY("AntiGravity", EnchantmentType.getFromName("Boots")),
	//	----------------Bows----------------  \\
	BOOM("Boom", EnchantmentType.getFromName("Bow"), 20, 10),
	PULL("Pull", EnchantmentType.getFromName("Bow"), 25, 10),
	VENOM("Venom", EnchantmentType.getFromName("Bow"), 10, 5),
	DOCTOR("Doctor", EnchantmentType.getFromName("Bow")),
	PIERCING("Piercing", EnchantmentType.getFromName("Bow"), 5, 5),
	ICEFREEZE("IceFreeze", EnchantmentType.getFromName("Bow"), 25, 10),
	LIGHTNING("Lightning", EnchantmentType.getFromName("Bow"), 25, 10),
	MULTIARROW("MultiArrow", EnchantmentType.getFromName("Bow"), 25, 10),
	STICKY_SHOT("Sticky-Shot", EnchantmentType.getFromName("Bow"), 10, 10),
	//	----------------Helmets----------------  \\
	GLOWING("Glowing", EnchantmentType.getFromName("Helmet")),
	MERMAID("Mermaid", EnchantmentType.getFromName("Helmet")),
	IMPLANTS("Implants", EnchantmentType.getFromName("Helmet"), 5, 5),
	COMMANDER("Commander", EnchantmentType.getFromName("Helmet")),
	//	----------------Swords----------------  \\
	TRAP("Trap", EnchantmentType.getFromName("Sword"), 10, 5),
	RAGE("Rage", EnchantmentType.getFromName("Sword")),
	VIPER("Viper", EnchantmentType.getFromName("Sword"), 10, 5),
	SNARE("Snare", EnchantmentType.getFromName("Sword"), 10, 3),
	SLOWMO("SlowMo", EnchantmentType.getFromName("Sword"), 5, 5),
	WITHER("Wither", EnchantmentType.getFromName("Sword"), 10, 3),
	VAMPIRE("Vampire", EnchantmentType.getFromName("Sword"), 5, 5),
	EXECUTE("Execute", EnchantmentType.getFromName("Sword")),
	FASTTURN("FastTurn", EnchantmentType.getFromName("Sword"), 5, 5),
	DISARMER("Disarmer", EnchantmentType.getFromName("Sword"), 5, 1),
	HEADLESS("Headless", EnchantmentType.getFromName("Sword"), 10, 10),
	PARALYZE("Paralyze", EnchantmentType.getFromName("Sword"), 15, 5),
	BLINDNESS("Blindness", EnchantmentType.getFromName("Sword"), 5, 1),
	LIFESTEAL("LifeSteal", EnchantmentType.getFromName("Sword"), 15, 5),
	CONFUSION("Confusion", EnchantmentType.getFromName("Sword"), 15, 5),
	NUTRITION("Nutrition", EnchantmentType.getFromName("Sword"), 15, 5),
	SKILLSWIPE("SkillSwipe", EnchantmentType.getFromName("Sword"), 5, 5),
	OBLITERATE("Obliterate", EnchantmentType.getFromName("Sword"), 10, 5),
	INQUISITIVE("Inquisitive", EnchantmentType.getFromName("Sword"), 50, 25),
	LIGHTWEIGHT("LightWeight", EnchantmentType.getFromName("Sword"), 15, 5),
	DOUBLEDAMAGE("DoubleDamage", EnchantmentType.getFromName("Sword"), 5, 1),
	DISORDER("Disorder", EnchantmentType.getFromName("Sword"), 1, 0),
	CHARGE("Charge", EnchantmentType.getFromName("Sword")),
	REVENGE("Revenge", EnchantmentType.getFromName("Sword")),
	FAMISHED("Famished", EnchantmentType.getFromName("Sword"), 10, 5),
	//	----------------Armor----------------  \\
	HULK("Hulk", EnchantmentType.getFromName("Armor")),
	VALOR("Valor", EnchantmentType.getFromName("Armor")),
	DRUNK("Drunk", EnchantmentType.getFromName("Armor")),
	NINJA("Ninja", EnchantmentType.getFromName("Armor")),
	ANGEL("Angel", EnchantmentType.getFromName("Armor")),
	TAMER("Tamer", EnchantmentType.getFromName("Armor")),
	GUARDS("Guards", EnchantmentType.getFromName("Armor")),
	VOODOO("Voodoo", EnchantmentType.getFromName("Armor"), 15, 5),
	MOLTEN("Molten", EnchantmentType.getFromName("Armor"), 10, 1),
	SAVIOR("Savior", EnchantmentType.getFromName("Armor"), 15, 5),
	CACTUS("Cactus", EnchantmentType.getFromName("Armor"), 25, 25),
	FREEZE("Freeze", EnchantmentType.getFromName("Armor"), 10, 5),
	RECOVER("Recover", EnchantmentType.getFromName("Armor")),
	NURSERY("Nursery", EnchantmentType.getFromName("Armor"), 5, 5),
	RADIANT("Radiant", EnchantmentType.getFromName("Armor")),
	FORTIFY("Fortify", EnchantmentType.getFromName("Armor"), 10, 5),
	OVERLOAD("OverLoad", EnchantmentType.getFromName("Armor")),
	BLIZZARD("Blizzard", EnchantmentType.getFromName("Armor")),
	INSOMNIA("Insomnia", EnchantmentType.getFromName("Armor"), 10, 5),
	ACIDRAIN("AcidRain", EnchantmentType.getFromName("Armor"), 5, 5),
	SANDSTORM("SandStorm", EnchantmentType.getFromName("Armor"), 5, 5),
	SMOKEBOMB("SmokeBomb", EnchantmentType.getFromName("Armor"), 5, 5),
	PAINGIVER("PainGiver", EnchantmentType.getFromName("Armor"), 10, 5),
	INTIMIDATE("Intimidate", EnchantmentType.getFromName("Armor")),
	BURNSHIELD("BurnShield", EnchantmentType.getFromName("Armor")),
	LEADERSHIP("Leadership", EnchantmentType.getFromName("Armor"), 10, 5),
	INFESTATION("Infestation", EnchantmentType.getFromName("Armor")),
	NECROMANCER("Necromancer", EnchantmentType.getFromName("Armor")),
	STORMCALLER("StormCaller", EnchantmentType.getFromName("Armor"), 10, 5),
	ENLIGHTENED("Enlightened", EnchantmentType.getFromName("Armor"), 10, 5),
	SELFDESTRUCT("SelfDestruct", EnchantmentType.getFromName("Armor")),
	//	----------------Axes----------------  \\
	REKT("Rekt", EnchantmentType.getFromName("Axe"), 5, 1),
	DIZZY("Dizzy", EnchantmentType.getFromName("Axe"), 10, 5),
	CURSED("Cursed", EnchantmentType.getFromName("Axe"), 10, 5),
	FEEDME("FeedMe", EnchantmentType.getFromName("Axe"), 10, 5),
	BERSERK("Berserk", EnchantmentType.getFromName("Axe"), 10, 1),
	BLESSED("Blessed", EnchantmentType.getFromName("Axe"), 10, 5),
	DECAPITATION("Decapitation", EnchantmentType.getFromName("Axe"), 10, 10),
	BATTLECRY("BattleCry", EnchantmentType.getFromName("Axe"), 10, 5),
	//	----------------PickAxes----------------  \\
	BLAST("Blast", EnchantmentType.getFromName("Pickaxe")),
	AUTOSMELT("AutoSmelt", EnchantmentType.getFromName("Pickaxe"), 25, 25),
	EXPERIENCE("Experience", EnchantmentType.getFromName("Pickaxe"), 25, 25),
	FURNACE("Furnace", EnchantmentType.getFromName("Pickaxe")),
	//	----------------Tools----------------  \\
	HASTE("Haste", EnchantmentType.getFromName("Tool")),
	TELEPATHY("Telepathy", EnchantmentType.getFromName("Tool")),
	OXYGENATE("Oxygenate", EnchantmentType.getFromName("Tool")),
	//	----------------Hoes----------------  \\
	GREENTHUMB("GreenThumb", EnchantmentType.getFromName("Hoe"), 10, 10),
	HARVESTER("Harvester", EnchantmentType.getFromName("Hoe")),
	TILLER("Tiller", EnchantmentType.getFromName("Hoe")),
	PLANTER("Planter", EnchantmentType.getFromName("Hoe")),
	//	----------------All----------------  \\
	HELLFORGED("HellForged", EnchantmentType.getFromName("Damaged-Items"), 5, 5);
	
	private String name;
	private EnchantmentType type;
	private boolean hasChanceSystem;
	private int chance;
	private int chanceIncrease;
	
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
	private CEnchantments(String name, EnchantmentType type, int chance, int chanceIncrease) {
		this.name = name;
		this.type = type;
		this.chance = chance;
		this.chanceIncrease = chanceIncrease;
		this.hasChanceSystem = true;
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
	public int getChance() {
		return chance;
	}
	
	/**
	 * Get the amount the enchantment chance increases by every level.
	 *
	 * Not all enchantments have a chance to activate.
	 *
	 * @return The amount the chance increases by every level.
	 */
	public int getChanceIncrease() {
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
	public boolean isActivated() {
		return getEnchantment() != null && getEnchantment().isActivated();
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
	public int getLevel(ItemStack item) {
		return getEnchantment().getPower(item);
	}
	
	/**
	 * Check to see if the enchantment's chance is successful.
	 * @return True if the chance was successful and false if not.
	 */
	public boolean chanceSuccessful() {
		int chance = getEnchantment().getChance();
		return chance >= 100 || chance <= 0 || (new Random().nextInt(100) + 1) <= chance;
	}
	
	/**
	 * Check to see if the enchantment's chance is successful.
	 * @param item The item being checked.
	 * @return True if the chance was successful and false if not.
	 */
	public boolean chanceSuccessful(ItemStack item) {
		return ce.getEnchantmentFromName(name).chanceSuccesful(getLevel(item));
	}
	
	/**
	 * Check if the CEnchantments uses a chance system.
	 */
	public boolean hasChanceSystem() {
		return hasChanceSystem;
	}
	
}