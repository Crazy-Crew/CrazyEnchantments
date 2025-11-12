package com.badbones69.crazyenchantments.paper.managers.currency.types;

import com.badbones69.crazyenchantments.paper.managers.currency.interfaces.ICurrency;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenchantments.constants.MessageKeys;

import java.util.Map;

public class ExpCurrency extends ICurrency {

    @Override
    public void add(@NotNull final Player player, final double amount) {
        player.setTotalExperience((int) (player.getTotalExperience() + amount));
    }

    @Override
    public void minus(@NotNull final Player player, final double amount) {
        player.setTotalExperience((int) (player.getTotalExperience() - amount));
    }

    @Override
    public final boolean hasAmount(@NotNull final Player player, final double amount) {
        return player.getTotalExperience() >= amount;
    }

    @Override
    public final double getAmount(@NotNull final Player player) {
        return player.getTotalExperience();
    }

    @Override
    public void failed(@NotNull final Player player, @NotNull final Map<String, String> placeholders) {
        this.userRegistry.getUser(player).sendMessage(MessageKeys.need_more_total_xp, placeholders);
    }

    @Override
    public final boolean isEnabled() {
        return true;
    }
}