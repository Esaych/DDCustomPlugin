package net.diamonddominion.esaych.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class VoteSQL {

	public static int getVotesNotRewarded(String player) {
		return filterVotesCount(player, 0, null, true, false);
	}

	public static int getVotesLast24(String player) {
		return filterVotesCount(player, (int) (System.currentTimeMillis()/1000 - 60*60*24), null, false, false);
	}

	public static int getAllPlayerVotes(String player) {
		return filterVotesCount(player, 0, null, false, false);
	}
	
	public static String getQuery(String player, int oldestTime, String site, boolean notRewarded, boolean notCredited) {
		String sql = "SELECT `id` FROM `dd-votes` WHERE";
		if (player != null) {
			sql += " `player` = '" + player + "'";
		}
		if (oldestTime != 0) {
			if (!sql.equals("SELECT * FROM `dd-votes` WHERE"))
				sql += " &&";
			sql += " `time` > " + oldestTime;
		}
		if (site != null) {
			if (!sql.equals("SELECT * FROM `dd-votes` WHERE"))
				sql += " &&";
			sql += " `site` = '" + site + "'";
		}
		if (notRewarded) {
			if (!sql.equals("SELECT * FROM `dd-votes` WHERE"))
				sql += " &&";
			sql += " `rewarded` = 0";
		}
		if (notCredited) {
			if (!sql.equals("SELECT * FROM `dd-votes` WHERE"))
				sql += " &&";
			sql += " `donationcredit` = 0";
		}
		return sql;
	}
	
	public static int filterVotesCount(String player, int oldestTime, String site, boolean notRewarded, boolean notCredited) {
		String sql = getQuery(player, oldestTime, site, notRewarded, notCredited);
		sql = sql.replaceAll("`id`", "COUNT(*) AS 'count'");
		int count = 0;
		Connection con = null;
		try {
			con = SQL.getConnection();

			Statement st = (Statement) con.createStatement(); 
			ResultSet result = st.executeQuery(sql);

			while (result.next()) {
				count = result.getInt("count");
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return count;
	}

	public static ArrayList<Vote> filterVotes(String player, int oldestTime, String site, boolean notRewarded, boolean notCredited) {
		String sql = getQuery(player, oldestTime, site, notRewarded, notCredited);

		ArrayList<Vote> votes = new ArrayList<Vote>();
		Connection con = null;
		try {
			con = SQL.getConnection();

			Statement st = (Statement) con.createStatement(); 
			ResultSet result = st.executeQuery(sql);

			while (result.next()) {
				Vote vote = new Vote(result.getInt("id"));
				votes.add(vote);
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return votes;
	}

	public static String userExists(String player) {
		String found = "";
		Connection con = null;
		try {
			con = SQL.getConnection();

			Statement st = (Statement) con.createStatement(); 
			ResultSet result = st.executeQuery("SELECT * FROM `BungeePlayers` WHERE `playername` = '" + player + "';");

			while (result.next() && found.equals("")) {
				found = result.getString("playername");
			}

			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return player;
	}

	public static ArrayList<Integer> getRewards(String player) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		Connection con = null;
		try {
			con = SQL.getConnection();

			Statement st = (Statement) con.createStatement(); 
			ResultSet result = st.executeQuery("SELECT * FROM `dd-voterewards` WHERE `player` = '" + player + "' && `expiretime` > '" + System.currentTimeMillis() + "';");

			while (result.next()) {
				ids.add(result.getInt("type"));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return ids;
	}
}
