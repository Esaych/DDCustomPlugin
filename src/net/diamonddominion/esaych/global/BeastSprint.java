package net.diamonddominion.esaych.global;

import java.util.ArrayList;
import java.util.Set;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.mcore.ps.PS;


public class BeastSprint {
	private CustomPlugin plugin;

	public BeastSprint(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		if (plugin.getServer().getPluginManager().getPlugin("Factions") != null) {
			factionsEnabled = true;
			Faction faction = FactionColls.get().getForWorld("Survival").getByName("SafeZone");
			Set<PS> chunks_ps = BoardColls.get().getChunks(faction);
			for (PS ps : chunks_ps) {
				safezone.add(ps.asBukkitChunk());
			}
		}
		Player insanity = Bukkit.getPlayer("InsanityGaz");
		if (insanity != null)
			disabledSprint.add(insanity);
		log("Enabled");
	}
	private ArrayList<Player> disabledSprint = new ArrayList<Player>();
	boolean factionsEnabled = false;
	private ArrayList<Chunk> safezone = new ArrayList<Chunk>();

	public boolean onCommand(CommandSender sender) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
//			if (!player.hasPermission("beastsprint.can")) {
//				player.sendMessage(ChatColor.GREEN + "You can't use beast sprint");
//				return true;
//			}
			if (disabledSprint.contains(player)) {
				player.sendMessage(ChatColor.GREEN + "Beast Sprint enabled.");
				disabledSprint.remove(player);
			} else {
				player.sendMessage(ChatColor.GREEN + "Beast Sprint disabled.");
				disabledSprint.add(player);
			}
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		Location loc = player.getLocation();
		Block glass = player.getLocation().add(0,-2,0).getBlock();
		//(factionsEnabled && safezone.contains(loc.getChunk()))
		if ((player.hasPermission("beastsprint.can") || (glass.getType().equals(Material.GLASS) && glass.getData() == (byte)2) || loc.getWorld().getName().equals("Build")) && !disabledSprint.contains(player)) {
			if (player.isSprinting()) {
				if ((player.hasPermission("beastsprint.can")))
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 10));
				else
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 5));
				player.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 1, 50);
			} else {
				player.removePotionEffect(PotionEffectType.SPEED);
			}
		}
	}
	
	public void log(String info) {
		plugin.getLogger().info("<BeastSprint> " + info);
	}
}
