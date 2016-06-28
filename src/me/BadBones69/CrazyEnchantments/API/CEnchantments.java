package me.BadBones69.CrazyEnchantments.API;

import me.BadBones69.CrazyEnchantments.Main;

public enum CEnchantments {
	
//	----------------Boots----------------  \\
	GEARS("Gears", EnchantmentType.BOOTS),
	WINGS("Wings", EnchantmentType.BOOTS),
	ROCKET("Rocket", EnchantmentType.BOOTS),
	SPRINGS("Springs", EnchantmentType.BOOTS),
	ANTIGRAVITY("AntiGravity", EnchantmentType.BOOTS),
//	----------------Bows----------------  \\
	BOOM("Boom", EnchantmentType.BOW),
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
	VOODOO("Voodoo", EnchantmentType.ARMOR),
	MOLTEN("Molten", EnchantmentType.ARMOR),
	SAVIOR("Savior", EnchantmentType.ARMOR),
	CACTUS("Cactus", EnchantmentType.ARMOR),
	FREEZE("Freeze", EnchantmentType.ARMOR),
	RECOVER("Recover", EnchantmentType.ARMOR),
	NURSERY("Nursery", EnchantmentType.ARMOR),
	FORTIFY("Fortify", EnchantmentType.ARMOR),
	OVERLOAD("OverLoad", EnchantmentType.ARMOR),
	INSOMNIA("Insomnia", EnchantmentType.ARMOR),
	SMOKEBOMB("SmokeBomb", EnchantmentType.ARMOR),
	PAINGIVER("PainGiver", EnchantmentType.ARMOR),
	BURNSHIELD("BurnShield", EnchantmentType.ARMOR),
	LEADERSHIP("Leadership", EnchantmentType.ARMOR),
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
	AUTOSMELT("AutoSmelt", EnchantmentType.PICKAXE),
	EXPERIENCE("Experience", EnchantmentType.PICKAXE),
//	----------------Tools----------------  \\
	HASTE("Haste", EnchantmentType.TOOL),
	TELEPATHY("Telepathy", EnchantmentType.TOOL),
	OXYGENATE("Oxygenate", EnchantmentType.TOOL),
//	----------------All----------------  \\
	HELLFORGED("HellForged", EnchantmentType.ALL);
	
	String Name;
	String CustomName;
	String BookColor;
	String EnchantmentColor;
	EnchantmentType Type;
	Boolean Toggle;
	
	private CEnchantments(String name, EnchantmentType type){
		Name=name;
		Type=type;
		if(Main.settings.getEnchs().contains("Enchantments."+name)){
			CustomName = Main.settings.getEnchs().getString("Enchantments."+Name+".Name");
			BookColor = Main.settings.getEnchs().getString("Enchantments."+name+".BookColor");
			EnchantmentColor = Main.settings.getEnchs().getString("Enchantments."+name+".Color");
			Toggle = Main.settings.getEnchs().getBoolean("Enchantments."+name+".Enabled");
		}else{
			Main.settings.getCustomEnchs().getString("Enchantments."+Name+".Name");
			BookColor = Main.settings.getCustomEnchs().getString("Enchantments."+name+".BookColor");
			EnchantmentColor = Main.settings.getCustomEnchs().getString("Enchantments."+name+".Color");
			Toggle = Main.settings.getCustomEnchs().getBoolean("Enchantments."+name+".Enabled");
		}
	}
	
	/**
	 * 
	 * @return The name of the enchantment.
	 */
	public String getName(){
		return Name;
	}
	
	/**
	 * 
	 * @return The custom name in the Enchantment.yml.
	 */
	public String getCustomName(){
		return CustomName;
	}
	
	/**
	 * 
	 * @return Return the color that goes on the Enchantment Book.
	 */
	public String getBookColor(){
		return BookColor;
	}
	
	/**
	 * 
	 * @return Returns the color that goes on the Enchanted Item.
	 */
	public String getEnchantmentColor(){
		return EnchantmentColor;
	}
	
	/**
	 * 
	 * @return The type the enchantment is.
	 */
	public EnchantmentType getType(){
		return Type;
	}
	
	/**
	 * 
	 * @return True if the enchantment is enabled and false if not.
	 */
	public Boolean isEnabled(){
		return Toggle;
	}
}