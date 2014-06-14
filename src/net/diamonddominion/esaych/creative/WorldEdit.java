package net.diamonddominion.esaych.creative;

import net.diamonddominion.esaych.CustomPlugin;

public class WorldEdit {
	private CustomPlugin plugin;

	public WorldEdit(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}
	
	/**Allowed commands: 
	 * 
	 * SELECTION COMMANDS
	 * wand
	 * ~sel
	 * desel
	 * pos1
	 * pos2
	 * expand
	 * contract
	 * outset
	 * inset
	 * shift
	 * size
	 * count
	 * distr
	 * 
	 * REGION COMMANDS
	 * set
	 * replace
	 * overlay
	 * walls
	 * outline
	 * smooth
	 * ~move
	 * ~stack
	 * naturalize
	 * 
	**/

	public void log(String info) {
		plugin.getLogger().info("<WorldEdit> " + info);
	}
}
