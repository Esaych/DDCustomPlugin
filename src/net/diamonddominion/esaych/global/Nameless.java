package net.diamonddominion.esaych.global;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Nameless {

	private CustomPlugin plugin;

	public Nameless(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	public boolean onCommand(CommandSender sender, String args[]) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (!player.hasPermission("customplugin.nameless")) {
				player.sendMessage(ChatColor.RED + "You can't use this command.");
				return true;
			}
			String msg = "";
			for (String arg : args) {
				msg += arg + " ";
			}
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));
		}
		return true;
	}

	public void log(String info) {
		plugin.getLogger().info("<Nameless> " + info);
	}
}
