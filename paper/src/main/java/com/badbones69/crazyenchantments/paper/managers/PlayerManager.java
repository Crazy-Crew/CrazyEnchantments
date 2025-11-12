package com.badbones69.crazyenchantments.paper.managers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.files.FileKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEPlayer;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GkitCoolDown;
import com.badbones69.crazyenchantments.paper.managers.configs.ConfigManager;
import org.bukkit.Server;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;

public class PlayerManager {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();
    private final ConfigManager configManager = this.plugin.getConfigManager();
    private final KitsManager kitsManager = this.plugin.getKitsManager();
    private final Server server = this.plugin.getServer();

    public PlayerManager() {

    }

    private final Map<UUID, CEPlayer> players = new HashMap<>();

    public void init() {
        final boolean isResetMaxHealth = this.configManager.isResetMaxHealth();

        for (final Player player : this.server.getOnlinePlayers()) {
            if (isResetMaxHealth) {
                @Nullable final AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);

                if (attribute != null) {
                    attribute.setBaseValue(attribute.getBaseValue());
                }
            }

            loadPlayer(player);
        }
    }

    public void loadPlayer(@NotNull final Player player) {
        final YamlConfiguration configuration = FileKeys.data.getPaperConfiguration();

        final UUID uuid = player.getUniqueId();
        final String asString = uuid.toString();

        final List<GkitCoolDown> cooldowns = new ArrayList<>();

        Optional.ofNullable(configuration.getConfigurationSection("Players.%s.GKitz".formatted(asString))).ifPresentOrElse(section -> {
            for (final GKitz kit : this.kitsManager.getKits()) {
                final String kitName = kit.getName();

                if (!section.contains(kitName)) {
                    //todo() debug

                    continue;
                }

                final long cooldown = section.getLong(kitName);

                final Calendar calendar = Calendar.getInstance();

                calendar.setTimeInMillis(cooldown);

                cooldowns.add(new GkitCoolDown(kit, calendar));
            }
         }, () -> {
            //todo() logging
        });

        this.players.putIfAbsent(uuid, new CEPlayer(player, cooldowns));
    }

    public void unloadPlayer(@NotNull final Player player, final boolean isBackUp) {
        final UUID uuid = player.getUniqueId();

        getPlayer(player).ifPresentOrElse(target -> {
            final YamlConfiguration configuration = FileKeys.data.getPaperConfiguration();
            final String asString = uuid.toString();

            Optional.ofNullable(configuration.getConfigurationSection("Players.%s.GKitz".formatted(asString))).ifPresentOrElse(section -> {
                for (final GkitCoolDown cooldown : target.getCoolDowns()) {
                    section.set(cooldown.getGKitz().getName(), cooldown.getCoolDown().getTimeInMillis());
                }
            }, () -> {
                //todo() logging
            });

            if (!isBackUp) {
                this.players.remove(uuid);
            }

            FileKeys.data.save(); // save after changing
        }, () -> {
            //todo() debug
        });
    }

    public void backupPlayer(@NotNull final Player player) {
        unloadPlayer(player, true);
    }

    public final boolean hasPlayer(@NotNull final Player player) {
        return this.players.containsKey(player.getUniqueId());
    }

    public @NotNull final Optional<CEPlayer> getPlayer(@NotNull final Player player) {
        return Optional.ofNullable(this.players.get(player.getUniqueId()));
    }

    public @NotNull final Map<UUID, CEPlayer> getPlayers() {
        return Collections.unmodifiableMap(this.players);
    }
}