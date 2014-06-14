package net.diamonddominion.esaych.events;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;


public class EWBlockFenceWalk {
	private CustomPlugin plugin;

	public EWBlockFenceWalk(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		if (player.getWorld().getName().equals("EventWorld") && player.getLocation().add(0,-1,0).getBlock().getType().equals(Material.FENCE)) {
			Location l = player.getLocation();
			if (l.getY()-l.getBlockY() >= .5) {
				Block b = search(l);
				if (b != null) {
					player.teleport(new Location(b.getWorld(), b.getX() + .5, b.getY() + 1.5, b.getZ() + .5, l.getYaw(), l.getPitch()));
				}
			}
		}
	}
	
	public Block search(Location l) {
		for (int y = -3; y <= 0; y++) {
			Block b = l.getWorld().getBlockAt(l.getBlockX()-1, l.getBlockY()+y, l.getBlockZ());
			if (b.getType().equals(Material.BEDROCK))
				return b;
		}
		for (int y = -3; y <= 0; y++) {
			Block b = l.getWorld().getBlockAt(l.getBlockX()+1, l.getBlockY()+y, l.getBlockZ());
			if (b.getType().equals(Material.BEDROCK))
				return b;
		}
		
		for (int y = -3; y <= 0; y++) {
			Block b = l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY()+y, l.getBlockZ()-1);
			if (b.getType().equals(Material.BEDROCK))
				return b;
		}
		for (int y = -3; y <= 0; y++) {
			Block b = l.getWorld().getBlockAt(l.getBlockX(), l.getBlockY()+y, l.getBlockZ()+1);
			if (b.getType().equals(Material.BEDROCK))
				return b;
		}

		for (int y = -3; y <= 0; y++) {
			Block b = l.getWorld().getBlockAt(l.getBlockX()-1, l.getBlockY()+y, l.getBlockZ()-1);
			if (b.getType().equals(Material.BEDROCK))
				return b;
		}
		for (int y = -3; y <= 0; y++) {
			Block b = l.getWorld().getBlockAt(l.getBlockX()+1, l.getBlockY()+y, l.getBlockZ()-1);
			if (b.getType().equals(Material.BEDROCK))
				return b;
		}
		
		for (int y = -3; y <= 0; y++) {
			Block b = l.getWorld().getBlockAt(l.getBlockX()-1, l.getBlockY()+y, l.getBlockZ()+1);
			if (b.getType().equals(Material.BEDROCK))
				return b;
		}
		for (int y = -3; y <= 0; y++) {
			Block b = l.getWorld().getBlockAt(l.getBlockX()+1, l.getBlockY()+y, l.getBlockZ()+1);
			if (b.getType().equals(Material.BEDROCK))
				return b;
		}
		
		return null;
	}

	public void log(String info) {
		plugin.getLogger().info("<EWBlockFenceWalk> " + info);
	}
}
