package net.diamonddominion.esaych.survival;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;


public class EnderEggCollection {
	private CustomPlugin plugin;

	public EnderEggCollection(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	public void onPlayerInteract(PlayerInteractEvent event) { 
		final Player player = event.getPlayer();
		if (player.getWorld().getName().equals("End")) {
			Block egg = event.getClickedBlock();
			if (egg != null && egg.getType() == Material.DRAGON_EGG) {
				event.setCancelled(true);
				egg.setType(Material.AIR);
				final Location eggLoc = egg.getLocation();
				player.getWorld().strikeLightning(eggLoc.clone().add(0,0,2));
				player.getWorld().strikeLightning(eggLoc.clone().add(0,0,-2));
				player.getWorld().strikeLightning(eggLoc.clone().add(2,0,0));
				player.getWorld().strikeLightning(eggLoc.clone().add(-2,0,0));
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@Override 
					public void run() {
						Item eggDrop = player.getWorld().dropItemNaturally(eggLoc, new ItemStack(Material.DRAGON_EGG));
						eggDrop.setVelocity(new Vector(0,0,0));
					}
				}, 20);
			}
		}
	}

	public void log(String info) {
		plugin.getLogger().info("<EnderEggCollection> " + info);
	}
}
