package com.ryderbelserion.crazyenchantments.core.objects;

import com.ryderbelserion.crazyenchantments.api.interfaces.IUser;
import com.ryderbelserion.crazyenchantments.core.enums.Files;
import com.ryderbelserion.fusion.core.FusionProvider;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.files.interfaces.ICustomFile;
import com.ryderbelserion.fusion.files.types.configurate.YamlCustomFile;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

public class User implements IUser {

    private final FusionKyori fusion = (FusionKyori) FusionProvider.getInstance();

    private final FileManager fileManager = this.fusion.getFileManager();

    private final Path path = this.fusion.getDataPath();

    private final Audience audience;

    public User(@NotNull final Audience audience) {
        this.audience = audience;
    }

    @Override
    public CommentedConfigurationNode locale() {
        @NotNull final Optional<YamlCustomFile> customFile = this.fileManager.getYamlFile(this.path.resolve(String.format("%s.yml", getLocale())));

        return customFile.map(ICustomFile::getConfiguration).orElseGet(Files.messages::getConfig);

    }

    private String locale = "en-US";

    @Override
    public void setLocale(@NotNull final Locale locale) {
        final String country = locale.getCountry();
        final String language = locale.getLanguage();

        this.locale = String.format("%s-%s", language, country);

        this.fusion.log("warn", "Locale Debug: Country: {}, Language: {}", country, language);
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