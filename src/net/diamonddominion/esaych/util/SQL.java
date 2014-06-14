package net.diamonddominion.esaych.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQL {

	static String url = "jdbc:mysql://localhost:3306/DiamondDom_Bungee";
	static String user = "root";
	static String password = "K2gEBBl6";
	static Connection con;
	
	public static Connection getConnection() {
		try {
			if (con != null && !con.isClosed()) {
				return con;
			} else {
				con = DriverManager.getConnection(url, user, password);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
	
	public static void singleQuery(String query) {
		try {
			Connection con = getConnection();

			Statement st = (Statement) con.createStatement(); 
			st.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
