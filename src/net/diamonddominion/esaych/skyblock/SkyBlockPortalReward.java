package net.diamonddominion.esaych.skyblock;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.inventory.ItemStack;


public class SkyBlockPortalReward {
	private CustomPlugin plugin;

	public SkyBlockPortalReward(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	@SuppressWarnings("deprecation")
	public void EntityPortalEnter(EntityPortalEnterEvent event) {
		if (event.getEntity().getLocation().getWorld().getName().equals("SkyBlock")) {
			if (event.getEntity() instanceof Player && getLowestBlock(event.getLocation().getBlock()).getData() != (byte) 2) {
				getLowestBlock(event.getLocation().getBlock()).setData((byte)2);
				((Player) event.getEntity()).getInventory().addItem(new ItemStack(Material.NETHERRACK, 20));
				((Player) event.getEntity()).getInventory().addItem(new ItemStack(Material.NETHER_BRICK, 3));
				((Player) event.getEntity()).getInventory().addItem(new ItemStack(Material.GLOWSTONE_DUST, 10));
				((Player) event.getEntity()).getInventory().addItem(new ItemStack(Material.GHAST_TEAR, 1));
				((Player) event.getEntity()).getInventory().addItem(new ItemStack(Material.SOUL_SAND, 15));
			}
		}
	}
	
	private Block getLowestBlock(Block block) {
		for (int y = block.getY()-1; y > block.getY()-4; y++) {
			Block b = block.getWorld().getBlockAt(block.getX(), y, block.getZ());
			if (b.getType().equals(Material.OBSIDIAN)) {
				return b;
			}
		}
		return block;
	}

	public void log(String info) {
		plugin.getLogger().info("<SkyBlockPortalReward> " + info);
	}
}
