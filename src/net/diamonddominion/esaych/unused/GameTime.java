package net.diamonddominion.esaych.unused;

import java.util.Calendar;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;


public class GameTime {
	private CustomPlugin plugin;

	public GameTime(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	public void enable() {
		Calendar time = Calendar.getInstance();
		int hour = time.get(Calendar.HOUR_OF_DAY);
		hour = hour + 1;
		if (hour < 0)
			hour = 24 + hour;
		log("<GameTime> Hour is: " + hour);
		if (hour <= 7 || hour >= 20) {
			log("<GameTime> Setting EventWorld to night");
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				@Override 
				public void run() {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tl set EventWorld night");
				}
			}, 60 * 20 * 1);
		} else {
			log("<GameTime> Setting EventWorld to day");
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				@Override 
				public void run() {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tl set EventWorld day");
				}
			}, 60 * 20 * 1);
		}
		log("<GameTime> Enabled");
		log("Enabled");
	}

	public void log(String info) {
		plugin.log("<GameTime> " + info);
	}
}
