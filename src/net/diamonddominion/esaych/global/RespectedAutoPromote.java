package net.diamonddominion.esaych.global;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.diamonddominion.esaych.CustomPlugin;
import net.diamonddominion.esaych.util.SQL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

public class RespectedAutoPromote {
	private CustomPlugin plugin;

	public RespectedAutoPromote(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	public boolean onCommand(CommandSender sender) {
		if (!(sender instanceof Player)) {
			log("INFINITY");
			return true;
		}
		Connection con = null;
		try {
			con = SQL.getConnection();

			Statement st = (Statement) con.createStatement(); 
			ResultSet result = st.executeQuery("SELECT `onlinetime` FROM `lb-players` WHERE `playername` = '" + sender.getName() + "';");

			while (result.next()) {
				int onlinetime = result.getInt("onlinetime");
				int minutes = onlinetime/60;
				int seconds = onlinetime - (minutes*60);
				int hours = minutes / 60;
				minutes = minutes - (hours*60);
				sender.sendMessage(ChatColor.GREEN + "You have been online a total of " + ChatColor.AQUA + hours + " hours, " + minutes + " minutes, " + seconds + " seconds");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return true;
	}
	
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		@SuppressWarnings("static-access")
		String group = plugin.chat.perms.getPlayerGroups(event.getPlayer())[0];
		if (group.contains("Player")) {
			Connection con = null;
			try {
				con = SQL.getConnection();

				Statement st = (Statement) con.createStatement(); 
				ResultSet result = st.executeQuery("SELECT `onlinetime` FROM `lb-players` WHERE `playername` = '" + event.getPlayer().getName() + "';");

				while (result.next()) {
					int onlinetime = result.getInt("onlinetime");
					if (onlinetime > 86400) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + event.getPlayer().getName() + " group set Respected");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gcmd pex reload");
						event.getPlayer().sendMessage(ChatColor.GREEN + "CONGRATS! You're now a Diamond Dominion RESPECTED player");
					}
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void log(String info) {
		plugin.getLogger().info("<RespectedAutoPromote> " + info);
	}
}
