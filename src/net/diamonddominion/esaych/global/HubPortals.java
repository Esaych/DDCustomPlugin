package net.diamonddominion.esaych.global;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;


public class HubPortals {
	private CustomPlugin plugin;

	public HubPortals(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	private ArrayList<Player> queue = new ArrayList<Player>();
	
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!queue.contains(player)) {
			Location playerLoc = player.getLocation();
			if (playerLoc.getY() < 11) {
				if (inCreativeArea(playerLoc)) {
					send(player, "creative");
				}
				if (inEventsArea(playerLoc)) {
					send(player, "events");
				}
				if (inDestructionArea(playerLoc)) {
					send(player, "destruction");
				}
				if (inSkyBlockArea(playerLoc)) {
					send(player, "skyblock");
				}
				if (inEWPortal(playerLoc)) {
					send(player, "survival");
				}
				if (inSBPortal(playerLoc)) {
					send(player, "survival");
				}
				if (inCRPortal(playerLoc)) {
					send(player, "survival");
				}
			}
		}
	}
	
	private void send(Player player, String server) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
		queue.add(player);
	}
	
	public boolean inCreativeArea(Location l) {
		

		if (l.getWorld().getName().equals("Survival"))
			if (l.distance(new Location(l.getWorld(), 76.5, 0, 186.5)) < 10)
				return true;
		return false;
	}

	public boolean inEventsArea(Location l) {
		

		if (l.getWorld().getName().equals("Survival"))
			if (l.distance(new Location(l.getWorld(), 76.5, 0, 210.5)) < 10)
				return true;
		return false;
	}

	public boolean inDestructionArea(Location l) {
		

		if (l.getWorld().getName().equals("Survival"))
			if (l.distance(new Location(l.getWorld(), 52.5, 0, 186.5)) < 10)
				return true;
		return false;
	}

	public boolean inSkyBlockArea(Location l) {
		

		if (l.getWorld().getName().equals("Survival"))
			if (l.distance(new Location(l.getWorld(), 52.5, 0, 210.5)) < 10)
				return true;
		return false;
	}

	public boolean inEWPortal(Location l) {
		

		if (l.getWorld().getName().equals("EventWorld"))
			if (l.distance(new Location(l.getWorld(), -455.5, 0, -508.5)) < 10)
				return true;
		return false;
	}
	
	public boolean inSBPortal(Location l) {
		

		if (l.getWorld().getName().equals("SkyBlock"))
			if (l.distance(new Location(l.getWorld(), 38.5, 0, 110.5)) < 10)
				return true;
		return false;
	}

	public boolean inCRPortal(Location l) {
		

		if (l.getWorld().getName().equals("Build"))
			if (l.distance(new Location(l.getWorld(), 14.5, 0, 0.5)) < 10)
				return true;
		return false;
	}
	
	public boolean inAPortal(Player player) {
		Location l = player.getLocation();
		if (l.getY() < 11) {
			if (inCreativeArea(l))
				return true;
			if (inEventsArea(l))
				return true;
			if (inDestructionArea(l))
				return true;
			if (inSkyBlockArea(l))
				return true;
			if (inEWPortal(l))
				return true;
			if (inSBPortal(l))
				return true;
			if (inCRPortal(l))
				return true;
		}
		return false;
	}
	
	public void onPlayerFallEvent(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.FALL && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (inAPortal(player)) {
				event.setCancelled(true);
			}
		}
	}
	
	public void onJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (inAPortal(player)) {
			player.setFallDistance(0);
			player.teleport(player.getLocation().clone().add(0,20,0));
			plugin.getServer().dispatchCommand(player, "worldspawn");
		}
		if (queue.contains(player)) {
			queue.remove(player);
		}
	}

	public void log(String info) {
		plugin.getLogger().info("<HubPortals> " + info);
	}
}
