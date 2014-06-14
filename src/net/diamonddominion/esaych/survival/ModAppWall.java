package net.diamonddominion.esaych.survival;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;


public class ModAppWall {
	private CustomPlugin plugin;
	private Location p_nameSignLoc;
    private Location p_rateSignLoc;
	private Location u_nameSignLoc;
    private Location u_rateSignLoc;
	private Location d_nameSignLoc;
    private Location d_rateSignLoc;
	private int num;
	
	public ModAppWall(CustomPlugin plugin) {
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	public void enable() {
			plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
				@Override  
				public void run() {
					updateWall();
				}
			}, 60, 20*60*10);
		log("Enabled");
	}
	
	public void updateWall() {
		
    	p_nameSignLoc = new Location(Bukkit.getWorld("Survival"), 68, 73, 161);
        p_rateSignLoc = new Location (Bukkit.getWorld("Survival"), 68, 73, 162);
    	u_nameSignLoc = new Location(Bukkit.getWorld("Survival"), 68, 73, 163);
        u_rateSignLoc = new Location (Bukkit.getWorld("Survival"), 68, 73, 164);
    	d_nameSignLoc = new Location(Bukkit.getWorld("Survival"), 68, 73, 165);
        d_rateSignLoc = new Location (Bukkit.getWorld("Survival"), 68, 73, 166);
        
		clearSigns();
		Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String url = "jdbc:mysql://192.185.24.94:3306/diamond_appstatus";
        String user = "diamond_admin";
        String password = "AdminControl123";
        
        Calendar cal = Calendar.getInstance();
        
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int year = cal.get(Calendar.YEAR);
        day -= 10;
        if (day < 1) {
        	month--;
        	day = 30+day-10;
        }
        
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
//            st.execute("SELECT * FROM Pending");
            rs = st.executeQuery("SELECT * FROM Applications WHERE `Rating` >= 8.5 && `LastLogin` >= '" + year + "-" + month + "-" + day + "' ORDER BY `Applications`.`Timestamp` DESC;");
            num = 1;
            while (rs.next()) {
//            	pending.put(rs.getString(1), rs.getString(2));
            	addToPendingWall(rs.getString(1), rs.getString(2));
            }
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM Applications WHERE `Rating` = 0.0 ORDER BY `Applications`.`Timestamp` DESC;");
            num = 1;
            while (rs.next()) {
            	addToUnreadWall(rs.getString(1), "NA");
            }
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM Applications WHERE `Rating` < 8.5 ORDER BY `Applications`.`Timestamp` DESC;");
            num = 1;
            while (rs.next()) {
            	addToDeniedWall(rs.getString(1), rs.getString(2).replaceFirst("-", ChatColor.DARK_RED + ""));
            }

        } catch (SQLException ex) {
            log(ex.getMessage());
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
	
	public void addToPendingWall(String name, String rating) {
        if (p_nameSignLoc.getChunk() != null && p_rateSignLoc.getChunk() != null) {
        	Sign sign;
        	BlockState signBlock = Bukkit.getWorld("Survival").getBlockAt(p_nameSignLoc).getState();
        	BlockState rateBlock = Bukkit.getWorld("Survival").getBlockAt(p_rateSignLoc).getState();
        	int place = ((num - 1) % 4);
        	if (signBlock instanceof Sign) {
        		sign = (Sign) signBlock;
        		sign.setLine(place, name);
        		sign.update();
        	}
        	if (rateBlock instanceof Sign) {
        		sign = (Sign) rateBlock;
        		sign.setLine(place, ChatColor.DARK_BLUE + "Rating: " + rating);
        		sign.update();
        	}
        	if (num % 4 == 0) {
        		p_nameSignLoc.add(0, -1, 0);
        		p_rateSignLoc.add(0, -1, 0);
        	}
        	num++;
        }
	}

	public void addToUnreadWall(String name, String rating) {
		if (u_nameSignLoc.getChunk() != null && u_rateSignLoc.getChunk() != null) {
			Sign sign;
			BlockState signBlock = Bukkit.getWorld("Survival").getBlockAt(u_nameSignLoc).getState();
			BlockState rateBlock = Bukkit.getWorld("Survival").getBlockAt(u_rateSignLoc).getState();
			int place = ((num - 1) % 4);
			if (signBlock instanceof Sign) {
				sign = (Sign) signBlock;
				sign.setLine(place, name);
				sign.update();
			}
			if (rateBlock instanceof Sign) {
				sign = (Sign) rateBlock;
				sign.setLine(place, ChatColor.DARK_BLUE + "Rating: " + rating);
				sign.update();
			}
			if (num % 4 == 0) {
				u_nameSignLoc.add(0, -1, 0);
				u_rateSignLoc.add(0, -1, 0);
			}
			num++;
		}
	}
	
	public void addToDeniedWall(String name, String rating) {
		if (u_nameSignLoc.getChunk() != null && u_rateSignLoc.getChunk() != null) {
			Sign sign;
			BlockState signBlock = Bukkit.getWorld("Survival").getBlockAt(d_nameSignLoc).getState();
			BlockState rateBlock = Bukkit.getWorld("Survival").getBlockAt(d_rateSignLoc).getState();
			int place = ((num - 1) % 4);
			if (signBlock instanceof Sign) {
				sign = (Sign) signBlock;
				sign.setLine(place, name);
				sign.update();
			}
			if (rateBlock instanceof Sign) {
				sign = (Sign) rateBlock;
				sign.setLine(place, ChatColor.DARK_BLUE + "Rating: " + rating);
				sign.update();
			}
			if (num % 4 == 0) {
				d_nameSignLoc.add(0, -1, 0);
				d_rateSignLoc.add(0, -1, 0);
			}
			num++;
		}
	}
	
	public void clearSigns() {
		try {
			for (int x = 25; x <= 30; x++) {
				for (int y = 74; y >= 72; y--) {
					BlockState signState = p_nameSignLoc.getWorld().getBlockAt(x, y, 195).getState();
					if (signState instanceof Sign) {
						Sign sign = (Sign) signState;
						sign.setLine(0, "");
						sign.setLine(1, "");
						sign.setLine(2, "");
						sign.setLine(3, "");
						sign.update();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void log(String info) {
		plugin.getLogger().info("<ModAppWall> " + info);
	}
}
