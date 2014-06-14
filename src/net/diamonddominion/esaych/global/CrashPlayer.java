package net.diamonddominion.esaych.global;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.Chest;


public class CrashPlayer {

	private CustomPlugin plugin;

	public CrashPlayer(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	public boolean onCommand(CommandSender sender, String args[]) {
			if (sender instanceof Player) {
				if (!((Player) sender).hasPermission("customplugin.crash")) {
					sender.sendMessage("Unknown command. Type \"/help\" for help.");
					return true;
				}
				if (args.length < 1) {
					sender.sendMessage(ChatColor.RED + "/crash [player]");
					return true;
				}
				if (Bukkit.getPlayer(args[0]) == null) {
					sender.sendMessage(ChatColor.RED + args[0] + " not found.");
					return false;
				}
				Player player = Bukkit.getPlayer(args[0]);
				Chest chest = null;
				Inventory inv = Bukkit.createInventory((InventoryHolder) chest, 360, "GET CRASHED");
				if (!player.hasPermission("customplugin.crash") || ((Player) sender).isOp()) {
					player.openInventory(inv);
					for (Player on : Bukkit.getOnlinePlayers()) {
						if (on.hasPermission("ddchat.staffchat")) {
							if (sender instanceof Player && on == (Player) sender)
								on.sendMessage(ChatColor.RED + player.getName() + " now crashing " + ChatColor.DARK_RED + ">:)");
							else {
								if (sender instanceof Player) 
									on.sendMessage(ChatColor.RED + player.getName() + " now crashing by: " + ChatColor.DARK_RED + sender.getName());
							}
						}
					}
					log(player.getName() + " now crashing");
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + args[0] + " is not a crashable player");
					return true;
				}
			} else {
				if (args.length < 1)
					return true;
				if (Bukkit.getPlayer(args[0]) == null)
					return false;
				Player player = Bukkit.getPlayer(args[0]);
				Chest chest = null;
				Inventory inv = Bukkit.createInventory((InventoryHolder) chest, 360, "Lol, get crashed");
				if (!player.hasPermission("customplugin.crash"))
					player.openInventory(inv);
			}
			return true;
		}

	public void log(String info) {
		plugin.getLogger().info("<CrashPlayer> " + info);
	}
}
