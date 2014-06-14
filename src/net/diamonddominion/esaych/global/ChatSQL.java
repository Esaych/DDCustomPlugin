package net.diamonddominion.esaych.global;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.diamonddominion.esaych.util.SQL;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class ChatSQL {
	static Chat chatPlugin = null;

	@SuppressWarnings({ "deprecation", "static-access" })
	public ChatSQL(Chat chat) {
		ChatUtils.chatPlugin = chat;
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(chat.plugin, new Runnable() {
			@Override
			public void run() {
				update();
			}
		}, 5, 20*15);
	}
	Connection pluginConnection;

	Map<String, Integer> cacheMuteData = new HashMap<String, Integer>();
	Map<String, String> cachePrefixData = new HashMap<String, String>();
	Map<String, String> cacheChannelData = new HashMap<String, String>();
	Map<String, Boolean> cacheBadProfileData = new HashMap<String, Boolean>();
	Map<String, String> cacheDatingPartner = new HashMap<String, String>();
	Map<String, ArrayList<String>> cacheIgnores = new HashMap<String, ArrayList<String>>();

	public int getMuteTime(String playerName) {
		if (cacheMuteData.containsKey(playerName))
			return cacheMuteData.get(playerName);
		return 0;
	}

	public void setMuteTime(String playerName, int timestamp) {
		insertQuery(playerName, "muted", String.valueOf(timestamp));
		cacheMuteData.put(playerName, timestamp);
	}

	public String getPrefix(String playerName) {
		return cachePrefixData.get(playerName);
	}

	public void setPrefix(String playerName, String prefix) {
		insertQuery(playerName, "prefix", prefix);
		cachePrefixData.put(playerName, prefix);
	}

	public String getChannel(String playerName) {
		return cacheChannelData.get(playerName);
	}

	public void setChannel(String playerName, String channel) {
		insertQuery(playerName, "channel", channel);
		cacheChannelData.put(playerName, channel);
	}

	public boolean getBadProfile(String playerName) {
		if (cacheBadProfileData.containsKey(playerName))
			return cacheBadProfileData.get(playerName);
		return false;
	}

	public void setBadProfile(String playerName, boolean bad) {
		if (bad)
			insertQuery(playerName, "badprofile", "1");
		else
			insertQuery(playerName, "badprofile", "0");
		cacheBadProfileData.put(playerName, bad);
	}

	public String getDatingPartner(String playerName) {
		return cacheDatingPartner.get(playerName);
	}

	public void setDatingPartner(String player1, String player2) {
		if (player2 != null) {
			Connection con = SQL.getConnection();
			try {

				con = SQL.getConnection();

				Statement st = (Statement) con.createStatement(); 
				st.executeUpdate("INSERT INTO `ddchat-playerdata` (`ign`,`dating`) VALUES ('" + player1 + "','" + player2 + "')"
						+ "ON DUPLICATE KEY UPDATE `dating`='" + player2 + "';");
				st = (Statement) con.createStatement(); 
				st.executeUpdate("INSERT INTO `ddchat-playerdata` (`ign`,`dating`) VALUES ('" + player2 + "','" + player1 + "')"
						+ "ON DUPLICATE KEY UPDATE `dating`='" + player1 + "';");

				

			} catch (SQLException e) {
				e.printStackTrace();
			}
			cacheDatingPartner.put(player1, player2);
			cacheDatingPartner.put(player2, player1);
		} else {
			Connection con = SQL.getConnection();
			try {

				con = SQL.getConnection();

				Statement st = (Statement) con.createStatement(); 
				st.executeUpdate("UPDATE `ddchat-playerdata` SET `dating`= NULL WHERE `ign`='" + player1 + "';");

				

			} catch (SQLException e) {
				e.printStackTrace();
			}
			cacheDatingPartner.remove(player1);
			cacheDatingPartner.remove(player2);
		}
	}

	public void clearDatingPartner(String player1) {
		String player2 = getDatingPartner(player1);
		try {
			Connection con = SQL.getConnection();
			con = SQL.getConnection();

			Statement st = (Statement) con.createStatement(); 
			st.executeUpdate("INSERT INTO `ddchat-playerdata` (`ign`,`dating`) VALUES ('" + player1 + "',NULL)"
					+ "ON DUPLICATE KEY UPDATE `dating`=NULL;");
			st = (Statement) con.createStatement(); 
			st.executeUpdate("INSERT INTO `ddchat-playerdata` (`ign`,`dating`) VALUES ('" + player2 + "',NULL)"
					+ "ON DUPLICATE KEY UPDATE `dating`=NULL;");

			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		cacheDatingPartner.remove(player1);
		cacheDatingPartner.remove(player2);
	}

	public ArrayList<String> getIgnoredPlayers(String playerName) {
		ArrayList<String> data = cacheIgnores.get(playerName);
		if (data != null)
			return data;
		return new ArrayList<String>();
	}

	public void addIgnoredPlayer(String playerName, String whoToIgnore) {
		SQL.singleQuery("INSERT INTO `ddchat-ignores` (`ign`,`ignoring`) VALUES ('" + playerName + "','" + whoToIgnore + "');");
		ArrayList<String> ignores = cacheIgnores.get(playerName);
		if (ignores == null)
			ignores = new ArrayList<String>();
		ignores.add(whoToIgnore);
		cacheIgnores.put(playerName, ignores);
	}

	public void removeIgnoredPlayer(String playerName, String whoToUnignore) {
		SQL.singleQuery("DELETE FROM `ddchat-ignores` WHERE `ign`='" + playerName + "' && `ignoring`='" + whoToUnignore + "';");
		ArrayList<String> ignores = cacheIgnores.get(playerName);
		ignores.remove(whoToUnignore);
		cacheIgnores.put(playerName, ignores);
	}

	public void insertQuery(String ign, String dataType, String data) {
		if (data != null && data != "0")
			SQL.singleQuery("INSERT INTO `ddchat-playerdata` (`ign`,`" + dataType + "`) VALUES ('" + ign + "','" + data + "')"
					+ "ON DUPLICATE KEY UPDATE `" + dataType + "`='" + data + "';");
		else
			SQL.singleQuery("INSERT INTO `ddchat-playerdata` (`ign`,`" + dataType + "`) VALUES ('" + ign + "', NULL)"
					+ "ON DUPLICATE KEY UPDATE `" + dataType + "`=NULL;");
	}

	public void update() {
		try {
			Connection con = SQL.getConnection();
			Statement st = (Statement) con.createStatement(); 
			ResultSet result = st.executeQuery("SELECT * FROM `ddchat-playerdata`;");
			
			Map<String, Integer> tempcacheMuteData = new HashMap<String, Integer>();
			Map<String, String> tempcachePrefixData = new HashMap<String, String>();
			Map<String, String> tempcacheChannelData = new HashMap<String, String>();
			Map<String, Boolean> tempcacheBadProfileData = new HashMap<String, Boolean>();
			Map<String, String> tempcacheDatingPartner = new HashMap<String, String>();
			Map<String, ArrayList<String>> tempcacheIgnores = new HashMap<String, ArrayList<String>>();

			while (result.next()) {
				String player = result.getString("ign");
				String channel = result.getString("channel");
				int muted = result.getInt("muted");
				String prefix = result.getString("prefix");
				String dating = result.getString("dating");
				boolean badprofile = result.getBoolean("badprofile");
				if (channel != null)
					tempcacheChannelData.put(player, channel);
				if (muted != 0)
					tempcacheMuteData.put(player, muted);
				if (prefix != null)
					tempcachePrefixData.put(player, prefix);
				if (dating != null)
					tempcacheDatingPartner.put(player, dating);
				tempcacheBadProfileData.put(player, badprofile);
			}
			
			st = (Statement) con.createStatement(); 
			result = st.executeQuery("SELECT * FROM `ddchat-ignores`;");
			
			Map<String, String> data = new HashMap<String, String>();
			
			while (result.next()) {
				String player = result.getString("ign");
				String ignoring = result.getString("ignoring");
				
				if (data.containsKey(player)) {
					String str = data.get(player);
					str += "," + ignoring;
					data.put(player, str);
				} else {
					data.put(player, ignoring);
				}
			}
			
			tempcacheIgnores.clear();
			
			for (String playerName : data.keySet()) {
				ArrayList<String> ignoring = new ArrayList<String>();
				for (String ignoredPlayer: data.get(playerName).split(",")) {
					ignoring.add(ignoredPlayer);
				}
				tempcacheIgnores.put(playerName, ignoring);
			}

			cacheMuteData = tempcacheMuteData;
			cachePrefixData = tempcachePrefixData;
			cacheChannelData = tempcacheChannelData;
			cacheBadProfileData = tempcacheBadProfileData;
			cacheDatingPartner = tempcacheDatingPartner;
			cacheIgnores = tempcacheIgnores;
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	public void logChat(Player player, String msg, String receiver) {
		msg = msg.replaceAll("'", "''");
		@SuppressWarnings("static-access")
		String[] ranks = chatPlugin.perms.getPlayerGroups(player);
		String rank = ranks[ranks.length-1];
//		System.out.println("INSERT INTO `ddchat-chatlog` (`time`, `player`, `message`, `msg-receiver`, `rank`) VALUES (" + (int)System.currentTimeMillis()/1000 + ", '" + player.getName() + "', '" + msg + "', '" + receiver + "', '" + rank + "');");
		SQL.singleQuery("INSERT INTO `ddchat-chatlog` (`time`, `player`, `message`, `msg-receiver`, `rank`) VALUES (" + (int)System.currentTimeMillis()/1000 + ", '" + player.getName() + "', '" + msg + "', '" + receiver + "', '" + rank + "');");
	}
	
	public void logCommand(Player player, String command) {
		command = command.replaceAll("'", "''");
		@SuppressWarnings("static-access")
		String[] ranks = chatPlugin.perms.getPlayerGroups(player);
		String rank = ranks[ranks.length-1];
		SQL.singleQuery("INSERT INTO `ddchat-commandlog` (`time`, `player`, `command`, `rank`, `world`) VALUES (" + (int)System.currentTimeMillis()/1000 + ", '" + player.getName() + "', '" + command + "', '" + rank + "', '" + player.getWorld().getName() + "');");
	}


}
