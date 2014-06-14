package net.diamonddominion.esaych.global;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;


public class Trampoline {
	private CustomPlugin plugin;

	public Trampoline(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	@SuppressWarnings("deprecation")
	public void onPlayerInteract(PlayerInteractEvent event) { 
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock().getTypeId() == 19) {
				player.setVelocity(new Vector(player.getVelocity().getX(), 1.3D, player.getVelocity().getZ()));
				player.getWorld().playEffect(event.getClickedBlock().getLocation(), Effect.STEP_SOUND, 19);
			}	
		}
	}

	@SuppressWarnings("deprecation")
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			if (event.getCause() == DamageCause.FALL) {
				Location l = event.getEntity().getLocation().add(0, -1, 0);
				Material mBelow = l.getBlock().getType();
				if (mBelow == Material.SPONGE) {
					event.setCancelled(true);
					Player player = (Player) event.getEntity();
					if (!player.isSneaking()) {
						player.setVelocity(new Vector(player.getVelocity().getX(), 1.3D, player.getVelocity().getZ()));
						if (player.hasPermission("customplugin.crazytramp") || l.getBlock().getData() == (byte) 1)
							player.setVelocity(new Vector(player.getVelocity().getX(), 10D, player.getVelocity().getZ()));
						player.getWorld().playEffect(l, Effect.STEP_SOUND, 19);
					}
				}
			}
		}
	}
	
	public void log(String info) {
		plugin.getLogger().info("<Trampoline> " + info);
	}
}
