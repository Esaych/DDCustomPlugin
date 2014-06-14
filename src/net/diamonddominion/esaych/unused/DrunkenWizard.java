package net.diamonddominion.esaych.unused;

import java.util.HashMap;
import java.util.Map;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class DrunkenWizard {
	private CustomPlugin plugin;
	
	private Map<Player, Integer> tasks = new HashMap<Player, Integer>();
	private Map<Player, Long> timeout = new HashMap<Player, Long>();
	
	public DrunkenWizard(CustomPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void enable() {
		log("Enabled");
	}
	
	private void attemptTeleport(final Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*10, 1000));
//		player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20*10, 50));
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*10, 1));
		int task = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override 
			public void run() {
				tpPlayer(player);
			}
		}, 20*5);
		tasks.put(player, task);
	}
	
	private void cancelTeleport(Player player) {
		if (tasks.containsKey(player)) {
			Bukkit.getScheduler().cancelTask(tasks.get(player));
			tasks.remove(player);
		}
		removeEffects(player);
		player.sendMessage(ChatColor.RED + "Keep sneaking to reach potion shop.");
	}
	
	private void tpPlayer(Player player) {
		for (int a = 0; a < 50; a++)
			player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
		if (player.getLocation().getBlock().equals(new Location(Bukkit.getWorld("Survival"), 173, 64, 227).getBlock()))
			player.teleport(new Location(Bukkit.getWorld("Survival"), 179.5, 80, 227.5, player.getLocation().getYaw(), player.getLocation().getPitch()));
		else
			player.teleport(new Location(Bukkit.getWorld("Survival"), 173.5, 64, 227.5, player.getLocation().getYaw(), player.getLocation().getPitch()));
		for (int a = 0; a < 20; a++)
			player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
		timeout.put(player, System.currentTimeMillis()/1000);
		removeEffects(player);
	}
	
	private void removeEffects(Player player) {
		player.removePotionEffect(PotionEffectType.CONFUSION);
		player.removePotionEffect(PotionEffectType.BLINDNESS);
//		player.removePotionEffect(PotionEffectType.NIGHT_VISION);
	}
	
	public void onPlayerSneaks(PlayerToggleSneakEvent e) {
		Player player = e.getPlayer();
		if (player.getLocation().getBlock().equals(new Location(Bukkit.getWorld("Survival"), 179, 80, 227).getBlock()) || 
				player.getLocation().getBlock().equals(new Location(Bukkit.getWorld("Survival"), 173, 64, 227).getBlock())) {
			if (timeout.containsKey(player)) {
				if (timeout.get(player) + 5 > System.currentTimeMillis()/1000)
					return;
			}
			if (e.isSneaking()) {
				attemptTeleport(player);
			} else {
				cancelTeleport(player);
			}
		}
	}
	
	public void log(String info) {
		plugin.getLogger().info("<DrunkenWizard> " + info);
	}
}
