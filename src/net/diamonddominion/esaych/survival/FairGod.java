package net.diamonddominion.esaych.survival;

import java.util.HashMap;
import java.util.Map;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;


public class FairGod {
	
	public CustomPlugin plugin;
	
	public FairGod(CustomPlugin plugin) {
		this.plugin = plugin;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Boolean> gods = new HashMap();

	public void enable()
	{
//		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		log("Enabled");
	}
	
	public boolean godCommand(CommandSender sender, String[] args)
	{
		Player player = null;
		if ((sender instanceof Player))
		{
			player = (Player)sender;
		}
		else
		{
			log("You must be a player to use that command!");
			return true;
		}

		if (args.length == 0)
		{
			if (player.hasPermission("fairgod.fair"))
			{
				toggleGod(player, true);
				return true;
			}

			player.sendMessage(ChatColor.RED + "You don't have permission to do that!");
			return true;
		}

		return false;
	}

	public boolean toggleGod(Player player, boolean fair)
	{
		if (this.gods.containsKey(player.getName()))
		{
			this.gods.remove(player.getName());
			player.sendMessage(ChatColor.GOLD + "You are now out of God mode.");

			return true;
		}

		this.gods.put(player.getName(), Boolean.valueOf(fair));
		player.sendMessage(ChatColor.GOLD + "You are now in God mode.");

		if (!fair)
		{
			player.sendMessage(ChatColor.GOLD + "You are in unfair mode! You may harm other players.");
		}
		return false;
	}

	public boolean isGod(Player player)
	{
		if (this.gods.containsKey(player.getName()))
		{
			return true;
		}

		return false;
	}

	public boolean isFair(Player player)
	{
		return ((Boolean)this.gods.get(player.getName())).booleanValue();
	}

//	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
//	{
//		if (commandLabel.equalsIgnoreCase("god")) {
//			log("Recieved Command");
//			Player player = null;
//			if ((sender instanceof Player))
//			{
//				player = (Player)sender;
//			}
//			else
//			{
//				log("You must be a player to use that command!");
//				return true;
//			}
//
//			if (args.length == 0)
//			{
//				if (player.hasPermission("fairgod.fair"))
//				{
//					toggleGod(player, true);
//					return true;
//				}
//
//				player.sendMessage(ChatColor.RED + "You don't have permission to do that!");
//				return true;
//			}
//
//			if (args.length == 1)
//			{
//				if (player.hasPermission("fairgod.unfair"))
//				{
//					toggleGod(player, false);
//					return true;
//				}
//
//				player.sendMessage(ChatColor.RED + "You don't have permission to do that!");
//				return true;
//			}
//
//			return false;
//		}
//		return false;
//	}
	
//	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if ((event.getDamager() instanceof Player)) {
			if ((event.getEntity() instanceof Player)) {
				Player damager = (Player) event.getDamager();
				if (isGod(damager)) {
					if (isFair(damager)) {
						if (!damager.getWorld().getName().equals("GameWorld")) {
							if (damager.getGameMode() == GameMode.SURVIVAL) {
								event.setCancelled(true);
								toggleGod(damager, true);
							}
						} else {
							toggleGod(damager, true);
						}
					}
				}
			}
		}
	}

//	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if ((event.getEntity() instanceof Player)) {
			Player player = (Player) event.getEntity();
			if (isGod(player)) {
				if (!player.getWorld().getName().equals("EventWorld")) {
					event.setCancelled(true);
				} else {
					toggleGod(player, true);
				}
			}
		}
	}

//	@EventHandler(priority = EventPriority.NORMAL)
	public void onShoot(EntityShootBowEvent event) {
		if ((event.getEntity() instanceof Player)) {
			Player archer = (Player) event.getEntity();
			if (isGod(archer)) {
				if (!archer.getWorld().getName().equals("EventWorld")) {
					if (archer.getGameMode() == GameMode.SURVIVAL) {
						event.setCancelled(true);
						toggleGod(archer, true);
					}
				} else {
					toggleGod(archer, true);
				}
			}
		}
	}
	
	public void log(String info) {
		plugin.getLogger().info("<FairGod> " + info);
	}
	
}