package net.diamonddominion.esaych.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DonationCredit {
	
	public DonationCredit() {
	}
	
	public static double getCredits(String player) {
		double credits = 0;
		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/DiamondDom_Bungee";
		String user = "root";
		String password = "K2gEBBl6";
		try {
			con = DriverManager.getConnection(url, user, password);

			Statement st = (Statement) con.createStatement(); 
			ResultSet result = st.executeQuery("SELECT `credits` FROM `dd-donationcredits` WHERE `user` = '" + player + "';");

			while (result.next()) {
				credits = result.getDouble("credits");
			}

			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return credits;
	}
	
	public static void setCredits(String player, int amount) {
		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/DiamondDom_Bungee";
		String user = "root";
		String password = "K2gEBBl6";
		try {
			con = DriverManager.getConnection(url, user, password);

			Statement st = (Statement) con.createStatement(); 
			st.executeUpdate("INSERT INTO `dd-donationcredits` (`user`,`credits`) VALUES ('" + player + "'," + amount + ")"
					+ "ON DUPLICATE KEY UPDATE `credits`='" + amount + "';");

			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static double addCredits(String player, double amount) {
		double credits = 0;
		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/DiamondDom_Bungee";
		String user = "root";
		String password = "K2gEBBl6";
		try {
			con = DriverManager.getConnection(url, user, password);

			Statement st = (Statement) con.createStatement(); 
			ResultSet result = st.executeQuery("SELECT `credits` FROM `dd-donationcredits` WHERE `user` = '" + player + "';");

			while (result.next()) {
				credits = result.getDouble("credits");
			}
			
			credits += amount;
			
			st = (Statement) con.createStatement(); 
			st.executeUpdate("INSERT INTO `dd-donationcredits` (`user`,`credits`) VALUES ('" + player + "'," + credits + ")"
					+ "ON DUPLICATE KEY UPDATE `credits`='" + credits + "';");
			
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return credits;
	}
	
}
