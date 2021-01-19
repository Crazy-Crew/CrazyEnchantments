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
    GEARS("Gears", "Boots"),
    WINGS("Wings", "Boots"),
    ROCKET("Rocket", "Boots", 15, 5),
    SPRINGS("Springs", "Boots"),
    ANTIGRAVITY("AntiGravity", "Boots"),
    //	----------------Bows----------------  \\
    BOOM("Boom", "Bow", 20, 10),
    PULL("Pull", "Bow", 25, 10),
    VENOM("Venom", "Bow", 10, 5),
    DOCTOR("Doctor", "Bow"),
    PIERCING("Piercing", "Bow", 5, 5),
    ICEFREEZE("IceFreeze", "Bow", 25, 10),
    LIGHTNING("Lightning", "Bow", 25, 10),
    MULTIARROW("MultiArrow", "Bow", 25, 10),
    STICKY_SHOT("Sticky-Shot", "Bow", 10, 10),
    SNIPER("Sniper", "Bow", 25, 5),
    //	----------------Helmets----------------  \\
    GLOWING("Glowing", "Helmet"),
    MERMAID("Mermaid", "Helmet"),
    IMPLANTS("Implants", "Helmet", 5, 5),
    COMMANDER("Commander", "Helmet"),
    //	----------------Swords----------------  \\
    TRAP("Trap", "Sword", 10, 5),
    RAGE("Rage", "Sword"),
    VIPER("Viper", "Sword", 10, 5),
    SNARE("Snare", "Sword", 10, 3),
    SLOWMO("SlowMo", "Sword", 5, 5),
    WITHER("Wither", "Sword", 10, 3),
    VAMPIRE("Vampire", "Sword", 5, 5),
    EXECUTE("Execute", "Sword"),
    FASTTURN("FastTurn", "Sword", 5, 5),
    DISARMER("Disarmer", "Sword", 5, 1),
    HEADLESS("Headless", "Sword", 10, 10),
    PARALYZE("Paralyze", "Sword", 15, 5),
    BLINDNESS("Blindness", "Sword", 5, 1),
    LIFESTEAL("LifeSteal", "Sword", 15, 5),
    CONFUSION("Confusion", "Sword", 15, 5),
    NUTRITION("Nutrition", "Sword", 15, 5),
    SKILLSWIPE("SkillSwipe", "Sword", 5, 5),
    OBLITERATE("Obliterate", "Sword", 10, 5),
    INQUISITIVE("Inquisitive", "Sword", 50, 25),
    LIGHTWEIGHT("LightWeight", "Sword", 15, 5),
    DOUBLEDAMAGE("DoubleDamage", "Sword", 5, 1),
    DISORDER("Disorder", "Sword", 1, 0),
    CHARGE("Charge", "Sword"),
    REVENGE("Revenge", "Sword"),
    FAMISHED("Famished", "Sword", 10, 5),
    //	----------------Armor----------------  \\
    HULK("Hulk", "Armor"),
    VALOR("Valor", "Armor"),
    DRUNK("Drunk", "Armor"),
    NINJA("Ninja", "Armor"),
    ANGEL("Angel", "Armor"),
    TAMER("Tamer", "Armor"),
    GUARDS("Guards", "Armor"),
    VOODOO("Voodoo", "Armor", 15, 5),
    MOLTEN("Molten", "Armor", 10, 1),
    SAVIOR("Savior", "Armor", 15, 5),
    CACTUS("Cactus", "Armor", 25, 25),
    FREEZE("Freeze", "Armor", 10, 5),
    RECOVER("Recover", "Armor"),
    NURSERY("Nursery", "Armor", 5, 5),
    RADIANT("Radiant", "Armor"),
    FORTIFY("Fortify", "Armor", 10, 5),
    OVERLOAD("OverLoad", "Armor"),
    BLIZZARD("Blizzard", "Armor"),
    INSOMNIA("Insomnia", "Armor", 10, 5),
    ACIDRAIN("AcidRain", "Armor", 5, 5),
    SANDSTORM("SandStorm", "Armor", 5, 5),
    SMOKEBOMB("SmokeBomb", "Armor", 5, 5),
    PAINGIVER("PainGiver", "Armor", 10, 5),
    INTIMIDATE("Intimidate", "Armor"),
    BURNSHIELD("BurnShield", "Armor"),
    LEADERSHIP("Leadership", "Armor", 10, 5),
    INFESTATION("Infestation", "Armor"),
    NECROMANCER("Necromancer", "Armor"),
    STORMCALLER("StormCaller", "Armor", 10, 5),
    ENLIGHTENED("Enlightened", "Armor", 10, 5),
    SELFDESTRUCT("SelfDestruct", "Armor"),
    CYBORG("Cyborg", "Armor"),
    BEEKEEPER("BeeKeeper", "Armor"),
    //	----------------Axes----------------  \\
    REKT("Rekt", "Axe", 5, 1),
    DIZZY("Dizzy", "Axe", 10, 5),
    CURSED("Cursed", "Axe", 10, 5),
    FEEDME("FeedMe", "Axe", 10, 5),
    BERSERK("Berserk", "Axe", 10, 1),
    BLESSED("Blessed", "Axe", 10, 5),
    DECAPITATION("Decapitation", "Axe", 10, 10),
    BATTLECRY("BattleCry", "Axe", 10, 5),
    //	----------------PickAxes----------------  \\
    BLAST("Blast", "Pickaxe"),
    AUTOSMELT("AutoSmelt", "Pickaxe", 25, 25),
    EXPERIENCE("Experience", "Pickaxe", 25, 25),
    FURNACE("Furnace", "Pickaxe"),
    //	----------------Tools----------------  \\
    HASTE("Haste", "Tool"),
    TELEPATHY("Telepathy", "Tool"),
    OXYGENATE("Oxygenate", "Tool"),
    //	----------------Hoes----------------  \\
    GREENTHUMB("GreenThumb", "Hoe", 10, 10),
    HARVESTER("Harvester", "Hoe"),
    TILLER("Tiller", "Hoe"),
    PLANTER("Planter", "Hoe"),
    //	----------------All----------------  \\
    HELLFORGED("HellForged", "Damaged-Items", 5, 5);
    
    private String name;
    private String typeName;
    private boolean hasChanceSystem;
    private int chance;
    private int chanceIncrease;
    
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    
    private CEnchantment cachedEnchantment = null;
    
    public static void invalidateCachedEnchants() {
        for (CEnchantments value : values()) {
            value.cachedEnchantment = null;
        }
    }
    
    /**
     *
     * @param name Name of the enchantment.
     * @param typeName Type of items it goes on.
     */
    private CEnchantments(String name, String typeName) {
        this.name = name;
        this.typeName = typeName;
        this.chance = 0;
        this.chanceIncrease = 0;
        this.hasChanceSystem = false;
    }
    
    /**
     *
     * @param name Name of the enchantment.
     * @param typeName Type of items it goes on.
     * @param chance The chance the enchantment has to active.
     * @param chanceIncrease The amount the chance increases by every level.
     */
    private CEnchantments(String name, String typeName, int chance, int chanceIncrease) {
        this.name = name;
        this.typeName = typeName;
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
        for (CEnchantments ench : values()) {
            if (ench.getName().equalsIgnoreCase(enchant) || ench.getCustomName().equalsIgnoreCase(enchant)) {
                return ench;
            }
        }
        return null;
    }
    
    public static List<CEnchantments> getFromeNames(List<CEnchantment> enchantments) {
        List<CEnchantments> cEnchantments = new ArrayList<>();
        for (CEnchantment cEnchantment : enchantments) {
            CEnchantments enchantment = getFromName(cEnchantment.getName());
            if (enchantment != null) {
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
        if (getEnchantment() == null || getEnchantment().getEnchantmentType() == null) {
            return EnchantmentType.getFromName(typeName);
        } else {
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
        if (cachedEnchantment == null) {
            cachedEnchantment = ce.getEnchantmentFromName(name);
        }
        return cachedEnchantment;
    }
    
    /**
     * Get the level of the enchantment on an item.
     * @param item The item that is being checked.
     * @return The level of the enchantment that is on the item.
     */
    public int getLevel(ItemStack item) {
        return getEnchantment().getLevel(item);
    }
    
    /**
     * Check to see if the enchantment's chance is successful.
     * @return True if the chance was successful and false if not.
     */
    public boolean chanceSuccessful() {
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