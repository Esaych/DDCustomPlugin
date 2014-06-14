package net.diamonddominion.esaych.global;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;


public class Voodoo {
	private CustomPlugin plugin;

	public Voodoo(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	@SuppressWarnings("deprecation")
	public void onPlayerInteract(final PlayerInteractEvent event) { 
		final Player player = event.getPlayer();
		if (player.hasPermission("customplugin.voodoo")) {
			ItemStack i = player.getItemInHand();
			if (i.getTypeId() == 397 && i.getDurability() == (byte) 3) {
				SkullMeta skull = (SkullMeta)i.getItemMeta();
				Player p = Bukkit.getPlayer(skull.getOwner());
				if (p != null) {
					if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
						p.setVelocity(player.getLocation().getDirection().multiply(-1));
					} else {
						p.setVelocity(player.getLocation().getDirection());
					}
				}
			}
		}
	}
	
	public void log(String info) {
		plugin.getLogger().info("<Voodoo> " + info);
	}
}
