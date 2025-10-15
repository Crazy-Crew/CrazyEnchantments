package com.badbones69.crazyenchantments.paper.managers.currency.interfaces;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.managers.configs.ConfigManager;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public abstract class ICurrency {

    protected final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    protected final ConfigManager configManager = this.plugin.getConfigManager();

    protected final Server server = this.plugin.getServer();

    public abstract void add(@NotNull final Player player, final int amount);

    public abstract void minus(@NotNull final Player player, final int amount);

    public abstract boolean hasAmount(@NotNull final Player player, final int amount);

    public abstract double getAmount(@NotNull final Player player);

    public abstract void failed(@NotNull final Player player, @NotNull final Map<String, String> placeholders);

    public void failed(@NotNull final Player player) {
        failed(player, new HashMap<>());
    }

    public abstract boolean isEnabled();

    public ICurrency init() {
        return this;
    }
}