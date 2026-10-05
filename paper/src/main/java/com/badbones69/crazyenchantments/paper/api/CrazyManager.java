package com.badbones69.crazyenchantments.paper.api;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.badbones69.crazyenchantments.paper.api.enums.ShopOption;
import com.badbones69.crazyenchantments.paper.api.enums.keys.FileKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.Enchant;
import com.badbones69.crazyenchantments.paper.api.managers.AllyManager;
import com.badbones69.crazyenchantments.paper.api.managers.ArmorEnchantmentManager;
import com.badbones69.crazyenchantments.paper.api.managers.BowEnchantmentManager;
import com.badbones69.crazyenchantments.paper.api.managers.ShopManager;
import com.badbones69.crazyenchantments.paper.api.managers.WingsManager;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEPlayer;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GkitCoolDown;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.AttributeUtils;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.api.utils.NumberUtils;
import com.badbones69.crazyenchantments.paper.api.utils.WingsUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.paper.listeners.ScramblerListener;
import com.badbones69.crazyenchantments.paper.listeners.ScrollListener;
import com.badbones69.crazyenchantments.paper.listeners.SlotCrystalListener;
import com.ryderbelserion.fusion.api.enums.Level;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.folia.FoliaScheduler;
import com.ryderbelserion.fusion.paper.builders.folia.Scheduler;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Server;
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
import org.spongepowered.configurate.serialize.SerializationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CrazyManager {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final CrazyPlatform platform = this.plugin.getPlatform();

    private final FusionPaper fusion = this.platform.getFusion();

    private final Server server = this.plugin.getServer();
    
    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final Methods methods = this.starter.getMethods();

    // Settings.
    @NotNull
    private final ProtectionCrystalSettings protectionCrystalSettings = this.starter.getProtectionCrystalSettings();
    @NotNull
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    // Listeners.
    @NotNull
    private final ScramblerListener scramblerListener = this.starter.getScramblerListener();
    @NotNull
    private final ScrollListener scrollListener = this.starter.getScrollListener();

    @NotNull
    private final SlotCrystalListener slotCrystalListener = this.starter.getSlotCrystalListener();

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
    private final Map<Dust, ItemBuilder> dusts = new HashMap<>();
    private final List<GKitz> gkitz = new ArrayList<>();
    private final Map<UUID, CEPlayer> players = new HashMap<>();
    private final List<Material> blockList = new ArrayList<>();
    private final Map<Material, Double> headMap = new HashMap<>();

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

    /**
     * Loads everything for the Crazy Enchantments plugin.
     * Do not use unless needed.
     */
    public void load() {
        final CommentedConfigurationNode config = FileKeys.CONFIG.getConfiguration();
        final CommentedConfigurationNode gkit = FileKeys.GKITZ.getConfiguration();
        final CommentedConfigurationNode enchants = FileKeys.ENCHANTMENTS.getConfiguration();

        final CommentedConfigurationNode blocks = FileKeys.BLOCKLIST.getConfiguration();
        final CommentedConfigurationNode heads = FileKeys.HEADMAP.getConfiguration();

        this.blockList.clear();
        this.headMap.clear();
        this.gkitz.clear();
        this.enchantmentBookSettings.getRegisteredEnchantments().clear();
        this.enchantmentBookSettings.getCategories().clear();

        // Check if we should patch player health.
        final boolean isPatchingHealth = config.node("Settings", "Reset-Players-Max-Health").getBoolean(true);

        for (final Player player : this.server.getOnlinePlayers()) {
            loadCEPlayer(player);

            if (isPatchingHealth) {
                AttributeUtils.setHealth(player, 0);
            }

            new FoliaScheduler(this.plugin, Scheduler.global_scheduler, TimeUnit.MINUTES) {
                @Override
                public void run() {
                    backupCEPlayer(player);
                }
            }.runAtFixedRate(5, 5);
        }

        // Invalidate cached enchants.
        CEnchantments.invalidateCachedEnchants();

        StringUtils.getStringList(blocks.node("Block-List")).forEach(id -> {
            try {
                this.blockList.add(new ItemBuilder().setMaterial(id).getMaterial());
            } catch (Exception ignored) {}
        });

        if (heads.hasChild("HeadOdds")) {
            final CommentedConfigurationNode section = heads.node("HeadOdds");

            section.childrenList().forEach(child -> {

            });

            section.childrenMap().forEach((object, child) -> {
                final double amount = child.node(object).getDouble(0.0);

                this.headMap.put(new ItemBuilder().setMaterial(object.toString()).getMaterial(), amount);
            });
        }

        Scrolls.getWhiteScrollProtectionName();

        this.enchantmentBookSettings.setEnchantmentBook(new ItemBuilder().setMaterial(Methods.getNode(config, "Settings.Enchantment-Book-Item").getString("BOOK")));
        this.useUnsafeEnchantments = Methods.getNode(config, "Settings.EnchantmentOptions.UnSafe-Enchantments").getBoolean(true);
        this.maxEnchantmentCheck = Methods.getNode(config, "Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle").getBoolean(true);
        this.useConfigLimits = Methods.getNode(config, "Settings.EnchantmentOptions.Limit.Check-Perms").getBoolean(false);
        this.defaultLimit = Methods.getNode(config, "Settings.EnchantmentOptions.Limit.Default-Limit").getInt(0);
        this.defaultBaseLimit = Methods.getNode(config, "Settings.EnchantmentOptions.Limit.Default-Base-Limit").getInt(0);
        this.useEnchantmentLimiter = Methods.getNode(config, "Settings.EnchantmentOptions.Limit.Enable-SlotCrystal").getBoolean(true);
        this.checkVanillaLimit = Methods.getNode(config, "Settings.EnchantmentOptions.IncludeVanillaEnchantments").getBoolean(false);
        this.gkitzToggle = !config.hasChild("Settings", "GKitz", "Enabled") || Methods.getNode(config, "Settings.GKitz.Enabled").getBoolean(true);
        this.rageMaxLevel = Methods.getNode(config, "Settings.EnchantmentOptions.MaxRageLevel").getInt(4);
        this.breakRageOnDamage = Methods.getNode(config, "Settings.EnchantmentOptions.Break-Rage-On-Damage").getBoolean(true);
        this.useRageBossBar = Methods.getNode(config, "Settings.EnchantmentOptions.Rage-Boss-Bar").getBoolean(false);
        this.rageIncrement = Methods.getNode(config, "Settings.EnchantmentOptions.Rage-Increase").getDouble(0.1);
        setDropBlocksBlast(Methods.getNode(config, "Settings.EnchantmentOptions.Drop-Blocks-For-Blast").getBoolean(true));
        setDropBlocksVeinMiner(Methods.getNode(config, "Settings.EnchantmentOptions.Drop-Blocks-For-VeinMiner").getBoolean(true));

        this.CEFailureOverride = Methods.getNode(config, "Settings.CEFailureOverride").getInt(-1);
        this.CESuccessOverride = Methods.getNode(config, "Settings.CESuccessOverride").getInt(-1);

        this.enchantmentBookSettings.populateMaps();

        for (CEnchantments cEnchantment : CEnchantments.values()) {
            String name = cEnchantment.getName();
            String path = "Enchantments." + name;

            if (!enchants.hasChild(path)) {
                continue;
            }

            final CEnchantment enchantment = new CEnchantment(name);

            final CommentedConfigurationNode section = enchants.node(path);

            final String displayName = section.node("Name").getString("");
            final boolean isEnabled = section.node("Enabled").getBoolean(true);
            final int maxPower = section.node("MaxPower").getInt(1);

            final String infoName = section.node("Info", "Name").getString("");
            final List<String> infoDescription = StringUtils.getStringList(section.node("Info", "Description"));

            final List<String> categories = StringUtils.getStringList(section.node("Categories"));

            final String sound = section.node("Sound").getString("");

            final List<String> conflicts = StringUtils.getStringList(section.node("Conflicts"));

            final int increase = cEnchantment.getChanceIncrease();
            final int chance = cEnchantment.getChance();

            enchantment.setCustomName(displayName)
                    .setActivated(isEnabled)
                    .setMaxLevel(maxPower)
                    .setInfoName(infoName)
                    .setInfoDescription(infoDescription)
                    .setCategories(categories)
                    .setChance(chance)
                    .setChanceIncrease(increase)
                    .setSound(sound)
                    .setConflicts(conflicts);

            if (section.hasChild("Enchantment-Type")) {
                enchantment.setEnchantmentType(this.methods.getFromName(section.node("Enchantment-Type").getString("")));
            }

            if (cEnchantment.hasChanceSystem()) {
                final CommentedConfigurationNode chanceSystem = section.node("Chance-System");

                if (chanceSystem != null) {
                    if (chanceSystem.hasChild("Base")) {
                        enchantment.setChance(chanceSystem.node("Base").getInt(chance));
                    } else {
                        enchantment.setChance(chance);
                    }

                    if (chanceSystem.hasChild("Increase")) {
                        enchantment.setChance(chanceSystem.node("Increase").getInt(increase));
                    } else {
                        enchantment.setChance(increase);
                    }
                }
            }

            enchantment.registerEnchantment();
        }

        if (this.gkitzToggle) {
            if (!gkit.hasChild("Gkitz")) {
                this.fusion.log(Level.warn, "The gkitz section cannot be found in gkitz.yml, It's possible the file is badly formatted!");
            } else {
                final CommentedConfigurationNode gkitz = gkit.node("GKitz");

                gkitz.childrenMap().forEach((id, child) -> {
                    final CommentedConfigurationNode display = child.node("Display");

                    final int displaySlot = display.node("Slot").getInt(-1);
                    final String displayItem = display.node("Item").getString(ColorUtils.getRandomPaneColor().getName());
                    final List<String> displayLore = StringUtils.getStringList(display.node("Lore"));
                    final String displayName = display.node("Name").getString("&cError fetching name for %s".formatted(id));

                    final String displayNamespace = display.node("Display", "Model", "Namespace").getString("");
                    final String displayKey = display.node("Display", "Model", "Key").getString("");

                    final boolean isGlowing = display.node("Glowing").getBoolean(false);

                    final ItemStack itemStack = new ItemBuilder().setMaterial(displayItem)
                            .setName(displayName)
                            .setLore(displayLore)
                            .setGlow(isGlowing)
                            .setItemModel(displayNamespace, displayKey)
                            .addKey(DataKeys.gkit_type.getNamespacedKey(), id.toString()).build();

                    final List<String> commands = StringUtils.getStringList(child.node("Commands"));
                    final List<String> items = StringUtils.getStringList(child.node("Items"));
                    final List<ItemStack> itemStacks = getInfoGKit(items);
                    final String time = child.node("Time").getString("");
                    final boolean isAutoEquip = child.node("Auto-Equip").getBoolean(false);

                    itemStacks.addAll(getInfoGKit(StringUtils.getStringList(child.node("Fake-Items"))));

                    this.gkitz.add(new GKitz(
                            id.toString(),
                            displaySlot,
                            time,
                            itemStack,
                            itemStacks,
                            commands,
                            items,
                            isAutoEquip
                    ));
                });
            }
        }

        // Load all scroll types.
        Scrolls.loadScrolls();
        // Load all dust types.
        loadDust();

        // Loads the protection crystals.
        this.protectionCrystalSettings.loadProtectionCrystal();
        // Loads the scrambler.
        this.scramblerListener.loadScrambler();
        // Loads Slot Crystal.
        this.slotCrystalListener.load();
        // Loads the Scroll Control settings.
        this.scrollListener.loadScrollControl();

        // Loads the ShopOptions.
        ShopOption.loadShopOptions();

        // Loads the shop manager.
        this.shopManager.load();

        // Loads the settings for wings enchantment.
        this.wingsManager.load();

        // Loads the settings for the bow enchantments.
        this.bowEnchantmentManager.load();

        // Loads the settings for the armor enchantments.
        this.armorEnchantmentManager.load();

        // Loads the settings for the ally enchantments.
        this.allyManager.load();

        // Starts the wings task.
        WingsUtils.startWings();
    }

    public void loadDust() {
        final CommentedConfigurationNode configuration = FileKeys.CONFIG.getConfiguration();

        this.dusts.clear();

        for (final Dust dust : Dust.values()) {
            addDust(configuration, dust);
        }
    }

    public ItemBuilder getDust(final Dust dust) {
        return this.dusts.get(dust);
    }

    public void addDust(final CommentedConfigurationNode configuration, final Dust dust) {
        final CommentedConfigurationNode section = configuration.node("Settings", "Dust", dust.getConfigName());

        final ItemBuilder itemBuilder = new ItemBuilder();

        itemBuilder.setName(section.node("Name").getString());
        itemBuilder.setLore(StringUtils.getStringList(section.node("Lore")));

        itemBuilder.setItemModel(
                section.node("Model", "Namespace").getString(""),
                section.node("Model", "Key").getString("")
        );

        itemBuilder.setMaterial(section.node("Item").getString("GLOWSTONE_DUST"));

        this.dusts.put(dust, itemBuilder);
    }

    /**
     * Only needs used when the player joins the server.
     * This plugin does it automatically, so there is no need to use it unless you have to.
     * @param player The player you wish to load.
     */
    public void loadCEPlayer(Player player) {
        final CommentedConfigurationNode data = FileKeys.DATA.getConfiguration();
        final UUID uuid = player.getUniqueId();
        final String asString = uuid.toString();

        List<GkitCoolDown> cooldowns = new ArrayList<>();

        final CommentedConfigurationNode section = data.node(asString, "GKitz");

        for (final GKitz kit : getGKitz()) {
            final CommentedConfigurationNode gkitz = section.node("GKitz");

            final String kitName = kit.getName();

            if (!gkitz.hasChild(kitName)) {
                continue;
            }

            final CommentedConfigurationNode type = gkitz.node(kitName);

            final Calendar calendar = Calendar.getInstance();

            calendar.setTimeInMillis(type.getLong());

            cooldowns.add(new GkitCoolDown(kit, calendar));
        }

        addCEPlayer(uuid, new CEPlayer(player.getName(), uuid, cooldowns));
    }

    /**
     * Only needs used when the player leaves the server.
     * This plugin removes the player automatically, so don't use this method unless needed for some reason.
     * @param player Player you wish to remove.
     */
    public void unloadCEPlayer(Player player) {
        final CommentedConfigurationNode data = FileKeys.DATA.getConfiguration();

        final String playerName = player.getName();
        final UUID uuid = player.getUniqueId();

        final CommentedConfigurationNode section = data.node(uuid.toString(), "GKitz");

        getCEPlayer(uuid).ifPresent(cePlayer -> {
            for (final GkitCoolDown cooldown : cePlayer.getCoolDowns()) {
                final String kitName = cooldown.getGKitz().getName();

                try {
                    section.node(kitName).set(cooldown.getCoolDown().getTimeInMillis());
                } catch (final SerializationException exception) {
                    this.fusion.log(Level.error, "Failed to set cooldown for the kit %s for player %s", kitName, playerName);
                }
            }

            FileKeys.DATA.save();

            removeCEPlayer(uuid);
        });
    }

    /**
     * This backup all the players data stored by this plugin.
     * @param player The player you wish to back up.
     */
    public void backupCEPlayer(Player player) {
        getCEPlayer(player).ifPresent(this::backupCEPlayer);
    }

    /**
     * This backup all the players data stored by this plugin.
     * @param cePlayer The player you wish to back up.
     */
    private void backupCEPlayer(CEPlayer cePlayer) {
        final CommentedConfigurationNode data = FileKeys.DATA.getConfiguration();

        final String uuid = cePlayer.getUuid().toString();
        final String playerName = cePlayer.getPlayerName();
        final CommentedConfigurationNode section = data.node(uuid, "GKitz");

        for (final GkitCoolDown cooldown : cePlayer.getCoolDowns()) {
            final String kitName = cooldown.getGKitz().getName();

            try {
                section.node(kitName).set(cooldown.getCoolDown().getTimeInMillis());
            } catch (final SerializationException exception) {
                this.fusion.log(Level.error, "Failed to set cooldown for the kit %s for player %s", kitName, playerName);
            }
        }

        FileKeys.DATA.save();
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
    public GKitz getGKitFromName(String kitName) {
        for (GKitz kit : getGKitz()) {
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
    public Optional<CEPlayer> getCEPlayer(Player player) {
        return Optional.ofNullable(this.players.get(player.getUniqueId()));
    }

    public Optional<CEPlayer> getCEPlayer(UUID uuid) {
        return Optional.ofNullable(this.players.get(uuid));
    }

    /**
     * This gets all the CEPlayer's that are loaded.
     * @return All CEPlayer's that are loading and in a list.
     */
    public List<CEPlayer> getCEPlayers() {
        return this.players.values().stream().toList();
    }
    
    public CEBook getRandomEnchantmentBook(Category category) {
        try {
            List<CEnchantment> enchantments = category.getEnabledEnchantments();
            CEnchantment enchantment = enchantments.get(new Random().nextInt(enchantments.size()));

            return new CEBook(enchantment, randomLevel(enchantment, category), 1, category);
        } catch (Exception e) {
            this.plugin.getLogger().info("The category " + category.getName() + " has no enchantments."
            + " Please add enchantments to the category in the Enchantments.yml. If you do not wish to have the category feel free to delete it from the Config.yml.");
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
        for (CEnchantment enchantment : this.enchantmentBookSettings.getRegisteredEnchantments()) {
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
    public void registerEnchantment(CEnchantment enchantment) {
        this.enchantmentBookSettings.getRegisteredEnchantments().add(enchantment);
    }

    /**
     * Unregister an enchantment that is registered into plugin.
     * @param enchantment The enchantment you wish to unregister.
     */
    public void unregisterEnchantment(CEnchantment enchantment) {
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

        final int newAmount = view.getOrDefault(DataKeys.limit_reducer.getNamespacedKey(), PersistentDataType.INTEGER, 0) + amount;

        itemStack.editPersistentDataContainer(container -> {
            if (newAmount <= 0) {
                container.remove(DataKeys.limit_reducer.getNamespacedKey());

                return;
            }

            container.set(DataKeys.limit_reducer.getNamespacedKey(), PersistentDataType.INTEGER, newAmount);
        });

        return itemStack;
    }

    /**
     *
     * @param item The {@link ItemStack} to check.
     * @return The limit set on the item by slot crystals.
     */
    public int getEnchantmentLimiter(@NotNull ItemStack item) {
        if (!useEnchantmentLimiter) return 0;

        return item.getPersistentDataContainer().getOrDefault(DataKeys.limit_reducer.getNamespacedKey(), PersistentDataType.INTEGER, 0);
    }

    /**
     * Force an update of a players armor potion effects.
     * @param player The player you are updating the effects of.
     */
    public void updatePlayerEffects(Player player) { // TODO Remove this method.
        if (player == null) return;
        Set<CEnchantments> allEnchantPotionEffects = getEnchantmentPotions().keySet();

        for (ItemStack armor : player.getEquipment().getArmorContents()) {
            Map<CEnchantment, Integer> enchantments = this.enchantmentBookSettings.getEnchantments(armor);
            for (CEnchantments ench : allEnchantPotionEffects) {
                if (!enchantments.containsKey(ench.getEnchantment())) continue;
                Map<PotionEffectType, Integer> effects = getUpdatedEffects(player, armor, new ItemStack(Material.AIR), ench);
                checkPotions(effects, player);
            }
        }
    }

    public void checkPotions(Map<PotionEffectType, Integer> effects, Player player) { //TODO Remove this Method
        for (Map.Entry<PotionEffectType, Integer> type : effects.entrySet()) {
            int value = type.getValue();
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
    public Map<PotionEffectType, Integer> getUpdatedEffects(Player player, ItemStack includedItem, ItemStack excludedItem, CEnchantments enchantment) { //TODO Remove this method.
        Map<PotionEffectType, Integer> effects = new HashMap<>();
        List<ItemStack> items = new ArrayList<>(Arrays.asList(player.getEquipment().getArmorContents()));

        if (includedItem == null) includedItem = new ItemStack(Material.AIR);

        if (excludedItem == null) excludedItem = new ItemStack(Material.AIR);

        if (excludedItem.isSimilar(includedItem)) excludedItem = new ItemStack(Material.AIR);

        items.add(includedItem);
        Map<CEnchantments, HashMap<PotionEffectType, Integer>> armorEffects = getEnchantmentPotions();

        for (ItemStack armor : items) {
            if (armor == null || armor.isSimilar(excludedItem)) continue;
            Map<CEnchantment, Integer> ench = this.enchantmentBookSettings.getEnchantments(armor);
            for (Entry<CEnchantments, HashMap<PotionEffectType, Integer>> enchantments : armorEffects.entrySet()) {
                if (!ench.containsKey(enchantments.getKey().getEnchantment())) continue;
                int level = ench.get(enchantments.getKey().getEnchantment());
                if (!this.useUnsafeEnchantments && level > enchantments.getKey().getEnchantment().getMaxLevel()) level = enchantments.getKey().getEnchantment().getMaxLevel();

                for (PotionEffectType type : enchantments.getValue().keySet()) {
                    if (effects.containsKey(type)) {
                        int updated = effects.get(type);

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
    public Map<CEnchantments, HashMap<PotionEffectType, Integer>> getEnchantmentPotions() {
        Map<CEnchantments, HashMap<PotionEffectType, Integer>> enchants = new HashMap<>();

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
        return useConfigLimits;
    }

    /**
     * Get a players max amount of enchantments.
     * @param player The player you are checking.
     * @return The max amount of enchantments a player can have on an item.
     */
    public int getPlayerMaxEnchantments(Player player) {
        int limit = defaultLimit;

        if (useConfigLimits) return limit;

        for (PermissionAttachmentInfo Permission : player.getEffectivePermissions()) {
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
    public int getPlayerBaseEnchantments(@NotNull Player player) {
        int limit = defaultBaseLimit;

        if (useConfigLimits) return limit;

        for (PermissionAttachmentInfo Permission : player.getEffectivePermissions()) {
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
    public boolean canAddEnchantment(@NotNull Player player, @NotNull ItemStack item) {
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
    public boolean canAddEnchantment(@NotNull Player player, int cEnchantments, int vanillaEnchantments) {
        if (!this.maxEnchantmentCheck || player.hasPermission("crazyenchantments.bypass.limit")) return true;

        int enchantAmount = cEnchantments;
        if (this.checkVanillaLimit) enchantAmount += vanillaEnchantments;

        return enchantAmount < getPlayerMaxEnchantments(player);
    }

    public int randomLevel(CEnchantment enchantment, Category category) {
        int enchantmentMax = enchantment.getMaxLevel(); // Max set by the enchantment.
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
    public void setDropBlocksBlast(boolean dropBlocksBlast) {
        this.dropBlocksBlast = dropBlocksBlast;
    }

    /**
     * @param dropBlocksVeinMiner If the vein-miner enchantment drops blocks.
     */
    public void setDropBlocksVeinMiner(boolean dropBlocksVeinMiner) {
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

    private void addCEPlayer(UUID uuid, CEPlayer player) {
        this.players.put(uuid, player);
    }

    private void removeCEPlayer(UUID uuid) {
        this.players.remove(uuid);
    }

    private List<ItemStack> getInfoGKit(List<String> itemStrings) {
        List<ItemStack> items = new ArrayList<>();

        for (String itemString : itemStrings) {
            // This is used to convert old v1.7- gkit files to use newer way.
            itemString = getNewItemString(itemString);

            ItemBuilder itemBuilder = ItemBuilder.convertString(itemString);
            List<String> customEnchantments = new ArrayList<>();
            HashMap<Enchantment, Integer> enchantments = new HashMap<>();

            for (String option : itemString.split(", ")) {
                try {
                    Enchantment enchantment = this.methods.getEnchantment(option.split(":")[0]);
                    CEnchantment cEnchantment = getEnchantmentFromName(option.split(":")[0]);
                    String level = option.split(":")[1];

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

    public String getNewItemString(String itemString) {
        StringBuilder newItemString = new StringBuilder();

        for (String option : itemString.split(", ")) {
            if (option.toLowerCase().startsWith("enchantments:") || option.toLowerCase().startsWith("customenchantments:")) {
                StringBuilder newOption = new StringBuilder();

                for (String enchantment : option.toLowerCase().replace("customenchantments:", "").replace("enchantments:", "").split(",")) {
                    newOption.append(enchantment).append(", ");
                }

                option = newOption.substring(0, newOption.length() - 2);
            }

            newItemString.append(option).append(", ");
        }

        if (!newItemString.isEmpty()) itemString = newItemString.substring(0, newItemString.length() - 2);
        return itemString;
    }

    public int pickLevel(int min, int max) {
        return min + new Random().nextInt((max + 1) - min);
    }

    /** Gets the success override from the config. Default -1 means no override should be used */
    public int getCESuccessOverride() { return CESuccessOverride; }

    /** Gets the failure override from the config. Default -1 means no override should be used */
    public int getCEFailureOverride() { return CEFailureOverride; }

}
