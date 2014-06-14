package net.diamonddominion.esaych.survival;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;


public class ExtraMiningLoot {
	private CustomPlugin plugin;

	public ExtraMiningLoot(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	@SuppressWarnings("deprecation")
	public void onPlayerBreakBlock(BlockBreakEvent e) {
		if (!e.isCancelled()) {
			if (!(e.getBlock().getType() != Material.DIAMOND_ORE || e.getPlayer().getGameMode().equals(GameMode.CREATIVE)))
				if (e.getBlock().getData() == 0) {
					e.setExpToDrop(100);
					e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.DIAMOND, 4));
				}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void onPlayerPlaceBlock(BlockPlaceEvent e) {
		if (e.getBlock().getType() == Material.DIAMOND_ORE && !e.isCancelled())
			e.getBlock().setData((byte) 1);
	}

	public void log(String info) {
		plugin.getLogger().info("<ExtraMiningLoot> " + info);
	}
}
