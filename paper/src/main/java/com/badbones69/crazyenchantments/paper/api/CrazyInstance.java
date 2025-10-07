package com.badbones69.crazyenchantments.paper.api;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsMenu;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.enums.ShopOption;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.Enchant;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.EnchantedBook;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEOption;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.api.utils.ConfigUtils;
import com.badbones69.crazyenchantments.paper.api.utils.EnchantUtils;
import com.badbones69.crazyenchantments.paper.api.utils.NumberUtils;
import com.badbones69.crazyenchantments.paper.config.ConfigOptions;
import com.google.common.collect.Lists;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class CrazyInstance {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Server server = this.plugin.getServer();

    private final PluginManager pluginManager = this.server.getPluginManager();

    private final PaperFileManager fileManager = this.plugin.getFileManager();

    private final ConfigOptions options = this.plugin.getOptions();

    private final FusionPaper fusion = this.plugin.getFusion();

    private final Methods methods = this.plugin.getStarter().getMethods();

    private final Path path = this.plugin.getDataPath();

    private final List<CEnchantment> registeredEnchantments = Lists.newArrayList();
    private final Map<ShopOption, CEOption> shopOptions = new HashMap<>();

    private final List<GKitz> gkitz = new ArrayList<>();

    public void init() {
        final YamlConfiguration config = FileKeys.config.getConfiguration();

        loadShopOptions(config); // load shop options

        loadExamples(); // load examples
    }

    public void reload() {
        this.fusion.reload(); // reload fusion api

        this.fileManager.refresh(false).saveFile(this.path.resolve("Data.yml")); // refresh files

        final YamlConfiguration config = FileKeys.config.getConfiguration();

        this.options.init(config); // re-map to objects

        this.kitsManager.init(); // update kits

        loadShopOptions(config); // load shop options

        loadExamples(); // load examples
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

    public final Map<ShopOption, CEOption> getShopOptions() {
        return Collections.unmodifiableMap(this.shopOptions);
    }

    public void loadExamples() {
        if (this.options.isUpdateExamplesFolder()) {
            try (final Stream<Path> values = Files.walk(this.path.resolve("examples"))) {
                values.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        this.fusion.log("info", "Successfully deleted path {}, re-generating the examples later.", path);

                        Files.delete(path);
                    } catch (final IOException exception) {
                        this.fusion.log("warn", "Failed to delete {} in loop, Reason: {}", path, exception.getMessage());
                    }
                });
            } catch (final Exception exception) {
                this.fusion.log("warn", "Failed to delete {}, Reason: {}", this.path.resolve("examples"), exception.getMessage());
            }

            List.of(
                    "config.yml",
                    "BlockList.yml",
                    "Data.yml",
                    "Enchantment-Types.yml",
                    "Enchantments.yml",
                    "GKitz.yml",
                    "HeadMap.yml",
                    "Messages.yml",
                    "Tinker.yml"
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

            newLore.add(ColorUtils.legacyTranslateColourCodes(loreString));

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
            lore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).contains(ColorUtils.stripStringColour(enchant.getCustomName())));

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
                lore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).contains(ColorUtils.stripStringColour(enchant.getCustomName())));

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

    public @NotNull final List<CEnchantment> getRegisteredEnchantments() {
        return Collections.unmodifiableList(this.registeredEnchantments);
    }

    private List<ItemStack> getInfoGKit(@NotNull final List<String> itemStrings) {
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

    public String getNewItemString(@NotNull String itemString) {
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

    public @NotNull final List<GKitz> getGKitz() {
        return Collections.unmodifiableList(this.gkitz);
    }

    public @NotNull final ItemStack getEnchantmentBookItem() {
        return this.options.getEnchantBook().build();
    }

    public @NotNull final ItemBuilder getEnchantmentBookBuilder() {
        return new ItemBuilder(this.options.getEnchantBook());
    }
}