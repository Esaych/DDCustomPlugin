package net.diamonddominion.esaych.global;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class ChatFacsTowns {
	static Chat chatPlugin = null;
	
	public ChatFacsTowns(Chat chat) {
		ChatUtils.chatPlugin = chat;
	}
	
	public String getFacName(Player p) {
		return UPlayer.get(p).getFactionName();
	}
	
	@SuppressWarnings("static-access")
	public String getFactionColorsName(Player player, Player rec) {
		UPlayer uplayer = UPlayer.get(player);
		Faction faction = uplayer.getFaction();
		String role = uplayer.getRole().getPrefix();
		String fac = role + faction.getName();
		if (faction.isNone()) {
			return "";
		} else {
			ChatColor color = faction.getColorTo(UPlayer.get(rec));
			return color + chatPlugin.utils.removeCussWords(fac, false,
					player, true, color + "") + " ";
		}
	}
	
	public ArrayList<Player> getFacPlayers(Player player) {
		if (getFacName(player).equals("")) {
			return new ArrayList<Player>();
		} else {
			List<Player> fplayers = UPlayer.get(player).getFaction().getOnlinePlayers();
			ArrayList<Player> players = new ArrayList<Player>();
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.hasPermission("ddchat.staffchat") || fplayers.contains(p))
					players.add(p);
			}
			return players;
		}
	}
	
	public boolean testTown(Player p) {
		try {
			Resident r = TownyUniverse.getDataSource().getResident(p.getName());
			if (r.getTown() == null) {
				return false;
			}
		} catch (NotRegisteredException e) {
			return false;
		}
		return true;
	}
	
	public String getTown(Player player) {
		try {
			Resident r = TownyUniverse.getDataSource().getResident(player.getName());
			//Resident re = TownyUniverse.getDataSource().getResident(rec.getName());
//			List<String> ranks = r.getTownRanks();
//			String title = "";
//			if (ranks.size() > 0) {
//				String t = ranks.get(0);
//				title = ":" + String.valueOf(t.charAt(0)).toUpperCase() + t.substring(1, t.length());
//			}
			//some type of relational string
			ChatColor color = ChatColor.AQUA;
			String str = r.getTown().getName() + " ";
//			if (str.startsWith("|")) {
//				str = str.substring(1, str.length());
//			}
			if (str.length() > 0) {
				return color + "\u00BB" + str;
			}
		} catch (NotRegisteredException e) {}
		return "";
	}
	
	public ArrayList<Player> getTownPlayers(Player player) {
		if (!testTown(player)) {
			return new ArrayList<Player>();
		} else {
			ArrayList<Player> players = new ArrayList<Player>();
			try {
				Resident r = TownyUniverse.getDataSource().getResident(player.getName());
				//Resident re = TownyUniverse.getDataSource().getResident(rec.getName());
				for (Resident res : r.getTown().getResidents()) {
					Player member = Bukkit.getPlayer(res.getName());
					if (member != null)
						players.add(member);
				}
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.hasPermission("ddchat.staffchat") && !players.contains(p))
						players.add(p);
				}
			} catch (NotRegisteredException e) {}
			return players;
		}
	}
	
	public boolean testNation(Player p) {
		try {
			Resident r = TownyUniverse.getDataSource().getResident(p.getName());
			if (r.getTown().getNation() == null) {
				return false;
			}
		} catch (NotRegisteredException e) {
			return false;
		}
		return true;
	}
	
	public ArrayList<Player> getNationPlayers(Player player) {
		if (!testNation(player)) {
			return new ArrayList<Player>();
		} else {
			ArrayList<Player> players = new ArrayList<Player>();
			try {
				Resident r = TownyUniverse.getDataSource().getResident(player.getName());
				if (r.getTown().getNation() == null || r.getTown().getNation().getName().equals("")) {
					return new ArrayList<Player>();
				} else {
					for (Resident res : r.getTown().getNation().getResidents()) {
						Player member = Bukkit.getPlayer(res.getName());
						if (member != null)
							players.add(member);
					}
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (p.hasPermission("ddchat.staffchat") && !players.contains(p))
							players.add(p);
					}
					return players;
				}
			} catch (NotRegisteredException e) {}
			return players;
		}
	}
	
	@SuppressWarnings("static-access")
	public String getFaction(Player player, Player rec) {
		UPlayer uplayer = UPlayer.get(player);
		Faction faction = uplayer.getFaction();
		String role = uplayer.getRole().getPrefix();
		String fac = role + faction.getName();
		if (faction.isNone()) {
			return "";
		} else {
			ChatColor color = faction.getColorTo(UPlayer.get(rec));
			return color + chatPlugin.utils.removeCussWords(fac, false,
					player, true, color + "") + " ";
		}
	}
}
