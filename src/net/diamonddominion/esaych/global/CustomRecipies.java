package net.diamonddominion.esaych.global;

import net.diamonddominion.esaych.CustomPlugin;

import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;


public class CustomRecipies {
	private CustomPlugin plugin;

	public CustomRecipies(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("FleshToLeather:");
		plugin.getServer().addRecipe(new FurnaceRecipe(new ItemStack(Material.LEATHER), Material.ROTTEN_FLESH));
		log("Enabled");
	}

	public void log(String info) {
		plugin.getLogger().info("<CustomRecipies> " + info);
	}
}
