package me.badbones69.crazyenchantments.multisupport;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.party.PartyManager;
import org.bukkit.entity.Player;

public class MCMMOParty {
    
    public static boolean isFriendly(Player player, Player other) {
        Party party = PartyManager.getParty(player);
        if (party != null) {
            return party.hasMember(other.getUniqueId());
        }
        return false;
    }
    
}