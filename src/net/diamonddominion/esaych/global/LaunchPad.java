package net.diamonddominion.esaych.global;

import java.util.HashMap;
import java.util.Map;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;


public class LaunchPad {

	private CustomPlugin plugin;
	
	private Map<Player, Integer> launched = new HashMap<Player, Integer>();
	
	public LaunchPad(CustomPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void enable() {
//		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		log("Enabled");
	}
	
	public void log(String info) {
		plugin.getLogger().info("<LaunchPad> " + info);
	}

	public void sendFlying(Block block, Player player) {
		Block u = block.getLocation().clone().add(0,-1,0).getBlock();
		if (u.getType() != Material.OBSIDIAN) {
			return;
		}
		boolean n = block.getLocation().clone().add(0,-1,1).getBlock().getType() == Material.REDSTONE_LAMP_OFF;
		boolean s = block.getLocation().clone().add(0,-1,-1).getBlock().getType() == Material.REDSTONE_LAMP_OFF;
		boolean e = block.getLocation().clone().add(1,-1,0).getBlock().getType() == Material.REDSTONE_LAMP_OFF;
		boolean w = block.getLocation().clone().add(-1,-1,0).getBlock().getType() == Material.REDSTONE_LAMP_OFF;
//		player.setVelocity(new Vector(0, 1, 0));
		int total = 0;
		
		if (n)
			total++;
		if (s)
			total++;
		if (e)
			total++;
		if (w)
			total++;
		if (total > 1) {
			return;
		} else if (total == 1) {
			block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, 80);	
		}
		
		if (n)
			player.setVelocity(new Vector(0,1,10));
		if (s)
			player.setVelocity(new Vector(0,1,-10));
		if (e)
			player.setVelocity(new Vector(10,1,0));
		if (w)
			player.setVelocity(new Vector(-10,1,0));
		
	}
	
//	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null) {
			if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.STONE_PLATE) {
//				Block b = event.getClickedBlock();
//				int x = b.getLocation().getBlockX();
//				int y = b.getLocation().getBlockY();
//				int z = b.getLocation().getBlockZ();
//				247, 192, 258
//				271, 192, 258
//				if (b.getWorld().getName().equals("Survival") && y == 192 && z == 258) {
//					if (x == 271) {
//						event.getPlayer().setVelocity(new Vector(-10, 1, 0));
//						return;
//					}
//					if (x == 247) {
//						event.getPlayer().setVelocity(new Vector(10, 1, 0));
//						return;
//					}
//				}
				//START SHOP SCRIPT
				if (event.getClickedBlock().getLocation().equals(new Location(Bukkit.getWorld("Survival"), 213, 68, 252))) {
					event.getPlayer().setVelocity(new Vector(-5.5, 2, -3.6));
					launched.put(event.getPlayer(), (int) (System.currentTimeMillis()/1000));
				}
				//END SHOP SCRIPT
				sendFlying(event.getClickedBlock(), event.getPlayer());
			}
		}
	}
	
//	@EventHandler
	@SuppressWarnings("deprecation")
	public void onPlayerDamage(EntityDamageEvent event){
		if (event.getEntity() instanceof Player){
			if (event.getCause() == DamageCause.FALL){
				Location l = event.getEntity().getLocation().add(0, -2, 0);
				Block block = l.getBlock();
				//START SHOP SCRIPT
				if (block.getType() == Material.SPONGE)
					if (block.getData() == (byte) 5)
						event.setCancelled(true);
				Player player = (Player) event.getEntity();
				if (launched.containsKey(player)) {
					if (System.currentTimeMillis()/1000 < launched.get(event.getEntity()) + 3) {
						event.setCancelled(true);
						if (!(block.getType() == Material.SPONGE && block.getData() == (byte) 5))
							player.teleport(new Location(Bukkit.getWorld("Survival"), 184.5, 80, 227.5, player.getLocation().getYaw(), player.getLocation().getPitch()));
						else
							launched.remove(player);
					} else {
						launched.remove(player);
					}
						
				}
				//END SHOP SCRIPT
				l = event.getEntity().getLocation().add(0, -1, 0);
				block = l.getBlock();
				if (block.getType() != Material.OBSIDIAN) {
					return;
				}
				boolean n = block.getLocation().clone().add(0,-1,1).getBlock().getType() == Material.REDSTONE_LAMP_OFF;
				boolean s = block.getLocation().clone().add(0,-1,-1).getBlock().getType() == Material.REDSTONE_LAMP_OFF;
				boolean e = block.getLocation().clone().add(1,-1,0).getBlock().getType() == Material.REDSTONE_LAMP_OFF;
				boolean w = block.getLocation().clone().add(-1,-1,0).getBlock().getType() == Material.REDSTONE_LAMP_OFF;
//				player.setVelocity(new Vector(0, 1, 0));
				int total = 0;
				
				if (n)
					total++;
				if (s)
					total++;
				if (e)
					total++;
				if (w)
					total++;
				if (total > 1) {
					return;
				}
				event.setCancelled(true);
			}
		}
	}
	
}
