package me.badbones69.crazyenchantments.multisupport.factions;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.WorldCoord;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TownySupport implements FactionPlugin {
    
    public boolean inTerritory(Player player) {
        try {
            TownBlock block = WorldCoord.parseWorldCoord(player).getTownBlock();
            Resident playerUser = TownyUniverse.getDataSource().getResident(player.getName());
            if (playerUser.hasTown() && playerUser.getTown().hasTownBlock(block)) {
                return true;
            }
        } catch (NotRegisteredException ignored) {
        }
        return false;
    }
    
    public boolean isFriendly(Player player, Player other) {
        try {
            Resident playerUser = TownyUniverse.getDataSource().getResident(player.getName());
            Resident otherUser = TownyUniverse.getDataSource().getResident(other.getName());
            if (playerUser.hasTown() && otherUser.hasTown() && playerUser.getTown().getName().equalsIgnoreCase(otherUser.getTown().getName())) {
                return true;
            }
        } catch (NotRegisteredException ignored) {
        }
        return false;
    }
    
    public boolean canBreakBlock(Player player, Block block) {
        return true;
    }
    
}