package net.diamonddominion.esaych.events;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;


public class EWButtons {
	private CustomPlugin plugin;

	public EWButtons(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}

	public void onPlayerInteract(PlayerInteractEvent event) { 
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Location loc = event.getClickedBlock().getLocation();
			if (loc.equals(new Location(Bukkit.getWorld("EventWorld"), -458, 64, -504))) {
				Bukkit.dispatchCommand(player, "warp sg");
			}

			if (loc.equals(new Location(Bukkit.getWorld("EventWorld"), 10004, 65, 10000))) {
				Bukkit.dispatchCommand(player, "sg join 1");
			}
			if (loc.equals(new Location(Bukkit.getWorld("EventWorld"), 10004, 65, 10001))) {
				Bukkit.dispatchCommand(player, "sg join 2");
			}
			if (loc.equals(new Location(Bukkit.getWorld("EventWorld"), 10004, 65, 10002))) {
				Bukkit.dispatchCommand(player, "sg join 3");
			}
			if (loc.equals(new Location(Bukkit.getWorld("EventWorld"), 10004, 65, 10003))) {
				Bukkit.dispatchCommand(player, "sg join 4");
			}
			if (loc.equals(new Location(Bukkit.getWorld("EventWorld"), 10004, 65, 10004))) {
				Bukkit.dispatchCommand(player, "sg join 5");
			}

			if (loc.equals(new Location(Bukkit.getWorld("EventWorld"), 9995, 65, 10004))) {
				Bukkit.dispatchCommand(player, "sg join 1");
			}
			if (loc.equals(new Location(Bukkit.getWorld("EventWorld"), 9995, 65, 10003))) {
				Bukkit.dispatchCommand(player, "sg join 2");
			}
			if (loc.equals(new Location(Bukkit.getWorld("EventWorld"), 9995, 65, 10002))) {
				Bukkit.dispatchCommand(player, "sg join 3");
			}
			if (loc.equals(new Location(Bukkit.getWorld("EventWorld"), 9995, 65, 10001))) {
				Bukkit.dispatchCommand(player, "sg join 4");
			}
			if (loc.equals(new Location(Bukkit.getWorld("EventWorld"), 9995, 65, 10000))) {
				Bukkit.dispatchCommand(player, "sg join 5");
			}

			if (loc.equals(new Location(Bukkit.getWorld("EventWorld"), -453, 64, -504))) {
				Bukkit.dispatchCommand(player, "ma join");
			}
			if (loc.equals(new Location(Bukkit.getWorld("EventWorld"), -460, 64, -506))) {
				Bukkit.dispatchCommand(player, "spleef join");
			}
			if (loc.equals(new Location(Bukkit.getWorld("EventWorld"), -460, 64, -511))) {
				Bukkit.dispatchCommand(player, "pb join");
			}
			if (loc.equals(new Location(Bukkit.getWorld("EventWorld"), -451, 64, -511))) {
				Bukkit.dispatchCommand(player, "hns join");
			}
			if (loc.equals(new Location(Bukkit.getWorld("EventWorld"), -451, 64, -506))) {
				Bukkit.dispatchCommand(player, "ctf join");
			}
		}
	}
	
	public void log(String info) {
		plugin.getLogger().info("<EWButtons> " + info);
	}
}
