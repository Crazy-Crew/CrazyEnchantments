package com.badbones69.crazyenchantments.paper.managers.configs.interfaces;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class IConfig {

    protected final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    protected final FusionPaper fusion = this.plugin.getFusion();

    public @NotNull final Optional<ConfigurationSection> getConfigurationSection(@NotNull final ConfigurationSection section, @NotNull final String path) {
        return Optional.ofNullable(section.getConfigurationSection(path));
    }

    public @NotNull final List<Component> asComponents(@Nullable final Audience player, @NotNull final List<String> lines) {
        final List<Component> components = new ArrayList<>();

        for (final String line : lines) {
            if (line.isEmpty()) {
                components.add(Component.text(line));

                continue;
            }

            components.add(this.fusion.parse(player == null ? Audience.empty() : player, line));
        }

        return components;
    }

    public @NotNull final Component asComponent(@Nullable final Audience player, @NotNull final String line) {
        return this.fusion.parse(player == null ? Audience.empty() : player, line);
    }
}