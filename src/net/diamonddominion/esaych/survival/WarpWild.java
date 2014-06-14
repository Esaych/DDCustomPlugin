package net.diamonddominion.esaych.survival;

import java.util.ArrayList;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


public class WarpWild {
	private CustomPlugin plugin;

	public WarpWild(CustomPlugin plugin) {
		this.plugin = plugin;
	}
	public ArrayList<Player> warpedWild = new ArrayList<Player>();

	public void enable() {
		log("Enabled");
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event)
	{	
		Player player = event.getPlayer();
		if (event.getMessage().equalsIgnoreCase("/warp wild")) {
			if (!warpedWild.contains(player)) {
				int min = -10000, max = 10000;
				int x = min + (int)(Math.random() * ((max - min) + 1));
				int z = min + (int)(Math.random() * ((max - min) + 1));
				int y;
				Location loc = new Location(player.getWorld(), x, 60, z);
				for (y = 60; y < 200; y++) {
					loc = new Location(player.getWorld(), x, y, z);
					if (player.getWorld().getBlockAt(loc).getType() == Material.WATER || player.getWorld().getBlockAt(loc).getType() == Material.STATIONARY_WATER) {
						x = min + (int)(Math.random() * ((max - min) + 1));
						z = min + (int)(Math.random() * ((max - min) + 1));
						y = 60;
					}
					if (player.getWorld().getBlockAt(loc).getType() == Material.AIR)
						break;
				}
				player.teleport(loc.clone().add(0.5, 0, 0.5));
				player.sendMessage(ChatColor.GOLD + "Warping to " + ChatColor.RED + "Wild" + ChatColor.GOLD + ". " + ChatColor.DARK_RED + "Caution: " + ChatColor.RED + "Might take some time to load.");
				warpedWild.add(player);
			} else {
				player.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED + "You may only use warp wild once.");
			}
			event.setCancelled(true);
		}
	}

	public void log(String info) {
		plugin.getLogger().info("<WarpWild> " + info);
	}
}
