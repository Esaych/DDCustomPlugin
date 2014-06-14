package net.diamonddominion.esaych.global;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.diamonddominion.esaych.CustomPlugin;
import net.diamonddominion.esaych.util.SQL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


public class CommandModifier {
	private CustomPlugin plugin;

	public CommandModifier(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();
		String command = event.getMessage().toLowerCase();
		if ((command.startsWith("/sg") || event.getMessage().toLowerCase().startsWith("/survivalgames")) && !event.getPlayer().getWorld().getName().equals("EventWorld")) {
			Bukkit.dispatchCommand(player, "warp sg");
			event.setCancelled(true);
		}
		
		if (command.startsWith("/tempban ")) {
			if (player.hasPermission("customplugin.ban")) {
				String[] args = event.getMessage().split(" ");
				String arg = args[2];
				long time = getTimeStamp(arg);
				if (time == -3) {
					event.setMessage("/tempban " + args[1] + " 5h");
					event.getPlayer().sendMessage(ChatColor.GREEN + "Mods can only ban up to 5 hours.");
				}
			}
		}
		
		if (command.startsWith("/auc start") || command.startsWith("/auction start")) {
			String msg = event.getMessage();
			if (msg.split(" ").length == 3 && event.getPlayer().getItemInHand() != null) {
				event.setMessage("/auc start " + event.getPlayer().getItemInHand().getAmount() + " " + msg.split(" ")[2] + " 10");
			}
		}
		
		if (command.equalsIgnoreCase("/pl") || command.equalsIgnoreCase("/plugins")) {
			event.setMessage("/plugman list");
		}
		
		if (command.equalsIgnoreCase("/hns")) {
			event.setMessage("/has");
		}
		
		if (command.startsWith("/hns") || command.startsWith("/hidenseek") || command.startsWith("/bh") || command.startsWith("/hideandseek") || command.startsWith("/has") || command.startsWith("/ph") || command.startsWith("/blockhunt") || command.startsWith("/prophunt")) {
			if (!command.contains(" ")) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.BLUE + "_______.[" + ChatColor.YELLOW + " HideNSeek " + ChatColor.BLUE + "]._______");
				player.sendMessage(ChatColor.AQUA + "Welcome to HideNSeek");
				player.sendMessage(ChatColor.AQUA + "Type " + ChatColor.YELLOW + "/hns join" + ChatColor.AQUA + " to join the fun!");
				player.sendMessage(ChatColor.AQUA + "Type " + ChatColor.YELLOW + "/hns leave" + ChatColor.AQUA + " if you need to go.");
				player.sendMessage(ChatColor.AQUA + "Type " + ChatColor.YELLOW + "/hns shop" + ChatColor.AQUA + " to check out the shop.");
				player.sendMessage(ChatColor.BLUE + "_______.[" + ChatColor.YELLOW + " HideNSeek " + ChatColor.BLUE + "]._______");
			}
			if (command.contains("j")) {
				event.setMessage("/has join Buried");
			}
			if (command.contains("l")) {
				event.setCancelled(true);
				Bukkit.dispatchCommand(event.getPlayer(), "has leave");
			}
			if (command.contains("shop")) {
				event.setMessage("/has shop");
			}
		}
		
		if (command.startsWith("/tnt ")) {
			event.setCancelled(true);
		}
		
		if (command.startsWith("/baltop") || command.startsWith("/balancetop")) {
			event.setMessage("/money top");
		}
		
		if (command.startsWith("/bal")) {
			event.setMessage("/money");
		}
		
		if (command.startsWith("/listhomes")) {
			event.setMessage("/homes");
		}
		
		if (command.startsWith("/warp ew") || command.startsWith("/warp event")) {
//			event.setMessage("/server events");
			event.setCancelled(true);
			tpServer(player, "events");
		} else
		
		if (command.startsWith("/warp sb") || command.startsWith("/warp sky")) {
//			event.setMessage("/server skyblock");
			event.setCancelled(true);
			tpServer(player, "skyblock");
		} else 
		
		if (command.startsWith("/warp ds") || command.startsWith("/warp dest")) {
//			event.setMessage("/server destruction");
			event.setCancelled(true);
			tpServer(player, "destruction");
		} else
		
		if (command.startsWith("/warp b") || command.startsWith("/warp build")) {
//			event.setMessage("/server build");
			event.setCancelled(true);
			tpServer(player, "creative");
		} else
		
		if (command.startsWith("/warp sv") || command.startsWith("/warp surviv")) {
//			event.setMessage("/server survival");
//			event.setCancelled(true);
			tpServer(player, "survival");
		} else
			
		if (command.startsWith("/warp ")) {
			event.setCancelled(true);
			String warpName = command.split(" ")[1];
			String server = "";
			String world = "";
			String x = "";
			String y = "";
			String z = "";
			String yaw = "";
			String pitch = "";
			ResultSet result = null;
			Connection con = null;
			try {
				con = SQL.getConnection();

				Statement st = (Statement) con.createStatement(); 
				result = st.executeQuery("SELECT * FROM `BungeeWarps` WHERE `warpname`='" + warpName.toLowerCase() + "';");
				
				while (result.next()) {
					server = result.getString("server");
					world = result.getString("world");
					x = result.getString("x");
					y = result.getString("y");
					z = result.getString("z");
					yaw = result.getString("yaw");
					pitch = result.getString("pitch");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (server.equals("")) {
				player.sendMessage(ChatColor.RED + "That warp does not exist");
			}
			if (server.equals(plugin.detectedServer())) {
				player.teleport(new Location(Bukkit.getWorld(world), Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z), Float.parseFloat(yaw), Float.parseFloat(pitch)));
			} else {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);
				
				try {
					
					out.writeUTF("Forward");
					out.writeUTF("ALL");
					out.writeUTF("DDCustomPlugin_Homes");

					ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
					DataOutputStream msgout = new DataOutputStream(msgbytes);
					msgout.writeUTF(player.getName());
					msgout.writeUTF(world + ";" + x + ";" + y + ";" + z + ";" + pitch + ";" + yaw);

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
		
		if (event.getPlayer().hasPermission("permissions.manage")) {
			if (event.getMessage().startsWith("/promote "))
				BungeeGlobalCommand.sendCommand("pex reload");
			else if (event.getMessage().startsWith("/demote "))
				BungeeGlobalCommand.sendCommand("pex reload");
			else if (event.getMessage().startsWith("/pex user "))
				BungeeGlobalCommand.sendCommand("pex reload");
		}
		if (command.equalsIgnoreCase("/spawn")) {
			event.setMessage("/worldspawn");
		}
		if (command.equalsIgnoreCase("/hub")) {
			event.setMessage("/globalspawn");
		}
	}
	
	private long getTimeStamp(String time) {
		long timestamp = System.currentTimeMillis() / 1000;
		String unit = time.substring(time.length() - 1, time.length());
		String input = time.substring(0, time.length() - 1);
		int add = 0;
		try {
			add = Integer.parseInt(input);
		} catch (Exception e) {
			return -1;
		}
		if (unit.equalsIgnoreCase("s")) {
			timestamp += add;
		} else if (unit.equalsIgnoreCase("m")) {
			timestamp += add*60;
		} else if (unit.equalsIgnoreCase("h")) {
			timestamp += add*60*60;
		} else if (unit.equalsIgnoreCase("d")) {
			timestamp += add*60*60*24;
		} else {
			return -2;
		}
		if (timestamp - System.currentTimeMillis() / 1000 > 60*60*5)
			return -3;
		return timestamp;
	}
	
	public void tpServer(Player player, String server) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
	}

	public void log(String info) {
		plugin.getLogger().info("<CommandModifier> " + info);
	}
}
