package net.diamonddominion.esaych.survival;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;


public class DonorShop {
	private CustomPlugin plugin;
	
	private Map<Player, Boolean> going = new HashMap<Player, Boolean>();
	private Location donorBlock;
	private Location pressurePlate;
	
	private Map<Player, Integer> tasks = new HashMap<Player, Integer>();
	private Map<Player, Long> timeout = new HashMap<Player, Long>();
	
	public DonorShop(CustomPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void enable() {
		donorBlock = new Location(Bukkit.getWorld("Survival"), 123.5, 75.5, 198.5);
		pressurePlate = new Location(Bukkit.getWorld("Survival"), 94, 200, 198);
		log("Enabled");
	}
	
	@SuppressWarnings("deprecation")
	public void disable() {
		for (Block b : exploded.keySet()) {
			b.setTypeId(Integer.parseInt(exploded.get(b).split(";")[0]));
			b.setData(Byte.parseByte(exploded.get(b).split(";")[1]));
		}
		if (tnt != null)
			tnt.remove();
	}
	
	private ArrayList<Player> queued = new ArrayList<Player>();
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (event.getPlayer().getWorld().equals(donorBlock.getWorld())) {
			Player player = event.getPlayer();
			Location pLoc = player.getLocation();
			if (!player.hasPermission("customplugin.donorshop"))
				return;
			if (pLoc.distance(donorBlock) < 1.5 && !queued.contains(player)) {
				msg(player, "Punch the quartz below to go to the donor shop.");
				queued.add(player);
			} else if (pLoc.distance(pressurePlate) < 1.5 && !queued.contains(player)) {
				msg(player, "Sneak on the pressure plate to return to spawn.");
				queued.add(player);
			} else if (pLoc.distance(pressurePlate) >= 1.5 && pLoc.distance(donorBlock) >= 1.5 && queued.contains(player)) {
				queued.remove(player);
			}
			if (going.containsKey(player) && !pLoc.getWorld().equals(donorBlock.getWorld())) {
				removePlayer(player);
				msg(player, "WHY ARE YOU IN ANOTHER WORLD! YOU WERE ABOUT TO GO FLYING UP!");
				return;
			}
			if (going.containsKey(player) && going.get(player)) {
				Location pLevelLoc = donorBlock.clone();
				pLevelLoc.setY(pLoc.getY());
				double dis = pLoc.distance(pLevelLoc);
				if (dis > 1.5) { // if the player is out of the scope that brings them up, this stops them
					pLoc.setX(donorBlock.getX());
					pLoc.setZ(donorBlock.getZ());
					player.teleport(pLoc);
				}
				if (pLoc.getY() < donorBlock.getY()) {
					pLoc.setY(donorBlock.getY());
					player.teleport(pLoc);
				}
				
				if (pLoc.getY() > 194) {
					removePlayer(player);
					player.setVelocity(new Vector(-1, .5, 0));
					msg(player, "Welcome to the Donor Shop!");
				} else {
					player.setVelocity(new Vector(0, 1.5, 0));
					player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 50);
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void removePlayer(Player player) {
		for (Block b : exploded.keySet()) {
			b.setTypeId(Integer.parseInt(exploded.get(b).split(";")[0]));
			b.setData(Byte.parseByte(exploded.get(b).split(";")[1]));
		}
		exploded.clear();
		if (tnt != null) {
			tnt.remove();
			tnt = null;
		}
		going.remove(player);
	}
	
	private Entity tnt = null;
	
	public void onPlayerPunchBlock(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null) {
			if (event.getClickedBlock().equals(donorBlock.getBlock())) {
				final Player player = event.getPlayer();
				if (!player.hasPermission("customplugin.donorshop"))
					return;
				if (exploded.size() > 0 || tnt != null) {
					msg(player, "A transport is already commencing! Wait a bit.");
					return;
				}
				msg(player, "Transport Commencing!");
				going.put(player, false);
				tnt = donorBlock.getWorld().spawnEntity(donorBlock.clone().add(0,11,0), EntityType.PRIMED_TNT);
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					@Override 
					public void run() {
						if (going.containsKey(player)) {
							going.put(player, true);
							player.setVelocity(new Vector(0, 2, 0));
						}
					}
				}, 5 * 17);
			}
		}
	}
	
	
	
	/*
	 * Further code is from the DrukenWizard (Code to tp back)
	 */
	
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
		msg(player, "Keep sneaking to reach the spawn.");
	}
	
	private void tpPlayer(Player player) {
		for (int a = 0; a < 50; a++)
			player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 0);
		player.teleport(new Location(donorBlock.getWorld(), donorBlock.getX(), donorBlock.getY() + 1, donorBlock.getZ(), player.getLocation().getYaw(), player.getLocation().getPitch()));
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
		if (player.getLocation().getBlock().equals(pressurePlate.getBlock())) {
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
	
	/*
	 * Events for the DonorShop tp
	 */
	
	public Map<Block, String> exploded = new HashMap<Block, String>();
	
	@SuppressWarnings("deprecation")
	public void onTntExplodeEvent(EntityExplodeEvent event) {
		if (tnt != null && event.getEntity().equals(tnt)) {
			for (Block b : exploded.keySet()) {
				b.setTypeId(Integer.parseInt(exploded.get(b).split(";")[0]));
				b.setData(Byte.parseByte(exploded.get(b).split(";")[1]));
			}
			exploded.clear();
			for (Block b : event.blockList()) {
				exploded.put(b, b.getTypeId() + ";" + b.getData());
				b.setTypeId(0);
			}
			event.setCancelled(false);
			tnt = null;
		}
	}
	
	public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
		if (!event.getPlayer().hasPermission("customplugin.donorshop")) {
			if (event.getTo().getWorld().equals(donorBlock.getWorld())) {
			if (event.getTo().distance(new Location(donorBlock.getWorld(), 104, 199, 198)) < 30) {
				event.setCancelled(true);
				msg(event.getPlayer(), "YOU may not teleport to the donor shop!");
			}
			}
		}
		if (going.containsKey(event.getPlayer())) {
			removePlayer(event.getPlayer());
			msg(event.getPlayer(), "Your teleportation has been cancelled");
		}
	}
	
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
		if (going.containsKey(event.getPlayer())) {
			removePlayer(event.getPlayer());
			msg(event.getPlayer(), "Don't mess with the system!");
		}
	}
	
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		if (going.containsKey(event.getPlayer())) {
			removePlayer(event.getPlayer());
		}
	}
	
	public void msg(Player p, String msg) {
		p.sendMessage("[" + ChatColor.GOLD + "DonorShop" + ChatColor.WHITE + "] " + ChatColor.AQUA + msg);
	}
	
	public void log(String info) {
		plugin.getLogger().info("<DonorShop> " + info);
	}
}
