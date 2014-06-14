package net.diamonddominion.esaych.global;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.event.player.PlayerJoinEvent;


public class ForumActivation {
	private CustomPlugin plugin;

	public ForumActivation(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		String playerName = event.getPlayer().getName().toLowerCase();
		
		Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String url = "jdbc:mysql://108.167.147.31:3306/diamond_forum";
        String user = "diamond_forumaa";
        String password = "forumAuth123";

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery("SELECT user_id, user_type FROM phpbb_users WHERE username_clean='" + playerName + "'");
            if (rs.next()) {
            	if (rs.getInt("user_type") == 1)
            		activate(con, rs.getInt("user_id"));
            }
        } catch (SQLException ex) {
            log("Activation failed: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                log(ex.getMessage());
            }
        }
	}
	
	public void activate(Connection con, int user_id) {
		Statement st = null;
        ResultSet rs = null;
        
        try {
        	st = con.createStatement();
        	st.executeUpdate("UPDATE phpbb_users SET user_type='0', user_actkey='', user_inactive_time='0' WHERE user_id=" + user_id);
        	log("User ID '" + user_id + "' authenticated");
        	
        	st = con.createStatement();
        	rs = st.executeQuery("SELECT * FROM phpbb_users WHERE user_type='0' ORDER BY user_regdate DESC LIMIT 1");
        	if (rs.next()) {
        		try {
        			updateStats(con, rs.getInt("user_id"), rs.getString("username"), rs.getString("user_colour"), rs.getInt("group_id"));
        		} catch (ClassNotFoundException e) {
        			log("Update failed: " + e.getMessage());
        			e.printStackTrace();
        		}
        	}
		} catch (SQLException e1) {
			log("Activation Failed: " + e1.getMessage());
			e1.printStackTrace();
		}
	}
	
	public void updateStats(Connection con, int id, String username, String color, int gID)
			throws SQLException, ClassNotFoundException {
		
        Statement st = null;
        ResultSet rs = null;
        
		if (color.equals("")) {
			st = con.createStatement();
            rs = st.executeQuery("SELECT group_colour FROM phpbb_groups WHERE group_id='" + gID + "'");
			if (rs.next()) {
				color = rs.getString("group_colour");
			}
		}
		st = con.createStatement();
    	st.executeUpdate("UPDATE phpbb_config SET config_value = '" + id + "' WHERE config_name = 'newest_user_id'");
    	
    	st = con.createStatement();
    	st.executeUpdate("UPDATE phpbb_config SET config_value = '" + username + "' WHERE config_name = 'newest_username'");
    	
    	st = con.createStatement();
    	st.executeUpdate("UPDATE phpbb_config SET config_value = '" + color + "' WHERE config_name = 'newest_user_colour'");
    	
    	st = con.createStatement();
    	st.executeUpdate("UPDATE phpbb_config SET config_value = config_value+1 WHERE config_name = 'num_users'");
	}

	public void log(String info) {
		plugin.getLogger().info("<ForumActivation> " + info);
	}
}
