package net.diamonddominion.esaych.survival;

import java.util.ArrayList;

import net.diamonddominion.esaych.CustomPlugin;
import net.diamonddominion.esaych.global.BungeeGlobalCommand;
import net.diamonddominion.esaych.util.DonationCredit;
import net.diamonddominion.esaych.util.Vote;
import net.diamonddominion.esaych.util.VoteSQL;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.Votifier;
import com.vexsoftware.votifier.model.VotifierEvent;

public class VoteReceiver implements Listener {
	private CustomPlugin plugin;

	public VoteReceiver(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		log("Enabled");
	}
	
	@EventHandler
	public void voteMade(VotifierEvent event) {
//		event.getVote().setUsername("Esaych");
		String username = event.getVote().getUsername();

		String foundUser = VoteSQL.userExists(username);
		if (foundUser.equals("")) {
			Votifier.getInstance().getServer().broadcastMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), "&6[&bDD&2Votes&6] &aA &4nonexistent&a player named '%name' voted for the server!".replaceAll("%name", username)));
			return;
		}
		Votifier.getInstance().getServer().broadcastMessage(ChatColor.translateAlternateColorCodes("&".charAt(0), "&6[&bDD&2Votes&6] &a%name voted for the server!".replaceAll("%name", foundUser)));
		com.vexsoftware.votifier.model.Vote inVote = event.getVote();
		new Vote(foundUser, (int) (System.currentTimeMillis()/1000), inVote.getServiceName(), 0, false);
		plugin.voteRewards.msg(foundUser, "Your vote from " + inVote.getServiceName() + " has been added! &e&lThank you!");

//		if (VoteSQL.getVotesLast24(foundUser) == 1)
//			BungeeGlobalCommand.sendCommand("broadcast " + foundUser + " just voted for today! Did you?");
		
		ArrayList<Vote> votes = VoteSQL.filterVotes(foundUser, (int) (System.currentTimeMillis()/1000 - 60*60*24), null, false, true);
		
		if (votes.size() == 3) {
			DonationCredit.addCredits(foundUser, 1);
			for (Vote dailyVote : votes) {
				dailyVote.donationcreditVote(true);
			}
			plugin.voteRewards.msg(foundUser, "You have been awarded 1 ($0.25) donation credits for voting 3 times today.");
		}
	}

	public void log(String info) {
		plugin.getLogger().info("<VoteReceiver> " + info);
	}
}
