package net.diamonddominion.esaych.survival;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;


public class ModListWall {
	private CustomPlugin plugin;
	
	public ModListWall(CustomPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void enable() {
		updateList(getModList());
		log("Enabled");
	}
	
	public ArrayList<String> getModList() {
		ArrayList<String> modList = new ArrayList<String>();
		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/DiamondDom_Bungee";
		String user = "root";
		String password = "K2gEBBl6";
		try {
			con = DriverManager.getConnection(url, user, password);

			Statement st = (Statement) con.createStatement(); 
			ResultSet result = st.executeQuery("SELECT * FROM permissions_inheritance WHERE parent LIKE '%mod' && type = '1' && parent != 'HeadMod'");
			
			while (result.next()) {
				modList.add(result.getString("child"));	
			}
			
			con.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		return modList;
	}
	
	public void updateList(ArrayList<String> list) {
		Sign a = null;
		Sign b = null;
		Sign c = null;
		Sign d = null;
		try {
			a = (Sign) Bukkit.getWorld("Survival").getBlockAt(60, 72, 172).getState();
			b = (Sign) Bukkit.getWorld("Survival").getBlockAt(60, 72, 171).getState();
			c = (Sign) Bukkit.getWorld("Survival").getBlockAt(60, 72, 170).getState();
			d = (Sign) Bukkit.getWorld("Survival").getBlockAt(60, 72, 169).getState();
			clearSign(a);
			clearSign(b);
			clearSign(c);
			clearSign(d);
		} catch (Exception e) {
			log("Failure to find signs.");
		}
		try {
			int i = 0;
			
			int spacing = 4;
			if (list.size() <= 16) {
				double s = list.size()/4;
				if (s > (int)s) {
					s++;
				}
				spacing = (int)s;
			}
			for (String name : list) {
				if (i < spacing) {
					a.setLine(i, ChatColor.AQUA + name);
				} else if (i < spacing*2) {
					b.setLine(i-spacing, ChatColor.AQUA + name);
				} else if (i < spacing*3) {
					c.setLine(i-spacing*2, ChatColor.AQUA + name);
				} else if (i < spacing*4) {
					d.setLine(i-spacing*3, ChatColor.AQUA + name);
				}
				i++;
			}
			a.update();
			b.update();
			c.update();
			d.update();
		} catch (Exception e) {
			log("Failure to write to signs.");
		}
	}
	
	public void clearSign(Sign sign) {
		sign.setLine(0, "");
		sign.setLine(1, "");
		sign.setLine(2, "");
		sign.setLine(3, "");
	}
	
	public void log(String info) {
		plugin.getLogger().info("<ModListWall> " + info);
	}
}
