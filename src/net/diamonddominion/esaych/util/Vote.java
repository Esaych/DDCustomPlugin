package net.diamonddominion.esaych.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Vote {
	
	public int id = -1; //Main Identification -1 being that it has not yet been identified;
	
	public String player;
	public int timestamp;
	public String site;
	public int rewarded;
	public boolean donationcredit;

	private Connection con;
	
	public Vote(int id) {
		this.id = id;
		try {
			con = SQL.getConnection();

			Statement st = (Statement) con.createStatement(); 
			ResultSet result = st.executeQuery("SELECT * FROM `dd-votes` WHERE `id` = " + id + ";");

			while (result.next()) {
				player = result.getString("player");
				timestamp = result.getInt("time");
				site = result.getString("site");
				rewarded = result.getInt("rewarded");
				donationcredit = result.getInt("donationcredit") == 1;
			}

			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	public Vote(String player, int timestamp, String site, int reward, boolean donationcredit) {
		this.player = player;
		this.timestamp = timestamp;
		this.site = site;
		this.rewarded = reward;
		this.donationcredit = donationcredit;
		update();
	}
	
	public void update() {
		Connection con = null;
		try {
			con = SQL.getConnection();

			Statement st = (Statement) con.createStatement(); 
			
			int d = 0;
			if (donationcredit)
				d = 1;
			
			if (id == -1) {
				st.execute("INSERT INTO `dd-votes`(`player`, `time`, `site`, `rewarded`, `donationcredit`) VALUES ('" + player + "', " + timestamp + ", '" + site + "', " + rewarded + ", " + d + ");");
				ResultSet result = st.executeQuery("SELECT `id` FROM `dd-votes` WHERE `player` = '" + player + "' && `time` = " + timestamp + " && `site` = '" + site + "';");
				while (result.next()) {
					id = result.getInt("id");
				}
			} else {
				st.execute("UPDATE `dd-votes` SET `player`='" + player + "', `time`='" + timestamp + "', `site`='" + site + "', `rewarded`='" + rewarded + "', `donationcredit`='" + d + "' WHERE `id`='" + id + "';");
				
				st = (Statement) con.createStatement(); 
				ResultSet result = st.executeQuery("SELECT * FROM `dd-votes` WHERE `id` = " + id + ";");

				while (result.next()) {
					player = result.getString("player");
					timestamp = result.getInt("time");
					site = result.getString("site");
					rewarded = result.getInt("rewarded");
					donationcredit = result.getInt("donationcredit") == 1;
				}
			}

			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	public void rewardVote(int rewardID) {
		rewarded = rewardID;
		update();
	}
	
	public void donationcreditVote(boolean value) {
		donationcredit = value;
		update();
	}
}
