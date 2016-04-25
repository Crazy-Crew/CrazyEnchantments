package me.BadBones69.CrazyEnchantments.API;

import me.BadBones69.CrazyEnchantments.Api;

public enum Enchantment {
	GEARS("Gears", EnchantmentType.BOOTS),
	SPRINGS("Springs", EnchantmentType.BOOTS),
	ANTIGRAVITY("AntiGravity", EnchantmentType.BOOTS),
	BOOM("Boom", EnchantmentType.BOW),
	DOCTOR("Doctor", EnchantmentType.BOW),
	PIERCING("Piercing", EnchantmentType.BOW),
	VENOM("Venom", EnchantmentType.BOW),
	GLOWING("Glowing", EnchantmentType.HELMET),
	MERMAID("Mermaid", EnchantmentType.HELMET),
	BLINDNESS("Blindness", EnchantmentType.SWORD),
	DOUBLEDAMAGE("DoubleDamage", EnchantmentType.SWORD),
	FASTTURN("FastTurn", EnchantmentType.SWORD),
	LIGHTWEIGHT("LightWeight", EnchantmentType.SWORD),
	LIFESTEAL("LifeSteal", EnchantmentType.SWORD),
	SLOWMO("SlowMo", EnchantmentType.SWORD),
	VAMPIRE("Vampire", EnchantmentType.SWORD),
	VIPER("Viper", EnchantmentType.SWORD),
	BURNSHIELD("BurnShield", EnchantmentType.ARMOR),
	ENLIGHTENED("Enlightened", EnchantmentType.ARMOR),
	FORTIFY("Fortify", EnchantmentType.ARMOR),
	FREEZE("Freeze", EnchantmentType.ARMOR),
	HULK("Hulk", EnchantmentType.ARMOR),
	MOLTEN("Molten", EnchantmentType.ARMOR),
	NINJA("Ninja", EnchantmentType.ARMOR),
	NURSERY("Nursery", EnchantmentType.ARMOR),
	OVERLOAD("OverLoad", EnchantmentType.ARMOR),
	SAVIOR("Savior", EnchantmentType.ARMOR),
	SELFDESTRUCT("SelfDestruct", EnchantmentType.ARMOR),
	BERSERK("Berserk", EnchantmentType.AXE),
	BLESSED("Blessed", EnchantmentType.AXE),
	CURSED("Cursed", EnchantmentType.AXE),
	DIZZY("Dizzy", EnchantmentType.AXE),
	FEEDME("FeedMe", EnchantmentType.AXE),
	REKT("Rekt", EnchantmentType.AXE);
	
	String Name;
	EnchantmentType Type;
	
	private Enchantment(String name, EnchantmentType type){
		this.Name=name;
		this.Type=type;
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
		return Api.getEnchName(Name);
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
	 * @return List of all the enchantments.
	 */
	public static Enchantment[] getEnchantments(){
		Enchantment[] enchs=Enchantment.values();
		return enchs;
	}
	/**
	 * 
	 * @param name The name or custom name of the enchantment.
	 * @return The enchantment.
	 */
	public static Enchantment getFromName(String name){
		for(Enchantment e : getEnchantments()){
			if(e.getName().equalsIgnoreCase(name)){
				return e;
			}
			if(e.getCustomName().equalsIgnoreCase(name)){
				return e;
			}
		}
		return null;
	}
}