package com.ryderbelserion.crazyenchantments.core.objects;

import com.ryderbelserion.crazyenchantments.api.interfaces.IUser;
import com.ryderbelserion.crazyenchantments.core.enums.Files;
import com.ryderbelserion.fusion.core.FusionProvider;
import com.ryderbelserion.fusion.core.api.FusionCore;
import com.ryderbelserion.fusion.core.api.interfaces.ILogger;
import com.ryderbelserion.fusion.core.files.FileManager;
import com.ryderbelserion.fusion.core.files.types.YamlCustomFile;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.Locale;

public class User implements IUser {

    private final FusionCore fusion = FusionProvider.get();

    private final FileManager fileManager = this.fusion.getFileManager();

    private final ILogger logger = this.fusion.getLogger();

    private final Path path = this.fusion.getPath();

    private final Audience audience;

    public User(@NotNull final Audience audience) {
        this.audience = audience;
    }

    @Override
    public CommentedConfigurationNode locale() {
        final YamlCustomFile customFile = this.fileManager.getYamlFile(this.path.resolve(String.format("%s.yml", getLocale())));

        if (customFile == null) {
            return Files.messages.getConfig();
        }

        return customFile.getConfiguration();
    }

    private String locale = "en-US";

    @Override
    public void setLocale(@NotNull final Locale locale) {
        final String country = locale.getCountry();
        final String language = locale.getLanguage();

        this.locale = String.format("%s-%s", language, country);

        this.logger.warn("Locale Debug: Country: {}, Language: {}", country, language);
    }

    @Override
    public @NotNull final Audience getAudience() {
        return this.audience;
    }

    @Override
    public @NotNull final String getLocale() {
        return this.locale;
    }
}