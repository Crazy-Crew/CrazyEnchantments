package me.badbones69.crazyenchantments.api;

public enum InfoType {

	HELMETS("Helmets"),
	BOOTS("Boots"),
	ARMOR("Armor"),
	SWORD("Sword"),
	AXE("Axe"),
	BOW("Bow"),
	PICKAXE("Pickaxe"),
	TOOL("Tool"),
	MISC("Misc");

	private String Name;

	private InfoType(String name) {
		Name = name;
	}

	public String getName() {
		return Name;
	}

	public static InfoType[] getTypes() {
		return values();
	}

}