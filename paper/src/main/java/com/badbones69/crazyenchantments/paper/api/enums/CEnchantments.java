package com.badbones69.crazyenchantments.paper.api.enums;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public enum CEnchantments {

    //	----------------Boots----------------  \\
    GEARS("Gears", "Boots"),
    WINGS("Wings", "Boots"),
    ADRENALINE("Adrenaline", "Boots", 10, 5),
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
    MANEUVER("Maneuver", "Armor", 10, 5),
    CROUCH("Crouch", "Armor", 10, 5),
    SHOCKWAVE("Shockwave", "Armor", 10, 5),
    SYSTEMREBOOT("SystemReboot", "Armor", 10, 5),
    //	----------------Axes----------------  \\
    REKT("Rekt", "Axe", 5, 1),
    DIZZY("Dizzy", "Axe", 10, 5),
    CURSED("Cursed", "Axe", 10, 5),
    FEEDME("FeedMe", "Axe", 10, 5),
    BERSERK("Berserk", "Axe", 10, 1),
    BLESSED("Blessed", "Axe", 10, 5),
    DECAPITATION("Decapitation", "Axe", 10, 10),
    BATTLECRY("BattleCry", "Axe", 10, 5),
    DEMONFORGED("DemonForged", "Axe", 10, 5),
    //	----------------PickAxes----------------  \\
    VEINMINER("VeinMiner", "Pickaxe"),
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

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final @NotNull Starter starter = this.plugin.getStarter();

    private final @NotNull CrazyManager crazyManager = this.starter.getCrazyManager();

    private final @NotNull Methods methods = this.starter.getMethods();

    private final String name;
    private final String typeName;
    private final boolean hasChanceSystem;
    private final int chance;
    private final int chanceIncrease;

    private CEnchantment cachedEnchantment = null;

    /**
     * @param name     Name of the enchantment.
     * @param typeName Type of items it goes on.
     */
    CEnchantments(String name, String typeName) {
        this.name = name;
        this.typeName = typeName;
        this.chance = 0;
        this.chanceIncrease = 0;
        this.hasChanceSystem = false;
    }

    /**
     * @param name           Name of the enchantment.
     * @param typeName       Type of items it goes on.
     * @param chance         The chance the enchantment has to active.
     * @param chanceIncrease The amount the chance increases by every level.
     */
    CEnchantments(String name, String typeName, int chance, int chanceIncrease) {
        this.name = name;
        this.typeName = typeName;
        this.chance = chance;
        this.chanceIncrease = chanceIncrease;
        this.hasChanceSystem = true;
    }

    public static void invalidateCachedEnchants() {
        for (CEnchantments value : values()) {
            value.cachedEnchantment = null;
        }
    }

    /**
     * @return The name of the enchantment.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return The custom name in the Enchantment.yml.
     */
    public String getCustomName() {
        return getEnchantment().getCustomName();
    }

    /**
     * Get the chance the enchantment will activate.
     * Not all enchantments have a chance to activate.
     *
     * @return The chance of the enchantment activating.
     */
    public int getChance() {
        return this.chance;
    }

    /**
     * Get the amount the enchantment chance increases by every level.
     * Not all enchantments have a chance to activate.
     *
     * @return The amount the chance increases by every level.
     */
    public int getChanceIncrease() {
        return this.chanceIncrease;
    }

    /**
     * @return The description of the enchantment in the Enchantments.yml.
     */
    public List<String> getDescription() {
        return getEnchantment().getInfoDescription();
    }

    /**
     * @return The type the enchantment is.
     */
    public EnchantmentType getType() {
        if (getEnchantment() == null || getEnchantment().getEnchantmentType() == null) {
            return this.methods.getFromName(this.typeName);
        } else {
            return getEnchantment().getEnchantmentType();
        }
    }

    /**
     * @return True if the enchantment is enabled and false if not.
     */
    public boolean isActivated() {
        return getEnchantment() != null && getEnchantment().isActivated();
    }

    /**
     * Get the enchantment that this is tied to.
     *
     * @return The enchantment this is tied to.
     */
    public CEnchantment getEnchantment() {
        if (this.cachedEnchantment == null)
            this.cachedEnchantment = this.crazyManager.getEnchantmentFromName(this.name);

        return this.cachedEnchantment;
    }

    /**
     * Check to see if the enchantment's chance is successful.
     *
     * @return True if the chance was successful and false if not.
     */
    public boolean chanceSuccessful(int level) {
        return this.chanceSuccessful(level, 1.0);
    }

    /**
     * Check to see if the enchantment's chance is successful.
     *
     * @return True if the chance was successful and false if not.
     */
    public boolean chanceSuccessful(int level, double multiplier) {
        return this.crazyManager.getEnchantmentFromName(this.name).chanceSuccessful(level, multiplier);
    }

    /**
     * Check if the CEnchantments uses a chance system.
     */
    public boolean hasChanceSystem() {
        return this.hasChanceSystem;
    }
}