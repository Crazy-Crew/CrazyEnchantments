package me.badbones69.crazyenchantments.multisupport;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.entity.Player;

public class SimpleClansSupport {
	
	private static SimpleClans simpleClans = SimpleClans.getInstance();
	private static ClanManager clanManager = simpleClans.getClanManager();
	
	public static Boolean isFriendly(Player player, Player other) {
		ClanPlayer p = clanManager.getClanPlayer(player);
		ClanPlayer o = clanManager.getClanPlayer(other);
		return p != null && o != null && (p.getClan().equals(o.getClan()) || p.isAlly(other));
	}
	
	//Plugin uses other plguins line PreciousStones for land checking.
	
}