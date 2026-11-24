package com.badbones69.crazyenchantments.paper.api;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.adapters.PaperSenderAdapter;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.enums.ShopOption;
import com.badbones69.crazyenchantments.paper.api.objects.CEOption;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.ryderbelserion.common.api.enums.Files;
import com.badbones69.crazyenchantments.paper.support.SupportUtils;
import com.ryderbelserion.common.CrazyEnchantmentsPlugin;
import com.ryderbelserion.fusion.api.enums.Level;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class CrazyPlatform extends CrazyEnchantmentsPlugin<Component, Audience, FusionPaper> {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Server server = this.plugin.getServer();

    private EnchantmentBookSettings bookSettings;
    private PaperSenderAdapter senderAdapter;
    private SupportUtils support;

    public CrazyPlatform(@NonNull final FusionPaper fusion, @NonNull final Path path) {
        super(fusion, path);
    }

    private final Map<String, CEOption> shopOptions = new HashMap<>();

    private CrazyItems crazyItems;

    @Override
    public void init() {
        super.init();

        for (final Files key : Files.values()) {
            key.addFile();
        }

        this.senderAdapter = new PaperSenderAdapter();

        loadMessages();

        this.support = new SupportUtils();
        this.support.init();

        this.bookSettings = new EnchantmentBookSettings();
        this.bookSettings.load();

        loadShopOptions(Files.CONFIG.getConfiguration()); // load shop options

        this.crazyItems = new CrazyItems();
        this.crazyItems.init();

        loadExamples();
    }

    @Override
    public void reload() {
        super.reload();

        loadShopOptions(Files.CONFIG.getConfiguration()); // load shop options

        loadExamples();
    }

    @Override
    public @NonNull final PaperSenderAdapter getSenderAdapter() {
        return this.senderAdapter;
    }

    public void loadExamples() {
        final Path examples = this.path.resolve("examples");

        if (java.nio.file.Files.exists(examples)) {
            try (final Stream<Path> values = java.nio.file.Files.walk(examples)) {
                values.sorted(Comparator.reverseOrder()).forEach(path -> { // sorted in reverse order, to ensure the directories are empty first.
                    try {
                        this.fusion.log(Level.warn, "Successfully deleted path %s, re-generating the examples later.", path);

                        java.nio.file.Files.delete(path);
                    } catch (final IOException exception) {
                        this.fusion.log(Level.warn, "Failed to delete %s in loop.", exception, path);
                    }
                });
            } catch (final Exception exception) {
                this.fusion.log(Level.warn, "Failed to delete %s.", exception, examples);
            }
        }

        try {
            java.nio.file.Files.createDirectory(examples);
        } catch (IOException exception) {
            this.fusion.log(Level.warn, "Failed to create directory %s.", exception, examples);
        }

        List.of(
                "BlockList.yml",
                "config.yml",
                "Data.yml",
                "Enchantment-Types.yml",
                "Enchantments.yml",
                "GKitz.yml",
                "HeadMap.yml",
                "Messages.yml",
                "Tinker.yml"
        ).forEach(file -> this.fileManager.extractFile(file, examples.resolve(file)));
    }

    public void loadShopOptions(final CommentedConfigurationNode config) {
        //this.shopOptions.clear();

        final CommentedConfigurationNode section = config.node("Settings");

        if (section == null) {
            this.fusion.log(Level.warn, "Failed to find the Settings configuration section in config.yml");

            return;
        }

        for (final ShopOption option : ShopOption.values()) {
            final String path = option.getPath();

            CommentedConfigurationNode itemNode = section.node(path);

            if (itemNode == null) {
                this.fusion.log(Level.warn, "Failed to find %s in the config.yml", path);

                continue;
            }

            if (option == ShopOption.SUCCESS_DUST || option == ShopOption.DESTROY_DUST) {
                final CommentedConfigurationNode dust = section.node("Dust", path);

                if (dust != null) {
                    itemNode = dust;
                }
            }

            final CommentedConfigurationNode costNode = section.node("Costs", path);

            addShopOption(option, itemNode, costNode, option.getNamePath(), option.getLorePath());
        }
    }

    public void addShopOption(final ShopOption shopOption, final CommentedConfigurationNode itemNode, final CommentedConfigurationNode costNode, final String namePath, final String lorePath) {
        try {
            final CEOption option = new CEOption(
                    new ItemBuilder().setMaterial(itemNode.node("Item").getString("CHEST")).setName(itemNode.node(namePath).getString(shopOption.getDefaultName()))
                            .setLore(StringUtils.getStringList(itemNode.node(lorePath), shopOption.getDefaultLore()))
                            .setPlayerName(itemNode.node("Player").getString(""))
                            .setGlow(itemNode.node("Glowing").getBoolean(false)),
                    itemNode.node("Slot").getInt(-1)-1,
                    itemNode.node("InGUI").getBoolean(true),
                    costNode.node("Cost").getInt(100),
                    Currency.getCurrency(costNode.node("Currency").getString("XP_LEVEL"))
            );

            this.shopOptions.put(shopOption.getPath(), option);
        } catch (final Exception exception) {
            this.fusion.log(Level.warn, "The option %s has failed to load for reason %s.", shopOption.getPath(), exception.getMessage());
        }
    }

    public @NonNull final Optional<CEOption> getOption(@NonNull final String option) {
        return Optional.of(this.shopOptions.get(option));
    }

    public @NonNull final Map<String, CEOption> getShopOptions() {
        return Collections.unmodifiableMap(this.shopOptions);
    }

    public @NonNull final Optional<Player> getPlayer(@NonNull final String name) {
        return Optional.ofNullable(this.server.getPlayer(name));
    }

    public @NonNull final EnchantmentBookSettings getBookSettings() {
        return this.bookSettings;
    }

    public @NonNull final SupportUtils getSupport() {
        return this.support;
    }

    @Override
    public @NonNull final FusionPaper getFusion() {
        return this.fusion;
    }

    public @NonNull final CrazyItems getCrazyItems() {
        return this.crazyItems;
    }
}