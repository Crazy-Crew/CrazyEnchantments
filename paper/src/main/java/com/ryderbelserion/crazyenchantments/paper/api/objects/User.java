package com.ryderbelserion.crazyenchantments.paper.api.objects;

import com.ryderbelserion.crazyenchantments.paper.CrazyEnchantments;
import com.ryderbelserion.crazyenchantments.paper.api.enums.Files;
import com.ryderbelserion.fusion.core.files.FileManager;
import com.ryderbelserion.fusion.core.files.types.YamlCustomFile;
import com.ryderbelserion.fusion.kyori.components.KyoriLogger;
import net.kyori.adventure.audience.Audience;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.Locale;

public class User {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final FileManager fileManager = this.plugin.getFileManager();

    private final Path path = this.plugin.getDataPath().resolve("locale");

    private final KyoriLogger logger = this.plugin.getPaper().getLogger();

    private final Audience audience;

    public User(@NotNull final Audience audience) {
        this.audience = audience;
    }

    public CommentedConfigurationNode locale() {
        final YamlCustomFile customFile = this.fileManager.getYamlFile(this.path.resolve(getLocale()));

        if (customFile == null) {
            return Files.messages.getConfig();
        }

        return customFile.getConfiguration();
    }

    private String locale = "en-US";

    public void setLocale(@NotNull final Locale locale) {
        final String country = locale.getCountry();
        final String language = locale.getLanguage();

        this.locale = language + "-" + country + ".yml";

        this.logger.warn("Country: {}, Language: {}", country, language);
    }

    public @NotNull final Audience getAudience() {
        return this.audience;
    }

    public @NotNull final String getLocale() {
        return this.locale;
    }
}