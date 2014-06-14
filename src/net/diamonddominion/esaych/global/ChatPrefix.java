package net.diamonddominion.esaych.global;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ChatPrefix {
	private CustomPlugin plugin;

	private ChatSQL sql;
	
	public ChatPrefix(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("static-access")
	public void enable() {
		sql = plugin.chat.sql;
		log("Enabled");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length > 1){
				Player otherplayer = Bukkit.getPlayer(args[0]);
				if (otherplayer != null){
					args[0] = otherplayer.getName();
					String prefix = args[1];
					for (int x = 2; x < args.length; x++){
						prefix = prefix + " " + args[x];
					}
					sql.setPrefix(args[0], prefix);
					log(otherplayer.getName() + "'s name has been formatted to: " + prefix);
				} else {
					log(args[0] + " not found");
				}
			} else {
				log("Try: /fullprefix [Player] [Name Format]");
			}
			return true;
		}
		Player player = (Player)sender;
		if (player.hasPermission("mchat.prefix") || player.isOp()) { // VIP User
			if (args.length != 0) { // Must have arguments
				if (player.hasPermission("mchat.prefix.admin") || player.isOp()) {
					Player otherplayer = Bukkit.getPlayer(args[0]);
					if (otherplayer != null && args.length != 1){ //Administrator changes someone's name
						args[0] = otherplayer.getName();
						if (otherplayer.hasPermission("mchat.prefix")) {
							String prefix = args[1];
							for (int x = 2; x < args.length; x++){
								prefix = prefix + " " + args[x];
							}
							if (cutLength(prefix) <= 10) {
								sql.setPrefix(args[0], removeFormattingCodes("&e<" + prefix + "&e> ") + "%name%");
								player.sendMessage(ChatColor.GREEN + args[0] + "'s name has been formatted to:");
								player.sendMessage(prefixWithFormatting("&e<" + prefix + "&e> ") + args[0]);
								otherplayer.sendMessage(ChatColor.GREEN + "Your name has been formatted to:");
								otherplayer.sendMessage(prefixWithFormatting("&e<" + prefix + "&e> ") + args[0] + " by " + player.getName());
								log(player.getName() + " changed " + args[0] + "'s name to be formatted: " + removeFormattingCodes("&e<" + prefix + "&e> ") + otherplayer.getPlayerListName());
								//[Administrator] changed [Player]'s name to be formatted <[Prefix]&6> [Player]
							} else {
								player.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "Prefix too long.");
							}
						} else {
							player.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + args[0] + " lacks permission for a prefix.");
						}
					} else { //Administrator changes his own name
						String prefix = args[0];
						for (int x = 1; x < args.length; x++){
							prefix = prefix + " " + args[x];
						}
						if (cutLength(prefix) <= 10) {
							sql.setPrefix(player.getName(), removeFormattingCodes("&e<" + prefix + "&e> ") + "%name%");
							player.sendMessage(ChatColor.GREEN + "Your name has been formatted to:");
							player.sendMessage(prefixWithFormatting("&e<" + prefix + "&e> ") + player.getPlayerListName());
							log(player.getName() + " changed their name to be formatted: " + removeFormattingCodes("&e<" + prefix + "&e> ") + player.getPlayerListName());
							//[AdministratorUsername] changed his name to be formatted: <[Prefix]&c> [AdministratorUsername]
						} else {
							player.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "Prefix too long.");
						}
					}
				} else {
					//Regular VIP user changing his name:
					String prefix = args[0];
					for (int x = 1; x < args.length; x++){
						prefix = prefix + " " + args[x];
					}
					if (cutLength(prefix) <= 10) {
						sql.setPrefix(player.getName(), removeFormattingCodes("&e<" + prefix + "&e> ") + "%name%");
						player.sendMessage(ChatColor.GREEN + "Your name has been formatted to:");
						player.sendMessage(prefixWithFormatting("&e<" + prefix + "&e> ") + player.getPlayerListName());
						log(player.getName() + " changed their name to be formatted: " + removeFormattingCodes("&e<" + prefix + "&e> ") + player.getPlayerListName());
						//[Player] changed his name to be formatted: <[Prefix]&6> [Player]
					} else {
						player.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + "Prefix too long.");
					}
				}
			} else { //No arguments???? Help Message
				player.sendMessage(ChatColor.YELLOW + "To change your perfix:");
				player.sendMessage(ChatColor.AQUA + "/prefix [" + ChatColor.GREEN + "Prefix" + ChatColor.AQUA+ "]");
				player.sendMessage(ChatColor.YELLOW + "To delete your perfix:");
				player.sendMessage(ChatColor.AQUA + "/delprefix");
				if (player.hasPermission("mchat.prefix.admin") || player.isOp()) { //Extra Administrator Help
					player.sendMessage(ChatColor.ITALIC + "-=-=-=-=-=-= Admin Commands =-=-=-=-=-=-");
					player.sendMessage(ChatColor.GOLD + "To change another player's prefix:");
					player.sendMessage(ChatColor.AQUA + "/prefix [" + ChatColor.GREEN + "Player" + ChatColor.AQUA + "] [" + ChatColor.GREEN + "Prefix" + ChatColor.AQUA+ "]");
					player.sendMessage(ChatColor.GOLD + "To delete another player's perfix:");
					player.sendMessage(ChatColor.AQUA + "/delprefix [" + ChatColor.GREEN + "Player" + ChatColor.AQUA+ "]");
					player.sendMessage(ChatColor.GOLD + "Reformat your name:");
					player.sendMessage(ChatColor.AQUA + "/fullprefix [" + ChatColor.GREEN + "Name Format" + ChatColor.AQUA + "]");
					player.sendMessage(ChatColor.GOLD + "Reformat another player's name:");
					player.sendMessage(ChatColor.AQUA + "/fullprefix [" + ChatColor.GREEN + "Player" + ChatColor.AQUA + "] [" + ChatColor.GREEN + "Name Format" + ChatColor.AQUA + "]");
					player.sendMessage(ChatColor.GOLD + "[Player] must be the full, case sensitive player's name");
				}
			}
		} else {
			player.sendMessage(ChatColor.RED + "You can't change you Prefix! You need to be a Elite status or higher!");
		}
		return true;
	}

	public boolean delPrefix(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player)sender;
			if (player.hasPermission("mchat.prefix") || player.isOp()) { // VIP User
				if (player.hasPermission("mchat.prefix.admin") || player.isOp()) { // Administrator
					if (args.length != 0) {
						Player otherplayer = Bukkit.getPlayer(args[0]);
						if (otherplayer != null) {
							args[0] = otherplayer.getName();
						}
						if (sql.getPrefix(args[0]) == null) {
							player.sendMessage(ChatColor.GREEN + args[0] + " never had a prefix");
						} else {
							sql.setPrefix(args[0], null);
							player.sendMessage(ChatColor.GREEN + args[0] + "'s prefix has been removed.");
							if (otherplayer != null){
								otherplayer.sendMessage(ChatColor.GREEN + "Your prefix has been removed by " + player.getName());
							}
							log(player.getName() + " removed " + args[0] + "'s prefix");
						}
					} else { // Administrator resetting his own prefix
						sql.setPrefix(player.getName(), null);
						player.sendMessage(ChatColor.GREEN + "Your prefix has been removed.");
						log(player.getName() + " removed their prefix");
					}
				} else { // Regular VIP
					sql.setPrefix(player.getName(), null);
					player.sendMessage(ChatColor.GREEN + "Your prefix has been removed.");
				}
			} else {
				player.sendMessage(ChatColor.RED + "You can't change you Prefix! You need to be a Sponsor, Premium or Boss!");
			}
		} else { //Console is executing
			if (args.length != 0) {
				sql.setPrefix(args[0], null);
				log(ChatColor.GREEN + args[0] + "'s prefix has been removed.");
			} else { // Administrator resetting his own prefix
				log("Please provide a username to reset");
			}				
		}
		return true;
	}

	public boolean fullPrefix(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player)sender;
			if (player.hasPermission("mchat.prefix.admin") || player.isOp()){
				if (args.length != 0){
					Player otherplayer = Bukkit.getPlayer(args[0]);
					if (otherplayer != null && args.length != 1){
						args[0] = otherplayer.getName();
						String prefix = args[1];
						for (int x = 2; x < args.length; x++){
							prefix = prefix + " " + args[x];
						}
						sql.setPrefix(args[0], prefix);
						player.sendMessage(ChatColor.GREEN + otherplayer.getName() + "'s name has been formatted to: " + prefix);
						log(args[0] + "'s name has been formatted to: " + prefix);
					} else { //Changing his own name
						String prefix = args[0];
						for (int x = 1; x < args.length; x++){
							prefix = prefix + " " + args[x];
						}
						sql.setPrefix(player.getName(), prefix);
						player.sendMessage(ChatColor.GREEN + "Your name has been formatted to: " + prefix);
						log(player.getName() + "'s name has been formatted to: " + prefix);
					}
				} else {
					player.sendMessage(ChatColor.GOLD + "Reformat your name:");
					player.sendMessage(ChatColor.AQUA + "/fullprefix [" + ChatColor.GREEN + "Name Format" + ChatColor.AQUA + "]");
					player.sendMessage(ChatColor.GOLD + "Reformat another player's name:");
					player.sendMessage(ChatColor.AQUA + "/fullprefix [" + ChatColor.GREEN + "Player" + ChatColor.AQUA + "] [" + ChatColor.GREEN + "Name Format" + ChatColor.AQUA + "]");
				}
			} else {
				if (player.hasPermission("mchat.prefix")) {
					player.sendMessage(ChatColor.RED + "You can't change you Name! Only your prefix with /prefix");
				} else {
					player.sendMessage(ChatColor.RED + "You must be an administrator to use this command.");
				}
			}
		}
		return true;
	}

	public String prefixWithFormatting(String input){	
		return input.replaceAll("&a", ChatColor.GREEN + "&a").replaceAll("&b", ChatColor.AQUA + "&b").replaceAll("&c", ChatColor.RED + "&c").replaceAll("&d", ChatColor.LIGHT_PURPLE + "&d").replaceAll("&e", ChatColor.YELLOW + "&e").replaceAll("&f", ChatColor.WHITE + "&f")
				.replaceAll("&0", ChatColor.BLACK + "&0").replaceAll("&1", ChatColor.DARK_BLUE + "&1").replaceAll("&2", ChatColor.DARK_GREEN + "&2").replaceAll("&3", ChatColor.DARK_AQUA + "&3").replaceAll("&4", ChatColor.DARK_RED + "&4").replaceAll("&5", ChatColor.DARK_PURPLE +  "&5")
				.replaceAll("&6", ChatColor.GOLD + "&6").replaceAll("&7", ChatColor.GRAY + "&7").replaceAll("&8", ChatColor.DARK_GRAY + "&8").replaceAll("&9", ChatColor.BLUE + "&9")
				.replaceAll("&k", "").replaceAll("&l", "").replaceAll("&m", "").replaceAll("&n", "").replaceAll("&o", "").replaceAll("&r", "");
	}

	public String removeFormattingCodes(String input){
		return input.replaceAll("&k", "").replaceAll("&l", "").replaceAll("&m", "").replaceAll("&n", "").replaceAll("&o", "").replaceAll("&r", "");
	}

	public int cutLength(String prefix){
		return removeColorCodes(prefix).length();
	}

	public String removeColorCodes(String input){
		return input.replaceAll("&a", "").replaceAll("&b", "").replaceAll("&c", "").replaceAll("&d", "").replaceAll("&e", "").replaceAll("&f", "")
				.replaceAll("&0", "").replaceAll("&1", "").replaceAll("&2", "").replaceAll("&3", "").replaceAll("&4", "").replaceAll("&5", "")
				.replaceAll("&6", "").replaceAll("&7", "").replaceAll("&8", "").replaceAll("&9", "")
				.replaceAll("&k", "").replaceAll("&l", "").replaceAll("&m", "").replaceAll("&n", "").replaceAll("&o", "").replaceAll("&r", "");
	}

	public void log(String info) {
		plugin.getLogger().info("<ChatPrefix> " + info);
	}
}
