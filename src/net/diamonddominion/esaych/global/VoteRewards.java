package net.diamonddominion.esaych.global;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.diamonddominion.esaych.CustomPlugin;
import net.diamonddominion.esaych.util.DonationCredit;
import net.diamonddominion.esaych.util.SQL;
import net.diamonddominion.esaych.util.Vote;
import net.diamonddominion.esaych.util.VoteSQL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class VoteRewards {

	CustomPlugin plugin;
	
	public VoteRewards(CustomPlugin plugin) {
		this.plugin = plugin;
	}
	public Map<String, Map<Integer, Integer>> rewardCache = new HashMap<String, Map<Integer, Integer>>();
	String head = "&2|&6-----------=-=-----=-=&2[ &bD D &2]&6=-=-----=-=---------&2|";

	@SuppressWarnings("deprecation")
	public void enable() {
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				updateRewards();
			}
		}, 5, 20*5);
		
		log("Enabled");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (args.length == 0 || !(sender instanceof Player)) {
			msg(sender, head);
			msg(sender, "  &lGO TO &b&ohttp://www.diamonddominion.net/vote");
			msg(sender, "  &a&lVOTE FOR US! -=- YOU WILL GET PRIZES");
			msg(sender, "&2|&b/vote redeem&6 to exchange votes prizes.");
			msg(sender, "&2|&b/vote stats&6 to see what you currently have.");
			msg(sender, head);
//			msg(sender, "Visit the website to use up donation credits you earn.");
//			msg(sender, "Voting helps our server gain popularity on server listing sites.");
			return true;
		} else {
			Player player = (Player) sender;
			String arg = args[0];
			if (arg.equalsIgnoreCase("redeem")) {
				if (args.length < 3) {
					msg(player, head);
					msg(player, "You have &b" + VoteSQL.getVotesNotRewarded(player.getName()) + "&6 votes to buy prizes.");
					msg(player, "Type &b/vote redeem <prize id> <prize amount>");
					msg(player, "| &2PrizeID&6 | &2VoteCost&6 | -- &2Prize Name &6--");
					for (int x = 1; x <= VotePrize.prizeAmount; x++) {
						msg(player, "&6 -- &b" + x + " &6-- | -- &b" + VotePrize.getCost(x) + "&6 --- | &a" + VotePrize.getDescription(x));
					}
					msg(player, head);
					return true;
				} else {
					String strid = args[1];
					int id;
					try {
						id = Integer.parseInt(strid);
					} catch (Exception e) {
						msg(player, "&4Error: &cPrize ID is incorrect.");
						msg(player, "Type &b/vote redeem <prize id> <prize amount>");
						return true;
					}
					if (!VotePrize.isValid(id)) {
						msg(player, "&4Error: &cThere is no such prize id.");
						msg(player, "Type &b/vote redeem <prize id> <prize amount>");
						return true;
					}
					String strvotes = args[2];
					int votesNum;
					try {
						votesNum = Integer.parseInt(strvotes);
					} catch (Exception e) {
						msg(player, "&4Error: &cVote number is incorrect.");
						msg(player, "Type &b/vote redeem <prize id> <prize amount>");
						return true;
					}
					redeem(player, id, votesNum);
					return true;
				}
			} else if (arg.equalsIgnoreCase("stats")) {
				msg(player, head);
				msg(player, "&5Your stats:");
				msg(player, "Unredeemed votes: &a" + VoteSQL.getVotesNotRewarded(player.getName()));
				msg(player, "Donation credits: &a" + DonationCredit.getCredits(player.getName()));
				msg(player, "Total votes ever: &a" + VoteSQL.getAllPlayerVotes(player.getName()));
				msg(player, "Votes in last 24 hours: &a" + VoteSQL.getVotesLast24(player.getName()));
				msg(player, head);
				return true;
			} else if (arg.equalsIgnoreCase("complete")) {
				if (!redeemQueue.containsKey(player.getName())) {
					msg(player, "&4Error: &cYou do not have a pending prize request.");
					msg(player, "Type &b/vote redeem <prize id> <prize amount>");
					return true;
				}
				int id = Integer.parseInt(redeemQueue.get(player.getName()).split(";")[0]);
				int amount = Integer.parseInt(redeemQueue.get(player.getName()).split(";")[1]);
				redeemQueue.remove(player.getName());
				ArrayList<Vote> votes = VoteSQL.filterVotes(player.getName(), 0, null, true, false);
				int cost = VotePrize.getCost(id) * amount;
				int has = votes.size();
				if (cost > has) {
					msg(player, "&4Error: &cYou can't redeem a prize that costs " + cost + " votes, but only have " + has + " votes to spend.");
					return true;
				}
				if (id == 4 || id == 5) {
					msg(player, "&4Error: &cPrizes 4-5 have not been set up yet.");
					return true;
				}
				String name = VotePrize.getName(id).toLowerCase();
				if (name.equals("fly")) {
					if (player.hasPermission("fly.fly")) {
						msg(player, "&4Error: &cYou already have Premium! You don't need to pay votes for Fly Mode");
						return true;
					}
					if (!plugin.detectedServer().equals("survival")) {
						msg(player, "&4Error: &cFly Mode is only available on the Survival server");
						return true;
					}
				}
				if (name.equals("god")) {
					if (player.hasPermission("fairgod.fair")) {
						msg(player, "&4Error: &cYou already have Premium! You don't need to pay votes for God Mode");
						return true;
					}
					if (!plugin.detectedServer().equals("survival")) {
						msg(player, "&4Error: &cGod Mode is only available on the Survival server");
						return true;
					}
				}
				if (name.equals("creative")) {
					if (player.hasPermission("customplugin.creative")) {
						msg(player, "&4Error: &cYou already have DemiGod! You don't need to pay votes for Creative");
						return true;
					}
					if (!plugin.detectedServer().equals("survival")) {
						msg(player, "&4Error: &cCreative Mode is only available on the Survival server");
						return true;
					}
					int a = 0;
					for (ItemStack i : player.getInventory()) {
						if (i != null)
							a++;
					}
					boolean helm = (player.getInventory().getHelmet() != null);
					boolean ches = (player.getInventory().getChestplate() != null);
					boolean legg = (player.getInventory().getLeggings() != null);
					boolean boot = (player.getInventory().getBoots() != null);
					if (a > 0 || helm || ches || legg || boot) {
						msg(player, "&4Error: &cYou must clear your inventory completely to switch to creative mode.");
						return true;
					}
				}
				
				
				// ---- ----  REWARD SCRIPT  ---- ----
				
				//Pay Cost
				int voteCounter = 1;
				for (Vote vote : votes) {
					if (voteCounter <= cost) {
						vote.rewardVote(id);
					} else {
						break;
					}
					voteCounter++;
				}
				
				//Add to DB the reward
				int mins = VotePrize.getExtraVar(id)*amount;
				int expiretime = (int) (System.currentTimeMillis()/1000) + (60*mins);
				
				try {
					Statement st = (Statement) SQL.getConnection().createStatement(); 
					ResultSet result = st.executeQuery("SELECT * FROM `dd-voterewards` WHERE `player` = '" + player.getName() + "' && `expiretime` > '" + System.currentTimeMillis()/1000 + "';");

					while (result.next()) {
						int qid = result.getInt("prizeid");
						int qexpire = result.getInt("expiretime");
						if (qid == id) {
							expiretime = qexpire + (60*mins);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				SQL.singleQuery("INSERT INTO `dd-voterewards`(`player`, `prizeid`, `expiretime`) VALUES ('" + player.getName() + "' , '" + id + "', '" + expiretime + "')");		
				
				msg(player, head);
				
				if (id <= 5) {
					Map<Integer, Integer> data = new HashMap<Integer, Integer>();
					if (rewardCache.containsKey(player)) {
						data = rewardCache.get(player);
					}
					data.put(id, expiretime);
					rewardCache.put(player.getName(), data);
				}
				
				if (name.equals("healing")) {
					double health = player.getHealth();
					health += VotePrize.getExtraVar(id);
					if (health > 20) 
						health = 20;
					player.setFoodLevel((int) health);
					msg(player, "You have been healed.");
				}
				if (name.equals("food")) {
					int foodLevel = player.getFoodLevel();
					foodLevel += VotePrize.getExtraVar(id);
					if (foodLevel > 20) 
						foodLevel = 20;
					player.setFoodLevel(foodLevel);
					msg(player, "You have been fed.");
				}
				if (name.equals("xp")) {
					player.giveExp(VotePrize.getExtraVar(id));
					msg(player, "You have been given &b" + VotePrize.getExtraVar(id) + "&6 XP.");
				}
				if (name.equals("fly")) {
					player.setAllowFlight(true);
					player.setFlying(true);
					msg(player, "You are now flying.");
				}
				if (name.equals("god")) {
					if (!plugin.fairGod.isGod(player)) {
						plugin.fairGod.toggleGod(player, true);
						msg(player, "You now have God mode.");
					}
				}
				if (name.equals("creative")) {
					player.setGameMode(GameMode.CREATIVE);
					msg(player, "You are now in creative.");
				}

				
				// ---- ----  END REWARD SCRIPT  ---- ----
				msg(player, "You now have &b" + VoteSQL.getVotesNotRewarded(player.getName()) + "&6 votes to spend.");
				msg(player, head);
				return true;
			} else {
				msg(player, "Command not found");
				return true;
			}
		}
	}
	
	Map<String, String> redeemQueue = new HashMap<String, String>();
	public void redeem(Player player, int id, int prizeamount) {
		int cost = VotePrize.getCost(id) * prizeamount;
		int has = VoteSQL.getVotesNotRewarded(player.getName());
		if (cost > has) {
			msg(player, "&4Error: &cYou can't redeem a prize that costs " + cost + " votes, but only have " + has + " votes to spend.");
			return;
		}
		msg(player, head);
		msg(player, "You are spending &a" + cost + "&6 votes for &a" + prizeamount + "&6 of the prize: &a" + VotePrize.getName(id));
		redeemQueue.put(player.getName(), id + ";" + prizeamount);
		msg(player, "Type &b/vote complete&6 to complete your spending");
		msg(player, head);
	}
	
	public void updateRewards() {
		Connection con = null;
		Map<String, Map<Integer, Integer>> temprewardCache = new HashMap<String, Map<Integer, Integer>>();
		try {
			con = SQL.getConnection();

			Statement st = (Statement) con.createStatement(); 
			ResultSet result = st.executeQuery("SELECT * FROM `dd-voterewards` WHERE `expiretime` > '" + System.currentTimeMillis()/1000 + "';");
			
			while (result.next()) {
				String player = result.getString("player");
				int prizeid = result.getInt("prizeid");
				int expire = result.getInt("expiretime");
				Map<Integer, Integer> data = new HashMap<Integer, Integer>();
				if (temprewardCache.containsKey(player)) {
					data = temprewardCache.get(player);
				}
				data.put(prizeid, expire);
				temprewardCache.put(player, data);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			return;
		}
		updatePlayerRewardStatus(temprewardCache);
		rewardCache = temprewardCache;
	}
	
	private Map<String, Integer> generateData(Map<String, Map<Integer, Integer>> data) {
		Map<String, Integer> set = new HashMap<String, Integer>();
		for (String player : data.keySet()) {
			Map<Integer, Integer> prizeMap = data.get(player);
			for (int i : prizeMap.keySet()) {
				set.put(player + ";" + i, prizeMap.get(i));
			}
		}
		return set;  //RETURNS IN FORMAT: Name;PrizeId > TimeStamp
	}
	
	@SuppressWarnings("deprecation")
	public void updatePlayerRewardStatus(Map<String, Map<Integer, Integer>> tempSet) {
		Map<String, Integer> tempData = generateData(tempSet);
		Map<String, Integer> prevData = generateData(rewardCache);
		int now = (int) (System.currentTimeMillis()/1000);
		String server = plugin.detectedServer();

		for (String data : tempData.keySet()) {
			Player player = Bukkit.getPlayer(data.split(";")[0]);
			int prizeId = Integer.parseInt(data.split(";")[1]);
			String name = VotePrize.getName(prizeId).toLowerCase();

			if (player != null) {
				if (server.equals("survival")) {
					if (name.equals("fly")) {
						player.setAllowFlight(true);
					}
					if (name.equals("god")) {
						if (!plugin.fairGod.isGod(player)) {
							plugin.fairGod.toggleGod(player, true);
						}
					}
					if (name.equals("creative")) {
						player.setGameMode(GameMode.CREATIVE);
					}

					int prizeTime = tempData.get(data) - now;
//					if (prizeTime <= 10) {
//						msg(player, "You have 10 secs remaining for: " + VotePrize.getName(prizeId));
//					} else if (prizeTime <= 60) {
//						msg(player, "You have 1 min remaining for: " + VotePrize.getName(prizeId));
//					} else if (prizeTime <= 120) {
//						msg(player, "You have 2 mins remaining for: " + VotePrize.getName(prizeId));
//					} else if (prizeTime <= 300) {
//						msg(player, "You have 5 mins remaining for: " + VotePrize.getName(prizeId));
//					}
					if (prizeTime >= 60)
						msg(player, "You have &e" + (int)prizeTime/60 + "&6 mins remaining for: &a" + VotePrize.getName(prizeId));
					if (prizeTime < 60)
						msg(player, "You have &e" + ((int)prizeTime/5)*5 + "&6 secs remaining for: &a" + VotePrize.getName(prizeId));
					//Set player to be doing what their reward requests
					//Send reminders of when their reward will run out

				}
			} else {
				msg(player, "Go back to SV! Vote prize time is running out! (And you can't use those prizes in this world)");
			}
		}
		
		for (String data : prevData.keySet()) {
			if (!tempData.containsKey(data)) {
			Player player = Bukkit.getPlayer(data.split(";")[0]);
			int prizeId = Integer.parseInt(data.split(";")[1]);
			String name = VotePrize.getName(prizeId).toLowerCase();
				msg(player, "Your prize: &a" + VotePrize.getName(prizeId) + " &6has expired.");
				if (name.equals("fly")) {
					player.setFlying(false);
					player.setAllowFlight(false);
				}
				if (name.equals("god")) {
					if (!plugin.fairGod.isGod(player))
						plugin.fairGod.toggleGod(player, true);
				}
				if (name.equals("creative")) {
					player.getInventory().clear();
					player.getInventory().setHelmet(new ItemStack(0));
					player.getInventory().setChestplate(new ItemStack(0));
					player.getInventory().setLeggings(new ItemStack(0));
					player.getInventory().setBoots(new ItemStack(0));
					player.setGameMode(GameMode.SURVIVAL);
				}
				//If players not in tempData, set expired & msg
			}
		}
	}


	public void msg(CommandSender player, String msg) {
		if (player instanceof Player) {
			player.sendMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), "&6" + msg.replaceAll("%name", player.getName())));
		} else {
			log(msg);
		}
	}

//	private void cmd(String player, String cmd) {
//		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd.replaceAll("%name", player));
//	}
	
	public void msg(String player, String msg) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		 
		try {
			out.writeUTF("Message");
			out.writeUTF(player);
			out.writeUTF(ChatColor.translateAlternateColorCodes("&".charAt(0), "&6[&bDD&2Votes&6] " + msg));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Player p = Bukkit.getOnlinePlayers()[0];
		 
		p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
	}

	public void log(String info) {
		plugin.getLogger().info("<VoteRewards> " + info);
	}

}
