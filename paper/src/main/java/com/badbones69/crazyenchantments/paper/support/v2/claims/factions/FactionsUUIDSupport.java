package com.badbones69.crazyenchantments.paper.support.v2.claims.factions;

import com.badbones69.crazyenchantments.paper.support.v2.interfaces.factions.FactionsSupport;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.perms.Role;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public final class FactionsUUIDSupport extends FactionsSupport<FPlayer, Faction> {

    private FPlayers instance = FPlayers.getInstance();

    @Override
    public boolean isWildernessWithFaction(@NonNull final Faction faction) {
        if (!isPluginReady()) {
            return true;
        }

        return faction.isWilderness();
    }

    @Override
    public boolean isWildernessWithPlayer(@NonNull final FPlayer player) {
        if (!isPluginReady()) {
            return true;
        }

        return isWildernessWithFaction(player.getFaction());
    }

    @Override
    public boolean isMember(@NonNull final Faction faction, @NonNull final Player player) {
        final FPlayer fPlayer = this.instance.getByPlayer(player);

        if (fPlayer == null || !fPlayer.hasFaction()) return false;

        final Faction playerFaction = fPlayer.getFaction();

        return playerFaction.getId().equals(faction.getId());
    }

    @Override
    public boolean isOwner(@NonNull final Player player) {
        final FPlayer fPlayer = this.instance.getByPlayer(player);

        if (fPlayer == null || !fPlayer.hasFaction()) return false;

        return fPlayer.getFaction().getDefaultRole().isAtLeast(Role.ADMIN);
    }

    @Override
    public boolean isPluginReady() {
        return this.pluginManager.isPluginEnabled("Factions");
    }
}