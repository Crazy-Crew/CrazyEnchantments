package com.badbones69.crazyenchantments.paper.api;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.enums.ShopOption;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEOption;
import com.badbones69.crazyenchantments.paper.api.utils.ConfigUtils;
import com.badbones69.crazyenchantments.paper.config.ConfigOptions;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class CrazyInstance {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final PaperFileManager fileManager = this.plugin.getFileManager();

    private final ConfigOptions options = this.plugin.getOptions();

    private final FusionPaper fusion = this.plugin.getFusion();

    private final Path path = this.plugin.getDataPath();

    private final Map<ShopOption, CEOption> shopOptions = new HashMap<>();

    public void init() {
        final YamlConfiguration config = FileKeys.config.getConfiguration();

        loadShopOptions(config); // load shop options

        loadExamples(); // load examples
    }

    public void reload() {
        final YamlConfiguration config = FileKeys.config.getConfiguration();

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
}