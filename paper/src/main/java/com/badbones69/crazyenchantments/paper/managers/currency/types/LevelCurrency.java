package com.badbones69.crazyenchantments.paper.managers.currency.types;

import com.badbones69.crazyenchantments.paper.api.enums.v2.Messages;
import com.badbones69.crazyenchantments.paper.managers.currency.interfaces.ICurrency;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Map;

public class LevelCurrency extends ICurrency {

    @Override
    public void add(@NotNull final Player player, final int amount) {
        player.setLevel(player.getLevel() + amount);
    }

    @Override
    public void minus(@NotNull final Player player, final int amount) {
        player.setLevel(player.getLevel() - amount);
    }

    @Override
    public final boolean hasAmount(@NotNull final Player player, final int amount) {
        return player.getLevel() >= amount;
    }

    @Override
    public final double getAmount(@NotNull final Player player) {
        return player.getLevel();
    }

    @Override
    public void failed(@NotNull final Player player, @NotNull final Map<String, String> placeholders) {
        Messages.NEED_MORE_XP_LEVELS.sendMessage(player, placeholders);
    }

    @Override
    public final boolean isEnabled() {
        return true;
    }
}