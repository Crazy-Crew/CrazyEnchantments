package me.badbones69.crazyenchantments.multisupport.factions;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TownySupport implements FactionPlugin {
    
    public boolean inTerritory(Player player) {
        try {
            Town town = TownyAPI.getInstance().getTownBlock(player.getLocation()).getTown();
            Resident resident = TownyAPI.getInstance().getDataSource().getResident(player.getName());
            if (resident.hasTown() && resident.getTown().equals(town)) {
                return true;
            }
        } catch (NotRegisteredException ignored) {
        }
        return false;
    }
    
    public boolean isFriendly(Player player, Player other) {
        return CombatUtil.preventDamageCall((Towny) SupportedPlugins.TOWNY.getPlugin(), player, other);
    }
    
    public boolean canBreakBlock(Player player, Block block) {
        return true;
    }
    
}