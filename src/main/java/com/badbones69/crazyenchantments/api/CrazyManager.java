package com.badbones69.crazyenchantments.api;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.enums.Dust;
import com.badbones69.crazyenchantments.api.enums.Scrolls;
import com.badbones69.crazyenchantments.api.enums.ShopOption;
import com.badbones69.crazyenchantments.api.enums.pdc.Enchant;
import com.badbones69.crazyenchantments.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.api.managers.*;
import com.badbones69.crazyenchantments.api.managers.guis.InfoMenuManager;
import com.badbones69.crazyenchantments.api.objects.*;
import com.badbones69.crazyenchantments.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.api.objects.gkitz.GkitCoolDown;
import com.badbones69.crazyenchantments.api.support.CropManager;
import com.badbones69.crazyenchantments.api.support.interfaces.CropManagerVersion;
import com.badbones69.crazyenchantments.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.listeners.ScramblerListener;
import com.badbones69.crazyenchantments.listeners.ScrollListener;
import com.badbones69.crazyenchantments.utilities.WingsUtils;
import com.badbones69.crazyenchantments.utilities.misc.ColorUtils;
import com.badbones69.crazyenchantments.utilities.misc.NumberUtils;
import com.google.gson.Gson;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import java.util.*;
import java.util.Map.Entry;

public class CrazyManager {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Methods methods = plugin.getStarter().getMethods();

    // Settings.
    private final ProtectionCrystalSettings protectionCrystalSettings = plugin.getStarter().getProtectionCrystalSettings();
    private final EnchantmentBookSettings enchantmentBookSettings = plugin.getStarter().getEnchantmentBookSettings();

    // Listeners.
    private final ScramblerListener scramblerListener = plugin.getStarter().getScramblerListener();
    private final ScrollListener scrollListener = plugin.getStarter().getScrollListener();

    private CropManagerVersion cropManagerVersion;

    private final AllyManager allyManager = plugin.getStarter().getAllyManager();

    // Wings.
    private final WingsManager wingsManager = plugin.getStarter().getWingsManager();

    private final ShopManager shopManager = plugin.getStarter().getShopManager();
    private final BowEnchantmentManager bowEnchantmentManager = plugin.getStarter().getBowEnchantmentManager();
    private final ArmorEnchantmentManager armorEnchantmentManager = plugin.getStarter().getArmorEnchantmentManager();

    private final InfoMenuManager infoMenuManager = plugin.getStarter().getInfoMenuManager();

    // Arrays.
    private final List<GKitz> gkitz = new ArrayList<>();
    private final List<CEPlayer> players = new ArrayList<>();
    private final List<Material> blockList = new ArrayList<>();

    // Random
    private final Random random = new Random();

    private int rageMaxLevel;
    private boolean gkitzToggle;
    private boolean useUnsafeEnchantments;
    private boolean breakRageOnDamage;
    private boolean useRageBossBar;
    private boolean enchantStackedItems;
    private boolean maxEnchantmentCheck;
    private boolean checkVanillaLimit;

    private boolean dropBlocksBlast;

    /**
     * Loads everything for the Crazy Enchantments plugin.
     * Do not use unless needed.
     */
    public void load() {
        FileConfiguration config = Files.CONFIG.getFile();
        FileConfiguration gkit = Files.GKITZ.getFile();
        FileConfiguration enchants = Files.ENCHANTMENTS.getFile();

        FileConfiguration blocks = Files.BLOCKLIST.getFile();

        blockList.clear();
        gkitz.clear();
        enchantmentBookSettings.getRegisteredEnchantments().clear();
        enchantmentBookSettings.getCategories().clear();

        plugin.getStarter().getPluginSupport().updateHooks();

        // Check if we should patch player health.
        boolean playerHealthPatch = config.getBoolean("Settings.Reset-Players-Max-Health");

        plugin.getServer().getOnlinePlayers().forEach(player -> {
            // Load our players.
            loadCEPlayer(player);

            // Check if we need to patch playerHealth.
            Attribute genericAttribute = Attribute.GENERIC_MAX_HEALTH;

            double baseValue = player.getAttribute(genericAttribute).getBaseValue();

            if (playerHealthPatch) player.getAttribute(genericAttribute).setBaseValue(baseValue);

            // Loop through all players & back them up.
            plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task ->
                    getCEPlayers().forEach(name ->
                            backupCEPlayer(name.getPlayer())), 5 * 20 * 60, 5 * 20 * 60);
        });

        // Invalidate cached enchants.
        CEnchantments.invalidateCachedEnchants();

        // Loop through block list.
        blocks.getStringList("Block-List").forEach(id -> {
            try {
                blockList.add(new ItemBuilder().setMaterial(id).getMaterial());
            } catch (Exception ignored) {}
        });

        // Loads the info menu manager and the enchantment types.
        infoMenuManager.load();

        Scrolls.getWhiteScrollProtectionName();

        enchantmentBookSettings.setEnchantmentBook(new ItemBuilder().setMaterial(Objects.requireNonNull(config.getString("Settings.Enchantment-Book-Item"))));
        useUnsafeEnchantments = config.getBoolean("Settings.EnchantmentOptions.UnSafe-Enchantments");
        maxEnchantmentCheck = config.getBoolean("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle");
        checkVanillaLimit = config.getBoolean("Settings.EnchantmentOptions.IncludeVanillaEnchantments");
        gkitzToggle = !config.contains("Settings.GKitz.Enabled") || config.getBoolean("Settings.GKitz.Enabled");
        rageMaxLevel = config.contains("Settings.EnchantmentOptions.MaxRageLevel") ? config.getInt("Settings.EnchantmentOptions.MaxRageLevel") : 4;
        breakRageOnDamage = !config.contains("Settings.EnchantmentOptions.Break-Rage-On-Damage") || config.getBoolean("Settings.EnchantmentOptions.Break-Rage-On-Damage");
        useRageBossBar = config.contains("Settings.EnchantmentOptions.Rage-Boss-Bar") && config.getBoolean("Settings.EnchantmentOptions.Rage-Boss-Bar");
        enchantStackedItems = config.contains("Settings.EnchantmentOptions.Enchant-Stacked-Items") && config.getBoolean("Settings.EnchantmentOptions.Enchant-Stacked-Items");
        setDropBlocksBlast(config.getBoolean("Settings.EnchantmentOptions.Drop-Blocks-For-Blast", true));

        enchantmentBookSettings.populateMaps();

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
                .setChanceIncrease(cEnchantment.getChanceIncrease());

                if (enchants.contains(path + ".Enchantment-Type")) enchantment.setEnchantmentType(methods.getFromName(enchants.getString(path + ".Enchantment-Type")));

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

        if (gkitzToggle) {
            for (String kit : gkit.getConfigurationSection("GKitz").getKeys(false)) {
                String path = "GKitz." + kit + ".";
                int slot = gkit.getInt(path + "Display.Slot");
                String time = gkit.getString(path + "Cooldown");
                boolean autoEquip = gkit.getBoolean(path + "Auto-Equip");
                NBTItem displayItem = new NBTItem(new ItemBuilder()
                .setMaterial(gkit.getString(path + "Display.Item"))
                .setName(gkit.getString(path + "Display.Name"))
                .setLore(gkit.getStringList(path + "Display.Lore"))
                .setGlow(gkit.getBoolean(path + "Display.Glowing")).build());
                displayItem.setString("gkit", kit);
                List<String> commands = gkit.getStringList(path + "Commands");
                List<String> itemStrings = gkit.getStringList(path + "Items");
                List<ItemStack> previewItems = getInfoGKit(itemStrings);
                previewItems.addAll(getInfoGKit(gkit.getStringList(path + "Fake-Items")));
                gkitz.add(new GKitz(kit, slot, time, displayItem.getItem(), previewItems, commands, itemStrings, autoEquip));
            }
        }

        // Load all scroll types.
        Scrolls.loadScrolls();
        // Load all dust types.
        Dust.loadDust();

        // Loads the protection crystals.
        protectionCrystalSettings.loadProtectionCrystal();
        // Loads the scrambler.
        scramblerListener.loadScrambler();
        // Loads the Scroll Control settings.
        scrollListener.loadScrollControl();

        cropManagerVersion = new CropManager();

        // Loads the scrolls.
        Scrolls.loadScrolls();
        // Loads the dust.
        Dust.loadDust();

        // Loads the ShopOptions.
        ShopOption.loadShopOptions();

        // Loads the shop manager.
        shopManager.load();

        // Loads the settings for wings enchantment.
        wingsManager.load();

        // Loads the settings for the bow enchantments.
        bowEnchantmentManager.load();

        // Loads the settings for the armor enchantments.
        armorEnchantmentManager.load();

        // Loads the settings for the ally enchantments.
        allyManager.load();

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

        addCEPlayer(new CEPlayer(player, souls, isActive, gkitCoolDowns));
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

            if (cePlayer.getSouls() > 0) {
                data.set("Players." + uuid + ".Name", player.getName());
                data.set("Players." + uuid + ".Souls-Information.Souls", cePlayer.getSouls());
                data.set("Players." + uuid + ".Souls-Information.Is-Active", cePlayer.isSoulsActive());
            }

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

        if (cePlayer.getSouls() > 0) {
            data.set("Players." + uuid + ".Name", cePlayer.getPlayer().getName());
            data.set("Players." + uuid + ".Souls-Information.Souls", cePlayer.getSouls());
            data.set("Players." + uuid + ".Souls-Information.Is-Active", cePlayer.isSoulsActive());
        }

        for (GkitCoolDown gkitCooldown : cePlayer.getCoolDowns()) {
            data.set("Players." + uuid + ".GKitz." + gkitCooldown.getGKitz().getName(), gkitCooldown.getCoolDown().getTimeInMillis());
        }

        Files.DATA.saveFile();
    }

    /**
     * @return NMS support class.
     */
    public CropManagerVersion getNMSSupport() {
        return cropManagerVersion;
    }

    public boolean checkVanillaLimit() {
        return checkVanillaLimit;
    }

    /**
     * Check if the gkitz option is enabled.
     * @return True if it is on and false if it is off.
     */
    public boolean isGkitzEnabled() {
        return gkitzToggle;
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
        return gkitz;
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

    /**
     * This gets all the CEPlayer's that are loaded.
     * @return All CEPlayer's that are loading and in a list.
     */
    public List<CEPlayer> getCEPlayers() {
        return players;
    }

    /**
     * @param item Item that you want to check if it has an enchantment.
     * @param enchantment The enchantment you want to check if the item has.
     * @return True if the item has the enchantment / False if it doesn't have the enchantment.
     */
    public boolean hasEnchantment(ItemStack item, CEnchantments enchantment) {
        return enchantmentBookSettings.hasEnchantment(item, enchantment.getEnchantment());
    }

    public CEBook getRandomEnchantmentBook(Category category) {
        try {
            List<CEnchantment> enchantments = category.getEnabledEnchantments();
            CEnchantment enchantment = enchantments.get(random.nextInt(enchantments.size()));

            return new CEBook(enchantment, randomLevel(enchantment, category), 1, category);
        } catch (Exception e) {
            plugin.getLogger().info("The category " + category.getName() + " has no enchantments."
            + " Please add enchantments to the category in the Enchantments.yml. If you do not wish to have the category feel free to delete it from the Config.yml.");
            return null;
        }
    }

    /**
     * Get all the current registered enchantments.
     * @return A list of all the registered enchantments in the plugin.
     */
    public List<CEnchantment> getRegisteredEnchantments() {
        return new ArrayList<>(enchantmentBookSettings.getRegisteredEnchantments());
    }

    /**
     * Get a CEnchantment enchantment from the name.
     * @param enchantmentString The name of the enchantment.
     * @return The enchantment as a CEnchantment but if not found will be null.
     */
    public CEnchantment getEnchantmentFromName(String enchantmentString) {
        enchantmentString = methods.stripString(enchantmentString);

        for (CEnchantment enchantment : enchantmentBookSettings.getRegisteredEnchantments()) {
            if (methods.stripString(enchantment.getName()).equalsIgnoreCase(enchantmentString) ||
            methods.stripString(enchantment.getCustomName()).equalsIgnoreCase(enchantmentString) ||
            enchantment.getCustomName().replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or]| |_)", "")
                    .equalsIgnoreCase(enchantmentString.replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or]| |_)", ""))) return enchantment;
        }

        return null;
    }

    /**
     * Register a new enchantment into the plugin.
     * @param enchantment The enchantment you wish to register.
     */
    public void registerEnchantment(CEnchantment enchantment) {
        enchantmentBookSettings.getRegisteredEnchantments().add(enchantment);
    }

    /**
     * Unregister an enchantment that is registered into plugin.
     * @param enchantment The enchantment you wish to unregister.
     */
    public void unregisterEnchantment(CEnchantment enchantment) {
        enchantmentBookSettings.getRegisteredEnchantments().remove(enchantment);
    }

    /**
     * @param item Item you want to add the enchantment to.
     * @param enchantment Enchantment you want added.
     * @param level Tier of the enchantment.
     * @return The item with the enchantment on it.
     */
    public ItemStack addEnchantment(ItemStack item, CEnchantment enchantment, int level) {
        Map<CEnchantment, Integer> enchantments = new HashMap<>();

        enchantments.put(enchantment, level);
        return addEnchantments(item, enchantments);
    }

    public ItemStack addEnchantments(ItemStack itemStack, Map<CEnchantment, Integer> enchantments) {
        ItemStack item = itemStack.clone();
        for (Entry<CEnchantment, Integer> entry : enchantments.entrySet()) {
            CEnchantment enchantment = entry.getKey();
            int level = entry.getValue();

            if (enchantmentBookSettings.hasEnchantment(item, enchantment)) enchantmentBookSettings.removeEnchantment(item, enchantment);
            
            String loreString = enchantment.getCustomName() + " " + NumberUtils.convertLevelString(level);
            ItemMeta meta = item.getItemMeta();
            List<Component> lore = meta.lore();

            if (lore == null) lore = new ArrayList<>();

            lore.add(ColorUtils.legacyTranslateColourCodes(loreString));

            meta.lore(lore);

            Gson gson = new Gson();

            String data;
            Enchant enchantData;

            data = itemStack.getItemMeta().getPersistentDataContainer().get(DataKeys.ENCHANTMENTS.getKey(), PersistentDataType.STRING);

            enchantData =  data != null ? gson.fromJson(data, Enchant.class) : new Enchant(new HashMap<>());

            for (Entry<CEnchantment, Integer> x : enchantments.entrySet()) {
                enchantData.addEnchantment(x.getKey().getName(), x.getValue());
            }

            meta.getPersistentDataContainer().set(DataKeys.ENCHANTMENTS.getKey(), PersistentDataType.STRING, gson.toJson(enchantData));

            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Force an update of a players armor potion effects.
     * @param player The player you are updating the effects of.
     */
    public void updatePlayerEffects(Player player) {
        if (player != null) {
            for (CEnchantments ench : getEnchantmentPotions().keySet()) {
                for (ItemStack armor : player.getEquipment().getArmorContents()) {
                    if (ench.isActivated() && enchantmentBookSettings.hasEnchantment(armor, ench.getEnchantment())) {
                        Map<PotionEffectType, Integer> effects = getUpdatedEffects(player, armor, new ItemStack(Material.AIR), ench);
                        methods.checkPotions(effects, player);
                    }
                }
            }
        }
    }

    /**
     * @param player The player you are adding it to.
     * @param includedItem Include an item.
     * @param excludedItem Exclude an item.
     * @param enchantment The enchantment you want the max level effects from.
     * @return The list of all the max potion effects based on all the armor on the player.
     */
    public Map<PotionEffectType, Integer> getUpdatedEffects(Player player, ItemStack includedItem, ItemStack excludedItem, CEnchantments enchantment) {
        HashMap<PotionEffectType, Integer> effects = new HashMap<>();
        List<ItemStack> items = new ArrayList<>(Arrays.asList(player.getEquipment().getArmorContents()));

        if (includedItem == null) includedItem = new ItemStack(Material.AIR);

        if (excludedItem == null) excludedItem = new ItemStack(Material.AIR);

        if (excludedItem.isSimilar(includedItem)) excludedItem = new ItemStack(Material.AIR);

        items.add(includedItem);
        Map<CEnchantments, HashMap<PotionEffectType, Integer>> armorEffects = getEnchantmentPotions();

        for (Entry<CEnchantments, HashMap<PotionEffectType, Integer>> enchantments : armorEffects.entrySet()) {
            if (enchantments.getKey().isActivated()) {
                for (ItemStack armor : items) {
                    if (armor != null && !armor.isSimilar(excludedItem) && enchantmentBookSettings.hasEnchantment(armor, enchantments.getKey().getEnchantment())) {
                        int level = enchantmentBookSettings.getLevel(armor, enchantments.getKey().getEnchantment());

                        if (!useUnsafeEnchantments && level > enchantments.getKey().getEnchantment().getMaxLevel()) level = enchantments.getKey().getEnchantment().getMaxLevel();

                        for (PotionEffectType type : enchantments.getValue().keySet()) {
                            if (enchantments.getValue().containsKey(type)) {
                                if (effects.containsKey(type)) {
                                    int updated = effects.get(type);

                                    if (updated < (level + enchantments.getValue().get(type))) effects.put(type, level + enchantments.getValue().get(type));
                                } else {
                                    effects.put(type, level + enchantments.getValue().get(type));
                                }
                            }
                        }
                    }
                }
            }
        }

        for (PotionEffectType type : armorEffects.get(enchantment).keySet()) {
            if (!effects.containsKey(type)) effects.put(type, -1);
        }

        return effects;
    }

    /**
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
        enchants.get(CEnchantments.DRUNK).put(PotionEffectType.SLOW, 0);

        enchants.put(CEnchantments.HULK, new HashMap<>());
        enchants.get(CEnchantments.HULK).put(PotionEffectType.INCREASE_DAMAGE, -1);
        enchants.get(CEnchantments.HULK).put(PotionEffectType.DAMAGE_RESISTANCE, -1);
        enchants.get(CEnchantments.HULK).put(PotionEffectType.SLOW, 0);

        enchants.put(CEnchantments.VALOR, new HashMap<>());
        enchants.get(CEnchantments.VALOR).put(PotionEffectType.DAMAGE_RESISTANCE, -1);

        enchants.put(CEnchantments.OVERLOAD, new HashMap<>());
        enchants.get(CEnchantments.OVERLOAD).put(PotionEffectType.HEALTH_BOOST, 0);

        enchants.put(CEnchantments.NINJA, new HashMap<>());
        enchants.get(CEnchantments.NINJA).put(PotionEffectType.HEALTH_BOOST, -1);
        enchants.get(CEnchantments.NINJA).put(PotionEffectType.SPEED, -1);

        enchants.put(CEnchantments.INSOMNIA, new HashMap<>());
        enchants.get(CEnchantments.INSOMNIA).put(PotionEffectType.CONFUSION, -1);
        enchants.get(CEnchantments.INSOMNIA).put(PotionEffectType.SLOW_DIGGING, -1);
        enchants.get(CEnchantments.INSOMNIA).put(PotionEffectType.SLOW, 0);

        enchants.put(CEnchantments.ANTIGRAVITY, new HashMap<>());
        enchants.get(CEnchantments.ANTIGRAVITY).put(PotionEffectType.JUMP, 1);

        enchants.put(CEnchantments.GEARS, new HashMap<>());
        enchants.get(CEnchantments.GEARS).put(PotionEffectType.SPEED, -1);

        enchants.put(CEnchantments.SPRINGS, new HashMap<>());
        enchants.get(CEnchantments.SPRINGS).put(PotionEffectType.JUMP, -1);

        enchants.put(CEnchantments.CYBORG, new HashMap<>());
        enchants.get(CEnchantments.CYBORG).put(PotionEffectType.SPEED, -1);
        enchants.get(CEnchantments.CYBORG).put(PotionEffectType.INCREASE_DAMAGE, 0);
        enchants.get(CEnchantments.CYBORG).put(PotionEffectType.JUMP, 0);

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

    public boolean canAddEnchantment(Player player, ItemStack item) {
        if (maxEnchantmentCheck && !player.hasPermission("crazyenchantments.bypass.limit")) return enchantmentBookSettings.getEnchantmentAmount(item, checkVanillaLimit) < getPlayerMaxEnchantments(player);

        return true;
    }

    /**
     * @param item Item you are getting the level from.
     * @param enchant The enchantment you want the level from.
     * @return The level the enchantment has.
     */
    public int getLevel(ItemStack item, CEnchantments enchant) {
        return enchantmentBookSettings.getLevel(item, enchant.getEnchantment());
    }

    public int randomLevel(CEnchantment enchantment, Category category) {
        int enchantmentMax = enchantment.getMaxLevel(); // Max set by the enchantment.
        int randomLevel = 1 + random.nextInt(enchantmentMax);

        if (category.useMaxLevel()) {
            if (randomLevel > category.getMaxLevel()) randomLevel = 1 + random.nextInt(category.getMaxLevel());

            if (randomLevel < category.getMinLevel()) randomLevel = category.getMinLevel();

            if (randomLevel > enchantmentMax) randomLevel = enchantmentMax;
        }

        return randomLevel;
    }

    /**
     * @return The block list for blast.
     */
    public List<Material> getBlockList() {
        return blockList;
    }

    /**
     * @return If the blast enchantment drops blocks.
     */
    public boolean isDropBlocksBlast() {
        return dropBlocksBlast;
    }

    /**
     * @param dropBlocksBlast If the blast enchantment drops blocks.
     */
    public void setDropBlocksBlast(boolean dropBlocksBlast) {
        this.dropBlocksBlast = dropBlocksBlast;
    }

    /**
     * @return The max rage stack level.
     */
    public int getRageMaxLevel() {
        return rageMaxLevel;
    }

    /**
     * Check if players lose their current rage stack on damage.
     * @return True if they do and false if not.
     */
    public boolean isBreakRageOnDamageOn() {
        return breakRageOnDamage;
    }

    /**
     * @return True if a boss bar will be used to display rage notifications.
     */
    public boolean useRageBossBar() {
        return useRageBossBar;
    }

    /**
     * Check if players can enchant a stack of items with an enchantment book.
     */
    public boolean enchantStackedItems() {
        return enchantStackedItems;
    }

    private void addCEPlayer(CEPlayer player) {
        players.add(player);
    }

    private void removeCEPlayer(CEPlayer player) {
        players.remove(player);
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
                    Enchantment enchantment = methods.getEnchantment(option.split(":")[0]);
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

            nbtItem.setInteger("random-number", random.nextInt(Integer.MAX_VALUE));
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

        if (newItemString.length() > 0) itemString = newItemString.substring(0, newItemString.length() - 2);
        return itemString;
    }

    public int pickLevel(int min, int max) {
        return min + random.nextInt((max + 1) - min);
    }
}