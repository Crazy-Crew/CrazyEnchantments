package com.badbones69.crazyenchantments.paper.controllers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarController {

    private final HashMap<UUID, BossBar> bossBars;
    private final CrazyEnchantments plugin;

    public BossBarController(CrazyEnchantments plugin) {
        this.bossBars = new HashMap<>();
        this.plugin = plugin;
    }

    /**
     *
     * @param player {@link Player}
     * @return true if the player currently has a boss bar.
     */
    public boolean hasBossBar(@NotNull final Player player) {
        return bossBars.containsKey(player.getUniqueId());
    }

    /**
     *
     * @param player the {@link Player} whose boss bar you want to get
     * @return the current boss bar or null.
     */
    public BossBar getBossBar(@NotNull final Player player) {
        return bossBars.get(player.getUniqueId());
    }

    private void createBossBars(@NotNull final Player player, @NotNull final Component displayText, final float progress) {
        if (hasBossBar(player)) return;
        BossBar bossBar = BossBar.bossBar(displayText, progress, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        bossBars.put(player.getUniqueId(), bossBar);
        player.showBossBar(bossBar);

        new FoliaScheduler(this.plugin, null, player) {
            @Override
            public void run() {
                removeBossBar(player);
            }
        }.runDelayed(600L);

    }

    /**
     * Updates the current boss bar or creates a
     * new one if the player does not have one yet
     * @param player {@link Player} whose boss bar you want to update
     * @param text the message that you want to be displayed by the boss bar
     * @param progress value between 0f and 1f of how much the progress bar should be filled
     */
    public void updateBossBar(@NotNull final Player player, @NotNull final Component text, final float progress) {
        if (!hasBossBar(player)) {
            createBossBars(player, text, progress);
        } else {
            bossBars.replace(player.getUniqueId(), getBossBar(player).name(text).progress(progress));
        }

        player.showBossBar(getBossBar(player));
    }

    /**
     * @see #updateBossBar(Player, Component, float)
     */
    public void updateBossBar(@NotNull final Player player, @NotNull final String text, final float progress) {
        updateBossBar(player, ColorUtils.legacyTranslateColourCodes(text), progress);
    }

    /**
     * Updates the boss bar repeatedly in order to make it appear smooth.
     * @param intervals amount of times that the
     * boss bar is updated to get to the final value
     * @see #updateBossBar
     */
    public void updateBossBarGradually(@NotNull final Player player, @NotNull final String text, final float progress, final int intervals) {
        Component newText = ColorUtils.legacyTranslateColourCodes(text);

        if (!hasBossBar(player)) {
            createBossBars(player, newText, progress);
        } else {
            BossBar bossBar = getBossBar(player).name(newText);

            float from = bossBar.progress();
            float difference = (progress - from)/intervals;

            if ((from != progress)) {
                for (int i = 0; i < intervals; ++i) {

                    from += difference;

                    if (from > 1 || from < 0) break;

                    bossBar.progress(from);

                }
            }

            bossBar.progress(progress);

            this.bossBars.replace(player.getUniqueId(), bossBar);
        }

        player.showBossBar(getBossBar(player));
    }

    /**
     * Removes the players boss bar
     * @param player the {@link Player} whose boss bar you wish to remove
     */
    public void removeBossBar(@NotNull final Player player) {
        if (!hasBossBar(player)) return;
        player.hideBossBar(getBossBar(player));

        this.bossBars.remove(player.getUniqueId());
    }

    /**
     * Deletes all boss bars.
     */
    public void removeAllBossBars() {
        if (bossBars.isEmpty()) return;

        for (Map.Entry<UUID, BossBar> entry : bossBars.entrySet()) {
            Player player = plugin.getServer().getPlayer(entry.getKey());

            assert player != null;

            player.hideBossBar(entry.getValue());
        }

        bossBars.clear();
    }
}
