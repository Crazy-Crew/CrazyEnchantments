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
    GEARS("Gears", Constants.BOOTS),
    WINGS("Wings", Constants.BOOTS),
    ROCKET("Rocket", Constants.BOOTS, 15, 5),
    SPRINGS("Springs", Constants.BOOTS),
    ANTIGRAVITY("AntiGravity", Constants.BOOTS),
    //	----------------Bows----------------  \\
    BOOM("Boom", Constants.BOW, 20, 10),
    PULL("Pull", Constants.BOW, 25, 10),
    VENOM("Venom", Constants.BOW, 10, 5),
    DOCTOR("Doctor", Constants.BOW),
    PIERCING("Piercing", Constants.BOW, 5, 5),
    ICEFREEZE("IceFreeze", Constants.BOW, 25, 10),
    LIGHTNING("Lightning", Constants.BOW, 25, 10),
    MULTIARROW("MultiArrow", Constants.BOW, 25, 10),
    STICKY_SHOT("Sticky-Shot", Constants.BOW, 10, 10),
    SNIPER("Sniper", Constants.BOW, 25, 5),
    //	----------------Helmets----------------  \\
    GLOWING("Glowing", Constants.HELMET),
    MERMAID("Mermaid", Constants.HELMET),
    IMPLANTS("Implants", Constants.HELMET, 5, 5),
    COMMANDER("Commander", Constants.HELMET),
    //	----------------Swords----------------  \\
    TRAP("Trap", Constants.SWORD, 10, 5),
    RAGE("Rage", Constants.SWORD),
    VIPER("Viper", Constants.SWORD, 10, 5),
    SNARE("Snare", Constants.SWORD, 10, 3),
    SLOWMO("SlowMo", Constants.SWORD, 5, 5),
    WITHER("Wither", Constants.SWORD, 10, 3),
    VAMPIRE("Vampire", Constants.SWORD, 5, 5),
    EXECUTE("Execute", Constants.SWORD),
    FASTTURN("FastTurn", Constants.SWORD, 5, 5),
    DISARMER("Disarmer", Constants.SWORD, 5, 1),
    HEADLESS("Headless", Constants.SWORD, 10, 10),
    PARALYZE("Paralyze", Constants.SWORD, 15, 5),
    BLINDNESS("Blindness", Constants.SWORD, 5, 1),
    LIFESTEAL("LifeSteal", Constants.SWORD, 15, 5),
    CONFUSION("Confusion", Constants.SWORD, 15, 5),
    NUTRITION("Nutrition", Constants.SWORD, 15, 5),
    SKILLSWIPE("SkillSwipe", Constants.SWORD, 5, 5),
    OBLITERATE("Obliterate", Constants.SWORD, 10, 5),
    INQUISITIVE("Inquisitive", Constants.SWORD, 50, 25),
    LIGHTWEIGHT("LightWeight", Constants.SWORD, 15, 5),
    DOUBLEDAMAGE("DoubleDamage", Constants.SWORD, 5, 1),
    DISORDER("Disorder", Constants.SWORD, 1, 0),
    CHARGE("Charge", Constants.SWORD),
    REVENGE("Revenge", Constants.SWORD),
    FAMISHED("Famished", Constants.SWORD, 10, 5),
    //	----------------Armor----------------  \\
    HULK("Hulk", Constants.ARMOR),
    VALOR("Valor", Constants.ARMOR),
    DRUNK("Drunk", Constants.ARMOR),
    NINJA("Ninja", Constants.ARMOR),
    ANGEL("Angel", Constants.ARMOR),
    TAMER("Tamer", Constants.ARMOR),
    GUARDS("Guards", Constants.ARMOR),
    VOODOO("Voodoo", Constants.ARMOR, 15, 5),
    MOLTEN("Molten", Constants.ARMOR, 10, 1),
    SAVIOR("Savior", Constants.ARMOR, 15, 5),
    CACTUS("Cactus", Constants.ARMOR, 25, 25),
    FREEZE("Freeze", Constants.ARMOR, 10, 5),
    RECOVER("Recover", Constants.ARMOR),
    NURSERY("Nursery", Constants.ARMOR, 5, 5),
    RADIANT("Radiant", Constants.ARMOR),
    FORTIFY("Fortify", Constants.ARMOR, 10, 5),
    OVERLOAD("OverLoad", Constants.ARMOR),
    BLIZZARD("Blizzard", Constants.ARMOR),
    INSOMNIA("Insomnia", Constants.ARMOR, 10, 5),
    ACIDRAIN("AcidRain", Constants.ARMOR, 5, 5),
    SANDSTORM("SandStorm", Constants.ARMOR, 5, 5),
    SMOKEBOMB("SmokeBomb", Constants.ARMOR, 5, 5),
    PAINGIVER("PainGiver", Constants.ARMOR, 10, 5),
    INTIMIDATE("Intimidate", Constants.ARMOR),
    BURNSHIELD("BurnShield", Constants.ARMOR),
    LEADERSHIP("Leadership", Constants.ARMOR, 10, 5),
    INFESTATION("Infestation", Constants.ARMOR),
    NECROMANCER("Necromancer", Constants.ARMOR),
    STORMCALLER("StormCaller", Constants.ARMOR, 10, 5),
    ENLIGHTENED("Enlightened", Constants.ARMOR, 10, 5),
    SELFDESTRUCT("SelfDestruct", Constants.ARMOR),
    CYBORG("Cyborg", Constants.ARMOR),
    //	----------------Axes----------------  \\
    REKT("Rekt", Constants.AXE, 5, 1),
    DIZZY("Dizzy", Constants.AXE, 10, 5),
    CURSED("Cursed", Constants.AXE, 10, 5),
    FEEDME("FeedMe", Constants.AXE, 10, 5),
    BERSERK("Berserk", Constants.AXE, 10, 1),
    BLESSED("Blessed", Constants.AXE, 10, 5),
    DECAPITATION("Decapitation", Constants.AXE, 10, 10),
    BATTLECRY("BattleCry", Constants.AXE, 10, 5),
    //	----------------PickAxes----------------  \\
    BLAST("Blast", Constants.PICKAXE),
    AUTOSMELT("AutoSmelt", Constants.PICKAXE, 25, 25),
    EXPERIENCE("Experience", Constants.PICKAXE, 25, 25),
    FURNACE("Furnace", Constants.PICKAXE),
    //	----------------Tools----------------  \\
    HASTE("Haste", Constants.TOOL),
    TELEPATHY("Telepathy", Constants.TOOL),
    OXYGENATE("Oxygenate", Constants.TOOL),
    //	----------------Hoes----------------  \\
    GREENTHUMB("GreenThumb", Constants.HOE, 10, 10),
    HARVESTER("Harvester", Constants.HOE),
    TILLER("Tiller", Constants.HOE),
    PLANTER("Planter", Constants.HOE),
    //	----------------All----------------  \\
    HELLFORGED("HellForged", Constants.DAMAGEABLE, 5, 5);
    
    private String name;
    private EnchantmentType type;
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
            return type;
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
        return getEnchantment().getPower(item);
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
    
    private static class Constants {
        
        public static final EnchantmentType BOOTS = EnchantmentType.getFromName("boots");
        public static final EnchantmentType BOW = EnchantmentType.getFromName("bow");
        public static final EnchantmentType HELMET = EnchantmentType.getFromName("helmet");
        public static final EnchantmentType ARMOR = EnchantmentType.getFromName("armor");
        public static final EnchantmentType SWORD = EnchantmentType.getFromName("sword");
        public static final EnchantmentType AXE = EnchantmentType.getFromName("axe");
        public static final EnchantmentType PICKAXE = EnchantmentType.getFromName("pickaxe");
        public static final EnchantmentType TOOL = EnchantmentType.getFromName("tool");
        public static final EnchantmentType HOE = EnchantmentType.getFromName("hoe");
        public static final EnchantmentType DAMAGEABLE = EnchantmentType.getFromName("damaged-items");
        
    }
    
}