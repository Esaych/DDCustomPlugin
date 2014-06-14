package net.diamonddominion.esaych.global;

import java.util.ArrayList;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


public class FullMute {
	private CustomPlugin plugin;
	
	public FullMute(CustomPlugin plugin) {
		this.plugin = plugin;
	}
	
	ChatSQL sql;
	
	@SuppressWarnings("static-access")
	public void enable() {
//		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		sql = plugin.chat.sql;
		blockedCommands.add("/me");
		log("Enabled");
	}
	
	public ArrayList<String> blockedCommands = new ArrayList<String>();
	
//	@SuppressWarnings("unchecked")
//	public void onEnable() {
//		plugin.getServer().getPluginManager().registerEvents(this, plugin);
//		saveMuted();
//		try {
//			log("Loading Muted Players...");
//			reloadMuted();
//		} catch (Exception e) {
//			log("Could not load the config file!!");
//		}
//		blockedCommands = (ArrayList<String>)getMuteTime().getList("blocked-commands");
//
//		log("FullMute Plugin Enabled");
//	}
//
//	public void onDisable() {
//		log("FullMute Plugin Disabled");
//	}

	@SuppressWarnings("static-access")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("mute")) {
			if (sender instanceof Player){
				Player player = (Player)sender;
				if (!player.hasPermission("fullmute.mute")){
					msg(player, "Insufficient Permissions");
					return true;
				}
			}

			if (args.length >= 1) {
				if (args.length >= 2) {
					mute(sender, args[0], args[1], false);
				} else {
					mute(sender, args[0], null, false);	
				}
			} else {
				msg(sender, "Try /mute [Player]");
			}
			return true;

		} else if (label.equalsIgnoreCase("me")) {
			if (sender instanceof Player){
				Player player = (Player)sender;
				if (player.hasPermission("mchat.me")) {
					if (args.length != 0) {
						String meMsg = args[0];
						for (int x = 1; x < args.length; x++) {
							meMsg += " " + args[x];
						}
						meMsg = plugin.chat.utils.removeCussWords(meMsg, true, player, !player.hasPermission("ddchat.censorbypass"), ChatColor.YELLOW + "");
						if (player.hasPermission("ddchat.color")) {
							meMsg = ChatColor.translateAlternateColorCodes('&', meMsg); 
						} else {
							meMsg = plugin.chat.utils.changeCussColors(meMsg);
						}
						if (!player.hasPermission("ddchat.caps"))
							meMsg = plugin.chat.utils.removeCAPS(meMsg);
						plugin.getServer().broadcastMessage(ChatColor.DARK_RED + "*" + player.getPlayerListName().substring(0, 2) + player.getName() + ChatColor.YELLOW + " " + meMsg);
					} else {
						player.sendMessage(ChatColor.RED + "Correct usage: /me <message>");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You do not have permission to use /me");
				}
			} else {
				msg(sender, "You just try to do /me console :P YOU CAN'T");
			}
			return true;
		}
		return false;
	}

	public void mute(CommandSender sender, String player, String time, boolean def) {
		Player realPlayer = Bukkit.getPlayer(player);
		if (playerMuted(player)) {
			sql.setMuteTime(realPlayer.getName(), 0);
			if (realPlayer != null) 
				msg(realPlayer, "You are now free to talk.");
			msg(sender, player + " successfully unmuted.");
			return;
		}
		
		if (realPlayer == null) {
			msg(sender, "Player Not Found!");
			return;
		}
		player = realPlayer.getName();
		
		if (time == null) {
			if (!playerMuted(player)) {
				for (Player on : Bukkit.getOnlinePlayers()) {
					if (on.hasPermission("fullmute.mute")) {
						if (sender instanceof Player && on == (Player) sender)
							msg(on, player + " muted for the default 5 minutes");
						else {
							if (sender instanceof Player) 
								msg(on, player + " muted for the default 5 minutes by: " + ((Player) sender).getName());
							else
								msg(on, player + " muted for the default 5 minutes by: Console");	
						}
					}
				}
				if (!(sender instanceof Player))
					msg(sender, player + " muted for the default 5 minutes");
				msg(realPlayer, "You have been muted for 5 minutes.");
				msg(sender, "To specify a time, type /mute <player> <time>s,m,h");
			}
			mute(sender, player, "5m", true);
			return;
		}
		
		if (getTimeStamp(time) == -1)
			msg(sender, "Invalid time format, Ex. 20m");
		if (getTimeStamp(time) == -2)
			msg(sender, "Unknown time value, accepts s,m,h");
		if (getTimeStamp(time) == -3)
			msg(sender, "Time too long! Max allowed time is 5 hours (5h)");
		if (getTimeStamp(time) < 0)
			return;

		sql.setMuteTime(realPlayer.getName(), (int) getTimeStamp(time));
		int left = (int)(sql.getMuteTime(realPlayer.getName()) - System.currentTimeMillis() / 1000);
		msg(realPlayer, "You were muted for " + timeLeft(left));
		if (!def)
			for (Player on : Bukkit.getOnlinePlayers()) {
				if (on.hasPermission("ddchat.prefix")) {
					msg(on, player + " muted for " + timeLeft(left));
				}
			}
		if (!(sender instanceof Player))
			msg(sender, realPlayer.getName() + " successfully muted.");
	}

//	@EventHandler
//	public void onPlayerChat(AsyncPlayerChatEvent event){
//		Player player = event.getPlayer();
//		log("HA");
//		if (playerMuted(player.getName())) {
//			log("A");
//			if (checkTime(event.getPlayer().getName())) {
//				int left = (int)(plugin.chat.sql.getMuteTime("muted-players." + player.getName()) - System.currentTimeMillis() / 1000);
//				msg(event.getPlayer(), "You are muted, and will stay muted for the remaining:");
//				msg(event.getPlayer(), timeLeft(left));
//				try {
//					event.setCancelled(true);
//				} catch (Exception e) {
//					event.setMessage("");
//				}
//			}
//		}
//	}

//	@EventHandler(priority=EventPriority.HIGHEST)
	public void onCommandPreProcess(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();
		if (playerMuted(player.getName())){
			for (int i = 0; i < blockedCommands.size(); i++) {
				if (event.getMessage().startsWith(blockedCommands.get(i)))
				{
					if (checkTime(event.getPlayer().getName())) {
						int left = (int)(sql.getMuteTime(player.getName()) - System.currentTimeMillis() / 1000);
						msg(event.getPlayer(), "You are muted, and will stay muted for the remaining:");
						msg(event.getPlayer(), timeLeft(left));
						event.setCancelled(true);
					}
				}
			}
		}
	}

	public void msg(CommandSender sender, String msg){
		if (sender instanceof Player) {
			Player player = (Player) sender;
			player.sendMessage("[" + ChatColor.RED + "Mute" + ChatColor.WHITE + "] " + ChatColor.AQUA + msg);
		} else {
			log(msg);
		}
	}
	
	public boolean playerMuted(String player) {
		return sql.getMuteTime(player) != 0;
	}
	
	public int getTimeStamp(String time) {
		int timestamp = (int) (System.currentTimeMillis() / 1000);
		String unit = time.substring(time.length() - 1, time.length());
		String input = time.substring(0, time.length() - 1);
		int add = 0;
		try {
			add = Integer.parseInt(input);
		} catch (Exception e) {
			return -1;
		}
		if (unit.equalsIgnoreCase("s")) {
			timestamp += add;
		} else if (unit.equalsIgnoreCase("m")) {
			timestamp += add*60;
		} else if (unit.equalsIgnoreCase("h")) {
			timestamp += add*60*60;
		} else {
			return -2;
		}
		if (timestamp - System.currentTimeMillis() / 1000 > 60*60*5)
			return -3;
		return timestamp;
	}
	
	public boolean checkTime(String player) {
		int time = sql.getMuteTime(player);
		if (System.currentTimeMillis() / 1000 > time) {
			mute(null, player, null, false);
			return false;
		}
		return true;
	}
	
	public String timeLeft(int time) {
		String msg = "";
		int divid = 0;
		if (time >= 60*60) {
			divid = time / (60*60);
			msg += divid + " hour";
			if (divid > 1)
				msg+= "s";
			time -= divid * (60*60);
			if (time != 0)
				msg += ", ";
			log(divid + ", " + time);
		}
		if (time >= 60) {
			divid = time / 60;
			msg += divid + " minute";
			if (divid > 1)
				msg+= "s";
			time -= divid * 60;
			if (time != 0)
				msg += ", ";
			if (time != 0)
				msg += "and ";
		}
		if (time != 0) {
			divid = time;
			msg += divid + " second";
			if (divid > 1)
				msg+= "s";
		}
		return msg;
	}
	
	public void log(String info) {
		plugin.getLogger().info("<FullMute> " + info);
	}
}
