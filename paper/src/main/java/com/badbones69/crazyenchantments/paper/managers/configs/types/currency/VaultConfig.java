package com.badbones69.crazyenchantments.paper.managers.configs.types.currency;

import com.badbones69.crazyenchantments.paper.managers.configs.interfaces.IConfig;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class VaultConfig extends IConfig {

    private final boolean isVaultEnabled;

    public VaultConfig(@NotNull final CommentedConfigurationNode node) {
        this.isVaultEnabled = node.node("enabled").getBoolean(true);
    }

    public final boolean isVaultEnabled() {
        return this.isVaultEnabled;
    }
}