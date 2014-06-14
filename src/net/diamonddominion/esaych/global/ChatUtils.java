package net.diamonddominion.esaych.global;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.diamonddominion.esaych.util.SQL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;


public class ChatUtils {
	static Chat chatPlugin = null;
	
	public ChatUtils(Chat chat) {
		World sv = Bukkit.getWorld("Survival");
		if (sv != null) {
			SQL.singleQuery("TRUNCATE TABLE `dd-owners`;");
			for (int a = 0; a <= 8; a++) {
				Sign sign = (Sign) sv.getBlockAt(60+a, 71, 159).getState();
				String data = sign.getLine(1);
				data = data.replaceAll(" Owner", "").toLowerCase();
				String name = sign.getLine(2).replaceAll("§4", "").replaceAll("§2", "").replaceAll("§o", "");
				SQL.singleQuery("INSERT INTO `dd-owners` (`server`, `owner`) VALUES ('"+data+"', '"+name+"');");
			}
		}
		Connection con = SQL.getConnection();
		try {
			Statement st = (Statement) con.createStatement(); 
			ResultSet result = st.executeQuery("SELECT * FROM `dd-owners`;");

			while (result.next()) {
				String server = result.getString("server");
				String owner = result.getString("owner");
				owners.put(server, owner);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ChatUtils.chatPlugin = chat;
	}
	
	public Map<String, String> owners = new HashMap<String, String>();
	
	@SuppressWarnings("static-access")
	public String getNamePrefix(Player player) {
		List<String> groups = Arrays.asList(chatPlugin.perms.getPlayerGroups(player));
		String prefix = ChatColor.GRAY + "";
		if (groups.contains("Respected"))
			prefix = ChatColor.AQUA + "";
		if (groups.contains("Assistant"))
			prefix = ChatColor.GREEN + "" + ChatColor.ITALIC;
		if (groups.contains("YouTuber"))
			prefix = ChatColor.LIGHT_PURPLE + "";
		if (groups.contains("VIP") || groups.contains("Premium") || groups.contains("Elite") || groups.contains("Exclusive") || groups.contains("Ultimate"))
			prefix = ChatColor.YELLOW + "";
		if (groups.contains("DemiGod") || groups.contains("DGMod"))
			prefix = ChatColor.BLUE + "";
		if (groups.contains("Mod") || groups.contains("VIMod") || groups.contains("PMod") || groups.contains("EMod") || groups.contains("ExMod") || groups.contains("UMod"))
			prefix = ChatColor.DARK_GREEN + "";
		if (groups.contains("HeadMod"))
			prefix = ChatColor.DARK_GREEN + "" + ChatColor.ITALIC;
		if (groups.contains("Admin")) {
			prefix = ChatColor.DARK_RED + "";
			String owner = owners.get(chatPlugin.plugin.detectedServer());
			if (player.getName().equals(owner)) {
				prefix = ChatColor.DARK_PURPLE + "";
			}
		}
		if (groups.contains("HeadAdmin"))
			prefix = ChatColor.DARK_RED + "" + ChatColor.ITALIC;
		if (groups.contains("Owner"))
			prefix = ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC;
		
		return prefix;
	}
	
	public String getDonorPrefix(Player player) {
		@SuppressWarnings("static-access")
		List<String> groups = Arrays.asList(chatPlugin.perms.getPlayerGroups(player));
		String prefix = "";
		if (groups.contains("YouTuber"))
			prefix = ChatColor.BLACK + "[" + ChatColor.DARK_RED + "You" + ChatColor.WHITE + "Tuber" + ChatColor.BLACK + "] ";
		if (groups.contains("VIP") || groups.contains("VIMod"))
			prefix = ChatColor.BLUE + "(" + ChatColor.DARK_RED + "" + ChatColor.BOLD + "V" + ChatColor.BLUE + ") ";
		if (groups.contains("Premium") || groups.contains("PMod"))
			prefix = ChatColor.BLUE + "(" + ChatColor.DARK_RED + "" + ChatColor.BOLD + "P" + ChatColor.BLUE + ") ";
		if (groups.contains("Elite") || groups.contains("EMod"))
			prefix = ChatColor.BLUE + "(" + ChatColor.DARK_RED + "" + ChatColor.BOLD + "E" + ChatColor.BLUE + ") ";
		if (groups.contains("Exclusive") || groups.contains("ExMod"))
			prefix = ChatColor.BLUE + "(" + ChatColor.DARK_RED + "" + ChatColor.BOLD + "X" + ChatColor.BLUE + ") ";
		if (groups.contains("Ultimate") || groups.contains("UMod"))
			prefix = ChatColor.BLUE + "(" + ChatColor.DARK_RED + "" + ChatColor.BOLD + "U" + ChatColor.BLUE + ") ";
		if (groups.contains("DGMod"))
			prefix = ChatColor.BLUE + "(" + ChatColor.DARK_RED + "" + ChatColor.BOLD + "DG" + ChatColor.BLUE + ") ";
		return prefix;
	}
	
	public String getWorld(Player player) {
		String world = player.getWorld().getName();
		String wt = "?";
		if (world.equals("Survival"))
			wt = "SV";
		if (world.equals("TownWorld"))
			wt = "TW";
		if (world.equals("EventWorld")) {
			double x = player.getLocation().getX();
			double z = player.getLocation().getZ();
			if (x > 5000 && z > 5000) {
				wt = "SG";
			} else {
				wt = "EW";
			}
		}
		if (world.equals("Build")) {
			double x = player.getLocation().getX();
			double z = player.getLocation().getZ();
			if (x > 50000 && z < -50000) {
				wt = "PX";
			} else if (x < -50000 && z < -50000) {
				wt = "FB";
			} else if (x > 50000 && z > 50000) {
				wt = "RS";
			} else {
				wt = "B";
			}
		}
		if (world.equals("Nether"))
			wt = "N";
		if (world.equals("End"))
			wt = "E";
		if (world.equals("Destruction"))
			wt = "DS";
		if (world.equals("SkyBlock"))
			wt = "SB";
		return ChatColor.DARK_AQUA + "<" + wt + getChannel(player) + ">";
	}
	@SuppressWarnings("static-access")
	public String getFaction(Player player, Player rec) {
		if (chatPlugin.f_t != null) {
			return chatPlugin.f_t.getFactionColorsName(player, rec);
		}
		return "";
	}
	@SuppressWarnings("static-access")
	public String getTown(Player player) {
		if (chatPlugin.f_t != null) {
			return chatPlugin.f_t.getTown(player);
		}
		return "";
	}
	@SuppressWarnings("static-access")
	public String getChannel(Player player) {
		String channel = chatPlugin.sql.getChannel(player.getName());
		if (channel == null) {
			channel = "g";
		}
			return getChannelPrefix(player) + "" + ChatColor.ITALIC + channel.toUpperCase() + ChatColor.DARK_AQUA;		
	}
	@SuppressWarnings("static-access")
	public String getChannelPrefix(Player player) {
		String channel = chatPlugin.sql.getChannel(player.getName());
		if (channel == null) {
			channel = "g";
		}
		if (channel.equals("g"))
			return ChatColor.WHITE + "";
		if (channel.equals("w"))
			return ChatColor.AQUA + "";
		if (channel.equals("l"))
			return ChatColor.YELLOW + "";
		if (channel.equals("f"))
			return ChatColor.GREEN + "";
		if (channel.equals("t"))
			return ChatColor.GOLD + "";
		if (channel.equals("n"))
			return ChatColor.translateAlternateColorCodes('&', "&6&o");
		if (channel.equals("s"))
			return ChatColor.RED + "";
		return ChatColor.GRAY + "";

	}
	@SuppressWarnings("static-access")
	public String removeCussWords(String msg, boolean checkCussed,
			Player player, boolean change, String color) {
		String secondMsg = "";
		for (String word : msg.split(" ")) {
			for (String in : chatPlugin.cusses.keySet()) {
				if (removeColorCodes(word).toLowerCase().contains(in)) {
					if (change) {
//						String let = cusses.get(in).substring(cusses.get(in).indexOf(">") + 1, cusses.get(in).length());
						word = removeColorCodes(word).toLowerCase().replaceAll(
								in,
								chatPlugin.cusses.get(in)
										.replaceAll("<", ChatColor.MAGIC + "")
										.replaceAll(">", ChatColor.RESET + color));
//						word = word.substring(0, word.length() - let.length());
					}
					if (checkCussed) {
						chatPlugin.addToCussNum(player);
					}
				}
			}
			secondMsg += (word + " ");
		}
		msg = secondMsg.substring(0, secondMsg.length() - 1);
		for (String in : chatPlugin.cusses.keySet()) {
			if (removeColorCodes(msg).toLowerCase().contains(in)) {
				if (change) {
//					String let = cusses.get(in).substring(cusses.get(in).indexOf(">") + 1, cusses.get(in).length());
					msg = removeMostColorCodes(msg).toLowerCase()
							.replaceAll(in, chatPlugin.cusses.get(in)
							.replaceAll("<", ChatColor.MAGIC + "")
							.replaceAll(">", ChatColor.RESET + color));
				}
				if (checkCussed) {
					chatPlugin.addToCussNum(player);
				}
			}
		}
		if (msg.endsWith(" ass")) {
			if (change)
				msg = msg.substring(0, msg.length() - 3) + "a"
						+ ChatColor.MAGIC + "ss";
			if (checkCussed) {
				chatPlugin.addToCussNum(player);
			}
		}
		if (msg.startsWith("ass ") || msg.equals("ass")) {
			if (change)
				msg = "a" + ChatColor.MAGIC + "ss" + ChatColor.RESET + "" + color + msg.substring(3, msg.length());
			if (checkCussed) {
				chatPlugin.addToCussNum(player);
			}
		}
		String pattern = "\\d{1,3}(?:\\.\\d{1,3}){3}(?::\\d{1,5})?";

		Pattern compiledPattern = Pattern.compile(pattern);
		Matcher matcher = compiledPattern.matcher(msg);
		String ip = "";
		while (matcher.find()) {
			ip = matcher.group();
		    msg = msg.replace(ip, "*.*.*.*");
		}
		final String name = player.getName();
		if (!ip.equals(""))
			chatPlugin.plugin.getServer().getScheduler().scheduleSyncDelayedTask(chatPlugin.plugin, new Runnable() {
				@Override 
				public void run() {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mute " + name);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + name + " Trying to advertise another server.");
				}
			}, 1);
		return msg;
	}
	public String changeCussColors(String msg) {
		String finalMsg = "";
		for (int charNum = 0; charNum < msg.length(); charNum++) {
			String c = msg.substring(charNum, charNum + 1);
			if (c.equals("&")) {
				try {
					if (msg.substring(charNum - 2, charNum).equals(ChatColor.RESET + "")) {
						c = ChatColor.translateAlternateColorCodes('&', msg.substring(charNum, charNum + 2));
						charNum++;
					}
				} catch (Exception e) {}
			}
			finalMsg = finalMsg + c;
		}
		return finalMsg;
	}
	public String removeCAPS(String msg) {
		String newMsg = "";
		for (String word : msg.split(" ")) {
			int caps = 0;
			for (int i = 0; i < word.length(); i++){
			    String c = word.substring(i, i+1);        
			    if (c.toUpperCase().equals(c))
			    	caps++;
			}
			if (caps > 3)
				word = word.toLowerCase();
			newMsg += word + " ";
		}
		return newMsg;
	}
	public String removeColorCodes(String s) {
		return s.replaceAll(ChatColor.DARK_BLUE + "", "")
				.replaceAll(ChatColor.DARK_GREEN + "", "")
				.replaceAll(ChatColor.DARK_AQUA + "", "")
				.replaceAll(ChatColor.DARK_RED + "", "")
				.replaceAll(ChatColor.GOLD + "", "")
				.replaceAll(ChatColor.DARK_PURPLE + "", "")
				.replaceAll(ChatColor.GRAY + "", "")
				.replaceAll(ChatColor.DARK_GRAY + "", "")
				.replaceAll(ChatColor.BLACK + "", "")
				.replaceAll(ChatColor.GREEN + "", "")
				.replaceAll(ChatColor.AQUA + "", "")
				.replaceAll(ChatColor.RED + "", "")
				.replaceAll(ChatColor.LIGHT_PURPLE + "", "")
				.replaceAll(ChatColor.YELLOW + "", "")
				.replaceAll(ChatColor.WHITE + "", "")
				.replaceAll(ChatColor.ITALIC + "", "")
				.replaceAll(ChatColor.BOLD + "", "")
				.replaceAll(ChatColor.UNDERLINE + "", "")
				.replaceAll(ChatColor.MAGIC + "", "")
				.replaceAll(ChatColor.RESET + "", "");
	}
	
	public String removeMostColorCodes(String s) { //except magic and underline
		return s.replaceAll(ChatColor.DARK_BLUE + "", "")
				.replaceAll(ChatColor.DARK_GREEN + "", "")
				.replaceAll(ChatColor.DARK_AQUA + "", "")
				.replaceAll(ChatColor.DARK_RED + "", "")
				.replaceAll(ChatColor.GOLD + "", "")
				.replaceAll(ChatColor.DARK_PURPLE + "", "")
				.replaceAll(ChatColor.GRAY + "", "")
				.replaceAll(ChatColor.DARK_GRAY + "", "")
				.replaceAll(ChatColor.BLACK + "", "")
				.replaceAll(ChatColor.GREEN + "", "")
				.replaceAll(ChatColor.AQUA + "", "")
				.replaceAll(ChatColor.RED + "", "")
				.replaceAll(ChatColor.LIGHT_PURPLE + "", "")
				.replaceAll(ChatColor.YELLOW + "", "")
				.replaceAll(ChatColor.WHITE + "", "")
				.replaceAll(ChatColor.ITALIC + "", "")
				.replaceAll(ChatColor.BOLD + "", "")
				.replaceAll(ChatColor.UNDERLINE + "", "");
	}
	
	public String getPlayerListName(Player player) {
		String pre = getNamePrefix(player);
		String[] fix = pre.split(" ");
		String name;
		if (fix.length > 1) {
			name = fix[fix.length-1] + player.getName();
		} else {
			name = getNamePrefix(player) + player.getName();
		}
		if (name.length() > 16)
			name = name.substring(0, 16);
		return name;
	}
	
	public String getDName(Player player) {
		return getNamePrefix(player) + player.getName();
	}
	
	@SuppressWarnings("static-access")
	public String getDate(Player p) {
		String date = chatPlugin.sql.getDatingPartner(p.getName());
		if (date == null)
			return "";
		return ChatColor.LIGHT_PURPLE + "\u2764" + date.substring(0, 4) + "\u2764 ";
	}
	@SuppressWarnings("static-access")
	public String getName(Player player) {
		String prefix = chatPlugin.sql.getPrefix(player.getName());
		if (prefix != null) {
			return ChatColor.translateAlternateColorCodes('&', prefix).replaceAll("%name%", getDName(player)).replaceAll(player.getName(), getDName(player));
		} else {
			return player.getName();
		}
	}
}
