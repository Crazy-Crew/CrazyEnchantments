package com.badbones69.crazyenchantments.platform;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class TinkerConfig implements SettingsHolder {

    public static final Property<String> currency = newProperty("root.currency", "XP_TOTAL");

    public static final Property<String> bottle_item = newProperty("root.bottle.item", "experience_bottle");

    public static final Property<String> bottle_name = newProperty("root.bottle.name", "<green>Recycled XP");

    public static final Property<List<String>> bottle_lore = newListProperty("root.bottle.lore", List.of(
            "<bold><green>Recycled</bold> <gold>%total% XP",
            "<bold><gray>(<gold>!<gray>)</bold> <gray>Throw to get XP."
    ));

    public static final Property<String> menu_name = newProperty("menu.name", "<bold><gray>The <dark_red>Crazy <red>Tinkerer</bold>");

    public static final Property<String> menu_trade_button = newProperty("menu.trade-button.name", "<yellow>Click to accept the trade");

    public static final Property<List<String>> menu_trade_lore = newListProperty("menu.trade-button.lore", List.of(
            ""
    ));

    @Comment({
            "Do not remove anything from this list.",
            "",
            "<Base amount of Xp(Lvl/Total)/Money>, <Amount per level>."
    })
    public static final Property<List<String>> enchantments = newListProperty("tinker.vanilla-enchantments", List.of(
            "protection: 24,1",
            "fire_protection: 20,1",
            "feather_falling: 20,1",
            "blast_protection: 20,1",
            "projectile_protection: 10,1",
            "respiration: 10,1",
            "aqua_affinity: 15,1",
            "sharpness: 35,1",
            "smite: 15,1",
            "bane_of_arthropods: 30,1",
            "looting: 25,1",
            "sweeping: 20,1",
            "efficiency: 10,1",
            "unbreaking: 10,1",
            "fortune: 20,1",
            "power: 25,1",
            "punch: 15,1",
            "flame: 15,1",
            "infinity: 25,1",
            "luck_of_the_sea: 25,1",
            "mending: 25,1",
            "silk_touch: 35,1",
            "luck: 15,1",
            "lure: 15,1",
            "loyalty: 15,1",
            "impaling: 15,1",
            "riptide: 15,1",
            "channeling: 15,1",
            "multishot: 15,1",
            "quick_charge: 15,1",
            "piercing: 15,1",
            "vanishing_curse: 15,1",
            "soul_speed: 15,1",
            "swift_sneak: 15,1"
    ));

    @Comment({
            "Do not remove anything from this list.",
            "",
            "<Base amount of Xp(Lvl/Total)/Money>, <Amount per level>."
    })
    public static final Property<List<String>> crazyEnchantments = newListProperty("tinker.crazy-enchantments", List.of(
            "Glowing: Items;20,1|Book;5,1",
            "BurnShield: Items;20,1|Book;5,1",
            "Piercing: Items;20,1|Book;5,1",
            "OverLoad: Items;20,1|Book;5,1",
            "SelfDestruct: Items;20,1|Book;5,1",
            "Springs: Items;20,1|Book;5,1",
            "Gears: Items;20,1|Book;5,1",
            "Hulk: Items;20,1|Book;5,1",
            "Mermaid: Items;20,1|Book;5,1",
            "Ninja: Items;20,1|Book;5,1",
            "Vampire: Items;20,1|Book;5,1",
            "LifeSteal: Items;20,1|Book;5,1",
            "Doctor: Items;20,1|Book;5,1",
            "Boom: Items;20,1|Book;5,1",
            "Venom: Items;20,1|Book;5,1",
            "DoubleDamage: Items;20,1|Book;5,1",
            "SlowMo: Items;20,1|Book;5,1",
            "Blindness: Items;20,1|Book;5,1",
            "Viper: Items;20,1|Book;5,1",
            "AntiGravity: Items;20,1|Book;5,1",
            "FastTurn: Items;20,1|Book;5,1",
            "LightWeight: Items;20,1|Book;5,1",
            "Blessed: Items;20,1|Book;5,1",
            "FeedMe: Items;20,1|Book;5,1",
            "Dizzy: Items;20,1|Book;5,1",
            "Berserk: Items;20,1|Book;5,1",
            "Cursed: Items;20,1|Book;5,1",
            "Rekt: Items;20,1|Book;5,1",
            "Enlightened: Items;20,1|Book;5,1",
            "Freeze: Items;20,1|Book;5,1",
            "Fortify: Items;20,1|Book;5,1",
            "Molten: Items;20,1|Book;5,1",
            "PainGiver: Items;20,1|Book;5,1",
            "Savior: Items;20,1|Book;5,1",
            "Nursery: Items;20,1|Book;5,1",
            "AutoSmelt: Items;20,1|Book;5,1",
            "Experience: Items;20,1|Book;5,1",
            "Telepathy: Items;20,1|Book;5,1",
            "Haste: Items;20,1|Book;5,1",
            "Oxygenate: Items;20,1|Book;5,1",
            "IceFreeze: Items;20,1|Book;5,1",
            "Lightning: Items;20,1|Book;5,1",
            "decapitation: Items;20,1|Book;5,1",
            "Confusion: Items;20,1|Book;5,1",
            "Disarmer: Items;20,1|Book;5,1",
            "Execute: Items;20,1|Book;5,1",
            "Headless: Items;20,1|Book;5,1",
            "Inquisitive: Items;20,1|Book;5,1",
            "Insomnia: Items;20,1|Book;5,1",
            "Nutrition: Items;20,1|Book;5,1",
            "Obliterate: Items;20,1|Book;5,1",
            "Paralyze: Items;20,1|Book;5,1",
            "SkillSwipe: Items;20,1|Book;5,1",
            "Snare: Items;20,1|Book;5,1",
            "Trap: Items;20,1|Book;5,1",
            "Wither: Items;20,1|Book;5,1",
            "Rage: Items;20,1|Book;5,1",
            "Valor: Items;20,1|Book;5,1",
            "SmokeBomb: Items;20,1|Book;5,1",
            "Drunk: Items;20,1|Book;5,1",
            "Voodoo: Items;20,1|Book;5,1",
            "MultiArrow: Items;20,1|Book;5,1",
            "Recover: Items;20,1|Book;5,1",
            "Cactus: Items;20,1|Book;5,1",
            "Rocket: Items;20,1|Book;5,1",
            "Adrenaline: Items;20,1|Book;5,1",
            "Angel: Items;20,1|Book;5,1",
            "HellForged: Items;20,1|Book;5,1",
            "Commander: Items;20,1|Book;5,1",
            "StormCaller: Items;20,1|Book;5,1",
            "Leadership: Items;20,1|Book;5,1",
            "Implants: Items;20,1|Book;5,1",
            "Wings: Items;20,1|Book;5,1",
            "VeinMiner: Items;20,1|Book;5,1",
            "Blast: Items;20,1|Book;5,1",
            "Furnace: Items;20,1|Book;5,1",
            "Blizzard: Items;20,1|Book;5,1",
            "AcidRain: Items;20,1|Book;5,1",
            "SandStorm: Items;20,1|Book;5,1",
            "Radiant: Items;20,1|Book;5,1",
            "Intimidate: Items;20,1|Book;5,1",
            "Pull: Items;20,1|Book;5,1",
            "Tamer: Items;20,1|Book;5,1",
            "Guards: Items;20,1|Book;5,1",
            "Necromancer: Items;20,1|Book;5,1",
            "BeeKeeper: Items;20,1|Book;5,1",
            "Maneuver: Items;20,1|Book;5,1",
            "Crouch: Items;20,1|Book;5,1",
            "Shockwave: Items;20,1|Book;5,1",
            "SystemReboot: Items;20,1|Book;5,1",
            "Sticky-Shot: Items;20,1|Book;5,1",
            "Disorder: Items;20,1|Book;5,1",
            "Charge: Items;20,1|Book;5,1",
            "Revenge: Items;20,1|Book;5,1",
            "BattleCry: Items;20,1|Book;5,1",
            "DemonForged: Items;20,1|Book;5,1",
            "Famished: Items;20,1|Book;5,1",
            "GreenThumb: Items;20,1|Book;5,1",
            "Harvester: Items;20,1|Book;5,1",
            "Tiller: Items;20,1|Book;5,1",
            "Planter: Items;20,1|Book;5,1"
    ));
}