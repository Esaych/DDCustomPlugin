package net.diamonddominion.esaych.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

public class BungeeTeleport {
	CustomPlugin plugin;
	
	public BungeeTeleport(CustomPlugin plugin) {
		this.plugin = plugin;
	}
	
	Map<String, String> toTp = new HashMap<String, String>();
	
	private void teleportPlayer(Player player, String location) {
//		log("TP: " + player.getName() + ": " + location);
		String[] array = location.split(";");
		if (array.length == 0) {
			return;
		}
		if (getServerWorld(array[0]).equals(plugin.detectedServer())) {
			Location loc = new Location(Bukkit.getWorld(array[0]), Double.parseDouble(array[1]), Double.parseDouble(array[2]), Double.parseDouble(array[3]), Float.parseFloat(array[5]), Float.parseFloat(array[4]));
			if (loc.getWorld() == null) {
				return;
			}
			player.teleport(loc);
		} else {
			String server = getServerWorld(array[0]);
			if (server.equals("")) {
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
	
	public void onBungeeMessageReceived(String channel, Player player, byte[] message) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
        String subchannel = null;
		try {
			subchannel = in.readUTF();
		} catch (IOException e) {
			System.out.println("Could not read plugin message");
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
	
	public void onJoinEvent(PlayerJoinEvent event) {
		String name = event.getPlayer().getName();
		if (toTp.containsKey(name)) {
			teleportPlayer(event.getPlayer(), toTp.get(name));
			toTp.remove(name);
		}
	}
}
