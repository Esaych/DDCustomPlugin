package net.diamonddominion.esaych.survival;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

public class FairFly {
	private CustomPlugin plugin;

	public FairFly(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("fly")) {
			if ((sender instanceof Player)) {
				Player player = (Player)sender;
				if (args.length == 0) {
					if (player.hasPermission("fly.fly"))
						if (player.getAllowFlight()) {
							player.setFlying(false);
							player.setAllowFlight(false);
							player.sendMessage(ChatColor.GOLD + "You were put out of fly mode.");
						} else {
							player.setAllowFlight(true);
							player.setFlySpeed(0.1F);
							player.sendMessage(ChatColor.GOLD + "You were put into fly mode.");
						}
				}
				else if (args.length == 1) {
					if (player.hasPermission("fly.other")) {
						Player otherplayer = Bukkit.getPlayer(args[0]);
						if (otherplayer != null) {
							if (otherplayer.getAllowFlight()) {
								otherplayer.setFlying(false);
								otherplayer.setAllowFlight(false);
								otherplayer.sendMessage(ChatColor.GOLD + "You were put out of fly mode.");
								player.sendMessage(args[0] + 
										" was put out of fly mode!");
							} else {
								otherplayer.setAllowFlight(true);
								otherplayer.setFlySpeed(0.1F);
								otherplayer.sendMessage(ChatColor.GOLD + "You were put into fly mode.");
								player.sendMessage(args[0] + 
										" was sent to sky!");
							}
						}
						else player.sendMessage(args[0] + 
								" is not online or doesn't exist!");
					}
				}
				else {
					return false;
				}
			} else {
				if (args.length == 1) {
					Player otherplayer = Bukkit.getPlayer(args[0]);
					if (otherplayer != null) {
						if (otherplayer.getAllowFlight()) {
							otherplayer.setFlying(false);
							otherplayer.setAllowFlight(false);
							otherplayer.sendMessage(ChatColor.GOLD + "You were put out of fly mode.");
							log(args[0] + " was put out of fly mode!");
						} else {
							otherplayer.setAllowFlight(true);
							otherplayer.setFlySpeed(0.1F);
							otherplayer.sendMessage(ChatColor.GOLD + "You were put into fly mode.");
							log(args[0] + " was sent to sky!");
						}
					}
					else log(args[0] + " is not online or doesn't exist!");
				} else {
					log("Console's can't fly!!!!!!");
				}
			}
			return true;
		}if (cmd.getName().equalsIgnoreCase("flyspeed")) {
			Player player = (Player)sender;
			if ((sender instanceof Player)) {
				if (args.length == 0)
					return false;
				if (args.length == 1) {
					int speed = 1;
					try {
						speed = Integer.parseInt(args[0]);
					} catch (NumberFormatException e) {
						player.sendMessage("Only integer values are allowed here");
						return true;
					}

					if ((player.hasPermission("fly.speed") | player.hasPermission("fly.speed.10"))) {
						if ((speed < -10) || (speed > 10)) {
							player.sendMessage("Speed can't be less than -10 or more than 10");
						} else {
							player.setFlySpeed(speed / 10.0F);
							player.sendMessage("Flyspeed set to " + speed);
						}
						return true;
					}if (player.hasPermission("fly.speed.9")) {
						if ((speed <= 9) && (speed >= -9)) {
							player.setFlySpeed(speed / 10.0F);
							player.sendMessage("Flyspeed set to " + speed);
						} else {
							player.sendMessage("You don't have permission to do that");
						}
						return true;
					}if (player.hasPermission("fly.speed.8")) {
						if ((speed <= 8) && (speed >= -8)) {
							player.setFlySpeed(speed / 10.0F);
							player.sendMessage("Flyspeed set to " + speed);
						} else {
							player.sendMessage("You don't have permission to do that");
						}
						return true;
					}if (player.hasPermission("fly.speed.7")) {
						if ((speed <= 7) && (speed >= -7)) {
							player.setFlySpeed(speed / 10.0F);
							player.sendMessage("Flyspeed set to " + speed);
						} else {
							player.sendMessage("You don't have permission to do that");
						}
						return true;
					}if (player.hasPermission("fly.speed.6")) {
						if ((speed <= 6) && (speed >= -6))
							player.setFlySpeed(speed / 10.0F);
						else {
							player.sendMessage("You don't have permission to do that");
						}
						return true;
					}if (player.hasPermission("fly.speed.5")) {
						if ((speed <= 5) && (speed >= -5)) {
							player.setFlySpeed(speed / 10.0F);
							player.sendMessage("Flyspeed set to " + speed);
						} else {
							player.sendMessage("You don't have permission to do that");
						}
						return true;
					}if (player.hasPermission("fly.speed.4")) {
						if ((speed <= 4) && (speed >= -4)) {
							player.setFlySpeed(speed / 10.0F);
							player.sendMessage("Flyspeed set to " + speed);
						} else {
							player.sendMessage("You don't have permission to do that");
						}
						return true;
					}if (player.hasPermission("fly.speed.3")) {
						if ((speed <= 3) && (speed >= -3)) {
							player.setFlySpeed(speed / 10.0F);
							player.sendMessage("Flyspeed set to " + speed);
						} else {
							player.sendMessage("You don't have permission to do that");
						}
						return true;
					}if (player.hasPermission("fly.speed.2")) {
						if ((speed <= 2) && (speed >= -2)) {
							player.setFlySpeed(speed / 10.0F);
							player.sendMessage("Flyspeed set to " + speed);
						} else {
							player.sendMessage("You don't have permission to do that");
						}
						return true;
					}
					player.sendMessage("You don't have permission to do that");

					return true;
				}
			}
		}

		return false;
	}

	//Flying player attacks another player
	public void onHit(EntityDamageByEntityEvent event)
	{
		if ((event.getDamager() instanceof Player)) {
			Player damager = (Player)event.getDamager();
			if (damager.getAllowFlight()) { //if punch player and they have FLY ENABLED
				if ((damager.isFlying()) && (!damager.hasPermission("Fly.damage")) && (damager.getGameMode() != GameMode.CREATIVE))
					event.setCancelled(true);

				if (!damager.isFlying() && event.getEntity() instanceof Player && damager.getGameMode() != GameMode.CREATIVE) {
					damager.setFlying(false);
					damager.setAllowFlight(false);
					damager.sendMessage(ChatColor.GOLD + "You were put out of fly mode.");
				}
			}
		}
	}

	//Flying player takes damage
	public void onDamage(EntityDamageEvent event)
	{
		if ((event.getEntity() instanceof Player)) {
			Player receiver = (Player)event.getEntity();
			if ((receiver.isFlying()) && (receiver.hasPermission("Fly.invincible")))
				event.setCancelled(true);
		}
	}

	//Flying player tries to shoot a bow
	@SuppressWarnings("deprecation")
	public void onShoot(EntityShootBowEvent event)
	{
		if ((event.getEntity() instanceof Player)) {
			Player archer = (Player)event.getEntity();
			if (archer.getAllowFlight()) {
				if ((archer.isFlying()) && (!archer.hasPermission("Fly.damage")) && 
						(archer.getGameMode() != GameMode.CREATIVE)) {
					event.setCancelled(true);
					archer.updateInventory();
				}
				if (!archer.isFlying() && archer.getGameMode() != GameMode.CREATIVE) {
					archer.setFlying(false);
					archer.setAllowFlight(false);
					archer.sendMessage(ChatColor.GOLD + "You were put out of fly mode.");
				}
			}
		}
	}

	public void log(String info) {
		plugin.getLogger().info("<FairFly> " + info);
	}
}
