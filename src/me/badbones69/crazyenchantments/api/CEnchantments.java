package me.badbones69.crazyenchantments.api;

import java.util.List;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;

public enum CEnchantments {
	
//	----------------Boots----------------  \\
	GEARS("Gears", EnchantmentType.BOOTS),
	WINGS("Wings", EnchantmentType.BOOTS),
	ROCKET("Rocket", EnchantmentType.BOOTS),
	SPRINGS("Springs", EnchantmentType.BOOTS),
	ANTIGRAVITY("AntiGravity", EnchantmentType.BOOTS),
//	----------------Bows----------------  \\
	BOOM("Boom", EnchantmentType.BOW),
	PULL("Pull", EnchantmentType.BOW),
	VENOM("Venom", EnchantmentType.BOW),
	DOCTOR("Doctor", EnchantmentType.BOW),
	PIERCING("Piercing", EnchantmentType.BOW),
	ICEFREEZE("IceFreeze", EnchantmentType.BOW),
	LIGHTNING("Lightning", EnchantmentType.BOW),
	MULTIARROW("MultiArrow", EnchantmentType.BOW),
//	----------------Helmets----------------  \\
	GLOWING("Glowing", EnchantmentType.HELMET),
	MERMAID("Mermaid", EnchantmentType.HELMET),
	IMPLANTS("Implants", EnchantmentType.HELMET),
	COMMANDER("Commander", EnchantmentType.HELMET),
//	----------------Swords----------------  \\
	TRAP("Trap", EnchantmentType.SWORD),
	RAGE("Rage", EnchantmentType.SWORD),
	VIPER("Viper", EnchantmentType.SWORD),
	SNARE("Snare", EnchantmentType.SWORD),
	SLOWMO("SlowMo", EnchantmentType.SWORD),
	WITHER("Wither", EnchantmentType.SWORD),
	VAMPIRE("Vampire", EnchantmentType.SWORD),
	EXECUTE("Execute", EnchantmentType.SWORD),
	FASTTURN("FastTurn", EnchantmentType.SWORD),
	DISARMER("Disarmer", EnchantmentType.SWORD),
	HEADLESS("Headless", EnchantmentType.SWORD),
	PARALYZE("Paralyze", EnchantmentType.SWORD),
	BLINDNESS("Blindness", EnchantmentType.SWORD),
	LIFESTEAL("LifeSteal", EnchantmentType.SWORD),
	CONFUSION("Confusion", EnchantmentType.SWORD),
	NUTRITION("Nutrition", EnchantmentType.SWORD),
	SKILLSWIPE("SkillSwipe", EnchantmentType.SWORD),
	OBLITERATE("Obliterate", EnchantmentType.SWORD),
	INQUISITIVE("Inquisitive", EnchantmentType.SWORD),
	LIGHTWEIGHT("LightWeight", EnchantmentType.SWORD),
	DOUBLEDAMAGE("DoubleDamage", EnchantmentType.SWORD),
//	----------------Armor----------------  \\
	HULK("Hulk", EnchantmentType.ARMOR),
	VALOR("Valor", EnchantmentType.ARMOR),
	DRUNK("Drunk", EnchantmentType.ARMOR),
	NINJA("Ninja", EnchantmentType.ARMOR),
	ANGEL("Angel", EnchantmentType.ARMOR),
	TAMER("Tamer", EnchantmentType.ARMOR),
	GUARDS("Guards", EnchantmentType.ARMOR),
	VOODOO("Voodoo", EnchantmentType.ARMOR),
	MOLTEN("Molten", EnchantmentType.ARMOR),
	SAVIOR("Savior", EnchantmentType.ARMOR),
	CACTUS("Cactus", EnchantmentType.ARMOR),
	FREEZE("Freeze", EnchantmentType.ARMOR),
	RECOVER("Recover", EnchantmentType.ARMOR),
	NURSERY("Nursery", EnchantmentType.ARMOR),
	RADIANT("Radiant", EnchantmentType.ARMOR),
	FORTIFY("Fortify", EnchantmentType.ARMOR),
	OVERLOAD("OverLoad", EnchantmentType.ARMOR),
	BLIZZARD("Blizzard", EnchantmentType.ARMOR),
	INSOMNIA("Insomnia", EnchantmentType.ARMOR),
	ACIDRAIN("AcidRain", EnchantmentType.ARMOR),
	SANDSTORM("SandStorm", EnchantmentType.ARMOR),
	SMOKEBOMB("SmokeBomb", EnchantmentType.ARMOR),
	PAINGIVER("PainGiver", EnchantmentType.ARMOR),
	INTIMIDATE("Intimidate", EnchantmentType.ARMOR),
	BURNSHIELD("BurnShield", EnchantmentType.ARMOR),
	LEADERSHIP("Leadership", EnchantmentType.ARMOR),
	INFESTATION("Infestation", EnchantmentType.ARMOR),
	NECROMANCER("Necromancer", EnchantmentType.ARMOR),
	STORMCALLER("StormCaller", EnchantmentType.ARMOR),
	ENLIGHTENED("Enlightened", EnchantmentType.ARMOR),
	SELFDESTRUCT("SelfDestruct", EnchantmentType.ARMOR),
//	----------------Axes----------------  \\
	REKT("Rekt", EnchantmentType.AXE),
	DIZZY("Dizzy", EnchantmentType.AXE),
	CURSED("Cursed", EnchantmentType.AXE),
	FEEDME("FeedMe", EnchantmentType.AXE),
	BERSERK("Berserk", EnchantmentType.AXE),
	BLESSED("Blessed", EnchantmentType.AXE),
	DECAPITATION("Decapitation", EnchantmentType.AXE),
//	----------------PickAxes----------------  \\
	BLAST("Blast", EnchantmentType.PICKAXE),
	AUTOSMELT("AutoSmelt", EnchantmentType.PICKAXE),
	EXPERIENCE("Experience", EnchantmentType.PICKAXE),
	FURNACE("Furnace", EnchantmentType.PICKAXE),
//	----------------Tools----------------  \\
	HASTE("Haste", EnchantmentType.TOOL),
	TELEPATHY("Telepathy", EnchantmentType.TOOL),
	OXYGENATE("Oxygenate", EnchantmentType.TOOL),
//	----------------All----------------  \\
	HELLFORGED("HellForged", EnchantmentType.ALL);
	
	private String name;
	private EnchantmentType type;
	
	/**
	 * 
	 * @param name Name of the enchantment.
	 * @param type Type of items it goes on.
	 */
	private CEnchantments(String name, EnchantmentType type){
		this.name = name;
		this.type = type;
	}
	
	/**
	 * 
	 * @return The name of the enchantment.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * 
	 * @return The custom name in the Enchantment.yml.
	 */
	public String getCustomName(){
		return getEnchantment().getCustomName();
	}
	
	/**
	 * 
	 * @return The description of the enchantment in the Enchantments.yml.
	 */
	public List<String> getDiscription(){
		return getEnchantment().getInfoDescription();
	}
	
	/**
	 * 
	 * @return Return the color that goes on the Enchantment Book.
	 */
	public String getBookColor(){
		return Methods.color(getEnchantment().getBookColor());
	}
	
	/**
	 * 
	 * @return Returns the color that goes on the Enchanted Item.
	 */
	public String getEnchantmentColor(){
		return Methods.color(getEnchantment().getColor());
	}
	
	/**
	 * 
	 * @return The type the enchantment is.
	 */
	public EnchantmentType getType(){
		if(getEnchantment() == null || getEnchantment().getEnchantmentType() == null){
			return type;
		}else{
			return getEnchantment().getEnchantmentType();
		}
	}
	
	/**
	 * 
	 * @return True if the enchantment is enabled and false if not.
	 */
	public Boolean isEnabled(){
		return getEnchantment().isActivated();
	}
	
	/**
	 * Get the enchantment that this is tied to.
	 * @return The enchantment this is tied to.
	 */
	public CEnchantment getEnchantment(){
		return Main.CE.getEnchantmentFromName(name);
	}
	
	/**
	 * Get a CEnchantments from the enchantment name.
	 * @param enchant The name of the enchantment.
	 * @return Returns the CEnchantments but if not found it will be null.
	 */
	public static CEnchantments getFromName(String enchant){
		for(CEnchantments ench : values()){
			if(ench.getName().equalsIgnoreCase(enchant) || ench.getCustomName().equalsIgnoreCase(enchant)){
				return ench;
			}
		}
		return null;
	}
	
}