package me.BadBones69.CrazyEnchantments.API;

public enum Enchantment {
	GEARS("Gears", EnchantmentType.BOOTS),
	SPRINGS("Springs",EnchantmentType.BOOTS),
	ANTIGRAVITY("AntiGravity", EnchantmentType.BOOTS);
	String Name;
	EnchantmentType Type;
	private Enchantment(String name, EnchantmentType type){
		this.Name=name;
		this.Type=type;
	}
	public String getName(){
		return Name;
	}
	public EnchantmentType getType(){
		return Type;
	}
}