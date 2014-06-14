package net.diamonddominion.esaych.survival;

import java.util.ArrayList;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;


public class SpawnMusic {
	
	private CustomPlugin plugin;

	public SpawnMusic(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				World w = Bukkit.getWorld("Survival");
				Jukebox a = (Jukebox) w.getBlockAt(64, 70, 198).getState();
				ArrayList<Material> discs = new ArrayList<Material>();
				discs.add(Material.getMaterial(2257));
				discs.add(Material.getMaterial(2258));
				discs.add(Material.getMaterial(2260));
				discs.add(Material.getMaterial(2261));
				discs.add(Material.getMaterial(2263));
				discs.add(Material.getMaterial(2264));
				discs.add(Material.getMaterial(2267));
				Block db = w.getBlockAt(a.getBlock().getLocation().clone().add(0,-1,0));
				int type = db.getData();
				type++;
				if (type > 6) {
					type = 0;
				}
				a.setPlaying(discs.get(type));
				db.setData((byte) type);
				//				
			}
		}, 20*5, 20*60*3);
		log("Enabled");
	}

	public void log(String info) {
		plugin.getLogger().info("<SpawnMusic> " + info);
	}
}
