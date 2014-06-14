package net.diamonddominion.esaych.global;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.diamonddominion.esaych.CustomPlugin;
import net.milkbowl.vault.permission.Permission;
import net.minecraft.server.v1_6_R3.EntityWolf;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;


public class Chat implements Listener {
	public static CustomPlugin plugin;
	public static ChatSQL sql;
	public static ChatUtils utils;
	public static ChatFacsTowns f_t;
	public static Permission perms = null;

	public Map<String, String> cusses = new HashMap<String, String>();
	public Map<Player, Integer> cussesNum = new HashMap<Player, Integer>();
	public ArrayList<String> cussMax = new ArrayList<String>();
	public ArrayList<CommandSender> disabledSpys = new ArrayList<CommandSender>();
	public Map<Player, String> spamData = new HashMap<Player, String>(); //timestamp;total;lastphrase
	public Map<String, String> lastMsg = new HashMap<String, String>();
	
	private ArrayList<String> globalPlayerList = new ArrayList<String>();
	
	public boolean marriageEnabled = true;

	public Chat(CustomPlugin plugin) {
		Chat.plugin = plugin;
	}
	@SuppressWarnings("deprecation")
	public void enable() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		setupPermissions();
		addCusses();
		createCussLoop();
		sql = new ChatSQL(this);
		utils = new ChatUtils(this);
		for (Plugin pl : Bukkit.getServer().getPluginManager().getPlugins()) {
			if (pl.getName().contains("Faction")) {
				f_t = new ChatFacsTowns(this);
				log("Factions/Towny Server Detected");
			}
		}
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				DataOutputStream out = new DataOutputStream(b);
				try {
					out.writeUTF("PlayerList");
					out.writeUTF("ALL");
				} catch (Exception e2) {}
				if (Bukkit.getOnlinePlayers().length > 0) {
					Bukkit.getOnlinePlayers()[0].sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
				}
			}
		}, 5, 20*15);
		log("Enabled");
	}
	
	private String convertMsg(Player player, String msg) {
		msg = utils.removeCussWords(msg, true, player, !player.hasPermission("ddchat.censorbypass"), utils.getChannelPrefix(player));
		if (player.hasPermission("ddchat.color")) {
			msg = ChatColor.translateAlternateColorCodes('&', msg); 
		} else {
			msg = utils.changeCussColors(msg);
		}
		if (!player.hasPermission("ddchat.caps"))
			msg = utils.removeCAPS(msg);
		msg = utils.getDate(player) + utils.getWorld(player) + " " + utils.getDonorPrefix(player) + utils.getNamePrefix(player)
				+ utils.getName(player) + ChatColor.GRAY + ": " + utils.getChannelPrefix(player)
				+ msg;
		return msg;
	}

	private void addCusses() {
		cusses.put("fuck", "f<uc>k");
		cusses.put("cunt", "c<u>nt");
		cusses.put("sex", "s<e>x");
		cusses.put("shit", "sh<i>t");
		cusses.put("cock", "c<o>ck");
		cusses.put("dick", "d<i>ck");
//		cusses.put("gay", "g<a>y");
		cusses.put("penis", "p<eni>s");
		cusses.put("bitch", "b<i>tch");
		cusses.put("nigger", "n<igg>er");
		cusses.put("nigga", "n<igg>er");
		cusses.put("slut", "sl<u>t");
		cusses.put("lesbian", "l<e>sbian");
		cusses.put("vagina", "vag<i>na");
		cusses.put("tits", "t<i>ts");
		cusses.put("tittie", "t<i>ttie");
		cusses.put("boob", "b<oo>b");
		cusses.put("fag", "f<a>g");
		cusses.put("cripple", "cr<i>pple");
		cusses.put("pussy", "p<u>ssy");
		cusses.put("whore", "wh<o>re");
		cusses.put("bastard", "b<a>stard");
		cusses.put("boner", "b<o>ner");
		cusses.put("dilldo", "d<i>lldo");
		cusses.put("asshole", "a<ss>hole");
		cusses.put(" ass ", " a<ss> ");
//		cusses.put("asses", "a<ss>es");
		cusses.put("queef", "qu<e>ef");
		cusses.put("valde", "välde");
	}
	
	public void addToCussNum(final Player player) {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (cussesNum.containsKey(player)) {
					cussesNum.put(player, cussesNum.get(player) + 1);
				} else {
					cussesNum.put(player, 1);
				}
				if (cussesNum.get(player) > 5) {
					String group = perms.getPlayerGroups(player)[0];
					String msg = "You may not curse more than 5 times a minute!";
					if (!(group.contains("Player")
							|| group.contains("Respected") || group
							.contains("Owner"))) {
						if (cussMax.contains(player.getName())) {
							Bukkit.getServer().dispatchCommand(
									Bukkit.getConsoleSender(),
									"manuaddp " + player.getName()
									+ " -ddchat.censorbypass");
							msg = "You have lost your donor permissions for cursing freely. No refunds.";
						} else {
							msg += "\n\nYou will lose your cursing permissions next time you do that.";
							cussMax.add(player.getName());
						}
					}
					Bukkit.getServer().dispatchCommand(
							Bukkit.getConsoleSender(),
							"kick " + player.getName() + " " + msg);
					cussesNum.put(player, 0);
				}
			}
		}, 1);
	}
	
	
	private Player lastSpammer = null;
	
	public void checkSpam(Player player, String msg) {
		double currTimestamp = System.currentTimeMillis()/1000;
		String[] data;
		if (spamData.containsKey(player)) {
			data = spamData.get(player).split(";");
		} else {
			spamData.put(player, currTimestamp + ";1;" + msg);
			return;
		}
		double timestamp = Double.valueOf(data[0]);
		int total = Integer.parseInt(data[1]);
		String lastMessage = data[2];
//		System.out.println(timestamp + ";" + total + ";" + lastMessage);
//		System.out.println(spamData.toString());
		if (lastMessage.equals(msg) && currTimestamp-timestamp < 5) {
			if (currTimestamp-timestamp < .25) {
				final String name = player.getName();
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@Override 
					public void run() {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mute " + name + " 30s");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + name + " SPAM! 1 time only plz");
					}
				}, 1);
			}
			total++;
		} else if (lastSpammer != null && player == lastSpammer && currTimestamp-timestamp < 2) {
			total++;
		} else if (player == lastSpammer && currTimestamp-timestamp < .25) {
			total++;
		} else {
			total = 1;
		}
		if (total >= 3) {
			final String name = player.getName();
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override 
				public void run() {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mute " + name);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + name + " SPAM! Talk less plz");
				}
			}, 1);
		}
		String newData = currTimestamp + ";" + total + ";" + msg;
//		System.out.println(newData);
//		System.out.println("msg="+msg+";lastMessage="+lastMessage);
		lastSpammer = player;
		spamData.put(player, newData);
	}	

	@SuppressWarnings("deprecation")
	private void createCussLoop() {
		plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				for (Player p : cussesNum.keySet()) {
					cussesNum.put(p, 0);
				}
			}
		}, 100L, 20 * 60);
	}
	
	public boolean msgCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (args.length < 2) {
			msg(sender, "/msg [Player] [Message]");
			return true;
		}
		String msg = "";
		for (int i = 1; i < args.length; i++)
			msg += args[i] + " ";
		
		if (!(sender instanceof Player)) {
			Player consoleReciever = Bukkit.getPlayer(args[0]);
			if (consoleReciever == null) {
				log("Player not found :(");
				return true;
			}
			msg = "Offline " + ChatColor.DARK_PURPLE + "Esaych" + ChatColor.AQUA + " --" + ChatColor.BLUE + "> " + utils.getNamePrefix(consoleReciever) + "You" + ChatColor.WHITE + ": " + msg;
			consoleReciever.sendMessage(msg);
			log(msg);
			return true;
		}
		Player player = (Player) sender;
		
		sql.logChat(player, msg, args[0]);
		
		Player reciever = Bukkit.getPlayer(args[0]);
		if (reciever == null) {
			boolean playerFound = false;
			for (String playerName : globalPlayerList) {
				if (playerName.toLowerCase().contains(args[0].toLowerCase())) {
					if (sql.getIgnoredPlayers(playerName).contains(player.getName())) {
						msg(player, playerName + " is ignoring you");
						return true;
					}
					msg = utils.removeCussWords(msg, true, player, !player.hasPermission("ddchat.censorbypass"), utils.getChannelPrefix(player));
					if (player.hasPermission("ddchat.color"))
						msg = ChatColor.translateAlternateColorCodes('&', msg);

					ByteArrayOutputStream b = new ByteArrayOutputStream();
					DataOutputStream out = new DataOutputStream(b);

					try {
						out.writeUTF("Forward");
						out.writeUTF("ALL");
						out.writeUTF("DDCustomPlugin_Chat");

						ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
						DataOutputStream msgout = new DataOutputStream(msgbytes);
						msgout.writeUTF("playermsg");
						msgout.writeUTF(utils.getDName(player));
						msgout.writeUTF(playerName);
						msgout.writeUTF(msg);

						out.writeShort(msgbytes.toByteArray().length);
						out.write(msgbytes.toByteArray());
					} catch (Exception e2) {}
					player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
					player.sendMessage(utils.getNamePrefix(player) + "You" + ChatColor.AQUA + " --" + ChatColor.BLUE + "> " + ChatColor.WHITE + playerName + ChatColor.WHITE + ": " + msg);
					lastMsg.put(playerName, player.getName());
					playerFound = true;
					break;
				}
			}
			if (!playerFound) {
				msg(sender, "The player " + args[0] + " does not seem to exist.");
			}
			return true;
		}
		
		
		if (sql.getIgnoredPlayers(player.getName()).contains(reciever.getName())) {
			msg(player, "You can't message people you are ignoring");
			return true;
		}

		if (sql.getIgnoredPlayers(reciever.getName()).contains(player.getName())) {
			msg(player, reciever.getName() + " is ignoring you.");
			return true;
		}
		
		
		msg = utils.removeCussWords(msg, true, player, !player.hasPermission("ddchat.censorbypass"), utils.getChannelPrefix(player));
		if (player.hasPermission("ddchat.color"))
			msg = ChatColor.translateAlternateColorCodes('&', msg);
		reciever.sendMessage(utils.getDName(player) + ChatColor.AQUA + " --" + ChatColor.BLUE + "> " + utils.getNamePrefix(reciever) + "You" + ChatColor.WHITE + ": " + msg);
		player.sendMessage(utils.getNamePrefix(player) + "You" + ChatColor.AQUA + " --" + ChatColor.BLUE + "> " + utils.getDName(reciever) + ChatColor.WHITE + ": " + msg);
//		lastMsg.put(player, reciever);
		lastMsg.put(reciever.getName(), player.getName());
		return true;
	}
	
	public boolean rCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			msg(sender, "You realize console's dont have any msg history...");
			return true;
		}
		if (!lastMsg.containsKey(sender.getName())) {
			msg(sender, "Nobody has messaged you!");
			return true;
		}
//		if (Bukkit.getPlayer(lastMsg.get(sender.getName())) == null) {
//			msg(sender, "They are not online!");
//			return true;
//		}
		String msg = "";
		for (int i = 0; i < args.length; i++)
			msg += args[i] + " ";
		return msgCommand(sender, cmd, commandLabel,
				new String[] { lastMsg.get(sender.getName()), msg });
	}

	public boolean chCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		/*
		 * l = local, 100 blocks
		 * w = world
		 * g = global
		 * f = faction chat
		 * s = staff chat
		 * n = towny nation
		 * t = towns
		 */
		if (!(sender instanceof Player)) {
			msg(sender, "NO CHAT FOR YOU");
			return true;
		}
		if (args.length == 0 && !(commandLabel.equalsIgnoreCase("sc"))) {
			msg(sender, "Try /ch <channel>");
			if (f_t != null) {
				msg(sender, "&aFaction: &bSends to faction members");
				msg(sender, "&6Town: &bSend to only town members");
				msg(sender, "&6&oNation: &bSend to all in your nation");
			}
			msg(sender, "&eLocal: &bOnly receive chat from players within 100 blocks");
			msg(sender, "World: Only receive chat from players in your world");
			msg(sender, "&fGlobal: &bGet all chat messages from everyone");
			msg(sender, "&cBecause you are in a smaller channel doesn't mean others don't get your messages when they are in global");
			return true;
		} 
		if (!(commandLabel.equalsIgnoreCase("sc"))) {
			char channel = args[0].toLowerCase().charAt(0);
			if (channel == 'l') {
				sql.setChannel(sender.getName(), "l");
				msg(sender, "Channel set to " + utils.getChannelPrefix((Player) sender) + "&oLocal");
				return true;
			}
			if (channel == 'w') {
				sql.setChannel(sender.getName(), "w");
				msg(sender, "Channel set to " + utils.getChannelPrefix((Player) sender) + "&oWorld");
				return true;
			}
			if (channel == 'g') {
				sql.setChannel(sender.getName(), "g");
				msg(sender, "Channel set to " + utils.getChannelPrefix((Player) sender) + "&oGlobal");
				return true;
			}
			if (channel == 'f') {
				if (f_t != null) {
					if (f_t.getFacName((Player)sender).equals("")) {
						msg(sender, "You are not in a faction");
						return true;
					}
					sql.setChannel(sender.getName(), "f");
					msg(sender, "Channel set to " + utils.getChannelPrefix((Player) sender) + "&oFaction");
					return true;
				}
			}
			if (channel == 't') {
				if (f_t != null) {
					if (f_t.testTown((Player)sender)) {
						sql.setChannel(sender.getName(), "t");
						msg(sender, "Channel set to " + utils.getChannelPrefix((Player) sender) + "&oTown");
						return true;
					} else {
						msg(sender, "You are not in a town");
						return true;
					}
				}
			}
			if (channel == 'n') {
				if (f_t != null) {
					if (f_t.testNation((Player)sender)) {
						sql.setChannel(sender.getName(), "n");
						msg(sender, "Channel set to " + utils.getChannelPrefix((Player) sender) + "&oNation");
						return true;
					} else {
						msg(sender, "You are not in a nation");
						return true;
					}
				}
			}
			if (channel == 's') {
				if (!sender.hasPermission("ddchat.staffchat")) {
					msg(sender, "You don't have permission for staff chat.");
					return true;
				}
				sql.setChannel(sender.getName(), "s");
				msg(sender, "Channel set to " + utils.getChannelPrefix((Player) sender) + "&oStaff Chat");
				return true;
			}
		} else {
			if (!sender.hasPermission("ddchat.staffchat")) {
				msg(sender, "You don't have permission for staff chat.");
				return true;
			}
			String c = sql.getChannel(sender.getName());
			if (c != null && c.equals("s")) {
				sql.setChannel(sender.getName(), "g");
				msg(sender, "Channel set to &f&oGlobal Chat");
			} else {
				sql.setChannel(sender.getName(), "s");
				msg(sender, "Channel set to &c&oStaff Chat");
			}
			return true;
		}
		msg(sender, "Thats not a channel! Try:");
		msg(sender, "&aFaction: &bSends to faction members");
		msg(sender, "&6Town: &bSend to only town members");
		msg(sender, "&6&oNation: &bSend to all in your nation");
		msg(sender, "&eLocal: &bOnly receive chat from players within 100 blocks");
		msg(sender, "World: Only receive chat from players in your world");
		msg(sender, "&fGlobal: &bGet all chat messages from everyone");
		msg(sender, "&cBecause you are in a smaller channel doesn't mean others don't get your messages when they are in global");
		return true;
	}
	
	public boolean badProfileCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender.hasPermission("ddchat.givesbadprofiles")) {
			if (args.length == 0) {
				msg(sender, "You must specify a player");
				return true;
			}
			Player player = Bukkit.getPlayer(args[0]);
			if (player == null) {
				msg(sender, "Player not found");
				return true;
			}
			if (sql.getBadProfile(player.getName())) {
				sql.setBadProfile(player.getName(), false);
				msg(sender, player.getName() + " has been removed their badprofile");
			} else {
				sql.setBadProfile(player.getName(), true);
				msg(sender, player.getName() + " has been given a badprofile");
			}
			return true;
		} else {
			msg(sender, "You don't have permission for this command");
			return true;
		}
	}
	
	public boolean spyCommand(CommandSender sender) {
		if (!sender.hasPermission("ddchat.commandsnoop")) {
			return true;
		}
		if (disabledSpys.contains(sender)) {
			disabledSpys.remove(sender);
			sender.sendMessage(ChatColor.GREEN + "Command Spy has been enabled");
		} else {
			disabledSpys.add(sender);
			sender.sendMessage(ChatColor.GREEN + "Command Spy has been disabled");
		}
		return true;
	}
	
	private Map<Player, String> marriageQueue = new HashMap<Player, String>(); //Proposed To, Proposer:ScheduleID
	
	public boolean dateCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (marriageEnabled) {
			if (args.length == 0) {
				mmsg(sender, ChatColor.GREEN + "-------  Dating Help  -------");
				mmsg(sender, "Type /date 'player' to ask someone out");
				mmsg(sender, "Type /dump to dump your current date");
				mmsg(sender, "Right click next to your date to kiss them");
				mmsg(sender, ChatColor.GREEN + "--------------------------");
				return true;
			}
			final Player player = Bukkit.getPlayer(args[0]);
			if (sql.getDatingPartner(sender.getName()) != null) {
				if (sql.getDatingPartner(sender.getName()).equals(player.getName())) {
					mmsg(sender, "You must dump " + sql.getDatingPartner(sender.getName()) + " before you date another person!");
				} else {
					mmsg(sender, "You are already dating someone.");
				}
				return true;
			}
			if (sender instanceof Player) {
				if (sender.isOp()) {
					if (args[0].equalsIgnoreCase("disable")) {
						marriageEnabled = false;
						mmsg(sender, "Dating Disabled");
						return true;
					}
					if (args[0].equalsIgnoreCase("enable")) {
						marriageEnabled = false;
						mmsg(sender, "Dating Enabled");
						return true;
					}
				}
				if (marriageQueue.containsKey(sender) && args[0].equalsIgnoreCase("reject")) {
					Bukkit.broadcastMessage(ChatColor.AQUA + "[" + ChatColor.RED + "Dating" + ChatColor.AQUA + "] " + ChatColor.RED +   
							sender.getName() + " rejected " + marriageQueue.get(sender) + ChatColor.WHITE + "!");
					marriageQueue.remove(sender);
					return true;
				}
				//check if actually online
				if (player == null) {
					mmsg(sender, "You must specify an online player.");
					return true;
				}
				if (player == sender) {
					mmsg(sender, "You can't date yourself :/");
					return true;
				}
				//check if they have a request
				if (marriageQueue.containsKey(sender)) {
					if (marriageQueue.get(sender).equalsIgnoreCase(player.getName())) {
						marry(Bukkit.getPlayer(args[0]), (Player) sender);
					} else {
						mmsg(sender, "You have a pending request from " + marriageQueue.get(sender) + "!");
						mmsg(sender, "You must '/date reject' them first.");
					}
					return true;
				}
				//check if they're a dirty cheater
				for (Player p : marriageQueue.keySet()) {
					String old = p.getName();
					if (sender.getName().equals(marriageQueue.get(p))) {
						if (!player.getName().equals(old)) {
							mmsg(sender, "What about " + old + "?");
							mmsg(sender, "You dirty cheater!");
							marriageQueue.remove(sender);
							marriageQueue.remove(p);
							marriageQueue.remove(player);
							Player dumped = Bukkit.getPlayer(old);
							if (dumped != null) {
								mmsg(dumped, sender.getName() + " ditched you for " + utils.getPlayerListName(player));
							}
						} else {
							mmsg(sender, "You are already requesting them!");
							return true;
						}
					}
				}
				
				if (!utils.getDate(player).equals("")) {
					mmsg(sender, player.getName() + " is already dating!");
					return true;
				}
				
				marriageQueue.put(player, sender.getName());

				mmsg(player, ChatColor.AQUA + "----------------------------------------");
				mmsg(player, utils.getPlayerListName((Player) sender)+ ChatColor.AQUA + " wants to date you!");
				mmsg(player, "Type '/date " + utils.getPlayerListName((Player) sender) + ChatColor.AQUA + "' to accept!");
				mmsg(player, "Or '/date reject' if you want to break their heart.");
				mmsg(player, ChatColor.AQUA + "----------------------------------------");

				mmsg(sender, "Your proposal has been made to " + utils.getPlayerListName(player));
//				+ ChatColor.AQUA + ", they have 5 minutes to reply.");

				return true;
			} else {
				if (args[0].equalsIgnoreCase("disable")) {
					marriageEnabled = false;
					mmsg(sender, "Dating Disabled");
					return true;
				}
				if (args[0].equalsIgnoreCase("enable")) {
					marriageEnabled = false;
					mmsg(sender, "Dating Disabled");
					return true;
				}
				mmsg(sender, "Consoles may not date!");
				return true;
			}
		} else {
			mmsg(sender, "Dating is not enabled");
			return true;
		}
	}
	
	public boolean dumpCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (marriageEnabled) {
			String divorced = sql.getDatingPartner(sender.getName());
			if (divorced != null) { 
				divorce((Player) sender, divorced);
				
				return true;
			} else {
				mmsg(sender, "You are not dating anyone!");
				return true;
			}
		} else {
			mmsg(sender, "Dating is not enabled");
			return true;
		}
	}
	
	public boolean ignoreCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if (!(sender instanceof Player)) {
			log("Console's can't ignore");
			return false;
		}
		Player player = (Player) sender;
		if (args.length == 0) {
			msg(sender, "Toggle ignoring a player with: /ignore <Player>");
			return true;
		}
		String playerToIgnore = args[0];
		Player player2 = Bukkit.getPlayer(playerToIgnore);
		
		if (player2 != null && player2.getName().equals(player.getName())) {
			msg(sender, "You can't ignore yourself!");
		}
		
		ArrayList<String> alreadyIgnoring = sql.getIgnoredPlayers(player.getName());
		
		
		//Possibly REMOVING that player
		if (player2 != null && alreadyIgnoring.contains(player2.getName())) {
			sql.removeIgnoredPlayer(player.getName(), player2.getName());
			msg(player, "You are no longer ignoring " + player2.getName());
			return true;
		} else {
			for (String name : alreadyIgnoring) {
				if (name.toLowerCase().contains(playerToIgnore.toLowerCase())) {
					sql.removeIgnoredPlayer(player.getName(), name);
					msg(player, "You are no longer ignoring " + name);
					return true;
				}
			}
		}
		
		if (cmdLabel.equalsIgnoreCase("unignore")) {
			msg(player, "You are not ignoring anyone with that name.");
			return true;
		}
		
		if (player2 == null) {
			for (String playerName : globalPlayerList) {
				if (playerName.toLowerCase().contains(playerToIgnore.toLowerCase())) {
					sql.addIgnoredPlayer(player.getName(), playerName);
					msg(player, "You are now ignoring " + playerToIgnore);
					return true;
				}
			} 
			msg(player, playerToIgnore + " not found.");
			msg(player, "Please use proper caps for cross server ignoring.");
			return true;
		} else {
			sql.addIgnoredPlayer(player.getName(), player2.getName());
			msg(player, "You are now ignoring " + player2.getName());
			msg(player2, player.getName() + " is now ignoring you");
			return true;
		}
		
		
	}
	
	private void marry(Player player, Player spouse) {
		Bukkit.broadcastMessage(ChatColor.AQUA + "[" + ChatColor.RED + "Dating" + ChatColor.AQUA + "] " +  
				utils.getPlayerListName(player) + ChatColor.WHITE + " is now dating " + ChatColor.AQUA + utils.getPlayerListName(spouse) + ChatColor.WHITE + "!");
		sql.setDatingPartner(player.getName(), spouse.getName());
		sql.setDatingPartner(spouse.getName(), player.getName());
		marriageQueue.remove(player);
		marriageQueue.remove(spouse);
		
	}
	
	private void divorce(Player player, String spouse) {
		Bukkit.broadcastMessage(ChatColor.AQUA + "[" + ChatColor.RED + "Dating" + ChatColor.AQUA + "] " + ChatColor.RED + 
				player.getName() + " dumped " + spouse + "!");
		sql.setDatingPartner(player.getName(), null);
		sql.setDatingPartner(spouse, null);
		marriageQueue.remove(player);
		
	}
	
	private ArrayList<Player> getRecievers(Player player) {
		String channel = sql.getChannel(player.getName());
		if (channel == null)
			channel = "g";
		if (channel.equals("f")) {
			if (f_t != null) {
				ArrayList<Player> players = f_t.getFacPlayers(player);
				if (players.size() == 0) {
					channel = "g";
					sql.setChannel(player.getName(), "g");
					
				} else {
					return players;
				}
			} else {
				channel = "g";
				sql.setChannel(player.getName(), "g");
				
			}
		}
		if (channel.equals("t")) {
			if (f_t != null) {
				ArrayList<Player> players = f_t.getTownPlayers(player);
				if (players.size() == 0) {
					channel = "g";
					sql.setChannel(player.getName(), "g");
					
				} else {
					return players;
				}
			} else {
				channel = "g";
				sql.setChannel(player.getName(), "g");
				
			}
		}
		if (channel.equals("n")) {
			if (f_t != null) {
				ArrayList<Player> players = f_t.getNationPlayers(player);
				if (players.size() == 0) {
					channel = "g";
					sql.setChannel(player.getName(), "g");
					
				} else {
					return players;
				}
			} else {
				channel = "g";
				sql.setChannel(player.getName(), "g");
				
			}
		}
		if (channel.equals("s")) {
			ArrayList<Player> players = new ArrayList<Player>();
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.hasPermission("ddchat.staffchat"))
					players.add(p);
			}
			return players;
		}
		ArrayList<Player> players = new ArrayList<Player>();
		for (Player rec : Bukkit.getOnlinePlayers()) {
			String c = sql.getChannel(rec.getName());
			if (sql.getBadProfile(player.getName()) && !rec.hasPermission("ddchat.getbad"))
				continue;
			if (sql.getIgnoredPlayers(rec.getName()).contains(player.getName()))
				continue;
			
			if (c == null)
				c = "g";
			if (c.equals("g") || c.equals("s")) {
				players.add(rec);
			}
			if (c.equals("w") || c.equals("f")) {
				if (rec.getWorld() == player.getWorld())
					players.add(rec);
			}
			if (c.equals("l")) {
				if (rec.getWorld() == player.getWorld())
					if (rec.getLocation().distance(player.getLocation()) < 100)
						players.add(rec);
			}
		}

		return players;
	}
	
	public void onBungeeMessageReceived(String channel, Player player, byte[] message) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
        String subchannel = null;
		try {
			subchannel = in.readUTF();
		} catch (IOException e) {
			log("Could not read plugin message");
			e.printStackTrace();
		}
        if (subchannel.equals("PlayerList")) {
        	try {
        		bungeePlayerListReceived(in.readUTF(), in.readUTF().split(", "), player);
        	} catch (Exception e) {}
        }
        if (subchannel.equals("DDCustomPlugin_Chat")) {
        	try {
        		short len = in.readShort();
        		byte[] msgbytes = new byte[len];
        		in.readFully(msgbytes);
        		
        		DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
        		String type = msgin.readUTF();
        		
        		if (type.equals("commandspy")) {
        			String command = msgin.readUTF();
        			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
        				if (p.hasPermission("ddchat.commandsnoop") && !disabledSpys.contains(p) ) {
        					p.sendMessage(command);
        				}
        			}
        		}
        		if (type.equals("gchat")) {
        			String playerName = msgin.readUTF();
        			globalPlayerList.add(playerName);
        			String msg = msgin.readUTF();
        			Boolean isBad = msgin.readBoolean();
        			ArrayList<Player> players = new ArrayList<Player>();
        			for (Player rec : Bukkit.getOnlinePlayers()) {
        				String c = sql.getChannel(rec.getName());
        				if (isBad && !rec.hasPermission("ddchat.getbad")) {
        					continue;
        				}
        				if (c == null)
        					c = "g";
        				if (c.equals("g") || c.equals("s")) {
        					players.add(rec);
        				}
        				if (c.equals("w") || c.equals("f")) {
        					if (rec.getWorld() == player.getWorld())
        						players.add(rec);
        				}
        				if (c.equals("l")) {
        					if (rec.getWorld() == player.getWorld())
        						if (rec.getLocation().distance(player.getLocation()) < 100)
        							players.add(rec);
        				}
        			}
        			for (Player p : players) {
        				if (sql.getIgnoredPlayers(p.getName()).contains(playerName))
        					continue;
        				p.sendMessage(msg);
        			}
        		}
        		if (type.equals("schat")) {
        			String playerName = msgin.readUTF();
        			globalPlayerList.add(playerName);
        			String msg = msgin.readUTF();
        			for (Player rec : Bukkit.getOnlinePlayers()) {
        				if (rec.hasPermission("ddchat.staffchat"))
        					rec.sendMessage(msg);
        			}
        		}
        		if (type.equals("playermsg")) {
        			String sender = msgin.readUTF();
        			Player reciever = Bukkit.getPlayer(msgin.readUTF());
        			String msg = msgin.readUTF();
        			if (reciever != null) {
        				if (sql.getIgnoredPlayers(reciever.getName()).contains(removeFormattingCodes(sender)))
        					return;
        				reciever.sendMessage(sender + ChatColor.AQUA + " --" + ChatColor.BLUE + "> " + utils.getNamePrefix(reciever) + "You" + ChatColor.WHITE + ": " + msg);
        				lastMsg.put(reciever.getName(), removeFormattingCodes(sender));
        			}
        		}
        	} catch (Exception e) {}
        }
	}
	private String removeFormattingCodes(String input) {
		return input.replaceAll("§a", "").replaceAll("§b", "").replaceAll("§c", "").replaceAll("§d", "").replaceAll("§e", "").replaceAll("§f", "")
				.replaceAll("§0", "").replaceAll("§1", "").replaceAll("§2", "").replaceAll("§3", "").replaceAll("§4", "").replaceAll("§5", "")
				.replaceAll("§6", "").replaceAll("§7", "").replaceAll("§8", "").replaceAll("§9", "")
				.replaceAll("§k", "").replaceAll("§l", "").replaceAll("§m", "").replaceAll("§n", "").replaceAll("§o", "").replaceAll("§r", "");
	}
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerChatStart(AsyncPlayerChatEvent e) {
		e.setCancelled(true);
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChatEnd(AsyncPlayerChatEvent e) {
		if (plugin.fullMute.playerMuted(e.getPlayer().getName())) {
			if (plugin.fullMute.checkTime(e.getPlayer().getName())) {
				int left = (int)(sql.getMuteTime(e.getPlayer().getName()) - System.currentTimeMillis() / 1000);
				plugin.fullMute.msg(e.getPlayer(), "You are muted, and will stay muted for the remaining:");
				plugin.fullMute.msg(e.getPlayer(), plugin.fullMute.timeLeft(left));
				try {
					e.setCancelled(true);
					return;
				} catch (Exception ignored) {}
			}
		}
		if (e.getMessage().contains("connected with a") && e.getMessage().contains("using MineChat")) {
			return;
		}
		e.setCancelled(true);
		
		String channel = sql.getChannel(e.getPlayer().getName());
		if (channel == null) {
			channel = "g";
		}
		System.out.println(e.getPlayer().getName() + ": " + e.getMessage());
		sql.logChat(e.getPlayer(), e.getMessage(), channel);
		
		checkSpam(e.getPlayer(), e.getMessage());
		e.getPlayer().setPlayerListName(utils.getPlayerListName(e.getPlayer()));
		e.getPlayer().setDisplayName(utils.getDName(e.getPlayer()));
		sendMessage(convertMsg(e.getPlayer(), e.getMessage()), e.getPlayer());
	}
	@EventHandler (priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (f_t == null) {
			if (utils.getChannel(e.getPlayer()) == "f" || utils.getChannel(e.getPlayer()) == "t") {
				sql.setChannel(e.getPlayer().getName(), "g");
				
				msg(e.getPlayer(), "Channel set to " + utils.getChannelPrefix(e.getPlayer()) + "&oGlobal");
			}
		}
		
		
		e.getPlayer().setPlayerListName(utils.getPlayerListName(e.getPlayer()));
		e.getPlayer().setDisplayName(utils.getDName(e.getPlayer()));
		e.getPlayer().setCustomName(utils.getDName(e.getPlayer()));
		e.setJoinMessage("");
		Bukkit.broadcastMessage("     " + ChatColor.GREEN + "+ " + utils.getNamePrefix(e.getPlayer()) + e.getPlayer().getName());
	}
	
	Map<String, String> quitPlayers = new HashMap<String, String>();
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		e.setQuitMessage(null);
		if (Bukkit.getServer().getOnlinePlayers().length > 0) {
			final ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			try {
				out.writeUTF("PlayerList");
				out.writeUTF("ALL");
			} catch (Exception e2) {}
			quitPlayers.put(e.getPlayer().getName(), utils.getNamePrefix(e.getPlayer()) + e.getPlayer().getName());
			final Player player = Bukkit.getServer().getOnlinePlayers()[0];
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
				@Override
				public void run() {
					player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
				}
			}, 2);
		}
	}
	
	@EventHandler (priority = EventPriority.LOW)
	public void onPlayerKick(PlayerKickEvent e) {
		e.setLeaveMessage("     " + ChatColor.DARK_RED + "" + ChatColor.BOLD + "! " + utils.getNamePrefix(e.getPlayer()) + e.getPlayer().getName() + ChatColor.WHITE +  " - " + ChatColor.RED + "" + ChatColor.ITALIC + e.getReason());
		System.out.println("! " + e.getPlayer().getName() + " " + e.getReason());
//		for (RegisteredListener list : e.getHandlers().getRegisteredListeners()) {
//			log(list.getPlugin().toString());
//		}
//		e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent e) {
//		if (!(e.getEntity() instanceof Player))
//			return;
//		if (!(e instanceof PlayerDeathEvent))
//            return;
//		PlayerDeathEvent subEvent = (PlayerDeathEvent) e;
//		Player p = (Player)e.getEntity();
//		String msg = utils.getDName(p) + ChatColor.WHITE + " ";
//		DamageCause cause;
//		try {
//			cause = e.getEntity().getLastDamageCause().getCause();
//		} catch (Exception e1) {
//			msg += "died horribly";
//			subEvent.setDeathMessage(msg);
//			return;
//		}
//		if (cause == DamageCause.BLOCK_EXPLOSION)
//			msg += "instantaneously combusted.";
//		if (cause == DamageCause.CONTACT)
//			msg += "hugged a cactus.";
//		if (cause == DamageCause.CUSTOM)
//			msg += "died horribly.";
//		if (cause == DamageCause.DROWNING)
//			msg += "is sleeping with the fishes.";
//		if (cause == DamageCause.ENTITY_ATTACK)
//			if (p.getKiller() != null) {
//				msg = utils.getDName(p.getKiller()) + ChatColor.WHITE + " struck down " + utils.getDName(p);
//			} else {
//				String mob = lastEntity.getType().getName().toLowerCase();
//				if (mob.startsWith("a") || mob.startsWith("e") || mob.startsWith("i") || mob.startsWith("o") || mob.startsWith("u")) {
//					msg = "An " + mob + " ate " + utils.getDName(p) + ChatColor.WHITE + ".";
//				} else {
//					msg = "A " + mob + " ate " + utils.getDName(p) + ChatColor.WHITE + ".";
//				}
//			}
//		if (cause == DamageCause.ENTITY_EXPLOSION) {
//			String mob = lastEntity.getType().getName().toLowerCase();
//			if (mob.startsWith("a") || mob.startsWith("e") || mob.startsWith("i") || mob.startsWith("o") || mob.startsWith("u")) {
//				msg += "got too close to an " + lastEntity.getType().getName().toLowerCase() + ".";
//			} else {
//				msg += "got too close to a " + lastEntity.getType().getName().toLowerCase() + ".";
//			}
////			msg += "got too close to a " + lastEntity.getType().getName().toLowerCase();
//		}
//		if (cause == DamageCause.FALL)
//			msg += "fell from too high.";
//		if (cause == DamageCause.FALLING_BLOCK)
//			msg += " was under something heavy...";
//		if (cause == DamageCause.FIRE)
//			msg += "tried fighting fire with their face.";
//		if (cause == DamageCause.FIRE_TICK)
//			msg += "touched the hot-hot.";
//		if (cause == DamageCause.LAVA)
//			msg += "found their melting point in lava.";
//		if (cause == DamageCause.LIGHTNING)
//			msg += "was struck from heaven.";
//		if (cause == DamageCause.MAGIC)
//			msg += "got avadakadavra''d.";
//		if (cause == DamageCause.MELTING)
//			msg += "awkwardly melted...?";
//		if (cause == DamageCause.POISON)
//			msg += "overdoesed on drugs.";
//		if (cause == DamageCause.PROJECTILE) {
//			if (p.getKiller() != null) {
//				msg += "took an arrow to the knee from " + utils.getDName(p.getKiller()) + ".";
//			} else {
//				msg += "took an arrow to the knee.";
//			}
//		}
//		if (cause == DamageCause.STARVATION)
//			msg += "could't get to the kitchen in time.";
//		if (cause == DamageCause.SUFFOCATION)
//			msg = utils.getDName(p) + ChatColor.WHITE + " lacked their neccessary oxygen.";
//		if (cause == DamageCause.SUICIDE)
//			msg += "hated their life.";
//		if (cause == DamageCause.THORNS)
//			msg = utils.getDName(p.getKiller()) + ChatColor.WHITE + " took negative damage. Killing " + utils.getDName(p);
//		if (cause == DamageCause.VOID)
//			msg += "fell tried to fight the system.";
//		if (cause == DamageCause.WITHER)
//			msg += "took a witherball to seriously.";
//		subEvent.setDeathMessage(msg);
	}
	
	@EventHandler
	public void onCommandSent(final PlayerCommandPreprocessEvent e) {
		sql.logCommand(e.getPlayer(), e.getMessage());
		String msg = e.getMessage().toLowerCase();
		if (msg.startsWith("/f c ") || msg.equals("/f c") || msg.equals("/f chat")) {
			String c = sql.getChannel(e.getPlayer().getName());
			if (c != null && c.equals("f")) {
				sql.setChannel(e.getPlayer().getName(), "g");
				msg(e.getPlayer(), "Channel set to " + utils.getChannelPrefix(e.getPlayer()) + "&oGlobal");
			} else {
				sql.setChannel(e.getPlayer().getName(), "f");
				msg(e.getPlayer(), "Channel set to " + utils.getChannelPrefix(e.getPlayer()) + "&oFaction");
			}
			
			e.setCancelled(true);
		}
		if (msg.startsWith("/t c ") || msg.equals("/t c") || msg.equals("/t chat")) {
			String c = sql.getChannel(e.getPlayer().getName());
			if (c != null && c.equals("t")) {
				sql.setChannel(e.getPlayer().getName(), "g");
				msg(e.getPlayer(), "Channel set to " + utils.getChannelPrefix(e.getPlayer()) + "&oGlobal");
			} else {
				sql.setChannel(e.getPlayer().getName(), "t");
				msg(e.getPlayer(), "Channel set to " + utils.getChannelPrefix(e.getPlayer()) + "&oTown");
			}
			
			e.setCancelled(true);
		}
		if (msg.startsWith("/n c ") || msg.equals("/n c") || msg.equals("/n chat")) {
			String c = sql.getChannel(e.getPlayer().getName());
			if (c != null && c.equals("n")) {
				sql.setChannel(e.getPlayer().getName(), "g");
				msg(e.getPlayer(), "Channel set to " + utils.getChannelPrefix(e.getPlayer()) + "&oGlobal");
			} else {
				sql.setChannel(e.getPlayer().getName(), "n");
				msg(e.getPlayer(), "Channel set to " + utils.getChannelPrefix(e.getPlayer()) + "&oNation");
			}
			e.setCancelled(true);
		}
		if (msg.equalsIgnoreCase("/ac") || msg.equalsIgnoreCase("/a") || msg.toLowerCase().startsWith("/ac ") || msg.toLowerCase().startsWith("/a ")) {
			if (!e.getPlayer().hasPermission("ddchat.staffchat")) {
				msg(e.getPlayer(), "You don't have permission for staff chat.");
				e.setCancelled(true);
				return;
			}
			String c = sql.getChannel(e.getPlayer().getName());
			if (c != null && c.equals("s")) {
				sql.setChannel(e.getPlayer().getName(), "g");
				msg(e.getPlayer(), "Channel set to " + utils.getChannelPrefix(e.getPlayer()) + "&oGlobal");
			} else {
				sql.setChannel(e.getPlayer().getName(), "s");
				msg(e.getPlayer(), "Channel set to " + utils.getChannelPrefix(e.getPlayer()) + "&oStaff Chat");
			}
			e.setCancelled(true);
		}
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			if (p.hasPermission("ddchat.commandsnoop") && !disabledSpys.contains(p) ) {
				p.sendMessage(utils.getPlayerListName(e.getPlayer()) + ChatColor.GRAY + ": " + e.getMessage());
			}
		}
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		
		try {
			out.writeUTF("Forward");
			out.writeUTF("ALL");
			out.writeUTF("DDCustomPlugin_Chat");
			
			ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
			DataOutputStream msgout = new DataOutputStream(msgbytes);
			msgout.writeUTF("commandspy");
			msgout.writeUTF(utils.getPlayerListName(e.getPlayer()) + ChatColor.GRAY + ": " + e.getMessage()); // You can do anything you want with msgout
			
			out.writeShort(msgbytes.toByteArray().length);
			out.write(msgbytes.toByteArray());
		} catch (Exception e2) {}
		Player p = Bukkit.getOnlinePlayers()[0];
		
		p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
		e.getPlayer().setPlayerListName(utils.getPlayerListName(e.getPlayer()));
		e.getPlayer().setDisplayName(utils.getDName(e.getPlayer()));
		e.getPlayer().setCustomName(utils.getDName(e.getPlayer()));
	}
	
	public void bungeePlayerListReceived(String server, String[] playerList, Player player) {
		
		updateGlobalPlayerList(playerList);
		ArrayList<String> playerList2 = new ArrayList<String>();
		for (String str : playerList) {
			playerList2.add(str);
		}
		
		
		for (String playerName : quitPlayers.keySet()) { // Loops through all players returned by Bungee
			if (playerList2.contains(playerName)) {
				Bukkit.getServer().broadcastMessage("     " + ChatColor.YELLOW + "~ " + quitPlayers.get(playerName));
			} else {
				Bukkit.getServer().broadcastMessage("     " + ChatColor.RED + "- " + quitPlayers.get(playerName));
			}
			quitPlayers.remove(playerName);
		}
	}
	
	public void updateGlobalPlayerList(String[] players) {
		globalPlayerList.clear();
		for (String data : players) {
			globalPlayerList.add(data);
		}
	}
	
	@EventHandler
	public void onPlayerKissEvent(PlayerInteractEntityEvent event) {
		final Player p = event.getPlayer();
		if (!utils.getDate(p).equals("")) {
			if (event.getRightClicked() instanceof Player) {
				if (sql.getDatingPartner(p.getName()) != null && sql.getDatingPartner(p.getName()).equals(((Player) event.getRightClicked()).getName())) {
					playWolfHearts(p.getLocation());
					playWolfHearts(event.getRightClicked().getLocation());
				}
			}
		}
	}
	
	public void playWolfHearts(Location loc) {
        World world = loc.getWorld();
        Wolf wo = (Wolf) world.spawn(loc, Wolf.class);
        wo.remove();
        net.minecraft.server.v1_6_R3.World nmsWorld = ((CraftWorld) world).getHandle();
        net.minecraft.server.v1_6_R3.EntityWolf nmsWolf = (EntityWolf) ((CraftEntity) wo).getHandle();
        nmsWorld.broadcastEntityEffect(nmsWolf, (byte) 7);
    }

	private void sendMessage(String msg, Player player) {
		String world = player.getWorld().getName();
		
		if (world.equals("Survival")) {
			for (Player p : getRecievers(player)) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', utils.getFaction(player, p)) + msg);
			}
		} else if (world.equals("TownWorld")) {
			for (Player p : getRecievers(player)) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', utils.getTown(player)) + msg);
			}
		} else {
			for (Player p : getRecievers(player)) {
				p.sendMessage(msg);
			}
		}
		String channel = sql.getChannel(player.getName());
		if (channel == null)
			channel = "g";
		if (channel.equals("g") || channel.equals("s")) {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			 
			try {
				out.writeUTF("Forward");
				out.writeUTF("ALL");
				out.writeUTF("DDCustomPlugin_Chat");
				
				ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
				DataOutputStream msgout = new DataOutputStream(msgbytes);
				msgout.writeUTF(channel + "chat");
				msgout.writeUTF(player.getName());
				msgout.writeUTF(msg);
				msgout.writeBoolean(sql.getBadProfile(player.getName()));
				 
				out.writeShort(msgbytes.toByteArray().length);
				out.write(msgbytes.toByteArray());
			} catch (Exception e2) {}
			player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
		}
	}
	
	private void msg(CommandSender sender, String msg) {
		if (sender instanceof Player) {
			((Player) sender).sendMessage(ChatColor.RESET + "[" + ChatColor.RED + "MSG" + ChatColor.WHITE + "] " + ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', msg));
		} else {
			log(msg);
		}
	}
	private void mmsg(CommandSender sender, String msg) {
		if (sender instanceof Player) {
			((Player) sender).sendMessage(ChatColor.AQUA + "[" + ChatColor.RED + "Dating" + ChatColor.AQUA + "] " + ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', msg));
		} else {
			log(msg);
		}
	}
	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = plugin.getServer()
				.getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	public void log(String info) {
		plugin.getLogger().info("<Chat> " + info);
	}
}
