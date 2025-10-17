package com.badbones69.crazyenchantments.paper.api;

import com.badbones69.crazyenchantments.CrazyPlugin;
import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.objects.CustomHead;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantType;
import com.badbones69.crazyenchantments.paper.managers.PlayerManager;
import com.badbones69.crazyenchantments.paper.managers.currency.enums.Currency;
import com.badbones69.crazyenchantments.paper.api.enums.ShopOption;
import com.badbones69.crazyenchantments.paper.api.enums.DataKeys;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.Enchant;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantedBook;
import com.badbones69.crazyenchantments.paper.api.enums.files.FileKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEOption;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.utils.ConfigUtils;
import com.badbones69.crazyenchantments.paper.api.utils.EnchantUtils;
import com.badbones69.crazyenchantments.paper.api.utils.NumberUtils;
import com.badbones69.crazyenchantments.paper.managers.TinkerManager;
import com.badbones69.crazyenchantments.paper.managers.configs.ConfigManager;
import com.badbones69.crazyenchantments.paper.managers.CategoryManager;
import com.badbones69.crazyenchantments.paper.managers.currency.CurrencyManager;
import com.badbones69.crazyenchantments.paper.managers.items.ItemManager;
import com.badbones69.crazyenchantments.paper.managers.KitsManager;
import com.badbones69.crazyenchantments.paper.support.mods.Dependencies;
import com.badbones69.crazyenchantments.paper.support.mods.vanish.GenericVanishMod;
import com.badbones69.crazyenchantments.enums.Files;
import com.ryderbelserion.fusion.core.api.support.ModManager;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import com.ryderbelserion.fusion.paper.utils.ItemUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazyenchantments.constants.MessageKeys;
import us.crazycrew.crazyenchantments.enums.Mode;
import us.crazycrew.crazyenchantments.exceptions.CrazyException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class CrazyInstance extends CrazyPlugin {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);
    private final PaperFileManager fileManager = this.plugin.getFileManager();
    private final ConfigManager options = this.plugin.getConfigManager();
    private final Server server = this.plugin.getServer();
    private final Path path = this.plugin.getDataPath();

    private final List<EnchantType> registeredEnchantmentTypes = new ArrayList<>();
    private final List<CEnchantment> registeredEnchantments = new ArrayList<>();
    private final Map<String, Map<String, CustomHead>> heads = new HashMap<>(); // category, ( head_name, custom_head[base64, chance] )
    private final Map<ShopOption, CEOption> shopOptions = new HashMap<>();
    private final List<String> blocks = new ArrayList<>();

    private final ModManager modManager;
    private final FusionPaper fusion;

    private CurrencyManager currencyManager;
    private CategoryManager categoryManager;
    private PlayerManager playerManager;
    private TinkerManager tinkerManager;
    private ItemManager itemManager;
    private KitsManager kitsManager;

    public CrazyInstance(@NotNull final FusionPaper fusion) {
        super(fusion);

        this.modManager = fusion.getModManager();
        this.fusion = fusion;
    }

    //private SuperiorSkyBlockSupport skyBlockSupport;
    //private PluginSupport pluginSupport;
    //private VaultSupport vaultSupport;

    @Override
    public void init() {
        this.modManager.addMod(Dependencies.generic_vanish, new GenericVanishMod());

        final List<String> blocks = ConfigUtils.getStringList(Files.blocks.getJsonConfiguration(), "blocks").stream().filter(String::isEmpty).toList();

        for (final String block : blocks) {
            final ItemType itemType = ItemUtils.getItemType(block);

            if (itemType == null) {
                this.fusion.log("warn", "Failed to fetch ItemType from {}", block);

                continue;
            }

            this.blocks.add(block);
        }

        //final BasicConfigurationNode root = Files.heads.getJsonConfiguration();

        /*final YamlConfiguration customHeads = FileKeys.head_map.getPaperConfiguration();

        Optional.ofNullable(customHeads.getConfigurationSection("HeadOdds")).ifPresentOrElse(section -> {
            for (final String value : section.getKeys(false)) {
                final double chance = section.getDouble(value);

                this.heads.put(value, new CustomHead(value, chance));
            }
        }, () -> {
            this.fusion.log("warn", "Failed to find `HeadOdds` section in HeadMap.yml");
        });*/

        final YamlConfiguration enchantmentTypes = FileKeys.enchantment_types.getPaperConfiguration();

        Optional.ofNullable(enchantmentTypes.getConfigurationSection("Types")).ifPresentOrElse(section -> {
            for (final String type : section.getKeys(false)) {
                this.registeredEnchantmentTypes.add(new EnchantType(type));
            }
        }, () -> {
            throw new CrazyException("Failed to find `Types` section in Enchantment-Types.yml");
        });

        final YamlConfiguration config = FileKeys.config.getPaperConfiguration();

        this.currencyManager = new CurrencyManager();
        this.categoryManager = new CategoryManager();
        this.tinkerManager = new TinkerManager();
        this.itemManager = new ItemManager();
        this.kitsManager = new KitsManager();

        this.playerManager = new PlayerManager(); // this requires KitsManager, so it needs to be created after the KitsManager!

        this.currencyManager.init(); // update currencies

        this.categoryManager.init(); // update categories

        this.itemManager.init(); // update items

        this.kitsManager.init(); // update kits

        this.playerManager.init(); // this needs to be loaded after the kits!

        loadShopOptions(config); // load shop options

        loadExamples(); // load examples

        //if (PluginSupport.SupportedPlugins.SUPERIORSKYBLOCK.isPluginLoaded()) {
        //    this.skyBlockSupport = new SuperiorSkyBlockSupport();
        //}

        // Plugin Support.
        //this.pluginSupport = new PluginSupport();
        //this.pluginSupport.initializeWorldGuard();
    }

    @Override
    public void reload(@Nullable final Audience audience) {
        this.fusion.reload(); // reload fusion api

        this.fileManager.refresh(false).saveFile(this.path.resolve("Data.yml")); // refresh files

        // clear, and re-fill list with new blocks.
        this.blocks.clear();

        final List<String> blocks = ConfigUtils.getStringList(Files.blocks.getJsonConfiguration(), "blocks").stream().filter(String::isEmpty).toList();

        for (final String block : blocks) {
            final ItemType itemType = ItemUtils.getItemType(block);

            if (itemType == null) {
                this.fusion.log("warn", "Failed to fetch ItemType from {}", block);

                continue;
            }

            this.blocks.add(block);
        }

        this.registeredEnchantmentTypes.clear();

        final YamlConfiguration config = FileKeys.config.getPaperConfiguration();

        this.options.init(config); // re-map to objects

        this.currencyManager.init(); // update currencies

        this.categoryManager.init(); // update categories

        this.itemManager.reloadItems(); // reload items

        this.kitsManager.init(); // update kits

        for (final Player player : this.server.getOnlinePlayers()) {
            this.playerManager.backupPlayer(player);
        }

        loadShopOptions(config); // load shop options

        loadExamples(); // load examples

        if (audience != null) {
            getUserRegistry().getUser(audience).sendMessage(MessageKeys.config_reload);
        }
    }

    @Override
    public void broadcast(@NotNull final Component component, @NotNull final String permission) {
        if (permission.isEmpty()) {
            this.server.broadcast(component);

            return;
        }

        this.server.broadcast(component, permission);
    }

    @Override
    public final boolean hasPermission(@NotNull final Audience audience, @NotNull final String permission) {
        final CommandSender sender = (CommandSender) audience;

        return sender.hasPermission(permission);
    }

    @Override
    public final boolean isConsoleSender(@NotNull final Audience audience) {
        return audience instanceof ConsoleCommandSender;
    }

    @Override
    public @NotNull final String getMessageType() {
        return this.options.getMessageAction();
    }

    @Override
    public @NotNull final String getPrefix() {
        return this.options.getPrefix();
    }

    @Override
    public void registerPermission(@NotNull final Mode mode, @NotNull final String parent, @NotNull final String description, @NotNull final Map<String, Boolean> children) {
        PermissionDefault permissionDefault;

        switch (mode) {
            case NOT_OP -> permissionDefault = PermissionDefault.NOT_OP;
            case TRUE -> permissionDefault = PermissionDefault.TRUE;
            case FALSE -> permissionDefault = PermissionDefault.FALSE;
            default -> permissionDefault = PermissionDefault.OP;
        }

        final PluginManager pluginManager = this.server.getPluginManager();

        if (pluginManager.getPermission(parent) != null) return;

        final Permission permission = new Permission(
                parent,
                description,
                permissionDefault,
                children
        );

        pluginManager.addPermission(permission);
    }

    public void loadShopOptions(final YamlConfiguration config) {
        this.shopOptions.clear();

        final ConfigurationSection section = config.getConfigurationSection("Settings");

        if (section == null) {
            this.fusion.log("warn", "Failed to find the Settings configuration section in config.yml");

            return;
        }

        for (final ShopOption option : ShopOption.values()) {
            ConfigurationSection itemNode = section.getConfigurationSection(option.getPath());

            if (itemNode == null) {
                this.fusion.log("warn", "Failed to find {} in the config.yml", option.getPath());

                continue;
            }

            if (option == ShopOption.SUCCESS_DUST || option == ShopOption.DESTROY_DUST) {
                final ConfigurationSection dust = section.getConfigurationSection("Dust.%s".formatted(option.getPath()));

                if (dust != null) {
                    itemNode = dust;
                }
            }

            final ConfigurationSection costNode = section.getConfigurationSection("Costs.%s".formatted(option.getPath()));

            if (costNode == null) {
                this.fusion.log("warn", "Failed to find {} in the config.yml", option.getPath());

                return;
            }

            addShopOption(option, itemNode, costNode, option.getNamePath(), option.getLorePath());
        }
    }

    public void addShopOption(final ShopOption shopOption, final ConfigurationSection itemNode, final ConfigurationSection costNode, final String namePath, final String lorePath) {
        try {
            final CEOption option = new CEOption(
                    new ItemBuilder().setMaterial(itemNode.getString("Item", "CHEST")).setName(itemNode.getString(namePath, shopOption.getDefaultName()))
                            .setLore(ConfigUtils.getStringList(itemNode, shopOption.getDefaultLore(), lorePath))
                            .setPlayerName(itemNode.getString("Player", ""))
                            .setGlow(itemNode.getBoolean("Glowing", false)),
                    itemNode.getInt("Slot", -1)-1,
                    itemNode.getBoolean("InGUI", true),
                    costNode.getInt("Cost", 100),
                    Currency.getCurrency(costNode.getString("Currency", "XP_LEVEL"))
            );

            this.shopOptions.put(shopOption, option);
        } catch (final Exception exception) {
            this.fusion.log("warn", "The option {} has failed to load.", shopOption.getPath(), exception);
        }
    }

    public @NotNull final Map<ShopOption, CEOption> getShopOptions() {
        return Collections.unmodifiableMap(this.shopOptions);
    }

    public final boolean hasBlock(@NotNull final String itemType) {
        return this.blocks.contains(itemType);
    }

    public @NotNull final List<String> getBlocks() {
        return Collections.unmodifiableList(this.blocks);
    }

    public void loadExamples() {
        if (this.options.isUpdateExamplesFolder()) {
            try (final Stream<Path> values = java.nio.file.Files.walk(this.path.resolve("examples"))) {
                values.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        this.fusion.log("info", "Successfully deleted path {}, re-generating the examples later.", path);

                        java.nio.file.Files.delete(path);
                    } catch (final IOException exception) {
                        this.fusion.log("warn", "Failed to delete {} in loop, Reason: {}", path, exception);
                    }
                });
            } catch (final Exception exception) {
                this.fusion.log("warn", "Failed to delete {}, Reason: {}", this.path.resolve("examples"), exception);
            }

            List.of( //todo() this throws an npe
                    "config.yml",
                    "Data.yml",
                    "Enchantment-Types.yml",
                    "Enchantments.yml",
                    "GKitz.yml",
                    "Messages.yml",
                    "Tinker.yml",

                    "blocks.json",
                    "heads.json"
            ).forEach(file -> this.fileManager.extractFile(this.path.resolve("examples").resolve(file)));
        }
    }

    public @Nullable final CEBook getBook(@NotNull final ItemStack book) {
        final PersistentDataContainerView view = book.getPersistentDataContainer();

        if (!view.has(DataKeys.stored_enchantments.getNamespacedKey())) return null;

        EnchantedBook data = Methods.getGson().fromJson(view.get(DataKeys.stored_enchantments.getNamespacedKey(), PersistentDataType.STRING), EnchantedBook.class);

        CEnchantment enchantment = null;

        for (final CEnchantment enchant : getRegisteredEnchantments()) {
            if (enchant.getName().equalsIgnoreCase(data.getName())) {
                enchantment = enchant;

                break;
            }
        }

        return new CEBook(enchantment, data.getLevel(), book.getAmount()).setSuccessRate(data.getSuccessChance()).setDestroyRate(data.getDestroyChance());
    }

    public int getLevel(@NotNull final ItemStack item, @NotNull final CEnchantment enchant) {
        final String data = item.getPersistentDataContainer().get(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING);

        int level = data == null ? 0 : Methods.getGson().fromJson(data, Enchant.class).getLevel(enchant.getName());

        if (!this.options.isUseUnsafeEnchantments() && level > enchant.getMaxLevel()) level = enchant.getMaxLevel();

        return level;
    }

    public @Nullable final ItemStack getScrambledBook(@NotNull final ItemStack book) {
        final PersistentDataContainerView view = book.getPersistentDataContainer();

        final EnchantedBook data = Methods.getGson().fromJson(view.get(DataKeys.stored_enchantments.getNamespacedKey(), PersistentDataType.STRING), EnchantedBook.class);

        CEnchantment enchantment = null;

        int bookLevel = 0;

        for (final CEnchantment enchantment1 : getRegisteredEnchantments()) {
            if (!enchantment1.getName().equalsIgnoreCase(data.getName())) continue;

            enchantment = enchantment1;

            bookLevel = data.getLevel();
        }

        if (enchantment == null) return null;

        return new CEBook(enchantment, bookLevel, EnchantUtils.getHighestEnchantmentCategory(enchantment)).buildBook();
    }

    public final boolean isEnchantmentBook(@NotNull final ItemStack book) {
        if (book.isEmpty()) return false;

        final PersistentDataContainerView view = book.getPersistentDataContainer();

        if (!view.has(DataKeys.stored_enchantments.getNamespacedKey())) return false;

        final String dataString = view.get(DataKeys.stored_enchantments.getNamespacedKey(), PersistentDataType.STRING);
        final EnchantedBook data = Methods.getGson().fromJson(dataString, EnchantedBook.class);

        for (final CEnchantment enchantment : getRegisteredEnchantments()) {
            if (enchantment.getName().equalsIgnoreCase(data.getName())) return true;
        }

        return false;
    }

    public final int getEnchantmentAmount(@NotNull final ItemStack item, final boolean includeVanillaEnchantments) {
        int amount = getEnchantments(item).size();

        if (includeVanillaEnchantments && item.hasData(DataComponentTypes.ENCHANTMENTS)) {
            amount += item.getEnchantments().size();
        }

        return amount;
    }

    public @NotNull final List<CEnchantment> getEnchantmentsOnItem(@NotNull final ItemStack item) {
        return new ArrayList<>(getEnchantments(item).keySet());
    }

    public @NotNull final Map<CEnchantment, Integer> getEnchantments(@Nullable final ItemStack item) {
        if (item == null) return Collections.emptyMap();

        final PersistentDataContainerView view = item.getPersistentDataContainer();

        final Map<CEnchantment, Integer> enchantments = new HashMap<>();

        final String data = view.get(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING);

        if (data == null) return Collections.emptyMap();

        final Enchant enchants = Methods.getGson().fromJson(data, Enchant.class);

        if (enchants.isEmpty()) return Collections.emptyMap();

        for (final CEnchantment enchantment : getRegisteredEnchantments()) {
            if (!enchantment.isActivated()) continue;

            if (enchants.hasEnchantment(enchantment.getName())) enchantments.put(enchantment, enchants.getLevel(enchantment.getName()));
        }

        return enchantments;
    }

    public void addEnchantments(@NotNull final ItemStack itemStack, @NotNull final Map<CEnchantment, Integer> enchantments) {
        final Map<CEnchantment, Integer> currentEnchantments = getEnchantments(itemStack);

        removeEnchantments(itemStack, enchantments.keySet().stream().filter(currentEnchantments::containsKey).toList());

        String data = itemStack.getPersistentDataContainer().get(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING);
        final Enchant enchantData = data != null ? Methods.getGson().fromJson(data, Enchant.class) : new Enchant(new HashMap<>());

        final List<Component> lore = itemStack.lore();

        final List<Component> oldLore = lore != null ? lore : new ArrayList<>();
        List<Component> newLore = new ArrayList<>();

        for (Map.Entry<CEnchantment, Integer> entry : enchantments.entrySet()) {
            CEnchantment enchantment = entry.getKey();
            int level = entry.getValue();

            String loreString = enchantment.getCustomName() + " " + NumberUtils.convertLevelString(level);

            //newLore.add(ColorUtils.legacyTranslateColourCodes(loreString)); //todo() legacy trash

            for (Map.Entry<CEnchantment, Integer> x : enchantments.entrySet()) {
                enchantData.addEnchantment(x.getKey().getName(), x.getValue());
            }
        }

        newLore.addAll(oldLore);

        itemStack.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(newLore).build());

        itemStack.editPersistentDataContainer(container -> container.set(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING, Methods.getGson().toJson(enchantData)));
    }

    public @NotNull ItemStack removeEnchantment(@NotNull final ItemStack itemStack, @NotNull final CEnchantment enchant) {
        final PersistentDataContainerView view = itemStack.getPersistentDataContainer();

        final List<Component> lore = itemStack.lore();

        if (lore != null) {
            //lore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).contains(ColorUtils.stripStringColour(enchant.getCustomName()))); //todo() legacy trash

            itemStack.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(lore).build());
        }

        Enchant data;

        if (view.has(DataKeys.enchantments.getNamespacedKey())) {
            data = Methods.getGson().fromJson(view.get(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING), Enchant.class);
        } else {
            data = new Enchant(new HashMap<>());
        }

        data.removeEnchantment(enchant.getName());

        if (data.isEmpty() && view.has(DataKeys.enchantments.getNamespacedKey())) {
            itemStack.editPersistentDataContainer(container -> container.remove(DataKeys.enchantments.getNamespacedKey()));
        } else {
            itemStack.editPersistentDataContainer(container -> container.set(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING, Methods.getGson().toJson(data)));
        }

        return itemStack;
    }

    public void removeEnchantments(@NotNull final ItemStack itemStack, @NotNull final List<CEnchantment> enchants) {
        final PersistentDataContainerView view = itemStack.getPersistentDataContainer();

        final List<Component> lore = itemStack.lore();

        if (lore != null) {
            for (final CEnchantment enchant : enchants) {
                //lore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).contains(ColorUtils.stripStringColour(enchant.getCustomName()))); //todo() legacy trash

                itemStack.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(lore).build());
            }
        }

        Enchant data;

        if (view.has(DataKeys.enchantments.getNamespacedKey())) {
            data = Methods.getGson().fromJson(view.get(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING), Enchant.class);
        } else {
            data = new Enchant(new HashMap<>());
        }

        enchants.forEach(enchant -> data.removeEnchantment(enchant.getName()));

        if (data.isEmpty() && view.has(DataKeys.enchantments.getNamespacedKey())) {
            itemStack.editPersistentDataContainer(container -> container.remove(DataKeys.enchantments.getNamespacedKey()));
        } else {
            itemStack.editPersistentDataContainer(container -> container.set(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING, Methods.getGson().toJson(data)));
        }
    }

    public CEnchantment getEnchantmentFromName(@NotNull final String enchant) {
        CEnchantment value = null;

        for (final CEnchantment enchantment : this.registeredEnchantments) {
            if (enchantment.getName().equalsIgnoreCase(enchant)) {
                value = enchantment;

                break;
            }
        }

        return value;
    }

    public void registerEnchantment(@NotNull final CEnchantment enchantment) {
        this.registeredEnchantments.add(enchantment);
    }

    public void unregisterEnchantment(@NotNull final CEnchantment enchantment) {
        this.registeredEnchantments.remove(enchantment);
    }

    public void purgeEnchantments() {
        this.registeredEnchantments.clear();
    }

    public @NotNull final List<EnchantType> getRegisteredEnchantmentTypes() {
        return Collections.unmodifiableList(this.registeredEnchantmentTypes);
    }

    public @NotNull final List<CEnchantment> getRegisteredEnchantments() {
        return Collections.unmodifiableList(this.registeredEnchantments);
    }

    public @NotNull final ItemStack getEnchantmentBookItem() {
        return this.options.getEnchantBook().build();
    }

    public @NotNull final ItemBuilder getEnchantmentBookBuilder() {
        return new ItemBuilder(this.options.getEnchantBook());
    }

    public @NotNull final CurrencyManager getCurrencyManager() {
        return this.currencyManager;
    }

    public @NotNull final CategoryManager getCategoryManager() {
        return categoryManager;
    }

    public @NotNull final TinkerManager getTinkerManager() {
        return this.tinkerManager;
    }

    public @NotNull final PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public @NotNull final ItemManager getItemManager() {
        return this.itemManager;
    }

    public @NotNull final KitsManager getKitsManager() {
        return kitsManager;
    }
}