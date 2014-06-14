package net.diamonddominion.esaych.global;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.diamonddominion.esaych.CustomPlugin;
import net.diamonddominion.esaych.util.SQL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

public class Homes {
	private CustomPlugin plugin;

	public Homes(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		if (!(sender instanceof Player)) {
			log("idgaf");
			return true;
		}
		
		Player player = (Player) sender;
		if (player.getWorld().getName().equals("EventWorld")) {
			msg(player, "You can't use homes in the EventWorld");
			return true;
		}

		String homeName;
		if (args.length == 0)
			homeName = "home";
		else
			homeName = args[0];
		int amountOfHomes = getHomesAmount(player);
		
		//sethome, delhome, home, homes
		if (label.equalsIgnoreCase("sethome")) {
			if (amountOfHomes > getHomesList(player).size() || amountOfHomes == -1) {
				addHome(player, homeName, player.getLocation());
			} else {
				msg(player, "You have exceeded your maximum of " + amountOfHomes + " homes.");
			}
			return true;
		} else if (label.equalsIgnoreCase("delhome")) {
			if (getHomesList(player).contains(homeName)) {
				delHome(player, homeName);
			} else {
				msg(player, "Your home '" + homeName + "' does not exist!");
			}
			return true;
		} else if (label.equalsIgnoreCase("home")) {
			teleportPlayer(player, getHomeLocation(player, homeName));
			return true;
		} else if (label.equalsIgnoreCase("homes")) {
			msg(player, "Your current homes list:");
			for (String home : getHomesList(player)) {
				player.sendMessage(" - " + ChatColor.AQUA + home);
			}
			return true;
		}
		return false;
	}
	
	Map<String, String> toTp = new HashMap<String, String>();
	
	public void onBungeeMessageReceived(String channel, Player player, byte[] message) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
        String subchannel = null;
		try {
			subchannel = in.readUTF();
		} catch (IOException e) {
			log("Could not read plugin message");
			e.printStackTrace();
		}
        if (subchannel.equals("DDCustomPlugin_Homes")) {
        	try {
        		short len = in.readShort();
        		byte[] msgbytes = new byte[len];
        		in.readFully(msgbytes);
        		
        		DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
				String playerName = msgin.readUTF(); 
				String location = msgin.readUTF();
				
//				log(location + "; " + playerName);
				
				ArrayList<String> onlinePlayers = new ArrayList<String>();
				
				for (Player onlineplayer : Bukkit.getOnlinePlayers()) {
					onlinePlayers.add(onlineplayer.getName());
				}
				
				if (onlinePlayers.contains(playerName)) {
					teleportPlayer(Bukkit.getPlayer(playerName), location);
				} else {
					toTp.put(playerName, location);
				}
        	} catch (Exception e) {}
        }
	}
	
	public void onJoinEvent(PlayerJoinEvent event) {
		String name = event.getPlayer().getName();
		if (toTp.containsKey(name)) {
			teleportPlayer(event.getPlayer(), toTp.get(name));
			toTp.remove(name);
		}
	}
	
	private ArrayList<String> getHomesList(Player player) {
		ArrayList<String> homes = new ArrayList<String>();
		
		ResultSet result = null;
		Connection con = null;
		try {
			con = SQL.getConnection();

			Statement st = (Statement) con.createStatement(); 
			result = st.executeQuery("SELECT `home_name` FROM BungeeHomes WHERE `player`='" + player.getName() + "';");
			while (result.next()) {
				homes.add(result.getString("home_name"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return homes;
	}
	
	private void teleportPlayer(Player player, String location) {
//		log("TP: " + player.getName() + ": " + location);
		String[] array = location.split(";");
		if (array.length == 0) {
			msg(player, "The location for this home is blank.");
			return;
		}
		if (getServerWorld(array[0]).equals(plugin.detectedServer())) {
			Location loc = new Location(Bukkit.getWorld(array[0]), Double.parseDouble(array[1]), Double.parseDouble(array[2]), Double.parseDouble(array[3]), Float.parseFloat(array[5]), Float.parseFloat(array[4]));
			if (loc.getWorld() == null) {
				msg(player, "The location for this home has a world that is unavailable");
				return;
			}
			player.teleport(loc);
//			msg(player, "You have been teleported to your home");
		} else {
			String server = getServerWorld(array[0]);
			if (server.equals("")) {
				msg(player, "The location for this home refers to a server that doesn't exist.");
				return;
			}
			
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			
			try {
				
				out.writeUTF("Forward");
				out.writeUTF("ALL");
				out.writeUTF("DDCustomPlugin_Homes");

				ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
				DataOutputStream msgout = new DataOutputStream(msgbytes);
				msgout.writeUTF(player.getName());
				msgout.writeUTF(location);

				out.writeShort(msgbytes.toByteArray().length);
				out.write(msgbytes.toByteArray());

				player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
				
				b = new ByteArrayOutputStream();
				out = new DataOutputStream(b);

				out.writeUTF("Connect");
				out.writeUTF(server);
			
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
		}
	}
	
	private String getServerWorld(String world) {
		if (world.equals("Survival") || world.equals("TownWorld") || world.equals("End") || world.equals("Nether"))
			return "survival";
		if (world.equals("EventWorld"))
			return "events";
		if (world.equals("Build"))
			return "creative";
		if (world.equals("Destruction"))
			return "destruction";
		if (world.equals("SkyBlock"))
			return "skyblock";
		return "";
	}
	
	private String getHomeLocation(Player player, String homeName) {
		//Location string is WORLD;X;Y;Z;YAW;PITCH
		String location = "";
		ResultSet result = null;
		Connection con = null;
		try {
			con = SQL.getConnection();

			Statement st = (Statement) con.createStatement(); 
			result = st.executeQuery("SELECT * FROM `BungeeHomes` WHERE `player`='" + player.getName() + "' && `home_name`='" + homeName + "';");
			
			while (result.next()) {
				location += result.getString("world") + ";";
				location += result.getString("x") + ";";
				location += result.getString("y") + ";";
				location += result.getString("z") + ";";
				location += result.getString("yaw") + ";";
				location += result.getString("pitch");
			}

			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return location;
	}
	
	private void addHome(Player player, String homeName, Location homeLoc) {
		SQL.singleQuery("DELETE FROM `BungeeHomes` WHERE `player`='" + player.getName() + "' && `home_name`='" + homeName + "';");

		SQL.singleQuery("INSERT INTO `BungeeHomes` (`player`, `home_name`, `server`, `world`, `x`, `y`, `z`, `yaw`, `pitch`) VALUES " + 
				"('" + player.getName() + "', '" + homeName + "', '" + plugin.detectedServer() + "', '" + homeLoc.getWorld().getName() + "', " + homeLoc.getX() + ", " + homeLoc.getY() + ", " + homeLoc.getZ() + ", " + homeLoc.getYaw() + ", " + homeLoc.getPitch() + ");");

		msg(player, "Home set");
	}
	
	private void delHome(Player player, String homeName) {
		SQL.singleQuery("DELETE FROM `BungeeHomes` WHERE `player`='" + player.getName() + "' && `home_name`='" + homeName + "';");
		msg(player, "Home deleted");
	}
	
	private int getHomesAmount(Player player) {
		@SuppressWarnings("static-access")
		String rank = plugin.chat.perms.getPlayerGroups(player)[0];
		if (rank.contains("Player"))
			return 5;
		if (rank.contains("YouTuber"))
			return 5;
		if (rank.contains("Respected"))
			return 5;
		if (rank.contains("Assistant"))
			return 5;
		if (rank.contains("VIP"))
			return 10;
		if (rank.contains("Premium"))
			return 15;
		if (rank.contains("Elite"))
			return 25;
		if (rank.contains("Exclusive"))
			return 30;
		if (rank.contains("Ultimate"))
			return 50;
		if (rank.contains("DemiGod"))
			return -1;
		if (rank.contains("Mod"))
			return 10;
		if (rank.contains("VIMod"))
			return 20;
		if (rank.contains("PMod"))
			return 25;
		if (rank.contains("EMod"))
			return 35;
		if (rank.contains("ExMod"))
			return 40;
		if (rank.contains("UMod"))
			return 60;
		if (rank.contains("DGMod"))
			return -1;
		if (rank.contains("HeadMod"))
			return 100;
		if (rank.contains("Admin"))
			return -1;
		if (rank.contains("HeadAdmin"))
			return -1;
		if (rank.contains("Owner"))
			return -1;
		return 5;
	}
	
	private void msg(Player player, String msg) {
		player.sendMessage(ChatColor.DARK_BLUE + "[" + ChatColor.WHITE + "DDHomes" + ChatColor.DARK_BLUE + "] " + ChatColor.GREEN + msg);
	}

	public void log(String info) {
		plugin.getLogger().info("<Homes> " + info);
	}
}
