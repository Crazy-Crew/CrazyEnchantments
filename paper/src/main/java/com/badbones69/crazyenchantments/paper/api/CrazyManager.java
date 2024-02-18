package com.badbones69.crazyenchantments.paper.api;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.badbones69.crazyenchantments.paper.api.enums.ShopOption;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.Enchant;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.managers.*;
import com.badbones69.crazyenchantments.paper.api.managers.guis.InfoMenuManager;
import com.badbones69.crazyenchantments.paper.api.objects.*;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GkitCoolDown;
import com.badbones69.crazyenchantments.paper.api.objects.other.ItemBuilder;
import com.badbones69.crazyenchantments.paper.support.CropManager;
import com.badbones69.crazyenchantments.paper.support.interfaces.CropManagerVersion;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.paper.listeners.ScramblerListener;
import com.badbones69.crazyenchantments.paper.listeners.ScrollListener;
import com.badbones69.crazyenchantments.paper.listeners.SlotCrystalListener;
import com.badbones69.crazyenchantments.paper.api.utils.WingsUtils;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.api.utils.NumberUtils;
import com.google.gson.Gson;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.Map.Entry;

public class CrazyManager {

    @NotNull
    private final CrazyEnchantments plugin = CrazyEnchantments.get();
    
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

    @NotNull
    private final InfoMenuManager infoMenuManager = this.starter.getInfoMenuManager();

    // Arrays.
    private final List<GKitz> gkitz = new ArrayList<>();
    private final List<CEPlayer> players = new ArrayList<>();
    private final List<Material> blockList = new ArrayList<>();
    private final Map<Material, Double> headMap = new HashMap<>();

    private int rageMaxLevel;
    private boolean gkitzToggle;
    private boolean useUnsafeEnchantments;
    private boolean breakRageOnDamage;
    private boolean useRageBossBar;
    private boolean enchantStackedItems;
    private boolean maxEnchantmentCheck;
    private boolean checkVanillaLimit;

    private boolean dropBlocksBlast;
    private boolean dropBlocksVeinMiner;

    /**
     * Loads everything for the Crazy Enchantments plugin.
     * Do not use unless needed.
     */
    public void load() {
        FileConfiguration config = Files.CONFIG.getFile();
        FileConfiguration gkit = Files.GKITZ.getFile();
        FileConfiguration enchants = Files.ENCHANTMENTS.getFile();

        FileConfiguration blocks = Files.BLOCKLIST.getFile();
        FileConfiguration heads = Files.HEADMAP.getFile();

        this.blockList.clear();
        this.headMap.clear();
        this.gkitz.clear();
        this.enchantmentBookSettings.getRegisteredEnchantments().clear();
        this.enchantmentBookSettings.getCategories().clear();

        this.starter.getPluginSupport().updateHooks();

        // Check if we should patch player health.
        boolean playerHealthPatch = config.getBoolean("Settings.Reset-Players-Max-Health", true);

        this.plugin.getServer().getOnlinePlayers().forEach(player -> {
            // Load our players.
            loadCEPlayer(player);

            // Check if we need to patch playerHealth.
            Attribute genericAttribute = Attribute.GENERIC_MAX_HEALTH;

            double baseValue = player.getAttribute(genericAttribute).getBaseValue();

            if (playerHealthPatch) player.getAttribute(genericAttribute).setBaseValue(baseValue);

            // Loop through all players & back them up.
            this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, task ->
                    getCEPlayers().forEach(name ->
                            backupCEPlayer(name.getPlayer())), 5 * 20 * 60, 5 * 20 * 60);
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

        if (headSec != null) {
            headSec.getKeys(false).forEach(id -> {
                try {
                    Material mat = new ItemBuilder().setMaterial(id).getMaterial();
                    this.headMap.put(mat, headSec.getDouble(id));
                } catch (Exception ignored) {}
            });
        }

        // Loads the info menu manager and the enchantment types.
        this.infoMenuManager.load();

        Scrolls.getWhiteScrollProtectionName();

        this.enchantmentBookSettings.setEnchantmentBook(new ItemBuilder().setMaterial(config.getString("Settings.Enchantment-Book-Item", "BOOK")));
        this.useUnsafeEnchantments = config.getBoolean("Settings.EnchantmentOptions.UnSafe-Enchantments", true);
        this.maxEnchantmentCheck = config.getBoolean("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle", true);
        this.checkVanillaLimit = config.getBoolean("Settings.EnchantmentOptions.IncludeVanillaEnchantments", false);
        this.gkitzToggle = !config.contains("Settings.GKitz.Enabled") || config.getBoolean("Settings.GKitz.Enabled", true);
        this.rageMaxLevel = config.getInt("Settings.EnchantmentOptions.MaxRageLevel", 4);
        this.breakRageOnDamage = !config.contains("Settings.EnchantmentOptions.Break-Rage-On-Damage") || config.getBoolean("Settings.EnchantmentOptions.Break-Rage-On-Damage");
        this.useRageBossBar = config.contains("Settings.EnchantmentOptions.Rage-Boss-Bar") && config.getBoolean("Settings.EnchantmentOptions.Rage-Boss-Bar");
        this.enchantStackedItems = config.contains("Settings.EnchantmentOptions.Enchant-Stacked-Items") && config.getBoolean("Settings.EnchantmentOptions.Enchant-Stacked-Items");
        setDropBlocksBlast(config.getBoolean("Settings.EnchantmentOptions.Drop-Blocks-For-Blast", true));
        setDropBlocksVeinMiner(config.getBoolean("Settings.EnchantmentOptions.Drop-Blocks-For-VeinMiner", true));

        this.enchantmentBookSettings.populateMaps();

        for (CEnchantments cEnchantment : CEnchantments.values()) {
            String name = cEnchantment.getName();
            String path = "Enchantments." + name;

            if (enchants.contains(path)) { // To make sure the enchantment isn't broken.
                CEnchantment enchantment = new CEnchantment(name)
                .setCustomName(enchants.getString(path + ".Name"))
                .setActivated(enchants.getBoolean(path + ".Enabled"))
                .setMaxLevel(enchants.getInt(path + ".MaxPower"))
                .setEnchantmentType(cEnchantment.getType())
                .setInfoName(enchants.getString(path + ".Info.Name"))
                .setInfoDescription(enchants.getStringList(path + ".Info.Description"))
                .setCategories(enchants.getStringList(path + ".Categories"))
                .setChance(cEnchantment.getChance())
                .setChanceIncrease(cEnchantment.getChanceIncrease())
                .setSound(enchants.getString(path + ".Sound"));

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
            for (String kit : gkit.getConfigurationSection("GKitz").getKeys(false)) {
                String path = "GKitz." + kit + ".";
                int slot = gkit.getInt(path + "Display.Slot");
                String time = gkit.getString(path + "Cooldown");
                boolean autoEquip = gkit.getBoolean(path + "Auto-Equip");
                NBTItem displayItem = new NBTItem(new ItemBuilder()
                .setMaterial(gkit.getString(path + "Display.Item", ColorUtils.getRandomPaneColor().getName()))
                .setName(gkit.getString(path + "Display.Name", "Error getting name."))
                .setLore(gkit.getStringList(path + "Display.Lore"))
                .setGlow(gkit.getBoolean(path + "Display.Glowing")).build());
                displayItem.setString("gkit", kit);
                List<String> commands = gkit.getStringList(path + "Commands");
                List<String> itemStrings = gkit.getStringList(path + "Items");
                List<ItemStack> previewItems = getInfoGKit(itemStrings);
                previewItems.addAll(getInfoGKit(gkit.getStringList(path + "Fake-Items")));
                this.gkitz.add(new GKitz(kit, slot, time, displayItem.getItem(), previewItems, commands, itemStrings, autoEquip));
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

    /**
     * Only needs used when the player joins the server.
     * This plugin does it automatically, so there is no need to use it unless you have to.
     * @param player The player you wish to load.
     */
    public void loadCEPlayer(Player player) {
        FileConfiguration data = Files.DATA.getFile();
        String uuid = player.getUniqueId().toString();
        int souls = 0;
        boolean isActive = false;

        if (data.contains("Players." + uuid + ".Souls-Information")) {
            souls = data.getInt("Players." + uuid + ".Souls-Information.Souls");
            isActive = data.getBoolean("Players." + uuid + ".Souls-Information.Is-Active");
        }

        List<GkitCoolDown> gkitCoolDowns = new ArrayList<>();

        for (GKitz kit : getGKitz()) {
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
    public void unloadCEPlayer(Player player) {
        FileConfiguration data = Files.DATA.getFile();
        String uuid = player.getUniqueId().toString();
        CEPlayer cePlayer = getCEPlayer(player);

        if (cePlayer != null) {
            for (GkitCoolDown gkitCooldown : cePlayer.getCoolDowns()) {
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
    public void backupCEPlayer(Player player) {
        backupCEPlayer(getCEPlayer(player));
    }

    /**
     * This backup all the players data stored by this plugin.
     * @param cePlayer The player you wish to back up.
     */
    private void backupCEPlayer(CEPlayer cePlayer) {
        FileConfiguration data = Files.DATA.getFile();
        String uuid = cePlayer.getPlayer().getUniqueId().toString();

        for (GkitCoolDown gkitCooldown : cePlayer.getCoolDowns()) {
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
    public CEPlayer getCEPlayer(Player player) {
        for (CEPlayer cePlayer : getCEPlayers()) {
            if (cePlayer.getPlayer() == player) return cePlayer;
        }

        return null;
    }

    public CEPlayer getCEPlayer(UUID uuid) {
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
            enchantmentString = enchantmentString.replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or]| |_)", "");
            if (enchantment.getCustomName().replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or]| |_)", "").equalsIgnoreCase(enchantmentString)) return enchantment;
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

    /**
     * @see #addEnchantments(ItemMeta, Map) 
     */
    public ItemStack addEnchantment(ItemStack item, CEnchantment enchantment, int level) {
        Map<CEnchantment, Integer> enchantments = new HashMap<>();

        enchantments.put(enchantment, level);
        return addEnchantments(item, enchantments);
    }

    /**
     * @see #addEnchantments(ItemMeta, Map) 
     */
    public ItemStack addEnchantments(ItemStack item, Map<CEnchantment, Integer> enchantments) {
        item.setItemMeta(addEnchantments(item.getItemMeta(), enchantments));

        return item;
    }

    /**
     * @param meta The meta you want to add the enchantment to.
     * @param enchantments The enchantments to be added.
     * @return The item with the enchantment on it.
     */
    public ItemMeta addEnchantments(ItemMeta meta, Map<CEnchantment, Integer> enchantments) {
        Gson gson = new Gson();
        Map<CEnchantment, Integer> currentEnchantments = this.enchantmentBookSettings.getEnchantments(meta);

        meta = this.enchantmentBookSettings.removeEnchantments(meta, enchantments.keySet().stream().filter(currentEnchantments::containsKey).toList());

        String data = meta.getPersistentDataContainer().get(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING);
        Enchant enchantData = data != null ? gson.fromJson(data, Enchant.class) : new Enchant(new HashMap<>());

        List<Component> lore = meta.lore();
        if (lore == null) lore = new ArrayList<>();

        for (Entry<CEnchantment, Integer> entry : enchantments.entrySet()) {
            CEnchantment enchantment = entry.getKey();
            int level = entry.getValue();

            String loreString = enchantment.getCustomName() + " " + NumberUtils.convertLevelString(level);

            lore.add(ColorUtils.legacyTranslateColourCodes(loreString));

            for (Entry<CEnchantment, Integer> x : enchantments.entrySet()) {
                enchantData.addEnchantment(x.getKey().getName(), x.getValue());
            }
        }

        meta.lore(lore);
        meta.getPersistentDataContainer().set(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING, gson.toJson(enchantData));

        return meta;
    }

    public ItemStack changeEnchantmentLimiter(ItemStack item, int amount) {
        item.setItemMeta(changeEnchantmentLimiter(item.getItemMeta(), amount));
        return item;
    }

    public ItemMeta changeEnchantmentLimiter(ItemMeta meta, int amount) {
        PersistentDataContainer container = meta.getPersistentDataContainer();
        int newAmount = container.getOrDefault(DataKeys.limit_reducer.getNamespacedKey(), PersistentDataType.INTEGER, 0);
        newAmount += amount;

        if (newAmount == 0) {
            container.remove(DataKeys.limit_reducer.getNamespacedKey());
        } else {
            container.set(DataKeys.limit_reducer.getNamespacedKey(), PersistentDataType.INTEGER, newAmount);
        }

        return meta;
    }

    public int getEnchantmentLimiter(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(DataKeys.limit_reducer.getNamespacedKey(), PersistentDataType.INTEGER, 0);
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
            Integer value = type.getValue();
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
        HashMap<PotionEffectType, Integer> effects = new HashMap<>();
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
        HashMap<CEnchantments, HashMap<PotionEffectType, Integer>> enchants = new HashMap<>();

        enchants.put(CEnchantments.GLOWING, new HashMap<>());
        enchants.get(CEnchantments.GLOWING).put(PotionEffectType.NIGHT_VISION, -1);

        enchants.put(CEnchantments.MERMAID, new HashMap<>());
        enchants.get(CEnchantments.MERMAID).put(PotionEffectType.WATER_BREATHING, -1);

        enchants.put(CEnchantments.BURNSHIELD, new HashMap<>());
        enchants.get(CEnchantments.BURNSHIELD).put(PotionEffectType.FIRE_RESISTANCE, -1);

        enchants.put(CEnchantments.DRUNK, new HashMap<>());
        enchants.get(CEnchantments.DRUNK).put(PotionEffectType.INCREASE_DAMAGE, -1);
        enchants.get(CEnchantments.DRUNK).put(PotionEffectType.SLOW_DIGGING, -1);
        enchants.get(CEnchantments.DRUNK).put(PotionEffectType.SLOW, -1);

        enchants.put(CEnchantments.HULK, new HashMap<>());
        enchants.get(CEnchantments.HULK).put(PotionEffectType.INCREASE_DAMAGE, -1);
        enchants.get(CEnchantments.HULK).put(PotionEffectType.DAMAGE_RESISTANCE, -1);
        enchants.get(CEnchantments.HULK).put(PotionEffectType.SLOW, -1);

        enchants.put(CEnchantments.VALOR, new HashMap<>());
        enchants.get(CEnchantments.VALOR).put(PotionEffectType.DAMAGE_RESISTANCE, -1);

        enchants.put(CEnchantments.OVERLOAD, new HashMap<>());
        enchants.get(CEnchantments.OVERLOAD).put(PotionEffectType.HEALTH_BOOST, -1);

        enchants.put(CEnchantments.NINJA, new HashMap<>());
        enchants.get(CEnchantments.NINJA).put(PotionEffectType.HEALTH_BOOST, -1);
        enchants.get(CEnchantments.NINJA).put(PotionEffectType.SPEED, -1);

        enchants.put(CEnchantments.INSOMNIA, new HashMap<>());
        enchants.get(CEnchantments.INSOMNIA).put(PotionEffectType.CONFUSION, -1);
        enchants.get(CEnchantments.INSOMNIA).put(PotionEffectType.SLOW_DIGGING, -1);
        enchants.get(CEnchantments.INSOMNIA).put(PotionEffectType.SLOW, -1);

        enchants.put(CEnchantments.ANTIGRAVITY, new HashMap<>());
        enchants.get(CEnchantments.ANTIGRAVITY).put(PotionEffectType.JUMP, 1);

        enchants.put(CEnchantments.GEARS, new HashMap<>());
        enchants.get(CEnchantments.GEARS).put(PotionEffectType.SPEED, -1);

        enchants.put(CEnchantments.SPRINGS, new HashMap<>());
        enchants.get(CEnchantments.SPRINGS).put(PotionEffectType.JUMP, -1);

        enchants.put(CEnchantments.CYBORG, new HashMap<>());
        enchants.get(CEnchantments.CYBORG).put(PotionEffectType.SPEED, -1);
        enchants.get(CEnchantments.CYBORG).put(PotionEffectType.INCREASE_DAMAGE, 0);
        enchants.get(CEnchantments.CYBORG).put(PotionEffectType.JUMP, -1);

        return enchants;
    }

    /**
     * Get a players max amount of enchantments.
     * @param player The player you are checking.
     * @return The max amount of enchantments a player can have on an item.
     */
    public int getPlayerMaxEnchantments(Player player) {
        int limit = 0;

        for (PermissionAttachmentInfo Permission : player.getEffectivePermissions()) {
            String perm = Permission.getPermission().toLowerCase();

            if (perm.startsWith("crazyenchantments.limit.")) {
                perm = perm.replace("crazyenchantments.limit.", "");

                if (NumberUtils.isInt(perm) && limit < Integer.parseInt(perm)) limit = Integer.parseInt(perm);
            }
        }

        return limit;
    }

    public int getPlayerBaseEnchantments(Player player) {
        int limit = 0;

        for (PermissionAttachmentInfo Permission : player.getEffectivePermissions()) {
            String perm = Permission.getPermission().toLowerCase();

            if (perm.startsWith("crazyenchantments.base-limit.")) {
                perm = perm.replace("crazyenchantments.base-limit.", "");

                if (NumberUtils.isInt(perm) && limit < Integer.parseInt(perm)) limit = Integer.parseInt(perm);
            }
        }

        return limit;
    }

    public boolean canAddEnchantment(Player player, ItemStack item) {
        //todo() update permissions
        if (!this.maxEnchantmentCheck || player.hasPermission("crazyenchantments.bypass.limit")) return true;

        return this.enchantmentBookSettings.getEnchantmentAmount(item, this.checkVanillaLimit) <
                Math.min(getPlayerBaseEnchantments(player) - getEnchantmentLimiter(item), getPlayerMaxEnchantments(player));
    }

    /**
     * @param item Item you are getting the level from.
     * @param enchant The enchantment you want the level from.
     * @return The level the enchantment has.
     */
    public int getLevel(ItemStack item, CEnchantments enchant) {
        return this.enchantmentBookSettings.getLevel(item, enchant.getEnchantment());
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

    /**
     * Check if players can enchant a stack of items with an enchantment book.
     */
    public boolean enchantStackedItems() {
        return this.enchantStackedItems;
    }

    private void addCEPlayer(CEPlayer player) {
        this.players.add(player);
    }

    private void removeCEPlayer(CEPlayer player) {
        this.players.remove(player);
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

            NBTItem nbtItem = new NBTItem(itemBuilder.build());
            // This is done so items do not stack if there are multiple of the same.

            nbtItem.setInteger("random-number", new Random().nextInt(Integer.MAX_VALUE));
            items.add(nbtItem.getItem());
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
}
