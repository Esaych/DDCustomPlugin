package net.diamonddominion.esaych.unused;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class RestartWarning {
	private CustomPlugin plugin;

	public RestartWarning(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	public boolean onCommand(CommandSender sender) {
		if (!(sender instanceof Player)) {
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&1&l==&4&oWARNING&1&l== &cServer restarting in 1 minute! &1&l==&4&oWARNING&1&l=="));
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override 
				public void run() {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "plugman unload PvPReward");
				}
			}, 60 * 20 * 1 - 3);
		}
		return true;
	}

	public void log(String info) {
		plugin.getLogger().info("<RestartWarning> " + info);
	}
}
