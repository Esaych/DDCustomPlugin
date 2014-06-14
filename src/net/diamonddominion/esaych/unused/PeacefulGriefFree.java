package net.diamonddominion.esaych.unused;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.PlayerInventory;


public class PeacefulGriefFree {
	private CustomPlugin plugin;
//	private ArrayList<Player> peacefulPlayer = new ArrayList<Player>();

	public PeacefulGriefFree(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override 
			public void run() {
				clearEvil();
			}
		}, 30, 20 * 15);
	}

//	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
//		if (sender instanceof Player) {
//			Player player = (Player) sender;
//			if (peacefulPlayer.contains(player)) {
//				player.sendMessage(ChatColor.GREEN + "Peaceful mode in the GriefFree world was disabled");
//				peacefulPlayer.remove(player);
//			} else {
//				player.sendMessage(ChatColor.GREEN + "Peaceful mode in the GriefFree world was enabled");
//				peacefulPlayer.add(player);
//			}
//			if (!player.getWorld().getName().equals("GriefFree")) {
//				player.sendMessage(ChatColor.RED + "Warning: You don't have peaceful in this world because you are not in GriefFree!");
//			}
//		}
//		return true;
//	}
	
	public void clearEvil() {
		for (Player player : Bukkit.getOnlinePlayers()) {
//			if (peacefulPlayer.contains(player) && player.getWorld().getName().equals("GriefFree")) {
			if (player.getWorld().getName().equals("GriefFree")) {
				PlayerInventory pinv = player.getInventory();
				if (pinv.getHelmet() == null && pinv.getChestplate() == null && pinv.getLeggings() == null && pinv.getBoots() != null && pinv.getBoots().getType() == Material.GOLD_BOOTS) {
					for (Entity e : player.getNearbyEntities(30, 30, 30)) {
						if (e instanceof LivingEntity) {
							if (e instanceof Monster) {
								e.remove();
							}
						}
					}
				}
			}
		}
	}
	
	public void onPlayerCloseInventoryEvent(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if (player.getWorld().getName().equals("GriefFree")) {
			PlayerInventory pinv = player.getInventory();
			if (pinv.getHelmet() == null && pinv.getChestplate() == null && pinv.getLeggings() == null && pinv.getBoots() != null && pinv.getBoots().getType() == Material.GOLD_BOOTS) {
				for (Entity e : player.getNearbyEntities(30, 30, 30)) {
					if (e instanceof LivingEntity) {
						if (e instanceof Monster) {
							e.remove();
						}
					}
				}
			}
		}
	}
	
	public void log(String info) {
		plugin.getLogger().info("<PeacefulGriefFree> " + info);
	}
}
