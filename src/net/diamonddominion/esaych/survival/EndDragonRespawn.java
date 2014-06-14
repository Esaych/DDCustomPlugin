package net.diamonddominion.esaych.survival;

import java.util.ArrayList;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;


public class EndDragonRespawn {
	
	private CustomPlugin plugin;
	
	public EndDragonRespawn(CustomPlugin plugin) {
		this.plugin = plugin;
	}
	
	private World End;
	
	private ArrayList<String> crystalLocations = new ArrayList<String>();
	
	@SuppressWarnings("deprecation")
	public void enable() {
		log("Enabling...");
//		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override 
			public void run() {
				End = Bukkit.getWorld("End");
				if (End == null) {
					plugin.getLogger().warning("No End world found!");
				}
			}
		}, 5 * 20);
		plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
			@Override  
			public void run() {
				scanEnd(false);
			}
		}, 20 * 6, 20 * 60);
		log("Searching for End shortly.");
		
		crystalLocations.add("-8,70,15");
		crystalLocations.add("-26,75,20");
		crystalLocations.add("-26,93,31");
		crystalLocations.add("-66,77,62");
		crystalLocations.add("-43,81,69");
		crystalLocations.add("-42,74,114");
		crystalLocations.add("-30,72,96");
		crystalLocations.add("-11,82,119");
		crystalLocations.add("-2,83,80");
		crystalLocations.add("29,94,65");
		crystalLocations.add("58,85,50");
		crystalLocations.add("50,78,13");
		crystalLocations.add("60,89,-2");
		crystalLocations.add("92,79,5");
		crystalLocations.add("72,97,-6");
		crystalLocations.add("75,74,-38");
		crystalLocations.add("32,82,-83");
		crystalLocations.add("6,76,-37");
		crystalLocations.add("-11,81,-82");
		crystalLocations.add("-46,91,-66");
		crystalLocations.add("-53,88,-30");
		crystalLocations.add("-41,71,-20");
		crystalLocations.add("-101,82,-8");

		log("Enabled");
	}
	
	public void scanEnd(final boolean spawnNew) {
		if (End == null) {
			End = Bukkit.getWorld("End");
		} else {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					boolean found = false;
					ArrayList<Location> foundCrystals = new ArrayList<Location>();
					for (Chunk chunk : End.getLoadedChunks()) {
						for (Entity e : chunk.getEntities()) {
							if (e instanceof EnderDragon) {
								if (found)
									e.remove();
								found = true;
							}
							if (e instanceof EnderCrystal) {
								boolean cfound = false;
								for (Location l : foundCrystals) {
									if (e.getLocation().distance(l) < 3) {
										e.remove();
										cfound = true;
										continue;
									}
								}
								if (!cfound) {
									foundCrystals.add(e.getLocation());
								}
							}
						}
					}
					if (!found && spawnNew) {
						Location loc = new Location(End, 0, 85, -1.5);
						End.spawnEntity(loc, EntityType.ENDER_DRAGON);
						respawnEnderCrystals();
						plugin.getServer().broadcastMessage(ChatColor.BLACK + "[" + ChatColor.DARK_RED + "EndDragonRespawn" + ChatColor.BLACK + "]" + ChatColor.RED + " The dragon has resurrected!");
						log("EnderDragon Respawned!");
					} else {
						if (spawnNew)
							log("Dragon found. No need to respawn.");
					}
				}
			}, 200L);
		}
	}
	
	public void respawnEnderCrystals() {
		for (String strLoc : crystalLocations) {
			String[] strL = strLoc.split(",");
			Location loc = new Location(End, Integer.parseInt(strL[0]) + .5, Integer.parseInt(strL[1]), Integer.parseInt(strL[2]) + .5);
			boolean pre = false;
			for (Entity e : End.getEntities()) {
				if (e instanceof EnderCrystal) {
					if (e.getLocation().distance(loc) < 3) {
						pre = true;
						break;
					}
				}
			}
			if (pre)
				continue;
			loc.getBlock().setType(Material.BEDROCK);
			End.spawnEntity(loc, EntityType.ENDER_CRYSTAL);
			Location search = loc.clone();
			search.add(0, -1, 0);
			while (!End.getBlockAt(search).getType().equals(Material.BEDROCK)) {
				search.add(0, -1, 0);
//				log(search.getBlock().getType().toString());
				if (search.getBlockY() <= 1) {
					log("ERROR! SPAWN CRYSTAL AT: " + strLoc + " DOES NOT HAVE BEDROCK.");
					break;
				}
			}
			if (search.getBlockY() <= 1) {
				continue;
			}
			ArrayList<Location> bedrockLayer = new ArrayList<Location>();
			for (int x = search.getBlockX() - 3; x <= search.getBlockX() + 3; x++) {
				for (int z = search.getBlockZ() - 3; z <= search.getBlockZ() + 3; z++) {
					Location l = new Location(End, x, search.getY(), z);
					if (l.getBlock().getType().equals(Material.BEDROCK)) {
						bedrockLayer.add(l);
					}
				}
			}
			for (Location l : bedrockLayer) {
				for (int y = l.getBlockY() + 1; y < loc.getBlockY(); y++) {
					Block b = End.getBlockAt(l.getBlockX(), y, l.getBlockZ());
					if (!b.getType().equals(Material.OBSIDIAN)) 
						b.setType(Material.OBSIDIAN);
				}
			}
		}
	}
	
//	@EventHandler
	public void onPlayerGoesToEnd(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		if (!player.getWorld().getEnvironment().equals(World.Environment.THE_END))
			return;
		scanEnd(true);
	}
	
//	@EventHandler
	@SuppressWarnings("deprecation")
	public void onDragonCreatesPortal(EntityCreatePortalEvent event)
	{
		if (event.isCancelled())
			return;
		
		Entity entity = event.getEntity();

		if (!(entity instanceof EnderDragon))
			return;
		
		log("The dragon has been killed!");
		
		Location enderEggLoc = entity.getLocation();
		
		if (enderEggLoc != null) {
			End.getBlockAt(enderEggLoc).setTypeId(122);
		} else {
			log("Failed to process portal, cancelling event anyway!");
		}
		event.setCancelled(true);
	}
	
	public void log(String info) {
		plugin.getLogger().info("<EndDragonRespawn> " + info);
	}
	
}
