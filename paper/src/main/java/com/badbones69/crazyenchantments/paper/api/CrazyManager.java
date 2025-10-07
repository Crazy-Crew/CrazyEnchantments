package com.badbones69.crazyenchantments.paper.api;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.Enchant;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
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
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.api.utils.NumberUtils;
import com.badbones69.crazyenchantments.paper.api.utils.WingsUtils;
import com.badbones69.crazyenchantments.paper.config.ConfigOptions;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.paper.listeners.ScramblerListener;
import com.badbones69.crazyenchantments.paper.listeners.ScrollListener;
import com.badbones69.crazyenchantments.paper.listeners.SlotCrystalListener;
import com.badbones69.crazyenchantments.paper.support.CropManager;
import com.badbones69.crazyenchantments.paper.support.interfaces.CropManagerVersion;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import com.ryderbelserion.fusion.paper.scheduler.Scheduler;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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

    private final CrazyInstance instance = this.plugin.getInstance();

    private final ConfigOptions options = this.plugin.getOptions();

    private final FusionPaper fusion = this.plugin.getFusion();
    
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
    private final List<CEPlayer> players = new ArrayList<>();
    private final List<Material> blockList = new ArrayList<>();
    private final Map<Material, Double> headMap = new HashMap<>();

    /**
     * Loads everything for the Crazy Enchantments plugin.
     * Do not use unless needed.
     */
    public void load() {
        final YamlConfiguration enchants = FileKeys.enchantments.getConfiguration();

        final YamlConfiguration blocks = FileKeys.blocklist.getConfiguration();
        final YamlConfiguration heads = FileKeys.head_map.getConfiguration();

        this.blockList.clear();
        this.headMap.clear();

        this.instance.purgeEnchantments();

        this.enchantmentBookSettings.getCategories().clear();

        this.starter.getPluginSupport().updateHooks();

        // Check if we should patch player health.
        boolean resetMaxHealth = this.options.isResetMaxHealth();

        this.plugin.getServer().getOnlinePlayers().forEach(player -> {
            // Load our players.
            loadCEPlayer(player);

            if (resetMaxHealth) {
                @Nullable final AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);

                if (attribute != null) {
                    attribute.setBaseValue(attribute.getBaseValue());
                }
            }

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
        blocks.getStringList("Block-List").forEach(id -> {
            try {
                this.blockList.add(new ItemBuilder().setMaterial(id).getMaterial());
            } catch (Exception ignored) {}
        });

        ConfigurationSection headSec = heads.getConfigurationSection("HeadOdds");

        if (headSec == null) {
            this.fusion.log("warn", "HeadOdds could not be found in HeadMap.yml!");
        } else {
            headSec.getKeys(false).forEach(id -> {
                try {
                    Material mat = new ItemBuilder().setMaterial(id).getMaterial();
                    this.headMap.put(mat, headSec.getDouble(id));
                } catch (Exception ignored) {}
            });
        }

        Scrolls.getWhiteScrollProtectionName();

        this.enchantmentBookSettings.populateMaps();

        for (CEnchantments cEnchantment : CEnchantments.values()) {
            String name = cEnchantment.getName();
            String path = "Enchantments." + name;

            if (enchants.contains(path)) { // To make sure the enchantment isn't broken.
                CEnchantment enchantment = new CEnchantment(name)
                .setCustomName(enchants.getString(path + ".Name", name))
                .setActivated(enchants.getBoolean(path + ".Enabled", false))
                .setMaxLevel(enchants.getInt(path + ".MaxPower")) //todo() default power
                .setEnchantmentType(cEnchantment.getType())
                .setInfoName(enchants.getString(path + ".Info.Name")) //todo() default name
                .setInfoDescription(enchants.getStringList(path + ".Info.Description"))
                .setCategories(enchants.getStringList(path + ".Categories"))
                .setChance(cEnchantment.getChance())
                .setChanceIncrease(cEnchantment.getChanceIncrease())
                .setSound(enchants.getString(path + ".Sound")) //todo() default sound
                .setConflicts(enchants.getStringList(path + ".Conflicts"));

                if (enchants.contains(path + ".Enchantment-Type")) enchantment.setEnchantmentType(this.methods.getFromName(enchants.getString(path + ".Enchantment-Type")));

                if (cEnchantment.hasChanceSystem()) {
                    enchantment.setChance(enchants.getInt(path + ".Chance-System.Base", cEnchantment.getChance()));

                    enchantment.setChanceIncrease(enchants.getInt(path + ".Chance-System.Increase", cEnchantment.getChanceIncrease()));
                }

                enchantment.registerEnchantment();
            }
        }

        // Load all scroll types.
        Scrolls.loadScrolls();
        // Load all dust types.
        Dust.loadDust();

        // Loads the protection crystals.
        this.protectionCrystalSettings.loadProtectionCrystal();
        // Loads the scrambler.
        this.scramblerListener.loadScrambler();
        // Loads Slot Crystal.
        this.slotCrystalListener.load();
        // Loads the Scroll Control settings.
        this.scrollListener.loadScrollControl();

        this.cropManagerVersion = new CropManager();

        // Loads the scrolls.
        Scrolls.loadScrolls();
        // Loads the dust.
        Dust.loadDust();

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

    /**
     * Only needs used when the player joins the server.
     * This plugin does it automatically, so there is no need to use it unless you have to.
     * @param player The player you wish to load.
     */
    public void loadCEPlayer(@NotNull final Player player) {
        final YamlConfiguration data = FileKeys.data.getConfiguration();

        String uuid = player.getUniqueId().toString();

        List<GkitCoolDown> gkitCoolDowns = new ArrayList<>();

        for (GKitz kit : this.instance.getGKitz()) {
            if (data.contains("Players." + uuid + ".GKitz." + kit.getName())) {
                Calendar coolDown = Calendar.getInstance();
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
    public void unloadCEPlayer(@NotNull final Player player) {
        final YamlConfiguration data = FileKeys.data.getConfiguration();

        String uuid = player.getUniqueId().toString();
        CEPlayer cePlayer = getCEPlayer(player);

        if (cePlayer != null) {
            for (GkitCoolDown gkitCooldown : cePlayer.getCoolDowns()) {
                data.set("Players." + uuid + ".GKitz." + gkitCooldown.getGKitz().getName(), gkitCooldown.getCoolDown().getTimeInMillis());
            }

            FileKeys.data.save();
        }

        if (cePlayer != null) {
            removeCEPlayer(cePlayer);
        }
    }

    /**
     * This backup all the players data stored by this plugin.
     * @param player The player you wish to back up.
     */
    public void backupCEPlayer(@NotNull final Player player) {
        backupCEPlayer(getCEPlayer(player));
    }

    /**
     * This backup all the players data stored by this plugin.
     * @param cePlayer The player you wish to back up.
     */
    private void backupCEPlayer(@NotNull final CEPlayer cePlayer) {
        final YamlConfiguration data = FileKeys.data.getConfiguration();

        String uuid = cePlayer.getPlayer().getUniqueId().toString();

        for (GkitCoolDown gkitCooldown : cePlayer.getCoolDowns()) {
            data.set("Players." + uuid + ".GKitz." + gkitCooldown.getGKitz().getName(), gkitCooldown.getCoolDown().getTimeInMillis());
        }

        FileKeys.data.save();
    }

    /**
     * @return NMS support class.
     */
    public CropManagerVersion getNMSSupport() {
        return this.cropManagerVersion;
    }

    /**
     * Get a GKit from its name.
     * @param kitName The kit you wish to get.
     * @return The kit as a GKitz object.
     */
    public GKitz getGKitFromName(@NotNull final String kitName) {
        for (GKitz kit : this.instance.getGKitz()) {
            if (kit.getName().equalsIgnoreCase(kitName)) return kit;
        }

        return null;
    }

    /**
     * This converts a normal Player into a CEPlayer that is loaded.
     * @param player The player you want to get as a CEPlayer.
     * @return The player but as a CEPlayer. Will return null if not found.
     */
    public CEPlayer getCEPlayer(@NotNull final Player player) {
        for (CEPlayer cePlayer : getCEPlayers()) {
            if (cePlayer.getPlayer() == player) return cePlayer;
        }

        return null;
    }

    public CEPlayer getCEPlayer(@NotNull final UUID uuid) {
        for (CEPlayer cePlayer : getCEPlayers()) {
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
    
    public CEBook getRandomEnchantmentBook(@NotNull final Category category) {
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

    public void addEnchantment(@NotNull final ItemStack item, @NotNull final CEnchantment enchantment, final int level) {
        Map<CEnchantment, Integer> enchantments = new HashMap<>();

        enchantments.put(enchantment, level);

        addEnchantments(item, enchantments);
    }

    /**
     * @param itemStack The meta you want to add the enchantment to.
     * @param enchantments The enchantments to be added.
     */
    public void addEnchantments(@NotNull final ItemStack itemStack, @NotNull final Map<CEnchantment, Integer> enchantments) {
        final Map<CEnchantment, Integer> currentEnchantments = this.instance.getEnchantments(itemStack);

        this.instance.removeEnchantments(itemStack, enchantments.keySet().stream().filter(currentEnchantments::containsKey).toList());

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
            if (newAmount <= 0) {
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
        if (!this.options.isUseEnchantmentLimiter()) return 0;

        return item.getPersistentDataContainer().getOrDefault(DataKeys.limit_reducer.getNamespacedKey(), PersistentDataType.INTEGER, 0);
    }

    /**
     * Force an update of a players armor potion effects.
     * @param player The player you are updating the effects of.
     */
    public void updatePlayerEffects(@Nullable final Player player) { // TODO Remove this method.
        if (player == null) return;

        Set<CEnchantments> allEnchantPotionEffects = getEnchantmentPotions().keySet();

        for (ItemStack armor : player.getEquipment().getArmorContents()) {
            final ItemStack safeArmor = armor == null ? ItemStack.empty() : armor;

            final Map<CEnchantment, Integer> enchantments = this.instance.getEnchantments(safeArmor);

            for (final CEnchantments ench : allEnchantPotionEffects) {
                if (!enchantments.containsKey(ench.getEnchantment())) continue;

                final Map<PotionEffectType, Integer> effects = getUpdatedEffects(player, safeArmor, ItemStack.empty(), ench);

                checkPotions(effects, player);
            }
        }
    }

    public void checkPotions(@NotNull final Map<PotionEffectType, Integer> effects, @NotNull final Player player) { //TODO Remove this Method
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
    public Map<PotionEffectType, Integer> getUpdatedEffects(@NotNull final Player player, @NotNull final ItemStack includedItem, @NotNull final ItemStack excludedItem, @NotNull final CEnchantments enchantment) { //TODO Remove this method.
        Map<PotionEffectType, Integer> effects = new HashMap<>();

        List<ItemStack> items = new ArrayList<>(Arrays.asList(player.getEquipment().getArmorContents()));

        ItemStack safeItem = excludedItem;

        if (safeItem.isSimilar(includedItem)) safeItem = ItemStack.empty();

        items.add(includedItem);

        Map<CEnchantments, HashMap<PotionEffectType, Integer>> armorEffects = getEnchantmentPotions();

        for (ItemStack armor : items) {
            if (armor == null || armor.isEmpty() || armor.isSimilar(safeItem)) continue;

            Map<CEnchantment, Integer> ench = this.instance.getEnchantments(armor);

            for (Entry<CEnchantments, HashMap<PotionEffectType, Integer>> enchantments : armorEffects.entrySet()) {
                if (!ench.containsKey(enchantments.getKey().getEnchantment())) continue;

                int level = ench.get(enchantments.getKey().getEnchantment());

                if (!this.options.isUseUnsafeEnchantments() && level > enchantments.getKey().getEnchantment().getMaxLevel()) level = enchantments.getKey().getEnchantment().getMaxLevel();

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
     * Get a players max amount of enchantments.
     * @param player The player you are checking.
     * @return The max amount of enchantments a player can have on an item.
     */
    public int getPlayerMaxEnchantments(@NotNull final Player player) {
        int limit = this.options.getDefaultLimit();

        if (this.options.isUseConfigLimits()) return limit;

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
    public int getPlayerBaseEnchantments(@NotNull final Player player) {
        int limit = this.options.getDefaultLimit();

        if (this.options.isUseConfigLimits()) return limit;

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
    public boolean canAddEnchantment(@NotNull final Player player, @NotNull final ItemStack item) {
        //todo() update permissions
        if (!this.options.isMaxEnchantmentCheck() || player.hasPermission("crazyenchantments.bypass.limit")) return true;

        return this.instance.getEnchantmentAmount(item, this.options.isCheckVanillaLimit()) < Math.min(getPlayerBaseEnchantments(player) - getEnchantmentLimiter(item), getPlayerMaxEnchantments(player));
    }

    /**
     * Checks if the player can add more enchants to the current item based on set limits without the enchant limiter.
     * @param player The {@link Player} that has the item.
     * @param cEnchantments The amount of crazy enchants on the item.
     * @param vanillaEnchantments The amount of vanilla enchantments on the item.
     * @return True if they are able to add more enchants.
     */
    public boolean canAddEnchantment(@NotNull final Player player, final int cEnchantments, final int vanillaEnchantments) {
        if (!this.options.isMaxEnchantmentCheck() || player.hasPermission("crazyenchantments.bypass.limit")) return true;

        int enchantAmount = cEnchantments;
        if (this.options.isCheckVanillaLimit()) enchantAmount += vanillaEnchantments;

        return enchantAmount < getPlayerMaxEnchantments(player);
    }

    public int randomLevel(@NotNull final CEnchantment enchantment, @NotNull final Category category) {
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

    private void addCEPlayer(@NotNull final CEPlayer player) {
        this.players.add(player);
    }

    private void removeCEPlayer(@NotNull final CEPlayer player) {
        this.players.remove(player);
    }

    public int pickLevel(final int min, final int max) {
        return min + new Random().nextInt((max + 1) - min);
    }
}