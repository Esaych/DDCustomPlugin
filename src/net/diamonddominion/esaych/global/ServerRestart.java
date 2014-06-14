package net.diamonddominion.esaych.global;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ServerRestart {
	private CustomPlugin plugin;

	public ServerRestart(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	public boolean onRestartCommand(CommandSender sender) {
		if (sender instanceof Player)
			return false;
		cmd("save-all");
		if (plugin.detectedServer().equals("survival")) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				plugin.commandModifier.tpServer(player, "events");
			}
		} else {
			for (Player player : Bukkit.getOnlinePlayers()) {
				plugin.commandModifier.tpServer(player, "survival");
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			@Override
			public void run() {
				cmd("stop");
			}
		}, 2);
		return true;
	}
	
	public boolean onWarnCommand(CommandSender sender) {
		if (sender instanceof Player)
			return false;
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&1&l==&4&oWARNING&1&l== &cServer restarting in 1 minute! &1&l==&4&oWARNING&1&l=="));
		return true;
	}
	
	private void cmd(String cmd) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
	}

	public void log(String info) {
		plugin.getLogger().info("<ServerRestart> " + info);
	}
}
