package net.diamonddominion.esaych.skyblock;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;


public class SkyBlockTP {
	private CustomPlugin plugin;

	public SkyBlockTP(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	public void onPlayerInteract(PlayerInteractEvent event) { 
		Player player = event.getPlayer();
		if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.STONE_PLATE) {
			Location loc = event.getClickedBlock().getLocation();
			int x = loc.getBlockX();
			int z = loc.getBlockZ();
			if (z == 150 && x <= -2 && x >= -4) {
				Bukkit.dispatchCommand(player, "island");
			}
		}
	}

	public void log(String info) {
		plugin.getLogger().info("<SkyBlockTP> " + info);
	}
}
