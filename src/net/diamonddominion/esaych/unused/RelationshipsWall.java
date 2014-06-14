package net.diamonddominion.esaych.unused;

import net.diamonddominion.esaych.CustomPlugin;

public class RelationshipsWall {
	private CustomPlugin plugin;

	public RelationshipsWall(CustomPlugin plugin) {
		this.plugin = plugin;
	}

	public void enable() {
		log("Enabled");
	}

	public void log(String info) {
		plugin.getLogger().info("<RelationshipsWall> " + info);
	}
}
