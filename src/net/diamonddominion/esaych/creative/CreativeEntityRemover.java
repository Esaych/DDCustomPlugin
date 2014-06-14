package net.diamonddominion.esaych.creative;

import java.util.List;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;


public class CreativeEntityRemover {
	private CustomPlugin plugin;

	public CreativeEntityRemover(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		if (plugin.detectedServer().equals("destruction")) {
			Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				@Override
				public void run() {
					List<Entity> entList = plugin.getServer().getWorld("Destruction").getEntities();

					for(Entity current : entList) {
						if (!(current instanceof Player || current instanceof ItemFrame)) {
							current.remove();
						}
					}
				}
			}, 20*5, 20*60*5);
		}
		if (plugin.detectedServer().equals("creative")) {
			Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				@Override
				public void run() {
					List<Entity> entList = plugin.getServer().getWorld("Build").getEntities();

					for(Entity current : entList) {
						if (!(current instanceof Player || current instanceof ItemFrame)) {
							current.remove();
						}
					}
				}
			}, 20*5, 20*60*5);
		}
		log("Enabled");
	}

	public void log(String info) {
		plugin.getLogger().info("<CreativeEntityRemover> " + info);
	}
}
