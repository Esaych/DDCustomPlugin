package net.diamonddominion.esaych.destruction;

import java.util.ArrayList;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;


public class TnTPunch {
	private CustomPlugin plugin;

	public TnTPunch(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}

	private ArrayList<Player> disabledTnTPunch = new ArrayList<Player>();
	
	public boolean onCommand(CommandSender sender) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (!player.getWorld().getName().equals("Destruction")) {
				player.sendMessage(ChatColor.RED + "You can only toggle the tnt punch in the Destruction World.");
			} else {
				if (disabledTnTPunch.contains(player)) {
					disabledTnTPunch.remove(player);
					player.sendMessage(ChatColor.GREEN + "TNT punch has been reenabled for you.");
				} else {
					disabledTnTPunch.add(player);
					player.sendMessage(ChatColor.GREEN + "TNT punch has been removed for you.");
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	public void onPlayerInteract(final PlayerInteractEvent event) { 
		Player player = event.getPlayer();
		if (player.getWorld().getName().equals("Destruction")) {
			if (!disabledTnTPunch.contains(player))
				Bukkit.dispatchCommand(player, "tnt");
		}
	}
	
	public void log(String info) {
		plugin.getLogger().info("<TnTPunch> " + info);
	}
}
