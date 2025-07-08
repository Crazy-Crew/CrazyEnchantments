package com.badbones69.crazyenchantments.paper.api;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.badbones69.crazyenchantments.paper.api.enums.ShopOption;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.Enchant;
import com.badbones69.crazyenchantments.paper.api.managers.AllyManager;
import com.badbones69.crazyenchantments.paper.api.managers.ArmorEnchantmentManager;
import com.badbones69.crazyenchantments.paper.api.managers.BowEnchantmentManager;
import com.badbones69.crazyenchantments.paper.api.managers.ShopManager;
import com.badbones69.crazyenchantments.paper.api.managers.WingsManager;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEOption;
import com.badbones69.crazyenchantments.paper.api.objects.CEPlayer;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GkitCoolDown;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.objects.items.ScramblerData;
import com.badbones69.crazyenchantments.paper.api.objects.items.ScrollData;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.api.utils.NumberUtils;
import com.badbones69.crazyenchantments.paper.api.utils.WingsUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.paper.support.CropManager;
import com.badbones69.crazyenchantments.paper.support.interfaces.CropManagerVersion;
import com.ryderbelserion.crazyenchantments.enums.FileKeys;
import com.ryderbelserion.crazyenchantments.objects.ConfigOptions;
import com.ryderbelserion.crazyenchantments.utils.ConfigUtils;
import com.ryderbelserion.fusion.core.files.types.YamlCustomFile;
import com.ryderbelserion.fusion.paper.api.enums.Scheduler;
import com.ryderbelserion.fusion.paper.api.scheduler.FoliaScheduler;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CrazyManager {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final ConfigOptions options = this.plugin.getOptions();

    private final ComponentLogger logger = this.plugin.getComponentLogger();
    
    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final Methods methods = this.starter.getMethods();

    // Settings.
    @NotNull
    private final ProtectionCrystalSettings protectionCrystalSettings = this.starter.getProtectionCrystalSettings();
    @NotNull
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    private CropManagerVersion cropManagerVersion;

    @NotNull
    private final AllyManager allyManager = this.starter.getAllyManager();

    // Wings.
    @NotNull
    private final WingsManager wingsManager = this.starter.getWingsManager();

    @NotNull
    private final ShopManager shopManager = this.starter.getShopManager();
    
    @NotNull
    private final BowEnchantmentManager bowEnchantmentManager = this.starter.getBowEnchantmentManager();
    
    @NotNull
    private final ArmorEnchantmentManager armorEnchantmentManager = this.starter.getArmorEnchantmentManager();

    // Arrays.
    private final List<GKitz> gkitz = new ArrayList<>();
    private final List<CEPlayer> players = new ArrayList<>();
    private final List<Material> blockList = new ArrayList<>();
    private final Map<Material, Double> headMap = new HashMap<>();
    private final Map<ShopOption, CEOption> shopOptions = new HashMap<>();

    private int rageMaxLevel;
    private boolean gkitzToggle;
    private boolean useUnsafeEnchantments;
    private boolean breakRageOnDamage;
    private boolean useRageBossBar;

    private double rageIncrement;
    private boolean maxEnchantmentCheck;
    private boolean checkVanillaLimit;

    private boolean dropBlocksBlast;
    private boolean dropBlocksVeinMiner;
    private int defaultLimit;
    private int defaultBaseLimit;
    private boolean useEnchantmentLimiter;
    private boolean useConfigLimits;

    private int CESuccessOverride;
    private int CEFailureOverride;

    private ScramblerData scramblerData;
    private ScrollData scrollData;
    private ItemStack slot_crystal;

    /**
     * Loads everything for the Crazy Enchantments plugin.
     * Do not use unless needed.
     */
    public void load() {
        final FileConfiguration gkit = Files.GKITZ.getFile();
        final FileConfiguration enchants = Files.ENCHANTMENTS.getFile();

        final FileConfiguration blocks = Files.BLOCKLIST.getFile();
        final FileConfiguration heads = Files.HEADMAP.getFile();

        this.blockList.clear();
        this.headMap.clear();
        this.gkitz.clear();
        this.enchantmentBookSettings.getRegisteredEnchantments().clear();
        this.enchantmentBookSettings.getCategories().clear();

        this.starter.getPluginSupport().updateHooks();

        // Check if we should patch player health.
        boolean playerHealthPatch = this.options.isResetPlayersMaxHealth();

        this.plugin.getServer().getOnlinePlayers().forEach(player -> { //todo() this is retarded
            // Load our players.
            loadCEPlayer(player);

            // Check if we need to patch playerHealth.
            Attribute genericAttribute = Attribute.MAX_HEALTH;

            double baseValue = player.getAttribute(genericAttribute).getBaseValue(); //todo() deprecated

            if (playerHealthPatch) player.getAttribute(genericAttribute).setBaseValue(baseValue);

            new FoliaScheduler(this.plugin, Scheduler.global_scheduler, TimeUnit.MINUTES) {
                @Override
                public void run() {
                    getCEPlayers().forEach(player -> backupCEPlayer(player.getPlayer()));
                }
            }.runAtFixedRate(5, 5);
        });

        // Invalidate cached enchants.
        CEnchantments.invalidateCachedEnchants();

        // Loop through block list.
        blocks.getStringList("Block-List").forEach(id -> { //todo() switch to json value
            try {
                this.blockList.add(new ItemBuilder().setMaterial(id).getMaterial());
            } catch (Exception ignored) {}
        });

        ConfigurationSection headSec = heads.getConfigurationSection("HeadOdds");

        if (headSec != null) {
            headSec.getKeys(false).forEach(id -> { //todo() switch to json file
                try {
                    Material mat = new ItemBuilder().setMaterial(id).getMaterial();

                    this.headMap.put(mat, headSec.getDouble(id));
                } catch (Exception ignored) {}
            });
        }

        Scrolls.getWhiteScrollProtectionName();

        final YamlCustomFile customFile = FileKeys.config.getCustomFile();
        final CommentedConfigurationNode config = customFile.getConfiguration();

        this.enchantmentBookSettings.setEnchantmentBook(new ItemBuilder().setMaterial(config.node("Settings", "Enchantment-Book-Item").getString("BOOK")));
        this.useUnsafeEnchantments = this.options.isUseUnsafeEnchantments();
        this.maxEnchantmentCheck = config.node("Settings", "MaxAmountOfEnchantmentsToggle").getBoolean(true);
        this.useConfigLimits = config.node("Settings", "EnchantmentOptions", "Limit", "Check-Perms").getBoolean(false);
        this.defaultLimit = config.node("Settings", "EnchantmentOptions", "Limit", "Default-Limit").getInt(0);
        this.defaultBaseLimit = config.node("Settings", "EnchantmentOptions", "Limit", "Default-Base-Limit").getInt(0);
        this.useEnchantmentLimiter = config.node("Settings", "EnchantmentOptions", "Limit", "Enable-SlotCrystal").getBoolean(true);
        this.checkVanillaLimit = config.node("Settings", "EnchantmentOptions", "IncludeVanillaEnchantments").getBoolean(false);
        this.gkitzToggle = config.node("Settings", "GKitz", "Enabled").getBoolean(true);
        this.rageMaxLevel = config.node("Settings", "EnchantmentOptions", "MaxRageLevel").getInt(4);
        this.breakRageOnDamage = config.node("Settings", "EnchantmentOptions", "Break-Rage-On-Damage").getBoolean(true);
        this.useRageBossBar = config.node("Settings", "EnchantmentOptions", "Rage-Boss-Bar").getBoolean(false);
        this.rageIncrement = config.node("Settings", "EnchantmentOptions", "Rage-Increase").getDouble(0.1);
        setDropBlocksBlast(config.node("Settings", "EnchantmentOptions", "Drop-Blocks-For-Blast").getBoolean(true));
        setDropBlocksVeinMiner(config.node("Settings", "EnchantmentOptions", "Drop-Blocks-For-VeinMiner").getBoolean(true));

        this.CEFailureOverride = config.node("Settings", "CEFailureOverride").getInt(-1);
        this.CESuccessOverride = config.node("Settings", "CESuccessOverride").getInt(-1);

        this.enchantmentBookSettings.populateMaps();

        for (final CEnchantments cEnchantment : CEnchantments.values()) {
            final String name = cEnchantment.getName();
            final String path = "Enchantments." + name;

            if (enchants.contains(path)) { // To make sure the enchantment isn't broken.
                CEnchantment enchantment = new CEnchantment(name).setCustomName(enchants.getString(path + ".Name", ""))
                .setActivated(enchants.getBoolean(path + ".Enabled", false))
                .setMaxLevel(enchants.getInt(path + ".MaxPower", 1))
                .setEnchantmentType(cEnchantment.getType())
                .setInfoName(enchants.getString(path + ".Info.Name", ""))
                .setInfoDescription(enchants.getStringList(path + ".Info.Description"))
                .setCategories(enchants.getStringList(path + ".Categories"))
                .setChance(cEnchantment.getChance())
                .setChanceIncrease(cEnchantment.getChanceIncrease())
                .setSound(enchants.getString(path + ".Sound", ""))
                .setConflicts(enchants.getStringList(path + ".Conflicts"));

                if (enchants.contains(path + ".Enchantment-Type")) enchantment.setEnchantmentType(this.methods.getFromName(enchants.getString(path + ".Enchantment-Type")));

                if (cEnchantment.hasChanceSystem()) {
                    if (enchants.contains(path + ".Chance-System.Base")) {
                        enchantment.setChance(enchants.getInt(path + ".Chance-System.Base"));
                    } else {
                        enchantment.setChance(cEnchantment.getChance());
                    }

                    if (enchants.contains(path + ".Chance-System.Increase")) {
                        enchantment.setChanceIncrease(enchants.getInt(path + ".Chance-System.Increase"));
                    } else {
                        enchantment.setChanceIncrease(cEnchantment.getChanceIncrease());
                    }
                }

                enchantment.registerEnchantment();
            }
        }

        if (this.gkitzToggle) {
            final ConfigurationSection section = gkit.getConfigurationSection("GKitz");

            if (section == null) {
                this.logger.warn("Could not find the configuration section f or Gkitz in the Gkitz.yml file.");

                return;
            }

            for (final String kit : section.getKeys(false)) {
                final String path = "GKitz." + kit + ".";

                final int slot = gkit.getInt(path + "Display.Slot", -1);

                if (slot == -1) {
                    this.logger.warn("The slot option for {} is either not present, or set to -1", kit);

                    continue;
                }

                final String time = gkit.getString(path + "Cooldown", "");

                final boolean autoEquip = gkit.getBoolean(path + "Auto-Equip", false);

                ItemStack displayItem = new ItemBuilder().setMaterial(gkit.getString(path + "Display.Item", ColorUtils.getRandomPaneColor().getName()))
                  .setName(gkit.getString(path + "Display.Name", "Error getting name."))
                  .setLore(gkit.getStringList(path + "Display.Lore"))
                  .setGlow(gkit.getBoolean(path + "Display.Glowing", false))
                  .addKey(DataKeys.gkit_type.getNamespacedKey(), kit).build();

                List<String> commands = gkit.getStringList(path + "Commands");

                List<String> itemStrings = gkit.getStringList(path + "Items");

                List<ItemStack> previewItems = getInfoGKit(itemStrings);

                previewItems.addAll(getInfoGKit(gkit.getStringList(path + "Fake-Items")));

                this.gkitz.add(new GKitz(kit, slot, time, displayItem, previewItems, commands, itemStrings, autoEquip));
            }
        }

        // Load all scroll types.
        Scrolls.loadScrolls(config);
        // Load all dust types.
        Dust.loadDust(config);

        // Loads the protection crystals.
        this.protectionCrystalSettings.loadProtectionCrystal(customFile);
        // Loads the scrambler.
        this.scramblerData = new ScramblerData();
        this.scramblerData.loadScrambler(config);

        // Loads Slot Crystal.
        this.slot_crystal = new ItemBuilder().setMaterial(config.node("Settings", "Slot_Crystal", "Item").getString("RED_WOOL"))
                .setName(config.node("Settings", "Slot_Crystal", "Name").getString("&5&lSlot &b&lCrystal"))
                .setLore(ConfigUtils.getStringList(config, List.of(
                        "&7A rare crystal that is said to",
                        "&7increase the amount of enchants",
                        "&7that can be added onto an item.",
                        "",
                        "&7&l(&6&l!&7&l) &7Drag and drop on an item."
                ), "Settings", "Slot_Crystal", "Lore"))
                .setGlow(config.node("Settings", "Slot_Crystal", "Glowing").getBoolean(false))
                .addKey(DataKeys.slot_crystal.getNamespacedKey(), "")
                .build();

        // Loads the Scroll Control settings.
        this.scrollData = new ScrollData();
        this.scrollData.loadScrollControl(config);

        this.cropManagerVersion = new CropManager();

        loadShopOptions(config);

        // Loads the shop manager.
        this.shopManager.load(config);

        // Loads the settings for wings enchantment.
        this.wingsManager.load(config);

        // Loads the settings for the bow enchantments.
        this.bowEnchantmentManager.load();

        // Loads the settings for the armor enchantments.
        this.armorEnchantmentManager.load();

        // Loads the settings for the ally enchantments.
        this.allyManager.load(config);

        // Starts the wings task.
        WingsUtils.startWings();
    }

    public void loadShopOptions(final CommentedConfigurationNode config) {
        this.shopOptions.clear();

        for (final ShopOption option : ShopOption.values()) {
            CommentedConfigurationNode itemNode = config.node("Settings", option.getPath());

            if (option == ShopOption.SUCCESS_DUST || option == ShopOption.DESTROY_DUST) {
                itemNode = config.node("Settings", "Dust", option.getPath());
            }

            final CommentedConfigurationNode costNode = config.node("Settings", "Costs", option.getPath());

            addShopOption(option, itemNode, costNode, option.getNamePath(), option.getLorePath());
        }
    }

    public void addShopOption(final ShopOption shopOption, final CommentedConfigurationNode itemNode, final CommentedConfigurationNode costNode, final String namePath, final String lorePath) {
        try {
            final CEOption option = new CEOption(
                    new ItemBuilder().setMaterial(itemNode.node("Item").getString("CHEST")).setName(itemNode.node(namePath).getString(shopOption.getDefaultName()))
                            .setLore(ConfigUtils.getStringList(itemNode, shopOption.getDefaultLore(), lorePath))
                            .setPlayerName(itemNode.node("Player").getString(""))
                            .setGlow(itemNode.node("Glowing").getBoolean(false)),
                    itemNode.node("Slot").getInt(1)-1,
                    itemNode.node("InGUI").getBoolean(true),
                    costNode.node("Cost").getInt(100),
                    Currency.getCurrency(costNode.node("Currency").getString("Vault"))
            );

            this.shopOptions.put(shopOption, option);
        } catch (final Exception exception) {
            this.logger.error("The option {} has failed to load.", shopOption.getPath(), exception);
        }
    }

    public final Map<ShopOption, CEOption> getShopOptions() {
        return Collections.unmodifiableMap(this.shopOptions);
    }

    /**
     * Only needs used when the player joins the server.
     * This plugin does it automatically, so there is no need to use it unless you have to.
     * @param player The player you wish to load.
     */
    public void loadCEPlayer(final Player player) {
        final FileConfiguration data = Files.DATA.getFile();
        final String uuid = player.getUniqueId().toString();

        final List<GkitCoolDown> gkitCoolDowns = new ArrayList<>();

        for (final GKitz kit : getGKitz()) {
            if (data.contains("Players." + uuid + ".GKitz." + kit.getName())) {
                final Calendar coolDown = Calendar.getInstance();

                coolDown.setTimeInMillis(data.getLong("Players." + uuid + ".GKitz." + kit.getName()));

                gkitCoolDowns.add(new GkitCoolDown(kit, coolDown));
            }
        }

        addCEPlayer(new CEPlayer(player, gkitCoolDowns));
    }

    /**
     * Only needs used when the player leaves the server.
     * This plugin removes the player automatically, so don't use this method unless needed for some reason.
     * @param player Player you wish to remove.
     */
    public void unloadCEPlayer(final Player player) {
        final FileConfiguration data = Files.DATA.getFile();
        final String uuid = player.getUniqueId().toString();
        final CEPlayer cePlayer = getCEPlayer(player);

        if (cePlayer != null) {
            for (final GkitCoolDown gkitCooldown : cePlayer.getCoolDowns()) {
                data.set("Players." + uuid + ".GKitz." + gkitCooldown.getGKitz().getName(), gkitCooldown.getCoolDown().getTimeInMillis());
            }

            Files.DATA.saveFile();
        }

        removeCEPlayer(cePlayer);
    }

    /**
     * This backup all the players data stored by this plugin.
     * @param player The player you wish to back up.
     */
    public void backupCEPlayer(final Player player) {
        backupCEPlayer(getCEPlayer(player));
    }

    /**
     * This backup all the players data stored by this plugin.
     * @param cePlayer The player you wish to back up.
     */
    private void backupCEPlayer(final CEPlayer cePlayer) {
        final FileConfiguration data = Files.DATA.getFile();
        final String uuid = cePlayer.getPlayer().getUniqueId().toString();

        for (final GkitCoolDown gkitCooldown : cePlayer.getCoolDowns()) {
            data.set("Players." + uuid + ".GKitz." + gkitCooldown.getGKitz().getName(), gkitCooldown.getCoolDown().getTimeInMillis());
        }

        Files.DATA.saveFile();
    }

    /**
     * @return NMS support class.
     */
    public CropManagerVersion getNMSSupport() {
        return this.cropManagerVersion;
    }

    public boolean checkVanillaLimit() {
        return this.checkVanillaLimit;
    }

    /**
     * Check if the gkitz option is enabled.
     * @return True if it is on and false if it is off.
     */
    public boolean isGkitzEnabled() {
        return this.gkitzToggle;
    }

    /**
     * Get a GKit from its name.
     * @param kitName The kit you wish to get.
     * @return The kit as a GKitz object.
     */
    public GKitz getGKitFromName(final String kitName) {
        for (final GKitz kit : getGKitz()) {
            if (kit.getName().equalsIgnoreCase(kitName)) return kit;
        }

        return null;
    }

    /**
     * Get all loaded gkitz.
     * @return All the loaded gkitz.
     */
    public List<GKitz> getGKitz() {
        return this.gkitz;
    }

    /**
     * This converts a normal Player into a CEPlayer that is loaded.
     * @param player The player you want to get as a CEPlayer.
     * @return The player but as a CEPlayer. Will return null if not found.
     */
    public CEPlayer getCEPlayer(final Player player) {
        for (final CEPlayer cePlayer : getCEPlayers()) {
            if (cePlayer.getPlayer() == player) return cePlayer;
        }

        return null;
    }

    public CEPlayer getCEPlayer(final UUID uuid) {
        for (final CEPlayer cePlayer : getCEPlayers()) {
            if (cePlayer.getPlayer().getUniqueId().equals(uuid)) return cePlayer;
        }

        return null;
    }

    /**
     * This gets all the CEPlayer's that are loaded.
     * @return All CEPlayer's that are loading and in a list.
     */
    public List<CEPlayer> getCEPlayers() {
        return this.players;
    }
    
    public CEBook getRandomEnchantmentBook(final Category category) {
        try {
            final List<CEnchantment> enchantments = category.getEnabledEnchantments();
            final CEnchantment enchantment = enchantments.get(new Random().nextInt(enchantments.size()));

            return new CEBook(enchantment, randomLevel(enchantment, category), 1, category);
        } catch (final Exception exception) {
            this.logger.info("The category {} has no enchantments. Please add enchantments to the category in the Enchantments.yml. If you do not wish to have the category feel free to delete it from the Config.yml.", category.getName(), exception);

            return null;
        }
    }

    /**
     * Get all the current registered enchantments.
     * @return A list of all the registered enchantments in the plugin.
     */
    public List<CEnchantment> getRegisteredEnchantments() {
        return new ArrayList<>(this.enchantmentBookSettings.getRegisteredEnchantments());
    }

    /**
     * Get a CEnchantment enchantment from the name.
     * @param enchantmentString The name of the enchantment.
     * @return The enchantment as a CEnchantment but if not found will be null.
     */
    public CEnchantment getEnchantmentFromName(String enchantmentString) {
        for (final CEnchantment enchantment : this.enchantmentBookSettings.getRegisteredEnchantments()) {
            if (enchantment.getName().equalsIgnoreCase(enchantmentString)) return enchantment;

            enchantmentString = enchantmentString.replaceAll("([&§]?#[0-9a-fA-F]{6}|[&§][1-9a-fA-Fk-or]| |_)", "");

            if (enchantment.getCustomName().replaceAll("([&§]?#[0-9a-fA-F]{6}|[&§][1-9a-fA-Fk-or]| |_)", "").equalsIgnoreCase(enchantmentString)) return enchantment;
        }

        return null;
    }

    /**
     * Register a new enchantment into the plugin.
     * @param enchantment The enchantment you wish to register.
     */
    public void registerEnchantment(final CEnchantment enchantment) {
        this.enchantmentBookSettings.getRegisteredEnchantments().add(enchantment);
    }

    /**
     * Unregister an enchantment that is registered into plugin.
     * @param enchantment The enchantment you wish to unregister.
     */
    public void unregisterEnchantment(final CEnchantment enchantment) {
        this.enchantmentBookSettings.getRegisteredEnchantments().remove(enchantment);
    }

    public void addEnchantment(final ItemStack item, final CEnchantment enchantment, final int level) {
        Map<CEnchantment, Integer> enchantments = new HashMap<>();

        enchantments.put(enchantment, level);

        addEnchantments(item, enchantments);
    }

    /**
     * @param itemStack The meta you want to add the enchantment to.
     * @param enchantments The enchantments to be added.
     */
    public void addEnchantments(final ItemStack itemStack, final Map<CEnchantment, Integer> enchantments) {
        final Map<CEnchantment, Integer> currentEnchantments = this.enchantmentBookSettings.getEnchantments(itemStack);

        this.enchantmentBookSettings.removeEnchantments(itemStack, enchantments.keySet().stream().filter(currentEnchantments::containsKey).toList());

        String data = itemStack.getPersistentDataContainer().get(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING);
        final Enchant enchantData = data != null ? Methods.getGson().fromJson(data, Enchant.class) : new Enchant(new HashMap<>());

        final List<Component> lore = itemStack.lore();

        final List<Component> oldLore = lore != null ? lore : new ArrayList<>();
        List<Component> newLore = new ArrayList<>();

        for (Entry<CEnchantment, Integer> entry : enchantments.entrySet()) {
            CEnchantment enchantment = entry.getKey();
            int level = entry.getValue();

            String loreString = enchantment.getCustomName() + " " + NumberUtils.convertLevelString(level);

            newLore.add(ColorUtils.legacyTranslateColourCodes(loreString));

            for (Entry<CEnchantment, Integer> x : enchantments.entrySet()) {
                enchantData.addEnchantment(x.getKey().getName(), x.getValue());
            }
        }

        newLore.addAll(oldLore);

        itemStack.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(newLore).build());

        itemStack.editPersistentDataContainer(container -> container.set(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING, Methods.getGson().toJson(enchantData)));
    }

    /**
     *
     * @param itemStack The {@link ItemStack} of the item to change.
     * @param amount The amount to change the stored limiter by.
     * @return The altered {@link ItemStack}.
     */
    public ItemStack changeEnchantmentLimiter(@NotNull final ItemStack itemStack, final int amount) {
        final PersistentDataContainerView view = itemStack.getPersistentDataContainer();

        int type = view.getOrDefault(DataKeys.limit_reducer.getNamespacedKey(), PersistentDataType.INTEGER, 0);

        final int newAmount = type += amount; //todo() this needs to be tested.

        itemStack.editPersistentDataContainer(container -> {
            if (newAmount == 0) {
                container.remove(DataKeys.limit_reducer.getNamespacedKey());
            } else {
                container.set(DataKeys.limit_reducer.getNamespacedKey(), PersistentDataType.INTEGER, newAmount);
            }
        });

        return itemStack;
    }

    /**
     *
     * @param item The {@link ItemStack} to check.
     * @return The limit set on the item by slot crystals.
     */
    public int getEnchantmentLimiter(@NotNull final ItemStack item) {
        if (!this.useEnchantmentLimiter) return 0;

        return item.getPersistentDataContainer().getOrDefault(DataKeys.limit_reducer.getNamespacedKey(), PersistentDataType.INTEGER, 0);
    }

    /**
     * Force an update of a players armor potion effects.
     * @param player The player you are updating the effects of.
     */
    public void updatePlayerEffects(@NotNull final Player player) { // TODO Remove this method.
        final Set<CEnchantments> allEnchantPotionEffects = getEnchantmentPotions().keySet();

        for (ItemStack armor : player.getEquipment().getArmorContents()) {
            final Map<CEnchantment, Integer> enchantments = this.enchantmentBookSettings.getEnchantments(armor);

            for (CEnchantments ench : allEnchantPotionEffects) {
                if (!enchantments.containsKey(ench.getEnchantment())) continue;

                final Map<PotionEffectType, Integer> effects = getUpdatedEffects(player, armor, new ItemStack(Material.AIR), ench);

                checkPotions(effects, player);
            }
        }
    }

    public void checkPotions(final Map<PotionEffectType, Integer> effects, final Player player) { //TODO Remove this Method
        for (final Map.Entry<PotionEffectType, Integer> type : effects.entrySet()) {
            final int value = type.getValue();

            PotionEffectType key = type.getKey();

            player.removePotionEffect(key);

            if (value == 0) continue; //TODO check usage with new addition of infinity.

            PotionEffect potionEffect = new PotionEffect(key, PotionEffect.INFINITE_DURATION, value);

            player.addPotionEffect(potionEffect);
        }
    }

    /**
     * @param player The player you are adding it to.
     * @param includedItem Include an item.
     * @param excludedItem Exclude an item.
     * @param enchantment The enchantment you want the max level effects from.
     * @return The list of all the max potion effects based on all the armor on the player.
     */
    public Map<PotionEffectType, Integer> getUpdatedEffects(@NotNull final Player player, ItemStack includedItem, ItemStack excludedItem, final CEnchantments enchantment) { //TODO Remove this method.
        Map<PotionEffectType, Integer> effects = new HashMap<>();

        List<ItemStack> items = new ArrayList<>(Arrays.asList(player.getEquipment().getArmorContents()));

        if (includedItem == null) includedItem = new ItemStack(Material.AIR);

        if (excludedItem == null) excludedItem = new ItemStack(Material.AIR);

        if (excludedItem.isSimilar(includedItem)) excludedItem = new ItemStack(Material.AIR);

        items.add(includedItem);

        Map<CEnchantments, Map<PotionEffectType, Integer>> armorEffects = getEnchantmentPotions();

        for (ItemStack armor : items) {
            if (armor == null || armor.isSimilar(excludedItem)) continue;

            Map<CEnchantment, Integer> ench = this.enchantmentBookSettings.getEnchantments(armor);

            for (Entry<CEnchantments, Map<PotionEffectType, Integer>> enchantments : armorEffects.entrySet()) {
                if (!ench.containsKey(enchantments.getKey().getEnchantment())) continue;

                int level = ench.get(enchantments.getKey().getEnchantment());

                if (!this.useUnsafeEnchantments && level > enchantments.getKey().getEnchantment().getMaxLevel()) level = enchantments.getKey().getEnchantment().getMaxLevel();

                for (PotionEffectType type : enchantments.getValue().keySet()) {
                    if (effects.containsKey(type)) {
                        final int updated = effects.get(type);

                        if (updated < (level + enchantments.getValue().get(type))) effects.put(type, level + enchantments.getValue().get(type));
                    } else {
                        effects.put(type, level + enchantments.getValue().get(type));
                    }
                }
            }
        }

        for (PotionEffectType type : armorEffects.get(enchantment).keySet()) {
            if (!effects.containsKey(type)) effects.put(type, 0); // -1 is now Infinity.
        }

        return effects;
    }

    /**
     *
     * @return All the effects for each enchantment that needs it.
     */
    public Map<CEnchantments, Map<PotionEffectType, Integer>> getEnchantmentPotions() {
        Map<CEnchantments, Map<PotionEffectType, Integer>> enchants = new HashMap<>();

        enchants.put(CEnchantments.GLOWING, new HashMap<>());
        enchants.get(CEnchantments.GLOWING).put(PotionEffectType.NIGHT_VISION, -1);

        enchants.put(CEnchantments.MERMAID, new HashMap<>());
        enchants.get(CEnchantments.MERMAID).put(PotionEffectType.WATER_BREATHING, -1);

        enchants.put(CEnchantments.BURNSHIELD, new HashMap<>());
        enchants.get(CEnchantments.BURNSHIELD).put(PotionEffectType.FIRE_RESISTANCE, -1);

        enchants.put(CEnchantments.DRUNK, new HashMap<>());
        enchants.get(CEnchantments.DRUNK).put(PotionEffectType.STRENGTH, -1);
        enchants.get(CEnchantments.DRUNK).put(PotionEffectType.MINING_FATIGUE, -1);
        enchants.get(CEnchantments.DRUNK).put(PotionEffectType.SLOWNESS, -1);

        enchants.put(CEnchantments.HULK, new HashMap<>());
        enchants.get(CEnchantments.HULK).put(PotionEffectType.STRENGTH, -1);
        enchants.get(CEnchantments.HULK).put(PotionEffectType.RESISTANCE, -1);
        enchants.get(CEnchantments.HULK).put(PotionEffectType.SLOWNESS, -1);

        enchants.put(CEnchantments.VALOR, new HashMap<>());
        enchants.get(CEnchantments.VALOR).put(PotionEffectType.RESISTANCE, -1);

        enchants.put(CEnchantments.OVERLOAD, new HashMap<>());
        enchants.get(CEnchantments.OVERLOAD).put(PotionEffectType.HEALTH_BOOST, -1);

        enchants.put(CEnchantments.NINJA, new HashMap<>());
        enchants.get(CEnchantments.NINJA).put(PotionEffectType.HEALTH_BOOST, -1);
        enchants.get(CEnchantments.NINJA).put(PotionEffectType.SPEED, -1);

        enchants.put(CEnchantments.INSOMNIA, new HashMap<>());
        enchants.get(CEnchantments.INSOMNIA).put(PotionEffectType.NAUSEA, -1);
        enchants.get(CEnchantments.INSOMNIA).put(PotionEffectType.MINING_FATIGUE, -1);
        enchants.get(CEnchantments.INSOMNIA).put(PotionEffectType.SLOWNESS, -1);

        enchants.put(CEnchantments.ANTIGRAVITY, new HashMap<>());
        enchants.get(CEnchantments.ANTIGRAVITY).put(PotionEffectType.JUMP_BOOST, 1);

        enchants.put(CEnchantments.GEARS, new HashMap<>());
        enchants.get(CEnchantments.GEARS).put(PotionEffectType.SPEED, -1);

        enchants.put(CEnchantments.SPRINGS, new HashMap<>());
        enchants.get(CEnchantments.SPRINGS).put(PotionEffectType.JUMP_BOOST, -1);

        enchants.put(CEnchantments.CYBORG, new HashMap<>());
        enchants.get(CEnchantments.CYBORG).put(PotionEffectType.SPEED, -1);
        enchants.get(CEnchantments.CYBORG).put(PotionEffectType.STRENGTH, 0);
        enchants.get(CEnchantments.CYBORG).put(PotionEffectType.JUMP_BOOST, -1);

        return enchants;
    }

    /**
     *
     * @return true if the plugin uses limits listed in config.yml.
     */
    public boolean useConfigLimit() {
        return this.useConfigLimits;
    }

    /**
     * Get a players max amount of enchantments.
     * @param player The player you are checking.
     * @return The max amount of enchantments a player can have on an item.
     */
    public int getPlayerMaxEnchantments(@NotNull final Player player) {
        int limit = this.defaultLimit;

        if (this.useConfigLimits) return limit;

        for (final PermissionAttachmentInfo Permission : player.getEffectivePermissions()) {
            String perm = Permission.getPermission().toLowerCase();

            if (perm.startsWith("crazyenchantments.limit.")) {
                perm = perm.replace("crazyenchantments.limit.", "");

                if (NumberUtils.isInt(perm) && limit < Integer.parseInt(perm)) limit = Integer.parseInt(perm);
            }
        }

        return limit;
    }

    /**
     * Based on config options, returns the base amount of enchants that the player can have on an.
     * @param player The {@link Player} to check.
     * @return The base amount of enchants the player can add to items.
     */
    public int getPlayerBaseEnchantments(@NotNull final Player player) {
        int limit = this.defaultBaseLimit;

        if (this.useConfigLimits) return limit;

        for (final PermissionAttachmentInfo Permission : player.getEffectivePermissions()) {
            String perm = Permission.getPermission().toLowerCase();

            if (perm.startsWith("crazyenchantments.base-limit.")) {
                perm = perm.replace("crazyenchantments.base-limit.", "");

                if (NumberUtils.isInt(perm) && limit < Integer.parseInt(perm)) limit = Integer.parseInt(perm);
            }
        }

        return limit;
    }

    /**
     * Checks if the player can add more enchants to the current item based on set limits.
     * @param player The {@link Player} that has the item.
     * @param item The {@link ItemStack} that they want to add the enchant to.
     * @return True if they are able to add more enchants.
     */
    public boolean canAddEnchantment(@NotNull final Player player, @NotNull final ItemStack item) {
        //todo() update permissions
        if (!this.maxEnchantmentCheck || player.hasPermission("crazyenchantments.bypass.limit")) return true;

        return this.enchantmentBookSettings.getEnchantmentAmount(item, this.checkVanillaLimit) <
                Math.min(getPlayerBaseEnchantments(player) - getEnchantmentLimiter(item), getPlayerMaxEnchantments(player));
    }

    /**
     * Checks if the player can add more enchants to the current item based on set limits without the enchant limiter.
     * @param player The {@link Player} that has the item.
     * @param cEnchantments The amount of crazy enchants on the item.
     * @param vanillaEnchantments The amount of vanilla enchantments on the item.
     * @return True if they are able to add more enchants.
     */
    public boolean canAddEnchantment(@NotNull final Player player, final int cEnchantments, final int vanillaEnchantments) {
        if (!this.maxEnchantmentCheck || player.hasPermission("crazyenchantments.bypass.limit")) return true;

        int enchantAmount = cEnchantments;

        if (this.checkVanillaLimit) enchantAmount += vanillaEnchantments;

        return enchantAmount < getPlayerMaxEnchantments(player);
    }

    public int randomLevel(final CEnchantment enchantment, final Category category) {
        final int enchantmentMax = enchantment.getMaxLevel(); // Max set by the enchantment.
        int randomLevel = 1 + new Random().nextInt(enchantmentMax);

        if (category.useMaxLevel()) {
            if (randomLevel > category.getMaxLevel()) randomLevel = 1 + new Random().nextInt(category.getMaxLevel());

            if (randomLevel < category.getMinLevel()) randomLevel = category.getMinLevel();

            if (randomLevel > enchantmentMax) randomLevel = enchantmentMax;
        }

        return randomLevel;
    }

    /**
     * @return The head multiplier map for decapitation and headless.
     */
    public Map<Material, Double> getDecapitationHeadMap() {
        return this.headMap;
    }

    /**
     * @return The block list for blast.
     */
    public List<Material> getBlastBlockList() {
        return this.blockList;
    }

    /**
     * @return If the blast enchantment drops blocks.
     */
    public boolean isDropBlocksBlast() {
        return this.dropBlocksBlast;
    }

    /**
     * @return If the vein-miner enchantment drops blocks.
     */
    public boolean isDropBlocksVeinMiner() {
        return this.dropBlocksVeinMiner;
    }

    /**
     * @param dropBlocksBlast If the blast enchantment drops blocks.
     */
    public void setDropBlocksBlast(final boolean dropBlocksBlast) {
        this.dropBlocksBlast = dropBlocksBlast;
    }

    /**
     * @param dropBlocksVeinMiner If the vein-miner enchantment drops blocks.
     */
    public void setDropBlocksVeinMiner(final boolean dropBlocksVeinMiner) {
        this.dropBlocksVeinMiner = dropBlocksVeinMiner;
    }

    /**
     * @return The max rage stack level.
     */
    public int getRageMaxLevel() {
        return this.rageMaxLevel;
    }

    /**
     * Check if players lose their current rage stack on damage.
     * @return True if they do and false if not.
     */
    public boolean isBreakRageOnDamageOn() {
        return this.breakRageOnDamage;
    }

    /**
     * @return True if a boss bar will be used to display rage notifications.
     */
    public boolean useRageBossBar() {
        return this.useRageBossBar;
    }

    public double getRageIncrement() {
        return this.rageIncrement;
    }

    private void addCEPlayer(final CEPlayer player) {
        this.players.add(player);
    }

    private void removeCEPlayer(final CEPlayer player) {
        this.players.remove(player);
    }

    private List<ItemStack> getInfoGKit(final List<String> itemStrings) {
        final List<ItemStack> items = new ArrayList<>();

        for (final String itemString : itemStrings) {
            final ItemBuilder itemBuilder = ItemBuilder.convertString(itemString);

            final List<String> customEnchantments = new ArrayList<>();

            final Map<Enchantment, Integer> enchantments = new HashMap<>();

            for (final String option : itemString.split(", ")) {
                try {
                    final Enchantment enchantment = this.methods.getEnchantment(option.split(":")[0]);
                    final CEnchantment cEnchantment = getEnchantmentFromName(option.split(":")[0]);
                    final String level = option.split(":")[1];

                    if (enchantment != null) {
                        if (level.contains("-")) {
                            customEnchantments.add("&7" + option.split(":")[0] + " " + level);
                        } else {
                            enchantments.put(enchantment, Integer.parseInt(level));
                        }
                    } else if (cEnchantment != null) {
                        customEnchantments.add(cEnchantment.getCustomName() + " " + level);
                    }
                } catch (Exception ignore) {}
            }

            itemBuilder.getLore().addAll(0, customEnchantments.stream().map(ColorUtils::legacyTranslateColourCodes).toList());
            itemBuilder.setEnchantments(enchantments);

            items.add(itemBuilder.addKey(DataKeys.random_number.getNamespacedKey(), String.valueOf(methods.getRandomNumber(0, Integer.MAX_VALUE))).build());
            // This is done so items do not stack if there are multiple of the same.
        }

        return items;
    }

    public int pickLevel(final int min, final int max) {
        return min + new Random().nextInt((max + 1) - min);
    }

    /** Gets the success override from the config. Default -1 means no override should be used */
    public int getCESuccessOverride() {
        return this.CESuccessOverride;
    }

    /** Gets the failure override from the config. Default -1 means no override should be used */
    public int getCEFailureOverride() {
        return this.CEFailureOverride;
    }

    public ScramblerData getScramblerData() {
        return this.scramblerData;
    }

    public ScrollData getScrollData() {
        return this.scrollData;
    }

    public ItemStack getSlotCrystal() {
        return this.slot_crystal;
    }
}